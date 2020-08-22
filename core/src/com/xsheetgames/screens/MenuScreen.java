package com.xsheetgames.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.xsheetgames.Configuration;
import com.xsheetgames.GameAssets;

public class MenuScreen extends AbstractScreen implements iAdAble {

	private SpriteBatch batch;
	private Sprite screenBackground, playButton, rate, share, settings, lite, more;
	private boolean assetsLoaded;
	private boolean disposed = false;
	private boolean endApp = false;
	private static boolean adShowed;
	private boolean lastConnectedState;
	
	
	public MenuScreen(Game game) {
		this.game = game;
	}
	
	int rendercount = 0;
	
	@Override
	public void render(float delta) {
		
		if(this.disposed == false) {
			if(GameAssets.assetsLoaded(batch)) {				
				if(assetsLoaded == false) this.doAssetProcessing();
				
				this.batch.getProjectionMatrix().setToOrtho2D(0, 0, Configuration.TARGET_WIDTH, Configuration.TARGET_HEIGHT);
				this.batch.begin();
				batch.disableBlending();
				this.screenBackground.draw(batch);
				batch.enableBlending();
				this.playButton.draw(batch);
				//if(Configuration.fullVersion == false) this.lite.draw(batch);
				this.settings.draw(batch);
				
				this.rate.draw(batch);
				this.share.draw(batch);
				if(this.more != null) this.more.draw(batch);
				
				
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
				
				
				
				this.batch.end();
			}
		}
		
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void show() {
		lastConnectedState = GameAssets.nativ.isControllerConnected();
		
		if(MenuScreen.adShowed == false) {
			MenuScreen.adShowed = true;
		}
		
		
		if(MenuScreen.adShowed == true) {
			if(this.endApp == true) {
				GameAssets.nativ.trackPageView("/Exit");
				Gdx.app.exit();
			}
			
			GameAssets.nativ.trackPageView("/MenuScreen");
			
			this.batch = new SpriteBatch();
			this.assetsLoaded = false;
		}
	}

	@Override
	public void hide() {
		//this.dispose();		
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		GameAssets.loadMenuAssets();
		this.assetsLoaded = false;
	}

	@Override
	public void dispose() {
		if(this.disposed == false) {
			if(this.batch != null) this.batch.dispose();
			this.playButton = null;
			this.lite = null;
			this.more = null;
			this.settings = null;
			this.share = null;
			this.rate = null;
			this.screenBackground = null;
			this.disposed = true;
		}		
	}
	
	@SuppressWarnings("unused")
	private void doAssetProcessing() {
		this.assetsLoaded = true;
		if(this.screenBackground == null) {
			this.screenBackground = new Sprite(GameAssets.fetchTexture("menu/images/menuScreen.jpg"));
			this.screenBackground.setSize(GameAssets.fetchTexture("menu/images/menuScreen.jpg").getWidth(), GameAssets.fetchTexture("menu/images/menuScreen.jpg").getHeight());
		}
		if(this.playButton == null) {
			
			this.playButton = new Sprite(GameAssets.fetchTextureAtlas("menu/images/menu_items.pack").findRegion("playbtn"));
			this.playButton.setSize(GameAssets.fetchTextureAtlas("menu/images/menu_items.pack").findRegion("playbtn").getRegionWidth(), GameAssets.fetchTextureAtlas("menu/images/menu_items.pack").findRegion("playbtn").getRegionHeight());
			this.playButton.setPosition(965f,290f);
		}
		
		if(Configuration.fullVersion == false && this.lite == null) {
			this.lite = new Sprite(GameAssets.fetchTextureAtlas("menu/images/menu_items.pack").findRegion("lite"));
			this.lite.setSize(GameAssets.fetchTextureAtlas("menu/images/menu_items.pack").findRegion("lite").getRegionWidth(), GameAssets.fetchTextureAtlas("menu/images/menu_items.pack").findRegion("lite").getRegionHeight());
			this.lite.setPosition(440f,505f);
		}
		
		if(this.settings == null) {
			this.settings = new Sprite(GameAssets.fetchTextureAtlas("menu/images/menu_items.pack").findRegion("settings"));
			this.settings.setSize(GameAssets.fetchTextureAtlas("menu/images/menu_items.pack").findRegion("settings").getRegionWidth(), GameAssets.fetchTextureAtlas("menu/images/menu_items.pack").findRegion("settings").getRegionHeight());
			this.settings.setPosition(1030f,130f);
		}
		
		if(this.rate == null)
		{			
			TextureAtlas atlas = GameAssets.fetchTextureAtlas("menu/images/menu_items.pack");
			
			this.rate = new Sprite(atlas.findRegion("rate"));
			this.rate.setSize(atlas.findRegion("rate").getRegionWidth(), atlas.findRegion("rate").getRegionHeight());
			this.rate.setPosition(190f,43f);
			
			this.share = new Sprite(atlas.findRegion("share"));
			this.share.setSize(atlas.findRegion("share").getRegionWidth(), atlas.findRegion("share").getRegionHeight());
			this.share.setPosition(190f, 115f);
			
			
			if(false) {
				this.rate.setPosition(20f,43f);
				this.share.setPosition(20f, 115f);
				
				this.more = new Sprite(atlas.findRegion("more"));
				this.more.setSize(atlas.findRegion("share").getRegionWidth(), atlas.findRegion("share").getRegionHeight());
				this.more.setPosition(20f, 187f);
			}
			
		}
		if(GameAssets.fetchMusic("menu/music/tribute.mp3").isPlaying() == false && Configuration.musicEnabled == true) {
			GameAssets.fetchMusic("menu/music/tribute.mp3").stop();
			GameAssets.playMusic(GameAssets.fetchMusic("menu/music/tribute.mp3"), true, 0.5f);
		}
	}
	
	
	public void shareButtonPressed() {		
		GameAssets.nativ.sendEvent("SocialAction", "Share Button", "pressed", 1);
		GameAssets.nativ.share("Dracoo","I am currently playing <a href=http://facebook.com/dracoo>Dracoo</a> for Android. Love this game! Cannot stop playing it.");
	}
	
	public void rateButtonPressed() {
		GameAssets.nativ.sendEvent("SocialAction", "Rate Button", "pressed", 1);
		GameAssets.nativ.rate();
	}
	
	public void moreButtonPressed() {
		GameAssets.nativ.sendEvent("SocialAction", "More Button", "pressed", 1);
		GameAssets.nativ.more();
	}

	
	private void touchedEvent(Vector2 touchPoint) {
		Gdx.app.log("Progress", "touched event");
		if(playButton != null && playButton.getBoundingRectangle().contains(touchPoint.x, touchPoint.y)) {
			this.dispose();
			GameAssets.playSound(GameAssets.fetchSound("menu/sounds/click.mp3"));
			ChooseLevelpackScreen cls = new ChooseLevelpackScreen(this.game, 0);
			this.game.setScreen(cls);
		}
		
		if(settings != null && settings.getBoundingRectangle().contains(touchPoint.x, touchPoint.y)) {
			this.dispose();
			GameAssets.playSound(GameAssets.fetchSound("menu/sounds/click.mp3"));
			this.game.setScreen(new SettingsScreen(this.game));
		}
	
		if(share != null && share.getBoundingRectangle().contains(touchPoint.x, touchPoint.y)) {
			GameAssets.playSound(GameAssets.fetchSound("menu/sounds/click.mp3"));
			this.shareButtonPressed();
		}
		if(rate != null && rate.getBoundingRectangle().contains(touchPoint.x, touchPoint.y)) {
			GameAssets.playSound(GameAssets.fetchSound("menu/sounds/click.mp3"));
			this.rateButtonPressed();
		}
		if(more != null && more.getBoundingRectangle().contains(touchPoint.x, touchPoint.y)) {
			GameAssets.playSound(GameAssets.fetchSound("menu/sounds/click.mp3"));
			this.moreButtonPressed();
		}
	}



	@Override
	public void setAdShowed(boolean adShowed) {
		MenuScreen.adShowed = adShowed;		
	}


	@Override
	public void stepBack(String source) {
		if(GameAssets.buttonTimer <= 0f) {
			this.endApp = true;			
			GameAssets.nativ.trackPageView("/Exit");
			Gdx.app.exit();
			GameAssets.buttonTimer = 0.35f;
		}
	}


	@Override
	public void startPress() {
		if(GameAssets.buttonTimer <= 0f) {
			if(this.disposed == false && this.assetsLoaded == true) {
				this.dispose();
				GameAssets.playSound(GameAssets.fetchSound("menu/sounds/click.mp3"));
				ChooseLevelpackScreen cls = new ChooseLevelpackScreen(this.game, 0);
				this.game.setScreen(cls);
			}
			GameAssets.buttonTimer = 0.35f;
		}
	}


	@Override
	public void primaryPress() {
		if(GameAssets.buttonTimer <= 0f) {
			if(this.disposed == false && this.assetsLoaded == true) {
				this.dispose();
				GameAssets.playSound(GameAssets.fetchSound("menu/sounds/click.mp3"));
				ChooseLevelpackScreen cls = new ChooseLevelpackScreen(this.game, 0);
				this.game.setScreen(cls);
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
		Gdx.app.log("Progress", "touched");
		Vector2 touchPoint = new Vector2((float)x*Configuration.TARGET_WIDTH/Gdx.graphics.getWidth(), Configuration.TARGET_HEIGHT - ((float)y*Configuration.TARGET_HEIGHT/Gdx.graphics.getHeight()));
		this.touchedEvent(touchPoint);
		return true;
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
