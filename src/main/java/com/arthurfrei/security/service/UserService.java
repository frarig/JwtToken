package com.arthurfrei.security.service;

import com.arthurfrei.security.store.entity.Role;
import com.arthurfrei.security.store.entity.Status;
import com.arthurfrei.security.store.entity.User;
import com.arthurfrei.security.store.repository.RoleRepository;
import com.arthurfrei.security.store.repository.UserRepository;
import com.arthurfrei.user.api.dto.NewUserDto;
import com.arthurfrei.user.api.dto.UserDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

public interface UserService {

    User register(NewUserDto UserDto);

    List<User> getAll();

    List<User> getActiveUsers();

    List<User> getNotActiveUsers();

    User findByUsername(String username);

    User findById(Long id);

    void delete(Long id);

    @Slf4j
    @Service
    @Transactional
    class Base implements UserService {

        private final UserRepository userRepository;

        private final RoleRepository roleRepository;

        private final BCryptPasswordEncoder passwordEncoder;


        public Base(UserRepository userRepository, RoleRepository roleRepository, @Lazy BCryptPasswordEncoder passwordEncoder) {
            this.userRepository = userRepository;
            this.roleRepository = roleRepository;
            this.passwordEncoder = passwordEncoder;
        }

        @Override
        public User register(NewUserDto userDto) {
            log.info("UserDTO: " + userDto);
            User user = new User(
                    userDto.getUsername(),
                    userDto.getFirstName(),
                    userDto.getLastName(),
                    userDto.getEmail(),
                    passwordEncoder.encode(userDto.getPassword()),
                    getUserRoles(),
                    Status.ACTIVE
            );

            User newUser = userRepository.save(user);
            log.info("IN register - user: {} successfully registred", newUser);

            return newUser;
        }

        private Set<Role> getUserRoles() {
            Role defaultRole = new Role();
            defaultRole.setName("ROLE_USER");

            final Role role = roleRepository.findByName("ROLE_USER").orElse(defaultRole);

            Set<Role> roles = new LinkedHashSet<>();
            roles.add(role);

            return roles;
        }

        @Override
        public List<User> getActiveUsers() {
            final List<User> users = userRepository.findAllByStatus(Status.ACTIVE).orElse(new ArrayList<>());
            log.info("IN getActiveUsers - {} users found", users.size());

            return users;
        }

        @Override
        public List<User> getNotActiveUsers() {
            final List<User> users = userRepository.findAllByStatus(Status.NOT_ACTIVE).orElse(new ArrayList<>());
            log.info("IN getNotActiveUsers - {} users found", users.size());

            return users;
        }

        @Override
        public List<User> getAll() {
            List<User> users = userRepository.findAll();
            log.info("IN getAll - {} users found", users.size());

            return users;
        }

        @Override
        public User findByUsername(String username) {
            Optional<User> optionalUser = userRepository.findByUsername(username);

            if (optionalUser.isEmpty())
                throw new UsernameNotFoundException("User with username '" + username + "' not found");

            final User user = optionalUser.get();
            log.info("IN findByUsername - user: {} found by username {}", user, username);

            return user;
        }

        @Override
        public User findById(Long id) {
            Optional<User> optionalUser = userRepository.findById(id);

            if (optionalUser.isEmpty()) {
                log.warn("IN findById - no user found by id {}", id);
                return null;
            }

            final User user = optionalUser.get();
            log.info("IN findById - user: {} found by id: {}", user, id);

            return user;
        }

        @Override
        public void delete(Long id) {
            Optional<User> optionalUser = userRepository.findById(id);

            if (optionalUser.isEmpty()) {
                log.warn("IN delete - user with id: {} not found", id);
                return;
            }

            User user = optionalUser.get();
            user.setStatus(Status.DELETE);

            log.info("IN delete - user with id: {} is marked as deleted", id);
            userRepository.save(user);
        }
    }

}
