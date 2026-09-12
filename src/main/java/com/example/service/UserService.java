package com.example.service;

import com.example.entity.User;

import java.util.List;

public interface UserService {
    User createUser(String name, String email, Integer age);
    User getUserById(Long id);
    List<User> getAllUsers();
    User updateUser(Long id, String name, String email, Integer age);
    void deleteUser(Long id);
}