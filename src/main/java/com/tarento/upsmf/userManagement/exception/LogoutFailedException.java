package com.tarento.upsmf.userManagement.exception;

import com.tarento.upsmf.userManagement.utility.ErrorCode;

public class LogoutFailedException extends CustomException {
    public LogoutFailedException(String message) {
        super(message);
    }

    public LogoutFailedException(String message, String description) {
        super(message, description);
    }

    public LogoutFailedException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }

    public LogoutFailedException(String message, ErrorCode errorCode, String description) {
        super(message, errorCode, description);
    }
}
