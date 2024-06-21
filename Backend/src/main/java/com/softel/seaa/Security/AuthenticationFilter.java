package com.softel.seaa.Security;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.PropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.softel.seaa.Entity.Rol;
import com.softel.seaa.Entity.User;
import com.softel.seaa.Services.Implement.UserImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.atomic.AtomicReference;

public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private AuthCredentials credentials;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        credentials = new AuthCredentials();

        try {
            credentials = new ObjectMapper().readValue(request.getReader(), AuthCredentials.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                credentials.getPhoneNumber(),
                credentials.getPassword()
        );

        return getAuthenticationManager().authenticate(authToken);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        AtomicReference<User> user = new AtomicReference<>();

        UserImpl.getUserRepository()
                .findByPhoneNumberContains(authResult.getName())
                .ifPresent(user::set);

        credentials.setAuthorities(
                user.get()
                        .getRoles()
                        .stream()
                        .map(Rol::getName)
                        .toList()
        );

        String token;

        try {
            token = Token.generateToken(user.get().getName() + " " + user.get().getLastName(), user.get().getId(),
                    user.get().getRoles().stream().map(Rol::getName).toList());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

        Cookie cookie = new Cookie("Authorization", token);
        cookie.setHttpOnly(true);
        cookie.setMaxAge(60 * 60 * 2);
        cookie.setPath("/api/");

        PropertyFilter userFilter = SimpleBeanPropertyFilter.serializeAllExcept("phoneNumber", "password");
        FilterProvider filters = new SimpleFilterProvider().addFilter("userFilter", userFilter);

        ObjectMapper mapper = new ObjectMapper();
        mapper.addMixIn(User.class, Mixin.class);
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.setFilterProvider(filters);

        String json = mapper.writeValueAsString(user.get());

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.addCookie(cookie);
        response.getWriter().write(json);
        response.getWriter().flush();
        super.successfulAuthentication(request, response, chain, authResult);
    }
}

@JsonFilter("userFilter")
abstract class Mixin{}
