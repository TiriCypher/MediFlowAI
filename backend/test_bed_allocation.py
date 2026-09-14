from app.services.optimization.bed_allocation import (
    BedAllocationEngine,
    BedCandidate,
)


print("=" * 70)
print("MEDIFLOW AI - BED ALLOCATION ENGINE TEST")
print("=" * 70)


engine = BedAllocationEngine()


beds = [

    BedCandidate(
        bed_id=1,
        department_id=1,
        bed_number="G-101",
        bed_type="General",
    ),

    BedCandidate(
        bed_id=2,
        department_id=1,
        bed_number="ICU-101",
        bed_type="ICU",
    ),

    BedCandidate(
        bed_id=3,
        department_id=2,
        bed_number="ICU-201",
        bed_type="ICU",
    ),

    BedCandidate(
        bed_id=4,
        department_id=2,
        bed_number="G-201",
        bed_type="General",
    ),

]


print("\nAVAILABLE BEDS")
print("-" * 70)

for bed in beds:

    print(
        f"Bed ID: {bed.bed_id} | "
        f"Number: {bed.bed_number} | "
        f"Type: {bed.bed_type} | "
        f"Department: {bed.department_id}"
    )


# ==============================================================
# TEST CASE 1
# ==============================================================

print("\n" + "=" * 70)
print("TEST CASE 1 - ICU WITH PREFERRED DEPARTMENT")
print("=" * 70)

result = engine.select_bed(
    beds=beds,
    required_bed_type="ICU",
    preferred_department_id=2,
)


if result:

    print("\nALLOCATED BED")
    print("-" * 70)

    print(f"Bed ID: {result.bed_id}")
    print(f"Bed Number: {result.bed_number}")
    print(f"Bed Type: {result.bed_type}")
    print(
        f"Department: "
        f"{result.department_id}"
    )

else:

    print("\nNo suitable bed found.")


# ==============================================================
# TEST CASE 2
# ==============================================================

print("\n" + "=" * 70)
print("TEST CASE 2 - ICU WITHOUT MATCHING DEPARTMENT")
print("=" * 70)

result = engine.select_bed(
    beds=beds,
    required_bed_type="ICU",
    preferred_department_id=5,
)


if result:

    print("\nALLOCATED BED")
    print("-" * 70)

    print(f"Bed ID: {result.bed_id}")
    print(f"Bed Number: {result.bed_number}")
    print(f"Bed Type: {result.bed_type}")
    print(
        f"Department: "
        f"{result.department_id}"
    )

else:

    print("\nNo suitable bed found.")


# ==============================================================
# TEST CASE 3
# ==============================================================

print("\n" + "=" * 70)
print("TEST CASE 3 - BED TYPE NOT AVAILABLE")
print("=" * 70)

result = engine.select_bed(
    beds=beds,
    required_bed_type="Ventilator",
    preferred_department_id=1,
)


if result:

    print("\nALLOCATED BED")
    print("-" * 70)

    print(f"Bed ID: {result.bed_id}")
    print(f"Bed Number: {result.bed_number}")

else:

    print(
        "\nNo suitable bed found "
        "for the requested type."
    )


print("\n" + "=" * 70)
print("BED ALLOCATION ENGINE TEST COMPLETED")
print("=" * 70)