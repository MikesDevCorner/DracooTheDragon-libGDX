package com.xsheetgames.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.xsheetgames.Configuration;
import com.xsheetgames.GameAssets;

public class EndPackScreen extends AbstractScreen {

	private SpriteBatch batch;
	private Sprite screenBackground,purchase,menu;
	private boolean disposed = false;
	private boolean assetsLoaded;
	private String comic;
	private GameScreen gs;
	
	public EndPackScreen(Game game, GameScreen gs) {
		this.game = game;
		this.gs = gs;
	}
	
	public void setComic(String comic) {
		this.comic = comic;
	}
	
	
	@Override
	public void render(float delta) {
		if(this.disposed == false) {
			if(GameAssets.assetsLoaded(batch)) {
				this.batch.getProjectionMatrix().setToOrtho2D(0, 0, Configuration.TARGET_WIDTH, Configuration.TARGET_HEIGHT);
				if(assetsLoaded == false) this.doAssetProcessing();
				batch.begin();
				screenBackground.draw(batch);
				this.menu.draw(batch);
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
		
		GameAssets.nativ.trackPageView("/EndPack");
		
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
			this.purchase = null;
			this.menu = null;
			batch = null;
			screenBackground = null;
			this.disposed = true;
		}		
	}
	
	
	private void doAssetProcessing() {			
		this.assetsLoaded = true;
		this.screenBackground = new Sprite(GameAssets.fetchTexture(this.comic));
		screenBackground.setSize(GameAssets.fetchTexture(this.comic).getWidth(), GameAssets.fetchTexture(this.comic).getHeight());
		
		if(this.purchase == null)
		{			
			TextureAtlas atlas = GameAssets.fetchTextureAtlas("game/images/game_objects.pack");
			
			this.menu = new Sprite(atlas.findRegion("menu"));
			this.menu.setSize(atlas.findRegion("menu").getRegionWidth(), atlas.findRegion("menu").getRegionHeight());
			this.menu.setPosition(1100f, 25f);
			
		}
	}
	
	
	public void purchaseButtonPressed() {		
		GameAssets.nativ.sendEvent("SocialAction", "Purchase Button", "pressed", 1);
		//GameAssets.nativ.purchaseItem(this.gs.getLevelpack().playStoreItemName);
	}



	public void commandToMainScreen() {
		this.dispose();
		this.gs.unloadAssetsAndFinalize();
		this.game.setScreen(new MenuScreen(this.game));
	}

	@Override
	public void stepBack(String source) {
		this.dispose();
		this.gs.unloadAssetsAndFinalize();
		this.game.setScreen(new MenuScreen(this.game));
		
	}

	@Override
	public void startPress() {
		this.dispose();
		this.gs.unloadAssetsAndFinalize();
		this.game.setScreen(new MenuScreen(this.game));
		
	}

	@Override
	public void primaryPress() {
		this.dispose();
		this.gs.unloadAssetsAndFinalize();
		this.game.setScreen(new MenuScreen(this.game));
		
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
		this.dispose();
		this.gs.unloadAssetsAndFinalize();
		this.game.setScreen(new MenuScreen(this.game));
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
