package com.xsheetgames.desktop;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Input.Keys;
import com.xsheetgames.GameAssets;
import com.xsheetgames.IControllerUtils;
import com.xsheetgames.screens.AbstractScreen;


public class ControllerUtils implements InputProcessor, IControllerUtils {

	InputMultiplexer multiplexer = null;
	Game game = null;
	
	
	public void initializeControllers(Object o) {
		// KEYBOARD INIT
	    Gdx.input.setCatchBackKey(true);
	    Gdx.input.setCatchMenuKey(true);
		multiplexer = new InputMultiplexer();
	    multiplexer.addProcessor(this);
	    Gdx.input.setInputProcessor(multiplexer);
	}
	
/****** CONTROLLER ACTIONS *************************************************************************/
	
	
	
	/******* CONTROLLER POLLING *******************************/
	
	
	public boolean pollControllerButtonState(int keycode) {
		
		//POLL KEYBOARD CONTROLLS
		if(keycode == GameAssets.KEY_PRIMARY) {
			return Gdx.input.isKeyPressed(Keys.SPACE);
		}
		if(keycode == GameAssets.KEY_SECONDARY) {
			return Gdx.input.isKeyPressed(Keys.ENTER);
		}
		if(keycode == GameAssets.KEY_START) {
			return Gdx.input.isKeyPressed(Keys.P);
		}
		if(keycode == GameAssets.KEY_UP) {
			return Gdx.input.isKeyPressed(Keys.UP);
		}
		if(keycode == GameAssets.KEY_DOWN) {
			return Gdx.input.isKeyPressed(Keys.DOWN);
		}
		if(keycode == GameAssets.KEY_LEFT) {
			return Gdx.input.isKeyPressed(Keys.LEFT);
		}
		if(keycode == GameAssets.KEY_RIGHT) {
			return Gdx.input.isKeyPressed(Keys.RIGHT);
		}
		
		
		return false;		
	}

	
	public float pollControllerAxis(int axis) {		
		return 0.0f;
	}
	
		
	
	public String getInputDevice() {
		return "moga_pocket";
	}
	
	
	/****** INPUT PROCESSOR HANDLERS ********************************/
	@Override
	public boolean keyDown(int keycode) {
		AbstractScreen screen = (AbstractScreen) DesktopLauncher.game.getScreen();
		
		//PRIMARY KEY
		if(keycode == Keys.SPACE) {
			screen.primaryPress();
			return true;
		}
		
		
		//START KEY
		if(keycode == Keys.P) {
			screen.startPress();
			return true;
		}
		
		//BACK KEY
		if(keycode == Keys.ESCAPE) {
			screen.stepBack("keyboard");
			return true;
		}
		
		//UP KEY
		if(keycode == Keys.UP) {
			screen.steerYAxis(1.0f);
			return true;
		}
		
		//DOWN KEY
		if(keycode == Keys.DOWN) {
			screen.steerYAxis(-1.0f);
			return true;
		}
		
		//LEFT KEY
		if(keycode == Keys.LEFT) {
			screen.steerXAxis(-1.0f);
			return true;
		}
		
		//RIGHT KEY
		if(keycode == Keys.RIGHT) {
			screen.steerXAxis(1.0f);
			return true;
		}
		return false;
	}



	@Override
	public boolean keyUp(int keycode) {
		return false;
	}

	
	
	@Override
	public boolean touchDown(int x, int y, int pointer, int button) {
		AbstractScreen screen = (AbstractScreen) DesktopLauncher.game.getScreen();
		return screen.screenTouched(x, y, pointer);
	}
	



	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		AbstractScreen screen = (AbstractScreen) DesktopLauncher.game.getScreen();
		return screen.screenAfterTouched(screenX, screenY, pointer);
	}	


	@Override
	public boolean keyTyped(char character) {
		return false;
	}



	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		AbstractScreen screen = (AbstractScreen) DesktopLauncher.game.getScreen();
		return screen.screenWhileTouch(screenX, screenY, pointer);
	}



	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}



	@Override
	public boolean scrolled(int amount) {
		return false;
	}
}
