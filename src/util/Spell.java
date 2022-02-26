package util;

import main.MainWindow;
import mvc.Viewer;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;


public class Spell implements Serializable{
    transient private final static float MAX_VELOCITY = 4f;

    transient private ArrayList<BufferedImage> frames = new ArrayList<BufferedImage>();
    transient private int manaCost;

    private Aim aim;
    private int damage;
    private float radius;
    private Projectile.Type element;
    private String name;

    public enum Aim {
        AIM_BY_MOUSE,
        FRONT_AND_BACK,
        SIDES,
        OCTOPUS,
    }

    public Spell() {
        name = "";
        element = Projectile.Type.FIRE;
        damage = 5;
        radius = 0.25f;
        aim = Aim.AIM_BY_MOUSE;
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
            Vector3f[] directions = applyAim(e);
            if(directions[0].getX()!=0||directions[0].getY()!=0) {
                MainWindow.getAudioManager().playSoundByName("magic");
                e.setMana(e.getMana()-manaCost);
                (new Thread() {
                    public void run() {
                        try {
                            sleep(delayOfElement());
                        } catch (Exception e) {}
                        for (Vector3f dir : directions) {
                            Projectile p = new Projectile(radius*2, radius*2, e.getCentre(), dir, damage * e.getDamage() / 10, e, element, Spell.this);
                            MainWindow.getModel().getProjectiles().add(p);
                        }
                    };
                }).start();
            } else {
                //Wasnt aiming
            }
        } else {
            //fizzle sound
        }
    }

    public static Vector3f[] aimByMouse(Entity e) {
        return new Vector3f[] {e.getController().getAimDirection().byScalar(MAX_VELOCITY)};
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
        Random r = new Random();
        switch (element) {
            case ARCANE:loadArcane(r.nextInt(6)+1);
                break;
            case BLOOD:loadBlood(r.nextInt(5)+1);
                break;
            case FIRE:loadFire(r.nextInt(4)+1);
                break;
            case LIGHT:loadLight(r.nextInt(5)+1);
                break;
            case STONE:loadStone(r.nextInt(5)+1);
                break;
            case WATER:loadWater(r.nextInt(5)+1);
                break;
            case WIND:loadWind(r.nextInt(5)+1);
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

    public void loadBlood(int variant) {
        for(int i=1;i<5;i++) {
            int p = (5*(variant-1)) + i;
            try {
                frames.add(ImageIO.read(new File("res/effects/blood/Blood-Magic-Effect_" + ((p < 10) ? ("0" + p) : (p)) + ".png")));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void loadWater(int variant) {
        File directory = new File("res/effects/water/0" + variant);
        for (File f : directory.listFiles()) {
            try {
                frames.add(ImageIO.read(f));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void loadWind(int variant) {
        for(int i=1;i<5;i++) {
            int p = (5*(variant-1)) + i;
            try {
                frames.add(ImageIO.read(new File("res/effects/wind/Pure_" + ((p < 10) ? ("0" + p) : (p)) + ".png")));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void loadLight(int variant) {
        for(int i=1;i<5;i++) {
            int p = (5*(variant-1)) + i;
            try {
                frames.add(ImageIO.read(new File("res/effects/light/LightEffect_" + ((p < 10) ? ("0" + p) : (p)) + ".png")));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void loadArcane(int variant) {
        File directory = new File("res/effects/arcane/0" + variant);
        for (File f : directory.listFiles()) {
            try {
                frames.add(ImageIO.read(f));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void loadStone(int variant) {
        for(int i=1;i<5;i++) {
            int p = (5*(variant-1)) + i;
            try {
                frames.add(ImageIO.read(new File("res/effects/stone/Earth-Impact_" + ((p < 10) ? ("0" + p) : (p)) + ".png")));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
    }

    private void readObject(ObjectInputStream in) throws ClassNotFoundException, IOException {
        in.defaultReadObject();
        frames = new ArrayList<BufferedImage>();
        calculateCost();
        updateFrames();
    }
}
