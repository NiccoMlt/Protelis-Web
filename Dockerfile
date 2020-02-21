FROM adoptopenjdk:11-jre-openj9

ENV VERTICLE_FILE protelis-on-web-all.jar
ENV VERTICLE_HOME /usr/verticles

EXPOSE 8080

COPY build/libs/$VERTICLE_FILE $VERTICLE_HOME/

WORKDIR $VERTICLE_HOME
ENTRYPOINT ["sh", "-c"]
CMD ["exec java -jar $VERTICLE_FILE"]
