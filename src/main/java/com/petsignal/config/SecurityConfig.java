package com.petsignal.config;

import com.petsignal.security.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.http.HttpMethod.POST;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

  private final CustomUserDetailsService userDetailsService;

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
    return authConfig.getAuthenticationManager();
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .csrf(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(auth -> auth
            .requestMatchers(POST, "/users").permitAll()
            // ✅ Authentication endpoints - PÚBLICOS
            .requestMatchers("/auth/login").permitAll()
            // ✅ Endpoints de recuperación de contraseña - PÚBLICOS
            .requestMatchers("/auth/forgot-password").permitAll()
            .requestMatchers("/auth/verify-reset-token/**").permitAll()
            .requestMatchers("/auth/reset-password").permitAll()
            .requestMatchers(
                "/swagger-ui/**",
                "/swagger-ui.html",
                "/v3/api-docs/**",
                "/v3/api-docs.yaml",
                "/v3/api-docs",
                "/webjars/**",
                "/configuration/**",
                "/swagger-resources/**",
                "/openapi.yaml",
                "/openapi/**"
            ).permitAll()
            .anyRequest().authenticated()
        )
        .userDetailsService(userDetailsService)
        .sessionManagement(sess -> sess.sessionCreationPolicy(STATELESS))
        .httpBasic(Customizer.withDefaults());

    return http.build();
  }
}
