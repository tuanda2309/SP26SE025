import io
import time

import cv2
import numpy as np
import torch
from fastapi import FastAPI, File, UploadFile
from fastapi.responses import StreamingResponse
from PIL import Image
from src.gradcam import GradCAM, overlay_cam
from src.model import MultiLabelRetinaModel
from torchvision import transforms

app = FastAPI(title="Retina AI Service")

# ===== CONFIG =====
MODEL_PATH = "models/retina_multilabel.pt"
LABELS = [
    "opacity",
    "diabetic_retinopathy",
    "glaucoma",
    "macular_edema",
    "macular_degeneration",
    "retinal_vascular_occlusion",
    "normal",
]

RISK_LEVELS = {"LOW": (0.0, 0.5), "MEDIUM": (0.5, 0.75), "HIGH": (0.75, 1.0)}

DESCRIPTIONS = {
    "opacity": "Phát hiện độ mờ bất thường trong võng mạc.",
    "diabetic_retinopathy": "Dấu hiệu bệnh võng mạc do tiểu đường.",
    "glaucoma": "Nguy cơ tổn thương dây thần kinh thị giác.",
    "macular_edema": "Phát hiện phù hoàng điểm.",
    "macular_degeneration": "Dấu hiệu thoái hóa điểm vàng.",
    "retinal_vascular_occlusion": "Tắc nghẽn mạch máu võng mạc.",
    "normal": "Không phát hiện bất thường đáng kể.",
}

MODEL_INFO = {
    "name": "ResNet50-MultiLabel-Retina",
    "version": "1.0.0",
    "input_size": "224x224",
    "labels": LABELS,
}

THRESHOLDS = {
    "LOW": 0.5,
    "MEDIUM": 0.65,
    "HIGH": 0.8,
}

DISPLAY_NAMES = {
    "opacity": "Đục môi trường trong suốt",
    "diabetic_retinopathy": "Bệnh võng mạc đái tháo đường",
    "glaucoma": "Bệnh tăng nhãn áp (Glaucoma)",
    "macular_edema": "Phù hoàng điểm",
    "macular_degeneration": "Thoái hóa điểm vàng",
    "retinal_vascular_occlusion": "Tắc mạch máu võng mạc",
    "normal": "Bình thường",
}

device = "cuda" if torch.cuda.is_available() else "cpu"

# ===== LOAD MODEL 1 LẦN =====
model = MultiLabelRetinaModel(num_classes=len(LABELS))
model.load_state_dict(torch.load(MODEL_PATH, map_location=device))
model.to(device)
model.eval()

transform = transforms.Compose(
    [
        transforms.Resize((224, 224)),
        transforms.ToTensor(),
    ]
)

gradcam = GradCAM(model, model.backbone.layer4)


@app.get("/")
def health_check():
    return {"status": "AI service is running"}


@app.post("/predict")
async def predict(file: UploadFile = File(...)):
    start_time = time.time()

    # ===== Load & preprocess image =====
    image_bytes = await file.read()
    image = Image.open(io.BytesIO(image_bytes)).convert("RGB")
    image = transform(image).unsqueeze(0).to(device)

    # ===== Inference =====
    with torch.no_grad():
        logits = model(image)
        probs = torch.sigmoid(logits).squeeze(0)

    predictions = []
    detected_conditions = []

    for i, label in enumerate(LABELS):
        prob = float(probs[i])

        # Risk classification
        if prob >= THRESHOLDS["HIGH"]:
            risk = "HIGH"
            detected = True
        elif prob >= THRESHOLDS["MEDIUM"]:
            risk = "MEDIUM"
            detected = True
        elif prob >= THRESHOLDS["LOW"]:
            risk = "LOW"
            detected = False
        else:
            risk = "VERY_LOW"
            detected = False

        if detected and label != "normal":
            detected_conditions.append(label)

        predictions.append(
            {
                "label": label,
                "display_name": DISPLAY_NAMES[label],
                "probability": round(prob, 4),
                "confidence_score": int(prob * 100),
                "detected": detected,
                "risk_level": risk,
                "description": DESCRIPTIONS.get(label, ""),
                "medical_note": (
                    "Xác suất cao, cần đánh giá lâm sàng."
                    if risk == "HIGH"
                    else (
                        "Có dấu hiệu nhẹ, cần theo dõi."
                        if risk == "MEDIUM"
                        else "Không có dấu hiệu rõ ràng."
                    )
                ),
            }
        )

    # ===== Tổng hợp phân tích =====
    if detected_conditions:
        overall_risk = "HIGH"
        clinical_impression = "Phát hiện dấu hiệu bất thường ở võng mạc."
        recommendation = "Nên thăm khám bác sĩ chuyên khoa mắt để đánh giá chi tiết."
        normal = False
    else:
        overall_risk = "LOW"
        clinical_impression = "Không phát hiện dấu hiệu bệnh lý võng mạc rõ ràng."
        recommendation = "Tiếp tục theo dõi định kỳ và kiểm tra mắt thường xuyên."
        normal = True

    inference_time = int((time.time() - start_time) * 1000)

    # ===== Response =====
    return {
        "status": "success",
        "input": {
            "filename": file.filename,
            "image_format": file.content_type,
        },
        "analysis": {
            "overall_risk": overall_risk,
            "normal": normal,
            "detected_conditions": detected_conditions,
            "clinical_impression": clinical_impression,
            "recommendation": recommendation,
        },
        "predictions": predictions,
        "meta": {
            "model": MODEL_INFO,
            "thresholds": THRESHOLDS,
            "device": device,
            "inference_time_ms": inference_time,
        },
    }


@app.post("/predict_with_cam")
async def predict_with_cam(file: UploadFile = File(...), class_idx: int = 0):
    image_bytes = await file.read()
    image = Image.open(io.BytesIO(image_bytes)).convert("RGB")

    input_tensor = transform(image).unsqueeze(0).to(device)

    with torch.no_grad():
        logits = model(input_tensor)
        probs = torch.sigmoid(logits).squeeze(0)

    cam = gradcam.generate(input_tensor, class_idx)

    img_np = np.array(image)
    img_np = cv2.cvtColor(img_np, cv2.COLOR_RGB2BGR)
    cam_img = overlay_cam(img_np, cam)

    _, buffer = cv2.imencode(".jpg", cam_img)

    return StreamingResponse(io.BytesIO(buffer.tobytes()), media_type="image/jpeg")
