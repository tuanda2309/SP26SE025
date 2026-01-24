import torch
from dataset import RetinaDataset
from model import MultiLabelRetinaModel
from torch.utils.data import DataLoader
from torchvision import transforms

LABELS = [
    "opacity",
    "diabetic_retinopathy",
    "glaucoma",
    "macular_edema",
    "macular_degeneration",
    "retinal_vascular_occlusion",
    "normal",
]


def main():
    device = "cuda" if torch.cuda.is_available() else "cpu"

    transform = transforms.Compose(
        [
            transforms.Resize((224, 224)),
            transforms.ToTensor(),
        ]
    )

    test_dataset = RetinaDataset(
        csv_path="data/test/test.csv", image_dir="data/test/", transform=transform
    )

    test_loader = DataLoader(test_dataset, batch_size=16, shuffle=False)

    model = MultiLabelRetinaModel(num_classes=len(LABELS))
    model.load_state_dict(
        torch.load("models/retina_multilabel.pt", map_location=device)
    )
    model.to(device)
    model.eval()

    with torch.no_grad():
        for images, labels in test_loader:
            images = images.to(device)
            outputs = torch.sigmoid(model(images))
            print(outputs[:2])
            break


if __name__ == "__main__":
    main()
