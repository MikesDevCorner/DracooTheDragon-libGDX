package com.xsheetgames.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.xsheetgames.Configuration;
import com.xsheetgames.GameAssets;
import com.xsheetgames.genericElements.AbstractLevelpack;
import com.xsheetgames.genericElements.CoverLayout;

public class DeadScreen extends AbstractScreen {

	private SpriteBatch batch;
	private boolean keepGameScreen = false;
	private Sprite screenBackground;
	private Sprite yes;
	private Sprite no;
	private Sprite deadText;
	private boolean disposed = false;
	private boolean soundPlayed = false;
	private boolean sound2Played = false;
	private float sound2Counter = 0.5f;
	private Sprite blackLayer;
	private GameScreen gameScreen;
	private boolean adShowed;
	public static int DeadScreenCounter = 0;
	private boolean lastConnectedState;
	
	public DeadScreen(Game game, GameScreen gameScreen) {
		this.game = game;
		this.gameScreen = gameScreen;
	}
	
	
	@Override
	public void render(float delta) {
		if(this.disposed == false) {
			if(GameAssets.assetsLoaded(batch)) {
				
				if(soundPlayed == false) {
					GameAssets.playSound(GameAssets.fetchSound("game/sounds/dead.mp3"),0.4f);
					GameAssets.vibrate(800);
					soundPlayed = true;
				}
				this.sound2Counter -= delta;
				if(sound2Played == false && sound2Counter <= 0f) {
					if(DeadScreen.DeadScreenCounter % 8 == 0) {
						GameAssets.playSound(GameAssets.fetchSound("game/sounds/dead2.mp3"),2f);
					} else if(DeadScreen.DeadScreenCounter % 8 == 4) {
						GameAssets.playSound(GameAssets.fetchSound("game/sounds/dead3.mp3"),2f);
					} else {
						GameAssets.playSound(GameAssets.fetchSound("game/sounds/dead1.mp3"),2f);
					}
					this.sound2Played = true;				
				}
				this.beginScreenPass(batch);
				CoverLayout.apply(screenBackground, screenBackground.getRegionWidth(), screenBackground.getRegionHeight(), Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
				this.blackLayer.setSize(Gdx.graphics.getWidth()+10f, Gdx.graphics.getHeight()+10f);
				this.blackLayer.setPosition(-5f, -5f);
				batch.begin();
				batch.disableBlending();
				screenBackground.draw(batch);
				batch.enableBlending();
				blackLayer.draw(batch);
				batch.end();

				this.beginUiPass(batch);
				batch.begin();
				yes.draw(batch);
				no.draw(batch);
				deadText.draw(batch);
				batch.end();
				this.endScreenRender();

				//Emulate Events
				if(GameAssets.buttonTimer > 0f) GameAssets.buttonTimer-=delta;
}
		}
	}

	@Override
	public void resize(int width, int height) {
		this.updateUiViewport(width, height);
	}

	@Override
	public void show() {
		
		
		this.adShowed = true;

		if(this.adShowed == true) {
			
			lastConnectedState = GameAssets.input.isControllerConnected();
		
			DeadScreen.DeadScreenCounter++;
			
			this.batch = new SpriteBatch();
			this.setupUiViewport();
			this.screenBackground = new Sprite(GameAssets.fetchTexture("game/images/background.jpg"));

			this.yes = new Sprite(GameAssets.fetchTextureAtlas("game/images/game_objects.pack").findRegion("retry"));
			this.yes.setSize(GameAssets.fetchTextureAtlas("game/images/game_objects.pack").findRegion("retry").getRegionWidth(), GameAssets.fetchTextureAtlas("game/images/game_objects.pack").findRegion("retry").getRegionHeight());
			this.yes.setPosition(Configuration.TARGET_WIDTH/2+10f, 250f);
			
			this.no = new Sprite(GameAssets.fetchTextureAtlas("game/images/game_objects.pack").findRegion("menu"));
			this.no.setSize(GameAssets.fetchTextureAtlas("game/images/game_objects.pack").findRegion("menu").getRegionWidth(), GameAssets.fetchTextureAtlas("game/images/game_objects.pack").findRegion("menu").getRegionHeight());
			this.no.setPosition(Configuration.TARGET_WIDTH/2-this.no.getWidth()-10f , 250f);
			
			this.deadText = new Sprite(GameAssets.fetchTextureAtlas("game/images/game_objects.pack").findRegion("game_over_screen"));
			this.deadText.setSize(GameAssets.fetchTextureAtlas("game/images/game_objects.pack").findRegion("game_over_screen").getRegionWidth(), GameAssets.fetchTextureAtlas("game/images/game_objects.pack").findRegion("game_over_screen").getRegionHeight());
			this.deadText.setPosition(Configuration.TARGET_WIDTH/2 - this.deadText.getWidth()/2 , 400f);
			
			this.blackLayer = new Sprite(GameAssets.fetchTextureAtlas("game/images/game_objects.pack").findRegion("blackLayer"));
		}
	}

	@Override
	public void hide() {
				
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
			if(batch != null) batch.dispose();
			batch = null;
			screenBackground = null;
			blackLayer = null;
			this.yes = null;
			this.no = null;
			if(keepGameScreen == false && this.gameScreen != null) this.gameScreen.dispose();
			this.disposed = true;
		}		
	}

	
	public void returnToMainMenu() {
		AbstractLevelpack tmpPack = this.gameScreen.getLevelpack();
		this.gameScreen.dispose();
		this.dispose();
		try {
			GameAssets.unloadGameAssets();
			tmpPack.unloadAssets();
		} catch(Exception e) {}
		tmpPack = null;
		GameAssets.manager.clear();
		GameAssets.manager.finishLoading();
		GameAssets.loadMenuAssets();
		this.game.setScreen(new MenuScreen(this.game));
	}


	@Override
	public void stepBack(String source) {
		if(GameAssets.buttonTimer <= 0f) {
			this.returnToMainMenu();
			GameAssets.buttonTimer = 0.35f;
		}
	}


	@Override
	public void startPress() {
		if(GameAssets.buttonTimer <= 0f) {
			this.keepGameScreen = true;
			this.dispose();
			this.gameScreen.retryLevel();
			this.game.setScreen(this.gameScreen);
			GameAssets.buttonTimer = 0.35f;
		}
	}


	@Override
	public void primaryPress() {
		if(GameAssets.buttonTimer <= 0f) {
			this.keepGameScreen = true;
			this.dispose();
			this.gameScreen.retryLevel();
			this.game.setScreen(this.gameScreen);
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
		try {
			Vector2 touchPoint = this.unprojectUi(x, y);

			if(this.yes.getBoundingRectangle().contains(touchPoint.x, touchPoint.y)) {
				this.keepGameScreen = true;
				this.dispose();
				this.gameScreen.retryLevel();
				this.game.setScreen(this.gameScreen);
				return true;
			}
			if(this.no.getBoundingRectangle().contains(touchPoint.x, touchPoint.y)) {
				this.returnToMainMenu();
				return true;
			}
		} catch(Exception e) {
			
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
