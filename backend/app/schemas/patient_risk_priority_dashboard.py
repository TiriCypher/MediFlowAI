from datetime import datetime

from pydantic import BaseModel, Field


class PatientRiskPriorityDashboardRequest(BaseModel):
    patient_id: int

    urgency: str = Field(
        ...,
        examples=["Emergency"],
    )

    waiting_time_minutes: int = Field(
        ...,
        ge=0,
        examples=[60],
    )

    comorbidity_count: int = Field(
        ...,
        ge=0,
        examples=[3],
    )

    age: int = Field(
        ...,
        ge=0,
        le=120,
        examples=[68],
    )


class PatientRiskPriorityDashboardResponse(BaseModel):
    patient_id: int

    prediction_id: int
    risk_score: float
    risk_level: str
    model_version: str | None
    prediction_created_at: datetime

    priority_score: int
    priority_level: str