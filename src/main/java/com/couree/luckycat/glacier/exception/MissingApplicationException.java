package com.couree.luckycat.glacier.exception;

public class MissingApplicationException extends RuntimeException {
    public MissingApplicationException(String applicationName) {
        super(String.format("Missing application: [ %s ].", applicationName));
    }
}
