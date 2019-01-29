package com.neo.exception;

public class UserPasswordRetryLimitExceedException extends RuntimeException {
    private int retryTimesLimit;

    public UserPasswordRetryLimitExceedException(int i) {
        retryTimesLimit = i;
    }
}
