package com.xsheetgames.genericElements;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.xsheetgames.Configuration;

public class InputButtonCollection {
	
	
	public static final int KEY_UP = 0;
	public static final int KEY_DOWN = 1;
	public static final int KEY_LEFT = 2;
	public static final int KEY_RIGHT = 3;
	public static final int KEY_FIRE = 4;
	
	private Array<InputButton> buttons;

	
	
	public InputButtonCollection(World world) {
		buttons = new Array<InputButton>(5);
		InputButton upButton = null;
		InputButton downButton = null;
		InputButton leftButton = null;		
		InputButton rightButton = null;
		InputButton fireButton = null;
		
		if(Configuration.inputType == 1) {
			upButton = new InputButton(0.65f,3.2f,"keydown");
			downButton = new InputButton(0.65f,0.2f,"keydown");
			leftButton = new InputButton(13.5f,0.45f,"keydown");		
			rightButton = new InputButton(16.5f,0.45f,"keydown");
			if(Configuration.autoFire == true) fireButton = new InputButton(25f, 5f, "keyfire");
			else fireButton = new InputButton(16.6f, 3.5f, "keyfire");
		}	
		else { //inputType == 3
			upButton = new InputButton(25f,10f,"keydown_a");
			downButton = new InputButton(25f,10f,"keydown_a");		
			leftButton = new InputButton(25f,5f,"keydown_a");		
			rightButton = new InputButton(25f,5f,"keydown_a");
			fireButton = new InputButton(25f, 1f, "keyfire");
		}
		
		if(upButton != null) upButton.setUp();
		leftButton.setLeft();
		rightButton.setRight();
		
		if(upButton != null) buttons.add(upButton);
		if(downButton != null)buttons.add(downButton);
		if(leftButton != null)buttons.add(leftButton);
		if(rightButton != null)buttons.add(rightButton);
		if(fireButton != null)buttons.add(fireButton);
	}
	
	public InputButton getButton(int number) {
		return buttons.get(number);
	}
	
	public Rectangle getRectangle(int number) {
		return buttons.get(number).getRectangle();
	}
	
	public void drawAllButtons(SpriteBatch batch) {
		for(InputButton b:buttons) {
			b.draw(batch);
		}
	}
	
	public void resetGraphics(TextureAtlas atlas) {
		for(InputButton b:buttons) {
			b.resetGraphics(atlas);
		}
	}
	
	public void dispose() {
		for(InputButton b:buttons) {
			b.dispose();
		}
		buttons = null;
	}

}
