package com.xsheetgames.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;


public abstract class AbstractScreen implements Screen {
	protected Game game;
	
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