FROM maven:3.9-eclipse-temurin-11 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -q
COPY src ./src
RUN mvn package -q

FROM eclipse-temurin:11-jre
WORKDIR /app
COPY --from=build /app/target/bank-client.jar bank-client.jar

# Necesario para que Swing funcione sobre X11 en Docker
ENV DISPLAY=:0

CMD ["java", "-jar", "bank-client.jar"]
