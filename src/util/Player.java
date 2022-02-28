package util;

//Programmed by Quinn Berrman
//Student number: 20363251


import java.util.ArrayList;
import java.util.Collections;
import java.util.function.Predicate;

import mvc.PlayerController;
import mvc.Viewer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Player extends Entity {
	transient private ArrayList<Topic> topics = new ArrayList<Topic>();
	private ArrayList<Quest> quests = new ArrayList<Quest>();
	private int gold = 0;

	protected Player() {

	}

    public Player(Skin s, float width, float height, Point3f centre, int maxHealth, int damage, int maxMana) { 
    	super(s, width, height, centre, new PlayerController(), maxHealth, damage, maxMana);
		topics.add(Topic.getTopic("Introduction"));
		topics.add(Topic.getTopic("About me"));
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

	public int numberOfQuestsCompleted() {
		int i=0;
		for (Quest quest : quests) {
			if(quest.getQuestRelevancy()%2==1) i++;
		}
		return i;
	}

	public Quest findFirstQuestByPredicate(Predicate<Quest> p) {
		for (Quest quest : quests) {
			if(p.test(quest)) return quest;
		}
		return null;
	}

	public boolean isIndoors() {
		return !centre.inBounds(Viewer.CAMERA_BOUND_TL, Viewer.CAMERA_BOUND_BR);
	}
	

	public int getGold() {
		return gold;
	}

	public void earnGold(int gold) {
		this.gold += gold;
	}
	public void spendGold(int gold) {
		this.gold -= gold;
	}

	private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeObject((PlayerController)getController());
		ArrayList<String> topicNames = new ArrayList<String>();
		for (Topic t : topics) {
			topicNames.add(t.getName());
		}
		out.writeObject(topicNames);
    }

	public boolean hasFinalQuest() {
		for (Quest quest : quests) {
			if(quest instanceof AssassinationQuest) {
				if(quest.getQuestGiver().equals(NPCLoader.getNPCByName("John"))) {
					if(((AssassinationQuest)quest).getTarget().equals(NPCLoader.getNPCByName("John"))) {
						return true;
					}
				}
			}
		}
		return false;
	}

    private void readObject(ObjectInputStream in) throws ClassNotFoundException, IOException {
        in.defaultReadObject();
		PlayerController pc = (PlayerController)in.readObject();
        pc.loadController();
        pc.reEstablishComponents();
        setController(pc);
		topics = new ArrayList<Topic>();
		ArrayList<String> topicNames = (ArrayList<String>)in.readObject();
		for (String s : topicNames) {
			topics.add(Topic.getTopic(s));
		}
    }
}

