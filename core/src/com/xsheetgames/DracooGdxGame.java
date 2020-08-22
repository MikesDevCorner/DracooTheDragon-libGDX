package com.xsheetgames;

import java.util.Date;

import aurelienribon.tweenengine.Tween;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.xsheetgames.genericGameObjects.GameObject;
import com.xsheetgames.genericGameObjects.GameObjectAccessor;
import com.xsheetgames.screens.ChooseLevelScreen;
import com.xsheetgames.screens.EndPackScreen;
import com.xsheetgames.screens.MenuScreen;

public class DracooGdxGame extends Game {
	
	
	public DracooGdxGame(iNativeFunctions nativeFunctions) {
    	GameAssets.setNative(nativeFunctions);
    }
	
	
	@Override
	public void create() {
		new World(new Vector2(0f,0f), false);
		Configuration.load();
		GameAssets.load();
		Gdx.app.setLogLevel(Configuration.debugLevel);
		Tween.registerAccessor(GameObject.class, new GameObjectAccessor());
		IControllerUtils utils = GameAssets.nativ.GetControllerUtils();
		if(utils != null) {
			utils.initializeControllers(GameAssets.nativ.getMyApplicationContext());
		}
		setScreen(new MenuScreen(this));
    }
	   
	   

   @Override
   public void render() {
	   try {
	   		Gdx.gl.glClearColor(1, 1, 1, 1);
	   		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
	   		super.render();
	   } catch(Exception e) {
		   try {
			   	GameAssets.nativ.trackPageView("/Exception");
			   	String myException = "";
			   	String ExceptionName = "";
			   	for(StackTraceElement ste : e.getStackTrace()) {
			   		myException += ste.toString() + "\r\n";
			   	}
			   	if(e.getMessage() == null || e.getMessage() == "" || e.getMessage() == "null") ExceptionName = e.getClass().getName();
			   	else ExceptionName = e.getMessage();
		   		GameAssets.nativ.sendException(ExceptionName + "\r\n" + myException, true);
		   } catch( Exception ex) {
			   
		   }
		   try {
			   if(Gdx.files.isExternalStorageAvailable()) {
				   Date n = new Date();
				   String stackTrace = "";
				   String ExceptionName = "";
				   if(e.getMessage() == null || e.getMessage() == "" || e.getMessage() == "null") ExceptionName = e.getClass().getName();
				   else ExceptionName = e.getMessage();
				   for(StackTraceElement element : e.getStackTrace()) {
					   stackTrace += "\r\n"+element.toString();
				   }
				   if(GameAssets.LogFileHandle != null) GameAssets.LogFileHandle.writeString("\r\n\r\n\r\n\r\n\r\n-----------------------\r\nException occured. Time: "+n.toString() +"\r\nMessage: "+ExceptionName+"\r\nStackTrace: "+stackTrace+"\r\n-----------------------", true);
			   }
		   }
		   catch(Exception ex) {
			   
		   }
		   finally {
			   if(Configuration.debugLevel >= Application.LOG_DEBUG) {
				   Gdx.app.error("Exception occured", "OH NO, AN EXCEPTION!", e);
			   }
			   Gdx.app.exit();
		   }
	   }
   }
   
   
   public void resetToMenuScreen() {
	   if(this.getScreen() instanceof ChooseLevelScreen) {
		   ChooseLevelScreen myScreen = (ChooseLevelScreen) this.getScreen();
		   myScreen.commandToMainScreen();
	   }
	   
	   if(this.getScreen() instanceof EndPackScreen) {
		   EndPackScreen myScreen = (EndPackScreen) this.getScreen();
		   myScreen.commandToMainScreen();
	   }
   }
   
   
   public void dispose() {
	   try {
		   this.getScreen().dispose();
		   GameAssets.dispose();
		   super.dispose();
	   } catch(Exception excp) { }	   
		
   }
}