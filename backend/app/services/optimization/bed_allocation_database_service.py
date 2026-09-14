from sqlalchemy.orm import Session

from app.models.bed import Bed
from app.models.patient import Patient
from app.services.optimization.bed_allocation_service import (
    get_best_available_bed,
)


def allocate_bed_to_patient(
    db: Session,
    patient_id: int,
    required_bed_type: str,
    preferred_department_id: int | None = None,
):

    patient = db.get(
        Patient,
        patient_id,
    )

    if not patient:
        return None

    allocation_result = get_best_available_bed(
        db=db,
        required_bed_type=required_bed_type,
        preferred_department_id=(
            preferred_department_id
        ),
    )

    if not allocation_result:
        return None

    bed = db.get(
        Bed,
        allocation_result.bed_id,
    )

    if not bed:
        return None

    bed.status = "OCCUPIED"
    bed.patient_id = patient_id

    db.commit()
    db.refresh(bed)

    return bed