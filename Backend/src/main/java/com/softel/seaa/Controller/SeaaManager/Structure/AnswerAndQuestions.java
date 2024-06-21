package com.softel.seaa.Controller.SeaaManager.Structure;

import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;
import java.util.List;

@Builder
@Getter
public class AnswerAndQuestions implements Serializable {
    protected Object answer;
    protected List<Object> question;
}
