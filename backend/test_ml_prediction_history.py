from app.dependencies.database import get_db
from app.services.ml_prediction_history_service import (
    MLPredictionHistoryService,
)


db = next(get_db())


try:

    prediction = (
        MLPredictionHistoryService.save_prediction(
            db=db,
            patient_id=1,
            risk_score=0.5139,
            risk_level="MEDIUM",
            model_version="1.0.0",
        )
    )

    print("=" * 70)
    print("MEDIFLOW AI - ML PREDICTION HISTORY TEST")
    print("=" * 70)

    print()
    print("Prediction ID:", prediction.id)
    print("Patient ID:", prediction.patient_id)
    print("Risk Score:", prediction.risk_score)
    print("Risk Level:", prediction.risk_level)
    print("Model Version:", prediction.model_version)
    print("Created At:", prediction.created_at)

    print()
    print("=" * 70)
    print("PREDICTION HISTORY TEST COMPLETED")
    print("=" * 70)

finally:
    db.close()