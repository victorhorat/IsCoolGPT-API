# 📚 IsCoolGPT: Assistente Inteligente de Estudos em Cloud

Este repositório contém o backend do **IsCoolGPT**, um assistente inteligente de estudos em Cloud Computing, desenvolvido com arquitetura cloud moderna e seguindo boas práticas de DevOps.

O projeto utiliza um **Modelo de Linguagem Avançado (LLM)** externo para auxiliar estudantes em suas disciplinas.

-----

## 🛠️ Fase 1: Stack de Desenvolvimento e Containerização

A Fase 1 se concentrou no desenvolvimento da API e na preparação do ambiente para implantação.

### Backend & Tecnologias

| Área | Tecnologia | Propósito |
| :--- | :--- | :--- |
| **Linguagem** | Java 21 | Linguagem de desenvolvimento.|
| **Framework** | Spring Boot (com WebFlux) | Construção da API REST e comunicação assíncrona com o LLM. |
| **Monitoramento** | Spring Boot Actuator | Fornece *endpoints* de saúde (`/actuator/health`) essenciais para orquestração (ECS)198, |
| **Documentação** | Swagger/OpenAPI (Springdoc) |Documentação clara e interativa da API. |
| **Containerização** | Docker | Garante a portabilidade e a consistência da API em todos os ambientes. |

### Arquivos Chave da API

  * **`pom.xml`**: Contém todas as dependências do Spring Boot, Actuator e Swagger.
  * **`Dockerfile`**: Utiliza **multi-stage builds** para criar uma imagem final otimizada, garantindo menor tamanho e melhor performance.
  * **`application.properties`**: Configurações de ambiente, onde a chave de API do LLM (`llm.api.key`) é lida como uma variável de ambiente, seguindo a boa prática de não armazenar segredos no código.

-----

## 🚀 Como Executar a Aplicação (Docker)

Para executar o IsCoolGPT, você deve construir a imagem Docker e passá-la para o container, injetando a chave de API como uma variável de ambiente.

### 1\. Construir a Imagem

Este comando constrói a imagem Docker usando o `Dockerfile` otimizado:

```bash
docker build -t iscoolgpt:prerelease .
```

### 2\. Executar o Contêiner

Este comando inicia o contêiner na porta `8080`, injetando a chave de API do LLM (`LLM_API_KEY`) necessária para a aplicação funcionar.

```bash
docker run -p 8080:8080 -e LLM_API_KEY="CHAVE_DE_TESTE_AQUI" iscoolgpt:prerelease
```

### 3\. Testar o Health Check

Com o contêiner rodando, verifique se a aplicação está ativa via Actuator:

```bash
curl http://localhost:8080/actuator/health
```

**Resultado Esperado:** `{"status":"UP"}`

### 4\. Acessar a Documentação

Você pode explorar todos os *endpoints* através da interface interativa do Swagger:

```
http://localhost:8080/swagger-ui.html
```

-----