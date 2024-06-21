package com.softel.seaa.Controller.SeaaManager.Structure;

import com.softel.seaa.Controller.SeaaManager.Tag;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public class Item{
    private String index;
    private String cert;
    private String text;
    private String indexVar;
    private String indexKB;
    private String selected;
    private String prop;
    private String var;
    private String domi;
    private String parti;

    public void appendTagsDoc(Document document, Element rootElement) {
        Element element = document.createElement(Tag.ITEM);
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
        if (index != null && !index.isEmpty()){
            e = document.createElement(Tag.INDICE);
            e.appendChild(document.createTextNode(index));
            element.appendChild(e);
        }

        if (indexKB != null && !indexKB.isEmpty()){
            e = document.createElement(Tag.INDICE_EN_KB);
            e.appendChild(document.createTextNode(indexKB));
            element.appendChild(e);
        }
        if (indexVar != null && !indexVar.isEmpty()){
            e = document.createElement(Tag.INDICE_EN_VAR);
            e.appendChild(document.createTextNode(indexVar));
            element.appendChild(e);
        }
        if (selected != null && !selected.isEmpty()){
            e = document.createElement(Tag.SELECCIONADO);
            e.appendChild(document.createTextNode(selected));
            element.appendChild(e);
        }
        if (prop != null && !prop.isEmpty()){
            e = document.createElement(Tag.PROP);
            e.appendChild(document.createTextNode(prop));
            element.appendChild(e);
        }
    }

}
