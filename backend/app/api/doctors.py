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
from app.models.department import Department
from app.models.doctor import Doctor
from app.models.user import User
from app.schemas.doctor import (
    DoctorCreate,
    DoctorResponse,
)


router = APIRouter(
    prefix="/doctors",
    tags=["Doctors"],
)


# --------------------------------------------------
# HELPER
# Builds a richer doctor response containing:
# - doctor name
# - department name
# - specialization
# - availability
# --------------------------------------------------

def build_doctor_response(
    doctor: Doctor,
    db: Session,
) -> DoctorResponse:

    user = db.get(
        User,
        doctor.user_id,
    )

    department_name = None

    if doctor.department_id is not None:

        department = db.get(
            Department,
            doctor.department_id,
        )

        if department:
            department_name = department.name

    return DoctorResponse(
        id=doctor.id,
        user_id=doctor.user_id,
        name=(
            user.name
            if user
            else "Unknown Doctor"
        ),
        department_id=doctor.department_id,
        department_name=department_name,
        specialization=doctor.specialization,
        availability=doctor.availability,
    )


# --------------------------------------------------
# CREATE DOCTOR PROFILE
# ADMIN ONLY
# --------------------------------------------------

@router.post(
    "",
    response_model=DoctorResponse,
    status_code=status.HTTP_201_CREATED,
)
def create_doctor(
    doctor_data: DoctorCreate,
    db: Session = Depends(get_db),
    current_user: User = Depends(
        require_roles("ADMIN")
    ),
):
    user = db.get(
        User,
        doctor_data.user_id,
    )

    if not user:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="User not found.",
        )

    if user.role.upper() != "DOCTOR":
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail=(
                "The selected user must have "
                "the DOCTOR role."
            ),
        )

    existing_doctor = db.scalar(
        select(Doctor).where(
            Doctor.user_id
            == doctor_data.user_id
        )
    )

    if existing_doctor:
        raise HTTPException(
            status_code=status.HTTP_409_CONFLICT,
            detail=(
                "Doctor profile already exists "
                "for this user."
            ),
        )

    if doctor_data.department_id is not None:

        department = db.get(
            Department,
            doctor_data.department_id,
        )

        if not department:
            raise HTTPException(
                status_code=status.HTTP_404_NOT_FOUND,
                detail="Department not found.",
            )

    doctor = Doctor(
        user_id=doctor_data.user_id,
        department_id=doctor_data.department_id,
        specialization=doctor_data.specialization,
        availability=doctor_data.availability,
    )

    db.add(doctor)
    db.commit()
    db.refresh(doctor)

    return build_doctor_response(
        doctor=doctor,
        db=db,
    )


# --------------------------------------------------
# CURRENT DOCTOR'S OWN PROFILE
#
# IMPORTANT:
# Keep /me above /{doctor_id}
# --------------------------------------------------

@router.get(
    "/me",
    response_model=DoctorResponse,
)
def get_my_doctor_profile(
    db: Session = Depends(get_db),
    current_user: User = Depends(
        require_roles("DOCTOR")
    ),
):
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

    return build_doctor_response(
        doctor=doctor,
        db=db,
    )


# --------------------------------------------------
# DOCTOR DIRECTORY
# ALL AUTHENTICATED ROLES
# --------------------------------------------------

@router.get(
    "",
    response_model=list[DoctorResponse],
)
def get_doctors(
    db: Session = Depends(get_db),
    current_user: User = Depends(
        get_current_user
    ),
):
    doctors = db.scalars(
        select(Doctor).order_by(
            Doctor.id
        )
    ).all()

    return [
        build_doctor_response(
            doctor=doctor,
            db=db,
        )
        for doctor in doctors
    ]


# --------------------------------------------------
# GET DOCTOR BY ID
# ALL AUTHENTICATED ROLES
# --------------------------------------------------

@router.get(
    "/{doctor_id}",
    response_model=DoctorResponse,
)
def get_doctor(
    doctor_id: int,
    db: Session = Depends(get_db),
    current_user: User = Depends(
        get_current_user
    ),
):
    doctor = db.get(
        Doctor,
        doctor_id,
    )

    if not doctor:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Doctor not found.",
        )

    return build_doctor_response(
        doctor=doctor,
        db=db,
    )