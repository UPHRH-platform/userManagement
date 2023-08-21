package com.tarento.upsmf.userManagement.handler;

import com.fasterxml.jackson.databind.JsonNode;
import com.tarento.upsmf.userManagement.model.KeyCloakUserDTO;
import com.tarento.upsmf.userManagement.services.UserService;
import com.tarento.upsmf.userManagement.utility.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.UUID;

@Component
public class UserHandler {

    private static final Logger logger = LoggerFactory.getLogger(UserHandler.class);
    @Autowired
    private UserService userService;

    @Autowired
    private KeycloakUserCreator keycloakUserCreator;

    @Autowired
    private KeycloakUserGetter keycloakUserGetter;

    @Autowired
    private KeycloakUserUpdater keycloakUserUpdater;

    @Autowired
    private KeycloakUserActivateDeActivate keycloakUserActivateDeActivate;

    public String createUser(final JsonNode body) throws URISyntaxException, IOException {
        logger.info("creating user with payload {} ", body.toPrettyString());
        String response = keycloakUserCreator.createUser(body);
        logger.info("user created ? {}", response);
        try {
            ResponseEntity<JsonNode> user = userService.createUser(body);
            logger.info("user created on ed {}",user);
            user.toString();
        } catch (Exception e){
            //response = e.getLocalizedMessage();
            logger.error("Error Occured",e);
        }
        return response;
    }

    public String updateUser(final JsonNode body) throws URISyntaxException, IOException {
        JsonNode request = body.get("request");
        logger.info("updating user in keycloak with payload {} ", body.toPrettyString());
        String userName = body.get("userName").asText();
        String respone = keycloakUserUpdater.updateUser(request, userName);
        logger.info("updating user with payload {} ", request.toPrettyString());
        try {
            ResponseEntity<JsonNode> jsonNodeResponseEntity = userService.updateUser(body);
        } catch (Exception exception){
            logger.error("Exception while updating user info.", exception);
        }
        return respone;
    }

    public String userDetails(final JsonNode body) throws IOException {
        JsonNode request = body.get("request");
        String userName = keycloakUserGetter.findUser(request.get("userName").asText());
        return userName;
    }


    public String listUser(final JsonNode body) throws URISyntaxException, IOException {
        logger.info("creating user with payload {} ", body.toPrettyString());
        String users = keycloakUserGetter.findUser(null);
        return users;
    }

    public String activateUser(final JsonNode body) throws URISyntaxException, IOException {
        logger.info("activating user with payload {} ", body.toPrettyString());
        JsonNode request = body.get("request");
        String response = keycloakUserActivateDeActivate.activateDeactivatUser(request.get("userName").asText(), true);
        try {
            ResponseEntity<JsonNode> jsonNodeResponseEntity = userService.activateUser(body);
        }catch (Exception exception){
            exception.printStackTrace();
        }
        return response;
    }

    public String  deactivateUser(final JsonNode body) throws URISyntaxException, IOException {
        logger.info("deactivating user with payload {} ", body.toPrettyString());

        JsonNode request = body.get("request");
        String response = keycloakUserActivateDeActivate.activateDeactivatUser(request.get("userName").asText(), false);
        try {
            ResponseEntity<JsonNode> jsonNodeResponseEntity = userService.deactivateUser(body);
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return response;
    }

    public ResponseEntity<String>  sendOTP(String phoneNumber, String name, String otp) throws URISyntaxException {
        logger.info("sending to name: {} OTP:{} to phone: {} ", name, phoneNumber,otp);
        ResponseEntity<String> stringResponseEntity = userService.sendOTP(phoneNumber, name, otp);
        return stringResponseEntity;
    }

    public ResponseEntity<String> generateOTP(String email) throws URISyntaxException, IOException {
        logger.info("generating otp to {} ", email);
        ResponseEntity<String> stringResponseEntity = userService.generateOTP(email);
        return stringResponseEntity;
    }

    public ResponseEntity<String> login(final JsonNode body) throws URISyntaxException, IOException {
        logger.info("login called with payload {} ", body.toPrettyString());
        return userService.login(body);
    }

    private String getorDefault(final JsonNode request, final String key, final String defaultValue){
        return request.get(key) != null ? request.get(key).asText() : defaultValue;
    }
}
