import math
import torch
import torch.nn as nn
from config import (
    VOCAB_SIZE, D_MODEL, NHEAD, NUM_ENCODER_LAYERS,
    NUM_DECODER_LAYERS, DIM_FEEDFORWARD, DROPOUT,
    PAD_TOKEN,
)


class PositionalEncoding(nn.Module):
    def __init__(self, d_model, max_len=512, dropout=0.1):
        super().__init__()
        self.dropout = nn.Dropout(p=dropout)

        pe = torch.zeros(max_len, d_model)
        position = torch.arange(0, max_len, dtype=torch.float).unsqueeze(1)
        div_term = torch.exp(
            torch.arange(0, d_model, 2).float() * (-math.log(10000.0) / d_model)
        )
        pe[:, 0::2] = torch.sin(position * div_term)
        pe[:, 1::2] = torch.cos(position * div_term)
        pe = pe.unsqueeze(0)
        self.register_buffer("pe", pe)

    def forward(self, x):
        x = x + self.pe[:, : x.size(1), :]
        return self.dropout(x)


class SortingTransformer(nn.Module):
    def __init__(
        self,
        vocab_size=VOCAB_SIZE,
        d_model=D_MODEL,
        nhead=NHEAD,
        num_encoder_layers=NUM_ENCODER_LAYERS,
        num_decoder_layers=NUM_DECODER_LAYERS,
        dim_feedforward=DIM_FEEDFORWARD,
        dropout=DROPOUT,
        pad_token=PAD_TOKEN,
    ):
        super().__init__()
        self.d_model = d_model
        self.pad_token = pad_token

        self.embedding = nn.Embedding(vocab_size, d_model, padding_idx=pad_token)
        self.pos_encoder = PositionalEncoding(d_model, dropout=dropout)

        self.transformer = nn.Transformer(
            d_model=d_model,
            nhead=nhead,
            num_encoder_layers=num_encoder_layers,
            num_decoder_layers=num_decoder_layers,
            dim_feedforward=dim_feedforward,
            dropout=dropout,
            batch_first=True,
        )

        self.fc_out = nn.Linear(d_model, vocab_size)
        self._init_weights()

    def _init_weights(self):
        for p in self.parameters():
            if p.dim() > 1:
                nn.init.xavier_uniform_(p)

    def _generate_causal_mask(self, sz, device):
        mask = torch.triu(torch.ones(sz, sz, device=device), diagonal=1)
        mask = mask.masked_fill(mask == 1, float("-inf"))
        return mask

    def forward(self, src, tgt, src_key_padding_mask=None, tgt_key_padding_mask=None):
        tgt_seq_len = tgt.size(1)
        causal_mask = self._generate_causal_mask(tgt_seq_len, tgt.device)

        src_emb = self.pos_encoder(self.embedding(src) * math.sqrt(self.d_model))
        tgt_emb = self.pos_encoder(self.embedding(tgt) * math.sqrt(self.d_model))

        output = self.transformer(
            src_emb,
            tgt_emb,
            tgt_mask=causal_mask,
            src_key_padding_mask=src_key_padding_mask,
            tgt_key_padding_mask=tgt_key_padding_mask,
            memory_key_padding_mask=src_key_padding_mask,
        )

        return self.fc_out(output)

    def count_parameters(self):
        return sum(p.numel() for p in self.parameters() if p.requires_grad)


if __name__ == "__main__":
    from config import DEVICE
    model = SortingTransformer().to(DEVICE)
    print(f"Model parameters: {model.count_parameters():,}")
    print(f"Device: {DEVICE}")
    dummy_src = torch.randint(0, 100, (4, 8)).to(DEVICE)
    dummy_tgt = torch.randint(0, 100, (4, 10)).to(DEVICE)
    out = model(dummy_src, dummy_tgt)
    print(f"Output shape: {out.shape}")
