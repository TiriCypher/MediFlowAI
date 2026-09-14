from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy import select
from sqlalchemy.orm import Session

from app.dependencies.database import get_db
from app.models.ml_prediction import MLPrediction
from app.models.patient import Patient
from app.schemas.patient_priority import (
    PatientPriorityRequest,
    PatientPriorityResponse,
)
from app.services.optimization.patient_priority import (
    PatientPriorityEngine,
    PatientPriorityInput,
)


router = APIRouter(
    prefix="/optimization",
    tags=["Patient Priority Optimization"],
)


@router.post(
    "/patient-priority",
    response_model=PatientPriorityResponse,
)
def calculate_patient_priority(
    priority_data: PatientPriorityRequest,
    db: Session = Depends(get_db),
):

    patient = db.get(
        Patient,
        priority_data.patient_id,
    )

    if not patient:

        raise HTTPException(
            status_code=404,
            detail="Patient not found.",
        )

    latest_prediction = db.scalars(
        select(MLPrediction)
        .where(
            MLPrediction.patient_id
            == priority_data.patient_id
        )
        .order_by(
            MLPrediction.created_at.desc()
        )
        .limit(1)
    ).first()

    if not latest_prediction:

        raise HTTPException(
            status_code=404,
            detail=(
                "No ML prediction found for this patient."
            ),
        )

    engine = PatientPriorityEngine()

    patient_input = PatientPriorityInput(
        patient_id=priority_data.patient_id,
        urgency=priority_data.urgency,
        risk_level=latest_prediction.risk_level,
        waiting_time_minutes=(
            priority_data.waiting_time_minutes
        ),
        comorbidity_count=(
            priority_data.comorbidity_count
        ),
        age=priority_data.age,
    )

    result = engine.calculate_priority(
        patient_input
    )

    return PatientPriorityResponse(
        patient_id=result.patient_id,
        risk_level=latest_prediction.risk_level,
        priority_score=result.priority_score,
        priority_level=result.priority_level,
    )