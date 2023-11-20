package com.tarento.upsmf.userManagement.utility;

import com.fasterxml.jackson.databind.JsonNode;
import com.tarento.upsmf.userManagement.exception.LoginFailedException;
import org.apache.http.HttpHeaders;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
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
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

@Component
@PropertySource({ "classpath:application.properties" })
public class KeycloakUserGetter {

    private static final Logger logger = LoggerFactory.getLogger(KeycloakUserCreator.class);

    @Autowired
    private Environment env;

    private static Environment environment;

    private String KEYCLOAK_USER_BASE_URL;

    @Autowired
    private KeycloakTokenRetriever keycloakTokenRetriever;

    @PostConstruct
    public void init(){
        environment = env;
        KEYCLOAK_USER_BASE_URL = getPropertyValue("keycloak.user.baseURL");
    }

    public static String getPropertyValue(String property){
        return environment.getProperty(property);
    }
    public String findUser(final String userID, final int offset, final int size) throws IOException {
        String userEndpoint = KEYCLOAK_USER_BASE_URL;
        logger.info("userEndpoint: " ,userEndpoint);
        if(userID != null ) {
//            userEndpoint = userEndpoint + "/" + userID;
            userEndpoint = userEndpoint + "?username=" + userID + "&exact=true";
        } else {
            String parameter = "?first=%s&max=%s";
            parameter = String.format(parameter,offset,size);
            userEndpoint = userEndpoint + parameter;
        }
        logger.info("userEndpoint {} after adding userId : " ,userEndpoint);
        JsonNode adminToken = keycloakTokenRetriever.getAdminToken();
        logger.info("adminToken: {}" ,adminToken);
        String accessToken = adminToken.get("access_token").asText();
        logger.info("accessToken: {}" ,accessToken);

        HttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(userEndpoint);

        httpGet.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        httpGet.setHeader(HttpHeaders.ACCEPT, "application/json");

        org.apache.http.HttpResponse response = httpClient.execute(httpGet);

        if (response.getStatusLine().getStatusCode() == 404) {
            logger.error("User is not available in keycloak");
            throw new LoginFailedException("User does not exist", ErrorCode.CE_UM_003,
                    "Unable to find user in keycloak");
        }

        String responseBody = EntityUtils.toString(response.getEntity());
        logger.info("ResponseBody {}", responseBody);
        return responseBody;
    }

    public String findUserByEmail(final String fieldName, final String fieldValue) throws IOException {
        String userEndpoint = KEYCLOAK_USER_BASE_URL;
        logger.info("userEndpoint: " ,userEndpoint);
        if(fieldName != null && fieldValue!= null ) {
            if(fieldName.equalsIgnoreCase("email")) {
                String encodeEmail = encodeEmail(fieldValue);
                userEndpoint = userEndpoint + "?" + fieldName + "=" + encodeEmail;
            } else {
                userEndpoint = userEndpoint + "?" + fieldName + "=" + fieldValue;
            }

            logger.info("userEndpoint {} after adding email : " ,userEndpoint);
            JsonNode adminToken = keycloakTokenRetriever.getAdminToken();
            logger.info("adminToken: {}" ,adminToken);
            String accessToken = adminToken.get("access_token").asText();
            logger.info("accessToken: {}" ,accessToken);

            HttpClient httpClient = HttpClients.createDefault();
            HttpGet httpGet = new HttpGet(userEndpoint);

            httpGet.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
            httpGet.setHeader(HttpHeaders.ACCEPT, "application/json");

            org.apache.http.HttpResponse response = httpClient.execute(httpGet);
            logger.info("url to be hit {} ",httpGet.toString());
            String responseBody = EntityUtils.toString(response.getEntity());
            logger.info("ResponseBody {}", responseBody);
            return responseBody;
        }
        return "No Response Generated since the inputs were null/empty.";
    }

    private String encodeEmail(String email) throws UnsupportedEncodingException {
        String encode = URLEncoder.encode(email, "UTF-8");
        return encode;
    }
}
