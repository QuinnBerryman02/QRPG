package util;

public class Entity extends GameObject {
    private Hitbox hitbox;
    public Entity(float w, float h, Point3f c) { 
    	super(w, h, c);
        hitbox = new Hitbox(c, w, h);
	}

    public void move(Vector3f v) {
        getCentre().applyVector(v); 
        hitbox.applyVector(v);
    }

    public Hitbox getHitbox() {
        return hitbox;
    }
}
