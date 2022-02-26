package util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public abstract class Quest implements Serializable{
    transient private NPC questGiver;
    private int reward;
    private boolean rewardCollected = false;

    protected Quest() {

    }

    public Quest(NPC questGiver, int reward) {
        this.questGiver = questGiver;
        this.reward = reward;
    }

    public abstract String getDetails();
    public abstract boolean isComplete();

    public NPC getQuestGiver() {
        return questGiver;
    }

    public int getReward() {
        return reward;
    }

    public boolean isFailed() {
        return questGiver != null ? questGiver.isDead() : true;
    }

    public boolean isRewardCollected() {
        return rewardCollected;
    }

    public void setRewardCollected(boolean rewardCollected) {
        this.rewardCollected = rewardCollected;
    }

    public int getQuestRelevancy() {
        if(isRewardCollected()) return 3;
        if(!isFailed()) {
            return isComplete() ? 1 : 2;
        }
        return 4;
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeObject(questGiver.getName());
    }

    private void readObject(ObjectInputStream in) throws ClassNotFoundException, IOException {
        in.defaultReadObject();
        questGiver = (NPC)NPCLoader.getNPCByName((String)in.readObject());
    }
}