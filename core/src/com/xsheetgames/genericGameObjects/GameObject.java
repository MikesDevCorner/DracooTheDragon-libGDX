package com.xsheetgames.genericGameObjects;

import aurelienribon.bodyeditor.BodyEditorLoader;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.xsheetgames.Configuration;
import com.xsheetgames.screens.GameScreen;

public abstract class GameObject {	
	
	//misc
	private boolean disposed;
	public boolean setDisposing; //use this instead of dispose() --> dispose will be called in the right order if this field changes to true
	private GameObjectPool pool;
	protected boolean meSensor = false;
	protected boolean stateTimeAlreadyIncremented;
	
	//box2d stuff
	protected BodyType bodyType;
	protected Body body;	
	protected Vector2 modelOrigin;
	protected Vector2 startVelocity;
	protected Vector2 startPosition;
	protected World world;
	protected Vector2 previousPosition= new Vector2();
    protected float previousAngle;
    protected BodyDef bd;
    protected FixtureDef fd;
    protected BodyEditorLoader loader;
    public String loaderName;
    
	
	//drawing stuff
	protected TextureAtlas atlas;
	protected Animation<TextureRegion> animation;
	protected Animation<TextureRegion> startAnimation;
	protected TextureRegion currentRegion;
	protected float stateTime;
	protected boolean looping;
	protected float delayTimer;
	protected float delayTimespan;
    protected Sprite sprite;
    protected boolean visible = false;
    protected boolean firstTimeSmoothened = false;
    protected float spriteOffsetX = 0f;
    protected float spriteOffsetY = 0f;
    protected float woundR=0f, woundG=0f, woundB=0f;
    protected Color spriteColor; 
    protected String atlasName;
    PlayMode playMode;
    
	
	public GameObject(World world, TextureAtlas atlas, float delayTimeSpan, Vector2 startVelocity, Vector2 startPosition, boolean looping) {
		this.looping = looping;
		this.world = world;
		this.atlas = atlas;
		this.stateTime = 0f;
		this.delayTimer = 0f;
		this.delayTimespan = delayTimeSpan;
		this.disposed = false;
		this.setDisposing = false;
		this.startVelocity = startVelocity;
		this.startPosition = startPosition;
		this.stateTimeAlreadyIncremented = false;
	}
	
	public void setBlack() {
		this.sprite.setColor(this.woundR,this.woundG,this.woundB,0.95f);
	}
	
	public void setOriginalColor() {
		this.sprite.setColor(this.spriteColor);
	}
	
	public boolean isBlack() {
		if(this.sprite.getColor().equals(this.spriteColor)) return false;
		else return true;
	}
	
	public void setPool(GameObjectPool pool) {
		this.pool = pool;
	}
	
	
	public void setVelocity(Vector2 vel) {
		this.body.setLinearVelocity(vel);
		this.startVelocity = vel;
	}
	
	public void setVelocity(float velX, float velY) {
		this.body.setLinearVelocity(velX, velY);
		this.startVelocity = this.startVelocity.set(velX, velY);
	}
	
	public Vector2 getStartVelocity() {
		return this.startVelocity;
	}
	
	public void setPosition(Vector2 pos) {
		this.body.setTransform(pos, 0f);
		this.startPosition = pos;
	}
	
	public void setPosition(float x, float y) {
		this.body.setTransform(x, y, 0f);
		this.startPosition = new Vector2(x,y);
	}
	
	public Vector2 getStartPosition() {
		return this.startPosition;
	}
	
