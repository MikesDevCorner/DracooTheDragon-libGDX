package com.xsheetgames.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.xsheetgames.Configuration;
import com.xsheetgames.GameAssets;
import com.xsheetgames.genericElements.AbstractLevelpack;
import com.xsheetgames.levelpacks.batmine.BatMineLevelPack;
import com.xsheetgames.levelpacks.jungle.JungleLevelPack;

public class ChooseLevelpackScreen extends AbstractScreen{

	private SpriteBatch batch;
	private Sprite screenBackground;
	private Sprite blackLayer, backBtn, levelPackBtn, leftButton, rightButton, tmpSprite;
	private int levelPackIndex;
	private Array<Sprite> allLevelPacks;
	private boolean disposed = false;
	private boolean assetsLoaded;
	private String levelPackHeading = "Choose Levelpack";
	private boolean lastConnectedState;
	private float xAxisCount = 0f;
	
	public ChooseLevelpackScreen(Game game, int levelPackIndex) {
		this.game = game;
		this.levelPackIndex = levelPackIndex;
	}
	
	
	@Override
	public void render(float delta) {if(GameAssets.assetsLoaded(batch)) {
		if(this.disposed == false) {
			this.batch.getProjectionMatrix().setToOrtho2D(0, 0, Configuration.TARGET_WIDTH, Configuration.TARGET_HEIGHT);
			if(assetsLoaded == false) this.doAssetProcessing();
				batch.begin();
				screenBackground.draw(batch);
				blackLayer.draw(batch);
				backBtn.draw(batch);
				levelPackBtn.draw(batch);
				if(this.levelPackIndex > 0) leftButton.draw(batch);
				if(this.levelPackIndex < this.allLevelPacks.size-1) rightButton.draw(batch);
				GameAssets.glyphLayout.setText(GameAssets.fetchFont("fonts/memory.fnt"), levelPackHeading);
				GameAssets.fetchFont("fonts/memory.fnt").draw(batch,levelPackHeading, Configuration.TARGET_WIDTH/2 - GameAssets.glyphLayout.width/2, 680f);
				batch.end();
				
				//Emulate Events
				if(this.xAxisCount > 0f) this.xAxisCount-=delta;
				if(GameAssets.buttonTimer > 0f) GameAssets.buttonTimer-=delta;
				if(GameAssets.nativ.getInputDevice().toLowerCase().contains("moga")) {
					if(GameAssets.nativ.pollControllerButtonState(GameAssets.KEY_START) == true) {
						this.startPress();
					}
					if(GameAssets.nativ.pollControllerButtonState(GameAssets.KEY_PRIMARY) == true) {
						this.primaryPress();
					}
					if(GameAssets.nativ.pollControllerButtonState(GameAssets.KEY_BACK) == true) {
						this.stepBack("moga");
					}
					if(GameAssets.nativ.pollControllerAxis(GameAssets.AXIS_X) > 0.06) {
						this.steerXAxis(1f);
					}
					if(GameAssets.nativ.pollControllerAxis(GameAssets.AXIS_X) < -0.06) {
						this.steerXAxis(-1f);
					}
					if(GameAssets.nativ.isControllerConnected() == true && this.lastConnectedState == false) {
						GameAssets.nativ.showMessage("Controller", "Moga Controller connected");
					}
					if(GameAssets.nativ.isControllerConnected() == false && this.lastConnectedState == true) {
						GameAssets.nativ.showMessage("Controller", "Moga Controller disconnected");
					}
					lastConnectedState = GameAssets.nativ.isControllerConnected();
				}
				
			}
		}
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
	}

	@Override
	public void show() {
		
		GameAssets.nativ.trackPageView("/ChooseLevelpackScreen");
		
		lastConnectedState = GameAssets.nativ.isControllerConnected();
		
		this.assetsLoaded = false;
		this.batch = new SpriteBatch();
		
		this.screenBackground = new Sprite(GameAssets.fetchTexture("menu/images/credits_back.jpg"));
		screenBackground.setSize(GameAssets.fetchTexture("menu/images/credits_back.jpg").getWidth(), GameAssets.fetchTexture("menu/images/credits_back.jpg").getHeight());
		this.blackLayer = new Sprite(GameAssets.fetchTextureAtlas("menu/images/menu_items.pack").findRegion("blackLayer"));
		this.blackLayer.setSize(1280f+10f,800f+10f);
		this.blackLayer.setPosition(-5f, -5f);
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
	}

	@Override
	public void dispose() {
		if(this.disposed == false) {
			batch.dispose();
			batch = null;
			screenBackground = null;
			blackLayer = null;
			backBtn = null;
			levelPackBtn = null;
			leftButton = null;
			rightButton = null;
			allLevelPacks.clear();
			allLevelPacks = null;
			this.disposed = true;
		}
	}
	
