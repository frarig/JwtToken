package com.arthurfrei.security.store.repository;

import com.arthurfrei.security.store.entity.Status;
import com.arthurfrei.security.store.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String name);

    Optional<List<User>> findAllByStatus(Status status);

}
