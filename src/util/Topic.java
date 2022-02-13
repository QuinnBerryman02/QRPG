package util;

import java.util.ArrayList;

public class Topic {
    private String name;
    public static ArrayList<Topic> allTopics = new ArrayList<Topic>();

    public static Topic getTopic(String name) {
        for (Topic topic : allTopics) {
            if(topic.getName().equals(name)) {
                return topic;
            }
        }
        Topic newTopic = new Topic(name);
        allTopics.add(newTopic);
        return newTopic;
    }

    private Topic(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}

class Response {
    private String answer;
    public Response(String answer) {
        this.answer = answer;
    }
    public String getRaw() {
        return answer;
    }
    public ArrayList<Topic> findReferencedTopics() {
        ArrayList<Topic> topics = new ArrayList<Topic>();
        String[] first = answer.split("\\(");
        for (int i=1;i<first.length;i++) {
            String[] second = first[i].split("\\)");
            String[] third = second[0].split("=");
            Topic t = Topic.getTopic(third[1]);
            topics.add(t);
        }
        return topics;
    }

    public String getText() {
        String text = "";
        String[] first = answer.split("\\(");
        text += first[0];
        for (int i=1;i<first.length;i++) {
            String[] second = first[i].split("\\)");
            String[] third = second[0].split("=");
            text += third[0];
            text += second[1];
        }
        return text;
    }
}

class TopicResponse {
    private Topic topic;
    private Response response;
    
    public TopicResponse(Topic t, Response r) {
        this.topic = t;
        this.response = r;
    }

    public Response getResponse() {
        return response;
    }

    public Topic getTopic() {
        return topic;
    }
}