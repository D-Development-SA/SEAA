package com.softel.seaa.Controller.SeaaManager.Structure;

import com.softel.seaa.Controller.SeaaManager.Tag;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public class Answer {
    private String problem;
    private String option;
    private Query query;

    public Document generateDocument() throws ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.newDocument();
        Element rootElement = document.createElement(Tag.CONSULTA);

        document.appendChild(rootElement);

        query.getVars().forEach(appendTags -> {
            if (appendTags instanceof Var var)
                var.appendTagsDoc(document, rootElement);
            else if (appendTags instanceof Prop prop) {
                prop.appendTagsDoc(document, rootElement);
            }else
                throw new IllegalArgumentException("The object is not Prop o Var");
        });

        return document;
    }
    public void insertProblemAndOption(String problem, String option) {
        this.problem = problem;
        this.option = option;
    }
}
