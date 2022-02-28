package mvc;

//Programmed by Quinn Berrman
//Student number: 20363251;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Font;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

import main.MainWindow;

import util.*;
import util.Entity.AnimationPhase;


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
	public static final Point3f CAMERA_BOUND_TL = new Point3f(-109f,-60f,0f);
	public static final Point3f CAMERA_BOUND_BR = new Point3f(14f, 145, 0f);
	private final float CAMERA_HOMING_SPEED = 64f;
	private static final int CHUNK_SIZE = 16;
	private static final int TILE_SIZE_DEF = 16;
	private static final int SCALE = 3;
	private static final int UNIT_DEF = SCALE * TILE_SIZE_DEF;

	private Vector3f cameraVector;
	private Point3f cameraOffset;
	private boolean inCameraMode = true;
	private ReadyListener cameraListener;
	private Point3f goingToPoint = null;
	private boolean inDebugMode = false;
	private Model gameWorld; 
	private ArrayList<Chunk> chunksLoaded = new ArrayList<Chunk>();
	private ArrayList<Entity> entitiesLoaded = new ArrayList<Entity>();
	private Map map;
	private int chunksOnScreen = 0;
	private ArrayList<File> files = new ArrayList<File>();
	private ArrayList<Image> images = new ArrayList<Image>();

	public Viewer() {
		this.gameWorld = MainWindow.getModel();
		this.map = MainWindow.getMap();
		cameraVector = new Vector3f(2f,2f,0f);
		cameraOffset = Point3f.generateRandomPoint(CAMERA_BOUND_TL, CAMERA_BOUND_BR);
		String[] fileNames = {"enemies/boss.png","enemies/enemies.png","enemies/witch1.png","enemies/witch2.png","attacking_transparent.png","casting_transparent.png","faces_transparent.png","walking_transparent.png"};
		for(String s : fileNames) {
			File f = new File("res/sprites/" + s);
			files.add(f);
			try {
				images.add(ImageIO.read(f));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public String flipSlashes(String s) {
		return s.replaceAll("\\\\", "/");
	}

	public Image imageByName(String s) {
		for(int i=0;i<files.size();i++) {
			if(flipSlashes(files.get(i).getPath()).equals(s)) {
				return images.get(i);
			}
		}
		return null;
	}
	public void updateview() {
		repaint();
	}

	public Model getGameWorld() {
		return gameWorld;
	}

	public void setGameWorld(Model gameWorld) {
		this.gameWorld = gameWorld;
	}

	public void moveCamera() {
		if(goingToPoint == null) {
			Vector3f realSpeed = cameraVector.byScalar(1f / (float)MainWindow.getTargetFPS());
			realSpeed = cameraOffset.bounce(realSpeed, CAMERA_BOUND_TL, CAMERA_BOUND_BR);
			cameraOffset.applyVector(realSpeed);
			cameraVector = realSpeed.byScalar((float)MainWindow.getTargetFPS());
		} else {
			Vector3f v = goingToPoint.minusPoint(cameraOffset);
			float vx = v.getX();
			float vy = v.getY();
			if(Math.round(vx)==0 && Math.round(vy) == 0) {
				cameraListener.ready();
				return;
			}
			cameraVector = v.relativeMax(CAMERA_HOMING_SPEED);
			cameraOffset.applyVector(cameraVector.byScalar(1f / (float)MainWindow.getTargetFPS()));
		}
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		synchronized(gameWorld) {
			if(inCameraMode) 
				moveCamera();

			chunksOnScreen = 0;
			chunksLoaded = map.findClosestChunks(inCameraMode ? cameraOffset : gameWorld.getPlayer().getCentre());
			entitiesLoaded = gameWorld.getEntitiesLoaded();
			gameWorld.sortEntities(entitiesLoaded);

			g.setColor(Color.BLACK);
			g.fillRect(0, 0, MainWindow.getW(), MainWindow.getH());

			drawBackground(g);

			drawEntities(g);

			if(!inCameraMode) {
				drawProjectiles(g);
			}

			drawForeground(g);

			if(!inCameraMode) {
				drawHealthBars(g);

				drawSpells(g);
			}
			if(inDebugMode && !inCameraMode) {
				drawCollisionsNearby(g);

				drawChunkLines(g);

				drawText(g);
			}
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
				int[] chunkCoords = chunk.getTrueCoords();
				Point3f worldPoint = new Point3f(chunkCoords[0],chunkCoords[1],0f);
				Point3f relativePoint = worldSpaceToScreen(worldPoint);
				int x = (int)relativePoint.getX();
				int y = (int)relativePoint.getY();
				if (chunk.getLayer().getAttribute("name").equals("collisions")) continue;
				if (chunk.getLayer().getAttribute("name").equals("audio")) continue;
				if(x + UNIT_DEF * CHUNK_SIZE <= 0 || y + UNIT_DEF * CHUNK_SIZE <= 0 || x > MainWindow.getW() || y > MainWindow.getH()) {
					continue;
				} else {
					chunksOnScreen++;
				}
				int xoff = chunk.getXOffset() * SCALE;
				int yoff = chunk.getYOffset() * SCALE;
				for(int i=0;i<16;i++) {
					int dy1 = y + yoff + i * UNIT_DEF;
					for(int j=0;j<16;j++) {
						int id = chunk.getTile(i, j);
						Tileset t = map.findTilesetByTileID(id);
						if (t == null) continue;
						int[] coords = t.getTile(id);
						int tileSize = t.getTileWidth();
						int tileUnit = tileSize * SCALE;
						int dx1 = x + xoff + j * UNIT_DEF;
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
		Image image = imageByName(p.getCurrentTexture());
		Point3f relativePoint = worldSpaceToScreen(p.getCentre());
		int x;
		int y;
		int w;
		int h;
		try {
			if(p.getPhase().equals(AnimationPhase.CASTING) && p.getProgress() <= 6) {
				int dir = p.getCurrentSpell().numberOfDirections();
				int prog = p.getProgress() > 4 ? 4 : p.getProgress();
				String file = "res/effects/glow" + dir + ".png";
				Image spellImage = ImageIO.read(new File(file));
				x = (int)relativePoint.getX();
				y = (int)relativePoint.getY();
				w = Math.round(0.5f * (prog+1) * UNIT_DEF);
				h = w;
				g.drawImage(spellImage, x - w/2, y - h/2, w, h, null); 
			}
			x = (int)relativePoint.getX();
			y = (int)relativePoint.getY() - (8*SCALE);
			w = Math.round(p.getWidth()) * UNIT_DEF;
			h = Math.round(p.getHeight()) * UNIT_DEF;
			int[] source = p.getSource();
			g.drawImage(image, x - w/2, y - h/2, x - w/2 + source[0]*SCALE, y - h/2 + source[1]*SCALE, source[2],source[3],source[4],source[5], null); 
		} catch (IOException e) {
			e.printStackTrace();
		} 
		if(inDebugMode) {
			Hitbox hb = p.getHitbox();
			g.setColor(new Color(1f,0f,0f,0.5f));
			//System.out.print(hb);
			x = (int)relativePoint.getX();
			y = (int)relativePoint.getY();
			w = Math.round((hb.getRightX() - hb.getLeftX()) * UNIT_DEF);
			h = Math.round((hb.getBotY() - hb.getTopY()) * UNIT_DEF);
			g.fillRect(x - w/2, y - h/2, w, h);
			g.setColor(Color.CYAN);
			g.fillOval(x, y, 6, 6);
		}
	}

	public void drawEntities(Graphics g) {
		for (Entity e : entitiesLoaded) {
			if (e instanceof Player) {
				drawPlayer(g);
				continue;
			}
			Image image = imageByName(e.getCurrentTexture());
			Point3f worldPoint = e.getCentre();
			Point3f relativePoint = worldSpaceToScreen(worldPoint);
			Hitbox hb = e.getHitbox();
			int x = (int)relativePoint.getX();
			int y = (int)relativePoint.getY() - (8*SCALE);
			int w = Math.round(e.getWidth()) * UNIT_DEF;
			int h = Math.round(e.getHeight()) * UNIT_DEF;
			try {
				int[] source = e.getSource();
				g.drawImage(image, x - w/2, y - h/2, x - w/2 + source[0]*SCALE, y - h/2 + source[1]*SCALE, source[2],source[3],source[4],source[5], null); 
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			if(inDebugMode) {
				x = (int)relativePoint.getX();
				y = (int)relativePoint.getY();
				w = Math.round((hb.getRightX() - hb.getLeftX()) * UNIT_DEF);
				h = Math.round((hb.getBotY() - hb.getTopY()) * UNIT_DEF);
				g.setColor(new Color(1f,0f,0f,0.5f));
				g.fillRect(x - w/2, y - h/2, w, h);
				g.setColor(Color.CYAN);
				g.fillOval(x, y, 6, 6);
			}
			
		}
	}

	public void drawProjectiles(Graphics g) {
		try {
			for (Projectile p : gameWorld.getProjectiles()) {
				p.refreshProgress();
				BufferedImage i = p.getSpell().getFrames().get(p.getAnimationProgress());
				BufferedImage i2 = new BufferedImage(i.getWidth(),i.getHeight(),i.getType());
				Graphics2D g2 = i2.createGraphics();
				double rad = Math.atan2(p.getVelocity().getY(), p.getVelocity().getX());
				rad += p.getType().equals(Projectile.Type.ARCANE) ? -Math.PI/2 : 0;
				g2.rotate(rad, i.getWidth()/2, i.getHeight()/2);
				g2.drawImage(i,null,0,0);
				Point3f worldPoint = p.getCentre();
				Point3f relativePoint = worldSpaceToScreen(worldPoint);
				Hitbox hb = p.getHitbox();
				int x = (int)relativePoint.getX();
				int y = (int)relativePoint.getY();
				int w = Math.round(p.getWidth() * UNIT_DEF);
				int h = Math.round(p.getHeight() * UNIT_DEF);
				g.drawImage(i2, x - w/2, y - h/2, w, h, null);
				if(inDebugMode) {
					w = Math.round((hb.getRightX() - hb.getLeftX()) * UNIT_DEF);
					h = Math.round((hb.getBotY() - hb.getTopY()) * UNIT_DEF);
					g.setColor(new Color(1f,0f,0f,0.5f));
					g.fillRect(x - w/2, y - h/2, w, h);
				}
				p.incrementProgress();
			}
		} catch (ConcurrentModificationException e) {
			e.printStackTrace();
		}
		
	}

	public void drawChunkLines(Graphics g) {
		for (Chunk chunk : chunksLoaded) {
			int[] chunkCoords = chunk.getTrueCoords();
			Point3f worldPoint = new Point3f(chunkCoords[0],chunkCoords[1],0f);
			Point3f relativePoint = worldSpaceToScreen(worldPoint);
			int x = (int)relativePoint.getX();
			int y = (int)relativePoint.getY();
			g.setColor(new Color(1f,1f,1f,1f));
			g.drawRect(x, y, CHUNK_SIZE * UNIT_DEF, CHUNK_SIZE * UNIT_DEF);
			char[] chunkInfo = ("x=" + chunkCoords[0] + " y=" + chunkCoords[1]).toCharArray();
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
		Dungeon d = gameWorld.getCurrentDungeon();
		if(d!=null) {
			char[] dungeonInfo = ("Current Dungeon: " + String.valueOf(d.getType())).toCharArray();
			char[] dungeonLevelInfo = ("Current Floor: " + String.valueOf(d.getCurrentLayer())).toCharArray();
			g.drawChars(dungeonInfo, 0, dungeonInfo.length, 10, 60);
			g.drawChars(dungeonLevelInfo, 0, dungeonLevelInfo.length, 10, 75);
		}
	}

	public void drawCollisionsNearby(Graphics g) {
		int SCAN_RANGE = Model.getScanRange();
		int[][] collisions = map.findCollisionTilesNearbyAPoint(gameWorld.getPlayer().getCentre(), SCAN_RANGE);
		int[] tile = Map.findTile(gameWorld.getPlayer().getCentre());
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
					case 1028:
						g.setColor(new Color(0f,0f,1f,0.5f));
						break;
					default:
						g.setColor(new Color(1f,0f,1f,0.5f));
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
			g.setColor(Color.BLACK);
			g.fillRect(x, y, UNIT_DEF, UNIT_DEF);
			if(s.equals(p.getCurrentSpell())) {
				g.setColor(Color.WHITE);
				g.drawRect(x, y, UNIT_DEF, UNIT_DEF);
			}
			g.drawImage(s.getFrames().get(0), x, y, UNIT_DEF, UNIT_DEF, null);
			// g.setColor(Projectile.getColor(s.getElement()));
			// g.fillOval(x, y, UNIT_DEF, UNIT_DEF);
			//draw
			x += UNIT_DEF;
		}
	}

	public boolean isObjectOnScreen(GameObject go) {
		Point3f relativePoint = worldSpaceToScreen(go.getCentre());
		int x = (int)relativePoint.getX();
		int y = (int)relativePoint.getY();
		int w = (int)go.getWidth();
		int h = (int)go.getHeight();
		return !(x + w*UNIT_DEF <= 0 || y + h*UNIT_DEF <= 0 || x > MainWindow.getW() || y > MainWindow.getH());
	}

	public Point3f worldSpaceToScreen(Point3f p) {
		return worldSpaceToScreen(p, inCameraMode ? cameraOffset : gameWorld.getPlayer().getCentre());
	}

	
	public Point3f screenToWorldSpace(Point3f p) {
		return screenToWorldSpace(p, inCameraMode ? cameraOffset : gameWorld.getPlayer().getCentre());
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

	public void setInCameraMode(boolean inCameraMode) {
		this.inCameraMode = inCameraMode;
	}

	public void setInDebugMode(boolean inDebugMode) {
		this.inDebugMode = inDebugMode;
	}

	public void setGoingToPoint(Point3f goingToPoint) {
		this.goingToPoint = goingToPoint;
	}

	public interface ReadyListener {
		void ready();
	}

	public void setListener(ReadyListener cameraListener) {
		this.cameraListener = cameraListener;
	}

	public void setCameraOffset(Point3f cameraOffset) {
		this.cameraOffset = cameraOffset;
	}

	public boolean isInCameraMode() {
		return inCameraMode;
	}
}