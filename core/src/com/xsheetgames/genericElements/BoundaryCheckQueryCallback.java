package com.xsheetgames.genericElements;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.QueryCallback;
import com.badlogic.gdx.utils.Array;

public class BoundaryCheckQueryCallback implements QueryCallback {

	public Array<Body> foundBodys;
	
	public BoundaryCheckQueryCallback() {
		this.foundBodys = new Array<Body>();
	}
	
	public void clearBodys() {
		this.foundBodys.clear();
	}
	
	@Override
	public boolean reportFixture(Fixture fixture) {
		Body b = fixture.getBody();
		if(this.foundBodys.contains(b, true) == false) {
			this.foundBodys.add(b);
		}
		return true;
	}

}
