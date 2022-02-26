package mvc;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;
import java.awt.event.InputEvent;

import main.MainWindow;
import util.*;
import util.Enemy.Type;
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
public class Model implements Serializable{
	private ArrayList<Dungeon> dungeons = new ArrayList<Dungeon>();
	transient private Player player;
	transient private Map map = MainWindow.getMap();
	transient private ArrayList<Entity> entities = new ArrayList<Entity>();
	transient private ArrayList<Entity> entitiesLoaded = new ArrayList<Entity>();
	transient private ArrayList<Projectile> projectiles = new ArrayList<Projectile>();
	transient private final static int SCAN_RANGE = 1;
	private STAGE stage;

	public enum STAGE {
		BEGINING,
		MIDGAME,
		ENDGAME
	}

	public Model() {
		
	}

	public void initialiseNewModel() {
		//World
		dungeons.add(new Dungeon(Dungeon.DType.CAVE, 5));
		dungeons.add(new Dungeon(Dungeon.DType.SEWER, 5));
		//Player 
		player = new Player(Skin.getSkins()[0], 0.5f, 0.5f, new Point3f(111,118,0),100,10,100);
		NPCLoader npcLoader = new NPCLoader(new File("res/npc.xml"));
		npcLoader.createAllNpcs().forEach(npc -> entities.add(npc));
		entities.add(player);
		Spell s2 = new Spell();
		s2.setElement(Projectile.Type.FIRE);
		s2.setDamage(50);
		s2.setRadius(0.25f);
		s2.setAim(Spell.Aim.AIM_BY_MOUSE);
		player.getSpells().add(s2);
		player.setCurrentSpell(s2);
	}
	
	public void gamelogic() { 
		synchronized(this) {
			boolean cameraMode = MainWindow.getCanvas().isInCameraMode();
			entitiesLoaded.clear();
			if(inADungeon() && !cameraMode) {
				Dungeon.CTYPE chunkType = getCurrentDungeon().getChunkByCoords(player.getCentre());
				if(chunkType.ordinal() -8 <= 2 && chunkType.ordinal() -8  >= 0) {
					Enemy[] enemies = getCurrentDungeon().generateEnemies(player.getCentre());
					if(enemies!=null) for(Enemy e : enemies) if(e!=null) entities.add(e);
				}
			}
			entities.forEach(e -> {
				if(MainWindow.getCanvas().isObjectOnScreen(e) || e instanceof Player) {
					if(!e.isDead()) entitiesLoaded.add(e);
					if(e instanceof Enemy && !e.isDead()) {
						e.setHostile(true);
						e.commenceCombat(player);
					}
				} else {
					if(!(e instanceof Player))
						e.setHostile(false);
				}
			});
			player.updateCombat();
			
			AudioManager am = MainWindow.getAudioManager();
			if(!cameraMode) {
				int id = map.getIdAudioLayer(player.getCentre());
				if(player.isInCombat()) {
					am.playSongByTileId(666);
				} else {
					if(inADungeon()) {
						if(getCurrentDungeon().getType().equals(Dungeon.DType.CAVE)) {
							am.playSongByTileId(639 + 36);
						} else {
							am.playSongByTileId(3424 + 81);
						}
					} else {
						am.playSongByTileId(id);
					}
				}
			}
			for (Entity e : entitiesLoaded) {
				if(e instanceof Player) {
					playerLogic(); 
				} else {
					entityLogic(e); 
				}
			}
			if(!cameraMode){
				for (int i=0;i<projectiles.size();i++) {
					if(MainWindow.getCanvas().isObjectOnScreen(projectiles.get(i))) {
						boolean hit = projectileLogic(projectiles.get(i));
						if(hit) {
							projectiles.remove(i);
							i--;
						}
					} else {
						projectiles.remove(i);
						i--;
					}
				}	
			}
		}
	}

