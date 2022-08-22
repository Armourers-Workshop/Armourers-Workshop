package com.apple.library.impl;

public enum InvokerResult {
    SUCCESS,
    PASS,
    FAIL;


    public static InvokerResult of(boolean value) {
        if (value) {
            return SUCCESS;
        }
        return FAIL;
    }

    public boolean isDecided() {
        return this == SUCCESS || this == FAIL;
    }

    public boolean conclusion() {
        return this == SUCCESS;
    }
}
