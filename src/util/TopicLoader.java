package util;

import java.io.File;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import org.w3c.dom.Element;

public class TopicLoader {
    private static ArrayList<TopicResponse> commonKnowledge = new ArrayList<TopicResponse>();
    private DocumentBuilderFactory factory;
    private DocumentBuilder builder;
    private Document document;

    public TopicLoader(File file) {
        try {
            factory = DocumentBuilderFactory.newInstance();
            builder = factory.newDocumentBuilder();
            document = builder.parse(file);
            document.getDocumentElement().normalize();
        } catch (Exception e) {
            e.printStackTrace();
        }
        loadCommonKnowledge();
    }

    public static ArrayList<TopicResponse> getCommonKnowledge() {
        return commonKnowledge;
    }

    public void loadCommonKnowledge() {
        NodeList topics = document.getElementsByTagName("topic");
        for (int j=0;j<topics.getLength();j++) {
            Element e = (Element)topics.item(j);
            Topic t = Topic.getTopic(e.getAttribute("name"));
            Response r = new Response(e.getTextContent());
            TopicResponse tr = new TopicResponse(t, r);
            commonKnowledge.add(tr);
        }
    }
}
