package com.xsheetgames.genericGameObjects;

import com.badlogic.gdx.physics.box2d.Body;

public interface Hurtable {
	public boolean reduceEnergy();
	public int getEnemyPoints();
	public short getEnergy();
	public short getStartEnergy();
	public Body getBody();
}
