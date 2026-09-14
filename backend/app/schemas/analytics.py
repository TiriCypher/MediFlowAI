from pydantic import BaseModel


class DoctorAppointmentAnalytics(BaseModel):

    total: int

    scheduled: int

    completed: int

    cancelled: int

    completion_rate: float


class DoctorRiskAnalytics(BaseModel):

    total_predictions: int

    low_risk: int

    medium_risk: int

    high_risk: int


class DoctorAnalyticsResponse(BaseModel):

    doctor_id: int

    doctor_name: str

    specialization: str | None

    assigned_patients: int

    appointments: DoctorAppointmentAnalytics

    patient_risk: DoctorRiskAnalytics