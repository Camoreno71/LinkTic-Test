FROM azul/zulu-openjdk:17

WORKDIR /app

COPY target/products-service-0.0.1-SNAPSHOT.jar /app/products-service-0.0.1-SNAPSHOT.jar

EXPOSE 8080

CMD [ "java", "-jar", "products-service-0.0.1-SNAPSHOT.jar" ]