package util;

import java.util.ArrayList;

import mvc.Controller;

public abstract class Entity extends GameObject {
    private Hitbox hitbox;
    private float speed;
    private Skin skin;
    private AnimationPhase phase = AnimationPhase.NEUTRAL;
    private int progress = 0;
    private Direction direction = Direction.UP;
    private boolean verticalMovement = false;
    private Controller controller;
    private boolean hostile = false;

    public Entity(Skin s, float width, float height, Point3f centre, float speed, Controller controller) { 
    	super(width, height, centre);
        hitbox = new Hitbox(centre, width, height);
        this.skin = s;
        this.speed = speed;
        this.controller = controller;
	}

    public void move(Vector3f v) {
        getCentre().applyVector(v); 
        hitbox.applyVector(v);
    }

    public Hitbox getHitbox() {
        return hitbox;
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
        this.hostile = hostile;
    }
}
