package com.xsheetgames.levelpacks.jungle;

import aurelienribon.tweenengine.TweenManager;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.WeldJointDef;
import com.xsheetgames.Configuration;
import com.xsheetgames.GameAssets;
import com.xsheetgames.genericElements.AbstractLevelpack;
import com.xsheetgames.genericGameObjects.Enemy;
import com.xsheetgames.genericGameObjects.GameObject;
import com.xsheetgames.genericGameObjects.GameObjectCollection;
import com.xsheetgames.genericGameObjects.GameObjectPool;
import com.xsheetgames.genericGameObjects.Hurtable;
import com.xsheetgames.screens.GameScreen;

public class BossSnake extends Enemy {
	
	
	//INNER SNAKE DRAWING STUFF
	protected float snakeStateTime;
	protected Animation<TextureRegion> snakeAnimation;
	protected TextureRegion currentSnakeRegion;
	protected Sprite snakeSprite;
	protected boolean snakeVisible = true;
	protected String currentSnakeAnimationName;
	private Animation snakeOpenLoop, snakeClosedLoop, snakeClosing, snakeOpening, snakeSpitting;
	private String snakeOpenName, snakeClosedName, snakeClosingName, snakeOpeningName, snakeSpittingName;
	private float addToXSprite = 2.7f, addToYSprite = 0.17f;
	
	//INNER SNAKE BOX2D
	private Body snakeBody;
	private Joint snakeJoint;
	protected Vector2 previousPositionSnake = new Vector2();
	protected float previousAngleSnake;
	private Vector2 snakeOrigin;
	
	
	
	public static GameObjectPool VenomPool = null;
	
	private float openTimer = 6f;
	private float openTimerInit = 6f;
	
	private float closeTimer = 5f;
	private float closeTimerInit = 5f;
	
	private boolean opened = false;
	private boolean closing = false, opening = false;
	
	private int forwardCounter = 5;
	private int forwardCounterInit = 5;
	private boolean beginForwardMotion = false;
	
	private float shootCounter = 2f;
	private float shootCounterInit = 2f;
	private float spitDelay = 0.23f;
	private float spitDelayInit = 0.23f;
	private boolean spitInitiated = false;
	private Vector2 shootOrigin = new Vector2();

	private float shootYdirection = 0f;	
	
	private GameObjectCollection venomCollection;
	private AbstractLevelpack pack;

	public BossSnake(World world, float x, float y, Vector2 vel, TweenManager m) {
		
		super(world, GameAssets.fetchTextureAtlas("jungle/images/jungle_boss.pack"), 0f, vel, new Vector2(x,y),false,m);
		
		this.dieTextureName = "snake_boss_snake_dying";
		this.dieSound = GameAssets.fetchSound("jungle/sounds/boss.mp3");
		this.energy = 20;
		this.enemyPoints = 50;
		
		this.deadXOffset = 0f;
		this.deadYOffset = 0f;
		
		snakeOpenName = "snake_boss_snake_loop";
		snakeClosedName = "snake_inside";
		snakeClosingName = "snake_in";
		snakeOpeningName = "snake_out";
		snakeSpittingName = "snake_spit";
		
		snakeOpenLoop = new Animation(1f/ 25f, this.atlas.findRegions(this.snakeOpenName), Animation.PlayMode.NORMAL);
		snakeClosedLoop = new Animation(1f/ 25f, this.atlas.findRegions(this.snakeClosedName), Animation.PlayMode.NORMAL);
		snakeClosing = new Animation(1f/ 25f, this.atlas.findRegions(this.snakeClosingName), Animation.PlayMode.NORMAL);
		snakeOpening = new Animation(1f/ 25f, this.atlas.findRegions(this.snakeOpeningName), Animation.PlayMode.NORMAL);
		snakeSpitting = new Animation(1f/ 25f, this.atlas.findRegions(this.snakeSpittingName), Animation.PlayMode.NORMAL);
		
		this.snakeAnimation = snakeClosedLoop;
		this.currentSnakeAnimationName = snakeClosedName;
		this.snakeStateTime = 0f;
		this.snakeSprite = new Sprite(this.snakeAnimation.getKeyFrame(this.snakeStateTime));
		this.snakeSprite.setSize(Configuration.VIEWPORT_WIDTH * this.snakeAnimation.getKeyFrame(this.snakeStateTime).getRegionWidth() / Configuration.TARGET_WIDTH, Configuration.VIEWPORT_HEIGHT * this.snakeAnimation.getKeyFrame(this.snakeStateTime).getRegionHeight() / Configuration.TARGET_HEIGHT);
		
		
		super.init(JungleLevelPack.objectLoader, "bosssnake_cover", "snake_boss_basket_loop");
		this.body.setFixedRotation(true);
		
		bd.position.set(x+addToXSprite, y+addToYSprite);

		snakeBody = world.createBody(bd);
		snakeBody.setUserData(new BossSnake.BossSnakeWrapper(this));
		
		fd.density = 1f;
		fd.filter.maskBits = 30; //do not collide with walls
		JungleLevelPack.objectLoader.attachFixture(snakeBody, "bosssnake_hurtable", fd, snakeSprite.getWidth());
		snakeOrigin = JungleLevelPack.objectLoader.getOrigin("bosssnake_hurtable", snakeSprite.getWidth()).cpy();
		
		WeldJointDef wjd = new WeldJointDef();
		wjd.initialize(this.body, this.snakeBody, new Vector2(this.body.getPosition().x+addToXSprite, this.body.getPosition().y+addToYSprite));
		this.snakeJoint = world.createJoint(wjd);
		this.setSnakeSensor(true);
		this.woundedSound = GameAssets.fetchSound("jungle/sounds/bosswounded.mp3");
		
		this.initWoundedCounter = 0.4f;
		this.invincibleTimeAfterHurtInit = 0.4f;
	}
	
	
	
