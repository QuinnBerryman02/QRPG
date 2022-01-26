package util;

public class Entity extends GameObject {
    public Entity(int width, int height, Point3f centre) { 
    	super(width, height, centre);
	}

    public void move(Vector3f v) {
        getCentre().applyVector(v); 
    }
}
