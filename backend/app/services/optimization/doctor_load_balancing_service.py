from sqlalchemy import func, select
from sqlalchemy.orm import Session

from app.models.appointment import Appointment
from app.models.doctor import Doctor
from app.services.optimization.doctor_load_balancing import (
    DoctorLoadBalancingEngine,
    DoctorWorkload,
)


ACTIVE_APPOINTMENT_STATUSES = [
    "SCHEDULED",
    "CONFIRMED",
    "IN_PROGRESS",
]


def get_least_loaded_doctor(
    db: Session,
    required_specialization: str,
):

    doctors = db.scalars(
        select(Doctor).where(
            func.lower(Doctor.specialization)
            == required_specialization.lower()
        )
    ).all()

    if not doctors:
        return None

    doctor_workloads = []

    for doctor in doctors:

        is_available = (
            doctor.availability is not None
            and doctor.availability.strip() != ""
        )

        appointment_count = db.scalar(
            select(
                func.count(Appointment.id)
            ).where(
                Appointment.doctor_id
                == doctor.id,
                Appointment.status.in_(
                    ACTIVE_APPOINTMENT_STATUSES
                ),
            )
        )

        doctor_workloads.append(
            DoctorWorkload(
                doctor_id=doctor.id,
                specialization=(
                    doctor.specialization
                    or ""
                ),
                current_appointments=(
                    appointment_count or 0
                ),
                is_available=is_available,
            )
        )

    engine = DoctorLoadBalancingEngine()

    return engine.select_doctor(
        doctors=doctor_workloads,
        required_specialization=(
            required_specialization
        ),
    )