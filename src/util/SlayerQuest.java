package util;

public class SlayerQuest extends Quest {
    private int numberToKill;
    private int progress = 0;
    private Enemy.Type typeOfEnemy;

    protected SlayerQuest() {

    }

    public SlayerQuest (NPC questGiver, int reward, int numberToKill, Enemy.Type type) {
        super(questGiver, reward);
        this.numberToKill = numberToKill;
        this.typeOfEnemy = type;
    }

    @Override
    public String getDetails() {
        String s = "Slayed " + progress + "/" + numberToKill + " " + typeOfEnemy.toString(); 
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

    public Enemy.Type getTypeOfEnemy() {
        return typeOfEnemy;
    }

    public void incrementProgress() {
        progress++;
    }
}
