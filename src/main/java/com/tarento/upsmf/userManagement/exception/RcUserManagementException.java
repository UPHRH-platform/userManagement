package com.tarento.upsmf.userManagement.exception;

import com.tarento.upsmf.userManagement.utility.ErrorCode;

public class RcUserManagementException extends CustomException {

    public RcUserManagementException(String message) {
        super(message);
    }

    public RcUserManagementException(String message, String description) {
        super(message, description);
    }

    public RcUserManagementException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }


    public RcUserManagementException(String message, ErrorCode errorCode, String description) {
        super(message, errorCode, description);
    }
}
