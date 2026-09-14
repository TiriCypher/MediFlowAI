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
from app.models.alert import Alert
from app.models.appointment import Appointment
from app.models.doctor import Doctor
from app.models.patient import Patient
from app.models.user import User
from app.schemas.alert import (
    AlertCreate,
    AlertResponse,
)


router = APIRouter(
    prefix="/alerts",
    tags=["Alerts"],
)


# --------------------------------------------------
# HELPER
# CHECK DOCTOR ACCESS TO PATIENT
# --------------------------------------------------

def doctor_has_patient_access(
    db: Session,
    current_user: User,
    patient_id: int,
) -> bool:

    doctor = db.scalar(
        select(Doctor).where(
            Doctor.user_id
            == current_user.id
        )
    )

    if not doctor:
        return False

    appointment = db.scalar(
        select(Appointment).where(
            Appointment.doctor_id
            == doctor.id,
            Appointment.patient_id
            == patient_id,
        )
    )

    return appointment is not None


# --------------------------------------------------
# CREATE ALERT
# ADMIN / STAFF ONLY
# --------------------------------------------------

@router.post(
    "",
    response_model=AlertResponse,
    status_code=status.HTTP_201_CREATED,
)
def create_alert(
    alert_data: AlertCreate,
    db: Session = Depends(get_db),
    current_user: User = Depends(
        require_roles(
            "ADMIN",
            "STAFF",
        )
    ),
):
    if alert_data.patient_id is not None:

        patient = db.get(
            Patient,
            alert_data.patient_id,
        )

        if not patient:
            raise HTTPException(
                status_code=status.HTTP_404_NOT_FOUND,
                detail="Patient not found.",
            )

    alert = Alert(
        patient_id=alert_data.patient_id,
        type=alert_data.type,
        message=alert_data.message,
        status=alert_data.status,
    )

    db.add(alert)
    db.commit()
    db.refresh(alert)

    return alert


# --------------------------------------------------
# GET ALERTS
#
# ADMIN / STAFF:
#     All alerts
#
# DOCTOR:
#     Alerts belonging to assigned patients
#
# PATIENT:
#     Own alerts only
# --------------------------------------------------

@router.get(
    "",
    response_model=list[AlertResponse],
)
def get_alerts(
    db: Session = Depends(get_db),
    current_user: User = Depends(
        get_current_user
    ),
):
    role = current_user.role.upper()

    query = select(Alert)

    if role in {
        "ADMIN",
        "STAFF",
    }:
        pass

    elif role == "DOCTOR":

        doctor = db.scalar(
            select(Doctor).where(
                Doctor.user_id
                == current_user.id
            )
        )

        if not doctor:
            raise HTTPException(
                status_code=status.HTTP_404_NOT_FOUND,
                detail=(
                    "Doctor profile is not linked "
                    "to this account."
                ),
            )

        patient_ids = select(
            Appointment.patient_id
        ).where(
            Appointment.doctor_id
            == doctor.id
        )

        query = query.where(
            Alert.patient_id.in_(
                patient_ids
            )
        )

    elif role == "PATIENT":

        patient = db.scalar(
            select(Patient).where(
                Patient.user_id
                == current_user.id
            )
        )

        if not patient:
            raise HTTPException(
                status_code=status.HTTP_404_NOT_FOUND,
                detail=(
                    "Patient profile is not linked "
                    "to this account."
                ),
            )

        query = query.where(
            Alert.patient_id
            == patient.id
        )

    else:
        raise HTTPException(
            status_code=status.HTTP_403_FORBIDDEN,
            detail=(
                "You do not have permission "
                "to access alerts."
            ),
        )

    alerts = db.scalars(
        query.order_by(
            Alert.created_at.desc()
        )
    ).all()

    return alerts


# --------------------------------------------------
# GET ALERT BY ID
#
# ADMIN / STAFF:
#     Any alert
#
# DOCTOR:
#     Alert for assigned patient only
#
# PATIENT:
#     Own alert only
# --------------------------------------------------

