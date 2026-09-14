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


def get_optimization_dashboard(
    db: Session,
):
    resources = get_resource_summary(
        db=db,
    )

    appointments = get_appointment_analytics(
        db=db,
    )

    doctor_workload = get_doctor_workload_summary(
        db=db,
    )

    return {
        "resources": resources,
        "appointments": appointments,
        "doctor_workload": doctor_workload,
    }