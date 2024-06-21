package com.softel.seaa.Services.Implement;

import com.softel.seaa.Services.Contract.QueryService;
import com.softel.seaa.Entity.Query;
import com.softel.seaa.Repository.QueryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class QueryImpl extends GenericImpl<Query, QueryRepository> implements QueryService {
    @Autowired
    public QueryImpl(QueryRepository dao) {
        super(dao);
    }
}
