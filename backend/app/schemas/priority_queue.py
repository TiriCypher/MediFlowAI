from pydantic import BaseModel


class PatientPriorityInput(BaseModel):

    patient_id: int
    urgency: str
    waiting_time_minutes: int
    comorbidity_count: int
    age: int


class PriorityQueueRequest(BaseModel):

    patients: list[PatientPriorityInput]


class RankedPatientResponse(BaseModel):

    rank: int
    patient_id: int
    risk_level: str
    priority_score: int
    priority_level: str


class PriorityQueueResponse(BaseModel):

    total_patients: int
    ranked_patients: list[RankedPatientResponse]