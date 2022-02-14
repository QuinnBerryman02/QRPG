package mvc;
import java.io.File;
import java.util.ArrayList;

import main.MainWindow;
import util.*;
import util.Entity.AnimationPhase;
import util.Entity.Direction;
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
public class Model {
	private Player player;
	private Map map;
	private ArrayList<NPC> entities = new ArrayList<NPC>();

	public Model() {
		//World
		map = new Map(new File("res/map.tmx"));
		map.loadTilesets();
		//Player 
		player = new Player(Skin.getSkins()[0], 0.5f, 0.5f, new Point3f(0,0,0), 4, new PlayerController());
		NPCLoader npcLoader = new NPCLoader(new File("res/npc.xml"));
		entities = npcLoader.createAllNpcs();
	}
	
	public void gamelogic() { 
		playerLogic(); 
	}

	private void playerLogic() {
		float speed = player.getSpeed() / (float)MainWindow.getTargetFPS();
		player.incrementProgress();
		AnimationPhase ap = player.getPhase();
		Controller controller = player.getController();
		if(controller.isKeySpacePressed()) {
			player.setSkin(Skin.getSkins()[(player.getSkin().getIndex()+1)%Skin.getSkins().length]);
			controller.setKeySpacePressed(false);
		}
		switch(ap) {
			case NEUTRAL:
			case WALKING:
				if(controller.isKeyQPressed()){
					player.setPhase(AnimationPhase.ATTACKING);
					player.setProgress(0);
					break;
					//attack
				}
				if(controller.isKeyEPressed()){
					player.setPhase(AnimationPhase.CASTING);
					player.setProgress(0);
					break;
					//cast
				}
				boolean wasMovingVertical = player.getVerticalMovement();
				player.setVerticalMovement(controller.isKeyWPressed() || controller.isKeySPressed());
				if(ap==AnimationPhase.WALKING && !controller.isKeyAPressed() && !controller.isKeyDPressed() && !player.getVerticalMovement()) {
					player.setPhase(AnimationPhase.NEUTRAL);
					player.setProgress(0);
					break;
				}
				if(controller.isKeyAPressed()) {
					if(!player.setDirection(Direction.LEFT) && !player.getVerticalMovement()) {
						player.setProgress(0);
					}
					player.setPhase(AnimationPhase.WALKING);
					collisionHandler(player,new Vector3f(-speed,0,0)); 
				}
				if(controller.isKeyDPressed()) {
					if(!player.setDirection(Direction.RIGHT) && !player.getVerticalMovement()) {
						player.setProgress(0);
					}
					player.setPhase(AnimationPhase.WALKING);
					collisionHandler(player,new Vector3f(speed,0,0));
				}
				if(controller.isKeyWPressed()) {
					if(!player.setDirection(Direction.UP) && !wasMovingVertical) {
						player.setProgress(0);
					}
					player.setPhase(AnimationPhase.WALKING);
					collisionHandler(player,new Vector3f(0,-speed,0));
				}
				if(controller.isKeySPressed()){
					if(!player.setDirection(Direction.DOWN) && !wasMovingVertical) {
						player.setProgress(0);
					}
					player.setPhase(AnimationPhase.WALKING);
					collisionHandler(player,new Vector3f(0,speed,0));
				}
				break;
            case ATTACKING: break;
                //todo check if the player hits an entity on climax of punch
            case CASTING:	break;
                //todo cast the spell at the climax
		}
		controller.setKeyAWasPressed(controller.isKeyAPressed());
		controller.setKeyDWasPressed(controller.isKeyDPressed());
		controller.setKeyWWasPressed(controller.isKeyWPressed());
		controller.setKeySWasPressed(controller.isKeySPressed());
	}

	public Player getPlayer() {
		return player;
	}

	public Map getMap() {
		return map;
	}

	public void collisionHandler (Entity entity, Vector3f v) {
		v = wallCollisionHandler(entity, v);
		for (Entity other : entities) {
			if(other == entity) {
				continue;
			} else {
				Vector3f v2 = entityCollisionHandler(entity, other, v);
				if(other instanceof NPC) {
					NPC npc = ((NPC)other);
					if (v2 != null) {
						npc.setInteractable(true);
						if(entity instanceof Player) {
							if(entity.getController().isKeyIPressed()) {
								MainWindow.initiateConversation((Player)entity, npc);
								System.out.println("Starting a coversation between player and " + npc.getName());
								entity.getController().setKeyIPressed(false);
								npc.setInteractable(false);
							}
						}
					} else {
						npc.setInteractable(false);
					}
				}
				v = (v2 != null) ? v2 : v;
			}
		}
		entity.move(v);
	}

	public Vector3f wallCollisionHandler(Entity entity, Vector3f v) {
		int[][] collisions = map.findCollisionTilesNearbyAPoint(entity.getCentre(), 2);
		int[] tile = map.findTile(entity.getCentre());
        int px = tile[0];
        int py = tile[1];
		Hitbox hb = entity.getHitbox();
		for (int i=0; i<collisions.length;i++) {
			for (int j=0; j<collisions[i].length;j++) {
				switch (collisions[i][j]) {
					case 8224:
						Hitbox c = new Hitbox(new Point3f(px - 2 + .5f + j,py - 2 + .5f + i,0),1,1);
						Vector3f v2 = hb.intersection(c, v);
						if(v2 != null) return v2;
						break;
					case 300:
						c = new Hitbox(new Point3f(px - 2 + .5f + j,py - 2 + .5f + i,0),1,1);
						Vector3f wasIntersecting = hb.intersection(c, new Vector3f());
						v2 = hb.intersection(c, v);
						if(v2 != null) {
							if(wasIntersecting != null) {
								break;
							}
							if(entity instanceof Player) {
								if(entity.getController().isKeyUpPressed()) {
									Point3f portalPoint = new Point3f(px - 2 + j, py - 2 + i, 0f);
									String teleport = map.findTeleportTypeByPoint(portalPoint);
									Point3f destinationPoint = map.findTeleportPointByOther(teleport, portalPoint); 
									System.out.println("Teleporting from " + portalPoint + " to " + destinationPoint + " via " + teleport);
									entity.getController().setKeyUpPressed(false);
									entity.move(new Point3f(destinationPoint.getX()+0.5f,destinationPoint.getY()+0.5f,0f).minusPoint(entity.getCentre()));
									return new Vector3f();
								}
							}
							return v2;
						}
						break;
					default:
						break;
				}
				
			}
		}
		return v;
	}

	public Vector3f entityCollisionHandler(Entity e1, Entity e2, Vector3f v) {
		Hitbox hb = e1.getHitbox();
		Hitbox hb2 = e2.getHitbox();
		Vector3f v2 = hb.intersection(hb2, v);
		return (v2 != null) ? (v2) : (v);
	}

	public ArrayList<NPC> getEntities() {
		return entities;
	}
}
