package com.xsheetgames.levelpacks.jungle;

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

public class JungleEnemies extends EnemyCollection {
	
	public static GameObjectPool bat1Pool;
	public static GameObjectPool bat2Pool;
	public static GameObjectPool flyingPlantPool;
	public static GameObjectPool evilPlantPool;
	public static GameObjectPool bossSnakePool;
	
	
	public JungleEnemies(World world, TweenManager t) {
		super(world, t);
		
		bat1Pool = new GameObjectPool(4, 8, new EnemyFactory(EnemyJungleBat1.class, world, t));		
		bat2Pool = new GameObjectPool(4, 8, new EnemyFactory(EnemyJungleBat2.class, world, t));		
		flyingPlantPool = new GameObjectPool(4, 8, new EnemyFactory(EnemyJungleFlyingPlant.class, world, t));		
		evilPlantPool = new GameObjectPool(3, 6, new EnemyFactory(EnemyJungleEvilPlant.class, world, t));
		EnemyJungleEvilPlant.ResinDropPool = new GameObjectPool(7, 10, new ResinDropFactory(ResinDrop.class, world));
		bossSnakePool = new GameObjectPool(1, 1, new EnemyFactory(BossSnake.class, world, t));
		BossSnake.VenomPool = new GameObjectPool(2, 2, new VenomFactory(Venom.class, world));
		
		pools.add(bat1Pool);
		pools.add(bat2Pool);
		pools.add(flyingPlantPool);
		pools.add(evilPlantPool);
		pools.add(EnemyJungleEvilPlant.ResinDropPool);
		
		super.preFillPools();
	}
	
	
	
	@Override
	public void spawnEnemy(String name, float y, float ySpeed, float xSpeed, float motionDuration, float motionPeculiarity, String motionEquation, boolean motionInfinite, GameScreen gs) {
		
		Enemy enemy = null;		
		TweenEquation eq = super.resolveEquation(motionEquation);
		
		
		if(name.equals("big_bat_flying")) {
			enemy = (Enemy) bat1Pool.obtain();
			enemy.setPool(bat1Pool);
		}
		
		if(name.equals("small_bat_flying")) {
			enemy = (Enemy) bat2Pool.obtain();
			enemy.setPool(bat2Pool);
		}
		
		if(name.equals("plant_flying")) {
			enemy = (Enemy) flyingPlantPool.obtain();
	    	enemy.setPool(flyingPlantPool);
		}
		if(name.equals("evil_plant_enm")) {
			enemy = (Enemy) evilPlantPool.obtain();
			((EnemyJungleEvilPlant)enemy).setSpitResources(gs.getLevelpack(), gs.getActualFireballs());
	    	enemy.setPool(evilPlantPool);
		}
		
		if(name.equals("BossSnake")) {
			enemy = (Enemy) bossSnakePool.obtain();
			((BossSnake)enemy).setShootResources(gs.getLevelpack(), gs.getActualFireballs());
	    	enemy.setPool(bossSnakePool);
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
		flyingPlantPool.clear();
		EnemyJungleEvilPlant.ResinDropPool.clear();
		BossSnake.VenomPool.clear();
		evilPlantPool.clear();
		super.dispose();
	}

}
