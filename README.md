# FIAP Tech Challenge — Fase 3

Backend para gerenciamento de consultas médicas, histórico de pacientes e notificações assíncronas, desenvolvido como parte do FIAP Tech Challenge — Fase 3.

O projeto utiliza uma arquitetura baseada em **microserviços**, com separação entre o serviço responsável pelo agendamento e histórico de consultas e o serviço responsável pelas notificações.

A comunicação entre os serviços é realizada de forma assíncrona utilizando **RabbitMQ**.

---

## 1. Objetivo

O sistema foi desenvolvido para atender a um cenário hospitalar no qual é necessário:

* realizar o agendamento de consultas;
* consultar o histórico dos pacientes;
* consultar consultas futuras;
* controlar o acesso de médicos, enfermeiros e pacientes;
* permitir que médicos e enfermeiros criem e alterem consultas;
* permitir o cancelamento de consultas;
* enviar notificações relacionadas às consultas;
* utilizar comunicação assíncrona entre serviços.

---

## 2. Arquitetura

O projeto é composto por dois microserviços independentes:

### Scheduling Service

Responsável por:

* usuários;
* pacientes;
* médicos;
* enfermeiros;
* consultas;
* histórico das consultas;
* API REST;
* API GraphQL;
* autenticação e autorização;
* publicação de eventos no RabbitMQ.

**Porta:** `8080`

### Notification Service

Responsável por:

* consumo dos eventos enviados pelo Scheduling Service;
* processamento das notificações;
* envio/registro dos lembretes relacionados às consultas.

**Porta:** `8081`

### Comunicação entre os serviços

```text
                     ┌──────────────────────┐
                     │   Scheduling Service │
                     │        :8080         │
                     └──────────┬───────────┘
                                │
                         RabbitMQ Event
                                │
                                ▼
                     ┌──────────────────────┐
                     │      RabbitMQ        │
                     │ appointment.exchange │
                     └──────────┬───────────┘
                                │
                                ▼
                     ┌──────────────────────┐
                     │  Notification        │
                     │      Service         │
                     │        :8081         │
                     └──────────────────────┘
```

Os eventos utilizados são:

```text
appointment.created
appointment.updated
appointment.cancelled
```

---

## 3. Tecnologias

### Scheduling Service

* Java 21
* Spring Boot 4.1.1
* Spring Web
* Spring Data JPA
* Spring Security
* Spring for GraphQL
* Spring for RabbitMQ
* PostgreSQL
* Maven

### Notification Service

* Java 21
* Spring Boot 4.1.1
* Spring Web
* Spring for RabbitMQ
* Maven

### Infraestrutura

* Docker
* Docker Compose
* PostgreSQL
* RabbitMQ

### Testes de API

* Postman

---

## 4. Estrutura do projeto

```text
tech-challenge-fase3/
│
├── docker-compose.yml
├── postman.json
├── README.md
│
├── scheduling-service/
│   ├── Dockerfile
│   ├── pom.xml
│   └── src/
│       └── main/
│           ├── java/
│           └── resources/
│
├── notification-service/
│   ├── Dockerfile
│   ├── pom.xml
│   └── src/
│       └── main/
│           ├── java/
│           └── resources/
```

Cada serviço possui seu próprio projeto Spring Boot, `pom.xml`, `Dockerfile` e processo de execução, mantendo a separação entre os microserviços.

---

## 5. Banco de dados

O Scheduling Service utiliza PostgreSQL.

O banco é criado automaticamente pelo Docker Compose com:

```text
Database: scheduling
Username: postgres
Password: postgres
Port: 5432
```

As principais entidades são:

```text
User
Patient
Doctor
Nurse
Appointment
```

Relacionamento simplificado:

```text
User
 ├── Patient
 ├── Doctor
 └── Nurse

Patient
   │
   └── Appointment ─── Doctor
                  └─── Nurse
```

---

## 6. Usuários iniciais

Para facilitar a demonstração e os testes da aplicação, o Scheduling Service possui um `DataInitializer`.

Esses usuários representam os usuários previamente cadastrados no sistema e são criados automaticamente quando o banco está **vazio**.

### Médicos

```text
Nome: Dr. Carlos
Email: carlos@email.com
Senha: 123456
Role: DOCTOR
```

```text
Nome: Dra. Patrícia
Email: patricia@email.com
Senha: 123456
Role: DOCTOR
```

### Enfermeiras

```text
Nome: Enfermeira Maria
Email: maria@email.com
Senha: 123456
Role: NURSE
```

```text
Nome: Enfermeiro Eduardo
Email: eduardo@email.com
Senha: 123456
Role: NURSE
```

### Pacientes

```text
Nome: João da Silva
Email: joao@email.com
Senha: 123456
Role: PATIENT
```

```text
Nome: Ana Souza
Email: ana@email.com
Senha: 123456
Role: PATIENT
```

```text
Nome: Tiago Maia
Email: tiago@email.com
Senha: 123456
Role: PATIENT
```

