from pydantic import BaseModel

from app.schemas.resource_summary import ResourceSummaryResponse
from app.schemas.appointment_analytics import AppointmentAnalyticsResponse
from app.schemas.doctor_workload_summary import DoctorWorkloadSummaryResponse
from app.schemas.latest_patient_risk_summary import (
    LatestPatientRiskSummaryResponse,
)


class SystemDashboardResponse(BaseModel):
    resources: ResourceSummaryResponse
    appointments: AppointmentAnalyticsResponse
    doctor_workload: DoctorWorkloadSummaryResponse
    patient_risk: LatestPatientRiskSummaryResponse