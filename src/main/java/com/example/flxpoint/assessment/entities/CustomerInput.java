package com.example.flxpoint.assessment.entities;

public class CustomerInput {

	private String customerId;
	private String firstName;
	private String lastName;
	private String email;
	private String phoneNumber;
	private Address address;

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public static class Address {
		private String street;
		private String city;
		private String state;
		private String zipCode;

		public String getStreet() {
			return street;
		}

		public void setStreet(String street) {
			this.street = street;
		}

		public String getCity() {
			return city;
		}

		public void setCity(String city) {
			this.city = city;
		}

		public String getState() {
			return state;
		}

		public void setState(String state) {
			this.state = state;
		}

		public String getZipCode() {
			return zipCode;
		}

		public void setZipCode(String zipCode) {
			this.zipCode = zipCode;
		}

	}

	public String toString() {
		String customerObj = "";
		customerObj += "{";
		customerObj += "\"id\": \"" + getCustomerId() + "\", ";
		customerObj += "\"firstName\": \"" + getFirstName() + "\", ";
		customerObj += "\"lastName\": \"" + getLastName() + "\", ";
		customerObj += "\"email\": \"" + getEmail() + "\", ";
		customerObj += "\"phoneNumber\": \"" + getPhoneNumber() + "\", ";
		customerObj += "\"address\": ";
		customerObj += "{";
		customerObj += "\"street\": \"" + getAddress().getStreet() + "\", ";
		customerObj += "\"city\": \"" + getAddress().getCity() + "\", ";
		customerObj += "\"state\": \"" + getAddress().getState() + "\", ";
		customerObj += "\"zipCode\": \"" + getAddress().getZipCode() + "\"";
		customerObj += "}";
		customerObj += "}";

		return customerObj;
	}


}
