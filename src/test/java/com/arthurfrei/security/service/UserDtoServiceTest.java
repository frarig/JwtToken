package com.arthurfrei.security.service;

import com.arthurfrei.security.store.entity.Status;
import com.arthurfrei.security.store.entity.User;
import com.arthurfrei.security.store.repository.UserRepository;
import com.arthurfrei.user.api.dto.UserDto;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UserDtoServiceTest {

    static UserDtoService userDtoService;
    static UserRepository userRepository;
    static List<User> userDtoList;

    static long id = 123L;
    static String username = "username";
    static String firstName = "firstName";
    static String lastName = "lastName";
    static String email = "email";

    @BeforeAll
    static void init() {
        userRepository = mock(UserRepository.class);
        userDtoService = new UserDtoService.Base(userRepository);

        User user = new User(username, firstName, lastName, email, "password", null);
        user.setId(id);
        userDtoList = List.of(user, user, user, user, user, user);
    }

    @Test
    void getAll() {
        when(userRepository.findAll()).thenReturn(userDtoList);
    }

    @Test
    @DisplayName("Получение корректного списка учеток")
    void getAllUsersByActiveOrNotActive() {
        when(userRepository.findAllByStatus(Status.ACTIVE)).thenReturn(Optional.of(userDtoList));
        List<UserDto> activeUsers = userDtoService.getAllByActive(true);
        assert !activeUsers.isEmpty();

        assertEquals(userDtoList.size(), activeUsers.size());

        activeUsers.forEach(it -> {
            assertEquals(it.getId(), id);
            assertEquals(it.getUsername(), username);
            assertEquals(it.getFirstName(), firstName);
            assertEquals(it.getLastName(), lastName);
            assertEquals(it.getEmail(), email);
        });

        when(userRepository.findAllByStatus(Status.NOT_ACTIVE)).thenReturn(Optional.of(userDtoList));
        List<UserDto> notActiveUsers = userDtoService.getAllByActive(false);
        assert !notActiveUsers.isEmpty();

        assertEquals(userDtoList.size(), notActiveUsers.size());

        notActiveUsers.forEach(it -> {
            assertEquals(it.getId(), id);
            assertEquals(it.getUsername(), username);
            assertEquals(it.getFirstName(), firstName);
            assertEquals(it.getLastName(), lastName);
            assertEquals(it.getEmail(), email);
        });
    }

    @Test
    @DisplayName("Получение пустого списка учеток")
    void getEmptyListByActiveOrNotActive() {
        when(userRepository.findAllByStatus(Status.ACTIVE)).thenReturn(Optional.of(new ArrayList<>()));
        List<UserDto> activeUsers1 = userDtoService.getAllByActive(true);
        assert activeUsers1.isEmpty();

        when(userRepository.findAllByStatus(Status.ACTIVE)).thenReturn(Optional.empty());
        List<UserDto> activeUsers2 = userDtoService.getAllByActive(true);
        assert activeUsers2.isEmpty();

        when(userRepository.findAllByStatus(Status.NOT_ACTIVE)).thenReturn(Optional.of(new ArrayList<>()));
        List<UserDto> notActiveUsers1 = userDtoService.getAllByActive(true);
        assert notActiveUsers1.isEmpty();

        when(userRepository.findAllByStatus(Status.NOT_ACTIVE)).thenReturn(Optional.empty());
        List<UserDto> notActiveUsers2 = userDtoService.getAllByActive(true);
        assert notActiveUsers2.isEmpty();
    }
}