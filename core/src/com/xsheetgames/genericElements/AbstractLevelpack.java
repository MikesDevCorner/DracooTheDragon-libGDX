package com.xsheetgames.genericElements;

import aurelienribon.bodyeditor.BodyEditorLoader;
import aurelienribon.tweenengine.TweenManager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.XmlReader;
import com.xsheetgames.Configuration;
import com.xsheetgames.GameAssets;
import com.xsheetgames.genericGameObjects.AbstractObstacleCollection;
import com.xsheetgames.genericGameObjects.EnemyCollection;
import com.xsheetgames.screens.GameScreen;

public abstract class AbstractLevelpack {

	protected int actualLevel;	
	protected Level level;
	
	protected String musicLocation;
	
	protected Texture backLayer;
	protected Texture middleLayer;
	protected Texture fogLayer;
	protected Texture frontLayer;
	
	protected String startComic;
	protected String endComic;
	
	protected AbstractObstacleCollection obstacles;
	protected int amountLevels;
	
	public boolean purchased;
	public String playStoreItemName;
	
	public String packName;
	
	public abstract void loadAssets();	
	public abstract void unloadAssets();
	
	public abstract void  loadBossAssets();
	public abstract void  unloadBossAssets();
	public boolean bossAssetsLoaded;
	
	public abstract boolean areAssetsLoaded();
	public abstract void createObstacleCollection(World world);
	
	
	public abstract void setAtlas();
	public abstract TextureAtlas getAtlas();
	public abstract void readLevelFromFile();
	
	
	//read the Level from the External Source and provide the Pack with the crafted Level-File
	public void readLevelFromFile(String assetFolder) {
		
		//Fetch the right File and prepare for reading:
		String nbr = this.getActualLevelNumber()+"";  // FOR GWT REASONS NOT String.format()
		if(nbr.length() == 1) nbr = "0"+nbr;
		FileHandle levelFile = Gdx.files.internal(assetFolder+"/levels/" + this.packName + "_" + nbr + ".json");

		//RootElement:
		JsonReader jsonReader = new JsonReader();
		JsonValue o = jsonReader.parse(levelFile);
		
		//Enemies
		JsonValue enemies = o.getChild("enemies");
		Array<EnemyDescription> structuredEnemies = new Array<EnemyDescription>();
		for (; enemies != null; enemies = enemies.next()) {
			 EnemyDescription tmpEnemy = new EnemyDescription();
			 tmpEnemy.ctime = (float) enemies.get("ctime").asDouble();
			 tmpEnemy.name = enemies.get("name").asString();
			 tmpEnemy.y = (float) enemies.get("y").asDouble();
			 tmpEnemy.xSpeed =(float) enemies.get("xSpeed").asDouble();
			 tmpEnemy.ySpeed = (float) enemies.get("ySpeed").asDouble();
			 tmpEnemy.motionDuration = (float) enemies.get("motionDuration").asDouble();
			 tmpEnemy.motionPeculiarity = (float) enemies.get("motionPeculiarity").asDouble();
			 tmpEnemy.motionEquation = enemies.get("motionEquation").asString();
			 tmpEnemy.motionInfinite = enemies.get("motionInfinite").asBoolean();
			 structuredEnemies.add(tmpEnemy);
		}
		structuredEnemies.sort();
		
		
		//Obstacles
		JsonValue obstacles = o.getChild("obstacles");
		Array<ObstacleDescription> structuredObstacles = new Array<ObstacleDescription>();
		for(; obstacles != null; obstacles = obstacles.next()) {
			 ObstacleDescription tmpObstacle = new ObstacleDescription();
			 tmpObstacle.ctime = (float) obstacles.get("ctime").asDouble();
			 tmpObstacle.name = obstacles.get("name").asString();
			 tmpObstacle.y = (float) obstacles.get("y").asDouble();
			 structuredObstacles.add(tmpObstacle);
		}
		structuredObstacles.sort();
		
		
		//Powerups
		JsonValue powerups = o.getChild("powerups");
		Array<PowerupDescription> structuredPowerups = new Array<PowerupDescription>();
		for(; powerups != null; powerups = powerups.next()) {
			 PowerupDescription tmpPowerup = new PowerupDescription();
			 tmpPowerup.ctime = (float) powerups.get("ctime").asDouble();
			 tmpPowerup.y = (float) powerups.get("y").asDouble();
			 tmpPowerup.x =(float) powerups.get("x").asDouble();
			 structuredPowerups.add(tmpPowerup);
		}
		structuredPowerups.sort();
		
		
		//Breaks
		JsonValue breaks = o.getChild("breaks");
		Array<BreakDescription> structuredBreaks = new Array<BreakDescription>();
		for(; breaks != null; breaks = breaks.next()) {
			BreakDescription tmpBreak = new BreakDescription();
			tmpBreak.ctime = (float) breaks.get("ctime").asDouble();
			tmpBreak.message = breaks.get("message").asString();
			structuredBreaks.add(tmpBreak);
		}
		structuredBreaks.sort();
		
		
		//other Levelspecific values (speed, duration, music):
		float speed = (float) o.get("speed").asDouble();
		float seconds = (float) o.get("seconds").asDouble();
		boolean openBack = o.get("openBack").asBoolean();
		float chiliRandomtime = (float) o.get("chiliRandomTime").asDouble();
		String music = o.get("music").asString();		
		
		//Generate the Level
		this.level = new Level(speed,seconds,this.getActualLevelNumber(),this.musicLocation+music);
		//Set the recognized Elements in the actual Level
		this.level.setLevelElements(structuredEnemies, structuredObstacles, structuredPowerups, structuredBreaks, chiliRandomtime);
		this.level.setOpenBack(openBack);
		
		//if there is an ready and waiting obstacles-Collection, set the x-Velocity from the Level
		if(this.obstacles != null) {
			this.obstacles.setVelocity(this.getActualLevel().getLevelSpeed());
		}
	}
	
	
	public Level getActualLevel() {
		return this.level;
	}
	
