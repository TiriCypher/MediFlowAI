from pathlib import Path

import matplotlib.pyplot as plt
import pandas as pd
import seaborn as sns


BASE_DIR = Path(__file__).resolve().parent.parent
RAW_DATA_DIR = BASE_DIR / "data" / "raw"
EDA_OUTPUT_DIR = BASE_DIR / "evaluation" / "eda"

EDA_OUTPUT_DIR.mkdir(parents=True, exist_ok=True)


def load_data():
    admissions = pd.read_csv(RAW_DATA_DIR / "admissions.csv")
    patients = pd.read_csv(RAW_DATA_DIR / "patients.csv")
    diagnoses = pd.read_csv(RAW_DATA_DIR / "diagnoses.csv")
    hospitals = pd.read_csv(RAW_DATA_DIR / "hospitals.csv")

    return admissions, patients, diagnoses, hospitals


def build_eda_dataset(admissions, patients, diagnoses, hospitals):
    diagnosis_features = (
        diagnoses.groupby("admission_id")
        .agg(
            diagnosis_count=("diag_id", "count"),
            primary_diagnosis_category=(
                "diag_category",
                "first"
            )
        )
        .reset_index()
    )

    dataset = admissions.merge(
        patients,
        on="patient_id",
        how="left",
        validate="many_to_one"
    )

    dataset = dataset.merge(
        hospitals,
        on="hospital_id",
        how="left",
        validate="many_to_one",
        suffixes=("_patient", "_hospital")
    )

    dataset = dataset.merge(
        diagnosis_features,
        on="admission_id",
        how="left",
        validate="one_to_one"
    )

    return dataset


def print_basic_summary(dataset):
    print("\n" + "=" * 70)
    print("MEDIFLOW AI - EDA SUMMARY")
    print("=" * 70)

    print(f"\nTotal records: {len(dataset)}")
    print(f"Total columns: {len(dataset.columns)}")

    print("\nREADMISSION DISTRIBUTION:")
    print(
        dataset["readmitted_30d"]
        .value_counts()
        .sort_index()
    )

    print("\nAGE SUMMARY:")
    print(dataset["age"].describe())

    print("\nCOMORBIDITY SUMMARY:")
    print(dataset["comorbidity_count"].describe())

    print("\nPREVIOUS ADMISSIONS SUMMARY:")
    print(dataset["prev_admissions"].describe())

    print("\nLENGTH OF STAY SUMMARY:")
    print(dataset["los_days"].describe())

    print("\nDIAGNOSIS COUNT SUMMARY:")
    print(dataset["diagnosis_count"].describe())


def save_readmission_distribution(dataset):
    plt.figure(figsize=(7, 5))

    sns.countplot(
        data=dataset,
        x="readmitted_30d"
    )

    plt.title("30-Day Readmission Distribution")
    plt.xlabel("Readmitted Within 30 Days")
    plt.ylabel("Number of Admissions")

    plt.tight_layout()

    plt.savefig(
        EDA_OUTPUT_DIR / "readmission_distribution.png",
        dpi=300
    )

    plt.close()


def save_age_distribution(dataset):
    plt.figure(figsize=(9, 5))

    sns.histplot(
        data=dataset,
        x="age",
        bins=30,
        kde=True
    )

    plt.title("Patient Age Distribution")
    plt.xlabel("Age")
    plt.ylabel("Number of Patients")

    plt.tight_layout()

    plt.savefig(
        EDA_OUTPUT_DIR / "age_distribution.png",
        dpi=300
    )

    plt.close()


def save_comorbidity_vs_readmission(dataset):
    plt.figure(figsize=(9, 5))

    sns.barplot(
        data=dataset,
        x="comorbidity_count",
        y="readmitted_30d",
        estimator="mean",
        errorbar=None
    )

    plt.title("Readmission Rate by Comorbidity Count")
    plt.xlabel("Comorbidity Count")
    plt.ylabel("Readmission Rate")

    plt.tight_layout()

    plt.savefig(
        EDA_OUTPUT_DIR / "comorbidity_vs_readmission.png",
        dpi=300
    )

    plt.close()


