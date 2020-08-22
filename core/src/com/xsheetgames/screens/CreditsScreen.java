package com.xsheetgames.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.xsheetgames.Configuration;
import com.xsheetgames.GameAssets;

public class CreditsScreen extends AbstractScreen {

	private SpriteBatch batch;
	private Sprite screenBackground;
	private Sprite blackLayer, backBtn;
	private boolean disposed = false;
	private boolean assetsLoaded;
	private String creditString1 = "Thank you for playing our game.";	
	private String creditString2 = "Mike and Sebi";
	private String creditString3 = "(Piano by Christoph Richter. Thx!!)";
	private boolean lastConnectedState;
	
	public CreditsScreen(Game game) {
		this.game = game;
	}
	
	
	@Override
	public void render(float delta) {
		if(this.disposed == false) {
			if(GameAssets.assetsLoaded(batch)) {
				this.batch.getProjectionMatrix().setToOrtho2D(0, 0, Configuration.TARGET_WIDTH, Configuration.TARGET_HEIGHT);
				if(assetsLoaded == false) this.doAssetProcessing();
				batch.begin();
				screenBackground.draw(batch);
				blackLayer.draw(batch);
				GameAssets.glyphLayout.setText(GameAssets.fetchFont("fonts/memory.fnt"), this.creditString1);
				GameAssets.fetchFont("fonts/memory.fnt").draw(batch,this.creditString1, Configuration.TARGET_WIDTH/2 - GameAssets.glyphLayout.width/2, 700f);
				GameAssets.fetchFont("fonts/memory.fnt").draw(batch,this.creditString2, 600f, 600f);
				GameAssets.fetchFont("fonts/memory.fnt").draw(batch,"xSheetGames", 600f, 550f);
                GameAssets.glyphLayout.setText(GameAssets.fetchFont("fonts/memory.fnt"), this.creditString3);
				GameAssets.fetchFont("fonts/memory.fnt").draw(batch,this.creditString3, Configuration.TARGET_WIDTH/2 - GameAssets.glyphLayout.width/2, 330f);
				
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
		this.assetsLoaded = false;
		this.batch = new SpriteBatch();
		
		lastConnectedState = GameAssets.nativ.isControllerConnected();
		
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
			screenBackground = null;
			this.disposed = true;
		}		
	}
	
	
	private void doAssetProcessing() {		
		TextureAtlas atlas = GameAssets.fetchTextureAtlas("menu/images/menu_items.pack");		
		this.backBtn = new Sprite(atlas.findRegion("backbtn"));
		this.backBtn.setSize(atlas.findRegion("backbtn").getRegionWidth(), atlas.findRegion("backbtn").getRegionHeight());
		this.backBtn.setPosition(1000f,80f);		
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
