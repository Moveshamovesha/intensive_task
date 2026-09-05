package com.example;

import com.example.entity.User;
import com.example.util.HibernateUtil;

import org.hibernate.Session;
import org.hibernate.Transaction;

public class TestConnection {

    public static void main(String[] args) {

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {

            Transaction transaction = session.beginTransaction();

            User user = new User("Тест Тестов", "test@example.com", 25);

            session.persist(user);

            transaction.commit();

            System.out.println("Сохранили пользователя с id = " + user.getId());

        } catch (Exception e) {
            System.out.println("ОШИБКА: " + e.getMessage());
            e.printStackTrace();
        } finally {
            HibernateUtil.shutdown();
        }
    }
}