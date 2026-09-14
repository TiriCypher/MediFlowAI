from pydantic import BaseModel


class DoctorLoadBalancingRequest(BaseModel):

    specialization: str


class DoctorLoadBalancingResponse(BaseModel):

    doctor_id: int
    specialization: str
    current_appointments: int