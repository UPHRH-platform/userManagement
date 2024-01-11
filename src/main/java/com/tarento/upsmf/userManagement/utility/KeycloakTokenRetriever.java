package com.tarento.upsmf.userManagement.utility;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tarento.upsmf.userManagement.services.UserService;
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

@Component
@PropertySource({ "classpath:application.properties" })
public class KeycloakTokenRetriever {

    private static final Logger logger = LoggerFactory.getLogger(KeycloakTokenRetriever.class);

    @Autowired
    private Environment env;

    private static Environment environment;

    private String ADMIN_TOKEN_ENDPOINT;

    private String ADMIN_TOKEN_SECRET;

    private String ADMIN_USERNAME;
    private String ADMIN_PASSWORD;

    private ObjectMapper mapper = new ObjectMapper();

    @PostConstruct
    public void init(){
        environment = env;
        ADMIN_TOKEN_ENDPOINT = getPropertyValue("adminToken.endPoint");
        ADMIN_TOKEN_SECRET = getPropertyValue("adminToken.clientSecret");
        ADMIN_USERNAME = getPropertyValue("adminToken.userName");
        ADMIN_PASSWORD = getPropertyValue("adminToken.password");
    }

    public static String getPropertyValue(String property){
        return environment.getProperty(property);
    }

    public JsonNode getAdminToken() throws IOException {
        String tokenEndpoint = ADMIN_TOKEN_ENDPOINT;
        logger.info("Token endpoint: {}" ,tokenEndpoint);

        HttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(tokenEndpoint);
        httpPost.setHeader(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded");

        String requestBody = "username=" + ADMIN_USERNAME +
            "&password=" + ADMIN_PASSWORD +
            "&grant_type=client_credentials" +
            "&client_id=admin-cli" +
            "&client_secret=" + ADMIN_TOKEN_SECRET;
        logger.info("Request body: {}", requestBody);

        StringEntity entity = new StringEntity(requestBody);
        httpPost.setEntity(entity);

        org.apache.http.HttpResponse response = httpClient.execute(httpPost);
        String responseBody = EntityUtils.toString(response.getEntity());
        JsonNode jsonNode = mapper.readTree(responseBody);

        return jsonNode;
    }

    public JsonNode getAdminTokenRead() throws IOException {
        String tokenEndpoint = ADMIN_TOKEN_ENDPOINT;
        logger.info("Token endpoint: {}" ,tokenEndpoint);

        HttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(tokenEndpoint);
        httpPost.setHeader(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded");

        String requestBody = "username=" + ADMIN_USERNAME +
                "&password=" + ADMIN_PASSWORD +
                "&grant_type=password" +
                "&client_id=admin-cli" +
                "&client_secret=" + ADMIN_TOKEN_SECRET;
        logger.info("Request body: {}", requestBody);

        StringEntity entity = new StringEntity(requestBody);
        httpPost.setEntity(entity);

        org.apache.http.HttpResponse response = httpClient.execute(httpPost);
        String responseBody = EntityUtils.toString(response.getEntity());
        JsonNode jsonNode = mapper.readTree(responseBody);

        return jsonNode;
    }
}
