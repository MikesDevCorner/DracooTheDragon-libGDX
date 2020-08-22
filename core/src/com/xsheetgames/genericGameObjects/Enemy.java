package com.xsheetgames.genericGameObjects;

import aurelienribon.bodyeditor.BodyEditorLoader;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenEquation;
import aurelienribon.tweenengine.TweenManager;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.xsheetgames.GameAssets;
import com.xsheetgames.screens.GameScreen;

public class Enemy extends GameObject implements Hurtable {

	
	protected String dieTextureName;
	protected short energy;
	protected short startEnergy;
	protected boolean dead = false;
	protected boolean deadPosition = false;
	protected short categoryBits = 4;
	protected short maskBits = 31;
	protected TweenManager tweens;
	
	protected Sound woundedSound;
	protected String woundedSoundString;
	protected Sound dieSound;
	protected String dieSoundString;
	
	protected float initWoundedCounter = 0.2f;
	public float woundedCounter = 0f;
	
	protected int enemyPoints = 1;
	
	protected Timeline tl;
	protected boolean accelerating = false;
	
	protected float gradientAngle = 13.0f;
	protected float turnDegreesPerSecond = 80f;
	
	public float invincibleTimeAfterHurt = 0f;
	protected float invincibleTimeAfterHurtInit = 0.2f;
	
	protected float deadXOffset = 0f;
	protected float deadYOffset = 0f;

	public Enemy(World world, TextureAtlas atlas, float delayTimeSpan, Vector2 startVelocity, Vector2 startPosition, boolean looping, TweenManager m) {
		super(world, atlas, delayTimeSpan, startVelocity, startPosition, looping);
		this.tweens = m;
	}
	
	public void pause() {
		
	}
	
	public void resume() {
		
	}
	
	public boolean reduceEnergy() {
		if(this.invincibleTimeAfterHurt <= 0f) {
			this.energy--;
			this.invincibleTimeAfterHurt = this.invincibleTimeAfterHurtInit;
			if(this.energy == 0) this.die();
			else {
				this.setBlack();				
				this.woundedCounter = this.initWoundedCounter;
				if(this.woundedSound != null) GameAssets.playSound(this.woundedSound);
			}
			return true;
		}
		return false;
	}
	
	public short getEnergy() {
		return this.energy;
	}
	
	public short getStartEnergy() {
		return this.startEnergy;
	}
	
	public int getEnemyPoints() {
		return this.enemyPoints;
	}
	
	@Override
	public void obtainInit() {
		super.obtainInit();
	}
	
	@Override
	public void resetGraphics(TextureAtlas atlas) {
		super.resetGraphics(atlas);
		//this.woundedSound = GameAssets.fetchSound(this.woundedSoundString);
		//this.dieSound = GameAssets.fetchSound(this.dieSoundString);
	}
	
	@Override
	public void reset() {
		this.dead = false;
		this.invincibleTimeAfterHurt = 0f;
		this.body.setLinearDamping(0f);
		this.spriteOffsetX = 0f;
		this.spriteOffsetY = 0f;
		this.deadPosition = false;
		this.setAsSensor(false);
		this.energy = this.startEnergy;
		this.tweens.killTarget(this);
		this.accelerating = false;
		this.setOriginalColor();
		super.reset();
	}
	
	
	public void init (BodyEditorLoader loader, String loaderName, String atlasName) {
		
		BodyDef bd = new BodyDef();
		bd.type = BodyType.DynamicBody;
		this.bodyType = BodyType.DynamicBody;
		bd.angularDamping = 1f;
		bd.linearDamping = 0f;
		bd.gravityScale = 0.0f;
		
		FixtureDef fd = new FixtureDef();
		fd.density = 0.7f;
		fd.friction = 0.5f;
		fd.restitution = 0.2f;
		fd.filter.categoryBits = this.categoryBits;
		fd.filter.maskBits = this.maskBits;
		
		this.startEnergy = this.energy;
		
		super.init(bd, fd, loader, loaderName, atlasName, Animation.PlayMode.NORMAL);
	}
	
