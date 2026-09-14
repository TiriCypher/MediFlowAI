from app.schemas import (
    AppointmentCreate,
    DepartmentCreate,
    PatientCreate,
    UserRoleUpdateRequest,
    UserSyncRequest,
)


def test_user_sync_schema():
    user = UserSyncRequest(
        name="Test User",
        email="test@example.com",
    )

    assert user.name == "Test User"
    assert user.email == "test@example.com"


def test_user_role_update_schema():
    role_update = UserRoleUpdateRequest(
        role="PATIENT"
    )

    assert role_update.role == "PATIENT"


def test_department_schema():
    department = DepartmentCreate(
        name="Cardiology",
        description="Heart care department",
    )

    assert department.name == "Cardiology"


def test_patient_schema():
    patient = PatientCreate(
        name="Test Patient",
        gender="Male",
        blood_group="O+",
    )

    assert patient.name == "Test Patient"


def test_appointment_schema():
    appointment = AppointmentCreate(
        patient_id=1,
        doctor_id=1,
        appointment_time="2026-09-10T10:00:00",
    )

    assert appointment.priority == 3
    assert appointment.status == "SCHEDULED"