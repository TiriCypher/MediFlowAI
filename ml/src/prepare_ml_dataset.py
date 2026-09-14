from pathlib import Path

import pandas as pd


# ============================================================
# PATH CONFIGURATION
# ============================================================

BASE_DIR = Path(__file__).resolve().parent.parent
DATA_DIR = BASE_DIR / "data" / "raw"
OUTPUT_DIR = BASE_DIR / "data" / "processed"

OUTPUT_DIR.mkdir(parents=True, exist_ok=True)


# ============================================================
# LOAD DATA
# ============================================================

def load_data():
    print("=" * 70)
    print("MEDIFLOW AI - ML DATASET PREPARATION")
    print("=" * 70)

    admissions = pd.read_csv(DATA_DIR / "admissions.csv")
    patients = pd.read_csv(DATA_DIR / "patients.csv")
    hospitals = pd.read_csv(DATA_DIR / "hospitals.csv")
    diagnoses = pd.read_csv(DATA_DIR / "diagnoses.csv")

    print("\nData loaded successfully.")

    print(f"Admissions: {len(admissions)}")
    print(f"Patients: {len(patients)}")
    print(f"Hospitals: {len(hospitals)}")
    print(f"Diagnoses: {len(diagnoses)}")

    return admissions, patients, hospitals, diagnoses


# ============================================================
# DIAGNOSIS FEATURE ENGINEERING
# ============================================================

def create_diagnosis_features(diagnoses):

    print("\nCreating diagnosis features...")

    diagnosis_count = (
        diagnoses
        .groupby("admission_id")
        .size()
        .reset_index(name="diagnosis_count")
    )

    primary_category = (
        diagnoses[
            diagnoses["diag_rank"] == 1
        ][
            ["admission_id", "diag_category"]
        ]
        .rename(
            columns={
                "diag_category": "primary_diagnosis_category"
            }
        )
    )

    diagnosis_features = (
        diagnosis_count
        .merge(
            primary_category,
            on="admission_id",
            how="left"
        )
    )

    return diagnosis_features


# ============================================================
# MERGE DATASETS
# ============================================================

def merge_datasets(
    admissions,
    patients,
    hospitals,
    diagnosis_features
):

    print("\nMerging datasets...")

    df = admissions.merge(
        patients,
        on="patient_id",
        how="left"
    )

    df = df.merge(
        hospitals[
            [
                "hospital_id",
                "state",
                "tier",
                "beds",
                "teaching"
            ]
        ].rename(
            columns={
                "state": "hospital_state"
            }
        ),
        on="hospital_id",
        how="left"
    )

    df = df.merge(
        diagnosis_features,
        on="admission_id",
        how="left"
    )

    print(f"Merged records: {len(df)}")

    return df


# ============================================================
# FEATURE ENGINEERING
# ============================================================

def create_features(df):

    print("\nCreating engineered features...")

    # --------------------------------------------------------
    # AGE GROUP
    # --------------------------------------------------------

    df["age_group"] = pd.cut(
        df["age"],
        bins=[-1, 17, 35, 50, 65, 120],
        labels=[
            "Child",
            "Young Adult",
            "Adult",
            "Senior",
            "Elderly"
        ]
    )

    # --------------------------------------------------------
    # COMORBIDITY RISK GROUP
    # --------------------------------------------------------

    df["comorbidity_level"] = pd.cut(
        df["comorbidity_count"],
        bins=[-1, 0, 2, 4, 100],
        labels=[
            "None",
            "Low",
            "Moderate",
            "High"
        ]
    )

    # --------------------------------------------------------
    # PREVIOUS ADMISSION GROUP
    # --------------------------------------------------------

    df["previous_admission_level"] = pd.cut(
        df["prev_admissions"],
        bins=[-1, 0, 1, 3, 100],
        labels=[
            "None",
            "Low",
            "Moderate",
            "High"
        ]
    )

    # --------------------------------------------------------
    # LENGTH OF STAY GROUP
    # --------------------------------------------------------

    df["los_group"] = pd.cut(
        df["los_days"],
        bins=[0, 2, 5, 10, 30, 1000],
        labels=[
            "Very Short",
            "Short",
            "Medium",
            "Long",
            "Very Long"
        ]
    )

    # --------------------------------------------------------
    # ADMISSION YEAR
    # --------------------------------------------------------

    df["admit_date"] = pd.to_datetime(
        df["admit_date"]
    )

    df["admission_year"] = (
        df["admit_date"].dt.year
    )

    # --------------------------------------------------------
    # ADMISSION MONTH
    # --------------------------------------------------------

    df["admission_month"] = (
        df["admit_date"].dt.month
    )

    return df


# ============================================================
# MISSING VALUE HANDLING
# ============================================================

def handle_missing_values(df):

    print("\nHandling missing values...")

    df["insurance_type"] = (
        df["insurance_type"]
        .fillna("Unknown")
    )

    return df


# ============================================================
# REMOVE ML-UNSAFE / IDENTIFIER COLUMNS
# ============================================================

def prepare_ml_columns(df):

    print("\nRemoving identifiers and unsafe columns...")

    columns_to_remove = [

        # Identifiers
        "admission_id",
        "patient_id",
        "hospital_id",

        # Dates
        "admit_date",
        "discharge_date",

        # Target-related leakage
        "readmitted_7d"
    ]

    existing_columns = [
        column
        for column in columns_to_remove
        if column in df.columns
    ]

    df = df.drop(
        columns=existing_columns
    )

    return df


# ============================================================
# VALIDATION
# ============================================================

def validate_dataset(df):

    print("\n" + "=" * 70)
    print("ML DATASET VALIDATION")
    print("=" * 70)

    print(f"\nTotal records: {len(df)}")
    print(f"Total columns: {len(df.columns)}")

    print("\nTARGET DISTRIBUTION:")

    print(
        df["readmitted_30d"]
        .value_counts()
        .sort_index()
    )

    print("\nMISSING VALUES:")

    missing = (
        df
        .isnull()
        .sum()
    )

    missing = missing[
        missing > 0
    ]

    if len(missing) == 0:

        print("No missing values found.")

    else:

        print(missing)

    print("\nFINAL COLUMNS:")

    for column in df.columns:
        print(f"- {column}")


# ============================================================
# SAVE DATASET
# ============================================================

def save_dataset(df):

    output_file = (
        OUTPUT_DIR
        / "readmission_ml_dataset.csv"
    )

    df.to_csv(
        output_file,
        index=False
    )

    print("\nDataset saved successfully.")

    print(
        f"\nOutput file:\n{output_file}"
    )


# ============================================================
# MAIN
# ============================================================

def main():

    (
        admissions,
        patients,
        hospitals,
        diagnoses
    ) = load_data()

    diagnosis_features = (
        create_diagnosis_features(
            diagnoses
        )
    )

    df = merge_datasets(
        admissions,
        patients,
        hospitals,
        diagnosis_features
    )

    df = create_features(df)

    df = handle_missing_values(df)

    df = prepare_ml_columns(df)

    validate_dataset(df)

    save_dataset(df)

    print("\n" + "=" * 70)
    print("ML DATASET PREPARATION COMPLETED SUCCESSFULLY")
    print("=" * 70)


if __name__ == "__main__":
    main()