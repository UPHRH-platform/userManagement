package com.tarento.upsmf.userManagement.config;

import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@Data
@Builder
@PropertySource(value = "classpath:application.properties", ignoreResourceNotFound = true)
@Slf4j
public class KeycloakConfig {

    //To be appended with /auth if keycloak version is less than 17
    //@Value("${keycloak.auth-server-url}")
    private String serverUrl="http://localhost:8080/auth";

    //@Value("${keycloak.realm}")
    private String realm="sunbird-rc";

    //@Value("${keycloak.client-id}")
    private String clientId="admin-api";

    //@Value("${keycloak.credentials.secret}")
    private String clientSecret="78712b5a-db33-4de6-9409-c84c9da33f1a";

    //@Value("${keycloak.admin-username}")
    private String userName="admin";

    //@Value("${keycloak.admin-password}")
    private String password="admin";

    //@Value("${keycloak.grant-type}")
    private String grantType="password";

    Keycloak keycloak = null;

    public Keycloak getKeycloakInstance() {
        //log.info("keycloak real {} and server url {} .", realm, serverUrl);
        if (null == keycloak) {
            keycloak = KeycloakBuilder.builder()
                    .serverUrl(serverUrl)
                    .realm(realm)
                    .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                    .clientId(clientId)
                    .clientSecret(clientSecret)
                    .build();
        }
        return keycloak;
    }
}

