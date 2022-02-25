package main;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.awt.Dimension;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import java.awt.event.WindowEvent;

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
	private static Model gameworld = new Model();
	private static Viewer canvas = new Viewer(gameworld);
	private static Menu menu;
	private final static int targetFPS = 15;
	private final static int W = Toolkit.getDefaultToolkit().getScreenSize().width;
	private final static int H = Toolkit.getDefaultToolkit().getScreenSize().height;
	private static int averageFPS = targetFPS;
	private static boolean gameStarted = false;
	private static AudioManager audioManager;
	  
	public MainWindow() {
	    frame.setPreferredSize(new Dimension( W, H));
	    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
	    frame.setLayout(null);
	    frame.add(canvas);  
	    canvas.setBounds(0, 0, W, H); 
		System.out.println("W: " + W + " H: " + H);
		canvas.setBackground(new Color(255,255,255));
		canvas.setVisible(true);   
		audioManager = new AudioManager();
		beginGame();
		frame.pack();
		frame.setVisible(true);  
		openMainMenu();    
	}

	public static void main(String[] args) {
		new MainWindow();  
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
		if(gameStarted)
			gameworld.gamelogic();
		canvas.updateview();  
		if(menu != null) {
			menu.update();
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

	public static void newGame() {
		closeMenu();
		menu = null;
		canvas.setGoingToPlayer(true);
	}

	public static boolean eligibleToOpenMenu(Class<?> newMenu) {
		if(menu==null) return true;
		if(!menu.isVisible()) return true;
		return (!menu.getClass().equals(newMenu));
	}

	public static void closeMenu() {
		if(menu!=null) menu.dispatchEvent(new WindowEvent(menu, WindowEvent.WINDOW_CLOSING));
		System.gc();
	}

	public static void ready() {
		canvas.setInCameraMode(false);
		canvas.setGoingToPlayer(false);
		gameStarted = true;
		gameworld.setStage(Model.STAGE.MIDGAME);
		PlayerController pc = (PlayerController)gameworld.getPlayer().getController();
		canvas.addKeyListener(pc); 
		canvas.addMouseListener(pc);  
		canvas.addMouseMotionListener(pc);
		canvas.addMouseWheelListener(pc);
		canvas.requestFocusInWindow(); 
		
		// int[] entry = gameworld.getDungeon().getEntries().get(0);
		// gameworld.getDungeon().setCurrentLayer(0);
		// int[] c = gameworld.getDungeon().dungeonSpaceToWorldSpace(entry);
		// System.out.println(c[0] + " " + c[1]);
		// int x = c[0] + 8;
		// int y = c[1] + 10;

		// float dx = x - gameworld.getPlayer().getCentre().getX();
		// float dy = y - gameworld.getPlayer().getCentre().getY();
		// gameworld.getPlayer().move(new Vector3f(dx,dy,0));
		
	}

	public static Model getModel() {
		return gameworld;
	}

	public static AudioManager getAudioManager() {
		return audioManager;
	}
}