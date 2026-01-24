from dataset import RetinaDataset
from torch.utils.data import DataLoader
from torchvision import transforms


def main():

    # Transform đơn giản để test
    transform = transforms.Compose(
        [
            transforms.Resize((224, 224)),
            transforms.ToTensor(),
        ]
    )

    train_csv = "data/train/train_split.csv"
    image_dir = "data/train/train"

    dataset = RetinaDataset(
        csv_path=train_csv, image_dir=image_dir, transform=transform
    )

    dataloader = DataLoader(dataset, batch_size=4, shuffle=True)

    images, labels = next(iter(dataloader))

    print("Image batch shape:", images.shape)
    print("Label batch shape:", labels.shape)
    print("Label sample:", labels[0])


if __name__ == "__main__":
    main()
