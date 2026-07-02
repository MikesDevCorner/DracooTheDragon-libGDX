package com.xsheetgames.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.xsheetgames.Configuration;


public abstract class AbstractScreen implements Screen {
	protected Game game;

	// Haelt die UI-Sprites (Buttons, Fonts) im 1280x800 Design-Raster ohne
	// Verzerrung; der Hintergrund wird separat als cover dahinter gezeichnet.
	protected Viewport uiViewport;

	/*********** UI-VIEWPORT / COVER-RENDERING ************/

	protected void setupUiViewport() {
		this.uiViewport = new FitViewport(Configuration.TARGET_WIDTH, Configuration.TARGET_HEIGHT);
		this.uiViewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
	}

	protected void updateUiViewport(int width, int height) {
		if(this.uiViewport != null) this.uiViewport.update(width, height, true);
	}

	// Nimmt rohe y-down Screen-Koordinaten entgegen - Drop-in-Ersatz fuer die
	// alte Stretch-Formel. Taps in den Letterbox-Raendern landen ausserhalb
	// von 0..1280/0..800 und verfehlen damit automatisch alle Hit-Tests.
	protected Vector2 unprojectUi(int x, int y) {
		Vector2 touchPoint = new Vector2(x, y);
		this.uiViewport.unproject(touchPoint);
		return touchPoint;
	}

	// Pass 1: volle Fenstergroesse in echten Pixeln (fuer cover-Hintergruende).
	protected void beginScreenPass(SpriteBatch batch) {
		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		batch.getProjectionMatrix().setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	}

	// Pass 2: 1280x800 FitViewport fuer alle UI-Sprites und Fonts.
	protected void beginUiPass(SpriteBatch batch) {
		this.uiViewport.apply(true);
		batch.setProjectionMatrix(this.uiViewport.getCamera().combined);
	}

	// Volles GL-Viewport wiederherstellen, damit der naechste Screen nicht geclippt wird.
	protected void endScreenRender() {
		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	}

	/*********** CONTROLLER EVENTS ************/
	public abstract void stepBack(String source); //controller, touch oder keyboard
	public abstract void startPress();
	public abstract void primaryPress();
	public abstract void steerXAxis(float peculiarity);
	public abstract void steerYAxis(float peculiarity);
	public abstract boolean screenTouched(int x, int y, int pointer);
	public abstract boolean screenAfterTouched(int x, int y, int pointer);
	public abstract boolean screenWhileTouch(int x, int y, int pointer);
}