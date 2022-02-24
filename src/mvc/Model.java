package mvc;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

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
public class Model {
	private Dungeon dungeon;
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
		dungeon = new Dungeon(Dungeon.DType.CAVE, 5);
		//Player 
		Enemy e1 = new Enemy(Type.WIND_ELEMENTAL, 0.5f, 0.5f, new Point3f(-12f,-10f,0f), 100, 10, 100);
		player = new Player(Skin.getSkins()[0], 0.5f, 0.5f, new Point3f(0,0,0),100,10,100);
		NPCLoader npcLoader = new NPCLoader(new File("res/npc.xml"));
		npcLoader.createAllNpcs().forEach(npc -> entities.add(npc));
		entities.add(player);
		entities.add(e1);
		Spell s2 = new Spell();
		s2.setElement(Projectile.Type.FIRE);
		s2.setDamage(50);
		s2.setRadius(0.25f);
		s2.setAim(Spell.Aim.AIM_BY_MOUSE);
		player.getSpells().add(s2);
		player.setCurrentSpell(s2);
		player.getQuests().add(new SlayerQuest((NPC)entities.get(0), 200, 10, Enemy.Type.SLIME));
		player.getQuests().add(new AssassinationQuest((NPC)entities.get(1), 500, (NPC)entities.get(0)));
	}
	
	public void gamelogic() { 
		synchronized(this) {
			entitiesLoaded.clear();
			if(dungeon.isInThis()!=null) {
				Dungeon.CTYPE chunkType = dungeon.getChunkByCoords(player.getCentre());
				if(chunkType.ordinal() -8 <= 2 && chunkType.ordinal() -8  >= 0) {
					Enemy[] enemies = dungeon.generateEnemies(player.getCentre());
					if(enemies!=null) for(Enemy e : enemies) if(e!=null) entities.add(e);
				}
			}
			entities.forEach(e -> {
				if(Viewer.isEntityOnscreen(e, player.getCentre())) {
					if(!e.isDead()) entitiesLoaded.add(e);
					if(e instanceof Enemy && !e.isDead()) {
						e.setHostile(true);
						e.commenceCombat(player);
					}
				} else {
					e.setHostile(false);
				}
			});
			player.updateCombat();
			AudioManager am = MainWindow.getAudioManager();
			int id = map.getIdAudioLayer(player.getCentre(), dungeon.isInThis());
			if(player.isInCombat()) {
				am.playSongByTileId(666);
			} else {
				if(dungeon.isInThis()!=null) {
					if(dungeon.getType().equals(Dungeon.DType.CAVE)) {
						am.playSongByTileId(639 + 36);
					}
				} else {
					am.playSongByTileId(id);
				}
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
		((PlayerController)controller).update();
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
				if(dir!=-1 && e.getProgress()==1) {
					if(player.isIndoors()) {
						if(dungeon.isInThis()!=null) {
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
		int[][] collisions = map.findCollisionTilesNearbyAPoint(go.getCentre(), SCAN_RANGE, dungeon.isInThis());
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
									String teleport = map.findTeleportTypeByPoint(portalPoint, dungeon);
									Point3f destinationPoint = map.findTeleportPointByOther(teleport, portalPoint, dungeon); 
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
								if(p.getController().isGuildPressed()) {
									System.out.println("Opening guild quest board");
									p.getController().setGuildPressed(false);
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

	public Dungeon getDungeon() {
		return dungeon;
	}

	public void setDungeon(Dungeon dungeon) {
		this.dungeon = dungeon;
	}
}
