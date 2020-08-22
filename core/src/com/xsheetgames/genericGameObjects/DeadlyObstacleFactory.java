package com.xsheetgames.genericGameObjects;

import aurelienribon.bodyeditor.BodyEditorLoader;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.physics.box2d.World;
import com.xsheetgames.GameAssets;

public class DeadlyObstacleFactory extends GameObjectFactory{

	private World world;
	private TextureAtlas atlas;
	private String atlasName;
	private BodyEditorLoader loader;
	private String loaderName;
	private float x, y;
	private short energy;
	private float deadX, deadY;
	private String dieTextureName;
	private Sound dieSound;
	private boolean collideNotWithEnemyFire;
	
	
	public DeadlyObstacleFactory(Class<? extends GameObject> c, World w, TextureAtlas a, String aName, BodyEditorLoader loader, String loaderName, 
			float x, float y, short energy, float deadX, float deadY, String dieTextureName, Sound dieSound, boolean collideNotWithEnemyFire ) {
		super(c);
		this.world = w;
		this.atlas = a;
		this.atlasName = aName;
		this.loader = loader;
		this.loaderName = loaderName;
		this.x = x;
		this.y = y;
		this.energy = energy;
		this.deadX = deadX;
		this.deadY = deadY;
		this.dieTextureName = dieTextureName;
		this.dieSound = dieSound;
		this.collideNotWithEnemyFire = collideNotWithEnemyFire;
	}
	

	@Override
	public GameObject createObject() {
		return new DeadlyObstacle(this.world, GameAssets.emptyVector2, this.atlas, this.atlasName, this.loader, this.loaderName, 
				this.x, this.y, this.energy, this.deadX, this.deadY, this.dieTextureName, this.dieSound, this.collideNotWithEnemyFire);
	}

}