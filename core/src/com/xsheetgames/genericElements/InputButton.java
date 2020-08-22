package com.xsheetgames.genericElements;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.xsheetgames.Configuration;
import com.xsheetgames.GameAssets;

public class InputButton {

	private Sprite sprite1;
	private Sprite sprite2;
	private TextureRegion tex1;
	private TextureRegion tex2;
	private boolean normal;
	public boolean hidden;
	private String buttonString;
	private float tx,ty;
	private boolean up=false,left=false,right=false;
	
	
	public InputButton(float x, float y, String button) {
		hidden = false;
		normal = true;
		tex1 = GameAssets.fetchTextureAtlas("game/images/game_objects.pack").findRegion(button);
		tex2 = GameAssets.fetchTextureAtlas("game/images/game_objects.pack").findRegion(button+"_black");

		sprite1 = new Sprite(tex1);
		sprite2 = new Sprite(tex2);
		sprite1.setSize(tex1.getRegionWidth() * Configuration.VIEWPORT_WIDTH / Configuration.TARGET_WIDTH, tex1.getRegionHeight() * Configuration.VIEWPORT_HEIGHT / Configuration.TARGET_HEIGHT);
		sprite1.setPosition(x, y);
		sprite2.setSize(tex2.getRegionWidth() * Configuration.VIEWPORT_WIDTH / Configuration.TARGET_WIDTH, tex2.getRegionHeight() * Configuration.VIEWPORT_HEIGHT / Configuration.TARGET_HEIGHT);
		sprite2.setPosition(x, y);
		buttonString = button;
		tx = x;
		ty = y;
	}
	
	public void setUp() {
		this.up = true;
		this.sprite1.rotate90(true);
		this.sprite2.rotate90(true);
		this.sprite1.rotate90(true);
		this.sprite2.rotate90(true);
	}
	
	public void setLeft() {
		this.left = true;
		this.sprite1.rotate90(true);
		this.sprite2.rotate90(true);
	}
	
	public void setRight() {
		this.right = true;
		this.sprite1.rotate90(false);
		this.sprite2.rotate90(false);
	}
	
	
	
	public void draw(SpriteBatch batch) {
		if(hidden != true) {
			if(normal == true) sprite1.draw(batch);
			else sprite2.draw(batch);
		}
	}
	
	public Rectangle getRectangle() {
		if(hidden != true) return sprite1.getBoundingRectangle();
		else return new Rectangle();
	}
	
	public void setDrawTarget(boolean normal) {
		this.normal = normal;
	}
	
	public void resetGraphics(TextureAtlas atlas) {
		tex1 = GameAssets.fetchTextureAtlas("game/images/game_objects.pack").findRegion(buttonString);
		tex2 = GameAssets.fetchTextureAtlas("game/images/game_objects.pack").findRegion(buttonString+"_black");

		sprite1 = new Sprite(tex1);
		sprite2 = new Sprite(tex2);
		sprite1.setSize(tex1.getRegionWidth() * Configuration.VIEWPORT_WIDTH / Configuration.TARGET_WIDTH, tex1.getRegionHeight() * Configuration.VIEWPORT_HEIGHT / Configuration.TARGET_HEIGHT);
		sprite1.setPosition(tx, ty);
		sprite2.setSize(tex2.getRegionWidth() * Configuration.VIEWPORT_WIDTH / Configuration.TARGET_WIDTH, tex2.getRegionHeight() * Configuration.VIEWPORT_HEIGHT / Configuration.TARGET_HEIGHT);
		sprite2.setPosition(tx, ty);
		
		if(up) this.setUp();
		if(left) this.setLeft();
		if(right) this.setRight();
	}
	
	public void dispose() {
		tex1 = null;
		tex2 = null;
		sprite1 = null;
		sprite2 = null;
	}
}
