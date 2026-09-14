from datetime import date, datetime
from typing import Optional

from sqlalchemy import (
    Date,
    DateTime,
    ForeignKey,
    Integer,
    String,
    Text,
    text,
)
from sqlalchemy.orm import Mapped, mapped_column

from app.database.base import Base


class Patient(Base):
    __tablename__ = "patients"

    id: Mapped[int] = mapped_column(
        Integer,
        primary_key=True,
    )

    user_id: Mapped[
        Optional[int]
    ] = mapped_column(
        ForeignKey(
            "users.id",
            ondelete="SET NULL",
        ),
        unique=True,
        nullable=True,
    )

    name: Mapped[str] = mapped_column(
        String(100),
        nullable=False,
    )

    date_of_birth: Mapped[
        Optional[date]
    ] = mapped_column(
        Date,
        nullable=True,
    )

    gender: Mapped[
        Optional[str]
    ] = mapped_column(
        String(20),
        nullable=True,
    )

    blood_group: Mapped[
        Optional[str]
    ] = mapped_column(
        String(10),
        nullable=True,
    )

    phone: Mapped[
        Optional[str]
    ] = mapped_column(
        String(20),
        nullable=True,
    )

    address: Mapped[
        Optional[str]
    ] = mapped_column(
        Text,
        nullable=True,
    )

    medical_information: Mapped[
        Optional[str]
    ] = mapped_column(
        Text,
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