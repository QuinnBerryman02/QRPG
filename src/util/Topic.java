package util;

//Programmed by Quinn Berrman
//Student number: 20363251


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

public class Topic {
    private String name;
    transient public static ArrayList<Topic> allTopics = new ArrayList<Topic>();

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

class Response implements Serializable {
    private String answer;
    transient private static final String HAS_TOPIC = "<font color=blue><b>";
    transient private static final String DOESNT_HAVE_TOPIC = "<font color=green><b>";
    transient private static final String CLOSE_TAGS = "<b/><font color=black>"; 
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

    public String getText(ArrayList<Topic> topics) {
        String text = "";
        String[] first = answer.split("\\(");
        text += "<font color=black>";
        text += first[0];
        for (int i=1;i<first.length;i++) {
            String[] second = first[i].split("\\)");
            String[] third = second[0].split("=");
            Topic t = Topic.getTopic(third[1]);
            text += topics.contains(t) ? HAS_TOPIC : DOESNT_HAVE_TOPIC;
            text += third[0];
            text += CLOSE_TAGS;
            text += second[1];
        }
        return text;
    }
}

class TopicResponse implements Serializable {
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

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.writeObject(response);
        out.writeObject(topic.getName());
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        response = (Response)in.readObject();
        topic = Topic.getTopic((String)in.readObject());
    }
}