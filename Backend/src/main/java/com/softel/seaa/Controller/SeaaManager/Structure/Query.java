package com.softel.seaa.Controller.SeaaManager.Structure;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public  class Query {
    private List<Object> vars;

    public Query(){
        vars = new ArrayList<>();
    }
}
