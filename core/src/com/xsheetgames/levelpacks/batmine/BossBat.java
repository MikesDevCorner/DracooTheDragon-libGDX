package com.xsheetgames.levelpacks.batmine;

import aurelienribon.tweenengine.TweenManager;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.xsheetgames.GameAssets;
import com.xsheetgames.genericElements.AbstractLevelpack;
import com.xsheetgames.genericGameObjects.Enemy;
import com.xsheetgames.genericGameObjects.GameObject;
import com.xsheetgames.genericGameObjects.GameObjectCollection;
import com.xsheetgames.genericGameObjects.GameObjectPool;
import com.xsheetgames.screens.GameScreen;

public class BossBat extends Enemy {
	
	public static GameObjectPool SpinningBonePool = null;
	
	private float forwardTimer;
	
	private float shootCounter = 2.5f;
	private float shootCounterInit = 2.5f;
	
	private float subShootCounter = 0.17f;
	private float subShootCounterInit = 0.17f;	
	
	
	private int amountShoots = 0;
	private int amountInitialShoots = 6;
	
	private Music chairSound;
	private float chairSoundInitTimer = 1.1f;
	private float chairSoundTimer;
	private boolean engineStarted = false;
	
	private float yAxeDeflection = 1.4f;
	private float shootYdirection = 0f;
	private Vector2 shootOrigin = new Vector2();
	
	private GameObjectCollection fireballs;
	private AbstractLevelpack pack;

	public BossBat(World world, float x, float y, Vector2 vel, TweenManager m) {
		
		super(world, GameAssets.fetchTextureAtlas("batmine/images/bossbat_objects.pack"), 0f, vel, new Vector2(x,y),false,m);
		
		this.dieTextureName = "big_bat_dying";
		this.dieSound = GameAssets.fetchSound("batmine/sounds/boss.mp3");
		this.energy = 25;
		this.enemyPoints = 50;
		
		this.deadXOffset = -3f;
		this.deadYOffset = 0f;
		
		super.init(BatMineLevelPack.objectLoader, "Bossbat", "fatbat_flying");
		this.body.setFixedRotation(true);
		this.forwardTimer = 15f;
		
		this.woundedSound = GameAssets.fetchSound("batmine/sounds/bosswounded.mp3");
		
		this.chairSound = GameAssets.fetchMusic("batmine/sounds/bosschair.mp3");
		this.chairSound.setLooping(true);
		
		this.initWoundedCounter = 0.4f;
		this.invincibleTimeAfterHurtInit = 0.4f;
	}
	
	
	public void setShootResources(AbstractLevelpack pack, GameObjectCollection fireballs) {
		this.fireballs = fireballs;
		this.pack = pack;
	}
	
	@Override
	public void obtainInit() {
		GameAssets.playSound(GameAssets.fetchSound("batmine/sounds/bosschair_start.mp3"),1.8f);
		super.obtainInit();
	}
	
	@Override
	public void pause() {
		this.engineStarted = false;
		this.chairSound.stop();
		super.pause();
	}
	
	@Override
	public void resume() {
		if(this.chairSoundTimer<= 0f && this.engineStarted == false) {
			this.chairSound.play();
			this.engineStarted = true;
		}
		super.resume();
	}
	
	
	@Override
	public void reset() {
		super.reset();
		this.shootCounter = this.shootCounterInit;
		this.subShootCounter = this.subShootCounterInit;
		this.amountShoots = this.amountInitialShoots;
		this.shootYdirection = 0f;
		this.chairSoundTimer = this.chairSoundInitTimer;
		this.engineStarted = false;
		this.chairSound.stop();
	}
	
	private void doShootSession(float delta) {
		if(this.dead == false) {
			if(this.shootCounter <= 0f && this.amountShoots == 0) {
				shoot();
				this.amountShoots++;
				this.subShootCounter = this.subShootCounterInit;
			} else this.shootCounter -= delta;
			
			if(this.shootCounter <= 0f) this.subShootCounter -= delta;		
			if(this.subShootCounter <= 0f) {
				this.shoot();
				amountShoots++;
				this.subShootCounter = this.subShootCounterInit;
			}
			if(this.amountShoots == this.amountInitialShoots) {
				this.shootCounter = this.shootCounterInit;
				this.amountShoots = 0;
			}
		}
	}
	
