package com.tarento.upsmf.userManagement.utility;

public enum ErrorCode {

    RC_UM_001("User creation failed - RC User Management"),
    RC_UM_002("Invalid user data - RC User Management"),
    RC_UM_003("User conflict | User already exist - RC User Management"),
    RC_UM_004("Failed user check  - RC User Management"),
    RC_UM_101("Token Inaccessibility - RC User Management"),
    RC_UM_201("Invalid OTP - RC User Management"),
    RC_UM_301("Login failed - RC User Management"),
    RC_UM_0("Undefined - RC User Management"),

    CE_UM_001("User creation failed - Central  User Management"),
    CE_UM_002("Invalid user data - Central User Management"),
    CE_UM_003("Keycloak User conflict | User already exist - Central  User Management"),
    CE_UM_101("Failed Admin Token Generation - Central User Management"),
    CE_UM_301("Login failed - Central User Management"),
    CE_UM_0("Undefined - Central User Management");


    private String description;

    ErrorCode(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
