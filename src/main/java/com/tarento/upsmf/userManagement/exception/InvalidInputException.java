package com.tarento.upsmf.userManagement.exception;

import com.tarento.upsmf.userManagement.utility.ErrorCode;

public class InvalidInputException extends CustomException {

    public InvalidInputException(String message) {
        super(message);
    }

    public InvalidInputException(String message, String description) {
        super(message, description);
    }

    public InvalidInputException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }


    public InvalidInputException(String message, ErrorCode errorCode, String description) {
        super(message, errorCode, description);
    }
}
