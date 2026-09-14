from app.services.ml_prediction_service import (
    MLPredictionService,
)


patient_data = {
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
    "admission_month": 8,
}


result = MLPredictionService.predict_readmission(
    patient_data
)


print("=" * 70)
print("MEDIFLOW AI - BACKEND ML SERVICE TEST")
print("=" * 70)

print()

print(
    "Prediction:",
    result["prediction"],
)

print(
    "Readmission Probability:",
    result["readmission_probability"],
)

print(
    "Risk Category:",
    result["risk_category"],
)

print()
print("=" * 70)
print("ML SERVICE TEST COMPLETED")
print("=" * 70)