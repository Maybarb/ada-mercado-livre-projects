# Guia de Testes — ms-comprovantes

## Pré-requisitos

Antes de testar, certifique-se de que os serviços estão rodando:

**Docker — Redis e RabbitMQ:**
```bash
docker run -d --name redis-comprovantes -p 6379:6379 redis:7-alpine
docker run -d --name rabbitmq-comprovantes -p 5672:5672 -p 15672:15672 rabbitmq:3-management-alpine
```

**Aplicação Spring Boot:**
```bash
mvn spring-boot:run
```

A aplicação sobe na porta `8080`.

---

## Endpoints

| Método | URL                             | Descrição                          |
|--------|---------------------------------|------------------------------------|
| POST   | `/comprovantes`                 | Cria comprovante e publica na fila |
| GET    | `/comprovantes/{id}`            | Busca comprovante por UUID         |

---

## 1. Criar comprovante — POST /comprovantes

A requisição é processada de forma **assíncrona**: o payload é publicado na fila RabbitMQ e o consumer persiste no banco em seguida.

### Via curl

```bash
curl -s -X POST http://localhost:8080/comprovantes \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "Maria Silva",
    "tipo_documento": "CPF",
    "numero_documento": "123.456.789-00",
    "numero_agencia": "0001",
    "numero_conta": "12345",
    "digito_verificador_conta": "6",
    "valor_transacao": 150.00,
    "tipo_chave_pix_destino": "EMAIL",
    "chave_pix_destino": "joao@email.com",
    "nome_cliente_destino": "João Santos",
    "identificacao_pix": "PIX-2024-001",
    "data_hora_transacao": "2026-07-06T18:00:00"
  }'
```

### Via Postman / Insomnia

- Método: `POST`
- URL: `http://localhost:8080/comprovantes`
- Body: `raw` → `JSON`
- Cole o JSON acima

### Resposta esperada — 202 Accepted

```json
{
  "id": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
  "data_hora_requisicao": "2026-07-06T18:00:00"
}
```

> Guarde o `id` retornado para usar no próximo passo.

### Valores válidos para os enums

| Campo                   | Valores aceitos                              |
|-------------------------|----------------------------------------------|
| `tipo_documento`        | `CPF`, `CNPJ`                                |
| `tipo_chave_pix_destino`| `CPF`, `CNPJ`, `EMAIL`, `CELULAR`, `CHAVE_ALEATORIA` |

---

## 2. Buscar comprovante — GET /comprovantes/{id}

Usa estratégia **cache-aside**:
1. Busca no Redis → se encontrar, retorna imediatamente (cache HIT)
2. Se não encontrar → busca no banco (cache MISS)
3. Se encontrou no banco → salva no Redis com TTL de 300s e retorna
4. Se não encontrou → aguarda e tenta mais 2 vezes (total de 3 tentativas)
5. Após 3 tentativas sem resultado → retorna `404 Not Found`

### Via curl

```bash
curl -s http://localhost:8080/comprovantes/3fa85f64-5717-4562-b3fc-2c963f66afa6
```

Substitua o UUID pelo `id` retornado no POST.

### Via Postman / Insomnia

- Método: `GET`
- URL: `http://localhost:8080/comprovantes/{id}`

### Resposta esperada — 200 OK

```json
{
  "id": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
  "nome": "Maria Silva",
  "tipo_documento": "CPF",
  "numero_documento": "123.456.789-00",
  "numero_agencia": "0001",
  "numero_conta": "12345",
  "digito_verificador_conta": "6",
  "valor_transacao": 150.00,
  "tipo_chave_pix_destino": "EMAIL",
  "chave_pix_destino": "joao@email.com",
  "nome_cliente_destino": "João Santos",
  "identificacao_pix": "PIX-2024-001",
  "data_hora_transacao": "2026-07-06T18:00:00",
  "data_hora_requisicao": "2026-07-06T18:00:00"
}
```

### Resposta esperada — 404 Not Found (ID inexistente)

```json
{
  "status": 404,
  "mensagem": "Comprovante com id '...' não encontrado após 3 tentativas."
}
```

---

## 3. Validação — erros no payload

Enviar um POST com campos obrigatórios faltando retorna `400 Bad Request`.

```bash
curl -s -X POST http://localhost:8080/comprovantes \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "Maria Silva"
  }'
```

Resposta esperada — 400 Bad Request com lista de erros de validação.

---

## Ferramentas de apoio

### Console H2 — visualizar dados no banco

Acesse no navegador:
```
http://localhost:8080/h2-console
```

| Campo    | Valor                       |
|----------|-----------------------------|
| JDBC URL | `jdbc:h2:mem:comprovantesdb`|
| User     | `sa`                        |
| Password | *(em branco)*               |

Após conectar, execute para ver os comprovantes salvos:
```sql
SELECT * FROM comprovante;
```

### RabbitMQ Management — visualizar filas

Acesse no navegador:
```
http://localhost:15672
```

| Campo  | Valor   |
|--------|---------|
| Login  | `guest` |
| Senha  | `guest` |

Na aba **Queues**, procure por `comprovantes.queue` para ver as mensagens publicadas e consumidas.

---

## Fluxo completo de teste

```
POST /comprovantes
      │
      ▼
 Publica na fila RabbitMQ (comprovantes.queue)
      │
      ▼
 Consumer recebe e persiste no banco H2
      │
      ▼
GET /comprovantes/{id}
      │
      ├── Cache HIT  → retorna do Redis
      └── Cache MISS → busca no H2 → salva no Redis → retorna
```
