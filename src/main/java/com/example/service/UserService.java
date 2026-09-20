package com.example.service;

import com.example.dto.UserCreateRequest;
import com.example.dto.UserResponse;
import com.example.dto.UserUpdateRequest;

import java.util.List;

public interface UserService {
    List<UserResponse> getAll();
    UserResponse getById(Long id);
    UserResponse create(UserCreateRequest request);
    UserResponse update(Long id, UserUpdateRequest request);
    void delete(Long id);
}
