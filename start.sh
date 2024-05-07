#!/bin/sh

# Start the Spring Boot application
./mvnw spring-boot:run &

# Start the Flask application
python3 src/python/Flask.py &