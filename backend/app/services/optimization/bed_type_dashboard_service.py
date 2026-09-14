from sqlalchemy import func, select
from sqlalchemy.orm import Session

from app.models.bed import Bed


def get_bed_type_dashboard(
    db: Session,
):
    bed_types = db.scalars(
        select(Bed.bed_type)
        .where(Bed.bed_type.is_not(None))
        .distinct()
        .order_by(Bed.bed_type)
    ).all()

    results = []

    for bed_type in bed_types:

        total_beds = db.scalar(
            select(func.count(Bed.id)).where(
                func.upper(Bed.bed_type)
                == bed_type.upper()
            )
        ) or 0

        available_beds = db.scalar(
            select(func.count(Bed.id)).where(
                func.upper(Bed.bed_type)
                == bed_type.upper(),
                func.upper(Bed.status)
                == "AVAILABLE",
            )
        ) or 0

        occupied_beds = db.scalar(
            select(func.count(Bed.id)).where(
                func.upper(Bed.bed_type)
                == bed_type.upper(),
                func.upper(Bed.status)
                == "OCCUPIED",
            )
        ) or 0

        results.append(
            {
                "bed_type": bed_type,
                "total_beds": total_beds,
                "available_beds": available_beds,
                "occupied_beds": occupied_beds,
            }
        )

    return {
        "bed_types": results
    }