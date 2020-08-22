package com.xsheetgames.genericGameObjects;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.xsheetgames.Configuration;
import com.xsheetgames.GameAssets;
import com.xsheetgames.genericElements.Level;

public class Chili extends GameObject {

	protected short categoryBits = 32;
	protected short maskBits = 8;
	public boolean ceaseCollision = false;
	public static GameObjectPool chiliPool;
	public boolean vanished;
	
	
	public Chili(World world, float x, float y, Vector2 vel) {
		super(world, GameAssets.fetchTextureAtlas("game/images/game_objects.pack"),0f,vel,new Vector2(x,y),true);		
		
		BodyDef bd = new BodyDef();
		bd.type = BodyType.DynamicBody;
		this.bodyType = BodyType.KinematicBody;
		bd.fixedRotation = true;
		bd.gravityScale = 0f;
		
		FixtureDef fd = new FixtureDef();
		fd.density = 1f;
		fd.friction = 0.5f;
		fd.restitution = 0.001f;
		fd.filter.categoryBits = this.categoryBits;
		fd.filter.maskBits = this.maskBits;
		fd.isSensor = true;
		
		super.init(bd, fd, GameAssets.getObjectLoader(), "Chili", "chili", Animation.PlayMode.NORMAL);
	}
	
	
	public static void createChiliPool(World world) {
		Chili.chiliPool = new GameObjectPool(8,12,new ChiliFactory(Chili.class,world));
	}
	
	public static void spawnChili(float x, float y, GameObjectCollection powerups, World world, Level actualLevel) {		
		Chili chili = (Chili) Chili.chiliPool.obtain();
		chili.setVelocity(new Vector2((-1f)*actualLevel.getLevelSpeed(),0f));
		chili.setPosition(x, y);
		chili.setPool(Chili.chiliPool);
		powerups.add(chili);
		if(Configuration.debugLevel >= Application.LOG_INFO && Configuration.spawnInfos) Gdx.app.log("Chili spawn successful","Chili-Hash: " + chili.hashCode());		
	}
	
	
	
	
	@Override
	public void dispose() {
		super.dispose();
	}

	
	@Override
	public void reset() {
		this.ceaseCollision = true;
		this.vanished = false;
		super.reset();
	}
	
	@Override
	public void obtainInit() {
		this.ceaseCollision = false;
		super.obtainInit();
	}
	
	
	@Override
	public boolean free() {
		this.ceaseCollision = true;
		return super.free();
	}
	
	public void vanish() {
		this.vanished = true;
	}
	

	@Override
	public void doMotionLogic(float delta) {
		if(this.vanished == true) {
			if(this.world.isLocked() == false) {
				if(this.free() == false) this.dispose();
			}
		}
	}
	
}
