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
public class Prop{
    private String prop;
    private String domi;
    private String parti;
    private String cert;
    private String text;

    public void appendTagsDoc(Document document, Element rootElement) {
        Element element = document.createElement(Tag.PROP);
        rootElement.appendChild(element);
        Element e;

        if (prop != null && !prop.isEmpty()){
            e = document.createElement(Tag.PROP);
            e.appendChild(document.createTextNode(prop));
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
        if (text != null && !text.isEmpty()){
            e = document.createElement(Tag.TEXTO);
            e.appendChild(document.createTextNode(text));
            element.appendChild(e);
        }
    }
}
