package com.xsheetgames.dracoo;

import android.os.Handler;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.controllers.mappings.Ouya;
import com.badlogic.gdx.math.Vector3;
import com.bda.controller.Controller;
import com.bda.controller.ControllerListener;
import com.bda.controller.KeyEvent;
import com.bda.controller.MotionEvent;
import com.bda.controller.StateEvent;
import com.xsheetgames.GameAssets;
import com.xsheetgames.IControllerUtils;
import com.xsheetgames.screens.AbstractScreen;


public class ControllerUtils  implements InputProcessor, ControllerListener, com.badlogic.gdx.controllers.ControllerListener, IControllerUtils {

	Controller mController = null;
	InputMultiplexer multiplexer = null;
	Game mGame = null;
	Handler handler = null;
	
	public ControllerUtils(Game game, Handler handler) {
		this.mGame = game;
		this.handler = handler;
	}
	
	public void initializeControllers(Object o) {
		AndroidApplication context = (AndroidApplication) o;
		// MOGA CONTROLLER INIT
        //mController = Controller.getInstance(context);
        //mController.init();
        //mController.setListener(this, ((AndroidApplication)Gdx.app).handler); //AUSKOMMENTIERT WEIL HANDLER EMULIERT WERDEN IN SCREENS
	    
	    // LIBGDX CONTROLLER INIT
        Controllers.addListener(this);
        
        // KEYBOARD INIT	    
		multiplexer = new InputMultiplexer();
	    multiplexer.addProcessor(this);	
	    Gdx.input.setInputProcessor(multiplexer);
	    Gdx.input.setCatchBackKey(true);
	    Gdx.input.setCatchMenuKey(true);
	}
	
	public void destroy() {
		if(mController != null)
		{
			mController.exit();
		}
	}
	
	public void pause() {
		if(mController != null)
		{
			mController.onPause();
		}
	}
	
	public void resume() {
		if(multiplexer != null) 
		{
			Gdx.input.setInputProcessor(this.multiplexer);
		    Gdx.input.setCatchBackKey(true);
		} else {
			multiplexer = new InputMultiplexer();
		    multiplexer.addProcessor(this);	
		    Gdx.input.setInputProcessor(multiplexer);
		    Gdx.input.setCatchBackKey(true);
		}
		if(mController != null)
		{
			mController.onResume();
		}
	}
	
	

	
	/****** CONTROLLER ACTIONS *************************************************************************/
	
	
	
	/******* CONTROLLER POLLING *******************************/
	
