import os

import pandas as pd
import torch
from PIL import Image
from torch.utils.data import Dataset


class RetinaDataset(Dataset):
    def __init__(self, csv_path, image_dir, transform=None):
        """
        csv_path: đường dẫn tới file csv (train / val / test)
        image_dir: thư mục chứa ảnh
        transform: torchvision transform
        """
        self.df = pd.read_csv(csv_path)
        self.image_dir = image_dir
        self.transform = transform

        # Cột nhãn = tất cả cột trừ filename
        self.label_cols = [col for col in self.df.columns if col != "filename"]

        # Check dataset type
        self.has_labels = len(self.label_cols) > 0

    def __len__(self):
        return len(self.df)

    def __getitem__(self, idx):
        row = self.df.iloc[idx]

        img_path = os.path.join(self.image_dir, row["filename"])
        image = Image.open(img_path).convert("RGB")

        if self.transform:
            image = self.transform(image)

        # TRAIN / VAL
        if self.has_labels:
            labels = row[self.label_cols].astype(float).values
            labels = torch.tensor(labels, dtype=torch.float32)
        else:
            # TEST: dummy label
            labels = torch.zeros(1, dtype=torch.float32)

        return image, labels
