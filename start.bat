@echo off

REM Start the Spring Boot application
start mvnw spring-boot:run

REM Start the Flask application
start python src\python\Flask.py