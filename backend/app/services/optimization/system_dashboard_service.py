from sqlalchemy.orm import Session

from app.services.optimization.resource_summary_service import (
    get_resource_summary,
)

from app.services.optimization.appointment_analytics_service import (
    get_appointment_analytics,
)

from app.services.optimization.doctor_workload_summary_service import (
    get_doctor_workload_summary,
)

from app.services.optimization.latest_patient_risk_summary_service import (
    get_latest_patient_risk_summary,
)


def get_system_dashboard(
    db: Session,
):
    return {
        "resources": get_resource_summary(db=db),
        "appointments": get_appointment_analytics(db=db),
        "doctor_workload": get_doctor_workload_summary(db=db),
        "patient_risk": get_latest_patient_risk_summary(db=db),
    }