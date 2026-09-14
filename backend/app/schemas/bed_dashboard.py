from pydantic import BaseModel


class BedDashboardResponse(BaseModel):
    total_beds: int
    available_beds: int
    occupied_beds: int

    total_icu_beds: int
    available_icu_beds: int

    total_general_beds: int
    available_general_beds: int

    occupancy_percentage: float