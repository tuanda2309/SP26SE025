import torch.nn as nn
from torchvision import models


class MultiLabelRetinaModel(nn.Module):
    def __init__(self, num_classes=7):
        super().__init__()

        # Load ResNet50 pretrained
        self.backbone = models.resnet50(pretrained=True)

        # Thay fully-connected layer cuối
        in_features = self.backbone.fc.in_features
        self.backbone.fc = nn.Linear(in_features, num_classes)

    def forward(self, x):
        return self.backbone(x)
