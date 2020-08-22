package com.xsheetgames.genericGameObjects;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.badlogic.gdx.physics.box2d.joints.WeldJointDef;
import com.badlogic.gdx.utils.Array;
import com.xsheetgames.Configuration;
import com.xsheetgames.GameAssets;
import com.xsheetgames.genericElements.AtlasAnimationCollection;
import com.xsheetgames.genericElements.InputButton;
import com.xsheetgames.genericElements.InputButtonCollection;
import com.xsheetgames.screens.GameScreen;


public class Draco extends GameObject{
	
	public static GameObjectPool fireballPool;
	
	/*******CHARACTER CONFIG************/
	
	private short energy = 1;
	private boolean fireModeEnabled = false;
	
	private float invincibleTimeAfterHurt = 0.5f;
	
	private float linearDamping = 5f;
	private float forceToApplyStart = 51f;
	private float forceToApply = 71f;
	private float timeStartForce = 0.35f;
	
	private float upCounter, leftCounter, rightCounter, downCounter;
	
	private float gradientAngle = 20.0f;	
	private float turnDegreesPerSecond = 120f;
	
	private float secondsBetweenShoots;
	private float secondsBetweenWingSounds = 0.80f;
	
	private float gravityScale = 0f;
	
	/********************************/
	
	
	//MISC
	private float fireCounter = 0f;
	private float wingCounter = 0f;
	
	public InputButton up, down, left, right, fire;
	public boolean upPressed, downPressed, leftPressed, rightPressed, firePressed;
	
	private Sound fireSound = GameAssets.fetchSound("game/sounds/fire.mp3");
	private Sound wingSound = GameAssets.fetchSound("game/sounds/wings.mp3");
	
	private boolean invincibleTextureSwitched = false;
	public float invincible = 0f;
	
	private int enemiesKilled = 0;
	
	
	//Physics Stuff (Box2d)
	private Body legBody;
	private Body handBody;
	private Body headBody;
	private Joint legJoint;
	private Joint handJoint;
	private Joint headJoint;
	
	protected Vector2 previousPositionLegs= new Vector2();
	protected Vector2 previousPositionHands= new Vector2();
	protected Vector2 previousPositionHead = new Vector2();
	protected float previousAngleLegs;
	protected float previousAngleHands;
	protected float previousAngleHead;
	
	private Vector2 legsOrigin;
	private Vector2 handsOrigin;
	private Vector2 headOrigin;
	
	protected short categoryBits = 8;
	protected short maskBits = 103;
	
	private Vector2 appliedForce;
	
	
	//Drawing Stuff
	private Sprite legSprite;
	private Sprite handSprite;
	private Sprite headSprite;
	private TextureRegion liveTexture1, liveTexture2, liveTexture3, liveTexture4;
	
	private Animation headAnimationGreenClosed;
	private Animation headAnimationRedOpen;
	private Animation headAnimationRedClosed;
	private Animation headAnimationInvClosed;
	
	private Animation<TextureRegion> headAnimation;
	
	private float headOpenTimer = 0f;
	private float headOpenTimerOriginal = 0.14f;
	private float headStateTime = 0f;
	
	

