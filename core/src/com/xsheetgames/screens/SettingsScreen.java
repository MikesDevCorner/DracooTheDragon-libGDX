package com.xsheetgames.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.xsheetgames.Configuration;
import com.xsheetgames.GameAssets;

public class SettingsScreen extends AbstractScreen {

	private SpriteBatch batch;
	private Sprite screenBackground, credits, control1, control2, control3, music, sound, vibrate, autoFire, autoFireOff, sound_off, vibrate_off, music_off;
	private Sprite blackLayer, backBtn;
	private boolean disposed = false;
	private boolean assetsLoaded;
	
	private String audioOnString = "Music is: ON";
	private String audioOffString = "Music is: OFF";
	
	private String effectsOnString = "Sound-Effects are: ON";
	private String effectsOffString = "Sound-Effects are: OFF";
	
	private String vibrateOnString = "Device-Vibration is: ON";
	private String vibrateOffString = "Device-Vibration is: OFF";
	
	private String control1String = "show controls";
	//private String control2String = "Input: Short-Swipe-Commands";

	private String autofireOnString = "Auto fire is: ON";
	private String autofireOffString = "Auto fire is: OFF";	
	
	private String creditsString = "show Credits";
	
	private boolean lastConnectedState;	
	
	public SettingsScreen(Game game) {
		this.game = game;
	}
	
	
	@Override
	public void render(float delta) {
		
		if(this.disposed == false) {
			if(GameAssets.assetsLoaded(batch)) {			
			
				if(assetsLoaded == false) this.doAssetProcessing();
				
				this.batch.getProjectionMatrix().setToOrtho2D(0, 0, Configuration.TARGET_WIDTH, Configuration.TARGET_HEIGHT);
				batch.begin();
				screenBackground.draw(batch);
				blackLayer.draw(batch);
	
				if(Configuration.musicEnabled == true) {
					this.music.draw(batch);
					GameAssets.fetchFont("fonts/memory.fnt").draw(batch,this.audioOnString, 328f, 650f+110f);
				}
				else {
					this.music_off.draw(batch);
					GameAssets.fetchFont("fonts/memory.fnt").draw(batch,this.audioOffString, 328f, 650f+110f);
				}
				
				if(Configuration.soundEnabled == true) {
					this.sound.draw(batch);
					GameAssets.fetchFont("fonts/memory.fnt").draw(batch,this.effectsOnString, 328f, 530f+110f);
				}
				else {
					this.sound_off.draw(batch);
					GameAssets.fetchFont("fonts/memory.fnt").draw(batch,this.effectsOffString, 328f, 530f+110f);
				}
				
				if(Configuration.vibrateEnabled == true) {
					this.vibrate.draw(batch);
					GameAssets.fetchFont("fonts/memory.fnt").draw(batch,this.vibrateOnString, 328f, 410f+110f);
				}
				else {
					this.vibrate_off.draw(batch);
					GameAssets.fetchFont("fonts/memory.fnt").draw(batch,this.vibrateOffString, 328f, 410f+110f);
				}
				
				if(Configuration.inputType == 1) {
					this.control1.draw(batch);
					GameAssets.fetchFont("fonts/memory.fnt").draw(batch,this.control1String, 328f, 290f+110f);
				}
				/*if(Configuration.inputType == 2) {
					this.control2.draw(batch);
					GameAssets.fetchFont("fonts/memory.fnt").draw(batch,this.control2String, 328f, 290f+110f);
				}*/
				
				if(Configuration.autoFire == true) {
					this.autoFire.draw(batch);
					GameAssets.fetchFont("fonts/memory.fnt").draw(batch,this.autofireOnString, 328f, 170f+110f);
				}
				else {
					this.autoFireOff.draw(batch);
					GameAssets.fetchFont("fonts/memory.fnt").draw(batch,this.autofireOffString, 328f, 170f+110f);
				}
				
				this.credits.draw(batch);
				GameAssets.fetchFont("fonts/memory.fnt").draw(batch,this.creditsString, 328f, 50f+110f);
				
				
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
		
		GameAssets.nativ.trackPageView("/SettingsScreen");
		GameAssets.nativ.closeBannerAd();
		
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
		GameAssets.nativ.showBannerAd();
		
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
			screenBackground=null;
			credits=null;
			control1=null;
			control2=null;
			control3=null;
			music=null;
			sound=null;
			vibrate=null;
			autoFire=null;
			autoFireOff=null;
			sound_off=null;
			vibrate_off=null;
			music_off=null;
			blackLayer=null;
			backBtn=null;
			screenBackground = null;
			this.disposed = true;
		}
	}

	
	private void doAssetProcessing() {
		
		TextureAtlas atlas = GameAssets.fetchTextureAtlas("menu/images/menu_items.pack");
		float width = atlas.findRegion("btn_music").getRegionWidth();
		float height = atlas.findRegion("btn_music").getRegionHeight();		
		
		this.backBtn = new Sprite(atlas.findRegion("backbtn"));
		this.backBtn.setSize(atlas.findRegion("backbtn").getRegionWidth(), atlas.findRegion("backbtn").getRegionHeight());
		this.backBtn.setPosition(1000f,80f);
		
		this.music = new Sprite(atlas.findRegion("btn_music"));
		this.music.setSize(width, height);
		this.music.setPosition(180f,650f);
		
		this.music_off = new Sprite(atlas.findRegion("btn_music_off"));
		this.music_off.setSize(width, height);
		this.music_off.setPosition(180f,650f);
		
		
		this.sound = new Sprite(atlas.findRegion("btn_sound"));
		this.sound.setSize(width, height);
		this.sound.setPosition(180f,530f);
		
		this.sound_off = new Sprite(atlas.findRegion("btn_sound_off"));
		this.sound_off.setSize(width, height);
		this.sound_off.setPosition(180f,530f);
		
		
		this.vibrate = new Sprite(atlas.findRegion("btn_vibrate"));
		this.vibrate.setSize(width, height);
		this.vibrate.setPosition(180f,410f);
		
		this.vibrate_off = new Sprite(atlas.findRegion("btn_vibrate_off"));
		this.vibrate_off.setSize(width, height);
		this.vibrate_off.setPosition(180f,410f);
		
		
		this.control1 = new Sprite(atlas.findRegion("controls1btn"));
		this.control1.setSize(width, height);
		this.control1.setPosition(180f,290f);
		
		this.control2 = new Sprite(atlas.findRegion("controls2btn"));
		this.control2.setSize(width, height);
		this.control2.setPosition(180f,290f);		
		
		this.control3 = new Sprite(atlas.findRegion("controls3btn"));
		this.control3.setSize(width, height);
		this.control3.setPosition(180f,290f);
		
		this.autoFire = new Sprite(atlas.findRegion("btn_autofire"));
		this.autoFire.setSize(width, height);
		this.autoFire.setPosition(180f,170f);
		
		this.autoFireOff = new Sprite(atlas.findRegion("btn_autofire_off"));
		this.autoFireOff.setSize(width, height);
		this.autoFireOff.setPosition(180f,170f);
		
		
		this.credits = new Sprite(atlas.findRegion("btn_credits"));
		this.credits.setSize(width, height);
		this.credits.setPosition(180f,50f);
		
		this.assetsLoaded = true;
	}


	
	private void touchedEvent(Vector2 touchPoint) {
		Rectangle rect = null;
		
		if(music != null) {
			rect = music.getBoundingRectangle();
			rect.setWidth(760f);
			if(rect.contains(touchPoint.x, touchPoint.y)) {
				GameAssets.playSound(GameAssets.fetchSound("menu/sounds/click.mp3"));
				Configuration.musicEnabled = !Configuration.musicEnabled;
				if(Configuration.musicEnabled == true) {
					GameAssets.playMusic(GameAssets.fetchMusic("menu/music/tribute.mp3"), true, 0.5f);
					GameAssets.nativ.sendEvent("Settings", "Music-Changed", "on", 1);
				} else {
					GameAssets.pauseMusic(GameAssets.fetchMusic("menu/music/tribute.mp3"));
					GameAssets.nativ.sendEvent("Settings", "Music-Changed", "off", 1);
				}
			}
			this.saveSettingState();
		}
		
		if(sound != null) {
			rect = sound.getBoundingRectangle();
			rect.setWidth(760f);
			if(rect.contains(touchPoint.x, touchPoint.y)) {
				Configuration.soundEnabled = !Configuration.soundEnabled;
				GameAssets.playSound(GameAssets.fetchSound("menu/sounds/click.mp3"));
				
				if(Configuration.soundEnabled == true) GameAssets.nativ.sendEvent("Settings", "Sound-Changed", "on", 1);
				else GameAssets.nativ.sendEvent("Settings", "Sound-Changed", "off", 1);
			}
			this.saveSettingState();
		}
		
		
		if(vibrate != null) {
			rect = vibrate.getBoundingRectangle();
			rect.setWidth(760f);
			if(rect.contains(touchPoint.x, touchPoint.y)) {
				GameAssets.playSound(GameAssets.fetchSound("menu/sounds/click.mp3"));
				Configuration.vibrateEnabled = !Configuration.vibrateEnabled;
				
				if(Configuration.vibrateEnabled == true) GameAssets.nativ.sendEvent("Settings", "Vibrate-Changed", "on", 1);
				else GameAssets.nativ.sendEvent("Settings", "Vibrate-Changed", "off", 1);
			}
			this.saveSettingState();
		}
		
		
		if(autoFire != null) {
			rect = autoFire.getBoundingRectangle();
			rect.setWidth(760f);
			if(rect.contains(touchPoint.x, touchPoint.y)) {
				GameAssets.playSound(GameAssets.fetchSound("menu/sounds/click.mp3"));
				Configuration.autoFire = !Configuration.autoFire;
				Configuration.altAutoFire = Configuration.autoFire;
				
				if(Configuration.autoFire == true) GameAssets.nativ.sendEvent("Settings", "AutoFire-Changed", "on", 1);
				else GameAssets.nativ.sendEvent("Settings", "AutoFire-Changed", "off", 1);
			}
			this.saveSettingState();
		}
		
		if(credits != null) {
			rect = credits.getBoundingRectangle();
			rect.setWidth(760f);
			if(rect.contains(touchPoint.x, touchPoint.y)) {
				GameAssets.playSound(GameAssets.fetchSound("menu/sounds/click.mp3"));
				this.dispose();
				this.game.setScreen(new CreditsScreen(this.game));
				
				GameAssets.nativ.sendEvent("Settings", "Credits", "viewed", 1);
			}
		}
		
		if(control1 != null) {
			rect = control1.getBoundingRectangle();
			rect.setWidth(760f);
			if(rect.contains(touchPoint.x, touchPoint.y)) {
				GameAssets.playSound(GameAssets.fetchSound("menu/sounds/click.mp3"));
				this.dispose();
				this.game.setScreen(new InputScreen(this.game));
				
				GameAssets.nativ.sendEvent("Settings", "Credits", "viewed", 1);
			}
		}
		
		if(backBtn != null && backBtn.getBoundingRectangle().contains(touchPoint.x, touchPoint.y)) {
			GameAssets.playSound(GameAssets.fetchSound("menu/sounds/click.mp3"));
			this.dispose();
			this.game.setScreen(new MenuScreen(this.game));
		}
	}
	
	private void saveSettingState() {
		if(Gdx.files.isLocalStorageAvailable()) {
			GameAssets.settingsFileHandle.writeString(Configuration.musicEnabled + ";" + Configuration.soundEnabled + ";" + Configuration.vibrateEnabled + ";" + Configuration.inputType + ";" + Configuration.altAutoFire,false);
		}
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
		// TODO Auto-generated method stub
		
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
