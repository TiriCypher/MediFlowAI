from datetime import date, datetime

from app.models.appointment import Appointment
from app.models.doctor import Doctor
from app.models.patient import Patient
from app.models.user import User


def seed_patient_ownership_data(
    db_session,
):
    now = datetime.now()

    # ----------------------------------------------
    # USERS
    # ----------------------------------------------

    doctor_user = User(
        id=100,
        firebase_uid="ownership-doctor",
        name="Dr. Ownership",
        email="ownership.doctor@example.com",
        role="DOCTOR",
    )

    patient_user_1 = User(
        id=101,
        firebase_uid="ownership-patient-1",
        name="Patient One",
        email="patient1@example.com",
        role="PATIENT",
    )

    patient_user_2 = User(
        id=102,
        firebase_uid="ownership-patient-2",
        name="Patient Two",
        email="patient2@example.com",
        role="PATIENT",
    )

    db_session.add_all(
        [
            doctor_user,
            patient_user_1,
            patient_user_2,
        ]
    )

    db_session.flush()

    # ----------------------------------------------
    # DOCTOR
    # ----------------------------------------------

    doctor = Doctor(
        id=100,
        user_id=doctor_user.id,
        department_id=None,
        specialization="Cardiology",
        availability="MON-FRI 09:00-17:00",
        created_at=now,
    )

    db_session.add(doctor)

    # ----------------------------------------------
    # PATIENTS
    # ----------------------------------------------

    patient_1 = Patient(
        id=101,
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
        created_at=now,
    )

    patient_2 = Patient(
        id=102,
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
        created_at=now,
    )

    db_session.add_all(
        [
            patient_1,
            patient_2,
        ]
    )

    db_session.flush()

    # ----------------------------------------------
    # ASSIGN ONLY PATIENT 1 TO DOCTOR
    # ----------------------------------------------

    appointment = Appointment(
        id=100,
        patient_id=patient_1.id,
        doctor_id=doctor.id,
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
        reason="Ownership test",
        created_at=now,
    )

    db_session.add(appointment)

    db_session.commit()

    return {
        "doctor_user": doctor_user,
        "patient_user_1": patient_user_1,
        "patient_user_2": patient_user_2,
        "doctor": doctor,
        "patient_1": patient_1,
        "patient_2": patient_2,
        "appointment": appointment,
    }


# --------------------------------------------------
# PATIENT OWNERSHIP
# --------------------------------------------------


def test_patient_can_access_own_profile(
    client,
    login_as,
    db_session,
):
    data = seed_patient_ownership_data(
        db_session
    )

    login_as(
        data["patient_user_1"]
    )

    response = client.get(
        f"/patients/{data['patient_1'].id}"
    )

    assert response.status_code == 200

    assert (
        response.json()["id"]
        == data["patient_1"].id
    )


def test_patient_cannot_access_another_patient(
    client,
    login_as,
    db_session,
):
    data = seed_patient_ownership_data(
        db_session
    )

    login_as(
        data["patient_user_1"]
    )

    response = client.get(
        f"/patients/{data['patient_2'].id}"
    )

    assert response.status_code == 403


def test_patient_me_returns_own_profile(
    client,
    login_as,
    db_session,
):
    data = seed_patient_ownership_data(
        db_session
    )

    login_as(
        data["patient_user_1"]
    )

    response = client.get(
        "/patients/me"
    )

    assert response.status_code == 200

    assert (
        response.json()["id"]
        == data["patient_1"].id
    )


# --------------------------------------------------
# DOCTOR OWNERSHIP
# --------------------------------------------------


def test_doctor_can_access_assigned_patient(
    client,
    login_as,
    db_session,
):
    data = seed_patient_ownership_data(
        db_session
    )

    login_as(
        data["doctor_user"]
    )

    response = client.get(
        f"/patients/{data['patient_1'].id}"
    )

    assert response.status_code == 200


def test_doctor_cannot_access_unrelated_patient(
    client,
    login_as,
    db_session,
):
    data = seed_patient_ownership_data(
        db_session
    )

    login_as(
        data["doctor_user"]
    )

    response = client.get(
        f"/patients/{data['patient_2'].id}"
    )

    assert response.status_code == 403


def test_doctor_patient_list_contains_only_assigned_patient(
    client,
    login_as,
    db_session,
):
    data = seed_patient_ownership_data(
        db_session
    )

    login_as(
        data["doctor_user"]
    )

    response = client.get(
        "/patients"
    )

    assert response.status_code == 200

    patients = response.json()

    assert len(patients) == 1

    assert (
        patients[0]["id"]
        == data["patient_1"].id
    )