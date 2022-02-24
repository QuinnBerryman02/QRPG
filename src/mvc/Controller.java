package mvc;


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
public class Controller {
	//Make it a direction vector
	protected boolean leftWasPressed = false;
	protected boolean downWasPressed = false;
	protected boolean rightWasPressed = false;
	protected boolean upWasPressed = false;

	protected boolean leftPressed = false;
	protected boolean downPressed = false;
	protected boolean rightPressed = false;
	protected boolean upPressed = false;

	protected boolean attackPressed = false;
	protected boolean castPressed = false;
	protected boolean talkPressed = false;
	protected boolean changeSkinPressed = false;
	protected boolean guildPressed = false;
	protected boolean questPressed = false;
	protected boolean spellPressed = false;

	protected int mouseX;
	protected int mouseY;
	protected double mouseWheelMoved;

	public boolean isAttackPressed() {
		return attackPressed;
	}
	public boolean isCastPressed() {
		return castPressed;
	}
	public boolean isChangeSkinPressed() {
		return changeSkinPressed;
	}
	public boolean isDownPressed() {
		return downPressed;
	}
	public boolean isDownWasPressed() {
		return downWasPressed;
	}
	public boolean isGuildPressed() {
		return guildPressed;
	}
	public boolean isLeftPressed() {
		return leftPressed;
	}
	public boolean isLeftWasPressed() {
		return leftWasPressed;
	}
	public boolean isQuestPressed() {
		return questPressed;
	}
	public boolean isRightPressed() {
		return rightPressed;
	}
	public boolean isRightWasPressed() {
		return rightWasPressed;
	}
	public boolean isSpellPressed() {
		return spellPressed;
	}
	public boolean isTalkPressed() {
		return talkPressed;
	}
	public boolean isUpPressed() {
		return upPressed;
	}
	public boolean isUpWasPressed() {
		return upWasPressed;
	}
	public void setAttackPressed(boolean attackPressed) {
		this.attackPressed = attackPressed;
	}
	public void setCastPressed(boolean castPressed) {
		this.castPressed = castPressed;
	}
	public void setChangeSkinPressed(boolean changeSkinPressed) {
		this.changeSkinPressed = changeSkinPressed;
	}
	public void setDownPressed(boolean downPressed) {
		this.downPressed = downPressed;
	}
	public void setDownWasPressed(boolean downWasPressed) {
		this.downWasPressed = downWasPressed;
	}
	public void setGuildPressed(boolean guildPressed) {
		this.guildPressed = guildPressed;
	}
	public void setLeftPressed(boolean leftPressed) {
		this.leftPressed = leftPressed;
	}
	public void setLeftWasPressed(boolean leftWasPressed) {
		this.leftWasPressed = leftWasPressed;
	}
	public void setQuestPressed(boolean questPressed) {
		this.questPressed = questPressed;
	}
	public void setRightPressed(boolean rightPressed) {
		this.rightPressed = rightPressed;
	}
	public void setRightWasPressed(boolean rightWasPressed) {
		this.rightWasPressed = rightWasPressed;
	}
	public void setSpellPressed(boolean spellPressed) {
		this.spellPressed = spellPressed;
	}
	public void setTalkPressed(boolean talkPressed) {
		this.talkPressed = talkPressed;
	}
	public void setUpPressed(boolean upPressed) {
		this.upPressed = upPressed;
	}
	public void setUpWasPressed(boolean upWasPressed) {
		this.upWasPressed = upWasPressed;
	}

	public void clear() {
		leftPressed = false; 
		downPressed = false; 
		upPressed = false; 
		rightPressed = false; 
		attackPressed = false; 
		talkPressed = false; 
		castPressed = false; 
		changeSkinPressed = false; 
		guildPressed = false; 
		questPressed = false; 
		spellPressed = false; 
	}
	//TODO make it direction instead of seperate values
	public int getMouseX() {
		return mouseX;
	}

	public int getMouseY() {
		return mouseY;
	}

	public void setMouseX(int mouseX) {
		this.mouseX = mouseX;
	}

	public void setMouseY(int mouseY) {
		this.mouseY = mouseY;
	}
	//TODO make it work with controller
	public double getMouseWheelMoved() {
		return mouseWheelMoved;
	}

	public void setMouseWheelMoved(double mouseWheelMoved) {
		this.mouseWheelMoved = mouseWheelMoved;
	}
}