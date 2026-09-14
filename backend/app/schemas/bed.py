from pydantic import BaseModel, ConfigDict


class BedCreate(BaseModel):
    department_id: int | None = None
    bed_number: str
    bed_type: str | None = None
    status: str = "AVAILABLE"
    patient_id: int | None = None


class BedResponse(BaseModel):
    model_config = ConfigDict(from_attributes=True)

    id: int
    department_id: int | None
    bed_number: str
    bed_type: str | None
    status: str
    patient_id: int | None