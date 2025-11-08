# ------------------------------------
# STAGE 1: BUILD - Compilação do Projeto Java
# ------------------------------------
# Usa uma imagem Maven com Java 21 para compilar o código
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build

# Define o diretório de trabalho dentro do container
WORKDIR /app

# 1. Copia o pom.xml e faz o download das dependências. 
#    Isso otimiza o cache do Docker: se o pom.xml não mudar, o Docker não re-executa este passo.
COPY pom.xml .
RUN mvn dependency:go-offline -B

# 2. Copia o código-fonte
COPY src ./src

# 3. Empacota a aplicação em um arquivo JAR (o -DskipTests é opcional se você for rodar testes aqui,
#    mas é comum pular testes longos no build e rodar no CI/CD)
RUN mvn package -DskipTests

# ------------------------------------
# STAGE 2: RUNTIME - Execução da Aplicação
# ------------------------------------
# Usa uma imagem minimalista apenas com o Java Runtime Environment (JRE) 
# para a menor imagem final possível (Multi-stage build)
FROM eclipse-temurin:21-jre-alpine

# Define o diretório de trabalho
WORKDIR /app

# Expõe a porta que o Spring Boot usa
EXPOSE 8080

# Copia o JAR compilado da Stage 1 (Build) para a Stage 2 (Runtime)
COPY --from=build /app/target/*.jar app.jar

# Define o usuário que vai rodar o container por segurança (Boa Prática de DevOps)
USER 1000

# Comando principal para rodar a aplicação Spring Boot
ENTRYPOINT ["java", "-jar", "app.jar"]