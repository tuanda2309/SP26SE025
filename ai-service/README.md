# AI Service - Retina Image Analysis

Dịch vụ AI phân tích hình ảnh võng mạc sử dụng Deep Learning để phát hiện các bệnh lý về mắt.

## Mô tả

Service này sử dụng mô hình ResNet50 Multi-Label Classification để phân tích hình ảnh võng mạc và phát hiện các bệnh lý:

- Đục môi trường trong suốt (Opacity)
- Bệnh võng mạc đái tháo đường (Diabetic Retinopathy)
- Bệnh tăng nhãn áp (Glaucoma)
- Phù hoàng điểm (Macular Edema)
- Thoái hóa điểm vàng (Macular Degeneration)
- Tắc mạch máu võng mạc (Retinal Vascular Occlusion)
- Bình thường (Normal)

## Yêu cầu hệ thống

- Python 3.8 trở lên
- CUDA (tùy chọn, để sử dụng GPU)
- Windows/Linux/macOS

## Cài đặt

### 1. Tạo Virtual Environment (venv)

#### Trên Windows:

```bash
python -m venv venv
```

#### Trên Linux/macOS:

```bash
python3 -m venv venv
```

### 2. Kích hoạt Virtual Environment

#### Trên Windows (PowerShell):

```powershell
.\venv\Scripts\Activate.ps1
```

#### Trên Windows (Command Prompt):

```cmd
venv\Scripts\activate.bat
```

#### Trên Linux/macOS:

```bash
source venv/bin/activate
```

Sau khi kích hoạt, bạn sẽ thấy `(venv)` ở đầu dòng lệnh.

### 3. Cài đặt các thư viện

Đảm bảo bạn đã kích hoạt venv, sau đó chạy:

```bash
pip install -r requirements.txt
```

Hoặc cài đặt từng thư viện:

```bash
pip install torch torchvision fastapi uvicorn python-multipart pillow pandas numpy opencv-python
```

