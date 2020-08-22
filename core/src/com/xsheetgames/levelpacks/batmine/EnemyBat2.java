package com.xsheetgames.levelpacks.batmine;

import aurelienribon.tweenengine.TweenManager;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.xsheetgames.GameAssets;
import com.xsheetgames.genericGameObjects.Enemy;

public class EnemyBat2 extends Enemy {

	public EnemyBat2(World world, float x, float y, Vector2 vel, TweenManager m) {
		
		super(world, GameAssets.fetchTextureAtlas("batmine/images/batmine_objects.pack"), 0f, vel, new Vector2(x,y),false,m);
		
		this.dieTextureName = "small_bat_dying";
		this.dieSound = GameAssets.fetchSound("batmine/sounds/bat2.mp3");
		this.energy = 1;
		
		this.deadXOffset = -0.5f;
		this.deadYOffset = -1.1f;
		
		super.init(BatMineLevelPack.objectLoader, "Bat2", "small_bat_flying");
	}

}
