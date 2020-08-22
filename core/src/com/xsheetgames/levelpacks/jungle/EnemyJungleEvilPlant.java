package com.xsheetgames.levelpacks.jungle;

import aurelienribon.tweenengine.TweenManager;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.xsheetgames.GameAssets;
import com.xsheetgames.genericElements.AbstractLevelpack;
import com.xsheetgames.genericGameObjects.Enemy;
import com.xsheetgames.genericGameObjects.GameObjectCollection;
import com.xsheetgames.genericGameObjects.GameObjectPool;

public class EnemyJungleEvilPlant extends Enemy {

	public static GameObjectPool ResinDropPool = null;
	
	private float resinCounter = 1f;
	private GameObjectCollection fireballs;
	private AbstractLevelpack pack;
	
	public EnemyJungleEvilPlant(World world, float x, float y, Vector2 vel, TweenManager m) {
		
		super(world, GameAssets.fetchTextureAtlas("jungle/images/jungle_objects.pack"), 0f, vel, new Vector2(x,y),false,m);
		
		this.dieTextureName = "evil_plant_enm";
		this.dieSound = GameAssets.fetchSound("jungle/sounds/plant.mp3");
		this.energy = 5;
		this.enemyPoints = 0;
		
		this.maskBits = 0;
		
		this.deadXOffset = 0f;
		this.deadYOffset = 0f;
		this.gradientAngle = 0f;
		
		super.init(JungleLevelPack.objectLoader, "evil_plant_enm", "evil_plant_spitting");
		this.setAsSensor(true);
		this.body.setFixedRotation(true);
	}
	
	@Override
	public void doMotionLogic(float delta) {
		if(this.energy == 0 && this.dead == false) {
			this.die();
		}
		
		this.resinCounter-= delta;
		if(this.resinCounter <= 0) {
			this.spitResin();
			this.resinCounter = 1f;
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
	}
	
	public void setSpitResources(AbstractLevelpack pack, GameObjectCollection fireballs) {
		this.fireballs = fireballs;
		this.pack = pack;
	}
	
	public void spitResin() {
		
		ResinDrop b = (ResinDrop) EnemyJungleEvilPlant.ResinDropPool.obtain();
		b.setPosition(this.body.getPosition());
		b.setVelocity(new Vector2(pack.getActualLevel().getLevelSpeed()*-1,-6f));
		b.setPool(EnemyJungleEvilPlant.ResinDropPool);		
		this.fireballs.add(b);
	}
	
	@Override
	public void reset() {
		super.reset();
		this.setAsSensor(true);
		this.body.setFixedRotation(true);
	}
	
}
