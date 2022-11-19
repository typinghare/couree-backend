package com.couree.luckycat.glacier.exception;

public class NoMetadataDefinedException extends RuntimeException {
    public NoMetadataDefinedException(String key, Class<?> sourceClass) {
        super(String.format(
            "Registry Key [ %s ] does not have metadata definition. Source class: [ %s ]",
            key, sourceClass.getName()
        ));
    }
}
