package com.arthurfrei.security.service;

import com.arthurfrei.security.store.entity.Role;
import com.arthurfrei.security.store.entity.Status;
import com.arthurfrei.security.store.entity.User;
import com.arthurfrei.security.store.repository.RoleRepository;
import com.arthurfrei.security.store.repository.UserRepository;
import com.arthurfrei.user.api.dto.NewUserDto;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UserServiceTest {

    static Role role;
    static User newUser;
    static UserService userService;
    static UserRepository userRepository;
    static RoleRepository roleRepository;
    static BCryptPasswordEncoder passwordEncoder;

    static long id = 555L;
    static String username = "username";
    static String firstName = "firstName";
    static String lastName = "lastName";
    static String email = "email";
    static String password = "password";
    static String encodePassword = "dDGKJGEDrb4376dcHGd";


    @BeforeAll
    static void init() {
        userRepository = mock(UserRepository.class);
        roleRepository = mock(RoleRepository.class);
        passwordEncoder = mock(BCryptPasswordEncoder.class);

        role = new Role("ROLE_USER", null);
        userService = new UserService.Base(userRepository, roleRepository, passwordEncoder);
        newUser = new User(username, firstName, lastName, email, encodePassword, Set.of(new Role("ROLE_USER", null)));
        newUser.setId(id);
    }

    @Test
    @DisplayName("Проверка регистрации учетки")
    void register() {
        newUser.setStatus(Status.ACTIVE);
        NewUserDto userDto = new NewUserDto(username, firstName, lastName, email, password);

        when(passwordEncoder.encode(password)).thenReturn(encodePassword);
        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.of(role));
        when(userRepository.save(any())).thenReturn(newUser);

        User user = userService.register(userDto);

        assertEquals(user.getId(), id);
        assertEquals(user.getUsername(), username);
        assertEquals(user.getFirstName(), firstName);
        assertEquals(user.getLastName(), lastName);
        assertEquals(user.getEmail(), email);
        assertEquals(user.getPassword(), encodePassword);
        assertEquals(user.getRoles(), Set.of(role));
        assertEquals(user.getStatus(), Status.ACTIVE);
    }

    @Test
    @DisplayName("Получение получения всех учеток")
    void getAll() {
        newUser.setStatus(Status.ACTIVE);

        when(userRepository.findAll()).thenReturn(List.of(newUser, newUser, newUser, newUser, newUser));

        userService.getAll().forEach(it -> {
            assertEquals(it.getId(), id);
            assertEquals(it.getUsername(), username);
            assertEquals(it.getFirstName(), firstName);
            assertEquals(it.getLastName(), lastName);
            assertEquals(it.getEmail(), email);
            assertEquals(it.getPassword(), encodePassword);
            assertEquals(it.getRoles(), Set.of(role));
            assertEquals(it.getStatus(), Status.ACTIVE);
        });
    }

    @Test
    @DisplayName("Проверка получения активных учеток")
    void getActiveUsers() {
        newUser.setStatus(Status.ACTIVE);

        when(userRepository.findAllByStatus(Status.ACTIVE))
                .thenReturn(Optional.of(List.of(newUser, newUser, newUser, newUser, newUser)));

        userService.getActiveUsers().forEach(it -> {
            assertEquals(it.getId(), id);
            assertEquals(it.getUsername(), username);
            assertEquals(it.getFirstName(), firstName);
            assertEquals(it.getLastName(), lastName);
            assertEquals(it.getEmail(), email);
            assertEquals(it.getPassword(), encodePassword);
            assertEquals(it.getRoles(), Set.of(role));
            assertEquals(it.getStatus(), Status.ACTIVE);
        });
    }

    @Test
    @DisplayName("Проверка получения неактивных учеток")
    void getNotActiveUsers() {
        newUser.setStatus(Status.NOT_ACTIVE);

        when(userRepository.findAllByStatus(Status.ACTIVE))
            .thenReturn(Optional.of(List.of(newUser, newUser, newUser, newUser, newUser)));

        userService.getActiveUsers().forEach(it -> {
            assertEquals(it.getId(), id);
            assertEquals(it.getUsername(), username);
            assertEquals(it.getFirstName(), firstName);
            assertEquals(it.getLastName(), lastName);
            assertEquals(it.getEmail(), email);
            assertEquals(it.getPassword(), encodePassword);
            assertEquals(it.getRoles(), Set.of(role));
            assertEquals(it.getStatus(), Status.NOT_ACTIVE);
        });
    }

    @Test
    @DisplayName("Получение учетки по username")
    void findByUsername() {
        newUser.setStatus(Status.ACTIVE);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(newUser));

        User user = userService.findByUsername(username);

        assertEquals(user.getId(), id);
        assertEquals(user.getUsername(), username);
        assertEquals(user.getFirstName(), firstName);
        assertEquals(user.getLastName(), lastName);
        assertEquals(user.getEmail(), email);
        assertEquals(user.getPassword(), encodePassword);
        assertEquals(user.getRoles(), Set.of(role));
        assertEquals(user.getStatus(), Status.ACTIVE);

    }

    @Test
    @DisplayName("Получение ошибки при попытке получения учетки по username")
    void getErrorWhenFindingByUsername() {
        newUser.setStatus(Status.ACTIVE);

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        UsernameNotFoundException _throws = assertThrows(
                UsernameNotFoundException.class,
                () -> userService.findByUsername(username));

        assertEquals(_throws.getMessage(), "User with username '" + username + "' not found");
    }

    @Test
    @DisplayName("Получение учетки по id")
    void findById() {
        when(userRepository.findById(any())).thenReturn(Optional.of(newUser));
        User user = userService.findById(id);

        assertEquals(user.getId(), id);
        assertEquals(user.getUsername(), username);
        assertEquals(user.getFirstName(), firstName);
        assertEquals(user.getLastName(), lastName);
        assertEquals(user.getEmail(), email);
        assertEquals(user.getPassword(), encodePassword);
        assertEquals(user.getRoles(), Set.of(role));

        when(userRepository.findById(any())).thenReturn(Optional.empty());

        assertNull(userService.findById(any()));
    }

    @Test
    @DisplayName("Успешное удаление учетки по id")
    void successfulRemoval() {
        when(userRepository.findById(any())).thenReturn(Optional.of(newUser));
        when(userRepository.save(newUser)).thenReturn(newUser);

        assertTrue(userService.delete(id));
    }

    @Test
    @DisplayName("Успешное удаление учетки по id")
    void notSuccessfulRemoval() {
        when(userRepository.findById(any())).thenReturn(Optional.empty());
        assertFalse(userService.delete(1L));

        newUser.setStatus(Status.DELETE);
        when(userRepository.findById(any())).thenReturn(Optional.of(newUser));
        assertFalse(userService.delete(1L));
    }
}