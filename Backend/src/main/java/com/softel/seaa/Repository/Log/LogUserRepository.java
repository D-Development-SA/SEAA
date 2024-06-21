package com.softel.seaa.Repository.Log;

import com.softel.seaa.Entity.Log.LogUser;
import com.softel.seaa.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface LogUserRepository extends JpaRepository<LogUser, Long>, PagingAndSortingRepository<LogUser, Long> {
    List<LogUser> findLogUsersByNameContains(String name);
}