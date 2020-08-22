package com.xsheetgames;


import java.util.Date;
import aurelienribon.bodyeditor.BodyEditorLoader;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.XmlReader;



public class GameAssets {

	public static BodyEditorLoader objectLoader;
	public static AssetManager manager, loadingManager;
	public static Vector2 emptyVector2 = new Vector2();
	public static Animation<TextureRegion> loadingAnimation;
	public static iNativeFunctions nativ; 
	public static FileHandle LogFileHandle;
	public static FileHandle ProgressFileHandle;
	public static FileHandle settingsFileHandle;
	public static XmlReader.Element gameProgress;
	public static Array<String> levelPacks;
	public static float buttonTimer = 0f;
	
	//FINAL KEY STATICS FOR POLLING INPUTS FROM SCREEN
	public static final int KEY_UP = 1;
	public static final int KEY_DOWN = 2;
	public static final int KEY_LEFT = 3;
	public static final int KEY_RIGHT = 4;
	public static final int KEY_PRIMARY = 5;
	public static final int KEY_SECONDARY = 6;
	public static final int KEY_START = 7;
	public static final int AXIS_X = 8;
	public static final int AXIS_Y = 9;
	public static final int KEY_BACK = 10;
	
	/******************************************************************************************/
	
	public static void load() {
		GameAssets.LogFileHandle = null;
		GameAssets.ProgressFileHandle = null;
		GameAssets.settingsFileHandle = null;
		manager = new AssetManager();
		loadingManager = new AssetManager();
		objectLoader = new BodyEditorLoader(Gdx.files.internal("game/models/generic.json"));
		loadAssetsForLoadingScreen(true);
	}
	
	
	private static void initLevelPacks() {
		GameAssets.levelPacks = new Array<String>();
		GameAssets.levelPacks.add("BatMine");
		GameAssets.levelPacks.add("Jungle");
	}
	
	
	private static void initStaticFiles() {
		Gdx.app.log("INIT","STATIC");
		if(Gdx.files.isExternalStorageAvailable()) {
			GameAssets.LogFileHandle = Gdx.files.external("dracoo_the_dragon/errorlog.txt");
		}
		
		if(Gdx.files.isLocalStorageAvailable()) {
			
			GameAssets.settingsFileHandle = Gdx.files.local("dracoo_the_dragon/settings.txt");
			GameAssets.ProgressFileHandle = Gdx.files.local("dracoo_the_dragon/progress.xml");
		
			//Settings:
			try {
				if(GameAssets.settingsFileHandle.exists() == false) { //create settings if not exists
					GameAssets.settingsFileHandle.writeString(Configuration.musicEnabled + ";" + Configuration.soundEnabled + ";" + Configuration.vibrateEnabled + ";" + Configuration.inputType + ";" + Configuration.autoFire,false);
				}
				
				String settingsString = GameAssets.settingsFileHandle.readString();
				String[] splitResult = settingsString.split(";");
				Configuration.musicEnabled = Boolean.parseBoolean(splitResult[0]);
				Configuration.soundEnabled = Boolean.parseBoolean(splitResult[1]);
				Configuration.vibrateEnabled = Boolean.parseBoolean(splitResult[2]);
				Configuration.inputType = Integer.parseInt(splitResult[3]);
				Configuration.autoFire = Boolean.parseBoolean(splitResult[4]);
				Configuration.altAutoFire = Configuration.autoFire;
				
				
			} catch(Exception e) {
				Date n = new Date();
				if(GameAssets.LogFileHandle != null) GameAssets.LogFileHandle.writeString(n.toString() + ": Problem reading the settings.txt.", true);
			}
			
			//Game Progress:
			try {
				if(GameAssets.ProgressFileHandle.exists() == false) { //create processfile if not exists
					FileHandle template = Gdx.files.internal("game/progress.xml");
					String templateText = template.readString();
					GameAssets.ProgressFileHandle.writeString(templateText, false);
				}
				XmlReader myReader = new XmlReader();
				GameAssets.gameProgress = myReader.parse(GameAssets.ProgressFileHandle);
				
				
				//nicht vorhandene Levelpacks in die Fortschritts-Datei schreiben:
				boolean foundPack;
				Array<XmlReader.Element> toAdd = new Array<XmlReader.Element>();
				
				for(String levelPack : GameAssets.levelPacks) {
					foundPack = false;
					for(XmlReader.Element pack : GameAssets.gameProgress.getChildrenByName("pack")) {
						if(pack.getAttribute("name").equals(levelPack)) {
							foundPack = true;
							break;
						}
					}
					if(foundPack == false) {
						XmlReader.Element tmpPack = new XmlReader.Element("pack", GameAssets.gameProgress);
						tmpPack.setAttribute("name", levelPack);
						for(int i = 1; i <= 15; i++) {
							XmlReader.Element tmpLevel = new XmlReader.Element("level",tmpPack);
							tmpLevel.setAttribute("number", String.valueOf(i));
							if(i == 1) tmpLevel.setAttribute("status", "noeggs");
							else tmpLevel.setAttribute("status", "locked");
							tmpPack.addChild(tmpLevel);
						}
						toAdd.add(tmpPack);
					}
				}
				if(toAdd.size > 0) {
					for(XmlReader.Element pack : toAdd) {
						GameAssets.gameProgress.addChild(pack);
					}
					GameAssets.ProgressFileHandle.writeString(GameAssets.gameProgress.toString(), false);
				}
				
				
			} catch(Exception e) {
				Date n = new Date();
				if(GameAssets.LogFileHandle != null) GameAssets.LogFileHandle.writeString(n.toString() + ": Problem parsing the progress xml.", true);
			}
		} else {
			GameAssets.ProgressFileHandle = null;
			GameAssets.LogFileHandle = null;
			GameAssets.gameProgress = null;
		}
		
	}
	
	
	public static void loadAssetsForLoadingScreen(boolean firstTime) {
		
		if(firstTime) initLevelPacks();			
		
		loadingManager.load("loading/loading.pack", TextureAtlas.class);
		loadingManager.load("loading/loading_back.jpg", Texture.class);
		loadingManager.load("fonts/memory.fnt", BitmapFont.class);
		loadingManager.finishLoading();	
		Gdx.app.log("INIT","Loadingscreen preparing");
		
		loadingAnimation = new Animation(1/60f, loadingManager.get("loading/loading.pack", TextureAtlas.class).findRegions("loading_bar"));
		
		if(firstTime) {
			initStaticFiles();
			loadMenuAssets();
			if(GameAssets.nativ.isControllerConnected()) {
				Configuration.autoFire = false;
			}
		}
	}
	
	
	
