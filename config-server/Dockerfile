FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY config-server/pom.xml config-server/pom.xml
COPY config-server/src config-server/src
RUN mvn -f config-server/pom.xml clean package -DskipTests

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/config-server/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]