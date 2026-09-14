from sqlalchemy import func, select
from sqlalchemy.orm import Session

from app.models.appointment import Appointment
from app.models.doctor import Doctor
from app.models.user import User


ACTIVE_STATUSES = [
    "SCHEDULED",
    "CONFIRMED",
    "IN_PROGRESS",
]


def get_appointment_analytics(
    db: Session,
):

    total_appointments = db.scalar(
        select(func.count(Appointment.id))
    ) or 0

    status_rows = db.execute(
        select(
            Appointment.status,
            func.count(Appointment.id),
        )
        .group_by(Appointment.status)
        .order_by(Appointment.status)
    ).all()

    status_summary = []

    for status, count in status_rows:
        status_summary.append(
            {
                "status": status,
                "count": count,
            }
        )

    doctors = db.execute(
        select(
            Doctor.id,
            User.name,
            Doctor.specialization,
        )
        .join(
            User,
            User.id == Doctor.user_id,
        )
        .order_by(User.name)
    ).all()

    doctor_workload = []

    for doctor_id, doctor_name, specialization in doctors:

        total_for_doctor = db.scalar(
            select(func.count(Appointment.id)).where(
                Appointment.doctor_id == doctor_id
            )
        ) or 0

        active_for_doctor = db.scalar(
            select(func.count(Appointment.id)).where(
                Appointment.doctor_id == doctor_id,
                Appointment.status.in_(ACTIVE_STATUSES),
            )
        ) or 0

        doctor_workload.append(
            {
                "doctor_id": doctor_id,
                "doctor_name": doctor_name,
                "specialization": specialization,
                "active_appointments": active_for_doctor,
                "total_appointments": total_for_doctor,
            }
        )

    return {
        "total_appointments": total_appointments,
        "status_summary": status_summary,
        "doctor_workload": doctor_workload,
    }