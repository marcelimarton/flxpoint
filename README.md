# Customer Assessment
Author: Marceli M. Marton Silva. 

Date: Nov 19th, 2024.

Email: marcelisilvadev@gmail.com

This service runs in Java Springboot application to add/update/remove/display customer data.

1 - There is one rest service and other service that will read the directory and try to send data to CRM constantly.

2 - The local database is in directory {ProjectRoot}/database and each file will have the name as customerId.json.

3 - Files to be sent to CRM are in {ProjectRoot}/queue and will have the name as date-time.json.

4 - Files processed by CRM are moved to {ProjectRoot}/processed.

5 - Logger is used.

6 - Documentation link by Swagger: http://localhost:8080/swagger-ui/index.html

7 - CRM URL = "https://www.crmexample.com/service"


Following is the rest service instructions:


***************************

## Customer creation:

Method: POST

URL: http://localhost:8080/customer/create

Body: 
```
{
	"customerId": "3",
	"firstName": "first name",
	"lastName": "last name",
	"email": "email@email.com",
	"phoneNumber": "999999999",
	"address": { 
		"street": "street name",
		"city" : "city name",
		"state" : "state name",
		"zipCode" : "1111222"
	}
}
```
Returns: Confirmation or error message


***************************

## Customer update

Method: PUT

URL: http://localhost:8080/customer/update/{customerId}

Body: 

```
{
	"customerId": "3",
	"firstName": "first name",
	"lastName": "last name",
	"email": "email@email.com",
	"phoneNumber": "999999999",
	"address": { 
		"street": "street name",
		"city" : "city name",
		"state" : "state name",
		"zipCode" : "1111222"
	}
}
```

Returns: Confirmation or error message

***************************

## Customer get

Method: GET

URL: http://localhost:8080/customer/get/{customerId}

Body: <empty>

Returns: Json customer data or error message


***************************

## Customer deletion

Method: DELETE

URL: http://localhost:8080/customer/delete/{customerId}
Body: <empty>
Returns: Confirmation or error message


