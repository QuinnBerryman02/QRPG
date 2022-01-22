import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

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

//Singeton pattern
public class Controller implements KeyListener {
	private static boolean keyAPressed = false;
	private static boolean keySPressed = false;
	private static boolean keyDPressed = false;
	private static boolean keyWPressed = false;
	private static boolean keySpacePressed = false;
	   
	private static final Controller instance = new Controller();
	  
	public static Controller getInstance(){
	    return instance;
	}

	@Override
	public void keyTyped(KeyEvent e) {}

	@Override
	public void keyPressed(KeyEvent e) { 
		switch (e.getKeyChar()) {
			case 'a': keyAPressed = true; break;  
			case 's': keySPressed = true; break;
			case 'w': keyWPressed = true; break;
			case 'd': keyDPressed = true; break;
			case ' ': keySpacePressed = true; break; 
		    default: break;
		}  
	}

	@Override
	public void keyReleased(KeyEvent e) { 
		switch (e.getKeyChar()) {
			case 'a': keyAPressed = false; break;  
			case 's': keySPressed = false; break;
			case 'w': keyWPressed = false; break;
			case 'd': keyDPressed = false; break;
			case ' ': keySpacePressed = false; break; 
		    default: break;
		}  
	}
 
	public boolean isKeyAPressed() {
		return keyAPressed;
	}

	public void setKeyAPressed(boolean value) {
		keyAPressed = value;
	}

	public boolean isKeySPressed() {
		return keySPressed;
	}

	public void setKeySPressed(boolean value) {
		keySPressed = value;
	}

	public boolean isKeyDPressed() {
		return keyDPressed;
	}

	public void setKeyDPressed(boolean value) {
		keyDPressed = value;
	}

	public boolean isKeyWPressed() {
		return keyWPressed;
	}

	public void setKeyWPressed(boolean value) {
		keyWPressed = value;
	}

	public boolean isKeySpacePressed() {
		return keySpacePressed;
	}

	public void setKeySpacePressed(boolean value) {
		keySpacePressed = value;
	}
}