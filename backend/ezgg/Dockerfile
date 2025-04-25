FROM eclipse-temurin:21-jdk

WORKDIR /app

# Gradle Wrapper 및 설정 파일만 먼저 복사
COPY gradlew .
COPY gradle gradle
COPY build.gradle .

# 의존성 미리 다운 (이 단계는 캐시로 재사용 가능)
RUN ./gradlew dependencies --no-daemon || return 0

COPY . .

RUN ./gradlew bootJar --no-daemon

RUN cp build/libs/app.jar /app.jar

COPY wait-for-it.sh /wait-for-it.sh
RUN chmod +x /wait-for-it.sh

CMD ["java", "-jar", "/app.jar", "-web", "-webAllowOthers", "-tcp", "-tcpAllowOthers", "-browser"]

