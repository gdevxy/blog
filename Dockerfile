####
# Multi-stage Dockerfile for Railway deployment with Quarkus native image
# Uses Mandrel (GraalVM) JDK 25 for native compilation
####

FROM quay.io/quarkus/ubi-quarkus-mandrel-builder-image:jdk-25 AS build

USER root
WORKDIR /build

COPY --chown=quarkus:quarkus mvnw /build/mvnw
COPY --chown=quarkus:quarkus .mvn /build/.mvn
COPY --chown=quarkus:quarkus pom.xml /build/
COPY --chown=quarkus:quarkus model/pom.xml /build/model/
COPY --chown=quarkus:quarkus client/pom.xml /build/client/
COPY --chown=quarkus:quarkus service/pom.xml /build/service/
COPY --chown=quarkus:quarkus component/pom.xml /build/component/

RUN chown -R quarkus:quarkus /build
USER quarkus

RUN ./mvnw -B dependency:go-offline

COPY --chown=quarkus:quarkus model/src /build/model/src
COPY --chown=quarkus:quarkus client/src /build/client/src
COPY --chown=quarkus:quarkus service/src /build/service/src
COPY --chown=quarkus:quarkus component/src /build/component/src

RUN ./mvnw clean package -Dnative -DskipTests \
    -Dquarkus.http.host=0.0.0.0 \
    -Dquarkus.native.container-build=false \
    -Dquarkus.native.builder-image=mandrel

FROM registry.access.redhat.com/ubi9/ubi-minimal:latest

WORKDIR /work/

RUN chown 1001 /work \
    && chmod "g+rwX" /work \
    && chown 1001:root /work

COPY --from=build --chown=1001:root /build/component/target/*-runner /work/application

EXPOSE 8080

USER 1001

ENV QUARKUS_HTTP_HOST=0.0.0.0

CMD ["sh", "-c", "./application -Dquarkus.http.port=${PORT:-8080}"]
