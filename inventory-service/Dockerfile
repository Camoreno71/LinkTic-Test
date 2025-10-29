FROM azul/zulu-openjdk:17

WORKDIR /app

COPY target/inventory-service-0.0.1-SNAPSHOT.jar /app/inventory-service-0.0.1-SNAPSHOT.jar

EXPOSE 8080

CMD [ "java", "-jar", "inventory-service-0.0.1-SNAPSHOT.jar" ]