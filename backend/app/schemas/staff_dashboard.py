from pydantic import BaseModel

from app.schemas.resource_summary import (
    ResourceSummaryResponse,
)

from app.schemas.appointment_analytics import (
    AppointmentAnalyticsResponse,
)


class StaffDashboardResponse(BaseModel):

    resources: ResourceSummaryResponse

    appointments: AppointmentAnalyticsResponse