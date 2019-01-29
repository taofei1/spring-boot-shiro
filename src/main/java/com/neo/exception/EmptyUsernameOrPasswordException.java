package com.neo.exception;

public class EmptyUsernameOrPasswordException extends RuntimeException {
    public EmptyUsernameOrPasswordException() {
        super();
    }

    public EmptyUsernameOrPasswordException(String msg) {
        super(msg);
    }
}
