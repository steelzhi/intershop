FROM gradle:8.13 AS BUILD
WORKDIR /usr/app/
COPY . .
RUN gradle build

# Package stage
FROM amazoncorretto:21
ENV JAR_NAME=practicum-0.0.1-SNAPSHOT.jar
ENV APP_HOME=/usr/app
WORKDIR $APP_HOME
COPY --from=BUILD $APP_HOME .
EXPOSE 8080
ENTRYPOINT exec java -jar $APP_HOME/build/libs/$JAR_NAME