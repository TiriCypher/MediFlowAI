from pydantic import BaseModel

from app.schemas.bed_dashboard import BedDashboardResponse
from app.schemas.department_bed_dashboard import DepartmentBedStats
from app.schemas.bed_type_dashboard import BedTypeStats


class ResourceSummaryResponse(BaseModel):
    bed_summary: BedDashboardResponse
    department_summary: list[DepartmentBedStats]
    bed_type_summary: list[BedTypeStats]