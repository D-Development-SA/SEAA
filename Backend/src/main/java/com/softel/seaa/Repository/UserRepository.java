package com.softel.seaa.Repository;

import com.softel.seaa.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>, PagingAndSortingRepository<User, Long> {
    Optional<User> findByNameContains(@NonNull String name);

    Optional<User> findByPhoneNumberContains(String phoneNumber);

    List<User> findByRoles_NameLikeAndSpecialistNotNull(String name);

    Optional<User> findByPhoneNumberAndPasswordContains(String phoneNumber, String password);
}