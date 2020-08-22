package com.xsheetgames.levelpacks.jungle;

import aurelienribon.tweenengine.TweenManager;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.xsheetgames.GameAssets;
import com.xsheetgames.genericGameObjects.Enemy;

public class EnemyJungleFlyingPlant extends Enemy {

	public EnemyJungleFlyingPlant(World world, float x, float y, Vector2 vel, TweenManager m) {
		
		super(world, GameAssets.fetchTextureAtlas("jungle/images/jungle_objects.pack"), 0f, vel, new Vector2(x,y),false,m);
		
		this.dieTextureName = "plant_dying";
		this.dieSound = GameAssets.fetchSound("jungle/sounds/plant.mp3");
		this.energy = 2;
		this.enemyPoints = 1;
		
		this.deadXOffset = 0f;
		this.deadYOffset = -1f;
		this.gradientAngle = 4f;
		
		super.init(JungleLevelPack.objectLoader, "plant_flying", "plant_flying");
	}
}
