from app.services.optimization.patient_priority import (
    PatientPriorityEngine,
    PatientPriorityInput,
    PatientPriorityQueue,
)


def main():

    print("=" * 70)
    print("MEDIFLOW AI - PATIENT PRIORITY ENGINE TEST")
    print("=" * 70)

    engine = PatientPriorityEngine()
    queue = PatientPriorityQueue()

    patients = [
        PatientPriorityInput(
            patient_id=1,
            urgency="Elective",
            risk_level="LOW",
            waiting_time_minutes=10,
            comorbidity_count=0,
            age=25,
        ),
        PatientPriorityInput(
            patient_id=2,
            urgency="Routine",
            risk_level="MEDIUM",
            waiting_time_minutes=30,
            comorbidity_count=2,
            age=45,
        ),
        PatientPriorityInput(
            patient_id=3,
            urgency="Urgent",
            risk_level="HIGH",
            waiting_time_minutes=60,
            comorbidity_count=3,
            age=68,
        ),
        PatientPriorityInput(
            patient_id=4,
            urgency="Emergency",
            risk_level="HIGH",
            waiting_time_minutes=90,
            comorbidity_count=4,
            age=78,
        ),
    ]

    print("\nCALCULATING PATIENT PRIORITIES\n")

    for patient_input in patients:

        result = engine.calculate_priority(
            patient_input
        )

        queue.add_patient(result)

        print(
            f"Patient ID: {result.patient_id}"
        )

        print(
            f"Priority Score: {result.priority_score}"
        )

        print(
            f"Priority Level: {result.priority_level}"
        )

        print("-" * 40)

    print("\nQUEUE SIZE")
    print(
        queue.get_queue_size()
    )

    next_patient = queue.get_next_patient()

    print("\nHIGHEST PRIORITY PATIENT")

    if next_patient:

        print(
            f"Patient ID: {next_patient.patient_id}"
        )

        print(
            f"Priority Score: "
            f"{next_patient.priority_score}"
        )

        print(
            f"Priority Level: "
            f"{next_patient.priority_level}"
        )

    print("\nPROCESSING PATIENTS BY PRIORITY")

    position = 1

    while not queue.is_empty():

        patient = queue.pop_next_patient()

        print(
            f"{position}. "
            f"Patient {patient.patient_id} | "
            f"Score: {patient.priority_score} | "
            f"Level: {patient.priority_level}"
        )

        position += 1

    print("\n" + "=" * 70)
    print(
        "PATIENT PRIORITY ENGINE TEST COMPLETED"
    )
    print("=" * 70)


if __name__ == "__main__":
    main()