package com.example.mapper;

import com.example.dto.UserCreateRequest;
import com.example.dto.UserResponse;
import com.example.dto.UserUpdateRequest;
import com.example.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User toEntity(UserCreateRequest request) {
        return new User(request.name(), request.email(), request.age());
    }

    public void updateEntity(User user, UserUpdateRequest request) {
        user.setName(request.name());
        user.setEmail(request.email());
        user.setAge(request.age());
    }

    public UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getAge(),
                user.getCreatedAt()
        );
    }
}