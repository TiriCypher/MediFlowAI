from app.database.connection import SessionLocal
from app.services.optimization.doctor_load_balancing_service import (
    get_least_loaded_doctor,
)


print("=" * 70)
print("MEDIFLOW AI - DATABASE DOCTOR LOAD BALANCING TEST")
print("=" * 70)


db = SessionLocal()


try:

    required_specialization = "Cardiology"

    print(
        f"\nRequested Specialization: "
        f"{required_specialization}"
    )

    print("\nFinding least loaded available doctor...")
    print("-" * 70)

    result = get_least_loaded_doctor(
        db=db,
        required_specialization=required_specialization,
    )

    if result:

        print("\nSELECTED DOCTOR")
        print("-" * 70)

        print(f"Doctor ID: {result.doctor_id}")
        print(
            f"Specialization: "
            f"{result.specialization}"
        )
        print(
            f"Active Appointments: "
            f"{result.current_appointments}"
        )

    else:

        print(
            "\nNo available doctor found "
            "for this specialization."
        )

finally:

    db.close()


print("\n" + "=" * 70)
print("DATABASE DOCTOR LOAD BALANCING TEST COMPLETED")
print("=" * 70)