	public Draco(World world, float x, float y, InputButtonCollection buttons, AtlasAnimationCollection sparkles) {
		
		super(world,GameAssets.fetchTextureAtlas("game/images/game_objects.pack"),0f,new Vector2(0f,0f),new Vector2(x,y),true);
		
		this.secondsBetweenShoots =  Configuration.autoFire?0.31f:0.27f;
		
		fireballPool = new GameObjectPool(8, 16, new FireballFactory(Fireball.class, world, sparkles));
		Array<GameObject> objects = new Array<GameObject>();
		for(int i = 0; i < fireballPool.initialCapacity; i++) { //Pool prebefllen
			GameObject o = (GameObject) fireballPool.obtain();
			o.setPool(fireballPool);
			objects.add(o);
		}
		for(GameObject o : objects) {
			fireballPool.free(o);
		}
		objects.clear();
		objects = null;
		
		
		if(buttons != null) {
			this.up = buttons.getButton(InputButtonCollection.KEY_UP);
			this.down = buttons.getButton(InputButtonCollection.KEY_DOWN);
			this.left = buttons.getButton(InputButtonCollection.KEY_LEFT);
			this.right = buttons.getButton(InputButtonCollection.KEY_RIGHT);
			this.fire = buttons.getButton(InputButtonCollection.KEY_FIRE);
			this.fire.hidden = !this.fireModeEnabled;
		}
		
		this.appliedForce = new Vector2();		
		
		
		this.upPressed = false;
		this.downPressed = false;
		this.leftPressed = false;
		this.rightPressed = false;
		
		this.liveTexture1 = this.atlas.findRegion("chili_empty");
		this.liveTexture2 = this.atlas.findRegion("chili_one");
		this.liveTexture3 = this.atlas.findRegion("chili_two");
		this.liveTexture4 = this.atlas.findRegion("chili_three");
		
		
		this.legSprite = new Sprite(this.atlas.findRegion("legs_green"));
		this.legSprite.setSize(Configuration.VIEWPORT_WIDTH * this.atlas.findRegion("legs_green").getRegionWidth() / Configuration.TARGET_WIDTH, Configuration.VIEWPORT_HEIGHT * this.atlas.findRegion("legs_green").getRegionHeight() / Configuration.TARGET_HEIGHT);
		
		this.handSprite = new Sprite(this.atlas.findRegion("hands_green"));
		this.handSprite.setSize(Configuration.VIEWPORT_WIDTH * this.atlas.findRegion("hands_green").getRegionWidth() / Configuration.TARGET_WIDTH, Configuration.VIEWPORT_HEIGHT * this.atlas.findRegion("hands_green").getRegionHeight() / Configuration.TARGET_HEIGHT);
		
		this.headAnimationGreenClosed = new Animation(1/25f,this.atlas.findRegions("draco_head_green"));
		this.headAnimationRedClosed = new Animation(1/25f,this.atlas.findRegions("draco_head_red"));
		this.headAnimationInvClosed = new Animation(1/25f,this.atlas.findRegions("draco_head_inv"));
		this.headAnimationRedOpen = new Animation(1/25f,this.atlas.findRegions("draco_head_open"));
		this.headAnimation = this.headAnimationGreenClosed;
		this.headSprite = new Sprite(this.headAnimation.getKeyFrame(this.headStateTime));
		this.headSprite.setSize(Configuration.VIEWPORT_WIDTH * this.headAnimation.getKeyFrame(this.headStateTime).getRegionWidth() / Configuration.TARGET_WIDTH, Configuration.VIEWPORT_HEIGHT * this.headAnimation.getKeyFrame(this.headStateTime).getRegionHeight() / Configuration.TARGET_HEIGHT);
		
		
		BodyDef bd = new BodyDef();
		this.bodyType = BodyType.DynamicBody;
		bd.linearDamping = this.linearDamping;
		bd.type = BodyType.DynamicBody;
		bd.angularDamping = 1f;
		bd.gravityScale = this.gravityScale;
		  
		FixtureDef fd = new FixtureDef();
		fd.density = 1.0f;
		fd.friction = 0.5f;
		fd.restitution = 0.4f;
		fd.filter.categoryBits = this.categoryBits;
		fd.filter.maskBits = this.maskBits;
		
		super.init(bd, fd, GameAssets.objectLoader, "Draco", "draco_green", Animation.PlayMode.NORMAL);
		
		fd.filter.maskBits = 3; //collide just with obstacles and boundaries --> erklrung zu diesem Konzept in Redmine (Dokumente)
		
		//LEGS
		bd.position.set(x-0.13f,y+0.145f);
		legBody = world.createBody(bd);
		legBody.setUserData(this);
		GameAssets.getObjectLoader().attachFixture(legBody, "Legs", fd, legSprite.getWidth());
		legsOrigin = GameAssets.getObjectLoader().getOrigin("Legs", legSprite.getWidth()).cpy();
		
		//HANDS
		bd.position.set(x+0.32f,y+0.22f);
		handBody = world.createBody(bd);
		handBody.setUserData(this);
		GameAssets.getObjectLoader().attachFixture(handBody, "Hands", fd, handSprite.getWidth());		
		handsOrigin = GameAssets.getObjectLoader().getOrigin("Hands", handSprite.getWidth()).cpy();
		
		//HEAD
		bd.position.set(x+0.54f, y+0.41f);
		headBody = world.createBody(bd);
		headBody.setUserData(this);
		fd.density = 0.0001f;
		GameAssets.getObjectLoader().attachFixture(headBody, "Head", fd, headSprite.getWidth());
		headOrigin = GameAssets.getObjectLoader().getOrigin("Head", headSprite.getWidth()).cpy();
		

		//JOIN Box2d BODIES
		RevoluteJointDef jd = new RevoluteJointDef();
		jd.collideConnected = false;
		jd.enableLimit = true;
		jd.lowerAngle = (float) (-35 / (180/Math.PI));
		jd.upperAngle = (float) (50 / (180/Math.PI));
		jd.initialize(this.body, this.legBody, new Vector2(this.body.getPosition().x-0.13f, this.body.getPosition().y+0.145f));		
		legJoint = world.createJoint(jd);
		jd.lowerAngle = (float) (-45 / (180/Math.PI));
		jd.upperAngle = (float) (30 / (180/Math.PI));
		jd.initialize(this.body, this.handBody, new Vector2(this.body.getPosition().x+0.32f, this.body.getPosition().y+0.22f));
		handJoint = world.createJoint(jd);
		
		jd.lowerAngle = (float) (0 / (180/Math.PI));
		jd.upperAngle = (float) (0 / (180/Math.PI));
		
		WeldJointDef wjd = new WeldJointDef();
		
		wjd.initialize(this.body, this.headBody, new Vector2(this.body.getPosition().x+0.54f, this.body.getPosition().y+0.41f));
		this.headJoint = world.createJoint(wjd);
		
		this.headBody.setUserData(this);

	}
	
	
	
