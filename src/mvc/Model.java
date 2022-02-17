package mvc;
import java.io.File;
import java.util.ArrayList;
import java.util.Random;

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
	private ArrayList<Entity> entities = new ArrayList<Entity>();

	public Model() {
		//World
		map = new Map(new File("res/map.tmx"));
		map.loadTilesets();
		//Player 
		player = new Player(Skin.getSkins()[0], 0.5f, 0.5f, new Point3f(0,0,0),100,10);
		NPCLoader npcLoader = new NPCLoader(new File("res/npc.xml"));
		npcLoader.createAllNpcs().forEach(npc -> entities.add(npc));
		entities.add(player);
	}
	
	public void gamelogic() { 
		for (Entity e : entities) {
			if(e instanceof Player) {
				playerLogic(); 
			} else {
				entityLogic(e); 
			}
		}
	}

	private void playerLogic() {
		Controller controller = player.getController();
		if(controller.isKeySpacePressed()) {
			player.setSkin(Skin.getSkins()[(player.getSkin().getIndex()+1)%Skin.getSkins().length]);
			controller.setKeySpacePressed(false);
		}
		animationLogic(player);
		controller.setKeyAWasPressed(controller.isKeyAPressed());
		controller.setKeyDWasPressed(controller.isKeyDPressed());
		controller.setKeyWWasPressed(controller.isKeyWPressed());
		controller.setKeySWasPressed(controller.isKeySPressed());
	}

	private void entityLogic(Entity e) {
		AIController controller = (AIController)e.getController();
		controller.run(this);
		animationLogic(e);
		controller.setKeyAWasPressed(controller.isKeyAPressed());
		controller.setKeyDWasPressed(controller.isKeyDPressed());
		controller.setKeyWWasPressed(controller.isKeyWPressed());
		controller.setKeySWasPressed(controller.isKeySPressed());
	}

	public void animationLogic(Entity e) {
		float speed = e.getSpeed() / (float)MainWindow.getTargetFPS();
		AnimationPhase ap = e.getPhase();
		Controller controller = e.getController(); 
		e.incrementProgress();
		switch(ap) {
			case NEUTRAL:
			case WALKING:
				if(controller.isKeyQPressed()){
					e.setPhase(AnimationPhase.ATTACKING);
					e.setProgress(0);
					break;
				}
				if(controller.isKeyEPressed()){
					e.setPhase(AnimationPhase.CASTING);
					e.setProgress(0);
					break;
					//cast
				}
				boolean wasMovingVertical = e.getVerticalMovement();
				e.setVerticalMovement(controller.isKeyWPressed() || controller.isKeySPressed());
				if(ap==AnimationPhase.WALKING && !controller.isKeyAPressed() && !controller.isKeyDPressed() && !e.getVerticalMovement()) {
					e.setPhase(AnimationPhase.NEUTRAL);
					e.setProgress(0);
					break;
				}
				if(controller.isKeyAPressed()) {
					if(!e.setDirection(Direction.LEFT) && !e.getVerticalMovement()) {
						e.setProgress(0);
					}
					e.setPhase(AnimationPhase.WALKING);
					collisionHandler(e,new Vector3f(-speed,0,0)); 
				}
				if(controller.isKeyDPressed()) {
					if(!e.setDirection(Direction.RIGHT) && !e.getVerticalMovement()) {
						e.setProgress(0);
					}
					e.setPhase(AnimationPhase.WALKING);
					collisionHandler(e,new Vector3f(speed,0,0));
				}
				if(controller.isKeyWPressed()) {
					if(!e.setDirection(Direction.UP) && !wasMovingVertical) {
						e.setProgress(0);
					}
					e.setPhase(AnimationPhase.WALKING);
					collisionHandler(e,new Vector3f(0,-speed,0));
				}
				if(controller.isKeySPressed()){
					if(!e.setDirection(Direction.DOWN) && !wasMovingVertical) {
						e.setProgress(0);
					}
					e.setPhase(AnimationPhase.WALKING);
					collisionHandler(e,new Vector3f(0,speed,0));
				}
				break;
            case ATTACKING:
				if(e.getProgress()==3) {
					Hitbox punch = getPunchHitbox(e);
					for (Entity other : entities) {
						if(other == e) {
							continue;
						} else if (punchCollisionHandler(punch, other.getHitbox())){
							System.out.println(e.getClass().getName() + " punched " + other.getClass().getName());
							if(!other.isHostile()) other.setHostile(true);
							other.dealDamage((new Random()).nextInt(e.getDamage()));
						}
					}
				}
				break;
            case CASTING:
                // TODO cast the spell at the climax
				break;
		}
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
				if(other instanceof NPC && entity instanceof Player) {
					NPC npc = (NPC)other;
					Player player = (Player)entity;
					if (v2 != null) {
						if(player.getController().isKeyIPressed()) {
							MainWindow.initiateConversation(player, npc);
							System.out.println("Starting a coversation between player and " + npc.getName());
							player.getController().setKeyIPressed(false);
						}
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

	public boolean punchCollisionHandler(Hitbox punch, Hitbox victim) {
		return punch.isColliding(victim);
	}

	public Hitbox getPunchHitbox(Entity assaliant) {
		Vector3f v;
		switch(assaliant.getDirection()) {
			case LEFT: v = new Vector3f(-0.5f,0f,0f);break;
			case RIGHT: v = new Vector3f(0.5f,0f,0f);break;
			case UP: v = new Vector3f(0f,-0.5f,0f);break;
			case DOWN: v = new Vector3f(0f,0.5f,0f);break;
			default: throw new IllegalArgumentException("Unknown Direction");
		}
		return new Hitbox(assaliant.getCentre().plusVector(v),0.25f,0.25f);
	}

	public ArrayList<Entity> getEntities() {
		return entities;
	}

	public boolean inRangeOfPlayer(Entity entity) {
		//returns true if the entity is in attack range
		float range = 0.66f;
		float dx = entity.getCentre().getX() - player.getCentre().getX();
		float dy = entity.getCentre().getY() - player.getCentre().getY();
		return (range*range >= dx*dx + dy*dy);
	}
}
