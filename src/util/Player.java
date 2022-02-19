package util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.function.Predicate;

import mvc.PlayerController;

public class Player extends Entity {
	private ArrayList<Topic> topics = new ArrayList<Topic>();
	private ArrayList<Quest> quests = new ArrayList<Quest>();
	private int gold = 0;
    public Player(Skin s, float width, float height, Point3f centre, int maxHealth, int damage, int maxMana) { 
    	super(s, width, height, centre, new PlayerController(), maxHealth, damage, maxMana);
		topics.add(Topic.getTopic("Wizardry"));
		topics.add(Topic.getTopic("Magic"));
		setHostile(true);
	}

	@Override
	public ArrayList<Topic> getKnownTopics() {
		return topics;
	}

	public ArrayList<Quest> getQuests() {
		return quests;
	}

	public void sortQuests() {
		Collections.sort(quests, (q1, q2) -> {
			int n1 = ((Quest)q1).getQuestRelevancy();
			int n2 = ((Quest)q2).getQuestRelevancy();
			return n1 > n2 ? 1 : n1 < n2 ? -1 : 0;
		});
	}

	public Quest findFirstQuestByPredicate(Predicate<Quest> p) {
		for (Quest quest : quests) {
			if(p.test(quest)) return quest;
		}
		return null;
	}

	public int getGold() {
		return gold;
	}

	public void setGold(int gold) {
		this.gold = gold;
	}
}

