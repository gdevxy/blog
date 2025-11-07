## Pre-requisit

GraalVM JDK must be installed. Any env manager do fine, I personally tend to rely on [sdkman](https://sdkman.io/).

```
sdk install java 25-graalce
```

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:

```shell script
mvn compile quarkus:dev
```

## Packaging and running the application

```shell script
mvn package -Dquarkus.package.jar.type=uber-jar
```

The application, packaged as an _über-jar_, is now runnable using `java -jar target/*-runner.jar`.

## Creating a native executable

You can create a native executable using:

```shell script
mvn package -Dnative
```

Publish native image to a container registry

```shell script
mvn clean package -Dnative -DskipTests \
    -Dquarkus.http.host=0.0.0.0 \
    -Dquarkus.container-image.build=true \
    -Dquarkus.container-image.push=true \
    -Dquarkus.container-image.group={group} \
    -Dquarkus.container-image.name={name} \
    -Dquarkus.container-image.tag={version} \
    -Dquarkus.container-image.registry={registry} \
    -Dquarkus.docker.dockerfile-jvm-path=src/main/docker/Dockerfile.native \
    -Dquarkus.docker.buildx.platform=linux/amd64 \
    -Dquarkus.native.builder-image=graalvm \
    -Dquarkus.native.container-build=true
```
