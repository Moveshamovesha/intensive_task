package com.example.controller;

import com.example.dto.UserCreateRequest;
import com.example.dto.UserResponse;
import com.example.dto.UserUpdateRequest;
import com.example.exception.EmailAlreadyExistsException;
import com.example.exception.UserNotFoundException;
import com.example.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    private UserResponse sampleUser() {
        return new UserResponse(1L, "Ivan", "ivan@example.com", 25, LocalDateTime.of(2024, 1, 1, 12, 0));
    }

    @Nested
    @DisplayName("GET /api/users")
    class GetAllTests {

        @Test
        void returnsEmptyList() throws Exception {
            when(userService.getAll()).thenReturn(List.of());

            mockMvc.perform(get("/api/users"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(0)));
        }

        @Test
        void returnsSingleUser() throws Exception {
            when(userService.getAll()).thenReturn(List.of(sampleUser()));

            mockMvc.perform(get("/api/users"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].id", is(1)))
                    .andExpect(jsonPath("$[0].name", is("Ivan")))
                    .andExpect(jsonPath("$[0].email", is("ivan@example.com")))
                    .andExpect(jsonPath("$[0].age", is(25)));
        }

        @Test
        void returnsMultipleUsersPreservingOrder() throws Exception {
            UserResponse second = new UserResponse(2L, "Petr", "petr@example.com", 30,
                    LocalDateTime.of(2024, 2, 2, 10, 0));
            when(userService.getAll()).thenReturn(List.of(sampleUser(), second));

            mockMvc.perform(get("/api/users"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].name", is("Ivan")))
                    .andExpect(jsonPath("$[1].name", is("Petr")));
        }

        @Test
        void returnsJsonContentType() throws Exception {
            when(userService.getAll()).thenReturn(List.of(sampleUser()));

            mockMvc.perform(get("/api/users"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
        }
    }

    @Nested
    @DisplayName("GET /api/users/{id}")
    class GetByIdTests {

        @Test
        void returnsAllFields() throws Exception {
            when(userService.getById(1L)).thenReturn(sampleUser());

            mockMvc.perform(get("/api/users/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(1)))
                    .andExpect(jsonPath("$.name", is("Ivan")))
                    .andExpect(jsonPath("$.email", is("ivan@example.com")))
                    .andExpect(jsonPath("$.age", is(25)))
                    .andExpect(jsonPath("$.createdAt", is("2024-01-01T12:00:00")));
        }

        @Test
        void returns404_whenNotFound() throws Exception {
            when(userService.getById(99L)).thenThrow(new UserNotFoundException("Пользователь не найден: id=99"));

            mockMvc.perform(get("/api/users/99"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status", is(404)))
                    .andExpect(jsonPath("$.message", is("Пользователь не найден: id=99")));
        }

        @Test
        void returns404_withTimestamp() throws Exception {
            when(userService.getById(99L)).thenThrow(new UserNotFoundException("Пользователь не найден: id=99"));

            mockMvc.perform(get("/api/users/99"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.timestamp").exists())
                    .andExpect(jsonPath("$.status", is(404)));
        }

        @Test
        void returns400_whenIdIsNotNumeric() throws Exception {
            mockMvc.perform(get("/api/users/abc"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        void delegatesAnyIdToService() throws Exception {
            when(userService.getById(0L)).thenReturn(sampleUser());

            mockMvc.perform(get("/api/users/0"))
                    .andExpect(status().isOk());

            verify(userService).getById(0L);
        }

        @Test
        void supportsLargeId() throws Exception {
            when(userService.getById(Long.MAX_VALUE)).thenReturn(sampleUser());

            mockMvc.perform(get("/api/users/" + Long.MAX_VALUE))
                    .andExpect(status().isOk());

            verify(userService).getById(Long.MAX_VALUE);
        }
    }

    @Nested
    @DisplayName("POST /api/users")
    class CreateTests {

        @Test
        void valid_returns201() throws Exception {
            UserCreateRequest request = new UserCreateRequest("Ivan", "ivan@example.com", 25);
            when(userService.create(any(UserCreateRequest.class))).thenReturn(sampleUser());

            mockMvc.perform(post("/api/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id", is(1)))
                    .andExpect(jsonPath("$.name", is("Ivan")))
                    .andExpect(jsonPath("$.email", is("ivan@example.com")));
        }

        @Test
        void valid_bodyContainsCreatedAt() throws Exception {
            UserCreateRequest request = new UserCreateRequest("Ivan", "ivan@example.com", 25);
            when(userService.create(any(UserCreateRequest.class))).thenReturn(sampleUser());

            mockMvc.perform(post("/api/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.createdAt", is("2024-01-01T12:00:00")));
        }

        @Test
        void passesRequestBodyToService() throws Exception {
            UserCreateRequest request = new UserCreateRequest("Ivan", "ivan@example.com", 25);
            when(userService.create(any(UserCreateRequest.class))).thenReturn(sampleUser());

            mockMvc.perform(post("/api/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated());

            ArgumentCaptor<UserCreateRequest> captor = ArgumentCaptor.forClass(UserCreateRequest.class);
            verify(userService).create(captor.capture());
            assertEquals("Ivan", captor.getValue().name());
            assertEquals("ivan@example.com", captor.getValue().email());
            assertEquals(25, captor.getValue().age());
        }

        @Test
        void ageZero_accepted() throws Exception {
            when(userService.create(any(UserCreateRequest.class))).thenReturn(sampleUser());

            mockMvc.perform(post("/api/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"name\":\"Ivan\",\"email\":\"ivan@example.com\",\"age\":0}"))
                    .andExpect(status().isCreated());
        }

        @Test
        void age150_accepted() throws Exception {
            when(userService.create(any(UserCreateRequest.class))).thenReturn(sampleUser());

            mockMvc.perform(post("/api/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"name\":\"Ivan\",\"email\":\"ivan@example.com\",\"age\":150}"))
                    .andExpect(status().isCreated());
        }

        @Test
        void age151_rejected() throws Exception {
            mockMvc.perform(post("/api/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"name\":\"Ivan\",\"email\":\"ivan@example.com\",\"age\":151}"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.validationErrors.age").exists());
        }

        @Test
        void negativeAge_rejected() throws Exception {
            mockMvc.perform(post("/api/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"name\":\"Ivan\",\"email\":\"ivan@example.com\",\"age\":-1}"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.validationErrors.age").exists());
        }

        @Test
        void missingAge_rejected() throws Exception {
            mockMvc.perform(post("/api/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"name\":\"Ivan\",\"email\":\"ivan@example.com\"}"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.validationErrors.age").exists());
        }

        @Test
        void nullName_rejected() throws Exception {
            mockMvc.perform(post("/api/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"name\":null,\"email\":\"ivan@example.com\",\"age\":25}"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.validationErrors.name").exists());
        }

        @Test
        void emptyName_rejected() throws Exception {
            mockMvc.perform(post("/api/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"name\":\"\",\"email\":\"ivan@example.com\",\"age\":25}"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.validationErrors.name").exists());
        }

        @Test
        void blankName_rejected() throws Exception {
            mockMvc.perform(post("/api/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"name\":\"   \",\"email\":\"ivan@example.com\",\"age\":25}"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.validationErrors.name").exists());
        }

        @Test
        void nameOf100Chars_accepted() throws Exception {
            when(userService.create(any(UserCreateRequest.class))).thenReturn(sampleUser());
            String longName = "a".repeat(100);

            mockMvc.perform(post("/api/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"name\":\"" + longName + "\",\"email\":\"ivan@example.com\",\"age\":25}"))
                    .andExpect(status().isCreated());
        }

        @Test
        void nameOf101Chars_rejected() throws Exception {
            String longName = "a".repeat(101);

            mockMvc.perform(post("/api/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"name\":\"" + longName + "\",\"email\":\"ivan@example.com\",\"age\":25}"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.validationErrors.name").exists());
        }

        @Test
        void nullEmail_rejected() throws Exception {
            mockMvc.perform(post("/api/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"name\":\"Ivan\",\"email\":null,\"age\":25}"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.validationErrors.email").exists());
        }

        @Test
        void emptyEmail_rejected() throws Exception {
            mockMvc.perform(post("/api/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"name\":\"Ivan\",\"email\":\"\",\"age\":25}"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.validationErrors.email").exists());
        }

        @Test
        void emailWithoutAt_rejected() throws Exception {
            mockMvc.perform(post("/api/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"name\":\"Ivan\",\"email\":\"not-an-email\",\"age\":25}"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.validationErrors.email").exists());
        }

        @Test
        void allFieldsInvalid_reportsAllFields() throws Exception {
            UserCreateRequest request = new UserCreateRequest("", "not-an-email", -5);

            mockMvc.perform(post("/api/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status", is(400)))
                    .andExpect(jsonPath("$.validationErrors.name").exists())
                    .andExpect(jsonPath("$.validationErrors.email").exists())
                    .andExpect(jsonPath("$.validationErrors.age").exists());
        }

        @Test
        void invalid_doesNotCallService() throws Exception {
            UserCreateRequest request = new UserCreateRequest("", "not-an-email", -5);

            mockMvc.perform(post("/api/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(userService);
        }

        @Test
        void duplicateEmail_returns409() throws Exception {
            UserCreateRequest request = new UserCreateRequest("Ivan", "ivan@example.com", 25);
            when(userService.create(any(UserCreateRequest.class)))
                    .thenThrow(new EmailAlreadyExistsException("ivan@example.com"));

            mockMvc.perform(post("/api/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.status", is(409)))
                    .andExpect(jsonPath("$.message", containsString("ivan@example.com")))
                    .andExpect(jsonPath("$.timestamp").exists());
        }

        @Test
        void malformedJson_returns400() throws Exception {
            mockMvc.perform(post("/api/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{not valid json"))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("PUT /api/users/{id}")
    class UpdateTests {

        @Test
        void valid_returns200AndUpdatedBody() throws Exception {
            UserUpdateRequest request = new UserUpdateRequest("Petr", "petr@example.com", 30);
            UserResponse updated = new UserResponse(1L, "Petr", "petr@example.com", 30,
                    LocalDateTime.of(2024, 1, 1, 12, 0));
            when(userService.update(eq(1L), any(UserUpdateRequest.class))).thenReturn(updated);

            mockMvc.perform(put("/api/users/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(1)))
                    .andExpect(jsonPath("$.name", is("Petr")))
                    .andExpect(jsonPath("$.email", is("petr@example.com")))
                    .andExpect(jsonPath("$.age", is(30)));
        }

        @Test
        void returns404_whenNotFound() throws Exception {
            UserUpdateRequest request = new UserUpdateRequest("Petr", "petr@example.com", 30);
            when(userService.update(eq(99L), any(UserUpdateRequest.class)))
                    .thenThrow(new UserNotFoundException("Пользователь не найден: id=99"));

            mockMvc.perform(put("/api/users/99")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status", is(404)));
        }

        @Test
        void invalid_returns400() throws Exception {
            UserUpdateRequest request = new UserUpdateRequest("", "bad", -1);

            mockMvc.perform(put("/api/users/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.validationErrors.name").exists())
                    .andExpect(jsonPath("$.validationErrors.email").exists())
                    .andExpect(jsonPath("$.validationErrors.age").exists());
        }

        @Test
        void invalid_doesNotCallService() throws Exception {
            UserUpdateRequest request = new UserUpdateRequest("", "bad", -1);

            mockMvc.perform(put("/api/users/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());

            verify(userService, never()).update(any(), any());
        }

        @Test
        void age150_accepted() throws Exception {
            UserResponse updated = new UserResponse(1L, "Petr", "petr@example.com", 150,
                    LocalDateTime.of(2024, 1, 1, 12, 0));
            when(userService.update(eq(1L), any(UserUpdateRequest.class))).thenReturn(updated);

            mockMvc.perform(put("/api/users/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"name\":\"Petr\",\"email\":\"petr@example.com\",\"age\":150}"))
                    .andExpect(status().isOk());
        }

        @Test
        void age151_rejected() throws Exception {
            mockMvc.perform(put("/api/users/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"name\":\"Petr\",\"email\":\"petr@example.com\",\"age\":151}"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.validationErrors.age").exists());
        }

        @Test
        void emailConflict_returns409() throws Exception {
            UserUpdateRequest request = new UserUpdateRequest("Petr", "petr@example.com", 30);
            when(userService.update(eq(1L), any(UserUpdateRequest.class)))
                    .thenThrow(new EmailAlreadyExistsException("petr@example.com"));

            mockMvc.perform(put("/api/users/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.status", is(409)));
        }

        @Test
        void passesPathIdAndBodyToService() throws Exception {
            UserUpdateRequest request = new UserUpdateRequest("Petr", "petr@example.com", 30);
            UserResponse updated = new UserResponse(7L, "Petr", "petr@example.com", 30,
                    LocalDateTime.of(2024, 1, 1, 12, 0));
            when(userService.update(eq(7L), any(UserUpdateRequest.class))).thenReturn(updated);

            mockMvc.perform(put("/api/users/7")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk());

            ArgumentCaptor<UserUpdateRequest> captor = ArgumentCaptor.forClass(UserUpdateRequest.class);
            verify(userService).update(eq(7L), captor.capture());
            assertEquals("Petr", captor.getValue().name());
            assertEquals("petr@example.com", captor.getValue().email());
            assertEquals(30, captor.getValue().age());
        }

        @Test
        void missingBody_returns400() throws Exception {
            mockMvc.perform(put("/api/users/1")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());
        }

        @Test
        void nonNumericId_returns400() throws Exception {
            mockMvc.perform(put("/api/users/abc")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"name\":\"Petr\",\"email\":\"petr@example.com\",\"age\":30}"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        void nameOf101Chars_rejected() throws Exception {
            String longName = "a".repeat(101);

            mockMvc.perform(put("/api/users/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"name\":\"" + longName + "\",\"email\":\"petr@example.com\",\"age\":30}"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.validationErrors.name").exists());
        }
    }

    @Nested
    @DisplayName("DELETE /api/users/{id}")
    class DeleteTests {

        @Test
        void existing_returns204WithEmptyBody() throws Exception {
            mockMvc.perform(delete("/api/users/1"))
                    .andExpect(status().isNoContent())
                    .andExpect(content().string(""));
        }

        @Test
        void notFound_returns404() throws Exception {
            doThrow(new UserNotFoundException("Пользователь не найден: id=99")).when(userService).delete(99L);

            mockMvc.perform(delete("/api/users/99"))
                    .andExpect(status().isNotFound());
        }

        @Test
        void notFound_bodyHasMessage() throws Exception {
            doThrow(new UserNotFoundException("Пользователь не найден: id=99")).when(userService).delete(99L);

            mockMvc.perform(delete("/api/users/99"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status", is(404)))
                    .andExpect(jsonPath("$.message", is("Пользователь не найден: id=99")));
        }

        @Test
        void delegatesToServiceExactlyOnce() throws Exception {
            mockMvc.perform(delete("/api/users/5"))
                    .andExpect(status().isNoContent());

            verify(userService, times(1)).delete(5L);
        }

        @Test
        void nonNumericId_returns400() throws Exception {
            mockMvc.perform(delete("/api/users/abc"))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("Общие случаи")
    class GeneralTests {

        @Test
        void missingBodyOnCreate_returns400() throws Exception {
            mockMvc.perform(post("/api/users")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());
        }

        @Test
        void wrongContentType_returns415() throws Exception {
            mockMvc.perform(post("/api/users")
                            .contentType(MediaType.TEXT_PLAIN)
                            .content("name=Ivan"))
                    .andExpect(status().isUnsupportedMediaType());
        }

        @Test
        void unknownJsonFields_areIgnored() throws Exception {
            when(userService.create(any(UserCreateRequest.class))).thenReturn(sampleUser());

            mockMvc.perform(post("/api/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"name\":\"Ivan\",\"email\":\"ivan@example.com\",\"age\":25,\"foo\":\"bar\"}"))
                    .andExpect(status().isCreated());
        }

        @Test
        void postToIdEndpoint_returns405() throws Exception {
            mockMvc.perform(post("/api/users/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"name\":\"Ivan\",\"email\":\"ivan@example.com\",\"age\":25}"))
                    .andExpect(status().isMethodNotAllowed());
        }
    }
}
