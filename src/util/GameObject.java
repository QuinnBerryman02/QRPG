package util;

//Programmed by Quinn Berrman
//Student number: 20363251


import java.io.Serializable;

/*
 * Created by Abraham Campbell on 15/01/2020.
 *   Copyright (c) 2020  Abraham Campbell

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
   
   (MIT LICENSE ) e.g do what you want with this :-) 
 */ 
public class GameObject implements Serializable {
	private Hitbox hitbox;
	protected Point3f centre= new Point3f(0,0,0);			// Centre of object, using 3D as objects may be scaled  
	protected float width = 10;
	protected float height = 10;

	protected GameObject() {

	}
	
    public GameObject(float width, float height, Point3f centre) { 
		hitbox = new Hitbox(centre.plusVector(new Vector3f()), width, height);
    	this.width = width;
		this.height = height;
		this.centre = centre.plusVector(new Vector3f());
	}

	public Point3f getCentre() {
		return centre;
	}

	public void setCentre(Point3f centre) {
		this.centre = centre;
	}

	public float getWidth() {
		return width;
	}

	public float getHeight() {
		return height;
	}

	public void move(Vector3f v) {
        getCentre().applyVector(v); 
        hitbox.applyVector(v);
    }

    public Hitbox getHitbox() {
        return hitbox;
    }

	@Override
	public String toString() {
		return 	"[centre= " + centre + "]," +
				"[hitbox= " + hitbox + "]," +
				"[width= " + width + "]," +
				"[height= " + height + "]";
	}
}