package com.xsheetgames.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.xsheetgames.Configuration;
import com.xsheetgames.GameAssets;
import com.xsheetgames.genericElements.CoverLayout;

public class CreditsScreen extends AbstractScreen {

	private SpriteBatch batch;
	private Sprite screenBackground;
	private Sprite blackLayer, backBtn;
	private boolean disposed = false;
	private boolean assetsLoaded;
	private String creditString1 = "Thank you for playing our game.";	
	private String creditString2 = "Mike and Sebi";
	private String creditString3 = "(Piano by Christoph Richter. Thx!!)";
	private boolean lastConnectedState;
	
	public CreditsScreen(Game game) {
		this.game = game;
	}
	
	
	@Override
	public void render(float delta) {
		if(this.disposed == false) {
			if(GameAssets.assetsLoaded(batch)) {
				if(assetsLoaded == false) this.doAssetProcessing();
				this.beginScreenPass(batch);
				CoverLayout.apply(screenBackground, screenBackground.getRegionWidth(), screenBackground.getRegionHeight(), Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
				this.blackLayer.setSize(Gdx.graphics.getWidth()+10f, Gdx.graphics.getHeight()+10f);
				this.blackLayer.setPosition(-5f, -5f);
				batch.begin();
				screenBackground.draw(batch);
				blackLayer.draw(batch);
				batch.end();

				this.beginUiPass(batch);
				batch.begin();
				GameAssets.glyphLayout.setText(GameAssets.fetchFont("fonts/memory.fnt"), this.creditString1);
				GameAssets.fetchFont("fonts/memory.fnt").draw(batch,this.creditString1, Configuration.TARGET_WIDTH/2 - GameAssets.glyphLayout.width/2, 700f);
				GameAssets.fetchFont("fonts/memory.fnt").draw(batch,this.creditString2, 600f, 600f);
				GameAssets.fetchFont("fonts/memory.fnt").draw(batch,"xSheetGames", 600f, 550f);
                GameAssets.glyphLayout.setText(GameAssets.fetchFont("fonts/memory.fnt"), this.creditString3);
				GameAssets.fetchFont("fonts/memory.fnt").draw(batch,this.creditString3, Configuration.TARGET_WIDTH/2 - GameAssets.glyphLayout.width/2, 330f);
				
				backBtn.draw(batch);
				batch.end();
				this.endScreenRender();

				//Emulate Events
				if(GameAssets.buttonTimer > 0f) GameAssets.buttonTimer-=delta;
}
		}
	}

	
	@Override
	public void resize(int width, int height) {
		this.updateUiViewport(width, height);
	}

	@Override
	public void show() {

		this.assetsLoaded = false;
		this.batch = new SpriteBatch();
		this.setupUiViewport();

		lastConnectedState = GameAssets.input.isControllerConnected();

		this.screenBackground = new Sprite(GameAssets.fetchTexture("menu/images/credits_back.jpg"));
		this.blackLayer = new Sprite(GameAssets.fetchTextureAtlas("menu/images/menu_items.pack").findRegion("blackLayer"));
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
			blackLayer=null;
			backBtn=null;
			screenBackground = null;
			this.disposed = true;
		}		
	}
	
	
	private void doAssetProcessing() {		
		TextureAtlas atlas = GameAssets.fetchTextureAtlas("menu/images/menu_items.pack");		
		this.backBtn = new Sprite(atlas.findRegion("backbtn"));
		this.backBtn.setSize(atlas.findRegion("backbtn").getRegionWidth(), atlas.findRegion("backbtn").getRegionHeight());
		this.backBtn.setPosition(1000f,80f);		
		this.assetsLoaded = true;
	}


	@Override
	public void stepBack(String source) {
		if(GameAssets.buttonTimer <= 0f) {
			this.game.setScreen(new MenuScreen(this.game));
			GameAssets.buttonTimer = 0.35f;
		}
	}


	@Override
	public void startPress() {
		if(GameAssets.buttonTimer <= 0f) {
			this.game.setScreen(new MenuScreen(this.game));
			GameAssets.buttonTimer = 0.35f;
		}
	}


	@Override
	public void primaryPress() {
		if(GameAssets.buttonTimer <= 0f) {
			this.game.setScreen(new MenuScreen(this.game));
			GameAssets.buttonTimer = 0.35f;
		}
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
		Vector2 touchPoint = this.unprojectUi(x, y);
		if(backBtn != null && backBtn.getBoundingRectangle().contains(touchPoint.x, touchPoint.y)) {
			GameAssets.playSound(GameAssets.fetchSound("menu/sounds/click.mp3"));
			this.dispose();
			this.game.setScreen(new SettingsScreen(this.game));
			return true;
		}
		return false;
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
