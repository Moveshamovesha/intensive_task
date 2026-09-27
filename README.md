# Intensive task 1 (Hibernate + PostgreSQL)

## Название модуля и текст домашнего задания

**Модуль:** Hibernate (без Spring)

**Задание:** Разработать консольное приложение (user-service) на Java, использующее Hibernate для взаимодействия с PostgreSQL, без использования Spring. Приложение должно поддерживать базовые операции CRUD (Create, Read, Update, Delete) над сущностью User.

**Требования:**
- Использовать Hibernate в качестве ORM.
- База данных — PostgreSQL.
- Настроить Hibernate без Spring, используя hibernate.cfg.xml или properties-файл.
- Реализовать CRUD-операции для сущности User (создание, чтение, обновление, удаление), которая состоит из полей: id, name, email, age, created_at.
- Использовать консольный интерфейс для взаимодействия с пользователем.
- Использовать Maven для управления зависимостями.
- Настроить логирование.
- Настроить транзакционность для операций с базой данных.
- Использовать DAO-паттерн для отделения логики работы с БД.
- Обработать возможные исключения, связанные с Hibernate и PostgreSQL.

**Дополнительные указания преподавателя:**
- Операции чтения тоже оборачивать в транзакцию (защита от проблем с lazy loading и консистентность данных).
- Код попадает в `develop` только через Pull Request с 2 ревьюерами.
- Javadoc для классов и публичных методов, однострочные комментарии `//` для пояснений.

## Состав команды и распределение задач на задание User Service
**Задание 1: Hibernate + PostgreSQL**
| Участник                | Роль | Задачи |
|-------------------------|---|---|
| Ким Константин (тимлид) | Архитектура, настройка проекта, Git | Создание репозитория, ветки main/develop, защита веток (2 ревьюера); pom.xml, hibernate.cfg.xml, HibernateUtil, сущность User; README |
| Александр Куприенко     | Слой данных (DAO) | Интерфейс UserDao, класс UserDaoImpl: CRUD-операции, транзакции (включая чтение), обработка исключений Hibernate/PostgreSQL |
| Сергей Ибрагимов        | Консольный интерфейс, логирование | Класс Main с консольным меню (Scanner), настройка logback.xml, ручное тестирование всех CRUD-операций |

### Фича-ветки

- `feature/project-setup` — pom.xml, hibernate.cfg.xml, HibernateUtil, сущность User (Константин)
- `feature/user-dao` — UserDao + UserDaoImpl (Александр, доработка — Константин)
- `feature/console-ui` — консольное меню (Сергей, доработка — Константин)
- `feature/logging` — logback.xml (Сергей)
- `bugfix/fix-dao-and-menu` — исправления после код-ревью (Константин)
- `feature/test-setup` — тестовые зависимости, плагины, конструктор в UserDaoImpl (Константин)
- `feature/user-service` — Service-слой и перевод Main на него (Сергей)
- `feature/service-unit-tests` — юнит-тесты сервиса на Mockito (Сергей)
- `feature/dao-integration-tests` — интеграционные тесты DAO на Testcontainers (Александр)
- `bugfix/add-missing-service-layer` — восстановление потерянного Service-слоя

### Git-регламент

- 1 мини-задача = 1 фича-ветка от `develop`.
- Коммиты атомарные, в формате: `feat: implement UserDao interface`, `fix: handle scanner newline bug` и т.д.
- Слияние в `develop` — только через Pull Request, обязательные аппрувы: 2 ревьюера.
- Стратегия слияния: Merge commit (единая для всего проекта).
- `main` — стабильная версия, мерджим из `develop` перед сдачей.

## Структура проекта

```
user-service/
├── pom.xml
├── README.md
└── src/
    ├── main/
    │   ├── java/com/example/
    │   │   ├── Main.java                    — консольное меню, работает через UserService (Сергей)
    │   │   ├── entity/
    │   │   │   └── User.java                — сущность (Константин)
    │   │   ├── dao/
    │   │   │   ├── UserDao.java             — интерфейс DAO (Александр)
    │   │   │   └── UserDaoImpl.java         — реализация DAO, SessionFactory через конструктор (Александр, доработка — Константин)
    │   │   ├── service/
    │   │   │   ├── UserService.java         — интерфейс сервиса: валидация и бизнес-логика (Сергей)
    │   │   │   └── UserServiceImpl.java     — реализация сервиса (Сергей)
    │   │   ├── exception/
    │   │   │   └── UserNotFoundException.java — ошибка «пользователь не найден» (Константин)
    │   │   └── util/
    │   │       └── HibernateUtil.java       — фабрика сессий (Константин)
    │   └── resources/
    │       ├── hibernate.cfg.xml            — конфигурация Hibernate (Константин)
    │       └── logback.xml                  — настройка логирования (Сергей)
    └── test/
        └── java/com/example/
            ├── dao/
            │   └── UserDaoImplIT.java       — интеграционные тесты DAO, Testcontainers + PostgreSQL (Александр)
            └── service/
                └── UserServiceImplTest.java — юнит-тесты сервиса, Mockito (Сергей)
```
## Тестирование

