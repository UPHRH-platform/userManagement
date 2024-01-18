package com.tarento.upsmf.userManagement.utility;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
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
public class KeycloakUserUpdater {

    @Autowired
    private Environment env;

    @Autowired
    private KeycloakTokenRetriever keycloakTokenRetriever;
    private static Environment environment;
    private static final Logger logger = LoggerFactory.getLogger(KeycloakUserUpdater.class);
    private String KEYCLOAK_USER_BASE_URL;

    @Autowired
    private KeycloakUserCredentialPersister keycloakUserCredentialPersister;

    @PostConstruct
    public void init(){
        environment = env;
        KEYCLOAK_USER_BASE_URL = getPropertyValue("keycloak.user.baseURL");
    }

    public static String getPropertyValue(String property){
        return environment.getProperty(property);
    }

    public String updateUser(final JsonNode requestBody, final String userName) throws IOException {
        JsonNode adminToken = keycloakTokenRetriever.getAdminTokenForWriteOperations();
        logger.info("adminToken: {}" ,adminToken);
        String accessToken = adminToken.get("access_token").asText();
        HttpClient httpClient = HttpClients.createDefault();
        HttpPut httpPut = new HttpPut(KEYCLOAK_USER_BASE_URL + "/" + userName);

        httpPut.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        httpPut.setHeader(HttpHeaders.ACCEPT, "application/json, text/plain, */*");
        httpPut.setHeader(HttpHeaders.CONTENT_TYPE, "application/json;charset=UTF-8");
        logger.info("Request body: {}", requestBody);
        StringEntity entity = new StringEntity(requestBody.toPrettyString());
        httpPut.setEntity(entity);
        HttpResponse response = httpClient.execute(httpPut);
        logger.info("******** Response after execution: {} ********",response);
        String responseBody = "";
        if(response.getEntity() != null) {
        logger.info("******** Response Entity: {} ********",response.getEntity());
            responseBody = EntityUtils.toString(response.getEntity());
        }
        logger.info("ResponseBody {}", responseBody);
        logger.info("response.getStatusLine().getStatusCode() {} ", response.getStatusLine().getStatusCode());
        if (response.getStatusLine().getStatusCode() == 204) {
            if(requestBody.get("credentials")!=null){
                logger.info("credentials is not null");
                ArrayNode credentials = (ArrayNode)requestBody.get("credentials");
                logger.info("credentials[] {}", credentials);
                if(!credentials.isEmpty() && !(credentials.get(0).get("value").asText().isBlank())) {
                    String email = requestBody.get("email").asText();
                    logger.info("email : {}, credentials.get(0).get(\"value\").asText() : {}",email, credentials.get(0).get("value").asText());
                    String value = keycloakUserCredentialPersister.persistUserInfo(email, credentials.get(0).get("value").asText());
                    logger.info("persistUserInfo response {}", value);
                }
            }
            responseBody = " Status 200. User updated successfully.";
        } else {
            responseBody = "Failed to update user." + responseBody;
        }
        return responseBody;
    }
}