	public void closeBox() {
		if(closing == false) {
			this.changeSnakeAnimation(this.snakeClosing, this.snakeClosingName);
			this.setSnakeSensor(true);
			closing = true;
		}
	}
	
	public void openBox() {
		if(opening == false) {
			this.changeSnakeAnimation(this.snakeOpening, this.snakeOpeningName);
			opening = true;
		}
	}
	
	public void InitiateOpenLoop() {
		this.changeSnakeAnimation(this.snakeOpenLoop, this.snakeOpenName);
		this.setSnakeSensor(false);
		opened = true;
		opening = false;
	}
	
	public void InitiateCloseLoop() {
		this.changeSnakeAnimation(this.snakeClosedLoop, this.snakeClosedName);
		opened = false;
		closing = false;
	}
	
	public void setSnakeSensor(boolean sensor) {
		for(Fixture f : this.snakeBody.getFixtureList()) {
			f.setSensor(sensor);			
		}
	}
	
	
	public void changeSnakeAnimation(Animation newCurrentAnimation, String currentAnimationName) {
		this.snakeAnimation = newCurrentAnimation;
		this.currentSnakeAnimationName = currentAnimationName;
		this.snakeStateTime = 0f;
		this.currentSnakeRegion = this.snakeAnimation.getKeyFrame(this.snakeStateTime);
		this.snakeSprite.setSize(Configuration.VIEWPORT_WIDTH * this.currentSnakeRegion.getRegionWidth() / Configuration.TARGET_WIDTH, Configuration.VIEWPORT_HEIGHT * this.currentSnakeRegion.getRegionHeight() / Configuration.TARGET_HEIGHT);
	}
	
	
	public void setShootResources(AbstractLevelpack pack, GameObjectCollection fireballs) {
		this.venomCollection = fireballs;
		this.pack = pack;
	}
	
	@Override
	public void setBlack() {
		this.snakeSprite.setColor(this.woundR,this.woundG,this.woundB,0.95f);
	}
	
	@Override
	public void setOriginalColor() {
		this.snakeSprite.setColor(this.spriteColor);
	}
	
	@Override
	public boolean isBlack() {
		if(this.snakeSprite.getColor().equals(this.spriteColor)) return false;
		else return true;
	}
	
	
	@Override
	public void smoothStates(float fixedTimestepAccumulatorRatio) {
		if(!this.isDisposed()) {
			float oneMinusRatio = 1.0f - fixedTimestepAccumulatorRatio;
			
			super.smoothStates(fixedTimestepAccumulatorRatio);
			
			Vector2 position = snakeBody.getPosition().sub(snakeOrigin);
			snakeSprite.setPosition((fixedTimestepAccumulatorRatio * position.x) + (oneMinusRatio * this.previousPositionSnake.x), (fixedTimestepAccumulatorRatio * position.y) + (oneMinusRatio * this.previousPositionSnake.y));
			snakeSprite.setRotation(snakeBody.getAngle() * MathUtils.radiansToDegrees * fixedTimestepAccumulatorRatio + oneMinusRatio * this.previousAngleSnake);
			snakeSprite.setOrigin(snakeOrigin.x, snakeOrigin.y);
		}
	}
	
