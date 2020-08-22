package com.xsheetgames.genericGameObjects;

import com.badlogic.gdx.physics.box2d.World;
import com.xsheetgames.genericGameObjects.GameObject;
import com.xsheetgames.genericGameObjects.GameObjectFactory;

public class DeadlyFireballFactory extends GameObjectFactory{

	private World world;
	
	
	public DeadlyFireballFactory(Class<? extends GameObject> c, World w) {
		super(c);
		this.world = w;
	}

	@Override
	public GameObject createObject() {		
		return new DeadlyFireball(this.world, 0f, 0f);
	}

}
