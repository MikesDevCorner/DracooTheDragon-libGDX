package com.xsheetgames.genericElements;

import java.util.Iterator;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Array;

public class AtlasAnimationCollection {

	public Array<AtlasAnimation> animations;
	TextureAtlas atlas;
	String AnimationName;
	
	public AtlasAnimationCollection(TextureAtlas atlas, int size, String AnimationName) {
		this.animations = new Array<AtlasAnimation>(size);
		this.atlas = atlas;
		this.AnimationName = AnimationName;
	}
	
	
	public void add(float x, float y, float vx, float vy, float percentX, float percentY) {
		if(this.animations != null) {
			this.animations.add(new AtlasAnimation(this.atlas, this.AnimationName, x, y, vx, vy, percentX, percentY));
		}
	}
	
	public void draw(SpriteBatch batch, float delta) {
		if(this.animations != null) {
			Iterator<AtlasAnimation> iter = this.animations.iterator();
			while(iter.hasNext()) {
				AtlasAnimation a = iter.next();
				a.draw(delta, batch);
				if(!a.isRunning()) {
					a.dispose();
					iter.remove();
				}
			}
		}
	}
	
	public void setAtlas(TextureAtlas atlas) {
		this.atlas = atlas;		
	}
	
	public void clear() {
		for(AtlasAnimation a:this.animations) {
			a.dispose();
		}
		this.animations.clear();
	}
	
	public void dispose() {
		for(AtlasAnimation a:this.animations) {
			a.dispose();
		}
		this.animations = null;
	}
}
