package com.xsheetgames;

import org.robovm.apple.foundation.NSAutoreleasePool;
import org.robovm.apple.uikit.UIApplication;

import com.badlogic.gdx.backends.iosrobovm.IOSApplication;
import com.badlogic.gdx.backends.iosrobovm.IOSApplicationConfiguration;
import com.xsheetgames.DracooGdxGame;

public class IOSLauncher extends IOSApplication.Delegate implements iNativeFunctions {
    @Override
    protected IOSApplication createApplication() {
        IOSApplicationConfiguration config = new IOSApplicationConfiguration();
        return new IOSApplication(new DracooGdxGame(this), config);
    }

    public static void main(String[] argv) {
        NSAutoreleasePool pool = new NSAutoreleasePool();
        UIApplication.main(argv, null, IOSLauncher.class);
        pool.close();
    }

    @Override
    public void showMessage(String title, String message) {

    }

    @Override
    public void openURL(String url) {

    }

    @Override
    public void share(String subject, String text) {

    }

    @Override
    public void rate() {

    }

    @Override
    public void more() {

    }

    @Override
    public Object getMyApplicationContext() {
        return null;
    }

    @Override
    public boolean pollControllerButtonState(int keycode) {
        return false;
    }

    @Override
    public float pollControllerAxis(int axis) {
        return 0;
    }

    @Override
    public boolean isControllerConnected() {
        return false;
    }

    @Override
    public boolean isMogaControllerConnected() {
        return false;
    }

    @Override
    public String getInputDevice() {
        return null;
    }

    @Override
    public IControllerUtils GetControllerUtils() {
        return null;
    }

    @Override
    public void initialize() {

    }

    @Override
    public void trackPageView(String path) {

    }

    @Override
    public void sendException(String description, boolean fatal) {

    }

    @Override
    public void sendEvent(String category, String subCategory, String component, long value) {

    }

    @Override
    public void showFullScreenAd(String point) {

    }

    @Override
    public void showBannerAd() {

    }

    @Override
    public void closeBannerAd() {

    }

    @Override
    public void TriggerStandingInterstitials() {

    }
}