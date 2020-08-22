package com.xsheetgames.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.xsheetgames.DracooGdxGame;
import com.xsheetgames.IControllerUtils;
import com.xsheetgames.iNativeFunctions;

public class DesktopLauncher implements iNativeFunctions {
	
	public static DracooGdxGame game;
	public static ControllerUtils controllerUtils;
	
	public static void main (String[] arg) {
		
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		
		DesktopLauncher main = new DesktopLauncher();
		cfg.title = "dracoo-the-dragon";
		cfg.width = 960; //1280;  //800;
		cfg.height = 640; //800;  //480;
		//cfg.vSyncEnabled = true;
		
		DesktopLauncher.game = new DracooGdxGame(main);
		
		new LwjglApplication(DesktopLauncher.game, cfg);
		
		controllerUtils = new ControllerUtils();
		//controllerUtils.initializeControllers(); --> Aufruf jetzt im Core
	}
	
	
	@Override
	public void openURL(String url) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void share(String subject, String text) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void rate() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void showMessage(String title, String message) {
		
	}

	@Override
	public void initialize() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void trackPageView(String path) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sendException(String description, boolean fatal) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sendEvent(String category, String subCategory,
			String component, long value) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void showFullScreenAd(String adPoint) {
		// TODO Auto-generated method stub
	}


	@Override
	public void showBannerAd() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void closeBannerAd() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void more() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean pollControllerButtonState(int keycode) {
		return controllerUtils.pollControllerButtonState(keycode);
	}

	@Override
	public float pollControllerAxis(int axis) {
		return controllerUtils.pollControllerAxis(axis);
	}

	@Override
	public boolean isControllerConnected() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getInputDevice() {
		return controllerUtils.getInputDevice();
	}

	@Override
	public boolean isMogaControllerConnected() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void TriggerStandingInterstitials() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public IControllerUtils GetControllerUtils() {
		return controllerUtils;
	}


	@Override
	public Object getMyApplicationContext() {
		// TODO Auto-generated method stub
		return null;
	}
}