package main;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.awt.Dimension;
import java.awt.Point;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.awt.Cursor;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import java.awt.event.WindowEvent;

import mvc.ControllerMenu;
import mvc.Model;
import mvc.PlayerController;
import mvc.Viewer;
import util.*;

import java.awt.Toolkit;

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



public class MainWindow {
	private static JFrame frame = new JFrame("Quinn's Game");   // Change to the name of your game 
	private static Model gameworld;
	private static Viewer canvas;
	private static Menu menu;
	private final static int targetFPS = 15;
	private final static int W = Toolkit.getDefaultToolkit().getScreenSize().width;
	private final static int H = Toolkit.getDefaultToolkit().getScreenSize().height;
	private static int averageFPS = targetFPS;
	private static AudioManager audioManager;
	private static Map map;
	private static Cursor invisible;
	private static Cursor visible;
	  
	public MainWindow() {
		map = new Map(new File("res/map.tmx"));
		map.loadTilesets();
		gameworld = new Model();
		canvas = new Viewer();
	    frame.setPreferredSize(new Dimension( W, H));
	    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
	    frame.setLayout(null);
	    frame.add(canvas);  
	    canvas.setBounds(0, 0, W, H); 
		System.out.println("W: " + W + " H: " + H);
		canvas.setBackground(new Color(255,255,255));
		canvas.setVisible(true);   
		audioManager = new AudioManager();
		gameworld.initialiseNewModel();
		beginGame();
		frame.pack();
		frame.setVisible(true); 
		BufferedImage noImage = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
		invisible = Toolkit.getDefaultToolkit().createCustomCursor(noImage, new Point(), "invisible cursor");
		try {
			BufferedImage image = ImageIO.read(new File("res/pointer.png"));
			visible = Toolkit.getDefaultToolkit().createCustomCursor(image, new Point(), "visible cursor");
		} catch (IOException e) {} 
		openMainMenu();    
	}

	public static void main(String[] args) {
		new MainWindow();  
	} 

	public static void establishListeners() {
		PlayerController pc = (PlayerController)gameworld.getPlayer().getController();
		canvas.addKeyListener(pc); 
		canvas.addMouseListener(pc);  
		canvas.addMouseMotionListener(pc);
		canvas.addMouseWheelListener(pc);
		canvas.requestFocusInWindow(); 
	}

	private static void beginGame() {
		Thread thread = new Thread() {
			private boolean running = false;
			@Override
			public synchronized void start() {
				running = true;
				super.start();
			}
			@Override
			public void run() {
				int timeBetweenFrames =  1000 / targetFPS;
				while (running) {
					try {
						long startTime = System.currentTimeMillis(); 
						gameloop();
						long finishTime = System.currentTimeMillis();
						long timeTaken = finishTime - startTime;
						long sleepTime = timeBetweenFrames - timeTaken;
						if(timeTaken==0) {
						} else {
							float fractionOfTimeUsed = (float)timeBetweenFrames / (float)(timeTaken);
							int potentialFPS = (int)(fractionOfTimeUsed * targetFPS);
							averageFPS = (potentialFPS + averageFPS) / 2;
						}
						if (sleepTime >= 0) {
							sleep(sleepTime);
						} else {
							System.out.println("Frame was late by " + (sleepTime * -1) + " ms");
						}
					} catch (InterruptedException e) {
						kill();
					}
				}
			}
			public void kill() {
				running = false;
			}
		};
		thread.start();
	}

	private static void gameloop() { 
		gameworld.gamelogic();
		if(canvas.getGameWorld() != gameworld) canvas.setGameWorld(gameworld);
		canvas.updateview();  
		refreshCursor(frame);
		if(menu != null) {
			menu.update();
			refreshCursor(menu);
		}
	}

	public static void printTime(String place) {
		System.out.println("Current time is" + System.currentTimeMillis() + "@" + place);
	}

	public static int getTargetFPS() {
		return targetFPS;
	}

	public static int getAverageFPS() {
		return averageFPS;
	}

	public static int getH() {
		return H;
	}

	public static int getW() {
		return W;
	}

	public static void initiateConversation(Player p, NPC npc) {
		if(eligibleToOpenMenu(Dialogue.class)) {
			closeMenu();
			menu = new Dialogue(p, npc);
		}
	}
	public static void openSpellMenu(Player p) {
		if(eligibleToOpenMenu(SpellMenu.class)) {
			closeMenu();
			menu = new SpellMenu(p);
		}
	}

	public static void openQuestMenu(Player p) {
		if(eligibleToOpenMenu(QuestMenu.class)) {
			closeMenu();
			menu = new QuestMenu(p);
		}
	}

	public static void openMainMenu() {
		if(eligibleToOpenMenu(MainMenu.class)) {
			closeMenu();
			menu = new MainMenu();
		}
	}

	public static void openGuildMenu(Player p) {
		if(eligibleToOpenMenu(GuildMenu.class)) {
			closeMenu();
			menu = new GuildMenu(p);
		}
	}

	public static void openControllerMenu(Player p) {
		if(eligibleToOpenMenu(ControllerMenu.class)) {
			closeMenu();
			menu = new ControllerMenu(p);
		}
	}

