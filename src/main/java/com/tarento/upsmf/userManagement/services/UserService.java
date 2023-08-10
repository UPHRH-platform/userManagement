package com.tarento.upsmf.userManagement.services;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.*;
import java.nio.charset.StandardCharsets;

@Component
public class UserService {


    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private Environment env;

    private final RestTemplate restTemplate = new RestTemplate();
    final String BASE_URL = "https://uphrh.in/api";

    private HttpHeaders getHeader(){
        System.out.println(env.getProperty("BaseURL"));
        logger.info("Getting headers...");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization","Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJSR3RkMkZzeG1EMnJER3I4dkJHZ0N6MVhyalhZUzBSSyJ9.kMLn6177rvY53i0RAN3SPD5m3ctwaLb32pMYQ65nBdA");
        headers.add("x-authenticated-user-token","eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJBbjFzUzNFanlFeUhYRGdlWXUxdGdpTjM2SDdyalFOcld0a3NOQzhnbUNVIn0.eyJqdGkiOiJmODNlZTgzOS05NmM0LTQ3NWMtYTk2Yi1lNDI2MDViYmNjMjMiLCJleHAiOjE2OTA4Mzk2OTgsIm5iZiI6MCwiaWF0IjoxNjkwNzk2NDk4LCJpc3MiOiJodHRwczovL3VwaHJoLmluL2F1dGgvcmVhbG1zL3N1bmJpcmQiLCJhdWQiOlsicmVhbG0tbWFuYWdlbWVudCIsImFjY291bnQiXSwic3ViIjoiYTk3YjQyY2UtNzkwNi00MTRlLWJjMzAtMzFhODk0NjgwZjUzIiwidHlwIjoiQmVhcmVyIiwiYXpwIjoibG1zIiwiYXV0aF90aW1lIjowLCJzZXNzaW9uX3N0YXRlIjoiZjc2NjlhYmItOWE4Ni00MWNjLWI2OWUtM2Q4ZTZjZjE2MjI2IiwiYWNyIjoiMSIsImFsbG93ZWQtb3JpZ2lucyI6WyJodHRwczovL3VwaHJoLmluIl0sInJlYWxtX2FjY2VzcyI6eyJyb2xlcyI6WyJvZmZsaW5lX2FjY2VzcyIsImFkbWluIiwidW1hX2F1dGhvcml6YXRpb24iXX0sInJlc291cmNlX2FjY2VzcyI6eyJyZWFsbS1tYW5hZ2VtZW50Ijp7InJvbGVzIjpbIm1hbmFnZS11c2VycyIsInZpZXctdXNlcnMiLCJxdWVyeS1ncm91cHMiLCJxdWVyeS11c2VycyJdfSwiYWNjb3VudCI6eyJyb2xlcyI6WyJtYW5hZ2UtYWNjb3VudCIsIm1hbmFnZS1hY2NvdW50LWxpbmtzIiwidmlldy1wcm9maWxlIl19fSwic2NvcGUiOiIiLCJuYW1lIjoiYWRtaW4gYWRtaW4iLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJhZG1pbiIsImdpdmVuX25hbWUiOiJhZG1pbiIsImZhbWlseV9uYW1lIjoiYWRtaW4iLCJlbWFpbCI6ImFkbWluQHN1bmJpcmQub3JnIn0.fh9Zj8htV_-ecMWF_5E2zNsZTZCITP509uJk7zSm6o-uqLRLXy6YupbNbyUZmAL_CkkmAawCtQcNA4GTyaacSA4mRBDfWrYeLfrguFbmgTMMuLOLGzfCfnTwDYhAoyZlyO8P8pnw9B8a0nKClqbqt1h2kpGYDmNV7fXltrE4f81IDXGGIbxuWrFhqmqT2xXi4gIf6Y6ANXFoU4jYaqRXw3hoYwCyYDFi57Dljzx_KWfCb4UxKqmVF1vlLokNVoYuhMNZSICEwJCBIS_PrY9sfoYZZK1Q04NWs1F6Hppqy85PhWn_EycaEeZ0OAByIvi2HagdT0Ris2sHVFntphQbkA");
        return headers;
    }

