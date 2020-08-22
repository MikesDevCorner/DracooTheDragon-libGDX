package com.xsheetgames.levelpacks.batmine;

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

public class BatMineLevelPack extends AbstractLevelpack {

	
	public static BodyEditorLoader objectLoader = new BodyEditorLoader(Gdx.files.internal("batmine/models/batmine.json"));
	public static TextureAtlas atlas;
	
	
	public BatMineLevelPack() {
		this.bossAssetsLoaded = false;
		this.packName = "BatMine";
		this.purchased = Configuration.fullBatmine;
		this.playStoreItemName = Configuration.batmineSku;
		this.amountLevels = Configuration.fullBatmine?15:3;
		
		
		//this.musicLocation = "batmine/music/";
		this.musicLocation = ""; //the location will be added in the level itself since version 0.92L
		
		if(Configuration.useIntroScreen) this.startComic ="batmine/images/batmine_intro_comic.jpg";
		this.endComic = "game/images/outro_comic";
		if(this.purchased == false) {
			this.endComic += "_demo";
		}
		this.endComic +=".jpg";
	}
		
	
	@Override
	public void readLevelFromFile() {
		super.readLevelFromFile("batmine");
	}
	

	@Override
	public void loadAssets() {		
		if(Configuration.useIntroScreen) GameAssets.manager.load("batmine/images/batmine_intro_comic.jpg", Texture.class);
		GameAssets.manager.load(this.endComic, Texture.class);
		
		GameAssets.manager.load("batmine/images/batmine_objects.pack", TextureAtlas.class);
		
		GameAssets.manager.load("batmine/images/bg.jpg", Texture.class);
		GameAssets.manager.load("batmine/images/fg.png", Texture.class);
		if(Configuration.FogActive)	GameAssets.manager.load("batmine/images/fog.png", Texture.class);
		GameAssets.manager.load("batmine/images/mg.png", Texture.class);
		
		GameAssets.manager.load("batmine/music/dracoo.mp3", Music.class);
		GameAssets.manager.load("batmine/music/reach.mp3", Music.class);
		
		GameAssets.manager.load("batmine/sounds/bat1.mp3", Sound.class);
		GameAssets.manager.load("batmine/sounds/bat2.mp3", Sound.class);
		
	}

	@Override
	public void unloadAssets() {		
		
		if(Configuration.useIntroScreen) {
			try { GameAssets.manager.unload("batmine/images/batmine_intro_comic.jpg"); } catch(Exception e) {}
		}
		try { GameAssets.manager.unload(this.endComic);  } catch(Exception e) {}
		
		try { GameAssets.manager.unload("batmine/images/batmine_objects.pack"); } catch(Exception e) {}		
		
		try { GameAssets.manager.unload("batmine/images/bg.jpg"); } catch(Exception e) {}
		try { GameAssets.manager.unload("batmine/images/fg.png"); } catch(Exception e) {}
		if(Configuration.FogActive)	{ try { GameAssets.manager.unload("batmine/images/fog.png"); } catch(Exception e) {} }
		try { GameAssets.manager.unload("batmine/images/mg.png"); } catch(Exception e) {}
				
		try { GameAssets.manager.unload("batmine/music/dracoo.mp3"); } catch(Exception e) {}
		try { GameAssets.manager.unload("batmine/music/reach.mp3"); } catch(Exception e) {}
		
		try { GameAssets.manager.unload("batmine/sounds/bat1.mp3"); } catch(Exception e) {}
		try { GameAssets.manager.unload("batmine/sounds/bat2.mp3"); } catch(Exception e) {}
	}
	
	@Override
	public void loadBossAssets() {
		if(Configuration.fullBatmine) {
			GameAssets.manager.load("batmine/sounds/boss.mp3", Sound.class);
			GameAssets.manager.load("batmine/sounds/bosswounded.mp3", Sound.class);		
			GameAssets.manager.load("batmine/sounds/bone.mp3", Sound.class);		
			GameAssets.manager.load("batmine/sounds/bosschair.mp3", Music.class);
			GameAssets.manager.load("batmine/sounds/bosschair_start.mp3", Sound.class);
			GameAssets.manager.load("batmine/images/bossbat_objects.pack", TextureAtlas.class);
		}
		this.bossAssetsLoaded = true;
	}
	
	@Override
	public void unloadBossAssets() {
		if(Configuration.fullBatmine) {
			BossBat.SpinningBonePool.clear();
			try { GameAssets.manager.unload("batmine/sounds/bone.mp3");  } catch(Exception e) {}	
			try { GameAssets.manager.unload("batmine/sounds/bosschair.mp3"); } catch(Exception e) {}
			try { GameAssets.manager.unload("batmine/sounds/bosschair_start.mp3"); } catch(Exception e) {}
			try { GameAssets.manager.unload("batmine/sounds/boss.mp3"); } catch(Exception e) {}
			try { GameAssets.manager.unload("batmine/sounds/bosswounded.mp3"); } catch(Exception e) {}
			try { GameAssets.manager.unload("batmine/images/bossbat_objects.pack"); } catch(Exception e) {}
		}
		this.bossAssetsLoaded = false;
	}
	
	@Override
	public boolean areAssetsLoaded() {
		return GameAssets.manager.isLoaded("batmine/images/batmine_objects.pack");
	}


	@Override
	public void createObstacleCollection(World world) {
		this.obstacles = new BatMineObstacles(world);
	}
	
	@Override
	public Texture getBackLayer() {
		return GameAssets.fetchTexture("batmine/images/bg.jpg");
	}
	
	@Override
	public Texture getMiddleLayer() {
		return GameAssets.fetchTexture("batmine/images/mg.png");
	}
	
	@Override
	public Texture getFogLayer() {
		if(Configuration.FogActive)	{
			return GameAssets.fetchTexture("batmine/images/fog.png");
		} else return null;
	}
	
	@Override
	public Texture getFrontLayer() {
		return GameAssets.fetchTexture("batmine/images/fg.png");
	}

	@Override
	public void setAtlas() {
		atlas = GameAssets.fetchTextureAtlas("batmine/images/batmine_objects.pack");		
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
		return new BatMineEnemies(world,tweenManager);
	}
	


}
