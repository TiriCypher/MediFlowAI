from fastapi import (
    APIRouter,
    Depends,
    HTTPException,
    status,
)
from sqlalchemy import select
from sqlalchemy.orm import Session

from app.dependencies.authorization import (
    get_current_user,
    require_roles,
)
from app.dependencies.database import get_db

from app.models.appointment import Appointment
from app.models.doctor import Doctor
from app.models.ml_prediction import MLPrediction
from app.models.patient import Patient
from app.models.user import User

from app.schemas.ml_prediction import (
    MLPredictionResponse,
    PatientReadmissionPredictionResponse,
    ReadmissionPredictionRequest,
    ReadmissionPredictionResponse,
)

from app.services.ml_prediction_history_service import (
    MLPredictionHistoryService,
)
from app.services.ml_prediction_service import (
    MLPredictionService,
)


router = APIRouter(
    prefix="/predictions",
    tags=["Predictions"],
)


# --------------------------------------------------
# HELPER:
# CHECK WHETHER DOCTOR CAN ACCESS PATIENT
# --------------------------------------------------

def doctor_has_patient_access(
    db: Session,
    current_user: User,
    patient_id: int,
) -> bool:

    doctor = db.scalar(
        select(Doctor).where(
            Doctor.user_id == current_user.id
        )
    )

    if not doctor:
        return False

    appointment = db.scalar(
        select(Appointment).where(
            Appointment.doctor_id == doctor.id,
            Appointment.patient_id == patient_id,
        )
    )

    return appointment is not None


# --------------------------------------------------
# STANDALONE ML PREDICTION
#
# ADMIN / DOCTOR ONLY
#
# Does NOT save prediction to patient history.
# Useful for clinical/academic prediction testing.
# --------------------------------------------------

@router.post(
    "/readmission",
    response_model=ReadmissionPredictionResponse,
)
def predict_readmission(
    request: ReadmissionPredictionRequest,
    current_user: User = Depends(
        require_roles(
            "ADMIN",
            "DOCTOR",
        )
    ),
):

    result = (
        MLPredictionService.predict_readmission(
            patient_data=request.model_dump()
        )
    )

    return {
        "prediction": result["prediction"],
        "readmission_probability":
            result["readmission_probability"],
        "risk_category":
            result["risk_category"],
        "model_version": "1.0.0",
    }


# --------------------------------------------------
# PATIENT-SPECIFIC ML PREDICTION
#
# ADMIN:
#     Any patient
#
# DOCTOR:
#     Only patients who have an appointment
#     with that doctor
#
# STAFF / PATIENT:
#     Cannot generate clinical prediction
# --------------------------------------------------

@router.post(
    "/patients/{patient_id}/readmission",
    response_model=(
        PatientReadmissionPredictionResponse
    ),
)
def predict_patient_readmission(
    patient_id: int,
    request: ReadmissionPredictionRequest,
    db: Session = Depends(get_db),
    current_user: User = Depends(
        require_roles(
            "ADMIN",
            "DOCTOR",
        )
    ),
):

    patient = db.get(
        Patient,
        patient_id,
    )

    if not patient:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Patient not found.",
        )

    role = current_user.role.upper()

    if role == "DOCTOR":

        has_access = doctor_has_patient_access(
            db=db,
            current_user=current_user,
            patient_id=patient_id,
        )

        if not has_access:
            raise HTTPException(
                status_code=status.HTTP_403_FORBIDDEN,
                detail=(
                    "You can only generate "
                    "predictions for patients "
                    "assigned to you."
                ),
            )

    result = (
        MLPredictionService.predict_readmission(
            patient_data=request.model_dump()
        )
    )

    prediction = (
        MLPredictionHistoryService.save_prediction(
            db=db,
            patient_id=patient_id,
            risk_score=(
                result[
                    "readmission_probability"
                ]
            ),
            risk_level=(
                result["risk_category"]
            ),
            model_version="1.0.0",
        )
    )

    return {
        "prediction_id": prediction.id,
        "patient_id": prediction.patient_id,
        "prediction": result["prediction"],
        "readmission_probability": (
            result[
                "readmission_probability"
            ]
        ),
        "risk_category": (
            result["risk_category"]
        ),
        "model_version": (
            prediction.model_version
        ),
        "created_at": prediction.created_at,
    }


# --------------------------------------------------
# PATIENT PREDICTION HISTORY
#
# ADMIN:
#     Any patient
#
# DOCTOR:
#     Only assigned patients
#
# PATIENT:
#     Only own prediction history
#
# STAFF:
#     No clinical prediction access
# --------------------------------------------------

@router.get(
    "/patients/{patient_id}",
    response_model=list[
        MLPredictionResponse
    ],
)
def get_patient_prediction_history(
    patient_id: int,
    db: Session = Depends(get_db),
    current_user: User = Depends(
        get_current_user
    ),
):

    patient = db.get(
        Patient,
        patient_id,
    )

    if not patient:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Patient not found.",
        )

    role = current_user.role.upper()

    # ADMIN can access any patient's history.
    if role == "ADMIN":
        pass

    # DOCTOR must have an appointment
    # relationship with this patient.
    elif role == "DOCTOR":

        has_access = doctor_has_patient_access(
            db=db,
            current_user=current_user,
            patient_id=patient_id,
        )

        if not has_access:
            raise HTTPException(
                status_code=status.HTTP_403_FORBIDDEN,
                detail=(
                    "You can only access "
                    "prediction history for "
                    "patients assigned to you."
                ),
            )

    # PATIENT can access only own history.
    elif role == "PATIENT":

        if patient.user_id != current_user.id:
            raise HTTPException(
                status_code=status.HTTP_403_FORBIDDEN,
                detail=(
                    "You can only access your "
                    "own prediction history."
                ),
            )

    # STAFF and any unknown roles are denied.
    else:
        raise HTTPException(
            status_code=status.HTTP_403_FORBIDDEN,
            detail=(
                "You do not have permission "
                "to access prediction history."
            ),
        )

    predictions = db.scalars(
        select(MLPrediction)
        .where(
            MLPrediction.patient_id
            == patient_id
        )
        .order_by(
            MLPrediction.created_at.desc()
        )
    ).all()

    return predictions