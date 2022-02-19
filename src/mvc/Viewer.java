package mvc;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Font;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import main.MainWindow;

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
	private Model gameWorld; 
	private ArrayList<Chunk> chunksLoaded = new ArrayList<Chunk>();
	private ArrayList<Entity> entitiesLoaded = new ArrayList<Entity>();
	private Map map;
	private int chunksOnScreen = 0;
	private static final int CHUNK_SIZE = 16;
	private static final int TILE_SIZE_DEF = 16;
	private static final int SCALE = 3;
	private static final int UNIT_DEF = SCALE * TILE_SIZE_DEF;
	 
	public Viewer(Model world) {
		this.gameWorld = world;
		this.map = gameWorld.getMap();
	}

	public void updateview() {
		repaint();
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		synchronized(gameWorld) {
			chunksOnScreen = 0;
			chunksLoaded = map.findClosestChunks(gameWorld.getPlayer().getCentre());
			entitiesLoaded.clear();
			gameWorld.getEntities().forEach(e -> {
				if(isEntityOnscreen(e)) {
					entitiesLoaded.add(e);
				}
			});
			gameWorld.sortEntities(entitiesLoaded);
			g.setColor(Color.BLACK);
			g.fillRect(0, 0, MainWindow.getW(), MainWindow.getH());

			drawBackground(g);
			
			drawEntities(g);

			drawForeground(g);

			drawProjectiles(g);

			drawHealthBars(g);

			drawSpells(g);

			drawCollisionsNearby(g);

			drawChunkLines(g);

			drawText(g);
		}
	}

	private void drawBackground(Graphics g) {
		ArrayList<Chunk> chunks = new ArrayList<Chunk>();
		int indexOfSprites = map.indexOfLayer("sprites");
		for(Chunk c : chunksLoaded) {
			if(map.indexOfLayer(c.getLayer().getAttribute("name")) < indexOfSprites) {
				chunks.add(c);
			}
		}
		drawChunks(g, chunks);
	}
	private void drawForeground(Graphics g) {
		ArrayList<Chunk> chunks = new ArrayList<Chunk>();
		int indexOfSprites = map.indexOfLayer("sprites");
		for(Chunk c : chunksLoaded) {
			if(map.indexOfLayer(c.getLayer().getAttribute("name")) >= indexOfSprites) {
				chunks.add(c);
			}
		}
		drawChunks(g, chunks);
	}

	private void drawChunks(Graphics g, ArrayList<Chunk> chunks) {
		try {
			for (Chunk chunk : chunks) {
				Point3f worldPoint = new Point3f(Float.parseFloat(chunk.getData().getAttribute("x")), Float.parseFloat(chunk.getData().getAttribute("y")),0f);
				Point3f relativePoint = worldSpaceToScreen(worldPoint);
				int x = (int)relativePoint.getX();
				int y = (int)relativePoint.getY();
				//System.out.println("x: " + x+ " y: " + y + " " + chunk);
				if (chunk.getLayer().getAttribute("name").equals("collisions")) continue;
				if(x + UNIT_DEF * CHUNK_SIZE <= 0 || y + UNIT_DEF * CHUNK_SIZE <= 0 || x > MainWindow.getW() || y > MainWindow.getH()) {
					continue;
				} else {
					chunksOnScreen++;
				}
				int xoff = chunk.getXOffset() * SCALE;
				int yoff = chunk.getYOffset() * SCALE;
				for(int i=0;i<16;i++) {
					for(int j=0;j<16;j++) {
						//System.out.println("i=" + i + " j=" + j + " layer" + chunk.getLayer().getAttribute("name"));
						int id = chunk.getTile(i, j);
						//System.out.println("tile: " + id);
						Tileset t = map.findTilesetByTileID(id);
						if (t == null) continue;
						int[] coords = t.getTile(id);
						//System.out.println((x + xoff)+ " " + (y + yoff)+ " "+ (x + xoff + j*16*2)+ " "+ (y + yoff + i*16*2)+ " "+ coords[0]+ " " + coords[1]+ " " + coords[2]+ " " +coords[3]);
						int tileSize = t.getTileWidth();
						int tileUnit = tileSize * SCALE;
						int dx1 = x + xoff + j * UNIT_DEF;
						int dy1 = y + yoff + i * UNIT_DEF;
						Image img = t.getImage();
						g.drawImage(img, dx1, dy1, dx1 + tileUnit, dy1 + tileUnit, coords[0], coords[1], coords[2], coords[3], null); 
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
		int x = MainWindow.getW()/2;
		int y = MainWindow.getH()/2 - (8*SCALE);
		int w = Math.round(p.getWidth()) * UNIT_DEF;
		int h = Math.round(p.getHeight()) * UNIT_DEF;
		try {
			int[] source = p.getSource();
			Image myImage = ImageIO.read(TextureToLoad);
			g.drawImage(myImage, x - w/2, y - h/2, x - w/2 + source[0]*SCALE, y - h/2 + source[1]*SCALE, source[2],source[3],source[4],source[5], null); 
		} catch (IOException e) {
			e.printStackTrace();
		} 
		Hitbox hb = p.getHitbox();
		g.setColor(new Color(1f,0f,0f,0.5f));
		//System.out.print(hb);
		x = MainWindow.getW()/2;
		y = MainWindow.getH()/2;
		w = Math.round((hb.getRightX() - hb.getLeftX()) * UNIT_DEF);
		h = Math.round((hb.getBotY() - hb.getTopY()) * UNIT_DEF);
		g.fillRect(x - w/2, y - h/2, w, h);
	}

	public void drawEntities(Graphics g) {
		for (Entity e : entitiesLoaded) {
			if (e instanceof Player) {
				drawPlayer(g);
				continue;
			}
			File TextureToLoad = new File(e.getCurrentTexture());
			Point3f worldPoint = e.getCentre();
			Point3f relativePoint = worldSpaceToScreen(worldPoint);
			Hitbox hb = e.getHitbox();
			int x = (int)relativePoint.getX();
			int y = (int)relativePoint.getY() - (8*SCALE);
			int w = Math.round(e.getWidth()) * UNIT_DEF;
			int h = Math.round(e.getHeight()) * UNIT_DEF;
			try {
				int[] source = e.getSource();
				Image myImage = ImageIO.read(TextureToLoad);
				g.drawImage(myImage, x - w/2, y - h/2, x - w/2 + source[0]*SCALE, y - h/2 + source[1]*SCALE, source[2],source[3],source[4],source[5], null); 
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			x = (int)relativePoint.getX();
			y = (int)relativePoint.getY();
			w = Math.round((hb.getRightX() - hb.getLeftX()) * UNIT_DEF);
			h = Math.round((hb.getBotY() - hb.getTopY()) * UNIT_DEF);
			g.setColor(new Color(1f,0f,0f,0.5f));
			g.fillRect(x - w/2, y - h/2, w, h);
		}
	}

	public void drawProjectiles(Graphics g) {
		for (Projectile p : gameWorld.getProjectiles()) {
			Point3f worldPoint = p.getCentre();
			Point3f relativePoint = worldSpaceToScreen(worldPoint);
			Hitbox hb = p.getHitbox();
			int x = (int)relativePoint.getX();
			int y = (int)relativePoint.getY();
			int w = Math.round(p.getWidth() * UNIT_DEF);
			int h = Math.round(p.getHeight() * UNIT_DEF);
			g.setColor(p.getColor());
			g.fillOval(x - w/2, y - h/2, w, h);
			w = Math.round((hb.getRightX() - hb.getLeftX()) * UNIT_DEF);
			h = Math.round((hb.getBotY() - hb.getTopY()) * UNIT_DEF);
			g.setColor(new Color(1f,0f,0f,0.5f));
			g.fillRect(x - w/2, y - h/2, w, h);
		}
	}

	public void drawChunkLines(Graphics g) {
		for (Chunk chunk : chunksLoaded) {
			Point3f worldPoint = new Point3f(Float.parseFloat(chunk.getData().getAttribute("x")), Float.parseFloat(chunk.getData().getAttribute("y")),0f);
			Point3f relativePoint = worldSpaceToScreen(worldPoint);
			int x = (int)relativePoint.getX();
			int y = (int)relativePoint.getY();
			g.setColor(new Color(1f,1f,1f,1f));
			g.drawRect(x, y, CHUNK_SIZE * UNIT_DEF, CHUNK_SIZE * UNIT_DEF);
			char[] chunkInfo = ("x=" + chunk.getData().getAttribute("x") + " y=" + chunk.getData().getAttribute("y")).toCharArray();
			g.setFont(new Font("Debug", Font.BOLD, 15));
			g.drawChars(chunkInfo, 0, chunkInfo.length, x+ 15, y+15);
		}
	}

	public void drawText(Graphics g) {
		char[] chunksInfo = ("Chunks on Screen: " + String.valueOf(chunksOnScreen)).toCharArray();
		char[] averageFPS = ("Max Frames Per Second: " + String.valueOf(MainWindow.getAverageFPS())).toCharArray();
		char[]  playerInfo = ("Player Coordinates: (" + String.valueOf(gameWorld.getPlayer().getCentre().getX()) + "," + String.valueOf(gameWorld.getPlayer().getCentre().getY()) + ")").toCharArray();
		g.setFont(new Font("Debug", Font.BOLD, 15));
		g.setColor(new Color(1f,0f,1f,1f));
		g.drawChars(chunksInfo, 0, chunksInfo.length, 10, 15);
		g.drawChars(averageFPS, 0, averageFPS.length, 10, 30);
		g.drawChars(playerInfo, 0, playerInfo.length, 10, 45);
	}

	public void drawCollisionsNearby(Graphics g) {
		int SCAN_RANGE = Model.getScanRange();
		int[][] collisions = map.findCollisionTilesNearbyAPoint(gameWorld.getPlayer().getCentre(), SCAN_RANGE);
		int[] tile = map.findTile(gameWorld.getPlayer().getCentre());
        int px = tile[0];
        int py = tile[1];
		for (int i=0; i<collisions.length;i++) {
			for (int j=0; j<collisions[i].length;j++) {
				Point3f worldPoint = new Point3f(px - SCAN_RANGE + j, py - SCAN_RANGE + i, 0);
				Point3f relativePoint = worldSpaceToScreen(worldPoint);
				//Hitbox hb = new Hitbox(new Point3f(worldPoint.getX() + .5f,worldPoint.getY() + .5f,0),1,1);
				int x = (int)relativePoint.getX();
				int y = (int)relativePoint.getY();
				int w = UNIT_DEF;
				int h = UNIT_DEF;
				//System.out.printf("x: %d y: %d w: %d h: %d\n",x,y,w,h);
				switch (collisions[i][j]) {
					case 8224:
						g.setColor(new Color(1f,0f,0f,0.5f));
						break;
					case 300:
						g.setColor(new Color(0f,1f,0f,0.5f));
						break;
					case 0:
						g.setColor(new Color(0f,0f,0f,0.0f));
						break;
					default:
						g.setColor(new Color(0f,0f,1f,0.5f));
						break;
				}
				g.fillRect(x,y,w,h);
			}
		}
	}

	public void drawHealthBars(Graphics g) {
		for (Entity e : entitiesLoaded) {
			if(!e.healthBarVisible() && !(e instanceof Player)) continue; 
			if(e.getHealth() > e.getMaxHealth() * 0.66f) {
				g.setColor(Color.GREEN);
			} else if(e.getHealth() > e.getMaxHealth() * 0.33f) {
				g.setColor(Color.YELLOW);
			} else {
				g.setColor(Color.RED);
			}
			Point3f relativePoint = worldSpaceToScreen(e.getCentre());
			int x = (int)relativePoint.getX();
			int y = (int)relativePoint.getY();
			g.fillRect(x - UNIT_DEF / 2, y-UNIT_DEF, (int)(e.getHealth() * UNIT_DEF / e.getMaxHealth()), 5);
			if(e instanceof Player) {
				g.setColor(Color.BLUE);
				g.fillRect(x - UNIT_DEF / 2, y-UNIT_DEF-6, (int)(e.getMana() * UNIT_DEF / e.getMaxMana()), 5);
			}
		}
	}

	public void drawSpells(Graphics g) {
		Player p = gameWorld.getPlayer();
		int x = (MainWindow.getW() / 2) - (int)(p.getSpells().size() / 2f * UNIT_DEF);
		int y = MainWindow.getH() - UNIT_DEF*2;
		for (Spell s : p.getSpells()) {
			
			if(s.equals(p.getCurrentSpell())) {
				g.setColor(Color.white);
				g.fillRect(x, y, UNIT_DEF, UNIT_DEF);
			}
			g.setColor(Projectile.getColor(s.getType()));
			g.fillOval(x, y, UNIT_DEF, UNIT_DEF);
			//draw
			x += UNIT_DEF;
		}
	}

	public boolean isEntityOnscreen(Entity e) {
		Point3f relativePoint = worldSpaceToScreen(e.getCentre());
		int x = (int)relativePoint.getX();
		int y = (int)relativePoint.getY();
		return !(x + UNIT_DEF <= 0 || y + UNIT_DEF <= 0 || x > MainWindow.getW() || y > MainWindow.getH());
	}

	public Point3f worldSpaceToScreen(Point3f p) {
		return worldSpaceToScreen(p, gameWorld.getPlayer().getCentre());
	}

	
	public Point3f screenToWorldSpace(Point3f p) {
		return screenToWorldSpace(p, gameWorld.getPlayer().getCentre());
	}

	public static Point3f worldSpaceToScreen(Point3f p, Point3f playerCentre) {
		float x = (UNIT_DEF * (p.getX() - playerCentre.getX())) + MainWindow.getW()/2;
		float y = (UNIT_DEF * (p.getY() - playerCentre.getY())) + MainWindow.getH()/2;
		return new Point3f(x, y, 0f);
	}
	public static Point3f screenToWorldSpace(Point3f p, Point3f playerCentre) {
		float x = (p.getX() - MainWindow.getW()/2) / UNIT_DEF + playerCentre.getX();
		float y = (p.getY() - MainWindow.getH()/2) / UNIT_DEF + playerCentre.getY();
		return new Point3f(x, y, 0f);
	}
}