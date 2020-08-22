package com.xsheetgames.levelpacks.jungle;

import com.badlogic.gdx.physics.box2d.World;
import com.xsheetgames.genericGameObjects.GameObject;
import com.xsheetgames.genericGameObjects.GameObjectFactory;

public class ResinDropFactory extends GameObjectFactory{

	private World world;
	
	
	public ResinDropFactory(Class<? extends GameObject> c, World w) {
		super(c);
		this.world = w;
	}

	@Override
	public GameObject createObject() {		
		return new ResinDrop(this.world, 0f, 0f);
	}

}