	public static void openStatueMenu(Player p) {
		if(eligibleToOpenMenu(StatueMenu.class)) {
			closeMenu();
			menu = new StatueMenu(p);
		}
	}

	public static void newGamePrep() {//plays main theme and puts the camera to center if the player dies and selects new game
		Point3f tl = Viewer.CAMERA_BOUND_TL;
		Point3f br = Viewer.CAMERA_BOUND_BR;
		canvas.setCameraOffset(new Point3f((br.getX()+tl.getX())/2,(br.getY()+tl.getY())/2,0));
		audioManager.playSongByTileId(777); 
		canvas.setInCameraMode(true);
	}

	public static void newGame() {
		closeMenu();
		gameworld = new Model();
		gameworld.initialiseNewModel();
		canvas.setGoingToPoint(new Point3f(-66,67,0));
		canvas.setListener(() -> {
			canvas.setCameraOffset(new Point3f(81,114,0));
			canvas.setGoingToPoint(new Point3f(73,109,0));
			canvas.setListener(() -> {
				NPC john = NPCLoader.getNPCByName("John");
				john.move(new Point3f(111.5f,121.5f,0).minusPoint(john.getCentre()));
				canvas.setCameraOffset(new Point3f(105,107,0));
				canvas.setGoingToPoint(gameworld.getPlayer().getCentre());
				canvas.setListener(() -> {
					canvas.setInCameraMode(false);
					canvas.setGoingToPoint(null);
					canvas.setListener(null);
					gameworld.setStage(Model.STAGE.BEGINING);
					establishListeners();
				});
			});
		});
	}

	public static void saveGame() {
		try {
			File file = new File("saves/save.txt");
			FileOutputStream fos = new FileOutputStream(file);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			System.out.println("____________________________________________");
			oos.writeObject(gameworld);
			oos.close();
			fos.close();
			System.out.println("Game saved SuccessFully");
			
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Couldn't save properly");
		}
		System.out.println("____________________________________________");
	}

	public static void loadGame() {
		JFileChooser chooser = new JFileChooser("saves");
		chooser.setBounds(200, 200, W-400,H-400);
		chooser.setVisible(true);
		int option = chooser.showOpenDialog(frame);
		if(option==JFileChooser.APPROVE_OPTION) {    
			try {
				File file = chooser.getSelectedFile();
				FileInputStream fis = new FileInputStream(file);   
				ObjectInputStream ois = new ObjectInputStream(fis);
				System.out.println("____________________________________________");
				Model loadedGame = (Model)ois.readObject();
				gameworld = loadedGame;
				closeMenu();
				ois.close();
				fis.close();
				if(gameworld.getPlayer().isIndoors()) {
					canvas.setCameraOffset(gameworld.getPlayer().getCentre());
					canvas.setInCameraMode(false);
					canvas.setGoingToPoint(null);
					canvas.setListener(null);
					establishListeners();
				} else {
					canvas.setGoingToPoint(gameworld.getPlayer().getCentre());
					canvas.setListener(() -> {
						canvas.setInCameraMode(false);
						canvas.setGoingToPoint(null);
						canvas.setListener(null);
						establishListeners();
					});
				}
				audioManager.playLastPlayed();
				System.out.println("Game loaded SuccessFully");
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("Couldn't load save file");
			}        
			System.out.println("____________________________________________");
		}  
	}

	public static boolean eligibleToOpenMenu(Class<?> newMenu) {
		if(menu==null) return true;
		if(!menu.isVisible()) return true;
		return (!menu.getClass().equals(newMenu));
	}
	public static void closeMenu(boolean internal) {
		if(menu!=null && !internal) menu.dispatchEvent(new WindowEvent(menu, WindowEvent.WINDOW_CLOSING));
		if(menu instanceof Dialogue && gameworld.getStage().equals(Model.STAGE.BOSS_FIGHT)) {
			NPC john = NPCLoader.getNPCByName("John");
			Point3f loc = john.getCentre().plusVector(new Vector3f());
			john.move(new Vector3f(10000,0,0)); 
			audioManager.playSoundByName("death_boss"); //roar
			Enemy boss = new Enemy(Enemy.Type.BOSS, 2.9f, 2.9f, loc, 1000, 10, 10000);
			gameworld.getEntities().add(boss);
		}
		menu = null;
		System.gc();
	}
	public static void closeMenu() {
		closeMenu(false);
	}

	public static Menu getMenu() {
		return menu;
	}

	public static void setMenu(Menu menu) {
		MainWindow.menu = menu;
	}

	public static boolean inAMenu() {
		return menu!=null;
	}

	public static Model getModel() {
		return gameworld;
	}

	public static AudioManager getAudioManager() {
		return audioManager;
	}

	public static Viewer getCanvas() {
		return canvas;
	}

	public static JFrame getFrame() {
		return frame;
	}

	public static void refreshCursor(JFrame frame) {
		if(gameworld.getPlayer()==null) return;
		if(!inAMenu() && ((PlayerController)gameworld.getPlayer().getController()).isControllerMode()) {
			frame.getContentPane().setCursor(invisible);
		} else {
			frame.getContentPane().setCursor(visible);
		}
	}

	public static Map getMap() {
		return map;
	}
}