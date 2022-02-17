package util;

import java.util.ArrayList;

import mvc.AIController;

public class NPC extends Entity {
    private String name;
    private ArrayList<TopicResponse> topicResponses = new ArrayList<TopicResponse>(); 
    //private Face face;

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
        return known;
    }

    public void setTopicResponses(ArrayList<TopicResponse> topicResponses) {
        this.topicResponses = topicResponses;
    }

    public Response getResponse(Topic topic) {
        for (TopicResponse topicResponse : topicResponses) {
            if(topicResponse.getTopic().equals(topic)) {
                return topicResponse.getResponse();
            }
        }
        return null;
    }

    
}
