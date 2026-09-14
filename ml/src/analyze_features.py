from pathlib import Path

import pandas as pd
import numpy as np


# ============================================================
# PATHS
# ============================================================

BASE_DIR = Path(__file__).resolve().parents[1]

DATA_PATH = BASE_DIR / "data" / "processed" / "readmission_ml_dataset.csv"
OUTPUT_DIR = BASE_DIR / "evaluation" / "feature_analysis"

OUTPUT_DIR.mkdir(parents=True, exist_ok=True)


# ============================================================
# CONFIGURATION
# ============================================================

TARGET = "readmitted_30d"

# Features that should never be used for model training
KNOWN_EXCLUDED = {
    "admission_id": "Identifier; no predictive meaning",
    "patient_id": "Identifier; no predictive meaning",
    "hospital_id": "Identifier; may cause memorization",
    "admit_date": "Raw date; replaced with engineered time features",
    "discharge_date": "Raw date; replaced with length-of-stay information",
    "readmitted_7d": "Directly related to readmission target and creates leakage",
}


# ============================================================
# LOAD DATA
# ============================================================

print("=" * 70)
print("MEDIFLOW AI - FEATURE AUDIT & LEAKAGE REVIEW")
print("=" * 70)

if not DATA_PATH.exists():
    raise FileNotFoundError(
        f"Dataset not found:\n{DATA_PATH}\n"
        "Run prepare_ml_dataset.py first."
    )

df = pd.read_csv(DATA_PATH)

print(f"\nDataset loaded successfully.")
print(f"Rows    : {df.shape[0]:,}")
print(f"Columns : {df.shape[1]}")


# ============================================================
# BASIC VALIDATION
# ============================================================

if TARGET not in df.columns:
    raise ValueError(f"Target column '{TARGET}' not found.")

print(f"\nTarget column: {TARGET}")

print("\nTarget distribution:")
print(df[TARGET].value_counts())

print("\nTarget percentage:")
print(
    (df[TARGET].value_counts(normalize=True) * 100)
    .round(2)
)


# ============================================================
# FEATURE AUDIT
# ============================================================

rows = []

feature_columns = [
    column for column in df.columns
    if column != TARGET
]


for feature in feature_columns:

    series = df[feature]

    missing_pct = series.isna().mean() * 100
    unique_count = series.nunique(dropna=True)

    dtype = str(series.dtype)

    # --------------------------------------------------------
    # Default decision
    # --------------------------------------------------------

    decision = "KEEP"
    reason = "Potentially useful predictive feature."

    # --------------------------------------------------------
    # Known exclusions
    # --------------------------------------------------------

    if feature in KNOWN_EXCLUDED:

        decision = "EXCLUDE"
        reason = KNOWN_EXCLUDED[feature]

    # --------------------------------------------------------
    # Leakage-sensitive features
    # --------------------------------------------------------

    elif feature == "discharge_type":

        decision = "REVIEW"
        reason = (
            "Known at discharge. Valid for discharge-time prediction, "
            "but leakage for admission-time prediction."
        )

    elif feature == "los_days":

        decision = "REVIEW"
        reason = (
            "Length of stay is known at discharge. Valid for "
            "discharge-time prediction, but unavailable at admission."
        )

    # --------------------------------------------------------
    # Redundant engineered features
    # --------------------------------------------------------

    elif feature in {
        "age_group",
        "comorbidity_level",
        "previous_admission_level",
        "los_group",
    }:

        decision = "REVIEW"
        reason = (
            "Engineered categorical representation of an existing "
            "numeric feature. Useful for categorical models but "
            "may be redundant."
        )

    # --------------------------------------------------------
    # Time features
    # --------------------------------------------------------

    elif feature in {
        "admission_year",
        "admission_month",
    }:

        decision = "REVIEW"
        reason = (
            "Time-derived feature. May capture temporal patterns, "
            "but should be monitored for dataset/time drift."
        )

    # --------------------------------------------------------
    # Socio-economic / administrative feature
    # --------------------------------------------------------

    elif feature in {
        "bpl_card",
        "insurance_type",
    }:

        decision = "REVIEW"
        reason = (
            "Administrative/socio-economic feature. Potentially useful "
            "for Indian hospital context but should be checked for "
            "fairness and unnecessary dependence."
        )

    # --------------------------------------------------------
    # Missing values
    # --------------------------------------------------------

    if missing_pct > 0:

        reason += (
            f" Missing values: {missing_pct:.2f}%."
        )

    rows.append({
        "feature": feature,
        "dtype": dtype,
        "missing_percent": round(missing_pct, 2),
        "unique_values": unique_count,
        "decision": decision,
        "reason": reason,
    })