	@Override
	public void draw(SpriteBatch batch, float delta) {
		if(!this.isDisposed() && this.isVisible()) {
			super.draw(batch, delta);				
				
			if(this.visible && this.firstTimeSmoothened && !this.isDisposed()) {
				if(!GameScreen.paused) {
					this.headStateTime += delta;
					if(this.headOpenTimer > 0f) this.headOpenTimer -= delta;
					if(this.headOpenTimer <= 0f && this.headAnimation == this.headAnimationRedOpen) {
						this.closeMouth();
					}
				}
				
				if(this.headAnimation.isAnimationFinished(this.headStateTime)) {
					this.headStateTime = 0f;
				}
				this.currentRegion = (AtlasRegion) this.headAnimation.getKeyFrame(this.headStateTime);
				
				if(this.body != null && this.currentRegion != null) {
					this.headSprite.setRegion(this.currentRegion);
					this.headSprite.draw(batch); //actually draw the image
				}
				
				if(legSprite!=null && legBody != null) legSprite.draw(batch);
				if(handSprite!=null && handBody != null) handSprite.draw(batch);
			}
		}
	}
	
	public void openMouth() {
		this.headOpenTimer = this.headOpenTimerOriginal;
		this.headAnimation = this.headAnimationRedOpen;
	}
	
	public void closeMouth() {
		this.headOpenTimer = 0f;
		if(this.invincible > 0f) this.headAnimation = this.headAnimationInvClosed;
		else if(this.fireModeEnabled) this.headAnimation = this.headAnimationRedClosed;
		else this.headAnimation = this.headAnimationGreenClosed;
	}
	
	
	
	@Override
	public void smoothStates(float fixedTimestepAccumulatorRatio) {
		if(!this.isDisposed()) {
			float oneMinusRatio = 1.0f - fixedTimestepAccumulatorRatio;
			
			super.smoothStates(fixedTimestepAccumulatorRatio);
			
			Vector2 position = legBody.getPosition().sub(legsOrigin);
			legSprite.setPosition((fixedTimestepAccumulatorRatio * position.x) + (oneMinusRatio * this.previousPositionLegs.x), (fixedTimestepAccumulatorRatio * position.y) + (oneMinusRatio * this.previousPositionLegs.y));
			legSprite.setRotation(legBody.getAngle() * MathUtils.radiansToDegrees * fixedTimestepAccumulatorRatio + oneMinusRatio * this.previousAngleLegs);
			legSprite.setOrigin(legsOrigin.x, legsOrigin.y);
			
			position = handBody.getPosition().sub(handsOrigin);
			handSprite.setPosition((fixedTimestepAccumulatorRatio * position.x) + (oneMinusRatio * this.previousPositionHands.x), (fixedTimestepAccumulatorRatio * position.y) + (oneMinusRatio * this.previousPositionHands.y));
			handSprite.setRotation(handBody.getAngle() * MathUtils.radiansToDegrees * fixedTimestepAccumulatorRatio + oneMinusRatio * this.previousAngleHands);
			handSprite.setOrigin(handsOrigin.x, handsOrigin.y);
			
			position = headBody.getPosition().sub(headOrigin);
			headSprite.setPosition((fixedTimestepAccumulatorRatio * position.x) + (oneMinusRatio * this.previousPositionHead.x), (fixedTimestepAccumulatorRatio * position.y) + (oneMinusRatio * this.previousPositionHead.y));
			headSprite.setRotation(headBody.getAngle() * MathUtils.radiansToDegrees * fixedTimestepAccumulatorRatio + oneMinusRatio * this.previousAngleHead);
			headSprite.setOrigin(headOrigin.x, headOrigin.y);
			
		}
	}
	
