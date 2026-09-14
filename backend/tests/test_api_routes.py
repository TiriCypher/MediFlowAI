
from app.main import app



def test_root(client):
    response = client.get("/")

    assert response.status_code == 200
    assert response.json()["status"] == "success"


def test_health(client):
    response = client.get("/health")

    assert response.status_code == 200
    assert response.json()["status"] == "healthy"


def test_database_health(client):
    response = client.get("/health/database")

    assert response.status_code == 200
    assert response.json()["database"] == "connected"


def test_departments_endpoint_requires_auth(client):
    response = client.get("/departments")

    assert response.status_code == 401


def test_users_endpoint_requires_auth(client):
    response = client.get("/users")

    assert response.status_code == 401


def test_patients_endpoint_requires_auth(client):
    response = client.get("/patients")

    assert response.status_code == 401


def test_doctors_endpoint_requires_auth(client):
    response = client.get("/doctors")

    assert response.status_code == 401


def test_appointments_endpoint_requires_auth(client):
    response = client.get("/appointments")

    assert response.status_code == 401


def test_beds_endpoint_requires_auth(client):
    response = client.get("/beds")

    assert response.status_code == 401


def test_alerts_endpoint_requires_auth(client):
    response = client.get("/alerts")

    assert response.status_code == 401