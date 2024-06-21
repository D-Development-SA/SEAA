package com.softel.seaa.Repository;

import com.softel.seaa.Entity.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface RolRepository extends JpaRepository<Rol, Long>, PagingAndSortingRepository<Rol, Long> {
}