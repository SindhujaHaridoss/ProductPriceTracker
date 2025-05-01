package com.nhsbsa.productpricealerttracker.exception;

public class InvalidDataException extends RuntimeException {

    public final String errorCode;

    public InvalidDataException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
}
