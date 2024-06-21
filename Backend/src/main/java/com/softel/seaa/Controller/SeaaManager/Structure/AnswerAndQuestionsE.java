package com.softel.seaa.Controller.SeaaManager.Structure;

import lombok.Getter;

import java.util.List;

@Getter
public class AnswerAndQuestionsE extends AnswerAndQuestions {
    private String uuid_queries;

    AnswerAndQuestionsE(Object answer, List<Object> question) {
        super(answer, question);
    }
}
