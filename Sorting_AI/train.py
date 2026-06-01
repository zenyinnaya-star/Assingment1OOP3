import os
import json
import time
import torch
import torch.nn as nn
from torch.optim import AdamW
from torch.optim.lr_scheduler import CosineAnnealingLR
from tqdm import tqdm
import matplotlib
matplotlib.use("Agg")
import matplotlib.pyplot as plt

from config import (
    DEVICE, NUM_EPOCHS, LEARNING_RATE, PAD_TOKEN, EOS_TOKEN,
    PLOT_DIR, MODEL_DIR, MODEL_PATH,
)
from dataset import get_dataloaders
from model import SortingTransformer


def compute_accuracy(logits, targets, pad_token=PAD_TOKEN):
    preds = logits.argmax(dim=-1)
    mask = targets != pad_token
    correct = (preds == targets) & mask
    total = mask.sum().item()
    if total == 0:
        return 0.0
    return correct.sum().item() / total


def compute_exact_match(logits, targets, pad_token=PAD_TOKEN, eos_token=EOS_TOKEN):
    preds = logits.argmax(dim=-1)
    batch_size = targets.size(0)
    matches = 0
    for i in range(batch_size):
        pred_seq = []
        tgt_seq = []
        for j in range(targets.size(1)):
            if targets[i][j].item() == eos_token:
                tgt_seq.append(targets[i][j].item())
                break
            if targets[i][j].item() != pad_token:
                tgt_seq.append(targets[i][j].item())

        for j in range(preds.size(1)):
            if preds[i][j].item() == eos_token:
                pred_seq.append(preds[i][j].item())
                break
            if preds[i][j].item() != pad_token:
                pred_seq.append(preds[i][j].item())

        if pred_seq == tgt_seq:
            matches += 1
    return matches / batch_size


def train_one_epoch(model, loader, optimizer, criterion, device):
    model.train()
    total_loss = 0
    total_acc = 0
    total_exact = 0
    num_batches = 0

    pbar = tqdm(loader, desc="Training", leave=False)
    for src, tgt_in, tgt_out, src_mask, tgt_mask in pbar:
        src = src.to(device)
        tgt_in = tgt_in.to(device)
        tgt_out = tgt_out.to(device)
        src_mask = src_mask.to(device)
        tgt_mask = tgt_mask.to(device)

        logits = model(src, tgt_in, src_key_padding_mask=src_mask, tgt_key_padding_mask=tgt_mask)

        loss = criterion(logits.view(-1, logits.size(-1)), tgt_out.view(-1))
        acc = compute_accuracy(logits, tgt_out)
        exact = compute_exact_match(logits, tgt_out)

        optimizer.zero_grad()
        loss.backward()
        torch.nn.utils.clip_grad_norm_(model.parameters(), max_norm=1.0)
        optimizer.step()

        total_loss += loss.item()
        total_acc += acc
        total_exact += exact
        num_batches += 1

        pbar.set_postfix(loss=f"{loss.item():.4f}", acc=f"{acc:.4f}", exact=f"{exact:.4f}")

    return total_loss / num_batches, total_acc / num_batches, total_exact / num_batches


@torch.no_grad()
def evaluate(model, loader, criterion, device):
    model.eval()
    total_loss = 0
    total_acc = 0
    total_exact = 0
    num_batches = 0

    pbar = tqdm(loader, desc="Evaluating", leave=False)
    for src, tgt_in, tgt_out, src_mask, tgt_mask in pbar:
        src = src.to(device)
        tgt_in = tgt_in.to(device)
        tgt_out = tgt_out.to(device)
        src_mask = src_mask.to(device)
        tgt_mask = tgt_mask.to(device)

        logits = model(src, tgt_in, src_key_padding_mask=src_mask, tgt_key_padding_mask=tgt_mask)

        loss = criterion(logits.view(-1, logits.size(-1)), tgt_out.view(-1))
        acc = compute_accuracy(logits, tgt_out)
        exact = compute_exact_match(logits, tgt_out)

        total_loss += loss.item()
        total_acc += acc
        total_exact += exact
        num_batches += 1

    return total_loss / num_batches, total_acc / num_batches, total_exact / num_batches


