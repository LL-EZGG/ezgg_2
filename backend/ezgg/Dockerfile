FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY . .

RUN ./gradlew bootJar --no-daemon

RUN cp build/libs/app.jar /app.jar

COPY wait-for-it.sh /wait-for-it.sh
RUN chmod +x /wait-for-it.sh

CMD ["java", "-jar", "/app.jar", "-web", "-webAllowOthers", "-tcp", "-tcpAllowOthers", "-browser"]

