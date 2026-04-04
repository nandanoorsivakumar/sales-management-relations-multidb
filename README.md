# Sales Management Relations MultiDB Project

## 📌 Overview
This is a Spring Boot based backend application designed to manage Sales operations including Employees, Customers, Orders, and User Authentication.

The project demonstrates real-time enterprise-level concepts like:
- Multi-database integration (MySQL + MongoDB)
- JWT Authentication & Authorization
- Audit Logging
- Layered Architecture (Controller → Service → Repository)

---

## 🚀 Technologies Used

- Java 17
- Spring Boot
- Spring Data JPA
- Spring Security (JWT)
- MySQL (Primary DB)
- MongoDB (Audit Logs)
- Maven
- Swagger (OpenAPI)

---

## 🏗️ Project Architecture
controller/
service/
repository/
entity/
dto/
security/
mongo/
exception/
config/



---

## ⚙️ Configuration

### application.yml

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/sales_relations_db
  data:
    mongodb:
      uri: mongodb://localhost:27017/sales_relations_logs_2

      git clone https://github.com/nandanoorsivakumar/sales-management-relations-multidb.git


## API Testing
Use Postman
Swagger UI (if enabled)
