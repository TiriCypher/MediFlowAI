from datetime import date, datetime, timedelta

from app.models.department import Department
from app.models.doctor import Doctor
from app.models.patient import Patient
from app.models.user import User


def seed_write_test_data(db_session):
    now = datetime.now()

    admin = User(
        id=300,
        firebase_uid="write-admin",
        name="Write Admin",
        email="write.admin@example.com",
        role="ADMIN",
    )

    staff = User(
        id=301,
        firebase_uid="write-staff",
        name="Write Staff",
        email="write.staff@example.com",
        role="STAFF",
    )

    doctor_user = User(
        id=302,
        firebase_uid="write-doctor",
        name="Write Doctor",
        email="write.doctor@example.com",
        role="DOCTOR",
    )

    patient_user_1 = User(
        id=303,
        firebase_uid="write-patient-1",
        name="Write Patient One",
        email="write.patient1@example.com",
        role="PATIENT",
    )

    patient_user_2 = User(
        id=304,
        firebase_uid="write-patient-2",
        name="Write Patient Two",
        email="write.patient2@example.com",
        role="PATIENT",
    )

    role_target = User(
        id=305,
        firebase_uid="role-target",
        name="Role Target",
        email="role.target@example.com",
        role="PATIENT",
    )

    db_session.add_all([
        admin,
        staff,
        doctor_user,
        patient_user_1,
        patient_user_2,
        role_target,
    ])

    db_session.flush()

    department = Department(
        id=300,
        name="Cardiology",
        description="Write test department",
    )

    db_session.add(department)
    db_session.flush()

    doctor = Doctor(
        id=300,
        user_id=doctor_user.id,
        department_id=department.id,
        specialization="Cardiology",
        availability="MON-FRI 09:00-17:00",
    )

    patient_1 = Patient(
        id=303,
        user_id=patient_user_1.id,
        name="Write Patient One",
        date_of_birth=date(1960, 1, 1),
        gender="F",
        blood_group="O+",
        phone="1111111111",
        address="Jaipur",
        medical_information="Test",
    )

    patient_2 = Patient(
        id=304,
        user_id=patient_user_2.id,
        name="Write Patient Two",
        date_of_birth=date(1970, 1, 1),
        gender="M",
        blood_group="A+",
        phone="2222222222",
        address="Jaipur",
        medical_information="Test",
    )

    db_session.add_all([
        doctor,
        patient_1,
        patient_2,
    ])

    db_session.commit()

    return {
        "admin": admin,
        "staff": staff,
        "doctor_user": doctor_user,
        "patient_user_1": patient_user_1,
        "patient_user_2": patient_user_2,
        "role_target": role_target,
        "department": department,
        "doctor": doctor,
        "patient_1": patient_1,
        "patient_2": patient_2,
    }


def appointment_payload(
    data,
    patient_id,
    appointment_time,
    priority=3,
    status="SCHEDULED",
):
    return {
        "patient_id": patient_id,
        "doctor_id": data["doctor"].id,
        "department_id": data["department"].id,
        "appointment_time": (
            appointment_time.isoformat()
        ),
        "priority": priority,
        "status": status,
        "reason": "Write security test",
    }


# --------------------------------------------------
# PATIENT APPOINTMENT CREATION
# --------------------------------------------------

def test_patient_can_create_own_appointment(
    client,
    login_as,
    db_session,
):
    data = seed_write_test_data(db_session)

    login_as(data["patient_user_1"])

    appointment_time = (
        datetime.now()
        + timedelta(days=5)
    )

    response = client.post(
        "/appointments",
        json=appointment_payload(
            data,
            data["patient_1"].id,
            appointment_time,
        ),
    )

    assert response.status_code == 201

    body = response.json()

    assert (
        body["patient_id"]
        == data["patient_1"].id
    )


def test_patient_cannot_create_appointment_for_other_patient(
    client,
    login_as,
    db_session,
):
    data = seed_write_test_data(db_session)

    login_as(data["patient_user_1"])

    response = client.post(
        "/appointments",
        json=appointment_payload(
            data,
            data["patient_2"].id,
            datetime.now()
            + timedelta(days=6),
        ),
    )

    assert response.status_code == 403


def test_patient_cannot_force_priority_or_status(
    client,
    login_as,
    db_session,
):
    data = seed_write_test_data(db_session)

    login_as(data["patient_user_1"])

    response = client.post(
        "/appointments",
        json=appointment_payload(
            data,
            data["patient_1"].id,
            datetime.now()
            + timedelta(days=7),
            priority=5,
            status="COMPLETED",
        ),
    )

    assert response.status_code == 201

    body = response.json()

    assert body["priority"] == 3
    assert body["status"] == "SCHEDULED"


# --------------------------------------------------
# DOCTOR APPOINTMENT CREATION
# --------------------------------------------------

def test_doctor_cannot_create_appointment(
    client,
    login_as,
    db_session,
):
    data = seed_write_test_data(db_session)

    login_as(data["doctor_user"])

    response = client.post(
        "/appointments",
        json=appointment_payload(
            data,
            data["patient_1"].id,
            datetime.now()
            + timedelta(days=8),
        ),
    )

    assert response.status_code == 403


# --------------------------------------------------
# STAFF APPOINTMENT CREATION
# --------------------------------------------------

