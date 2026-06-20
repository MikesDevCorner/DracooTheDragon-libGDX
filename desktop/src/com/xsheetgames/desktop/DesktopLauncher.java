package com.xsheetgames.desktop;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.xsheetgames.DracooGdxGame;
import com.xsheetgames.iNativeFunctions;

public class DesktopLauncher implements iNativeFunctions {

	public static DracooGdxGame game;

	public static void main (String[] arg) {
		Lwjgl3ApplicationConfiguration cfg = new Lwjgl3ApplicationConfiguration();

		DesktopLauncher main = new DesktopLauncher();
		cfg.setTitle("dracoo-the-dragon");
		cfg.setWindowedMode(960, 640); //1280x800 //800x480
		//cfg.useVsync(true);

		DesktopLauncher.game = new DracooGdxGame(main);

		new Lwjgl3Application(DesktopLauncher.game, cfg);
	}

	@Override
	public void openURL(String url) {
		// no-op on desktop dev build
	}

	@Override
	public void share(String subject, String text) {
		// no-op on desktop dev build
	}

	@Override
	public void rate() {
		// no-op on desktop dev build
	}

	@Override
	public void more() {
		// no-op on desktop dev build
	}

	@Override
	public void showMessage(String title, String message) {
		// no-op on desktop dev build
	}
}
