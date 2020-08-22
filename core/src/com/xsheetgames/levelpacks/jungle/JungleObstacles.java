package com.xsheetgames.levelpacks.jungle;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.xsheetgames.Configuration;
import com.xsheetgames.GameAssets;
import com.xsheetgames.genericGameObjects.AbstractObstacleCollection;
import com.xsheetgames.genericGameObjects.DeadlyObstacle;
import com.xsheetgames.genericGameObjects.DeadlyObstacleFactory;
import com.xsheetgames.genericGameObjects.GameObjectPool;
import com.xsheetgames.genericGameObjects.Obstacle;
import com.xsheetgames.genericGameObjects.ObstacleFactory;

public class JungleObstacles extends AbstractObstacleCollection {
	
	
	public static GameObjectPool bambusPool, columnPool, evil_plan_obsPool, plant_thorns_btmPool, plant_thorns_topPool, spider_column_bottomPool, spider_column_topPool, spider_stonePool, spiky_plant_btmPool;
	public static GameObjectPool spiky_plant_topPool, stone_bigPool, stone_breakablePool, stome_smlPool, treePool, treetrunkPool, vine_aPool, vine_bPool;

	
	public static GameObjectPool sign_rightWayWHPool;
	public static GameObjectPool sign_arrowWHPool;
	public static GameObjectPool sign_oopsWHPool;
	public static GameObjectPool sign_deadEndWHPool;
	
	public static GameObjectPool sign_rightWayWOHPool;
	public static GameObjectPool sign_arrowWOHPool;
	public static GameObjectPool sign_oopsWOHPool;
	public static GameObjectPool sign_deadEndWOHPool;
	
	public static GameObjectPool sign_nexLevelPool;
	
	
	
