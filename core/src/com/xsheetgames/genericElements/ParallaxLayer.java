package com.xsheetgames.genericElements;

import java.util.Iterator;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.xsheetgames.Configuration;

public class ParallaxLayer extends Pool<Sprite>{

	// Kapazitaets-Hinweis: mehr Kacheln als je gleichzeitig sichtbar sind.
	private static final int INITIAL_CAPACITY = 4;

	private float initialVelocity;
	private float layerVelocity;
	private Array<Sprite> layerElements;
	private Texture layerTexture;
	private boolean isStatic;


	public ParallaxLayer(float speed, Texture texture) {

		super(INITIAL_CAPACITY);
		this.initialVelocity = speed;
		layerElements = new Array<Sprite>(INITIAL_CAPACITY);
		this.layerVelocity = this.initialVelocity;

		layerTexture = texture;
		// Non-scrolling layers (e.g. the static background) never tile, so they
		// must cover the whole - possibly wider - viewport on their own.
		this.isStatic = (speed == 0f);

		this.spawnLayerElement(0f,0f);
		// Scrollende Layer sofort ueber die volle (aspektabhaengige) Weltbreite
		// kacheln - doLogic laeuft im Start-Pause-Zustand noch nicht.
		if(!this.isStatic) this.fillToWorldWidth(this.nativeTileWidth());
	}

	public void setSpeed(float speed) {
		this.initialVelocity = speed;
		this.layerVelocity = speed;
		this.isStatic = (speed == 0f);
	}

	public void setPause(boolean pause) {
		if(pause) this.layerVelocity = 0;
		else this.layerVelocity = this.initialVelocity;
	}

	public void doLogic(float delta) {
	   if(this.isStatic) {
	      // Cover jeden Frame neu anwenden - macht statische Layer resize-sicher.
	      for(Sprite layerEl : layerElements) applyStaticCover(layerEl);
	      return;
	   }

	   // Alle Kacheln bewegen, links rausgelaufene freigeben und dabei die
	   // rechteste abgedeckte Kante verfolgen.
	   float rightEdge = 0f;
	   Iterator<Sprite> iter = layerElements.iterator();
	   while(iter.hasNext()) {
	      Sprite layerEl = iter.next();
	      layerEl.setPosition(layerEl.getX()-this.layerVelocity * delta, layerEl.getY());
	      if(layerEl.getX() + layerEl.getWidth() <= 0) {
	     	 iter.remove();
	     	 this.free(layerEl);
	      } else if(layerEl.getX() + layerEl.getWidth() > rightEdge) {
	     	 rightEdge = layerEl.getX() + layerEl.getWidth();
	      }
	   }

	   this.fillToWorldWidth(rightEdge);
	}

	public void draw(SpriteBatch batch) {
	   for(Sprite layerelement: layerElements) {
		   layerelement.draw(batch);
	   }
	}

	public Array<Sprite> getLayerElements() {
		return this.layerElements;
	}



   // Native Kachelbreite in Metern (Pixel-Seitenverhaeltnis bleibt erhalten).
   private float nativeTileWidth() {
      return Configuration.VIEWPORT_WIDTH * this.layerTexture.getWidth() / Configuration.TARGET_WIDTH;
   }

   // Luecken bis zur tatsaechlichen (aspektabhaengigen) Weltbreite auffuellen;
   // neue Kacheln haengen sich immer an/hinter der sichtbaren Kante an.
   private void fillToWorldWidth(float rightEdge) {
      float nativeWidth = nativeTileWidth();
      while(rightEdge < Configuration.GAME_WORLD_WIDTH) {
         spawnLayerElement(rightEdge, 0f);
         rightEdge += nativeWidth;
      }
   }

   private void spawnLayerElement(float x, float y) {
      Sprite layerElement = this.obtain();
      // Native size of the texture expressed in world meters (keeps pixel aspect).
      float nativeWidth = Configuration.VIEWPORT_WIDTH * this.layerTexture.getWidth() / Configuration.TARGET_WIDTH;
      float nativeHeight = Configuration.VIEWPORT_HEIGHT * this.layerTexture.getHeight() / Configuration.TARGET_HEIGHT;

      if(this.isStatic) {
         applyStaticCover(layerElement);
      } else {
         layerElement.setPosition(x,y);
         layerElement.setSize(nativeWidth, nativeHeight);
      }
      layerElements.add(layerElement);
   }

   // object-fit: cover - scale uniformly to fill the (possibly wider)
   // viewport, cropping the overflow, then center. No axis is stretched.
   private void applyStaticCover(Sprite layerElement) {
      float nativeWidth = Configuration.VIEWPORT_WIDTH * this.layerTexture.getWidth() / Configuration.TARGET_WIDTH;
      float nativeHeight = Configuration.VIEWPORT_HEIGHT * this.layerTexture.getHeight() / Configuration.TARGET_HEIGHT;
      CoverLayout.apply(layerElement, nativeWidth, nativeHeight,
            Configuration.GAME_WORLD_WIDTH, Configuration.VIEWPORT_HEIGHT, CoverLayout.CENTER, CoverLayout.CENTER);
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
