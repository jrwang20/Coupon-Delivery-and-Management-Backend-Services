# Coupon-Delivery-and-Management-Backend-Services

## Prepartion
1. Start the Hbase to store the Coupon, Users, Users Holding Coupon, and Users Comments Data
2. Start the MySQL to store the Merchants Data
3. Start the Kafka to transmit message between merchants module and consumers module
4. Start the Redis to store the Coupon Token info

## Test Merchants Module
1. Create Merchants API
  
  POST: 127.0.0.1:9527/merchants/create
	header: token/imooc-passbook-merchants
	{
		"name": "IMooc",
		"logoUrl": "www.imooc.com",
		"businessLicenseUrl": "www.imooc.com",
		"phone": "180808800",
		"address": "Beijing, China"
	}

* Start the server and POST the data through the given url
* Test Results: merchantsId - 21

2. Query Merchants Info API

	GET: 127.0.0.1:9527/merchants/12
	header: token/imooc-passbook-merchants
	
* Start the server, GET the data through the give url
* Test Results: getting the merchants info posted just now

3. Drop the Coupon PassTemplate

	POST: 127.0.0.1:9527/merchants/drop
	header: token/imooc-passbook-merchants
	{
		"background": 1,
		"desc": "IMooc Coupon",
		"end": "2019-09-01",
		"hasToken": false,
		"id": 21,
		"limit": 1000,
		"start": "2019-08-01",
		"summary": "description of coupon-5",
		"title": "coupon-5"
	}
	{
		"background": 2,
		"desc": "Udemy Coupon",
		"end": "2019-09-01",
		"hasToken": true,
		"id": 21,
		"limit": 1000,
		"start": "2019-08-01",
		"summary": "description of coupon-6",
		"title": "coupon-6"
	}
	
* Start both Merchants module and Consumers module servers 
  Drop the PassTemplate through merchants server, and listenning Kafka message and store into HBase through consumers
* Test Results: real time message can be found in Kafka consumer console, while data can be found in HBase

## Test Consumers Module
0. Upload Token API(show the form, upload the token, show the result)
   The Postman cannot POST files, so using the template page
   
	GET: 127.0.0.1:9528/upload
	MerchantsId: 21
	PassTemplateId: 82e391ca1fc1b3e7079f825c9624d781
	
* Test Results: POST Token data through the form and success
                Token file is already in /tmp/token/ 
                Data with key of given PassTemplateId can be found in Redis client

1. Create Consumers API

	POST: 127.0.0.1:9528/passbook/createuser
	{
		"baseInfo": {
			"name": "imooc",
			"age": 10,
			"sex": "m"
		},
		"otherInfo": {
			"phone": "180808800",
			"address": "Beijing China"
		}
	}
	
* Test Result: userID - 188952

2. Query the Coupon Inventory Info which Consumer can gain API

	GET: 127.0.0.1:9528/passbook/inventoryinfo?userId=188952
	
* Test Results: getting the two PassTemplate Info which has just been dropped through Kafka

3. Consumers Gain the Coupon API

	POST: 127.0.0.1:9528/passbook/gainpasstemplate
	{
		"userId": 188952,
		"passTemplate": {
			"id": 21,
			"title": "coupon-5",
			"hasToken": true
		}
	}
	
* Test Results: the consumer having Coupon PassTemplate info can be found in "pb:pass" table in HBase shell
             the Redis has already removed one token
 
4. Query the Coupon Info of Current Consumer

	GET: 127.0.0.1:9528/passbook/userpassinfo?userId=188952
	
* Test Results: returning the Coupon which belongs to current consumer but not used yet

5. Query the Coupon Info of Current Consumer which has already used

	GET: 127.0.0.1:9528/passbook/userusedpassinfo?userId=188952
	
* Test Results: returning empty since none of them has been used

6. Consumer Use the Coupon API

	POST: 127.0.0.1:9528/passbook/userusepass
	{
		"userId": 188952,
		"templateId": "82e391ca1fc1b3e7079f825c9624d781"
	}
	
* Test Results: the consume_date field is updated to current time in HBase shell
              or using the APIs above to check if there is a Coupon is used
              
7. Create the Comments API

	POST: 127.0.0.1:9528/passbook/createfeedback
	{
		"userId": 188952,
		"type": "app",
		"templateId": -1,
		"comment": "comment for application"
	}
	{
		"userId": 188952,
		"type": "pass",
		"templateId": "82e391ca1fc1b3e7079f825c9624d781",
		"comment": "comment for coupon"
	}
	
* Test Results: using the following API to check if there are comments(on App and on Coupon PassTemplate)

8. Query the Comments API

	GET: 127.0.0.1:9528/passbook/getfeedback?userId=188952
	
* Test Results: getting the two comments above
