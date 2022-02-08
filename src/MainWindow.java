import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
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
	private static Controller controller = new Controller()  ; 
	private final static int targetFPS = 15;
	private static JLabel backgroundImageForStartMenu ;
	public final static int W = Toolkit.getDefaultToolkit().getScreenSize().width;
	public final static int H = Toolkit.getDefaultToolkit().getScreenSize().height;
	private static int averageFPS = targetFPS;
	  
	public MainWindow() {
	    frame.setSize(W, H);
	    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
	    frame.setLayout(null);
	    frame.add(canvas);  
	    canvas.setBounds(0, 0, W, H); 
		System.out.println("W: " + W + " H: " + H);
		canvas.setBackground(new Color(255,255,255));
		canvas.setVisible(false);
		            
		JButton startMenuButton = new JButton("Start Game");
		startMenuButton.setBounds(400, 500, 200, 40); 
		startMenuButton.addActionListener((ActionEvent e) -> {
			startMenuButton.setVisible(false);
			backgroundImageForStartMenu.setVisible(false); 
			canvas.setVisible(true); 
			canvas.addKeyListener(controller);   
			canvas.requestFocusInWindow();   
			beginGame();
		});  
		
		File BackroundToLoad = new File("res/old/startscreen.png");
		try {	
			BufferedImage myPicture = ImageIO.read(BackroundToLoad);
			backgroundImageForStartMenu = new JLabel(new ImageIcon(myPicture));
			backgroundImageForStartMenu.setBounds(0, 0, 1000, 1000);
			frame.add(backgroundImageForStartMenu); 
		}  catch (IOException e) { 
			e.printStackTrace();
		}   

		frame.add(startMenuButton);  
		frame.setVisible(true);   
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
		gameworld.gamelogic();
		canvas.updateview();  
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
}