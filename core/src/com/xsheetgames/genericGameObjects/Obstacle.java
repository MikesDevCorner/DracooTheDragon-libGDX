package com.xsheetgames.genericGameObjects;

import java.util.Random;

import aurelienribon.bodyeditor.BodyEditorLoader;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.xsheetgames.GameAssets;
import com.xsheetgames.screens.GameScreen;

public class Obstacle extends GameObject{

	protected Vector2 velocity;
	protected Vector2 startPointUp;
	protected Vector2 startPointDown;
	
	protected static Random rand = new Random();
	protected short categoryBits = 2;
	protected short maskBits = 92;
	protected short maskBitsCollideNotWithEnemyfire = 28;
	
	//TODO: Fill with Constructor:
	public boolean willDieOnFireball = false;
	protected short energy = 1;
	protected short startEnergy = 1;
	protected float deadXOffset = 0f;
	protected float deadYOffset = 0f;
	protected String dieTextureName = "";
	protected Sound dieSound;
	
	private boolean dead = false;
	private boolean deadPosition = false;
	
	
	public Obstacle(World world, Vector2 vel, TextureAtlas atlas, String atlasName, BodyEditorLoader loader, String loaderName, float x, float y, short energy, float deadX, float deadY, String dieTextureName, Sound dieSound, boolean collideNotEnemyFire) {
		super(world,atlas,0f,vel,new Vector2(x,y),false);
		
		this.dieTextureName = dieTextureName;
		this.energy = energy;
		this.startEnergy = energy;
		this.deadXOffset = deadX;
		this.deadYOffset = deadY;
		this.dieSound = dieSound;
		
		BodyDef bd = new BodyDef();
		bd.type = BodyType.KinematicBody;
		this.bodyType = BodyType.KinematicBody;
		  
		FixtureDef fd = new FixtureDef();
		fd.density = 1.0f;
		fd.friction = 0.5f;
		fd.restitution = 0.3f;
		
		fd.filter.categoryBits = this.categoryBits;		
		
		fd.filter.maskBits = collideNotEnemyFire?this.maskBitsCollideNotWithEnemyfire:this.maskBits;
		
		super.init(bd, fd, loader, loaderName, atlasName, Animation.PlayMode.NORMAL);
	}
	
	public void reduceEnergy() {
		this.energy--;
		if(this.energy == 0) this.die();
	}
	
	
	public void die() {		
		if(this.dieSound != null) GameAssets.playSound(this.dieSound, 1.3f);
		this.dead = true;
	}
	
	
	@Override
	public void draw(SpriteBatch batch, float delta) {
		if(this.dead == true && this.deadPosition == true) {
			
			if(!GameScreen.paused) {
				this.stateTime += delta;
				this.stateTimeAlreadyIncremented = true;
			}
			if(this.animation.isAnimationFinished(this.stateTime)) {
				if(!this.free()) this.dispose();				
			}
		}
		
		super.draw(batch, delta);
	}
		
	
	@Override
	public void setVelocity(Vector2 vel) {
		super.setVelocity(vel);
	}	
	
	
	@Override
	public void setPosition(Vector2 pos) {
		super.setPosition(pos);
	}	
		
	
	@Override
	public void obtainInit() {
		super.obtainInit();
	}
	
	@Override
	public void reset() {
		this.dead = false;
		this.spriteOffsetX = 0f;
		this.spriteOffsetY = 0f;
		this.deadPosition = false;
		this.setAsSensor(false);
		this.energy = this.startEnergy;
		super.reset();
	}	
	
	
	@Override
	public void doMotionLogic(float delta) {
		if(this.energy == 0 && this.dead == false) {
			this.die();
		}
		
		if(this.dead == true) {
			if(this.deadPosition == true) {
				if(this.stateTime >= this.animation.getAnimationDuration()/3 && this.body.getFixtureList().get(0).isSensor() == false) {
					this.setAsSensor(true);
				}
			}			
			
			if(this.deadPosition == false) {
				if(this.world.isLocked() == false) {
					this.deadPosition = true;
					this.spriteOffsetX = this.deadXOffset;
					this.spriteOffsetY = this.deadYOffset;
					this.previousPosition.add(this.spriteOffsetX, this.spriteOffsetY);					
					super.changeAnimation(this.dieTextureName,true, Animation.PlayMode.NORMAL);
				}
			}
		}
		
	}
	
	public void dispose() {
		this.velocity = null;
		this.startPointDown = null;
		this.startPointUp = null;
		super.dispose();
	}

}
