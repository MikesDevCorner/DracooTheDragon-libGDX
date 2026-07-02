package com.xsheetgames;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;

public class Configuration {

	//THE CONFIGURATION GOES HERE
	// Fixed design reference (16:10). Used by the UI screens and as the
	// pixel<->meter ratio for sizing all sprites (this ratio never changes).
	public static float VIEWPORT_WIDTH;   //20 Meter
	public static float VIEWPORT_HEIGHT;  //12.5 Meter
	public static int TARGET_WIDTH;
	public static int TARGET_HEIGHT;

	// Actual game world extent. The height is fixed (full screen height) while
	// the width follows the device aspect ratio so nothing gets stretched and
	// the available width is used. GAME_WORLD_* is in meters (box2d/physics),
	// GAME_PIXEL_* is in pixels (HUD rendering).
	public static float GAME_WORLD_WIDTH;
	public static int GAME_PIXEL_WIDTH;

	// How far past the live right world edge enemies/obstacles/random chilis
	// spawn (meters), so they scroll in from just off-screen on every aspect ratio.
	public static final float SPAWN_MARGIN = 5f;
	public static boolean soundEnabled;
	public static boolean musicEnabled;
	public static boolean vibrateEnabled;
	public static int inputType;
	public static boolean autoFire, altAutoFire;
	public static boolean FogActive;


	/************INTERN VERSION INFO*****************************/
	public static final String VERSION = "27.0";
	public static final String VERSION_DATE = "02.07.2026";
	/************************************************************/


	public static boolean useIntroScreen = false;
	public static boolean useOutroScreen = true;

	public static boolean poolingInfos, contactInfos, spawnInfos, itemsQueryCallbackInfos;
	public static int debugLevel;


	public static String moreUrl;
	public static String rateTarget;
	public static String shareTarget;


	public static void load() {

		/*****INITIAL SETTINGS CONFIGURATION****************/
		soundEnabled = true;
		musicEnabled = true;
		vibrateEnabled = true;
		inputType = 1;
		autoFire = false;
		altAutoFire = true;
		/***************************************************/


		/*****DEBUG CONFIGURATION****************************/
		debugLevel = Application.LOG_ERROR;
		//debugLevel = Application.LOG_DEBUG;
		//debugLevel = Application.LOG_NONE;
		//debugLevel = Application.LOG_INFO;
		poolingInfos = false;
		contactInfos = false;
		spawnInfos = false;
		itemsQueryCallbackInfos = false;
		/***************************************************/


		VIEWPORT_HEIGHT = 12.5f;
		VIEWPORT_WIDTH = 20f;
		TARGET_HEIGHT = 800;
		TARGET_WIDTH = 1280;
		updateGameViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		FogActive = (Gdx.graphics.getWidth() < 800) ? false : true;

		// Single, fully unlocked, ad-free edition on Google Play.
		rateTarget = "market://details?id=";
		shareTarget = "https://play.google.com/store/apps/details?id=";
		moreUrl = "";
	}


	/**
	 * Recomputes the game world width from the current screen aspect ratio.
	 * The world/HUD height stays fixed (full screen height) and the width is
	 * stretched out to match the device aspect ratio, so the camera maps 1:1
	 * to the screen and no asset gets distorted in any direction.
	 */
	public static void updateGameViewport(int screenWidth, int screenHeight) {
		float aspect = (screenHeight > 0) ? (float) screenWidth / (float) screenHeight : (float) TARGET_WIDTH / (float) TARGET_HEIGHT;
		GAME_WORLD_WIDTH = VIEWPORT_HEIGHT * aspect;
		GAME_PIXEL_WIDTH = Math.round(TARGET_HEIGHT * aspect);
	}


	public static void changeDebugMode() {
		if(debugLevel == Application.LOG_DEBUG) {
			debugLevel = Application.LOG_ERROR;
			Gdx.app.setLogLevel(Configuration.debugLevel);
		} else {
			debugLevel=Application.LOG_DEBUG;
			Gdx.app.setLogLevel(Configuration.debugLevel);
		}
	}
}
