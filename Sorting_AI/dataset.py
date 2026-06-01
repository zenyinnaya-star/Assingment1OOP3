import random
import torch
from torch.utils.data import Dataset, DataLoader
from config import (
    MIN_SEQ_LEN, MAX_SEQ_LEN, NUM_RANGE, NUM_BINS,
    TRAIN_SAMPLES, VAL_SAMPLES, TEST_SAMPLES,
    SOS_TOKEN, EOS_TOKEN, PAD_TOKEN, BATCH_SIZE,
)


class SortingDataset(Dataset):
    def __init__(self, num_samples, min_len, max_len, num_range, duplicate_ratio=0.0):
        self.samples = []
        for _ in range(num_samples):
            seq_len = random.randint(min_len, max_len)
            if random.random() < duplicate_ratio:
                num_unique = random.randint(max(1, seq_len // 3), seq_len - 1)
                unique_vals = [random.randint(num_range[0], num_range[1]) for _ in range(num_unique)]
                arr = []
                for v in unique_vals:
                    arr.append(v)
                while len(arr) < seq_len:
                    arr.append(random.choice(unique_vals))
                random.shuffle(arr)
            else:
                arr = [random.randint(num_range[0], num_range[1]) for _ in range(seq_len)]
            self.samples.append(arr)

    def __len__(self):
        return len(self.samples)

    def __getitem__(self, idx):
        unsorted = self.samples[idx]
        sorted_arr = sorted(unsorted)
        return unsorted, sorted_arr


def collate_fn(batch):
    src_seqs, tgt_seqs = zip(*batch)

    src_max_len = max(len(s) for s in src_seqs)
    tgt_max_len = max(len(s) for s in tgt_seqs) + 2  # SOS + EOS

    src_padded = []
    tgt_input = []
    tgt_output = []

    for src, tgt in zip(src_seqs, tgt_seqs):
        src_pad = src + [PAD_TOKEN] * (src_max_len - len(src))
        src_padded.append(src_pad)

        tgt_in = [SOS_TOKEN] + tgt + [EOS_TOKEN]
        tgt_out = tgt + [EOS_TOKEN] + [PAD_TOKEN]
        tgt_in += [PAD_TOKEN] * (tgt_max_len - len(tgt_in))
        tgt_out += [PAD_TOKEN] * (tgt_max_len - len(tgt_out))
        tgt_input.append(tgt_in)
        tgt_output.append(tgt_out)

    src_tensor = torch.tensor(src_padded, dtype=torch.long)
    tgt_in_tensor = torch.tensor(tgt_input, dtype=torch.long)
    tgt_out_tensor = torch.tensor(tgt_output, dtype=torch.long)

    src_mask = (src_tensor == PAD_TOKEN)
    tgt_mask = (tgt_in_tensor == PAD_TOKEN)

    return src_tensor, tgt_in_tensor, tgt_out_tensor, src_mask, tgt_mask


def get_dataloaders():
    train_ds = SortingDataset(TRAIN_SAMPLES, MIN_SEQ_LEN, MAX_SEQ_LEN, NUM_RANGE, duplicate_ratio=0.6)
    val_ds = SortingDataset(VAL_SAMPLES, MIN_SEQ_LEN, MAX_SEQ_LEN, NUM_RANGE, duplicate_ratio=0.5)
    test_ds = SortingDataset(TEST_SAMPLES, MIN_SEQ_LEN, MAX_SEQ_LEN, NUM_RANGE, duplicate_ratio=0.0)

    train_loader = DataLoader(
        train_ds, batch_size=BATCH_SIZE, shuffle=True,
        collate_fn=collate_fn, num_workers=0, pin_memory=True,
    )
    val_loader = DataLoader(
        val_ds, batch_size=BATCH_SIZE, shuffle=False,
        collate_fn=collate_fn, num_workers=0, pin_memory=True,
    )
    test_loader = DataLoader(
        test_ds, batch_size=BATCH_SIZE, shuffle=False,
        collate_fn=collate_fn, num_workers=0, pin_memory=True,
    )

    return train_loader, val_loader, test_loader


if __name__ == "__main__":
    train_loader, val_loader, test_loader = get_dataloaders()
    for src, tgt_in, tgt_out, src_mask, tgt_mask in train_loader:
        print(f"Source shape:      {src.shape}")
        print(f"Target input shape: {tgt_in.shape}")
        print(f"Target output shape:{tgt_out.shape}")
        print(f"Source mask shape:  {src_mask.shape}")
        print(f"Target mask shape:  {tgt_mask.shape}")
        print(f"Source example:     {src[0][:10]}")
        print(f"Target in example:  {tgt_in[0][:10]}")
        print(f"Target out example: {tgt_out[0][:10]}")
        break
