package org.example.class_activity_9.services;


import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.example.class_activity_9.models.User;
import org.example.class_activity_9.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.User.UserBuilder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByUsername(username).stream().findFirst();

        if (user.isEmpty()) {
            throw new UsernameNotFoundException("User not found: " + username);
        }

        UserBuilder builder = org.springframework.security.core.userdetails.User.withUsername(username);
        builder.password(user.get().getPassword()); // Password should be encoded
        builder.roles("USER"); // Default role

        return builder.build();
    }
}
