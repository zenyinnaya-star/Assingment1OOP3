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

    min_v = min(values)
    max_v = max(values)
    if max_v == min_v:
        indices = list(range(len(values)))
        print(json.dumps({"indices": indices}))
        return

    quantized = [int(round((v - min_v) / (max_v - min_v) * 100)) for v in values]
    quantized = [max(0, min(100, q)) for q in quantized]

    if len(quantized) > MAX_SEQ_LEN:
        print(json.dumps({"error": f"Too many elements ({len(quantized)}), max is {MAX_SEQ_LEN}"}))
        return

    m = load_model()
    if m is None:
        print(json.dumps({"error": "Model not found"}))
        return

    sorted_vals = sort_array(m, quantized)
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

    print(json.dumps({"indices": indices}))


if __name__ == "__main__":
    main()