- Юнит-тесты Service-слоя (Mockito, без базы данных): `mvn test`
- Интеграционные тесты DAO-слоя (Testcontainers, нужен запущенный Docker): `mvn verify`
- Изоляция тестов: юнит-тесты получают новые моки в каждом методе, интеграционные очищают таблицу перед каждым тестом и работают в одноразовом контейнере PostgreSQL.

## Состав команды и распределение задач
**Задание 2: тестирование (JUnit 5 + Mockito + Testcontainers)**

| Участник        | Роль | Задачи                                                                                                                                                                                                                         |
|-----------------|---|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Ким Константин  | Тестовая инфраструктура, рефакторинг, Git | Тестовые зависимости в pom.xml (JUnit 5, Mockito, Testcontainers), плагины surefire/failsafe; рефакторинг UserDaoImpl (SessionFactory через конструктор); UserNotFoundException; раздел «Тестирование» в README; ревью всех PR |
| Сергей Ибрагимов (тимлид) | Service-слой, юнит-тесты | Интерфейс UserService и класс UserServiceImpl (валидация, бизнес-логика); перевод консольного меню Main на сервис; юнит-тесты UserServiceImplTest с Mockito (11 тестов)                                                        |
| Александр Куприенко | Интеграционные тесты DAO | Класс UserDaoImplIT: контейнер PostgreSQL через Testcontainers, программная настройка SessionFactory, очистка таблицы перед каждым тестом, покрытие всех CRUD-операций (8 тестов)                                              |

Порядок выполнения: feature/test-setup → параллельно feature/user-service и feature/dao-integration-tests → feature/service-unit-tests → bugfix/add-missing-service-layer → develop.
## Запуск проекта

1. Установить PostgreSQL и создать базу данных:
   ```sql
   CREATE DATABASE user_db;
   ```
2. Задать переменные окружения `DB_USER` и `DB_PASSWORD` (логин и пароль от вашего локального PostgreSQL). В IntelliJ IDEA: Run → Edit Configurations → Main → Environment variables → `DB_USER=postgres;DB_PASSWORD=ваш_пароль`.
3. Собрать проект:
   ```bash
   mvn clean compile
   ```
4. Запустить класс `Main` из IDE (или `mvn exec:java -Dexec.mainClass="com.example.Main"`).

## Сложности и вопросы

*(раздел обновляется по ходу работы)*

## Название модуля и текст домашнего задания
**Задание 4: Spring Boot + Spring Data JPA + REST API**

**Задание:** Добавить в user-service поддержку Spring и разработать API,
которое позволит управлять данными.

**Требования:**
- Подключить необходимые модули Spring (Boot, Web, Data JPA и др.).
- Реализовать REST API для получения, создания, обновления и удаления пользователя.
- Entity не должен возвращаться из контроллера — использовать DTO.
- Заменить Hibernate на Spring Data JPA.
- Написать тесты для API (MockMvc или другие средства).

## Состав команды и распределение задач
**Задание 4: Spring Boot + Spring Data JPA + REST API**

| Участник              | Роль                          | Задачи                                                                                                                                        |
|-----------------------|-------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------|
| Александр (тимлид)    | Архитектура, инфраструктура   | pom.xml, application.properties, UserServiceApplication, сервис, юнит-тест сервиса, интеграционный тест, снос legacy, ревью и мёрдж, README   |
| Сергей Ибрагимов      | Слой данных                   | Entity (правки), DTO, репозиторий, маппер                                                                                                     |
| Ким Константин        | Слой API                      | Контроллер, GlobalExceptionHandler, ErrorResponse, кастомные исключения, тесты контроллера (@WebMvcTest)                                      |

## Структура проекта
**Задание 4: Spring Boot + Spring Data JPA + REST API**

