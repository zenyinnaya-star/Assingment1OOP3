import sys
import json
import os
import warnings
warnings.filterwarnings("ignore")

SCRIPT_DIR = os.path.dirname(os.path.abspath(__file__))
os.chdir(SCRIPT_DIR)
sys.path.insert(0, SCRIPT_DIR)

import torch
from config import DEVICE, SOS_TOKEN, EOS_TOKEN, PAD_TOKEN, MAX_SEQ_LEN, MODEL_PATH
from model import SortingTransformer

model = None


def load_model():
    global model
    if model is not None:
        return model
    model = SortingTransformer().to(DEVICE)
    if not os.path.exists(MODEL_PATH):
        return None
    checkpoint = torch.load(MODEL_PATH, weights_only=False, map_location=DEVICE)
    model.load_state_dict(checkpoint["model_state_dict"])
    model.eval()
    return model


@torch.no_grad()
def sort_array(model, array):
    src = torch.tensor([array], dtype=torch.long, device=DEVICE)
    src_mask = torch.zeros(1, src.size(1), dtype=torch.bool, device=DEVICE)
    tgt = torch.tensor([[SOS_TOKEN]], dtype=torch.long, device=DEVICE)
    tgt_mask = torch.zeros(1, 1, dtype=torch.bool, device=DEVICE)
    result = []
    for _ in range(MAX_SEQ_LEN + 2):
        logits = model(src, tgt, src_key_padding_mask=src_mask, tgt_key_padding_mask=tgt_mask)
        next_token = logits[0, -1].argmax().item()
        if next_token == EOS_TOKEN or next_token == PAD_TOKEN:
            break
        result.append(next_token)
        tgt = torch.cat([tgt, torch.tensor([[next_token]], device=DEVICE)], dim=1)
        tgt_mask = torch.cat([tgt_mask, torch.zeros(1, 1, dtype=torch.bool, device=DEVICE)], dim=1)
    return result


def ai_sort_block(model, values, descending=True):
    n = len(values)
    if n == 0:
        return []
    if n == 1:
        return [0]
    min_v = min(values)
    max_v = max(values)
    if max_v == min_v:
        return list(range(n))
    quantized = [int(round((v - min_v) / (max_v - min_v) * 100)) for v in values]
    quantized = [max(0, min(100, q)) for q in quantized]
    sorted_vals = sort_array(model, quantized)
    original_quant = list(quantized)
    used = [False] * len(original_quant)
    indices = []
    for sv in sorted_vals:
        for i in range(len(original_quant)):
            if not used[i] and original_quant[i] == sv:
                used[i] = True
                indices.append(i)
                break
    for i in range(len(used)):
        if not used[i]:
            indices.append(i)
    if descending:
        indices = indices[::-1]
    return indices


def merge_sorted_blocks(block_indices, values, descending=True):
    if len(block_indices) == 0:
        return []
    if len(block_indices) == 1:
        return block_indices[0]
    merged = block_indices[0]
    for b in range(1, len(block_indices)):
        left = merged
        right = block_indices[b]
        result = []
        i = j = 0
        while i < len(left) and j < len(right):
            if descending:
                if values[left[i]] >= values[right[j]]:
                    result.append(left[i])
                    i += 1
                else:
                    result.append(right[j])
                    j += 1
            else:
                if values[left[i]] <= values[right[j]]:
                    result.append(left[i])
                    i += 1
                else:
                    result.append(right[j])
                    j += 1
        result.extend(left[i:])
        result.extend(right[j:])
        merged = result
    return merged


def main():
    raw = sys.stdin.read()
    data = json.loads(raw)
    values = data["values"]
    descending = data.get("descending", True)

    if len(values) == 0:
        print(json.dumps({"indices": []}))
        return
    if len(values) == 1:
        print(json.dumps({"indices": [0]}))
        return

    m = load_model()
    if m is None:
        print(json.dumps({"error": "Model not found"}))
        return

    n = len(values)
    if n <= MAX_SEQ_LEN:
        indices = ai_sort_block(m, values, descending)
        print(json.dumps({"indices": indices}))
        return

    block_size = MAX_SEQ_LEN
    block_indices = []
    for start in range(0, n, block_size):
        end = min(start + block_size, n)
        block_values = values[start:end]
        local_indices = ai_sort_block(m, block_values, descending)
        global_indices = [start + li for li in local_indices]
        block_indices.append(global_indices)

    indices = merge_sorted_blocks(block_indices, values, descending)
    print(json.dumps({"indices": indices}))


if __name__ == "__main__":
    main()
