# О! Subscriber Service (Backend)

Spring Boot 3.2 / Java 17+ backend для управления абонентами мобильного оператора **О!**: REST API, валидация (в т.ч. кастомная), несколько подходов DAO (JPA + JDBC), транзакции, фото в файловую систему, Spring Security (Basic + JWT).

## Требования

1. Presentation Layer:
   a.  Spring MVC Service.
   b.  RESTful API Web Service.
   c.  Validation.
   d.  Custom Validation.
2. Service Layer:
   a.  Standard Service.
   b.  Abstract class.
   c.  Bean Component.
3. Repository (DAO Access Layer):
   a.  Native JDBC.
   b.  JdbcCleint.
   c.  JdbcTemplate.
   d.  JdbcOperation.
   e.  JPA.
   f.  Transactional.
   g.  Photo save to File System.
4. Spring Security:
   a.  Base Authentication.
   b.  JWT.


## Быстрый старт

### 1) База данных PostgreSQL

По умолчанию приложение ожидает PostgreSQL:

- **URL**: `jdbc:postgresql://localhost:5432/operator_db`
- **user**: `postgres`
- **password**: `postgres`

Настройки лежат в `src/main/resources/application.properties`.

Создайте базу:

```sql
CREATE DATABASE operator_db;
```

Если у вас другие креды/порт — поменяйте:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/operator_db
spring.datasource.username=postgres
spring.datasource.password=postgres
```

Схема и сиды создаются при старте из `src/main/resources/schema.sql`.

### 2) Сборка

```bash
mvn -DskipTests clean package
```

### 3) Запуск

```bash
mvn spring-boot:run
```

Приложение поднимается на `http://localhost:8080`.

## Аутентификация и безопасность

- **JWT Bearer**: основной способ авторизации.
- **HTTP Basic**: включён и может использоваться (например, для Actuator).
- `GET /api/tariffs/**` и `/api/auth/**` доступны без токена.
- `/actuator/**` требует роль `ADMIN`.

### Admin по умолчанию

В `schema.sql` создаётся админ:

- **username**: `admin`
- **password**: `admin123`

## REST API (основное)

### Auth

- `POST /api/auth/register` — регистрация (возвращает JWT)
- `POST /api/auth/login` — логин (возвращает JWT)

### Subscribers

- `POST /api/subscribers` — создать
- `GET /api/subscribers/{id}` — получить по id
- `GET /api/subscribers?msisdn=...` — найти по msisdn или список всех
- `PUT /api/subscribers/{id}` — обновить
- `PATCH /api/subscribers/{id}/tariff/{tariffId}` — сменить тариф
- `POST /api/subscribers/{id}/topup` — пополнение баланса
- `POST /api/subscribers/{id}/charge` — списание
- `POST /api/subscribers/{id}/photo` — загрузить фото (`multipart/form-data`, part name: `file`)
- `GET /api/subscribers/{id}/photo` — получить фото

### Tariffs

- `GET /api/tariffs` — список активных тарифов (без авторизации)
- `GET /api/tariffs/{id}` — тариф по id
- `POST /api/tariffs` — создать тариф

### Transactions

- `GET /api/transactions?subscriberId=...` — транзакции абонента

## Валидация номера телефона

Кастомная аннотация: `@ValidPhoneNumber` (см. `src/main/java/com/operator/subscriber/validation`).

Формат MSISDN:

- `996` + допустимый префикс оператора + `\\d{6}`

Пример: `996700123456`.

## Слои доступа к данным (DAO)

В проекте одновременно используются:

- **JPA**: `src/main/java/com/operator/subscriber/repository/*`
- **JdbcClient**: `TariffJdbcClientDao`
- **JdbcTemplate/JdbcOperations**: `TransactionJdbcTemplateDao`
- **Native JDBC** (`java.sql`): `NativeJdbcMsisdnLookupDao`

## Фото в файловую систему

Путь задаётся свойством:

```properties
app.photo.upload-dir=uploads/photos
```

Файлы сохраняются в поддиректорию по `subscriberId`.

## Частые проблемы

### Ошибка подключения к БД

Если видите `PSQLException ... user "postgres" ...`, значит:

- PostgreSQL не запущен, или
- неверный пароль/пользователь, или
- нет базы `operator_db`.

Исправьте креды в `application.properties` или настройте PostgreSQL под них.

