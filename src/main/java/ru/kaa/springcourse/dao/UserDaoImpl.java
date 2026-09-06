package ru.kaa.springcourse.dao;

import org.hibernate.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.kaa.springcourse.entity.User;
import ru.kaa.springcourse.util.HibernateUtil;

public class UserDaoImpl implements UserDao {

    private static final Logger log = LoggerFactory.getLogger(UserDaoImpl.class);
    private final SessionFactory sessionFactory = HibernateUtil.getSessionFactory();

    @Override
    public User createUser(User user) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.persist(user);
            transaction.commit();
            return user;
        } catch (HibernateException e) {
            if (transaction != null){
                transaction.rollback();
            }
            log.error("Hibernate error", e);
            throw new RuntimeException(e);
        } catch (Exception e) {
            if (transaction != null){
                transaction.rollback();
            }
            log.error("Error creating user", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public User getUser(Long id) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            User search = session.find(User.class, id);
            transaction.commit();
            return search;
        } catch (HibernateException e) {
            if (transaction != null){
                transaction.rollback();
            }
            log.error("HibernateException", e);
            throw new RuntimeException(e);
        } catch (Exception e) {
            if(transaction != null){
                transaction.rollback();
            }
            log.error("Error getting user", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public User updateUser(User user) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            User updated = session.merge(user);
            transaction.commit();
            return updated;
        } catch (HibernateException e) {
            if (transaction != null){
                transaction.rollback();
            }
            log.error("HibernateException", e);
            throw new RuntimeException(e);
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            log.error("Error updating user", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteUser(Long id) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            User user = session.find(User.class, id);
            session.remove(user);
            transaction.commit();
        } catch (HibernateException e) {
            if (transaction != null){
                transaction.rollback();
            }
            log.error("HibernateException", e);
            throw  new RuntimeException(e);
        } catch (Exception e) {
            if (transaction != null){
                transaction.rollback();
            }
            log.error("Error deleting user", e);
            throw new RuntimeException(e);
        }
    }
}