	public static void loadMenuAssets() {
		manager.load("menu/images/menuScreen.jpg", Texture.class);
		manager.load("menu/images/credits_back.jpg", Texture.class);
		
		manager.load("menu/images/moga_pocket.png", Texture.class);
		manager.load("menu/images/moga_pro.png", Texture.class);
		manager.load("menu/images/ouya_controller.png", Texture.class);
		
		manager.load("menu/images/menu_items.pack", TextureAtlas.class);
		manager.load("menu/music/tribute.mp3", Music.class);
		
		manager.load("menu/sounds/click.mp3", Sound.class);
		manager.load("menu/sounds/click2.mp3", Sound.class);
	}
	
	public static void unloadMenuAssets() {
		try { manager.unload("menu/images/menuScreen.jpg"); } catch(Exception e) {}
		try { manager.unload("menu/images/credits_back.jpg"); } catch(Exception e) {}
		
		try { manager.unload("menu/images/moga_pocket.png"); } catch(Exception e) {}
		try { manager.unload("menu/images/moga_pro.png"); } catch(Exception e) {}
		try { manager.unload("menu/images/ouya_controller.png"); } catch(Exception e) {}
		
		try { manager.unload("menu/images/menu_items.pack"); } catch(Exception e) {}
		try { manager.unload("menu/music/tribute.mp3"); } catch(Exception e) {}
		
		try { manager.unload("menu/sounds/click.mp3"); } catch(Exception e) {}
		try { manager.unload("menu/sounds/click2.mp3"); } catch(Exception e) {}
	}
	
	
	public static void loadGameAssets() {
		manager.load("game/images/background.jpg", Texture.class);
		manager.load("game/images/game_objects.pack", TextureAtlas.class);		
		manager.load("game/sounds/already_energy.mp3", Sound.class);
		manager.load("game/sounds/cheers.mp3", Sound.class);
		manager.load("game/sounds/dead.mp3", Sound.class);
		manager.load("game/sounds/dead1.mp3", Sound.class);
		manager.load("game/sounds/dead2.mp3", Sound.class);
		manager.load("game/sounds/dead3.mp3", Sound.class);
		manager.load("game/sounds/energylose.mp3", Sound.class);
		manager.load("game/sounds/fire.mp3", Sound.class);
		manager.load("game/sounds/powerup.mp3", Sound.class);
		manager.load("game/sounds/wings.mp3", Sound.class);
		manager.load("game/sounds/win0.mp3", Sound.class);
		manager.load("game/sounds/win1.mp3", Sound.class);
		manager.load("game/sounds/win2.mp3", Sound.class);
		manager.load("game/sounds/win3.mp3", Sound.class);
		manager.load("game/sounds/egg.mp3", Sound.class);
		manager.load("game/sounds/counting.mp3", Sound.class);
	}
	
