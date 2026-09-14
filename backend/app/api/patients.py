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
from app.models.patient import Patient
from app.models.user import User
from app.schemas.patient import (
    PatientCreate,
    PatientResponse,
    PatientSelfLinkRequest,
    PatientUpdate,
)
from app.models.appointment import Appointment
from app.models.doctor import Doctor

router = APIRouter(
    prefix="/patients",
    tags=["Patients"],
)


# --------------------------------------------------
# CREATE PATIENT
# ADMIN / STAFF ONLY
# --------------------------------------------------

@router.post(
    "",
    response_model=PatientResponse,
    status_code=status.HTTP_201_CREATED,
)
def create_patient(
    patient_data: PatientCreate,
    db: Session = Depends(get_db),
    current_user: User = Depends(
        require_roles(
            "ADMIN",
            "STAFF",
        )
    ),
):
    # Prevent multiple patient profiles
    # being linked to the same user account.
    if patient_data.user_id is not None:

        existing_patient = db.scalar(
            select(Patient).where(
                Patient.user_id
                == patient_data.user_id
            )
        )

        if existing_patient:
            raise HTTPException(
                status_code=status.HTTP_409_CONFLICT,
                detail=(
                    "A patient profile is already "
                    "linked to this user."
                ),
            )

    patient = Patient(
        user_id=patient_data.user_id,
        name=patient_data.name,
        date_of_birth=patient_data.date_of_birth,
        gender=patient_data.gender,
        blood_group=patient_data.blood_group,
        phone=patient_data.phone,
        address=patient_data.address,
        medical_information=(
            patient_data.medical_information
        ),
    )

    db.add(patient)
    db.commit()
    db.refresh(patient)

    return patient


# --------------------------------------------------
# CURRENT PATIENT'S OWN PROFILE
# PATIENT ONLY
#
# IMPORTANT:
# Keep this route ABOVE /{patient_id}
# --------------------------------------------------

@router.get(
    "/me",
    response_model=PatientResponse,
)
def get_my_patient_profile(
    db: Session = Depends(get_db),
    current_user: User = Depends(
        require_roles("PATIENT")
    ),
):
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

    return patient

@router.patch(
    "/me",
    response_model=PatientResponse,
)
def update_my_patient_profile(
    patient_data: PatientUpdate,
    db: Session = Depends(get_db),
    current_user: User = Depends(
        require_roles("PATIENT")
    ),
):
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

    update_data = patient_data.model_dump(
        exclude_unset=True
    )

    for field, value in update_data.items():
        setattr(
            patient,
            field,
            value,
        )

    db.commit()
    db.refresh(patient)

    return patient


# --------------------------------------------------
# GET ALL PATIENTS
# HOSPITAL ROLES ONLY
# --------------------------------------------------

@router.get(
    "",
    response_model=list[PatientResponse],
)
def get_patients(
    db: Session = Depends(get_db),
    current_user: User = Depends(
        require_roles(
            "ADMIN",
            "DOCTOR",
            "STAFF",
        )
    ),
):
    role = current_user.role.upper()

    query = select(Patient)

    # ADMIN and STAFF can view
    # hospital-wide patient records.
    if role in {
        "ADMIN",
        "STAFF",
    }:
        pass

    # DOCTOR can view only patients
    # assigned through appointments.
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

        assigned_patient_ids = (
            select(
                Appointment.patient_id
            )
            .where(
                Appointment.doctor_id
                == doctor.id
            )
            .distinct()
        )

        query = query.where(
            Patient.id.in_(
                assigned_patient_ids
            )
        )

    patients = db.scalars(
        query.order_by(
            Patient.name
        )
    ).all()

    return patients


@router.post(
    "/link-me",
    response_model=PatientResponse,
)
def link_my_patient_profile(
    request: PatientSelfLinkRequest,
    db: Session = Depends(get_db),
    current_user: User = Depends(
        require_roles("PATIENT")
    ),
):
    existing_link = db.scalar(
        select(Patient).where(
            Patient.user_id == current_user.id
        )
    )

    if existing_link:
        raise HTTPException(
            status_code=status.HTTP_409_CONFLICT,
            detail=(
                "This account is already linked "
                "to a patient profile."
            ),
        )

    patient = db.get(
        Patient,
        request.patient_id,
    )

    if not patient:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Patient not found.",
        )

    if patient.user_id is not None:
        raise HTTPException(
            status_code=status.HTTP_409_CONFLICT,
            detail=(
                "This patient profile is already "
                "linked to another account."
            ),
        )

    patient.user_id = current_user.id

    db.commit()
    db.refresh(patient)

    return patient


# --------------------------------------------------
# GET PATIENT BY ID
#
# ADMIN / DOCTOR / STAFF:
#     Can access patient records
#
# PATIENT:
#     Can access ONLY own record
# --------------------------------------------------

@router.get(
    "/{patient_id}",
    response_model=PatientResponse,
)
def get_patient(
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

    # ADMIN / STAFF:
    # hospital-wide access.
    if role in {
        "ADMIN",
        "STAFF",
    }:
        return patient

    # DOCTOR:
    # only patients assigned through appointments.
    if role == "DOCTOR":

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

        assigned_appointment = db.scalar(
            select(Appointment).where(
                Appointment.doctor_id
                == doctor.id,
                Appointment.patient_id
                == patient.id,
            )
        )

        if not assigned_appointment:
            raise HTTPException(
                status_code=status.HTTP_403_FORBIDDEN,
                detail=(
                    "You can only access patients "
                    "assigned to you."
                ),
            )

        return patient

    # PATIENT:
    # only own linked profile.
    if role == "PATIENT":

        if patient.user_id != current_user.id:
            raise HTTPException(
                status_code=status.HTTP_403_FORBIDDEN,
                detail=(
                    "You can only access "
                    "your own patient record."
                ),
            )

        return patient

    raise HTTPException(
        status_code=status.HTTP_403_FORBIDDEN,
        detail=(
            "You do not have permission "
            "to access this patient."
        ),
    )