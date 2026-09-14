from datetime import date, datetime
from decimal import Decimal

from app.models.appointment import Appointment
from app.models.doctor import Doctor
from app.models.ml_prediction import MLPrediction
from app.models.patient import Patient
from app.models.user import User


def seed_optimization_ownership(
    db_session,
):
    now = datetime.now()

    admin = User(
        id=400,
        firebase_uid="optimization-admin",
        name="Optimization Admin",
        email="optimization.admin@example.com",
        role="ADMIN",
    )

    staff = User(
        id=401,
        firebase_uid="optimization-staff",
        name="Optimization Staff",
        email="optimization.staff@example.com",
        role="STAFF",
    )

    doctor_user_1 = User(
        id=402,
        firebase_uid="optimization-doctor-1",
        name="Doctor One",
        email="optimization.doctor1@example.com",
        role="DOCTOR",
    )

    doctor_user_2 = User(
        id=403,
        firebase_uid="optimization-doctor-2",
        name="Doctor Two",
        email="optimization.doctor2@example.com",
        role="DOCTOR",
    )

    patient_user_1 = User(
        id=404,
        firebase_uid="optimization-patient-1",
        name="Patient One",
        email="optimization.patient1@example.com",
        role="PATIENT",
    )

    patient_user_2 = User(
        id=405,
        firebase_uid="optimization-patient-2",
        name="Patient Two",
        email="optimization.patient2@example.com",
        role="PATIENT",
    )

    db_session.add_all([
        admin,
        staff,
        doctor_user_1,
        doctor_user_2,
        patient_user_1,
        patient_user_2,
    ])

    db_session.flush()

    doctor_1 = Doctor(
        id=400,
        user_id=doctor_user_1.id,
        department_id=None,
        specialization="Cardiology",
        availability="MON-FRI 09:00-17:00",
    )

    doctor_2 = Doctor(
        id=401,
        user_id=doctor_user_2.id,
        department_id=None,
        specialization="Neurology",
        availability="MON-FRI 09:00-17:00",
    )

    patient_1 = Patient(
        id=404,
        user_id=patient_user_1.id,
        name="Assigned Patient",
        date_of_birth=date(
            1960,
            1,
            1,
        ),
        gender="F",
        blood_group="O+",
        phone="1111111111",
        address="Jaipur",
        medical_information="Test",
    )

    patient_2 = Patient(
        id=405,
        user_id=patient_user_2.id,
        name="Unrelated Patient",
        date_of_birth=date(
            1970,
            1,
            1,
        ),
        gender="M",
        blood_group="A+",
        phone="2222222222",
        address="Jaipur",
        medical_information="Test",
    )

    db_session.add_all([
        doctor_1,
        doctor_2,
        patient_1,
        patient_2,
    ])

    db_session.flush()

    appointment = Appointment(
        id=400,
        patient_id=patient_1.id,
        doctor_id=doctor_1.id,
        department_id=None,
        appointment_time=datetime(
            2026,
            9,
            25,
            10,
            0,
        ),
        priority=3,
        status="SCHEDULED",
        reason="Optimization ownership test",
    )

    prediction_1 = MLPrediction(
        id=400,
        patient_id=patient_1.id,
        risk_score=Decimal("0.7000"),
        risk_level="HIGH",
        model_version="1.0.0",
        created_at=now,
    )

    prediction_2 = MLPrediction(
        id=401,
        patient_id=patient_2.id,
        risk_score=Decimal("0.4500"),
        risk_level="MEDIUM",
        model_version="1.0.0",
        created_at=now,
    )

    db_session.add_all([
        appointment,
        prediction_1,
        prediction_2,
    ])

    db_session.commit()

    return {
        "admin": admin,
        "staff": staff,
        "doctor_user_1": doctor_user_1,
        "doctor_user_2": doctor_user_2,
        "patient_user_1": patient_user_1,
        "patient_1": patient_1,
        "patient_2": patient_2,
    }


def priority_payload(patient_id):
    return {
        "patient_id": patient_id,
        "urgency": "EMERGENCY",
        "waiting_time_minutes": 90,
        "comorbidity_count": 4,
        "age": 78,
    }


def test_doctor_can_access_assigned_patient_priority(
    client,
    login_as,
    db_session,
):
    data = seed_optimization_ownership(
        db_session
    )

    login_as(
        data["doctor_user_1"]
    )

    response = client.post(
        (
            "/optimization/"
            "patient-risk-priority-dashboard"
        ),
        json=priority_payload(
            data["patient_1"].id
        ),
    )

    assert response.status_code == 200


def test_doctor_cannot_access_unassigned_patient_priority(
    client,
    login_as,
    db_session,
):
    data = seed_optimization_ownership(
        db_session
    )

    login_as(
        data["doctor_user_1"]
    )

    response = client.post(
        (
            "/optimization/"
            "patient-risk-priority-dashboard"
        ),
        json=priority_payload(
            data["patient_2"].id
        ),
    )

    assert response.status_code == 403


def test_staff_cannot_access_clinical_risk_priority(
    client,
    login_as,
    db_session,
):
    data = seed_optimization_ownership(
        db_session
    )

    login_as(
        data["staff"]
    )

    response = client.post(
        (
            "/optimization/"
            "patient-risk-priority-dashboard"
        ),
        json=priority_payload(
            data["patient_1"].id
        ),
    )

    assert response.status_code == 403


def test_patient_cannot_access_risk_priority(
    client,
    login_as,
    db_session,
):
    data = seed_optimization_ownership(
        db_session
    )

    login_as(
        data["patient_user_1"]
    )

    response = client.post(
        (
            "/optimization/"
            "patient-risk-priority-dashboard"
        ),
        json=priority_payload(
            data["patient_1"].id
        ),
    )

    assert response.status_code == 403


def test_admin_can_access_any_patient_priority(
    client,
    login_as,
    db_session,
):
    data = seed_optimization_ownership(
        db_session
    )

    login_as(
        data["admin"]
    )

    response = client.post(
        (
            "/optimization/"
            "patient-risk-priority-dashboard"
        ),
        json=priority_payload(
            data["patient_2"].id
        ),
    )

    assert response.status_code == 200