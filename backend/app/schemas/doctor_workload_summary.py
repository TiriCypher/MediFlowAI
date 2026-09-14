from pydantic import BaseModel


class DoctorWorkloadItem(BaseModel):
    doctor_id: int
    doctor_name: str
    specialization: str | None
    availability: str | None
    active_appointments: int
    total_appointments: int


class DoctorWorkloadSummaryResponse(BaseModel):
    total_doctors: int
    least_loaded_doctor_id: int | None
    most_loaded_doctor_id: int | None
    doctors: list[DoctorWorkloadItem]