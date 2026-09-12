package com.example.dao;

import com.example.entity.User;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
class UserDaoImplTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    static SessionFactory sessionFactory;
    static UserDaoImpl userDao;

    @BeforeAll
    static void setup() {
        sessionFactory = new Configuration()
                .setProperty("hibernate.connection.url", postgres.getJdbcUrl())
                .setProperty("hibernate.connection.username", postgres.getUsername())
                .setProperty("hibernate.connection.password", postgres.getPassword())
                .setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect")
                .setProperty("hibernate.hbm2ddl.auto", "create-drop")
                .setProperty("hibernate.show_sql", "true")
                .addAnnotatedClass(User.class)
                .buildSessionFactory();

        userDao = new UserDaoImpl(sessionFactory);
    }

    @AfterAll
    static void teardown() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }

    @AfterEach
    void cleanUp() {
        try (var session = sessionFactory.openSession()) {
            var tx = session.beginTransaction();
            session.createMutationQuery("delete from User").executeUpdate();
            tx.commit();
        }
    }

    @Test
    void save_shouldPersistUser() {
        User user = new User("Санёк", "sanok@example.com", 25);

        User saved = userDao.save(user);

        assertNotNull(saved.getId());
        assertNotNull(saved.getCreatedAt());
        assertEquals("Санёк", saved.getName());
        assertEquals("sanok@example.com", saved.getEmail());
        assertEquals(25, saved.getAge());
    }

    @Test
    void findById_shouldReturnUser_whenExists() {
        User user = new User("Иван", "ivan@example.com", 30);
        userDao.save(user);

        Optional<User> found = userDao.findById(user.getId());

        assertTrue(found.isPresent());
        assertEquals("Иван", found.get().getName());
    }

    @Test
    void findById_shouldReturnEmpty_whenNotExists() {
        Optional<User> found = userDao.findById(999L);

        assertFalse(found.isPresent());
    }

    @Test
    void findAll_shouldReturnAllUsers() {
        userDao.save(new User("Иван", "ivan@example.com", 30));
        userDao.save(new User("Мария", "maria@example.com", 25));

        List<User> users = userDao.findAll();

        assertEquals(2, users.size());
    }

    @Test
    void findAll_shouldReturnEmpty_whenNoUsers() {
        List<User> users = userDao.findAll();

        assertTrue(users.isEmpty());
    }

    @Test
    void update_shouldChangeUserFields() {
        User user = new User("Иван", "ivan@example.com", 30);
        userDao.save(user);

        user.setName("Иван Обновлённый");
        user.setAge(31);
        User updated = userDao.update(user);

        assertEquals("Иван Обновлённый", updated.getName());
        assertEquals(31, updated.getAge());
    }

    @Test
    void deleteById_shouldRemoveUser_whenExists() {
        User user = new User("Иван", "ivan@example.com", 30);
        userDao.save(user);
        Long id = user.getId();

        userDao.deleteById(id);

        Optional<User> found = userDao.findById(id);
        assertFalse(found.isPresent());
    }

    @Test
    void deleteById_shouldDoNothing_whenNotExists() {
        assertDoesNotThrow(() -> userDao.deleteById(999L));
    }
}