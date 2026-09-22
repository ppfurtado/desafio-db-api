# 🗳️ Desafio Votação - API REST

## Inicio instruções
 
API REST desenvolvida em **Java 17** e **Spring Boot 3** para gerenciar pautas, sessões de votação e computar votos de associados em assembleias de cooperativas.
# Votação

## Objetivo

No cooperativismo, cada associado possui um voto e as decisões são tomadas em assembleias, por votação. Imagine que você deve criar uma solução para dispositivos móveis para gerenciar e participar dessas sessões de votação.
Essa solução deve ser executada na nuvem e promover as seguintes funcionalidades através de uma API REST:

- Cadastrar uma nova pauta
- Abrir uma sessão de votação em uma pauta (a sessão de votação deve ficar aberta por
  um tempo determinado na chamada de abertura ou 1 minuto por default)
- Receber votos dos associados em pautas (os votos são apenas 'Sim'/'Não'. Cada associado
  é identificado por um id único e pode votar apenas uma vez por pauta)
- Contabilizar os votos e dar o resultado da votação na pauta

Para fins de exercício, a segurança das interfaces pode ser abstraída e qualquer chamada para as interfaces pode ser considerada como autorizada. A solução deve ser construída em java, usando Spring-boot, mas os frameworks e bibliotecas são de livre escolha (desde que não infrinja direitos de uso).

É importante que as pautas e os votos sejam persistidos e que não sejam perdidos com o restart da aplicação.

O foco dessa avaliação é a comunicação entre o backend e o aplicativo mobile. Essa comunicação é feita através de mensagens no formato JSON, onde essas mensagens serão interpretadas pelo cliente para montar as telas onde o usuário vai interagir com o sistema. A aplicação cliente não faz parte da avaliação, apenas os componentes do servidor. O formato padrão dessas mensagens será detalhado no anexo 1.

## Como proceder

Por favor, **CLONE** o repositório e implemente sua solução, ao final, notifique a conclusão e envie o link do seu repositório clonado no GitHub, para que possamos analisar o código implementado.

Lembre de deixar todas as orientações necessárias para executar o seu código.

### Tarefas bônus

- Tarefa Bônus 1 - Integração com sistemas externos
    - Criar uma Facade/Client Fake que retorna aleátoriamente se um CPF recebido é válido ou não.
    - Caso o CPF seja inválido, a API retornará o HTTP Status 404 (Not found). Você pode usar geradores de CPF para gerar CPFs válidos
    - Caso o CPF seja válido, a API retornará se o usuário pode (ABLE_TO_VOTE) ou não pode (UNABLE_TO_VOTE) executar a operação. Essa operação retorna resultados aleatórios, portanto um mesmo CPF pode funcionar em um teste e não funcionar no outro.

```
// CPF Ok para votar
{
    "status": "ABLE_TO_VOTE
}
// CPF Nao Ok para votar - retornar 404 no client tb
{
    "status": "UNABLE_TO_VOTE
}
```

Exemplos de retorno do serviço

### Tarefa Bônus 2 - Performance

- Imagine que sua aplicação possa ser usada em cenários que existam centenas de
  milhares de votos. Ela deve se comportar de maneira performática nesses
  cenários
- Testes de performance são uma boa maneira de garantir e observar como sua
  aplicação se comporta

### Tarefa Bônus 3 - Versionamento da API

○ Como você versionaria a API da sua aplicação? Que estratégia usar?

## O que será analisado

- Simplicidade no design da solução (evitar over engineering)
- Organização do código
- Arquitetura do projeto
- Boas práticas de programação (manutenibilidade, legibilidade etc)
- Possíveis bugs
- Tratamento de erros e exceções
- Explicação breve do porquê das escolhas tomadas durante o desenvolvimento da solução
- Uso de testes automatizados e ferramentas de qualidade
- Limpeza do código
- Documentação do código e da API
- Logs da aplicação
- Mensagens e organização dos commits

## Dicas

- Teste bem sua solução, evite bugs
- Deixe o domínio das URLs de callback passiveis de alteração via configuração, para facilitar
  o teste tanto no emulador, quanto em dispositivos fisicos.
  Observações importantes
- Não inicie o teste sem sanar todas as dúvidas
- Iremos executar a aplicação para testá-la, cuide com qualquer dependência externa e
  deixe claro caso haja instruções especiais para execução do mesmo
  Classificação da informação: Uso Interno

## Anexo 1

### Introdução

A seguir serão detalhados os tipos de tela que o cliente mobile suporta, assim como os tipos de campos disponíveis para a interação do usuário.

### Tipo de tela – FORMULARIO

A tela do tipo FORMULARIO exibe uma coleção de campos (itens) e possui um ou dois botões de ação na parte inferior.

