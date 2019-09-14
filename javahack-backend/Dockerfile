FROM gradle:jdk8 as builder

RUN mkdir sources
COPY --chown=gradle:gradle . /sources
WORKDIR /sources
RUN gradle fatJar

FROM openjdk:8-jre-alpine

ENV APPLICATION_USER ktor
RUN adduser -D -g '' $APPLICATION_USER

RUN mkdir /app
RUN chown -R $APPLICATION_USER /app

USER $APPLICATION_USER

COPY --from=builder /sources/build/libs/* /app
WORKDIR /app

CMD ["java", "-jar", "javahack-backend-full.jar"]
