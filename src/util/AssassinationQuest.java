package util;

public class AssassinationQuest extends Quest {
    private final NPC target;
    public AssassinationQuest (NPC questGiver, int reward, NPC target) {
        super(questGiver, reward);
        if(target.isDead()) {
            throw new IllegalArgumentException("Already dead");
        }
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