	public void resetGraphics(TextureAtlas atlas) {
		this.atlas = atlas;
		if(this.atlas != null) {
			this.animation = new Animation(1f/ 25f, this.atlas.findRegions(atlasName), playMode);
			this.startAnimation = this.animation;
		}
		if(this.animation != null) this.currentRegion = this.animation.getKeyFrame(this.stateTime);
		
		if(this.currentRegion != null) {
			this.sprite = new Sprite(this.currentRegion);
			this.sprite.setSize(Configuration.VIEWPORT_WIDTH * this.currentRegion.getRegionWidth() / Configuration.TARGET_WIDTH, Configuration.VIEWPORT_HEIGHT * this.currentRegion.getRegionHeight() / Configuration.TARGET_HEIGHT);
		}
	}
	
	
	//create the box2d bodies and the sprite for drawing
	public void init(BodyDef bd, FixtureDef fd, BodyEditorLoader loader, String loaderName, String atlasName, PlayMode playMode) {
		if(this.atlas != null) {
			this.animation = new Animation(1f/ 25f, this.atlas.findRegions(atlasName), playMode);
			this.startAnimation = this.animation;
		}
		if(this.animation != null) this.currentRegion = this.animation.getKeyFrame(this.stateTime);
		
		if(this.currentRegion != null) {
			this.sprite = new Sprite(this.currentRegion);
			this.sprite.setSize(Configuration.VIEWPORT_WIDTH * this.currentRegion.getRegionWidth() / Configuration.TARGET_WIDTH, Configuration.VIEWPORT_HEIGHT * this.currentRegion.getRegionHeight() / Configuration.TARGET_HEIGHT);
		}
	
		bd.linearVelocity.set(this.startVelocity);
		bd.position.set(this.startPosition);
		
		this.bd = bd;
		this.fd = fd;
		this.loader = loader;
		this.loaderName = loaderName;
		
		this.body = world.createBody(bd);
		this.body.setUserData(this);
		loader.attachFixture(this.body, loaderName, fd, sprite.getWidth());
		this.modelOrigin = loader.getOrigin(loaderName, this.sprite.getWidth()).cpy();
		this.visible = true;
		
		this.spriteColor = new Color(sprite.getColor());
		
		this.atlasName = atlasName;
		this.playMode = playMode;
	}
	
	public void obtainInit() {
		this.visible = true;
		this.body.setActive(true);
		
		//this.body = this.world.createBody(this.bd);
		//this.body.setUserData(this);
		//this.loader.attachFixture(this.body, this.loaderName, this.fd, this.sprite.getWidth());
	}

	
	
	public void setAsSensor(boolean sensor) {
		this.meSensor = sensor;
		for(Fixture f : this.body.getFixtureList()) {
			f.setSensor(sensor);			
		}
	}
	
	
	
	public void reset() {
		this.stateTime = 0f;
		this.delayTimer = 0f;
		this.body.setTransform(0f, 0f, 0f);
		if(this.body.isActive()) this.body.setActive(false);
		
		//try { world.destroyBody(this.body); }
		//catch(Exception e) {}
		
		this.visible = false;
		this.animation = this.startAnimation;
		this.currentRegion = this.animation.getKeyFrame(this.stateTime);
		this.sprite.setSize(Configuration.VIEWPORT_WIDTH * this.currentRegion.getRegionWidth() / Configuration.TARGET_WIDTH, Configuration.VIEWPORT_HEIGHT * this.currentRegion.getRegionHeight() / Configuration.TARGET_HEIGHT);
		this.firstTimeSmoothened = false;
	}
    
    
    
	public abstract void doMotionLogic(float delta);
	
	
	
	public void smoothStates(float fixedTimestepAccumulatorRatio) { //bring the position of the sprite in sync with the position of the box2d object
		if(!this.disposed && this.visible && this.firstTimeSmoothened)  {
			float oneMinusRatio = 1.0f - fixedTimestepAccumulatorRatio;
			Vector2 position = body.getPosition().sub(modelOrigin);
			position.add(this.spriteOffsetX, this.spriteOffsetY);
			
			sprite.setPosition((fixedTimestepAccumulatorRatio * position.x) + (oneMinusRatio * this.previousPosition.x), (fixedTimestepAccumulatorRatio * position.y) + (oneMinusRatio * this.previousPosition.y));
			sprite.setRotation(body.getAngle() * MathUtils.radiansToDegrees * fixedTimestepAccumulatorRatio + oneMinusRatio * this.previousAngle);
			sprite.setOrigin(modelOrigin.x, modelOrigin.y);
		}
	}
	
	
	public void resetSmoothStates() { //bring the position of the sprite in sync with the position of the box2d object
		if(!this.disposed && this.visible)  {
			Vector2 position = body.getPosition().sub(modelOrigin);
			position.add(this.spriteOffsetX, this.spriteOffsetY);
			
			this.previousAngle = body.getAngle()* MathUtils.radiansToDegrees;
			this.previousPosition.set(position.x,position.y);
			sprite.setPosition(this.previousPosition.x, this.previousPosition.y);
			sprite.setRotation(this.previousAngle);
			sprite.setOrigin(modelOrigin.x, modelOrigin.y);
			if(!firstTimeSmoothened) this.firstTimeSmoothened = true;
		}
	}	
	
	
	public Vector2 getLeftBottom() {
		if(!this.disposed) return this.body.getPosition().sub(this.modelOrigin);
		else return null;
	}
	
