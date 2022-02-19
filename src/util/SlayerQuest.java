package util;

public class SlayerQuest extends Quest {
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
        String s = "Slayed " + progress + "/" + numberToKill + " " + typeOfEntity.getSimpleName(); 
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

    public void incrementProgress() {
        progress++;
    }
}
