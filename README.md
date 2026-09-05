# User Service (Hibernate + PostgreSQL)

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

## Состав команды и распределение задач

| Участник                | Роль | Задачи |
|-------------------------|---|---|
| Ким Константин (тимлид) | Архитектура, настройка проекта, Git | Создание репозитория, ветки main/develop, защита веток (2 ревьюера); pom.xml, hibernate.cfg.xml, HibernateUtil, сущность User; README |
| Александр Куприенко     | Слой данных (DAO) | Интерфейс UserDao, класс UserDaoImpl: CRUD-операции, транзакции (включая чтение), обработка исключений Hibernate/PostgreSQL |
| Сергей Ибрагимов        | Консольный интерфейс, логирование | Класс Main с консольным меню (Scanner), настройка logback.xml, ручное тестирование всех CRUD-операций |

### Фича-ветки

- `feature/project-setup` — pom.xml, hibernate.cfg.xml, HibernateUtil (тимлид)
- `feature/add-entity` — сущность User (тимлид)
- `feature/user-dao` — UserDao + UserDaoImpl (Александр)
- `feature/console-ui` — консольное меню (Сергей)
- `feature/logging` — logback.xml (Сергей)
- `docs/readme` — README.md (тимлид)

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
    └── main/
        ├── java/com/example/
        │   ├── Main.java              — консольное меню (Сергей)
        │   ├── entity/
        │   │   └── User.java          — сущность (тимлид)
        │   ├── dao/
        │   │   ├── UserDao.java       — интерфейс DAO (Александр)
        │   │   └── UserDaoImpl.java   — реализация DAO (Александр)
        │   └── util/
        │       └── HibernateUtil.java — фабрика сессий (тимлид)
        └── resources/
            ├── hibernate.cfg.xml      — конфигурация Hibernate (тимлид)
            └── logback.xml            — настройка логирования (Сергей)
```

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
