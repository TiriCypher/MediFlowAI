from pathlib import Path

import pandas as pd
import matplotlib.pyplot as plt

from sklearn.compose import ColumnTransformer
from sklearn.impute import SimpleImputer
from sklearn.linear_model import LogisticRegression
from sklearn.metrics import (
    accuracy_score,
    precision_score,
    recall_score,
    f1_score,
    roc_auc_score,
    classification_report,
    ConfusionMatrixDisplay,
    RocCurveDisplay,
    PrecisionRecallDisplay
)
from sklearn.model_selection import train_test_split
from sklearn.pipeline import Pipeline
from sklearn.preprocessing import OneHotEncoder, StandardScaler
from sklearn.tree import DecisionTreeClassifier
from sklearn.ensemble import RandomForestClassifier


# ============================================================
# PATHS
# ============================================================

BASE_DIR = Path(__file__).resolve().parents[1]

DATA_PATH = BASE_DIR / "data" / "processed" / "readmission_ml_dataset.csv"

EVALUATION_DIR = BASE_DIR / "evaluation" / "model_evaluation"

CONFUSION_DIR = EVALUATION_DIR / "confusion_matrices"
ROC_DIR = EVALUATION_DIR / "roc_curves"
PR_DIR = EVALUATION_DIR / "precision_recall_curves"
FEATURE_DIR = EVALUATION_DIR / "feature_importance"

for folder in [
    CONFUSION_DIR,
    ROC_DIR,
    PR_DIR,
    FEATURE_DIR
]:
    folder.mkdir(parents=True, exist_ok=True)


# ============================================================
# LOAD DATA
# ============================================================

df = pd.read_csv(DATA_PATH)

target = "readmitted_30d"

selected_features = [
    "los_days",
    "admit_type",
    "ward_type",
    "discharge_type",
    "num_procedures",
    "charlson_index",
    "hba1c",
    "creatinine",
    "haemoglobin",
    "systolic_bp",
    "age",
    "gender",
    "state",
    "bpl_card",
    "insurance_type",
    "comorbidity_count",
    "prev_admissions",
    "hospital_state",
    "tier",
    "beds",
    "teaching",
    "diagnosis_count",
    "primary_diagnosis_category",
    "admission_month"
]

X = df[selected_features]
y = df[target]


# ============================================================
# FEATURE TYPES
# ============================================================

numeric_features = [
    "los_days",
    "num_procedures",
    "charlson_index",
    "hba1c",
    "creatinine",
    "haemoglobin",
    "systolic_bp",
    "age",
    "bpl_card",
    "comorbidity_count",
    "prev_admissions",
    "beds",
    "teaching",
    "diagnosis_count",
    "admission_month"
]

categorical_features = [
    "admit_type",
    "ward_type",
    "discharge_type",
    "gender",
    "state",
    "insurance_type",
    "hospital_state",
    "tier",
    "primary_diagnosis_category"
]


# ============================================================
# PREPROCESSOR
# ============================================================

numeric_transformer = Pipeline(
    steps=[
        ("imputer", SimpleImputer(strategy="median")),
        ("scaler", StandardScaler())
    ]
)

categorical_transformer = Pipeline(
    steps=[
        ("imputer", SimpleImputer(strategy="most_frequent")),
        ("onehot", OneHotEncoder(handle_unknown="ignore"))
    ]
)

preprocessor = ColumnTransformer(
    transformers=[
        ("num", numeric_transformer, numeric_features),
        ("cat", categorical_transformer, categorical_features)
    ]
)


# ============================================================
# MODELS
# ============================================================

models = {
    "Logistic Regression": LogisticRegression(
        max_iter=1000,
        class_weight="balanced"
    ),

    "Decision Tree": DecisionTreeClassifier(
        max_depth=8,
        min_samples_split=20,
        min_samples_leaf=10,
        class_weight="balanced",
        random_state=42
    ),

    "Random Forest": RandomForestClassifier(
        n_estimators=200,
        max_depth=12,
        min_samples_split=10,
        min_samples_leaf=5,
        class_weight="balanced",
        random_state=42,
        n_jobs=-1
    )
}


# ============================================================
# SAME TRAIN/TEST SPLIT AS TRAINING
# ============================================================

X_train, X_test, y_train, y_test = train_test_split(
    X,
    y,
    test_size=0.20,
    stratify=y,
    random_state=42
)


print("=" * 70)
print("MODEL EVALUATION")
print("=" * 70)

print(f"Total records: {len(df):,}")
print(f"Training records: {len(X_train):,}")
print(f"Testing records: {len(X_test):,}")
print(f"Selected features: {len(selected_features)}")

print("\nTest target distribution:")
print(y_test.value_counts().sort_index())


# ============================================================
# TRAIN + EVALUATE
# ============================================================

results = []
trained_pipelines = {}

roc_data = []
pr_data = []