	public JungleObstacles(World world) {
		super(world);
		
		//nonbreakable ObstaclePools
		bambusPool = new GameObjectPool(4,8, new ObstacleFactory(Obstacle.class,world,JungleLevelPack.atlas, "bambus", JungleLevelPack.objectLoader, "bambus", 0f, 0f,(short)1,0f,0f,"",null,false));
		columnPool = new GameObjectPool(4,8, new ObstacleFactory(Obstacle.class,world,JungleLevelPack.atlas, "column", JungleLevelPack.objectLoader, "column", 0f, 0f,(short)1,0f,0f,"",null,false));
		evil_plan_obsPool = new GameObjectPool(4,8, new ObstacleFactory(Obstacle.class,world,JungleLevelPack.atlas, "evil_plant_obs", JungleLevelPack.objectLoader, "evil_plant_obs", 0f, 0f,(short)1,0f,0f,"",null,true));
		plant_thorns_btmPool = new GameObjectPool(4,8, new DeadlyObstacleFactory(DeadlyObstacle.class,world,JungleLevelPack.atlas, "plant_thorns_btm", JungleLevelPack.objectLoader, "plant_thorns_btm", 0f, 0f,(short)1,0f,0f,"",null,false));
		plant_thorns_topPool = new GameObjectPool(4,8, new DeadlyObstacleFactory(DeadlyObstacle.class,world,JungleLevelPack.atlas, "plant_thorns_top", JungleLevelPack.objectLoader, "plant_thorns_top", 0f, 0f,(short)1,0f,0f,"",null,false));
		spider_column_bottomPool = new GameObjectPool(4,8, new ObstacleFactory(Obstacle.class,world,JungleLevelPack.atlas, "spider_column_bottom", JungleLevelPack.objectLoader, "spider_column_bottom", 0f, 0f,(short)1,0f,0f,"",null,false));
		spider_column_topPool = new GameObjectPool(4,8, new ObstacleFactory(Obstacle.class,world,JungleLevelPack.atlas, "spider_column_top", JungleLevelPack.objectLoader, "spider_column_top", 0f, 0f,(short)1,0f,0f,"",null,false));
		spider_stonePool = new GameObjectPool(8,16, new ObstacleFactory(Obstacle.class,world,JungleLevelPack.atlas, "spider_stone", JungleLevelPack.objectLoader, "spider_stone", 0f, 0f,(short)1,0f,0f,"",null,false));
		spiky_plant_btmPool = new GameObjectPool(4,8, new DeadlyObstacleFactory(DeadlyObstacle.class,world,JungleLevelPack.atlas, "spiky_plant_btm", JungleLevelPack.objectLoader, "spiky_plant_btm", 0f, 0f,(short)1,0f,0f,"",null,false));
		spiky_plant_topPool = new GameObjectPool(4,8, new DeadlyObstacleFactory(DeadlyObstacle.class,world,JungleLevelPack.atlas, "spiky_plant_top", JungleLevelPack.objectLoader, "spiky_plant_top", 0f, 0f,(short)1,0f,0f,"",null,false));
		stone_bigPool = new GameObjectPool(5,8, new ObstacleFactory(Obstacle.class,world,JungleLevelPack.atlas, "stone_big", JungleLevelPack.objectLoader, "stone_big", 0f, 0f,(short)1,0f,0f,"",null,false));
		stome_smlPool = new GameObjectPool(10,16, new ObstacleFactory(Obstacle.class,world,JungleLevelPack.atlas, "stone_sml", JungleLevelPack.objectLoader, "stone_sml", 0f, 0f,(short)1,0f,0f,"",null,false));
		treePool = new GameObjectPool(3,6, new ObstacleFactory(Obstacle.class,world,JungleLevelPack.atlas, "tree", JungleLevelPack.objectLoader, "tree", 0f, 0f,(short)1,0f,0f,"",null,false));
		treetrunkPool = new GameObjectPool(2,6, new ObstacleFactory(Obstacle.class,world,JungleLevelPack.atlas, "treetrunk", JungleLevelPack.objectLoader, "treetrunk", 0f, 0f,(short)1,0f,0f,"",null,false));
		vine_aPool = new GameObjectPool(5,10, new ObstacleFactory(Obstacle.class,world,JungleLevelPack.atlas, "vine_a", JungleLevelPack.objectLoader, "vine_a", 0f, 0f,(short)1,0f,0f,"",null,false));
		vine_bPool = new GameObjectPool(5,10, new ObstacleFactory(Obstacle.class,world,JungleLevelPack.atlas, "vine_b", JungleLevelPack.objectLoader, "vine_b", 0f, 0f,(short)1,0f,0f,"",null,false));
		
		//breakable ObstaclePools:
		stone_breakablePool = new GameObjectPool(14,20, new ObstacleFactory(Obstacle.class,world,JungleLevelPack.atlas, "stone_breakable", JungleLevelPack.objectLoader, "stone_breakable", 0f, 0f,(short)1 ,-0.3f,-2.1f,"stone_explosion",GameAssets.fetchSound("jungle/sounds/rockburst.mp3"),false));
		
		//sign ObstaclePools:
		sign_rightWayWHPool = new GameObjectPool(1,4, new ObstacleFactory(Obstacle.class,world,JungleLevelPack.atlas, "sign1wh", JungleLevelPack.objectLoader, "sign_wh", 0f, 0f,(short)1,0f,0f,"",null,false));
		sign_arrowWHPool = new GameObjectPool(1,4, new ObstacleFactory(Obstacle.class,world,JungleLevelPack.atlas, "sign2wh", JungleLevelPack.objectLoader, "sign_wh", 0f, 0f,(short)1,0f,0f,"",null,false));
		sign_oopsWHPool = new GameObjectPool(1,4, new ObstacleFactory(Obstacle.class,world,JungleLevelPack.atlas, "sign3wh", JungleLevelPack.objectLoader, "sign_wh", 0f, 0f,(short)1,0f,0f,"",null,false));
		sign_deadEndWHPool = new GameObjectPool(1,4, new ObstacleFactory(Obstacle.class,world,JungleLevelPack.atlas, "sign4wh", JungleLevelPack.objectLoader, "sign_wh", 0f, 0f,(short)1,0f,0f,"",null,false));
		
		sign_rightWayWOHPool = new GameObjectPool(1,4, new ObstacleFactory(Obstacle.class,world,JungleLevelPack.atlas, "sign1woh", JungleLevelPack.objectLoader, "sign_woh", 0f, 0f,(short)1,0f,0f,"",null,false));
		sign_arrowWOHPool = new GameObjectPool(1,4, new ObstacleFactory(Obstacle.class,world,JungleLevelPack.atlas, "sign2woh", JungleLevelPack.objectLoader, "sign_woh", 0f, 0f,(short)1,0f,0f,"",null,false));
		sign_oopsWOHPool = new GameObjectPool(1,4, new ObstacleFactory(Obstacle.class,world,JungleLevelPack.atlas, "sign3woh", JungleLevelPack.objectLoader, "sign_woh", 0f, 0f,(short)1,0f,0f,"",null,false));
		sign_deadEndWOHPool = new GameObjectPool(1,4, new ObstacleFactory(Obstacle.class,world,JungleLevelPack.atlas, "sign4woh", JungleLevelPack.objectLoader, "sign_woh", 0f, 0f,(short)1,0f,0f,"",null,false));
		
		sign_nexLevelPool = new GameObjectPool(1,4, new ObstacleFactory(Obstacle.class,world,JungleLevelPack.atlas, "nextlevel", JungleLevelPack.objectLoader, "nextlevel", 0f, 0f,(short)1,0f,0f,"",null,false));
		
		this.pools.add(bambusPool);
		this.pools.add(columnPool);
		this.pools.add(evil_plan_obsPool);
		this.pools.add(plant_thorns_btmPool);
		this.pools.add(plant_thorns_topPool);
		this.pools.add(spider_column_bottomPool);
		this.pools.add(spider_column_topPool);
		this.pools.add(spider_stonePool);
		this.pools.add(spiky_plant_btmPool);
		this.pools.add(spiky_plant_topPool);
		this.pools.add(stone_bigPool);
		this.pools.add(stome_smlPool);
		this.pools.add(treePool);
		this.pools.add(treetrunkPool);
		this.pools.add(vine_aPool);
		this.pools.add(vine_bPool);
		this.pools.add(stone_breakablePool);

		this.pools.add(sign_rightWayWHPool);
		this.pools.add(sign_arrowWHPool);
		this.pools.add(sign_oopsWHPool);
		this.pools.add(sign_deadEndWHPool);
		
		this.pools.add(sign_rightWayWOHPool);
		this.pools.add(sign_arrowWOHPool);
		this.pools.add(sign_oopsWOHPool);
		this.pools.add(sign_deadEndWOHPool);
		
		this.pools.add(sign_nexLevelPool);
		
		this.preFillPools();
	}
	
	
	
