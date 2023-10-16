package com.arthurfrei.security.service;

import com.arthurfrei.security.store.entity.Status;
import com.arthurfrei.security.store.repository.UserRepository;
import com.arthurfrei.user.api.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public interface UserDtoService {

    List<UserDto> getAll();

    List<UserDto> getAllByActive(Boolean bool);

    @Service
    @Transactional
    @RequiredArgsConstructor
    class Base implements UserDtoService {

        private final UserRepository userRepository;

        @Override
        public List<UserDto> getAll() {
            return userRepository.findAll()
                    .stream()
                    .map(UserDto::new)
                    .collect(Collectors.toList());
        }

        @Override
        public List<UserDto> getAllByActive(Boolean bool) {
            Status status = bool ? Status.ACTIVE : Status.NOT_ACTIVE;

            return userRepository.findAllByStatus(status)
                    .orElse(new ArrayList<>())
                    .stream()
                    .map(UserDto::new)
                    .collect(Collectors.toList());
        }
    }

}
