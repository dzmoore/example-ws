FROM eclipse-temurin:17-jre AS builder

WORKDIR /app

ADD dependencies/ ./
ADD spring-boot-loader/ ./
ADD snapshot-dependencies/ ./
ADD application/ ./

ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]