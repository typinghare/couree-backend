package com.couree.luckycat.glacier.exception;

public class UninitializedRegistryEntryException extends RuntimeException {
    public UninitializedRegistryEntryException(String key) {
        super(String.format(
            "Registry key [ %s ] haven't initialized.", key
        ));
    }
}
