import cv2
import numpy as np
import torch
from gradcam import GradCAM, overlay_cam
from model import MultiLabelRetinaModel
from PIL import Image
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

    model = MultiLabelRetinaModel(num_classes=len(LABELS))
    model.load_state_dict(
        torch.load("models/retina_multilabel.pt", map_location=device)
    )
    model.to(device)
    model.eval()

    gradcam = GradCAM(model, model.backbone.layer4)

    transform = transforms.Compose(
        [
            transforms.Resize((224, 224)),
            transforms.ToTensor(),
        ]
    )

    img = Image.open("data/test/0a2229abced7.jpg").convert("RGB")
    input_tensor = transform(img).unsqueeze(0).to(device)

    cam = gradcam.generate(input_tensor, class_idx=1)

    img_np = np.array(img)
    img_np = cv2.cvtColor(img_np, cv2.COLOR_RGB2BGR)

    result = overlay_cam(img_np, cam)

    cv2.imwrite("gradcam_result.jpg", result)
    print("Saved gradcam_result.jpg")


if __name__ == "__main__":
    main()
