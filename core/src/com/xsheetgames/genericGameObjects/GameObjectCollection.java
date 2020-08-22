package com.xsheetgames.genericGameObjects;

import java.util.Iterator;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.utils.Array;
import com.xsheetgames.Configuration;
import com.xsheetgames.genericElements.Level;

public class GameObjectCollection implements Iterator<GameObject> {
	
	public Array<GameObject> objects;
	public Array<GameObjectPool> pools;
	
	public GameObjectCollection() {
		objects = new Array<GameObject>(false, 6);
		this.pools = new Array<GameObjectPool>();
	}	
	
	public void smoothStates(float fixedTimestepAccumulatorRatio) {
		for(GameObject d : objects) {
			d.smoothStates(fixedTimestepAccumulatorRatio);
		}
	}
	
	public void resetSmoothStates() {
		for(GameObject d : objects) {
			d.resetSmoothStates();
		}
	}
	
	
	public void add(GameObject a) {
		objects.add(a);
	}
	
	public void invokeObjectLogic(float delta, Level actualLevel) {
		for(GameObject d : objects) {
			d.doMotionLogic(delta);
		}
	}
	
	public void drawObjects(SpriteBatch batch, float delta) {
		for(GameObject d : objects) {
			d.draw(batch, delta);
		}
	}
	
	public void removeDraco() {
		Iterator<GameObject> iter = this.objects.iterator();
		while(iter.hasNext()) {
			if(iter.next() instanceof com.xsheetgames.genericGameObjects.Draco) {
				iter.remove();
			}
		}
	}


	@Override
	public boolean hasNext() {
		return objects.iterator().hasNext();
	}


	@Override
	public GameObject next() {
		return objects.iterator().next();
	}


	@Override
	public void remove() {
		objects.iterator().remove();		
	}
	
	
	
	public void preFillPools() {
		for(GameObjectPool a : this.pools) {
			Array<GameObject> objects = new Array<GameObject>();
			for(int i = 0; i < a.initialCapacity; i++) { //Pool prebefllen
				GameObject o = (GameObject) a.obtain();
				o.setPool(a);
				objects.add(o);
			}
			for(GameObject o : objects) {
				a.free(o);
			}
			objects.clear();
			objects = null;
		}
	}
	
	public void resetGraphics(TextureAtlas atlas) {
		for(GameObjectPool a : this.pools) {
			a.resetGraphics(atlas);
		}		
	}
	
	
	public void removeOutlaws() {
		Iterator<GameObject> iter = this.objects.iterator();

		while(iter.hasNext()) {
			GameObject g = iter.next();
			if(g.isDisposed() == false && g.body.isActive()) {
				if(g.getBodyType() != BodyType.KinematicBody) {
					if(g.getLeftBottom().x > Configuration.VIEWPORT_WIDTH+(g instanceof Fireball ? 1f : 5f) || g.getLeftBottom().y > Configuration.VIEWPORT_HEIGHT || g.getRightTop().x < 0 || g.getRightTop().y < 0 ) {
  						if(!g.free()) g.dispose();
						iter.remove();
					}
				} else {
					if(g.getLeftBottom().y > Configuration.VIEWPORT_HEIGHT || g.getRightTop().x < 0 || g.getRightTop().y < 0 ) {
						if(!g.free()) g.dispose();
						iter.remove();
					}				
				}
			} else iter.remove();
		}
	}
	
	public void clear() {
		for(GameObject g:objects) {
			if(!g.free()) g.dispose();
		}
		this.objects.clear();
	}
	
	public void dispose() {
		for(GameObject g:objects) {
			if(g != null) {
				if(!g.free()) g.dispose();
			}
		}
		this.pools.clear();
		this.pools = null;
		this.objects.clear();
		this.objects = null;
	}	

}