	public boolean pollControllerButtonState(int keycode) {
		
		//POLL MOGA CONTROLS
		if(this.mController != null) {
			if(mController.getState(Controller.STATE_CONNECTION) == Controller.ACTION_CONNECTED) {
				if(this.mController.getState(Controller.STATE_CURRENT_PRODUCT_VERSION) == Controller.ACTION_VERSION_MOGA || this.mController.getState(Controller.STATE_CURRENT_PRODUCT_VERSION) == Controller.ACTION_VERSION_MOGAPRO)
				{
					if(keycode == GameAssets.KEY_PRIMARY) {
						if(this.mController.getKeyCode(Controller.KEYCODE_BUTTON_A) == Controller.ACTION_DOWN || this.mController.getKeyCode(Controller.KEYCODE_BUTTON_R1) == Controller.ACTION_DOWN) return true;
						else return false;
					}
					if(keycode == GameAssets.KEY_SECONDARY) {
						if(this.mController.getKeyCode(Controller.KEYCODE_BUTTON_X) == Controller.ACTION_DOWN || this.mController.getKeyCode(Controller.KEYCODE_BUTTON_L1) == Controller.ACTION_DOWN) return true;
						else return false;
					}
					if(keycode == GameAssets.KEY_START) {
						if(this.mController.getKeyCode(Controller.KEYCODE_BUTTON_START) == Controller.ACTION_DOWN) return true;
						else return false;
					}
					if(keycode == GameAssets.KEY_UP) {
						if(this.mController.getKeyCode(Controller.KEYCODE_DPAD_UP) == Controller.ACTION_DOWN) return true;
						else return false;
					}
					if(keycode == GameAssets.KEY_DOWN) {
						if(this.mController.getKeyCode(Controller.KEYCODE_DPAD_DOWN) == Controller.ACTION_DOWN) return true;
						else return false;
					}
					if(keycode == GameAssets.KEY_LEFT) {
						if(this.mController.getKeyCode(Controller.KEYCODE_DPAD_LEFT) == Controller.ACTION_DOWN) return true;
						else return false;
					}
					if(keycode == GameAssets.KEY_RIGHT) {
						if(this.mController.getKeyCode(Controller.KEYCODE_DPAD_RIGHT) == Controller.ACTION_DOWN) return true;
						else return false;
					}
					if(keycode == GameAssets.KEY_BACK) {
						if(this.mController.getKeyCode(Controller.KEYCODE_BUTTON_B) == Controller.ACTION_DOWN) return true;
						else return false;
					}
				}
			}
		}
		
		
		//POLL OUYA AND HID CONTROLLS
		for(com.badlogic.gdx.controllers.Controller controller: Controllers.getControllers()) {			
			if(controller.getName().equals(Ouya.ID)) {	
				//OUYA
				if(keycode == GameAssets.KEY_PRIMARY) {
					return (controller.getButton(Ouya.BUTTON_O) || controller.getButton(Ouya.BUTTON_R1));
				}
				if(keycode == GameAssets.KEY_SECONDARY) {
					return (controller.getButton(Ouya.BUTTON_U) || controller.getButton(Ouya.BUTTON_L1));
				}
				if(keycode == GameAssets.KEY_BACK) {
					return controller.getButton(Ouya.BUTTON_A);
				}
				if(keycode == GameAssets.KEY_START) {
					return controller.getButton(Ouya.BUTTON_MENU);
				}
				if(keycode == GameAssets.KEY_UP) {
					return controller.getButton(Ouya.BUTTON_DPAD_UP);
				}
				if(keycode == GameAssets.KEY_DOWN) {
					return controller.getButton(Ouya.BUTTON_DPAD_DOWN);
				}
				if(keycode == GameAssets.KEY_LEFT) {
					return controller.getButton(Ouya.BUTTON_DPAD_LEFT);
				}
				if(keycode == GameAssets.KEY_RIGHT) {
					return controller.getButton(Ouya.BUTTON_DPAD_RIGHT);
				}				
			} else {
				//HID
				if(keycode == GameAssets.KEY_PRIMARY) {
					return (controller.getButton(Controller.KEYCODE_BUTTON_A) || controller.getButton(Controller.KEYCODE_BUTTON_R1));
				}
				if(keycode == GameAssets.KEY_SECONDARY) {
					return (controller.getButton(Controller.KEYCODE_BUTTON_X) || controller.getButton(Controller.KEYCODE_BUTTON_L1));
				}
				if(keycode == GameAssets.KEY_BACK) {
					return controller.getButton(Controller.KEYCODE_BUTTON_B);
				}
				if(keycode == GameAssets.KEY_START) {
					return controller.getButton(Controller.KEYCODE_BUTTON_START);
				}
				if(keycode == GameAssets.KEY_UP) {
					return controller.getButton(Controller.KEYCODE_DPAD_UP);
				}
				if(keycode == GameAssets.KEY_DOWN) {
					return controller.getButton(Controller.KEYCODE_DPAD_DOWN);
				}
				if(keycode == GameAssets.KEY_LEFT) {
					return controller.getButton(Controller.KEYCODE_DPAD_LEFT);
				}
				if(keycode == GameAssets.KEY_RIGHT) {
					return controller.getButton(Controller.KEYCODE_DPAD_RIGHT);
				}
			}
		}		
		
		
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
		
		//MOGA AXIS
		if(this.mController != null) {
			if(mController.getState(Controller.STATE_CONNECTION) == Controller.ACTION_CONNECTED) {
				if(this.mController.getState(Controller.STATE_CURRENT_PRODUCT_VERSION) == Controller.ACTION_VERSION_MOGA || this.mController.getState(Controller.STATE_CURRENT_PRODUCT_VERSION) == Controller.ACTION_VERSION_MOGAPRO)
				{
					if(axis == GameAssets.AXIS_X) {
						float retVal = this.mController.getAxisValue(Controller.AXIS_X); 
						if(Math.abs(retVal) > 0.07f) return retVal;
						else return 0.0f;
					}
					if(axis == GameAssets.AXIS_Y) {
						float retVal = this.mController.getAxisValue(Controller.AXIS_Y); 
						if(Math.abs(retVal) > 0.07f) return retVal;
						else return 0.0f;
					}
				}
			}
		}
		
		
		//OUYA UND HID AXIS
		for(com.badlogic.gdx.controllers.Controller controller: Controllers.getControllers()) {			
			if(controller.getName().equals(Ouya.ID)) {	
				//OUYA
				if(axis == GameAssets.AXIS_X) {
					float retVal = controller.getAxis(Ouya.AXIS_LEFT_X); 
					if(Math.abs(retVal) > 0.05f) return retVal;
					else return 0.0f;
				}
				if(axis == GameAssets.AXIS_Y) {
					float retVal = controller.getAxis(Ouya.AXIS_LEFT_Y); 
					if(Math.abs(retVal) > 0.05f) return retVal;
					else return 0.0f;
				}
			} else {
				//HID
				if(axis == GameAssets.AXIS_X) {
					float retVal = controller.getAxis(Controller.AXIS_X); 
					if(Math.abs(retVal) > 0.05f) return retVal;
					else retVal = 0.0f;
					
					if(retVal == 0.0f) {
						retVal = controller.getAxis(6); //HID DPAD XAXIS 
						if(Math.abs(retVal) > 0.05f) return retVal;
						else return 0.0f;
					}
					
				}
				if(axis == GameAssets.AXIS_Y) {
					float retVal = controller.getAxis(Controller.AXIS_Y); 
					if(Math.abs(retVal) > 0.05f) return retVal;
					else retVal = 0.0f;
					
					if(retVal == 0.0f) {
						retVal = controller.getAxis(7); //HID DPAD YAXIS 
						if(Math.abs(retVal) > 0.05f) return retVal;
						else return 0.0f;
					}
				}
			}
		}
		
		//NEIGUNG DES DEVICES
		/*if(axis == GameAssets.AXIS_X) {
			float retVal = Gdx.input.getAccelerometerY() / 10.0f; 
			if(Math.abs(retVal) > 0.05f) return retVal;
			else return 0.0f;
		}
		if(axis == GameAssets.AXIS_Y) {
			float retVal = Gdx.input.getAccelerometerZ();
			if(Math.abs(retVal) > 0.05f) return retVal;
			else return 0.0f;
		}*/
		
		return 0.0f;
	}
	
		
	
	
	
	
	/****** INPUT PROCESSOR HANDLERS ********************************/
	
