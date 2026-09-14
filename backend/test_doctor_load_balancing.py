from app.services.optimization.doctor_load_balancing import (
    DoctorLoadBalancingEngine,
    DoctorWorkload,
)


print("=" * 70)
print("MEDIFLOW AI - DOCTOR LOAD BALANCING ENGINE TEST")
print("=" * 70)


engine = DoctorLoadBalancingEngine()


doctors = [

    DoctorWorkload(
        doctor_id=1,
        specialization="Cardiology",
        current_appointments=12,
        is_available=True,
    ),

    DoctorWorkload(
        doctor_id=2,
        specialization="Cardiology",
        current_appointments=7,
        is_available=True,
    ),

    DoctorWorkload(
        doctor_id=3,
        specialization="Cardiology",
        current_appointments=9,
        is_available=True,
    ),

    DoctorWorkload(
        doctor_id=4,
        specialization="Cardiology",
        current_appointments=3,
        is_available=False,
    ),

    DoctorWorkload(
        doctor_id=5,
        specialization="Neurology",
        current_appointments=2,
        is_available=True,
    ),
]


print("\nDOCTOR WORKLOADS")
print("-" * 70)

for doctor in doctors:

    availability = (
        "Available"
        if doctor.is_available
        else "Unavailable"
    )

    print(
        f"Doctor {doctor.doctor_id} | "
        f"{doctor.specialization} | "
        f"Appointments: {doctor.current_appointments} | "
        f"{availability}"
    )


print("\n" + "=" * 70)
print("REQUESTED SPECIALIZATION: Cardiology")
print("=" * 70)


result = engine.select_doctor(
    doctors=doctors,
    required_specialization="Cardiology",
)


if result:

    print("\nSELECTED DOCTOR")
    print("-" * 70)

    print(f"Doctor ID: {result.doctor_id}")
    print(f"Specialization: {result.specialization}")
    print(
        f"Current Appointments: "
        f"{result.current_appointments}"
    )

else:

    print(
        "\nNo available doctor found "
        "for the requested specialization."
    )


print("\n" + "=" * 70)
print("DOCTOR LOAD BALANCING TEST COMPLETED")
print("=" * 70)