FROM gradle:jdk17-alpine AS build

WORKDIR /app

COPY . .

RUN gradle build

FROM openjdk:17

WORKDIR /app

COPY --from=build /app/build/libs/*.jar ./application.jar

CMD ["java", "-jar", "./application.jar"]


#FROM gradle:7.4.2-jdk11 AS build
#
#WORKDIR /app
#
#COPY . .
#
## Переходим обратно на пользователя gradle
#USER gradle
#
#RUN gradle clean
#
#RUN gradle build --no-daemon --info
#
#FROM openjdk:11-jre-slim
#
#WORKDIR /app
#
#COPY --from=build /app/build/libs/*.jar ./application.jar
#
#CMD ["java", "-jar", "./application.jar"]
