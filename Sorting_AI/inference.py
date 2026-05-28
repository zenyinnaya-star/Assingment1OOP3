import torch
from config import DEVICE, SOS_TOKEN, EOS_TOKEN, PAD_TOKEN, MAX_SEQ_LEN, MODEL_PATH
from model import SortingTransformer


@torch.no_grad()
def sort_array(model, array):
    model.eval()
    src = torch.tensor([array], dtype=torch.long, device=DEVICE)
    src_mask = torch.zeros(1, src.size(1), dtype=torch.bool, device=DEVICE)

    tgt = torch.tensor([[SOS_TOKEN]], dtype=torch.long, device=DEVICE)
    tgt_mask = torch.zeros(1, 1, dtype=torch.bool, device=DEVICE)

    result = []
    for _ in range(MAX_SEQ_LEN + 2):
        logits = model(src, tgt, src_key_padding_mask=src_mask, tgt_key_padding_mask=tgt_mask)
        next_token = logits[0, -1].argmax().item()

        if next_token == EOS_TOKEN:
            break
        if next_token == PAD_TOKEN:
            break

        result.append(next_token)
        tgt = torch.cat([tgt, torch.tensor([[next_token]], device=DEVICE)], dim=1)
        tgt_mask = torch.cat(
            [tgt_mask, torch.zeros(1, 1, dtype=torch.bool, device=DEVICE)], dim=1
        )

    return result


def interactive_sort():
    import os
    print("=" * 60)
    print("  SORTING AI — Interactive Mode")
    print("=" * 60)

    model = SortingTransformer().to(DEVICE)
    if not os.path.exists(MODEL_PATH):
        print(f"Error: No trained model found at {MODEL_PATH}")
        print("Run train.py first!")
        return

    checkpoint = torch.load(MODEL_PATH, weights_only=False)
    model.load_state_dict(checkpoint["model_state_dict"])
    print(f"Loaded model from epoch {checkpoint['epoch']} (val_loss={checkpoint['val_loss']:.4f})")
    print(f"Model parameters: {model.count_parameters():,}")
    print(f"Device: {DEVICE}")
    print()
    print("Enter comma-separated integers (0-100), or 'quit' to exit.")

    while True:
        user_input = input("\nInput array: ").strip()
        if user_input.lower() in ("quit", "exit", "q"):
            print("Goodbye!")
            break

        try:
            arr = [int(x.strip()) for x in user_input.split(",")]
            if any(x < 0 or x > 100 for x in arr):
                print("Numbers must be between 0 and 100.")
                continue
            if len(arr) > MAX_SEQ_LEN:
                print(f"Max length is {MAX_SEQ_LEN}.")
                continue
        except ValueError:
            print("Invalid input. Use comma-separated integers.")
            continue

        sorted_result = sort_array(model, arr)
        expected = sorted(arr)

        print(f"  Input:     {arr}")
        print(f"  AI sorted: {sorted_result}")
        print(f"  Expected:  {expected}")
        match = "CORRECT" if sorted_result == expected else "WRONG"
        print(f"  Status:    {match}")


if __name__ == "__main__":
    interactive_sort()
