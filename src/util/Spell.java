package util;

import main.MainWindow;
import mvc.Viewer;

import java.awt.Color;
import java.io.File;
import java.util.ArrayList;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;


public class Spell {
    private ArrayList<BufferedImage> frames = new ArrayList<BufferedImage>();
    private final static float MAX_VELOCITY = 4f;
    private int manaCost;

    private Aim aim;
    private int damage;
    private float radius;
    private Projectile.Type element;
    private String name = "";

    public enum Aim {
        AIM_BY_MOUSE,
        FRONT_AND_BACK,
        SIDES,
        OCTOPUS,
    }

    public Spell() {
        aim = Aim.AIM_BY_MOUSE;
        damage = 5;
        radius = 0.25f;
        element = Projectile.Type.FIRE;
        updateFrames();
        calculateCost();
    }

    public Color getColor() {
        return Projectile.getColor(element);
    }

    public void cast(Entity e) {
        calculateCost();
        System.out.println(toString());
        if(e.getMana() >= manaCost) {
            e.setMana(e.getMana()-manaCost);
            (new Thread() {
                public void run() {
                    try {
                        sleep(delayOfElement());
                    } catch (Exception e) {}
                    Vector3f[] directions = applyAim(e);
                    for (Vector3f dir : directions) {
                        Projectile p = new Projectile(radius*2, radius*2, e.getCentre(), dir, damage, e, element, Spell.this);
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
        return damage;
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
        switch(element) {
            case STONE: return 0.9f;
            case WATER: return 1.5f;
            case WIND:  return 1f;
            case FIRE: return 2.5f;
            case ICE:   return 2f;
            case BLOOD: return 7f;
            case LIGHT: return 10f;
            default: return 1f;
        }
    }

    public int delayOfElement() {
        switch(element) {
            case STONE: return 500;
            case WATER: return 100;
            case WIND:  return 50;
            case FIRE: return 150;
            case ICE:   return 200;
            case BLOOD: return 600;
            case LIGHT: return 1;
            default: return 100;
        }
    }

    public void calculateCost() {
        manaCost = (int)(radius * radius * damage * numberOfDirections() * costOfElement());
    }

    public int getManaCost() {
        return manaCost;
    }

    @Override
    public String toString() {
        calculateCost();
        return
        "[name: " + name + "]," +
        "[manaCost: " + manaCost + "]," +
        "[castDelay: " + delayOfElement() + "]," +
        "[aim: " + aim.toString() + "]," +
        "[baseDamage: " + damage + "]," +
        "[totalDamage: " + getDamage() + "]," +
        "[radius: " + radius + "]," +
        "[type: " + element + "]";
    }

    public Aim getAim() {
        return aim;
    }

    public static float getMaxVelocity() {
        return MAX_VELOCITY;
    }

    public float getRadius() {
        return radius;
    }

    public Projectile.Type getElement() {
        return element;
    }

    public void setAim(Aim aim) {
        this.aim = aim;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public void setElement(Projectile.Type element) {
        this.element = element;
        updateFrames();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<BufferedImage> getFrames() {
        return frames;
    }

    public void updateFrames() {
        frames.clear();
        switch (element) {
            case ARCANE:
                break;
            case BLOOD:
                break;
            case FIRE:loadFire(2);
                break;
            case ICE:
                break;
            case LIGHT:
                break;
            case STONE:
                break;
            case WATER:
                break;
            case WIND:
                break;
            default:
                break;
            
        }
    }

    public void loadFire(int variant) {
        for(int i=0;i<7;i++) {
            int p = (4*i) + variant;
            try {
                frames.add(ImageIO.read(new File("res/effects/fire/Effects_Fire_0_" + ((p < 10) ? ("0" + p) : (p)) + ".png")));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