	public static void unloadGameAssets() {
		try { manager.unload("game/images/background.jpg"); } catch(Exception e) {}
		try { manager.unload("game/images/game_objects.pack"); } catch(Exception e) {}	
		try { manager.unload("game/sounds/already_energy.mp3"); } catch(Exception e) {}
		try { manager.unload("game/sounds/cheers.mp3"); } catch(Exception e) {}
		try { manager.unload("game/sounds/dead.mp3"); } catch(Exception e) {}
		try { manager.unload("game/sounds/dead1.mp3"); } catch(Exception e) {}
		try { manager.unload("game/sounds/dead2.mp3"); } catch(Exception e) {}
		try { manager.unload("game/sounds/dead3.mp3"); } catch(Exception e) {}
		try { manager.unload("energylose.mp3"); } catch(Exception e) {}
		try { manager.unload("game/sounds/fire.mp3"); } catch(Exception e) {}
		try { manager.unload("game/sounds/powerup.mp3"); } catch(Exception e) {}
		try { manager.unload("game/sounds/wings.mp3"); } catch(Exception e) {}
		try { manager.unload("game/sounds/win0.mp3"); } catch(Exception e) {}
		try { manager.unload("game/sounds/win1.mp3"); } catch(Exception e) {}
		try { manager.unload("game/sounds/win2.mp3"); } catch(Exception e) {}
		try { manager.unload("game/sounds/win3.mp3"); } catch(Exception e) {}
		try { manager.unload("game/sounds/egg.mp3"); } catch(Exception e) {}
		try { manager.unload("game/sounds/counting.mp3"); } catch(Exception e) {}
	}
	
	public static void setNative(iNativeFunctions n) {
		nativ = n;
	}
	
	/******************************************************************************************/
	
	
	public static void playSound (Sound sound) {
		try {
			if (Configuration.soundEnabled == true) sound.play(1f);
		} catch(Exception e) {
			Gdx.app.log("PlaySound", "Fehler beim Abspielen von Soundfile", e);
		}
	}
	
	public static void playSound (Sound sound, float volume) {
		try {
			if (Configuration.soundEnabled == true) sound.play(volume);
		} catch(Exception e) {
			Gdx.app.log("PlaySound", "Fehler beim Abspielen von Soundfile", e);
		}		
	}
	
	public static void playMusic (Music music, boolean looping, float volume) {
		try {
			if (Configuration.musicEnabled == true) {
				music.setLooping(looping);
				music.setVolume(volume);
				music.play();
			}
		} catch(Exception e) {
			Gdx.app.log("PlayMusic", "Fehler beim Abspielen von Musicfile", e);
		}		
	}
	
	public static void pauseMusic(Music music) {
		try {
			music.pause();
		} catch(Exception e) {}
	}	
	
	public static void queueTextureAssetLoading(String asset) {
		manager.load(asset, Texture.class);
	}
	
	public static void queueSoundAssetLoading(String asset) {
		manager.load(asset, Sound.class);
	}
	
	public static void queueMusicAssetLoading(String asset) {
		manager.load(asset, Music.class);
	}
	
	public static void queueTextureAtlasAssetLoading(String asset) {
		manager.load(asset, TextureAtlas.class);
	}

    public static GlyphLayout glyphLayout = new GlyphLayout();
	
	public static Texture fetchTexture(String name) {
		if(manager.isLoaded(name)) {
			return manager.get(name, Texture.class);
		} else return null;
	}
	
	public static BitmapFont fetchFont(String name) {
		if(loadingManager.isLoaded(name)) {
			return loadingManager.get(name, BitmapFont.class);
		} else return null;
	}
	
	public static Sound fetchSound(String name) {
		if(manager.isLoaded(name)) {
			return manager.get(name, Sound.class);
		} else return null;
	}
	
	public static Music fetchMusic(String name) {
		if(manager.isLoaded(name)) {
			return manager.get(name, Music.class);
		} else return null;
	}
	
	public static TextureAtlas fetchTextureAtlas(String name) {
		if(manager.isLoaded(name)) {
			return manager.get(name, TextureAtlas.class);
		} else return null;
	}
	
	public static void vibrate(int duration) {
		if(Configuration.vibrateEnabled) Gdx.input.vibrate(duration);
	}
	
	
	
	/******************************************************************************************/
	
	
	public static boolean assetsLoaded(SpriteBatch batch) {
		if(manager != null && batch != null) {
			boolean val = manager.update();
			
			if(val == false) {
				batch.getProjectionMatrix().setToOrtho2D(0, 0, Configuration.TARGET_WIDTH, Configuration.TARGET_HEIGHT);
				drawLoadingScreen(batch);
			}
			return val;
		} else return false;
	}
	
	
	public static boolean loadAssets() {
		return !manager.update();
	}
	
	public static float getLoadingProcess() {
		return manager.getProgress();
	}
	
	public static int getLoadingSteps(int step) {
		int progress = (int)(getLoadingProcess()*100);
		progress = ((int)(progress/step))*step;
		return progress;	
	}
	
	public static void drawLoadingScreen(SpriteBatch batch) {
		batch.begin();
		batch.draw(loadingManager.get("loading/loading_back.jpg", Texture.class), 0, 0, Configuration.TARGET_WIDTH, Configuration.TARGET_HEIGHT);
		batch.draw(loadingAnimation.getKeyFrame(getLoadingProcess()),270,225);		
		batch.end();
	}
	
	public static void unloadAsset(String fileName) {
		manager.unload(fileName);
	}
	
	public static BodyEditorLoader getObjectLoader() {
		return objectLoader;
	}
	
	public static void dispose() {
		manager.dispose();
		loadingManager.dispose();
		manager = null;
		loadingManager = null;
		objectLoader = null;
	}	
	
}
