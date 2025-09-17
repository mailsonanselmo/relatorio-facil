# Build
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /relatorio-facil
COPY pom.xml .
RUN mvn -B -q -e -DskipTests dependency:go-offline || true
COPY src ./src
RUN mvn -B -DskipTests clean package

# Runtime
FROM eclipse-temurin:17-jre
ENV JAVA_TOOL_OPTIONS="-Dfile.encoding=UTF-8 -Duser.timezone=America/Fortaleza"
WORKDIR /app
COPY --from=build /relatorio-facil/target/relatorio-facil-0.0.1-SNAPSHOT.jar /app/app.jar
RUN mkdir -p /app/scripts /app/output
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]