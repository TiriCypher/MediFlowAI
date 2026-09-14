from fastapi import Depends, FastAPI
from sqlalchemy import text
from sqlalchemy.orm import Session

from app.api.departments import router as departments_router
from app.dependencies.database import get_db
from app.api.patients import router as patients_router
from app.api.doctors import router as doctors_router
from app.api.users import router as users_router
from app.api.appointments import router as appointments_router
from app.api.beds import router as beds_router
from app.api.alerts import router as alerts_router
from app.api.predictions import router as predictions_router
from app.api.patient_priority import (
    router as patient_priority_router,
)

from app.api.priority_queue import (
    router as priority_queue_router,
)
from app.api.optimization import router as optimization_router
from app.core.firebase import initialize_firebase
from app.api.analytics import (
    router as analytics_router,
)

initialize_firebase()

app = FastAPI(
    title="MediFlow AI API",
    description="AI-Based Smart Hospital Resource Management and Patient Risk Prediction System",
    version="1.0.0",
)


app.include_router(departments_router)
app.include_router(patients_router)
app.include_router(doctors_router)
app.include_router(users_router)
app.include_router(appointments_router)
app.include_router(beds_router)
app.include_router(alerts_router)
app.include_router(predictions_router)
app.include_router(patient_priority_router)
app.include_router(priority_queue_router)
app.include_router(optimization_router)
app.include_router(
    analytics_router
)


@app.get("/")
def root():
    return {
        "message": "MediFlow AI API is running",
        "status": "success",
    }


@app.get("/health")
def health_check():
    return {
        "status": "healthy",
        "service": "MediFlow AI Backend",
    }


@app.get("/health/database")
def database_health(
    db: Session = Depends(get_db),
):
    result = db.execute(text("SELECT 1"))
    value = result.scalar()

    return {
        "database": "connected",
        "test_result": value,
    }