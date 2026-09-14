from app.database.connection import SessionLocal
from app.models.doctor import Doctor


print("=" * 70)
print("MEDIFLOW AI - DOCTOR DATABASE INSPECTION")
print("=" * 70)


db = SessionLocal()


try:

    doctors = db.query(Doctor).order_by(
        Doctor.id
    ).all()

    print(f"\nTotal Doctors: {len(doctors)}")

    print("\nDOCTOR RECORDS")
    print("-" * 70)

    if not doctors:
        print("No doctors found in database.")

    for doctor in doctors:

        print(
            f"\nDoctor ID       : {doctor.id}"
        )

        print(
            f"Department ID   : "
            f"{doctor.department_id}"
        )

        print(
            f"Specialization  : "
            f"{doctor.specialization}"
        )

        print(
            f"Availability    : "
            f"{doctor.availability}"
        )

finally:

    db.close()


print("\n" + "=" * 70)
print("DOCTOR DATABASE INSPECTION COMPLETED")
print("=" * 70)