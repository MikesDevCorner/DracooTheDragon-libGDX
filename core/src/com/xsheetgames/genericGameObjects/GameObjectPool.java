package com.xsheetgames.genericGameObjects;



import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Array;
import com.xsheetgames.Configuration;


public class GameObjectPool {
	public final int max;
	public int initialCapacity;

	private final Array<GameObject> freeObjects;
	public final GameObjectFactory factory;


	public GameObjectPool (GameObjectFactory f) {
		this(4, Integer.MAX_VALUE, f);
	}


	public GameObjectPool (int initialCapacity, GameObjectFactory f) {
		this(initialCapacity, Integer.MAX_VALUE, f);
	}


	public GameObjectPool (int initialCapacity, int max, GameObjectFactory f) {
		freeObjects = new Array<GameObject>(false, initialCapacity);
		this.initialCapacity = initialCapacity;
		this.factory = f;
		this.max = max;
	}

	protected GameObject newObject(GameObjectFactory f) {
		return f.createObject();
	}


	public GameObject obtain () {
		if(freeObjects.size < 1) {
			GameObject o = this.newObject(this.factory); 
			if(Configuration.debugLevel >= Application.LOG_INFO && Configuration.poolingInfos) Gdx.app.log("Entering stage (new)",o.getClass().getName()+" - "+o.hashCode()+" / PoolSize: "+freeObjects.size);
			return o;
		} else {
			GameObject o = freeObjects.pop();
			freeObjects.removeValue(o, false);
			if(Configuration.debugLevel >= Application.LOG_INFO && Configuration.poolingInfos) Gdx.app.log("Entering stage (existing)",o.getClass().getName()+" - "+o.hashCode()+" / PoolSize: "+freeObjects.size);
			o.getBody().setTransform(0f, 0f, 0f);
			o.getBody().setLinearVelocity(0f, 0f);
			o.obtainInit();
			return o;
		}
	}
	
	public void free (GameObject object) {
		if (object == null) throw new IllegalArgumentException("object cannot be null.");
		else object.reset();
		if (freeObjects.size < max) {
			if(!freeObjects.contains(object, false)) freeObjects.add(object);
			if(Configuration.debugLevel >= Application.LOG_INFO && Configuration.poolingInfos) Gdx.app.log("Leaving stage: ",object.getClass().getName()+" - "+object.hashCode()+" / PoolSize: "+freeObjects.size);
		} else {
			object.setDisposing = true;
		}
	}

	
	public void free (Array<GameObject> objects) {
		for (int i = 0, n = Math.min(objects.size, max - freeObjects.size); i < n; i++) {
			objects.get(i).reset();
			freeObjects.add(objects.get(i));
		}
	}
	
	public void resetGraphics(TextureAtlas atlas) {
		for (GameObject a : freeObjects) {
			a.resetGraphics(atlas);
		}		
	}

	
	public void clear () {
		for(GameObject a : freeObjects) {
			a.dispose();
		}
		freeObjects.clear();
	}
	
	public void dispose() {
		for(GameObject o:this.freeObjects) {
			o.dispose();
		}
		this.clear();
	}
}
