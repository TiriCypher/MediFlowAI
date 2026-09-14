import pytest

from fastapi.testclient import TestClient

from sqlalchemy import create_engine
from sqlalchemy.orm import (
    Session,
    sessionmaker,
)
from sqlalchemy.pool import StaticPool

from app.main import app

from app.database.base import Base
from app.dependencies.database import get_db

from app.dependencies.authorization import (
    get_current_user,
)

from app.models.user import User

# --------------------------------------------------
# IMPORTANT:
# Import every model before Base.metadata.create_all()
# so SQLAlchemy knows about every table.
# --------------------------------------------------

from app.models.department import Department
from app.models.doctor import Doctor
from app.models.patient import Patient
from app.models.appointment import Appointment
from app.models.bed import Bed
from app.models.ml_prediction import MLPrediction
from app.models.alert import Alert


# --------------------------------------------------
# ISOLATED TEST DATABASE
# --------------------------------------------------

TEST_DATABASE_URL = "sqlite://"


test_engine = create_engine(
    TEST_DATABASE_URL,
    connect_args={
        "check_same_thread": False,
    },
    poolclass=StaticPool,
)


TestingSessionLocal = sessionmaker(
    bind=test_engine,
    autocommit=False,
    autoflush=False,
)


# --------------------------------------------------
# CREATE TEST TABLES
# --------------------------------------------------

@pytest.fixture(
    scope="session",
    autouse=True,
)
def create_test_database():

    Base.metadata.create_all(
        bind=test_engine
    )

    yield

    Base.metadata.drop_all(
        bind=test_engine
    )


# --------------------------------------------------
# OVERRIDE DATABASE DEPENDENCY
# --------------------------------------------------

@pytest.fixture(
    autouse=True,
)
def override_database():

    def _get_test_db():

        db = TestingSessionLocal()

        try:
            yield db

        finally:
            db.close()

    app.dependency_overrides[
        get_db
    ] = _get_test_db

    yield

    app.dependency_overrides.pop(
        get_db,
        None,
    )


# --------------------------------------------------
# CLEAN DATABASE BETWEEN TESTS
# --------------------------------------------------

@pytest.fixture(
    autouse=True,
)
def clean_test_database(
    create_test_database,
):

    yield

    with test_engine.begin() as connection:

        for table in reversed(
            Base.metadata.sorted_tables
        ):

            connection.execute(
                table.delete()
            )


# --------------------------------------------------
# FASTAPI CLIENT
# --------------------------------------------------

@pytest.fixture
def client():

    with TestClient(app) as test_client:
        yield test_client


# --------------------------------------------------
# FAKE AUTHENTICATED USERS
# --------------------------------------------------

def make_user(
    user_id: int,
    role: str,
    email: str,
) -> User:

    return User(
        id=user_id,
        firebase_uid=(
            f"test-{role.lower()}-uid"
        ),
        name=(
            f"Test {role.title()}"
        ),
        email=email,
        role=role,
    )


@pytest.fixture
def admin_user():

    return make_user(
        user_id=9001,
        role="ADMIN",
        email="admin.test@example.com",
    )


@pytest.fixture
def doctor_user():

    return make_user(
        user_id=9002,
        role="DOCTOR",
        email="doctor.test@example.com",
    )


@pytest.fixture
def staff_user():

    return make_user(
        user_id=9003,
        role="STAFF",
        email="staff.test@example.com",
    )


@pytest.fixture
def patient_user():

    return make_user(
        user_id=9004,
        role="PATIENT",
        email="patient.test@example.com",
    )


# --------------------------------------------------
# AUTH OVERRIDE
# --------------------------------------------------

@pytest.fixture
def login_as():

    def _login(
        user: User,
    ):

        app.dependency_overrides[
            get_current_user
        ] = lambda: user

    yield _login

    app.dependency_overrides.pop(
        get_current_user,
        None,
    )


# --------------------------------------------------
# DIRECT TEST DB ACCESS
#
# We will use this in upcoming ownership tests.
# --------------------------------------------------

@pytest.fixture
def db_session():

    db: Session = TestingSessionLocal()

    try:
        yield db

    finally:
        db.close()