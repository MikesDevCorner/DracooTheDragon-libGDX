package com.xsheetgames.genericElements;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.xsheetgames.GameAssets;
import com.xsheetgames.genericGameObjects.Hurtable;

public class BossEnergyMeter {
	
	private Hurtable myHurtable;
	private Sprite track, display;
	private boolean disposed;
	private float fullWidth;
	private float fullHeight;
	
	public BossEnergyMeter() {
		this.myHurtable = null;
		this.disposed = false;
		this.track = new Sprite(GameAssets.fetchTextureAtlas("game/images/game_objects.pack").findRegion("life_bar_enemy"));
		this.track.setSize(GameAssets.fetchTextureAtlas("game/images/game_objects.pack").findRegion("life_bar_enemy").getRegionWidth(), GameAssets.fetchTextureAtlas("game/images/game_objects.pack").findRegion("life_bar_enemy").getRegionHeight());
		this.track.setPosition(920f,756f);
		
		this.fullWidth = GameAssets.fetchTextureAtlas("game/images/game_objects.pack").findRegion("life_bar_enemies_status").getRegionWidth();
		this.fullHeight = GameAssets.fetchTextureAtlas("game/images/game_objects.pack").findRegion("life_bar_enemies_status").getRegionHeight();
		this.display = new Sprite(GameAssets.fetchTextureAtlas("game/images/game_objects.pack").findRegion("life_bar_enemies_status"));
		this.display.setSize(this.fullWidth, this.fullHeight);
		this.display.setPosition(936f,761f);
	}
	
	public void registerHurtable(Hurtable h) {
		this.myHurtable = h;
	}
	
	public void unregisterHurtable() {
		this.myHurtable = null;
	}
	
	public void draw(SpriteBatch batch) {
		if(this.disposed == false && this.myHurtable != null)
		{			
			if(this.track != null) track.draw(batch);
			if(this.display != null) {
				float percent = 100 * myHurtable.getEnergy() / myHurtable.getStartEnergy();
				float newWidth = percent * fullWidth / 100;
				this.display.setSize(newWidth, this.fullHeight);
				display.draw(batch);
			}
		}
	}
	
	public void resetGraphics() {
		this.track = new Sprite(GameAssets.fetchTextureAtlas("game/images/game_objects.pack").findRegion("life_bar_enemy"));
		this.track.setSize(GameAssets.fetchTextureAtlas("game/images/game_objects.pack").findRegion("life_bar_enemy").getRegionWidth(), GameAssets.fetchTextureAtlas("game/images/game_objects.pack").findRegion("life_bar_enemy").getRegionHeight());
		this.track.setPosition(920f,756f);
		
		this.fullWidth = GameAssets.fetchTextureAtlas("game/images/game_objects.pack").findRegion("life_bar_enemies_status").getRegionWidth();
		this.fullHeight = GameAssets.fetchTextureAtlas("game/images/game_objects.pack").findRegion("life_bar_enemies_status").getRegionHeight();
		this.display = new Sprite(GameAssets.fetchTextureAtlas("game/images/game_objects.pack").findRegion("life_bar_enemies_status"));
		this.display.setSize(this.fullWidth, this.fullHeight);
		this.display.setPosition(936f,761f);
	}
	
	public void dispose() {
		this.track = null;
		this.display = null;
		this.myHurtable = null;
		this.disposed = true;
	}
	
	
}
