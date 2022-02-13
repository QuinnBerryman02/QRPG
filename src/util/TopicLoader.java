package util;

import java.io.File;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import mvc.AIController;

import org.w3c.dom.Element;

public class TopicLoader {
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
        
    }

    public Response getDefaultResponse(Topic t) {
        NodeList topics = document.getElementsByTagName("topic");
        for (int j=0;j<topics.getLength();j++) {
            Element e = (Element)topics.item(j);
            if(e.getAttribute("name").equals(t.getName())) {
                return new Response(e.getTextContent());
            }
        }
        return null;
    }
}
