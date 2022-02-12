package util;

import java.util.ArrayList;

import mvc.Controller;

public class Player extends Entity {
	private ArrayList<Topic> topics = new ArrayList<Topic>();
    public Player(Skin s, float width, float height, Point3f centre, float speed, Controller controller) { 
    	super(s, width, height, centre, speed, controller);
		topics.add(Topic.getTopic("Wizardry"));
	}

	@Override
	public ArrayList<Topic> getKnownTopics() {
		return topics;
	}
}

