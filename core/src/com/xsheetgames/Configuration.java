package com.xsheetgames;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;

public class Configuration {

	//THE CONFIGURATION GOES HERE
	public static float VIEWPORT_WIDTH;   //20 Meter
	public static float VIEWPORT_HEIGHT;  //12.5 Meter
	public static int TARGET_WIDTH;
	public static int TARGET_HEIGHT;
	public static boolean soundEnabled;
	public static boolean musicEnabled;
	public static boolean vibrateEnabled;
	public static int inputType;
	public static boolean autoFire, altAutoFire;
	public static boolean FogActive;


	/************INTERN VERSION INFO*****************************/
	public static final String VERSION = "26.0";
	public static final String VERSION_DATE = "20.06.2026";
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
		FogActive = (Gdx.graphics.getWidth() < 800) ? false : true;

		// Single, fully unlocked, ad-free edition on Google Play.
		rateTarget = "market://details?id=";
		shareTarget = "https://play.google.com/store/apps/details?id=";
		moreUrl = "";
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