def plot_metrics(history, save_dir):
    os.makedirs(save_dir, exist_ok=True)

    epochs = range(1, len(history["train_loss"]) + 1)

    fig, axes = plt.subplots(2, 2, figsize=(16, 12))
    fig.suptitle("Sorting Transformer — Training Metrics", fontsize=18, fontweight="bold", y=0.98)

    # Loss
    ax = axes[0][0]
    ax.plot(epochs, history["train_loss"], "b-o", label="Train Loss", markersize=3, linewidth=1.5)
    ax.plot(epochs, history["val_loss"], "r-o", label="Val Loss", markersize=3, linewidth=1.5)
    ax.set_xlabel("Epoch", fontsize=12)
    ax.set_ylabel("Cross-Entropy Loss", fontsize=12)
    ax.set_title("Loss Over Epochs", fontsize=14, fontweight="bold")
    ax.legend(fontsize=11)
    ax.grid(True, alpha=0.3)
    ax.set_xlim(1, len(epochs))

    # Token accuracy
    ax = axes[0][1]
    ax.plot(epochs, history["train_acc"], "b-o", label="Train Token Acc", markersize=3, linewidth=1.5)
    ax.plot(epochs, history["val_acc"], "r-o", label="Val Token Acc", markersize=3, linewidth=1.5)
    ax.set_xlabel("Epoch", fontsize=12)
    ax.set_ylabel("Token Accuracy", fontsize=12)
    ax.set_title("Token-Level Accuracy Over Epochs", fontsize=14, fontweight="bold")
    ax.legend(fontsize=11)
    ax.grid(True, alpha=0.3)
    ax.set_xlim(1, len(epochs))
    ax.set_ylim(0, 1.05)

    # Exact match
    ax = axes[1][0]
    ax.plot(epochs, history["train_exact"], "b-o", label="Train Exact Match", markersize=3, linewidth=1.5)
    ax.plot(epochs, history["val_exact"], "r-o", label="Val Exact Match", markersize=3, linewidth=1.5)
    ax.set_xlabel("Epoch", fontsize=12)
    ax.set_ylabel("Exact Match Ratio", fontsize=12)
    ax.set_title("Exact Sequence Match Over Epochs", fontsize=14, fontweight="bold")
    ax.legend(fontsize=11)
    ax.grid(True, alpha=0.3)
    ax.set_xlim(1, len(epochs))
    ax.set_ylim(0, 1.05)

    # Learning rate
    ax = axes[1][1]
    ax.plot(epochs, history["lr"], "g-o", label="Learning Rate", markersize=3, linewidth=1.5, color="#2ca02c")
    ax.set_xlabel("Epoch", fontsize=12)
    ax.set_ylabel("Learning Rate", fontsize=12)
    ax.set_title("Learning Rate Schedule (Cosine Annealing)", fontsize=14, fontweight="bold")
    ax.legend(fontsize=11)
    ax.grid(True, alpha=0.3)
    ax.set_xlim(1, len(epochs))

    plt.tight_layout(rect=[0, 0, 1, 0.96])
    path = os.path.join(save_dir, "training_metrics.png")
    plt.savefig(path, dpi=150, bbox_inches="tight")
    plt.close()
    print(f"Saved metrics plot to {path}")

    # Individual high-res plots
    for metric, title, ylabel in [
        ("loss", "Loss Over Epochs", "Cross-Entropy Loss"),
        ("acc", "Token-Level Accuracy Over Epochs", "Accuracy"),
        ("exact", "Exact Sequence Match Over Epochs", "Exact Match Ratio"),
    ]:
        fig, ax = plt.subplots(figsize=(10, 6))
        ax.plot(epochs, history[f"train_{metric}"], "b-o", label=f"Train", markersize=3, linewidth=1.5)
        ax.plot(epochs, history[f"val_{metric}"], "r-o", label=f"Validation", markersize=3, linewidth=1.5)
        ax.set_xlabel("Epoch", fontsize=12)
        ax.set_ylabel(ylabel, fontsize=12)
        ax.set_title(title, fontsize=14, fontweight="bold")
        ax.legend(fontsize=11)
        ax.grid(True, alpha=0.3)
        ax.set_xlim(1, len(epochs))
        if metric in ("acc", "exact"):
            ax.set_ylim(0, 1.05)
        plt.tight_layout()
        path = os.path.join(save_dir, f"{metric}_plot.png")
        plt.savefig(path, dpi=150, bbox_inches="tight")
        plt.close()
        print(f"Saved {path}")