	@Override
	public void spawnObstacle(String name, float y) {
		Obstacle o = null;
		DeadlyObstacle o2 = null;
		
		if(name.equals("bambus")) {
			o = (Obstacle) bambusPool.obtain();
			o.setPool(bambusPool);
		}
		else if(name.equals("column")) {
			o = (Obstacle) columnPool.obtain();
			o.setPool(columnPool);  
		}
		else if(name.equals("evil_plant_obs")) {
			o = (Obstacle) evil_plan_obsPool.obtain();
			o.setPool(evil_plan_obsPool);  
		}
		else if(name.equals("plant_thorns_btm")) {
			o2 = (DeadlyObstacle) plant_thorns_btmPool.obtain();
			o2.setPool(plant_thorns_btmPool); 
		}
		else if(name.equals("plant_thorns_top")) {
			o2 = (DeadlyObstacle) plant_thorns_topPool.obtain();
			o2.setPool(plant_thorns_topPool);  
		}
		else if(name.equals("spider_column_bottom")) {
			o = (Obstacle) spider_column_bottomPool.obtain();
			o.setPool(spider_column_bottomPool);  
		}
		else if(name.equals("spider_column_top")) {
			o = (Obstacle) spider_column_topPool.obtain();
			o.setPool(spider_column_topPool);  
		}
		else if(name.equals("spider_stone")) {
			o = (Obstacle) spider_stonePool.obtain();
			o.setPool(spider_stonePool);  
		}
		else if(name.equals("spiky_plant_btm")) {
			o2 = (DeadlyObstacle) spiky_plant_btmPool.obtain();
			o2.setPool(spiky_plant_btmPool);  
		}
		else if(name.equals("spiky_plant_top")) {
			o2 = (DeadlyObstacle) spiky_plant_topPool.obtain();
			o2.setPool(spiky_plant_topPool);  
		}
		else if(name.equals("stone_big")) {
			o = (Obstacle) stone_bigPool.obtain();
			o.setPool(stone_bigPool);  
		}
		else if(name.equals("stone_breakable")) {
			o = (Obstacle) stone_breakablePool.obtain();
			o.willDieOnFireball = true;
			o.setPool(stone_breakablePool);  
		}
		else if(name.equals("stone_sml")) {
			o = (Obstacle) stome_smlPool.obtain();
			o.setPool(stome_smlPool);  
		}
		else if(name.equals("tree")) {
			o = (Obstacle) treePool.obtain();
			o.setPool(treePool);  
		}
		else if(name.equals("treetrunk")) {
			o = (Obstacle) treetrunkPool.obtain();
			o.setPool(treetrunkPool);  
		}
		else if(name.equals("vine_a")) {
			o = (Obstacle) vine_aPool.obtain();
			o.setPool(vine_aPool);  
		}
		else if(name.equals("vine_b")) {
			o = (Obstacle) vine_bPool.obtain();
			o.setPool(vine_bPool);  
		}		
		else if(name.equals("sign_right_way_wh")) {
			o = (Obstacle) sign_rightWayWHPool.obtain();
			o.setPool(sign_rightWayWHPool);  
		}
		else if(name.equals("sign_arrow_wh")) {
			o = (Obstacle) sign_arrowWHPool.obtain();
			o.setPool(sign_arrowWHPool);  
		}
		else if(name.equals("sign_oops_wh")) {
			o = (Obstacle) sign_oopsWHPool.obtain();
			o.setPool(sign_oopsWHPool);  
		}
		else if(name.equals("sign_dead_end_wh")) {
			o = (Obstacle) sign_deadEndWHPool.obtain();
			o.setPool(sign_deadEndWHPool);  
		}
		else if(name.equals("sign_right_way_woh")) {
			o = (Obstacle) sign_rightWayWOHPool.obtain();
			o.setPool(sign_rightWayWOHPool);  
		}
		else if(name.equals("sign_arrow_woh")) {
			o = (Obstacle) sign_arrowWOHPool.obtain();
			o.setPool(sign_arrowWOHPool);  
		}
		else if(name.equals("sign_oops_woh")) {
			o = (Obstacle) sign_oopsWOHPool.obtain();
			o.setPool(sign_oopsWOHPool);  
		}
		else if(name.equals("sign_dead_end_woh")) {
			o = (Obstacle) sign_deadEndWOHPool.obtain();
			o.setPool(sign_deadEndWOHPool);  
		}
		else if(name.equals("nextlevel")) {
			o = (Obstacle) sign_nexLevelPool.obtain();
			o.setPool(sign_nexLevelPool);  
		}
		
		
		if(o!=null) {
			o.setPosition(new Vector2(25f,y));
			o.setVelocity(this.velocity);
			this.add(o);
			if(Configuration.debugLevel >= Application.LOG_INFO && Configuration.spawnInfos) Gdx.app.log("Obstacle spawn successful","Obstacle-Hash: " + o.hashCode());
		} else {
			o2.setPosition(new Vector2(25f,y));
			o2.setVelocity(this.velocity);
			this.add(o2);
			if(Configuration.debugLevel >= Application.LOG_INFO && Configuration.spawnInfos) Gdx.app.log("Obstacle spawn successful","Obstacle-Hash: " + o2.hashCode());
		}
	}
	
	
	@Override
	public void dispose()  {		
		bambusPool.dispose();
		columnPool.dispose();
		evil_plan_obsPool.dispose();
		plant_thorns_btmPool.dispose();
		plant_thorns_topPool.dispose();
		spider_column_bottomPool.dispose();
		spider_column_topPool.dispose();
		spider_stonePool.dispose();
		spiky_plant_btmPool.dispose();
		spiky_plant_topPool.dispose();
		stone_bigPool.dispose();
		stone_breakablePool.dispose();
		stome_smlPool.dispose();
		treePool.dispose();
		treetrunkPool.dispose();
		vine_aPool.dispose();
		vine_bPool.dispose();
		
		sign_rightWayWHPool.dispose();
		sign_arrowWHPool.dispose();
		sign_oopsWHPool.dispose();
		sign_deadEndWHPool.dispose();
		
		sign_rightWayWOHPool.dispose();
		sign_arrowWOHPool.dispose();
		sign_oopsWOHPool.dispose();
		sign_deadEndWOHPool.dispose();
		
		sign_nexLevelPool.dispose();
		
		super.dispose();
	}

}
