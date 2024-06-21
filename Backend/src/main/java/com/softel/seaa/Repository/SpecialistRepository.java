package com.softel.seaa.Repository;

import com.softel.seaa.Entity.Specialist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface SpecialistRepository extends JpaRepository<Specialist, Long>, PagingAndSortingRepository<Specialist, Long> {
}