	@Override
	public void resetSmoothStates() {
		if(!this.isDisposed()) {
			
			super.resetSmoothStates();
			
			Vector2 position = legBody.getPosition().sub(legsOrigin);
			this.previousPositionLegs.set(position.x, position.y);
			this.previousAngleLegs = legBody.getAngle()*MathUtils.radiansToDegrees;
			legSprite.setPosition(this.previousPositionLegs.x, this.previousPositionLegs.y);
			legSprite.setRotation(this.previousAngleLegs);
			legSprite.setOrigin(legsOrigin.x, legsOrigin.y);
			
			position = handBody.getPosition().sub(handsOrigin);
			this.previousPositionHands.set(position.x, position.y);
			this.previousAngleHands = handBody.getAngle()*MathUtils.radiansToDegrees;
			handSprite.setPosition(this.previousPositionHands.x, this.previousPositionHands.y);
			handSprite.setRotation(this.previousAngleHands);
			handSprite.setOrigin(handsOrigin.x, handsOrigin.y);
			
			position = headBody.getPosition().sub(headOrigin);
			this.previousPositionHead.set(position.x, position.y);
			this.previousAngleHead = headBody.getAngle()*MathUtils.radiansToDegrees;
			headSprite.setPosition(this.previousPositionHead.x, this.previousPositionHead.y);
			headSprite.setRotation(this.previousAngleHead);
			headSprite.setOrigin(headOrigin.x, headOrigin.y);
		}
	}
	
	
	public void reduceEnergy() {
		if(this.invincible <= 0f) {
			this.energy--;
			this.invincible = this.invincibleTimeAfterHurt;
			if(this.energy > 0) {
				GameAssets.playSound(GameAssets.fetchSound("game/sounds/energylose.mp3"));
				GameAssets.vibrate(350);
			}
			if(this.energy < 2) {
				this.disableFireMode();
			}
		}
	}
	
	public TextureRegion getLiveTexture() {
		if(this.energy <= 1) return this.liveTexture1;
		else if(this.energy == 2) return this.liveTexture2;
		else if(this.energy == 3) return this.liveTexture3;
		else if(this.energy > 3) return this.liveTexture4;
		else return null;
	}
	
	public short getEnergy() {
		return this.energy;
	}
	
	public void incrementEnergy() {
		GameAssets.playSound(GameAssets.fetchSound("game/sounds/powerup.mp3"),0.8f);
		GameAssets.vibrate(200);
		this.energy++;
	}
	
	public void setEnergy(short e) {
		this.energy = e;
	}
	
	public void resetGraphicsFireballPool(TextureAtlas atlas) {
		Draco.fireballPool.resetGraphics(atlas);
	}
	
