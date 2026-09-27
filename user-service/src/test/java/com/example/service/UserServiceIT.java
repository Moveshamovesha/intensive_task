package com.example.service;

import com.example.dto.UserCreateRequest;
import com.example.dto.UserResponse;
import com.example.dto.UserUpdateRequest;
import com.example.exception.UserNotFoundException;
import com.example.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Testcontainers
class UserServiceIT {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("user_db")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void create_shouldSaveUserToDatabase() {
        UserCreateRequest request = new UserCreateRequest("Test User", "testuser@example.com", 25);

        UserResponse response = userService.create(request);

        assertThat(response.id()).isNotNull();
        assertThat(response.name()).isEqualTo("Test User");
        assertThat(response.email()).isEqualTo("testuser@example.com");
        assertThat(userRepository.count()).isEqualTo(1);
    }

    @Test
    void getAll_shouldReturnAllUsers() {
        UserCreateRequest request1 = new UserCreateRequest("Test User 1", "testuser1@example.com", 25);
        UserCreateRequest request2 = new UserCreateRequest("Test User 2", "testuser2@example.com", 26);

        userService.create(request1);
        userService.create(request2);

        List<UserResponse> result = userService.getAll();

        assertThat(result).hasSize(2);
    }

    @Test
    void getById_shouldReturnUser_whenExists() {
        UserCreateRequest request = new UserCreateRequest("Test User", "testuser@example.com", 25);

        UserResponse created = userService.create(request);
        UserResponse found = userService.getById(created.id());

        assertThat(found.id()).isEqualTo(created.id());
        assertThat(found.name()).isEqualTo("Test User");
    }

    @Test
    void getById_shouldThrowUserNotFoundException_whenNotExists() {
        assertThatThrownBy(() -> userService.getById(999L))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("999");
    }

    @Test
    void update_shouldUpdateUserInDatabase() {
        UserCreateRequest createRequest = new UserCreateRequest("Test User", "testuser@example.com", 25);

        UserResponse created = userService.create(createRequest);

        UserUpdateRequest updateRequest = new UserUpdateRequest("Updated User", "updateduser@example.com", 30);

        UserResponse updated = userService.update(created.id(), updateRequest);

        assertThat(updated.name()).isEqualTo("Updated User");
        assertThat(updated.email()).isEqualTo("updateduser@example.com");
    }

    @Test
    void delete_shouldRemoveUserFromDatabase() {
        UserCreateRequest request = new UserCreateRequest("Test User", "testuser@example.com", 25);

        UserResponse created = userService.create(request);
        userService.delete(created.id());

        assertThat(userRepository.count()).isEqualTo(0);
    }

    @Test
    void delete_shouldThrowUserNotFoundException_whenNotExists() {
        assertThatThrownBy(() -> userService.delete(999L))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("999");
    }
}