def save_previous_admissions_vs_readmission(dataset):
    summary = (
        dataset.groupby("prev_admissions")[
            "readmitted_30d"
        ]
        .mean()
        .reset_index()
    )

    plt.figure(figsize=(9, 5))

    sns.lineplot(
        data=summary,
        x="prev_admissions",
        y="readmitted_30d",
        marker="o"
    )

    plt.title(
        "Readmission Rate by Previous Admissions"
    )

    plt.xlabel("Previous Admissions")
    plt.ylabel("Readmission Rate")

    plt.tight_layout()

    plt.savefig(
        EDA_OUTPUT_DIR /
        "previous_admissions_vs_readmission.png",
        dpi=300
    )

    plt.close()


def save_admission_type_vs_readmission(dataset):
    summary = (
        dataset.groupby("admit_type")[
            "readmitted_30d"
        ]
        .mean()
        .sort_values(
            ascending=False
        )
        .reset_index()
    )

    plt.figure(figsize=(9, 5))

    sns.barplot(
        data=summary,
        x="admit_type",
        y="readmitted_30d"
    )

    plt.title(
        "Readmission Rate by Admission Type"
    )

    plt.xlabel("Admission Type")
    plt.ylabel("Readmission Rate")

    plt.xticks(rotation=20)

    plt.tight_layout()

    plt.savefig(
        EDA_OUTPUT_DIR /
        "admission_type_vs_readmission.png",
        dpi=300
    )

    plt.close()


def save_diagnosis_category_vs_readmission(
    dataset
):
    summary = (
        dataset.groupby(
            "primary_diagnosis_category"
        )["readmitted_30d"]
        .mean()
        .sort_values(
            ascending=False
        )
        .reset_index()
    )

    plt.figure(figsize=(11, 6))

    sns.barplot(
        data=summary,
        x="primary_diagnosis_category",
        y="readmitted_30d"
    )

    plt.title(
        "Readmission Rate by Primary Diagnosis Category"
    )

    plt.xlabel("Diagnosis Category")
    plt.ylabel("Readmission Rate")

    plt.xticks(rotation=45, ha="right")

    plt.tight_layout()

    plt.savefig(
        EDA_OUTPUT_DIR /
        "diagnosis_category_vs_readmission.png",
        dpi=300
    )

    plt.close()


def save_correlation_heatmap(dataset):
    columns = [
        "age",
        "los_days",
        "num_procedures",
        "charlson_index",
        "hba1c",
        "creatinine",
        "haemoglobin",
        "systolic_bp",
        "comorbidity_count",
        "prev_admissions",
        "diagnosis_count",
        "readmitted_30d",
    ]

    correlation = (
        dataset[columns]
        .corr(numeric_only=True)
    )

    plt.figure(figsize=(11, 8))

    sns.heatmap(
        correlation,
        annot=True,
        fmt=".2f",
        cmap="coolwarm"
    )

    plt.title(
        "Feature Correlation Heatmap"
    )

    plt.tight_layout()

    plt.savefig(
        EDA_OUTPUT_DIR /
        "correlation_heatmap.png",
        dpi=300
    )

    plt.close()


def main():
    admissions, patients, diagnoses, hospitals = load_data()

    dataset = build_eda_dataset(
        admissions,
        patients,
        diagnoses,
        hospitals
    )

    print_basic_summary(dataset)

    print("\nGenerating EDA charts...")

    save_readmission_distribution(dataset)
    save_age_distribution(dataset)
    save_comorbidity_vs_readmission(dataset)
    save_previous_admissions_vs_readmission(
        dataset
    )
    save_admission_type_vs_readmission(
        dataset
    )
    save_diagnosis_category_vs_readmission(
        dataset
    )
    save_correlation_heatmap(dataset)

    print(
        "\nEDA completed successfully."
    )

    print(
        f"Charts saved to:\n{EDA_OUTPUT_DIR}"
    )


if __name__ == "__main__":
    main()