	@Override
	public void resetGraphics(TextureAtlas atlas) {
		super.resetGraphics(atlas);
		
		this.legSprite = new Sprite(this.atlas.findRegion("legs_green"));
		this.legSprite.setSize(Configuration.VIEWPORT_WIDTH * this.atlas.findRegion("legs_green").getRegionWidth() / Configuration.TARGET_WIDTH, Configuration.VIEWPORT_HEIGHT * this.atlas.findRegion("legs_green").getRegionHeight() / Configuration.TARGET_HEIGHT);
		
		this.handSprite = new Sprite(this.atlas.findRegion("hands_green"));
		this.handSprite.setSize(Configuration.VIEWPORT_WIDTH * this.atlas.findRegion("hands_green").getRegionWidth() / Configuration.TARGET_WIDTH, Configuration.VIEWPORT_HEIGHT * this.atlas.findRegion("hands_green").getRegionHeight() / Configuration.TARGET_HEIGHT);
		
		this.headAnimationGreenClosed = new Animation(1/25f,this.atlas.findRegions("draco_head_green"));
		this.headAnimationRedClosed = new Animation(1/25f,this.atlas.findRegions("draco_head_red"));
		this.headAnimationInvClosed = new Animation(1/25f,this.atlas.findRegions("draco_head_inv"));
		this.headAnimationRedOpen = new Animation(1/25f,this.atlas.findRegions("draco_head_open"));
		this.headAnimation = this.headAnimationGreenClosed;
		this.headSprite = new Sprite(this.headAnimation.getKeyFrame(this.headStateTime));
		this.headSprite.setSize(Configuration.VIEWPORT_WIDTH * this.headAnimation.getKeyFrame(this.headStateTime).getRegionWidth() / Configuration.TARGET_WIDTH, Configuration.VIEWPORT_HEIGHT * this.headAnimation.getKeyFrame(this.headStateTime).getRegionHeight() / Configuration.TARGET_HEIGHT);
		
		if(this.fireModeEnabled) {
			this.changeAnimation("draco_red",false, Animation.PlayMode.NORMAL);
			this.headAnimation = this.headAnimationRedClosed;			
			this.handSprite.setRegion(this.atlas.findRegion("hands_red"));
			this.legSprite.setRegion(this.atlas.findRegion("legs_red"));
		}
		
		this.liveTexture1 = this.atlas.findRegion("chili_empty");
		this.liveTexture2 = this.atlas.findRegion("chili_one");
		this.liveTexture3 = this.atlas.findRegion("chili_two");
		this.liveTexture4 = this.atlas.findRegion("chili_three");
	}
	
	public void enableFireMode(boolean silent) {
		if(!this.isDisposed()) { 
			this.fireModeEnabled = true;
			if(fire!=null) this.fire.hidden = !this.fireModeEnabled;
			
			this.changeAnimation("draco_red",false, Animation.PlayMode.NORMAL);
			
			this.headAnimation = this.headAnimationRedClosed;			
			this.handSprite.setRegion(this.atlas.findRegion("hands_red"));
			this.legSprite.setRegion(this.atlas.findRegion("legs_red"));
			
			if(silent == false) {
				if(this.energy <= 3) this.incrementEnergy();
				else GameAssets.playSound(GameAssets.fetchSound("game/sounds/already_energy.mp3"),0.8f);
			}
		}
	}
	
	public void disableFireMode() {
		if(!this.isDisposed()) {
			this.fireModeEnabled = false;
			if(fire!=null) this.fire.hidden = !this.fireModeEnabled;
			
			this.changeAnimation("draco_green",false, Animation.PlayMode.NORMAL);
			
			this.headAnimation = this.headAnimationGreenClosed;
			
			this.handSprite.setRegion(this.atlas.findRegion("hands_green"));
			this.legSprite.setRegion(this.atlas.findRegion("legs_green"));
		}
	}
	