    public ResponseEntity<JsonNode> createUser(final JsonNode body) throws URISyntaxException {
        logger.info("Creating user...");
        URI uri = new URI(BASE_URL + "/user/v1/sso/create");
        HttpHeaders headers = getHeader();
        HttpEntity<JsonNode> httpEntity = new HttpEntity(body, headers);
        ResponseEntity<JsonNode> result = restTemplate.postForEntity(uri,httpEntity,JsonNode.class);
        return result;
    }
    public ResponseEntity<JsonNode> updateUser(final JsonNode body) throws URISyntaxException {
        logger.info("Updating user...");
        RestTemplate restTemplate = new RestTemplate();
        // Create HttpClient with PATCH support
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
        restTemplate.setRequestFactory(requestFactory);

        URI uri = new URI(BASE_URL + "/user/v1/update");
        HttpHeaders headers = getHeader();
        HttpEntity<JsonNode> httpEntity = new HttpEntity<>(body, headers);
        ResponseEntity<JsonNode> result = restTemplate.exchange(uri, HttpMethod.PATCH, httpEntity, JsonNode.class);
        return result;
    }


    public ResponseEntity<JsonNode> listUser(final JsonNode body) throws URISyntaxException{
        logger.info("Listing users...");
        RestTemplate restTemplate = new RestTemplate();
        URI uri = new URI(BASE_URL + "/user/v1/search");
        HttpHeaders headers = getHeader();
        HttpEntity httpEntity = new HttpEntity(body, headers);
        ResponseEntity<JsonNode> result = restTemplate.postForEntity(uri,httpEntity,JsonNode.class);
        return result;
    }

    public ResponseEntity<JsonNode> activateUser(final JsonNode body) throws URISyntaxException{
        logger.info("Activating user...");
        RestTemplate restTemplate = new RestTemplate();
        URI uri = new URI(BASE_URL + "/user/v1/unblock");
        HttpHeaders headers = getHeader();
        HttpEntity httpEntity = new HttpEntity(body, headers);
        ResponseEntity<JsonNode> result = restTemplate.postForEntity(uri,httpEntity,JsonNode.class);
        return result;
    }

    public ResponseEntity<JsonNode> deactivateUser(final JsonNode body) throws URISyntaxException {
        logger.info("Deactivating user...");
        RestTemplate restTemplate = new RestTemplate();
        URI uri = new URI(BASE_URL + "/user/v1/block");
        HttpHeaders headers = getHeader();
        HttpEntity httpEntity = new HttpEntity(body, headers);
        ResponseEntity<JsonNode> result = restTemplate.postForEntity(uri,httpEntity,JsonNode.class);
        return result;
    }
    public ResponseEntity<String> getOtp(String phoneNumber) throws URISyntaxException {
        logger.info("getting otp...");

        String baseUrl = "";
        String username = "";
        String password = "";
        String senderId = "";
        String message = "Hello abc, Your OTP is 1234 sfasa, Lucknow";
        String destMobileNo = "";
        String msgType = "TXT";
        String response = "Y";

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .queryParam("username", username)
                .queryParam("pass", password)
                .queryParam("senderid", senderId)
                .queryParam("message", message)
                .queryParam("dest_mobileno", String.valueOf(phoneNumber))
                .queryParam("msgtype", msgType)
                .queryParam("response", response);

        URI uri = builder.build().encode().toUri();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Accept", "application/json");
        HttpEntity<?> httpEntity = new HttpEntity<>(headers);
        ResponseEntity<String> result = restTemplate.exchange(uri, HttpMethod.GET, httpEntity, String.class);
        return result;
    }
}