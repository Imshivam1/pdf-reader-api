# PDF Reader API

## Overview
The **PDF Reader API** is a Spring Boot-based application that allows users to upload and parse PDF documents, extract text, and process the extracted data using an LLM (Language Model). This API supports standard PDFs and password-protected PDFs, and it integrates OpenAI's GPT-4 Turbo for data extraction.

## Features
- 📄 **Upload and parse PDF files**
- 🔑 **Secure PDF parsing with dynamic password generation**
- 🧠 **Extract structured data using GPT-4 Turbo**
- 📂 **Parse PDFs stored on disk**

## Technologies Used
- **Java 17**
- **Spring Boot**
- **Apache PDFBox** (for PDF text extraction)
- **OpenAI API** (for processing extracted text)
- **Maven** (for dependency management)

---
## Installation and Setup

### 1️⃣ Clone the Repository
```sh
 git clone <repository-url>
 cd pdf-reader-api
```

### 2️⃣ Configure Application Properties
Create an `application.yml` file inside `src/main/resources/` with the following content:
```yaml
spring:
  servlet:
    multipart:
      enabled: true
      max-file-size: 10MB
      max-request-size: 20MB

theokanning:
  openai:
    api-key: YOUR_OPENAI_API_KEY
```
Replace `YOUR_OPENAI_API_KEY` with a valid OpenAI API key.

### 3️⃣ Build and Run the Application
```sh
mvn clean install
mvn spring-boot:run
```
The application will start at **http://localhost:8080**

---
## API Endpoints

### 🟢 Upload & Parse PDF
**Endpoint:**
```http
POST /api/pdf/parse
```
**Request:**
- `file` (Multipart File) → Upload a PDF file

**Response:**
```json
{
  "name": "Krishna Kumar",
  "email": "krishna.kumar@example.com",
  "opening_balance": 1000,
  "closing_balance": 1500
}
```

---
### 🔐 Secure PDF Parsing (Password Required)
**Endpoint:**
```http
POST /api/pdf/parse-secure
```
**Request:**
- `file` (Multipart File)
- `firstname` (String)
- `dob` (String, format: YYYY-MM-DD)

**Response:**
```json
{
  "name": "Krishna Kumar",
  "email": "krishna.kumar@example.com",
  "opening_balance": 1000,
  "closing_balance": 1500
}
```

---
### 📂 Parse PDF from Disk
**Endpoint:**
```http
GET /api/pdf/parse-from-disk
```
**Request:**
- `filename` (String) → Name of the PDF file stored in `src/main/resources/pdf/`

**Response:**
```json
{
  "name": "Krishna Kumar",
  "email": "krishna.kumar@example.com",
  "opening_balance": 1000,
  "closing_balance": 1500
}
```

---
## Possible Issues and Fixes

### ❌ `cannot find symbol log` in `LLMService`
✅ **Fix:** Add Lombok dependency in `pom.xml` and enable annotation processing in IDE.
```xml
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <version>1.18.30</version>
    <scope>provided</scope>
</dependency>
```

### ❌ `OpenAiService` not working
✅ **Fix:** Ensure `openai.api-key` is set in `application.yml`.

### ❌ `PDFTextStripper` fails to extract text
✅ **Fix:** Ensure the PDF is not encrypted or corrupt.

---
## Contributors
- **Shivam** - *Initial Development*

## License
This project is licensed under the MIT License.
