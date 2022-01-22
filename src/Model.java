import java.util.concurrent.CopyOnWriteArrayList;

import util.GameObject;
import util.Point3f;
import util.Vector3f; 
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
	private GameObject player;
	private Controller controller = Controller.getInstance();
	private CopyOnWriteArrayList<GameObject> enemiesList = new CopyOnWriteArrayList<GameObject>();
	private CopyOnWriteArrayList<GameObject> bulletList = new CopyOnWriteArrayList<GameObject>();
	private int score = 0; 

	public Model() {
		//World

		//Player 
		player = new GameObject("res/Lightning.png", 50, 50, new Point3f(500,500,0));
		//Enemies
		enemiesList.add(new GameObject("res/UFO.png", 50, 50, new Point3f(((float)Math.random()*50+400), 0, 0))); 
		enemiesList.add(new GameObject("res/UFO.png", 50, 50, new Point3f(((float)Math.random()*50+500), 0, 0)));
		enemiesList.add(new GameObject("res/UFO.png", 50, 50, new Point3f(((float)Math.random()*100+500), 0, 0)));
		enemiesList.add(new GameObject("res/UFO.png", 50, 50, new Point3f(((float)Math.random()*100+400), 0, 0)));
	}
	
	public void gamelogic() 
	{
		// Player Logic first 
		playerLogic(); 
		// Enemy Logic next
		enemyLogic();
		// Bullets move next 
		bulletLogic();
		// interactions between objects 
		objectLogic(); 
	}

	private void objectLogic() { 
		for (GameObject enemy : enemiesList) {
			for (GameObject Bullet : bulletList) {
				if ( Math.abs(enemy.getCentre().getX() - Bullet.getCentre().getX())< enemy.getWidth() && Math.abs(enemy.getCentre().getY() - Bullet.getCentre().getY()) < enemy.getHeight()) {
					enemiesList.remove(enemy);
					bulletList.remove(Bullet);
					score++;
				}  
			}
		}	
	}

	private void enemyLogic() {
		for (GameObject temp : enemiesList) {
			temp.getCentre().applyVector(new Vector3f(0,-1,0));

			if (temp.getCentre().getY() >= 900.0f) {
				enemiesList.remove(temp);
				score--;
			} 
		}

		if (enemiesList.size()<2) {
			while (enemiesList.size()<6) {
				enemiesList.add(new GameObject("res/UFO.png", 50, 50, new Point3f(((float)Math.random()*1000), 0, 0))); 
			}
		}
	}

	private void bulletLogic() {
		for (GameObject bullet : bulletList) {
			bullet.getCentre().applyVector(new Vector3f(0,1,0));
			if (bullet.getCentre().getY() <=0 ) {
			 	bulletList.remove(bullet);
			} 
		} 
	}

	private void playerLogic() {
		if(controller.isKeyAPressed()) {
			player.getCentre().applyVector( new Vector3f(-2,0,0)); 
		}
		if(controller.isKeyDPressed()) {
			player.getCentre().applyVector( new Vector3f(2,0,0));
		}
		if(controller.isKeyWPressed()) {
			player.getCentre().applyVector( new Vector3f(0,2,0));
		}
		if(controller.isKeySPressed()){
			player.getCentre().applyVector( new Vector3f(0,-2,0));
		}
		if(controller.isKeySpacePressed()) {
			createBullet();
			controller.setKeySpacePressed(false);
		} 
		
	}

	private void createBullet() {
		bulletList.add(new GameObject("res/Bullet.png", 32, 64, new Point3f(player.getCentre().getX(), player.getCentre().getY(), 0.0f)));
	}

	public GameObject getPlayer() {
		return player;
	}

	public CopyOnWriteArrayList<GameObject> getEnemies() {
		return enemiesList;
	}
	
	public CopyOnWriteArrayList<GameObject> getBullets() {
		return bulletList;
	}

	public int getScore() { 
		return score;
	}
}