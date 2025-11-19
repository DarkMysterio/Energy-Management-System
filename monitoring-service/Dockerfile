FROM maven:3.9 AS build

ENV TZ=UTC
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
ENV CATALINA_OPTS="-Duser.timezone=UTC"

COPY ./pom.xml /demo/pom.xml
WORKDIR /demo
RUN mvn dependency:go-offline
COPY ./src/ /demo/src
WORKDIR /demo
RUN mvn clean install -DskipTests

FROM eclipse-temurin:21-jdk
WORKDIR /monitoring-service
COPY --from=build /demo/target/monitoring-service-0.0.1-SNAPSHOT.jar /monitoring-service/demo.jar
EXPOSE 8083
CMD ["java", "-jar", "demo.jar"]
