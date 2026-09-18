# Atividade M1 — Integração de Sistemas

**Java Spring Fundamentals · Aula 4 · Integração com a API ViaCEP**

## Descrição da atividade

Crie um endpoint que integre com o serviço ViaCEP para verificar a disponibilidade de um produto em uma determinada cidade, recebendo como parâmetros o ID do produto e o CEP a ser consultado.

O endpoint deve consultar a ViaCEP para descobrir a cidade correspondente ao CEP informado e aplicar uma lógica para determinar se essa cidade é a mesma associada ao produto.

Esta atividade é cumulativa: além da integração com a ViaCEP, o projeto entregue deve manter funcionando tudo o que foi construído nas Aulas 1 a 3 (CRUD, validações, tratamento de exceções e os endpoints de consulta).

## Definition of Done

- Endpoint retornando corretamente a resposta esperada, testado via Postman.
- Projeto publicado no Git, incluindo a collection do Postman utilizada nos testes.
- Tudo o que foi desenvolvido nas Aulas 1 a 3 continua funcionando normalmente.

## Requisitos obrigatórios — Aulas 1 a 3

Antes de entregar, confira se o projeto ainda atende ao que foi construído nas aulas anteriores:

- Projeto Spring Boot configurado com PostgreSQL e migrations Flyway.
- CRUD completo de produtos: criar, listar, atualizar e inativar.
- Validação dos dados de entrada e tratamento de exceções (ex.: erro ao buscar um produto inexistente).
- Uso correto de Path Variable, Request Param, Request Header e Request Body.
- Endpoints de consulta/filtro: por categoria, por id e top 5 produtos por preço.

## Passo a passo — Aula 4

**1. Banco de dados**

Crie um script SQL, interpretado pelo Flyway, que adicione o campo `distribution_center` na tabela `product`. Defina manualmente o valor desse campo para os produtos já existentes, distribuindo-os de forma aleatória entre 3 valores possíveis: Mogi das Cruzes, Recife e Porto Alegre.

Exemplo:

```sql
SET distribution_center = 'Mogi das Cruzes'
WHERE id IN ('p1', 'p2', 'p3');
```

**2. Entidade**

Refatore as entidades necessárias para acomodar o novo campo `distribution_center`, garantindo que ele esteja corretamente mapeado na classe de modelo.

**3. Serviço de integração**

Crie uma classe de serviço (`@Service`) responsável pela integração com a ViaCEP, através de uma URL no formato:

```
https://viacep.com.br/ws/{cep}/json/
ex: https://viacep.com.br/ws/08773380/json/
```

**4. Endpoint**

Crie um novo endpoint (método na controller) que receba o CEP e o ID do produto como parâmetros.

**5. Endpoint chama o serviço**

Faça o endpoint chamar a classe de serviço criada no passo 3.

**6. Lógica de comparação**

Implemente, dentro do serviço, a lógica que retorna um valor booleano, comparando a cidade obtida a partir do CEP com o `distribution_center` do produto. Caso sejam iguais, o retorno deve ser `true`; caso contrário, `false`.

**7. Postman**

Monte a chamada no Postman e salve a collection utilizada para testar o endpoint.

**8. Resiliência na integração**

O serviço da ViaCEP pode falhar: CEP mal formatado, CEP inexistente, ou o serviço indisponível no momento da chamada. Faça o endpoint se comportar de forma adequada nesses cenários, sem quebrar a aplicação.

## Critérios de avaliação

Nota de 0 a 10, cobrindo tanto os fundamentos das Aulas 1 a 3 quanto a integração da Aula 4:

### Fundamentos (Aulas 1 a 3)

| Critério | Peso |
|---|---|
| Projeto configurado corretamente (Spring Boot + PostgreSQL + Flyway sobem sem ajustes manuais) | 0,20 |
| CRUD completo de produtos funcionando (criar, listar, atualizar, inativar) | 0,20 |
| Validação dos dados de entrada e tratamento de exceções implementados | 0,20 |
| Uso correto de Path Variable, Request Param, Request Header e Request Body | 0,20 |
| Endpoints de consulta/filtro (por categoria, por id, top 5 por preço) funcionando | 0,20 |

### Integração com a ViaCEP (Aula 4)

| Critério | Peso |
|---|---|
| Entidade Product refatorada, com os novos campos devidamente mapeados | 1,5 |
| Serviço integrando corretamente com a ViaCEP (tratamento de exceções, corner cases, etc.) | 3,0 |
| Endpoint novo, com a controller devidamente mapeada e utilizando os recursos corretos | 1,5 |
| Lógica de comparação (cidade × centro de distribuição) | 1,5 |
| Resiliência: tolerância a falhas externas da API | 1,5 |

**Total: 10,0**

*Pontos são deduzidos por código ilegível, desorganizado, sem padronização, commits sem sentido ou ausência do projeto no Git, etc.*

## Entrega

- Prazo: **[DEFINIR DATA]**
- Link do repositório Git com o código-fonte
- Collection do Postman exportada e incluída no repositório