	/*************** KEYBOARD HANDLERS *******************/
	@Override
	public boolean keyDown(int keycode) {
		AbstractScreen screen = (AbstractScreen) this.mGame.getScreen();
		
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
		
		//Escape KEY
		if(keycode == Keys.ESCAPE) {
			screen.stepBack("keyboard");
			return true;
		}
		
		//BACK KEY (Handy)
		if(keycode == Keys.BACK) {
			screen.stepBack("touch");
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
		AbstractScreen screen = (AbstractScreen) this.mGame.getScreen();
		return screen.screenTouched(x, y, pointer);
	}
	



	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		AbstractScreen screen = (AbstractScreen) this.mGame.getScreen();
		return screen.screenAfterTouched(screenX, screenY, pointer);
	}	


	@Override
	public boolean keyTyped(char character) {
		return false;
	}



	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		AbstractScreen screen = (AbstractScreen) this.mGame.getScreen();
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


	
	
	/******** LIBGDX CONTROLLER HANDLERS (OUYA, HID, ...) **********/
	
	
	@Override
	public boolean buttonDown(com.badlogic.gdx.controllers.Controller controller, int keycode) {
		
		AbstractScreen screen = (AbstractScreen) this.mGame.getScreen();
		if(controller.getName().equals(Ouya.ID)) {			
			if(keycode == Ouya.BUTTON_O) {
				screen.primaryPress();
				return true;
			}
			if(keycode == Ouya.BUTTON_U) {
				screen.primaryPress();
				return true;
			}
			if(keycode == Ouya.BUTTON_A) {
				screen.stepBack("controller");
				return true;
			}
			if(keycode == Ouya.BUTTON_MENU) {
				screen.startPress();
				return true;
			}
			if(keycode == Ouya.BUTTON_L1) {
				screen.primaryPress();
				return true;
			}
			if(keycode == Ouya.BUTTON_R1) {
				screen.primaryPress();
				return true;
			}
			if(keycode == Ouya.BUTTON_DPAD_UP) {
				screen.steerYAxis(1.0f);
				return true;
			}
			if(keycode == Ouya.BUTTON_DPAD_DOWN) {
				screen.steerYAxis(-1.0f);
				return true;
			}
			if(keycode == Ouya.BUTTON_DPAD_LEFT) {
				screen.steerXAxis(-1.0f);
				return true;
			}
			if(keycode == Ouya.BUTTON_DPAD_RIGHT) {
				screen.steerXAxis(1.0f);
				return true;
			}
		} else {
			if(keycode == Controller.KEYCODE_BUTTON_A) {
				screen.primaryPress();
				return true;
			}
			if(keycode == Controller.KEYCODE_BUTTON_X) {
				screen.primaryPress();
				return true;
			}
			if(keycode == Controller.KEYCODE_BUTTON_B) {
				screen.stepBack("controller");
				return true;
			}
			if(keycode == Controller.KEYCODE_BUTTON_START) {
				screen.startPress();
				return true;
			}
			if(keycode == Controller.KEYCODE_BUTTON_L1) {
				screen.primaryPress();
				return true;
			}
			if(keycode == Controller.KEYCODE_BUTTON_R1) {
				screen.primaryPress();
				return true;
			}
			if(keycode == Controller.KEYCODE_DPAD_UP) {
				screen.steerYAxis(1.0f);
				return true;
			}
			if(keycode == Controller.KEYCODE_DPAD_DOWN) {
				screen.steerYAxis(-1.0f);
				return true;
			}
			if(keycode == Controller.KEYCODE_DPAD_LEFT) {
				screen.steerXAxis(-1.0f);
				return true;
			}
			if(keycode == Controller.KEYCODE_DPAD_RIGHT) {
				screen.steerXAxis(1.0f);
				return true;
			}
		}
		return false;
	}
	
	
	@Override
	public boolean axisMoved(com.badlogic.gdx.controllers.Controller controller, int axis, float peculiarity) {
		
		AbstractScreen screen = (AbstractScreen) this.mGame.getScreen();
		if(controller.getName().equals(Ouya.ID)) {
			if(axis == Ouya.AXIS_LEFT_X) {
				screen.steerXAxis(peculiarity);
				return true;
			}
			if(axis == Ouya.AXIS_LEFT_Y) {
				screen.steerYAxis(peculiarity);
				return true;
			}
		} else {
			if(axis == com.bda.controller.Controller.AXIS_X || axis == 6) {
				screen.steerXAxis(peculiarity);
				return true;
			}
			if(axis == com.bda.controller.Controller.AXIS_Y || axis == 7) {
				screen.steerYAxis(peculiarity);
				return true;
			}
		}		
		return false;
	}
	
