package com.tarento.upsmf.userManagement.utility;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.http.HttpHeaders;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;


@Component
@PropertySource({ "classpath:application.properties" })
public class KeycloakUserActivateDeActivate {

    private static final Logger logger = LoggerFactory.getLogger(KeycloakUserActivateDeActivate.class);

    @Autowired
    private Environment env;

    @Autowired
    private KeycloakTokenRetriever keycloakTokenRetriever;

    private static Environment environment;

    private String KEYCLOAK_USER_BASE_URL;

    @PostConstruct
    public void init(){
        environment = env;
        KEYCLOAK_USER_BASE_URL = getPropertyValue("keycloak.user.baseURL");
    }

    public static String getPropertyValue(String property){
        return environment.getProperty(property);
    }
    public String activateDeactivatUser(final String userName, boolean activate) throws IOException {
        String userEndpoint = KEYCLOAK_USER_BASE_URL + "/" + userName;
        logger.info("User {} is being activated ? : {} with userEndPoint : {}",userName, activate, userEndpoint);
        JsonNode adminToken = keycloakTokenRetriever.getAdminTokenForWriteOperations();
        String accessToken = adminToken.get("access_token").asText();

        HttpClient httpClient = HttpClients.createDefault();
        HttpPut httpPut = new HttpPut(userEndpoint);

        httpPut.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        httpPut.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");

        String requestBody = "{\"enabled\": " + activate + "}";

        StringEntity entity = new StringEntity(requestBody);
        httpPut.setEntity(entity);

        org.apache.http.HttpResponse response = httpClient.execute(httpPut);
        StatusLine statusLine = response.getStatusLine();

        String responseBody = statusLine.getStatusCode() + "  " + statusLine.getReasonPhrase();
        return responseBody;
    }
}
