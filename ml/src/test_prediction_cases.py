from predict_readmission import predict_readmission


# ============================================================
# TEST CASES
# ============================================================

TEST_CASES = {
    "LOW RISK PATIENT": {
        "los_days": 2,
        "admit_type": "Elective",
        "ward_type": "General",
        "num_procedures": 0,
        "charlson_index": 0,
        "hba1c": 5.4,
        "creatinine": 0.8,
        "haemoglobin": 14.5,
        "systolic_bp": 120,
        "age": 28,
        "gender": "F",
        "state": "Rajasthan",
        "bpl_card": False,
        "insurance_type": "Private",
        "comorbidity_count": 0,
        "prev_admissions": 0,
        "hospital_state": "Rajasthan",
        "tier": "tier1",
        "beds": 500,
        "teaching": True,
        "diagnosis_count": 1,
        "primary_diagnosis_category": "Respiratory",
        "admission_month": 3
    },

    "MEDIUM RISK PATIENT": {
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
    },

    "HIGH RISK PATIENT": {
        "los_days": 18,
        "admit_type": "Emergency",
        "ward_type": "ICU",
        "num_procedures": 5,
        "charlson_index": 8,
        "hba1c": 11.5,
        "creatinine": 2.8,
        "haemoglobin": 8.5,
        "systolic_bp": 175,
        "age": 82,
        "gender": "M",
        "state": "Rajasthan",
        "bpl_card": True,
        "insurance_type": "Government",
        "comorbidity_count": 7,
        "prev_admissions": 6,
        "hospital_state": "Rajasthan",
        "tier": "tier2",
        "beds": 250,
        "teaching": False,
        "diagnosis_count": 4,
        "primary_diagnosis_category": "Cardiovascular",
        "admission_month": 10
    }
}


# ============================================================
# RUN TESTS
# ============================================================

print("=" * 70)
print("MEDIFLOW AI - MULTIPLE PREDICTION VALIDATION")
print("=" * 70)


for case_name, patient_data in TEST_CASES.items():

    print("\n" + "-" * 70)
    print(case_name)
    print("-" * 70)

    result = predict_readmission(
        patient_data
    )

    print(
        f"Prediction               : "
        f"{result['prediction']}"
    )

    print(
        f"Readmission Probability  : "
        f"{result['readmission_probability']}"
    )

    print(
        f"Risk Category            : "
        f"{result['risk_category']}"
    )


print("\n" + "=" * 70)
print("MULTIPLE PREDICTION VALIDATION COMPLETED")
print("=" * 70)