from pathlib import Path

import joblib
import matplotlib.pyplot as plt
import pandas as pd

from sklearn.metrics import (
    accuracy_score,
    precision_score,
    recall_score,
    f1_score,
    confusion_matrix
)
from sklearn.model_selection import train_test_split


# ============================================================
# PATHS
# ============================================================

BASE_DIR = Path(__file__).resolve().parent.parent

DATA_PATH = (
    BASE_DIR
    / "data"
    / "processed"
    / "readmission_ml_dataset.csv"
)

MODEL_PATH = (
    BASE_DIR
    / "models"
    / "final_readmission_model.joblib"
)

OUTPUT_DIR = (
    BASE_DIR
    / "evaluation"
    / "threshold_analysis"
)

OUTPUT_DIR.mkdir(
    parents=True,
    exist_ok=True
)


# ============================================================
# LOAD DATA
# ============================================================

print("=" * 70)
print("MEDIFLOW AI - FINAL MODEL THRESHOLD ANALYSIS")
print("=" * 70)

df = pd.read_csv(DATA_PATH)

print("\nDataset loaded successfully.")
print(f"Rows: {len(df):,}")
print(f"Columns: {len(df.columns)}")


# ============================================================
# TARGET AND FEATURES
# ============================================================

TARGET_COLUMN = "readmitted_30d"

EXCLUDED_FEATURES = [
    TARGET_COLUMN,
    "readmitted_7d",
    "discharge_type",
    "age_group",
    "comorbidity_level",
    "previous_admission_level",
    "los_group",
    "admission_year"
]

EXCLUDED_FEATURES = [
    column
    for column in EXCLUDED_FEATURES
    if column in df.columns
]

FEATURE_COLUMNS = [
    column
    for column in df.columns
    if column not in EXCLUDED_FEATURES
]

X = df[FEATURE_COLUMNS]
y = df[TARGET_COLUMN]

print("\nFeatures used:", len(FEATURE_COLUMNS))


# ============================================================
# RECREATE TEST SPLIT
# ============================================================

X_train, X_test, y_train, y_test = train_test_split(
    X,
    y,
    test_size=0.20,
    random_state=42,
    stratify=y
)

print("\nTest records:", f"{len(X_test):,}")


# ============================================================
# LOAD FINAL MODEL
# ============================================================

model = joblib.load(MODEL_PATH)

print("\nFinal model loaded successfully.")
print(f"Model path: {MODEL_PATH}")


# ============================================================
# GENERATE PROBABILITIES
# ============================================================

probabilities = model.predict_proba(X_test)[:, 1]

print("\nPrediction probabilities generated.")

print(
    f"Minimum probability: {probabilities.min():.4f}"
)

print(
    f"Maximum probability: {probabilities.max():.4f}"
)

print(
    f"Average probability: {probabilities.mean():.4f}"
)


# ============================================================
# TEST MULTIPLE THRESHOLDS
# ============================================================

thresholds = [
    0.20,
    0.25,
    0.30,
    0.35,
    0.40,
    0.45,
    0.50,
    0.55,
    0.60,
    0.65,
    0.70,
    0.75,
    0.80
]

results = []


for threshold in thresholds:

    predictions = (
        probabilities >= threshold
    ).astype(int)

    tn, fp, fn, tp = confusion_matrix(
        y_test,
        predictions
    ).ravel()


    accuracy = accuracy_score(
        y_test,
        predictions
    )

    precision = precision_score(
        y_test,
        predictions,
        zero_division=0
    )

    recall = recall_score(
        y_test,
        predictions,
        zero_division=0
    )

    f1 = f1_score(
        y_test,
        predictions,
        zero_division=0
    )


    results.append(
        {
            "threshold": threshold,
            "accuracy": accuracy,
            "precision": precision,
            "recall": recall,
            "f1_score": f1,
            "true_negative": tn,
            "false_positive": fp,
            "false_negative": fn,
            "true_positive": tp
        }
    )


# ============================================================
# CREATE RESULTS DATAFRAME
# ============================================================

results_df = pd.DataFrame(results)

print("\n" + "=" * 70)
print("THRESHOLD COMPARISON")
print("=" * 70)

print(
    results_df.to_string(
        index=False
    )
)


# ============================================================
# SAVE RESULTS
# ============================================================

csv_path = (
    OUTPUT_DIR
    / "threshold_comparison.csv"
)

results_df.to_csv(
    csv_path,
    index=False
)


# ============================================================
# PRECISION / RECALL / F1 CHART
# ============================================================

plt.figure(figsize=(10, 6))

plt.plot(
    results_df["threshold"],
    results_df["precision"],
    marker="o",
    label="Precision"
)

plt.plot(
    results_df["threshold"],
    results_df["recall"],
    marker="o",
    label="Recall"
)

plt.plot(
    results_df["threshold"],
    results_df["f1_score"],
    marker="o",
    label="F1 Score"
)

plt.xlabel("Prediction Threshold")
plt.ylabel("Score")

plt.title(
    "MediFlow AI - Threshold Performance Analysis"
)

plt.legend()

plt.grid(
    alpha=0.3
)

plt.tight_layout()

performance_chart_path = (
    OUTPUT_DIR
    / "threshold_performance.png"
)

plt.savefig(
    performance_chart_path,
    dpi=300
)

plt.close()


# ============================================================
# CONFUSION MATRIX VALUES CHART
# ============================================================

plt.figure(figsize=(10, 6))

plt.plot(
    results_df["threshold"],
    results_df["true_positive"],
    marker="o",
    label="True Positive"
)

plt.plot(
    results_df["threshold"],
    results_df["false_positive"],
    marker="o",
    label="False Positive"
)

plt.plot(
    results_df["threshold"],
    results_df["false_negative"],
    marker="o",
    label="False Negative"
)

plt.xlabel("Prediction Threshold")
plt.ylabel("Number of Patients")

plt.title(
    "MediFlow AI - Threshold Confusion Analysis"
)

plt.legend()

plt.grid(
    alpha=0.3
)

plt.tight_layout()

confusion_chart_path = (
    OUTPUT_DIR
    / "threshold_confusion_analysis.png"
)

plt.savefig(
    confusion_chart_path,
    dpi=300
)

plt.close()


# ============================================================
# BEST F1 THRESHOLD
# ============================================================

best_f1_row = results_df.loc[
    results_df["f1_score"].idxmax()
]


print("\n" + "=" * 70)
print("BEST F1 THRESHOLD")
print("=" * 70)

print(
    f"\nThreshold: "
    f"{best_f1_row['threshold']:.2f}"
)

print(
    f"F1 Score : "
    f"{best_f1_row['f1_score']:.4f}"
)

print(
    f"Precision: "
    f"{best_f1_row['precision']:.4f}"
)

print(
    f"Recall   : "
    f"{best_f1_row['recall']:.4f}"
)


# ============================================================
# SUMMARY
# ============================================================

print("\n" + "=" * 70)
print("THRESHOLD ANALYSIS COMPLETED SUCCESSFULLY")
print("=" * 70)

print(f"\nCSV saved to:\n{csv_path}")

print(
    f"\nPerformance chart saved to:\n"
    f"{performance_chart_path}"
)

print(
    f"\nConfusion analysis chart saved to:\n"
    f"{confusion_chart_path}"
)