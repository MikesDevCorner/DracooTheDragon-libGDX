package com.xsheetgames.screens;

import com.badlogic.gdx.Game;
//import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.xsheetgames.Configuration;
import com.xsheetgames.GameAssets;
//import sun.security.ssl.Debug;

import java.io.Console;

public class AdScreen extends AbstractScreen {

	private SpriteBatch batch;
	private Sprite screenBackground, blackLayer;
	private boolean disposed = false;
	private boolean assetsLoaded;
	private AbstractScreen nextScreen;
	private boolean menu;
	private String menuString = "menu/images/credits_back.jpg";
	private String gameString = "game/images/background.jpg";
	private TextureAtlas menuAtlas;
	private TextureAtlas gameAtlas;
	private boolean adShowed;
	public static int howOftenShowed = 0;
	private String message  = "Please wait for the AD";
	private String message2  = "afterwards tap to continue...";
	private float halfLineSpace = 30f;
	private int modOperator = 4;
	private int modResidue = 3;
	private boolean forced = false;
	private String adPoint = "";
	
	
	public AdScreen(Game game, AbstractScreen nextScreen, boolean menu, boolean force, String adPoint) {
		this.game = game;
		this.nextScreen = nextScreen;
		this.menu = menu;
		this.adPoint = adPoint;
		this.forced = force;
	}
	
	
	@Override
	public void render(float delta) {
		int myModResidue = this.modResidue;
		if((this.modResidue+1) % this.modOperator == 0) myModResidue = -1;
		if(this.disposed == false && (AdScreen.howOftenShowed % this.modOperator == myModResidue+1 || this.forced == true)) {
			if(GameAssets.assetsLoaded(batch)) {
					
				this.batch.getProjectionMatrix().setToOrtho2D(0, 0, Configuration.TARGET_WIDTH, Configuration.TARGET_HEIGHT);
				if(assetsLoaded == false) this.doAssetProcessing();
				batch.begin();
				screenBackground.draw(batch);
				
				if(this.adShowed == false && this.forced == true) {
					batch.end();
					this.adShowed = true;
					GameAssets.nativ.showFullScreenAd(this.adPoint);
				} else {
				
					if(Configuration.waitForAd == false) {
						batch.end();
						if(this.adShowed == false) {
							this.adShowed = true;
							GameAssets.nativ.showFullScreenAd(this.adPoint);
						}
						this.changeToNextScreen();
					} else {
						blackLayer.draw(batch);
						batch.getProjectionMatrix().setToOrtho2D(0, 0, Configuration.TARGET_WIDTH, Configuration.TARGET_HEIGHT);
						GameAssets.glyphLayout.setText(GameAssets.fetchFont("fonts/memory.fnt"), this.message);
						GameAssets.fetchFont("fonts/memory.fnt").draw(batch,this.message,Configuration.TARGET_WIDTH/2-GameAssets.glyphLayout.width/2,(Configuration.TARGET_HEIGHT/2-GameAssets.glyphLayout.height/2)+this.halfLineSpace);
						GameAssets.glyphLayout.setText(GameAssets.fetchFont("fonts/memory.fnt"), this.message2);
						GameAssets.fetchFont("fonts/memory.fnt").draw(batch,this.message2,Configuration.TARGET_WIDTH/2-GameAssets.glyphLayout.width/2,(Configuration.TARGET_HEIGHT/2-GameAssets.glyphLayout.height/2)-this.halfLineSpace);
						
						batch.end();
						if(this.adShowed == false) {
							this.adShowed = true;
							GameAssets.nativ.showFullScreenAd(this.adPoint);
						}
					}
				}
			}
		}
	}

	
	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub		
	}

	@Override
	public void show() {
		if( AdScreen.howOftenShowed % this.modOperator == this.modResidue || this.forced == true) {
			//GameAssets.nativ.trackPageView("/AdScreen");
			this.assetsLoaded = false;
			this.batch = new SpriteBatch();
			this.adShowed = false;
		} else {
			this.dispose();
			if(this.nextScreen instanceof iAdAble) {
				((iAdAble) this.nextScreen).setAdShowed(true);
			}
			this.game.setScreen(this.nextScreen);
		}
		if(this.forced == false) AdScreen.howOftenShowed++;
	}


	@Override
	public void dispose() {
		if(this.disposed == false) {
			if(batch != null) batch.dispose();
			batch = null;
			screenBackground = null;
			this.disposed = true;
		}		
	}
	
	
	private void doAssetProcessing() {
		this.assetsLoaded = true;
		String assetSource = this.gameString;
		if(menu == true) {
			this.menuAtlas = GameAssets.fetchTextureAtlas("menu/images/menu_items.pack");
			assetSource = this.menuString;
			this.blackLayer = new Sprite(this.menuAtlas.findRegion("blackLayer"));
		} else {
			this.gameAtlas = GameAssets.fetchTextureAtlas("game/images/game_objects.pack");
			this.blackLayer = new Sprite(this.gameAtlas.findRegion("blackLayer"));
		}
		this.blackLayer.setSize(1280f+10f,800f+10f);
		this.blackLayer.setPosition(-5f, -5f);
		this.screenBackground = new Sprite(GameAssets.fetchTexture(assetSource));
		screenBackground.setSize(GameAssets.fetchTexture(assetSource).getWidth(), GameAssets.fetchTexture(assetSource).getHeight());		 
	}
	
	
	public void changeToNextScreen() {
		this.dispose();
		if(this.nextScreen instanceof iAdAble) {
			((iAdAble) this.nextScreen).setAdShowed(true);
		}
		this.game.setScreen(this.nextScreen);
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
	public void stepBack(String source) {
		this.changeToNextScreen();		
	}


	@Override
	public void startPress() {
		this.changeToNextScreen();		
	}


	@Override
	public void primaryPress() {
		this.changeToNextScreen();		
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
		this.changeToNextScreen();
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
