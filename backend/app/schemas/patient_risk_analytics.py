from pydantic import BaseModel


class RiskLevelStats(BaseModel):
    risk_level: str
    patient_count: int


class PatientRiskAnalyticsResponse(BaseModel):
    total_predictions: int
    low_risk: int
    medium_risk: int
    high_risk: int
    risk_distribution: list[RiskLevelStats]