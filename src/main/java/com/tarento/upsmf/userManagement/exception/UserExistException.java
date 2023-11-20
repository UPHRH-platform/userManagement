package com.tarento.upsmf.userManagement.exception;

import com.tarento.upsmf.userManagement.utility.ErrorCode;

public class UserExistException extends CustomException {

    public UserExistException(String message) {
        super(message);
    }

    public UserExistException(String message, String description) {
        super(message, description);
    }

    public UserExistException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }


    public UserExistException(String message, ErrorCode errorCode, String description) {
        super(message, errorCode, description);
    }
}
