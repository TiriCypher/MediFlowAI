from fastapi import (
    APIRouter,
    Depends,
    HTTPException,
    status,
)
from sqlalchemy import select
from sqlalchemy.orm import Session

from app.dependencies.database import get_db
from app.schemas.doctor_load_balancing import (
    DoctorLoadBalancingRequest,
    DoctorLoadBalancingResponse,
)
from app.services.optimization.doctor_load_balancing_service import (
    get_least_loaded_doctor,
)

from app.schemas.appointment import (
    OptimizedAppointmentRequest,
    OptimizedAppointmentResponse,
)
from app.schemas.staff_dashboard import (
    StaffDashboardResponse,
)

from app.dependencies.authorization import require_roles
from app.models.user import User
from app.models.appointment import Appointment
from app.models.doctor import Doctor

from app.services.optimization.intelligent_appointment_service import (
    schedule_optimized_appointment,
)

from app.models.patient import Patient
from app.schemas.bed_allocation import (
    BedAllocationRequest,
    BedAllocationResponse,
    BedReleaseResponse,
)

from app.schemas.system_dashboard import (
    SystemDashboardResponse,
)

from app.services.optimization.system_dashboard_service import (
    get_system_dashboard,
)

from app.schemas.latest_patient_risk_summary import (
    LatestPatientRiskSummaryResponse,
)

from app.services.optimization.latest_patient_risk_summary_service import (
    get_latest_patient_risk_summary,
)

from app.schemas.patient_risk_analytics import (
    PatientRiskAnalyticsResponse,
)

from app.services.optimization.patient_risk_analytics_service import (
    get_patient_risk_analytics,
)
from app.services.optimization.bed_allocation_database_service import (
    allocate_bed_to_patient,
)
from app.services.optimization.bed_release_service import (
    release_bed,
)
from app.schemas.bed_dashboard import (
    BedDashboardResponse,
)

from app.services.optimization.bed_dashboard_service import (
    get_bed_dashboard,
)
from app.schemas.department_bed_dashboard import (
    DepartmentBedDashboardResponse,
)

from app.services.optimization.department_bed_dashboard_service import (
    get_department_bed_dashboard,
)
from app.schemas.bed_type_dashboard import (
    BedTypeDashboardResponse,
)

from app.services.optimization.bed_type_dashboard_service import (
    get_bed_type_dashboard,
)
from app.schemas.resource_summary import (
    ResourceSummaryResponse,
)

from app.services.optimization.resource_summary_service import (
    get_resource_summary,
)
from app.schemas.appointment_analytics import (
    AppointmentAnalyticsResponse,
)

from app.services.optimization.appointment_analytics_service import (
    get_appointment_analytics,
)
from app.schemas.doctor_workload_summary import (
    DoctorWorkloadSummaryResponse,
)

from app.services.optimization.doctor_workload_summary_service import (
    get_doctor_workload_summary,
)
from app.schemas.optimization_dashboard import (
    OptimizationDashboardResponse,
)

from app.services.optimization.optimization_dashboard_service import (
    get_optimization_dashboard,
)

from app.schemas.patient_risk_priority_dashboard import (
    PatientRiskPriorityDashboardRequest,
    PatientRiskPriorityDashboardResponse,
)

from app.services.optimization.patient_risk_priority_dashboard_service import (
    get_patient_risk_priority_dashboard,
)


router = APIRouter(
    prefix="/optimization",
    tags=["Optimization"],
)

def doctor_has_patient_access(
    db: Session,
    current_user: User,
    patient_id: int,
) -> bool:

    doctor = db.scalar(
        select(Doctor).where(
            Doctor.user_id
            == current_user.id
        )
    )

    if not doctor:
        return False

    appointment = db.scalar(
        select(Appointment).where(
            Appointment.doctor_id
            == doctor.id,
            Appointment.patient_id
            == patient_id,
        )
    )

    return appointment is not None


@router.post(
    "/doctor-load-balancing",
    response_model=DoctorLoadBalancingResponse,
)
def balance_doctor_load(
    request: DoctorLoadBalancingRequest,
    db: Session = Depends(get_db),
    current_user: User = Depends(
        require_roles("ADMIN", "STAFF")
    ),
):
    result = get_least_loaded_doctor(
        db=db,
        required_specialization=request.specialization,
    )

    if result is None:

        raise HTTPException(
            status_code=404,
            detail=(
                "No available doctor found "
                "for the requested specialization."
            ),
        )

    return result


