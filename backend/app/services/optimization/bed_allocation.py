from dataclasses import dataclass


@dataclass
class BedCandidate:

    bed_id: int
    department_id: int | None
    bed_number: str
    bed_type: str | None


@dataclass
class BedAllocationResult:

    bed_id: int
    department_id: int | None
    bed_number: str
    bed_type: str | None


class BedAllocationEngine:

    def select_bed(
        self,
        beds: list[BedCandidate],
        required_bed_type: str,
        preferred_department_id: int | None,
    ) -> BedAllocationResult | None:

        if not beds:
            return None

        matching_type_beds = []

        for bed in beds:

            if (
                bed.bed_type is not None
                and bed.bed_type.lower()
                == required_bed_type.lower()
            ):
                matching_type_beds.append(bed)

        if not matching_type_beds:
            return None

        if preferred_department_id is not None:

            for bed in matching_type_beds:

                if (
                    bed.department_id
                    == preferred_department_id
                ):
                    return BedAllocationResult(
                        bed_id=bed.bed_id,
                        department_id=bed.department_id,
                        bed_number=bed.bed_number,
                        bed_type=bed.bed_type,
                    )

        selected_bed = matching_type_beds[0]

        return BedAllocationResult(
            bed_id=selected_bed.bed_id,
            department_id=selected_bed.department_id,
            bed_number=selected_bed.bed_number,
            bed_type=selected_bed.bed_type,
        )