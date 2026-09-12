# integration-viacep

API REST em Java + Spring Boot para cadastro de produtos, com um endpoint de
integração com o serviço [ViaCEP](https://viacep.com.br/) para verificar a
disponibilidade de um produto em determinada cidade a partir de um CEP.

## Stack

- Java 17
- Spring Boot 3 (Web, Data JPA, Validation)
- PostgreSQL
- Flyway (migrations)
- Maven

## Como rodar

1. Suba o banco com Docker:

   ```
   docker-compose up -d
   ```

   Isso cria um Postgres em `localhost:5432` (banco `product`, usuário
   `postgres`, senha `password`) e um pgAdmin em `localhost:15432`.

2. Confira se `src/main/resources/application.properties` aponta para o
   mesmo banco/usuário/senha do `docker-compose.yml`.

3. Rode a aplicação:

   ```
   ./mvnw spring-boot:run
   ```

   O Flyway aplica as migrations automaticamente na subida. A API fica
   disponível em `http://localhost:8080`.

## Endpoints principais

| Método | Rota                              | Descrição                                              |
|--------|------------------------------------|---------------------------------------------------------|
| GET    | `/product`                         | Lista todos os produtos ativos                          |
| POST   | `/product`                         | Cadastra um novo produto                                 |
| PUT    | `/product`                         | Atualiza nome/preço de um produto                        |
| DELETE | `/product/{id}`                    | Inativa um produto (soft delete)                         |
| GET    | `/product/availability/{id}`       | Verifica se o CEP informado pertence ao `distribution_center` do produto |
| GET    | `/product/endpoint1`               | Lista produtos por categoria (`categoryAsParam`)          |
| GET    | `/product/endpoint2/{id}`          | Busca um produto por id                                   |
| GET    | `/product/endpoint3/top5byprice`   | Top 5 produtos por preço                                   |
| GET    | `/product/category/{categoryAsPath}` | Exemplo combinando Path, Param, Header e Body            |
| GET    | `/product/cep`                     | Busca CEP a partir de estado/cidade/rua (uso didático à parte)       |

## Estrutura do banco

As migrations ficam em `src/main/resources/db/migration` e são versionadas
pelo Flyway (`V1` a `V5`). A tabela `product` tem, entre outros, o campo
`distribution_center`, usado pelo endpoint de disponibilidade para comparar
com a cidade retornada pela ViaCEP.
