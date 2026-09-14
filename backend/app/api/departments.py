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
from app.models.user import User
from app.schemas.department import (
    DepartmentCreate,
    DepartmentResponse,
)


router = APIRouter(
    prefix="/departments",
    tags=["Departments"],
)


# --------------------------------------------------
# CREATE DEPARTMENT
# ADMIN ONLY
# --------------------------------------------------

@router.post(
    "",
    response_model=DepartmentResponse,
    status_code=status.HTTP_201_CREATED,
)
def create_department(
    department_data: DepartmentCreate,
    db: Session = Depends(get_db),
    current_user: User = Depends(
        require_roles("ADMIN")
    ),
):
    existing_department = db.scalar(
        select(Department).where(
            Department.name
            == department_data.name
        )
    )

    if existing_department:
        raise HTTPException(
            status_code=status.HTTP_409_CONFLICT,
            detail="Department already exists.",
        )

    department = Department(
        name=department_data.name,
        description=(
            department_data.description
        ),
    )

    db.add(department)
    db.commit()
    db.refresh(department)

    return department


# --------------------------------------------------
# GET ALL DEPARTMENTS
# ANY AUTHENTICATED USER
# --------------------------------------------------

@router.get(
    "",
    response_model=list[DepartmentResponse],
)
def get_departments(
    db: Session = Depends(get_db),
    current_user: User = Depends(
        get_current_user
    ),
):
    departments = db.scalars(
        select(Department).order_by(
            Department.name
        )
    ).all()

    return departments


# --------------------------------------------------
# GET DEPARTMENT BY ID
# ANY AUTHENTICATED USER
# --------------------------------------------------

@router.get(
    "/{department_id}",
    response_model=DepartmentResponse,
)
def get_department(
    department_id: int,
    db: Session = Depends(get_db),
    current_user: User = Depends(
        get_current_user
    ),
):
    department = db.get(
        Department,
        department_id,
    )

    if not department:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Department not found.",
        )

    return department