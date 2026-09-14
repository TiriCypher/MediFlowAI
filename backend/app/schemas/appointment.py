from datetime import datetime

from pydantic import BaseModel, ConfigDict


class AppointmentCreate(BaseModel):
    patient_id: int
    doctor_id: int
    department_id: int | None = None
    appointment_time: datetime
    priority: int = 3
    status: str = "SCHEDULED"
    reason: str | None = None


class AppointmentResponse(BaseModel):
    model_config = ConfigDict(from_attributes=True)

    id: int
    patient_id: int
    doctor_id: int
    department_id: int | None
    appointment_time: datetime
    priority: int
    status: str
    reason: str | None


class OptimizedAppointmentRequest(BaseModel):
    patient_id: int
    specialization: str
    appointment_time: datetime
    department_id: int | None = None
    priority: int = 3
    reason: str | None = None


class OptimizedAppointmentResponse(BaseModel):
    model_config = ConfigDict(from_attributes=True)

    appointment_id: int
    patient_id: int
    doctor_id: int
    doctor_specialization: str
    department_id: int | None
    appointment_time: datetime
    priority: int
    status: str
    reason: str | None
    doctor_active_appointments: int