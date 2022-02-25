package mvc;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;

import main.MainWindow;
import net.java.games.input.ControllerEnvironment;
import util.Player;
import util.Point3f;
import util.Vector3f;
import net.java.games.input.Component;

public class PlayerController extends mvc.Controller implements KeyListener, MouseListener, MouseMotionListener, MouseWheelListener{
	private static final String myController = "Wireless Gamepad";
	private static final float PRECISION = 0.3f;
	private net.java.games.input.Controller gameController = null;
	private boolean controllerMode = false;
	private ArrayList<Button> buttonSet = new ArrayList<Button>();
	private ArrayList<Toggle> toggleSet = new ArrayList<Toggle>();

	public PlayerController() {
		ControllerEnvironment ce = ControllerEnvironment.getDefaultEnvironment();
		for (net.java.games.input.Controller controller : ce.getControllers()) {
			if(controller.getName().equals(myController)) {
				gameController = controller;
			}
		}
		if(gameController==null) {
			System.out.println("Didnt Find Controller");
			controllerMode = false;
		} else {
			System.out.println("Found Controller");
			controllerMode = true;
			
		}
		initializeDefaultControls();
	}

	public static void main(String[] args) {
		PlayerController p = new PlayerController();
		for(Component c : p.getGameController().getComponents()) {
			System.out.println(c.getName());
		}
	}

	public net.java.games.input.Controller getGameController() {
		return gameController;
	}

	@Override
	public void keyTyped(KeyEvent e) {}

