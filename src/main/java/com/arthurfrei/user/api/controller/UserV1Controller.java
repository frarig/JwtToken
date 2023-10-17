package com.arthurfrei.user.api.controller;

import com.arthurfrei.security.service.UserService;
import com.arthurfrei.security.store.entity.User;
import com.arthurfrei.user.api.dto.NewUserDto;
import com.arthurfrei.user.api.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserV1Controller {

    private final UserService userService;

    private static final String GET_USER_BY_ID = "/{id}";

    private static final String CREATE_USER = "/register";

    @GetMapping(GET_USER_BY_ID)
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
        final User user = userService.findById(id);

        if (user == null) return ResponseEntity.noContent().build();

        return ResponseEntity.ok(new UserDto(user));
    }

    @PostMapping(CREATE_USER)
    public ResponseEntity<UserDto> register(@RequestBody NewUserDto userDto) {
        final User register = userService.register(userDto);
        final UserDto newUser = new UserDto(register);
        return ResponseEntity.ok(newUser);
    }

}
