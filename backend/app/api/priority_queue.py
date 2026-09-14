from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy import select
from sqlalchemy.orm import Session

from app.dependencies.database import get_db
from app.models.ml_prediction import MLPrediction
from app.models.patient import Patient
from app.schemas.priority_queue import (
    PatientPriorityInput,
    PriorityQueueRequest,
    PriorityQueueResponse,
    RankedPatientResponse,
)
from app.services.optimization.patient_priority import (
    PatientPriorityEngine,
    PatientPriorityInput as EnginePatientPriorityInput,
)
from app.services.optimization.priority_queue import (
    PatientPriorityQueue,
)


router = APIRouter(
    prefix="/optimization",
    tags=["Priority Queue Optimization"],
)


@router.post(
    "/priority-queue",
    response_model=PriorityQueueResponse,
)
def create_priority_queue(
    queue_data: PriorityQueueRequest,
    db: Session = Depends(get_db),
):

    priority_engine = PatientPriorityEngine()
    priority_queue = PatientPriorityQueue()

    processed_patients = []

    for patient_data in queue_data.patients:

        patient = db.get(
            Patient,
            patient_data.patient_id,
        )

        if not patient:

            raise HTTPException(
                status_code=404,
                detail=(
                    f"Patient {patient_data.patient_id} "
                    "not found."
                ),
            )

        latest_prediction = db.scalars(
            select(MLPrediction)
            .where(
                MLPrediction.patient_id
                == patient_data.patient_id
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
                    f"No ML prediction found for "
                    f"patient {patient_data.patient_id}."
                ),
            )

        priority_input = EnginePatientPriorityInput(
            patient_id=patient_data.patient_id,
            urgency=patient_data.urgency,
            risk_level=latest_prediction.risk_level,
            waiting_time_minutes=(
                patient_data.waiting_time_minutes
            ),
            comorbidity_count=(
                patient_data.comorbidity_count
            ),
            age=patient_data.age,
        )

        priority_result = (
            priority_engine.calculate_priority(
                priority_input
            )
        )

        priority_queue.push(
            priority_result
        )

        processed_patients.append(
            {
                "patient_id": (
                    priority_result.patient_id
                ),
                "risk_level": (
                    latest_prediction.risk_level
                ),
            }
        )

    ranked_patients = []

    rank = 1

    while not priority_queue.is_empty():

        result = priority_queue.pop()

        patient_info = next(
            item
            for item in processed_patients
            if item["patient_id"]
            == result.patient_id
        )

        ranked_patients.append(
            RankedPatientResponse(
                rank=rank,
                patient_id=result.patient_id,
                risk_level=patient_info[
                    "risk_level"
                ],
                priority_score=(
                    result.priority_score
                ),
                priority_level=(
                    result.priority_level
                ),
            )
        )

        rank += 1

    return PriorityQueueResponse(
        total_patients=len(ranked_patients),
        ranked_patients=ranked_patients,
    )