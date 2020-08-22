package com.xsheetgames.genericGameObjects;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.xsheetgames.GameAssets;
import com.xsheetgames.genericGameObjects.EnemyFire;

public class DeadlyFireball extends EnemyFire {

	public DeadlyFireball(World world, float x, float y) {
		super(world, x, y, GameAssets.fetchTextureAtlas("game/images/game_objects.pack"), new Vector2(0f,0f), GameAssets.getObjectLoader(), "enemy_fireball", "enemy_fireball", false);
	}

	@Override
	public void dispose() {
		super.dispose();
	}


	@Override
	public void doMotionLogic(float delta) {
		
		super.doMotionLogic(delta);
	}
	
}
