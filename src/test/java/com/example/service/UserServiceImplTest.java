package com.example.service;

import com.example.dto.UserCreateRequest;
import com.example.dto.UserResponse;
import com.example.dto.UserUpdateRequest;
import com.example.entity.User;
import com.example.exception.EmailAlreadyExistsException;
import com.example.exception.UserNotFoundException;
import com.example.mapper.UserMapper;
import com.example.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Spy
    private UserMapper userMapper = new UserMapper();

    @InjectMocks
    private UserServiceImpl userService;

    private User userWithId(Long id, String name, String email, Integer age) {
        User user = new User(name, email, age);
        ReflectionTestUtils.setField(user, "id", id);
        return user;
    }

    @Test
    void create_savesUser_whenEmailIsFree() {
        UserCreateRequest request = new UserCreateRequest("Ivan", "ivan@example.com", 25);
        when(userRepository.existsByEmail("ivan@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserResponse response = userService.create(request);

        assertEquals("Ivan", response.name());
        assertEquals("ivan@example.com", response.email());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void create_throws_whenEmailTaken() {
        UserCreateRequest request = new UserCreateRequest("Ivan", "ivan@example.com", 25);
        when(userRepository.existsByEmail("ivan@example.com")).thenReturn(true);

        assertThrows(EmailAlreadyExistsException.class, () -> userService.create(request));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void getById_throws_whenNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getById(99L));
    }

    @Test
    void getAll_returnsMappedUsers() {
        when(userRepository.findAll()).thenReturn(List.of(userWithId(1L, "Ivan", "ivan@example.com", 25)));

        List<UserResponse> all = userService.getAll();

        assertEquals(1, all.size());
        assertEquals("ivan@example.com", all.get(0).email());
    }

    @Test
    void update_throws_whenEmailBelongsToAnotherUser() {
        User existing = userWithId(1L, "Ivan", "ivan@example.com", 25);
        User another = userWithId(2L, "Petr", "petr@example.com", 30);
        UserUpdateRequest request = new UserUpdateRequest("Ivan", "petr@example.com", 25);
        when(userRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(userRepository.findByEmail("petr@example.com")).thenReturn(Optional.of(another));

        assertThrows(EmailAlreadyExistsException.class, () -> userService.update(1L, request));
    }

    @Test
    void update_allowsKeepingOwnEmail() {
        User existing = userWithId(1L, "Ivan", "ivan@example.com", 25);
        UserUpdateRequest request = new UserUpdateRequest("Ivan Petrov", "ivan@example.com", 26);
        when(userRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(userRepository.findByEmail("ivan@example.com")).thenReturn(Optional.of(existing));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserResponse response = userService.update(1L, request);

        assertEquals("Ivan Petrov", response.name());
        assertEquals(26, response.age());
    }

    @Test
    void delete_throws_whenNotFound() {
        when(userRepository.existsById(99L)).thenReturn(false);

        assertThrows(UserNotFoundException.class, () -> userService.delete(99L));
        verify(userRepository, never()).deleteById(any());
    }
}