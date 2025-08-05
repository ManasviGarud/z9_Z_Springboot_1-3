# 📦 Real-Time Order Management System

A Spring Boot-based backend application that powers a full-stack Order Management System. Integrated with AWS services and deployable via CI/CD (GitHub Actions or Jenkins).

---

## 📁 Project Structure

z9_Z_Springboot_1-3/
├── src/ # Source code (main/java and test/java)
├── pom.xml # Maven project file
├── .gitignore # Git ignored files
├── mvnw, mvnw.cmd # Maven wrapper scripts
├── .mvn/ # Maven wrapper folder
├── HELP.md # Spring Boot-generated help docs
└── target/ # Auto-generated compiled output


## 🚀 Key Features

- REST APIs to create and fetch orders
- AWS DynamoDB integration for persistent storage
- AWS S3 integration to upload invoice PDFs
- AWS SNS integration to send notifications on order creation
- Swagger UI for easy API exploration
- CI/CD pipeline using GitHub Actions (or Jenkins)

---

## 🛠️ Tech Stack

- **Java 17+**
- **Spring Boot**
- **Maven**
- **Amazon DynamoDB**
- **Amazon S3**
- **Amazon SNS**
- **Swagger UI**
- **CI/CD**: GitHub Actions or Jenkins

---

## 📄 API Endpoints

| Method | Endpoint       | Description |
|--------|----------------|-------------|
| POST   | `/orders`      | Create a new order (saves to DynamoDB, uploads PDF to S3, sends SNS message) |
| GET    | `/orders/{id}` | Fetch an order by its ID |


🧪 Running Locally
Ensure Java and Maven are installed.

Before running, ensure your .env (or application.properties) contains:
AWS_ACCESS_KEY_ID=your-access-key
AWS_SECRET_ACCESS_KEY=your-secret-key
AWS_REGION=your-region
S3_BUCKET_NAME=your-bucket
SNS_TOPIC_ARN=your-topic-arn

📂 Example path: .github/workflows/deploy.yml

📃 License
This project is licensed under the MIT License.

📧 garudmanasvi26@gmail.com
🔗 GitHub - @ManasviGarud
