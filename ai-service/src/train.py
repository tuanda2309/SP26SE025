import torch
import torch.nn as nn
from dataset import RetinaDataset
from model import MultiLabelRetinaModel
from torch.utils.data import DataLoader
from torchvision import transforms


def train_one_epoch(model, loader, criterion, optimizer, device):
    model.train()
    running_loss = 0.0

    for images, labels in loader:
        images = images.to(device)
        labels = labels.to(device)

        optimizer.zero_grad()
        outputs = model(images)
        loss = criterion(outputs, labels)
        loss.backward()
        optimizer.step()

        running_loss += loss.item()

    return running_loss / len(loader)


def validate(model, loader, criterion, device):
    model.eval()
    running_loss = 0.0

    with torch.no_grad():
        for images, labels in loader:
            images = images.to(device)
            labels = labels.to(device)

            outputs = model(images)
            loss = criterion(outputs, labels)
            running_loss += loss.item()

    return running_loss / len(loader)


def main():
    device = "cuda" if torch.cuda.is_available() else "cpu"
    print("Using device:", device)

    # Transform
    transform = transforms.Compose(
        [
            transforms.Resize((224, 224)),
            transforms.ToTensor(),
        ]
    )

    # Dataset
    train_dataset = RetinaDataset(
        csv_path="data/train/train_split.csv",
        image_dir="data/train/train",
        transform=transform,
    )

    val_dataset = RetinaDataset(
        csv_path="data/train/val.csv",
        image_dir="data/train/train",
        transform=transform,
    )

    # DataLoader
    train_loader = DataLoader(train_dataset, batch_size=8, shuffle=True)

    val_loader = DataLoader(val_dataset, batch_size=8, shuffle=False)

    # Model
    model = MultiLabelRetinaModel(num_classes=7)
    model = model.to(device)

    # Loss + Optimizer
    criterion = nn.BCEWithLogitsLoss()
    optimizer = torch.optim.Adam(model.parameters(), lr=1e-4)

    num_epochs = 5

    for epoch in range(num_epochs):
        train_loss = train_one_epoch(model, train_loader, criterion, optimizer, device)

        val_loss = validate(model, val_loader, criterion, device)

        print(
            f"Epoch [{epoch+1}/{num_epochs}] "
            f"Train Loss: {train_loss:.4f} "
            f"Val Loss: {val_loss:.4f}"
        )

    # Lưu model
    torch.save(model.state_dict(), "models/retina_multilabel.pt")
    print("Model saved to models/retina_multilabel.pt")


if __name__ == "__main__":
    main()
