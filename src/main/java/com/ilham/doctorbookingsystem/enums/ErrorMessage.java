package com.ilham.doctorbookingsystem.enums;

import lombok.Getter;

@Getter
public enum ErrorMessage {

    USER_NOT_FOUND("User not found"),
    DOCTOR_NOT_FOUND("Doctor not found"),
    PATIENT_NOT_FOUND("Patient not found"),
    EMAIL_ALREADY_EXISTS("Email already exists"),
    USER_NOT_VERIFIED("User not verified. Please verify OTP first."),
    REFRESH_TOKEN_NOT_FOUND("Refresh token not found"),
    REFRESH_TOKEN_EXPIRED("Refresh token expired"),
    USER_NOT_ACTIVE("User is not active"),
    INVALID_HEADER("Invalid authorization header"),
    APPOINTMENT_NOT_FOUND("Appointment not found"),
    INVALID_REQUEST("Invalid request"),
    SLOT_ALREADY_BOOKED("This slot is already booked"),
    SLOT_ALREADY_CANCELED("This slot is already canceled"),
    DOCTOR_NOT_APPROVED("Doctor not approved"),
    APPOINTMENT_DATE_EXCEPTION("Date must be within next 30 days"),
    CANNOT_CANCEL_APPOINTMENT("You cannot cancel this appointment"),
    CANNOT_COMPLETE_APPOINTMENT("You cannot complete this appointment"),
    DOCTOR_ALREADY_APPROVED("Doctor is already approved"),
    DOCTOR_ALREADY_REJECTED("Doctor is already rejected"),
    YOU_CANNOT_CONFIRM_APPOINTMENT("You cannot confirm this appointment"),
    PENDING_OR_CONFIRM_APPOINTMENT_CAN_BE_CANCELED("Only pending or confirmed appointments can be cancelled"),
    YOU_CAN_COMPLETE_ONLY_CONFIRMED_APPOINTMENTS("You can complete only confirmed appointments"),
    YOU_CANNOT_REVIEW_THIS_APPOINTMENT("You cannot review this appointment"),
    ONLY_COMPLETED_APPOINTMENTS_CAN_BE_REVIEW("Only completed appointments can be reviewed"),
    REVIEW_ALREADY_EXISTS("Review already exists for this appointment");

    private final String message;

    ErrorMessage(String message) {
        this.message = message;
    }

}
