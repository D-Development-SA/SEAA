package com.softel.seaa.Services.Implement;

import com.softel.seaa.Entity.Specialist;
import com.softel.seaa.Repository.SpecialistRepository;
import com.softel.seaa.Services.Contract.SpecialistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SpecialistImpl extends GenericImpl<Specialist, SpecialistRepository> implements SpecialistService {
    @Autowired
    public SpecialistImpl(SpecialistRepository dao) {
        super(dao);
    }
}
