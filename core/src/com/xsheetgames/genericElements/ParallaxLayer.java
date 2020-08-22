package com.xsheetgames.genericElements;

import java.util.Iterator;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.xsheetgames.Configuration;

public class ParallaxLayer extends Pool<Sprite>{
	
	private float initialVelocity;
	private float layerVelocity;
	private Array<Sprite> layerElements;
	private boolean doElement;
	private Texture layerTexture;
	
	
	public ParallaxLayer(float speed, Texture texture) {
		
		super((int)(Math.floor(Configuration.VIEWPORT_WIDTH / Configuration.VIEWPORT_WIDTH * texture.getWidth() / Configuration.TARGET_WIDTH))+1);
		this.initialVelocity = speed;
		layerElements = new Array<Sprite>((int)(Math.floor(Configuration.VIEWPORT_WIDTH / Configuration.VIEWPORT_WIDTH * texture.getWidth() / Configuration.TARGET_WIDTH))+1);
		this.layerVelocity = this.initialVelocity;
	   
		layerTexture = texture;
	   
		doElement = true;
	   
		this.spawnLayerElement(0f,0f);
	}
	
	public void setSpeed(float speed) {
		this.initialVelocity = speed;
		this.layerVelocity = speed;
	}
	
	public void setPause(boolean pause) {
		if(pause) this.layerVelocity = 0;
		else this.layerVelocity = this.initialVelocity;
	}
	
	public void doLogic(float delta) {
	   Iterator<Sprite> iter = layerElements.iterator();
	   while(iter.hasNext()) {
	      Sprite layerEl = iter.next();
	      layerEl.setPosition(layerEl.getX()-this.layerVelocity * delta, layerEl.getY());
	      if(layerEl.getX() <= 0 && doElement == true) {
	    	  if(this.layerVelocity > 0f) {
		    	  spawnLayerElement(layerEl.getX()+layerEl.getWidth(), 0f);
		    	  doElement = false;
	    	  }
	      }
	      if(layerEl.getX() + layerEl.getWidth() <= 0) {
	     	 iter.remove();
	     	 this.free(layerEl);
	     	 doElement = true;
	      } 
	   }
	}
	
	public void draw(SpriteBatch batch) {
	   for(Sprite layerelement: layerElements) {
		   layerelement.draw(batch);
	   }
	}
	
	public Array<Sprite> getLayerElements() {
		return this.layerElements;
	}
	
	
	
   private void spawnLayerElement(float x, float y) {
      Sprite layerElement = this.obtain();
      layerElement.setPosition(x,y);
      layerElement.setSize(Configuration.VIEWPORT_WIDTH * this.layerTexture.getWidth() / Configuration.TARGET_WIDTH, Configuration.VIEWPORT_HEIGHT * this.layerTexture.getHeight() / Configuration.TARGET_HEIGHT);
      layerElements.add(layerElement);
   }
	
	public void dispose() {
		this.freeAll(this.layerElements);
		this.clear();
		this.layerElements.clear();
		this.layerElements = null;
		this.layerTexture = null;
	}
	

	@Override
	protected Sprite newObject() {
		return new Sprite(layerTexture);
	}
}
