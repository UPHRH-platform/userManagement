package com.tarento.upsmf.userManagement.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.tarento.upsmf.userManagement.exception.LogoutFailedException;
import com.tarento.upsmf.userManagement.handler.UserHandler;
import com.tarento.upsmf.userManagement.model.Transaction;
import com.tarento.upsmf.userManagement.services.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/api/v1/user")
public class UserController {

    @Autowired
    PaymentService paymentService;

    @Autowired
    private UserHandler userHandler;

    @PostMapping(value = "/create", consumes = "application/json", produces = "application/json")
    public String createUser(@RequestBody final JsonNode body) throws URISyntaxException, IOException {
        return userHandler.createUser(body);
    }

    @PutMapping(value = "/update", consumes = "application/json", produces = "application/json")
    public String updateUser(@RequestBody final JsonNode body) throws URISyntaxException, IOException {
        return userHandler.updateUser(body);
    }

    @PostMapping(value = "/details", consumes = "application/json", produces = "application/json")
    public String userDetails(@RequestBody final JsonNode body) throws URISyntaxException, IOException {
        return userHandler.userDetails(body);
    }

    /**
     * @param body
     * @return
     * @throws URISyntaxException
     * @throws IOException
     */
    @PostMapping(value = "/detailsById", consumes = "application/json", produces = "application/json")
    public String userDetailsId(@RequestBody final JsonNode body) throws URISyntaxException, IOException {
        return userHandler.userDetailsById(body);
    }

    @PostMapping(value = "/isUserExist", consumes = "application/json", produces = "application/json")
    public String isUserExist(@RequestBody final JsonNode body) throws URISyntaxException, IOException {
        return userHandler.userDetailsById(body);
    }

    @PostMapping(value = "/emaildetails", consumes = "application/json", produces = "application/json")
    public String userEmailDetails(@RequestBody final JsonNode body) throws URISyntaxException, IOException {
        return userHandler.userEmailDetails(body);
    }

    @PostMapping(value = "/list", consumes = "application/json", produces = "application/json")
    public String listUser(@RequestBody final JsonNode body) throws URISyntaxException, IOException {
        return userHandler.listUser(body);
    }

    @GetMapping(value = "/count", produces = "application/json")
    public String userCount() throws IOException {
        return userHandler.userCount();
    }

    @PostMapping(value = "/activate", consumes = "application/json", produces = "application/json")
    public String activateUser(@RequestBody final JsonNode body) throws URISyntaxException, IOException {
        return userHandler.activateUser(body);
    }

    @PostMapping(value = "/deactivate", consumes = "application/json", produces = "application/json")
    public String deactivateUser(@RequestBody final JsonNode body) throws URISyntaxException, IOException {
        return userHandler.deactivateUser(body);
    }

    @GetMapping(value = "/sms/otp", produces = "application/json")
    public ResponseEntity<String> sendOTP(@RequestParam String phoneNumber, @RequestParam String name, @RequestParam String otp) throws URISyntaxException {
        return userHandler.sendOTP(phoneNumber, name, otp);
    }

    @PostMapping(value = "/keycloak/otp", consumes = "application/json", produces = "application/json")
    public ResponseEntity<String> generateOTP(@RequestBody String email) throws URISyntaxException, IOException {
        return userHandler.generateOTP(email);
    }

    @PostMapping(value = "/keycloak/login", consumes = "application/json", produces = "application/json")
    public ResponseEntity<String> login(@RequestBody JsonNode body ) throws URISyntaxException, IOException {
        return userHandler.login(body);
    }

    @PostMapping(value = "/payment", consumes = {"*/*"})
    public ResponseEntity<String> paymentRedirect(@RequestParam Map<String, String> requestData) throws URISyntaxException, IOException {
        return userHandler.paymentRedirect(requestData);
    }

    @PostMapping(value = "/keycloak/usrlogin")
    public String usrlogin(@RequestBody JsonNode body) throws IOException {
        return userHandler.usrLogin(body);
    }

    @PostMapping(value = "/keycloak/usrOTP")
    public String usrOTP(@RequestBody JsonNode body) throws IOException {
        return userHandler.usrOTP(body);
    }

    @GetMapping(value = "/transaction", produces = "application/json")
    public ResponseEntity<List<Transaction>> getAllTransactions() {
        return userHandler.getAllTransactions();
    }

    @GetMapping(value = "/transaction/{uniqueRefNumber}", produces = "application/json")
    public ResponseEntity<?> getTransactionByUniqueRefNumber(@PathVariable String uniqueRefNumber) {
        return userHandler.getTransactionByUniqueRefNumber(uniqueRefNumber);
    }

    @PostMapping(value = "/attribute", produces = "application/json")
    public ResponseEntity<String> getUserByAttribute(@RequestBody JsonNode body) throws SQLException, IOException, URISyntaxException {
        return userHandler.getUserByAttribute(body);
    }

    @PostMapping(value = "/logout", produces = "application/json")
    public ResponseEntity<?> logout(@RequestBody JsonNode jsonNode) throws LogoutFailedException {
        return userHandler.logout(jsonNode);
    }
}
