package com.tarento.upsmf.userManagement.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.tarento.upsmf.userManagement.handler.UserHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;

@RestController(value = "/api/v1/user)")
public class UserController {

    @Autowired
    private UserHandler userHandler;

    @PostMapping(value = "/create", consumes = "application/json", produces = "application/json")
    public ResponseEntity<JsonNode> createUser(@RequestBody final JsonNode body) throws URISyntaxException {
        return userHandler.createUser(body);
    }

    @PutMapping(value = "/update", consumes = "application/json", produces = "application/json")
    public ResponseEntity<JsonNode> updateUser(@RequestBody final JsonNode body) throws URISyntaxException {
        return userHandler.updateUser(body);
    }

    @PostMapping(value = "/list", consumes = "application/json", produces = "application/json")
    public ResponseEntity<JsonNode> listUser(@RequestBody final JsonNode body) throws URISyntaxException {
        return userHandler.listUser(body);
    }

    @PostMapping(value = "/activate", consumes = "application/json", produces = "application/json")
    public ResponseEntity<JsonNode> activateUser(@RequestBody final JsonNode body) throws URISyntaxException {
        return userHandler.activateUser(body);
    }

    @PostMapping(value = "/deactivate", consumes = "application/json", produces = "application/json")
    public ResponseEntity<JsonNode> deactivateUser(@RequestBody final JsonNode body) throws URISyntaxException {
        return userHandler.deactivateUser(body);
    }
    @GetMapping(value = "/otp", consumes = "application/json", produces = "application/json")
    public ResponseEntity<JsonNode> getOtp(@RequestParam int  phoneNumber) throws URISyntaxException {
        return userHandler.sendOtp(phoneNumber);

    }
}