O aplicativo envia uma requisição POST para a url informada e com o body definido pelo objeto dentro de cada botão quando o mesmo é acionado. Nos casos onde temos campos de entrada
de dados na tela, os valores informados pelo usuário são adicionados ao corpo da requisição. Abaixo o exemplo da requisição que o aplicativo vai fazer quando o botão “Ação 1” for acionado:

```
POST http://seudominio.com/ACAO1
{
    “campo1”: “valor1”,
    “campo2”: 123,
    “idCampoTexto”: “Texto”,
    “idCampoNumerico: 999
    “idCampoData”: “01/01/2000”
}
```

Obs: o formato da url acima é meramente ilustrativo e não define qualquer padrão de formato.

### Tipo de tela – SELECAO

A tela do tipo SELECAO exibe uma lista de opções para que o usuário.

O aplicativo envia uma requisição POST para a url informada e com o body definido pelo objeto dentro de cada item da lista de seleção, quando o mesmo é acionado, semelhando ao funcionamento dos botões da tela FORMULARIO.

## Fim instruções
---

## 🚀 Funcionalidades Principais

1. **Cadastrar Pauta**: Registro de pautas para deliberação na assembleia.
2. **Abrir Sessão de Votação**: Abertura de sessão para uma pauta com tempo de duração configurável (padrão de 1 minuto).
3. **Receber Votos dos Associados**:
   - Cada associado (identificado por CPF) pode votar apenas uma vez por pauta (`SIM` ou `NAO`).
   - Apenas sessões abertas recebem votos.
4. **Contabilizar e Exibir Resultados**:
   - Apuração agrupada e em tempo real dos votos com percentuais e status final (`APROVADA`, `REJEITADA`, `EMPATE`, `SEM_VOTOS`).
5. **Tarefas Bônus Implementadas**:
   - **Bônus 1 (Integração com Sistemas Externos)**: Validador de CPF com algoritmo Módulo 11 e fachada mock para verificar se o associado está habilitado a votar (`ABLE_TO_VOTE` / `UNABLE_TO_VOTE`), retornando `404` para CPF inválido.
   - **Bônus 2 (Performance & Alta Carga)**:
     - Índices de banco de dados (`sessao_id`, `(sessao_id, opcao_voto)`).
     - Unique constraint no nível de banco de dados `(sessao_id, associado_cpf)` para prevenir concorrência indevida.
     - Consulta SQL agregada (`GROUP BY`) via Spring Data JPA Projection para apuração com complexidade $O(1)$ de transferência de rede.
   - **Bônus 3 (Versionamento de API)**: Versionamento por URI prefixado (`/v1/`).
   - **Anexo 1 (Suporte a Mobile)**: Endpoints que fornecem as estruturas de tela dinâmicas em JSON (`/v1/mobile/telas/...`).

---

## 🛠️ Tecnologias Utilizadas

- **Linguagem**: Java 21
- **Framework**: Spring Boot 3.3.4
- **Persistência**: Spring Data JPA / Hibernate
- **Banco de Dados**: 
  - PostgreSQL (configuração principal via perfil `prod` e Docker Compose)
- **Documentação**: OpenAPI 3 / Swagger UI (`springdoc-openapi-starter-webmvc-ui` 2.6.0)
- **Testes**: JUnit 5, Mockito, AssertJ, Spring Boot Test (`MockMvc`)
- **Containerização**: Docker e Docker Compose

---

## 📋 Endpoints da API (v1)

### 📌 Pautas
| Método | Endpoint | Descrição |
|---|---|---|
| `POST` | `/v1/pautas` | Cadastrar uma nova pauta |
| `GET` | `/v1/pautas/{id}` | Buscar detalhes de uma pauta por ID |
| `GET` | `/v1/pautas` | Listar todas as pautas (com paginação) |
| `GET` | `/v1/pautas/{id}/resultado` | Obter resultado da votação de uma pauta |
| `POST` | `/v1/pautas/{id}/votos` | Votar em uma pauta |

### ⏱️ Sessões de Votação
| Método | Endpoint | Descrição |
|---|---|---|
| `POST` | `/v1/sessoes` | Abrir sessão de votação (duração em minutos opcional) |
| `GET` | `/v1/sessoes/{id}` | Buscar detalhes e status da sessão |
| `POST` | `/v1/sessoes/{id}/votos` | Registrar voto de associado na sessão |
| `GET` | `/v1/sessoes/{id}/resultado` | Obter resultado apurado da sessão |

### 🔍 Validação de CPF (Bônus 1)
| Método | Endpoint | Descrição |
|---|---|---|
| `GET` | `/v1/users/{cpf}` | Validar status de votação do associado (`ABLE_TO_VOTE` / `UNABLE_TO_VOTE`) |

