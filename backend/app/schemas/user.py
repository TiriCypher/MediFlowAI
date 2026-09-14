from datetime import datetime
from typing import Literal

from pydantic import BaseModel, ConfigDict, EmailStr


class UserSyncRequest(BaseModel):
    name: str
    email: EmailStr


class UserRoleUpdateRequest(BaseModel):
    role: Literal[
        "ADMIN",
        "DOCTOR",
        "STAFF",
        "PATIENT",
    ]


class UserResponse(BaseModel):
    id: int
    firebase_uid: str
    name: str
    email: EmailStr
    role: str
    created_at: datetime

    model_config = ConfigDict(
        from_attributes=True
    )