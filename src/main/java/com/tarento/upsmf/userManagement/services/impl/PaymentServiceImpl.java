package com.tarento.upsmf.userManagement.services.impl;

import com.tarento.upsmf.userManagement.model.ResponseDto;
import com.tarento.upsmf.userManagement.model.Transaction;
import com.tarento.upsmf.userManagement.repository.TransactionRepository;
import com.tarento.upsmf.userManagement.services.PaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.net.URI;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Service
@PropertySource({ "classpath:application.properties" })
public class PaymentServiceImpl implements PaymentService {

    private static final Logger logger = LoggerFactory.getLogger(PaymentServiceImpl.class);

    @Autowired
    private Environment env;

    private static Environment environment;

    @Autowired
    TransactionRepository repository;

    private String REGISTRATION_PAYMENT_GATEWAY_ENDPOINT;
    private String AFFILIATION_PAYMENT_GATEWAY_ENDPOINT;
    private String EXAM_PAYMENT_GATEWAY_ENDPOINT;
    private String FEE_STATUS_UPDATE_ENDPOINT;
    private String RETOTALING_FEE_STATUS_UPDATE_ENDPOINT;
    private String EXAMS_AUTH_TOKEN;
    private String AES_KEY_FOR_PAYMENT_SUCCESS;

    @Autowired
    private RestTemplate restTemplate;

    @PostConstruct
    public void init(){
        environment = env;
        REGISTRATION_PAYMENT_GATEWAY_ENDPOINT = getPropertyValue("registration_payment_Gateway_EndPoint");
        AFFILIATION_PAYMENT_GATEWAY_ENDPOINT = getPropertyValue("affiliation_payment_Gateway_EndPoint");
        AES_KEY_FOR_PAYMENT_SUCCESS = getPropertyValue("aes_key_for_payment_success");
        EXAM_PAYMENT_GATEWAY_ENDPOINT = getPropertyValue("exam_payment_Gateway_EndPoint");
        FEE_STATUS_UPDATE_ENDPOINT = getPropertyValue("exam_fee_status_update_EndPoint");
        RETOTALING_FEE_STATUS_UPDATE_ENDPOINT = getPropertyValue("retotaling_fee_status_update_EndPoint");
        EXAMS_AUTH_TOKEN = getPropertyValue("exam_service_auth_token");
    }

    public static String getPropertyValue(String property){
        return environment.getProperty(property);
    }

    @Override
    public ResponseEntity<String> makePayment(Map<String, String> requestData) {
        logger.info("payment details...{} ", requestData);
        org.springframework.http.HttpHeaders httpHeaders = new org.springframework.http.HttpHeaders();
        String mandatoryFields = requestData.get("mandatory fields");
        logger.info("mandatory fields details...{} ", mandatoryFields);
        if(mandatoryFields != null && !mandatoryFields.isEmpty()) {
            String[] split = mandatoryFields.split(Pattern.quote("|"));
            List<String> strings = Arrays.asList(split);
            String strEndPoint = "";
            if (strings.contains("registration")) {
                strEndPoint = REGISTRATION_PAYMENT_GATEWAY_ENDPOINT;
            } else if (strings.contains("affiliation")) {
                strEndPoint = AFFILIATION_PAYMENT_GATEWAY_ENDPOINT;
            } else if(strings.contains("exam_institute") || strings.contains("EXAM_INSTITUTE")) {
                strEndPoint = EXAM_PAYMENT_GATEWAY_ENDPOINT;
                // get ref no
                String referenceNo = requestData.get("ReferenceNo");
                // update db for provided transaction id
                updateInstituteFeeStatus(referenceNo);
            } else if(strings.contains("exam_retotaling") || strings.contains("EXAM_RETOTALING")) {
                strEndPoint = EXAM_PAYMENT_GATEWAY_ENDPOINT;
                // get ref no
                String referenceNo = requestData.get("ReferenceNo");
                // update db for provided transaction id
                updateStudentRetotalingFeeStatus(referenceNo);
            }
            String responseString = "", transaction_status = "";
            String transactionDetails = getTransactionDetails(requestData);
            if ((requestData != null) && (requestData.get("Response Code") != null)
                    && requestData.get("Response Code").equals("E000")) {
                responseString = strEndPoint + "?resp=success" + transactionDetails;
                transaction_status = "success";
                logger.info("Payment is successful.");
                logger.info("Record saved to DB.");
            } else {
                responseString = strEndPoint + "?resp=failure" + transactionDetails;
                transaction_status = "failed.";
                logger.info("Payment failed.");
            }
            logger.info("creating transaction.");
            Transaction transaction = getTransaction(requestData, strEndPoint, transaction_status);
            repository.save(transaction);
            logger.info("Transaction {} saved. ResponseString details...{} ", transaction, responseString);
            httpHeaders.setLocation(URI.create(responseString));
            return new ResponseEntity<String>(null, httpHeaders, HttpStatus.FOUND);
        }
        return new ResponseEntity<String>(null, httpHeaders, HttpStatus.NOT_FOUND);
    }

    private void updateInstituteFeeStatus(String referenceNo) {
        logger.info("updating student fee for ref - {}", referenceNo);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.set("x-authenticated-user-token", EXAMS_AUTH_TOKEN);
        HttpEntity<String> entity = new HttpEntity<String>(referenceNo, httpHeaders);
        logger.info("exam url - {}", FEE_STATUS_UPDATE_ENDPOINT);
        try {
            ResponseEntity responseEntity = restTemplate.postForObject(FEE_STATUS_UPDATE_ENDPOINT, entity, ResponseEntity.class);
            logger.info("Update student fee status - {}", responseEntity);
            if (responseEntity != null && responseEntity.getStatusCode() == HttpStatus.OK) {
                logger.info("Student Fee updated successfully");
            }
        } catch (Exception e) {
            logger.error("Error in updating status for institute exam fee request ");
        }
    }

