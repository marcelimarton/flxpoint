package com.example.flxpoint.assessment.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.flxpoint.assessment.entities.CustomerInput;
import com.example.flxpoint.assessment.services.InputService;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/customer")
public class CustomerController {
	
	@Autowired
    private InputService inputService;

	@Tag(name = "post", description = "POST method to create a customer")
    @PostMapping("/create")
    public ResponseEntity<String> createCustomer(@RequestBody CustomerInput customer) {
    	return inputService.createCustomer(customer);
    }
	@Tag(name = "put", description = "PUT method to update a customer")    
    @PutMapping("/update/{customerId}")
    public ResponseEntity<String> updateCustomer(@PathVariable String customerId, @RequestBody CustomerInput customer) {
    	return inputService.updateCustomer(customer);
    }

	@Tag(name = "get", description = "GET method to display a customer")
    @GetMapping("/get/{customerId}")
    public ResponseEntity<String> getCustomer(@PathVariable String customerId) {
        return inputService.getCustomer(customerId);
    }

	@Tag(name = "delete", description = "DELETE method to delete a customer")
    @DeleteMapping("/delete/{customerId}")
    public ResponseEntity<String> deleteCustomer(@PathVariable String customerId) {
        return inputService.deleteCustomer(customerId);
    }
}