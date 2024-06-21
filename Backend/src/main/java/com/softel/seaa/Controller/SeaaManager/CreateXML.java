package com.softel.seaa.Controller.SeaaManager;

import com.softel.seaa.Controller.SeaaManager.Structure.Answer;
import com.softel.seaa.Controller.SeaaManager.Structure.Prop;
import com.softel.seaa.Controller.SeaaManager.Structure.Var;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.List;

public abstract class CreateXML {
    public static <E> void addAnswer(String root, E answer) throws Exception {
        if (!(answer instanceof Var) && !(answer instanceof Prop))
            throw new IllegalArgumentException("The object is not Prop o Var");

        File file = new File(root);

        Answer finalAnswer = ReadXML.readAnswer(root);

        finalAnswer.getQuery().getVars().add(answer);

        buildXML(file, finalAnswer);

        if (initialTags(file, finalAnswer, false))
            throw new Exception("Could not add initial tag to xml answer, a new query was created");
    }

    public static <E> void modifyAnswer(String root, E answer, int pos) throws Exception {
        File file = new File(root);
        Answer finalAnswer = ReadXML.readAnswer(root);
        List<Object> listAnswer = finalAnswer.getQuery().getVars();
        int size = listAnswer.size();

        listAnswer.subList(pos, size).clear();
        listAnswer.add(answer);

        buildXML(file, finalAnswer);

        if (initialTags(file, finalAnswer, false))
            throw new Exception("Could not add initial tag to xml answer, a new query was created");
    }

    public static void buildXML(File file, Answer finalAnswer) throws ParserConfigurationException, TransformerException {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(finalAnswer.generateDocument());
        StreamResult result = new StreamResult(file);

        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");

        transformer.transform(source, result);
    }

    public static boolean initialTags(File root, Answer answer, boolean newQuery) throws IOException {
        if (!root.exists())
            if (!root.createNewFile() || !new File(FileSystem.getRoot()).exists())
                throw new IOException("Cannot create the file of the user");

        BufferedReader br = new BufferedReader(new FileReader(root));
        String xml = br.lines().reduce((a, b) -> a + "\n" + b).orElse("");
        String finalXml = "<" + Tag.PROBLEMA + ">" + answer.getProblem() + "</" + Tag.PROBLEMA + ">\n"
                + "<" + Tag.OPCION + ">" + answer.getOption() + "</" + Tag.OPCION + ">\n";

        br.close();

        BufferedWriter bw = new BufferedWriter(new FileWriter(root));

        if (xml.endsWith("</OPCION>") || xml.endsWith("</OPCION>\n") || newQuery) {
            bw.write(finalXml);
        }else bw.write(finalXml+xml);

        bw.flush();
        bw.close();

        return xml.isEmpty();
    }
}