	private void doAssetProcessing() {
		TextureAtlas atlas = GameAssets.fetchTextureAtlas("menu/images/menu_items.pack");		
		this.backBtn = new Sprite(atlas.findRegion("backbtn"));
		this.backBtn.setSize(atlas.findRegion("backbtn").getRegionWidth(), atlas.findRegion("backbtn").getRegionHeight());
		this.backBtn.setPosition(Configuration.TARGET_WIDTH-backBtn.getWidth()-50f,50f);
		
		this.leftButton = new Sprite(atlas.findRegion("btn_left"));
		this.leftButton.setSize(atlas.findRegion("btn_left").getRegionWidth(), atlas.findRegion("btn_left").getRegionHeight());
		this.leftButton.setPosition(220f,290f);
		
		this.rightButton = new Sprite(atlas.findRegion("btn_right"));
		this.rightButton.setSize(atlas.findRegion("btn_right").getRegionWidth(), atlas.findRegion("btn_right").getRegionHeight());
		this.rightButton.setPosition(Configuration.TARGET_WIDTH-leftButton.getWidth()-220f,290f);
		
		this.allLevelPacks = new Array<Sprite>();
		
		this.tmpSprite = new Sprite(atlas.findRegion("batmine_packpic"));
		this.tmpSprite.setSize(atlas.findRegion("batmine_packpic").getRegionWidth(), atlas.findRegion("batmine_packpic").getRegionHeight());
		this.tmpSprite.setPosition(Configuration.TARGET_WIDTH/2-208f,240f);
		this.allLevelPacks.add(this.tmpSprite);
		
		this.tmpSprite = new Sprite(atlas.findRegion("jungle_packpic"));
		this.tmpSprite.setSize(atlas.findRegion("jungle_packpic").getRegionWidth(), atlas.findRegion("jungle_packpic").getRegionHeight());
		this.tmpSprite.setPosition(Configuration.TARGET_WIDTH/2-208f,240f);
		this.allLevelPacks.add(this.tmpSprite);
		
		this.levelPackBtn = this.allLevelPacks.get(this.levelPackIndex);
		
		this.assetsLoaded = true;
	}
	
	
	private void activateLeft() {
		if(this.levelPackIndex > 0) {
			GameAssets.playSound(GameAssets.fetchSound("menu/sounds/click.mp3"));
			this.levelPackIndex--;
			this.levelPackBtn = this.allLevelPacks.get(this.levelPackIndex);
		} else {
			GameAssets.playSound(GameAssets.fetchSound("menu/sounds/click2.mp3"));
		}
	}
	
	private void activateRight() {
		if(this.levelPackIndex < (this.allLevelPacks.size-1)) {
			GameAssets.playSound(GameAssets.fetchSound("menu/sounds/click.mp3"));
			this.levelPackIndex++;
			this.levelPackBtn = this.allLevelPacks.get(this.levelPackIndex);
		} else {
			GameAssets.playSound(GameAssets.fetchSound("menu/sounds/click2.mp3"));
		}
	}
	
	private void activateLevelpackButton() {
		GameAssets.playSound(GameAssets.fetchSound("menu/sounds/click.mp3"));
		ChooseLevelScreen chooseLevelScreen = new ChooseLevelScreen(this.game, this.levelPackIndex);
		AbstractLevelpack actualLevelPack = null;
		
		//ERWEITERN WENN NEUE LEVELPACKS ANFALLEN:
		if(this.levelPackIndex == 0) actualLevelPack = new BatMineLevelPack();
		if(this.levelPackIndex == 1) actualLevelPack = new JungleLevelPack();
		
		this.dispose();
		chooseLevelScreen.setLevelPack(actualLevelPack);
		this.game.setScreen(chooseLevelScreen);
	}
	

	
	@Override
	public boolean screenTouched(int x, int y, int pointer) {
		try {
			Vector2 touchPoint = new Vector2((float)x*Configuration.TARGET_WIDTH/Gdx.graphics.getWidth(), Configuration.TARGET_HEIGHT - ((float)y*Configuration.TARGET_HEIGHT/Gdx.graphics.getHeight()));
			if(backBtn != null && backBtn.getBoundingRectangle().contains(touchPoint.x, touchPoint.y)) {
				GameAssets.playSound(GameAssets.fetchSound("menu/sounds/click.mp3"));
				this.dispose();
				this.game.setScreen(new MenuScreen(this.game));
				return true;
			}
			
			if(levelPackBtn != null && levelPackBtn.getBoundingRectangle().contains(touchPoint.x, touchPoint.y)) {
				this.activateLevelpackButton();
				return true;
			}
			
			if(leftButton != null && leftButton.getBoundingRectangle().contains(touchPoint.x, touchPoint.y)) {
				this.activateLeft();
				return true;
			}
			
			if(rightButton != null && rightButton.getBoundingRectangle().contains(touchPoint.x, touchPoint.y)) {
				this.activateRight();
				return true;
			}
		} catch(Exception e) {
			
		}
		return false;	
	}


	@Override
	public void stepBack(String source) {
		if(GameAssets.buttonTimer <= 0f) {
			this.dispose();
			this.game.setScreen(new MenuScreen(this.game));
			GameAssets.buttonTimer = 0.35f;
		}
	}


	@Override
	public void startPress() {
		if(GameAssets.buttonTimer <= 0f) {
			this.activateLevelpackButton();	
			GameAssets.buttonTimer = 0.35f;
		}
	}


	@Override
	public void primaryPress() {
		if(GameAssets.buttonTimer <= 0f) {
			this.activateLevelpackButton();
			GameAssets.buttonTimer = 0.35f;
		}
	}


	@Override
	public void steerXAxis(float peculiarity) {			
		if(this.xAxisCount <= 0f) {
			if(peculiarity > 0f) this.activateRight();
			else if(peculiarity < 0f) this.activateLeft();
			this.xAxisCount = 0.4f;
		}
	}



	@Override
	public void steerYAxis(float peculiarity) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public boolean screenAfterTouched(int x, int y, int pointer) {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public boolean screenWhileTouch(int x, int y, int pointer) {
		// TODO Auto-generated method stub
		return false;
	}

}