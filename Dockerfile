# Build stage from https://github.com/keeganwitt/docker-gradle/
FROM adoptopenjdk:11-jdk-openj9 as builder

CMD ["gradle"]

ENV GRADLE_HOME /opt/gradle

RUN set -o errexit -o nounset \
    && echo "Adding gradle user and group" \
    && groupadd --system --gid 1000 gradle \
    && useradd --system --gid gradle --uid 1000 --shell /bin/bash --create-home gradle \
    && mkdir /home/gradle/.gradle \
    && chown --recursive gradle:gradle /home/gradle \
    \
    && echo "Symlinking root Gradle cache to gradle Gradle cache" \
    && ln -s /home/gradle/.gradle /root/.gradle

VOLUME /home/gradle/.gradle

WORKDIR /home/gradle

RUN apt-get update \
    && apt-get install --yes --no-install-recommends \
        fontconfig \
        unzip \
        wget \
        \
        bzr \
        git \
        git-lfs \
        mercurial \
        openssh-client \
        subversion \
    && rm -rf /var/lib/apt/lists/*

ENV GRADLE_VERSION 6.2
ARG GRADLE_DOWNLOAD_SHA256=b93a5f30d01195ec201e240f029c8b42d59c24086b8d1864112c83558e23cf8a
RUN set -o errexit -o nounset \
    && echo "Downloading Gradle" \
    && wget --no-verbose --output-document=gradle.zip "https://services.gradle.org/distributions/gradle-${GRADLE_VERSION}-bin.zip" \
    \
    && echo "Checking download hash" \
    && echo "${GRADLE_DOWNLOAD_SHA256} *gradle.zip" | sha256sum --check - \
    \
    && echo "Installing Gradle" \
    && unzip gradle.zip \
    && rm gradle.zip \
    && mv "gradle-${GRADLE_VERSION}" "${GRADLE_HOME}/" \
    && ln --symbolic "${GRADLE_HOME}/bin/gradle" /usr/bin/gradle \
    \
    && echo "Testing Gradle installation" \
    && gradle --version

COPY buildSrc/ buildSrc/
COPY gradle/ gradle/
COPY build.gradle.kts build.gradle.kts
COPY gradle.properties gradle.properties
COPY gradlew gradlew
COPY settings.gradle.kts settings.gradle.kts
COPY src/ src/

RUN ["gradle", "shadowJar"]

FROM adoptopenjdk:11-jre-openj9

ENV VERTICLE_FILE protelis-on-web-all.jar
ENV VERTICLE_HOME /usr/verticles

EXPOSE 8080

COPY --from=builder $GRADLE_HOME/build/libs/$VERTICLE_FILE $VERTICLE_HOME/

WORKDIR $VERTICLE_HOME
ENTRYPOINT ["sh", "-c"]
CMD ["exec java -jar $VERTICLE_FILE"]
