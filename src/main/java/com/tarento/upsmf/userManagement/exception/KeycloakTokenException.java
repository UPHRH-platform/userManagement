package com.tarento.upsmf.userManagement.exception;

import com.tarento.upsmf.userManagement.utility.ErrorCode;
import lombok.NoArgsConstructor;

public class KeycloakTokenException extends CustomException {

    public KeycloakTokenException(String message) {
        super(message);
    }

    public KeycloakTokenException(String message, String description) {
        super(message, description);
    }

    public KeycloakTokenException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }


    public KeycloakTokenException(String message, ErrorCode errorCode, String description) {
        super(message, errorCode, description);
    }
}
