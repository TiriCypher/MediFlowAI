from pathlib import Path

import joblib
import pandas as pd

from sklearn.compose import ColumnTransformer
from sklearn.ensemble import RandomForestClassifier
from sklearn.impute import SimpleImputer
from sklearn.linear_model import LogisticRegression
from sklearn.metrics import (
    accuracy_score,
    precision_score,
    recall_score,
    f1_score,
    roc_auc_score
)
from sklearn.model_selection import train_test_split
from sklearn.pipeline import Pipeline
from sklearn.preprocessing import OneHotEncoder, StandardScaler
from sklearn.tree import DecisionTreeClassifier


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

MODEL_DIR = BASE_DIR / "models"

EVALUATION_DIR = (
    BASE_DIR
    / "evaluation"
    / "final_model_training"
)

MODEL_DIR.mkdir(exist_ok=True)
EVALUATION_DIR.mkdir(
    parents=True,
    exist_ok=True
)


# ============================================================
# LOAD DATA
# ============================================================

print("=" * 70)
print("MEDIFLOW AI - FINAL LEAKAGE-SAFE MODEL TRAINING")
print("=" * 70)

df = pd.read_csv(DATA_PATH)

print("\nDataset loaded successfully.")
print(f"Rows    : {len(df):,}")
print(f"Columns : {len(df.columns)}")


# ============================================================
# TARGET
# ============================================================

TARGET_COLUMN = "readmitted_30d"


# ============================================================
# REMOVE UNSAFE / INVALID FEATURES
# ============================================================

EXCLUDED_FEATURES = [
    TARGET_COLUMN,

    # Secondary target
    "readmitted_7d",

    # Discharge outcome should not be used
    # as a predictor for future readmission
    "discharge_type",

    # Redundant engineered features
    "age_group",
    "comorbidity_level",
    "previous_admission_level",
    "los_group",

    # Optional temporal features excluded
    # for cleaner generalization
    "admission_year",
]


available_columns = df.columns.tolist()

EXCLUDED_FEATURES = [
    column
    for column in EXCLUDED_FEATURES
    if column in available_columns
]


FEATURE_COLUMNS = [
    column
    for column in df.columns
    if column not in EXCLUDED_FEATURES
]


X = df[FEATURE_COLUMNS]
y = df[TARGET_COLUMN]


print("\nSelected features:", len(FEATURE_COLUMNS))

print("\nExcluded features:")
for feature in EXCLUDED_FEATURES:
    print("-", feature)


print("\nTarget distribution:")
print(y.value_counts())

print("\nTarget percentage:")
print(
    (
        y.value_counts(
            normalize=True
        ) * 100
    ).round(2)
)


# ============================================================
# FEATURE TYPES
# ============================================================

NUMERIC_FEATURES = X.select_dtypes(
    include=[
        "int64",
        "float64",
        "int32",
        "float32",
        "bool"
    ]
).columns.tolist()


CATEGORICAL_FEATURES = [
    column
    for column in X.columns
    if column not in NUMERIC_FEATURES
]


print("\nNumeric features:")
print(NUMERIC_FEATURES)

print("\nCategorical features:")
print(CATEGORICAL_FEATURES)


# ============================================================
# PREPROCESSING
# ============================================================

numeric_transformer = Pipeline(
    steps=[
        (
            "imputer",
            SimpleImputer(
                strategy="median"
            )
        ),
        (
            "scaler",
            StandardScaler()
        )
    ]
)


categorical_transformer = Pipeline(
    steps=[
        (
            "imputer",
            SimpleImputer(
                strategy="most_frequent"
            )
        ),
        (
            "onehot",
            OneHotEncoder(
                handle_unknown="ignore"
            )
        )
    ]
)


preprocessor = ColumnTransformer(
    transformers=[
        (
            "numeric",
            numeric_transformer,
            NUMERIC_FEATURES
        ),
        (
            "categorical",
            categorical_transformer,
            CATEGORICAL_FEATURES
        )
    ]
)


