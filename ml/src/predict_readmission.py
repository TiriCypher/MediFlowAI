from pathlib import Path

import joblib
import pandas as pd


# ============================================================
# PATH CONFIGURATION
# ============================================================

BASE_DIR = Path(__file__).resolve().parent.parent

MODEL_PATH = (
    BASE_DIR
    / "models"
    / "final_readmission_model.joblib"
)


# ============================================================
# RISK THRESHOLDS
# ============================================================

LOW_RISK_THRESHOLD = 0.40
HIGH_RISK_THRESHOLD = 0.65


# ============================================================
# LOAD MODEL
# ============================================================

_model = None


def load_model():
    """
    Load the trained MediFlow AI readmission model.
    """

    global _model

    if _model is None:

        if not MODEL_PATH.exists():
            raise FileNotFoundError(
                f"Model not found: {MODEL_PATH}"
            )

        _model = joblib.load(MODEL_PATH)

    return _model


# ============================================================
# RISK CATEGORY
# ============================================================

def get_risk_category(probability: float) -> str:
    """
    Convert readmission probability into a risk category.
    """

    if probability < LOW_RISK_THRESHOLD:
        return "LOW"

    if probability < HIGH_RISK_THRESHOLD:
        return "MEDIUM"

    return "HIGH"


# ============================================================
# PREDICTION FUNCTION
# ============================================================

def predict_readmission(patient_data: dict) -> dict:
    """
    Predict 30-day hospital readmission risk.

    Parameters
    ----------
    patient_data : dict
        Dictionary containing all model input features.

    Returns
    -------
    dict
        Prediction result containing:
        - prediction
        - readmission_probability
        - risk_category
    """

    model = load_model()

    input_df = pd.DataFrame(
        [patient_data]
    )

    required_features = list(
        model.feature_names_in_
    )

    missing_features = [
        feature
        for feature in required_features
        if feature not in input_df.columns
    ]

    if missing_features:

        raise ValueError(
            "Missing required features: "
            + ", ".join(missing_features)
        )

    input_df = input_df[
        required_features
    ]

    probability = float(
        model.predict_proba(input_df)[0][1]
    )

    prediction = int(
        probability >= 0.50
    )

    risk_category = get_risk_category(
        probability
    )

    return {
        "prediction": prediction,
        "readmission_probability": round(
            probability,
            4
        ),
        "risk_category": risk_category
    }


# ============================================================
# TEST EXECUTION
# ============================================================

if __name__ == "__main__":

    print("=" * 70)
    print("MEDIFLOW AI - READMISSION PREDICTION TEST")
    print("=" * 70)

    sample_patient = {
        "los_days": 7,
        "admit_type": "Emergency",
        "ward_type": "General",
        "num_procedures": 2,
        "charlson_index": 3,
        "hba1c": 8.2,
        "creatinine": 1.4,
        "haemoglobin": 11.2,
        "systolic_bp": 145,
        "age": 68,
        "gender": "M",
        "state": "Rajasthan",
        "bpl_card": False,
        "insurance_type": "Private",
        "comorbidity_count": 3,
        "prev_admissions": 2,
        "hospital_state": "Rajasthan",
        "tier": "tier2",
        "beds": 350,
        "teaching": True,
        "diagnosis_count": 2,
        "primary_diagnosis_category": "Cardiovascular",
        "admission_month": 8
    }

    result = predict_readmission(
        sample_patient
    )

    print("\nPrediction Result:")

    print(
        f"Prediction: "
        f"{result['prediction']}"
    )

    print(
        f"Readmission Probability: "
        f"{result['readmission_probability']}"
    )

    print(
        f"Risk Category: "
        f"{result['risk_category']}"
    )

    print("\n" + "=" * 70)
    print("PREDICTION TEST COMPLETED SUCCESSFULLY")
    print("=" * 70)