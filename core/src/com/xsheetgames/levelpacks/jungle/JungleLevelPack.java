package com.xsheetgames.levelpacks.jungle;

import aurelienribon.bodyeditor.BodyEditorLoader;
import aurelienribon.tweenengine.TweenManager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.physics.box2d.World;
import com.xsheetgames.Configuration;
import com.xsheetgames.GameAssets;
import com.xsheetgames.genericElements.AbstractLevelpack;
import com.xsheetgames.genericGameObjects.EnemyCollection;

public class JungleLevelPack extends AbstractLevelpack {

	
	public static BodyEditorLoader objectLoader = new BodyEditorLoader(Gdx.files.internal("jungle/models/jungle_objects.json"));
	public static TextureAtlas atlas;
	
	
	public JungleLevelPack() {
		this.bossAssetsLoaded = false;
		this.packName = "Jungle";
		this.purchased = Configuration.fullJungle;
		this.playStoreItemName = Configuration.jungleSku;
		this.amountLevels = Configuration.fullJungle?15:3;
		
		//this.musicLocation = "jungle/music/";
		this.musicLocation = ""; //the location will be added in the level itself since version 0.92L
		
		if(Configuration.useIntroScreen) this.startComic = "jungle/images/jungle_intro_comic.jpg";
		this.endComic = "game/images/outro_comic";
		if(this.purchased == false) {
			this.endComic += "_demo";
		}
		this.endComic +=".jpg";
	}	
		
	
	@Override
	public void readLevelFromFile() {
		super.readLevelFromFile("jungle");
	}
	

	@Override
	public void loadAssets() {
		
		if(Configuration.useIntroScreen) GameAssets.manager.load("jungle/images/jungle_intro_comic.jpg", Texture.class);
		GameAssets.manager.load(this.endComic, Texture.class);
		
		GameAssets.manager.load("jungle/images/jungle_objects.pack", TextureAtlas.class);
		GameAssets.manager.load("jungle/images/jungle_bg.jpg", Texture.class);
		GameAssets.manager.load("jungle/images/jungle_fg.png", Texture.class);
		if(Configuration.FogActive)	GameAssets.manager.load("jungle/images/jungle_fog.png", Texture.class);
		GameAssets.manager.load("jungle/images/jungle_mg.png", Texture.class);
		
		GameAssets.manager.load("batmine/music/dracoo.mp3", Music.class);
		GameAssets.manager.load("jungle/music/wilbur.mp3", Music.class);
		
		GameAssets.manager.load("batmine/sounds/bat1.mp3", Sound.class);
		GameAssets.manager.load("batmine/sounds/bat2.mp3", Sound.class);
		GameAssets.manager.load("jungle/sounds/plant.mp3", Sound.class);
		GameAssets.manager.load("jungle/sounds/rockburst.mp3", Sound.class);
	}
	

	@Override
	public void unloadAssets() {
		
		if(Configuration.useIntroScreen) {
			try { GameAssets.manager.unload("jungle/images/jungle_intro_comic.jpg"); } catch(Exception e) {}
		}
		try { GameAssets.manager.unload(this.endComic);  } catch(Exception e) {}
		
		try { GameAssets.manager.unload("jungle/images/jungle_objects.pack"); } catch(Exception e) {}
		try { GameAssets.manager.unload("jungle/images/jungle_bg.jpg"); } catch(Exception e) {}
		try { GameAssets.manager.unload("jungle/images/jungle_fg.png"); } catch(Exception e) {}
		if(Configuration.FogActive)	{ try { GameAssets.manager.unload("jungle/images/jungle_fog.png"); } catch(Exception e) {}}
		try { GameAssets.manager.unload("jungle/images/jungle_mg.png"); } catch(Exception e) {}
		
		try { GameAssets.manager.unload("batmine/music/dracoo.mp3"); } catch(Exception e) {}
		try { GameAssets.manager.unload("jungle/music/wilbur.mp3"); } catch(Exception e) {}
		
		try { GameAssets.manager.unload("batmine/sounds/bat1.mp3"); } catch(Exception e) {}
		try { GameAssets.manager.unload("batmine/sounds/bat2.mp3"); } catch(Exception e) {}
		try { GameAssets.manager.unload("jungle/sounds/plant.mp3"); } catch(Exception e) {}
		try { GameAssets.manager.unload("jungle/sounds/rockburst.mp3"); } catch(Exception e) {}
	}
	
	@Override
	public void loadBossAssets() {
		if(Configuration.fullJungle) {
			GameAssets.manager.load("jungle/sounds/boss.mp3", Sound.class);
			GameAssets.manager.load("jungle/sounds/bosswounded.mp3", Sound.class);		
			GameAssets.manager.load("jungle/sounds/venomspit.mp3", Sound.class);
			GameAssets.manager.load("jungle/images/jungle_boss.pack", TextureAtlas.class);
		}
		this.bossAssetsLoaded = true;		
	}


	@Override
	public void unloadBossAssets() {
		if(Configuration.fullJungle) {
			try { GameAssets.manager.unload("jungle/sounds/boss.mp3");  } catch(Exception e) {}
			try { GameAssets.manager.unload("jungle/sounds/bosswounded.mp3");  } catch(Exception e) {}
			try { GameAssets.manager.unload("jungle/sounds/venomspit.mp3");  } catch(Exception e) {}
			try { GameAssets.manager.unload("jungle/images/jungle_boss.pack");  } catch(Exception e) {}
		}
		this.bossAssetsLoaded = false;
	}
	
	@Override
	public boolean areAssetsLoaded() {
		return GameAssets.manager.isLoaded("jungle/images/jungle_objects.pack");
	}


	@Override
	public void createObstacleCollection(World world) {
		this.obstacles = new JungleObstacles(world);
	}
	
	@Override
	public Texture getBackLayer() {
		return GameAssets.fetchTexture("jungle/images/jungle_bg.jpg");
	}
	
	@Override
	public Texture getMiddleLayer() {
		return GameAssets.fetchTexture("jungle/images/jungle_mg.png");
	}
	
	@Override
	public Texture getFogLayer() {
		if(Configuration.FogActive)	{
			return GameAssets.fetchTexture("jungle/images/jungle_fog.png");
		} else return null;
	}
	
	@Override
	public Texture getFrontLayer() {
		return GameAssets.fetchTexture("jungle/images/jungle_fg.png");
	}

	@Override
	public void setAtlas() {
		atlas = GameAssets.fetchTextureAtlas("jungle/images/jungle_objects.pack");		
	}
	
	@Override
	public TextureAtlas getAtlas() {
		return atlas;
	}
	
	@Override
	public BodyEditorLoader getBodyEditorLoader() {
		return objectLoader;
	}
	
	@Override
	public EnemyCollection getEnemyCollection(World world, TweenManager tweenManager) {
		return new JungleEnemies(world,tweenManager);
	}
	


}
