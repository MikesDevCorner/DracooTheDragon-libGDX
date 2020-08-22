package com.xsheetgames.genericGameObjects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.xsheetgames.genericElements.Level;

public abstract class AbstractObstacleCollection extends GameObjectCollection {
	
	
	protected Vector2 velocity;
	protected World world;
	
	
	public AbstractObstacleCollection(World world) {
		this.world = world;
		this.velocity = new Vector2(0f,0f);
	}
	
	public void setVelocity(float velocity) {
		this.velocity.set(velocity*-1, 0f);
	}
	
	
	@Override
	public void drawObjects(SpriteBatch batch, float delta) {
		for(GameObject d : objects) {
			if(d instanceof DeadlyObstacle == true) d.draw(batch, delta);
		}
		for(GameObject d : objects) {
			if(d instanceof DeadlyObstacle == false) d.draw(batch, delta);
		}
	}
	
	
	@Override
	public void invokeObjectLogic(float delta, Level actualLevel) {
		super.invokeObjectLogic(delta, actualLevel);
	}
	
	public abstract void spawnObstacle(String name, float y);
	
	
	public void dispose() {
		for(GameObject g:this.objects) {
			g.dispose();
		}
		world = null;
	}

}
