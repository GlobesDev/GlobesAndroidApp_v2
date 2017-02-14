package il.co.globes.android;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.graphics.Color;
import android.net.Uri;
import android.os.*;
import android.preference.PreferenceManager;
import android.provider.Settings.Secure;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.appsflyer.AppsFlyerLib;
//import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.Crashlytics;
import com.google.android.apps.analytics.GoogleAnalyticsTracker;
//import com.google.android.gcm.GCMRegistrar;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.doubleclick.PublisherAdView;
import com.google.android.gms.ads.doubleclick.PublisherInterstitialAd;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import il.co.globes.android.app.lifecycle.Foreground;
import il.co.globes.android.fragments.PortFolioFragment;
import il.co.globes.android.interfaces.WallaAdvTaskCompleted;
import il.co.globes.android.objects.AjillionObj;
import il.co.globes.android.objects.DataContext;
import il.co.globes.android.objects.GlobesURL;
import il.co.globes.android.objects.Ticker;
import il.co.globes.android.parsers.GlobesAllWithMapSAXHandler;
import il.co.globes.android.parsers.WallaAdvParser;
import mobi.pushapps.PAActivity;
import mobi.pushapps.notifications.PANotificationBuilder.PAClickType;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;

//import com.groboot.pushapps.PushManager;

public class SplashScreen extends Activity implements WallaAdvTaskCompleted {
    private static final int time_before_ad_in_doc_s = 300;

    private String sakindoURL = "<div style=\"text-align:center;\"><script src=\"http://live.sekindo.com/live/liveView.php?s=52471&subId=DEFAULT&nofr=1\"></script></div>";

    private int SPLASH_DISPLAY_LENGTH = 5000;// 10000; // 5000
    private int SPLASH_DISPLAY_LENGTH_AFTER_CLOSE_PRESSED = 200;
    private final int DOC_ID_LENGTH = 10;
    private ScheduledExecutorService scheduleTaskExecutor;

    // scheme params
    String schemeDocID, schemeFID, schemeURIShare;

    /**
     * TAG
     */
    private final static String TAG = "SplashScreen";

    static final int CLOSESPLASH = 1;

    /**
     * the dfpAdview Ad
     */
    // DfpAdView dfpAdView;
    PublisherAdView dfpAdView;
    /**
     * the frame layout for the dfpAdview container
     */
    FrameLayout frameLayAdContainer;

    /**
     * Boolean to check if Ad was clicked so we wait and not move to the
     * MainTabActivity
     */
    private boolean isAdClicked;
    private boolean isDFPInterstitial = false;

    String pushNotificationDocID;

    /* google analytics tracker */
    GoogleAnalyticsTracker tracker;

    // dynamic menu items
    private DataStore dataStore;
    private final int MAX_TASK_COUNT = 2;
    private int taskCounter = 0;
    private boolean splashFromFolder = false;
    private boolean splashFromRichMedia = false;
    private boolean splashFromRichMediaGlobal = false;
    private String wallaPageType = "homepage";
    private String wallaLayoutType = "l_globes_app_h";
    private Runnable closeRunable;
    private boolean isClosedImageClicked = false;
    private Thread trDelay;
    private boolean isArticleFromScheme = false;
    private boolean isFolderFromScheme = false;
    private PublisherInterstitialAd mPublisherInterstitialAd;

    private Handler contentSwitcher = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == CLOSESPLASH) {
                // try to move to next activity
                upTaskCounter();
            }
        }

    };
    private RelativeLayout black_wraper;

    private void cleanPushAndSchemeVars() {
        pushNotificationDocID = null;
        schemeDocID = null;
        schemeURIShare = null;
        //schemeFID = null;
    }

    public int dpToPx(int dp) {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }

    @SuppressLint("NewApi")
    @Override
    public void onCreate(Bundle icicle) {
        // Definitions.appActive = true;

        super.onCreate(icicle);

//String sDID1 = Utils.getUUIDForPush(getApplicationContext());
//String sDID2 = Secure.getString(getApplicationContext().getContentResolver(), Secure.ANDROID_ID);
//Log.e("alex", "DEVICEID: " + sDID1 + "===" + sDID2);

        dataStore = DataStore.getInstance();
        dataStore.setSplashScreenClosed(false);
        try {
            Bundle extrasVals = getIntent().getExtras();
            if (extrasVals != null) {
                boolean bSplashFromFolder = (extrasVals.containsKey("fromFolder")) ? extrasVals.getBoolean("fromFolder") : false;
                wallaPageType = (extrasVals.containsKey("wallaPage")) ? extrasVals.getString("wallaPage") : wallaPageType;
                wallaLayoutType = (extrasVals.containsKey("wallaLayout")) ? extrasVals.getString("wallaLayout") : wallaLayoutType;

                Log.e("alex", "showRichMedia 000");

                if (extrasVals.containsKey("fromFolderRichMedia")) {
                    splashFromRichMedia = extrasVals.getBoolean("fromFolderRichMedia");
                    splashFromRichMediaGlobal = splashFromRichMedia;
                }

                if (bSplashFromFolder || splashFromRichMedia) {
                    //Log.e("alex", "showRichMedia Folder: " + wallaPage + "===" + wallaLayout);

                    drawSplashOnFolderEntrance(wallaPageType, wallaLayoutType);
                    return;
                }
            }
        } catch (Exception ex) {
            Log.e("alex", "SplashScreenShowInterstatial Folder Exception: " + ex);
        }

        //WallaAdvParser.setStartCookie(getApplicationContext());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            // TODO - eli : is only work from 4.0 and above
            Foreground.init(getApplication());
        }
        // get instance

        try {
            new WoorldsSDKInit(getApplicationContext()).getInstance();
            Log.e("alex", "WoorldsSDKInit...");
        } catch (Exception ex) {
        }

