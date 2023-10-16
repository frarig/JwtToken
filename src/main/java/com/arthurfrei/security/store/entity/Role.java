package com.arthurfrei.security.store.entity;

import java.util.List;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.lang.NonNull;

@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Role extends BaseEntity {

    @NonNull
    @Column(name = "name", nullable = false)
    private String name;

    @ManyToMany(mappedBy = "roles", fetch = FetchType.LAZY)
    private List<User> users;

    @Override
    public String toString() {
        return "Role{id=" + super.getId() + ", name='" + name + "}";
    }
}
