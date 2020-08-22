package com.xsheetgames.levelpacks.batmine;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.xsheetgames.GameAssets;
import com.xsheetgames.genericGameObjects.EnemyFire;

public class SpinningSkull extends EnemyFire {
	
	public SpinningSkull(World world, float x, float y) {
		super(world, x, y, GameAssets.fetchTextureAtlas("batmine/images/bossbat_objects.pack"), new Vector2(0f,0f), BatMineLevelPack.objectLoader, "skull", "skull", true);
		this.maskBits = 26;
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
