package com.xsheetgames.levelpacks.batmine;

import aurelienribon.tweenengine.TweenEquation;
import aurelienribon.tweenengine.TweenManager;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.xsheetgames.Configuration;
import com.xsheetgames.genericGameObjects.Enemy;
import com.xsheetgames.genericGameObjects.EnemyCollection;
import com.xsheetgames.genericGameObjects.EnemyFactory;
import com.xsheetgames.genericGameObjects.GameObjectPool;
import com.xsheetgames.screens.GameScreen;

public class BatMineEnemies extends EnemyCollection {
	
	public static GameObjectPool bat1Pool;
	public static GameObjectPool bat2Pool;
	public static GameObjectPool bossBatPool;
	
	
	public BatMineEnemies(World world, TweenManager t) {
		super(world, t);
		bat1Pool = new GameObjectPool(11, 8, new EnemyFactory(EnemyBat1.class, world, t));
		bat2Pool = new GameObjectPool(4, 8, new EnemyFactory(EnemyBat2.class, world, t));
		bossBatPool = new GameObjectPool(1, 1, new EnemyFactory(BossBat.class, world, t));
		BossBat.SpinningBonePool = new GameObjectPool(6, 10, new SpinningBoneFactory(SpinningBone.class, world));
		
		pools.add(bat1Pool);
		pools.add(bat2Pool);
		//pools.add(bossBatPool);
		//pools.add(BossBat.SpinningBonePool);
		
		super.preFillPools();
	}
	
	
	
	@Override
	public void spawnEnemy(String name, float y, float ySpeed, float xSpeed, float motionDuration, float motionPeculiarity, String motionEquation, boolean motionInfinite, GameScreen gs) {
		
		Enemy enemy = null;		
		TweenEquation eq = super.resolveEquation(motionEquation);
		
		
		if(name.equals("Bat1")) {
			enemy = (Enemy) bat1Pool.obtain();
			enemy.setPool(bat1Pool);
		}
		
		if(name.equals("Bat2")) {
			enemy = (Enemy) bat2Pool.obtain();
			enemy.setPool(bat2Pool);
		}
		
		if(name.equals("Bossbat")) {
			enemy = (Enemy) bossBatPool.obtain();
			((BossBat)enemy).setShootResources(gs.getLevelpack(), gs.getActualFireballs());
	    	enemy.setPool(bossBatPool);
	    	if(GameScreen.bossEnergyMeter != null) GameScreen.bossEnergyMeter.registerHurtable(enemy);
		}
		
		enemy.setPosition(new Vector2(25.0f,y));
		enemy.setVelocity(new Vector2(xSpeed*-1, ySpeed));
		enemy.setFunnyMotion( motionDuration,motionPeculiarity,eq);
		
		this.add(enemy);
		if(Configuration.debugLevel >= Application.LOG_INFO && Configuration.spawnInfos) Gdx.app.log("Enemy spawn successful","Enemy-Hash: " + enemy.hashCode());
		this.enemyCounter += enemy.getEnemyPoints();
		
	}
	
	
	@Override
	public void dispose() {
		bat1Pool.clear();
		bat2Pool.clear();
		BossBat.SpinningBonePool.clear();
		bossBatPool.clear();
		super.dispose();
	}

}
