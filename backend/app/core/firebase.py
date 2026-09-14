""" import os

import firebase_admin
from firebase_admin import credentials


def initialize_firebase():
    if firebase_admin._apps:
        return

    service_account_path = os.path.join(
        os.path.dirname(os.path.dirname(os.path.dirname(__file__))),
        "firebase-service-account.json",
    )

    if not os.path.exists(service_account_path):
        raise RuntimeError(
            "Firebase service account file not found."
        )

    credential = credentials.Certificate(
        service_account_path
    )

    firebase_admin.initialize_app(
        credential
    ) """
    
    
import os
from pathlib import Path

import firebase_admin
from firebase_admin import credentials


def initialize_firebase():
    if firebase_admin._apps:
        return

    # --------------------------------------------------------
    # 1. CLOUD
    # --------------------------------------------------------
    # In production/cloud deployment, this environment
    # variable points to the securely mounted Firebase
    # service-account JSON file.
    cloud_credentials_path = os.getenv(
        "FIREBASE_CREDENTIALS_PATH"
    )

    if cloud_credentials_path:
        service_account_path = Path(
            cloud_credentials_path
        )

    # --------------------------------------------------------
    # 2. LOCAL DEVELOPMENT
    # --------------------------------------------------------
    else:
        backend_root = Path(
            __file__
        ).resolve().parents[2]

        service_account_path = (
            backend_root
            / "firebase-service-account.json"
        )

    # --------------------------------------------------------
    # VALIDATION
    # --------------------------------------------------------
    if not service_account_path.exists():
        raise RuntimeError(
            "Firebase service account file not found at: "
            f"{service_account_path}"
        )

    credential = credentials.Certificate(
        str(service_account_path)
    )

    firebase_admin.initialize_app(
        credential
    )