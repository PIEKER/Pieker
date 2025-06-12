# Step 1: Build frontend
FROM node:18 AS frontend-build
WORKDIR /app/frontend
COPY app/package*.json ./
RUN npm install
COPY app/ .
RUN npm run build

# Step 2: Build backend with Gradle
FROM eclipse-temurin:24-jdk-alpine AS backend-build

# Install bash, curl, unzip, and openjdk-jdk for Gradle installation & build
RUN apk add --no-cache bash curl unzip

WORKDIR /app/backend

# Install Gradle (change version if needed)
ENV GRADLE_VERSION=8.14
RUN curl -sLo gradle.zip https://services.gradle.org/distributions/gradle-${GRADLE_VERSION}-bin.zip && \
    unzip gradle.zip && \
    rm gradle.zip && \
    mv gradle-${GRADLE_VERSION} /opt/gradle

ENV PATH="/opt/gradle/bin:${PATH}"

COPY server/ .
RUN gradle clean build -x test

# Step 3: Final image
FROM eclipse-temurin:24-jre-alpine
WORKDIR /app

# Copy the built JAR
COPY --from=backend-build /app/backend/build/libs/*.jar app.jar

# Copy frontend build into public folder
COPY --from=frontend-build /app/frontend/dist /app/public

# Expose port and run the app
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]