@router.post(
    "/schedule-appointment",
    response_model=OptimizedAppointmentResponse,
    status_code=201,
)
def schedule_appointment(
    request: OptimizedAppointmentRequest,
    db: Session = Depends(get_db),
    current_user: User = Depends(
        require_roles("ADMIN", "STAFF")
    ),
):
    result = schedule_optimized_appointment(
        db=db,
        patient_id=request.patient_id,
        specialization=request.specialization,
        appointment_time=request.appointment_time,
        department_id=request.department_id,
        priority=request.priority,
        reason=request.reason,
    )

    appointment = result["appointment"]

    return {
        "appointment_id": appointment.id,
        "patient_id": appointment.patient_id,
        "doctor_id": appointment.doctor_id,
        "doctor_specialization": (
            result["doctor_specialization"]
        ),
        "department_id": appointment.department_id,
        "appointment_time": appointment.appointment_time,
        "priority": appointment.priority,
        "status": appointment.status,
        "reason": appointment.reason,
        "doctor_active_appointments": (
            result["doctor_active_appointments"]
        ),
    }


@router.post(
    "/allocate-bed",
    response_model=BedAllocationResponse,
    status_code=201,
)
def allocate_bed(
    allocation_data: BedAllocationRequest,
    db: Session = Depends(get_db),
    current_user: User = Depends(
        require_roles("ADMIN", "STAFF")
    ),
):
    patient = db.get(
        Patient,
        allocation_data.patient_id,
    )

    if not patient:
        raise HTTPException(
            status_code=404,
            detail="Patient not found.",
        )

    allocated_bed = allocate_bed_to_patient(
        db=db,
        patient_id=allocation_data.patient_id,
        required_bed_type=(
            allocation_data.required_bed_type
        ),
        preferred_department_id=(
            allocation_data.preferred_department_id
        ),
    )

    if not allocated_bed:
        raise HTTPException(
            status_code=404,
            detail=(
                "No available bed found "
                "for the requested type."
            ),
        )

    return BedAllocationResponse(
        bed_id=allocated_bed.id,
        patient_id=allocation_data.patient_id,
        bed_number=allocated_bed.bed_number,
        bed_type=allocated_bed.bed_type,
        department_id=(
            allocated_bed.department_id
        ),
        status=allocated_bed.status,
    )

@router.post(
    "/release-bed/{bed_id}",
    response_model=BedReleaseResponse,
)
def release_allocated_bed(
    bed_id: int,
    db: Session = Depends(get_db),
    current_user: User = Depends(
        require_roles("ADMIN", "STAFF")
    ),
):
    result = release_bed(
        db=db,
        bed_id=bed_id,
    )

    if result is None:
        raise HTTPException(
            status_code=404,
            detail="Bed not found.",
        )

    if result is False:
        raise HTTPException(
            status_code=409,
            detail="Bed is not currently occupied.",
        )

    return BedReleaseResponse(
        bed_id=result.id,
        bed_number=result.bed_number,
        status=result.status,
        patient_id=result.patient_id,
    )

@router.get(
    "/bed-dashboard",
    response_model=BedDashboardResponse,
)
def get_bed_resource_dashboard(
    db: Session = Depends(get_db),
    current_user: User = Depends(
        require_roles("ADMIN", "STAFF")
    ),
):
    return get_bed_dashboard(
        db=db,
    )

@router.get(
    "/department-bed-dashboard",
    response_model=DepartmentBedDashboardResponse,
)
def get_department_bed_resource_dashboard(
    db: Session = Depends(get_db),
    current_user: User = Depends(
        require_roles("ADMIN", "STAFF")
    ),
):

    return get_department_bed_dashboard(
        db=db,
    )

@router.get(
    "/bed-type-dashboard",
    response_model=BedTypeDashboardResponse,
)
def get_bed_type_resource_dashboard(
    db: Session = Depends(get_db),
    current_user: User = Depends(
        require_roles("ADMIN", "STAFF")
    ),
):

    return get_bed_type_dashboard(
        db=db,
    )

