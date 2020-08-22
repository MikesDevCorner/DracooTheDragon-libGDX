package com.xsheetgames.genericGameObjects;

import com.badlogic.gdx.physics.box2d.World;
import com.xsheetgames.GameAssets;

public class ChiliFactory extends GameObjectFactory{

	private World world;
	
	public ChiliFactory(Class<? extends GameObject> c, World w) {
		super(c);
		this.world = w;
	}

	@Override
	public GameObject createObject() {
		return new Chili(world, 0f, 0f, GameAssets.emptyVector2);
	}

}
