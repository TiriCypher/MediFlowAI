from pydantic import BaseModel

from app.schemas.resource_summary import ResourceSummaryResponse
from app.schemas.appointment_analytics import AppointmentAnalyticsResponse
from app.schemas.doctor_workload_summary import DoctorWorkloadSummaryResponse


class OptimizationDashboardResponse(BaseModel):
    resources: ResourceSummaryResponse
    appointments: AppointmentAnalyticsResponse
    doctor_workload: DoctorWorkloadSummaryResponse