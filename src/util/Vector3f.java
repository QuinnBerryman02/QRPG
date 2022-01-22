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

public class Vector3f {
	private float x = 0;
	private float y = 0;
	private float z = 0;
	
	public Vector3f() {  
		this.x = 0.0f;
		this.y = 0.0f;
		this.z = 0.0f;
	}
	 
	public Vector3f(float x, float y, float z) { 
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public Vector3f plusVector(Vector3f additional) { 
		return new Vector3f(x + additional.getX(), y + additional.getY(), z + additional.getZ());
	} 
	
	public Vector3f minusVector(Vector3f minus) { 
		return new Vector3f(x - minus.getX(), y - minus.getY(), z - minus.getZ());
	}
	
	public Point3f plusPoint(Point3f additional) { 
		return new Point3f(x + additional.getX(), y + additional.getY(), z + additional.getZ());
	} 
	
	public Vector3f byScalar(float scale) {
		return new Vector3f(x * scale, y * scale, z * scale);
	}
	
	public Vector3f negateVector() {
		return new Vector3f(-x, -y, -z);
	}
	
	public float length() {
	    return (float) Math.sqrt(x * x + y * y + z * z);
	}
	
	public Vector3f normal() {
		float lengthOfTheVector =  length();
		return byScalar(1.0f/ lengthOfTheVector); 
	} 
	
	public float dot(Vector3f v) { 
		return (x * v.getX() + y * v.getY() + z * v.getZ());
	}
	
	public Vector3f cross(Vector3f v) { 
		float u0 = (y * v.getZ() - z * v.getY());
		float u1 = (z * v.getX() - x * v.getZ());
		float u2 = (x * v.getY() - y * v.getX());
		return new Vector3f(u0,u1,u2);
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
		return ("Vector3F (" + x + "," + y + "," + z + ")");
    }
}