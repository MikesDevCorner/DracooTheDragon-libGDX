package com.xsheetgames.genericGameObjects;

import aurelienribon.bodyeditor.BodyEditorLoader;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class EnemyFire extends GameObject {

	protected short categoryBits = 64;
	protected short maskBits;
	public boolean ceaseCollision = false;
	private float livingTimer;
	
	public EnemyFire(World world, float x, float y, TextureAtlas atlas, Vector2 velocity, BodyEditorLoader objectLoader, String loaderName, String atlasName, boolean fireballHit) {
		super( world,atlas,0f,velocity,new Vector2(x,y),true);

		if(fireballHit) this.maskBits = 26;
		else this.maskBits = 10;
		
		BodyDef bd = new BodyDef();
		bd.type = BodyType.DynamicBody;
		bd.bullet = true;
		this.bodyType = BodyType.DynamicBody;
		bd.fixedRotation = true;
		bd.gravityScale = 0f;
		
		FixtureDef fd = new FixtureDef();
		fd.density = 6f;
		fd.friction = 0.5f;
		fd.restitution = 0.5f;
		fd.filter.categoryBits = this.categoryBits;
		fd.filter.maskBits = this.maskBits;
		this.livingTimer = 5f;

		super.init( bd, fd, objectLoader, loaderName, atlasName, Animation.PlayMode.NORMAL);
	}
	
	
	@Override
	public void dispose() {
		super.dispose();
	}
	
	@Override
	public void reset() {
		super.reset();
		this.livingTimer = 5f;
	}


	@Override
	public void doMotionLogic(float delta) {		
		if(this.livingTimer > 0f) this.livingTimer -= delta;
		else this.free();
	}
	
}
