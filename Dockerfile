FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app

# Copy your project files into the Docker container
COPY . .

# Fix Windows line-endings and make the wrapper executable
RUN sed -i 's/\r$//' mvnw
RUN chmod +x mvnw

# Build the Spring Boot application
RUN ./mvnw clean package -DskipTests

# Expose the port your app runs on
EXPOSE 8081

# Run the built .jar file
CMD ["sh", "-c", "java -jar target/*.jar"]