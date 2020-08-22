package com.xsheetgames.genericGameObjects;

import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.xsheetgames.Configuration;

public class BoundaryCollection {

	private Array<Boundary> boundaries;

	
	public BoundaryCollection(World world, boolean openBack) {
		boundaries = new Array<Boundary>(4);		
		boundaries.add(new Boundary(world, 0f, -0.2f, Configuration.VIEWPORT_WIDTH, 0.2f, "bottom"));
		boundaries.add(new Boundary(world, 0f, Configuration.VIEWPORT_HEIGHT, Configuration.VIEWPORT_WIDTH, 0.2f, "top"));
		boundaries.add(new Boundary(world, Configuration.VIEWPORT_WIDTH, 0f, 0.2f, Configuration.VIEWPORT_HEIGHT, "right"));
		boundaries.add(new Boundary(world, -0.2f, 0f, 0.2f, Configuration.VIEWPORT_HEIGHT, "left"));
		this.setBackWall(openBack);
	}
	
	public void setBackWall(boolean open) {
		for(Boundary b : this.boundaries) {
			if(b.name.equals("left")) {
				b.getBody().setActive(!open);
			}
		}
	}
	
	public void dispose() {
		for(Boundary b:boundaries) {
			b.dispose();
		}
		boundaries = null;
	}
	
}
