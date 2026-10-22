FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY discovery-server/pom.xml discovery-server/pom.xml
COPY discovery-server/src discovery-server/src
RUN mvn -f discovery-server/pom.xml clean package -DskipTests

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/discovery-server/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]