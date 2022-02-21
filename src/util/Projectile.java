package util;

import java.awt.Color;

public class Projectile extends GameObject{
    private int animationProgress = 0;
    private Vector3f velocity;
    private float damage;
    private Entity caster;
    private Type type;
    private Spell spell;
    public enum Type {
        FIRE,
        WATER,
        LIGHT,
        STONE,
        WIND,
        BLOOD,
        ARCANE
    }

    public Projectile(float width, float height, Point3f centre, Vector3f velocity, float damage, Entity caster, Type type, Spell spell) {
        super(width, height, centre);
        this.velocity = velocity;
        this.damage = damage;
        this.caster = caster;
        this.type = type;
        this.spell = spell;
    }

    public Vector3f getVelocity() {
        return velocity;
    }

    public void setVelocity(Vector3f velocity) {
        this.velocity = velocity;
    }

    public float getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public Entity getCaster() {
        return caster;
    }

    public void setCaster(Entity caster) {
        this.caster = caster;
    }

    public Color getColor() {
        return getColor(type);
    }

    public Spell getSpell() {
        return spell;
    }

    public Type getType() {
        return type;
    }

    public void refreshProgress() {
        if(animationProgress >= spell.getFrames().size()) animationProgress = 0;
    }

    public void incrementProgress() {
        if(animationProgress+1 >= spell.getFrames().size()) animationProgress = 0;
        else animationProgress++;
    }

    public int getAnimationProgress() {
        return animationProgress;
    }
    

    public static Color getColor(Type t) {
        switch (t) {
            case FIRE: return Color.ORANGE;
            case BLOOD: return Color.RED;
            case WATER: return Color.BLUE;
            case WIND:  return Color.GREEN;
            case STONE: return Color.DARK_GRAY;
            case LIGHT: return Color.YELLOW;
            case ARCANE: return Color.MAGENTA;
            default: return Color.BLACK;
        }
    }
}
