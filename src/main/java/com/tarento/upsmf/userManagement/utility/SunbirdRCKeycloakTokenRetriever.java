package com.tarento.upsmf.userManagement.utility;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tarento.upsmf.userManagement.exception.KeycloakTokenException;
import com.tarento.upsmf.userManagement.services.UserService;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.nio.charset.StandardCharsets;

@Component
@PropertySource({ "classpath:application.properties" })
public class SunbirdRCKeycloakTokenRetriever {

    private static final Logger logger = LoggerFactory.getLogger(SunbirdRCKeycloakTokenRetriever.class);

    @Autowired
    private Environment env;

    private static Environment environment;

    private String ADMIN_TOKEN_ENDPOINT;

    private String ADMIN_TOKEN_SECRET;

    private String ADMIN_USERNAME;
    private String ADMIN_PASSWORD;

    private String ADMIN_CLIENTID;

    private ObjectMapper mapper = new ObjectMapper();

    @PostConstruct
    public void init(){
        environment = env;
        ADMIN_TOKEN_ENDPOINT = getPropertyValue("sunbirdRC.keycloak.adminToken.endPoint");
        ADMIN_TOKEN_SECRET = getPropertyValue("sunbirdRC.keycloak.adminToken.clientSecret");
        ADMIN_USERNAME = getPropertyValue("sunbirdRC.keycloak.adminToken.userName");
        ADMIN_CLIENTID = getPropertyValue("sunbirdRC.keycloak.adminToken.clientID");
        ADMIN_PASSWORD = getPropertyValue("sunbirdRC.keycloak.adminToken.password");
    }

    public static String getPropertyValue(String property){
        return environment.getProperty(property);
    }
    public JsonNode getAdminToken() throws IOException {
        String tokenEndpoint = ADMIN_TOKEN_ENDPOINT;
        logger.info("Token endpoint: {}" ,tokenEndpoint);
        HttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(tokenEndpoint);

        String requestBody = "username=" + ADMIN_USERNAME +
                "&grant_type=client_credentials" +
                "&client_id=" + ADMIN_CLIENTID +
                "&client_secret=" + ADMIN_TOKEN_SECRET;

        logger.info("Request body: {}", requestBody);
        StringEntity entity = new StringEntity(requestBody);

        httpPost.setEntity(entity);
        httpPost.setHeader(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded");
        logger.info("headers {}",httpPost);

        org.apache.http.HttpResponse response = httpClient.execute(httpPost);
        HttpEntity httpEntity = response.getEntity();
        String responseBody = EntityUtils.toString(httpEntity, StandardCharsets.UTF_8);

        if (response.getStatusLine().getStatusCode() == 200) {
            logger.info("Access token obtained successfully.");
            logger.info("Response: " + responseBody);
        } else {
            logger.error("Failed to obtain access token. - Response Body: " + responseBody);
            throw new KeycloakTokenException("Unable to generate admin access token", ErrorCode.CE_UM_101,
                    "Check keycloak client configuration");
        }

        logger.info("Response body: {}", responseBody);
        JsonNode jsonNode = mapper.readTree(responseBody);
        return jsonNode;
    }
}
