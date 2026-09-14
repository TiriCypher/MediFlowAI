from sqlalchemy.orm import Session

from app.models.bed import Bed


def release_bed(
    db: Session,
    bed_id: int,
):

    bed = db.get(
        Bed,
        bed_id,
    )

    if not bed:
        return None

    if bed.status != "OCCUPIED":
        return False

    bed.status = "AVAILABLE"
    bed.patient_id = None

    db.commit()
    db.refresh(bed)

    return bed