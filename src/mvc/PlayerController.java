package mvc;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

public class PlayerController extends Controller implements KeyListener, MouseListener, MouseMotionListener, MouseWheelListener{
    @Override
	public void keyTyped(KeyEvent e) {}

	@Override
	public void keyPressed(KeyEvent e) {
		switch (e.getKeyChar()) {
			case 'a': leftPressed = true; break;  
			case 's': downPressed = true; break;
			case 'w': upPressed = true; break;
			case 'd': rightPressed = true; break;
			case 'q': attackPressed = true; break;
            case 'i': talkPressed = true; break;
			case 'e': castPressed = true; break;
			case ' ': changeSkinPressed = true; break; 
			case 'j': guildPressed = true; break;
			case 'h': questPressed = true; break;
			case 'g': spellPressed = true; break;
		    default: break;
		}  
	}

	@Override
	public void keyReleased(KeyEvent e) { 
		switch (e.getKeyChar()) {
			case 'a': leftPressed = false; break;  
			case 's': downPressed = false; break;
			case 'w': upPressed = false; break;
			case 'd': rightPressed = false; break;
			case 'q': attackPressed = false; break;
            case 'i': talkPressed = false; break;
			case 'e': castPressed = false; break;
			case ' ': changeSkinPressed = false; break; 
			case 'j': guildPressed = false; break;
			case 'h': questPressed = false; break;
			case 'g': spellPressed = false; break;
		    default: break;
		}  
	}

	@Override
	public void mouseClicked(MouseEvent e) {}
	@Override
	public void mouseEntered(MouseEvent e) {}
	@Override
	public void mouseExited(MouseEvent e) {
		clear();
	}
	@Override
	public void mousePressed(MouseEvent e) {}
	@Override
	public void mouseReleased(MouseEvent e) {}
	@Override
	public void mouseDragged(MouseEvent e) {}
	@Override
	public void mouseMoved(MouseEvent e) {
		mouseX = e.getX();
		mouseY = e.getY();	
	}
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		mouseWheelMoved = e.getPreciseWheelRotation();
	}
}
