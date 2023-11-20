package com.tarento.upsmf.userManagement.exception;

import com.tarento.upsmf.userManagement.utility.ErrorCode;

public class LoginFailedException extends CustomException {

    public LoginFailedException(String message) {
        super(message);
    }

    public LoginFailedException(String message, String description) {
        super(message, description);
    }

    public LoginFailedException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }


    public LoginFailedException(String message, ErrorCode errorCode, String description) {
        super(message, errorCode, description);
    }
}
