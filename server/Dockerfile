FROM maven:3.9-eclipse-temurin-11 AS build
WORKDIR /app
COPY pom.xml .
# Descargar dependencias primero (cache layer)
RUN mvn dependency:go-offline -q
COPY src ./src
RUN mvn package -q

FROM eclipse-temurin:11-jre
WORKDIR /app
COPY --from=build /app/target/bank-server.jar bank-server.jar
EXPOSE 1099
CMD ["java", "-jar", "bank-server.jar"]
