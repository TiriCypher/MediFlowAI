from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy import select
from sqlalchemy.orm import Session

from app.dependencies.authorization import require_roles
from app.dependencies.database import get_db
from app.models.appointment import Appointment
from app.models.doctor import Doctor
from app.models.ml_prediction import MLPrediction
from app.models.user import User
from app.schemas.analytics import DoctorAnalyticsResponse


router = APIRouter(
    prefix="/analytics",
    tags=["Analytics"],
)


@router.get(
    "/doctor/me",
    response_model=DoctorAnalyticsResponse,
)
def get_doctor_analytics(
    db: Session = Depends(get_db),
    current_user: User = Depends(
        require_roles("DOCTOR")
    ),
):
    # ---------------------------------------------
    # CURRENT DOCTOR
    # ---------------------------------------------

    doctor = db.scalar(
        select(Doctor).where(
            Doctor.user_id == current_user.id
        )
    )

    if not doctor:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Doctor profile is not linked to this account.",
        )

    # ---------------------------------------------
    # APPOINTMENTS
    # ---------------------------------------------

    appointments = db.scalars(
        select(Appointment).where(
            Appointment.doctor_id == doctor.id
        )
    ).all()

    total_appointments = len(appointments)

    scheduled = sum(
        1
        for appointment in appointments
        if appointment.status.upper() == "SCHEDULED"
    )

    completed = sum(
        1
        for appointment in appointments
        if appointment.status.upper() == "COMPLETED"
    )

    cancelled = sum(
        1
        for appointment in appointments
        if appointment.status.upper() == "CANCELLED"
    )

    completion_rate = (
        round(
            (completed / total_appointments) * 100,
            2,
        )
        if total_appointments > 0
        else 0.0
    )

    # ---------------------------------------------
    # DISTINCT ASSIGNED PATIENTS
    # ---------------------------------------------

    patient_ids = {
        appointment.patient_id
        for appointment in appointments
    }

    assigned_patients = len(patient_ids)

    # ---------------------------------------------
    # LATEST PREDICTION PER PATIENT
    # ---------------------------------------------

    latest_predictions = []

    for patient_id in patient_ids:
        prediction = db.scalar(
            select(MLPrediction)
            .where(
                MLPrediction.patient_id == patient_id
            )
            .order_by(
                MLPrediction.created_at.desc()
            )
            .limit(1)
        )

        if prediction:
            latest_predictions.append(prediction)

    total_predictions = len(latest_predictions)

    low_risk = sum(
        1
        for prediction in latest_predictions
        if prediction.risk_level.upper() == "LOW"
    )

    medium_risk = sum(
        1
        for prediction in latest_predictions
        if prediction.risk_level.upper() == "MEDIUM"
    )

    high_risk = sum(
        1
        for prediction in latest_predictions
        if prediction.risk_level.upper() == "HIGH"
    )

    # ---------------------------------------------
    # RESPONSE
    # ---------------------------------------------

    return {
        "doctor_id": doctor.id,
        "doctor_name": current_user.name,
        "specialization": doctor.specialization,
        "assigned_patients": assigned_patients,
        "appointments": {
            "total": total_appointments,
            "scheduled": scheduled,
            "completed": completed,
            "cancelled": cancelled,
            "completion_rate": completion_rate,
        },
        "patient_risk": {
            "total_predictions": total_predictions,
            "low_risk": low_risk,
            "medium_risk": medium_risk,
            "high_risk": high_risk,
        },
    }