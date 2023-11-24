package com.tarento.upsmf.userManagement.exception;

import com.tarento.upsmf.userManagement.utility.ErrorCode;

public class OtpException extends CustomException {

    public OtpException(String message) {
        super(message);
    }

    public OtpException(String message, String description) {
        super(message, description);
    }

    public OtpException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }


    public OtpException(String message, ErrorCode errorCode, String description) {
        super(message, errorCode, description);
    }
}
