package util;

import java.io.File;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import mvc.AIController;

import org.w3c.dom.Element;

public class NPCLoader {
    private DocumentBuilderFactory factory;
    private DocumentBuilder builder;
    private Document document;
    private TopicLoader topicLoader;

    public NPCLoader(File file) {
        topicLoader = new TopicLoader(new File("res/topic.xml"));
        try {
            factory = DocumentBuilderFactory.newInstance();
            builder = factory.newDocumentBuilder();
            document = builder.parse(file);
            document.getDocumentElement().normalize();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }

    public ArrayList<NPC> createAllNpcs() {
        //TODO create the rest of the NPCs
        NodeList npcNodes = document.getElementsByTagName("npc");
        ArrayList<NPC> npcs = new ArrayList<NPC>();
        for (int i=0;i<npcNodes.getLength();i++) {
            Element e = (Element)npcNodes.item(i);
            npcs.add(createNPC(e));
        }
        return npcs;
    }

    public NPC createNPC(Element npcElement) {
        float x = Float.parseFloat(npcElement.getAttribute("x"));
        float y = Float.parseFloat(npcElement.getAttribute("y"));
        String skinName = npcElement.getAttribute("skin");
        String name = npcElement.getAttribute("name");
        Skin skin = Skin.getSkinByName(skinName);
        Point3f	c = new Point3f(x,y,0);
        NPC npc = new NPC(0.5f, 0.5f, c, skin, name, 100, 10, 100);
        ((AIController)npc.getController()).setEntity(npc);
        NodeList topics = npcElement.getElementsByTagName("topic");
        ArrayList<TopicResponse> trs = new ArrayList<TopicResponse>();
        for (int j=0;j<topics.getLength();j++) {
            Element e = (Element)topics.item(j);
            Topic t = Topic.getTopic(e.getAttribute("name"));
            Response r;
            if(e.getAttribute("default").equals("true")) {
                r = topicLoader.getDefaultResponse(t);
            } else {
                r = new Response(e.getTextContent());
            }
            trs.add(new TopicResponse(t, r));
        }
        npc.setTopicResponses(trs);
        return npc;
    }
}
