import { Answer } from "./Answer";
import { Question } from "./question";

export interface AnswerAndQuestion{
    answer: Answer;
    question: Question[];
    uuid_queries?: string;
    date_queries?: Date;
}