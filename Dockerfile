# --- Stage 1: Build ---
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build
WORKDIR /app

# Copia apenas o pom.xml para cache das dependências
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copia o código-fonte e realiza o build
COPY src ./src
RUN mvn clean package -DskipTests

# --- Stage 2: Runtime ---
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Boas práticas de segurança: cria um usuário sem privilégios root
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Copia o artefato gerado do Stage de Build
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

# Gerenciamento dinâmico de memória para containers e execução direta sem 'sh -c'
ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]