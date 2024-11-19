package com.example.flxpoint.assessment.services;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.example.flxpoint.assessment.entities.CustomerInput;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class InputService {
	
	private static final Logger logger = LoggerFactory.getLogger(InputService.class); // Logger instance

	private String baseDir = new File("").getAbsolutePath();
	private String databaseDir = baseDir + "\\database\\";
	private String queueDir = baseDir + "\\queue\\";

	private static final String COMMAND_UPDATE = "UPDATE";
	private static final String COMMAND_DELETE = "DELETE";
	private static final String COMMAND_CREATE = "CREATE";

	
	@SuppressWarnings("unchecked")
	public ResponseEntity<String> createCustomer(CustomerInput customer) {
		JSONObject customerObj = new JSONObject();  
		customerObj.put("customerId", customer.getCustomerId()); 
		customerObj.put("firstName", customer.getFirstName()); 
		customerObj.put("lastName", customer.getLastName()); 
		customerObj.put("email", customer.getEmail()); 
		customerObj.put("phoneNumber", customer.getPhoneNumber()); 

		JSONObject addressObj = new JSONObject();  

		addressObj.put("street", customer.getAddress().getStreet()); 
		addressObj.put("city", customer.getAddress().getCity()); 
		addressObj.put("state", customer.getAddress().getState()); 
		addressObj.put("zipCode", customer.getAddress().getZipCode()); 

		customerObj.put("address", addressObj);

		try {  

			File file=new File(databaseDir + customer.getCustomerId() + ".json");  
			if (file.exists()) {
				logger.error("The customer " + customer.getCustomerId() + " already exists.");
				return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Customer creation failed: The customer "+ customer.getCustomerId() + " already exists.");
			}

			file.createNewFile();

			FileWriter fileWriter = new FileWriter(file);  
			logger.info("Writing JSON object to file " + file.getAbsolutePath());  
			logger.info("-----------------------");  
			logger.info(customerObj.toJSONString());  

			fileWriter.write(customerObj.toJSONString());  
			fileWriter.flush();  
			fileWriter.close();  

		} catch (IOException e) {  
			logger.error(e.getMessage());
			return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Customer creation failed: " + e.getMessage());
		}  
		
		try {
			createQueuedFiles(COMMAND_CREATE, null, customer);
		} catch (Exception ex) {
			logger.error("Customer queue creation failed: " + ex.getMessage());
			return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Customer queue creation failed: " + ex.getMessage());
		}
		
		return ResponseEntity.status(HttpStatus.CREATED).
				body("Customer created successfully with ID: " + customer.getCustomerId());
	}
	
	@SuppressWarnings("unchecked")
	public ResponseEntity<String> updateCustomer(CustomerInput customer) {
		JSONObject customerObj = new JSONObject();  
		customerObj.put("customerId", customer.getCustomerId()); 
		customerObj.put("firstName", customer.getFirstName()); 
		customerObj.put("lastName", customer.getLastName()); 
		customerObj.put("email", customer.getEmail()); 
		customerObj.put("phoneNumber", customer.getPhoneNumber()); 

		JSONObject addressObj = new JSONObject();  

		addressObj.put("street", customer.getAddress().getStreet()); 
		addressObj.put("city", customer.getAddress().getCity()); 
		addressObj.put("state", customer.getAddress().getState()); 
		addressObj.put("zipCode", customer.getAddress().getZipCode()); 

		customerObj.put("address", addressObj);

		try {  

			File file=new File(databaseDir + customer.getCustomerId() + ".json");  
			if (!file.exists()) {
				logger.error("The customer " + customer.getCustomerId() + " does not exist.");
				return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Customer update failed: The customer " + customer.getCustomerId() + " does not exist.");
			}

			file.delete();
			file.createNewFile();

			FileWriter fileWriter = new FileWriter(file);  
			logger.info("Writing JSON object to file " + file.getAbsolutePath());  
			logger.info("-----------------------");  
			logger.info(customerObj.toJSONString());  

			fileWriter.write(customerObj.toJSONString());  
			fileWriter.flush();  
			fileWriter.close();  

		} catch (IOException e) {  
			logger.error("Customer update failed: " + e.getMessage());
			return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Customer update failed: ");
		}  
		
		try {
			createQueuedFiles(COMMAND_UPDATE, null, customer);
		} catch (Exception ex) {
			logger.error("Customer queue update failed: " + ex.getMessage());
			return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Customer queue update failed: " + ex.getMessage());
		}
		
		return ResponseEntity.status(HttpStatus.CREATED).
				body("Customer created successfully with ID: " + customer.getCustomerId());
	}

	public ResponseEntity<String> getCustomer(String customerId) {

		CustomerInput customerInput = null; 
		try {  

			File file=new File(databaseDir + customerId + ".json");  
			if (file.exists()) {
				ObjectMapper objectMapper = new ObjectMapper();
				customerInput = objectMapper.readValue(file, CustomerInput.class);
			} else {
				logger.error("The customer " + customerId + " does not exist.");
				return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Customer get failed: The customer " + customerId + " does not exist.");
		}
		}catch(Exception ex) {
			logger.error(ex.getMessage());
			return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Customer get failed: " + ex.getMessage());
		}
		return ResponseEntity.status(HttpStatus.OK).body(customerInput.toString());
	}

	public ResponseEntity<String> deleteCustomer(String customerId) {
		try {  

			File file=new File(databaseDir + customerId + ".json");  
			if (file.exists()) {
				file.delete();
			} else {
				logger.error("The customer " + customerId + " does not exist.");
				return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Customer delete failed: The customer " + customerId + " does not exist");
			}
		}catch(Exception ex) {
			logger.error(ex.getMessage());  
			return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Customer update failed: " + ex.getMessage());
		}
		
		try {
			createQueuedFiles(COMMAND_DELETE, customerId, null);
		} catch (Exception ex) {
			logger.error("Customer queue update failed." + ex.getMessage());
			return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Customer queue update failed: " + ex.getMessage());
		}

		return ResponseEntity.status(HttpStatus.OK).
				body("Customer deleted successfully with ID: " + customerId);

	}

	@SuppressWarnings("unchecked")
	private void createQueuedFiles(String command, String customerId, CustomerInput customerInput) 
		throws Exception{
		String pattern = "yyyy-MM-dd-HH_mm_ss";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		String date = simpleDateFormat.format(new Date());
		logger.info(date);

		File file = null; 
		try {  

			file=new File(queueDir + date + ".json");  

			JSONObject customerObj = new JSONObject();  
			customerObj.put("command", command); 
			customerObj.put("customerId", customerId); 
			customerObj.put("data", mapToCustomerCrm(customerInput)); 

			file.createNewFile();

			FileWriter fileWriter = new FileWriter(file);  
			logger.info("Writing JSON object to file " + file.getAbsolutePath());  
			logger.info("-----------------------");  
			logger.info(customerObj.toJSONString());  

			fileWriter.write(customerObj.toJSONString());  
			fileWriter.flush();  
			fileWriter.close();  
		}catch(Exception ex) {
			logger.error(ex.getMessage());
			throw new Exception ("Error creating queue file " + file);
		}

	}

	@SuppressWarnings({ "unchecked", "unused" })
	private JSONObject mapToCustomerCrm(CustomerInput customer) {
		if (customer == null) {
			return null;
		}
		JSONObject customerCrmObj = new JSONObject(); 
		customerCrmObj.put("id", customer.getCustomerId()); 
		customerCrmObj.put("fullName", customer.getFirstName() + " " + customer.getLastName());
		customerCrmObj.put("contactEmail", customer.getEmail());
		customerCrmObj.put("primaryPhone", customer.getPhoneNumber());
		customerCrmObj.put("location", String.join(", ",
				customer.getAddress().getStreet(),
				customer.getAddress().getCity(),
				customer.getAddress().getState(),
				customer.getAddress().getZipCode()));
		return customerCrmObj;
	}
}
