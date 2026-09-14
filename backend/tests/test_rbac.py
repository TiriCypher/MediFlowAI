from app.main import app


def test_system_dashboard_requires_auth(client):
    response = client.get(
        "/optimization/system-dashboard"
    )

    assert response.status_code == 401


def test_patients_requires_auth(client):
    response = client.get(
        "/patients"
    )

    assert response.status_code == 401


def test_doctors_requires_auth(client):
    response = client.get(
        "/doctors"
    )

    assert response.status_code == 401


def test_appointments_requires_auth(client):
    response = client.get(
        "/appointments"
    )

    assert response.status_code == 401


def test_beds_requires_auth(client):
    response = client.get(
        "/beds"
    )

    assert response.status_code == 401


def test_departments_requires_auth(client):
    response = client.get(
        "/departments"
    )

    assert response.status_code == 401


def test_alerts_requires_auth(client):
    response = client.get(
        "/alerts"
    )

    assert response.status_code == 401


def test_prediction_requires_auth(client):
    response = client.post(
        "/predictions/readmission",
        json={
            "los_days": 5,
            "admit_type": "Emergency",
            "ward_type": "GENERAL",
            "num_procedures": 1,
            "charlson_index": 2,
            "hba1c": 6.5,
            "creatinine": 1.1,
            "haemoglobin": 13.0,
            "systolic_bp": 120,
            "age": 60,
            "gender": "F",
            "state": "Rajasthan",
            "bpl_card": False,
            "insurance_type": "Private",
            "comorbidity_count": 2,
            "prev_admissions": 1,
            "hospital_state": "Rajasthan",
            "tier": "Tier 2",
            "beds": 200,
            "teaching": True,
            "diagnosis_count": 2,
            "primary_diagnosis_category": "Cardiovascular",
            "admission_month": 9,
        },
    )

    assert response.status_code == 401