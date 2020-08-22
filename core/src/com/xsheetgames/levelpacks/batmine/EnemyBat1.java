package com.xsheetgames.levelpacks.batmine;

import aurelienribon.tweenengine.TweenManager;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.xsheetgames.GameAssets;
import com.xsheetgames.genericGameObjects.Enemy;

public class EnemyBat1 extends Enemy {

	
	public EnemyBat1(World world, float x, float y, Vector2 vel, TweenManager m) {
		
		super(world, GameAssets.fetchTextureAtlas("batmine/images/batmine_objects.pack"), 0f, vel, new Vector2(x,y),false,m);
		
		this.dieTextureName = "big_bat_dying";
		this.dieSound = GameAssets.fetchSound("batmine/sounds/bat1.mp3");
		this.energy = 2;
		this.enemyPoints = 2;
		
		this.deadXOffset = -0.5f;
		this.deadYOffset = -3.7f;
		
		super.init(BatMineLevelPack.objectLoader, "Bat1", "big_bat_flying");
	}
	
	
}
