# API de Gestão de Empreendimentos

API REST em Java/Spring Boot para gerenciamento de empreendimentos, permitindo o cadastro, atualização, remoção e listagem paginada de empreendimentos, com filtros opcionais por status, segmento e município.

Este projeto foi desenvolvido seguindo a arquitetura MVC em camadas (controller, service, repository, domain), com uso de DTOs, mapeadores e tratamento centralizado de erros. A aplicação expõe endpoints REST documentados via Swagger/OpenAPI e utiliza banco de dados H2 em memória para ambiente de desenvolvimento.

---

## Sumário

- [Requisitos](#requisitos)
- [Tecnologias utilizadas](#tecnologias-utilizadas)
- [Como executar](#como-executar)
  - [Executar com Maven](#executar-com-maven)
  - [Executar com Docker](#executar-com-docker)
  - [Executar via IDE](#executar-via-ide)
- [Endpoints principais](#endpoints-principais)
- [Modelo de dados e enums](#modelo-de-dados-e-enums)
- [Paginação e ordenação](#paginação-e-ordenação)
- [Banco de dados](#banco-de-dados)
- [Testes](#testes)
- [Documentação da API](#documentação-da-api)
- [Vídeo](#pitch-em-vídeo)

---

## Requisitos

- Java compatível com Spring Boot 4.0.3 (recomendado: **Java 21** ou superior)
- Maven (ou uso do Maven Wrapper `mvnw`/`mvnw.cmd` inclusos no projeto)
- Opcional: Docker e Docker Compose para rodar sem configurar Java/Maven localmente
- IDE recomendada: IntelliJ IDEA, Eclipse, VS Code com extensões Java, etc.

---

## Tecnologias utilizadas

**Backend e Frameworks**
- Java
- Spring Boot 4.0.3
- Spring Web (API REST)
- Spring Data JPA (persistência e paginação)
- Springdoc OpenAPI (Swagger UI para documentação da API)

**Banco de dados**
- H2 Database em memória

**Build e automação**
- Maven

**Conteinerização**
- Docker
- Docker Compose

**Testes**
- JUnit (testes unitários)

---

## Como executar

### Executar com Maven

No Windows PowerShell:

```powershell
cd C:\Users\thiago\Downloads\empreendimentos\empreendimentos
.\mvnw.cmd spring-boot:run
```

Ou, se você tiver o Maven instalado globalmente:

```powershell
cd C:\Users\thiago\Downloads\empreendimentos\empreendimentos
mvn spring-boot:run
```

Após o start, a API ficará disponível em: `http://localhost:8080`.

### Executar com Docker

#### Usando apenas Docker

Na raiz do projeto (onde está o `Dockerfile`):

```powershell
cd C:\Users\thiago\Downloads\empreendimentos\empreendimentos
docker build -t empreendimentos-api .
docker run --name empreendimentos-api -p 8080:8080 empreendimentos-api
```

A API ficará disponível em `http://localhost:8080`.

#### Usando Docker Compose (recomendado)

Na raiz do projeto (onde está o `docker-compose.yml`):

```powershell
cd C:\Users\thiago\Downloads\empreendimentos\empreendimentos
docker compose up --build
```

Para parar os containers:

```powershell
docker compose down
```

### Executar via IDE

1. Importe o projeto como **Projeto Maven** na sua IDE (IntelliJ, Eclipse, VS Code com extensão Java, etc.).
2. Certifique-se de que o SDK Java configurado na IDE é compatível (Java 21 recomendado).
3. Localize a classe `EmpreendimentosApplication` no pacote `com.sctec.empreendimentos`.
4. Execute a aplicação como **Spring Boot Application** / **Java Application**.
5. A aplicação iniciará na porta `8080` por padrão (`http://localhost:8080`).

---

## Endpoints principais

Todos os endpoints estão sob o prefixo `/api`.

- `GET /api/empreendimentos`  
  Lista empreendimentos com paginação e filtros opcionais:
  - `status` (enum `Status`)
  - `segmento` (enum `Segmento`)
  - `municipio` (trecho do nome do município, case-insensitive)

- `GET /api/empreendimentos/{id}`  
  Retorna os dados de um empreendimento específico.

- `POST /api/empreendimentos`  
  Cria um novo empreendimento.

- `PUT /api/empreendimentos/{id}`  
  Atualiza um empreendimento existente.

- `DELETE /api/empreendimentos/{id}`  
  Remove um empreendimento (deleção física, conforme implementação atual).

---

## Modelo de dados e enums

### Exemplo de JSON para criação de empreendimento

```json
{
  "nomeEmpreendimento": "Residencial Vista Alegre",
  "nomeEmpreendedorResponsavel": "João da Silva",
  "municipio": "Florianópolis",
  "segmento": "RESIDENCIAL",
  "contato": "email@exemplo.com",
  "status": "ATIVO"
}
```

> Observação: os valores aceitos para `segmento` e `status` seguem os enums definidos no código:
>
> - `Segmento`: `TECNOLOGIA`, `COMERCIO`, `INDUSTRIA`, `SERVICOS`, `AGRONEGOCIO`
> - `Status`: `ATIVO`, `INATIVO`

---

## Paginação e ordenação

O endpoint `GET /api/empreendimentos` utiliza paginação padrão do Spring Data:

- Parâmetros padrão:
  - `page` (padrão = 0)
  - `size` (padrão = 5)

A ordenação é fixa por `id` ascendente na API, independentemente dos parâmetros de sort enviados pelo cliente (isso evita erros de ordenação gerados automaticamente pelo Swagger).

---

## Banco de dados

- Banco **H2 em memória**, configurado em `src/main/resources/application.properties`.
- Console do H2 habilitado para desenvolvimento em `http://localhost:8080/h2-console`.
- Os dados são recriados a cada inicialização da aplicação (scripts como `data.sql` podem povoar dados iniciais).

---

## Testes

Os testes automatizados são responsáveis por validar as regras de negócio, o comportamento dos serviços, dos controllers e dos mapeamentos entre entidades e DTOs.

Para executar todos os testes via Maven:

```powershell
cd C:\Users\thiag\Downloads\empreendimentos\empreendimentos
mvn test
```

Exemplos de cenários cobertos (podem variar conforme evolução do projeto):

- Criação de empreendimento com status padrão **ATIVO** quando não informado;
- Filtro de empreendimentos por status, segmento e município na camada de serviço;
- Tratamento de erro quando um empreendimento não é encontrado pelo ID;
- Mapeamento correto entre `Empreendimento` e `EmpreendimentoResponseDTO`.

---

## Documentação da API

Após subir a aplicação, acesse a documentação interativa (Swagger UI):

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI (JSON): `http://localhost:8080/v3/api-docs`

A documentação é gerada automaticamente a partir das anotações da API, facilitando o entendimento dos endpoints, modelos de dados e códigos de resposta.

---

## Vídeo

- **Link do vídeo:** https://youtu.be/NKtIqJvKCx0