//		WoorldsEventsReceiver eventsReceiver = new WoorldsEventsReceiver()
//		{
//			@Override
//			public void woorldsError(String errorString)
//			{
//				// write it to log
//				Log.e(TAG, "Woorlds Error: " + errorString);
//				Toast.makeText(getApplicationContext(), "Error: " + errorString, Toast.LENGTH_SHORT).show();
//			}
//        
//			@Override
//			public void woorldsDataUpdated(WoorldsData woorldsData)
//			{
//
//				// check if we are inside a woorld
//				WoorldInfo inWoorld = null;
//				if (null != woorldsData.serverData && null != woorldsData.serverData.wifiWorlds)
//				{
//					List<WoorldInfo> woorlds = woorldsData.serverData.wifiWorlds;
//
//					// find the world we are in
//					for (WoorldInfo woorld : woorlds)
//					{
//						if (woorld.InWoorld)
//						{
//							inWoorld = woorld;
//						}
//					}
//				}
//				if (inWoorld != null)
//				{
//					Log.i(TAG, "We are in woorld: " + inWoorld.worldName);
//				}
//			}
//		};

        // Register the event receiver
        //mWoorldsSDK.registerWoorldsEvents(eventsReceiver);

        /**
         * Loading the default preferences from the preferences.xml only once
         * the applications is loading for first time notificationsPref = true ,
         * show notifications by default mediaPlayerPref = false , do not show
         * videos in browser by default checkboxPref = false , do not align text
         * to the right by default
         * */

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        // tracker
        tracker = GoogleAnalyticsTracker.getInstance();

        // TODO eli ************ add this when production crashlytics
        // ****************
        setContentView(R.layout.globes_splash);
        frameLayAdContainer = (FrameLayout) findViewById(R.id.frame_layout_splash_ad_container);
        Crashlytics.start(this);

        //new AjillionHandle().execute();

        // set content view

        // black_wraper = (RelativeLayout)findViewById(R.id.black_wraper);

        // get Globes URLS

        checkForPushNotificationClicks();
        getGlobesURLs();

        // Integrating AppsFlyer SDK
        AppsFlyerLib.setAppsFlyerKey(Definitions.APPSFLYER_DEV);
        AppsFlyerLib.sendTracking(this);

        // eli REGISTER GCM
