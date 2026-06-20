package com.xsheetgames;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerAdapter;
import com.badlogic.gdx.controllers.ControllerMapping;
import com.badlogic.gdx.controllers.Controllers;
import com.xsheetgames.screens.AbstractScreen;

/**
 * Zentrale, plattformuebergreifende Eingabe-Verwaltung.
 *
 * Ersetzt das alte iNativeFunctions/IControllerUtils-Polling samt MOGA-SDK.
 * Tastatur und Touch laufen ueber {@link InputProcessor}, Gamepads ueber die
 * moderne gdx-controllers-API ({@link ControllerAdapter}).
 *
 * Diskrete Aktionen (Menue-Navigation, Pause) werden als Events an den aktuellen
 * {@link AbstractScreen} weitergereicht. Kontinuierliche Steuerung (Drache) fragt
 * pro Frame {@link #pollButton(int)} / {@link #pollAxis(int)} ab.
 */
public class InputManager extends ControllerAdapter implements InputProcessor {

	private static final float AXIS_DEADZONE = 0.20f;

	private final Game game;

	public InputManager(Game game) {
		this.game = game;
	}

	/** Registriert Tastatur/Touch- und Controller-Listener. */
	public void setup() {
		Gdx.input.setInputProcessor(this);
		try {
			Gdx.input.setCatchKey(Keys.BACK, true);
			Gdx.input.setCatchKey(Keys.MENU, true);
		} catch (Exception e) { /* nicht auf jeder Plattform vorhanden */ }
		try {
			Controllers.addListener(this);
		} catch (Exception e) { /* Controller-Subsystem evtl. nicht verfuegbar */ }
	}

	private AbstractScreen currentScreen() {
		if (game != null && game.getScreen() instanceof AbstractScreen) {
			return (AbstractScreen) game.getScreen();
		}
		return null;
	}

	/** Liefert das zuletzt benutzte Gamepad oder {@code null}. */
	private Controller currentController() {
		try {
			return Controllers.getCurrent();
		} catch (Exception e) {
			return null;
		}
	}

	public boolean isControllerConnected() {
		try {
			return Controllers.getControllers().size > 0;
		} catch (Exception e) {
			return false;
		}
	}

	/* ************************ POLLING (kontinuierlich) ************************ */

	/** Tastatur ODER aktives Gamepad fuer den abstrakten Tastencode (GameAssets.KEY_*). */
	public boolean pollButton(int code) {
		if (pollKeyboard(code)) return true;
		Controller c = currentController();
		if (c == null) return false;
		ControllerMapping m = c.getMapping();
		if (m == null) return false;
		try {
			switch (code) {
				case GameAssets.KEY_UP:        return c.getButton(m.buttonDpadUp)    || c.getAxis(m.axisLeftY) < -AXIS_DEADZONE;
				case GameAssets.KEY_DOWN:      return c.getButton(m.buttonDpadDown)  || c.getAxis(m.axisLeftY) >  AXIS_DEADZONE;
				case GameAssets.KEY_LEFT:      return c.getButton(m.buttonDpadLeft)  || c.getAxis(m.axisLeftX) < -AXIS_DEADZONE;
				case GameAssets.KEY_RIGHT:     return c.getButton(m.buttonDpadRight) || c.getAxis(m.axisLeftX) >  AXIS_DEADZONE;
				case GameAssets.KEY_PRIMARY:   return c.getButton(m.buttonA) || c.getButton(m.buttonR1);
				case GameAssets.KEY_SECONDARY: return c.getButton(m.buttonX) || c.getButton(m.buttonL1);
				case GameAssets.KEY_START:     return c.getButton(m.buttonStart);
				case GameAssets.KEY_BACK:      return c.getButton(m.buttonB) || c.getButton(m.buttonBack);
				default: return false;
			}
		} catch (Exception e) {
			return false;
		}
	}