    private void updateStudentRetotalingFeeStatus(String referenceNo) {
        logger.info("updating student fee for ref - {}", referenceNo);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.set("x-authenticated-user-token", EXAMS_AUTH_TOKEN);
        HttpEntity<String> entity = new HttpEntity<String>(referenceNo, httpHeaders);
        logger.info("exam url - {}", FEE_STATUS_UPDATE_ENDPOINT);
        try {
            ResponseEntity responseEntity = restTemplate.postForObject(RETOTALING_FEE_STATUS_UPDATE_ENDPOINT, entity, ResponseEntity.class);
            logger.info("Update student fee status - {}", responseEntity);
            if (responseEntity != null && responseEntity.getStatusCode() == HttpStatus.OK) {
                logger.info("Student Fee updated successfully");
            }
        } catch (Exception e) {
            logger.error("Error in updating status for retotaling request ");
        }
    }

    private String getTransactionDetails(Map<String, String> requestData) {
        String transactionAmount = requestData.get("Transaction Amount") != null ? requestData.get("Transaction Amount") : "";
        String uniqueRefNumber = requestData.get("Unique Ref Number") != null ? requestData.get("Unique Ref Number") : "";
        String response = "&transaction_amount="+transactionAmount+"&transaction_id="+uniqueRefNumber;
        logger.info("transaction details {}",response);
        return response;
    }

    private Transaction getTransaction(final Map<String, String> requestData,  final String strEndPoint, final String transaction_status) {

        Long id = -1L;
        if(requestData.get("ID") != null) {
            id = Long.valueOf(requestData.get("ID"));
        }

        Double transactionAmount = -1.00;
        if(requestData.get("Transaction Amount") != null) {
            transactionAmount = Double.valueOf(requestData.get("Transaction Amount"));
        }

        String paymentMode = "";
        if(requestData.get("Payment Mode") != null) {
            paymentMode = requestData.get("Payment Mode");
        }

        String RS = "";
        if(requestData.get("RS") != null) {
            RS = requestData.get("RS");
        }
        String RSV = "";
        if(requestData.get("RSV") != null) {
            RSV = requestData.get("RSV");
        }
        String TDR = "";
        if(requestData.get("TDR") != null) {
            TDR = requestData.get("TDR");
        }
        String interchangeValue = "";
        if(requestData.get("Interchange Value") != null) {
            interchangeValue = requestData.get("Interchange Value");
        }

        Double processingFeeAmount = -1.00;
        if(requestData.get("Processing Fee Amount") != null) {
            processingFeeAmount = Double.valueOf(requestData.get("Processing Fee Amount"));
        }

        Double totalAmount = -1.00;
        if(requestData.get("Total Amount") != null) {
            totalAmount = Double.valueOf(requestData.get("Total Amount"));
        }

        String TPS = "";
        if(requestData.get("TPS")  != null) {
            TPS = requestData.get("TPS");
        }

        Double serviceTaxAmount = -1.00;
        if(requestData.get("Service Tax Amount") != null) {
            serviceTaxAmount = Double.valueOf(requestData.get("Service Tax Amount"));
        }

        String optionalFields = "";
        if(requestData.get("optional fields") != null) {
            optionalFields = requestData.get("optional fields");
        }

        String module = "";
        if(requestData.get("module") != null) {
            module = requestData.get("module");
        }

        String referenceNo = "";
        if(requestData.get("ReferenceNo") != null) {
            referenceNo = requestData.get("ReferenceNo");
        }

        Integer subMerchantId = -1;
        if(requestData.get("SubMerchantId") != null) {
            subMerchantId = Integer.valueOf(requestData.get("SubMerchantId"));
        }

        String uniqueRefNumber = null;
        if(requestData.get("Unique Ref Number") != null) {
            uniqueRefNumber = requestData.get("Unique Ref Number");
        }

        String responseCode = requestData.get("Response Code");

        Date transactionDate = null;
        try {
            String date = requestData.get("Transaction Date") != null ? requestData.get("Transaction Date") : "";
            DateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            if(!date.isBlank()) {
                transactionDate = format.parse(date);
            }
        } catch (ParseException e) {
            logger.error("parsing date failed.",e);
        }
        String mandatoryFields = requestData.get("mandatory fields");
        Transaction transaction = Transaction.builder().transactionDate(transactionDate)
                .transactionAmount(transactionAmount).paymentMode(paymentMode).rs(RS).rsv(RSV).tdr(TDR)
                .entityId(id).interchangeValue(interchangeValue).processingFeeAmount(processingFeeAmount)
                .totalAmount(totalAmount).tps(TPS).serviceTaxAmount(serviceTaxAmount).mandatoryFields(mandatoryFields)
                .optionalFields(optionalFields).module(module).referenceNo(referenceNo).subMerchantId(subMerchantId)
                .uniqueRefNumber(uniqueRefNumber).responseCode(responseCode).transaction_status(transaction_status).build();
        transaction.setModule(strEndPoint);
        logger.info("Record to be saved {}",transaction);
        return transaction;
    }

    private String sha512Hash(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-512");
            byte[] hashBytes = digest.digest(input.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte hashByte : hashBytes) {
                String hex = Integer.toHexString(0xff & hashByte);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            logger.error("Error occurred while encrypting",e);
            return null;
        }
    }
}
