from dataclasses import dataclass


@dataclass
class DoctorWorkload:

    doctor_id: int
    specialization: str
    current_appointments: int
    is_available: bool


@dataclass
class DoctorAssignmentResult:

    doctor_id: int
    specialization: str
    current_appointments: int


class DoctorLoadBalancingEngine:

    def select_doctor(
        self,
        doctors: list[DoctorWorkload],
        required_specialization: str,
    ) -> DoctorAssignmentResult | None:

        eligible_doctors = []

        for doctor in doctors:

            if (
                doctor.specialization.lower()
                == required_specialization.lower()
                and doctor.is_available
            ):
                eligible_doctors.append(doctor)

        if not eligible_doctors:
            return None

        selected_doctor = eligible_doctors[0]

        for doctor in eligible_doctors[1:]:

            if (
                doctor.current_appointments
                < selected_doctor.current_appointments
            ):
                selected_doctor = doctor

        return DoctorAssignmentResult(
            doctor_id=selected_doctor.doctor_id,
            specialization=selected_doctor.specialization,
            current_appointments=(
                selected_doctor.current_appointments
            ),
        )