user-service/
├── pom.xml
├── README.md
└── src/
├── main/
│   ├── java/com/example/
│   │   ├── UserServiceApplication.java      — точка входа Spring Boot
│   │   ├── entity/
│   │   │   └── User.java                    — сущность (Сергей)
│   │   ├── dto/
│   │   │   ├── UserRequestDto.java          — входящий DTO (Сергей)
│   │   │   └── UserResponseDto.java         — исходящий DTO (Сергей)
│   │   ├── mapper/
│   │   │   └── UserMapper.java              — маппер Entity ↔ DTO (Сергей)
│   │   ├── repository/
│   │   │   └── UserRepository.java          — Spring Data JPA репозиторий (Сергей)
│   │   ├── service/
│   │   │   ├── UserService.java             — интерфейс сервиса (Александр)
│   │   │   └── UserServiceImpl.java         — реализация сервиса (Александр)
│   │   ├── controller/
│   │   │   └── UserController.java          — REST контроллер (Константин)
│   │   └── exception/
│   │       ├── UserNotFoundException.java   — кастомное исключение (Константин)
│   │       ├── ErrorResponse.java           — модель ошибки (Константин)
│   │       └── GlobalExceptionHandler.java  — обработчик исключений (Константин)
│   └── resources/
│       ├── application.properties           — конфигурация Spring Boot (Александр)
│       └── logback.xml                      — настройка логирования
└── test/
└── java/com/example/
├── controller/
│   └── UserControllerTest.java      — тесты контроллера @WebMvcTest (Константин)
└── service/
└── UserServiceImplTest.java     — юнит-тесты сервиса (Александр)

## Запуск проекта
**Задание 4: Spring Boot + Spring Data JPA + REST API**

1. Установить PostgreSQL и создать базу данных:
   ```sql
   CREATE DATABASE user_db;
## Название модуля и текст домашнего задания
**Задание 5: Kafka + Notification Service**

Реализовать микросервис (notification-service) для отправки сообщения на почту при удалении или добавлении пользователя.

При удалении или создании юзера приложение user-service отправляет сообщение в Kafka, в котором содержится информация об операции (удаление или создание) и email юзера. Микросервис notification-service получает сообщение из Kafka и отправляет письмо в зависимости от операции: удаление — «Здравствуйте! Ваш аккаунт был удалён.», создание — «Здравствуйте! Ваш аккаунт на сайте ваш сайт был успешно создан.». Отдельно реализован API для отправки сообщения на почту (тот же функционал, что и через Kafka). Написаны интеграционные тесты для проверки отправки сообщения на почту.

## Состав команды и распределение задач
**Задание 5: Kafka + Notification Service**

| Участник | Роль | Задачи |
| --- | --- | --- |
| Ким Константин (тимлид) | Инфраструктура, архитектура, Git | docker-compose с Kafka; разделение проекта на модули user-service и notification-service; восстановление pom.xml (JDBC-драйвер, Testcontainers, failsafe) и настроек БД; стабилизация тестов; README; ревью всех PR |
| Александр Куприенко | Продюсер (user-service) | UserEvent, UserOperation; отправка событий в Kafka из UserServiceImpl при создании и удалении юзера; юнит-тесты отправки событий (Mockito + ArgumentCaptor) |
| Сергей Ибрагимов | Консьюмер и почта (notification-service) | UserEventConsumer, EmailService, NotificationController + EmailRequest; интеграционный тест отправки почты EmailServiceIT (GreenMail) |

### Фича-ветки
- `kafka-producer` — событие и отправка сообщений при create/delete (Александр)
- `feature/kafka-consumer` — консьюмер и EmailService (Сергей)
- `feature/notification-api` — REST API отправки письма (Сергей)
- `producer-test` — юнит-тесты продюсера (Александр)
- `feature/email-integration-tests` — EmailServiceIT (Сергей)
- `bugfix/restore-build` — восстановление зависимостей и настроек БД (Константин)
- `bugfix/fix-tests` — стабилизация тестов после внедрения Kafka (Константин)
- `bugfix/kafka-deserialization` — настройка десериализации, сеттеры в событиях, логирование (Константин)
- `feature/multi-module-split` — разделение на два микросервиса (Константин)
- `feature/kafka-email-flow-test` — сквозной тест Kafka → письмо (Константин)

## Контракт сообщения
Топик: `user-events`. Формат JSON:

```json
{"email": "ivan@mail.com", "userOperation": "CREATE"}
{"email": "ivan@mail.com", "userOperation": "DELETE"}
```

Типовые заголовки Kafka не используются (`spring.json.use.type.headers=false`): консьюмер распаковывает сообщение в свой собственный класс и не зависит от классов user-service. Топик создаётся автоматически при первом сообщении.

## Структура проекта
**Задание 5: Kafka + Notification Service**

```
intensive-task/
├── pom.xml                              — родительский pom, модули (Константин)
├── docker-compose.yml                   — Kafka, режим KRaft (Константин)
├── user-service/
│   ├── pom.xml                          — web, validation, data-jpa, kafka, postgresql, testcontainers (Константин)
│   └── src/
│       ├── main/java/com/example/
│       │   ├── UserServiceApplication.java
│       │   ├── controller/UserController.java
│       │   ├── dto/                     — UserCreateRequest, UserUpdateRequest, UserResponse
│       │   ├── entity/User.java
│       │   ├── event/                   — UserEvent, UserOperation (Александр)
│       │   ├── exception/
│       │   ├── mapper/UserMapper.java
│       │   ├── repository/UserRepository.java
│       │   └── service/                 — UserService, UserServiceImpl: отправка в Kafka при create/delete (Александр)
│       ├── main/resources/application.properties
│       └── test/java/com/example/
│           ├── controller/UserControllerTest.java
│           └── service/                 — UserServiceImplTest (юнит, Mockito), UserServiceIT (Testcontainers)
└── notification-service/
    ├── pom.xml                          — web, validation, kafka, mail, greenmail, awaitility (Константин)
    └── src/
        ├── main/java/com/example/notificationservice/
        │   ├── NotificationServiceApplication.java
        │   ├── consumer/UserEventConsumer.java     — слушатель топика user-events (Сергей)
        │   ├── controller/NotificationController.java — POST /api/notifications/email (Сергей)
        │   ├── dto/EmailRequest.java               (Сергей)
        │   ├── event/                              — UserEvent, UserOperation (Сергей)
        │   └── service/EmailService.java           — отправка писем (Сергей)
        ├── main/resources/application.properties
        └── test/java/com/example/notificationservice/
            ├── NotificationFlowIT.java             — сквозной тест Kafka → письмо (EmbeddedKafka + GreenMail)
            └── service/EmailServiceIT.java         — тест отправки почты (Сергей)
