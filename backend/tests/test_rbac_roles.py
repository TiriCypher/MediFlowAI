# --------------------------------------------------
# PATIENT RBAC
# --------------------------------------------------


def test_patient_can_view_departments(
    client,
    login_as,
    patient_user,
):
    login_as(patient_user)

    response = client.get(
        "/departments"
    )

    assert response.status_code == 200


def test_patient_cannot_view_beds(
    client,
    login_as,
    patient_user,
):
    login_as(patient_user)

    response = client.get(
        "/beds"
    )

    assert response.status_code == 403


def test_patient_cannot_create_bed(
    client,
    login_as,
    patient_user,
):
    login_as(patient_user)

    response = client.post(
        "/beds",
        json={
            "bed_number": "TEST-RBAC-PATIENT",
            "bed_type": "GENERAL",
            "status": "AVAILABLE",
        },
    )

    assert response.status_code == 403


def test_patient_cannot_create_department(
    client,
    login_as,
    patient_user,
):
    login_as(patient_user)

    response = client.post(
        "/departments",
        json={
            "name": "RBAC Patient Department",
            "description": "Should fail",
        },
    )

    assert response.status_code == 403


def test_patient_cannot_create_alert(
    client,
    login_as,
    patient_user,
):
    login_as(patient_user)

    response = client.post(
        "/alerts",
        json={
            "type": "TEST",
            "message": "Should fail",
            "status": "UNREAD",
        },
    )

    assert response.status_code == 403


# --------------------------------------------------
# STAFF RBAC
# --------------------------------------------------


def test_staff_can_view_departments(
    client,
    login_as,
    staff_user,
):
    login_as(staff_user)

    response = client.get(
        "/departments"
    )

    assert response.status_code == 200


def test_staff_can_view_beds(
    client,
    login_as,
    staff_user,
):
    login_as(staff_user)

    response = client.get(
        "/beds"
    )

    assert response.status_code == 200


def test_staff_cannot_create_bed(
    client,
    login_as,
    staff_user,
):
    login_as(staff_user)

    response = client.post(
        "/beds",
        json={
            "bed_number": "TEST-RBAC-STAFF",
            "bed_type": "GENERAL",
            "status": "AVAILABLE",
        },
    )

    assert response.status_code == 403


def test_staff_cannot_create_department(
    client,
    login_as,
    staff_user,
):
    login_as(staff_user)

    response = client.post(
        "/departments",
        json={
            "name": "RBAC Staff Department",
            "description": "Should fail",
        },
    )

    assert response.status_code == 403


# --------------------------------------------------
# DOCTOR RBAC
# --------------------------------------------------


def test_doctor_can_view_departments(
    client,
    login_as,
    doctor_user,
):
    login_as(doctor_user)

    response = client.get(
        "/departments"
    )

    assert response.status_code == 200


def test_doctor_can_view_beds(
    client,
    login_as,
    doctor_user,
):
    login_as(doctor_user)

    response = client.get(
        "/beds"
    )

    assert response.status_code == 200


def test_doctor_cannot_create_bed(
    client,
    login_as,
    doctor_user,
):
    login_as(doctor_user)

    response = client.post(
        "/beds",
        json={
            "bed_number": "TEST-RBAC-DOCTOR",
            "bed_type": "GENERAL",
            "status": "AVAILABLE",
        },
    )

    assert response.status_code == 403


def test_doctor_cannot_create_department(
    client,
    login_as,
    doctor_user,
):
    login_as(doctor_user)

    response = client.post(
        "/departments",
        json={
            "name": "RBAC Doctor Department",
            "description": "Should fail",
        },
    )

    assert response.status_code == 403


def test_doctor_cannot_create_alert(
    client,
    login_as,
    doctor_user,
):
    login_as(doctor_user)

    response = client.post(
        "/alerts",
        json={
            "type": "TEST",
            "message": "Should fail",
            "status": "UNREAD",
        },
    )

    assert response.status_code == 403


# --------------------------------------------------
# ADMIN RBAC
# --------------------------------------------------


def test_admin_can_view_departments(
    client,
    login_as,
    admin_user,
):
    login_as(admin_user)

    response = client.get(
        "/departments"
    )

    assert response.status_code == 200


def test_admin_can_view_beds(
    client,
    login_as,
    admin_user,
):
    login_as(admin_user)

    response = client.get(
        "/beds"
    )

    assert response.status_code == 200


def test_admin_can_view_users(
    client,
    login_as,
    admin_user,
):
    login_as(admin_user)

    response = client.get(
        "/users"
    )

    assert response.status_code == 200


def test_admin_can_view_system_dashboard(
    client,
    login_as,
    admin_user,
):
    login_as(admin_user)

    response = client.get(
        "/optimization/system-dashboard"
    )

    assert response.status_code == 200

# --------------------------------------------------
# ADMIN-ONLY ENDPOINT PROTECTION
# --------------------------------------------------


def test_patient_cannot_view_users(
    client,
    login_as,
    patient_user,
):
    login_as(patient_user)

    response = client.get(
        "/users"
    )

    assert response.status_code == 403


def test_staff_cannot_view_users(
    client,
    login_as,
    staff_user,
):
    login_as(staff_user)

    response = client.get(
        "/users"
    )

    assert response.status_code == 403


def test_doctor_cannot_view_users(
    client,
    login_as,
    doctor_user,
):
    login_as(doctor_user)

    response = client.get(
        "/users"
    )

    assert response.status_code == 403


def test_patient_cannot_view_system_dashboard(
    client,
    login_as,
    patient_user,
):
    login_as(patient_user)

    response = client.get(
        "/optimization/system-dashboard"
    )

    assert response.status_code == 403


def test_staff_cannot_view_system_dashboard(
    client,
    login_as,
    staff_user,
):
    login_as(staff_user)

    response = client.get(
        "/optimization/system-dashboard"
    )

    assert response.status_code == 403


def test_doctor_cannot_view_system_dashboard(
    client,
    login_as,
    doctor_user,
):
    login_as(doctor_user)

    response = client.get(
        "/optimization/system-dashboard"
    )

    assert response.status_code == 403

def test_test_database_starts_empty(
    client,
    login_as,
    admin_user,
):
    login_as(admin_user)

    response = client.get(
        "/beds"
    )

    assert response.status_code == 200
    assert response.json() == []