from sqlalchemy import select
from sqlalchemy.orm import Session

from app.models.user import User


def sync_user(
    db: Session,
    firebase_uid: str,
    name: str,
    email: str,
) -> User:

    user = db.scalar(
        select(User).where(
            User.firebase_uid == firebase_uid
        )
    )

    if user:
        changed = False

        if user.name != name:
            user.name = name
            changed = True

        if user.email != email:
            user.email = email
            changed = True

        if changed:
            db.commit()
            db.refresh(user)

        return user

    user = User(
        firebase_uid=firebase_uid,
        name=name,
        email=email,
        role="PATIENT",
    )

    db.add(user)
    db.commit()
    db.refresh(user)

    return user