import pandas as pd
from sklearn.model_selection import train_test_split

TRAIN_CSV_PATH = "data/train/train.csv"

# Đọc dữ liệu
df = pd.read_csv(TRAIN_CSV_PATH)

print("Tổng số mẫu ban đầu:", len(df))

# Chia 80% train - 20% validation
train_df, val_df = train_test_split(
    df,
    test_size=0.2,
    random_state=42,
    shuffle=True
)

print("Số mẫu train:", len(train_df))
print("Số mẫu validation:", len(val_df))

# Lưu ra file mới
train_df.to_csv("data/train/train_split.csv", index=False)
val_df.to_csv("data/train/val.csv", index=False)

print("Đã tạo xong train_split.csv và val.csv")
