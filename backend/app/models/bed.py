from datetime import datetime
from typing import Optional

from sqlalchemy import (
    DateTime,
    ForeignKey,
    Integer,
    String,
    text,
)
from sqlalchemy.orm import Mapped, mapped_column

from app.database.base import Base


class Bed(Base):
    __tablename__ = "beds"

    id: Mapped[int] = mapped_column(
        Integer,
        primary_key=True,
    )

    department_id: Mapped[
        Optional[int]
    ] = mapped_column(
        ForeignKey(
            "departments.id",
            ondelete="SET NULL",
        ),
        nullable=True,
    )

    bed_number: Mapped[str] = mapped_column(
        String(30),
        unique=True,
        nullable=False,
    )

    bed_type: Mapped[
        Optional[str]
    ] = mapped_column(
        String(50),
        nullable=True,
    )

    status: Mapped[str] = mapped_column(
        String(30),
        nullable=False,
        default="AVAILABLE",
    )

    patient_id: Mapped[
        Optional[int]
    ] = mapped_column(
        ForeignKey(
            "patients.id",
            ondelete="SET NULL",
        ),
        nullable=True,
    )

    created_at: Mapped[
        datetime
    ] = mapped_column(
        DateTime,
        nullable=False,
        server_default=text(
            "CURRENT_TIMESTAMP"
        ),
    )