```text
Nome: Helena Santos
Email: helena@email.com
Senha: 123456
Role: PATIENT
```

As senhas são armazenadas utilizando BCrypt.

Os usuários acima existem para facilitar a execução e avaliação do projeto.

**Importante:** Não foram implementados endpoints para criação e manutenção de usuários, a fim de manter o projeto mais objetivo nesta análise e entrega da Fase 3, priorizando os requisitos e objetivos do Tech Challenge.

---

## 7. Segurança

A autenticação utiliza:

```text
Spring Security
HTTP Basic Authentication
BCrypt Password Encoder
```

As roles disponíveis são:

```text
ROLE_DOCTOR
ROLE_NURSE
ROLE_PATIENT
```

### Permissões

| Funcionalidade                | Médico | Enfermeiro |   Paciente |
| ----------------------------- | -----: | ---------: | ---------: |
| Criar consulta                |      ✅ |          ✅ |          ❌ |
| Alterar consulta              |      ✅ |          ✅ |          ❌ |
| Cancelar consulta             |      ✅ |          ✅ |          ❌ |
| Consultar histórico           |      ✅ |          ✅ |  ✅ próprio |
| Consultar consultas futuras   |      ✅ |          ✅ | ✅ próprias |
| Consultar consulta individual |      ✅ |          ✅ |  ✅ própria |

Além da role, pacientes possuem uma validação de propriedade dos dados.

Um paciente somente pode acessar consultas pertencentes a ele.

---

# 8. Scheduling Service

**Base URL:**

```text
http://localhost:8080
```

---

## 8.1 Health Check

```http
GET /health
```

Não requer autenticação.

Resposta:

```text
Scheduling Service is running
```

---

# 9. Consultas

Base:

```text
/api/appointments
```

## 9.1 Criar consulta

```http
POST /api/appointments
```

Permitido para:

```text
DOCTOR
NURSE
```

Exemplo:

```json
{
    "patientId": 1,
    "doctorId": 1,
    "dateTime": "2026-10-20T14:00:00",
    "description": "Consulta cardiológica"
}
```

A consulta deve possuir um médico ou enfermeiro.

A data da consulta deve ser futura.

Quando uma consulta é criada, o Scheduling Service publica o evento:

```text
appointment.created
```

no RabbitMQ.

---

## 9.2 Buscar consulta

```http
GET /api/appointments/{id}
```

Médicos e enfermeiros podem consultar qualquer consulta.

Pacientes podem consultar somente consultas pertencentes a eles.

---

## 9.3 Histórico do paciente

```http
GET /api/appointments/patient/{patientId}
```

Retorna todas as consultas do paciente.

Médicos e enfermeiros podem consultar qualquer paciente.

Pacientes podem consultar somente o próprio histórico.

---

## 9.4 Consultas futuras

```http
GET /api/appointments/patient/{patientId}/future
```

Retorna somente consultas futuras do paciente.

As mesmas regras de autorização do histórico são aplicadas.

---

## 9.5 Alterar consulta

```http
PUT /api/appointments/{id}
```

Permitido para:

```text
DOCTOR
NURSE
```

Exemplo:

```json
{
    "doctorId": 1,
    "dateTime": "2026-10-21T15:00:00",
    "description": "Horário alterado"
}
```

Quando a consulta é alterada, o Scheduling Service publica:

```text
appointment.updated
```

---

## 9.6 Cancelar consulta

```http
PATCH /api/appointments/{id}/cancel
```

Permitido para:

```text
DOCTOR
NURSE
```

A consulta passa para o status:

```text
CANCELLED
```

Quando a consulta é cancelada, o Scheduling Service publica:

```text
appointment.cancelled
```

---

# 10. GraphQL

**Endpoint:**

```http
POST /graphql
```

GraphiQL:

```text
http://localhost:8080/graphiql
```

## 10.1 Histórico

```graphql
query {
    patientHistory(patientId: 1) {
        id
        patientId
        doctorId
        nurseId
        dateTime
        status
        description
        createdAt
        updatedAt
    }
}
```

## 10.2 Consultas futuras

```graphql
query {
    futureAppointments(patientId: 1) {
        id
        patientId
        doctorId
        nurseId
        dateTime
        status
        description
    }
}
```

## 10.3 Consulta individual

```graphql
query {
    appointment(id: 1) {
        id
        patientId
        doctorId
        nurseId
        dateTime
        status
        description
    }
}
```

O GraphQL utiliza as mesmas regras de autorização da API REST.

---

# 11. RabbitMQ

RabbitMQ é utilizado para realizar a comunicação assíncrona entre os microserviços.

Management UI:

```text
http://localhost:15672
```

Credenciais:

```text
Username: guest
Password: guest
```

Exchange:

```text
appointment.exchange
```

Queue:

```text
notification.queue
```

Routing Keys:

```text
appointment.created
appointment.updated
appointment.cancelled
```

# 12. Notification Service

