package util;

import java.util.ArrayList;
import java.util.Random;

import mvc.AIController;
import mvc.Controller;
import mvc.MageController;

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
        super(getSkin(type), width, height, centre, generateController(type), maxHealth, damage, maxMana);
        ((AIController)getController()).setEntity(this);
        this.type = type;
        generateSpell(type);
	}
    @Override
    public ArrayList<Topic> getKnownTopics() {
        return null;
    }

    public Type getType() {
        return type;
    }

    @Override
    public int progressMax() {
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
                return super.progressMax();
            case ELDER_WITCH:
            case YOUNG_WITCH:
            case BAT:
            case GHOST:
            case SLIME:
            case SPIDER:
            return getPhase().equals(AnimationPhase.NEUTRAL) ? 1 : 4;
            default:
                return -1;
        }   
    }

    public int progressWave() {
        int max = progressMax(); 
        if(getProgress() > max/2) {
            return max - getProgress() - (max % 2);
        } else {
            return getProgress();
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
        int dy2 = 32;

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

    public void generateSpell(Type t){
        Spell s = new Spell();
        s.setAim(Spell.Aim.values()[(new Random()).nextInt((int)getMaxMana()/100)]);
        s.setDamage((new Random()).nextInt((int)getDamage()) + 5);
        s.setRadius((new Random()).nextFloat() / 2f + 0.25f);
        switch(type) {
            case CULTIST:
                s.setElement(Projectile.Type.ARCANE);break;
            case DARK_ELEMENTAL:
                s.setElement(Projectile.Type.ARCANE);break;
            case EARTH_ELEMENTAL:
                s.setElement(Projectile.Type.STONE);break;
            case VAMPIRE:
                s.setElement(Projectile.Type.BLOOD);break;
            case WATER_ELEMENTAL:
                s.setElement(Projectile.Type.WATER);break;
            case WIND_ELEMENTAL:
                s.setElement(Projectile.Type.WIND);break;
            case FIRE_ELEMENTAL:
                s.setElement(Projectile.Type.FIRE);break;
            case LIGHT_ELEMENTAL: 
                s.setElement(Projectile.Type.LIGHT);break;
            case ELDER_WITCH:
                s.setElement(Projectile.Type.values()[(new Random()).nextInt(7)]);break;
            case YOUNG_WITCH:
                int type = (new Random()).nextInt(4);
                s.setElement(type==0?Projectile.Type.FIRE:type==0?Projectile.Type.WATER:type==0?Projectile.Type.WIND:Projectile.Type.STONE);break;
            default:
                return;
        }  
        s.updateFrames();
        s.calculateCost();
        getSpells().add(s);
        setCurrentSpell(s);
    }

    public static Controller generateController(Type t) {
        switch(t) {
            case CULTIST:
            case DARK_ELEMENTAL:
            case EARTH_ELEMENTAL:
            case VAMPIRE:
            case WATER_ELEMENTAL:
            case WIND_ELEMENTAL:
            case FIRE_ELEMENTAL:
            case LIGHT_ELEMENTAL:
            case ELDER_WITCH:
            case YOUNG_WITCH:
                return new MageController();
            case BLACK_KNIGHT:
            case BAT:
            case GHOST:
            case SLIME:
            case SPIDER:
                return new AIController();
            default:
                return null;
        }
    }
}
