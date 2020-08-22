package com.xsheetgames.levelpacks.jungle;

import aurelienribon.tweenengine.TweenManager;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.xsheetgames.GameAssets;
import com.xsheetgames.genericGameObjects.Enemy;

public class EnemyJungleBat1 extends Enemy {

	public EnemyJungleBat1(World world, float x, float y, Vector2 vel, TweenManager m) {
		
		super(world, GameAssets.fetchTextureAtlas("jungle/images/jungle_objects.pack"), 0f, vel, new Vector2(x,y),false,m);
		
		this.dieTextureName = "big_bat_dying";
		this.dieSound = GameAssets.fetchSound("batmine/sounds/bat1.mp3");
		this.energy = 2;
		this.enemyPoints = 2;
		
		this.deadXOffset = -0.5f;
		this.deadYOffset = -3.7f;
		
		super.init(JungleLevelPack.objectLoader, "big_bat_flying", "big_bat_flying");
	}
}
