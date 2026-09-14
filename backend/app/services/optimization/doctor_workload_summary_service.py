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


def get_doctor_workload_summary(
    db: Session,
):
    doctors = db.execute(
        select(
            Doctor.id,
            User.name,
            Doctor.specialization,
            Doctor.availability,
        )
        .join(
            User,
            User.id == Doctor.user_id,
        )
    ).all()

    results = []

    for (
        doctor_id,
        doctor_name,
        specialization,
        availability,
    ) in doctors:

        active_appointments = db.scalar(
            select(func.count(Appointment.id)).where(
                Appointment.doctor_id == doctor_id,
                Appointment.status.in_(ACTIVE_STATUSES),
            )
        ) or 0

        total_appointments = db.scalar(
            select(func.count(Appointment.id)).where(
                Appointment.doctor_id == doctor_id
            )
        ) or 0

        results.append(
            {
                "doctor_id": doctor_id,
                "doctor_name": doctor_name,
                "specialization": specialization,
                "availability": availability,
                "active_appointments": active_appointments,
                "total_appointments": total_appointments,
            }
        )

    results.sort(
        key=lambda doctor: (
            doctor["active_appointments"],
            doctor["doctor_id"],
        )
    )

    least_loaded_doctor_id = None
    most_loaded_doctor_id = None

    if results:
        least_loaded_doctor_id = results[0]["doctor_id"]
        most_loaded_doctor_id = max(
            results,
            key=lambda doctor: doctor["active_appointments"],
        )["doctor_id"]

    return {
        "total_doctors": len(results),
        "least_loaded_doctor_id": least_loaded_doctor_id,
        "most_loaded_doctor_id": most_loaded_doctor_id,
        "doctors": results,
    }