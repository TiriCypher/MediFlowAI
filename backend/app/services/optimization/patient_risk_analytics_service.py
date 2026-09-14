from sqlalchemy import func, select
from sqlalchemy.orm import Session

from app.models.ml_prediction import MLPrediction


def get_patient_risk_analytics(
    db: Session,
):
    total_predictions = db.scalar(
        select(func.count(MLPrediction.id))
    ) or 0

    rows = db.execute(
        select(
            func.upper(MLPrediction.risk_level),
            func.count(MLPrediction.id),
        )
        .group_by(
            func.upper(MLPrediction.risk_level)
        )
        .order_by(
            func.upper(MLPrediction.risk_level)
        )
    ).all()

    risk_counts = {
        "LOW": 0,
        "MEDIUM": 0,
        "HIGH": 0,
    }

    risk_distribution = []

    for risk_level, count in rows:
        risk_counts[risk_level] = count

        risk_distribution.append(
            {
                "risk_level": risk_level,
                "patient_count": count,
            }
        )

    return {
        "total_predictions": total_predictions,
        "low_risk": risk_counts["LOW"],
        "medium_risk": risk_counts["MEDIUM"],
        "high_risk": risk_counts["HIGH"],
        "risk_distribution": risk_distribution,
    }