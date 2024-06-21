package com.softel.seaa.Services.Contract;

import com.softel.seaa.Entity.User;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Optional;

public interface UserService extends GenericService<User>{
    Optional<User> findByNameContains(@NonNull String name);
    List<User> findByRoles_NameLikeAndSpecialistNotNull(String name);
    User findByPhoneNumberAndPasswordContains(String phoneNumber, String password);
    Optional<User> findByPhoneNumberContains(String phoneNumber);

}
