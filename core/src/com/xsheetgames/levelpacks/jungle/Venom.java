package com.xsheetgames.levelpacks.jungle;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.xsheetgames.GameAssets;
import com.xsheetgames.genericGameObjects.EnemyFire;

public class Venom extends EnemyFire {
	
	public Venom(World world, float x, float y) {
		super(world, x, y, GameAssets.fetchTextureAtlas("jungle/images/jungle_boss.pack"), new Vector2(0f,0f), JungleLevelPack.objectLoader, "bossnake_venom", "venom", true);
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
