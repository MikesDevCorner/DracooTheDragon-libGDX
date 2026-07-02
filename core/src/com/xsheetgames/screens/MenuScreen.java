package com.xsheetgames.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.xsheetgames.Configuration;
import com.xsheetgames.GameAssets;
import com.xsheetgames.genericElements.CoverLayout;

public class MenuScreen extends AbstractScreen {

	private SpriteBatch batch;
	private Sprite screenBackground, playButton, rate, share, settings, more;
	private float bgWidth, bgHeight;
	private boolean assetsLoaded;
	private boolean disposed = false;
	private boolean endApp = false;
	private static boolean adShowed;

	
	public MenuScreen(Game game) {
		this.game = game;
	}
	
	int rendercount = 0;
	
	@Override
	public void render(float delta) {
		
		if(this.disposed == false) {
			if(GameAssets.assetsLoaded(batch)) {
				if(assetsLoaded == false) this.doAssetProcessing();

				// 1) Background: object-fit:cover - fill the whole screen keeping
				//    the image aspect ratio. Anchor left/top: das 16:9-Motiv wird
				//    rechts (4:3, 16:10) bzw. unten (21:10) beschnitten.
				this.beginScreenPass(batch);
				CoverLayout.apply(this.screenBackground, this.bgWidth, this.bgHeight,
						Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), CoverLayout.LEFT, CoverLayout.TOP);
				this.batch.begin();
				batch.disableBlending();
				this.screenBackground.draw(batch);
				batch.enableBlending();
				this.batch.end();

				// 2) UI: keep the buttons at the correct aspect ratio and fully on
				//    screen (the letterbox margins are covered by the background).
				this.beginUiPass(batch);
				this.batch.begin();
				this.playButton.draw(batch);
				this.settings.draw(batch);
				this.rate.draw(batch);
				this.share.draw(batch);
				if(this.more != null) this.more.draw(batch);
				this.batch.end();

				this.endScreenRender();

				//Emulate Events
				if(GameAssets.buttonTimer > 0f) GameAssets.buttonTimer-=delta;
			}
		}

	}

	// ExtendViewport statt FitViewport: die UI-Welt waechst mit dem
	// Seitenverhaeltnis mit, damit die Buttons wirklich in den
	// Bildschirmecken sitzen (keine Letterbox-Raender).
	@Override
	protected void setupUiViewport() {
		this.uiViewport = new ExtendViewport(Configuration.TARGET_WIDTH, Configuration.TARGET_HEIGHT);
		this.uiViewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
	}

	@Override
	public void resize(int width, int height) {
		this.updateUiViewport(width, height);
		this.layoutButtons();
	}

	// Play oben rechts, Settings rechtsbuendig darunter; Share/Rate unten
	// links - jeweils mit etwas Abstand zur Bildschirmkante. Auf schmalen
	// Displays (Aspect < 1.666) wandern Play/Settings als zweite Spalte
	// direkt neben die Rate/Share-Buttons.
	private void layoutButtons() {
		if(this.uiViewport == null || this.playButton == null) return;
		float pad = 24f;
		float gap = 16f;
		float worldW = this.uiViewport.getWorldWidth();
		float worldH = this.uiViewport.getWorldHeight();

		if(this.rate != null) this.rate.setPosition(pad, pad);
		if(this.share != null && this.rate != null) this.share.setPosition(pad, this.rate.getY() + this.rate.getHeight() + gap);
		if(this.more != null && this.share != null) this.more.setPosition(pad, this.share.getY() + this.share.getHeight() + gap);

		if(worldW / worldH < 1.666f && this.rate != null && this.share != null) {
			float colX = pad + Math.max(this.rate.getWidth(), this.share.getWidth()) + gap;
			if(this.settings != null) this.settings.setPosition(colX, pad);
			this.playButton.setPosition(colX, (this.settings != null) ? this.settings.getY() + this.settings.getHeight() + gap : pad);
		} else {
			this.playButton.setPosition(worldW - this.playButton.getWidth() - pad, worldH - this.playButton.getHeight() - pad);
			if(this.settings != null) this.settings.setPosition(worldW - this.settings.getWidth() - pad, this.playButton.getY() - this.settings.getHeight() - gap);
		}
	}

	@Override
	public void show() {

		if(MenuScreen.adShowed == false) {
			MenuScreen.adShowed = true;
		}
		
		
		if(MenuScreen.adShowed == true) {
			if(this.endApp == true) {
				Gdx.app.exit();
			}
			
			
			this.batch = new SpriteBatch();
			this.setupUiViewport();
			this.assetsLoaded = false;
		}
	}

	@Override
	public void hide() {
		//this.dispose();		
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		GameAssets.loadMenuAssets();
		this.assetsLoaded = false;
	}

	@Override
	public void dispose() {
		if(this.disposed == false) {
			if(this.batch != null) this.batch.dispose();
			this.playButton = null;
			this.more = null;
			this.settings = null;
			this.share = null;
			this.rate = null;
			this.screenBackground = null;
			this.disposed = true;
		}		
	}
	
	@SuppressWarnings("unused")
	private void doAssetProcessing() {
		this.assetsLoaded = true;
		if(this.screenBackground == null) {
			this.screenBackground = new Sprite(GameAssets.fetchTexture("menu/images/menuScreen2.jpg"));
			this.bgWidth = GameAssets.fetchTexture("menu/images/menuScreen2.jpg").getWidth();
			this.bgHeight = GameAssets.fetchTexture("menu/images/menuScreen2.jpg").getHeight();
		}
		if(this.playButton == null) {

			this.playButton = new Sprite(GameAssets.fetchTextureAtlas("menu/images/menu_items.pack").findRegion("playbtn"));
			this.playButton.setSize(GameAssets.fetchTextureAtlas("menu/images/menu_items.pack").findRegion("playbtn").getRegionWidth(), GameAssets.fetchTextureAtlas("menu/images/menu_items.pack").findRegion("playbtn").getRegionHeight());
		}

		if(this.settings == null) {
			this.settings = new Sprite(GameAssets.fetchTextureAtlas("menu/images/menu_items.pack").findRegion("settings"));
			this.settings.setSize(GameAssets.fetchTextureAtlas("menu/images/menu_items.pack").findRegion("settings").getRegionWidth(), GameAssets.fetchTextureAtlas("menu/images/menu_items.pack").findRegion("settings").getRegionHeight());
		}

		if(this.rate == null)
		{
			TextureAtlas atlas = GameAssets.fetchTextureAtlas("menu/images/menu_items.pack");

			this.rate = new Sprite(atlas.findRegion("rate"));
			this.rate.setSize(atlas.findRegion("rate").getRegionWidth(), atlas.findRegion("rate").getRegionHeight());

			this.share = new Sprite(atlas.findRegion("share"));
			this.share.setSize(atlas.findRegion("share").getRegionWidth(), atlas.findRegion("share").getRegionHeight());
		}
		this.layoutButtons();
		if(GameAssets.fetchMusic("menu/music/tribute.mp3").isPlaying() == false && Configuration.musicEnabled == true) {
			GameAssets.fetchMusic("menu/music/tribute.mp3").stop();
			GameAssets.playMusic(GameAssets.fetchMusic("menu/music/tribute.mp3"), true, 0.5f);
		}
	}
	
	
	public void shareButtonPressed() {		
		GameAssets.nativ.share("Dracoo","I am currently playing <a href=http://facebook.com/dracoo>Dracoo</a> for Android. Love this game! Cannot stop playing it.");
	}
	
	public void rateButtonPressed() {
		GameAssets.nativ.rate();
	}
	
	public void moreButtonPressed() {
		GameAssets.nativ.more();
	}

	
	private void touchedEvent(Vector2 touchPoint) {
		Gdx.app.log("Progress", "touched event");
		if(playButton != null && playButton.getBoundingRectangle().contains(touchPoint.x, touchPoint.y)) {
			this.dispose();
			GameAssets.playSound(GameAssets.fetchSound("menu/sounds/click.mp3"));
			ChooseLevelpackScreen cls = new ChooseLevelpackScreen(this.game, 0);
			this.game.setScreen(cls);
		}
		
		if(settings != null && settings.getBoundingRectangle().contains(touchPoint.x, touchPoint.y)) {
			this.dispose();
			GameAssets.playSound(GameAssets.fetchSound("menu/sounds/click.mp3"));
			this.game.setScreen(new SettingsScreen(this.game));
		}
	
		if(share != null && share.getBoundingRectangle().contains(touchPoint.x, touchPoint.y)) {
			GameAssets.playSound(GameAssets.fetchSound("menu/sounds/click.mp3"));
			this.shareButtonPressed();
		}
		if(rate != null && rate.getBoundingRectangle().contains(touchPoint.x, touchPoint.y)) {
			GameAssets.playSound(GameAssets.fetchSound("menu/sounds/click.mp3"));
			this.rateButtonPressed();
		}
		if(more != null && more.getBoundingRectangle().contains(touchPoint.x, touchPoint.y)) {
			GameAssets.playSound(GameAssets.fetchSound("menu/sounds/click.mp3"));
			this.moreButtonPressed();
		}
	}





	@Override
	public void stepBack(String source) {
		if(GameAssets.buttonTimer <= 0f) {
			this.endApp = true;			
			Gdx.app.exit();
			GameAssets.buttonTimer = 0.35f;
		}
	}


	@Override
	public void startPress() {
		if(GameAssets.buttonTimer <= 0f) {
			if(this.disposed == false && this.assetsLoaded == true) {
				this.dispose();
				GameAssets.playSound(GameAssets.fetchSound("menu/sounds/click.mp3"));
				ChooseLevelpackScreen cls = new ChooseLevelpackScreen(this.game, 0);
				this.game.setScreen(cls);
			}
			GameAssets.buttonTimer = 0.35f;
		}
	}


	@Override
	public void primaryPress() {
		if(GameAssets.buttonTimer <= 0f) {
			if(this.disposed == false && this.assetsLoaded == true) {
				this.dispose();
				GameAssets.playSound(GameAssets.fetchSound("menu/sounds/click.mp3"));
				ChooseLevelpackScreen cls = new ChooseLevelpackScreen(this.game, 0);
				this.game.setScreen(cls);
			}
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
		Gdx.app.log("Progress", "touched");
		this.touchedEvent(this.unprojectUi(x, y));
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
