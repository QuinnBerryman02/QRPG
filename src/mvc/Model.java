package mvc;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.function.Function;

import main.MainWindow;
import util.*;
import util.Entity.AnimationPhase;
import util.Entity.Direction;
import util.Spell.Aim;
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
	private ArrayList<Entity> entitiesLoaded = new ArrayList<Entity>();
	private ArrayList<Projectile> projectiles = new ArrayList<Projectile>();
	private final static int SCAN_RANGE = 1;

	public Model() {
		//World
		map = new Map(new File("res/map.tmx"));
		map.loadTilesets();
		//Player 
		player = new Player(Skin.getSkins()[0], 0.5f, 0.5f, new Point3f(0,0,0),100,10,100);
		NPCLoader npcLoader = new NPCLoader(new File("res/npc.xml"));
		npcLoader.createAllNpcs().forEach(npc -> entities.add(npc));
		entities.add(player);
		Spell s2 = new Spell();
		s2.setElement(Projectile.Type.FIRE);
		s2.setDamage(5);
		s2.setRadius(0.25f);
		s2.setAim(Spell.Aim.OCTOPUS);
		player.getSpells().add(s2);
		player.setCurrentSpell(s2);
		player.getQuests().add(new SlayerQuest((NPC)entities.get(0), 200, 1, NPC.class));
		player.getQuests().add(new AssassinationQuest((NPC)entities.get(1), 500, (NPC)entities.get(0)));
	}
	
	public void gamelogic() { 
		synchronized(this) {
			entitiesLoaded.clear();
			entities.forEach(e -> {
				if(Viewer.isEntityOnscreen(e, player.getCentre())) {
					if(!e.isDead()) entitiesLoaded.add(e);
				} else {
					if(e instanceof NPC) {
						e.setHostile(false);
					}
				}
			});
			AudioManager am = MainWindow.getAudioManager();
			int id = map.getIdAudioLayer(player.getCentre());
			if(!player.isInCombat()) {
				am.playSongByTileId(id);
			} else {
				am.playSongByTileId(666);
			}
			for (Entity e : entitiesLoaded) {
				if(e instanceof Player) {
					playerLogic(); 
				} else {
					entityLogic(e); 
				}
			}
			for (int i=0;i<projectiles.size();i++) {
				boolean hit = projectileLogic(projectiles.get(i));
				if(hit) {
					projectiles.remove(i);
					i--;
				}
			}
		}
	}

	private void playerLogic() {
		player.regenMana();
		Controller controller = player.getController();
		if(controller.isKeySpacePressed()) {
			player.setSkin(Skin.getSkins()[(player.getSkin().getIndex()+1)%Skin.getSkins().length]);
			controller.setKeySpacePressed(false);
		}
		if(controller.isKeyLeftPressed()) {
			MainWindow.openSpellMenu(player);
			controller.setKeyLeftPressed(false);
		}
		if(controller.isKeyDownPressed()) {
			MainWindow.openQuestMenu(player);
			controller.setKeyDownPressed(false);
		}
		Integer moved = (int)controller.getMouseWheelMoved();
		if(moved != 0) {
			int index = player.getSpells().indexOf(player.getCurrentSpell());
			int newIndex = index + moved;
			if(newIndex > player.getSpells().size()-1) newIndex = 0;
			if(newIndex < 0) newIndex = player.getSpells().size()-1;
			player.setCurrentSpell(player.getSpells().get(newIndex));
			controller.setMouseWheelMoved(0.0);
		}
		animationLogic(player);
		controller.setKeyAWasPressed(controller.isKeyAPressed());
		controller.setKeyDWasPressed(controller.isKeyDPressed());
		controller.setKeyWWasPressed(controller.isKeyWPressed());
		controller.setKeySWasPressed(controller.isKeySPressed());
	}

	private void entityLogic(Entity e) {
		e.regenMana();
		AIController controller = (AIController)e.getController();
		controller.run(this);
		animationLogic(e);
		controller.setKeyAWasPressed(controller.isKeyAPressed());
		controller.setKeyDWasPressed(controller.isKeyDPressed());
		controller.setKeyWWasPressed(controller.isKeyWPressed());
		controller.setKeySWasPressed(controller.isKeySPressed());
	}

	private boolean projectileLogic(Projectile p) {
		Vector3f velocity = p.getVelocity().byScalar(1f / (float)MainWindow.getTargetFPS());
		return collisionHandler(p, velocity);
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
					for (Entity other : entitiesLoaded) {
						if(other == e) {
							continue;
						} else if (punchCollisionHandler(punch, other.getHitbox())){
							if(other.isDead()) break;
							System.out.println(e.getClass().getName() + " punched " + other.getClass().getName());
							other.dealDamage((new Random()).nextInt((int)e.getDamage()),e);
							e.attack(other);
							if(e instanceof Player) updateQuests(other);
						}
					}
				}
				break;
            case CASTING:
				if(e.getProgress()==4) {
					Spell s = e.getCurrentSpell();
					if(s != null) {
						s.cast(e);
					}
				}
				break;
		}
	}

	public Player getPlayer() {
		return player;
	}

	public Map getMap() {
		return map;
	}

	public ArrayList<Projectile> getProjectiles() {
		return projectiles;
	}

	public void updateQuests(Entity e) {
		if(e.isDead()) {
			for (Quest q : player.getQuests()) {
				if(q instanceof SlayerQuest) {
					SlayerQuest sq = (SlayerQuest)q;
					Class<? extends Entity> type = sq.getTypeOfEntity();
					if(e.getClass().equals(type)) {
						sq.incrementProgress();
						System.out.println("progressed in quest " + sq.getDetails());
					}
				}
			}
		}
	}

	public boolean collisionHandler (GameObject go, Vector3f v0) {
		Vector3f v = wallCollisionHandler(go, v0);
		for (Entity other : entitiesLoaded) {
			if(other == go) {
				continue;
			} else if (go instanceof Projectile && other.equals(((Projectile)go).getCaster())) {
				continue;
			} else {
				Vector3f v2 = entityCollisionHandler(go, other, v);
				if (v2 != v) {
					if(other instanceof NPC && go instanceof Player) {
						NPC npc = (NPC)other;
						Player player = (Player)go;
						if(player.getController().isKeyIPressed()) {
							MainWindow.initiateConversation(player, npc);
							System.out.println("Starting a coversation between player and " + npc.getName());
							player.getController().setKeyIPressed(false);
						}
					} else if(go instanceof Projectile) {
						if(!other.isDead()) {
							Projectile p = (Projectile)go;
							System.out.println("Projectile hit " + other.getClass().getName());
							other.dealDamage((new Random()).nextInt((int)p.getDamage()),p.getCaster());
							p.getCaster().attack(other);
							if((p.getCaster()) instanceof Player) updateQuests(other);
						}
						return true;
					}
				}
				v = v2;
			}
		}
		go.move(v);
		return v != v0;
	}

	public Vector3f wallCollisionHandler(GameObject go, Vector3f v) {
		int[][] collisions = map.findCollisionTilesNearbyAPoint(go.getCentre(), SCAN_RANGE);
		int[] tile = map.findTile(go.getCentre());
        int px = tile[0];
        int py = tile[1];
		Hitbox hb = go.getHitbox();
		for (int i=0; i<collisions.length;i++) {
			for (int j=0; j<collisions[i].length;j++) {
				switch (collisions[i][j]) {
					case 8224:
						Hitbox c = new Hitbox(new Point3f(px - SCAN_RANGE + .5f + j,py - SCAN_RANGE + .5f + i,0),1,1);
						Vector3f v2 = hb.intersection(c, v);
						if(v2 != null) return v2;
						break;
					case 300:
						c = new Hitbox(new Point3f(px - SCAN_RANGE + .5f + j,py - SCAN_RANGE + .5f + i,0),1,1);
						Vector3f wasIntersecting = hb.intersection(c, new Vector3f());
						v2 = hb.intersection(c, v);
						if(v2 != null) {
							if(wasIntersecting != null) {
								break;
							}
							if(go instanceof Player) {
								Player p = (Player)go;
								if(p.getController().isKeyUpPressed()) {
									Point3f portalPoint = new Point3f(px - SCAN_RANGE + j, py - SCAN_RANGE + i, 0f);
									String teleport = map.findTeleportTypeByPoint(portalPoint);
									Point3f destinationPoint = map.findTeleportPointByOther(teleport, portalPoint); 
									System.out.println("Teleporting from " + portalPoint + " to " + destinationPoint + " via " + teleport);
									p.getController().setKeyUpPressed(false);
									p.move(new Point3f(destinationPoint.getX()+0.5f,destinationPoint.getY()+0.5f,0f).minusPoint(p.getCentre()));
									return new Vector3f();
								}
							}
							return v2;
						}
						break;
					case 1028:
						c = new Hitbox(new Point3f(px - SCAN_RANGE + .5f + j,py - SCAN_RANGE + .5f + i,0),1,1);
						v2 = hb.intersection(c, v);
						if(v2 != null) {
							if(go instanceof Player) {
								Player p = (Player)go;
								if(p.getController().isKeyRightPressed()) {
									System.out.println("Opening guild quest board");
									p.getController().setKeyRightPressed(false);
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

	public Vector3f entityCollisionHandler(GameObject go, Entity e, Vector3f v) {
		Hitbox hb = go.getHitbox();
		Hitbox hb2 = e.getHitbox();
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

	public void sortEntities(ArrayList<Entity> entities) {
		Collections.sort(entities, (e, o) -> {
			Point3f e1c = e.getCentre();
			Point3f e2c = o.getCentre();
			if(e1c.getY() > e2c.getY()) return 1; 
			else if (e1c.getY() < e2c.getY()) return -1; 
			else return 0; 
		});
	}

	public static int getScanRange() {
		return SCAN_RANGE;
	}

	public ArrayList<Entity> getEntitiesLoaded() {
		return entitiesLoaded;
	}
}
