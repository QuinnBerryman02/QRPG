import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Font;
import java.awt.Image;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.awt.TexturePaint;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.Timer;

import util.*;


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
	//private long currentAnimationTime = 0; 
	private Model gameWorld; 
	private int spriteScale = 3;
	private ArrayList<Chunk> chunksLoaded = new ArrayList<Chunk>();
	private MapLoader mapLoader;
	private int chunksOnScreen = 0;
	private float playerX;
	private float playerY;
	 
	public Viewer(Model world) {
		this.gameWorld = world;
		mapLoader = new MapLoader(new File("res/map.tmx"));
		mapLoader.loadTilesets();
	}

	public void updateview() {
		chunksLoaded = mapLoader.findClosestChunks((int)gameWorld.getPlayer().getCentre().getX(), (int)gameWorld.getPlayer().getCentre().getY());
		// if(gameWorld.getChunkX() != chunkX || gameWorld.getChunkY() != chunkY) {
		// 	chunkX = gameWorld.getChunkX();
		// 	chunkY = gameWorld.getChunkY();
		// 	chunks = mapLoader.getChunksByCoordinate(chunkX, chunkY);
		// }
		repaint();
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		//currentAnimationTime++;
		playerX = gameWorld.getPlayer().getCentre().getX();
		playerY = gameWorld.getPlayer().getCentre().getY();
		chunksOnScreen = 0;
		drawBackground(g);
		
		drawPlayer(g);

		drawForeground(g);

		drawText(g);
	}

	private void drawBackground(Graphics g) {
		ArrayList<Chunk> chunks = new ArrayList<Chunk>();
		int indexOfSprites = mapLoader.indexOfLayer("sprites");
		for(Chunk c : chunksLoaded) {
			if(mapLoader.indexOfLayer(c.getLayer().getAttribute("name")) < indexOfSprites) {
				chunks.add(c);
			}
		}
		drawChunks(g, chunks);
	}
	private void drawForeground(Graphics g) {
		ArrayList<Chunk> chunks = new ArrayList<Chunk>();
		int indexOfSprites = mapLoader.indexOfLayer("sprites");
		for(Chunk c : chunksLoaded) {
			if(mapLoader.indexOfLayer(c.getLayer().getAttribute("name")) >= indexOfSprites) {
				chunks.add(c);
			}
		}
		drawChunks(g, chunks);
	}

	private void drawChunks(Graphics g, ArrayList<Chunk> chunks) {
		int chunkSize = 16;
		int tileSizeDefault = 16;
		int tileMulti = 2;
		int defaultUnit = tileMulti * tileSizeDefault;
		//chunks.forEach((c) -> System.out.println(c));
		try {
			for (Chunk chunk : chunks) {
				int x = (int)(defaultUnit * (Integer.parseInt(chunk.getData().getAttribute("x")) - playerX));
				int y = (int)(defaultUnit * (Integer.parseInt(chunk.getData().getAttribute("y")) - playerY));
				//System.out.println("x: " + x+ " y: " + y + " " + chunk);
				if(x + defaultUnit * chunkSize <= 0 || y + defaultUnit * chunkSize <= 0 || x > MainWindow.W || y > MainWindow.H) {
					continue;
				} else {
					chunksOnScreen++;
				}
				if (chunk.getLayer().getAttribute("name").equals("collisions")) continue;
				int xoff = chunk.getXOffset() * tileMulti;
				int yoff = chunk.getYOffset() * tileMulti;
				for(int i=0;i<16;i++) {
					for(int j=0;j<16;j++) {
						//System.out.println("i=" + i + " j=" + j + " layer" + chunk.getLayer().getAttribute("name"));
						int id = chunk.getTile(i, j);
						//System.out.println("tile: " + id);
						Tileset t = mapLoader.findTilesetByTileID(id);
						if (t == null) continue;
						int[] coords = t.getTile(id);
						//System.out.println((x + xoff)+ " " + (y + yoff)+ " "+ (x + xoff + j*16*2)+ " "+ (y + yoff + i*16*2)+ " "+ coords[0]+ " " + coords[1]+ " " + coords[2]+ " " +coords[3]);
						int tileSize = t.getTileWidth();
						int tileUnit = tileSize*tileMulti;
						int dx1 = x + xoff + j * defaultUnit;
						int dy1 = y + yoff + i * defaultUnit;
						g.drawImage(t.getImage(), dx1, dy1, dx1 + tileUnit, dy1 + tileUnit, coords[0], coords[1], coords[2], coords[3], null); 
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	private void drawPlayer(Graphics g) { 
		Player p = gameWorld.getPlayer();
		File TextureToLoad = new File(p.getCurrentTexture());
		try {
			int x = MainWindow.W/2;
			int y = MainWindow.H/2;
			int[] source = p.getSource();
			Image myImage = ImageIO.read(TextureToLoad);
			g.drawImage(myImage, x, y, x+source[0]*spriteScale, y+source[1]*spriteScale, source[2],source[3],source[4],source[5], null); 
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}

	public void drawText(Graphics g) {
		char[] chunksInfo = ("Chunks on Screen: " + String.valueOf(chunksOnScreen)).toCharArray();
		char[] averageFPS = ("Max Frames Per Second: " + String.valueOf(MainWindow.getAverageFPS())).toCharArray();
		char[]  playerInfo = ("Player Coordinates: (" + String.valueOf(gameWorld.getPlayer().getCentre().getX()) + "," + String.valueOf(gameWorld.getPlayer().getCentre().getY()) + ")").toCharArray();
		g.setFont(new Font("Debug", Font.BOLD, 30));
		g.setColor(new Color(1f,1f,1f,1f));
		g.drawChars(chunksInfo, 0, chunksInfo.length, 10, 30);
		g.drawChars(averageFPS, 0, averageFPS.length, 10, 60);
		g.drawChars(playerInfo, 0, playerInfo.length, 10, 90);
	}
}