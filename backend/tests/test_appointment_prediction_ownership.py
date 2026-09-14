from datetime import date, datetime
from decimal import Decimal

from app.models.appointment import Appointment
from app.models.doctor import Doctor
from app.models.ml_prediction import MLPrediction
from app.models.patient import Patient
from app.models.user import User


def seed_ownership_data(db_session):
    now = datetime.now()

    doctor_user_1 = User(
        id=200,
        firebase_uid="doctor-one",
        name="Doctor One",
        email="doctor1@example.com",
        role="DOCTOR",
    )

    doctor_user_2 = User(
        id=201,
        firebase_uid="doctor-two",
        name="Doctor Two",
        email="doctor2@example.com",
        role="DOCTOR",
    )

    patient_user_1 = User(
        id=202,
        firebase_uid="patient-one",
        name="Patient One",
        email="patient1@example.com",
        role="PATIENT",
    )

    patient_user_2 = User(
        id=203,
        firebase_uid="patient-two",
        name="Patient Two",
        email="patient2@example.com",
        role="PATIENT",
    )

    db_session.add_all([
        doctor_user_1,
        doctor_user_2,
        patient_user_1,
        patient_user_2,
    ])

    db_session.flush()

    doctor_1 = Doctor(
        id=200,
        user_id=doctor_user_1.id,
        department_id=None,
        specialization="Cardiology",
        availability="MON-FRI 09:00-17:00",
        created_at=now,
    )

    doctor_2 = Doctor(
        id=201,
        user_id=doctor_user_2.id,
        department_id=None,
        specialization="Neurology",
        availability="MON-FRI 09:00-17:00",
        created_at=now,
    )

    db_session.add_all([
        doctor_1,
        doctor_2,
    ])

    patient_1 = Patient(
        id=202,
        user_id=patient_user_1.id,
        name="Assigned Patient",
        date_of_birth=date(1960, 1, 1),
        gender="F",
        blood_group="O+",
        phone="1111111111",
        address="Jaipur",
        medical_information="Test",
        created_at=now,
    )

    patient_2 = Patient(
        id=203,
        user_id=patient_user_2.id,
        name="Other Patient",
        date_of_birth=date(1970, 1, 1),
        gender="M",
        blood_group="A+",
        phone="2222222222",
        address="Jaipur",
        medical_information="Test",
        created_at=now,
    )

    db_session.add_all([
        patient_1,
        patient_2,
    ])

    db_session.flush()

    appointment_1 = Appointment(
        id=200,
        patient_id=patient_1.id,
        doctor_id=doctor_1.id,
        department_id=None,
        appointment_time=datetime(
            2026,
            9,
            20,
            10,
            0,
        ),
        priority=3,
        status="SCHEDULED",
        reason="Patient 1 with Doctor 1",
        created_at=now,
    )

    appointment_2 = Appointment(
        id=201,
        patient_id=patient_2.id,
        doctor_id=doctor_2.id,
        department_id=None,
        appointment_time=datetime(
            2026,
            9,
            20,
            11,
            0,
        ),
        priority=3,
        status="SCHEDULED",
        reason="Patient 2 with Doctor 2",
        created_at=now,
    )

    db_session.add_all([
        appointment_1,
        appointment_2,
    ])

    prediction_1 = MLPrediction(
        id=200,
        patient_id=patient_1.id,
        risk_score=Decimal("0.6500"),
        risk_level="HIGH",
        model_version="1.0.0",
        created_at=now,
    )

    prediction_2 = MLPrediction(
        id=201,
        patient_id=patient_2.id,
        risk_score=Decimal("0.2500"),
        risk_level="LOW",
        model_version="1.0.0",
        created_at=now,
    )

    db_session.add_all([
        prediction_1,
        prediction_2,
    ])

    db_session.commit()

    return {
        "doctor_user_1": doctor_user_1,
        "doctor_user_2": doctor_user_2,
        "patient_user_1": patient_user_1,
        "patient_user_2": patient_user_2,
        "doctor_1": doctor_1,
        "doctor_2": doctor_2,
        "patient_1": patient_1,
        "patient_2": patient_2,
        "appointment_1": appointment_1,
        "appointment_2": appointment_2,
    }


