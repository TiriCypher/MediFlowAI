from app.schemas.alert import AlertCreate, AlertResponse
from app.schemas.appointment import AppointmentCreate, AppointmentResponse
from app.schemas.bed import BedCreate, BedResponse
from app.schemas.department import DepartmentCreate, DepartmentResponse
from app.schemas.doctor import DoctorCreate, DoctorResponse
from app.schemas.ml_prediction import MLPredictionResponse
from app.schemas.patient import PatientCreate, PatientResponse
from app.schemas.user import (
    UserRoleUpdateRequest,
    UserSyncRequest,
    UserResponse,
)


__all__ = [
    "AlertCreate",
    "AlertResponse",
    "AppointmentCreate",
    "AppointmentResponse",
    "BedCreate",
    "BedResponse",
    "DepartmentCreate",
    "DepartmentResponse",
    "DoctorCreate",
    "DoctorResponse",
    "MLPredictionResponse",
    "PatientCreate",
    "PatientResponse",
    "UserRoleUpdateRequest",
    "UserSyncRequest",
    "UserResponse",
]