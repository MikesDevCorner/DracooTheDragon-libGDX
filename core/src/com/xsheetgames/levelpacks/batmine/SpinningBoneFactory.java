package com.xsheetgames.levelpacks.batmine;

import com.badlogic.gdx.physics.box2d.World;
import com.xsheetgames.genericGameObjects.GameObject;
import com.xsheetgames.genericGameObjects.GameObjectFactory;

public class SpinningBoneFactory extends GameObjectFactory{

	private World world;
	private int count;
	
	public SpinningBoneFactory(Class<? extends GameObject> c, World w) {
		super(c);
		this.world = w;
		this.count = 0;
	}

	@Override
	public GameObject createObject() {
		this.count++;
		if(this.count%3 == 0) return new SpinningSkull(this.world, 0f, 0f);
		else return new SpinningBone(this.world, 0f, 0f);
	}

}