### 📱 Dynamic UI Mobile (Anexo 1)
| Método | Endpoint | Descrição |
|---|---|---|
| `GET` | `/v1/mobile/telas/formulario-voto/{pautaId}` | Retorna o schema JSON da tela de formulário de voto |
| `GET` | `/v1/mobile/telas/selecao-pautas` | Retorna o schema JSON da tela de listagem e seleção de pautas |

---

## 📖 Exemplos de Uso (cURL)

### 1. Criar uma Pauta
```bash
curl -X POST https://desafio-db-api.onrender.com/v1/pautas \
  -H "Content-Type: application/json" \
  -d '{
    "titulo": "Aprovação do Balanço Anual 2024",
    "descricao": "Deliberação sobre a aprovação das contas do exercício anterior."
  }'
```

### 2. Abrir uma Sessão de Votação (ex: 5 minutos)
```bash
curl -X POST https://desafio-db-api.onrender.com/v1/sessoes \
  -H "Content-Type: application/json" \
  -d '{
    "pautaId": 1,
    "duracaoMinutos": 5
  }'
```

### 3. Registrar um Voto
```bash
curl -X POST https://desafio-db-api.onrender.com/v1/sessoes/1/votos \
  -H "Content-Type: application/json" \
  -d '{
    "associadoCpf": "12345678909",
    "opcaoVoto": "SIM"
  }'
```

### 4. Consultar o Resultado da Votação
```bash
curl -X GET https://desafio-db-api.onrender.com/v1/pautas/1/resultado
```

**Exemplo de Resposta:**
```json
{
  "pautaId": 1,
  "pautaTitulo": "Aprovação do Balanço Anual 2024",
  "sessaoId": 1,
  "statusSessao": "ABERTA",
  "totalVotos": 1,
  "totalVotosSim": 1,
  "totalVotosNao": 0,
  "percentualSim": "100.00%",
  "percentualNao": "0.00%",
  "resultado": "APROVADA"
}
```

---

## 🧪 Como Executar os Testes

Para executar toda a suíte de testes unitários e de integração:

```bash
mvn clean test
```

---

## 🏃 Como Rodar a Aplicação

### Opção 1: Executando Localmente (com Maven)
```bash
mvn spring-boot:run
```
A aplicação iniciará na porta `8089` conectada ao PostgreSQL local em `localhost:5432` (database `votacaodb`).

### Opção 2: Executando via Docker Compose (com PostgreSQL)
```bash
docker-compose up --build -d
```
A API estará acessível em `https://desafio-db-api.onrender.com` conectada ao container PostgreSQL na porta `5432`.

---

# 🚀 Testes de Carga e Estresse com k6

Este diretório contém os scripts de testes de performance e estresse da API do **Desafio Votação**, utilizando a ferramenta [k6](https://k6.io/).

---

## 📂 Estrutura do Projeto

Os scripts de teste devem ser mantidos dentro da pasta de testes do projeto para evitar que entrem no pacote `.jar` de produção:

```text
desafio-votacao/
├── src/
│   └── test/
│       └── k6/
│           ├── stress-test.js      # Script principal de estresse
│           └── payload-example.json # Mocks e payloads utilizados (opcional)
└── K6.md
```

## 🛠️ Pré-requisitos (Instalação)

Windows (via Chocolatey ou Winget) ->  winget install k6 --source winget
 ou
choco install k6

macOS (via Homebrew) -> brew install k6

Linux (Debian/Ubuntu)  -> 
gpg --no-default-keyring --keyring /usr/share/keyrings/k6-archive-keyring.gpg --keyserver hkp://keyserver.ubuntu.com:80 --recv-keys C5AD17C747E3415A3642D57D77C6C491D6AC1D69
echo "deb [signed-by=/usr/share/keyrings/k6-archive-keyring.gpg] [https://dl.k6.io/deb](https://dl.k6.io/deb) stable main" | sudo tee /etc/apt/sources.list.d/k6.list
sudo apt-get update
sudo apt-get install k6

# 🧪 Como Executar os Testes

### 🚀 Executar Teste de Estresse do k6

Para rodar o script de estresse a partir do diretório raiz do projeto, execute:

```bash
k6 run src/test/k6/teste_estresse.js
```

---

## 📑 Documentação Swagger & OpenAPI

Após iniciar a aplicação, acesse a documentação interativa:
- **Swagger UI**: [https://desafio-db-api.onrender.com/swagger-ui.html](https://desafio-db-api.onrender.com/swagger-ui.html)
- **OpenAPI JSON**: [https://desafio-db-api.onrender.com/v3/api-docs](https://desafio-db-api.onrender.com/v3/api-docs)
- **Spring Actuator Health**: [https://desafio-db-api.onrender.com/actuator/health](https://desafio-db-api.onrender.com/actuator/health)
