from app.models import (
    Alert,
    Appointment,
    Bed,
    Department,
    Doctor,
    MLPrediction,
    Patient,
    User,
)


def test_models_import():
    assert Alert is not None
    assert Appointment is not None
    assert Bed is not None
    assert Department is not None
    assert Doctor is not None
    assert MLPrediction is not None
    assert Patient is not None
    assert User is not None