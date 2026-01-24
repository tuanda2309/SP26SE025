import torch
import torch.nn.functional as F
from model import MultiLabelRetinaModel
from PIL import Image
from torchvision import transforms

# Tên nhãn đúng thứ tự CSV
LABELS = [
    "opacity",
    "diabetic_retinopathy",
    "glaucoma",
    "macular_edema",
    "macular_degeneration",
    "retinal_vascular_occlusion",
    "normal",
]


def load_model(model_path, device):
    model = MultiLabelRetinaModel(num_classes=len(LABELS))
    model.load_state_dict(torch.load(model_path, map_location=device))
    model.to(device)
    model.eval()
    return model


def predict_image(image_path, model, device, threshold=0.5):
    transform = transforms.Compose(
        [
            transforms.Resize((224, 224)),
            transforms.ToTensor(),
        ]
    )

    image = Image.open(image_path).convert("RGB")
    image = transform(image).unsqueeze(0).to(device)

    with torch.no_grad():
        logits = model(image)
        probs = torch.sigmoid(logits).squeeze(0)

    results = {}
    for i, label in enumerate(LABELS):
        results[label] = {
            "probability": float(probs[i]),
            "predicted": bool(probs[i] >= threshold),
        }

    return results


def main():
    device = "cuda" if torch.cuda.is_available() else "cpu"

    model = load_model("models/retina_multilabel.pt", device)

    test_image = "data/test/012df36af16b.jpg"

    results = predict_image(test_image, model, device)

    print("Prediction result:")
    for k, v in results.items():
        print(f"{k:30} -> {v}")


if __name__ == "__main__":
    main()