	@Override
	public boolean buttonUp(com.badlogic.gdx.controllers.Controller arg0,
			int arg1) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public boolean accelerometerMoved(
			com.badlogic.gdx.controllers.Controller arg0, int arg1, Vector3 arg2) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void connected(com.badlogic.gdx.controllers.Controller arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void disconnected(com.badlogic.gdx.controllers.Controller arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean povMoved(com.badlogic.gdx.controllers.Controller arg0,
			int arg1, PovDirection arg2) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean xSliderMoved(com.badlogic.gdx.controllers.Controller arg0,
			int arg1, boolean arg2) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean ySliderMoved(com.badlogic.gdx.controllers.Controller arg0,
			int arg1, boolean arg2) {
		// TODO Auto-generated method stub
		return false;
	}

	
	
	/******** MOGA CONTROLLER HANDLERS **********/
	//MOGA CONTROLLR HANDLER EMULATED IN SCREENS BECAUSE OF THREAD TROUBLES
	
	@Override
	public void onKeyEvent(KeyEvent event) {		
		/*if(this.mController != null) {
			if(mController.getState(Controller.STATE_CONNECTION) == Controller.ACTION_CONNECTED) {
				if(this.mController.getState(Controller.STATE_CURRENT_PRODUCT_VERSION) == Controller.ACTION_VERSION_MOGA || this.mController.getState(Controller.STATE_CURRENT_PRODUCT_VERSION) == Controller.ACTION_VERSION_MOGAPRO) 
				{
					//AbstractScreen screen = (AbstractScreen) this.mGame.getScreen();
					switch(event.getKeyCode())
					{
						case KeyEvent.KEYCODE_BUTTON_A:
							if(event.getAction() == KeyEvent.ACTION_DOWN)
							{
								//screen.primaryPress();
							}
							else { }
						break;
						case KeyEvent.KEYCODE_BUTTON_X:
							if(event.getAction() == KeyEvent.ACTION_DOWN)
							{
								//screen.primaryPress();
							}
							else { }
						break;
						case KeyEvent.KEYCODE_BUTTON_START:
							if(event.getAction() == KeyEvent.ACTION_DOWN)
							{
								//screen.startPress();
							}
							else { }
						break;
						case KeyEvent.KEYCODE_BUTTON_B:
							if(event.getAction() == KeyEvent.ACTION_DOWN)
							{
								//screen.stepBack("controller");
							}
							else { }
						break;
						case KeyEvent.KEYCODE_BUTTON_L1:
							if(event.getAction() == KeyEvent.ACTION_DOWN)
							{
								//screen.primaryPress();
							}
							else { }
						break;
						case KeyEvent.KEYCODE_BUTTON_R1:
							if(event.getAction() == KeyEvent.ACTION_DOWN)
							{
								//screen.primaryPress();
							}
							else { }
						break;
						case KeyEvent.KEYCODE_DPAD_UP:
							if(event.getAction() == KeyEvent.ACTION_DOWN)
							{
								//screen.steerYAxis(1.0f);
							}
							else { }
						break;
						case KeyEvent.KEYCODE_DPAD_DOWN:
							if(event.getAction() == KeyEvent.ACTION_DOWN)
							{
								//screen.steerYAxis(-1.0f);
							}
							else { }
						break;
						case KeyEvent.KEYCODE_DPAD_LEFT:
							if(event.getAction() == KeyEvent.ACTION_DOWN)
							{
								//screen.steerXAxis(-1.0f);
							}
							else { }
						break;
						case KeyEvent.KEYCODE_DPAD_RIGHT:
							if(event.getAction() == KeyEvent.ACTION_DOWN)
							{
								//screen.steerXAxis(1.0f);
							}
							else { }
						break;
					}
				}
			}
		}*/
	}

	@Override
	public void onMotionEvent(MotionEvent event) {
		/*if(this.mController != null) {
			if(mController.getState(Controller.STATE_CONNECTION) == Controller.ACTION_CONNECTED) {
				if(this.mController.getState(Controller.STATE_CURRENT_PRODUCT_VERSION) == Controller.ACTION_VERSION_MOGA || this.mController.getState(Controller.STATE_CURRENT_PRODUCT_VERSION) == Controller.ACTION_VERSION_MOGAPRO) 
				{
					AbstractScreen screen = (AbstractScreen) this.mGame.getScreen();
					screen.steerXAxis(event.getAxisValue(MotionEvent.AXIS_X));
					screen.steerYAxis(event.getAxisValue(MotionEvent.AXIS_Y));	
				}
			}
		}*/	
	}

	@Override
	public void onStateEvent(StateEvent event) {
		/*switch(event.getState())
		{
			case StateEvent.STATE_CONNECTION:
			switch(event.getAction())
			{
				case StateEvent.ACTION_DISCONNECTED:
				//disconnected from controller
				GameAssets.nativ.showMessage("Controller", "Moga Controller disconnected");
				break;
				case StateEvent.ACTION_CONNECTED:
				// connected to controller
				GameAssets.nativ.showMessage("Controller", "Moga Controller connected");
				break;
			}
			break;
		}*/
	}
	
	
	public boolean isMogaControllerConnected() {
		if(this.mController != null) return mController.getState(Controller.STATE_CONNECTION) == Controller.ACTION_CONNECTED;
		else return false;
	}
	
	public boolean isControllerConnected() {
		boolean returnValue = false;
		if(this.mController != null) {
			if(mController.getState(Controller.STATE_CONNECTION) == Controller.ACTION_CONNECTED) {
				if(this.mController.getState(Controller.STATE_CURRENT_PRODUCT_VERSION) == Controller.ACTION_VERSION_MOGA || this.mController.getState(Controller.STATE_CURRENT_PRODUCT_VERSION) == Controller.ACTION_VERSION_MOGAPRO) 
				{
					returnValue = true;
				}
			}
		}
		
		for(@SuppressWarnings("unused") com.badlogic.gdx.controllers.Controller c : Controllers.getControllers()) {
			returnValue = true;
		}
		
		//if(Controllers.getControllers().toArray().length > 0) returnValue = true;
		
		return returnValue;
	}
	
	public String getInputDevice() {
		String contr = "keyboard";
		if(this.mController != null) {
			if(mController.getState(Controller.STATE_CONNECTION) == Controller.ACTION_CONNECTED) {
				if(this.mController.getState(Controller.STATE_CURRENT_PRODUCT_VERSION) == Controller.ACTION_VERSION_MOGA) 
				{
					contr = "moga_pocket";
				}
				if(this.mController.getState(Controller.STATE_CURRENT_PRODUCT_VERSION) == Controller.ACTION_VERSION_MOGAPRO) 
				{
					contr = "moga_pro";
				}
			}
		}
		for(@SuppressWarnings("unused") com.badlogic.gdx.controllers.Controller c : Controllers.getControllers()) {
			contr = "ouya";
		}
		//if(Controllers.getControllers().toArray().length > 0) contr = "ouya";
		return contr;
	}
}
