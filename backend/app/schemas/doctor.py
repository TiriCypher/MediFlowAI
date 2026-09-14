from pydantic import BaseModel


class DoctorCreate(BaseModel):
    user_id: int
    department_id: int | None = None
    specialization: str | None = None
    availability: str | None = None


class DoctorResponse(BaseModel):
    id: int
    user_id: int
    name: str
    department_id: int | None
    department_name: str | None = None
    specialization: str | None
    availability: str | None