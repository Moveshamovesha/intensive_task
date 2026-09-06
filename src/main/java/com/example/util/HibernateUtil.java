package com.example.util;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * Утилитный класс для работы с Hibernate.
 * Создаёт и хранит единственный на всё приложение SessionFactory (паттерн Singleton).
 * Настройки подключения читаются из файла hibernate.cfg.xml.
 */
public class HibernateUtil {

    private static final Logger log = LoggerFactory.getLogger(HibernateUtil.class);

    private static final SessionFactory sessionFactory = buildSessionFactory();
    /**
     * Создаёт SessionFactory по настройкам из hibernate.cfg.xml.
     * Вызывается один раз при загрузке класса.
     *
     * @return настроенная фабрика сессий
     * @throws ExceptionInInitializerError если подключиться к базе не удалось
     */
    private static SessionFactory buildSessionFactory() {
        try {
            return new Configuration().configure().buildSessionFactory();
        } catch (Throwable ex) {
            log.error("Ошибка создания SessionFactory", ex);
            throw new ExceptionInInitializerError(ex);
        }
    }
    /**
     * Возвращает единственный экземпляр SessionFactory.
     * Используется DAO-слоем для открытия сессий.
     *
     * @return фабрика сессий
     */
    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }
    /**
     * Закрывает SessionFactory и освобождает соединения с базой.
     * Необходимо вызвать перед завершением приложения.
     */
    public static void shutdown() {
        if (sessionFactory != null && !sessionFactory.isClosed()) {
            sessionFactory.close();
            log.info("SessionFactory закрыта");
        }
    }
    /**
     * Приватный конструктор: класс утилитный, создание экземпляров запрещено.
     */
    private HibernateUtil() {
    }
}