from datetime import date

from pydantic import BaseModel, ConfigDict


class PatientSelfLinkRequest(BaseModel):
    patient_id: int


class PatientCreate(BaseModel):
    user_id: int | None = None
    name: str
    date_of_birth: date | None = None
    gender: str | None = None
    blood_group: str | None = None
    phone: str | None = None
    address: str | None = None
    medical_information: str | None = None


class PatientUpdate(BaseModel):
    name: str | None = None
    phone: str | None = None
    address: str | None = None
    medical_information: str | None = None


class PatientResponse(BaseModel):
    model_config = ConfigDict(from_attributes=True)

    id: int
    user_id: int | None
    name: str
    date_of_birth: date | None
    gender: str | None
    blood_group: str | None
    phone: str | None
    address: str | None
    medical_information: str | None