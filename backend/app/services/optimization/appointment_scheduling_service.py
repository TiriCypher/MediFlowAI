from datetime import datetime

from sqlalchemy import select
from sqlalchemy.orm import Session

from app.models.appointment import Appointment


ACTIVE_APPOINTMENT_STATUSES = [
    "SCHEDULED",
    "CONFIRMED",
    "IN_PROGRESS",
]


def has_appointment_conflict(
    db: Session,
    doctor_id: int,
    appointment_time: datetime,
) -> bool:

    existing_appointment = db.scalar(
        select(Appointment).where(
            Appointment.doctor_id == doctor_id,
            Appointment.appointment_time == appointment_time,
            Appointment.status.in_(
                ACTIVE_APPOINTMENT_STATUSES
            ),
        )
    )

    return existing_appointment is not None