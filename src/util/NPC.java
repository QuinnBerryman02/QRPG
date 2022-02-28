package util;

//Programmed by Quinn Berrman
//Student number: 20363251


import java.util.ArrayList;

import mvc.AIController;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class NPC extends Entity {
    private String name;
    private ArrayList<TopicResponse> topicResponses = new ArrayList<TopicResponse>(); 
    private boolean commonKnowledge = true;
    //private Face face;

    protected NPC() {
        
    }

    public NPC(float w, float h, Point3f c, Skin skin, String name, int maxHealth, int damage, int maxMana) {
        super(skin, w, h, c, new AIController(),maxHealth,damage,maxMana);
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public ArrayList<Topic> getKnownTopics() {
        ArrayList<Topic> known = new ArrayList<Topic>();
        for (TopicResponse topicResponse : topicResponses) {
            known.add(topicResponse.getTopic());
        }
        if(commonKnowledge) {
            for (TopicResponse defaults : TopicLoader.getCommonKnowledge()) {
                known.add(defaults.getTopic());
            }
        }
        return known;
    }

    public void setTopicResponses(ArrayList<TopicResponse> topicResponses) {
        this.topicResponses = topicResponses;
    }

    public boolean hasResponse(Topic topic) {
        for (TopicResponse topicResponse : topicResponses) {
            if(topicResponse.getTopic().equals(topic)) {
                return true;
            }
        }
        if(commonKnowledge) {
            for (TopicResponse defaults : TopicLoader.getCommonKnowledge()) {
                if(defaults.getTopic().equals(topic)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public boolean hasCommonKnowledge() {
        return commonKnowledge;
    }

    public void setCommonKnowledge(boolean commonKnowledge) {
        this.commonKnowledge = commonKnowledge;
    }

    public void addTopicResponse(TopicResponse tr) {
        topicResponses.add(tr);
    }

    public ArrayList<TopicResponse> getTopicResponses() {
        return topicResponses;
    }

    public Response getResponse(Topic topic) {
        for (TopicResponse topicResponse : topicResponses) {
            if(topicResponse.getTopic().equals(topic)) {
                return topicResponse.getResponse();
            }
        }
        if(commonKnowledge) {
            for (TopicResponse defaults : TopicLoader.getCommonKnowledge()) {
                if(defaults.getTopic().equals(topic)) {
                    return defaults.getResponse();
                }
            }
        }   
        return null;
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
    }

    private void readObject(ObjectInputStream in) throws ClassNotFoundException, IOException {
        in.defaultReadObject();
        AIController ac = new AIController();
        ac.setEntity(this);
        setController(ac);
    }
}
