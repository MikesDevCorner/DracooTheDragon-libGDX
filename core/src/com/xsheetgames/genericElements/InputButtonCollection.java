package com.xsheetgames.genericElements;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.xsheetgames.Configuration;
import com.xsheetgames.GameAssets;

public class InputButtonCollection {
	
	
	public static final int KEY_UP = 0;
	public static final int KEY_DOWN = 1;
	public static final int KEY_LEFT = 2;
	public static final int KEY_RIGHT = 3;
	public static final int KEY_FIRE = 4;
	
	private Array<InputButton> buttons;

	//D-Pad (inputType == 2): eine input_cross-Grafik statt einzelner
	//Richtungs-Buttons, die Richtung wird per Quadranten-Test ermittelt.
	private Sprite dpad;
	private float dpadX, dpadY, dpadSize;



	public InputButtonCollection(World world) {
		buttons = new Array<InputButton>(5);
		InputButton upButton = null;
		InputButton downButton = null;
		InputButton leftButton = null;
		InputButton rightButton = null;
		InputButton fireButton = null;

		if(Configuration.inputType == 2) { //D-Pad
			// D-Pad rechts unten (dort wo im Button-Schema links/rechts liegen),
			// Feuerknopf wandert dafuer nach links unten.
			this.dpadSize = 350f * Configuration.VIEWPORT_WIDTH / Configuration.TARGET_WIDTH;
			this.dpadX = Configuration.GAME_WORLD_WIDTH - this.dpadSize - 0.53f;
			this.dpadY = 0.3f;
			this.createDpadSprite();
			if(Configuration.autoFire == true) fireButton = new InputButton(Configuration.GAME_WORLD_WIDTH + 5f, 5f, "keyfire");
			else fireButton = new InputButton(0.65f, 0.45f, "keyfire");
		}
		else { //inputType == 1: Einzel-Buttons
			// Right-side buttons are anchored to the actual (aspect-dependent)
			// right edge so they stay reachable on every screen width. Offsets
			// are kept identical to the original 20m-wide design layout.
			upButton = new InputButton(0.65f,3.2f,"keydown");
			downButton = new InputButton(0.65f,0.2f,"keydown");
			leftButton = new InputButton(Configuration.GAME_WORLD_WIDTH - 6.5f,0.45f,"keydown");
			rightButton = new InputButton(Configuration.GAME_WORLD_WIDTH - 3.5f,0.45f,"keydown");
			if(Configuration.autoFire == true) fireButton = new InputButton(Configuration.GAME_WORLD_WIDTH + 5f, 5f, "keyfire");
			else fireButton = new InputButton(Configuration.GAME_WORLD_WIDTH - 3.4f, 3.5f, "keyfire");
		}

		if(upButton != null) upButton.setUp();
		if(leftButton != null) leftButton.setLeft();
		if(rightButton != null) rightButton.setRight();

		//Die Positionen muessen den KEY_*-Indizes entsprechen -
		//im D-Pad-Modus bleiben die Richtungs-Eintraege null.
		buttons.add(upButton);
		buttons.add(downButton);
		buttons.add(leftButton);
		buttons.add(rightButton);
		buttons.add(fireButton);
	}

	private void createDpadSprite() {
		dpad = new Sprite(GameAssets.fetchTextureAtlas("game/images/game_objects.pack").findRegion("input_cross"));
		dpad.setSize(dpadSize, dpadSize);
		dpad.setPosition(dpadX, dpadY);
	}

	/**
	 * Liefert KEY_UP/KEY_DOWN/KEY_LEFT/KEY_RIGHT fuer einen Punkt (Meter) auf dem
	 * D-Pad, sonst -1. Die Quadranten entsprechen den Diagonalen der
	 * input_cross-Grafik; die Kreismitte ist neutral.
	 */
	public int getDpadDirection(float x, float y) {
		if(dpad == null) return -1;
		float half = dpadSize / 2f;
		float dx = x - (dpadX + half);
		float dy = y - (dpadY + half);
		if(dx * dx + dy * dy > half * half) return -1;
		float dead = half * 0.15f;
		if(dx * dx + dy * dy < dead * dead) return -1;
		if(dy > Math.abs(dx)) return KEY_UP;
		if(dy < -Math.abs(dx)) return KEY_DOWN;
		if(dx < 0f) return KEY_LEFT;
		return KEY_RIGHT;
	}
	
	public InputButton getButton(int number) {
		return buttons.get(number);
	}
	
	public Rectangle getRectangle(int number) {
		return buttons.get(number).getRectangle();
	}
	
	public void drawAllButtons(SpriteBatch batch) {
		if(dpad != null) dpad.draw(batch);
		for(InputButton b:buttons) {
			if(b != null) b.draw(batch);
		}
	}

	public void resetGraphics(TextureAtlas atlas) {
		if(dpad != null) this.createDpadSprite();
		for(InputButton b:buttons) {
			if(b != null) b.resetGraphics(atlas);
		}
	}

	public void dispose() {
		for(InputButton b:buttons) {
			if(b != null) b.dispose();
		}
		buttons = null;
		dpad = null;
	}

}
