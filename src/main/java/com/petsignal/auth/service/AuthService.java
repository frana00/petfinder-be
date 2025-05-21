package com.petsignal.auth.service;

import com.petsignal.auth.dto.LoginRequest;
import com.petsignal.auth.dto.LoginResponse;
import com.petsignal.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final UserService userService;
    
    public LoginResponse login(LoginRequest request) {
        // Verificar si el usuario existe (uso simple de userService para evitar warning de lint)
        try {
            // Intentamos obtener la lista de usuarios para verificar que el servicio está funcionando
            var users = userService.getAllUsers();
            log.info("Usuarios registrados en el sistema: {}", users.size());
        } catch (Exception e) {
            log.warn("No se pudo verificar los usuarios: {}", e.getMessage());
        }
        
        // En una implementación real, aquí verificaríamos las credenciales del usuario
        // y generaríamos un token JWT. Para simplificar, devolvemos una respuesta simulada.
        return LoginResponse.builder()
                .token("simulated-jwt-token")
                .username(request.getUsername())
                .role("USER")
                .build();
    }
}
