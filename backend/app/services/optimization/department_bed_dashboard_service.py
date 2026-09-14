from sqlalchemy import func, select
from sqlalchemy.orm import Session

from app.models.bed import Bed
from app.models.department import Department


def get_department_bed_dashboard(
    db: Session,
):

    departments = db.scalars(
        select(Department)
        .order_by(Department.name)
    ).all()

    results = []

    for department in departments:

        total_beds = db.scalar(
            select(
                func.count(Bed.id)
            ).where(
                Bed.department_id
                == department.id
            )
        ) or 0

        available_beds = db.scalar(
            select(
                func.count(Bed.id)
            ).where(
                Bed.department_id
                == department.id,
                func.upper(Bed.status)
                == "AVAILABLE",
            )
        ) or 0

        occupied_beds = db.scalar(
            select(
                func.count(Bed.id)
            ).where(
                Bed.department_id
                == department.id,
                func.upper(Bed.status)
                == "OCCUPIED",
            )
        ) or 0

        results.append(
            {
                "department_id": department.id,
                "department_name": department.name,
                "total_beds": total_beds,
                "available_beds": available_beds,
                "occupied_beds": occupied_beds,
            }
        )

    return {
        "departments": results
    }