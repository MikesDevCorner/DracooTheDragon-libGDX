package com.xsheetgames.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.xsheetgames.Configuration;
import com.xsheetgames.GameAssets;
import com.xsheetgames.genericElements.AbstractLevelpack;

public class PreBossScreen extends AbstractScreen {

	private SpriteBatch batch;
	private Sprite screenBackground;
	private Sprite blackLayer;
	private boolean disposed = false;
	private boolean readyForBoss;
	
	private String text1 = "Prepare yourself.";	
	private String text2 = "You will now be introduced";
	private String text21 = "to the mighty boss from this region...";
	
	private String text3 = "Sorry, you have to collect at least 24 eggs";
	private String text4 = "in this region to face the mighty boss";
	
	private GameScreen gameScreen;
	private boolean lastConnectedState;
	
	
	public PreBossScreen(Game game, GameScreen gameScreen) {
		this.game = game;
		this.gameScreen = gameScreen;
	}
	
	
	@Override
	public void render(float delta) {
		if(this.disposed == false) {
			if(GameAssets.assetsLoaded(batch)) {
				this.batch.getProjectionMatrix().setToOrtho2D(0, 0, Configuration.TARGET_WIDTH, Configuration.TARGET_HEIGHT);
				batch.begin();
				screenBackground.draw(batch);
				blackLayer.draw(batch);
				
				if(this.readyForBoss == true) {
					GameAssets.glyphLayout.setText(GameAssets.fetchFont("fonts/memory.fnt"), this.text1);
					GameAssets.fetchFont("fonts/memory.fnt").draw(batch,this.text1, Configuration.TARGET_WIDTH/2 - GameAssets.glyphLayout.width/2, 495f);
					GameAssets.glyphLayout.setText(GameAssets.fetchFont("fonts/memory.fnt"), this.text2);
					GameAssets.fetchFont("fonts/memory.fnt").draw(batch,this.text2, Configuration.TARGET_WIDTH/2 - GameAssets.glyphLayout.width/2, 435f);
					GameAssets.glyphLayout.setText(GameAssets.fetchFont("fonts/memory.fnt"), this.text21);
					GameAssets.fetchFont("fonts/memory.fnt").draw(batch,this.text21, Configuration.TARGET_WIDTH/2 - GameAssets.glyphLayout.width/2, 365f);
				} else {
					GameAssets.glyphLayout.setText(GameAssets.fetchFont("fonts/memory.fnt"), this.text3);
					GameAssets.fetchFont("fonts/memory.fnt").draw(batch,this.text3, Configuration.TARGET_WIDTH/2 - GameAssets.glyphLayout.width/2, 495f);
					GameAssets.glyphLayout.setText(GameAssets.fetchFont("fonts/memory.fnt"), this.text4);
					GameAssets.fetchFont("fonts/memory.fnt").draw(batch,this.text4, Configuration.TARGET_WIDTH/2 - GameAssets.glyphLayout.width/2, 435f);
				}
				
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
		
		GameAssets.nativ.trackPageView("/PreBossScreen");
		
		lastConnectedState = GameAssets.nativ.isControllerConnected();
		
		this.batch = new SpriteBatch();
		this.readyForBoss = false;
		this.readyForBoss = (this.gameScreen.getLevelpack().getReachedEggs() >= 24);
		
		this.screenBackground = new Sprite(GameAssets.fetchTexture("game/images/background.jpg"));
		screenBackground.setSize(GameAssets.fetchTexture("game/images/background.jpg").getWidth(), GameAssets.fetchTexture("game/images/background.jpg").getHeight());
		this.blackLayer = new Sprite(GameAssets.fetchTextureAtlas("game/images/game_objects.pack").findRegion("blackLayer"));
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
			screenBackground = null;
			this.disposed = true;
		}		
	}



	@Override
	public void stepBack(String source) {
		if(GameAssets.buttonTimer <= 0f) {
			AbstractLevelpack tmpPack = this.gameScreen.getLevelpack();
			this.dispose();
			this.gameScreen.dispose();
			GameAssets.unloadGameAssets();
			tmpPack.unloadAssets();
			tmpPack = null;
			GameAssets.manager.clear();
			GameAssets.manager.finishLoading();
			GameAssets.loadMenuAssets();
			this.game.setScreen(new MenuScreen(this.game));
			GameAssets.buttonTimer = 0.35f;
		}
	}


	@Override
	public void startPress() {
		if(GameAssets.buttonTimer <= 0f) {
			if(this.readyForBoss == false) {
				AbstractLevelpack tmpPack = this.gameScreen.getLevelpack();
				this.dispose();
				this.gameScreen.dispose();
				GameAssets.unloadGameAssets();
				tmpPack.unloadAssets();
				tmpPack = null;
				GameAssets.manager.clear();
				GameAssets.manager.finishLoading();
				GameAssets.loadMenuAssets();
				this.game.setScreen(new MenuScreen(this.game));
			} else {
				this.game.setScreen(this.gameScreen);
			}
			GameAssets.buttonTimer = 0.35f;
		}
	}


	@Override
	public void primaryPress() {
		if(GameAssets.buttonTimer <= 0f) {
			if(this.readyForBoss == false) {
				AbstractLevelpack tmpPack = this.gameScreen.getLevelpack();
				this.dispose();
				this.gameScreen.dispose();
				GameAssets.unloadGameAssets();
				tmpPack.unloadAssets();
				tmpPack = null;
				GameAssets.manager.clear();
				GameAssets.manager.finishLoading();
				GameAssets.loadMenuAssets();
				this.game.setScreen(new MenuScreen(this.game));
			} else {
				this.game.setScreen(this.gameScreen);
			}
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
		if(this.readyForBoss == false) {
			AbstractLevelpack tmpPack = this.gameScreen.getLevelpack();
			this.dispose();
			this.gameScreen.dispose();
			GameAssets.unloadGameAssets();
			tmpPack.unloadAssets();
			tmpPack = null;
			GameAssets.manager.clear();
			GameAssets.manager.finishLoading();
			GameAssets.loadMenuAssets();
			this.game.setScreen(new MenuScreen(this.game));
			return true;
		} else {
			this.game.setScreen(this.gameScreen);
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
