package com.softel.seaa.Services.Implement;

import com.softel.seaa.Services.Contract.SeaaService;
import com.softel.seaa.Entity.Seaa;
import com.softel.seaa.Repository.SeaaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class SeaaImpl extends GenericImpl<Seaa, SeaaRepository> implements SeaaService {
    @Autowired
    public SeaaImpl(SeaaRepository dao) {
        super(dao);
    }

    @Transactional
    @Override
    public Optional<Seaa> findByNameIgnoreCase(String name) {
        return dao.findByNameIgnoreCase(name);
    }
}
