from fastapi import (
    APIRouter,
    Depends,
    HTTPException,
    status,
)
from sqlalchemy import select
from sqlalchemy.orm import Session

from app.dependencies.authorization import get_current_user
from app.dependencies.database import get_db
from app.models.appointment import Appointment
from app.models.department import Department
from app.models.doctor import Doctor
from app.models.patient import Patient
from app.models.user import User
from app.schemas.appointment import (
    AppointmentCreate,
    AppointmentResponse,
)


router = APIRouter(
    prefix="/appointments",
    tags=["Appointments"],
)


# --------------------------------------------------
# CREATE APPOINTMENT
#
# ADMIN / STAFF:
#     Can create for any patient.
#
# PATIENT:
#     Can create only for own patient profile.
#     Priority and status are controlled by backend.
#
# DOCTOR:
#     Cannot create appointments here.
# --------------------------------------------------

@router.post(
    "",
    response_model=AppointmentResponse,
    status_code=status.HTTP_201_CREATED,
)
def create_appointment(
    appointment_data: AppointmentCreate,
    db: Session = Depends(get_db),
    current_user: User = Depends(
        get_current_user
    ),
):
    role = current_user.role.upper()

    if role not in {
        "ADMIN",
        "STAFF",
        "PATIENT",
    }:
        raise HTTPException(
            status_code=status.HTTP_403_FORBIDDEN,
            detail=(
                "You do not have permission "
                "to create appointments."
            ),
        )

    patient = db.get(
        Patient,
        appointment_data.patient_id,
    )

    if not patient:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Patient not found.",
        )

    # PATIENT can create appointment
    # only for their own linked profile.
    if role == "PATIENT":

        own_patient = db.scalar(
            select(Patient).where(
                Patient.user_id
                == current_user.id
            )
        )

        if not own_patient:
            raise HTTPException(
                status_code=status.HTTP_404_NOT_FOUND,
                detail=(
                    "Patient profile is not linked "
                    "to this account."
                ),
            )

        if appointment_data.patient_id != own_patient.id:
            raise HTTPException(
                status_code=status.HTTP_403_FORBIDDEN,
                detail=(
                    "You can only create "
                    "appointments for yourself."
                ),
            )

    doctor = db.get(
        Doctor,
        appointment_data.doctor_id,
    )

    if not doctor:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Doctor not found.",
        )

    if appointment_data.department_id is not None:

        department = db.get(
            Department,
            appointment_data.department_id,
        )

        if not department:
            raise HTTPException(
                status_code=status.HTTP_404_NOT_FOUND,
                detail="Department not found.",
            )

    # Prevent invalid priority values.
    if (
        appointment_data.priority < 1
        or appointment_data.priority > 5
    ):
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail=(
                "Priority must be between "
                "1 and 5."
            ),
        )

    # Prevent the same doctor from being
    # double-booked at the exact same time.
    conflicting_appointment = db.scalar(
        select(Appointment).where(
            Appointment.doctor_id
            == appointment_data.doctor_id,
            Appointment.appointment_time
            == appointment_data.appointment_time,
            Appointment.status
            != "CANCELLED",
        )
    )

    if conflicting_appointment:
        raise HTTPException(
            status_code=status.HTTP_409_CONFLICT,
            detail=(
                "Doctor already has an appointment "
                "at this time."
            ),
        )

    # Patients cannot control operational
    # priority or appointment status.
    if role == "PATIENT":
        appointment_priority = 3
        appointment_status = "SCHEDULED"

    else:
        appointment_priority = (
            appointment_data.priority
        )

        appointment_status = (
            appointment_data.status.upper()
        )

    appointment = Appointment(
        patient_id=patient.id,
        doctor_id=doctor.id,
        department_id=(
            appointment_data.department_id
        ),
        appointment_time=(
            appointment_data.appointment_time
        ),
        priority=appointment_priority,
        status=appointment_status,
        reason=appointment_data.reason,
    )

    db.add(appointment)
    db.commit()
    db.refresh(appointment)

    return appointment


# --------------------------------------------------
# GET APPOINTMENTS
#
# ADMIN / STAFF:
#     All appointments
#
# DOCTOR:
#     Only appointments assigned to them
#
# PATIENT:
#     Only their own appointments
# --------------------------------------------------

