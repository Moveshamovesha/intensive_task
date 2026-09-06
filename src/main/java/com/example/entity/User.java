package com.example.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
/**
 * Сущность пользователя.
 * Отображается на таблицу "users" в базе данных PostgreSQL.
 */
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "age")
    private Integer age;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Пустой конструктор, обязательный для Hibernate:
     * Hibernate создаёт объект через него и затем заполняет поля данными из таблицы.
     */
    public User() {
    }
    public User() {
    }
    /**
     * Создаёт нового пользователя для сохранения в базу.
     *
     * @param name  имя пользователя
     * @param email адрес электронной почты (уникальный)
     * @param age   возраст
     */
    public User(String name, String email, Integer age) {
    public User(String name, String email, Integer age) {
        this.name = name;
        this.email = email;
        this.age = age;
    }
    /**
     * Вызывается Hibernate автоматически перед сохранением в базу.
     * Проставляет текущую дату и время в поле createdAt.
     */
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
    /** @return уникальный идентификатор пользователя */
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    /**
     * Возвращает строковое представление пользователя для вывода в консоль.
     *
     * @return строка со всеми полями пользователя
     */
    @Override
    public String toString() {
        return "User id=" + id + ", name='" + name + "', email='" + email
                + "', age=" + age + ", createdAt=" + createdAt + ".";
    }
}