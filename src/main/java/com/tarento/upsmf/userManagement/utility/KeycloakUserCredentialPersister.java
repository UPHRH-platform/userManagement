package com.tarento.upsmf.userManagement.utility;

import com.fasterxml.jackson.databind.JsonNode;
import com.tarento.upsmf.userManagement.exception.LoginFailedException;
import org.apache.http.HttpHeaders;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
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
import java.util.UUID;

@Component
@PropertySource({ "classpath:application.properties" })
public class KeycloakUserCredentialPersister {

    private static final Logger logger = LoggerFactory.getLogger(KeycloakUserCredentialPersister.class);

    @Autowired
    private Environment env;

    @Autowired
    private KeycloakUserActivateDeActivate keycloakUserActivateDeActivate;

    @Autowired
    private SunbirdRCKeycloakTokenRetriever sunbirdRCKeycloakTokenRetriever;

    private static Environment environment;

    private String REGISTRY_ENDPOINT_SAVE_USERINFO;

    private String OTP_MAIL_ENDPOINT;

    private String USER_CREATE_MAIL_ENDPOINT;

    private String USER_LOGIN;

    private String USER_LOGOUT;

    @PostConstruct
    public void init(){
        environment = env;
        REGISTRY_ENDPOINT_SAVE_USERINFO = getPropertyValue("registry.endpoint.save.userinfo");
        OTP_MAIL_ENDPOINT = getPropertyValue("otp.mail.endpoint");
        USER_CREATE_MAIL_ENDPOINT = getPropertyValue("user.create.mail.endpoint");
        USER_LOGIN = getPropertyValue("user.login");
        USER_LOGOUT = getPropertyValue("user.logout");
    }

    public static String getPropertyValue(String property){
        return environment.getProperty(property);
    }
    public String persistUserInfo(final String userName, final String password) throws IOException {
        logger.info("saving user info to endpoint {}",REGISTRY_ENDPOINT_SAVE_USERINFO);
        HttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(REGISTRY_ENDPOINT_SAVE_USERINFO);
        JsonNode adminToken = sunbirdRCKeycloakTokenRetriever.getAdminToken();
        String authToken = adminToken.get("access_token").asText();
        httpPost.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
        httpPost.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + authToken);
        String requestBody = "{" +
            "\"username\": " + "\"" + userName + "\"" + "," +
            "\"password\": " + "\"" + password + "\"" +
        "}";
        logger.info("payload to save user info with body {} and header {}",requestBody,httpPost.getAllHeaders());
        StringEntity entity = new StringEntity(requestBody);
        httpPost.setEntity(entity);
        org.apache.http.HttpResponse response = httpClient.execute(httpPost);
        logger.info("Response from server {}",response);
        String responseBody = EntityUtils.toString(response.getEntity());
        return responseBody;
    }

    public String sendOTPMail(JsonNode body) throws IOException {
        logger.info("sending OTP to user email endpoint {} ",OTP_MAIL_ENDPOINT);
        HttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(OTP_MAIL_ENDPOINT);
        JsonNode adminToken = sunbirdRCKeycloakTokenRetriever.getAdminToken();
        String authToken = adminToken.get("access_token").asText();
        httpPost.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
        httpPost.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + authToken);
        logger.info("payload to send OTP mail to user with body {} and header {}", body, httpPost);
        StringEntity entity = new StringEntity(body.toPrettyString());
        httpPost.setEntity(entity);
        org.apache.http.HttpResponse response = httpClient.execute(httpPost);
        logger.info("Response from server {}",response);
        String responseBody = EntityUtils.toString(response.getEntity());
        return responseBody;
    }

    public String sendUserCreateMail(JsonNode body) throws IOException {
        logger.info("sending user create successful mail to user endpoint {}. ",USER_CREATE_MAIL_ENDPOINT);
        HttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(USER_CREATE_MAIL_ENDPOINT);
        JsonNode adminToken = sunbirdRCKeycloakTokenRetriever.getAdminToken();
        String authToken = adminToken.get("access_token").asText();
        httpPost.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
        httpPost.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + authToken);
        logger.info("payload to send user create successful mail to user with body {} and header {}", body, httpPost);
        StringEntity entity = new StringEntity(body.toPrettyString());
        httpPost.setEntity(entity);
        org.apache.http.HttpResponse response = httpClient.execute(httpPost);
        logger.info("Response from server {}",response);
        String responseBody = EntityUtils.toString(response.getEntity());
        return responseBody;
    }

    public String usrLogin(JsonNode body) throws IOException {
        try {
            logger.info("login user endpoint {}. ", USER_LOGIN);
            HttpClient httpClient = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost(USER_LOGIN);
            JsonNode adminToken = sunbirdRCKeycloakTokenRetriever.getAdminToken();
            String authToken = adminToken.get("access_token").asText();
            httpPost.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
            httpPost.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + authToken);
            logger.info("payload login user with body {} and header {}", body, httpPost);
            StringEntity entity = new StringEntity(body.toPrettyString());
            httpPost.setEntity(entity);
            org.apache.http.HttpResponse response = httpClient.execute(httpPost);
            logger.info("Response from server {}", response);
            String responseBody = EntityUtils.toString(response.getEntity());

            if (response.getStatusLine().getStatusCode() == 500) {
                logger.error("Error while trying to login in RC User Management");
                throw new LoginFailedException("Invalid user credential - login failed in RC UM", ErrorCode.RC_UM_301,
                        responseBody);
            }

            if (response.getStatusLine().getStatusCode() == 400) {
                logger.error("Error while trying to login in - Credentials have authorization issue - check password");
                throw new LoginFailedException("Credentials have authorization issue - login failed in RC UM", ErrorCode.RC_UM_301,
                        responseBody);
            }

            return responseBody;
        } catch (Exception e) {
            logger.error("Error while tring to login with password");
            throw new LoginFailedException("Error while tring to login with password", ErrorCode.CE_UM_301, e.getMessage());
        }
    }

    public ResponseEntity<String> usrLogout(String userId) {
        try {
            logger.info("login user endpoint {}. ", USER_LOGIN);
            HttpClient httpClient = HttpClients.createDefault();
            HttpGet httpGet = new HttpGet(USER_LOGOUT.concat("/").concat(userId));
            JsonNode adminToken = sunbirdRCKeycloakTokenRetriever.getAdminToken();
            String authToken = adminToken.get("access_token").asText();
            httpGet.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
            httpGet.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + authToken);
            logger.info("payload logout user with header {}", httpGet);
            org.apache.http.HttpResponse response = httpClient.execute(httpGet);
            logger.info("Response from server {}", response);
            String responseBody = EntityUtils.toString(response.getEntity());

            if (response.getStatusLine().getStatusCode() == 500) {
                logger.error("Error while trying to logout in RC User Management -- code -- 500");
                throw new LogoutFailedException("Error in terminating session", ErrorCode.RC_UM_302,
                        responseBody);
            }

            return ResponseEntity.ok(responseBody);
        } catch (Exception e) {
            logger.error("Error while terminating session");
            throw new LogoutFailedException("Error while terminating session", ErrorCode.RC_UM_302, e.getMessage());
        }
    }

}