	@Override
	public void keyPressed(KeyEvent e) {
		if(!controllerMode) {
			for (Button control : buttonSet) {
				if(control.getKey()==e.getKeyCode()) {
					pressByName(control.getName(), true);
					return;
				}
			}
			for (Toggle t : toggleSet) {
				for (int i=0;i<t.getKeys().length;i++) {
					if(t.getKeys()[i]==e.getKeyCode()) {
						boolean[] vals = t.getValues();
						vals[i] = true;
						return;
					}
				}
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if(!controllerMode) {
			for (Button control : buttonSet) {
				if(control.getKey()==e.getKeyCode()) {
					pressByName(control.getName(), false);
				}
			}
			for (Toggle t : toggleSet) {
				for (int i=0;i<t.getKeys().length;i++) {
					if(t.getKeys()[i]==e.getKeyCode()) {
						boolean[] vals = t.getValues();
						vals[i] = false;
						return;
					}
				}
			}
		}
	}

	public Component findComponentByName(String name) {
		if(controllerMode) {
			for(Component c : gameController.getComponents()) {
				if(c.getName().equals(name)) {
					return c;
				}
			}
		}
		return null;
	}
	public Component[] findComponentsByName(String[] names) {
		if(controllerMode) {
			Component[] cs = new Component[names.length];
			for(int i=0;i<names.length;i++) {
				for(Component c : gameController.getComponents()) {
					if(c.getName().equals(names[i])) {
						cs[i] = c;
					}
				}
			}
			return cs;
		} 
		return null;
	}

	public void initializeDefaultControls() {
		buttonSet.add(makeButton("skin", KeyEvent.VK_H, "Button 0"));		//B
		buttonSet.add(makeButton("talk", KeyEvent.VK_T, "Button 1"));		//A
		buttonSet.add(makeButton("quest", KeyEvent.VK_Y, "Button 2"));		//Y
		buttonSet.add(makeButton("spell", KeyEvent.VK_U, "Button 3"));		//X
		buttonSet.add(makeButton("attack", KeyEvent.VK_Q, "Button 4"));		//L
		buttonSet.add(makeButton("cast", KeyEvent.VK_E, "Button 5"));		//R
		buttonSet.add(makeButton("escape", KeyEvent.VK_ESCAPE,"Button 7")); //ZR
		toggleSet.add(makeToggle("move", new int[] {KeyEvent.VK_W,KeyEvent.VK_D,KeyEvent.VK_S,KeyEvent.VK_A}, new String[]{"X Axis","Y Axis"}));
		toggleSet.add(makeToggle("aim", new int[] {-1,-1,-1,-1}, new String[]{"X Rotation","Y Rotation"}));
	}

	public Button makeButton(String name, int key, String buttonName) {
		Button c = new Button(name);
		c.setKey(key);
		c.setButton(findComponentByName(buttonName));
		return c;
	}

	public Toggle makeToggle(String name, int[] keys, String[] toggleNames) {
		Toggle t = new Toggle(name);
		t.setKeys(keys);
		t.setToggles(findComponentsByName(toggleNames));
		return t;
	}

	public void setDirectionByName(String s, Vector3f v) {
		switch(s) {
			case "move": moveDirection = v;break;
			case "aim": aimDirection = v;break;
		}
	}

	public void pressByName(String s, boolean value) { 
		switch (s) {
			case "attack": attackPressed = value; break;
			case "talk": talkPressed = value; break;
			case "cast": castPressed = value; break;
			case "skin": changeSkinPressed = value; break; 
			case "quest": questPressed = value; break;
			case "spell": spellPressed = value; break;
			case "escape": escapePressed = value; break;
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
		if(!controllerMode) {
			aimDirection = new Vector3f(1,0,0).rotateCreate(Math.atan2(e.getY()-MainWindow.getH()/2, e.getX()-MainWindow.getW()/2));
		}
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		mouseWheelMoved = e.getPreciseWheelRotation();
	}

	public void poll() {
		if(gameController!=null) {
			gameController.poll();
		}
	}

	public void update() {
		if(controllerMode) {
			poll();
			for (Button button : buttonSet) {
				if(button.getButton().getPollData()==1f) {
					pressByName(button.getName(), true);
				} else {
					pressByName(button.getName(), false);
				}
			}
			for (Toggle toggle : toggleSet) {
				float x = toggle.getToggles()[0].getPollData();
				float y = toggle.getToggles()[1].getPollData();
				float hypeSqrd = x*x + y*y;
				if(hypeSqrd>PRECISION*PRECISION) {
					Vector3f v = new Vector3f(1,0,0).rotateCreate(Math.atan2(y, x));
					setDirectionByName(toggle.getName(), v);
				} else {
					setDirectionByName(toggle.getName(), new Vector3f());
				}
				
			}
		} else {
			for (Toggle t : toggleSet) {
				if(t.getName().equals("move")) {
					float x = 0;
					float y = 0;
					boolean[] vals = t.getValues();
					if(vals[0]) y--;
					if(vals[1]) x++;
					if(vals[2]) y++;
					if(vals[3]) x--;
					if(x!=0 && y!=0) {
						x /= Math.sqrt(2);
						y /= Math.sqrt(2);
					}
					setDirectionByName(t.getName(), new Vector3f(x, y, 0));
				}
			}
		}
	}

	@Override
	public void clear() {
		super.clear();
		java.awt.Component c = MainWindow.getCanvas();
		for (Button button : buttonSet) {
			KeyEvent k = new KeyEvent(c,KeyEvent.KEY_RELEASED,0,0,button.getKey(),KeyEvent.CHAR_UNDEFINED);
			keyReleased(k);
		}
		for (Toggle t : toggleSet) {
			for (int i : t.getKeys()) {
				KeyEvent k = new KeyEvent(c,KeyEvent.KEY_RELEASED,0,0,i,KeyEvent.CHAR_UNDEFINED);
				keyReleased(k);
			}
		}
	}
}

class Button {
	private String name;
	private int key;
	private Component button;
	public Button(String name) {
		this.name = name;
	}
	public String getName() {
		return name;
	}
	public void setButton(Component button) {
		this.button = button;
	}
	public void setKey(int key) {
		this.key = key;
	}
	public Component getButton() {
		return button;
	}
	public int getKey() {
		return key;
	}
	
}

class Toggle {
	private String name;
	private int[] keys;
	private boolean[] values = {false,false,false,false};
	private Component[] toggles;
	public Toggle(String name) {
		this.name = name;
	}
	public String getName() {
		return name;
	}
	public void setToggles(Component[] toggles) {
		this.toggles = toggles;
	}
	public void setKeys(int[] keys) {
		this.keys = keys;
	}
	public Component[] getToggles() {
		return toggles;
	}
	public int[] getKeys() {
		return keys;
	}
	public boolean[] getValues() {
		return values;
	}
	public void setValues(boolean[] values) {
		this.values = values;
	}
	
}