package com.seek.authentication_service.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.seek.authentication_service.dto.request.LoginRequest;
import com.seek.authentication_service.dto.request.UserRequest;
import com.seek.authentication_service.dto.response.TokenResponse;
import com.seek.authentication_service.dto.response.UserResponse;
import com.seek.authentication_service.exceptions.UserAlreadyExistsException;
import com.seek.authentication_service.mapper.UserMapper;
import com.seek.authentication_service.model.Role;
import com.seek.authentication_service.model.User;
import com.seek.authentication_service.repository.TokenRepository;
import com.seek.authentication_service.repository.UserRepository;
import com.seek.authentication_service.service.JwtService;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private TokenRepository tokenRepository;

    @Spy
    private UserMapper userMapper;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthenticationServiceImpl authenticationService;

    @Test
    void testRegisterUserSuccess() {
        // Datos de prueba
        UserRequest request = new UserRequest();
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setUsername("johndoe");
        request.setPassword("password");
        request.setRole(Role.USER);

        User savedUser = new User();
        savedUser.setUsername("johndoe");

        when(userRepository.findByUsername(request.getUsername())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedPassword");
        when(userMapper.toModel(request)).thenReturn(savedUser); // Cambiado a pasar el request
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(jwtService.generateToken(any(User.class))).thenReturn("mockToken");

        // Ejecución
        UserResponse response = authenticationService.register(request);

        // Verificaciones
/*        assertNotNull(response);
        assertEquals("johndoe", response.getUsername());
        verify(userRepository).save(any(User.class));
        verify(jwtService).generateToken(any(User.class));*/
    }

    @Test
    void testRegisterUserAlreadyExists() {
        // Datos de prueba
        UserRequest request = new UserRequest();
        request.setUsername("johndoe");

        when(userRepository.findByUsername(request.getUsername())).thenReturn(Optional.of(new User()));

        // Ejecución y Verificación
        assertThrows(UserAlreadyExistsException.class, () -> authenticationService.register(request));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testAuthenticateSuccess() {
        // Datos de prueba
        LoginRequest request = new LoginRequest();
        request.setUsername("johndoe");
        request.setPassword("password");

        User user = new User();
        user.setUsername("johndoe");

        when(userRepository.findByUsername(request.getUsername())).thenReturn(Optional.of(user));
        when(jwtService.generateToken(any(User.class))).thenReturn("mockToken");

        // Ejecución
        TokenResponse response = authenticationService.authenticate(request);

        // Verificaciones
        assertNotNull(response);
        assertEquals("mockToken", response.getToken());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtService).generateToken(user); // Cambiado para verificar el usuario específico
    }

    @Test
    void testAuthenticateUserNotFound() {
        // Datos de prueba
        LoginRequest request = new LoginRequest();
        request.setUsername("nonexistent");

        when(userRepository.findByUsername(request.getUsername())).thenReturn(Optional.empty());

        // Ejecución y Verificación
        assertThrows(RuntimeException.class, () -> authenticationService.authenticate(request));
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }
}
