import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.awt.TexturePaint;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.Timer;

import util.GameObject;


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
 
 * Credits: Kelly Charles (2020)
 */ 
public class Viewer extends JPanel {
	private long currentAnimationTime= 0; 
	private Model gameWorld; 
	 
	public Viewer(Model world) {
		this.gameWorld = world;
	}

	public void updateview() {
		repaint();
	}
	
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		currentAnimationTime++;

		//Draw player Game Object 
		int x = (int)gameWorld.getPlayer().getCentre().getX();
		int y = (int)gameWorld.getPlayer().getCentre().getY();
		int width = (int)gameWorld.getPlayer().getWidth();
		int height = (int)gameWorld.getPlayer().getHeight();
		String texture = gameWorld.getPlayer().getTexture();
		
		drawBackground(g);
		
		drawPlayer(x, y, width, height, texture,g);
		  
		gameWorld.getBullets().forEach((bullet) -> { 
			drawBullet((int)bullet.getCentre().getX(), (int)bullet.getCentre().getY(), (int)bullet.getWidth(), (int)bullet.getHeight(), bullet.getTexture(), g);	 
		}); 
		
		gameWorld.getEnemies().forEach((enemy) -> {
			drawEnemies((int)enemy.getCentre().getX(), (int)enemy.getCentre().getY(), (int)enemy.getWidth(), (int)enemy.getHeight(), enemy.getTexture(), g);	 
	    }); 
	}
	
	private void drawEnemies(int x, int y, int width, int height, String texture, Graphics g) {
		File TextureToLoad = new File(texture);

		try {
			Image myImage = ImageIO.read(TextureToLoad);
			//The sprite is 32x32 pixel wide and 4 of them are placed together so we need to grab a different one each time 
			//remember your training :-) computer science everything starts at 0 so 32 pixels gets us to 31  
			int currentPositionInAnimation= ((int)(currentAnimationTime % 4) * 32);
			g.drawImage(myImage, x, y, x+width, y+height, currentPositionInAnimation , 0, currentPositionInAnimation+31, 32, null); 
		} catch (IOException e) {
			e.printStackTrace();
		} 
		
	}

	private void drawBackground(Graphics g) {
		File TextureToLoad = new File("res/spacebackground.png");

		try {
			Image myImage = ImageIO.read(TextureToLoad); 
			g.drawImage(myImage, 0, 0, 1000, 1000, 0 , 0, 1000, 1000, null); 
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void drawBullet(int x, int y, int width, int height, String texture,Graphics g) {
		File TextureToLoad = new File(texture);

		try {
			Image myImage = ImageIO.read(TextureToLoad); 
			//64 by 128 
			g.drawImage(myImage, x,y, x+width, y+height, 0 , 0, 63, 127, null); 
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void drawPlayer(int x, int y, int width, int height, String texture,Graphics g) { 
		File TextureToLoad = new File(texture);
		
		try {
			Image myImage = ImageIO.read(TextureToLoad);
			int currentPositionInAnimation= ((int)((currentAnimationTime%40)/10))*32; //slows down animation so every 10 frames we get another frame so every 100ms 
			g.drawImage(myImage, x, y, x+width, y+height, currentPositionInAnimation  , 0, currentPositionInAnimation+31, 32, null); 
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
}