	@Override
	public boolean free() {
		if(GameScreen.bossEnergyMeter != null) GameScreen.bossEnergyMeter.unregisterHurtable();
		return super.free();
	}
	
	private void shoot() {
		
		GameObject b = BossBat.SpinningBonePool.obtain();
		this.shootOrigin.set(this.body.getPosition().x-0.5f, this.body.getPosition().y-0.1f);
		b.setPosition(this.shootOrigin);
		b.setVelocity(new Vector2(-16f,this.shootYdirection));
		b.setPool(BossBat.SpinningBonePool);	
		this.fireballs.add(b);
		
		if(this.shootYdirection == (2*yAxeDeflection)) this.shootYdirection = (-2f*yAxeDeflection);
		else if(this.shootYdirection == (yAxeDeflection)) this.shootYdirection = (-1*yAxeDeflection);
		else if(this.shootYdirection == 0f) this.shootYdirection = (yAxeDeflection);
		else if(this.shootYdirection == (-1*yAxeDeflection)) this.shootYdirection = (2*yAxeDeflection);
		else if(this.shootYdirection == (-2f*yAxeDeflection)) this.shootYdirection = 0f;
		
		GameAssets.playSound(GameAssets.fetchSound("batmine/sounds/bone.mp3"), 0.55f);
		
	}
	
	
	public void handleChairSound(float delta) {
		if(this.chairSoundTimer > 0f) this.chairSoundTimer-= delta;
		if(this.chairSoundTimer<= 0f && this.engineStarted == false) {
			this.engineStarted = true;
			this.chairSound.play();
		}
	}
	
	
	@Override
	public void resetGraphics(TextureAtlas atlas) {
		super.resetGraphics(atlas);
		//this.chairSound = GameAssets.fetchMusic("batmine/sounds/bosschair.mp3");
		//this.chairSound.setLooping(true);
	}
	
	
	
	@Override
	public void die() {
		super.die();
	}
	
	
	
	
	@Override
	public void doMotionLogic(float delta) {
		
		if(this.energy == 0 && this.dead == false) {
			this.die();
		}
		
		if(this.invincibleTimeAfterHurt > 0f) this.invincibleTimeAfterHurt -= delta;
		this.doShootSession(delta);
		this.handleChairSound(delta);
		
		if(this.woundedCounter >= 0f) this.woundedCounter-=delta;
		else {
			if(this.isBlack()) this.setOriginalColor();
		}
		
		if(this.dead == true) {
			if(this.deadPosition == true) {
				if(this.stateTime >= this.animation.getAnimationDuration()/3 && this.body.getFixtureList().get(0).isSensor() == false) {
					this.setAsSensor(true);
				}
			}			
			
			if(this.deadPosition == false) {
				if(this.world.isLocked() == false) {
					this.body.setLinearDamping(6f);
					this.body.setLinearVelocity(0f,0f);
					this.pack.getActualLevel().setRemainingSeconds(5f);
					this.deadPosition = true;
					this.spriteOffsetX = this.deadXOffset;
					this.spriteOffsetY = this.deadYOffset;
					this.previousPosition.add(this.spriteOffsetX, this.spriteOffsetY);					
					super.changeAnimation(this.dieTextureName,true, Animation.PlayMode.NORMAL);
				}
			}
		} else {
		
			//ENEMY MOTION CODE:
			
			//enemy is on the right side: x >= 16.6f		
			Vector2 vel = this.body.getLinearVelocity();
			Vector2 pos = this.body.getPosition();	
			
			
			if(vel.x >= -3 && vel.x < 8f) {
				this.body.setLinearVelocity(vel.x+2f, vel.y);
			}
			
			if(pos.x >= 16.6f && vel.x > 0f) {
				this.body.setLinearVelocity(vel.x-2f, vel.y);
			}
			
			this.forwardTimer -= delta;
			if(this.forwardTimer <= 0f) {
				this.body.setLinearVelocity(-7f, vel.y);
				this.forwardTimer = 15f;
			}
		}
	}
	
	@Override
	public void dispose() {
		this.chairSound = null;
		super.dispose();
	}

}
