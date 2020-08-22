package com.xsheetgames.genericElements;


import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.xsheetgames.Configuration;
import com.xsheetgames.screens.GameScreen;

public class AtlasAnimation {

	private Animation<TextureRegion> animation;
	private TextureRegion currentFrame;
	private float stateTime;
	private float renderX;
	private float renderY;
	private boolean animationRunning;
	private TextureAtlas atlas;
	private float velX, velY;
	private String animationName;
	
	
	public AtlasAnimation(TextureAtlas atlas, String animationName, float x, float y, float vx, float vy, float percentX, float percentY) {
		this.atlas =atlas;
		this.renderX = x - (atlas.findRegions(animationName).get(0).getRegionWidth() / 100 * percentX) * Configuration.VIEWPORT_WIDTH / Configuration.TARGET_WIDTH;
		this.renderY = y - (atlas.findRegions(animationName).get(0).getRegionHeight() / 100 * percentY) * Configuration.VIEWPORT_HEIGHT / Configuration.TARGET_HEIGHT;
        this.animation = new Animation(1f/ 25f, this.atlas.findRegions(animationName), Animation.PlayMode.NORMAL);
        this.stateTime = 0f;
        this.animationRunning = true;
        this.velX = vx;
        this.velY = vy;
        this.animationName = animationName;
	}
	
	
	public boolean isRunning() {
		return this.animationRunning;
	}
	
	
	public void resetGraphics(TextureAtlas atlas) {
		this.atlas = atlas;
		this.animation = new Animation(1f/ 25f, this.atlas.findRegions(this.animationName));
	}
	

	public void draw(float delta, SpriteBatch batch) {
		if(GameScreen.paused == false)
		{
			this.stateTime += delta;
			this.renderX = this.renderX + (delta*this.velX);
			this.renderY = this.renderY + (delta*this.velY);
			
	        if(this.stateTime >= this.animation.getAnimationDuration())  /*this.animation.isAnimationFinished(this.stateTime)*/
	        {
	        	this.animationRunning = false;
	        }
        }
        
        if(this.animationRunning) {
        	this.currentFrame = animation.getKeyFrame(this.stateTime, true);
            batch.draw(currentFrame, this.renderX, this.renderY, Configuration.VIEWPORT_WIDTH * currentFrame.getRegionWidth() / Configuration.TARGET_WIDTH, Configuration.VIEWPORT_HEIGHT * currentFrame.getRegionHeight() / Configuration.TARGET_HEIGHT);
        }
	}
	
	public void dispose() {
		this.animation = null;
		this.atlas = null;
		this.currentFrame = null;
	}
	
}
