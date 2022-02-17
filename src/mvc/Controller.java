package mvc;


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
public class Controller {
	protected boolean keyAWasPressed = false;
	protected boolean keySWasPressed = false;
	protected boolean keyDWasPressed = false;
	protected boolean keyWWasPressed = false;
	protected boolean keyAPressed = false;
	protected boolean keySPressed = false;
	protected boolean keyDPressed = false;
	protected boolean keyWPressed = false;
	protected boolean keyQPressed = false;
	protected boolean keyEPressed = false;
	protected boolean keyIPressed = false;
	protected boolean keySpacePressed = false;
	protected boolean keyUpPressed = false;
	protected boolean keyRightPressed = false;
	protected boolean keyDownPressed = false;
	protected boolean keyLeftPressed = false;

	
 
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
	public void setKeyEPressed(boolean value) {
		keyEPressed = value;
	}

	public void setKeyQPressed(boolean value) {
		keyQPressed = value;
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
	public void setKeyDownPressed(boolean value) {
		keyDownPressed = value;
	}
	public void setKeyLeftPressed(boolean value) {
		keyLeftPressed = value;
	}
	public void setKeyRightPressed(boolean value) {
		keyRightPressed = value;
	}
	public void setKeyUpPressed(boolean value) {
		keyUpPressed = value;
	}

	public void setKeyAWasPressed(boolean value) {
		keyAWasPressed = value;
	}
	public void setKeyDWasPressed(boolean value) {
		keyDWasPressed = value;
	}
	public void setKeySWasPressed(boolean value) {
		keySWasPressed = value;
	}
	public void setKeyWWasPressed(boolean value) {
		keyWWasPressed = value;
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

	public void setKeyIPressed(boolean value) {
		this.keyIPressed = value;
	}

	public boolean isKeyIPressed() {
		return keyIPressed;
	}

	public void clear() {
		keyAPressed = false; 
		keySPressed = false; 
		keyWPressed = false; 
		keyDPressed = false; 
		keyQPressed = false; 
		keyIPressed = false; 
		keyEPressed = false; 
		keySpacePressed = false; 
		keyUpPressed = false; 
		keyRightPressed = false; 
		keyDownPressed = false; 
		keyLeftPressed = false; 
	}
}