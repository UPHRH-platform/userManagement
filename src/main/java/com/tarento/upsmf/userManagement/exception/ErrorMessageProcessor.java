package com.tarento.upsmf.userManagement.exception;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tarento.upsmf.userManagement.utility.ErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

/**
 * It is dedicated to serve RC UM error message
 * It has predefined contract with RC UM error response with status code 200;
 * e.g.
 *      {
 *          "code":"400",
 *          "error":"User is not available in User Management System"
*       }
 */
@Component
public class ErrorMessageProcessor {
    private static final Logger logger = LoggerFactory.getLogger(ErrorMessageProcessor.class);

    /**
     * @param response
     */
    public void processOtpMessage(@NonNull String response) {
        JsonNode responseNode = null;

        try {
            if (!response.contains("\"code\":\"RC_UM_400\"")) {
                return;
            }

            ObjectMapper mapper = new ObjectMapper();
            responseNode = mapper.readTree(response);

        } catch (Exception e) {
            logger.error("Error while processing otp mismatch");
            throw new CustomException("Unable to process otp message validation");
        }

        if (responseNode.get("code") != null && !responseNode.get("code").asText().isBlank()
                && responseNode.get("error") != null && !responseNode.get("error").asText().isBlank()) {

            logger.error("OTP error while validating in RC UM");
            throw new InvalidInputException(responseNode.get("error").asText(), ErrorCode.RC_UM_201);
        }
    }


    public void processCredentialMessage(@NonNull String response) {
        JsonNode responseNode = null;

        try {
            if (!response.contains("\"code\":\"RC_UM_400\"")) {
                return;
            }

            ObjectMapper mapper = new ObjectMapper();
            responseNode = mapper.readTree(response);

        } catch (Exception e) {
            logger.error("Error while processing login credential error message");
            throw new CustomException("Unable to process credentials");
        }

        if (responseNode.get("code") != null && !responseNode.get("code").asText().isBlank()
                && responseNode.get("error") != null && !responseNode.get("error").asText().isBlank()) {

            logger.error("Login credentials error while validating in RC UM");
            throw new InvalidInputException(responseNode.get("error").asText(), ErrorCode.RC_UM_301);
        }
    }
}
