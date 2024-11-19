package com.example.flxpoint.assessment.services;

import org.slf4j.Logger;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import com.example.flxpoint.assessment.entities.CustomerCrm;

public class CrmProcessor {

    private static final String CRM_URL = "https://www.crmexample.com/service";
    private RestTemplate restTemplate = new RestTemplate();
    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(CrmProcessor.class);

    public void createCRMRecord(CustomerCrm data) throws Exception {
    	try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<CustomerCrm> requestEntity = new HttpEntity<>(data, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                CRM_URL + "/create", HttpMethod.POST, requestEntity, String.class);

            logger.info("Create Response: " + response.getBody());
        } catch (Exception e) {
            throw new Exception("Error creating CRM record: " + e.getMessage());
        }
    }

    public void updateCRMRecord(CustomerCrm data) throws Exception {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<CustomerCrm> requestEntity = new HttpEntity<>(data, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                CRM_URL + "/update", HttpMethod.PUT, requestEntity, String.class);

            logger.info("Update Response: " + response.getBody());
        } catch (Exception e) {
        	throw new Exception("Error updating CRM record: " + e.getMessage());
        }
    }

    public void deleteCRMRecord(String data) throws Exception {
        try {
            String recordId = extractRecordId(data);  // Your logic for extracting the ID
            String deleteUrl = CRM_URL + "/delete/" + recordId;

            ResponseEntity<String> response = restTemplate.exchange(
                deleteUrl, HttpMethod.DELETE, null, String.class);

            logger.info("Delete Response: " + response.getBody());
        } catch (Exception e) {
        	throw new Exception("Error deleting CRM record: " + e.getMessage());
        }
    }

    private String extractRecordId(String content) {
        return content.substring(content.indexOf("id:") + 3, content.indexOf("\n", content.indexOf("id:")));
    }
}