	public Vector2 getRightTop() { //TODO: Arbeitet nicht richtig. Das Sprite hat nix mit der Breite des Bodys zu tun!!
		if(!this.disposed) return this.body.getPosition().sub(this.modelOrigin).add(sprite.getWidth(), sprite.getHeight());
		else return null;
	}
	
	
	public void draw(SpriteBatch batch, float delta) {
		if(!this.disposed && this.visible && this.firstTimeSmoothened) {
			if(!GameScreen.paused && !this.stateTimeAlreadyIncremented) this.stateTime += delta;
			this.stateTimeAlreadyIncremented = false;
			
			if(this.animation.isAnimationFinished(this.stateTime)) {
				this.stateTime = 0f;
				if(this.looping) this.delayTimer = this.delayTimespan;
			}
			
			if(this.delayTimer > 0f) {
				if(!GameScreen.paused) this.delayTimer -= delta;
				this.currentRegion = this.animation.getKeyFrame(0f);
			}
			else this.currentRegion = this.animation.getKeyFrame(this.stateTime);
			
			if(this.body != null && this.currentRegion != null) {
				if(this.sprite.getTexture().equals(this.currentRegion) == false)
					this.sprite.setRegion(this.currentRegion);
				
				this.sprite.draw(batch); //actually draw the image
			}
		}
		if(this.setDisposing == true) this.dispose(); //now is the correct time to dispose our object if somwhere arises the need for it.
	}
	
	
	public boolean isSensor() {
		return this.body.getFixtureList().get(0).isSensor();
	}
	
	
	public void setLooping(boolean looping) {
		this.looping = looping;
	}
	
	public void setLooping(boolean looping, float delayTimeSpan) {
		this.looping = looping;
		this.delayTimespan = delayTimeSpan;
		this.delayTimer = delayTimeSpan;
	}
	
	public void restartAnimation() {
		this.delayTimer = 0f;
		this.stateTime = 0f;
	}
	
	public void changeAnimation(String atlasName, boolean reset, PlayMode playMode) {
		this.animation = new Animation(1f/ 25f, this.atlas.findRegions(atlasName), playMode);
		if(reset) this.restartAnimation();
		this.currentRegion = this.animation.getKeyFrame(this.stateTime);
		this.sprite.setSize(Configuration.VIEWPORT_WIDTH * this.currentRegion.getRegionWidth() / Configuration.TARGET_WIDTH, Configuration.VIEWPORT_HEIGHT * this.currentRegion.getRegionHeight() / Configuration.TARGET_HEIGHT);
	}
	
	
	public Body getBody() {
		return this.body;
	}
	
	public World getWorld() {
		return this.world;
	}
	
	public BodyType getBodyType() {
		return this.bodyType;
	}
	
	public boolean isDisposed() {
		return this.disposed;
	}
	
	public boolean isVisible() {
		return this.visible;
	}
	
	public boolean free() {
		if(this.pool != null && this.body != null) {
			if(this.body.isActive()) this.pool.free(this);
			return true;
		} else {
			//this.setDisposing = true;
			return false;
		}
	}
	
	
	public void dispose() {		
		if(!this.disposed) {
			this.disposed = true;
			if(this.body != null) world.destroyBody(this.body);
			this.bodyType = null;
			this.body = null;
			this.modelOrigin = null;
			this.startVelocity = null;
			this.world = null;
			this.previousPosition = null;
			this.atlas = null;
			this.animation = null;
			this.currentRegion = null;
			this.sprite = null;
			this.visible = false;
		}
	}
	
	
}
