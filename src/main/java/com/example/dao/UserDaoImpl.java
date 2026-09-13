package com.example.dao;

import com.example.entity.User;
import com.example.util.HibernateUtil;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
/**
 * Реализация UserDao через Hibernate.
 * Каждая операция выполняется в отдельной транзакции (включая чтение —
 * это обеспечивает консистентность данных и защищает от проблем с lazy loading).
 * При ошибках транзакция откатывается, исключение логируется и пробрасывается выше.
 */
public class UserDaoImpl implements UserDao {

    private static final Logger log = LoggerFactory.getLogger(UserDaoImpl.class);

    private final SessionFactory sessionFactory;

    public UserDaoImpl() {
        this(HibernateUtil.getSessionFactory());
    }

    public UserDaoImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
    /** {@inheritDoc} */
    @Override
    public User save(User user) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.persist(user);
            transaction.commit();
            log.info("Пользователь сохранён: id={}", user.getId());
            return user;
        } catch (Exception e) {
            rollbackQuietly(transaction);
            log.error("Ошибка при сохранении пользователя", e);
            throw new RuntimeException("Не удалось сохранить пользователя", e);
        }
    }

    @Override
    public Optional<User> findById(Long id) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            User user = session.find(User.class, id);
            transaction.commit();
            return Optional.ofNullable(user);
        } catch (Exception e) {
            rollbackQuietly(transaction);
            log.error("Ошибка при поиске пользователя id={}", id, e);
            throw new RuntimeException("Не удалось найти пользователя с id=" + id, e);
        }
    }

    @Override
    public List<User> findAll() {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            List<User> users = session.createQuery("from User", User.class).getResultList();
            transaction.commit();
            return users;
        } catch (Exception e) {
            rollbackQuietly(transaction);
            log.error("Ошибка при получении списка пользователей", e);
            throw new RuntimeException("Не удалось получить список пользователей", e);
        }
    }

    @Override
    public User update(User user) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            User merged = session.merge(user);
            transaction.commit();
            log.info("Пользователь обновлён: id={}", merged.getId());
            return merged;
        } catch (Exception e) {
            rollbackQuietly(transaction);
            log.error("Ошибка при обновлении пользователя id={}", user.getId(), e);
            throw new RuntimeException("Не удалось обновить пользователя с id=" + user.getId(), e);
        }
    }

    @Override
    public void deleteById(Long id) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            User user = session.find(User.class, id);
            if (user == null) {
                log.warn("Пользователь с id={} не найден, удалять нечего", id);
                return;
            }
            session.remove(user);
            log.info("Пользователь удалён: id={}", id);
            transaction.commit();
        } catch (Exception e) {
            rollbackQuietly(transaction);
            log.error("Ошибка при удалении пользователя id={}", id, e);
            throw new RuntimeException("Не удалось удалить пользователя с id=" + id, e);
        }
    }
    /**
     * Откатывает транзакцию, если она существует и ещё активна.
     * Проверки нужны, чтобы откат не бросил собственное исключение
     * и не затёр первоначальную причину ошибки.
     *
     * @param transaction транзакция или null, если она не была начата
     */
    private void rollbackQuietly(Transaction transaction) {
        if (transaction != null && transaction.getStatus().canRollback()) {
            transaction.rollback();
        }
    }
}