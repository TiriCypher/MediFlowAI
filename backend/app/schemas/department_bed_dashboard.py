from pydantic import BaseModel


class DepartmentBedStats(BaseModel):
    department_id: int
    department_name: str
    total_beds: int
    available_beds: int
    occupied_beds: int


class DepartmentBedDashboardResponse(BaseModel):
    departments: list[DepartmentBedStats]