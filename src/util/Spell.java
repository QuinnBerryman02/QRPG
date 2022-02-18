package util;

import main.MainWindow;
import mvc.Viewer;

public class Spell {
    private final static float MAX_VELOCITY = 4f;
    private int manaCost;
    
    private int castDelay;
    private Aim aim;
    private int damage;
    private float radius;
    private Projectile.Type type;

    public enum Aim {
        AIM_BY_MOUSE,
        FRONT_AND_BACK,
        SIDES,
        OCTOPUS,
    }

    public Spell() {
        //TODO make spell maker ui menu
        aim = Aim.AIM_BY_MOUSE;
        damage = 5;
        radius = 0.5f;
        type = Projectile.Type.FIRE;
        castDelay = 100;
        calculateCost();
    }

    public void cast(Entity e) {
        calculateCost();
        System.out.println(toString());
        if(e.getMana() >= manaCost) {
            e.setMana(e.getMana()-manaCost);
            (new Thread() {
                public void run() {
                    try {
                        sleep(castDelay);
                    } catch (Exception e) {}
                    Vector3f[] directions = applyAim(e);
                    for (Vector3f dir : directions) {
                        Projectile p = new Projectile(radius, radius, e.getCentre(), dir, getDamage(), e, type);
                        MainWindow.getModel().getProjectiles().add(p);
                    }
                };
            }).start();
        }
    }

    public static Vector3f[] aimByMouse(Entity e) {
        Point3f dst = Viewer.screenToWorldSpace(new Point3f(e.getController().getMouseX(),e.getController().getMouseY(),0f), MainWindow.getModel().getPlayer().getCentre());
        Point3f src = e.getCentre();

		Vector3f v = dst.minusPoint(src);
		float vx = v.getX();
		float vy = v.getY();
		vx = vx > MAX_VELOCITY ? MAX_VELOCITY : vx;
		vx = vx < -MAX_VELOCITY ? -MAX_VELOCITY : vx;
		vy = vy > MAX_VELOCITY ? MAX_VELOCITY : vy;
		vy = vy < -MAX_VELOCITY ? -MAX_VELOCITY : vy;
		return new Vector3f[] {new Vector3f(vx,vy,0f)};
    }

    public static Vector3f[] frontAndBack(Entity e) {
        Vector3f[] vectors = new Vector3f[2];
        vectors[0] = aimByMouse(e)[0];
        vectors[1] = vectors[0].rotateCreate(Math.PI);
		return vectors;
    }

    public static Vector3f[] sides(Entity e) {
        Vector3f[] vectors = new Vector3f[4];
        vectors[0] = aimByMouse(e)[0];
        for(int i=1;i<=3;i++) {
            vectors[i] = vectors[i-1].rotateCreate(Math.PI / 2);
        }
		return vectors;
    }

    public static Vector3f[] octopus(Entity e) {
        Vector3f[] vectors = new Vector3f[8];
        vectors[0] = aimByMouse(e)[0];
        for(int i=1;i<=7;i++) {
            vectors[i] = vectors[i-1].rotateCreate(Math.PI / 4);
        }
		return vectors;
    }

    public float getDamage() {
        return damage * (costOfElement()*costOfElement());
    }

    public Vector3f[] applyAim(Entity e) {
        switch (aim) {
            case AIM_BY_MOUSE:return aimByMouse(e);
            case FRONT_AND_BACK:return frontAndBack(e);
            case SIDES:return sides(e);
            case OCTOPUS:return octopus(e);
            default: return null;
        }
    }

    public int numberOfDirections() {
        switch (aim) {
            case AIM_BY_MOUSE:return 1;
            case FRONT_AND_BACK:return 2;
            case SIDES:return 4;
            case OCTOPUS:return 8;
            default: return -1;
        }
    }

    public float costOfElement() {
        switch(type) {
            case STONE: return 0.9f;
            case WATER: return 1.1f;
            case WIND:  return 1.2f;
            case FIRE: return 1.3f;
            case ICE:   return 1.4f;
            case LAVA: return 2f;
            case LIGHTNING: return 3f;
            default: return 1f;
        }
    }

    public void calculateCost() {
        manaCost = (int)(radius * radius * damage * numberOfDirections() * costOfElement() / (castDelay / 500f));
    }

    public int getManaCost() {
        return manaCost;
    }

    @Override
    public String toString() {
        return
        "[manaCost: " + manaCost + "]," +
        "[castDelay: " + castDelay + "]," +
        "[aim: " + aim.toString() + "]," +
        "[damage: " + damage + "]," +
        "[radius: " + radius + "]," +
        "[type: " + type + "]";
    }

    public Aim getAim() {
        return aim;
    }

    public int getCastDelay() {
        return castDelay;
    }

    public static float getMaxVelocity() {
        return MAX_VELOCITY;
    }

    public float getRadius() {
        return radius;
    }

    public Projectile.Type getType() {
        return type;
    }

    public void setAim(Aim aim) {
        this.aim = aim;
    }

    public void setCastDelay(int castDelay) {
        this.castDelay = castDelay;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public void setManaCost(int manaCost) {
        this.manaCost = manaCost;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public void setType(Projectile.Type type) {
        this.type = type;
    }
}
