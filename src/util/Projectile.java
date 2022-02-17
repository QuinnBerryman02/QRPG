package util;

public class Projectile extends GameObject{
    private Vector3f velocity;
    private int damage;
    private Entity caster;

    public Projectile(float width, float height, Point3f centre, Vector3f velocity, int damage, Entity caster) {
        super(width, height, centre);
        this.velocity = velocity;
        this.damage = damage;
        this.caster = caster;
    }

    public Vector3f getVelocity() {
        return velocity;
    }

    public void setVelocity(Vector3f velocity) {
        this.velocity = velocity;
    }

    public int getDamage() {
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
}
