package com.arthurfrei.admin.api.controller;

import com.arthurfrei.admin.api.dto.AdminDto;
import com.arthurfrei.security.service.UserDtoService;
import com.arthurfrei.security.service.UserService;
import com.arthurfrei.security.store.entity.Role;
import com.arthurfrei.security.store.entity.Status;
import com.arthurfrei.security.store.entity.User;
import com.arthurfrei.user.api.dto.UserDto;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AdminV1ControllerTest {

    static User userActive;

    static UserService userService;

    static UserDtoService userDtoService;

    static AdminV1Controller adminV1Controller;

    @BeforeAll
    static void mockServices() {
        userService = mock(UserService.class);
        userDtoService = mock(UserDtoService.class);
        adminV1Controller = new AdminV1Controller(userService, userDtoService);

        userActive = new User();
        userActive.setId(1L);
        userActive.setUsername("username");
        userActive.setFirstName("firstName");
        userActive.setLastName("lastName");
        userActive.setEmail("email");
        userActive.setRoles(Set.of(new Role("user", null)));
        userActive.setStatus(Status.ACTIVE);
    }

    @Test
    @DisplayName("Получение учетки по id")
    void getUserById_Ok() {
        when(userService.findById(1L)).thenReturn(userActive);
        ResponseEntity<AdminDto> ok = adminV1Controller.getUserById(1L);
        assertEquals(ok.getStatusCode(), HttpStatus.OK);

        AdminDto body = ok.getBody();
        assert body != null;
        assertEquals(body.getId(), userActive.getId());
        assertEquals(body.getUsername(), userActive.getUsername());
        assertEquals(body.getFirstName(), userActive.getFirstName());
        assertEquals(body.getLastName(), userActive.getLastName());
        assertEquals(body.getEmail(), userActive.getEmail());
        assertEquals(body.getStatus(), userActive.getStatus().name());
    }
    @Test
    @DisplayName("Получение null вместо учетки по id")
    void getUserById_NoContent() {
        when(userService.findById(null)).thenReturn(null);
        ResponseEntity<AdminDto> noContent = adminV1Controller.getUserById(null);
        assertEquals(noContent.getStatusCode(), HttpStatus.NO_CONTENT);
    }

    @Test
    @DisplayName("Получение полного списка учетки")
    void getAllUsers() {
        UserDto userDto = new UserDto(userActive);
        List<UserDto> _users = List.of(userDto, userDto, userDto, userDto, userDto);

        when(userDtoService.getAll()).thenReturn(_users);
        ResponseEntity<List<UserDto>> response = adminV1Controller.getUsers(null);

        assertEquals(response.getStatusCode(), HttpStatus.OK);

        List<UserDto> users = response.getBody();
        assert users != null;

        assertEquals(users.size(), 5);
        users.forEach(it -> {
            assertEquals(it.getId(), userDto.getId());
            assertEquals(it.getUsername(), userDto.getUsername());
            assertEquals(it.getFirstName(), userDto.getFirstName());
            assertEquals(it.getLastName(), userDto.getLastName());
            assertEquals(it.getEmail(), userDto.getEmail());
        });
    }

    @Test
    @DisplayName("Получение списка только активных учеток")
    void getActiveUsers() {
        UserDto userDto = new UserDto(userActive);
        List<UserDto> _users = List.of(userDto, userDto, userDto, userDto, userDto);

        when(userDtoService.getAllByActive(true)).thenReturn(_users);
        ResponseEntity<List<UserDto>> response = adminV1Controller.getUsers(true);

        assertEquals(response.getStatusCode(), HttpStatus.OK);

        List<UserDto> users = response.getBody();
        assert users != null;

        assertEquals(users.size(), 5);
        users.forEach(it -> {
            assertEquals(it.getId(), userDto.getId());
            assertEquals(it.getUsername(), userDto.getUsername());
            assertEquals(it.getFirstName(), userDto.getFirstName());
            assertEquals(it.getLastName(), userDto.getLastName());
            assertEquals(it.getEmail(), userDto.getEmail());
        });
    }

    @Test
    @DisplayName("Получение списка неактивных учеток")
    void getNoActiveUsers() {
        UserDto userDto = new UserDto(userActive);
        List<UserDto> _users = List.of(userDto, userDto, userDto, userDto, userDto);

        when(userDtoService.getAllByActive(false)).thenReturn(_users);
        ResponseEntity<List<UserDto>> response = adminV1Controller.getUsers(false);

        assertEquals(response.getStatusCode(), HttpStatus.OK);

        List<UserDto> users = response.getBody();
        assert users != null;

        assertEquals(users.size(), 5);
        users.forEach(it -> {
            assertEquals(it.getId(), userDto.getId());
            assertEquals(it.getUsername(), userDto.getUsername());
            assertEquals(it.getFirstName(), userDto.getFirstName());
            assertEquals(it.getLastName(), userDto.getLastName());
            assertEquals(it.getEmail(), userDto.getEmail());
        });
    }

    @Test
    @DisplayName("Успешное удаление учетки по id")
    void deleteUser_Ok() {
        when(userService.delete(any())).thenReturn(true);
        ResponseEntity<?> response = adminV1Controller.deleteUser(1L);
        assertEquals(response.getStatusCode(), HttpStatus.OK);
    }
    @Test
    @DisplayName("Удалить учетки по id не удалось")
    void deleteUser_BadRequest() {
        when(userService.delete(null)).thenReturn(false);
        ResponseEntity<?> response = adminV1Controller.deleteUser(null);
        assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
    }
}