@router.get(
    "",
    response_model=list[AppointmentResponse],
)
def get_appointments(
    db: Session = Depends(get_db),
    current_user: User = Depends(
        get_current_user
    ),
):
    role = current_user.role.upper()

    query = select(Appointment)

    if role in {
        "ADMIN",
        "STAFF",
    }:
        pass

    elif role == "DOCTOR":

        doctor = db.scalar(
            select(Doctor).where(
                Doctor.user_id
                == current_user.id
            )
        )

        if not doctor:
            raise HTTPException(
                status_code=status.HTTP_404_NOT_FOUND,
                detail=(
                    "Doctor profile is not linked "
                    "to this account."
                ),
            )

        query = query.where(
            Appointment.doctor_id
            == doctor.id
        )

    elif role == "PATIENT":

        patient = db.scalar(
            select(Patient).where(
                Patient.user_id
                == current_user.id
            )
        )

        if not patient:
            raise HTTPException(
                status_code=status.HTTP_404_NOT_FOUND,
                detail=(
                    "Patient profile is not linked "
                    "to this account."
                ),
            )

        query = query.where(
            Appointment.patient_id
            == patient.id
        )

    else:
        raise HTTPException(
            status_code=status.HTTP_403_FORBIDDEN,
            detail=(
                "You do not have permission "
                "to access appointments."
            ),
        )

    query = query.order_by(
        Appointment.appointment_time
    )

    appointments = db.scalars(
        query
    ).all()

    return appointments


# --------------------------------------------------
# GET APPOINTMENT BY ID
#
# ADMIN / STAFF:
#     Any appointment
#
# DOCTOR:
#     Assigned appointment only
#
# PATIENT:
#     Own appointment only
# --------------------------------------------------

@router.get(
    "/{appointment_id}",
    response_model=AppointmentResponse,
)
def get_appointment(
    appointment_id: int,
    db: Session = Depends(get_db),
    current_user: User = Depends(
        get_current_user
    ),
):
    appointment = db.get(
        Appointment,
        appointment_id,
    )

    if not appointment:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Appointment not found.",
        )

    role = current_user.role.upper()

    if role in {
        "ADMIN",
        "STAFF",
    }:
        return appointment

    if role == "DOCTOR":

        doctor = db.scalar(
            select(Doctor).where(
                Doctor.user_id
                == current_user.id
            )
        )

        if not doctor:
            raise HTTPException(
                status_code=status.HTTP_404_NOT_FOUND,
                detail=(
                    "Doctor profile is not linked "
                    "to this account."
                ),
            )

        if appointment.doctor_id != doctor.id:
            raise HTTPException(
                status_code=status.HTTP_403_FORBIDDEN,
                detail=(
                    "You can only access "
                    "appointments assigned to you."
                ),
            )

        return appointment

    if role == "PATIENT":

        patient = db.scalar(
            select(Patient).where(
                Patient.user_id
                == current_user.id
            )
        )

        if not patient:
            raise HTTPException(
                status_code=status.HTTP_404_NOT_FOUND,
                detail=(
                    "Patient profile is not linked "
                    "to this account."
                ),
            )

        if appointment.patient_id != patient.id:
            raise HTTPException(
                status_code=status.HTTP_403_FORBIDDEN,
                detail=(
                    "You can only access "
                    "your own appointments."
                ),
            )

        return appointment

    raise HTTPException(
        status_code=status.HTTP_403_FORBIDDEN,
        detail=(
            "You do not have permission "
            "to access this appointment."
        ),
    )


# --------------------------------------------------
# CANCEL APPOINTMENT
#
# PATIENT:
#     Can cancel only their own appointment.
#
# Completed or already cancelled appointments
# cannot be cancelled.
# --------------------------------------------------

