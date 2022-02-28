package util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

import main.MainWindow;
import mvc.AIController;
import mvc.Controller;
import mvc.Model;
import mvc.PlayerController;

public abstract class Entity extends GameObject{
    transient private ArrayList<Entity> inCombatWith = new ArrayList<Entity>();
    private Stat[] stats; 
    transient private AnimationPhase phase = AnimationPhase.NEUTRAL;
    transient private Direction direction = Direction.DOWN;
    private boolean dead = false;
    private float hostileSpeed = 4;
    transient private int progress = 0;
    private ArrayList<Spell> spells = new ArrayList<Spell>();
    private Spell currentSpell;

    private boolean hostile;
    private float health;
    private float mana;
    private float speed;
    private Skin skin;
    transient private Controller controller;
    
    protected Entity() {
        
    }

    public Entity(Skin s, float width, float height, Point3f centre, Controller controller, float maxHealth, float damage, float maxMana) { 
    	super(width, height, centre);
        this.skin = s;
        this.controller = controller;
        stats = new Stat[3];
        stats[0] = new Stat("Max Health", maxHealth, 10, true);
        stats[1] = new Stat("Max Mana", maxMana, 10, true);
        stats[2] = new Stat("Damage", damage, 10, true);
        this.health = maxHealth;
        this.mana = maxMana;
        setHostile(false);
	}

    public abstract ArrayList<Topic> getKnownTopics();

    public ArrayList<Topic> findCommonTopics(Entity other) {
        ArrayList<Topic> common = new ArrayList<Topic>(getKnownTopics());
        common.retainAll(other.getKnownTopics());
        return common;
    }

    public enum AnimationPhase {
        NEUTRAL,
        WALKING,
        ATTACKING,
        CASTING,
    }
    public enum Direction {
        UP(0),
        RIGHT(1),
        DOWN(2),
        LEFT(3);
        private int value;
        Direction(int value) {
            this.value = value;
        }
    }

    public float getSpeed() {
        return speed;
    }

    public void setHostileSpeed(float hostileSpeed) {
        this.hostileSpeed = hostileSpeed;
    }

    public void incrementProgress() {
        progress++;
        if(progress==0) {
            return;
        }
        int modulo = progressMax();
        progress = progress % modulo;
        if(progress==0) {
            phase = AnimationPhase.NEUTRAL;
        }
    }

    public int progressMax() {
        switch(phase) {
            case NEUTRAL:   return 1; 
            case WALKING:   return 4; 
            case ATTACKING: return 7; 
            case CASTING:   return 9; 
            default:        return -1;
        }
    }

    public int progressWave() {
        int max = progressMax();
        if(progress > max/2) {
            return max - progress - (max % 2);
        } else {
            return progress;
        }
    }

    public String getCurrentTexture() {
        switch(phase) {
            case NEUTRAL:
                return "res/sprites/casting_transparent.png";
            case WALKING:
                return "res/sprites/walking_transparent.png";
            case ATTACKING:
                return "res/sprites/attacking_transparent.png";
            case CASTING:
                return "res/sprites/casting_transparent.png";
            default:
                return null;
        }
    }

    public int[] getSource() {
        switch(phase) {
            case NEUTRAL:
                return skin.getNeutralCoords(direction.value);
            case WALKING:
                return skin.getWalkingCoords(progressWave(), direction.value);
            case ATTACKING:
                return skin.getAttackCoords(progressWave(), direction.value);
            case CASTING:
                return skin.getCastingCoords(progressWave(), direction.value);
            default:
                return null;
        }
    }

    public AnimationPhase getPhase() {
        return phase;
    }

    public int getProgress() {
        return progress;
    }

    public Direction getDirection() {
        return direction;
    }

    public boolean setDirection(Direction direction) {
        if (this.direction == direction) {
            return true;
        }
        this.direction = direction;
        return false;
    }
    
