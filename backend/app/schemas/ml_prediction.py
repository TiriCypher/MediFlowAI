from datetime import datetime
from decimal import Decimal

from pydantic import BaseModel, ConfigDict, Field


class ReadmissionPredictionRequest(BaseModel):

    los_days: int = Field(
        ge=1,
        le=365,
    )

    admit_type: str

    ward_type: str

    num_procedures: int = Field(
        ge=0,
    )

    charlson_index: int = Field(
        ge=0,
    )

    hba1c: float = Field(
        ge=0,
    )

    creatinine: float = Field(
        gt=0,
    )

    haemoglobin: float = Field(
        gt=0,
    )

    systolic_bp: int = Field(
        ge=50,
        le=300,
    )

    age: int = Field(
        ge=0,
        le=120,
    )

    gender: str

    state: str

    bpl_card: bool

    insurance_type: str | None = None

    comorbidity_count: int = Field(
        ge=0,
    )

    prev_admissions: int = Field(
        ge=0,
    )

    hospital_state: str

    tier: str

    beds: int = Field(
        ge=1,
    )

    teaching: bool

    diagnosis_count: int = Field(
        ge=1,
    )

    primary_diagnosis_category: str

    admission_month: int = Field(
        ge=1,
        le=12,
    )


class ReadmissionPredictionResponse(BaseModel):

    prediction: int

    readmission_probability: float

    risk_category: str

    model_version: str
    
class PatientReadmissionPredictionResponse(
    ReadmissionPredictionResponse
):

    prediction_id: int

    patient_id: int

    created_at: datetime


class MLPredictionResponse(BaseModel):

    model_config = ConfigDict(
        from_attributes=True
    )

    id: int

    patient_id: int

    risk_score: Decimal

    risk_level: str

    model_version: str | None

    created_at: datetime