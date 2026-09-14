from sqlalchemy import select
from sqlalchemy.orm import Session

from app.models.bed import Bed
from app.services.optimization.bed_allocation import (
    BedAllocationEngine,
    BedCandidate,
)


def get_best_available_bed(
    db: Session,
    required_bed_type: str,
    preferred_department_id: int | None = None,
):

    available_beds = db.scalars(
        select(Bed)
        .where(
            Bed.status == "AVAILABLE"
        )
        .order_by(Bed.id)
    ).all()

    if not available_beds:
        return None

    bed_candidates = []

    for bed in available_beds:

        bed_candidates.append(
            BedCandidate(
                bed_id=bed.id,
                department_id=bed.department_id,
                bed_number=bed.bed_number,
                bed_type=bed.bed_type,
            )
        )

    engine = BedAllocationEngine()

    return engine.select_bed(
        beds=bed_candidates,
        required_bed_type=required_bed_type,
        preferred_department_id=(
            preferred_department_id
        ),
    )