package com.xsheetgames;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

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
	public static String currentAdPoint = "gamepause";
	public static final String batmineSku = "batmine";
	public static final String jungleSku = "jungle";

	public static final Calendar promotionDateStart = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
	public static final Calendar promotionDateEnd = new GregorianCalendar(TimeZone.getTimeZone("UTC"));


	/***********CONFIG FOR FULL VERSION / DEMO********************/

	public static boolean fullVersion = true;
	public static boolean useAds = true;
	public static boolean fullBatmine = true;
	public static boolean fullJungle = true;
	public static boolean purchasedPacksAdFree = false;

	public static String adPartner = "chartboost";  //chartboost, samsung
	public static String store = "play"; //play, amazon, samsung

	public static String GoogleAnalyticsTracker = "UA-31094209-6";

	/*************************************************************/



	/************INTERN VERSION INFO*****************************/
	public static final String VERSION = "1.9";
	public static final String VERSION_DATE = "23.08.2020";
	/************************************************************/


	public static boolean waitForAd = false;
	public static boolean useIntroScreen = false;
	public static boolean useOutroScreen = true;

	public static String chartboostAppKeyPlay = "5194ed6617ba474e38000001";
	public static String chartboostAppSigneturePlay = "751ca5e0eb970bcbcc074d09353562c83570715f";

	public static String chartboostAppKeyAmazon = "51981bec17ba47d870000002";
	public static String chartboostAppSignetureAmazon = "b016dcbb181f5dd70856a7de49799c482558c8f6";

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

		if(Configuration.store.equals("amazon")) {
			rateTarget = "http://www.amazon.com/gp/mas/dl/android?p=";
			shareTarget = "http://www.amazon.com/gp/mas/dl/android?p=";
		}
		if(Configuration.store.equals("play")) {
			rateTarget = "market://details?id=";
			shareTarget = "https://play.google.com/store/apps/details?id=";
		}
		if(Configuration.store.equals("samsung")) {
			rateTarget = "market://details?id=";
			shareTarget = "https://play.google.com/store/apps/details?id=";
		}


		if(Configuration.adPartner.equals("chartboost")) {
			moreUrl = "";
		}
		if(Configuration.adPartner.equals("samsung")) {
			moreUrl = "";
		}
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