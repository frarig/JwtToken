package com.arthurfrei.security.jwt;

import com.arthurfrei.security.service.UserService;
import com.arthurfrei.security.store.entity.Role;
import com.arthurfrei.security.store.entity.Status;
import com.arthurfrei.security.store.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtUserDetailService implements UserDetailsService {

    private final UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        final User user = userService.findByUsername(username);
        final Collection<GrantedAuthority> grantedAuthorities = mapperGrantedAuthority(user.getRoles());

        log.info("IN loadUserByUsername - user with username: {} successfully loaded", username);

        return JwtUser.builder()
                .id(user.getId())
                .username(user.getUsername())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .password(user.getPassword())
                .authorities(grantedAuthorities)
                .enable(user.getStatus().equals(Status.ACTIVE))
                .lastPasswordResetDate(user.getUpdated())
                .build();
    }

    private List<GrantedAuthority> mapperGrantedAuthority(Collection<Role> roles) {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());
    }
}