# --------------------------------------------------
# PATIENT APPOINTMENT OWNERSHIP
# --------------------------------------------------

def test_patient_sees_only_own_appointments(
    client,
    login_as,
    db_session,
):
    data = seed_ownership_data(db_session)

    login_as(data["patient_user_1"])

    response = client.get("/appointments")

    assert response.status_code == 200

    appointments = response.json()

    assert len(appointments) == 1
    assert (
        appointments[0]["id"]
        == data["appointment_1"].id
    )


def test_patient_can_access_own_appointment(
    client,
    login_as,
    db_session,
):
    data = seed_ownership_data(db_session)

    login_as(data["patient_user_1"])

    response = client.get(
        f"/appointments/"
        f"{data['appointment_1'].id}"
    )

    assert response.status_code == 200


def test_patient_cannot_access_other_appointment(
    client,
    login_as,
    db_session,
):
    data = seed_ownership_data(db_session)

    login_as(data["patient_user_1"])

    response = client.get(
        f"/appointments/"
        f"{data['appointment_2'].id}"
    )

    assert response.status_code == 403


# --------------------------------------------------
# DOCTOR APPOINTMENT OWNERSHIP
# --------------------------------------------------

def test_doctor_sees_only_assigned_appointments(
    client,
    login_as,
    db_session,
):
    data = seed_ownership_data(db_session)

    login_as(data["doctor_user_1"])

    response = client.get("/appointments")

    assert response.status_code == 200

    appointments = response.json()

    assert len(appointments) == 1
    assert (
        appointments[0]["id"]
        == data["appointment_1"].id
    )


def test_doctor_can_access_assigned_appointment(
    client,
    login_as,
    db_session,
):
    data = seed_ownership_data(db_session)

    login_as(data["doctor_user_1"])

    response = client.get(
        f"/appointments/"
        f"{data['appointment_1'].id}"
    )

    assert response.status_code == 200


def test_doctor_cannot_access_unassigned_appointment(
    client,
    login_as,
    db_session,
):
    data = seed_ownership_data(db_session)

    login_as(data["doctor_user_1"])

    response = client.get(
        f"/appointments/"
        f"{data['appointment_2'].id}"
    )

    assert response.status_code == 403


# --------------------------------------------------
# PATIENT PREDICTION HISTORY OWNERSHIP
# --------------------------------------------------

def test_patient_can_view_own_prediction_history(
    client,
    login_as,
    db_session,
):
    data = seed_ownership_data(db_session)

    login_as(data["patient_user_1"])

    response = client.get(
        f"/predictions/patients/"
        f"{data['patient_1'].id}"
    )

    assert response.status_code == 200

    predictions = response.json()

    assert len(predictions) == 1
    assert (
        predictions[0]["patient_id"]
        == data["patient_1"].id
    )


def test_patient_cannot_view_other_prediction_history(
    client,
    login_as,
    db_session,
):
    data = seed_ownership_data(db_session)

    login_as(data["patient_user_1"])

    response = client.get(
        f"/predictions/patients/"
        f"{data['patient_2'].id}"
    )

    assert response.status_code == 403


# --------------------------------------------------
# DOCTOR PREDICTION HISTORY OWNERSHIP
# --------------------------------------------------

def test_doctor_can_view_assigned_patient_prediction_history(
    client,
    login_as,
    db_session,
):
    data = seed_ownership_data(db_session)

    login_as(data["doctor_user_1"])

    response = client.get(
        f"/predictions/patients/"
        f"{data['patient_1'].id}"
    )

    assert response.status_code == 200

    predictions = response.json()

    assert len(predictions) == 1


def test_doctor_cannot_view_unassigned_patient_prediction_history(
    client,
    login_as,
    db_session,
):
    data = seed_ownership_data(db_session)

    login_as(data["doctor_user_1"])

    response = client.get(
        f"/predictions/patients/"
        f"{data['patient_2'].id}"
    )

    assert response.status_code == 403