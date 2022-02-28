package util;

//Programmed by Quinn Berrman
//Student number: 20363251


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class AssassinationQuest extends Quest {
    transient private NPC target;

    protected AssassinationQuest() {

    }

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

    public NPC getTarget() {
        return target;
    }

    public void setTarget(NPC target) {
        this.target = target;
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeObject(target.getName());
    }

    private void readObject(ObjectInputStream in) throws ClassNotFoundException, IOException {
        in.defaultReadObject();
        target = (NPC)NPCLoader.getNPCByName((String)in.readObject());
    }
}
