package com.tarento.upsmf.userManagement.exception;

import com.tarento.upsmf.userManagement.utility.ErrorCode;

public class UserCreationException extends CustomException {

    public UserCreationException(String message) {
        super(message);
    }

    public UserCreationException(String message, String description) {
        super(message, description);
    }

    public UserCreationException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }


    public UserCreationException(String message, ErrorCode errorCode, String description) {
        super(message, errorCode, description);
    }
}