def test_staff_can_create_appointment(
    client,
    login_as,
    db_session,
):
    data = seed_write_test_data(db_session)

    login_as(data["staff"])

    response = client.post(
        "/appointments",
        json=appointment_payload(
            data,
            data["patient_1"].id,
            datetime.now()
            + timedelta(days=9),
            priority=4,
        ),
    )

    assert response.status_code == 201
    assert response.json()["priority"] == 4


# --------------------------------------------------
# DOUBLE BOOKING
# --------------------------------------------------

def test_same_doctor_same_time_is_rejected(
    client,
    login_as,
    db_session,
):
    data = seed_write_test_data(db_session)

    login_as(data["staff"])

    appointment_time = (
        datetime.now()
        + timedelta(days=10)
    )

    first = client.post(
        "/appointments",
        json=appointment_payload(
            data,
            data["patient_1"].id,
            appointment_time,
        ),
    )

    assert first.status_code == 201

    second = client.post(
        "/appointments",
        json=appointment_payload(
            data,
            data["patient_2"].id,
            appointment_time,
        ),
    )

    assert second.status_code == 409


# --------------------------------------------------
# ALERT WRITE PERMISSIONS
# --------------------------------------------------

def test_patient_cannot_create_alert(
    client,
    login_as,
    db_session,
):
    data = seed_write_test_data(db_session)

    login_as(data["patient_user_1"])

    response = client.post(
        "/alerts",
        json={
            "patient_id": (
                data["patient_1"].id
            ),
            "type": "TEST",
            "message": "Should be denied",
            "status": "UNREAD",
        },
    )

    assert response.status_code == 403


def test_staff_can_create_alert(
    client,
    login_as,
    db_session,
):
    data = seed_write_test_data(db_session)

    login_as(data["staff"])

    response = client.post(
        "/alerts",
        json={
            "patient_id": (
                data["patient_1"].id
            ),
            "type": "FOLLOW_UP",
            "message": "Follow-up required",
            "status": "UNREAD",
        },
    )

    assert response.status_code == 201

    assert (
        response.json()["patient_id"]
        == data["patient_1"].id
    )


# --------------------------------------------------
# BED WRITE PERMISSIONS
# --------------------------------------------------

def test_staff_cannot_create_bed(
    client,
    login_as,
    db_session,
):
    data = seed_write_test_data(db_session)

    login_as(data["staff"])

    response = client.post(
        "/beds",
        json={
            "department_id": (
                data["department"].id
            ),
            "bed_number": "WRITE-STAFF-001",
            "bed_type": "GENERAL",
            "status": "AVAILABLE",
        },
    )

    assert response.status_code == 403


def test_admin_can_create_bed(
    client,
    login_as,
    db_session,
):
    data = seed_write_test_data(db_session)

    login_as(data["admin"])

    response = client.post(
        "/beds",
        json={
            "department_id": (
                data["department"].id
            ),
            "bed_number": "WRITE-ADMIN-001",
            "bed_type": "GENERAL",
            "status": "AVAILABLE",
        },
    )

    assert response.status_code == 201

    assert (
        response.json()["bed_number"]
        == "WRITE-ADMIN-001"
    )


# --------------------------------------------------
# DEPARTMENT WRITE PERMISSIONS
# --------------------------------------------------

def test_staff_cannot_create_department(
    client,
    login_as,
    db_session,
):
    data = seed_write_test_data(db_session)

    login_as(data["staff"])

    response = client.post(
        "/departments",
        json={
            "name": "Staff Department",
            "description": "Should fail",
        },
    )

    assert response.status_code == 403


def test_admin_can_create_department(
    client,
    login_as,
    db_session,
):
    data = seed_write_test_data(db_session)

    login_as(data["admin"])

    response = client.post(
        "/departments",
        json={
            "name": "Neurology",
            "description": (
                "Admin-created department"
            ),
        },
    )

    assert response.status_code == 201

    assert (
        response.json()["name"]
        == "Neurology"
    )


# --------------------------------------------------
# ROLE UPDATE SECURITY
# --------------------------------------------------

def test_patient_cannot_change_user_role(
    client,
    login_as,
    db_session,
):
    data = seed_write_test_data(db_session)

    login_as(data["patient_user_1"])

    response = client.patch(
        (
            f"/users/"
            f"{data['role_target'].id}/role"
        ),
        json={
            "role": "ADMIN",
        },
    )

    assert response.status_code == 403


def test_staff_cannot_change_user_role(
    client,
    login_as,
    db_session,
):
    data = seed_write_test_data(db_session)

    login_as(data["staff"])

    response = client.patch(
        (
            f"/users/"
            f"{data['role_target'].id}/role"
        ),
        json={
            "role": "DOCTOR",
        },
    )

    assert response.status_code == 403


def test_doctor_cannot_change_user_role(
    client,
    login_as,
    db_session,
):
    data = seed_write_test_data(db_session)

    login_as(data["doctor_user"])

    response = client.patch(
        (
            f"/users/"
            f"{data['role_target'].id}/role"
        ),
        json={
            "role": "ADMIN",
        },
    )

    assert response.status_code == 403


def test_admin_can_change_user_role(
    client,
    login_as,
    db_session,
):
    data = seed_write_test_data(db_session)

    login_as(data["admin"])

    response = client.patch(
        (
            f"/users/"
            f"{data['role_target'].id}/role"
        ),
        json={
            "role": "STAFF",
        },
    )

    assert response.status_code == 200
    assert response.json()["role"] == "STAFF"