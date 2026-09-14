from pydantic import BaseModel, Field


class PatientPriorityRequest(BaseModel):

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


class PatientPriorityResponse(BaseModel):

    patient_id: int

    risk_level: str

    priority_score: int

    priority_level: str