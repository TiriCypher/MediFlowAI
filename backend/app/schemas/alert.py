from datetime import datetime

from pydantic import BaseModel, ConfigDict


class AlertCreate(BaseModel):
    patient_id: int | None = None
    type: str
    message: str
    status: str = "UNREAD"


class AlertResponse(BaseModel):
    model_config = ConfigDict(
        from_attributes=True
    )

    id: int
    patient_id: int | None
    type: str
    message: str
    status: str
    created_at: datetime