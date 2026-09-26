package com.example.service;

import com.example.dto.UserCreateRequest;
import com.example.dto.UserResponse;
import com.example.dto.UserUpdateRequest;
import com.example.entity.User;
import com.example.event.UserEvent;
import com.example.event.UserOperation;
import com.example.exception.EmailAlreadyExistsException;
import com.example.exception.UserNotFoundException;
import com.example.mapper.UserMapper;
import com.example.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final KafkaTemplate<String, UserEvent> kafkaTemplate;
    @Value("${app.kafka.topic.user-events}")
    private String userEvents;


    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper, KafkaTemplate<String, UserEvent> kafkaTemplate) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getAll() {
        return userRepository.findAll().stream()
                .map(userMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getById(Long id) {
        return userRepository.findById(id)
                .map(userMapper::toResponse)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден: id=" + id));
    }

    @Override
    public UserResponse create(UserCreateRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new EmailAlreadyExistsException(request.email());
        }
        User user = userMapper.toEntity(request);
        User saved = userRepository.save(user);
        kafkaTemplate.send(userEvents, new UserEvent(saved.getEmail(), UserOperation.CREATE));
        return userMapper.toResponse(saved);
    }

    @Override
    public UserResponse update(Long id, UserUpdateRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден: id=" + id));
        userRepository.findByEmail(request.email())
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new EmailAlreadyExistsException(request.email());
                });
        userMapper.updateEntity(user, request);
        User saved = userRepository.save(user);
        return userMapper.toResponse(saved);
    }

    @Override
    public void delete(Long id) {
        User user = userRepository.findById(id).orElseThrow(()-> new UserNotFoundException("Пользователь не найден: id= "
        +id));
        userRepository.deleteById(id);
        kafkaTemplate.send(userEvents, new UserEvent(user.getEmail(),UserOperation.DELETE));
    }
}