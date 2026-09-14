from pydantic import BaseModel


class BedTypeStats(BaseModel):
    bed_type: str
    total_beds: int
    available_beds: int
    occupied_beds: int


class BedTypeDashboardResponse(BaseModel):
    bed_types: list[BedTypeStats]