# ============================================================
# TRAIN / TEST SPLIT
# ============================================================

X_train, X_test, y_train, y_test = train_test_split(
    X,
    y,
    test_size=0.20,
    random_state=42,
    stratify=y
)


print("\nTrain/Test split:")
print(
    "Training records :",
    f"{len(X_train):,}"
)
print(
    "Testing records  :",
    f"{len(X_test):,}"
)


# ============================================================
# MODELS
# ============================================================

models = {
    "Logistic Regression":
        LogisticRegression(
            max_iter=2000,
            class_weight="balanced",
            random_state=42
        ),

    "Decision Tree":
        DecisionTreeClassifier(
            class_weight="balanced",
            random_state=42,
            max_depth=15,
            min_samples_split=20,
            min_samples_leaf=10
        ),

    "Random Forest":
        RandomForestClassifier(
            n_estimators=200,
            class_weight="balanced",
            random_state=42,
            n_jobs=-1,
            min_samples_leaf=5
        )
}


results = []

trained_pipelines = {}


# ============================================================
# TRAIN MODELS
# ============================================================

print("\n" + "=" * 70)
print("TRAINING MODELS")
print("=" * 70)


for model_name, model in models.items():

    print(f"\nTraining: {model_name}")

    pipeline = Pipeline(
        steps=[
            (
                "preprocessor",
                preprocessor
            ),
            (
                "model",
                model
            )
        ]
    )

    pipeline.fit(
        X_train,
        y_train
    )

    predictions = pipeline.predict(
        X_test
    )

    probabilities = pipeline.predict_proba(
        X_test
    )[:, 1]


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

    roc_auc = roc_auc_score(
        y_test,
        probabilities
    )


    print(
        f"Accuracy : {accuracy:.4f}"
    )

    print(
        f"Precision: {precision:.4f}"
    )

    print(
        f"Recall   : {recall:.4f}"
    )

    print(
        f"F1 Score : {f1:.4f}"
    )

    print(
        f"ROC-AUC  : {roc_auc:.4f}"
    )


    results.append(
        {
            "model": model_name,
            "accuracy": accuracy,
            "precision": precision,
            "recall": recall,
            "f1_score": f1,
            "roc_auc": roc_auc
        }
    )


    trained_pipelines[
        model_name
    ] = pipeline


# ============================================================
# MODEL COMPARISON
# ============================================================

results_df = pd.DataFrame(
    results
)

results_df = results_df.sort_values(
    by="roc_auc",
    ascending=False
)


print("\n" + "=" * 70)
print("FINAL MODEL COMPARISON")
print("=" * 70)

print(
    results_df.to_string(
        index=False
    )
)


comparison_path = (
    EVALUATION_DIR
    / "final_model_comparison.csv"
)

results_df.to_csv(
    comparison_path,
    index=False
)


# ============================================================
# SELECT BEST MODEL
# ============================================================

best_model_name = (
    results_df.iloc[0]["model"]
)


best_pipeline = (
    trained_pipelines[
        best_model_name
    ]
)


model_path = (
    MODEL_DIR
    / "final_readmission_model.joblib"
)


joblib.dump(
    best_pipeline,
    model_path
)


# ============================================================
# SAVE FEATURE LIST
# ============================================================

feature_path = (
    EVALUATION_DIR
    / "final_selected_features.csv"
)


pd.DataFrame(
    {
        "feature": FEATURE_COLUMNS
    }
).to_csv(
    feature_path,
    index=False
)


# ============================================================
# SUMMARY
# ============================================================

print("\n" + "=" * 70)
print("BEST FINAL MODEL")
print("=" * 70)

print(
    f"\nSelected model: {best_model_name}"
)

print(
    f"Saved model: {model_path}"
)

print(
    f"Comparison: {comparison_path}"
)

print(
    f"Features: {feature_path}"
)


print("\n" + "=" * 70)
print("FINAL MODEL TRAINING COMPLETED SUCCESSFULLY")
print("=" * 70)