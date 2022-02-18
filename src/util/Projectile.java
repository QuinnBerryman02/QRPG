package util;

import java.awt.Color;

public class Projectile extends GameObject{
    private Vector3f velocity;
    private float damage;
    private Entity caster;
    private Type type;
    public enum Type {
        FIRE,
        WATER,
        ICE,
        LIGHTNING,
        STONE,
        WIND,
        LAVA
    }

    public Projectile(float width, float height, Point3f centre, Vector3f velocity, float damage, Entity caster, Type type) {
        super(width, height, centre);
        this.velocity = velocity;
        this.damage = damage;
        this.caster = caster;
        this.type = type;
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
        switch (type) {
            case FIRE: return Color.ORANGE;
            case LAVA: return Color.RED;
            case WATER: return Color.BLUE;
            case WIND:  return Color.GREEN;
            case STONE: return Color.DARK_GRAY;
            case LIGHTNING: return Color.YELLOW;
            case ICE:   return Color.CYAN;
            default: return Color.BLACK;
        }
    }
}
