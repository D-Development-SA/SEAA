package com.softel.seaa.Services.Contract;

import com.softel.seaa.Entity.Seaa;

import java.util.Optional;

public interface SeaaService extends GenericService<Seaa>{
    Optional<Seaa> findByNameIgnoreCase(String name);
}
