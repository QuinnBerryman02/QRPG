package util;

import java.awt.Graphics;
import java.util.ArrayList;

public abstract class Menu {
    public abstract void display(Graphics g);
}

class Dialogue extends Menu {
    private Player player;
    private NPC npc; 
    private ArrayList<Topic> topics = new ArrayList<Topic>();
    private ArrayList<Topic> availableTopics;

    public Dialogue(Player player, NPC npc) {
        this.player = player;
        this.npc = npc;
        topics.add(Topic.getTopic("Introduction"));
        availableTopics = player.findCommonTopics(npc);
    }

    public void display(Graphics g) {

    }
    
    @Override
    public String toString() {
        String s = "";
        for (Topic topic : topics) {
            s += "Player asked about: " + topic.getName() + "\n"; 
            s += npc.getName() + " responded with: " + npc.getResponse(topic) + "\n";
        }
        return s;
    }
}
