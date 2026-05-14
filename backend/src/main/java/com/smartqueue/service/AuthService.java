package com.smartqueue.service;

import com.smartqueue.dto.request.LoginRequest;
import com.smartqueue.dto.request.RegisterRequest;
import com.smartqueue.dto.response.AuthResponse;
import com.smartqueue.exception.BusinessException;
import com.smartqueue.model.User;
import com.smartqueue.repository.UserRepository;
import com.smartqueue.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername()))
            throw new BusinessException("Username already taken");
        if (userRepository.existsByEmail(request.getEmail()))
            throw new BusinessException("Email already registered");

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .phoneNumber(request.getPhoneNumber())
                .role(User.UserRole.USER)
                .build();
        userRepository.save(user);

        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(auth);
        String token = jwtTokenProvider.generateToken(auth);

        return AuthResponse.builder()
                .token(token).tokenType("Bearer")
                .username(user.getUsername())
                .role(user.getRole().name())
                .userId(user.getId())
                .build();
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(auth);
        String token = jwtTokenProvider.generateToken(auth);

        User user = userRepository.findByUsername(request.getUsername()).orElseThrow();
        return AuthResponse.builder()
                .token(token).tokenType("Bearer")
                .username(user.getUsername())
                .role(user.getRole().name())
                .userId(user.getId())
                .build();
    }
}
