from sqlalchemy.orm import Session

from app.services.optimization.bed_dashboard_service import (
    get_bed_dashboard,
)

from app.services.optimization.department_bed_dashboard_service import (
    get_department_bed_dashboard,
)

from app.services.optimization.bed_type_dashboard_service import (
    get_bed_type_dashboard,
)


def get_resource_summary(
    db: Session,
):

    bed_summary = get_bed_dashboard(
        db=db,
    )

    department_summary = get_department_bed_dashboard(
        db=db,
    )

    bed_type_summary = get_bed_type_dashboard(
        db=db,
    )

    return {
        "bed_summary": bed_summary,
        "department_summary": department_summary[
            "departments"
        ],
        "bed_type_summary": bed_type_summary[
            "bed_types"
        ],
    }