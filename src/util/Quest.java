package util;

public abstract class Quest {
    private final NPC questGiver;
    private final int reward;
    private boolean rewardCollected = false;

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
}