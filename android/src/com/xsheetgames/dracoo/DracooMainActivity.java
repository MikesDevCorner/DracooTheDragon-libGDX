package com.xsheetgames.dracoo;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.xsheetgames.Configuration;
import com.xsheetgames.DracooGdxGame;
import com.xsheetgames.iNativeFunctions;

/**
 * Android-Launcher fuer Dracoo the Dragon.
 *
 * Schlanke, werbefreie Variante: keine Chartboost-Ads, keine Google Analytics,
 * kein MOGA-SDK. Gamepad-Unterstuetzung laeuft komplett ueber gdx-controllers
 * (im Core via {@code com.xsheetgames.InputManager}).
 */
public class DracooMainActivity extends AndroidApplication implements iNativeFunctions
{
	private DracooMainActivity me = this;
	private DracooGdxGame theGame;
	private RelativeLayout layout;

	private static final int SHOW_MESSAGE = 3;

	/**ACTIVITY LIFECYCLE*****************************************************************/

	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.layout = new RelativeLayout(this);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);

		AndroidApplicationConfiguration cfg = new AndroidApplicationConfiguration();
		cfg.useAccelerometer = true;
		cfg.useCompass = false;
		cfg.useWakelock = true;

		this.theGame = new DracooGdxGame(this);
		View gameView = initializeForView(this.theGame, cfg);
		layout.addView(gameView);

		setContentView(layout);
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if (hasFocus) {
			hideSystemUI();
		}
	}

	private void hideSystemUI() {
		View decorView = getWindow().getDecorView();
		decorView.setSystemUiVisibility(
				View.SYSTEM_UI_FLAG_IMMERSIVE
						| View.SYSTEM_UI_FLAG_LAYOUT_STABLE
						| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
						| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
						| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
						| View.SYSTEM_UI_FLAG_FULLSCREEN);
	}

	/****NATIVE FUNCTIONS*****************************************************************/

	@Override
	public void openURL(String url) {
		if (url == null || url.isEmpty()) return;
		Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
		startActivity(browserIntent);
	}

	@Override
	public void share(String subject, String text) {
		Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
		sharingIntent.setType("text/plain");
		sharingIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
		sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, Configuration.shareTarget + this.getApplicationContext().getPackageName());
		startActivity(Intent.createChooser(sharingIntent, "Share via"));
	}

	@Override
	public void rate() {
		Uri uri = Uri.parse(Configuration.rateTarget + this.getApplicationContext().getPackageName());
		Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
		try {
			startActivity(goToMarket);
		} catch (ActivityNotFoundException e) {
			try {
				Toast.makeText(this.getApplicationContext(), "Couldn't launch the targeted store", Toast.LENGTH_LONG).show();
			} catch(Exception noHandling) { }
		}
	}

	@Override
	public void more() {
		if (Configuration.moreUrl == null || Configuration.moreUrl.isEmpty()) return;
		Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Configuration.moreUrl));
		startActivity(browserIntent);
	}

	@Override
	public void showMessage(String title, String message) {
		try {
			Message msg = Message.obtain();
			msg.what = SHOW_MESSAGE;
			msg.obj = title + "###" + message;
			handler.sendMessage(msg);
		} catch(Exception e) { }
	}

	/**HANDLER FUER UI-AKTIONEN AUF DEM HAUPT-THREAD************************************/

	@SuppressLint("HandlerLeak")
	protected Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == SHOW_MESSAGE) {
				try {
					AlertDialog.Builder builder = new AlertDialog.Builder(me);
					String msgTitle = ((String) msg.obj).split("###")[0];
					String msgMessage = ((String) msg.obj).split("###")[1];
					builder.setTitle(msgTitle)
							.setMessage(msgMessage)
							.setCancelable(false)
							.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int id) {
									dialog.cancel();
								}
							});
					AlertDialog alert = builder.create();
					alert.show();
				} catch(Exception e) { Gdx.app.log("Show Message","Failed to show Message"); }
			}
		}
	};
}