def main():
    print(f"Using device: {DEVICE}")
    if DEVICE.type == "cuda":
        print(f"GPU: {torch.cuda.get_device_name(0)}")
        print(f"VRAM: {torch.cuda.get_device_properties(0).total_memory / 1e9:.1f} GB")

    os.makedirs(MODEL_DIR, exist_ok=True)
    os.makedirs(PLOT_DIR, exist_ok=True)

    print("\nLoading datasets...")
    train_loader, val_loader, test_loader = get_dataloaders()
    print(f"Train batches: {len(train_loader)}, Val batches: {len(val_loader)}, Test batches: {len(test_loader)}")

    model = SortingTransformer().to(DEVICE)
    print(f"\nModel parameters: {model.count_parameters():,}")

    criterion = nn.CrossEntropyLoss(ignore_index=PAD_TOKEN)
    optimizer = AdamW(model.parameters(), lr=LEARNING_RATE, weight_decay=1e-4)
    scheduler = CosineAnnealingLR(optimizer, T_max=NUM_EPOCHS, eta_min=1e-6)

    history = {
        "train_loss": [], "val_loss": [],
        "train_acc": [], "val_acc": [],
        "train_exact": [], "val_exact": [],
        "lr": [],
    }

    best_val_loss = float("inf")
    start_time = time.time()

    print(f"\nStarting training for {NUM_EPOCHS} epochs...")
    print("=" * 80)

    for epoch in range(1, NUM_EPOCHS + 1):
        epoch_start = time.time()

        train_loss, train_acc, train_exact = train_one_epoch(
            model, train_loader, optimizer, criterion, DEVICE
        )
        val_loss, val_acc, val_exact = evaluate(
            model, val_loader, criterion, DEVICE
        )

        current_lr = optimizer.param_groups[0]["lr"]
        scheduler.step()

        history["train_loss"].append(train_loss)
        history["val_loss"].append(val_loss)
        history["train_acc"].append(train_acc)
        history["val_acc"].append(val_acc)
        history["train_exact"].append(train_exact)
        history["val_exact"].append(val_exact)
        history["lr"].append(current_lr)

        elapsed = time.time() - epoch_start

        if val_loss < best_val_loss:
            best_val_loss = val_loss
            torch.save({
                "epoch": epoch,
                "model_state_dict": model.state_dict(),
                "optimizer_state_dict": optimizer.state_dict(),
                "val_loss": val_loss,
                "val_acc": val_acc,
                "val_exact": val_exact,
            }, MODEL_PATH)
            marker = " *BEST*"
        else:
            marker = ""

        print(
            f"Epoch {epoch:3d}/{NUM_EPOCHS} | "
            f"Train Loss: {train_loss:.4f} | Val Loss: {val_loss:.4f} | "
            f"Train Acc: {train_acc:.4f} | Val Acc: {val_acc:.4f} | "
            f"Train EM: {train_exact:.4f} | Val EM: {val_exact:.4f} | "
            f"LR: {current_lr:.2e} | {elapsed:.1f}s{marker}"
        )

        if epoch % 5 == 0:
            plot_metrics(history, PLOT_DIR)

    total_time = time.time() - start_time
    print(f"\nTraining completed in {total_time / 60:.1f} minutes")

    plot_metrics(history, PLOT_DIR)

    with open(f"{MODEL_DIR}/history.json", "w") as f:
        json.dump(history, f, indent=2)
    print(f"Training history saved to {MODEL_DIR}/history.json")

    print("\nEvaluating on test set...")
    checkpoint = torch.load(MODEL_PATH, weights_only=False)
    model.load_state_dict(checkpoint["model_state_dict"])
    test_loss, test_acc, test_exact = evaluate(model, test_loader, criterion, DEVICE)
    print(f"Test Loss: {test_loss:.4f} | Test Acc: {test_acc:.4f} | Test Exact Match: {test_exact:.4f}")

    test_results = {
        "test_loss": test_loss,
        "test_accuracy": test_acc,
        "test_exact_match": test_exact,
        "best_epoch": checkpoint["epoch"],
        "total_training_minutes": total_time / 60,
        "model_parameters": model.count_parameters(),
    }
    with open(f"{MODEL_DIR}/test_results.json", "w") as f:
        json.dump(test_results, f, indent=2)


if __name__ == "__main__":
    main()
