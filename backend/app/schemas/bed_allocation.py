from pydantic import BaseModel


class BedAllocationRequest(BaseModel):
    patient_id: int
    required_bed_type: str
    preferred_department_id: int | None = None


class BedAllocationResponse(BaseModel):
    bed_id: int
    patient_id: int
    bed_number: str
    bed_type: str | None
    department_id: int | None
    status: str

class BedReleaseResponse(BaseModel):
    bed_id: int
    bed_number: str
    status: str
    patient_id: int | None