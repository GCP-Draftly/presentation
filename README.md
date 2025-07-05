**📦 Project Overview: Draftly AI Slide Generator**

**🌐 1. Project Description**

**Draftly** is an AI-powered slide generation platform that allows users to upload content files (TXT, DOCX, etc.), configure presentation settings, and automatically generate slide outlines using Google Gemini.

The project is divided into a frontend and backend, with full deployment on **Google Cloud Platform (GCP)**.

---

**🧱 2. Tech Stack**

**🔹 Frontend**

- **Framework**: React
- **Styling**: Tailwind CSS
- **Routing & State**: React Router, useState/useEffect
- **Features**:
    - File upload interface
    - Presentation configuration (title, purpose, slide count)
    - Outline preview page
- **Build Tool**: Vite or Create React App

**🔹 Backend**

- **Framework**: Spring Boot (Java 21)
- **REST API** with `@RestController`
- **Key Modules**:
    - `FileStorageUtil`: Saves uploaded files to server
    - `GeminiApiClient`: Sends prompt to Google Gemini API
    - `SlideParser`: Converts Gemini response to slide outlines
- **Dependencies**: Spring Web, Lombok, RestTemplate
- **Build Tool**: Gradle

---

**☁️ 3. Deployment Environment**

**☁️ Google Cloud Platform (GCP)**

- **Frontend Hosting**:
    - Cloud Storage (static website)
    - OR Firebase Hosting
- **Backend Hosting**:
    - Google Cloud Run (Dockerized Spring Boot)
    - OR Compute Engine (VM with JAR)
- **Secrets Management**: Secret Manager (for Gemini API Key)
- **Storage**: Cloud Storage bucket for uploaded files
- **Logging/Monitoring**: Cloud Logging (Stackdriver)
