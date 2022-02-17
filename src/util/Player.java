package util;

import java.util.ArrayList;
import mvc.PlayerController;

public class Player extends Entity {
	private ArrayList<Topic> topics = new ArrayList<Topic>();
    public Player(Skin s, float width, float height, Point3f centre, int maxHealth, int damage) { 
    	super(s, width, height, centre, new PlayerController(), maxHealth, damage);
		topics.add(Topic.getTopic("Wizardry"));
		topics.add(Topic.getTopic("Magic"));
		setHostile(true);
	}

	@Override
	public ArrayList<Topic> getKnownTopics() {
		return topics;
	}
}

