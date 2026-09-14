from app.database.connection import SessionLocal
from app.services.optimization.bed_allocation_service import (
    get_best_available_bed,
)


print("=" * 70)
print("MEDIFLOW AI - DATABASE BED ALLOCATION TEST")
print("=" * 70)


db = SessionLocal()


try:

    required_bed_type = "ICU"
    preferred_department_id = 1

    print(
        f"\nRequired Bed Type: "
        f"{required_bed_type}"
    )

    print(
        f"Preferred Department: "
        f"{preferred_department_id}"
    )

    print(
        "\nFinding best available bed..."
    )

    print("-" * 70)


    result = get_best_available_bed(
        db=db,
        required_bed_type=required_bed_type,
        preferred_department_id=(
            preferred_department_id
        ),
    )


    if result:

        print("\nSELECTED BED")
        print("-" * 70)

        print(
            f"Bed ID: {result.bed_id}"
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

    else:

        print(
            "\nNo suitable available bed "
            "found."
        )


finally:

    db.close()


print("\n" + "=" * 70)
print(
    "DATABASE BED ALLOCATION TEST COMPLETED"
)
print("=" * 70)