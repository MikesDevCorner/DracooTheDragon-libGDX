package com.xsheetgames.levelpacks.batmine;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.xsheetgames.Configuration;
import com.xsheetgames.genericGameObjects.AbstractObstacleCollection;
import com.xsheetgames.genericGameObjects.DeadlyObstacle;
import com.xsheetgames.genericGameObjects.DeadlyObstacleFactory;
import com.xsheetgames.genericGameObjects.GameObjectPool;
import com.xsheetgames.genericGameObjects.Obstacle;
import com.xsheetgames.genericGameObjects.ObstacleFactory;

public class BatMineObstacles extends AbstractObstacleCollection {
	
	
	public static GameObjectPool boner_downPool;
	public static GameObjectPool boner_upPool;	
	public static GameObjectPool flatPool;		
	public static GameObjectPool needle_downPool;
	public static GameObjectPool needle_upPool;
	public static GameObjectPool pyramid_downPool;	
	public static GameObjectPool pyramid_upPool;	
	public static GameObjectPool squarePool;
	public static GameObjectPool stalacmite_aPool;
	public static GameObjectPool stalacmite_bPool;
	public static GameObjectPool stalacmite_cPool;
	public static GameObjectPool stalactite_aPool;
	public static GameObjectPool stalactite_bPool;	
	public static GameObjectPool block_fullPool;
	public static GameObjectPool block_halfPool;
	public static GameObjectPool columnPool;
	public static GameObjectPool long_bottom_firstPool;
	public static GameObjectPool long_bottom_secondPool;
	public static GameObjectPool long_top_firstPool;
	public static GameObjectPool long_top_secondPool;
	public static GameObjectPool longflatPool;
	public static GameObjectPool stone_stringPool;
	public static GameObjectPool tilted_lPool;
	public static GameObjectPool tilted_rPool;
	public static GameObjectPool volcanoPool;
	public static GameObjectPool lavaColumnPool;
	public static GameObjectPool volcano_revPool;
	public static GameObjectPool lava_revColumnPool;
	
	public static GameObjectPool sign_rightWayWHPool;
	public static GameObjectPool sign_arrowWHPool;
	public static GameObjectPool sign_oopsWHPool;
	public static GameObjectPool sign_deadEndWHPool;
	
	public static GameObjectPool sign_rightWayWOHPool;
	public static GameObjectPool sign_arrowWOHPool;
	public static GameObjectPool sign_oopsWOHPool;
	public static GameObjectPool sign_deadEndWOHPool;
	
	public static GameObjectPool sign_nexLevelPool;
	
	
	
