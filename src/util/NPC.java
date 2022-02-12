package util;

import java.util.ArrayList;

import mvc.Controller;

public class NPC extends Entity {
    private String name;
    private boolean interactable = false;
    private ArrayList<TopicResponse> topicResponses = new ArrayList<TopicResponse>(); 
    //private Face face;

    public NPC(float w, float h, Point3f c, float speed, Skin skin, String name, Controller controller) {
        super(skin, w, h, c, speed, controller);
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setInteractable(boolean interactable) {
        this.interactable = interactable;
    }

    public boolean isInteractable() {
        return interactable;
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
