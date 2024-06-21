package com.softel.seaa.Controller.SeaaManager.Structure;

import com.softel.seaa.Controller.SeaaManager.Tag;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public class Var{
    private String var;
    private String domi;
    private String parti;
    private String cert;
    private String value;
    private String index;
    private List<Item> items;

    public Var() {
        items = new ArrayList<>();
    }

    public void appendTagsDoc(Document document, Element rootElement) {
        Element element = document.createElement(Tag.VAR);
        rootElement.appendChild(element);
        Element e;

        if (var != null && !var.isEmpty()) {
            e = document.createElement(Tag.VAR);
            e.appendChild(document.createTextNode(var));
            element.appendChild(e);
        }
        if (domi != null && !domi.isEmpty()){
            e = document.createElement(Tag.DOMI);
            e.appendChild(document.createTextNode(domi));
            element.appendChild(e);
        }
        if (parti != null && !parti.isEmpty()){
            e = document.createElement(Tag.PARTI);
            e.appendChild(document.createTextNode(parti));
            element.appendChild(e);
        }
        if (cert != null && !cert.isEmpty()){
            e = document.createElement(Tag.CERT);
            e.appendChild(document.createTextNode(cert));
            element.appendChild(e);
        }
        if (value != null && !value.isEmpty()){
            e = document.createElement(Tag.VALOR);
            e.appendChild(document.createTextNode(value));
            element.appendChild(e);
        }
        if (index != null && !index.isEmpty()){
            e = document.createElement(Tag.INDICE);
            e.appendChild(document.createTextNode(index));
            element.appendChild(e);
        }
        if (!items.isEmpty())
            items.forEach(item -> item.appendTagsDoc(document, element));
    }
}
