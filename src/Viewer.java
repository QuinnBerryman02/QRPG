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
import java.util.ArrayList;
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
	private ArrayList<Chunk> chunks;
	private int chunkX = 0;
	private int chunkY = 0;
	private MapLoader mapLoader;
	 
	public Viewer(Model world) {
		this.gameWorld = world;
		mapLoader = new MapLoader(new File("res/map.tmx"));
		chunks = mapLoader.getChunksByCoordinate(chunkX, chunkY);
		mapLoader.loadTilesets();
	}

	public void updateview() {
		if(gameWorld.getChunkX() != chunkX || gameWorld.getChunkY() != chunkY) {
			chunkX = gameWorld.getChunkX();
			chunkY = gameWorld.getChunkY();
			chunks = mapLoader.getChunksByCoordinate(chunkX, chunkY);
		}
		repaint();
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		//currentAnimationTime++;

		drawBackground(g);
		
		drawPlayer(g);
	}

	private void drawBackground(Graphics g) {
		int x = 100, y = 100;
		int tileMulti = 2;
		//chunks.forEach((c) -> System.out.println(c));
		try {
			for (Chunk chunk : chunks) {
				if (chunk.getLayer().getAttribute("name").equals("collisions")) continue;
				int xoff = chunk.getXOffset() * 2;
				int yoff = chunk.getYOffset() * 2;
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
						int dx1 = x + xoff + j*16*tileMulti;
						int dy1 = y + yoff + i*16*tileMulti;
						g.drawImage(t.getImage(), dx1, dy1, dx1 + tileUnit, dy1 + tileUnit, coords[0],coords[1],coords[2],coords[3], null); 
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
			int x = (int)p.getCentre().getX();
			int y = (int)p.getCentre().getY();
			int[] source = p.getSource();
			Image myImage = ImageIO.read(TextureToLoad);
			g.drawImage(myImage, x, y, x+source[0]*spriteScale, y+source[1]*spriteScale, source[2],source[3],source[4],source[5], null); 
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
}