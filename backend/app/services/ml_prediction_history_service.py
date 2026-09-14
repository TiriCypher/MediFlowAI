from datetime import datetime, timezone
from decimal import Decimal

from sqlalchemy.orm import Session

from app.models.ml_prediction import MLPrediction


class MLPredictionHistoryService:

    @staticmethod
    def save_prediction(
        db: Session,
        patient_id: int,
        risk_score: float,
        risk_level: str,
        model_version: str,
    ) -> MLPrediction:

        prediction = MLPrediction(
            patient_id=patient_id,
            risk_score=Decimal(
                str(round(risk_score, 4))
            ),
            risk_level=risk_level,
            model_version=model_version,
            created_at=datetime.now(timezone.utc),
        )

        db.add(prediction)
        db.commit()
        db.refresh(prediction)

        return prediction