	public BatMineObstacles(World world) {
		super(world);
		
		boner_downPool = new GameObjectPool(3,6, new ObstacleFactory(Obstacle.class,world,BatMineLevelPack.atlas, "boner_down", BatMineLevelPack.objectLoader, "boner_down", 0f, 0f,(short)1,0f,0f,"",null,false));
		boner_upPool = new GameObjectPool(3,6, new ObstacleFactory(Obstacle.class,world,BatMineLevelPack.atlas, "boner_up", BatMineLevelPack.objectLoader, "boner_up", 0f, 0f,(short)1,0f,0f,"",null,false));
		flatPool = new GameObjectPool(6,15, new ObstacleFactory(Obstacle.class,world,BatMineLevelPack.atlas, "flat", BatMineLevelPack.objectLoader, "flat", 0f, 0f,(short)1,0f,0f,"",null,false));		
		needle_downPool = new GameObjectPool(4,12, new ObstacleFactory(Obstacle.class,world,BatMineLevelPack.atlas, "needle_down", BatMineLevelPack.objectLoader, "needle_down", 0f, 0f,(short)1,0f,0f,"",null,false));
		needle_upPool = new GameObjectPool(4,12, new ObstacleFactory(Obstacle.class,world,BatMineLevelPack.atlas, "needle_up", BatMineLevelPack.objectLoader, "needle_up", 0f, 0f,(short)1,0f,0f,"",null,false));
		pyramid_downPool = new GameObjectPool(2,7, new ObstacleFactory(Obstacle.class,world,BatMineLevelPack.atlas, "pyramid_down", BatMineLevelPack.objectLoader, "pyramid_down", 0f, 0f,(short)1,0f,0f,"",null,false));		
		pyramid_upPool = new GameObjectPool(2,7, new ObstacleFactory(Obstacle.class,world,BatMineLevelPack.atlas, "pyramid_up", BatMineLevelPack.objectLoader, "pyramid_up", 0f, 0f,(short)1,0f,0f,"",null,false));
		squarePool = new GameObjectPool(10,15, new ObstacleFactory(Obstacle.class,world,BatMineLevelPack.atlas, "square", BatMineLevelPack.objectLoader, "square", 0f, 0f,(short)1,0f,0f,"",null,false));
		stalacmite_aPool = new GameObjectPool(3,8, new ObstacleFactory(Obstacle.class,world,BatMineLevelPack.atlas, "stalacmite_a", BatMineLevelPack.objectLoader, "stalacmite_a", 0f, 0f,(short)1,0f,0f,"",null,false));
		stalacmite_bPool = new GameObjectPool(3,8, new ObstacleFactory(Obstacle.class,world,BatMineLevelPack.atlas, "stalacmite_b", BatMineLevelPack.objectLoader, "stalacmite_b", 0f, 0f,(short)1,0f,0f,"",null,false));
		stalacmite_cPool = new GameObjectPool(3,8, new ObstacleFactory(Obstacle.class,world,BatMineLevelPack.atlas, "stalacmite_c", BatMineLevelPack.objectLoader, "stalacmite_c", 0f, 0f,(short)1,0f,0f,"",null,false));
		stalactite_aPool = new GameObjectPool(3,8, new ObstacleFactory(Obstacle.class,world,BatMineLevelPack.atlas, "stalactite_a", BatMineLevelPack.objectLoader, "stalactite_a", 0f, 0f,(short)1,0f,0f,"",null,false));
		stalactite_bPool = new GameObjectPool(3,8, new ObstacleFactory(Obstacle.class,world,BatMineLevelPack.atlas, "stalactite_b", BatMineLevelPack.objectLoader, "stalactite_b", 0f, 0f,(short)1,0f,0f,"",null,false));
		block_fullPool = new GameObjectPool(5,8, new ObstacleFactory(Obstacle.class,world,BatMineLevelPack.atlas, "block_full", BatMineLevelPack.objectLoader, "block_full", 0f, 0f,(short)1,0f,0f,"",null,false));
		block_halfPool = new GameObjectPool(8,8, new ObstacleFactory(Obstacle.class,world,BatMineLevelPack.atlas, "block_half", BatMineLevelPack.objectLoader, "block_half", 0f, 0f,(short)1,0f,0f,"",null,false));
		columnPool = new GameObjectPool(3,8, new ObstacleFactory(Obstacle.class,world,BatMineLevelPack.atlas, "column", BatMineLevelPack.objectLoader, "column_b", 0f, 0f,(short)1,0f,0f,"",null,false));
		long_bottom_firstPool = new GameObjectPool(1,8, new ObstacleFactory(Obstacle.class,world,BatMineLevelPack.atlas, "long_bottom_first", BatMineLevelPack.objectLoader, "long_bottom_first", 0f, 0f,(short)1,0f,0f,"",null,false));
		long_bottom_secondPool = new GameObjectPool(1,8, new ObstacleFactory(Obstacle.class,world,BatMineLevelPack.atlas, "long_bottom_second", BatMineLevelPack.objectLoader, "long_bottom_second", 0f, 0f,(short)1,0f,0f,"",null,false));
		long_top_firstPool = new GameObjectPool(1,8, new ObstacleFactory(Obstacle.class,world,BatMineLevelPack.atlas, "long_top_first", BatMineLevelPack.objectLoader, "long_top_first", 0f, 0f,(short)1,0f,0f,"",null,false));
		long_top_secondPool = new GameObjectPool(1,8, new ObstacleFactory(Obstacle.class,world,BatMineLevelPack.atlas, "long_top_second", BatMineLevelPack.objectLoader, "long_top_second", 0f, 0f,(short)1,0f,0f,"",null,false));
		longflatPool = new GameObjectPool(4,8, new ObstacleFactory(Obstacle.class,world,BatMineLevelPack.atlas, "longflat", BatMineLevelPack.objectLoader, "longflat", 0f, 0f,(short)1,0f,0f,"",null,false));
		stone_stringPool = new GameObjectPool(5,12, new ObstacleFactory(Obstacle.class,world,BatMineLevelPack.atlas, "stone_string", BatMineLevelPack.objectLoader, "stone_string", 0f, 0f,(short)1,0f,0f,"",null,false));
		tilted_lPool = new GameObjectPool(2,8, new ObstacleFactory(Obstacle.class,world,BatMineLevelPack.atlas, "tilted_l", BatMineLevelPack.objectLoader, "tilted_l", 0f, 0f,(short)1,0f,0f,"",null,false));
		tilted_rPool = new GameObjectPool(2,8, new ObstacleFactory(Obstacle.class,world,BatMineLevelPack.atlas, "tilted_r", BatMineLevelPack.objectLoader, "tilted_r", 0f, 0f,(short)1,0f,0f,"",null,false));
		volcanoPool = new GameObjectPool(5,12, new ObstacleFactory(Obstacle.class,world,BatMineLevelPack.atlas, "volcano", BatMineLevelPack.objectLoader, "volcano", 0f, 0f,(short)1,0f,0f,"",null,false));
		lavaColumnPool = new GameObjectPool(5,12, new  DeadlyObstacleFactory(DeadlyObstacle.class,world,BatMineLevelPack.atlas, "lava", BatMineLevelPack.objectLoader, "lava", 0f, 0f,(short)1,0f,0f,"",null,false));
		volcano_revPool = new GameObjectPool(5,12, new ObstacleFactory(Obstacle.class,world,BatMineLevelPack.atlas, "volcano_rev", BatMineLevelPack.objectLoader, "volcano_rev", 0f, 0f,(short)1,0f,0f,"",null,false));
		lava_revColumnPool = new GameObjectPool(5,12, new  DeadlyObstacleFactory(DeadlyObstacle.class,world,BatMineLevelPack.atlas, "lava_rev", BatMineLevelPack.objectLoader, "lava_rev", 0f, 0f,(short)1,0f,0f,"",null,false));
		
		sign_rightWayWHPool = new GameObjectPool(1,4, new ObstacleFactory(Obstacle.class,world,BatMineLevelPack.atlas, "sign1wh", BatMineLevelPack.objectLoader, "sign_wh", 0f, 0f,(short)1,0f,0f,"",null,false));
		sign_arrowWHPool = new GameObjectPool(1,4, new ObstacleFactory(Obstacle.class,world,BatMineLevelPack.atlas, "sign2wh", BatMineLevelPack.objectLoader, "sign_wh", 0f, 0f,(short)1,0f,0f,"",null,false));
		sign_oopsWHPool = new GameObjectPool(1,4, new ObstacleFactory(Obstacle.class,world,BatMineLevelPack.atlas, "sign3wh", BatMineLevelPack.objectLoader, "sign_wh", 0f, 0f,(short)1,0f,0f,"",null,false));
		sign_deadEndWHPool = new GameObjectPool(1,4, new ObstacleFactory(Obstacle.class,world,BatMineLevelPack.atlas, "sign4wh", BatMineLevelPack.objectLoader, "sign_wh", 0f, 0f,(short)1,0f,0f,"",null,false));
		
		sign_rightWayWOHPool = new GameObjectPool(1,4, new ObstacleFactory(Obstacle.class,world,BatMineLevelPack.atlas, "sign1woh", BatMineLevelPack.objectLoader, "sign_woh", 0f, 0f,(short)1,0f,0f,"",null,false));
		sign_arrowWOHPool = new GameObjectPool(1,4, new ObstacleFactory(Obstacle.class,world,BatMineLevelPack.atlas, "sign2woh", BatMineLevelPack.objectLoader, "sign_woh", 0f, 0f,(short)1,0f,0f,"",null,false));
		sign_oopsWOHPool = new GameObjectPool(1,4, new ObstacleFactory(Obstacle.class,world,BatMineLevelPack.atlas, "sign3woh", BatMineLevelPack.objectLoader, "sign_woh", 0f, 0f,(short)1,0f,0f,"",null,false));
		sign_deadEndWOHPool = new GameObjectPool(1,4, new ObstacleFactory(Obstacle.class,world,BatMineLevelPack.atlas, "sign4woh", BatMineLevelPack.objectLoader, "sign_woh", 0f, 0f,(short)1,0f,0f,"",null,false));
		
		sign_nexLevelPool = new GameObjectPool(1,4, new ObstacleFactory(Obstacle.class,world,BatMineLevelPack.atlas, "nextlevel", BatMineLevelPack.objectLoader, "nextlevel", 0f, 0f,(short)1,0f,0f,"",null,false));
		
		
		this.pools.add(boner_downPool);
		this.pools.add(boner_upPool);
		this.pools.add(flatPool);
		this.pools.add(needle_downPool);
		this.pools.add(needle_upPool);
		this.pools.add(pyramid_downPool);
		this.pools.add(pyramid_upPool);
		this.pools.add(squarePool);
		this.pools.add(stalacmite_aPool);
		this.pools.add(stalacmite_bPool);
		this.pools.add(stalacmite_cPool);
		this.pools.add(stalactite_aPool);
		this.pools.add(stalactite_bPool);		
		this.pools.add(block_fullPool);
		this.pools.add(block_halfPool);
		this.pools.add(columnPool);
		this.pools.add(long_bottom_firstPool);
		this.pools.add(long_bottom_secondPool);
		this.pools.add(long_top_firstPool);
		this.pools.add(long_top_secondPool);
		this.pools.add(longflatPool);
		this.pools.add(stone_stringPool);
		this.pools.add(tilted_lPool);
		this.pools.add(tilted_rPool);
		this.pools.add(volcanoPool);
		this.pools.add(lavaColumnPool);
		this.pools.add(volcano_revPool);
		this.pools.add(lava_revColumnPool);
		
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
		
		
		if(name.equals("boner_down")) {
			o = (Obstacle) boner_downPool.obtain();
			o.setPool(boner_downPool);
		}
		else if(name.equals("boner_up")) {
			o = (Obstacle) boner_upPool.obtain();
			o.setPool(boner_upPool);  
		}
		else if(name.equals("flat")) {
			o = (Obstacle) flatPool.obtain();
			o.setPool(flatPool); 
		}
		else if(name.equals("needle_down")) {
			o = (Obstacle) needle_downPool.obtain();
			o.setPool(needle_downPool);  
		}
		else if(name.equals("needle_up")) {
			o = (Obstacle) needle_upPool.obtain();
			o.setPool(needle_upPool);  
		}
		else if(name.equals("pyramid_down")) {
			o = (Obstacle) pyramid_downPool.obtain();
			o.setPool(pyramid_downPool);  
		}		
		else if(name.equals("pyramid_up")) {
			o = (Obstacle) pyramid_upPool.obtain();
			o.setPool(pyramid_upPool);  
		}
		else if(name.equals("square")) {
			o = (Obstacle) squarePool.obtain();
			o.setPool(squarePool);  
		}
		else if(name.equals("stalacmite_a")) {
			o = (Obstacle) stalacmite_aPool.obtain();
			o.setPool(stalacmite_aPool);  
		}
		else if(name.equals("stalacmite_b")) {
			o = (Obstacle) stalacmite_bPool.obtain();
			o.setPool(stalacmite_bPool);  
		}
		else if(name.equals("stalacmite_c")) {
			o = (Obstacle) stalacmite_cPool.obtain();
			o.setPool(stalacmite_cPool);  
		}
		else if(name.equals("stalactite_a")) {
			o = (Obstacle) stalactite_aPool.obtain();
			o.setPool(stalactite_aPool);  
		}
		else if(name.equals("stalactite_b")) {
			o = (Obstacle) stalactite_bPool.obtain();
			o.setPool(stalactite_bPool);  
		}		
		else if(name.equals("block_full")) {
			o = (Obstacle) block_fullPool.obtain();
			o.setPool(block_fullPool);  
		}
		else if(name.equals("block_half")) {
			o = (Obstacle) block_halfPool.obtain();
			o.setPool(block_halfPool);  
		}
		else if(name.equals("column_b")) {
			o = (Obstacle) columnPool.obtain();
			o.setPool(columnPool);  
		}
		else if(name.equals("long_bottom_first")) {
			o = (Obstacle) long_bottom_firstPool.obtain();
			o.setPool(long_bottom_firstPool);  
		}
		else if(name.equals("long_bottom_second")) {
			o = (Obstacle) long_bottom_secondPool.obtain();
			o.setPool(long_bottom_secondPool);  
		}
		else if(name.equals("long_top_first")) {
			o = (Obstacle) long_top_firstPool.obtain();
			o.setPool(long_top_firstPool);  
		}
		else if(name.equals("long_top_second")) {
			o = (Obstacle) long_top_secondPool.obtain();
			o.setPool(long_top_secondPool);  
		}
		else if(name.equals("longflat")) {
			o = (Obstacle) longflatPool.obtain();
			o.setPool(longflatPool);  
		}
		else if(name.equals("stone_string")) {
			o = (Obstacle) stone_stringPool.obtain();
			o.setPool(stone_stringPool);  
		}
		else if(name.equals("tilted_l")) {
			o = (Obstacle) tilted_lPool.obtain();
			o.setPool(tilted_lPool);  
		}
		else if(name.equals("tilted_r")) {
			o = (Obstacle) tilted_rPool.obtain();
			o.setPool(tilted_rPool);  
		}
		else if(name.equals("volcano")) {
			o = (Obstacle) volcanoPool.obtain();
			o.setPool(volcanoPool);  
		}
		else if(name.equals("lava")) {
			o2 = (DeadlyObstacle) lavaColumnPool.obtain();
			o2.setAsSensor(true);
			o2.setPool(lavaColumnPool);  
		}
		else if(name.equals("volcano_rev")) {
			o = (Obstacle) volcano_revPool.obtain();
			o.setPool(volcano_revPool);  
		}
		else if(name.equals("lava_rev")) {
			o2 = (DeadlyObstacle) lava_revColumnPool.obtain();
			o2.setAsSensor(true);
			o2.setPool(lava_revColumnPool);  
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
			if(o2 != null) {
				o2.setPosition(new Vector2(25f,y));
				o2.setVelocity(this.velocity);
				this.add(o2);
				if(Configuration.debugLevel >= Application.LOG_INFO && Configuration.spawnInfos) Gdx.app.log("Obstacle spawn successful","Obstacle-Hash: " + o2.hashCode());
			} else {
				o2 = null;
			}
		}	
	}
	
	
	@Override
	public void dispose()  {		
		boner_downPool.dispose();
		boner_upPool.dispose();
		flatPool.dispose();
		needle_downPool.dispose();
		needle_upPool.dispose();
		pyramid_downPool.dispose();
		pyramid_upPool.dispose();
		squarePool.dispose();
		stalacmite_aPool.dispose();
		stalacmite_bPool.dispose();
		stalacmite_cPool.dispose();
		stalactite_aPool.dispose();
		stalactite_bPool.dispose();
		block_fullPool.dispose();
		block_halfPool.dispose();
		columnPool.dispose();
		long_bottom_firstPool.dispose();
		long_bottom_secondPool.dispose();
		long_top_firstPool.dispose();
		long_top_secondPool.dispose();
		longflatPool.dispose();
		stone_stringPool.dispose();
		tilted_lPool.dispose();
		tilted_rPool.dispose();
		volcanoPool.dispose();
		lavaColumnPool.dispose();
		volcano_revPool.dispose();
		lava_revColumnPool.dispose();
		
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