	private boolean pollKeyboard(int code) {
		switch (code) {
			case GameAssets.KEY_UP:        return Gdx.input.isKeyPressed(Keys.UP);
			case GameAssets.KEY_DOWN:      return Gdx.input.isKeyPressed(Keys.DOWN);
			case GameAssets.KEY_LEFT:      return Gdx.input.isKeyPressed(Keys.LEFT);
			case GameAssets.KEY_RIGHT:     return Gdx.input.isKeyPressed(Keys.RIGHT);
			case GameAssets.KEY_PRIMARY:   return Gdx.input.isKeyPressed(Keys.SPACE);
			case GameAssets.KEY_SECONDARY: return Gdx.input.isKeyPressed(Keys.ENTER);
			case GameAssets.KEY_START:     return Gdx.input.isKeyPressed(Keys.P);
			case GameAssets.KEY_BACK:      return Gdx.input.isKeyPressed(Keys.ESCAPE);
			default: return false;
		}
	}

	/** Achswert [-1..1] aus aktivem Gamepad oder Tastatur (AXIS_X / AXIS_Y). */
	public float pollAxis(int axis) {
		Controller c = currentController();
		if (c != null && c.getMapping() != null) {
			try {
				float v = (axis == GameAssets.AXIS_X)
						? c.getAxis(c.getMapping().axisLeftX)
						: c.getAxis(c.getMapping().axisLeftY);
				if (Math.abs(v) >= AXIS_DEADZONE) return v;
			} catch (Exception e) { /* fall through to keyboard */ }
		}
		if (axis == GameAssets.AXIS_X) {
			if (Gdx.input.isKeyPressed(Keys.LEFT)) return -1f;
			if (Gdx.input.isKeyPressed(Keys.RIGHT)) return 1f;
		} else {
			if (Gdx.input.isKeyPressed(Keys.UP)) return -1f;
			if (Gdx.input.isKeyPressed(Keys.DOWN)) return 1f;
		}
		return 0f;
	}

	/* ************************ CONTROLLER EVENTS ************************ */

	@Override
	public boolean buttonDown(Controller controller, int buttonCode) {
		AbstractScreen screen = currentScreen();
		if (screen == null || controller.getMapping() == null) return false;
		ControllerMapping m = controller.getMapping();
		if (buttonCode == m.buttonA || buttonCode == m.buttonR1) { screen.primaryPress(); return true; }
		if (buttonCode == m.buttonStart) { screen.startPress(); return true; }
		if (buttonCode == m.buttonB || buttonCode == m.buttonBack) { screen.stepBack("controller"); return true; }
		if (buttonCode == m.buttonDpadUp) { screen.steerYAxis(1f); return true; }
		if (buttonCode == m.buttonDpadDown) { screen.steerYAxis(-1f); return true; }
		if (buttonCode == m.buttonDpadLeft) { screen.steerXAxis(-1f); return true; }
		if (buttonCode == m.buttonDpadRight) { screen.steerXAxis(1f); return true; }
		return false;
	}

	/* ************************ KEYBOARD / TOUCH (InputProcessor) ************************ */

	@Override
	public boolean keyDown(int keycode) {
		AbstractScreen screen = currentScreen();
		if (screen == null) return false;
		switch (keycode) {
			case Keys.SPACE: screen.primaryPress(); return true;
			case Keys.P: screen.startPress(); return true;
			case Keys.ESCAPE:
			case Keys.BACK: screen.stepBack("keyboard"); return true;
			case Keys.UP: screen.steerYAxis(1f); return true;
			case Keys.DOWN: screen.steerYAxis(-1f); return true;
			case Keys.LEFT: screen.steerXAxis(-1f); return true;
			case Keys.RIGHT: screen.steerXAxis(1f); return true;
			default: return false;
		}
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		AbstractScreen screen = currentScreen();
		return screen != null && screen.screenTouched(screenX, screenY, pointer);
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		AbstractScreen screen = currentScreen();
		return screen != null && screen.screenAfterTouched(screenX, screenY, pointer);
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		AbstractScreen screen = currentScreen();
		return screen != null && screen.screenWhileTouch(screenX, screenY, pointer);
	}

	@Override
	public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

	@Override
	public boolean scrolled(float amountX, float amountY) {
		return false;
	}
}