	@Override
	public void resetSmoothStates() {
		if(!this.isDisposed()) {
			
			super.resetSmoothStates();
			
			Vector2 position = snakeBody.getPosition().sub(snakeOrigin);
			this.previousPositionSnake.set(position.x, position.y);
			this.previousAngleSnake = snakeBody.getAngle()*MathUtils.radiansToDegrees;
			snakeSprite.setPosition(this.previousPositionSnake.x, this.previousPositionSnake.y);
			snakeSprite.setRotation(this.previousAngleSnake);
			snakeSprite.setOrigin(snakeOrigin.x, snakeOrigin.y);
		}
	}
	
	
	
	@Override
	public void obtainInit() {
		super.obtainInit();
	}
	
	@Override
	public void pause() {
		super.pause();
	}
	
	@Override
	public void resume() {
		super.resume();
	}
	
	
	@Override
	public void reset() {
		this.visible = true;
		this.shootYdirection = 0f;
		this.openTimer = this.openTimerInit;
		this.closeTimer = this.closeTimerInit;
		this.opened = false;
		this.closing = false;
		this.opening = false;
		this.forwardCounter = this.forwardCounterInit;
		this.beginForwardMotion = false;
		this.shootCounter = this.shootCounterInit;
		this.spitDelay = this.spitDelayInit;
		this.spitInitiated = false;
		this.energy = 20;
		this.snakeAnimation = snakeClosedLoop;
		this.currentSnakeAnimationName = snakeClosedName;
		this.snakeStateTime = 0f;
		this.setSnakeSensor(true);
		this.snakeVisible = true;
		this.body.setLinearVelocity(0f, 0f);		
		super.reset();
	}
	
	@Override
	public void setPosition(Vector2 pos) {
		world.destroyJoint(this.snakeJoint);
		this.body.setTransform(pos, 0f);
		this.snakeBody.setTransform(pos.x+addToXSprite, pos.y+addToYSprite, 0f);
		this.startPosition = pos;
		WeldJointDef wjd = new WeldJointDef();
		wjd.initialize(this.body, this.snakeBody, new Vector2(pos.x+addToXSprite, pos.y+addToYSprite));
		this.snakeJoint = world.createJoint(wjd);
	}
	

	
	@Override
	public boolean free() {
		if(GameScreen.bossEnergyMeter != null) GameScreen.bossEnergyMeter.unregisterHurtable();
		return super.free();
	}
	
	
	private void doShootSession(float delta) {
		if(this.dead == false) {
			if(this.opened) {
				if(this.shootCounter <= 0f) {
					initiateShoot();
					this.shootCounter = this.shootCounterInit;
				} else this.shootCounter -= delta;
			} else this.shootCounter = 1f;
			
			if(this.spitInitiated) {
				if(this.spitDelay >= 0f) this.spitDelay -= delta;
				else shoot();
			}
		}
	}
	
	private void initiateShoot() {
		this.changeSnakeAnimation(this.snakeSpitting, this.snakeSpittingName);
		this.spitInitiated = true;
		this.spitDelay = this.spitDelayInit;
	}
	
	
	private void shoot() {
		this.spitInitiated = false;
		GameObject b = BossSnake.VenomPool.obtain();
		this.shootOrigin.set(this.body.getPosition().x-2f, this.body.getPosition().y+1.7f);
		b.setPosition(this.shootOrigin);
		b.setVelocity(new Vector2(-28f,this.shootYdirection));
		b.setPool(BossSnake.VenomPool);	
		this.venomCollection.add(b);		
		GameAssets.playSound(GameAssets.fetchSound("jungle/sounds/venomspit.mp3"), 0.55f);		
	}
	
	
	