**Lưu ý:** Nếu bạn có GPU NVIDIA và muốn sử dụng CUDA, hãy cài đặt PyTorch với CUDA từ [pytorch.org](https://pytorch.org/) phù hợp với phiên bản CUDA của bạn.

### 4. Kiểm tra mô hình

Đảm bảo file mô hình `models/retina_multilabel.pt` đã có trong thư mục `models/`. Nếu chưa có, bạn cần tải mô hình về và đặt vào thư mục này.

## Chạy Server

### Chạy server ở chế độ development:

```bash
uvicorn src.main:app --reload --host 0.0.0.0 --port 8000
```

### Chạy server ở chế độ production:

```bash
uvicorn src.main:app --host 0.0.0.0 --port 8000
```

Sau khi khởi động, server sẽ chạy tại:

- **URL:** `http://localhost:8000`
- **API Documentation:** `http://localhost:8000/docs` (Swagger UI)
- **Alternative Docs:** `http://localhost:8000/redoc` (ReDoc)

## Sử dụng API

### 1. Health Check

Kiểm tra trạng thái của service:

```bash
GET http://localhost:8000/
```

**Response:**

```json
{
  "status": "AI service is running"
}
```

### 2. Predict - Phân tích hình ảnh võng mạc

Endpoint chính để phân tích hình ảnh và trả về kết quả dự đoán.

**Request:**

```bash
POST http://localhost:8000/predict
Content-Type: multipart/form-data
```

**Body (form-data):**

- `file`: File hình ảnh (jpg, png, jpeg)

**Ví dụ sử dụng cURL:**

```bash
curl -X POST "http://localhost:8000/predict" \
  -H "accept: application/json" \
  -H "Content-Type: multipart/form-data" \
  -F "file=@path/to/your/image.jpg"
```

**Ví dụ sử dụng Python:**

```python
import requests

url = "http://localhost:8000/predict"
files = {"file": open("path/to/image.jpg", "rb")}
response = requests.post(url, files=files)
print(response.json())
```

**Response:**

```json
{
  "status": "success",
  "input": {
    "filename": "image.jpg",
    "image_format": "image/jpeg"
  },
  "analysis": {
    "overall_risk": "HIGH",
    "normal": false,
    "detected_conditions": ["diabetic_retinopathy", "macular_edema"],
    "clinical_impression": "Phát hiện dấu hiệu bất thường ở võng mạc.",
    "recommendation": "Nên thăm khám bác sĩ chuyên khoa mắt để đánh giá chi tiết."
  },
  "predictions": [
    {
      "label": "opacity",
      "display_name": "Đục môi trường trong suốt",
      "probability": 0.2345,
      "confidence_score": 23,
      "detected": false,
      "risk_level": "VERY_LOW",
      "description": "Phát hiện độ mờ bất thường trong võng mạc.",
      "medical_note": "Không có dấu hiệu rõ ràng."
    },
    {
      "label": "diabetic_retinopathy",
      "display_name": "Bệnh võng mạc đái tháo đường",
      "probability": 0.8234,
      "confidence_score": 82,
      "detected": true,
      "risk_level": "HIGH",
      "description": "Dấu hiệu bệnh võng mạc do tiểu đường.",
      "medical_note": "Xác suất cao, cần đánh giá lâm sàng."
    }
    // ... các nhãn khác
  ],
  "meta": {
    "model": {
      "name": "ResNet50-MultiLabel-Retina",
      "version": "1.0.0",
      "input_size": "224x224",
      "labels": ["opacity", "diabetic_retinopathy", ...]
    },
    "thresholds": {
      "LOW": 0.5,
      "MEDIUM": 0.65,
      "HIGH": 0.8
    },
    "device": "cuda",
    "inference_time_ms": 150
  }
}
```

**Giải thích Response:**

- `overall_risk`: Mức độ rủi ro tổng thể (LOW/MEDIUM/HIGH)
- `normal`: Có phải bình thường không
- `detected_conditions`: Danh sách các bệnh lý được phát hiện
- `predictions`: Chi tiết dự đoán cho từng nhãn
  - `probability`: Xác suất (0-1)
  - `confidence_score`: Điểm tin cậy (%)
  - `risk_level`: Mức độ rủi ro (VERY_LOW/LOW/MEDIUM/HIGH)
  - `detected`: Có phát hiện bệnh lý không

### 3. Predict with CAM - Phân tích với Grad-CAM

Endpoint trả về hình ảnh với heatmap Grad-CAM để hiển thị vùng quan trọng trong hình ảnh.

**Request:**

```bash
POST http://localhost:8000/predict_with_cam
Content-Type: multipart/form-data
```

**Body (form-data):**

- `file`: File hình ảnh (jpg, png, jpeg)
- `class_idx`: Chỉ số lớp để tạo CAM (mặc định: 0)
  - 0: opacity
  - 1: diabetic_retinopathy
  - 2: glaucoma
  - 3: macular_edema
  - 4: macular_degeneration
  - 5: retinal_vascular_occlusion
  - 6: normal

**Ví dụ sử dụng cURL:**

```bash
curl -X POST "http://localhost:8000/predict_with_cam?class_idx=1" \
  -H "accept: image/jpeg" \
  -H "Content-Type: multipart/form-data" \
  -F "file=@path/to/your/image.jpg" \
  --output result_cam.jpg
```

**Ví dụ sử dụng Python:**

```python
import requests

url = "http://localhost:8000/predict_with_cam"
files = {"file": open("path/to/image.jpg", "rb")}
params = {"class_idx": 1}  # diabetic_retinopathy
response = requests.post(url, files=files, params=params)

with open("result_cam.jpg", "wb") as f:
    f.write(response.content)
```

**Response:**

- Content-Type: `image/jpeg`
- Body: Hình ảnh JPEG với heatmap Grad-CAM overlay

## Cấu trúc thư mục

```
ai-service/
├── src/
│   ├── main.py          # FastAPI application và endpoints
│   ├── model.py         # Định nghĩa mô hình
│   ├── infer.py         # Inference utilities
│   ├── gradcam.py       # Grad-CAM implementation
│   └── ...
├── models/
│   └── retina_multilabel.pt  # File mô hình đã train
├── requirements.txt      # Danh sách thư viện cần thiết
├── README.md            # File này
└── .gitignore
```

## Lưu ý

1. **Mô hình:** Đảm bảo file `models/retina_multilabel.pt` tồn tại trước khi chạy server.
2. **GPU:** Service sẽ tự động sử dụng GPU nếu có CUDA, nếu không sẽ dùng CPU.
3. **Kích thước ảnh:** Ảnh đầu vào sẽ được resize về 224x224 pixels.
4. **Định dạng ảnh:** Hỗ trợ các định dạng: JPG, JPEG, PNG.
5. **Performance:** Thời gian inference thường từ 100-500ms tùy vào phần cứng.

## Troubleshooting

### Lỗi khi cài đặt PyTorch

Nếu gặp lỗi khi cài PyTorch, hãy cài đặt từ trang chính thức:

```bash
pip install torch torchvision --index-url https://download.pytorch.org/whl/cu118
```

### Lỗi "Model not found"

Đảm bảo file mô hình `models/retina_multilabel.pt` đã được tải về và đặt đúng vị trí.

### Lỗi khi kích hoạt venv trên Windows

Nếu PowerShell báo lỗi về execution policy:

```powershell
Set-ExecutionPolicy -ExecutionPolicy RemoteSigned -Scope CurrentUser
```

## Tắt Virtual Environment

Khi hoàn thành công việc, bạn có thể tắt venv bằng lệnh:

```bash
deactivate
```

## Liên hệ

Nếu có vấn đề hoặc câu hỏi, vui lòng liên hệ team phát triển.
