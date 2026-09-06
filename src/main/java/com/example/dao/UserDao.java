package com.example.dao;

import com.example.entity.User;

import java.util.List;
import java.util.Optional;
/**
 * DAO-интерфейс для работы с сущностью User.
 * Определяет базовые CRUD-операции, скрывая от остального кода детали работы с базой данных.
 */
public interface UserDao {
public interface UserDao {
    /**
     * Сохраняет нового пользователя в базу данных.
     *
     * @param user пользователь без id (id сгенерирует база)
     * @return сохранённый пользователь с присвоенным id
     */
    User save(User user);
    /**
     * Ищет пользователя по идентификатору.
     *
     * @param id идентификатор пользователя
     * @return Optional с пользователем или пустой Optional, если не найден
     */
    Optional<User> findById(Long id);
    /**
     * Возвращает всех пользователей из базы.
     *
     * @return список пользователей (пустой, если записей нет)
     */
    List<User> findAll();
    /**
     * Обновляет данные существующего пользователя.
     *
     * @param user пользователь с изменёнными полями (id должен существовать в базе)
     * @return обновлённый пользователь
     */
    User update(User user);
    /**
     * Удаляет пользователя по идентификатору.
     * Если пользователя с таким id нет, метод ничего не делает.
     *
     * @param id идентификатор пользователя
     */
    void deleteById(Long id);
}