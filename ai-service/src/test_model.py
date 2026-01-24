import torch
from model import MultiLabelRetinaModel


def main():
    model = MultiLabelRetinaModel(num_classes=7)

    # Tạo dummy input: batch 4 ảnh RGB 224x224
    x = torch.randn(4, 3, 224, 224)

    # Forward pass
    outputs = model(x)

    print("Output shape:", outputs.shape)
    print("Output sample:", outputs[0])


if __name__ == "__main__":
    main()