	@Override
	public void resetGraphics(TextureAtlas atlas) {
		super.resetGraphics(atlas);
		if(this.atlas != null) {
			snakeOpenLoop = new Animation(1f/ 25f, this.atlas.findRegions(this.snakeOpenName), Animation.PlayMode.NORMAL);
			snakeClosedLoop = new Animation(1f/ 25f, this.atlas.findRegions(this.snakeClosedName), Animation.PlayMode.NORMAL);
			snakeClosing = new Animation(1f/ 25f, this.atlas.findRegions(this.snakeClosingName), Animation.PlayMode.NORMAL);
			snakeOpening = new Animation(1f/ 25f, this.atlas.findRegions(this.snakeOpeningName), Animation.PlayMode.NORMAL);
			snakeSpitting = new Animation(1f/ 25f, this.atlas.findRegions(this.snakeSpittingName), Animation.PlayMode.NORMAL);
			
			if(this.currentSnakeAnimationName.equals(snakeOpenName)) this.snakeAnimation = snakeOpenLoop;
			if(this.currentSnakeAnimationName.equals(snakeClosedName)) this.snakeAnimation = snakeClosedLoop;
			if(this.currentSnakeAnimationName.equals(snakeClosingName)) this.snakeAnimation = snakeClosing;
			if(this.currentSnakeAnimationName.equals(snakeOpeningName)) this.snakeAnimation = snakeOpening;
			if(this.currentSnakeAnimationName.equals(snakeSpittingName)) this.snakeAnimation = snakeSpitting;
		}
		if(this.snakeAnimation != null) this.currentSnakeRegion = this.snakeAnimation.getKeyFrame(this.snakeStateTime);
		
		if(this.currentSnakeRegion != null) {
			this.snakeSprite = new Sprite(this.currentSnakeRegion);
			this.snakeSprite.setSize(Configuration.VIEWPORT_WIDTH * this.currentSnakeRegion.getRegionWidth() / Configuration.TARGET_WIDTH, Configuration.VIEWPORT_HEIGHT * this.currentSnakeRegion.getRegionHeight() / Configuration.TARGET_HEIGHT);
		}
	}
	
	
	@Override
	public boolean reduceEnergy() {
		this.forwardCounter--;
		if(this.forwardCounter == 0) {
			this.forwardCounter = this.forwardCounterInit;
			this.beginForwardMotion = true;
		}
		return false;
	}
	
	
	public void draw(SpriteBatch batch, float delta) {		
		
		if(!this.isDisposed() && this.visible && this.firstTimeSmoothened) {
			if(!GameScreen.paused && !this.stateTimeAlreadyIncremented) this.stateTime += delta;
			this.stateTimeAlreadyIncremented = false;
			this.currentRegion = this.animation.getKeyFrame(this.stateTime);
			
			if(this.body != null && this.currentRegion != null) {
				if(this.sprite.getTexture().equals(this.currentRegion) == false)
					this.sprite.setRegion(this.currentRegion);
				
				this.sprite.draw(batch); //actually draw the image
			}
			
			if(this.dead == false) {
				if(this.animation.isAnimationFinished(this.stateTime)) {
					this.stateTime = 0f;
					if(this.looping) this.delayTimer = this.delayTimespan;
				}
			}
		}

		
			
		if(this.dead == false && this.visible && this.snakeVisible && this.firstTimeSmoothened && !this.isDisposed()) {
			if(!GameScreen.paused) {
				this.snakeStateTime += delta;
				
				//Change back from spitting:
				if(this.currentSnakeAnimationName.equals(this.snakeSpittingName)) {
					if(this.snakeAnimation.isAnimationFinished(this.snakeStateTime)) {
						this.changeSnakeAnimation(this.snakeOpenLoop, this.snakeOpenName);
					}
				}
				
				//Open and Close Controlling
				if(opened == true) {
					if(this.openTimer >= 0f) this.openTimer -= delta;
					else if(this.snakeAnimation.isAnimationFinished(this.snakeStateTime)) this.closeBox();
					this.closeTimer = this.closeTimerInit;
				} else {
					if(this.closeTimer >= 0f) this.closeTimer -= delta;
					else this.openBox();
					this.openTimer = this.openTimerInit;
				}
				
				if(closing == true && this.snakeAnimation.isAnimationFinished(this.snakeStateTime)) {
					this.InitiateCloseLoop();
				}
				
				if(opening == true && this.snakeAnimation.isAnimationFinished(this.snakeStateTime)) {
					this.InitiateOpenLoop();
				}
			}
			
			if(this.snakeAnimation.isAnimationFinished(this.snakeStateTime)) {
				this.snakeStateTime = 0f;
			}
			this.currentSnakeRegion = (AtlasRegion) this.snakeAnimation.getKeyFrame(this.snakeStateTime);
			
			if(this.snakeBody != null && this.currentSnakeRegion != null) {
				this.snakeSprite.setRegion(this.currentSnakeRegion);
				this.snakeSprite.draw(batch); //actually draw the image
			}
		}
		
		if(this.setDisposing == true) this.dispose(); //now is the correct time to dispose our object if somwhere arises the need for it.
	}
	
	
	@Override
	public void die() {
		this.snakeVisible = false;
		super.die();
	}
	
	
	