//        regiserToGCM();

        Log.e("eli", "user id for the push= " + Utils.getUUIDForPush(this));
        /** /7263/Android.mavaron - 480x800 */

    }

    private void checkForPushNotificationClicks() {
        Bundle bundle = getIntent().getExtras();
        //removing our extra value so if the activity is resumed it won't seem like a new push click
        getIntent().removeExtra(PAActivity.PANotificationKeys.ClickType);
        if (bundle != null) {
            String clickType = bundle.getString(PAActivity.PANotificationKeys.ClickType);
            if (clickType != null) {
                if (clickType.equals(PAClickType.PADefaultLayoutClick) ||
                        clickType.equals(PAClickType.PAMainSmallViewClick) ||
                        clickType.equals(PAClickType.PAMainLayoutClick)) {

	            	/*
	            	String strData = "{";
	                for (String key : bundle.keySet()) {
	                	strData += " " + key + " => " + bundle.get(key) + ";";
	                }
	                strData += " }";
	            	*/

                    String did = null;
                    Intent i = null;

                    if (bundle.get("did") != null) {
                        String sDidJson = String.valueOf(bundle.get("did"));

                        if (sDidJson != "") {
                            try {
                                did = new JSONObject(sDidJson).getString("did");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                    }

                    if (did != null) {
                        Definitions.pushDid = did;
                        pushNotificationDocID = did;
	            		
	            		/*
	            		if(did.length() < 6)
						{
							Log.e("alex", "PushAppsTest 10: did=" + did);
							i = new Intent(getApplicationContext(), MainSlidingActivity.class);
						}
						else
						{
							i = new Intent(getApplicationContext(), DocumentActivity_new.class);
						}
	            		
	            		i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
						//i.putExtra("pushNotificationDocID", did);
						Definitions.pushDid = did;						
						
						pushNotificationDocID = did;
						Log.e("alex", "pushNotificationDocID checkForPushNotificationClicks: " + pushNotificationDocID);
						
						cleanPushAndSchemeVars();
						SplashScreen.this.startActivity(i);
						SplashScreen.this.finish();
						Definitions.appActive = true;
						*/
                    }

                    // Main notification was clicked, due whatever
                    // you used to do (e.g. extract your custom
                    // keys and direct the user to a specific screen).
                    // You can also access the main notification
                    // url, by bundle.getString(
                    // PAActivity.PANotificationKeys.ArticleUrl).

                } else if (clickType.equals(PAClickType.PAEnrichmentClick)) {

                    String articleUrl = bundle.getString(
                            PAActivity.PANotificationKeys.ArticleUrl);

                    // Take the user to this article url,
                    // or extract information from it.
                }

            }
        }
    }

    protected void onStart() {
        super.onStart();
    }

    private void drawSplashOnFolderEntrance(String wallaPage, String wallaLayout) {
        if (Definitions.wallafusion) {
            try {
                Log.e("alex", "showRichMedia drawSplashOnFolderEntrance...");
                splashFromFolder = true;

                dataStore = DataStore.getInstance();
                dataStore.setShowAdMob(false);
                Log.e("alex", "SplashScreenShowInterstatial dataStore.setShowAdMob(true)..." + dataStore.showAdMob());

                WallaAdvParser wap = new WallaAdvParser(getApplicationContext(), getApplication(), SplashScreen.this, wallaPage, wallaLayout, "", 0);
                wap.execute();

                //setContentView(R.layout.globes_splash);
                setContentView(R.layout.globes_splash_transparent);
                frameLayAdContainer = (FrameLayout) findViewById(R.id.frame_layout_splash_ad_container);

                //RelativeLayout rMain = (RelativeLayout) frameLayAdContainer.getParent();
                //rMain.setBackground( getResources().getDrawable(R.drawable.m2));
                //rMain.setBackgroundColor(android.R.color.white);

                startFlow();
            } catch (Exception ex) {
                Log.e("alex", "showRichMedia ERROR:" + ex);
            }

        } else {
            Log.e("alex", "dfpAdHandle drawSplashOnFolderEntrance...");

            setContentView(R.layout.globes_splash_transparent);
            frameLayAdContainer = (FrameLayout) findViewById(R.id.frame_layout_splash_ad_container);
            dfpAdHandle(false);
            startFlow();
        }
    }

    private void dfpAdHandle(boolean isMainSplash) {
        Log.e("alex", "dfpAdHandle ver2...");
        isDFPInterstitial = false;
        mPublisherInterstitialAd = new PublisherInterstitialAd(SplashScreen.this);

        String space = (isMainSplash) ? Definitions.dfp_interstitial_main : Definitions.dfp_interstitial_article;

        Log.e("alex", "dfpAdHandle ver2 InterstitialAd:" + space);

        mPublisherInterstitialAd.setAdUnitId(space);
        //mPublisherInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");  // "/6499/example/interstitial"

        mPublisherInterstitialAd.setAdListener(new AdListener() {
            public void onAdLoaded() {

                Log.e("alex", "dfpAdHandle splash screen received Ad with AdUnit v1 :" + Definitions.dfp_interstitial_main);

                mPublisherInterstitialAd.show();
                isAdClicked = false;
                isDFPInterstitial = true;
                countDownAndCloseSplash();
                //startApp();
            }

            public void onAdFailedToLoad(int errorCode) {
                /**
                 * Log the Ad Error and close the Activity move to
                 * MainTabActivity
                 */
                Log.e("alex", "dfpAdHandle1 splash failed to receive Ad with AdUnit: " + Definitions.dfp_interstitial_main + " the error: " + errorCode);
                isAdClicked = false;

                String message = "Google onAdFailedToLoad: " + getErrorReason(errorCode);
                Log.e("alex", "dfpAdHandle Error:" + message);

                // TODO add tracking to track the Ad failure
				/*
				 * if (tracker != null) { try {
				 * tracker.trackPageView("/Main splashScreen" +
				 * Definitions.AD_FAILURE_TAG_SPLASH + err.toString()); } catch
				 * (Exception e) {
				 * 
				 * } }
				 */
                contentSwitcher.sendEmptyMessage(CLOSESPLASH);
            }

            public void onAdOpened() {
                isAdClicked = true;
            }

            public void onAdClosed() {
                Log.e("alex", "dfpAdHandle splash screen onAdClosed");
                isAdClicked = false;
                if (!contentSwitcher.hasMessages(CLOSESPLASH)) {
                    contentSwitcher.sendEmptyMessage(CLOSESPLASH);
                }

            }

            public void onAdLeftApplication() {
              //  isAdClicked = true;

            }
        });

        PublisherAdRequest adRequest = new PublisherAdRequest.Builder()
                //.addTestDevice("7BBA10612964F988FEC2CB656AD07570")
                .build();

        mPublisherInterstitialAd.loadAd(adRequest);

    }

    private String getErrorReason(int errorCode) {
        // Gets a string error reason from an error code.
        String errorReason = "";
        switch (errorCode) {
            case AdRequest.ERROR_CODE_INTERNAL_ERROR:
                errorReason = "Internal error";
                break;
            case AdRequest.ERROR_CODE_INVALID_REQUEST:
                errorReason = "Invalid request";
                break;
            case AdRequest.ERROR_CODE_NETWORK_ERROR:
                errorReason = "Network Error";
                break;
            case AdRequest.ERROR_CODE_NO_FILL:
                errorReason = "No fill!!!";
                break;
        }
        return errorReason;
    }

    private void dfpAdHandle_old(boolean isMainSplash) {
        Log.e("alex", "startFlow dfpAdHandle");

        black_wraper = new RelativeLayout(SplashScreen.this);
        frameLayAdContainer.addView(black_wraper);
        dfpAdView = new PublisherAdView(SplashScreen.this);

        //frameLayAdContainer.setBackgroundColor(Color.RED);

        if (isMainSplash) {
            Log.e("alex", "dfpAdHandle Main Splash!!!");
            dfpAdView.setAdUnitId(Definitions.dfp_interstitial_main);
        } else {
            Log.e("alex", "dfpAdHandle Not Main Splash!!!");
            dfpAdView.setAdUnitId(Definitions.dfp_interstitial_article);
        }

        dfpAdView.setAdSizes(new AdSize(320, 480));
        //dfpAdView.setAdSizes(AdSize.SMART_BANNER);
        frameLayAdContainer.addView(dfpAdView, FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);

        dfpAdView.setAdListener(new AdListener() {
            public void onAdLoaded() {
                /**
                 * Log the Ad received , change background for black and start
                 * countDown for closing the Ad Ad clicked boolean for notifying
                 * if Ad was clicked
                 */
                Log.e("alex", "dfpAdHandle splash screen received Ad with AdUnit :" + Definitions.dfp_interstitial_main);

                isAdClicked = false;
                dfpAdView.setBackgroundColor(Color.BLACK);
                countDownAndCloseSplash();

                black_wraper.setVisibility(View.VISIBLE);
                ImageView imageView = new ImageView(SplashScreen.this);
                frameLayAdContainer.addView(imageView, FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
                imageView.setImageResource(R.drawable.close_ad);
                imageView.setPadding(32, 32, 32, 32);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // TODO eli add button close

                        Log.e("alex", "btnClose Pressed");

                        isClosedImageClicked = true;
                        //SplashScreen.this.finish();
                        contentSwitcher.sendEmptyMessage(CLOSESPLASH);
                        startApp();
                        black_wraper.setVisibility(View.GONE);
                        frameLayAdContainer.setVisibility(View.GONE);

                    }
                });
            }

            public void onAdFailedToLoad(int errorCode) {

                /**
                 * Log the Ad Error and close the Activity move to
                 * MainTabActivity
                 */
                Log.e("alex", "dfpAdHandle splash failed to receive Ad with AdUnit: " + Definitions.dfp_interstitial_main + " the error: " + errorCode);
                isAdClicked = false;

                // TODO add tracking to track the Ad failure
				/*
				 * if (tracker != null) { try {
				 * tracker.trackPageView("/Main splashScreen" +
				 * Definitions.AD_FAILURE_TAG_SPLASH + err.toString()); } catch
				 * (Exception e) {
				 * 
				 * } }
				 */
                contentSwitcher.sendEmptyMessage(CLOSESPLASH);

            }

            public void onAdOpened() {
                isAdClicked = true;
            }

            public void onAdClosed() {
                isAdClicked = false;
                if (!contentSwitcher.hasMessages(CLOSESPLASH)) {
                    contentSwitcher.sendEmptyMessage(CLOSESPLASH);
                }

            }

            public void onAdLeftApplication() {
                isAdClicked = true;

            }
        });
        PublisherAdRequest request = new PublisherAdRequest.Builder().build();
        dfpAdView.loadAd(request);
    }

    private void startApp() {
        // android.os.Debug.waitForDebugger();

        //if(splashFromRichMediaGlobal){return;}

        Log.e("alex", "countDownAndCloseSplash1 startApp!!!!!!!!!!!!!!!! isClosedImageClicked:" + isClosedImageClicked);

        Log.e("alex", "pushNotificationDocID startApp: " + pushNotificationDocID);

        final Intent mainIntent = new Intent(SplashScreen.this, MainSlidingActivity.class);

        // not null pass it on to mainTabActivity
        if (pushNotificationDocID != null) {
            mainIntent.putExtra(Definitions.KEY_BUNDLE_PUSH_NOTIFICATIONS_DOC_ID, pushNotificationDocID);
            Definitions.PUSH_WAS_HANDLE = false;
        }

        // if we have a share from the scheme put the ready URI to
        // parse inside intent
        else if (schemeURIShare != null && schemeURIShare.length() > 0) {
            if (Definitions.appActive) // if app is active
            {
                mainIntent.setClass(SplashScreen.this, ShareActivity.class);
                mainIntent.putExtra(Definitions.CALLER, Definitions.SHARES);
                mainIntent.putExtra(Definitions.PARSER, Definitions.SHARES);
                mainIntent.putExtra("shareId", "0");
                mainIntent.putExtra("feederId", "0");
                mainIntent.putExtra("URI", schemeURIShare);
            } else { // if app is not active
                mainIntent.putExtra(Definitions.KEY_BUNDLE_SCHEME_URI_SHARE, schemeURIShare);
            }
        }
        // if we have a docID from the scheme
        else if (schemeDocID != null && schemeDocID.length() > 0) {
            mainIntent.putExtra("pushNotificationDocID", schemeDocID);
            mainIntent.putExtra(Definitions.SCHEME_COMING_FROM_SCHEME, true);
            mainIntent.putExtra("articleId", schemeDocID);

            if (Definitions.appActive || isArticleFromScheme) // if app is active
            {
                Log.e("alex", "PushAppsTest v2");
                mainIntent.setClass(SplashScreen.this, DocumentActivity_new.class);
                //Log.e("alex", " DocumentActivity_new.class");
            }
        }

//		else if(schemeFID != null && isFolderFromScheme)
//		{
//			Log.e("alex", "LoadFolderDataFromScheme 111");
//			
//			String uri;
//			String parser = Definitions.MAINSCREEN;;
//			String caller = Definitions.SECTIONS;;
//			uri = GlobesURL.URLSections + schemeFID;
//			// move to fragment TTT
//			SectionListFragment fragment = new SectionListFragment();
//			Bundle args = new Bundle();
//			args.putString(Definitions.URI_TO_PARSE, uri);
//			args.putString(Definitions.CALLER, caller);
//			args.putString(Definitions.PARSER, parser);
//			args.putString(Definitions.HEADER, Utils.GetFolderNameByID(Integer.parseInt(schemeFID)));
//			fragment.setArguments(args);
//			
//			if(MainSlidingActivity.getMainSlidingActivity() == null)
//			{
//				Log.e("alex", "LoadFolderDataFromScheme 111.1");
//				startActivity(new Intent(this, MainSlidingActivity.class));
//				Log.e("alex", "LoadFolderDataFromScheme 111.2");
//			}
//			Log.e("alex", "LoadFolderDataFromScheme 111.3: " + MainSlidingActivity.getMainSlidingActivity());
//	        MainSlidingActivity.getMainSlidingActivity().onSwitchContentFragment(fragment, SectionListFragment.class.getSimpleName(), true, false, false);
//	        Log.e("alex", "LoadFolderDataFromScheme 111.4");
//	        cleanPushAndSchemeVars();
//	        SplashScreen.this.finish();
//	        Definitions.appActive = true;
//	        return;
//		}

        cleanPushAndSchemeVars();
        SplashScreen.this.startActivity(mainIntent);
        SplashScreen.this.finish();
        Definitions.appActive = true;
    }

    private void getGlobesURLs() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                GlobesAllWithMapSAXHandler globesAllWithMapSAXHandler = new GlobesAllWithMapSAXHandler();

                URL url;
                try {
                    String android_id = Secure.getString(getApplicationContext().getContentResolver(), Secure.ANDROID_ID);
                    Log.e("eli", "device id: " + android_id);
                    PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                    String version = pInfo.versionName;

                    if (Definitions.MapURLMode == Definitions.MAP_URL_MODE_DEBUG) {
                        url = new URL(Definitions.URLGlobesAllWithMap + "&UDID=" + android_id + "&mapMode=debug&ver=" + version);
                    } else {
                        url = new URL(Definitions.URLGlobesAllWithMap + "&UDID=" + android_id + "&mapMode=prod&ver=" + version);
                    }

                    Log.e("alex", "Parser1: " + url);
                    Utils.parseXmlFromUrlUsingHandler(url, globesAllWithMapSAXHandler);

                } catch (MalformedURLException e) {
                    Log.e(TAG, "getGlobesURLs(): " + e.getMessage());
                } catch (Exception e) {
                    Log.e(TAG, "getGlobesURLs(): " + e.getMessage());
                }
                return null;
            }

