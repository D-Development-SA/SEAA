package com.softel.seaa.Services.Implement;

import com.softel.seaa.Services.Contract.RolService;
import com.softel.seaa.Entity.Rol;
import com.softel.seaa.Repository.RolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RolImpl extends GenericImpl<Rol, RolRepository> implements RolService {
    @Autowired
    public RolImpl(RolRepository dao) {
        super(dao);
    }
}