@router.patch(
    "/{appointment_id}/cancel",
    response_model=AppointmentResponse,
)
def cancel_appointment(
    appointment_id: int,
    db: Session = Depends(get_db),
    current_user: User = Depends(
        get_current_user
    ),
):
    role = current_user.role.upper()

    # This endpoint is specifically for
    # patient self-service cancellation.
    if role != "PATIENT":
        raise HTTPException(
            status_code=status.HTTP_403_FORBIDDEN,
            detail=(
                "Only patients can cancel "
                "their appointments here."
            ),
        )

    # ----------------------------------------------
    # RESOLVE CURRENT PATIENT
    # ----------------------------------------------

    patient = db.scalar(
        select(Patient).where(
            Patient.user_id
            == current_user.id
        )
    )

    if not patient:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail=(
                "Patient profile is not linked "
                "to this account."
            ),
        )

    # ----------------------------------------------
    # FIND APPOINTMENT
    # ----------------------------------------------

    appointment = db.get(
        Appointment,
        appointment_id,
    )

    if not appointment:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Appointment not found.",
        )

    # ----------------------------------------------
    # OBJECT-LEVEL AUTHORIZATION
    # ----------------------------------------------

    if appointment.patient_id != patient.id:
        raise HTTPException(
            status_code=status.HTTP_403_FORBIDDEN,
            detail=(
                "You can only cancel "
                "your own appointments."
            ),
        )

    # ----------------------------------------------
    # VALIDATE CURRENT STATUS
    # ----------------------------------------------

    current_status = (
        appointment.status.upper()
    )

    if current_status == "CANCELLED":
        raise HTTPException(
            status_code=status.HTTP_409_CONFLICT,
            detail=(
                "Appointment is already cancelled."
            ),
        )

    if current_status == "COMPLETED":
        raise HTTPException(
            status_code=status.HTTP_409_CONFLICT,
            detail=(
                "Completed appointments "
                "cannot be cancelled."
            ),
        )

    # ----------------------------------------------
    # CANCEL
    # ----------------------------------------------

    appointment.status = "CANCELLED"

    db.commit()
    db.refresh(appointment)

    return appointment


# --------------------------------------------------
# COMPLETE APPOINTMENT
#
# DOCTOR:
#     Can complete only appointments
#     assigned to them.
#
# Cancelled or already completed
# appointments cannot be completed.
# --------------------------------------------------

@router.patch(
    "/{appointment_id}/complete",
    response_model=AppointmentResponse,
)
def complete_appointment(
    appointment_id: int,
    db: Session = Depends(get_db),
    current_user: User = Depends(
        get_current_user
    ),
):
    role = current_user.role.upper()

    # This endpoint is specifically for
    # doctor appointment completion.
    if role != "DOCTOR":
        raise HTTPException(
            status_code=status.HTTP_403_FORBIDDEN,
            detail=(
                "Only doctors can complete "
                "appointments here."
            ),
        )

    # ----------------------------------------------
    # RESOLVE CURRENT DOCTOR
    # ----------------------------------------------

    doctor = db.scalar(
        select(Doctor).where(
            Doctor.user_id
            == current_user.id
        )
    )

    if not doctor:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail=(
                "Doctor profile is not linked "
                "to this account."
            ),
        )

    # ----------------------------------------------
    # FIND APPOINTMENT
    # ----------------------------------------------

    appointment = db.get(
        Appointment,
        appointment_id,
    )

    if not appointment:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Appointment not found.",
        )

    # ----------------------------------------------
    # OBJECT-LEVEL AUTHORIZATION
    # ----------------------------------------------

    if appointment.doctor_id != doctor.id:
        raise HTTPException(
            status_code=status.HTTP_403_FORBIDDEN,
            detail=(
                "You can only complete "
                "appointments assigned to you."
            ),
        )

    # ----------------------------------------------
    # VALIDATE STATUS
    # ----------------------------------------------

    current_status = (
        appointment.status.upper()
    )

    if current_status == "COMPLETED":
        raise HTTPException(
            status_code=status.HTTP_409_CONFLICT,
            detail=(
                "Appointment is already completed."
            ),
        )

    if current_status == "CANCELLED":
        raise HTTPException(
            status_code=status.HTTP_409_CONFLICT,
            detail=(
                "Cancelled appointments "
                "cannot be completed."
            ),
        )

    # ----------------------------------------------
    # COMPLETE
    # ----------------------------------------------

    appointment.status = "COMPLETED"

    db.commit()
    db.refresh(appointment)

    return appointment