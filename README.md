# Carbon Tracker AI

![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=java&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-316192?style=for-the-badge&logo=postgresql&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white)
![Google Gemini](https://img.shields.io/badge/Google_Gemini-8E75B2?style=for-the-badge&logo=google&logoColor=white)
![Security](https://img.shields.io/badge/Security-JWT-black?style=for-the-badge)

Carbon Tracker AI is  web application that helps users monitor their daily activities, calculate their environmental impact, and receive personalized advice from Google's AI on how to live more sustainably.

I built this project to demonstrate how to successfully connect a user-friendly web interface with a secure, enterprise-grade database and third-party Artificial Intelligence.

---

## What It Does

* **Secure User Accounts:** Users can safely create accounts and log in. Passwords are mathematically encrypted before being saved, and active sessions are protected using industry-standard security tokens (JWT) to keep data safe.
* **Activity Tracking:** Users can log daily habits—like miles driven, electricity used, or diet choices. The system instantly calculates the exact carbon footprint of those actions.
* **AI Eco-Coach:** The application connects directly to Google's Gemini Artificial Intelligence. The AI reads the user's specific dashboard data and acts as a personal coach, generating customized, realistic tips to help them reduce their carbon footprint.
* **Seamless Web Experience:** The frontend is built as a Single Page Application (SPA). This means the website updates data and switches screens instantly without ever forcing the user to reload the web browser.

---

## How It Works Under the Hood

To make this application scalable and secure, I separated the code into three distinct layers:

1. **Frontend:** Built with pure JavaScript, HTML, and CSS. It communicates securely with the server behind the scenes to fetch data and display it in a clean, modern dashboard.
2. **Backend API:** Built with Java and Spring Boot. It rejects bad or malicious data before it enters the system, performing the footprint math, and orchestrating the requests to Google's AI.
3. **Database:** A PostgreSQL relational database. It is housed inside a Docker container, ensuring the data storage environment is reliable, secure, and isolated from the rest of the computer.

---

## How to Run the Project 

If you would like to run this code on your own machine, follow these steps:

**Prerequisites:** Java 17+, Springboot, Maven, Docker, and a free Google AI Studio API Key.

1. **Clone the code to your machine:**
   ```bash
   git clone [https://github.com/YourUsername/CarbonTrackerAI.git](https://github.com/YourUsername/CarbonTrackerAI.git)
   
2. **Start the isolated Database:**
   ```bash
   docker run --name carbon-db -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=password -e POSTGRES_DB=carbon_tracker -p 5432:5432 -d postgres

3. **Add your AI Secret Key:**
   Create a .env file in the project folder and add your key so the app can talk to Google:
   ```bash
   Geminiapikey=your_secret_api_key_here

4. **Start the Server:**
   ```bash
   mvn spring-boot:run

5. **Open the App:**
   Open the index.html file in any web browser to start tracking!