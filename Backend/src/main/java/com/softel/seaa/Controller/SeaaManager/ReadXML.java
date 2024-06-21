package com.softel.seaa.Controller.SeaaManager;

import com.softel.seaa.Controller.Exception.GeneralExceptionAndControllerAdvice.ListEmptyException;
import com.softel.seaa.Controller.SeaaManager.Structure.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public abstract class ReadXML {
    private static boolean wrote = false;
    public static Question readQuestion(String root) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();

        Question question = new Question();

        if (addTagsRetorno(root, question) == 1 && wrote){
            for (int i = 0; i < 2; i++) {
                buildDocumentAndQuestion(root + i, builder, question);
                new File(root + i).delete();
            }
        }else if (!wrote){
            question.setText("notConclusion");
            question.setType("CONCLU");
            return question;
        }else buildDocumentAndQuestion(root, builder, question);

        return question;
    }

    private static void buildDocumentAndQuestion(String root, DocumentBuilder builder, Question question) throws SAXException, IOException {
        Document document = builder.parse(new File(root));
        document.getDocumentElement().normalize();

        createQuestion(document, question);
    }

    private static int addTagsRetorno(String root, Question question) throws IOException {
        List<String> tags =  extract(new File(root), Tag.RETORNO_VexMain, Tag.RETORNO);

        if (tags.size() == 2) {
            question.setReturn0(tags.get(1));
            question.setReturnVexMain(tags.get(0));
        }else question.setReturn0(tags.get(0));

        return tags.size();
    }

    private static void createQuestion(Document document, Question question) {
        NodeList nodeList = document.getDocumentElement().getChildNodes();
        String nodeName;
        Element element;
        Node node;

        for (int i = 0; i < nodeList.getLength(); i++) {
            node = nodeList.item(i);

            if (isElementNode(node)) {
                element = (Element) node;
                nodeName = element.getNodeName();

                switch (nodeName) {
                    case Tag.VAR -> question.setVar(element.getTextContent());
                    case Tag.DOMI -> question.setDomi(element.getTextContent());
                    case Tag.PARTI -> question.setParti(element.getTextContent());
                    case Tag.TIPO -> question.setType(element.getTextContent());
                    case Tag.DOBLECLIC -> question.setDoubleClick(element.getTextContent());
                    case Tag.TEXTO -> question.setText(element.getTextContent());
                    case Tag.CERT -> question.setCerti(element.getTextContent());
                    case Tag.NINGUNA -> question.setNeither(element.getTextContent());
                    case Tag.PROP -> question.setProp(element.getTextContent());
                    case Tag.ITEM -> question.getList().add(createItemToQuestionOrAnswer(new Item(), element));
                    case Tag.CONCLUF -> {
                        question.setType("CONCLU");
                        question.setConcluF(createItemToConclusion(element));
                        return;
                    }
                    case Tag.CONCLUP -> {
                        question.setType("CONCLU");
                        question.setConcluP(createItemToConclusion(element));
                        return;
                    }
                }
            }
        }

    }

    private static Item createItemToQuestionOrAnswer(Item item, Element element) {
        String nodeName;
        NodeList itemList = element.getChildNodes();

        for (int j = 0; j < itemList.getLength(); j++) {
            Node n = itemList.item(j);

            if (isElementNode(n)) {
                Element e = (Element) n;
                nodeName = e.getNodeName();

                createObject(item, nodeName, e);
            }
        }

        return item;
    }

    private static List<Item> createItemToConclusion(Element element) {
        List<Item> items = new ArrayList<>();
        NodeList itemList = element.getChildNodes();

        for (int j = 0; j < itemList.getLength(); j++) {
            Node n = itemList.item(j);

            if (isElementNode(n)) {
                Element e = (Element) n;
                items.add(createItemToQuestionOrAnswer(new Item(), e));
            }
        }
        return items;
    }

    private static void createObject(Item item, String nodeName, Element e) {
        switch (nodeName) {
            case Tag.TEXTO -> item.setText(e.getTextContent());
            case Tag.INDICE_EN_VAR -> item.setIndexVar(e.getTextContent());
            case Tag.INDICE -> item.setIndex(e.getTextContent());
            case Tag.CERT -> item.setCert(e.getTextContent());
            case Tag.SELECCIONADO -> item.setSelected(e.getTextContent());
            case Tag.INDICE_EN_KB -> item.setIndexKB(e.getTextContent());
            case Tag.VAR -> item.setVar(e.getTextContent());
            case Tag.DOMI -> item.setDomi(e.getTextContent());
            case Tag.PARTI -> item.setParti(e.getTextContent());
            case Tag.PROP -> item.setProp(e.getTextContent());
        }
    }

    private static boolean isElementNode(Node node) {
        return node.getNodeType() == Node.ELEMENT_NODE;
    }

    public static Answer readAnswer(String root) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();

        Answer answer = new Answer();
        addTagsAnswer(root, answer);

        Document document;

        if (wrote) {
            document = builder.parse(new File(root));
            document.getDocumentElement().normalize();
            createAnswer(document, answer);
        }else answer.setQuery(new Query());

        return answer;
    }

    private static void addTagsAnswer(String root, Answer answer) throws IOException {
        List<String> tagsList = extract(new File(root), Tag.PROBLEMA, Tag.OPCION);

        if (tagsList.isEmpty())
            throw new ListEmptyException(Tag.PROBLEMA + ", " + Tag.OPCION);

        answer.setOption(tagsList.get(1));
        answer.setProblem(tagsList.get(0));
    }

    private static void createAnswer(Document document, Answer answer) {
        NodeList nodeList = document.getDocumentElement().getChildNodes();
        Query query = new Query();
        List<Object> list = new ArrayList<>();

        createObjectToAnswer(nodeList, "", list, null, null);

        query.setVars(list);
        answer.setQuery(query);
    }

    private static void createObjectToAnswer(NodeList nodeList, String varR, List<Object> list, Var labelVar, Prop labelProp) {
        Node node;
        String nodeName;
        Element element;
        Item item;

        for (int i = 0; i < nodeList.getLength(); i++) {
            node = nodeList.item(i);

            if (isElementNode(node)) {
                element = (Element) node;
                nodeName = element.getNodeName();

                if (nodeName.equalsIgnoreCase(Tag.VAR) && node.getChildNodes().getLength() != 1) {
                    createObjectToAnswer(node.getChildNodes(), Tag.VAR, list, new Var(), null);
                }
                if (nodeName.equalsIgnoreCase(Tag.PROP) && node.getChildNodes().getLength() != 1) {
                    createObjectToAnswer(node.getChildNodes(), Tag.PROP, list, null, new Prop());
                }

                if (varR.equals(Tag.VAR)) {

                    switch (nodeName) {
                        case Tag.VAR -> labelVar.setVar(element.getTextContent());
                        case Tag.DOMI -> labelVar.setDomi(element.getTextContent());
                        case Tag.PARTI -> labelVar.setParti(element.getTextContent());
                        case Tag.VALOR -> labelVar.setValue(element.getTextContent());
                        case Tag.CERT -> labelVar.setCert(element.getTextContent());
                        case Tag.INDICE -> labelVar.setIndex(element.getTextContent());
                        case Tag.ITEM -> {
                            item = new Item();
                            labelVar.getItems().add(createItemToQuestionOrAnswer(item, element));
                        }
                    }
                } else if (varR.equals(Tag.PROP)) {

                    switch (nodeName) {
                        case Tag.PROP -> labelProp.setProp(element.getTextContent());
                        case Tag.CERT -> labelProp.setCert(element.getTextContent());
                        case Tag.PARTI -> labelProp.setParti(element.getTextContent());
                        case Tag.TEXTO -> labelProp.setText(element.getTextContent());
                        case Tag.DOMI -> labelProp.setDomi(element.getTextContent());
                    }
                }
            }
        }

        if (labelVar != null) list.add(labelVar);
        else if (labelProp != null)list.add(labelProp);
    }

    private static List<String> extract(File file , String... tag) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(file));
        AtomicReference<Matcher> matcher = new AtomicReference<>();
        List<String> returnList = new ArrayList<>();
        List<String> list;

        list = captureTextAndDeletingTags(br, matcher, returnList, tag);
        br.close();

        if (detectConclusions(list))
            writeConclusions(list, file);
        else
            writingListWithTags(file, list);

        return returnList;
    }

    private static boolean detectConclusions(List<String> list) {
        String lineFirst = list.isEmpty() ? "" : list.get(0) ;

        return lineFirst.contains(Tag.CONCLUP) || lineFirst.contains(Tag.CONCLUF);
    }

    private static void writeConclusions(List<String> list, File file) throws IOException {
        String root = file.getPath();
        File[] auxFile = new File[2];
        BufferedWriter bw;

        AtomicBoolean change = new AtomicBoolean(false);

        String[] conclusion = {"<"+Tag.TEMP+">","<"+Tag.TEMP+">"};

        list.forEach(s -> {
            if (s.contains(Tag.CONCLUF))
                change.set(true);
            else if (s.contains(Tag.CONCLUP))
                change.set(false);

            if (change.get())
                conclusion[0] += s + "\n";
            else conclusion[1] += s + "\n";
        });

        for (int i = 0; i < conclusion.length; i++) {
            conclusion[i] += "</"+Tag.TEMP+">";
        }

        for (int i = 0; i < auxFile.length; i++) {
            auxFile[i] = new File(root + i);
            if (!auxFile[i].exists()) {
                auxFile[i].createNewFile();
            }
            bw = new BufferedWriter(new FileWriter(auxFile[i]));
            bw.write(conclusion[i]);
            bw.close();
        }

    }

    private static List<String> captureTextAndDeletingTags(BufferedReader br, AtomicReference<Matcher> matcher, List<String> returnList, String[] tag) {
        Pattern[] pattern = new Pattern[tag.length];

        for (int i = 0; i < tag.length; i++) {
            pattern[i] = Pattern.compile("<"+ tag[i] +">(.*?)</"+ tag[i] +">");
        }

        List<String> list;
        list = br.lines()
                .peek(text -> {
                    if (pattern[0] != null) {

                        matcher.set(pattern[0].matcher(text));
                        if (matcher.get().find()){
                            returnList.add(matcher.get().group(1));
                        }

                    }
                })
                .peek(text -> {
                    if (pattern[1] != null) {
                        matcher.set(pattern[1].matcher(text));

                        if (matcher.get().find()) {
                            returnList.add(matcher.get().group(1));
                        }
                    }
                })
                .filter(s -> {
                    for (String value : tag) {
                        if (s.matches(".*" + value + ".*"))
                            return false;
                    }
                    return true;
                })
                .collect(Collectors.toList());

        return list;
    }
    private static void writingListWithTags(File file, List<String> list) throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(file));

        if (list.size() > 0) {
            list.forEach(text -> {
                try {
                    bw.write(text + "\n");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            wrote = true;
        }
        else {
            bw.write("");
            wrote = false;
        }

        bw.flush();
        bw.close();
    }

    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException {
        //System.out.println(readAnswer("answer.tmp"));
        System.out.println(readQuestion("question.tmp"));
    }
}