@router.get(
    "/{alert_id}",
    response_model=AlertResponse,
)
def get_alert(
    alert_id: int,
    db: Session = Depends(get_db),
    current_user: User = Depends(
        get_current_user
    ),
):
    alert = db.get(
        Alert,
        alert_id,
    )

    if not alert:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Alert not found.",
        )

    role = current_user.role.upper()

    if role in {
        "ADMIN",
        "STAFF",
    }:
        return alert

    if role == "DOCTOR":

        if alert.patient_id is None:
            raise HTTPException(
                status_code=status.HTTP_403_FORBIDDEN,
                detail=(
                    "You do not have permission "
                    "to access this alert."
                ),
            )

        if not doctor_has_patient_access(
            db=db,
            current_user=current_user,
            patient_id=alert.patient_id,
        ):
            raise HTTPException(
                status_code=status.HTTP_403_FORBIDDEN,
                detail=(
                    "You can only access alerts "
                    "for patients assigned to you."
                ),
            )

        return alert

    if role == "PATIENT":

        patient = db.scalar(
            select(Patient).where(
                Patient.user_id
                == current_user.id
            )
        )

        if not patient:
            raise HTTPException(
                status_code=status.HTTP_404_NOT_FOUND,
                detail=(
                    "Patient profile is not linked "
                    "to this account."
                ),
            )

        if alert.patient_id != patient.id:
            raise HTTPException(
                status_code=status.HTTP_403_FORBIDDEN,
                detail=(
                    "You can only access "
                    "your own alerts."
                ),
            )

        return alert

    raise HTTPException(
        status_code=status.HTTP_403_FORBIDDEN,
        detail=(
            "You do not have permission "
            "to access this alert."
        ),
    )

# --------------------------------------------------
# MARK ALERT AS READ
#
# ADMIN / STAFF:
#     Can mark any alert as READ.
#
# PATIENT:
#     Can mark own alert as READ only.
#
# DOCTOR:
#     Read-only access. Cannot change alert status.
# --------------------------------------------------

@router.patch(
    "/{alert_id}/read",
    response_model=AlertResponse,
)
def mark_alert_as_read(
    alert_id: int,
    db: Session = Depends(get_db),
    current_user: User = Depends(
        get_current_user
    ),
):
    # ----------------------------------------------
    # FIND ALERT
    # ----------------------------------------------

    alert = db.get(
        Alert,
        alert_id,
    )

    if not alert:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Alert not found.",
        )

    role = current_user.role.upper()

    # ----------------------------------------------
    # ADMIN / STAFF
    # MAY MARK ANY ALERT AS READ
    # ----------------------------------------------

    if role in {
        "ADMIN",
        "STAFF",
    }:
        alert.status = "READ"

        db.commit()
        db.refresh(alert)

        return alert

    # ----------------------------------------------
    # PATIENT
    # MAY UPDATE OWN ALERT ONLY
    # ----------------------------------------------

    if role == "PATIENT":

        patient = db.scalar(
            select(Patient).where(
                Patient.user_id
                == current_user.id
            )
        )

        if not patient:
            raise HTTPException(
                status_code=status.HTTP_404_NOT_FOUND,
                detail=(
                    "Patient profile is not linked "
                    "to this account."
                ),
            )

        if alert.patient_id != patient.id:
            raise HTTPException(
                status_code=status.HTTP_403_FORBIDDEN,
                detail=(
                    "You can only update "
                    "your own alerts."
                ),
            )

        alert.status = "READ"

        db.commit()
        db.refresh(alert)

        return alert

    # ----------------------------------------------
    # ALL OTHER ROLES
    # ----------------------------------------------

    raise HTTPException(
        status_code=status.HTTP_403_FORBIDDEN,
        detail=(
            "You do not have permission "
            "to update alerts."
        ),
    )