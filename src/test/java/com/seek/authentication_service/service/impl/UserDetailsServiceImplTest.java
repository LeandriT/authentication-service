package com.seek.authentication_service.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.seek.authentication_service.model.User;
import com.seek.authentication_service.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@ExtendWith({MockitoExtension.class})
class UserDetailsServiceImplTest {


    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    @Test
    void testLoadUserByUsernameSuccess() {
        // Datos de prueba
        String username = "johndoe";
        User user = new User();
        user.setUsername(username);

        // Configuración del comportamiento simulado
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        // Ejecución
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        // Verificaciones
        assertNotNull(userDetails);
        assertEquals(username, userDetails.getUsername());
        verify(userRepository).findByUsername(username);
    }

    @Test
    void testLoadUserByUsernameUserNotFound() {
        // Datos de prueba
        String username = "nonexistent";

        // Configuración del comportamiento simulado
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        // Ejecución y Verificación
        assertThrows(UsernameNotFoundException.class, () -> userDetailsService.loadUserByUsername(username));
        verify(userRepository).findByUsername(username);
    }

}
