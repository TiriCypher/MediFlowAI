import os

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
    )