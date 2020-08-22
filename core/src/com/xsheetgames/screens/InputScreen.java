package com.xsheetgames.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.xsheetgames.Configuration;
import com.xsheetgames.GameAssets;

public class InputScreen extends AbstractScreen {

	private SpriteBatch batch;
	private Sprite screenBackground;
	private Sprite blackLayer, backBtn, moga_pro, moga_pocket, ouya;
	private boolean disposed = false;
	private boolean assetsLoaded;
	private String creditString1 = "use onscreen keys to steer and fire";
	private String creditString2 = "or connect any HID controller...";
	private String inputMethod = "";
	private boolean lastConnectedState;
	
	
	public InputScreen(Game game) {
		this.game = game;
	}
	
	
	@Override
	public void render(float delta) {
		if(this.disposed == false) {
			if(GameAssets.assetsLoaded(batch)) {
				this.inputMethod = GameAssets.nativ.getInputDevice();
				this.batch.getProjectionMatrix().setToOrtho2D(0, 0, Configuration.TARGET_WIDTH, Configuration.TARGET_HEIGHT);
				if(assetsLoaded == false) this.doAssetProcessing();
				batch.begin();
				screenBackground.draw(batch);
				blackLayer.draw(batch);
				
				if(this.inputMethod.equals("keyboard")) {
					GameAssets.glyphLayout.setText(GameAssets.fetchFont("fonts/memory.fnt"), this.creditString1);
					GameAssets.fetchFont("fonts/memory.fnt").draw(batch,this.creditString1, Configuration.TARGET_WIDTH/2 - GameAssets.glyphLayout.width/2, 535f);
					GameAssets.glyphLayout.setText(GameAssets.fetchFont("fonts/memory.fnt"), this.creditString2);
					GameAssets.fetchFont("fonts/memory.fnt").draw(batch,this.creditString2, Configuration.TARGET_WIDTH/2 - GameAssets.glyphLayout.width/2, 460f);
				}
				
				if(this.inputMethod.equals("moga_pro")) {
					moga_pro.draw(batch);
				}
				
				if(this.inputMethod.equals("moga_pocket")) {
					moga_pocket.draw(batch);
				}
				
				if(this.inputMethod.equals("ouya")) {
					ouya.draw(batch);
				}
				
				
				
				backBtn.draw(batch);
				batch.end();
				
				//Emulate Events
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
		
		GameAssets.nativ.trackPageView("/Credits");
		
		lastConnectedState = GameAssets.nativ.isControllerConnected();

		this.assetsLoaded = false;
		this.batch = new SpriteBatch();		
		this.inputMethod = GameAssets.nativ.getInputDevice();
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
			blackLayer=null;
			backBtn=null;
			ouya = null;
			moga_pocket = null;
			moga_pro = null;
			screenBackground = null;
			this.disposed = true;
		}		
	}
	
	
	private void doAssetProcessing() {		
		TextureAtlas atlas = GameAssets.fetchTextureAtlas("menu/images/menu_items.pack");		
		this.backBtn = new Sprite(atlas.findRegion("backbtn"));
		this.backBtn.setSize(atlas.findRegion("backbtn").getRegionWidth(), atlas.findRegion("backbtn").getRegionHeight());
		this.backBtn.setPosition(1000f,80f);		
		
		this.ouya = new Sprite(GameAssets.fetchTexture("menu/images/ouya_controller.png"));
		this.ouya.setSize(GameAssets.fetchTexture("menu/images/ouya_controller.png").getWidth(), GameAssets.fetchTexture("menu/images/ouya_controller.png").getHeight());
		this.ouya.setPosition(175f,80f);
		
		this.moga_pocket = new Sprite(GameAssets.fetchTexture("menu/images/moga_pocket.png"));
		this.moga_pocket.setSize(GameAssets.fetchTexture("menu/images/moga_pocket.png").getWidth(), GameAssets.fetchTexture("menu/images/moga_pocket.png").getHeight());
		this.moga_pocket.setPosition(175f,80f);
		
		this.moga_pro = new Sprite(GameAssets.fetchTexture("menu/images/moga_pro.png"));
		this.moga_pro.setSize(GameAssets.fetchTexture("menu/images/moga_pro.png").getWidth(), GameAssets.fetchTexture("menu/images/moga_pro.png").getHeight());
		this.moga_pro.setPosition(175f,80f);
		
		this.assetsLoaded = true;
	}


	@Override
	public void stepBack(String source) {
		if(GameAssets.buttonTimer <= 0f) {
			this.game.setScreen(new MenuScreen(this.game));
			GameAssets.buttonTimer = 0.35f;
		}
	}


	@Override
	public void startPress() {
		if(GameAssets.buttonTimer <= 0f) {
			this.game.setScreen(new MenuScreen(this.game));
			GameAssets.buttonTimer = 0.35f;
		}
	}


	@Override
	public void primaryPress() {
		if(GameAssets.buttonTimer <= 0f) {
			this.game.setScreen(new MenuScreen(this.game));
			GameAssets.buttonTimer = 0.35f;
		}
	}


	@Override
	public void steerXAxis(float peculiarity) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void steerYAxis(float peculiarity) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public boolean screenTouched(int x, int y, int pointer) {
		Vector2 touchPoint = new Vector2((float)x*Configuration.TARGET_WIDTH/Gdx.graphics.getWidth(), Configuration.TARGET_HEIGHT - ((float)y*Configuration.TARGET_HEIGHT/Gdx.graphics.getHeight()));
		if(backBtn != null && backBtn.getBoundingRectangle().contains(touchPoint.x, touchPoint.y)) {
			GameAssets.playSound(GameAssets.fetchSound("menu/sounds/click.mp3"));
			this.dispose();
			this.game.setScreen(new SettingsScreen(this.game));
			return true;
		}
		return false;
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
