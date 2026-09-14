from collections.abc import Callable

from fastapi import Depends, HTTPException, status
from sqlalchemy import select
from sqlalchemy.orm import Session

from app.dependencies.database import get_db
from app.dependencies.firebase_auth import get_current_firebase_user
from app.models.user import User


def get_current_user(
    firebase_user=Depends(get_current_firebase_user),
    db: Session = Depends(get_db),
) -> User:

    uid = firebase_user.get("uid")

    if not uid:
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Firebase UID missing.",
        )

    user = db.scalar(
        select(User).where(
            User.firebase_uid == uid
        )
    )

    if not user:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="User profile not found.",
        )

    return user


def require_roles(
    *allowed_roles: str,
) -> Callable:

    allowed = {
        role.upper()
        for role in allowed_roles
    }

    def role_checker(
        current_user: User = Depends(get_current_user),
    ) -> User:

        if current_user.role.upper() not in allowed:
            raise HTTPException(
                status_code=status.HTTP_403_FORBIDDEN,
                detail="You do not have permission to access this resource.",
            )

        return current_user

    return role_checker