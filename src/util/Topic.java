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
    public String getAnswer() {
        return answer;
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