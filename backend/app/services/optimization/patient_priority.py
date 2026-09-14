import heapq
from dataclasses import dataclass
from typing import Optional


@dataclass
class PatientPriorityInput:
    patient_id: int
    urgency: str
    risk_level: str
    waiting_time_minutes: int
    comorbidity_count: int
    age: int


@dataclass
class PatientPriorityResult:
    patient_id: int
    priority_score: int
    priority_level: str


class PatientPriorityEngine:

    URGENCY_SCORES = {
        "Emergency": 40,
        "Urgent": 30,
        "Routine": 15,
        "Elective": 5,
    }

    RISK_SCORES = {
        "HIGH": 25,
        "MEDIUM": 15,
        "LOW": 5,
    }

    def calculate_priority(
        self,
        patient: PatientPriorityInput,
    ) -> PatientPriorityResult:

        urgency_score = self.URGENCY_SCORES.get(
            patient.urgency,
            0,
        )

        risk_score = self.RISK_SCORES.get(
            patient.risk_level.upper(),
            0,
        )

        waiting_score = min(
            patient.waiting_time_minutes // 10,
            15,
        )

        comorbidity_score = min(
            patient.comorbidity_count * 3,
            12,
        )

        age_score = 0

        if patient.age >= 75:
            age_score = 8

        elif patient.age >= 60:
            age_score = 5

        priority_score = (
            urgency_score
            + risk_score
            + waiting_score
            + comorbidity_score
            + age_score
        )

        priority_level = self.get_priority_level(
            priority_score
        )

        return PatientPriorityResult(
            patient_id=patient.patient_id,
            priority_score=priority_score,
            priority_level=priority_level,
        )

    def get_priority_level(
        self,
        priority_score: int,
    ) -> str:

        if priority_score >= 70:
            return "CRITICAL"

        if priority_score >= 45:
            return "HIGH"

        if priority_score >= 25:
            return "MEDIUM"

        return "LOW"
    
class PatientPriorityQueue:

    def __init__(self):
        self._heap = []
        self._counter = 0

    def add_patient(
        self,
        patient: PatientPriorityResult,
    ) -> None:

        heapq.heappush(
            self._heap,
            (
                -patient.priority_score,
                self._counter,
                patient,
            ),
        )

        self._counter += 1

    def get_next_patient(
        self,
    ) -> Optional[PatientPriorityResult]:

        if not self._heap:
            return None

        return self._heap[0][2]

    def pop_next_patient(
        self,
    ) -> Optional[PatientPriorityResult]:

        if not self._heap:
            return None

        return heapq.heappop(
            self._heap
        )[2]

    def get_queue_size(
        self,
    ) -> int:

        return len(self._heap)

    def is_empty(
        self,
    ) -> bool:

        return len(self._heap) == 0