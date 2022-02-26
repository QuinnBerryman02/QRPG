package mvc;

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
public class Controller {

	protected Vector3f moveDirection = new Vector3f();
	protected Vector3f aimDirection = new Vector3f();

	protected boolean escapePressed = false;
	protected boolean attackPressed = false;
	protected boolean castPressed = false;
	protected boolean talkPressed = false;
	protected boolean changeSkinPressed = false;
	protected boolean questPressed = false;
	protected boolean spellPressed = false;
	protected boolean nextSpellPressed = false;
	protected boolean prevSpellPressed = false;

	public boolean isEscapePressed() {
		return escapePressed;
	}
	public void setEscapePressed(boolean escapePressed) {
		this.escapePressed = escapePressed;
	}
	public boolean isAttackPressed() {
		return attackPressed;
	}
	public boolean isCastPressed() {
		return castPressed;
	}
	public boolean isChangeSkinPressed() {
		return changeSkinPressed;
	}
	public boolean isNextSpell() {
		return nextSpellPressed;
	}
	public boolean isPrevSpell() {
		return prevSpellPressed;
	}
	public boolean isQuestPressed() {
		return questPressed;
	}
	public boolean isSpellPressed() {
		return spellPressed;
	}
	public boolean isTalkPressed() {
		return talkPressed;
	}
	public void setAttackPressed(boolean attackPressed) {
		this.attackPressed = attackPressed;
	}
	public void setCastPressed(boolean castPressed) {
		this.castPressed = castPressed;
	}
	public void setNextSpellPressed(boolean nextSpellPressed) {
		this.nextSpellPressed = nextSpellPressed;
	}
	public void setPrevSpellPressed(boolean prevSpellPressed) {
		this.prevSpellPressed = prevSpellPressed;
	}
	public void setChangeSkinPressed(boolean changeSkinPressed) {
		this.changeSkinPressed = changeSkinPressed;
	}
	public void setQuestPressed(boolean questPressed) {
		this.questPressed = questPressed;
	}
	public void setSpellPressed(boolean spellPressed) {
		this.spellPressed = spellPressed;
	}
	public void setTalkPressed(boolean talkPressed) {
		this.talkPressed = talkPressed;
	}

	public void clear() {
		moveDirection = new Vector3f();
		aimDirection = new Vector3f();
		attackPressed = false; 
		talkPressed = false; 
		castPressed = false; 
		changeSkinPressed = false; 
		questPressed = false; 
		spellPressed = false; 
		nextSpellPressed = false;
		prevSpellPressed = false;
	}

	public Vector3f getAimDirection() {
		return aimDirection;
	}

	public Vector3f getMoveDirection() {
		return moveDirection;
	}
}