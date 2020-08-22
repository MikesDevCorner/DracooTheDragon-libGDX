package com.xsheetgames.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.xsheetgames.Configuration;
import com.xsheetgames.GameAssets;

public class StartPackScreen extends AbstractScreen {

	private SpriteBatch batch;
	private Sprite screenBackground;
	private boolean disposed = false;
	private boolean assetsLoaded;
	private GameScreen gameScreen;
	private String comic;
	
	
	public StartPackScreen(Game game) {
		this.game = game;
	}
	
	public void setComic(String comic) {
		this.comic = comic;
	}
	
	public void setGameScreen(GameScreen gameScreen) {
		this.gameScreen = gameScreen;
	}
	
	
	@Override
	public void render(float delta) {
		if(this.disposed == false) {
			if(GameAssets.assetsLoaded(batch)) {
				this.batch.getProjectionMatrix().setToOrtho2D(0, 0, Configuration.TARGET_WIDTH, Configuration.TARGET_HEIGHT);
				if(assetsLoaded == false) this.doAssetProcessing();
				batch.begin();
				screenBackground.draw(batch);
				batch.end();
			}
		}
	}

	
	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub		
	}

	@Override
	public void show() {
		
		GameAssets.nativ.trackPageView("/StartPackScreen");
		this.assetsLoaded = false;
		this.batch = new SpriteBatch();
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub		
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub		
	}

	@Override
	public void dispose() {
		if(this.disposed == false) {
			batch.dispose();
			batch = null;
			screenBackground = null;
			this.disposed = true;
		}		
	}
	
	
	private void doAssetProcessing() {
		this.assetsLoaded = true;
		this.screenBackground = new Sprite(GameAssets.fetchTexture(this.comic));
		screenBackground.setSize(GameAssets.fetchTexture(this.comic).getWidth(), GameAssets.fetchTexture(this.comic).getHeight());
	}
	



	@Override
	public void stepBack(String source) {
		this.dispose();
		GameAssets.unloadGameAssets();
		GameAssets.manager.clear();
		GameAssets.manager.finishLoading();
		GameAssets.loadMenuAssets();
		this.game.setScreen(new MenuScreen(this.game));
	}

	@Override
	public void startPress() {
		this.dispose();
		this.game.setScreen(gameScreen);
	}

	@Override
	public void primaryPress() {
		this.dispose();
		this.game.setScreen(gameScreen);
	}

	@Override
	public void steerXAxis(float peculiarity) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void steerYAxis(float peculiarity) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean screenTouched(int x, int y, int pointer) {
		this.game.setScreen(gameScreen);
		return true;
	}

	@Override
	public boolean screenAfterTouched(int x, int y, int pointer) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean screenWhileTouch(int x, int y, int pointer) {
		// TODO Auto-generated method stub
		return false;
	}

}
