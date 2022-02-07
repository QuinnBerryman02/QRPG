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
	private static boolean keyAWasPressed = false;
	private static boolean keySWasPressed = false;
	private static boolean keyDWasPressed = false;
	private static boolean keyWWasPressed = false;
	private static boolean keyAPressed = false;
	private static boolean keySPressed = false;
	private static boolean keyDPressed = false;
	private static boolean keyWPressed = false;
	private static boolean keyQPressed = false;
	private static boolean keyEPressed = false;
	private static boolean keySpacePressed = false;
	private static boolean keyUpPressed = false;
	private static boolean keyRightPressed = false;
	private static boolean keyDownPressed = false;
	private static boolean keyLeftPressed = false;
	   
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
			case 'q': keyQPressed = true; break;
			case 'e': keyEPressed = true; break;
			case ' ': keySpacePressed = true; break; 
			case 'y': keyUpPressed = true; break;
			case 'j': keyRightPressed = true; break;
			case 'h': keyDownPressed = true; break;
			case 'g': keyLeftPressed = true; break;
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
			case 'q': keyQPressed = false; break;
			case 'e': keyEPressed = false; break;
			case ' ': keySpacePressed = false; break; 
			case 'y': keyUpPressed = false; break;
			case 'j': keyRightPressed = false; break;
			case 'h': keyDownPressed = false; break;
			case 'g': keyLeftPressed = false; break;
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

	public boolean isKeyEPressed() {
		return keyEPressed;
	}

	public boolean isKeyQPressed() {
		return keyQPressed;
	}
	public void setKeyEPressed(boolean keyEPressed) {
		Controller.keyEPressed = keyEPressed;
	}

	public void setKeyQPressed(boolean keyQPressed) {
		Controller.keyQPressed = keyQPressed;
	}

	public boolean isKeyDownPressed() {
		return keyDownPressed;
	}
	public boolean isKeyLeftPressed() {
		return keyLeftPressed;
	}
	public boolean isKeyRightPressed() {
		return keyRightPressed;
	}
	public boolean isKeyUpPressed() {
		return keyUpPressed;
	}
	public void setKeyDownPressed(boolean keyDownPressed) {
		Controller.keyDownPressed = keyDownPressed;
	}
	public void setKeyLeftPressed(boolean keyLeftPressed) {
		Controller.keyLeftPressed = keyLeftPressed;
	}
	public void setKeyRightPressed(boolean keyRightPressed) {
		Controller.keyRightPressed = keyRightPressed;
	}
	public void setKeyUpPressed(boolean keyUpPressed) {
		Controller.keyUpPressed = keyUpPressed;
	}

	public void setKeyAWasPressed(boolean keyAWasPressed) {
		Controller.keyAWasPressed = keyAWasPressed;
	}
	public void setKeyDWasPressed(boolean keyDWasPressed) {
		Controller.keyDWasPressed = keyDWasPressed;
	}
	public void setKeySWasPressed(boolean keySWasPressed) {
		Controller.keySWasPressed = keySWasPressed;
	}
	public void setKeyWWasPressed(boolean keyWWasPressed) {
		Controller.keyWWasPressed = keyWWasPressed;
	}
	public boolean isKeyAWasPressed() {
		return keyAWasPressed;
	}
	public boolean isKeyDWasPressed() {
		return keyDWasPressed;
	}
	public boolean isKeySWasPressed() {
		return keySWasPressed;
	}
	public boolean isKeyWWasPressed() {
		return keyWWasPressed;
	}
}