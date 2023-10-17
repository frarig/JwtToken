package com.arthurfrei.user.api.controller;

import com.arthurfrei.security.service.UserService;
import com.arthurfrei.security.store.entity.Role;
import com.arthurfrei.security.store.entity.Status;
import com.arthurfrei.security.store.entity.User;
import com.arthurfrei.user.api.dto.NewUserDto;
import com.arthurfrei.user.api.dto.UserDto;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UserV1ControllerTest {

    static User userActive;

    static UserService userService;

    static UserV1Controller userV1Controller;

    @BeforeAll
    static void mockServices() {
        userService = mock(UserService.class);
        userV1Controller = new UserV1Controller(userService);

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
        ResponseEntity<UserDto> ok = userV1Controller.getUserById(1L);
        assertEquals(ok.getStatusCode(), HttpStatus.OK);

        UserDto body = ok.getBody();
        assert body != null;
        assertEquals(body.getId(), userActive.getId());
        assertEquals(body.getUsername(), userActive.getUsername());
        assertEquals(body.getFirstName(), userActive.getFirstName());
        assertEquals(body.getLastName(), userActive.getLastName());
        assertEquals(body.getEmail(), userActive.getEmail());
    }
    @Test
    @DisplayName("Получение null вместо учетки по id")
    void getUserById_NoContent() {
        when(userService.findById(null)).thenReturn(null);
        ResponseEntity<UserDto> noContent =  userV1Controller.getUserById(null);
        assertEquals(noContent.getStatusCode(), HttpStatus.NO_CONTENT);
    }

    @Test
    void register() {
        String username = "username";
        String firstName = "firstName";
        String lastName = "lastName";
        String email = "email";
        String password = "password";

        NewUserDto userDto = new NewUserDto(username, firstName, lastName, email, password);
        User user = new User();
        user.setUsername(username);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setPassword(password);

        when(userService.register(userDto)).thenReturn(user);
        ResponseEntity<UserDto> response = userV1Controller.register(userDto);

        assertEquals(response.getStatusCode(), HttpStatus.OK);

        UserDto _userDto = response.getBody();
        assert _userDto != null;

        assertEquals(_userDto.getUsername(), username);
        assertEquals(_userDto.getFirstName(), firstName);
        assertEquals(_userDto.getLastName(), lastName);
        assertEquals(_userDto.getEmail(), email);
    }
}