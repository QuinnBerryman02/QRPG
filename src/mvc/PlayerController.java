package mvc;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class PlayerController extends Controller implements KeyListener {
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
            case 'i': keyIPressed = true; break;
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
            case 'i': keyIPressed = false; break;
			case 'e': keyEPressed = false; break;
			case ' ': keySpacePressed = false; break; 
			case 'y': keyUpPressed = false; break;
			case 'j': keyRightPressed = false; break;
			case 'h': keyDownPressed = false; break;
			case 'g': keyLeftPressed = false; break;
		    default: break;
		}  
	}
}
