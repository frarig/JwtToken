package com.arthurfrei.admin.api.controller;

import com.arthurfrei.admin.api.dto.AdminDto;
import com.arthurfrei.security.service.UserDtoService;
import com.arthurfrei.security.service.UserService;
import com.arthurfrei.security.store.entity.User;
import com.arthurfrei.user.api.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminV1Controller {

    private final UserService userService;

    private final UserDtoService userDtoService;

    private static final String GET_USER_BY_ID = "/users/{id}";

    private static final String GET_USERS = "/users";

    private static final String DELETE_USER = "/users/{id}";

    @GetMapping(GET_USER_BY_ID)
    public ResponseEntity<AdminDto> getUserById(@PathVariable Long id) {
        final User user = userService.findById(id);

        if (user == null) return ResponseEntity.noContent().build();

        return ResponseEntity.ok(new AdminDto(user));
    }

    @GetMapping(GET_USERS)
    public ResponseEntity<List<UserDto>> getUsers(@RequestParam(name = "isActive", required = false) Boolean isActive) {

        if (isActive == null) {
            return ResponseEntity.ok(
                    userDtoService.getAll()
            );
        }

        return ResponseEntity.ok(
                userDtoService.getAllByActive(isActive)
        );
    }

    @DeleteMapping(DELETE_USER)
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        return userService.delete(id)
                ? ResponseEntity.ok().build()
                : ResponseEntity.badRequest().build() ;
    }

}
