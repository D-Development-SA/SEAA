package com.softel.seaa.Services.Implement;

import com.softel.seaa.Services.Contract.UserService;
import com.softel.seaa.Entity.User;
import com.softel.seaa.Repository.UserRepository;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserImpl extends GenericImpl<User, UserRepository> implements UserService, UserDetailsService {
    @Getter
    private static UserRepository userRepository;

    @Autowired
    public UserImpl(UserRepository dao) {
        super(dao);
        userRepository = dao;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByNameContains(@NonNull String name) {
        return dao.findByNameContains(name);
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> findByRoles_NameLikeAndSpecialistNotNull(String name) {
        return dao.findByRoles_NameLikeAndSpecialistNotNull(name);
    }

    @Override
    @Transactional(readOnly = true)
    public User findByPhoneNumberAndPasswordContains(String phoneNumber, String password) {
        return dao.findByPhoneNumberAndPasswordContains(phoneNumber, password).orElseThrow();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByPhoneNumberContains(String phoneNumber) {
        return dao.findByPhoneNumberContains(phoneNumber);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = dao.findByPhoneNumberContains(username)
                .orElseThrow(() -> new UsernameNotFoundException("Error login: Not exist user whit name '" + username + "' in the system"));

        List<GrantedAuthority> authorities = user.getRoles().stream()
                .map(rol -> new SimpleGrantedAuthority(rol.getName()))
                .collect(Collectors.toList());

        return new org.springframework.security.core.userdetails.User(
                user.getPhoneNumber(),
                user.getPassword(),
                user.isEnabled(),
                true,
                true,
                true,
                authorities);
    }

}
