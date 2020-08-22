package com.xsheetgames;

public interface iNativeFunctions {
	
	//Misc
	public void showMessage(String title, String message);
	public void openURL(String url);
	public void share(String subject, String text);
	public void rate();
	public void more();
	public Object getMyApplicationContext();
	
	//Controller
	public boolean pollControllerButtonState(int keycode);
	public float pollControllerAxis(int axis);
	public boolean isControllerConnected();
	public boolean isMogaControllerConnected();
	public String getInputDevice();
	public IControllerUtils GetControllerUtils();
	
	//Analytics
	void initialize();	
    void trackPageView(String path);
    
    //Exception Handling
    void sendException(String description, boolean fatal);
    void sendEvent(String category, String subCategory, String component, long value);
    
    //Ads
    void showFullScreenAd(String point);
    void showBannerAd();
    void closeBannerAd();
	void TriggerStandingInterstitials();
}