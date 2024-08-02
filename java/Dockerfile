FROM docker.io/{ORGANIZATION}/maven:3.8.7 AS builder

ARG APP_NAME

ARG MAVEN_USERNAME
ARG MAVEN_PASSWORD

COPY settings.xml /opt/maven/conf/settings.xml
COPY pom.xml .
RUN mvn -ntp dependency:go-offline

COPY src src
RUN mvn -ntp package -Dskiptests -DskipFormatCode=true

FROM docker.io/{ORGANIZATION}/javajdk:temurin-19 AS layers

COPY --from=builder target/$APP_NAME.jar .
RUN java -Djarmode=layertools -jar $APP_NAME.jar extract

FROM docker.io/{ORGANIZATION}/javajdk:temurin-19

ENV CONTRAST_AGENT_VERSION=4.4.0
ENV BINARY_URL="https://repo1.maven.org/maven2/com/contrastsecurity/contrast-agent/$CONTRAST_AGENT_VERSION/contrast-agent-$CONTRAST_AGENT_VERSION.jar"
ENV CONTRAST_DIR="/opt/contrast"
ENV CONTRAST_AGENT_PATH="$CONTRAST_DIR/contrast.jar"

RUN mkdir "$CONTRAST_DIR" && \
    curl "$BINARY_URL" -o "$CONTRAST_AGENT_PATH" && \
    chmod 644 "$CONTRAST_AGENT_PATH"

COPY --from=layers dependencies/ .
COPY --from=layers snapshot-dependencies/ .
COPY --from=layers spring-boot-loader/ .
COPY --from=layers application/ .

COPY --chmod=755 ./devops/entrypoint.sh .

RUN chmod 775 -R /BOOT-INF/classes/keystore

ENTRYPOINT ["./entrypoint.sh"]
