import java.util.concurrent.CopyOnWriteArrayList;

import util.*;
import util.Player.AnimationPhase;
import util.Player.Direction;
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
	private Controller controller = Controller.getInstance();

	public Model() {
		//World

		//Player 
		player = new Player(Skin.getSkins()[0], 50, 50, new Point3f(500,500,0), 10);
	}
	
	public void gamelogic() { 
		playerLogic(); 
	}

	private void playerLogic() {
		float speed = player.getSpeed();
		player.incrementProgress();
		AnimationPhase ap = player.getPhase();
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
					player.getCentre().applyVector( new Vector3f(-speed,0,0)); 
				}
				if(controller.isKeyDPressed()) {
					if(!player.setDirection(Direction.RIGHT) && !player.getVerticalMovement()) {
						player.setProgress(0);
					}
					player.setPhase(AnimationPhase.WALKING);
					player.getCentre().applyVector( new Vector3f(speed,0,0));
				}
				if(controller.isKeyWPressed()) {
					if(!player.setDirection(Direction.UP) && !wasMovingVertical) {
						player.setProgress(0);
					}
					player.setPhase(AnimationPhase.WALKING);
					player.getCentre().applyVector( new Vector3f(0,speed,0));
				}
				if(controller.isKeySPressed()){
					if(!player.setDirection(Direction.DOWN) && !wasMovingVertical) {
						player.setProgress(0);
					}
					player.setPhase(AnimationPhase.WALKING);
					player.getCentre().applyVector( new Vector3f(0,-speed,0));
				}
				break;
            case ATTACKING: break;
                //todo check if the player hits an entity on climax of punch
            case CASTING:	break;
                //todo cast the spell at the climax
		}
	}

	public Player getPlayer() {
		return player;
	}
}