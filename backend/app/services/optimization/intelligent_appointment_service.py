from datetime import datetime

from fastapi import HTTPException
from sqlalchemy.orm import Session

from app.models.appointment import Appointment
from app.models.patient import Patient
from app.services.optimization.appointment_scheduling_service import (
    has_appointment_conflict,
)
from app.services.optimization.doctor_load_balancing_service import (
    get_least_loaded_doctor,
)


def schedule_optimized_appointment(
    db: Session,
    patient_id: int,
    specialization: str,
    appointment_time: datetime,
    department_id: int | None,
    priority: int,
    reason: str | None,
):

    patient = db.get(
        Patient,
        patient_id,
    )

    if not patient:
        raise HTTPException(
            status_code=404,
            detail="Patient not found.",
        )

    if priority < 1 or priority > 5:
        raise HTTPException(
            status_code=400,
            detail="Priority must be between 1 and 5.",
        )

    doctor_result = get_least_loaded_doctor(
        db=db,
        required_specialization=specialization,
    )

    if doctor_result is None:
        raise HTTPException(
            status_code=404,
            detail=(
                "No available doctor found "
                "for the requested specialization."
            ),
        )

    doctor_id = doctor_result.doctor_id

    has_conflict = has_appointment_conflict(
        db=db,
        doctor_id=doctor_id,
        appointment_time=appointment_time,
    )

    if has_conflict:
        raise HTTPException(
            status_code=409,
            detail=(
                "The selected doctor already has an "
                "appointment at the requested time."
            ),
        )

    appointment = Appointment(
        patient_id=patient_id,
        doctor_id=doctor_id,
        department_id=department_id,
        appointment_time=appointment_time,
        priority=priority,
        status="SCHEDULED",
        reason=reason,
    )

    db.add(appointment)
    db.commit()
    db.refresh(appointment)

    return {
        "appointment": appointment,
        "doctor_specialization": (
            doctor_result.specialization
        ),
        "doctor_active_appointments": (
            doctor_result.current_appointments
        ),
    }