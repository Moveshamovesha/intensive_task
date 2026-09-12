package com.example.dao;

import com.example.entity.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
class UserDaoImplIT {

    @Container
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("test_db")
            .withUsername("test")
            .withPassword("test");

    private static SessionFactory sessionFactory;

    private UserDao userDao;

    @BeforeAll
    static void setUpSessionFactory() {
        Configuration configuration = new Configuration();
        configuration.setProperty("hibernate.connection.driver_class", "org.postgresql.Driver");
        configuration.setProperty("hibernate.connection.url", postgres.getJdbcUrl());
        configuration.setProperty("hibernate.connection.username", postgres.getUsername());
        configuration.setProperty("hibernate.connection.password", postgres.getPassword());
        configuration.setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        configuration.setProperty("hibernate.hbm2ddl.auto", "create-drop");
        configuration.setProperty("hibernate.show_sql", "false");
        configuration.addAnnotatedClass(User.class);
        sessionFactory = configuration.buildSessionFactory();
    }

    @BeforeEach
    void setUp() {
        userDao = new UserDaoImpl(sessionFactory);
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            session.createMutationQuery("delete from User").executeUpdate();
            transaction.commit();
        }
    }

    @AfterAll
    static void tearDown() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }

    @Test
    void save_savesUserAndGeneratesId() {
        User saved = userDao.save(new User("Ivan", "ivan@test.com", 25));

        assertNotNull(saved.getId());

        Optional<User> found = userDao.findById(saved.getId());
        assertTrue(found.isPresent());
        assertEquals("Ivan", found.get().getName());
        assertEquals("ivan@test.com", found.get().getEmail());
        assertEquals(25, found.get().getAge());
        assertNotNull(found.get().getCreatedAt());
    }

    @Test
    void findById_missingId_returnsEmpty() {
        Optional<User> result = userDao.findById(999L);

        assertTrue(result.isEmpty());
    }

    @Test
    void findAll_returnsAllSavedUsers() {
        userDao.save(new User("Ivan", "ivan@test.com", 25));
        userDao.save(new User("Maria", "maria@test.com", 30));

        List<User> users = userDao.findAll();

        assertEquals(2, users.size());
    }

    @Test
    void update_changesUserFields() {
        User saved = userDao.save(new User("Ivan", "ivan@test.com", 25));
        saved.setName("Petr");
        saved.setAge(40);

        userDao.update(saved);

        User found = userDao.findById(saved.getId()).orElseThrow();
        assertEquals("Petr", found.getName());
        assertEquals(40, found.getAge());
        assertEquals("ivan@test.com", found.getEmail());
    }

    @Test
    void deleteById_removesUser() {
        User saved = userDao.save(new User("Ivan", "ivan@test.com", 25));

        userDao.deleteById(saved.getId());

        assertFalse(userDao.findById(saved.getId()).isPresent());
    }

    @Test
    void deleteById_missingId_doesNothing() {
        userDao.deleteById(999L);

        assertTrue(userDao.findAll().isEmpty());
    }

    @Test
    void save_duplicateEmail_throwsException() {
        userDao.save(new User("Ivan", "same@test.com", 25));

        assertThrows(RuntimeException.class,
                () -> userDao.save(new User("Petr", "same@test.com", 30)));
    }

    @Test
    void findAll_onEmptyTable_returnsEmptyList() {
        List<User> users = userDao.findAll();

        assertTrue(users.isEmpty());
    }
}