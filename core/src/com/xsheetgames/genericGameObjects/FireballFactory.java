package com.xsheetgames.genericGameObjects;

import com.badlogic.gdx.physics.box2d.World;
import com.xsheetgames.genericElements.AtlasAnimationCollection;

public class FireballFactory extends GameObjectFactory{

	private World world;
	private AtlasAnimationCollection sparkles;
	
	
	public FireballFactory(Class<? extends GameObject> c, World w, AtlasAnimationCollection sparkles) {
		super(c);
		this.world = w;
		this.sparkles = sparkles;
	}

	@Override
	public GameObject createObject() {
		return new Fireball(this.world, 0f, 0f, this.sparkles);
	}

}
