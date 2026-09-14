from pathlib import Path
import sys


PROJECT_ROOT = Path(__file__).resolve().parents[3]
ML_DIR = PROJECT_ROOT / "ml"

if str(ML_DIR) not in sys.path:
    sys.path.append(str(ML_DIR))


from src.predict_readmission import predict_readmission


class MLPredictionService:

    @staticmethod
    def predict_readmission(
        patient_data: dict,
    ) -> dict:

        result = predict_readmission(
            patient_data
        )

        return {
            "prediction": result["prediction"],
            "readmission_probability":
                result["readmission_probability"],
            "risk_category":
                result["risk_category"],
        }