audit_df = pd.DataFrame(rows)


# ============================================================
# NUMERIC ASSOCIATION WITH TARGET
# ============================================================

numeric_features = [
    column
    for column in feature_columns
    if pd.api.types.is_numeric_dtype(df[column])
]

correlations = []

for feature in numeric_features:

    if feature in KNOWN_EXCLUDED:
        continue

    try:
        correlation = df[feature].corr(df[TARGET])

        if pd.isna(correlation):
            correlation = 0.0

    except Exception:
        correlation = 0.0

    correlations.append({
        "feature": feature,
        "target_correlation": round(float(correlation), 4),
        "absolute_correlation": round(abs(float(correlation)), 4),
    })


correlation_df = pd.DataFrame(correlations)

if not correlation_df.empty:
    correlation_df = correlation_df.sort_values(
        "absolute_correlation",
        ascending=False
    )


# ============================================================
# CATEGORICAL TARGET ASSOCIATION
# ============================================================

categorical_features = [
    column
    for column in feature_columns
    if not pd.api.types.is_numeric_dtype(df[column])
]

categorical_results = []

for feature in categorical_features:

    grouped = (
        df.groupby(feature, dropna=False)[TARGET]
        .agg(["count", "mean"])
        .reset_index()
    )

    grouped["readmission_rate_percent"] = (
        grouped["mean"] * 100
    ).round(2)

    grouped = grouped.drop(columns=["mean"])

    grouped.insert(0, "feature", feature)

    categorical_results.append(grouped)


if categorical_results:

    categorical_association_df = pd.concat(
        categorical_results,
        ignore_index=True
    )

else:

    categorical_association_df = pd.DataFrame()


# ============================================================
# HIGH-CARDINALITY CHECK
# ============================================================

high_cardinality_rows = []

for feature in feature_columns:

    unique_count = df[feature].nunique(dropna=True)

    if unique_count > 50:

        high_cardinality_rows.append({
            "feature": feature,
            "unique_values": unique_count,
            "unique_ratio_percent": round(
                unique_count / len(df) * 100,
                2
            ),
        })


high_cardinality_df = pd.DataFrame(
    high_cardinality_rows
)


# ============================================================
# SAVE REPORTS
# ============================================================

audit_path = OUTPUT_DIR / "feature_audit.csv"

correlation_path = OUTPUT_DIR / "numeric_target_correlation.csv"

categorical_path = (
    OUTPUT_DIR / "categorical_target_association.csv"
)

cardinality_path = (
    OUTPUT_DIR / "high_cardinality_features.csv"
)

audit_df.to_csv(
    audit_path,
    index=False
)

correlation_df.to_csv(
    correlation_path,
    index=False
)

categorical_association_df.to_csv(
    categorical_path,
    index=False
)

high_cardinality_df.to_csv(
    cardinality_path,
    index=False
)


# ============================================================
# SUMMARY
# ============================================================

print("\n" + "=" * 70)
print("FEATURE AUDIT SUMMARY")
print("=" * 70)

print(f"\nTotal features: {len(feature_columns)}")

print(
    f"KEEP    : {(audit_df['decision'] == 'KEEP').sum()}"
)

print(
    f"REVIEW  : {(audit_df['decision'] == 'REVIEW').sum()}"
)

print(
    f"EXCLUDE : {(audit_df['decision'] == 'EXCLUDE').sum()}"
)


# ============================================================
# PRINT EXCLUDED FEATURES
# ============================================================

excluded = audit_df[
    audit_df["decision"] == "EXCLUDE"
]

print("\nExcluded features:")

if excluded.empty:

    print("None")

else:

    for _, row in excluded.iterrows():

        print(
            f"  - {row['feature']}: {row['reason']}"
        )


# ============================================================
# PRINT REVIEW FEATURES
# ============================================================

review = audit_df[
    audit_df["decision"] == "REVIEW"
]

print("\nFeatures requiring review:")

if review.empty:

    print("None")

else:

    for _, row in review.iterrows():

        print(
            f"  - {row['feature']}: {row['reason']}"
        )


# ============================================================
# TOP NUMERIC ASSOCIATIONS
# ============================================================

print("\nTop numeric feature associations with target:")

if correlation_df.empty:

    print("No numeric features found.")

else:

    print(
        correlation_df.head(10).to_string(
            index=False
        )
    )


# ============================================================
# OUTPUT FILES
# ============================================================

print("\n" + "=" * 70)
print("REPORTS CREATED")
print("=" * 70)

print(f"\n{audit_path}")
print(correlation_path)
print(categorical_path)
print(cardinality_path)

print("\nFeature audit completed successfully.")