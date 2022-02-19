package util;

public abstract class Quest {
    private final NPC questGiver;
    private final int reward;

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
}

class SlayerQuest extends Quest {
    private final int numberToKill;
    private int progress = 0;
    private final Class<? extends Entity> typeOfEntity;
    public SlayerQuest (NPC questGiver, int reward, int numberToKill, Class<? extends Entity> typeOfEntity) {
        super(questGiver, reward);
        this.numberToKill = numberToKill;
        this.typeOfEntity = typeOfEntity;
    }

    @Override
    public String getDetails() {
        String s = "Slayed " + progress + "/" + numberToKill + " " + typeOfEntity.getName(); 
        return s;
    }
    @Override
    public boolean isComplete() {
        return progress >= numberToKill;
    }

    public int getProgress() {
        return progress;
    }

    public int getNumberToKill() {
        return numberToKill;
    }

    public Class<? extends Entity> getTypeOfEntity() {
        return typeOfEntity;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }
}

class AssassinationQuest extends Quest {
    private final NPC target;
    public AssassinationQuest (NPC questGiver, int reward, NPC target) {
        super(questGiver, reward);
        this.target = target;
    }

    @Override
    public String getDetails() {
        String s = "Assassinate " + target.getName(); 
        return s;
    }

    @Override
    public boolean isComplete() {
        return target.isDead();
    }
}