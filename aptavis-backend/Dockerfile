# Stage 1: Build backend with Maven & JDK 21
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

COPY pom.xml .
COPY aptavis-backend/pom.xml aptavis-backend/
COPY aptavis-frontend/pom.xml aptavis-frontend/
COPY aptavis-backend/src aptavis-backend/src

RUN mvn clean package -pl aptavis-backend -am -DskipTests

# Stage 2: Deploy to WildFly 32
FROM quay.io/wildfly/wildfly:32.0.1.Final-jdk21
COPY --from=build /app/aptavis-backend/target/aptavis-backend.war /opt/jboss/wildfly/standalone/deployments/ROOT.war
RUN touch /opt/jboss/wildfly/standalone/deployments/ROOT.war.dodeploy
EXPOSE 8080
