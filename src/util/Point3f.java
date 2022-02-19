package util;
/*
 * Modified by Abraham Campbell on 15/01/2020.
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
//Modified from Graphics 3033J course point class  by Abey Campbell 

import java.util.Random;

public class Point3f {
	private float x;
	private float y;
	private float z;

	public Point3f() { 
		this.x = 0.0f;
		this.y = 0.0f;
		this.z = 0.0f;
	}
	public Point3f(float x, float y, float z) { 
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public float getPostion(int postion) {
		switch(postion) {
			case 0: return x;
			case 1: return y;
			case 2: return z; 
			default: return Float.NaN;  
		} 
	}

	public Point3f plusVector(Vector3f additional) { 
		return new Point3f(x + additional.getX(), y + additional.getY(), z + additional.getZ());
	} 
	
	public Point3f minusVector(Vector3f minus) { 
		return new Point3f(x - minus.getX(), y - minus.getY(), z - minus.getZ());
	}
	
	public Vector3f minusPoint(Point3f minus) { 
		return new Vector3f(x - minus.getX(), y - minus.getY(), z - minus.getZ());
	}
	 
	public void applyVector(Vector3f vector) { 
		 setX(x + vector.getX());
		 setY(y + vector.getY());
		 setZ(z + vector.getZ()); 
	}

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	public float getZ() {
		return z;
	}

	public void setZ(float z) {
		this.z = z;
	}

	@Override
	public String toString() {
		return ("Point3F (" + x + "," + y + "," + z + ")");
    }

	public Vector3f calculateDirectionToPoint(Point3f p) {
		//returns a vector with 1s and 0s to represent the direction
		float dx = p.getX() - x;
		float dy = p.getY() - y;
		if(dx==0 && dy==0) return new Vector3f();
		double rad = Math.atan2(-dy, dx);
		int eighth = radToEighth(rad);
		int vx;
		int vy;
		switch(eighth % 8) {
			case 7:case 0:case 1:	vx=-1; break;
			case 3:case 4:case 5: 	vx=1;  break;
			default: 				vx=0;  break;
		}
		switch(eighth % 8) {
			case 1:case 2:case 3:	vy=1;  break;
			case 5:case 6:case 7: 	vy=-1; break;
			default: 				vy=0;  break;
		}
		return new Vector3f(vx,vy,0f);
	}

	public static int radToEighth(double rad) {
		int eighth = (int)Math.round((1 / Math.PI) * 4 * rad + 4);
		return eighth;
	}

	public static Point3f generateRandomPoint(Point3f topleft, Point3f botright) {
		Random r = new Random(System.currentTimeMillis());
		float x = (float)r.nextDouble() * (botright.getX() - topleft.getX()) + topleft.getX();
		float y = (float)r.nextDouble() * (botright.getY() - topleft.getY()) + topleft.getY();
		return new Point3f(x,y,0f);
	}
	public static void main(String[] args) {
		System.out.printf("rad:%.2fpi eighth:%d\n",(0.0),radToEighth(0));
		System.out.printf("rad:%.2fpi eighth:%d\n",(0.25),radToEighth(0.25 * Math.PI));
		System.out.printf("rad:%.2fpi eighth:%d\n",(0.5),radToEighth(0.5	* Math.PI));
		System.out.printf("rad:%.2fpi eighth:%d\n",(0.75),radToEighth(0.75	* Math.PI));
		System.out.printf("rad:%.2fpi eighth:%d\n",(1.0),radToEighth(1	* Math.PI));
		System.out.printf("rad:%.2fpi eighth:%d\n",(-0.25),radToEighth(-0.25	* Math.PI));
		System.out.printf("rad:%.2fpi eighth:%d\n",(-0.5),radToEighth(-0.5	* Math.PI));
		System.out.printf("rad:%.2fpi eighth:%d\n",(-0.75),radToEighth(-0.75	* Math.PI));
		System.out.printf("rad:%.2fpi eighth:%d\n",(-1.0),radToEighth(-1	* Math.PI));
		Point3f p = new Point3f(0f,0f,0f);
		for(int i=-10;i<=10;i+=10) {
			for(int j=-10;j<=10;j+=10) {
				Point3f e = new Point3f(j,i,0f);
				Vector3f v = e.calculateDirectionToPoint(p);
				System.out.printf("[%d,%d] -> [%d,%d] via [%d,%d]\n",j,i,0,0,(int)v.getX(),(int)v.getY());
			} 
		}
	}
}