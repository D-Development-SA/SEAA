package com.softel.seaa.Security;

import com.softel.seaa.Services.Implement.UserImpl;
import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

@Configuration
@AllArgsConstructor
public class Config {
    private final UserImpl userImpl;
    private final AuthorizationFilter authorizationFilter;
    private final String API = "/api/v1/";
    private final String USER = "user";
    private final String SPECIALIST = "specialist";
    private final String NOTIFICATION = "notification/";
    private final String SEAA = "seaa/";

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, AuthenticationManager manager) throws Exception {
        AuthenticationFilter authenticationFilter = new AuthenticationFilter();
        authenticationFilter.setAuthenticationManager(manager);

        return http.csrf(AbstractHttpConfigurer::disable)
                .cors(corsConfigurer -> corsConfigurer.configurationSource(request -> {
                    CorsConfiguration config = new CorsConfiguration();

                    config.setAllowedOrigins(List.of("http://localhost:4200"));
                    config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));
                    config.setAllowedHeaders(List.of("*"));
                    config.setExposedHeaders(List.of("Set-Cookie"));
                    config.setAllowCredentials(true);

                    return config;
                }))
                .authorizeHttpRequests(authorization -> authorization
                        .requestMatchers(HttpMethod.POST,API + USER + "/create").permitAll()
                        .requestMatchers(API + SPECIALIST + "/**").hasRole("EXPERT")
                        .requestMatchers(API + SEAA + SPECIALIST + "/**").hasRole("EXPERT")
                        .requestMatchers(API + USER + "/**" ).hasAnyRole("USER", "EXPERT")
                        .requestMatchers(API + SEAA + USER + "/**" ).hasAnyRole("USER", "EXPERT")
                        .requestMatchers(API + NOTIFICATION + USER + "/**" ).hasAnyRole("USER", "EXPERT")
                        .requestMatchers(API + "**").hasRole("ADMIN")
                        .anyRequest()
                        .authenticated()
                )
                .sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilter(authenticationFilter)
                .addFilterBefore(authorizationFilter, UsernamePasswordAuthenticationFilter.class)
                .logout(logout -> logout.deleteCookies("Authorization"))
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder managerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        managerBuilder.userDetailsService(userImpl).passwordEncoder(passwordEncode());
        return managerBuilder.build();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncode() {
        return new BCryptPasswordEncoder();
    }
}