//			@Override
//			protected Void doInBackground(Void... params)
//			{
//				GlobesMapSAXHandler globesMapSAXHandler = new GlobesMapSAXHandler();
//				URL url;
//				try
//				{
//					String android_id = Secure.getString(getApplicationContext().getContentResolver(), Secure.ANDROID_ID);
//					Log.e("eli", "device id: " + android_id);
//					PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
//					String version = pInfo.versionName;
//
//					if (Definitions.MapURLMode == Definitions.MAP_URL_MODE_DEBUG)
//					{
//						url = new URL(Definitions.URLGlobesMap + "UDID=" + android_id + "&mode=debug&ver=" + version);
//					}
//					else
//					{
//						url = new URL(Definitions.URLGlobesMap + "UDID=" + android_id + "&mode=prod&ver=" + version);
//					}
//									
//					Log.e("alex","Parser1: " + url);
//					Utils.parseXmlFromUrlUsingHandler(url, globesMapSAXHandler);
//					
//				}
//				catch (MalformedURLException e)
//				{
//					Log.e(TAG, "getGlobesURLs(): " + e.getMessage());
//				}
//				catch (Exception e)
//				{
//					Log.e(TAG, "getGlobesURLs(): " + e.getMessage());
//				}
//				return null;
//			}

            protected void onPostExecute(Void result) {
                Log.e("alex", "onPostExecute: " + Definitions.wallafusion + " Definitions.appActive:" + Definitions.appActive);
                if (!Definitions.appActive) {
                    if (Definitions.wallafusion) {
                        Log.e("alex", "SplashScreenShowInterstatial starting...");
                        /////////////////////WALLA TEST///////////////////////////////////

                        try {
                            WallaAdvParser wap = new WallaAdvParser(getApplicationContext(), getApplication(), SplashScreen.this, "homepage", "na2", "", 0);
                            wap.execute();
                        } catch (Exception ex) {
                            Log.e("alex", "SplashScreen onPostExecute Error: " + ex);
                        }
                        //////////////////////////////////////////////////////////////////
                    } else {
                        if (Definitions.toUseAjillion) {
                            new AjillionHandle().execute();
                        } else {
                            dfpAdHandle(true);
                            Log.e("alex", "onPostExecute dfpAdHandle");
                        }
                    }
                } else startApp();

                startFlow();

				/*
				scheduleTaskExecutor = Executors.newScheduledThreadPool(1);
				scheduleTaskExecutor.scheduleAtFixedRate(new Runnable()
				{
					@Override
					public void run()
					{
						dataStore.setShowAdMob(true);
					}
				}, Definitions.TIME_MAAVARON, Definitions.TIME_MAAVARON, TimeUnit.SECONDS);
			*/
                //Log.e("alex", "AAAAAAAAAAAAAAAAA: " + Definitions.TIME_MAAVARON);
            }

            ;
        }.execute();
    }

    /**
     * Start rest of splash screen flow , after URLs are fetched
     */
    private void startFlow() {
        /**
         * Ticker init just for background tasks to start and populate data on
         * background
         */

        Log.e("alex", "startFlow!!!!!!!!!!!!!");

        Ticker.getInstance(this);

        // set dynamic items
        setDynamicMenuItems();

    }

    /**
     * Parse dynamic menu items for later use in right Menu
     */
    private void setDynamicMenuItems() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    URL url = new URL(GlobesURL.URLMainScreen);
                    //Log.e("alex", "Parser1.1:" + GlobesURL.URLMainScreen);
                    //NewsSet newsSet = NewsSet.parseURL(url, Definitions.SECTIONS);

                    // dataStore.setDynamicMenuItemsHolder(newsSet);
                } catch (MalformedURLException e1) {
                    Log.e(TAG, "setDynamicMenuItems: " + ((e1 != null) ? e1.getMessage() : "error is unll"));
                } catch (Exception e) {
                    Log.e(TAG, "setDynamicMenuItems: " + ((e != null) ? e.getMessage() : "error is unll"));
                }
                return null;

            }

            protected void onPostExecute(Void result) {
                upTaskCounter();
            }

            ;

        }.execute();
    }

    @Override
    protected void onResume() {

        super.onResume();
        // Setting up intent data push notifications & url scheme different
        // scenarios
        Intent dataIntent = getIntent();
        if (dataIntent != null) {
            // push test , getting the intent and the string DocID
            //pushNotificationDocID = dataIntent.getStringExtra("pushNotificationDocID");
            // PushApps handling
            try {
                if (dataIntent.hasExtra("notificationId")) {
                    // PushManager.getInstance(getApplicationContext()).reportNotificationOpened(
                    // dataIntent.getExtras().getString("notificationId"));
                }
            } catch (Exception e) {
            }
            String url = dataIntent.getDataString();

            Log.e("alex", "PushAppsTest 2: url=" + url);

            if (url != null && url.length() > 0) {
                handleSchemeIntent(url);
            }
        }
        // com.facebook.Settings.publishInstallAsync(context,
        // Definitions.FACEBOOK_APP_ID);
    }

    @Override
    protected void onDestroy() {
        Log.e("alex", "showRichMedia on splash destroy...");
        dataStore.setSplashScreenClosed(true);
        super.onDestroy();
    }

    /**
     * Parse the data from the string DATA from the onNewIntent() Puts data in
     * schemeURIShare or schemeDocID
     *
     * @param url - the string DATA from intent
     */
    private void handleSchemeIntent(String url) {
        // handle the scheme intent , parse and determine whether its a share or
        // regular article

        // case its a share

        //Log.e("alex", "handleSchemeIntent: " + url);

        if (url.contains("instrument.aspx"))

        {
            String feeder, rawIidAndFeeder;
            feeder = "0";
            int feederStartIndex;
            rawIidAndFeeder = new String(url.substring(url.indexOf("iid=") + 4));
            feederStartIndex = rawIidAndFeeder.indexOf("&f");
            if (feederStartIndex != -1) {
                feeder = new String(rawIidAndFeeder.substring(feederStartIndex + 3));
                rawIidAndFeeder = new String(rawIidAndFeeder.substring(0, feederStartIndex));
            }
            schemeURIShare = GlobesURL.URLSharePage.replaceAll("XXXX", feeder).replaceAll("YYYY", rawIidAndFeeder);
        } else if (url.contains("article.aspx?did=")) // case article
        {
            Uri uri = Uri.parse(url);
            schemeDocID = uri.getQueryParameter("did");
            isArticleFromScheme = true;
        } else if (url.contains("home.aspx?fid=")) // case folder
        {
            Uri uri = Uri.parse(url);
            schemeFID = uri.getQueryParameter("fid");
            isFolderFromScheme = true;

            //Log.e("alex", "LoadFolderDataFromScheme handleSchemeIntent fid: " + schemeFID);
        }

    }

    void countDownAndCloseSplash_v2() {
        Log.e("alex", "countDownAndCloseSplash1 ENTER..." + SPLASH_DISPLAY_LENGTH);

        closeRunable = new Runnable() {
            public void run() {
                isAdClicked = false;
                startFlow();
                Log.e("alex", "countDownAndCloseSplash1 RUN...");
                //frameLayAdContainer.setVisibility(View.GONE);
                SplashScreen.this.finish();
            }
        };
        frameLayAdContainer.postDelayed(closeRunable, SPLASH_DISPLAY_LENGTH);
    }

    void countDownAndCloseSplash() {
        Log.e("alex", "countDownAndCloseSplash !!!!!!!!!!!!!!!!!! isAdClicked:" + isAdClicked);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(SPLASH_DISPLAY_LENGTH);
                } catch (Exception e) {
                    Log.e("alex", "countDownAndCloseSplash error: " + e.getMessage());
                } finally {
                    if (!isAdClicked) {
                        Log.e("alex", "countDownAndCloseSplash CLOSESPLASH");
                        contentSwitcher.sendEmptyMessage(CLOSESPLASH);
                    }

                    if(isDFPInterstitial)
                    {
                        Log.e("alex", "countDownAndCloseSplash isDFPInterstitial");

                        //isAdClicked = false;
                        contentSwitcher.sendEmptyMessage(CLOSESPLASH);
                        startApp();
                        SplashScreen.this.finish();
                    }

//					if(splashFromRichMediaGlobal)
//					{
//						Log.e("alex", "countDownAndCloseSplash1 splashFromRichMediaGlobal..."); 
//						//startFlow();
//						SplashScreen.this.finish();
//					}

                }
            }
        }).start();
    }

    void closeSplashImmediately() {
        Log.e("alex", "countDownAndCloseSplash1 Immediately...");

//		frameLayAdContainer.removeCallbacks(closeRunable);
//		SplashScreen.this.finish();
//		if(1==1)return;

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(SPLASH_DISPLAY_LENGTH_AFTER_CLOSE_PRESSED);

                } catch (Exception e) {
                } finally {
                    if (!isAdClicked) {
                        contentSwitcher.sendEmptyMessage(SPLASH_DISPLAY_LENGTH_AFTER_CLOSE_PRESSED);
                    }

                }
            }
        }).start();
    }

    /**
     * Act as a Semaphore , we need both splash & dynamic items to finish their
     * tasks before continuing
     */
    private synchronized void upTaskCounter() {
        taskCounter++;

        if (taskCounter == MAX_TASK_COUNT) {
            // initialize counter
            taskCounter = 0;
Log.e("alex", "upTaskCounter+++");
            // if Ad not clicked we move to the MainTabActivity
            if (!isAdClicked && !isClosedImageClicked) {
                startApp();
            }
        }
    }

    class AjillionHandle extends AsyncTask<Void, Void, Void> {
        private ImageView ajillionObjImage;
        private AjillionObj ajillionObj;
        private boolean isImageSplash;
        private ImageView imageCloseView;
        private WebView webView_iframe_ad;

        @Override
        protected void onPreExecute() {
            isImageSplash = false;

            ajillionObjImage = new ImageView(SplashScreen.this);
            imageCloseView = new ImageView(SplashScreen.this);
            webView_iframe_ad = new WebView(SplashScreen.this);

            imageCloseView.setImageResource(R.drawable.close_ad);
            imageCloseView.setPadding(32, 32, 32, 32);
            imageCloseView.setVisibility(View.GONE);
            imageCloseView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    frameLayAdContainer.setVisibility(View.GONE);
                    // imageCloseView.setVisibility(View.GONE);
                    // webView_iframe_ad.setVisibility(View.GONE);
                }
            });

            webView_iframe_ad.setOnTouchListener(new View.OnTouchListener() {
                @SuppressLint("ClickableViewAccessibility")
                public boolean onTouch(View v, MotionEvent event) {
                    isAdClicked = true;
                    startApp();
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(ajillionObj.getClick_url()));
                    startActivity(browserIntent);
                    return true;
                    // return (event.getAction() == MotionEvent.ACTION_MOVE);
                }
            });
            webView_iframe_ad.setVerticalScrollBarEnabled(false);
            webView_iframe_ad.setHorizontalScrollBarEnabled(false);
            webView_iframe_ad.setWebChromeClient(new WebChromeClient());
            webView_iframe_ad.setPadding(0, 0, 0, 0);
            WebSettings webViewSettings = webView_iframe_ad.getSettings();
            webViewSettings.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
            webViewSettings.setJavaScriptCanOpenWindowsAutomatically(false);
            webViewSettings.setJavaScriptEnabled(true);
            webViewSettings.setUseWideViewPort(false);
            webViewSettings.setBuiltInZoomControls(false);
            webViewSettings.setLoadWithOverviewMode(false);
            webViewSettings.setSupportZoom(false);
            webView_iframe_ad.setVisibility(View.GONE);

            frameLayAdContainer.addView(webView_iframe_ad, FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
            frameLayAdContainer.addView(ajillionObjImage, FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
            frameLayAdContainer.addView(imageCloseView, FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);

        }

        @Override
        protected Void doInBackground(Void... params) {
            ajillionObj = Utils.getAjillionObj(SplashScreen.this, Definitions.AJILLION_AD_ID_SPLASH_SCREEN, 320, 480, false);

            Log.e("alex", "Get ajillion obj: " + ajillionObj.getClick_url());

            if (ajillionObj == null || ajillionObj.getSuccess() == null || !ajillionObj.getSuccess()
                    || ajillionObj.getCreative_url() == null || ajillionObj.getCreative_url().equals("")) {
                isAdClicked = false;
                contentSwitcher.sendEmptyMessage(CLOSESPLASH);
                return null;
            } else if (ajillionObj.getCreative_type().equals("image")) {
                isImageSplash = true;

            } else {
                // video
                isImageSplash = false;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (isImageSplash) {
                Picasso.with(SplashScreen.this).load(ajillionObj.getCreative_url()).fit().into(ajillionObjImage, new Callback() {
                    @Override
                    public void onSuccess() {
                        Log.e("eli", "yyyyyyyyyyyy");
                        imageCloseView.setVisibility(View.VISIBLE);
                        isAdClicked = false;
                        countDownAndCloseSplash();

                        ajillionObjImage.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                isAdClicked = true;
                                startApp();
                                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(ajillionObj.getClick_url()));
                                startActivity(browserIntent);
                            }
                        });
                    }

                    @Override
                    public void onError() {
                        Log.e("eli", "nnnnnnnnnnn");
                        isAdClicked = false;
                        contentSwitcher.sendEmptyMessage(CLOSESPLASH);
                    }
                });
            } else {
                ajillionObjImage.setVisibility(View.GONE);
                imageCloseView.setVisibility(View.VISIBLE);
                webView_iframe_ad.setVisibility(View.VISIBLE);

                webView_iframe_ad.loadData(sakindoURL, "text/html; charset=utf-8", "UTF-8");
                webView_iframe_ad.setWebViewClient(new WebViewClient() {
                    public void onPageFinished(WebView view, String url) {
                        countDownAndCloseSplash();

                    }

                    @Override
                    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                        isAdClicked = false;
                        contentSwitcher.sendEmptyMessage(CLOSESPLASH);
                        super.onReceivedError(view, errorCode, description, failingUrl);
                    }

                });
            }
        }
    }

    /**
     * Disable back button.
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return false;
    }

    @Override
    protected void onStop() {
        super.onStop();
        checkSchemeAndRedirectPortfolio();
    }

    //Redirecting to Portfolio if link is globes://m.globes.co.il/news/m/portfolio.aspx
    private void checkSchemeAndRedirectPortfolio() {
        try {
            Uri intentStart = getIntent().getData();
            //Log.e("alex", "CheckScheme: " + intentStart);

            if (null != intentStart) {
                String startScheme = intentStart.getScheme().toLowerCase();

                if (startScheme.equals("globes") && intentStart.toString().contains("portfolio.aspx")) {
                    String globesAccessKey = DataContext.Instance().getAccessKey();

                    //Log.e("alex", "globesAccessKey: " + globesAccessKey);

                    if (globesAccessKey.length() < 2) {
                        Intent intPortfolio = new Intent(SplashScreen.this, NewLoginActivity.class);
                        startActivity(intPortfolio);
                        SplashScreen.this.finish();
                        Definitions.appActive = true;
                        return;
                    } else {
                        //Log.e("alex", "StartApp you are login:" + globesAccessKey);

                        PortFolioFragment fragment = new PortFolioFragment();
                        // set bundle to fragment
                        Bundle args = new Bundle();
                        args.putString(Definitions.URI_TO_PARSE, GlobesURL.URLMainScreen);
                        args.putString(Definitions.CALLER, Definitions.MAINSCREEN);
                        args.putString(Definitions.PARSER, Definitions.PORTFOLIO);
                        args.putString(Definitions.HEADER, "תיק אישי");
                        fragment.setArguments(args);

                        // switch content
                        MainSlidingActivity.getMainSlidingActivity().onSwitchContentFragment(fragment, PortFolioFragment.class.getSimpleName(),
                                true, false, false);

                        SplashScreen.this.finish();
                        return;
                    }
                }
            }
        } catch (Exception ex) {
            Log.e("alex", "ERROR! checkScheme(): " + ex);
        }
    }

    private void onErrorLoadingSplashBanner() {
        isAdClicked = false;
        contentSwitcher.sendEmptyMessage(CLOSESPLASH);
    }

    @Override
    public void onTaskCompleted(Map<String, Map<String, String>> map) {
        if(Definitions.wallafusion) {
            //if(1==1){return;}
            showSplashScreenInterstatial(map);
        }
    }

    public void showSplashScreenInterstatial(Map<String, Map<String, String>> map) {
        Log.e("alex", "SplashScreenShowInterstatial on SplashScreen");

        //Log.e("alex", "showRichMedia on SplashScreen Start!!!");

        try {
            String space = (splashFromFolder) ? Definitions.WALLA_SPACE_MAAVARON : Definitions.WALLA_SPACE_MAIN_SPLASH; //"n_interstitial"; //"n_slider";

            if (splashFromRichMedia) {
                space = Definitions.WALLA_SPACE_N_RM1;
                splashFromRichMedia = false;

                Log.e("alex", "showRichMedia on SplashScreen...");
            }

            if (map == null || !map.containsKey(space)) {
                onErrorLoadingSplashBanner();
                return;
            }

            Map<String, String> m = map.get(space);
            if (m == null) {
                onErrorLoadingSplashBanner();
                return;
            }

            String sViewReportURL = m.get("ADSRV") + m.get("VIEWREPORT");
            final String sClickReportURL = m.get("ADSRV") + m.get("CLICKREPORT");

            if (sClickReportURL == "") {
                onErrorLoadingSplashBanner();
                return;
            }

            if (sViewReportURL == "") {
                onErrorLoadingSplashBanner();
                return;
            } else {
                WallaAdvParser.sendWallaReportEvent(sViewReportURL);
            }

            String sContentType = m.get("CONTENTTYPE").toLowerCase();

            Log.e("alex", "countDownAndCloseSplash sContentType: " + sContentType);

            if (m.containsKey("DELAYTIME")) {
                SPLASH_DISPLAY_LENGTH = (Utils.tryParseInt(m.get("DELAYTIME"))) ? (Integer.parseInt(m.get("DELAYTIME")) * 1000) : SPLASH_DISPLAY_LENGTH;
            }

            //sContentType = "html"; //"anigif"; //test
            //sAdUrl = "http://x.walla.co.il:80/C6BDD2C8/E4489453.gif";

            Log.e("alex", "SplashScreenShowInterstatial ContentType Is: " + sContentType + "=== SPLASH_DISPLAY_LENGTH=" + SPLASH_DISPLAY_LENGTH);

            final ImageView wallaImage;
            boolean isImageSplash = false;
            final ImageView imageCloseView;
            final WebView webView_iframe_ad;

            wallaImage = new ImageView(SplashScreen.this);
            imageCloseView = new ImageView(SplashScreen.this);
            webView_iframe_ad = new WebView(SplashScreen.this);
            //Utils.centerViewInRelative(webView_iframe_ad, false);
            imageCloseView.setImageResource(R.drawable.close_ad);
            imageCloseView.setPadding(32, 32, 32, 32);
            imageCloseView.setVisibility(View.GONE);

            //dataStore.setShowAdMob(true);

            wallaImage.setVisibility(View.VISIBLE);
            webView_iframe_ad.setVisibility(View.VISIBLE);

            imageCloseView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isClosedImageClicked = true;

                    frameLayAdContainer.setVisibility(View.GONE);

                    startApp();
                    Log.e("alex", "countDownAndCloseSplash imageCloseView clicked!!!!!!!!!!!!");

                    isAdClicked = false;
                    closeSplashImmediately();
                    contentSwitcher.sendEmptyMessage(CLOSESPLASH);

                    //imageCloseView.setVisibility(View.GONE);
                    //webView_iframe_ad.setVisibility(View.GONE);
                }
            });

            webView_iframe_ad.setVerticalScrollBarEnabled(false);
            webView_iframe_ad.setHorizontalScrollBarEnabled(false);
            webView_iframe_ad.setWebChromeClient(new WebChromeClient());
            webView_iframe_ad.setPadding(0, 0, 0, 0);
            WebSettings webViewSettings = webView_iframe_ad.getSettings();
            webViewSettings.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
            webViewSettings.setJavaScriptCanOpenWindowsAutomatically(true);
            webViewSettings.setJavaScriptEnabled(true);
            webViewSettings.setUseWideViewPort(false);
            webViewSettings.setBuiltInZoomControls(false);
            webViewSettings.setLoadWithOverviewMode(false);
            webViewSettings.setSupportZoom(false);
            webViewSettings.setSupportMultipleWindows(true);
            //webView_iframe_ad.setVisibility(View.GONE);

            frameLayAdContainer.addView(webView_iframe_ad, FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
            frameLayAdContainer.addView(wallaImage, FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
            frameLayAdContainer.addView(imageCloseView, FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);

            if (sContentType.equals("image") || sContentType.equals("anigif")) {
                String sAdUrl = m.get("ADURL_01");
                //Log.e("alex", "GGGGG 2: sAdUrl " + sAdUrl);
                String sClickUrl = m.get("CLICKURL");

                //Log.e("alex", "SplashScreenShowInterstatial on SplashScreen AdUrl: " + sAdUrl);
                //Log.e("alex", "SplashScreenShowInterstatial on SplashScreen ClickUrl: " + sClickUrl);

                //Log.e("alex", "SplashScreenShowInterstatial on SplashScreen data: " + m);

                //sClickUrl = "http://www.google.com";

                if (sAdUrl == "" || sClickUrl == "") {
                    onErrorLoadingSplashBanner();
                    return;
                }

                isImageSplash = true;
                final Uri lnkToRedirect = Uri.parse(sClickUrl);

                if (!sAdUrl.endsWith(".gif") && !sAdUrl.endsWith(".png")) {
                    Picasso.with(SplashScreen.this).load(sAdUrl).fit().into(wallaImage, new Callback() {
                        @Override
                        public void onSuccess() {
                            Log.e("alex", "Walla Splash Success!");
                            imageCloseView.setVisibility(View.VISIBLE);
                            webView_iframe_ad.setVisibility(View.GONE);
                            isAdClicked = false;

                            Log.e("alex", "countDownAndCloseSplash 1...");

                            countDownAndCloseSplash();

                            wallaImage.setOnClickListener(new OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    isAdClicked = true;
                                    startApp();
                                    try {
                                        WallaAdvParser.sendWallaReportEvent(sClickReportURL);
                                    } catch (MalformedURLException e) {
                                        // TODO Auto-generated catch block
                                        e.printStackTrace();
                                    }
                                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, lnkToRedirect);
                                    startActivity(browserIntent);
                                }
                            });
                        }

                        @Override
                        public void onError() {
                            Log.e("alex", "Walla Spash Error");
                            onErrorLoadingSplashBanner();
                        }
                    });
                } else {
                    wallaImage.setVisibility(View.GONE);
                    imageCloseView.setVisibility(View.VISIBLE);
                    webView_iframe_ad.setVisibility(View.VISIBLE);

                    webView_iframe_ad.getSettings().setJavaScriptEnabled(true);
                    webView_iframe_ad.getSettings().setAppCacheEnabled(false);
                    webView_iframe_ad.loadDataWithBaseURL(null, "<!DOCTYPE html><html><body style = \"text-align:center\"><img style=\"width:99%;\" src= " + sAdUrl + " alt=\"page Not Found\"></body></html>", "text/html", "UTF-8", null);

                    Log.e("alex", "countDownAndCloseSplash 2...");

                    countDownAndCloseSplash();

                    webView_iframe_ad.setOnTouchListener(new View.OnTouchListener() {
                        @SuppressLint("ClickableViewAccessibility")
                        public boolean onTouch(View v, MotionEvent event) {
                            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                                isAdClicked = true;
                                startApp();

                                try {
                                    WallaAdvParser.sendWallaReportEvent(sClickReportURL);
                                } catch (MalformedURLException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }

                                Intent browserIntent = new Intent(Intent.ACTION_VIEW, lnkToRedirect);
                                startActivity(browserIntent);
                                return true;
                            }
                            return false;
                            // return (event.getAction() == MotionEvent.ACTION_MOVE);
                        }
                    });

                }
                Log.e("alex", "Walla Spash Loaded: " + sAdUrl);
            }

            if (sContentType.equals("html") || sContentType.equals("url")) {
                try {
                    isImageSplash = false;
                    String Payload = m.get("Payload");

                    //Payload = "<!doctype html> <html> <head><meta content=\"width=device-width,minimum-scale=1.0,maximum-scale=1.0,user-scalable=no\" name=\"viewport\"><style type=\"text/css\">html,body{    height:100%;}body{margin:0;}.center{display:table; overflow:hidden; width:100%; height:100%; text-align:center; vertical-align:middle;}.center>.wrapper{display:table-cell; vertical-align:middle;}.wrapper:before{}.wrapper:before,.wrapper:after{content:\"\"; display:block; height:50%;}</style></head>  <body> <div id=\"centerdiv\" class=\"center\">   <div class=\"wrapper\"><script type=\"text/javascript\">var fusionVars = {'ADID': '3830026636','ADTYPE': 'Interstitial','CONTENTTYPE':'Html','ADURL_01':'','WIDTH_01': '320','HEIGHT_01': '416','ORIENTATION':'Portrait','ALIGNMENT':'Center','CLICKURL':'','CLICKREPORT': 'event/xyvnd/mint.globes_app.capitalmarket/3830026636/click','ADTRANSITION': 'None','ADSERVER': 'http://fus.walla.co.il:80/','ADSRV': 'http://fus.walla.co.il/','VIEWREPORT': 'impression/xyvnd/mint.globes_app.capitalmarket/l_globes_app_c/3830026637','CLOSEBTNSHOW':'True','CLOSEBTNALIGNMENT':'TopLeft','CLOSEBTNDELAY':'0','CLOSEBTNURL':'','BGCOLOR':'000000','DELAYTIME':'6','EXTERNALBROWSER':'False','FULLSCREEN':'False'}if (fusionVars['CONTENTTYPE'] == \"Mraid\"){  document.getElementById(\"centerdiv\").style.height = \"0px\";}</script><!--advertiser tag goes here--><IFRAME SRC=\"http://ad.doubleclick.net/adi/N9940.109338WALLA.CO.IL/B8193802.110694681;sz=320x460;ord=[timestamp]?\" WIDTH=320 HEIGHT=460 MARGINWIDTH=0 MARGINHEIGHT=0 HSPACE=0 VSPACE=0 FRAMEBORDER=0 SCROLLING=no BORDERCOLOR='#000000'><SCRIPT language='JavaScript1.1' SRC=\"http://ad.doubleclick.net/adj/N9940.109338WALLA.CO.IL/B8193802.110694681;abr=!ie;sz=320x460;ord=[timestamp]?\"></SCRIPT><NOSCRIPT><A HREF=\"http://ad.doubleclick.net/jump/N9940.109338WALLA.CO.IL/B8193802.110694681;abr=!ie4;abr=!ie5;sz=320x460;ord=[timestamp]?\"><IMG SRC=\"http://ad.doubleclick.net/ad/N9940.109338WALLA.CO.IL/B8193802.110694681;abr=!ie4;abr=!ie5;sz=320x460;ord=[timestamp]?\" BORDER=0 WIDTH=320 HEIGHT=460 ALT=\"Advertisement\"></A></NOSCRIPT></IFRAME><!--ends here--></div></div></body></html>";

                    wallaImage.setVisibility(View.GONE);
                    imageCloseView.setVisibility(View.VISIBLE);
                    webView_iframe_ad.setVisibility(View.VISIBLE);

                    Log.e("alex", "NNNNN html:" + Payload);

                    webView_iframe_ad.setWebChromeClient(new WebChromeClient() {
                        @Override
                        public boolean onCreateWindow(WebView view, boolean dialog, boolean userGesture, android.os.Message resultMsg) {
                            WebView newWebView = new WebView(view.getContext());
                            view.addView(newWebView);
                            WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
                            newWebView.setWebViewClient(new WebViewClient() {
                                @Override
                                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                                    if (null != url) {
                                        try {
                                            Log.e("alex", "KKKKK:" + url);
                                            webView_iframe_ad.setVisibility(View.GONE);
                                            WallaAdvParser.sendWallaReportEvent(sClickReportURL);
                                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                                            startActivity(browserIntent);
                                        } catch (Exception ex) {
                                        }
                                    }
                                    return true;
                                }

                            });
                            transport.setWebView(newWebView);
                            resultMsg.sendToTarget();
                            return true;
                        }
                    });

                    webView_iframe_ad.loadDataWithBaseURL(null, Payload, "text/html", "utf-8", null);

                    Log.e("alex", "countDownAndCloseSplash 3...");

                    countDownAndCloseSplash();

                } catch (Exception ex) {
                    Log.e("alex", "GGGGG HTML Error: " + ex);
                }
            }

            if (sContentType.equals("mraid")) {
                Log.e("alex", "showRichMedia mraid...");
                SPLASH_DISPLAY_LENGTH = 1;
                isAdClicked = false;
                SplashScreen.this.finish();
                //countDownAndCloseSplash();
                return;
            }
        } catch (Exception ex) {
            Log.e("alex", "Walla Spash Global Error: " + ex);
            onErrorLoadingSplashBanner();
        }
    }
}
