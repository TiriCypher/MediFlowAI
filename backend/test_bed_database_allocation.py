from app.database.connection import SessionLocal
from app.services.optimization.bed_allocation_database_service import (
    allocate_bed_to_patient,
)


print("=" * 70)
print("MEDIFLOW AI - BED DATABASE ALLOCATION TEST")
print("=" * 70)


db = SessionLocal()


try:

    patient_id = 1

    required_bed_type = "ICU"

    preferred_department_id = 1


    print(
        f"\nPatient ID: "
        f"{patient_id}"
    )

    print(
        f"Required Bed Type: "
        f"{required_bed_type}"
    )

    print(
        f"Preferred Department: "
        f"{preferred_department_id}"
    )

    print(
        "\nAllocating bed..."
    )

    print("-" * 70)


    result = allocate_bed_to_patient(
        db=db,
        patient_id=patient_id,
        required_bed_type=required_bed_type,
        preferred_department_id=(
            preferred_department_id
        ),
    )


    if result:

        print("\nBED ALLOCATED SUCCESSFULLY")
        print("-" * 70)

        print(
            f"Bed ID: {result.id}"
        )

        print(
            f"Bed Number: "
            f"{result.bed_number}"
        )

        print(
            f"Bed Type: "
            f"{result.bed_type}"
        )

        print(
            f"Department ID: "
            f"{result.department_id}"
        )

        print(
            f"Status: "
            f"{result.status}"
        )

        print(
            f"Allocated Patient ID: "
            f"{result.patient_id}"
        )

    else:

        print(
            "\nBed allocation failed."
        )


finally:

    db.close()


print("\n" + "=" * 70)
print(
    "BED DATABASE ALLOCATION TEST COMPLETED"
)
print("=" * 70)