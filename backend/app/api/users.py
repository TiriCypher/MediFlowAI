from fastapi import (
    APIRouter,
    Depends,
    HTTPException,
    status,
)
from sqlalchemy import select
from sqlalchemy.orm import Session

from app.dependencies.authorization import (
    get_current_user as get_authenticated_user,
    require_roles,
)
from app.dependencies.database import get_db
from app.dependencies.firebase_auth import (
    get_current_firebase_user,
)
from app.models.user import User
from app.schemas.user import (
    UserResponse,
    UserRoleUpdateRequest,
    UserSyncRequest,
)
from app.services.user_service import sync_user


router = APIRouter(
    prefix="/users",
    tags=["Users"],
)


# --------------------------------------------------
# FIREBASE -> POSTGRESQL USER SYNC
# --------------------------------------------------

@router.post(
    "/sync",
    response_model=UserResponse,
)
def sync_authenticated_user(
    request: UserSyncRequest,
    firebase_user=Depends(
        get_current_firebase_user
    ),
    db: Session = Depends(get_db),
):
    uid = firebase_user.get("uid")
    token_email = firebase_user.get("email")

    if not uid:
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Firebase UID missing.",
        )

    if (
        token_email
        and token_email.lower()
        != request.email.lower()
    ):
        raise HTTPException(
            status_code=status.HTTP_403_FORBIDDEN,
            detail=(
                "Email does not match "
                "authenticated Firebase user."
            ),
        )

    return sync_user(
        db=db,
        firebase_uid=uid,
        name=request.name.strip()
        or "MediFlow AI User",
        email=request.email,
    )


# --------------------------------------------------
# CURRENT LOGGED-IN USER
# --------------------------------------------------

@router.get(
    "/me",
    response_model=UserResponse,
)
def get_my_profile(
    current_user: User = Depends(
        get_authenticated_user
    ),
):
    return current_user


# --------------------------------------------------
# ADMIN: GET ALL USERS
# --------------------------------------------------

@router.get(
    "",
    response_model=list[UserResponse],
)
def get_all_users(
    db: Session = Depends(get_db),
    current_user: User = Depends(
        require_roles("ADMIN")
    ),
):
    users = db.scalars(
        select(User).order_by(User.id)
    ).all()

    return users


# --------------------------------------------------
# ADMIN: GET USER BY ID
# --------------------------------------------------

@router.get(
    "/{user_id}",
    response_model=UserResponse,
)
def get_user_by_id(
    user_id: int,
    db: Session = Depends(get_db),
    current_user: User = Depends(
        require_roles("ADMIN")
    ),
):
    user = db.get(
        User,
        user_id,
    )

    if not user:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="User not found.",
        )

    return user


# --------------------------------------------------
# ADMIN: CHANGE USER ROLE
# --------------------------------------------------

@router.patch(
    "/{user_id}/role",
    response_model=UserResponse,
)
def update_user_role(
    user_id: int,
    role_data: UserRoleUpdateRequest,
    db: Session = Depends(get_db),
    current_user: User = Depends(
        require_roles("ADMIN")
    ),
):
    user = db.get(
        User,
        user_id,
    )

    if not user:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="User not found.",
        )

    user.role = role_data.role

    db.commit()
    db.refresh(user)

    return user