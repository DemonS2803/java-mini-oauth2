
## Проект java-mini-oauth2

### Используемые технологии

- Java 21
- Spring Boot 3.4
- PostgreSQL
- Docker Compose
- JUnit 5, Mockito


### Как запустить

Docker compose'ом
```bash
docker compose --build
```

### Описание структуры проекта

Проект состоит из 3 основных модулей

1. common - отвечает за общие переменные и утилиты, такие как JWT кодировщик (он нужен во всех сервисах)
2. rs-server - имитация ресурсного сервера. Умеет проверять входящие JWT, перенаправлять на login и отдавать 200
3. oauth-server - основная логика по аутентификации. Выдает токены, следит за их ЖЦ


### Примеры запросов

Для удобства сделал коллекцию Postman'а (см. [json](postman/postman-mini-oauth2.json))

В стартовой [конфигурации](oauth-server/src/main/resources/data.sql) есть 

Пользователь | пароль
- test_username | test_password
- test_reader | test_password

Клиент | секрет
- test client | test secret

Логин по паролю
```bash
curl --location 'http://localhost:9090/oauth/token' \
--header 'Content-Type: application/json' \
--data '{
    "client_id": "test client",
    "client_secret": "test secret",
    "username": "test_username",
    "password": "test_password",
    "grant_type": "password",
    "scopes": ["payments:read", "payments:edit"]
}'
```

Логин по кредам
```bash
curl --location 'http://localhost:9090/oauth/token' \
--header 'Accept: application/json' \
--header 'Content-Type: application/json' \
--data '{
    "client_id": "test client",
    "client_secret": "test secret",
    "grant_type": "client_credentials",
    "scopes": ["payments:read", "payments:edit"]
}'
```

Обмен токена
```bash
curl --location 'http://localhost:9090/oauth/token/refresh' \
--header 'Accept: application/json' \
--header 'Content-Type: application/json' \
--data '{
    "client_id": "test client",
    "client_secret": "test secret",
    "grant_type": "refresh_token",
    "refresh_token": "{{your_refresh_token}}"
}'
```

Статус токена
```bash
curl --location --request GET 'http://localhost:9090/oauth/introspect' \
--header 'Accept: application/json' \
--header 'Content-Type: application/json' \
--data '{
    "token_type_hint": "access_token",
    "token": "{{token}}"
}'
```

Протухание токена
```bash
curl --location 'http://localhost:9090/oauth/revoke' \
--header 'Content-Type: application/json' \
--data '{
    "token_type_hint": "access_token",
    "token": "{{token}}"
}'
```

Получение ресурса
```bash
curl --location 'http://localhost:8080/api/payments' \
--header 'Authorization: {{token}}'
```