	public void die() {		
		GameAssets.playSound(this.dieSound, 1.3f);
		this.dead = true;
		this.tweens.killTarget(this);
	}
	
	
	public void setFunnyMotion(float time, float yAdder, TweenEquation e) {		
		
		 tl = Timeline.createSequence()
	    .push(Tween.to(this, GameObjectAccessor.VELOCITY_Y, time).target(this.getStartVelocity().y-yAdder).ease(e))
	    .push(Tween.to(this, GameObjectAccessor.VELOCITY_Y, time).target(0f).ease(e))
	    .push(Tween.to(this, GameObjectAccessor.VELOCITY_Y, time).target(this.getStartVelocity().y+yAdder).ease(e))
	    .push(Tween.to(this, GameObjectAccessor.VELOCITY_Y, time).target(0f).ease(e))
	    .repeat(Tween.INFINITY, 0f)
	    .delay(0.2f)
	    .start(this.tweens);
		
		
	}
	
	
	@Override
	public void doMotionLogic(float delta) { //if enemy is too slow, accelerate it to its target velocity in 0.3 seconds 
		if(this.body != null && this.dead == false) {
			if(this.body.getLinearVelocity().x > (this.startVelocity.x / 3 * 2) && !this.accelerating) {
				Tween.to(this, GameObjectAccessor.VELOCITY_X, 0.4f).target(this.startVelocity.x).delay(0.9f).start(this.tweens);
				this.accelerating = true;
			}
			if(this.tweens.containsTarget(this) == false) { //TODO: Kriterium muss noch ge�ndert werden.
				this.accelerating = false;
			}
		}
		
		if(this.invincibleTimeAfterHurt > 0f) this.invincibleTimeAfterHurt -= delta;
		
		if(this.energy == 0 && this.dead == false) {
			this.die();
		}
		
		if(this.woundedCounter >= 0f) this.woundedCounter-=delta;
		else {
			if(this.isBlack()) this.setOriginalColor();
		}
		
		if(this.dead == true) {
			this.body.setLinearDamping(6f);
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
		
			
		//ANGLE OPERATIONS
		if(this.body.getAngle() < this.gradientAngle * MathUtils.degreesToRadians * (-1)) { //FIX ANGLE TO LOWER LIMIT
			this.body.setTransform(body.getPosition(), this.gradientAngle * MathUtils.degreesToRadians * (-1));
		}
		if(this.body.getAngle() > this.gradientAngle * MathUtils.degreesToRadians) { // FIX ANGLE TO UPPER LIMIT
			this.body.setTransform(body.getPosition(), this.gradientAngle * MathUtils.degreesToRadians);
		}
		
		float x = this.body.getLinearVelocity().x;
		float y = this.body.getLinearVelocity().y;
		
		if(Math.abs(y) > 3f || Math.abs(x) > 5f) {
			if( ((y > 0) && (Math.abs(y) > Math.abs(x))) || ((x < 0) && (Math.abs(x) > Math.abs(y)))) {
				//nach oben neigen
				if((this.body.getAngle() * MathUtils.radiansToDegrees) <= this.gradientAngle) {	
	                this.body.setTransform(body.getPosition(), body.getAngle()+(this.turnDegreesPerSecond*delta*MathUtils.degreesToRadians));
	            }
			}
			
			if( ((y < 0) && (Math.abs(y) > Math.abs(x))) || ((x > 0) && (Math.abs(x) > Math.abs(y)))) {
				//nach unten neigen
				if((body.getAngle() * MathUtils.radiansToDegrees) >= (this.gradientAngle*-1)) {	
					body.setTransform(body.getPosition(), body.getAngle()-(this.turnDegreesPerSecond*delta*MathUtils.degreesToRadians));
	            }
			}
		} else {		
			if(Math.abs(body.getAngle()) <= this.turnDegreesPerSecond*delta*MathUtils.degreesToRadians) {
				this.body.setTransform(body.getPosition(), 0f);
			}
			if(body.getAngle() < 0 && body.getAngle() >= this.gradientAngle * MathUtils.degreesToRadians * (-1)) {			
				this.body.setTransform(body.getPosition(), body.getAngle()+(this.turnDegreesPerSecond*delta*MathUtils.degreesToRadians));
			}
			if(body.getAngle() > 0 && body.getAngle() <= this.gradientAngle) {
				this.body.setTransform(body.getPosition(), body.getAngle()-(this.turnDegreesPerSecond*delta*MathUtils.degreesToRadians));
			}
		}		
		this.body.setAngularVelocity(0f);
		
		
	}
	
	public boolean isDead() {
		return (this.energy <= 0f);
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
	public void dispose() {
		this.tweens.killTarget(this);
		this.dieSound = null;
		this.dieTextureName = null;
		super.dispose();
	}

}
