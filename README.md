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
2. Прописать свои логин/пароль от PostgreSQL в `src/main/resources/hibernate.cfg.xml`.
3. Собрать проект:
   ```bash
   mvn clean compile
   ```
4. Запустить класс `Main` из IDE (или `mvn exec:java -Dexec.mainClass="com.example.Main"`).

## Сложности и вопросы

*(раздел обновляется по ходу работы)*
