package ru.kaa.springcourse.dao;

import ru.kaa.springcourse.entity.User;

public interface UserDao {
    User createUser(User user);
    User getUser(Long id);
    User updateUser(User user);
    void deleteUser(Long id);
}
