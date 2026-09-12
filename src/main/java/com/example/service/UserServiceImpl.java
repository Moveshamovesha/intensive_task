package com.example.service;

import com.example.dao.UserDao;
import com.example.entity.User;
import com.example.exception.UserNotFoundException;

import java.util.List;

public class UserServiceImpl implements UserService {

    private final UserDao userDao;

    public UserServiceImpl(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public User createUser(String name, String email, Integer age) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Имя не должно быть пустым");
        }
        if (email == null || !email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            throw new IllegalArgumentException("Некорректный email");
        }
        if (age != null && (age < 0 || age > 150)) {
            throw new IllegalArgumentException("Некорректный возраст");
        }
        return userDao.save(new User(name, email, age));
    }

    @Override
    public User getUserById(Long id) {
        return userDao.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден: id=" + id));
    }

    @Override
    public List<User> getAllUsers() {
        return userDao.findAll();
    }

    @Override
    public User updateUser(Long id, String name, String email, Integer age) {
        User user = getUserById(id);
        if (name != null && !name.isBlank()) {
            user.setName(name);
        }
        if (email != null && !email.isBlank()) {
            user.setEmail(email);
        }
        if (age != null) {
            user.setAge(age);
        }
        return userDao.update(user);
    }

    @Override
    public void deleteUser(Long id) {
        getUserById(id);
        userDao.deleteById(id);
    }
}