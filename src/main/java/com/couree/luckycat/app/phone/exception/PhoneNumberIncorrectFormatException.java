package com.couree.luckycat.app.phone.exception;

public class PhoneNumberIncorrectFormatException extends RuntimeException {
    public PhoneNumberIncorrectFormatException(String phoneNumberString) {
        super(String.format("Incorrect phone number format: [ %s ].", phoneNumberString));
    }
}
