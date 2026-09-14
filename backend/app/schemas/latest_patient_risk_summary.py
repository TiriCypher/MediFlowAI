from pydantic import BaseModel


class LatestPatientRiskItem(BaseModel):
    patient_id: int
    risk_level: str
    risk_score: float


class LatestPatientRiskSummaryResponse(BaseModel):
    total_patients: int
    low_risk: int
    medium_risk: int
    high_risk: int
    patients: list[LatestPatientRiskItem]