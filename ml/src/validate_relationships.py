from pathlib import Path
import pandas as pd


BASE_DIR = Path(__file__).resolve().parent.parent
RAW_DATA_DIR = BASE_DIR / "data" / "raw"


def load_data():
    admissions = pd.read_csv(RAW_DATA_DIR / "admissions.csv")
    patients = pd.read_csv(RAW_DATA_DIR / "patients.csv")
    diagnoses = pd.read_csv(RAW_DATA_DIR / "diagnoses.csv")
    billing = pd.read_csv(RAW_DATA_DIR / "billing.csv")
    hospitals = pd.read_csv(RAW_DATA_DIR / "hospitals.csv")

    return admissions, patients, diagnoses, billing, hospitals


def check_primary_keys(admissions, patients, diagnoses, billing, hospitals):
    print("\n" + "=" * 70)
    print("PRIMARY KEY DUPLICATE CHECK")
    print("=" * 70)

    checks = {
        "Admissions admission_id": admissions["admission_id"].duplicated().sum(),
        "Patients patient_id": patients["patient_id"].duplicated().sum(),
        "Diagnoses diag_id": diagnoses["diag_id"].duplicated().sum(),
        "Billing bill_id": billing["bill_id"].duplicated().sum(),
        "Hospitals hospital_id": hospitals["hospital_id"].duplicated().sum(),
    }

    for name, duplicates in checks.items():
        print(f"{name}: {duplicates} duplicate values")


def check_relationships(admissions, patients, diagnoses, billing, hospitals):
    print("\n" + "=" * 70)
    print("RELATIONSHIP INTEGRITY CHECK")
    print("=" * 70)

    missing_patients = (
        ~admissions["patient_id"].isin(patients["patient_id"])
    ).sum()

    missing_hospitals = (
        ~admissions["hospital_id"].isin(hospitals["hospital_id"])
    ).sum()

    missing_billing_admissions = (
        ~billing["admission_id"].isin(admissions["admission_id"])
    ).sum()

    missing_diagnosis_admissions = (
        ~diagnoses["admission_id"].isin(admissions["admission_id"])
    ).sum()

    print(f"Admissions without matching patient: {missing_patients}")
    print(f"Admissions without matching hospital: {missing_hospitals}")
    print(
        "Billing records without matching admission: "
        f"{missing_billing_admissions}"
    )
    print(
        "Diagnosis records without matching admission: "
        f"{missing_diagnosis_admissions}"
    )


def check_admission_coverage(admissions, diagnoses, billing):
    print("\n" + "=" * 70)
    print("ADMISSION DATA COVERAGE")
    print("=" * 70)

    admissions_with_diagnosis = diagnoses["admission_id"].nunique()
    admissions_with_billing = billing["admission_id"].nunique()

    total_admissions = admissions["admission_id"].nunique()

    print(f"Total admissions: {total_admissions}")
    print(
        "Admissions with at least one diagnosis: "
        f"{admissions_with_diagnosis}"
    )
    print(
        "Admissions with billing record: "
        f"{admissions_with_billing}"
    )

    diagnosis_counts = (
        diagnoses.groupby("admission_id")
        .size()
    )

    print("\nDIAGNOSIS COUNT PER ADMISSION")
    print(f"Minimum: {diagnosis_counts.min()}")
    print(f"Maximum: {diagnosis_counts.max()}")
    print(f"Average: {diagnosis_counts.mean():.2f}")
    print(f"Median: {diagnosis_counts.median():.2f}")


def check_target_distribution(admissions):
    print("\n" + "=" * 70)
    print("READMISSION TARGET DISTRIBUTION")
    print("=" * 70)

    distribution = (
        admissions["readmitted_30d"]
        .value_counts()
        .sort_index()
    )

    percentages = (
        admissions["readmitted_30d"]
        .value_counts(normalize=True)
        .sort_index()
        * 100
    )

    for value in distribution.index:
        print(
            f"Class {value}: "
            f"{distribution[value]} "
            f"({percentages[value]:.2f}%)"
        )


def check_missing_values(patients):
    print("\n" + "=" * 70)
    print("PATIENT MISSING VALUE ANALYSIS")
    print("=" * 70)

    missing = patients.isnull().sum()

    for column, count in missing.items():
        if count > 0:
            percentage = count / len(patients) * 100

            print(
                f"{column}: "
                f"{count} missing "
                f"({percentage:.2f}%)"
            )


def test_merges(admissions, patients, hospitals):
    print("\n" + "=" * 70)
    print("MERGE VALIDATION")
    print("=" * 70)

    merged = admissions.merge(
        patients,
        on="patient_id",
        how="left",
        validate="many_to_one"
    )

    print(
        "Rows after admissions + patients merge: "
        f"{len(merged)}"
    )

    merged = merged.merge(
        hospitals,
        on="hospital_id",
        how="left",
        validate="many_to_one"
    )

    print(
        "Rows after hospital merge: "
        f"{len(merged)}"
    )

    print(
        "Missing patient data after merge: "
        f"{merged['age'].isnull().sum()}"
    )

    print(
        "Missing hospital data after merge: "
        f"{merged['beds'].isnull().sum()}"
    )


def main():
    print("=" * 70)
    print("MEDIFLOW AI - DATASET RELATIONSHIP VALIDATION")
    print("=" * 70)

    admissions, patients, diagnoses, billing, hospitals = load_data()

    check_primary_keys(
        admissions,
        patients,
        diagnoses,
        billing,
        hospitals
    )

    check_relationships(
        admissions,
        patients,
        diagnoses,
        billing,
        hospitals
    )

    check_admission_coverage(
        admissions,
        diagnoses,
        billing
    )

    check_target_distribution(admissions)

    check_missing_values(patients)

    test_merges(
        admissions,
        patients,
        hospitals
    )

    print("\n" + "=" * 70)
    print("VALIDATION COMPLETED SUCCESSFULLY")
    print("=" * 70)


if __name__ == "__main__":
    main()