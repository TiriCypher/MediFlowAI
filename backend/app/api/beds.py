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
from app.models.bed import Bed
from app.models.department import Department
from app.models.patient import Patient
from app.models.user import User
from app.schemas.bed import (
    BedCreate,
    BedResponse,
)


router = APIRouter(
    prefix="/beds",
    tags=["Beds"],
)


# --------------------------------------------------
# CREATE BED
# ADMIN ONLY
# --------------------------------------------------

@router.post(
    "",
    response_model=BedResponse,
    status_code=status.HTTP_201_CREATED,
)
def create_bed(
    bed_data: BedCreate,
    db: Session = Depends(get_db),
    current_user: User = Depends(
        require_roles("ADMIN")
    ),
):
    existing_bed = db.scalar(
        select(Bed).where(
            Bed.bed_number
            == bed_data.bed_number
        )
    )

    if existing_bed:
        raise HTTPException(
            status_code=status.HTTP_409_CONFLICT,
            detail=(
                "Bed with this number "
                "already exists."
            ),
        )

    if bed_data.department_id is not None:

        department = db.get(
            Department,
            bed_data.department_id,
        )

        if not department:
            raise HTTPException(
                status_code=status.HTTP_404_NOT_FOUND,
                detail="Department not found.",
            )

    if bed_data.patient_id is not None:

        patient = db.get(
            Patient,
            bed_data.patient_id,
        )

        if not patient:
            raise HTTPException(
                status_code=status.HTTP_404_NOT_FOUND,
                detail="Patient not found.",
            )

    bed = Bed(
        department_id=bed_data.department_id,
        bed_number=bed_data.bed_number,
        bed_type=bed_data.bed_type,
        status=bed_data.status,
        patient_id=bed_data.patient_id,
    )

    db.add(bed)
    db.commit()
    db.refresh(bed)

    return bed


# --------------------------------------------------
# GET ALL BEDS
# ADMIN / STAFF / DOCTOR
# --------------------------------------------------

@router.get(
    "",
    response_model=list[BedResponse],
)
def get_beds(
    db: Session = Depends(get_db),
    current_user: User = Depends(
        require_roles(
            "ADMIN",
            "STAFF",
            "DOCTOR",
        )
    ),
):
    beds = db.scalars(
        select(Bed).order_by(
            Bed.bed_number
        )
    ).all()

    return beds


# --------------------------------------------------
# GET BED BY ID
# ADMIN / STAFF / DOCTOR
# --------------------------------------------------

@router.get(
    "/{bed_id}",
    response_model=BedResponse,
)
def get_bed(
    bed_id: int,
    db: Session = Depends(get_db),
    current_user: User = Depends(
        require_roles(
            "ADMIN",
            "STAFF",
            "DOCTOR",
        )
    ),
):
    bed = db.get(
        Bed,
        bed_id,
    )

    if not bed:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Bed not found.",
        )

    return bed