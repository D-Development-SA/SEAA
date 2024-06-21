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
public class Question {
    private String var;
    private String domi;
    private String parti;
    private String type;
    private String certi;
    private String neither;
    private String doubleClick;
    private String text;
    private String prop;
    private List<Item> list;
    private List<Item> concluF;
    private List<Item> concluP;
    private String returnVexMain;
    private String return0;

    public Question(){
        list = new ArrayList<>();
    }
}
