FROM openjdk:11
ENV WP /usr/src/app
COPY *.jar $WP/app.jar
WORKDIR $WP
EXPOSE 8802
CMD ["java", "-jar", "app.jar"]