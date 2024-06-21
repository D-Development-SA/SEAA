package com.softel.seaa.Repository;

import com.softel.seaa.Entity.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface QueryRepository extends JpaRepository<Query, Long>, PagingAndSortingRepository<Query, Long> {
}