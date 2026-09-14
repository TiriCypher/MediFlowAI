from dataclasses import dataclass

from app.services.optimization.patient_priority import (
    PatientPriorityResult,
)


@dataclass
class HeapNode:

    priority_score: int
    patient: PatientPriorityResult


class PatientPriorityQueue:

    def __init__(self):

        self.heap: list[HeapNode] = []

    def is_empty(self) -> bool:

        return len(self.heap) == 0

    def push(
        self,
        patient: PatientPriorityResult,
    ) -> None:

        node = HeapNode(
            priority_score=patient.priority_score,
            patient=patient,
        )

        self.heap.append(node)

        self._heapify_up(
            len(self.heap) - 1
        )

    def pop(self) -> PatientPriorityResult:

        if self.is_empty():

            raise IndexError(
                "Priority queue is empty."
            )

        highest_priority = self.heap[0]

        last_node = self.heap.pop()

        if not self.is_empty():

            self.heap[0] = last_node

            self._heapify_down(0)

        return highest_priority.patient

    def _heapify_up(
        self,
        index: int,
    ) -> None:

        while index > 0:

            parent_index = (
                index - 1
            ) // 2

            if (
                self.heap[index].priority_score
                <= self.heap[parent_index].priority_score
            ):
                break

            self.heap[index], self.heap[parent_index] = (
                self.heap[parent_index],
                self.heap[index],
            )

            index = parent_index

    def _heapify_down(
        self,
        index: int,
    ) -> None:

        heap_size = len(self.heap)

        while True:

            largest = index

            left_child = (
                2 * index + 1
            )

            right_child = (
                2 * index + 2
            )

            if (
                left_child < heap_size
                and self.heap[
                    left_child
                ].priority_score
                > self.heap[
                    largest
                ].priority_score
            ):

                largest = left_child

            if (
                right_child < heap_size
                and self.heap[
                    right_child
                ].priority_score
                > self.heap[
                    largest
                ].priority_score
            ):

                largest = right_child

            if largest == index:

                break

            self.heap[index], self.heap[largest] = (
                self.heap[largest],
                self.heap[index],
            )

            index = largest