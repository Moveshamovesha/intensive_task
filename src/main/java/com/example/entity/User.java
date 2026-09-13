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

    /**
     * Создаёт нового пользователя для сохранения в базу.
     *
     * @param name  имя пользователя
     * @param email адрес электронной почты (уникальный)
     * @param age   возраст
     */
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

    /** @param id уникальный идентификатор пользователя */
    public void setId(Long id) {
        this.id = id;
    }

    /** @return имя пользователя */
    public String getName() {
        return name;
    }

    /** @param name имя пользователя */
    public void setName(String name) {
        this.name = name;
    }

    /** @return адрес электронной почты */
    public String getEmail() {
        return email;
    }

    /** @param email адрес электронной почты */
    public void setEmail(String email) {
        this.email = email;
    }

    /** @return возраст пользователя */
    public Integer getAge() {
        return age;
    }

    /** @param age возраст пользователя */
    public void setAge(Integer age) {
        this.age = age;
    }

    /** @return дата и время создания записи */
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
        return "User{id=" + id + ", name='" + name + "', email='" + email
                + "', age=" + age + ", createdAt=" + createdAt + "}";
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        User user = (User) o;
        return id != null && id.equals(user.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}