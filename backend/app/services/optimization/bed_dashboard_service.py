from sqlalchemy import func, select
from sqlalchemy.orm import Session

from app.models.bed import Bed


def get_bed_dashboard(
    db: Session,
):

    total_beds = db.scalar(
        select(
            func.count(Bed.id)
        )
    ) or 0

    available_beds = db.scalar(
        select(
            func.count(Bed.id)
        ).where(
            func.upper(Bed.status)
            == "AVAILABLE"
        )
    ) or 0

    occupied_beds = db.scalar(
        select(
            func.count(Bed.id)
        ).where(
            func.upper(Bed.status)
            == "OCCUPIED"
        )
    ) or 0

    total_icu_beds = db.scalar(
        select(
            func.count(Bed.id)
        ).where(
            func.upper(Bed.bed_type)
            == "ICU"
        )
    ) or 0

    available_icu_beds = db.scalar(
        select(
            func.count(Bed.id)
        ).where(
            func.upper(Bed.bed_type)
            == "ICU",
            func.upper(Bed.status)
            == "AVAILABLE",
        )
    ) or 0

    total_general_beds = db.scalar(
        select(
            func.count(Bed.id)
        ).where(
            func.upper(Bed.bed_type)
            == "GENERAL"
        )
    ) or 0

    available_general_beds = db.scalar(
        select(
            func.count(Bed.id)
        ).where(
            func.upper(Bed.bed_type)
            == "GENERAL",
            func.upper(Bed.status)
            == "AVAILABLE",
        )
    ) or 0

    occupancy_percentage = 0.0

    if total_beds > 0:
        occupancy_percentage = round(
            (occupied_beds / total_beds) * 100,
            2,
        )

    return {
        "total_beds": total_beds,
        "available_beds": available_beds,
        "occupied_beds": occupied_beds,
        "total_icu_beds": total_icu_beds,
        "available_icu_beds": available_icu_beds,
        "total_general_beds": total_general_beds,
        "available_general_beds": (
            available_general_beds
        ),
        "occupancy_percentage": (
            occupancy_percentage
        ),
    }