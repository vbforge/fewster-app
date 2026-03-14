package com.vladproduction.fewster.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security configuration.
 *
 * Changes from original:
 *   - Added /settings/** to the authenticated (hasRole) block.
 *   - Cleaned up rule ordering — more specific rules first.
 *   - Static resource permit moved to a single matcher block.
 *   - CSRF remains disabled (Thymeleaf forms use POST without CSRF tokens in
 *     this project; re-enable and add th:action CSRF token if hardening later).
 */
@Configuration
public class SecurityConfig {

    @Value("${role.name}")
    private String role;

    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth

                        // ── Static resources ───────────────────────────────────────────
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/favicon.ico").permitAll()

                        // ── Public pages ───────────────────────────────────────────────
                        .requestMatchers(HttpMethod.GET,  "/").permitAll()
                        .requestMatchers(HttpMethod.GET,  "/login").permitAll()
                        .requestMatchers(HttpMethod.GET,  "/register").permitAll()
                        .requestMatchers(HttpMethod.POST, "/register").permitAll()
                        .requestMatchers(HttpMethod.POST, "/demo-create").permitAll()
                        .requestMatchers(HttpMethod.GET,  "/r/**").permitAll()

                        // ── Public API ─────────────────────────────────────────────────
                        .requestMatchers(HttpMethod.POST, "/api/v1/demo-url").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/user/**").permitAll()

                        // ── Authenticated — dashboard ──────────────────────────────────
                        .requestMatchers("/dashboard/**").hasRole(role)

                        // ── Authenticated — settings (NEW) ─────────────────────────────
                        .requestMatchers("/settings/**").hasRole(role)

                        // ── Authenticated — URL management API ────────────────────────
                        .requestMatchers("/api/v1/url/**").hasRole(role)

                        // ── Everything else requires authentication ────────────────────
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/dashboard", true)
                        .failureUrl("/login?error=true")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/?logout=true")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                )
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