	public int getActualLevelNumber() {
		return this.actualLevel;
	}	
	
	public int getLevelCount() {
		return this.amountLevels;
	}
	
	public void advanceActualLevel(float delta, GameScreen gameScreen) {
		this.getActualLevel().advanceLevel(delta, gameScreen);
		if(this.level.getLevelMusic() == null) {
			this.level.setLevelMusic(GameAssets.fetchMusic(this.level.musicString));
		}
		if(this.level.getLevelMusic() != null && GameScreen.paused == false) {
			if(Configuration.musicEnabled == true && this.level.getLevelMusic().isPlaying() == false) {
				this.level.getLevelMusic().stop();
				GameAssets.playMusic(this.level.getLevelMusic(),true,0.8f);
			}
		}
	}
	
	public boolean actualLevelDone() {
		return this.level.levelDone();
	}
	
	
	public boolean setActualLevel(int level) {
		this.actualLevel = level;
		if(this.actualLevel > this.amountLevels || this.actualLevel <= 0) {
			return false;
		} else {
			this.obstacles.setVelocity(this.level.getLevelSpeed());
			return true;
		}
	}
	
	public void setStartLevel(int level) {
		this.actualLevel = level;
	}
	
	public float getRemainingLevelSeconds() {
		return this.level.getRemainingSeconds();
	}
	
	public boolean nextLevel() {
		this.actualLevel++;
		if(this.actualLevel > this.amountLevels) {
			this.actualLevel = 1;
			return false;
		} else {
			return true;
		}
	}
	
	public String getStartComic() {
		return this.startComic;
	}
	public String getEndComic() {
		return this.endComic;
	}
	
	public int getReachedEggs() {
		int countEggs = 0;
		if(GameAssets.gameProgress != null) {
			
			for(int i = 0; i<15; i++) {			
				for(XmlReader.Element pack : GameAssets.gameProgress.getChildrenByName("pack")) {
					if(pack.getAttribute("name").equals(this.packName)) {
						for(XmlReader.Element levelItem : pack.getChildrenByName("level")) {
							if(levelItem.getAttribute("number").equals((i+1)+"")) {
								if(i<this.getLevelCount()) {								
									if(levelItem.getAttribute("status").equals("oneegg")) {
										countEggs += 1;
									}
									if(levelItem.getAttribute("status").equals("twoeggs")) {
										countEggs += 2;
									}
									if(levelItem.getAttribute("status").equals("threeeggs")) {
										countEggs += 3;
									}
								}
							}
						}
					}
				}
			}
			return countEggs;
		} else return 24;
	}
	
	
	public abstract Texture getBackLayer();
	public float getBackLayerSpeed() {
		return this.level.getLevelSpeed() * 0f;
	}
	
	public abstract Texture getMiddleLayer();
	public float getMiddleLayerSpeed() {
		return this.level.getLevelSpeed() * 0.47f;
	}
	
	public abstract Texture getFogLayer();
	public float getFogLayerSpeed() {
		return this.level.getLevelSpeed() * 0f;
	}

	public abstract Texture getFrontLayer();
	public float getFrontLayerSpeed() {
		return this.level.getLevelSpeed() * 1.5f;
	}
	
	public abstract EnemyCollection getEnemyCollection(World world, TweenManager tweenManager);

	
	public void dispose() {
		if(this.level != null) this.level.dispose();
		this.level = null;
		if(this.obstacles != null) this.obstacles.dispose();
	}
	
	
	public AbstractObstacleCollection getObstacles() {
		return this.obstacles;
	}
	
	public abstract BodyEditorLoader getBodyEditorLoader();
	
	
}
