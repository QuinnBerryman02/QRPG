package util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Random;

import mvc.AIController;
import mvc.Controller;
import mvc.MageController;

public class Enemy extends Entity{
    private Type type;
    private int bossPhase;
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
        DARK_ELEMENTAL,
        BOSS
    }

    protected Enemy() {
    
    }
    
    public Enemy(Type type, float width, float height, Point3f centre, float maxHealth, float damage, float maxMana) { 
        super(getSkin(type), width, height, centre, null, maxHealth, damage, maxMana);
        this.type = type;
        bossPhase = (!type.equals(Type.BOSS)) ? -1 : 1; 
        setController(generateController(this));
        generateSpell(type);
        setHostileSpeed(getHostileSpeed());
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
            case BOSS:
                return 3;
            default:
                return -1;
        }   
    }

    public float getHostileSpeed(){
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
            case ELDER_WITCH:
            case YOUNG_WITCH:
                return 3.9f;
            case BAT:
            case GHOST:
            case SLIME:
            case SPIDER:
                return 2.5f;
            case BOSS:
                return 3f;
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
            case BOSS:
                return "res/sprites/enemies/boss.png";
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
            case BOSS:
                return getSourceBoss();
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

    public int[] getSourceBoss() {
        if(getHealth() > getMaxHealth()*0.66) {
            bossPhase = 1;
        } else if (getHealth() > getMaxHealth()*0.33){
            bossPhase = 2;
        } else {
            bossPhase = 3;
        }
        float dir = getController().getMoveDirection().getX();      //boss only has 1 (2 if flipped) static sprites for each phase
        int sy1 = dir >= 0 ? 0 : 110;
        int dy2 = (int)(2.9*16);
        int sy2 = sy1 + 110;
        int sx1, dx2;
        switch(bossPhase) {
            case 1:
                sx1 = 0;
                dx2 = 80;
                break;
            case 2:
                sx1 = 80;
                dx2 = 141;
                break;
            case 3:
                sx1 = 223;
                dx2 = 94;
                break;
            default:
                throw new IllegalArgumentException("Unknown phase");
        }
        int sx2 = sx1 + dx2;
        dx2 = (int)(2.9*16);
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
            case BOSS:
            default:
                return null;
        }
    }

    public void generateSpell(Type t){
        if(t.equals(Type.BOSS)) {
            generateBossSpells();
            return;
        }
        Spell s = new Spell();
        s.setAim(Spell.Aim.values()[(new Random()).nextInt((int)getMaxMana()/100)]);
        s.setDamage((new Random()).nextInt(25));
        s.setRadius((new Random()).nextFloat() + 0.25f);
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

    public void generateBossSpells() {
        for (Projectile.Type element : Projectile.Type.values()) {
            Spell s = new Spell();
            s.setAim(Spell.Aim.values()[(new Random()).nextInt(4)]);
            s.setDamage(25);
            s.setElement(element);
            s.setRadius((new Random()).nextFloat() * 2.5f);
            s.updateFrames();
            s.calculateCost();
            getSpells().add(s);
        }
        
        setCurrentSpell(getSpells().get(0));
    }

    public static Controller generateController(Enemy e) {
        switch(e.getType()) {
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
            case BOSS:
                MageController mc = new MageController();
                mc.setEntity(e);
                return mc;
            case BLACK_KNIGHT:
            case BAT:
            case GHOST:
            case SLIME:
            case SPIDER:
                AIController ac = new AIController();
                ac.setEntity(e);
                return ac;
            default:
                return null;
        }
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
    }

    private void readObject(ObjectInputStream in) throws ClassNotFoundException, IOException {
        in.defaultReadObject();
        setController(generateController(this));
    }
}