@router.get(
    "/resource-summary",
    response_model=ResourceSummaryResponse,
)
def get_hospital_resource_summary(
    db: Session = Depends(get_db),
    current_user: User = Depends(
        require_roles("ADMIN", "STAFF")
    ),
):
    return get_resource_summary(
        db=db,
    )

@router.get(
    "/appointment-analytics",
    response_model=AppointmentAnalyticsResponse,
)
def get_appointment_resource_analytics(
    db: Session = Depends(get_db),
    current_user: User = Depends(
        require_roles("ADMIN")
    ),
):
    return get_appointment_analytics(
        db=db,
    )

@router.get(
    "/doctor-workload-summary",
    response_model=DoctorWorkloadSummaryResponse,
)
def get_doctor_workload_resource_summary(
    db: Session = Depends(get_db),
    current_user: User = Depends(
        require_roles("ADMIN")
    ),
):
    return get_doctor_workload_summary(
        db=db,
    )

@router.get(
    "/dashboard",
    response_model=OptimizationDashboardResponse,
)
def get_optimization_dashboard_data(
    db: Session = Depends(get_db),
    current_user: User = Depends(
        require_roles("ADMIN")
    ),
):
    return get_optimization_dashboard(
        db=db,
    )

@router.post(
    "/patient-risk-priority-dashboard",
    response_model=(
        PatientRiskPriorityDashboardResponse
    ),
)
def get_patient_risk_priority_dashboard_data(
    request: PatientRiskPriorityDashboardRequest,
    db: Session = Depends(get_db),
    current_user: User = Depends(
        require_roles(
            "ADMIN",
            "DOCTOR",
        )
    ),
):
    patient = db.get(
        Patient,
        request.patient_id,
    )

    if not patient:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Patient not found.",
        )

    role = current_user.role.upper()

    # Doctor may calculate priority only
    # for patients assigned to them.
    if role == "DOCTOR":

        if not doctor_has_patient_access(
            db=db,
            current_user=current_user,
            patient_id=request.patient_id,
        ):
            raise HTTPException(
                status_code=(
                    status.HTTP_403_FORBIDDEN
                ),
                detail=(
                    "You can only access risk "
                    "priority information for "
                    "patients assigned to you."
                ),
            )

    result = (
        get_patient_risk_priority_dashboard(
            db=db,
            patient_id=request.patient_id,
            urgency=request.urgency,
            waiting_time_minutes=(
                request.waiting_time_minutes
            ),
            comorbidity_count=(
                request.comorbidity_count
            ),
            age=request.age,
        )
    )

    if result is None:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail=(
                "No readmission prediction found "
                "for this patient."
            ),
        )

    return result

@router.get(
    "/patient-risk-analytics",
    response_model=PatientRiskAnalyticsResponse,
)
def get_patient_risk_analytics_data(
    db: Session = Depends(get_db),
    current_user: User = Depends(
        require_roles("ADMIN")
    ),
):
    return get_patient_risk_analytics(
        db=db,
    )

@router.get(
    "/latest-patient-risk-summary",
    response_model=LatestPatientRiskSummaryResponse,
)
def get_latest_patient_risk_summary_data(
    db: Session = Depends(get_db),
    current_user: User = Depends(
        require_roles("ADMIN")
    ),
):
    return get_latest_patient_risk_summary(
        db=db,
    )

@router.get(
    "/system-dashboard",
    response_model=SystemDashboardResponse,
)
def get_system_dashboard_data(
    db: Session = Depends(get_db),
    current_user: User = Depends(
        require_roles("ADMIN")
    ),
):
    return get_system_dashboard(
        db=db,
    )

@router.get(
    "/staff-dashboard",
    response_model=StaffDashboardResponse,
)
def get_staff_dashboard_data(
    db: Session = Depends(get_db),
    current_user: User = Depends(
        require_roles(
            "ADMIN",
            "STAFF",
        )
    ),
):
    return StaffDashboardResponse(
        resources=get_resource_summary(
            db=db,
        ),
        appointments=get_appointment_analytics(
            db=db,
        ),
    )