	private void playerLogic() {
		PlayerController controller = (PlayerController)player.getController();
		controller.update();
		if(!MainWindow.getCanvas().isInCameraMode()) {
			player.regenMana();
			if(controller.isMinusPressed()) {
				MainWindow.saveGame();
				controller.setMinusPressed(false);
			}
			if(controller.isPlusPressed()) {
				MainWindow.loadGame();
				controller.setPlusPressed(false);
			}
			if(controller.isChangeSkinPressed()) {
				player.setSkin(Skin.getSkins()[(player.getSkin().getIndex()+1)%Skin.getSkins().length]);
				controller.setChangeSkinPressed(false);
			}
			if(controller.isSpellPressed()) {
				MainWindow.openSpellMenu(player);
				controller.setSpellPressed(false);
			}
			if(controller.isQuestPressed()) {
				MainWindow.openQuestMenu(player);
				controller.setQuestPressed(false);
			}
			if(controller.isEscapePressed()) {
				MainWindow.closeMenu();
				controller.setEscapePressed(false);
			}
			if(controller.isNextSpell() || controller.isPrevSpell()) {
				int index = player.getSpells().indexOf(player.getCurrentSpell());
				int newIndex = index + (controller.isNextSpell() ? 1 : -1);
				if(newIndex > player.getSpells().size()-1) newIndex = 0;
				if(newIndex < 0) newIndex = player.getSpells().size()-1;
				player.setCurrentSpell(player.getSpells().get(newIndex));
				controller.setNextSpellPressed(false);
				controller.setPrevSpellPressed(false);
			}
			if(!MainWindow.inAMenu()) {
				animationLogic(player);
			}
		}
		if(MainWindow.inAMenu()) {
			if(controller.isControllerMode()) {
				try {
					Robot robot = new Robot();
					Point p = MouseInfo.getPointerInfo().getLocation();
					Point3f p2 = new Point3f((float)p.getX(),(float)p.getY(),0);
					Vector3f dir = player.getController().getMoveDirection();
					Vector3f dir2 = dir.byScalar(PlayerController.MOUSE_MENU_SPEED);
					Point3f newPoint = p2.plusVector(dir2);
					robot.mouseMove((int)newPoint.getX(), (int)newPoint.getY());
					if(controller.isTalkPressed()) {
						if(!controller.isPressedOnMenu()) {
							robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
							controller.setPressedOnMenu(true);
						}
					} else {
						if(controller.isPressedOnMenu()) {
							robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
							controller.setPressedOnMenu(false);
						}
					}
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void entityLogic(Entity e) {
		e.regenMana();
		AIController controller = (AIController)e.getController();
		controller.run(this);
		animationLogic(e);
	}

	private boolean projectileLogic(Projectile p) {
		Vector3f velocity = p.getVelocity().byScalar(1f / (float)MainWindow.getTargetFPS());
		return collisionHandler(p, velocity);
	}

	public void animationLogic(Entity e) {
		float speed = e.getSpeed() / (float)MainWindow.getTargetFPS();
		Vector3f v = e.getController().getMoveDirection().byScalar(speed);
		e.incrementProgress();
		AnimationPhase ap = e.getPhase();
		Controller controller = e.getController(); 
		switch(ap) {
			case NEUTRAL:
			case WALKING:
				if(controller.isAttackPressed()){
					e.setPhase(AnimationPhase.ATTACKING);
					e.setProgress(0);
					break;
				}
				if(controller.isCastPressed()){
					e.setPhase(AnimationPhase.CASTING);
					e.setProgress(0);
					break;
				}
				
				int dir = v.roundToQuad();
				switch(dir) {
					case 0:
						if(!e.setDirection(Direction.UP)) {
							e.setProgress(0);
						}
						e.setPhase(AnimationPhase.WALKING);
						break;
					case 1:
						if(!e.setDirection(Direction.RIGHT)) {
							e.setProgress(0);
						}
						e.setPhase(AnimationPhase.WALKING);
						break;
					case 2:
						if(!e.setDirection(Direction.DOWN)) {
							e.setProgress(0);
						}
						e.setPhase(AnimationPhase.WALKING);
						break;
					case 3:
						if(!e.setDirection(Direction.LEFT)) {
							e.setProgress(0);
						}
						e.setPhase(AnimationPhase.WALKING);
						break;
					default:
						e.setPhase(AnimationPhase.NEUTRAL);
						e.setProgress(0);
						break;
				}
				if(dir!=-1 && e.getProgress()==1 && !MainWindow.getCanvas().isInCameraMode()) {
					if(player.isIndoors()) {
						if(inADungeon()) {
							MainWindow.getAudioManager().playSoundByName("cave_steps");
						} else {
							MainWindow.getAudioManager().playSoundByName("wood_steps");
						}
					} else {
						MainWindow.getAudioManager().playSoundByName("dirt_steps");
					}
					
				}
				collisionHandler(e, new Vector3f(v.getX(),0,0));
				collisionHandler(e, new Vector3f(0,v.getY(),0));
				break;
            case ATTACKING:
				if(e.getProgress()==e.progressMax() / 2) {
					MainWindow.getAudioManager().playSoundByName("punch");
					Hitbox punch = getPunchHitbox(e);
					for (Entity other : entitiesLoaded) {
						if(other == e) {
							continue;
						} else if (punchCollisionHandler(punch, other.getHitbox())){
							if(other.isDead()) break;
							System.out.println(e.getClass().getName() + " punched " + other.getClass().getName());
							other.dealDamage((new Random()).nextInt((int)e.getDamage()),e);
							if(e instanceof Player) updateQuests(other);
						}
					}
				}
				break;
            case CASTING:
				if(e.getProgress()==e.progressMax() / 2) {
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
					if(e instanceof Enemy) {
						Enemy enemy = (Enemy)e;
						if(enemy.getType().equals(sq.getTypeOfEnemy())) {
							sq.incrementProgress();
							System.out.println("progressed in quest " + sq.getDetails());
						}
					}
				}
			}
		}
	}

	public boolean collisionHandler (GameObject go, Vector3f v0) {
		Vector3f v = wallCollisionHandler(go, v0);
		if(go instanceof Projectile && v != v0) return true;
		for (Entity other : entitiesLoaded) {
			if(other == go) {
				continue;
			} else if (go instanceof Projectile && other.equals(((Projectile)go).getCaster())) {
				continue;
			} else {
				if(go instanceof Projectile) {
					Projectile p = (Projectile)go;
					boolean colliding = false;
					Hitbox h2 = p.getHitbox().plusVector(v0);
					colliding = colliding || h2.isColliding(other.getHitbox());
					colliding = colliding || other.getHitbox().isColliding(h2);
					if(colliding) {
						if(!other.isDead()) {
							System.out.println("Projectile hit " + other.getClass().getName());
							other.dealDamage((new Random()).nextInt((int)p.getDamage()),p.getCaster());
							if((p.getCaster()) instanceof Player) updateQuests(other);
						}
						return true;
					}
					continue;
				}
				Vector3f v2 = entityCollisionHandler(go, other, v);
				if (v2 != v) {
					if(other instanceof NPC && go instanceof Player) {
						NPC npc = (NPC)other;
						Player player = (Player)go;
						if(player.getController().isTalkPressed()) {
							MainWindow.initiateConversation(player, npc);
							System.out.println("Starting a coversation between player and " + npc.getName());
							player.getController().setTalkPressed(false);
						}
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
		int[] tile = Map.findTile(go.getCentre());
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
					case 3236:
						if(go instanceof Projectile) continue;
						c = new Hitbox(new Point3f(px - SCAN_RANGE + .5f + j,py - SCAN_RANGE + .5f + i,0),1,1);
						v2 = hb.intersection(c, v);
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
								//if(p.getController().isDoorPressed()) {
									Point3f portalPoint = new Point3f(px - SCAN_RANGE + j, py - SCAN_RANGE + i, 0f);
									String teleport = map.findTeleportTypeByPoint(portalPoint);
									Point3f destinationPoint = map.findTeleportPointByOther(teleport, portalPoint); 
									System.out.println("Teleporting from " + portalPoint + " to " + destinationPoint + " via " + teleport);
									p.move(new Point3f(destinationPoint.getX()+0.5f,destinationPoint.getY()+0.5f,0f).minusPoint(p.getCentre()));
									return new Vector3f();
								//}
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
								if(p.getController().isTalkPressed()) {
									System.out.println("Opening guild quest board");
									MainWindow.openGuildMenu(p);
									p.getController().setTalkPressed(false);
								}
							}
							return v2;
						}
						break;
					case 1558:
						c = new Hitbox(new Point3f(px - SCAN_RANGE + .5f + j,py - SCAN_RANGE + .5f + i,0),1,1);
						v2 = hb.intersection(c, v);
						if(v2 != null) {
							if(go instanceof Player) {
								Player p = (Player)go;
								if(p.getController().isTalkPressed()) {
									System.out.println("Opening Statue Menu");
									MainWindow.openStatueMenu(p);
									p.getController().setTalkPressed(false);
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

	public boolean inRangeOfPlayer(Entity entity, float RANGE) {
		//returns true if the entity is in attack range
		float dx = entity.getCentre().getX() - player.getCentre().getX();
		float dy = entity.getCentre().getY() - player.getCentre().getY();
		return (RANGE*RANGE >= dx*dx + dy*dy);
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

	public ArrayList<Entity> getNonPlayerEntities() {
		ArrayList<Entity> newList = new ArrayList<Entity>();
		for (Entity e : entities) {
			if((e instanceof NPC) || (e instanceof Enemy && !e.isDead())) {
				newList.add(e);
			}
		}
		return newList;
	}

	public ArrayList<Dungeon> getDungeons() {
		return dungeons;
	}

	public Dungeon getCurrentDungeon() {
		for (Dungeon dungeon : dungeons) {
			if(dungeon.isInThis()) return dungeon;
		}
		return null;
	}

	public boolean inADungeon() {
		return (getCurrentDungeon()!=null);
	}

	public STAGE getStage() {
		return stage;
	}

	public void setStage(STAGE stage) {
		this.stage = stage;
	}

	private void writeObject(ObjectOutputStream out) throws IOException{
		out.defaultWriteObject();
		out.writeObject(getNonPlayerEntities());
		out.writeObject(player);
		AudioManager am =  MainWindow.getAudioManager();
		out.writeObject(am.getLastCombatPlayedSong());
		out.writeObject(am.getLastNonCombatSong());
		out.writeObject(am.getLastOverworldPlayedSong());
		out.writeObject(am.getLastPlayedSong());
		out.writeObject(am.getLastTownPlayedSong());
		System.out.println(this.toString());
	}

	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException{
		in.defaultReadObject();
		entities = (ArrayList<Entity>)in.readObject();
		NPCLoader.getNpcs().clear();
		for(Entity e : entities) {
			if(e instanceof Enemy) continue;
			NPCLoader.getNpcs().add((NPC)e);
		}
		player = (Player)in.readObject();
		AudioManager am = MainWindow.getAudioManager();
		am.setLastCombatPlayedSong(am.getSongByFile((File)in.readObject()));
		am.setLastNonCombatSong(am.getSongByFile((File)in.readObject()));
		am.setLastOverworldPlayedSong(am.getSongByFile((File)in.readObject()));
		am.setLastPlayedSong(am.getSongByFile((File)in.readObject()));
		am.setLastTownPlayedSong(am.getSongByFile((File)in.readObject()));
		am.playLastPlayed();
		entities.add(player);
		map = MainWindow.getMap();
		entitiesLoaded = new ArrayList<Entity>();
		projectiles = new ArrayList<Projectile>();
		System.out.println(this.toString());
	}
	
	@Override
	public String toString() {
		String s = "";
		s+=stage.name() + "\n";
		for (Entity e : entities) {
			s+=e.toString() + "\n\n";
		}
		return s;
	}
}
