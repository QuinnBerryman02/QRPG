package util;

import java.util.ArrayList;

import mvc.Controller;

public abstract class Entity extends GameObject {
    private AnimationPhase phase = AnimationPhase.NEUTRAL;
    private Direction direction = Direction.UP;
    private boolean verticalMovement = false;
    private boolean inCombat = false;
    private boolean dead = false;
    private float hostileSpeed = 4;
    private int progress = 0;

    private boolean hostile;
    private int damage;
    private int maxHealth;
    private int health;
    private int maxMana;
    private int mana;
    private float speed;
    private Skin skin;
    private Controller controller;
    

    public Entity(Skin s, float width, float height, Point3f centre, Controller controller, int maxHealth, int damage, int maxMana) { 
    	super(width, height, centre);
        this.skin = s;
        this.controller = controller;
        this.maxHealth = maxHealth;
        this.health = maxHealth;
        this.maxMana = maxMana;
        this.mana = maxMana;
        this.damage = damage;
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

    public void incrementProgress() {
        progress++;
        int modulo;
        switch(phase) {
            case NEUTRAL:   modulo = 1;     break;
            case WALKING:   modulo = 4;     break;
            case ATTACKING: modulo = 7;     break;
            case CASTING:   modulo = 9;     break;
            default:        modulo = -1;    break;
        }
        progress = progress % modulo;
        if(progress==0) {
            phase = AnimationPhase.NEUTRAL;
            verticalMovement = false;
        }
    }

    private int progressWave() {
        int max;
        switch(phase) {
            case NEUTRAL:   max = 1; break;     //0|     
            case WALKING:   max = 4; break;     //2|     3
            case ATTACKING: max = 7; break;     //3|     4      5     6      
            case CASTING:   max = 9; break;     //4|     5      6     7      8
            default:        max = -1;break;
        }
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

    public void setVerticalMovement(boolean b) {
        verticalMovement = b;
    }

    public boolean getVerticalMovement() {
        return verticalMovement;
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

    public int getMaxHealth() {
        return maxHealth;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public void setMaxHealth(int maxHealth) {
        this.maxHealth = maxHealth;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public int getDamage() {
        return damage;
    }

    public int getMana() {
        return mana;
    }

    public int getMaxMana() {
        return maxMana;
    }

    public void setMana(int mana) {
        this.mana = mana;
    }

    public void setMaxMana(int maxMana) {
        this.maxMana = maxMana;
    }

    public void dealDamage(int damage) {
        if(dead) return;
        if(!isHostile()) setHostile(true);
        health -= damage;
        if(health <= 0) {
            die();
        }
        if(!inCombat) {
            (new Thread() {
                @Override
                public void run() {
                    inCombat = true;
                    try {
                        sleep(5000);
                    } catch (Exception e) {}
                    inCombat = false;
                }
            }).start();
        }
    }

    public boolean healthBarVisible() {
        return (health <= 0.33 * maxHealth) || inCombat;
    }

    public void die() {
        dead = true;
        hostile = false;
        if(this instanceof NPC) {
            System.out.println(((NPC)this).getName() + " died");
        } else if(this instanceof Player) {
            System.out.println("Player died");
        } else {
            System.out.println(getClass() + " died");
        }
    }

    @Override
    public String toString() {
        return (
            super.toString() + "," +
            "[phase= " + phase + "]," +
            "[direction= " + direction + "]," +
            "[verticalMovement= " + verticalMovement + "]," +
            "[hostile= " + hostile + "]," +
            "[hostileSpeed= " + hostileSpeed + "]," +
            "[progress= " + progress + "]," +
            "[damage= " + damage + "]," +
            "[maxHealth= " + maxHealth + "]," +
            "[health= " + health + "]," +
            "[speed= " + speed + "]," +
            "[skin= " + skin + "]," +
            "[controller= " + controller + "]"
        );
    }
}

