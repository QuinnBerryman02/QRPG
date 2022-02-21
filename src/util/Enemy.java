package util;

import java.util.ArrayList;

import mvc.AIController;
import mvc.Controller;

public class Enemy extends Entity{
    private Type type;
    public enum Type {
        SPIDER,
        VAMPIRE,
        BLACK_KNIGHT,
        SLIME,
        YOUNG_WITCH,
        ELDER_WITCH,
        GHOST,
        BAT,
        CULTIST,
        FIRE_ELEMENTAL,
        WATER_ELEMENTAL,
        WIND_ELEMENTAL,
        EARTH_ELEMENTAL,
        LIGHT_ELEMENTAL,
        DARK_ELEMENTAL
    }
    public Enemy(Type type, float width, float height, Point3f centre, float maxHealth, float damage, float maxMana) { 
        super(getSkin(type), width, height, centre, new AIController(), maxHealth, damage, maxMana);
        ((AIController)getController()).setEntity(this);
        this.type = type;
	}
    @Override
    public ArrayList<Topic> getKnownTopics() {
        return null;
    }

    public Type getType() {
        return type;
    }

    public void incrementProgress() {
        switch(type) {
            case BLACK_KNIGHT:
            case CULTIST:
            case DARK_ELEMENTAL:
            case EARTH_ELEMENTAL:
            case VAMPIRE:
            case WATER_ELEMENTAL:
            case WIND_ELEMENTAL:
            case FIRE_ELEMENTAL:
            case LIGHT_ELEMENTAL:
                super.incrementProgress();
                break;
            case ELDER_WITCH:
            case YOUNG_WITCH:
            case BAT:
            case GHOST:
            case SLIME:
            case SPIDER:
                setProgress(getProgress()+1);
                int modulo;
                switch(getPhase()) {
                    case NEUTRAL:   modulo = 1;     break;
                    case WALKING:   modulo = 4;     break;
                    case ATTACKING: modulo = 4;     break;
                    case CASTING:   modulo = 4;     break;
                    default:        modulo = -1;    break;
                }
                setProgress(getProgress()%modulo);
                if(getProgress()==0) {
                    setPhase(AnimationPhase.NEUTRAL);
                    setVerticalMovement(false);
                }
                break;
        }
    }

    public int progressWave() {
        switch(type) {
            case BLACK_KNIGHT:
            case CULTIST:
            case DARK_ELEMENTAL:
            case EARTH_ELEMENTAL:
            case VAMPIRE:
            case WATER_ELEMENTAL:
            case WIND_ELEMENTAL:
            case FIRE_ELEMENTAL:
            case LIGHT_ELEMENTAL:
                return super.progressWave();
            case ELDER_WITCH:
            case YOUNG_WITCH:
            case BAT:
            case GHOST:
            case SLIME:
            case SPIDER:
                int max;
                switch(getPhase()) {
                    case NEUTRAL:   max = 1; break;    
                    case WALKING:   max = 4; break;    
                    case ATTACKING: max = 4; break;    
                    case CASTING:   max = 4; break;    
                    default:        max = -1;break;
                }
                if(getProgress() > max/2) {
                    return max - getProgress() - (max % 2);
                } else {
                    return getProgress();
                }
            default:
                return -1;
        }   
    }
    @Override
    public String getCurrentTexture() {
        switch(type) {
            case BLACK_KNIGHT:
            case CULTIST:
            case DARK_ELEMENTAL:
            case EARTH_ELEMENTAL:
            case VAMPIRE:
            case WATER_ELEMENTAL:
            case WIND_ELEMENTAL:
            case FIRE_ELEMENTAL:
            case LIGHT_ELEMENTAL:
                return super.getCurrentTexture();
            case ELDER_WITCH:
                return "res/sprites/enemies/witch2.png";
            case YOUNG_WITCH:
                return "res/sprites/enemies/witch1.png";
            case BAT:
            case GHOST:
            case SLIME:
            case SPIDER:
                return "res/sprites/enemies/enemies.png";   
            default:
                return null;
        }
        
    }
    
    public int[] getSource() {
        switch(type) {
            case BLACK_KNIGHT:
            case CULTIST:
            case DARK_ELEMENTAL:
            case EARTH_ELEMENTAL:
            case VAMPIRE:
            case WATER_ELEMENTAL:
            case WIND_ELEMENTAL:
            case FIRE_ELEMENTAL:
            case LIGHT_ELEMENTAL:
                return super.getSource();
            case ELDER_WITCH:
            case YOUNG_WITCH:
                return getSourceWitch();
            case BAT:
            case GHOST:
            case SLIME:
            case SPIDER:
                return getSourceEnemy();   
        }
        return null;
    }

    public int[] getSourceWitch() {
        //24x * 34y
        int row = getDirection().ordinal();
        int column = getPhase().equals(AnimationPhase.NEUTRAL) ? 0 : progressWave();
        int dx2 = 24;
        int dy2 = 34;

        int sx1 = (column * dx2);
        int sy1 = (row * dy2);
        int sx2 = sx1 + dx2;
        int sy2 = sy1 + dy2;
        return new int[] {dx2, dy2, sx1, sy1, sx2, sy2};
    }

    public int[] getSourceEnemy() {
        //16x * 16y
        int dir = getDirection().ordinal();
        int row = dir==0 ? 3 : dir==1 ? 2 : dir==2 ? 0 : 1;
        int column = getPhase().equals(AnimationPhase.NEUTRAL) ? 0 : progressWave();
        int dx2 = 16;
        int dy2 = 16;
        int num = type==Type.SLIME ? 0 : type==Type.BAT ? 1 : type==Type.GHOST ? 2 : 3;
        int sx1 = (column * dx2) + (dx2 * 3) * num;
        int sy1 = (row * dy2) + (dy2*4);
        int sx2 = sx1 + dx2;
        int sy2 = sy1 + dy2;
        return new int[] {dx2, dy2, sx1, sy1, sx2, sy2};
    }

    @Override
    public String toString() {
        return (
            super.toString() + "," +
            "[type= " + type + "]"
        );
    }
    public static Skin getSkin(Type t) {
        switch(t) {
            case BLACK_KNIGHT:
                return Skin.getSkinByName("Dark-Knight_M");
            case CULTIST:
                return Skin.getSkinByName("Cultist_U");
            case DARK_ELEMENTAL:
                return Skin.getSkinByName("Dark-Elemental_U");
            case EARTH_ELEMENTAL:
                return Skin.getSkinByName("Earth-Elemental_U");
            case VAMPIRE:
                return Skin.getSkinByName("Vampire_M");
            case WATER_ELEMENTAL:
                return Skin.getSkinByName("Water-Elemental_U");
            case WIND_ELEMENTAL:
                return Skin.getSkinByName("Wind-Elemental_U");
            case FIRE_ELEMENTAL:
                return Skin.getSkinByName("Fire-Elemental_U");
            case LIGHT_ELEMENTAL:
                return Skin.getSkinByName("Light-Elemental_U");
            case ELDER_WITCH:
            case YOUNG_WITCH:
            case BAT:
            case GHOST:
            case SLIME:
            case SPIDER:
            default:
                return null;
        }
    }
}
