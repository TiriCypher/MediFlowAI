from sqlalchemy import select
from sqlalchemy.orm import Session

from app.models.ml_prediction import MLPrediction

from app.services.optimization.patient_priority import (
    PatientPriorityEngine,
    PatientPriorityInput,
)


def get_patient_risk_priority_dashboard(
    db: Session,
    patient_id: int,
    urgency: str,
    waiting_time_minutes: int,
    comorbidity_count: int,
    age: int,
):
    latest_prediction = db.scalars(
        select(MLPrediction)
        .where(
            MLPrediction.patient_id == patient_id
        )
        .order_by(
            MLPrediction.created_at.desc()
        )
        .limit(1)
    ).first()

    if not latest_prediction:
        return None

    priority_input = PatientPriorityInput(
        patient_id=patient_id,
        urgency=urgency,
        risk_level=latest_prediction.risk_level,
        waiting_time_minutes=waiting_time_minutes,
        comorbidity_count=comorbidity_count,
        age=age,
    )

    engine = PatientPriorityEngine()

    priority_result = engine.calculate_priority(
        priority_input
    )

    return {
        "patient_id": patient_id,
        "prediction_id": latest_prediction.id,
        "risk_score": float(
            latest_prediction.risk_score
        ),
        "risk_level": latest_prediction.risk_level,
        "model_version": latest_prediction.model_version,
        "prediction_created_at": latest_prediction.created_at,
        "priority_score": priority_result.priority_score,
        "priority_level": priority_result.priority_level,
    }