**Base URL:**

```text
http://localhost:8081
```

## 12.1 Health Check

```http
GET /health
```

Resposta:

```text
Notification Service is running
```

O serviço recebe os eventos do RabbitMQ por meio de um `@RabbitListener`.

Exemplo de processamento:

```text
Processing appointment notification.
Event type: APPOINTMENT_CREATED
Appointment ID: 1
Patient: João da Silva
Email: joao@email.com
Appointment date: 2026-10-20T14:00
```

Para atualização:

```text
APPOINTMENT_UPDATED
```

Para cancelamento:

```text
APPOINTMENT_CANCELLED
```

Atualmente o processamento do lembrete é demonstrado através dos logs da aplicação.

---

# 13. Executando o projeto

Suba todo o ambiente:

```bash
docker compose up -d --build
```

O Docker irá:

1. criar o PostgreSQL;
2. criar o RabbitMQ;
3. compilar o Scheduling Service;
4. compilar o Notification Service;
5. criar as imagens;
6. iniciar os quatro containers.

Verifique os containers:

```bash
docker compose ps
```

Os serviços esperados são:

```text
techchallenge-postgres
techchallenge-rabbitmq
techchallenge-scheduling
techchallenge-notification
```

---

# 14. Logs

Para acompanhar o Scheduling Service:

```bash
docker compose logs -f scheduling-service
```

Para acompanhar o Notification Service:

```bash
docker compose logs -f notification-service
```

Para acompanhar o RabbitMQ:

```bash
docker compose logs -f rabbitmq
```

---

# 15. Postman

A Collection do Postman está disponível em:

https://raw.githubusercontent.com/brayan-schroeder/fiap-techchallenge-fase3/refs/heads/main/postman.json

A Collection possui testes para:

* health checks;
* consultas;
* histórico;
* consultas futuras;
* atualização;
* cancelamento;
* GraphQL;
* autenticação;
* autorização por role;
* isolamento dos dados dos pacientes.

---

# 16. Cenários de segurança

O projeto contempla os seguintes cenários:

### Paciente tentando criar consulta

```http
POST /api/appointments
```

Resultado esperado:

```text
403 Forbidden
```

### Paciente tentando alterar consulta

```http
PUT /api/appointments/{id}
```

Resultado esperado:

```text
403 Forbidden
```

### Paciente tentando cancelar consulta

```http
PATCH /api/appointments/{id}/cancel
```

Resultado esperado:

```text
403 Forbidden
```

### Paciente acessando o próprio histórico

```http
GET /api/appointments/patient/{ownPatientId}
```

Resultado esperado:

```text
200 OK
```

### Paciente acessando outro paciente

```http
GET /api/appointments/patient/{otherPatientId}
```

Resultado esperado:

```text
403 Forbidden
```

### Paciente acessando a própria consulta

```http
GET /api/appointments/{ownAppointmentId}
```

Resultado esperado:

```text
200 OK
```

### Paciente acessando consulta de outro paciente

```http
GET /api/appointments/{otherAppointmentId}
```

Resultado esperado:

```text
403 Forbidden
```

### Médico acessando histórico

```text
200 OK
```

### Enfermeiro acessando histórico

```text
200 OK
```

---

# 17. Resumo dos serviços

| Serviço              | Porta | Responsabilidade                                                                                |
| -------------------- | ----: | ----------------------------------------------------------------------------------------------- |
| Scheduling Service   |  8080 | Usuários, pacientes, profissionais, consultas, histórico, REST, GraphQL e publicação de eventos |
| Notification Service |  8081 | Consumo de eventos e processamento de notificações                                              |
| PostgreSQL           |  5432 | Persistência dos dados do Scheduling Service                                                    |
| RabbitMQ             |  5672 | Comunicação assíncrona                                                                          |
| RabbitMQ Management  | 15672 | Interface administrativa do RabbitMQ                                                            |

---

# 18. Status dos eventos

| Evento                  | Origem             | Destino              | Ação                               |
| ----------------------- | ------------------ | -------------------- | ---------------------------------- |
| `appointment.created`   | Scheduling Service | Notification Service | Processa lembrete de nova consulta |
| `appointment.updated`   | Scheduling Service | Notification Service | Processa atualização da consulta   |
| `appointment.cancelled` | Scheduling Service | Notification Service | Processa cancelamento da consulta  |

---

# 19. Considerações finais

O projeto demonstra:

* autenticação com Spring Security;
* HTTP Basic Authentication;
* BCrypt Password Encoder;
* autorização baseada em roles;
* controle de acesso aos dados dos pacientes;
* API REST;
* GraphQL;
* persistência com PostgreSQL;
* separação em microserviços;
* comunicação assíncrona utilizando RabbitMQ;
* processamento de eventos de criação, atualização e cancelamento de consultas;
* execução completa utilizando Docker Compose.

A arquitetura foi desenvolvida buscando manter os serviços independentes e separar as responsabilidades de agendamento e notificações.
