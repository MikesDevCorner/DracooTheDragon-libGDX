package com.xsheetgames.genericGameObjects;

import aurelienribon.bodyeditor.BodyEditorLoader;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

public class DeadlyObstacle extends Obstacle {

	public DeadlyObstacle(World world, Vector2 vel, TextureAtlas atlas,
			String atlasName, BodyEditorLoader loader, String loaderName,
			float x, float y, short energy, float deadX, float deadY,
			String dieTextureName, Sound dieSound, boolean collideNotWithEnemyFire) {
		super(world, vel, atlas, atlasName, loader, loaderName, x, y, energy, deadX,
				deadY, dieTextureName, dieSound,collideNotWithEnemyFire);
		// TODO Auto-generated constructor stub
	}

	

}
