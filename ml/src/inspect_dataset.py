from pathlib import Path
import pandas as pd


BASE_DIR = Path(__file__).resolve().parent.parent
RAW_DATA_DIR = BASE_DIR / "data" / "raw"

FILES = [
    "admissions.csv",
    "billing.csv",
    "diagnoses.csv",
    "hospitals.csv",
    "patients.csv",
]


def inspect_file(file_name: str):
    file_path = RAW_DATA_DIR / file_name

    print("\n" + "=" * 80)
    print(f"FILE: {file_name}")
    print("=" * 80)

    if not file_path.exists():
        print(f"ERROR: File not found -> {file_path}")
        return

    df = pd.read_csv(file_path)

    print(f"\nRows: {df.shape[0]}")
    print(f"Columns: {df.shape[1]}")

    print("\nCOLUMN NAMES:")
    for column in df.columns:
        print(f"- {column}")

    print("\nDATA TYPES:")
    print(df.dtypes.to_string())

    print("\nMISSING VALUES:")
    missing = df.isnull().sum()
    missing = missing[missing > 0]

    if missing.empty:
        print("No missing values found.")
    else:
        print(missing.to_string())

    print(f"\nDUPLICATE ROWS: {df.duplicated().sum()}")

    print("\nFIRST 3 ROWS:")
    print(df.head(3).to_string())

    print("\n" + "-" * 80)


def main():
    print("=" * 80)
    print("MEDIFLOW AI - DATASET INSPECTION")
    print("=" * 80)

    for file_name in FILES:
        inspect_file(file_name)

    print("\nDATASET INSPECTION COMPLETED.")


if __name__ == "__main__":
    main()