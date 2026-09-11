package com.example.service;

import com.example.dao.UserDao;
import com.example.entity.User;
import com.example.exception.UserNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserDao userDao;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void createUser_validData_savesUser() {
        when(userDao.save(any(User.class))).thenAnswer(invocation -> {
            User u = invocation.getArgument(0);
            u.setId(1L);
            return u;
        });

        User result = userService.createUser("Ivan", "ivan@mail.com", 25);

        assertEquals(1L, result.getId());
        assertEquals("Ivan", result.getName());
        assertEquals("ivan@mail.com", result.getEmail());
        assertEquals(25, result.getAge());
        verify(userDao, times(1)).save(any(User.class));
    }

    @Test
    void createUser_emptyName_throwsException() {
        assertThrows(IllegalArgumentException.class,
                () -> userService.createUser("", "ivan@mail.com", 25));
        verifyNoInteractions(userDao);
    }

    @Test
    void createUser_invalidEmail_throwsException() {
        assertThrows(IllegalArgumentException.class,
                () -> userService.createUser("Ivan", "not-an-email", 25));
        verifyNoInteractions(userDao);
    }

    @Test
    void createUser_invalidAge_throwsException() {
        assertThrows(IllegalArgumentException.class,
                () -> userService.createUser("Ivan", "ivan@mail.com", -5));
        verifyNoInteractions(userDao);
    }

    @Test
    void getUserById_existingId_returnsUser() {
        User user = new User("Ivan", "ivan@mail.com", 25);
        user.setId(1L);
        when(userDao.findById(1L)).thenReturn(Optional.of(user));

        User result = userService.getUserById(1L);

        assertEquals(1L, result.getId());
        assertEquals("Ivan", result.getName());
    }

    @Test
    void getUserById_missingId_throwsUserNotFoundException() {
        when(userDao.findById(99L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUserById(99L));
    }

    @Test
    void getAllUsers_returnsListFromDao() {
        when(userDao.findAll()).thenReturn(List.of(
                new User("Ivan", "ivan@mail.com", 25),
                new User("Maria", "maria@mail.com", 30)));

        List<User> result = userService.getAllUsers();

        assertEquals(2, result.size());
    }

    @Test
    void updateUser_existingUser_updatesOnlyGivenFields() {
        User existing = new User("Old name", "old@mail.com", 20);
        existing.setId(1L);
        when(userDao.findById(1L)).thenReturn(Optional.of(existing));
        when(userDao.update(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User result = userService.updateUser(1L, "New name", null, null);

        assertEquals("New name", result.getName());
        assertEquals("old@mail.com", result.getEmail());
        assertEquals(20, result.getAge());
        verify(userDao, times(1)).update(existing);
    }

    @Test
    void updateUser_missingUser_throwsAndDoesNotUpdate() {
        when(userDao.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> userService.updateUser(1L, "New name", null, null));
        verify(userDao, never()).update(any(User.class));
    }

    @Test
    void deleteUser_existingUser_callsDaoDelete() {
        when(userDao.findById(1L)).thenReturn(Optional.of(new User("Ivan", "ivan@mail.com", 25)));

        userService.deleteUser(1L);

        verify(userDao, times(1)).deleteById(1L);
    }

    @Test
    void deleteUser_missingUser_throwsAndDoesNotDelete() {
        when(userDao.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.deleteUser(1L));
        verify(userDao, never()).deleteById(anyLong());
    }
}