	public void toggleFireMode() {
		if(this.fireModeEnabled) this.disableFireMode();
		else this.enableFireMode(false);
	}
	
	
	@Override
	public void doMotionLogic(float delta) {
		if(!this.isDisposed()) {
			
			//deal with invinciblity
			if(this.invincible > 0f) {
				if(this.invincibleTextureSwitched == false) {
					this.changeAnimation("draco_inv",false, Animation.PlayMode.NORMAL);
					
					this.headAnimation = this.headAnimationInvClosed;
					
					this.handSprite.setRegion(this.atlas.findRegion("hands_inv"));
					this.legSprite.setRegion(this.atlas.findRegion("legs_inv"));
					this.invincibleTextureSwitched = true;
				}
				this.invincible -= delta;
			}
			if(this.invincible <= 0 && this.invincibleTextureSwitched == true) {
				if(this.getEnergy() <= 1) {
					this.changeAnimation("draco_green",false, Animation.PlayMode.NORMAL);
					
					this.headAnimation = this.headAnimationGreenClosed;
					
					this.handSprite.setRegion(this.atlas.findRegion("hands_green"));
					this.legSprite.setRegion(this.atlas.findRegion("legs_green"));
				}
				else {
					this.changeAnimation("draco_red",false, Animation.PlayMode.NORMAL);
					
					this.headAnimation = this.headAnimationRedClosed;
					
					this.handSprite.setRegion(this.atlas.findRegion("hands_red"));
					this.legSprite.setRegion(this.atlas.findRegion("legs_red"));
				}
				this.invincibleTextureSwitched = false;
			}
			
			//deal with auto fire and keyboard input
			
			if(Configuration.inputType == 1) {
				
				if(GameAssets.nativ.isControllerConnected() || Gdx.app.getType() == ApplicationType.Desktop || Gdx.app.getType() == ApplicationType.WebGL)
				{
					this.leftPressed = GameAssets.nativ.pollControllerButtonState(GameAssets.KEY_LEFT);
					this.rightPressed = GameAssets.nativ.pollControllerButtonState(GameAssets.KEY_RIGHT);
					this.upPressed = GameAssets.nativ.pollControllerButtonState(GameAssets.KEY_UP);
					this.downPressed = GameAssets.nativ.pollControllerButtonState(GameAssets.KEY_DOWN);
					this.firePressed = GameAssets.nativ.pollControllerButtonState(GameAssets.KEY_PRIMARY);
					
					if(this.leftPressed == false) this.leftPressed = GameAssets.nativ.pollControllerAxis(GameAssets.AXIS_X) < -0.20f;
					if(this.rightPressed == false) this.rightPressed = GameAssets.nativ.pollControllerAxis(GameAssets.AXIS_X) > 0.20f;
					if(this.downPressed == false) this.downPressed = GameAssets.nativ.pollControllerAxis(GameAssets.AXIS_Y) > 0.20f;
					if(this.upPressed == false) this.upPressed = GameAssets.nativ.pollControllerAxis(GameAssets.AXIS_Y) < -0.20f;
				}				
			}
			if(Configuration.autoFire == true) {
				this.firePressed = true;
			}
			
			
			
			
	
			/***************************MOVEMENT PROCESSING*************************/
			
			//Sebis heigeliebter Wunsch mit hinten nicht rausfahren knnen:
			if(this.body.getPosition().x <= 1.53f) {
				this.leftPressed = false;
				this.body.applyForceToCenter(this.appliedForce.set(35f,0f), true);
			}
			
			
			//ANGLE OPERATIONS
			if(this.body.getAngle() < this.gradientAngle * MathUtils.degreesToRadians * (-1)) { //FIX ANGLE TO LOWER LIMIT
				this.body.setTransform(body.getPosition(), this.gradientAngle * MathUtils.degreesToRadians * (-1));
			}
			if(this.body.getAngle() > this.gradientAngle * MathUtils.degreesToRadians) { // FIX ANGLE TO UPPER LIMIT
				this.body.setTransform(body.getPosition(), this.gradientAngle * MathUtils.degreesToRadians);
			}		
			
			//TURN DRAGON DEPENDING ON THE ACTUAL VELOCITY AND DIRECTION:
			//if((Math.abs(this.body.getLinearVelocity().x) > 1f || Math.abs(this.body.getLinearVelocity().y) > 1f) && ((Math.abs(this.body.getLinearVelocity().y) - Math.abs(this.body.getLinearVelocity().x) > -1f) || (Math.abs(this.body.getLinearVelocity().y) - Math.abs(this.body.getLinearVelocity().x) < 1f)))
			if(this.upPressed || this.downPressed || this.leftPressed || this.rightPressed) //turn dragon not for velocity-reasons, but for touched buttons 
			{					
				if(this.upPressed || this.leftPressed) {
					//nach oben neigen
					if((this.body.getAngle() * MathUtils.radiansToDegrees) <= this.gradientAngle) {	
	                    this.body.setTransform(body.getPosition(), body.getAngle()+(this.turnDegreesPerSecond*delta*MathUtils.degreesToRadians));
	                }
				}
				
				if(this.downPressed || this.rightPressed) {
					//nach unten neigen
					if((body.getAngle() * MathUtils.radiansToDegrees) >= (this.gradientAngle*-1)) {	
						body.setTransform(body.getPosition(), body.getAngle()-(this.turnDegreesPerSecond*delta*MathUtils.degreesToRadians));
	                }
				}
				
				//NEIGUNG NACH GESCHWINDIGKEIT ERMESSEN:
				/*if((this.upPressed || this.downPressed) && (this.leftPressed || this.rightPressed)) {
				    float x = this.body.getLinearVelocity().x;
				    float y = this.body.getLinearVelocity().y;
				    
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
				}
				*/
				if(this.upPressed && this.rightPressed) {
					//nach oben neigen
					if((this.body.getAngle() * MathUtils.radiansToDegrees) <= this.gradientAngle) {	
	                    this.body.setTransform(body.getPosition(), body.getAngle()+(this.turnDegreesPerSecond*delta*MathUtils.degreesToRadians));
	                }
				}
				
				if(this.downPressed && this.leftPressed) {
					//nach unten neigen
					if((body.getAngle() * MathUtils.radiansToDegrees) >= (this.gradientAngle*-1)) {	
						body.setTransform(body.getPosition(), body.getAngle()-(this.turnDegreesPerSecond*delta*MathUtils.degreesToRadians));
	                }
				}
				
				
			} else { //TURN DRAGON BACK:				
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
			
			
			
			
			//PROCESS UP MOVEMENT
			if(this.upPressed) {
				this.upCounter += delta;
				if(up!=null) up.setDrawTarget(false); //Button richtig stellen...	
				if(this.leftPressed || this.rightPressed) this.body.applyForceToCenter(this.appliedForce.set(0f, this.forceToApply/3*2),true);
				else {
					if(this.upCounter < this.timeStartForce) this.body.applyForceToCenter(this.appliedForce.set(0f, this.forceToApplyStart),true);
					else this.body.applyForceToCenter(this.appliedForce.set(0f, this.forceToApply), true);
				}
		    } else { 
		    	if(up!=null) up.setDrawTarget(true);
		    	this.upCounter = 0f;
		    }
			
			
			//PROCESS DOWN MOVEMENT
			if(this.downPressed) {
				this.downCounter += delta;
				if(down!=null) down.setDrawTarget(false);
				if(this.leftPressed || this.rightPressed) this.body.applyForceToCenter(this.appliedForce.set(0f, this.forceToApply/3*2*(-1)),true);
				else {
					if(this.downCounter < this.timeStartForce) this.body.applyForceToCenter(this.appliedForce.set(0f, this.forceToApplyStart*(-1)),true);
					else this.body.applyForceToCenter(this.appliedForce.set(0f, this.forceToApply*(-1)),true);
				}
		    } else { 
		    	if(down!=null) down.setDrawTarget(true);
		    	this.downCounter = 0f;
		    }
			
	
			//PROCESS RIGHT MOVEMENT
			if(this.rightPressed) {
				this.rightCounter += delta;
				if(right!=null) right.setDrawTarget(false);
				if(this.upPressed || this.downPressed) this.body.applyForceToCenter(this.appliedForce.set(this.forceToApply/3*2,0f),true);
				else {
					if(this.rightCounter < this.timeStartForce) this.body.applyForceToCenter(this.appliedForce.set(this.forceToApplyStart,0f),true);
					else this.body.applyForceToCenter(this.appliedForce.set(this.forceToApply,0f),true);
				}
		    } else { 
		    	if(right!=null) right.setDrawTarget(true);
		    	this.rightCounter = 0f;
		    }
			
			
			//PROCESS LEFT MOVEMENT
			if(this.leftPressed) {
				this.leftCounter += delta;
				if(left!=null) left.setDrawTarget(false);
				if(this.upPressed || this.downPressed) this.body.applyForceToCenter(this.appliedForce.set(this.forceToApply/3*2*(-1),0f),true);
				else {
					if(this.leftCounter < this.timeStartForce) this.body.applyForceToCenter(this.appliedForce.set(this.forceToApplyStart*(-1.4f),0f),true);
					else this.body.applyForceToCenter(this.appliedForce.set(this.forceToApply*(-1.4f),0f),true);
				}
		    } else { 
		    	if(left!=null) left.setDrawTarget(true);
		    	leftCounter = 0f;
		    }
			
			
			
			//PROCESS FLYING SOUND
			if(this.upPressed || this.downPressed || this.leftPressed || this.rightPressed) {
				if(this.wingCounter  <= 0f) {
					GameAssets.playSound(this.wingSound);
					this.wingCounter = this.secondsBetweenWingSounds;
				}
			}
			if(this.wingCounter > 0f) this.wingCounter -= delta;
			
			
			//CHECK_DEAD
			if(this.energy < 1) {
				this.dispose();
			}		
		}
	}
	
	
	public void processFireKey(GameObjectCollection objects, float deltaTime) {
		if(!this.isDisposed()) {
			//PROCESS FIRE KEY
			if(this.fireModeEnabled) {
				if(this.firePressed) {
					if(fire!=null) fire.setDrawTarget(false);
					if(this.fireCounter  <= 0f && this.invincible <= 0f) {
						if(this.invincible <= 0f) this.openMouth();
						Fireball f = null;						
						if(this.energy > 2 && this.energy < 4) {
							f = (Fireball) fireballPool.obtain();
							f.setPosition(this.headBody.getPosition().add(0.6f, 0f));
							f.setVelocity(24f, 1f);
							f.setPool(fireballPool);
							f.setDraco(this);
							objects.add(f);
							
							f = (Fireball) fireballPool.obtain();
							f.setPosition(this.headBody.getPosition().add(0.6f, 0f));
							f.setVelocity(24f, -1f);
							f.setPool(fireballPool);
							f.setDraco(this);
							objects.add(f);							
						} else if(this.energy > 3) {
							f = (Fireball) fireballPool.obtain();
							f.setPosition(this.headBody.getPosition().add(0.6f, 0f));
							f.setVelocity(24f, 2.5f);
							f.setPool(fireballPool);
							f.setDraco(this);
							objects.add(f);
							
							f = (Fireball) fireballPool.obtain();
							f.setPosition(this.headBody.getPosition().add(0.6f, 0f));
							f.setVelocity(24f, -2.5f);
							f.setPool(fireballPool);
							f.setDraco(this);
							objects.add(f);
							
							f = (Fireball) fireballPool.obtain();
							f.setPosition(this.headBody.getPosition().add(0.6f, 0f));
							f.setVelocity(24f, 0f);
							f.setPool(fireballPool);
							f.setDraco(this);
							objects.add(f);
						} else {
							f = (Fireball) fireballPool.obtain();
							f.setPosition(this.headBody.getPosition().add(0.6f, 0f));
							f.setVelocity(24f, 0f);
							f.setPool(fireballPool);
							f.setDraco(this);
							objects.add(f);
						}
						
						GameAssets.playSound(this.fireSound, Configuration.autoFire?0.25f:0.50f);
						this.fireCounter = this.secondsBetweenShoots;
					}
				} else {
					if(fire!=null) fire.setDrawTarget(true);
				}
				if(this.fireCounter > 0f) this.fireCounter -= deltaTime;
			}
		}
	}

	
	public void incrementEnemiesKilled(int howMuch) {
		this.enemiesKilled += howMuch;
	}
	
	public int getEnemiesKilled() {
		return this.enemiesKilled;
	}
	
	public void resetEnemeisKilled() {
		this.enemiesKilled = 0;
	}
	
	public void setInvincible(float time) {
		this.invincible = time;
	}
	
	public void startInvincibleMode() {
		this.invincible = 25f;
	}
	
	public void endInvincibleMode() {
		this.invincible = 0f;
	}
	
	public float getInvincible() {
		return this.invincible;
	}
	
	public void checkIfOutlaw() {
		if(this.isDisposed() == false) {
			if(this.getLeftBottom().y > Configuration.VIEWPORT_HEIGHT || this.getRightTop().x < 0 || this.getRightTop().y < 0 ) {
				this.dispose();
			}
		}
	}
	
	
	public void dispose() {
		this.down = null;
		this.up = null;
		this.left = null;
		this.right = null;
		this.fire = null;
		if(!this.isDisposed()) {
			world.destroyJoint(this.legJoint);
			world.destroyJoint(this.handJoint);
			world.destroyJoint(this.headJoint);
			world.destroyBody(this.handBody);
			world.destroyBody(this.headBody);
			world.destroyBody(this.legBody);
		}
		fireballPool.dispose();
		this.headAnimation = null;
		this.headAnimationGreenClosed = null;
		this.headAnimationInvClosed = null;
		this.headAnimationRedClosed = null;
		this.headAnimationRedOpen = null;
		this.legJoint = null;
		this.handJoint = null;
		this.headJoint = null;
		this.handBody = null;
		this.legBody = null;
		this.legsOrigin = null;
		this.legSprite = null;
		this.handsOrigin = null;
		this.handSprite = null;
		this.energy = 0;
		super.dispose();
	}

}