```

## Запуск проекта
**Задание 5: Kafka + Notification Service**

1. Поднять Kafka:
   ```bash
   docker compose up -d
   ```
2. Установить PostgreSQL и создать базу данных:
   ```sql
   CREATE DATABASE user_db;
   ```
3. Задать переменные окружения:
   - `DB_USER`, `DB_PASSWORD` — логин и пароль локального PostgreSQL;
   - `MAIL_USERNAME` — Gmail-адрес, с которого уходят письма;
   - `MAIL_PASSWORD` — пароль приложения Gmail (Google-аккаунт → Безопасность → Двухэтапная аутентификация → Пароли приложений; обычный пароль не подойдёт).
4. Запустить `UserServiceApplication` (порт 8080), затем `NotificationServiceApplication` (порт 8081).
5. Проверка:
   - `POST /api/users` → на email юзера приходит «Здравствуйте! Ваш аккаунт на сайте ваш сайт был успешно создан.»
   - `DELETE /api/users/{id}` → приходит «Здравствуйте! Ваш аккаунт был удалён.»
   - `POST /api/notifications/email` с телом `{"to": "...", "subject": "...", "text": "..."}` → произвольное письмо (API работает напрямую, минуя Kafka)

## Тестирование
- `mvn clean verify` — все тесты обоих модулей (юнит-тесты через surefire, интеграционные `*IT` через failsafe).
- `UserServiceIT` требует запущенный Docker (Testcontainers поднимает одноразовый PostgreSQL); отправка в Kafka в нём замокирована через `@MockBean`.
- Почтовые тесты Docker не требуют:
   - `EmailServiceIT` — отправка письма напрямую через EmailService, ловушка GreenMail;
   - `NotificationFlowIT` — сквозной сценарий: сообщение в топик `user-events` (EmbeddedKafka) → консьюмер → письмо с текстом в зависимости от операции (GreenMail, ожидание через Awaitility).

## Сложности и вопросы
- Десериализация событий: по умолчанию JsonDeserializer доверяет типовым заголовкам продюсера и списку trusted packages. Решение: `use.type.headers=false` + `value.default.type` + корректный `trusted.packages` — консьюмер не зависит от классов user-service.
- У классов событий должны быть сеттеры: без них Jackson создаёт объект с полями null.
- Отправка в Kafka выполняется после коммита операции с БД, чтобы не разослать уведомления о несостоявшихся событиях. Известное узкое место: если брокер недоступен в момент отправки, событие теряется; production-решение — паттерн Outbox.