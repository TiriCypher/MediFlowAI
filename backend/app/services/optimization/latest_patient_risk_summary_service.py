from sqlalchemy import select
from sqlalchemy.orm import Session

from app.models.ml_prediction import MLPrediction


def get_latest_patient_risk_summary(
    db: Session,
):
    patient_ids = db.scalars(
        select(MLPrediction.patient_id)
        .distinct()
        .order_by(MLPrediction.patient_id)
    ).all()

    patients = []

    risk_counts = {
        "LOW": 0,
        "MEDIUM": 0,
        "HIGH": 0,
    }

    for patient_id in patient_ids:

        latest_prediction = db.scalars(
            select(MLPrediction)
            .where(
                MLPrediction.patient_id == patient_id
            )
            .order_by(
                MLPrediction.created_at.desc(),
                MLPrediction.id.desc(),
            )
            .limit(1)
        ).first()

        if latest_prediction is None:
            continue

        risk_level = latest_prediction.risk_level.upper()

        if risk_level in risk_counts:
            risk_counts[risk_level] += 1

        patients.append(
            {
                "patient_id": patient_id,
                "risk_level": risk_level,
                "risk_score": float(
                    latest_prediction.risk_score
                ),
            }
        )

    return {
        "total_patients": len(patients),
        "low_risk": risk_counts["LOW"],
        "medium_risk": risk_counts["MEDIUM"],
        "high_risk": risk_counts["HIGH"],
        "patients": patients,
    }