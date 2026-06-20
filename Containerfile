# Step 1: Use a minimal Alpine Linux image with OpenJDK 17 pre-installed
FROM alpine:3.19

# Step 2: Install the headless Java 17 runtime environment for a smaller image size
RUN apk add --no-cache openjdk17-jre-headless

# Step 3: Set a secure working directory inside the container capsule
WORKDIR /app

# Step 4: Copy the compiled Spring Boot JAR from your local target directory into the container
COPY target/secure-hello-world-*.jar app.jar

# Step 5: Configure the container to run as a non-root user for security (Podman best practice)
RUN addgroup -S stackbill && adduser -S mithun -G stackbill \
    && chown -R mithun:stackbill /app
USER mithun

# Step 6: Define the execution command to launch your Java application when the container starts
ENTRYPOINT ["java", "-jar", "app.jar"]
