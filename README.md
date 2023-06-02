# MINI ASPIRE
A mini project for Aspire Backend Challenge, solved with Java using Spring Boot.


## Brief explaination of the project
This is a mini version of Aspire. It allows you to 
- create users
- request loans
- approve loans if admin
- view loans
- view pending installments<br>
You can create loans, pay installments, view loans only if you're a registered user. <br>
Users can only view their own loans. Admins cannot create loans. <br>
It was written using Spring Boot and testing is with Mockito and JUnit.<br>
There is no persistence to database, however I do use a repository for handling data access.<br>
I have made the assumption that when the request is sent from the UI, it will be properly formatted. <br>
Which means it will contain the expected headers. <br>
Unit test coverage for non lombok lines are a 100%, there are integration tests as well. <br>


## How to run

	Building the application
		From the MiniAspire folder, run the following command. Please use your download location for maven. 
    If you don't have it, please install from https://maven.apache.org/download.cgi
		~/Downloads/apache-maven-3.9.2/bin/mvn package
		OR
		~/Downloads/apache-maven-3.9.2/bin/mvn package -DskipTests

	Running the server
		From the same folder, run
		java -jar target/MiniAspire-0.0.1-SNAPSHOT.jar

## How to use

Following are example curl requests for hitting each of the api endpoints. <br>
Please note that for all the requests other than creating user, you need to provide username of an existing user and password in the headers X-Username and X-Password. <br>
For admin access, use<br>
username:admin<br>
password:xyz123<br>
For the sake of simplicity, I haven't handled the case of multiple admins, since that would require a multithreaded approach.<br> 
Requests are expected to be properly formed.


### CREATE A USER 
curl -X POST -w "\n%{http_code}\n" -H "Content-Type: application/json" -d '{"username":"abc","password":"123"}' http://localhost:8080/api/aspire/user

### CREATE A LOAN
curl -X POST -w "\n%{http_code}\n" -H "Content-Type: application/json" -H "X-Username: abc" -H "X-Password: 123" -d '{"amount":"100.0","term":"3"}' http://localhost:8080/api/aspire/loan

### RETRIEVE LOANS FOR ADMIN
curl -X GET -w "\n%{http_code}\n" -H "Content-Type: application/json" -H "X-Username: admin" -H "X-Password: xyz123" http://localhost:8080/api/aspire/loan

### RETRIEVE LOANS FOR CUSTOMER
curl -X GET -w "\n%{http_code}\n" -H "Content-Type: application/json" -H "X-Username: abc" -H "X-Password: 123" http://localhost:8080/api/aspire/loan

### PROCESS A LOAN FOR ADMIN
curl -X PUT -w "\n%{http_code}\n" -H "Content-Type: application/json" -H "X-Username: admin" -H "X-Password: xyz123" -d '{"loanId":"0","loanResponse":"APPROVED"}' http://localhost:8080/api/aspire/loan

### GET LOANS FOR CUSTOMER
curl -X GET -w "\n%{http_code}\n" -H "Content-Type: application/json" -H "X-Username: abc" -H "X-Password: 123" http://localhost:8080/api/aspire/loan

### PAY INSTALLMENT FOR CUSTOMER
curl -X PUT -w "\n%{http_code}\n" -H "Content-Type: application/json" -H "X-Username: abc" -H "X-Password: 123" -d '{"installmentId":"0","loanId":"0"}' http://localhost:8080/api/aspire/installment

### GET INSTALLMENTS OF CUSTOMER
curl -X GET -w "\n%{http_code}\n" -H "Content-Type: application/json" -H "X-Username: abc" -H "X-Password: 123" http://localhost:8080/api/aspire/installment
