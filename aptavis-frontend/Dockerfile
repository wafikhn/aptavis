# Stage 1: Build frontend with Maven & JDK 21
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# Install Dart Sass
RUN curl -L https://github.com/sass/dart-sass/releases/download/1.77.8/dart-sass-1.77.8-linux-x64.tar.gz | tar -xz -C /usr/local/bin --strip-components=1

COPY pom.xml .
COPY aptavis-backend/pom.xml aptavis-backend/
COPY aptavis-frontend/pom.xml aptavis-frontend/
COPY aptavis-backend/src aptavis-backend/src
COPY aptavis-frontend/src aptavis-frontend/src
COPY aptavis-frontend/frontend aptavis-frontend/frontend

RUN mvn clean package -pl aptavis-frontend -am -DskipTests

# Stage 2: Deploy to WildFly 32
FROM quay.io/wildfly/wildfly:32.0.1.Final-jdk21
COPY --from=build /app/aptavis-frontend/target/aptavis-frontend.war /opt/jboss/wildfly/standalone/deployments/
EXPOSE 8080