for name, model in models.items():

    print("\n" + "-" * 70)
    print(name)
    print("-" * 70)

    pipeline = Pipeline(
        steps=[
            ("preprocessor", preprocessor),
            ("model", model)
        ]
    )

    pipeline.fit(X_train, y_train)

    y_pred = pipeline.predict(X_test)
    y_probability = pipeline.predict_proba(X_test)[:, 1]

    accuracy = accuracy_score(y_test, y_pred)
    precision = precision_score(
        y_test,
        y_pred,
        zero_division=0
    )
    recall = recall_score(
        y_test,
        y_pred,
        zero_division=0
    )
    f1 = f1_score(
        y_test,
        y_pred,
        zero_division=0
    )
    roc_auc = roc_auc_score(
        y_test,
        y_probability
    )

    print(f"Accuracy : {accuracy:.4f}")
    print(f"Precision: {precision:.4f}")
    print(f"Recall   : {recall:.4f}")
    print(f"F1 Score : {f1:.4f}")
    print(f"ROC-AUC  : {roc_auc:.4f}")

    print("\nClassification Report:")
    print(
        classification_report(
            y_test,
            y_pred,
            target_names=[
                "No Readmission",
                "Readmission"
            ],
            zero_division=0
        )
    )

    results.append({
        "model": name,
        "accuracy": accuracy,
        "precision": precision,
        "recall": recall,
        "f1_score": f1,
        "roc_auc": roc_auc
    })

    trained_pipelines[name] = pipeline

    roc_data.append(
        (name, y_test, y_probability)
    )

    pr_data.append(
        (name, y_test, y_probability)
    )

    # ========================================================
    # CONFUSION MATRIX
    # ========================================================

    plt.figure(figsize=(7, 6))

    ConfusionMatrixDisplay.from_predictions(
        y_test,
        y_pred,
        display_labels=[
            "No Readmission",
            "Readmission"
        ],
        cmap="Blues"
    )

    plt.title(f"Confusion Matrix - {name}")
    plt.tight_layout()

    filename = name.lower().replace(" ", "_") + "_confusion_matrix.png"

    plt.savefig(
        CONFUSION_DIR / filename,
        dpi=200
    )

    plt.close()

    # ========================================================
    # FEATURE IMPORTANCE
    # ========================================================

    fitted_preprocessor = pipeline.named_steps["preprocessor"]
    fitted_model = pipeline.named_steps["model"]

    feature_names = fitted_preprocessor.get_feature_names_out()

    if hasattr(fitted_model, "coef_"):
        importance_values = fitted_model.coef_[0]
        importance_type = "coefficient"

    else:
        importance_values = fitted_model.feature_importances_
        importance_type = "feature_importance"

    importance_df = pd.DataFrame({
        "feature": feature_names,
        "importance": importance_values,
        "absolute_importance": abs(importance_values)
    })

    importance_df = importance_df.sort_values(
        "absolute_importance",
        ascending=False
    )

    importance_df.to_csv(
        FEATURE_DIR /
        f"{name.lower().replace(' ', '_')}_feature_importance.csv",
        index=False
    )

    # Top 20 plot
    top_features = importance_df.head(20).sort_values(
        "absolute_importance"
    )

    plt.figure(figsize=(10, 8))

    plt.barh(
        top_features["feature"],
        top_features["absolute_importance"]
    )

    plt.title(
        f"Top 20 Feature Importance - {name}"
    )

    plt.xlabel("Absolute Importance")

    plt.tight_layout()

    plt.savefig(
        FEATURE_DIR /
        f"{name.lower().replace(' ', '_')}_top_features.png",
        dpi=200
    )

    plt.close()


# ============================================================
# MODEL COMPARISON
# ============================================================

results_df = pd.DataFrame(results)

results_df = results_df.sort_values(
    "roc_auc",
    ascending=False
)

results_df.to_csv(
    EVALUATION_DIR / "evaluation_summary.csv",
    index=False
)


# ============================================================
# COMPARATIVE ROC CURVE
# ============================================================

plt.figure(figsize=(9, 7))

for name, y_true, y_probability in roc_data:

    RocCurveDisplay.from_predictions(
        y_true,
        y_probability,
        name=name
    )

plt.plot(
    [0, 1],
    [0, 1],
    linestyle="--"
)

plt.title("ROC Curve Comparison")
plt.xlabel("False Positive Rate")
plt.ylabel("True Positive Rate")

plt.tight_layout()

plt.savefig(
    ROC_DIR / "roc_curve_comparison.png",
    dpi=200
)

plt.close()


# ============================================================
# COMPARATIVE PRECISION-RECALL CURVE
# ============================================================

plt.figure(figsize=(9, 7))

for name, y_true, y_probability in pr_data:

    PrecisionRecallDisplay.from_predictions(
        y_true,
        y_probability,
        name=name
    )

plt.title("Precision-Recall Curve Comparison")
plt.xlabel("Recall")
plt.ylabel("Precision")

plt.tight_layout()

plt.savefig(
    PR_DIR / "precision_recall_comparison.png",
    dpi=200
)

plt.close()


# ============================================================
# SAVE FINAL REPORT
# ============================================================

report_path = EVALUATION_DIR / "classification_reports.txt"

with open(report_path, "w", encoding="utf-8") as report:

    report.write("MediFlow AI - Readmission Model Evaluation\n")
    report.write("=" * 70 + "\n\n")

    for name, pipeline in trained_pipelines.items():

        y_pred = pipeline.predict(X_test)

        report.write(f"\n{name}\n")
        report.write("-" * 70 + "\n")

        report.write(
            classification_report(
                y_test,
                y_pred,
                target_names=[
                    "No Readmission",
                    "Readmission"
                ],
                zero_division=0
            )
        )

        report.write("\n")


# ============================================================
# FINAL OUTPUT
# ============================================================

print("\n" + "=" * 70)
print("MODEL COMPARISON")
print("=" * 70)

print(
    results_df.to_string(
        index=False,
        float_format=lambda x: f"{x:.4f}"
    )
)

best_model = results_df.iloc[0]["model"]
best_auc = results_df.iloc[0]["roc_auc"]

print("\n" + "=" * 70)
print("BEST MODEL")
print("=" * 70)

print(f"Selected model by ROC-AUC: {best_model}")
print(f"ROC-AUC: {best_auc:.4f}")

print("\nEvaluation files saved to:")
print(EVALUATION_DIR)

print("\nEvaluation completed successfully.")