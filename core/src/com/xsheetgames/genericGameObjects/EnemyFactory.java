package com.xsheetgames.genericGameObjects;

import aurelienribon.tweenengine.TweenManager;

import com.badlogic.gdx.physics.box2d.World;
import com.xsheetgames.GameAssets;
import com.xsheetgames.levelpacks.batmine.BossBat;
import com.xsheetgames.levelpacks.batmine.EnemyBat1;
import com.xsheetgames.levelpacks.batmine.EnemyBat2;
import com.xsheetgames.levelpacks.jungle.BossSnake;
import com.xsheetgames.levelpacks.jungle.EnemyJungleBat1;
import com.xsheetgames.levelpacks.jungle.EnemyJungleBat2;
import com.xsheetgames.levelpacks.jungle.EnemyJungleEvilPlant;
import com.xsheetgames.levelpacks.jungle.EnemyJungleFlyingPlant;

public class EnemyFactory extends GameObjectFactory{

	private World world;
	private TweenManager t;
	
	public EnemyFactory(Class<? extends GameObject> c, World w, TweenManager t) {
		super(c);
		this.world = w;
		this.t = t;
	}

	@Override
	public GameObject createObject() {
		//WOULD BE MUCH SMARTER THIS WAY, BUT UNFORTUNATLY THE GWT-BACKEND DOES NOT UNDERSTAND REFLECTION:
		/*try {
			return this.myClass.getConstructor(World.class, float.class, float.class, Vector2.class, TweenManager.class).newInstance(world, 0f, 0f, GameAssets.emptyVector2, t);
		} catch (Exception e) {
			return null;
		}*/
		
		//REFLECTION FOR THE POOR MAN INSTEAD:
		if(this.myClass == EnemyBat1.class) {
			return new EnemyBat1(world, 0f, 0f, GameAssets.emptyVector2, t);
		}else if(this.myClass == EnemyBat2.class) {
			return new EnemyBat2(world, 0f, 0f, GameAssets.emptyVector2, t);
		}else if(this.myClass == BossBat.class) {
			return new BossBat(world, 0f, 0f, GameAssets.emptyVector2, t);
		}else  if(this.myClass == EnemyJungleBat1.class) {
			return new EnemyJungleBat1(world, 0f, 0f, GameAssets.emptyVector2, t);
		}else if(this.myClass == EnemyJungleBat2.class) {
			return new EnemyJungleBat2(world, 0f, 0f, GameAssets.emptyVector2, t);
		}else if(this.myClass == EnemyJungleEvilPlant.class) {
			return new EnemyJungleEvilPlant(world, 0f, 0f, GameAssets.emptyVector2, t);
		}else if(this.myClass == EnemyJungleFlyingPlant.class) {
			return new EnemyJungleFlyingPlant(world, 0f, 0f, GameAssets.emptyVector2, t);
		}else if(this.myClass == BossSnake.class) {
			return new BossSnake(world, 0f, 0f, GameAssets.emptyVector2, t);
		} return null;
	}

}
