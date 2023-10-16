package com.arthurfrei.admin.api.dto;

import com.arthurfrei.security.store.entity.User;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AdminDto {

    private Long id;

    private String username;

    private String firstName;

    private String lastName;

    private String email;

    private String status;

    public AdminDto(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.email = user.getEmail();
        this.status = user.getStatus().name();
    }
}
