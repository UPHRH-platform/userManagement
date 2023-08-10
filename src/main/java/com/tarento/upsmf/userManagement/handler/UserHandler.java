package com.tarento.upsmf.userManagement.handler;

import com.fasterxml.jackson.databind.JsonNode;
import com.tarento.upsmf.userManagement.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.net.URISyntaxException;

@Component
public class UserHandler {

    @Autowired
    private UserService userService;

    public ResponseEntity<JsonNode>  createUser(final JsonNode body) throws URISyntaxException {
        return userService.createUser(body);
    }

    public ResponseEntity<JsonNode>  updateUser(final JsonNode body) throws URISyntaxException{
        return userService.updateUser(body);
    }

    public ResponseEntity<JsonNode>  listUser(final JsonNode body) throws URISyntaxException{
        return userService.listUser(body);
    }

    public ResponseEntity<JsonNode>  activateUser(final JsonNode body) throws URISyntaxException{
        return userService.activateUser(body);
    }

    public ResponseEntity<JsonNode>  deactivateUser(final JsonNode body) throws URISyntaxException{
        return userService.deactivateUser(body);
    }
    public ResponseEntity<JsonNode>  sendOtp(int  phoneNumber) throws URISyntaxException {
        return userService.sendOtp(phoneNumber);
    }

}