	@Override
	public void doMotionLogic(float delta) {
		
		//Energy Controlling
		if(this.energy == 0 && this.dead == false) {
			this.die();
		}
		
		if(this.invincibleTimeAfterHurt > 0f) this.invincibleTimeAfterHurt -= delta;
		this.doShootSession(delta);
		
		if(this.woundedCounter >= 0f) this.woundedCounter-=delta;
		else {
			if(this.isBlack()) this.setOriginalColor();
		}
		
		if(this.dead == true) {
			if(this.deadPosition == true) {
				if(this.stateTime >= this.animation.getAnimationDuration()/3 && this.body.getFixtureList().get(0).isSensor() == false) {
					this.setAsSensor(true);
				}
				if(this.snakeBody.getFixtureList().get(0).isSensor() == false) this.setSnakeSensor(true);
				if(this.stateTime >= this.animation.getAnimationDuration()) this.body.applyForceToCenter(0f, 60f, true);
			}			
			
			if(this.deadPosition == false) {
				if(this.world.isLocked() == false) {
					this.body.setLinearDamping(2f);
					this.body.setLinearVelocity(0f,0f);
					this.pack.getActualLevel().setRemainingSeconds(7f);
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
			
			
			/*if(vel.x >= -3 && vel.x < 8f) {
				this.body.setLinearVelocity(vel.x+2f, vel.y);
			}*/
			
			/*if(pos.x >= 16.6f && vel.x > 0f) {
				this.body.setLinearVelocity(vel.x-2f, vel.y);
			}*/
			
			if(pos.x <= 0.8f) {
				this.body.setLinearVelocity(5f, vel.y);
			}
			
			if(pos.x >= 13.5f && vel.x > 0f) {
				this.body.setLinearVelocity(0f, vel.y);
			}
			
			if(this.beginForwardMotion == true) {
				this.body.setLinearVelocity(-15f, vel.y);
				this.beginForwardMotion = false;
			}
		}
	}
	
	@Override
	public void dispose() {
		super.dispose();
		
		if(!this.isDisposed()) {
			world.destroyJoint(this.snakeJoint);
			world.destroyBody(this.snakeBody);
			this.snakeJoint = null;
			this.snakeBody = null;
		}
	}
	
	
	
	
	
	
	
	
	
	
	public class BossSnakeWrapper implements Hurtable {
		BossSnake snake;		
		public BossSnakeWrapper(BossSnake snake) {
			this.snake = snake;
		}		
		@Override
		public boolean reduceEnergy() {
			snake.reduceEnergy();
			if(snake.invincibleTimeAfterHurt <= 0f) {
				snake.energy--;
				snake.invincibleTimeAfterHurt = snake.invincibleTimeAfterHurtInit;
				if(snake.energy == 0) snake.die();
				else {
					snake.setBlack();				
					snake.woundedCounter = snake.initWoundedCounter;
					if(snake.woundedSound != null) GameAssets.playSound(snake.woundedSound);
				}
				return true;
			}
			return false;
		}
		@Override
		public int getEnemyPoints() {
			return snake.getEnemyPoints();
		}
		@Override
		public short getEnergy() {
			return snake.getEnergy();
		}
		@Override
		public Body getBody() {
			return snake.getBody();
		}
		@Override
		public short getStartEnergy() {
			return snake.getStartEnergy();
		}		
	}

}