    public void setPhase(AnimationPhase phase) {
        this.phase = phase;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public void setSkin(Skin skin) {
        this.skin = skin;
    }

    public Skin getSkin() {
        return skin;
    }

    public Controller getController() {
        return controller;
    }

    public boolean isHostile() {
        return hostile;
    }

    public void setHostile(boolean hostile) {
        speed = hostile ? hostileSpeed : 0.25f*hostileSpeed;
        this.hostile = hostile;
    }

    public float getMaxHealth() {
        return stats[0].getValue();
    }

    public float getHealth() {
        return health;
    }

    public void setHealth(float health) {
        this.health = health;
    }

    public float getDamage() {
        return stats[2].getValue();
    }

    public float getMana() {
        return mana;
    }

    public float getMaxMana() {
        return stats[1].getValue();
    }

    public void setMana(float mana) {
        this.mana = mana;
    }

    public void regenMana() {
        float amount = getMaxMana() / 20f / MainWindow.getTargetFPS();
        if(mana + amount >= getMaxMana()) {
            mana = getMaxMana();
        } else {
            mana += amount;
        }
    }

    public void updateCombat() {
        if(dead) {
            inCombatWith.forEach(e -> e.setHostile(false));
            inCombatWith.clear();
            return;
        }
        for(int i=0;i<inCombatWith.size();i++) {
            Entity e = inCombatWith.get(i);
            if(!e.isHostile() || e.isDead()) {
                inCombatWith.remove(e);
                i--;
            }
        }
    }

    public void commenceCombat(Entity other) {
        setHostile(true);
        if(!other.getInCombatWith().contains(this)) {
            other.getInCombatWith().add(this);
        }
    }

    public ArrayList<Entity> getInCombatWith() {
        return inCombatWith;
    }

    public void dealDamage(float damage, Entity other) {
        if(dead) return;
        health -= damage;
        if(health <= 0) {
            die();
        } else {
            MainWindow.getAudioManager().playSoundByName("hit_" + (isFemale() ? "female" : isMale() ? "male" : isBoss() ? "boss" : "monster"));
            if(other instanceof Player) {
                commenceCombat(other);
            }
            if(this instanceof Player) {
                other.commenceCombat(this);
            }
        }
    }

    public ArrayList<Spell> getSpells() {
        return spells;
    }

    public Spell getCurrentSpell() {
        return currentSpell;
    }

    public void setCurrentSpell(Spell currentSpell) {
        this.currentSpell = currentSpell;
    }

    public boolean healthBarVisible() {
        return (health <= 0.33f * getMaxHealth()) || isHostile();
    }

    public boolean isInCombat() {
        return !inCombatWith.isEmpty();
    }

    public boolean isMale() {
        if(skin!=null) {
            return skin.isMale();
        } 
        if(this instanceof Enemy) {
            Enemy e = (Enemy)this;
            if(e.getType().equals(Enemy.Type.ELDER_WITCH) || e.getType().equals(Enemy.Type.YOUNG_WITCH)) {
                return false;
            } else {
                return false;
            }
        }
        return false;
    }
    public boolean isFemale() {
        if(skin!=null) {
            return skin.isFemale();
        } 
        if(this instanceof Enemy) {
            Enemy e = (Enemy)this;
            if(e.getType().equals(Enemy.Type.ELDER_WITCH) || e.getType().equals(Enemy.Type.YOUNG_WITCH)) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    public boolean isBoss() {
        if(this instanceof Enemy) {
            return (((Enemy)this).getType().equals(Enemy.Type.BOSS));
        }
        return false;
    }

    public boolean isMonster() {
        if(this instanceof Enemy && skin==null) {
            Enemy e = (Enemy)this;
            if(e.getType().equals(Enemy.Type.ELDER_WITCH) || e.getType().equals(Enemy.Type.YOUNG_WITCH)) {
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    public void die() {
        MainWindow.getAudioManager().playSoundByName("death_" + (isFemale() ? "female" : isMale() ? "male" : isBoss() ? "boss" : "monster"));
        dead = true;
        setHostile(false);
        if(this instanceof NPC) {
            System.out.println(((NPC)this).getName() + " died");
        } else if(this instanceof Player) {
            MainWindow.openMainMenu();
            System.out.println("Player died");
        } else if(this instanceof Enemy) {
            System.out.println(((Enemy)this).getType() + " died");
            if (((Enemy)this).getType().equals(Enemy.Type.BOSS)) {
                MainWindow.getModel().setStage(Model.STAGE.VICTORY);
                NPC john = NPCLoader.getNPCByName("John");
			    Point3f loc = getCentre().plusVector(new Vector3f());
			    john.move(loc.minusPoint(john.getCentre())); 
                Topic t = Topic.getTopic("Final Words");
                Response r = new Response("Thank you, I am finally free...");
                MainWindow.getModel().getPlayer().getKnownTopics().add(t);
                john.getTopicResponses().clear();
                john.setCommonKnowledge(false);
                john.addTopicResponse(new TopicResponse(t, r));
                MainWindow.getAudioManager().playSongByTileId(5152 + 1681);
            }
        }
    }
    
    public Stat[] getStats() {
        return stats;
    }

    @Override
    public String toString() {
        return (
            "[class= " + getClass() + "]," +
            "[alive= " + !isDead() + "]," + 
            super.toString() + "," +
            "[phase= " + phase + "]," +
            "[direction= " + direction + "]," +
            "[hostile= " + hostile + "]," +
            "[hostileSpeed= " + hostileSpeed + "]," +
            "[progress= " + progress + "]," +
            "[damage= " + getDamage() + "]," +
            "[maxHealth= " + getMaxHealth() + "]," +
            "[health= " + health + "]," +
            "[maxMana= " + getMaxMana() + "]," +
            "[mana= " + mana + "]," +
            "[speed= " + speed + "]," +
            "[skin= " + skin + "]," +
            "[controller= " + controller + "]"
        );
    }

    public boolean isDead() {
        return dead;
    }

    private void writeObject(ObjectOutputStream out) throws IOException{
        out.defaultWriteObject();
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    private void readObject(ObjectInputStream in) throws ClassNotFoundException, IOException {
        in.defaultReadObject();
        inCombatWith = new ArrayList<Entity>();
        progress = 0;
        phase = AnimationPhase.NEUTRAL;
        direction = Direction.DOWN;
    }
}

