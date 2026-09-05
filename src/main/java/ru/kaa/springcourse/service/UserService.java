package ru.kaa.springcourse.service;

import ru.kaa.springcourse.dao.UserDao;
import ru.kaa.springcourse.entity.User;

public class UserService {
    private UserDao userDAO;

    public UserService(UserDao userDao) {
        this.userDAO = userDao;
    }

    public User createUser(User user){
        return userDAO.createUser(user);
    }

    public User getUser(Long id){
        return userDAO.getUser(id);
    }

    public User updateUser(User user){
        return userDAO.updateUser(user);
    }

    public void delete(Long id){
        userDAO.deleteUser(id);
    }
}
