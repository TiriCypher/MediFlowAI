from pydantic import BaseModel


class AppointmentStatusStats(BaseModel):
    status: str
    count: int


class DoctorWorkloadStats(BaseModel):
    doctor_id: int
    doctor_name: str
    specialization: str | None
    active_appointments: int
    total_appointments: int


class AppointmentAnalyticsResponse(BaseModel):
    total_appointments: int
    status_summary: list[AppointmentStatusStats]
    doctor_workload: list[DoctorWorkloadStats]