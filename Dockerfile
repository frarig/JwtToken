FROM gradle:8.2.1-jdk17-alpine AS GRADLE

COPY . /app
WORKDIR /app

RUN gradle clean build

FROM openjdk:17-alpine

COPY --from=GRADLE /app/build/libs/JWT_example-1.0.0.jar /app/JWT_example-1.0.0.jar

CMD ["java", "-jar", "/app/JWT_example-1.0.0.jar"]