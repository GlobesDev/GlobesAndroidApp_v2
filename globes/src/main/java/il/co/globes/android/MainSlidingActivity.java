package il.co.globes.android;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.MenuItem;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu.OnCloseListener;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu.OnOpenListener;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;
import com.sekindo.ads.SekindoSDK;
import com.squareup.picasso.Picasso;
import com.woorlds.woorldssdk.WoorldsSDK;
import il.co.globes.android.fragments.*;
import il.co.globes.android.interfaces.GlobesListener;
import il.co.globes.android.objects.ArticleSmallOpinion;
import il.co.globes.android.objects.CustomMenuItem;
import il.co.globes.android.objects.GlobesURL;
import il.co.globes.android.objects.NewsSet;
import net.tensera.sdk.api.TenseraApi;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

//import com.groboot.pushapps.PushManager;

public class MainSlidingActivity extends SlidingFragmentActivity implements GlobesListener {
    private static final String StackedBarBackColor = "#FFFFFF";
    private static final String BarBackColor = "#FFFFFF";

    // TAG
    private final static String TAG = MainSlidingActivity.class.getSimpleName();
    private static final int SHARE_PAGE = 5;

    // SlidingMenu / Action Bar
    private SlidingMenu sm;
    private ActionBar actionBar;
    private NewsSet newsSet;
    /**
     * If a fragment with a webview is loaded the fragment must set it's current
     * webview to this webview for handling back press navigation correctly
     */

    private WebView curFragmentWebView;

    // avoiding reloading the same fragment
    private String loadedFragment;

    // the current fragment
    private Fragment mContent;

    // the fragment transaction
    private FragmentTransaction transaction;

    // indicate if the commit is needed
    private boolean isCommitNeeded;

    /* Stack for loaded fragment state after back pressed */
    private Stack<String> stackLoadedFragment;

    // handler
    public static Handler mHandler;

    public static MainSlidingActivity getMainSlidingActivity() {
        return mainSlidingActivity;
    }

    // resources
    private Resources resources;

    // data store
    private DataStore dataStore;
    private static MainSlidingActivity mainSlidingActivity;

    // left menu ListOpinionsFragment
    private ListOpinionsFragment listOpinionsFragment;

    private AsyncTask<Void, Void, Void> createpictures;

    private String mActionBarTitle;
    private int[] imagesOfAutors =
            {R.drawable.m1, R.drawable.m2, R.drawable.m3, R.drawable.m4};
    Typeface almoni_aaa_blod;
    private boolean isMainPageLoading;
    private boolean isRegularMador;
    public static boolean isActive;

    WoorldsSDK mWoorldsSDK;
    private Handler mUpdateHandler;

    private static boolean isFirstLoading = false;
    private String currentDateandTime = "";

    //private SekindoBannerAdView bannerAdView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        currentDateandTime = sdf.format(new Date());

        //Log.e("alex","MainSlidingActivity onCreate");

        isMainPageLoading = true;
        isActive = true;
        isFirstLoading = false;

        // if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
        // {
        // getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        // }
        mActionBarTitle = getResources().getString(R.string.action_bar_analytics_default_title);

        mainSlidingActivity = this;
        // handler
        mHandler = new Handler(Looper.getMainLooper());
        // resources
        resources = getResources();

        // data store
        dataStore = DataStore.getInstance();

//		String sCurrentVersion = "0";
//		String sSettingsVersion = dataStore.getAppVersion();
//		
//		try
//		{
//			PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
//			sCurrentVersion = pInfo.versionName;
//		}
//		catch (NameNotFoundException e)
//		{
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//		if(sCurrentVersion != null && !sCurrentVersion.isEmpty() && sSettingsVersion != null && !sSettingsVersion.isEmpty())
//		{			
//			try
//			{
//				float iCurrentVersion = Float.parseFloat(sCurrentVersion) * 1000;
//				float iSettingsVersion = Float.parseFloat(sSettingsVersion) * 1000;
//				
//			    if(iSettingsVersion > iCurrentVersion)
//			    {
// 					mUpdateHandler = new Handler();
// 					checkVersionUpdates.start();
//			    }
//				
//				//Log.e("alex","BBBBBBB:" + iCurrentVersion + "===" + iSettingsVersion);	
//			}
//			catch(Exception ex){}
//			
//		}

        // set device details set in dataStore
        setDeviceDetailsInDataStore();

        Definitions.appActive = true;

        // set the Above View Main Fragment
        if (savedInstanceState != null) {
            mContent = getSupportFragmentManager().getFragment(savedInstanceState, "mContent");

            try {
                Log.e("alex", "Not MainFragment Loading..." + mContent.getClass().getSimpleName());
            }
            catch(Exception ex){}
        }
        if (mContent == null) {
            Log.e("alex", "MainFragment Loading...");
            MainFragment fragment = new MainFragment();
            Bundle args = new Bundle();
            args.putString(Definitions.CALLER, Definitions.MAINTABACTVITY);
            args.putString(Definitions.PARSER, Definitions.MAINSCREEN);
            args.putString(Definitions.URI_TO_PARSE, GlobesURL.URLMainScreen);
            fragment.setArguments(args);
            mContent = fragment;
            loadedFragment = MainFragment.class.getSimpleName();
            Log.e("alex", "MainSlidingActivity");
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, mContent, mContent.getClass().getSimpleName()).commit();

        // set the Above View
        setContentView(R.layout.sliding_menu_content_frame);
        almoni_aaa_blod = Typeface.createFromAsset(getAssets(), "almoni-dl-aaa-bold.otf");

        // customize the SlidingMenu
        sm = getSlidingMenu();
        sm.setShadowWidthRes(R.dimen.shadow_width);
        sm.setShadowDrawable(R.drawable.shadow);
        sm.setBehindOffsetRes(R.dimen.slidingmenu_offset);
        sm.setFadeDegree(0.35f);

        sm.setMode(SlidingMenu.LEFT_RIGHT);
        // TODO touch mode
        sm.setTouchModeAbove(SlidingMenu.SLIDING_WINDOW);
        // sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
        // customize the actionBar
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);

        listOpinionsFragment = new ListOpinionsFragment();
        createpictures();
        // init the stack
        stackLoadedFragment = new Stack<String>();

        // set open listenr
        sm.setOnOpenListener(new OnOpenListener() {

            @Override
            public void onOpen() {
                //Log.i("alex", "פתיחת תפריט דעות");
                Utils.writeEventToGoogleAnalytics(MainSlidingActivity.this, "עמוד הבית", "פתיחת תפריט דעות", null);
                listOpinionsFragment.refreshList();
            }
        });

        sm.setSecondaryOnOpenListner(new OnOpenListener() {
            @Override
            public void onOpen() {
                //Log.i("alex", "פתיחת תפריט");
                Utils.writeEventToGoogleAnalytics(MainSlidingActivity.this, "תפריט", "פתיחת תפריט", null);
            }
        });

        // createActionBarMenu();

        // set the Behind View
        setBehindContentView(R.layout.sliding_menu_menu_frame);
        getSupportFragmentManager().beginTransaction().replace(R.id.menu_frame, listOpinionsFragment).commit();

        // set secondary menu
        sm.setSecondaryMenu(R.layout.sliding_menu_secondary_menu_frame);

        getSupportFragmentManager().beginTransaction().replace(R.id.menu_secondary_frame, new MenuListFragment()).commit();

        // get intent data
        Intent intent = getIntent();
        if (intent != null) {

        }

        // set the Above View Main Fragment
//		if (savedInstanceState != null) mContent = getSupportFragmentManager().getFragment(savedInstanceState, "mContent");
//		if (mContent == null)
//		{
//			Log.e("alex", "MainFragment Loading...");
//			MainFragment fragment = new MainFragment();
//			Bundle args = new Bundle();
//			args.putString(Definitions.CALLER, Definitions.MAINTABACTVITY);
//			args.putString(Definitions.PARSER, Definitions.MAINSCREEN);
//			args.putString(Definitions.URI_TO_PARSE, GlobesURL.URLMainScreen);
//			fragment.setArguments(args);
//			mContent = fragment;
//			loadedFragment = MainFragment.class.getSimpleName();
//			Log.e("alex","MainSlidingActivity");
//		}
//		getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, mContent, mContent.getClass().getSimpleName()).commit();

        // set on Close listener
        getSlidingMenu().setOnCloseListener(new OnCloseListener() {

            @Override
            public void onClose() {
                // if need to commit the fragment commit it
                commitFragmentIfAvailable();
            }
        });

        // first time commit is not needed
        isCommitNeeded = false;

        //=======================Sekindo=========================//
        try {
            SekindoSDK.start(this);
        } catch (Exception ex) {
            Log.e("alex", "SekindoSDK loading error: " + ex);
        }

    }

    /**
     * Set Device details in {@code DataStore} for later use
     */
    private void setDeviceDetailsInDataStore() {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        dataStore.setScreenHeight(dm.heightPixels);
        dataStore.setScreenWidth(dm.widthPixels);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        String pushNotificationDocID = intent.getStringExtra(Definitions.KEY_BUNDLE_PUSH_NOTIFICATIONS_DOC_ID);
        if (pushNotificationDocID != null && !Definitions.PUSH_WAS_HANDLE) {
            Log.e("alex", "PushAppsTest 3: pushNotificationDocID=" + pushNotificationDocID);

            if (pushNotificationDocID.length() < 6) {
                String uri = GlobesURL.URLSections + pushNotificationDocID;  //node_id=4621
                SectionListFragment fragment = new SectionListFragment();
                Bundle args = new Bundle();
                args.putString(Definitions.URI_TO_PARSE, uri);
                args.putString(Definitions.CALLER, "Sections");
                args.putString(Definitions.PARSER, "MainScreen");
                args.putString("pushNotificationDocID", pushNotificationDocID);
                //args.putString(Definitions.HEADER, ((InnerGroup) parsedNewsSet.itemHolder.get(position)).getTitle());
                args.putString(Definitions.HEADER, Definitions.SPECIAL_FOLDER_FOR_PUSH);
                fragment.setArguments(args);
                // switch content

                MainSlidingActivity.getMainSlidingActivity().onSwitchContentFragment(fragment, SectionListFragment.class.getSimpleName(), true, false, false);
            } else {
                Intent i = new Intent(this, DocumentActivity_new.class);
                i.putExtra("pushNotificationDocID", pushNotificationDocID);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                Definitions.PUSH_WAS_HANDLE = true;
                startActivity(i);
            }
        }
        Log.e("alex", "onNewIntent MainSlidingActivity");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                toggle();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu) {
        getSupportMenuInflater().inflate(R.menu.main_sliding, menu);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isActive = false;
        Log.e("eli", "lll isActive = " + isActive);
        Definitions.appActive = false;
        createpictures.cancel(true);
        newsSet = null;
        GlobesURL.clearURLs();

        try {
//			 if (mWoorldsSDK != null) {
//				  String campaignId = mWoorldsSDK.getCampaign(); 
//				  Log.e("alex", "WoorldsSDK campaignId onDestroy:" + campaignId);
//				  		
//				  new WoorldsSDKInit(getApplicationContext()).destroy();
//				  mWoorldsSDK.destroy();
//		          mWoorldsSDK = null;		         
//		     }

            new WoorldsSDKInit(getApplicationContext()).destroy();
        } catch (Exception ex) {
        }
    }

    protected void startDocumentSearchListActivity() {
        Intent documentSearchListActivityIntent = new Intent(this, DocumentSearchListActivity.class);
        documentSearchListActivityIntent.putExtra("URI", GlobesURL.URLSearchArticle);
        documentSearchListActivityIntent.putExtra(Definitions.CALLER, Definitions.MAINSCREEN);
        documentSearchListActivityIntent.putExtra(Definitions.PARSER, Definitions.SEARCH);
        startActivity(documentSearchListActivityIntent);
    }

    /**
     * Commit fragment transaction if needed. This function is for the lag
     * created using slidingMenu with fragment , the fragment must be committed
     * after the menu closes
     */
    private void commitFragmentIfAvailable() {

        if (isCommitNeeded) {
            // reset value
            isCommitNeeded = false;

            // commit
            transaction.commit();
        }
    }

    @Override
    public void onSwitchContentFragment(Fragment fragment, String tag, boolean addToBackStack, boolean isLoadAfterSlidingMenuCloses,
                                        boolean isRegMador) {
        // TODO eli - in tik ishi
        switchContent(fragment, tag, addToBackStack, isLoadAfterSlidingMenuCloses, isRegMador);
    }

    /**
     * Called whenever is needed to switch content of the main screen (not the
     * list)
     *
     * @param fragment                     the fragment to to load
     * @param tag                          the name of the fragment
     * @param addToBackStack               boolean whether to add the fragment to the back stack
     * @param isLoadAfterSlidingMenuCloses - commit after menu closes
     */
    public void switchContent(Fragment fragment, String tag, boolean addToBackStack, boolean isLoadAfterSlidingMenuCloses,
                              boolean isRegularMador) {
        Log.e("alex", "CheckMador switchContent (MainSlidingActivity): " + tag);

        this.isRegularMador = isRegularMador;
        isMainPageLoading = tag.compareTo("MainFragment") == 0;
        if (isRegularMador && !isMainPageLoading) {
            Log.e("eli", "regular mador yes");
        }
        // TODO eli
        if (tag.compareTo("PortFolioFragment") == 0) {
            // Log.i("eli", "תיק אישי");
            Utils.writeEventToGoogleAnalytics(this, "תפריט", "פורטל פיננסי", "תיק אישי");
        }
        mContent = fragment;
        if (!tag.equals(loadedFragment) || !isLoadAfterSlidingMenuCloses) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            transaction = fragmentManager.beginTransaction();

            // add to loaded fragment backStack
            stackLoadedFragment.add(loadedFragment);

            // update value to current
            loadedFragment = tag;

            // replace transaction
            transaction.replace(R.id.content_frame, fragment, tag);

            // add to backstack
            transaction.addToBackStack(null);

            // loading after menu closes ?

            if (isLoadAfterSlidingMenuCloses) {
                isCommitNeeded = true;
            } else {
                /*
                 * if there is no need for loading fragment after the menu
				 * closes (Avoids loading lag) commit transaction now
				 */
                transaction.commitAllowingStateLoss();
            }
        }

        getSlidingMenu().showContent();
    }

    // override for fragment back behavior
    @Override
    public void onBackPressed() {
        if (isMainPageLoading) {
            isMainPageLoading = false;
        }

        if (curFragmentWebView != null && curFragmentWebView.canGoBack()) {
            curFragmentWebView.goBack();
        } else {
            FragmentManager fm = getSupportFragmentManager();

            if (fm.getBackStackEntryCount() == 1) {
                isMainPageLoading = true;
            }
            if (fm.getBackStackEntryCount() > 0) {
                Log.i(TAG, "popping backstack");
                fm.popBackStack();

                // update value if loaded fargment
                if (!stackLoadedFragment.isEmpty()) {
                    loadedFragment = stackLoadedFragment.pop();
                    isMainPageLoading = loadedFragment.compareTo("MainFragment") == 0;
                }
            } else {
                Log.i(TAG, "nothing on backstack, calling super");
                Log.e("alex", "EXXXXXIT");
                //showDialogLeaveApplication();

                if (checkIfShowLastPreloader() && SekindoSDK.isReady()) {
                    SekindoSDK.loadLastPreloader(this, Definitions.Sekindo_Unique_Space_Identifier);
                    MainSlidingActivity.this.finish();
                } else {
                    showDialogLeaveApplication();
                    Log.e("alex", "checkIfShowLastPreloader EXXXXXIT");
                }

//				if(SekindoSDK.isReady())
//				{
//					SekindoSDK.loadLastPreloader(this, Definitions.Sekindo_Unique_Space_Identifier);
//				}
//				else
//				{
//					Log.e("alex", "EXXXXXIT");
//				}
//				
//				MainSlidingActivity.this.finish();			
            }
        }
    }

    private boolean checkIfShowLastPreloader() {
        boolean bRes = true;

        try {
            SharedPreferences prefs = getApplication().getSharedPreferences(Definitions.Exit_Splash_Preference, getApplicationContext().MODE_PRIVATE);

//			SharedPreferences.Editor editor = prefs.edit();
//			editor.putString(Definitions.Exit_Splash_Preference, "20150801" + "_1");
//			editor.commit();

            String sData = prefs.getString(Definitions.Exit_Splash_Preference, currentDateandTime + "_1");
            String[] arrData = sData.split("_");

            if (arrData.length == 2) {
                String sDT = arrData[0];
                int iCount = Integer.parseInt(arrData[1]);

                Log.e("alex", "checkIfShowLastPreloader 1: " + sDT + "===" + iCount);

                if (currentDateandTime.equals(sDT)) {
                    if (iCount >= Definitions.Exit_Splash_Max_Show_Count) {
                        Log.e("alex", "checkIfShowLastPreloader 2: " + sDT + "===" + iCount);

                        return false;
                    } else {
                        Log.e("alex", "checkIfShowLastPreloader 3: " + sDT + "===" + iCount);

                        saveSharedPreference(prefs, String.valueOf(iCount + 1));
                    }
                } else {
                    Log.e("alex", "checkIfShowLastPreloader 4: " + sDT + "===" + iCount);

                    saveSharedPreference(prefs, "1");
                }
            }
        } catch (Exception ex) {
        }
        return bRes;
    }

    private void saveSharedPreference(SharedPreferences prefs, String Data) {
        SharedPreferences.Editor sh_editor = prefs.edit();
        sh_editor.putString(Definitions.Exit_Splash_Preference, currentDateandTime + "_" + Data);
        sh_editor.commit();
    }

    private boolean checkIsRegularMadorWhenBackpressed() {
        // Log.e("eli", "++++++++++++  Title = " + getActionBarTitle());
        return getActionBarTitle().compareTo("שוק ההון") == 0 || getActionBarTitle().compareTo("וול סטריט ושוקי עולם") == 0
                || getActionBarTitle().compareTo("נדל\"ן ותשתיות") == 0 || getActionBarTitle().compareTo("גלובס TV") == 0
                || getActionBarTitle().compareTo("היי-טק תקשורת ואינטרנט") == 0 || getActionBarTitle().compareTo("נתח שוק וצרכנות") == 0
                || getActionBarTitle().compareTo("דין וחשבון") == 0 || getActionBarTitle().compareTo("גז ונפט") == 0
                || getActionBarTitle().compareTo("קריירה ויזמות") == 0 || getActionBarTitle().compareTo("עסקי ספורט") == 0
                || getActionBarTitle().compareTo("פנאי") == 0 || getActionBarTitle().compareTo("רכב") == 0
                || getActionBarTitle().compareTo("דעות") == 0 || getActionBarTitle().compareTo("duns100") == 0
                || getActionBarTitle().compareTo("Feeder") == 0 || getActionBarTitle().compareTo("Sponser") == 0
                || getActionBarTitle().compareTo("שירות לקוחות") == 0 || getActionBarTitle().compareTo("redMail") == 0
                || getActionBarTitle().compareTo("שירות לקוחות") == 0;
    }

    /**
     * Show LeaveApplicationDialog
     */
    private void showDialogLeaveApplication() {
        AlertDialog.Builder builder = new Builder(this);
        builder.setCancelable(false).setTitle(R.string.dialog_leave_application_title).setMessage(R.string.dialog_leave_application_msg)
                .setPositiveButton(R.string.dialog_leave_application_ok, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // createpictures.cancel(true);
                        MainSlidingActivity.this.finish();
                        System.exit(0);

                    }
                });
        builder.setNegativeButton(R.string.dialog_leave_application_cancel, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.create().show();
    }

    // Callback from main right menu items
    @Override
    public void onMainRightMenuItemSelected(CustomMenuItem item) {
        if (!item.isFinancialLink) {
            // Log.i("eli", item.name);
            Utils.writeEventToGoogleAnalytics(this, "תפריט", "גלובס", item.name);
        } else {
            // Log.i("eli", item.name);
            Utils.writeEventToGoogleAnalytics(this, "תפריט", "פורטל פיננסי", item.name);
        }
        // financial portal
        if ((item.name).equals(resources.getString(R.string.financial_portal))) {
            // eli lunch financial_portal
            FinancialPortalFragment fragment = new FinancialPortalFragment();
            // load fragment
            fragment.setInitialURL(item.uriToParse);
            Bundle args = new Bundle();
            args.putString(Definitions.HEADER, item.name);
            fragment.setArguments(args);
            switchContent(fragment, FinancialPortalFragment.class.getSimpleName(), true, true, true);
        }
        // news
        else if ((item.name).equals(resources.getString(R.string.news))) {
            MainFragment fragment = new MainFragment();
            fragment.addArguments(item);
            // load fragment
            switchContent(fragment, MainFragment.class.getSimpleName(), true, true, true);
        } else if (item.isNodeWeb || item.isFinancialLink) {
            if (item.name.equals("duns100")) {
                Log.e("eli", "duns100");
                SectionListFragment fragment = new SectionListFragment();

                // add args
                Bundle args = new Bundle();
                args.putString(Definitions.CALLER, item.caller);
                args.putString(Definitions.PARSER, item.parser);
                args.putString(Definitions.URI_TO_PARSE, item.uriToParse.replaceAll("null", ""));
                args.putString(Definitions.HEADER, "Duns 100");
                fragment.setArguments(args);
                switchContent(fragment, item.name, true, true, true);
            } else if (item.name.equals("redMail")) {
                RedMailFragment fragment = new RedMailFragment();
                // load fragment
                switchContent(fragment, item.name, true, true, true);

            } else if (item.name.equals("הגדרות")) {

                Intent intent = new Intent(MainSlidingActivity.this, SettingsActivity.class);
                startActivity(intent);
            } else {
                if (item.name.compareTo("עזרה") == 0) {
                    WhatNewDialogFragment fragment = new WhatNewDialogFragment();
                    fragment.setCancelable(true);
                    fragment.setInitialURL(item.uriToParse);
                    Bundle args = new Bundle();
                    args.putString(Definitions.HEADER, item.name);
                    fragment.setArguments(args);

                    FragmentManager fragmentManager = getSupportFragmentManager();
                    transaction = fragmentManager.beginTransaction();
                    fragment.show(transaction, item.name);

                } else {
                    WebViewFragment fragment = new WebViewFragment();
                    fragment.setInitialURL(item.uriToParse);
                    Bundle args = new Bundle();
                    args.putString(Definitions.HEADER, item.name);
                    fragment.setArguments(args);
                    // load fragment
                    switchContent(fragment, item.name, true, true, true);
                }
            }

        }

        // about
        else if ((item.name).equals(resources.getString(R.string.about))) {
            AboutFragment fragment = new AboutFragment();

            // load fragment
            switchContent(fragment, AboutFragment.class.getSimpleName(), true, true, true);
        }
//		// app Definitions
//		else if ((item.name).equals(resources.getString(R.string.preferences)))
//		{
//			Intent intent = new Intent(this, GlobesPreferences.class);
//			startActivity(intent);
//		}

        else {
            SectionListFragment fragment = new SectionListFragment();

            // add args
            Bundle args = new Bundle();
            args.putString(Definitions.CALLER, item.caller);
            args.putString(Definitions.PARSER, item.parser);
            args.putString(Definitions.URI_TO_PARSE, item.uriToParse.replaceAll("null", ""));
            args.putString(Definitions.HEADER, item.name);
            fragment.setArguments(args);

            // load fragment
            switchContent(fragment, item.name, true, true, true);
        }

    }

    // callback from fragments with WebView
    @Override
    public void onSetFragmentWebview(WebView webView) {
        this.curFragmentWebView = webView;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((resultCode == RESULT_OK)) {
            if (requestCode == Definitions.REQUEST_CLIP_CAPTURE) {
                Uri uriVideo1 = data.getData();

                String test = getVideoAttachmentPath(uriVideo1);

                startRedEmail(test);
            } else {
                if (requestCode != SHARE_PAGE) {
                    String attachmentUri = data.getStringExtra("imageUri");
                    startRedEmail(attachmentUri);
                }
            }
        }
    }

    protected void startRedEmail(String attachmentUri) {
        String mailBody = "×�× ×� ×ž×œ×� ×�×ª ×”×¤×¨×˜×™×� ×”×‘×�×™×�:\n×©×�:\n×˜×œ×¤×•×Ÿ:\n×“×•×�×¨ ×�×œ×§×˜×¨×•× ×™:\n×ª×•×›×Ÿ:\n\n\n\n";
        Intent emailSend = new Intent();
        emailSend.setAction(Intent.ACTION_SEND);
        String mimeType = "message/rfc822";

        emailSend.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]
                {Definitions.RED_ALERT_MAIL_TARGET});

        // emailSend.putExtra(Intent.EXTRA_CC, R.string.redemail_globes_co_il);
        emailSend.putExtra(Intent.EXTRA_SUBJECT, "×“×•×�×¨ ×�×“×•×�");

        emailSend.putExtra(Intent.EXTRA_TEXT, mailBody);
        emailSend.putExtra(Intent.EXTRA_TITLE, R.string.red_email);
        if (attachmentUri.contains(".")) {
            emailSend.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + attachmentUri));
            mimeType = "image/jpeg";
        }
        emailSend.setType(mimeType);
        startActivity(Intent.createChooser(emailSend, getString(R.string.do_you_have_news)));
    }

    // get the name of the media from the Uri
    protected String getVideoAttachmentPath(Uri uri) {
        String filename = null;

        try {
            String[] projection =
                    {MediaStore.Images.Media.DATA};// DISPLAY_NAME
            Cursor cursor = managedQuery(uri, projection, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);// DISPLAY_NAME
                filename = cursor.getString(column_index);
            } else {
                filename = null;
            }
        } catch (Exception e) {
        }

        return filename;
    }

//    private InputStream OpenHttpConnection(String urlString) throws IOException {
//        InputStream in = null;
//        int response = -1;
//
//        URL url = new URL(urlString);
//
//        TenseraResponseStream tenseraResponseStream = null;
//        try {
//            tenseraResponseStream = TenseraApi.fetchUrl(url.toString(), new ArrayMap<String, String>(), CacheMode.CACHE_OTHERWISE_NETWORK);
//            if (tenseraResponseStream != null) {
//                if (tenseraResponseStream.responseCode == HTTP_OK) {
//                    in = tenseraResponseStream.stream;
//                }
//            }
//        } finally {
//            if (tenseraResponseStream != null) {
//                tenseraResponseStream.close();
//            }
//        }

//		URLConnection conn = url.openConnection();
//
//		if (!(conn instanceof HttpURLConnection)) throw new IOException("Not an HTTP connection");
//
//		try
//		{
//			HttpURLConnection httpConn = (HttpURLConnection) conn;
//			httpConn.setAllowUserInteraction(false);
//			httpConn.setInstanceFollowRedirects(true);
//			httpConn.setRequestMethod("GET");
//			httpConn.connect();
//			response = httpConn.getResponseCode();
//			if (response == HttpURLConnection.HTTP_OK)
//			{
//				in = httpConn.getInputStream();
//			}
//		}
//		catch (Exception ex)
//		{
//			throw new IOException("Error connecting");
//		}
//        return in;
//    }

//    private Bitmap DownloadImage(String URL) {
//        Bitmap bitmap = null;
//        InputStream in = null;
//        try {
//            in = OpenHttpConnection(URL);
//            bitmap = BitmapFactory.decodeStream(in);
//            in.close();
//        } catch (IOException e1) {
//            // TODO Auto-generated catch block
//            e1.printStackTrace();
//        }
//        return bitmap;
//    }

    /**
     * Creates a Custom ActionBar view
     */
    private void createActionBarMenu() {
        View view = getLayoutInflater().inflate(R.layout.actionbar_custom_view_image_title, null);
        final ImageView btn_home = (ImageView) view.findViewById(R.id.btn_home);
        ImageView imagViewRightIcon = (ImageView) view.findViewById(R.id.imageView_actionBar_right_icon);
        if (isMainPageLoading) {
            setLeftIconToOpinion(btn_home);
        } else {
            Picasso.with(this).load(R.drawable.btn_home).fit().into(btn_home);
            btn_home.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    loadMainHome();
                    isMainPageLoading = true;
                    // setLeftIconToOpinion(btn_home);
                }
            });
        }
        imagViewRightIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (sm.isSecondaryMenuShowing())
                    sm.showContent(true);
                else sm.showSecondaryMenu(true);
            }
        });
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor(BarBackColor)));
        actionBar.setStackedBackgroundDrawable(new ColorDrawable(Color.parseColor(StackedBarBackColor)));
        actionBar.setCustomView(view);
    }

    private void loadMainHome() {
        String name = getString(R.string.news);
        CustomMenuItem menuItem = new CustomMenuItem(name, -1, -1, -1, GlobesURL.URLMainScreen, Definitions.MAINSCREEN,
                Definitions.MAINTABACTVITY, null, false, null, false, false);
        MainFragment fragment = new MainFragment();
        fragment.addArguments(menuItem);
        // load fragment
        switchContent(fragment, MainFragment.class.getSimpleName(), false, false, true);
    }

    private void setLeftIconToOpinion(ImageView btn_home) {
        Random ran = new Random();
        int i = ran.nextInt(imagesOfAutors.length);
        Picasso.with(MainSlidingActivity.this).load(imagesOfAutors[i]).fit().into(btn_home);
        btn_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                toggle();
            }
        });
    }

    /**
     * Creates a Custom ActionBar view With text Title
     *
     * @param title the title to put
     */
    private void createActionBarMenuTextTitle(String title, int color) {
        title = title.compareTo("Duns 100") == 0 ? "" : title;
        View view = getLayoutInflater().inflate(R.layout.actionbar_custom_view_text_title, null);
        if (title.length() == 0) {
            view.setBackgroundDrawable(getResources().getDrawable(R.drawable.markofexcellence));
        }
        final ImageView btn_home = (ImageView) view.findViewById(R.id.btn_home);

        ImageView imagViewRightIcon = (ImageView) view.findViewById(R.id.imageView_actionBar_right_icon);
        TextView textViewTitle = (TextView) view.findViewById(R.id.textView_actionBar_title);
        textViewTitle.setTypeface(almoni_aaa_blod);
        textViewTitle.setText(title);
        textViewTitle.setTextColor(getResources().getColor(color));

        if (isMainPageLoading) {
            setLeftIconToOpinion(btn_home);
        } else {
            Picasso.with(this).load(R.drawable.btn_home).fit().into(btn_home);
            btn_home.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    loadMainHome();
                    isMainPageLoading = true;
                    // setLeftIconToOpinion(btn_home);
                }
            });
        }

        imagViewRightIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (sm.isSecondaryMenuShowing())
                    sm.showContent(true);
                else sm.showSecondaryMenu(true);
            }
        });
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor(BarBackColor)));
        actionBar.setStackedBackgroundDrawable(new ColorDrawable(Color.parseColor(StackedBarBackColor)));
        actionBar.setCustomView(view);
    }

    @Override
    public void onRedMailSendPicture() {
        openRedMailDialog(Definitions.SEND_PIC_DIALOG);
    }

    @Override
    public void onRedMailSendVideo() {
        openRedMailDialog(Definitions.SEND_CLIP_DIALOG);
    }

    /**
     * Show a Dialog related to redMail options
     *
     * @param dialogID Dialog type to show
     */
    private void openRedMailDialog(int dialogID) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        // dialog.getWindow().setGravity(Gravity.BOTTOM);
        dialog.setContentView(R.layout.red_email_dialog);
        TextView tvEmailTitle = (TextView) dialog.findViewById(R.id.tvEmailTitle);
        Button btnEmailWrite = (Button) dialog.findViewById(R.id.btnEmailWrite);
        Button btnRedEmailClip = (Button) dialog.findViewById(R.id.btnRedEmailClip);
        Button btnRedEmailPic = (Button) dialog.findViewById(R.id.btnRedEmailPic);

        switch (dialogID) {
            // clip dialog
            case Definitions.SEND_CLIP_DIALOG:
                btnEmailWrite.setVisibility(View.GONE);

                tvEmailTitle.setText(R.string.dialog_redMail_choose_video);
                btnRedEmailClip.setText(R.string.dialog_redMail_take_video);

                btnRedEmailClip.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        getAttachment(Definitions.CLIP_ATTACHMENT, Definitions.MAKE_ATTACHMENT);
                        dialog.dismiss();
                    }
                });

                btnRedEmailPic.setText(R.string.dialog_redMail_choose_from_gallery);
                btnRedEmailPic.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        getAttachment(Definitions.CLIP_ATTACHMENT, Definitions.CHOOSE_ATTACHMENT);
                        dialog.dismiss();
                    }
                });

                break;
            // pic dialog
            case Definitions.SEND_PIC_DIALOG:
                tvEmailTitle.setText(R.string.dialog_redMail_choose_pic);

                btnEmailWrite.setVisibility(View.GONE);
                btnRedEmailClip.setText(R.string.dialog_redMail_take_pic);
                btnRedEmailClip.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        getAttachment(Definitions.PIC_ATTACHMENT, Definitions.MAKE_ATTACHMENT);
                        dialog.dismiss();
                    }
                });

                btnRedEmailPic.setText(R.string.dialog_redMail_choose_from_gallery);
                btnRedEmailPic.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        getAttachment(Definitions.PIC_ATTACHMENT, Definitions.CHOOSE_ATTACHMENT);
                        dialog.dismiss();
                    }
                });
                break;

            default:
                break;
        }

        Button btnCancelDialog = (Button) dialog.findViewById(R.id.btnCancel);
        btnCancelDialog.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        // show dialog
        dialog.show();
    }

    private void getAttachment(int attachmentType, int getAttacmentMode) {
        if ((attachmentType == Definitions.CLIP_ATTACHMENT) && (getAttacmentMode == Definitions.MAKE_ATTACHMENT)) {
            Intent i = new Intent(android.provider.MediaStore.ACTION_VIDEO_CAPTURE);
            try {
                startActivityForResult(i, Definitions.REQUEST_CLIP_CAPTURE);
            } catch (ActivityNotFoundException ex) {
                Toast.makeText(this, "Your device does not contain an application to run this action", Toast.LENGTH_LONG).show();
            }
        } else {
            Intent getImage = new Intent(this, AttachmentPicker.class);
            getImage.putExtra("attachmentType", attachmentType);
            getImage.putExtra("getAttachmentMode", getAttacmentMode);
            startActivityForResult(getImage, 1010);
        }
    }

    @Override
    public void onSetDefaultActionBar() {
        mActionBarTitle = getResources().getString(R.string.action_bar_analytics_default_title);
        // Log.e("eli","onSetDefaultActionBar"+ mActionBarTitle);
        createActionBarMenu();
    }

    @Override
    public void onCloseGeneralSearch() {
        // clear search query
        // editTextSearchQuery.setText("");

        // Replace the search fragment with the menu fragment
        getSupportFragmentManager().beginTransaction().replace(R.id.menu_secondary_frame, new MenuListFragment()).commit();
    }

    @Override
    public SlidingMenu getsSlidingMenu() {
        return sm;
    }

    @Override
    public void onSetActionBarWithTextTitle(String title, int color) {
        // Log.e("eli","onSetActionBarWithTextTitle"+ title);
        mActionBarTitle = title;
        createActionBarMenuTextTitle(title, color);
    }

    public void createpictures() {
        //Log.e("alex","Createpictures");
        createpictures = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                URL url = null;
                // try
                // {
                try {
                    url = new URL(GlobesURL.URLTop50SpokenIdeas);
                } catch (MalformedURLException e) {

                }
                try {
                    //newsSet = NewsSet.parseURL(url, Definitions.LIST_OPINIONS_LEFT_MENU);
                } catch (Exception e) {

                }
                // }
                // catch (Exception e)
                // {
                // e.printStackTrace();
                // }
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                if (newsSet != null) {
                    ArrayList<ArticleSmallOpinion> articleSmallOpinionArrayList = new ArrayList<ArticleSmallOpinion>();
                    for (int i = 0; i < newsSet.itemHolder.size(); i++) {
                        articleSmallOpinionArrayList.add((ArticleSmallOpinion) newsSet.itemHolder.get(i));
                    }
                    dataStore.setArticleSmallOpinion(articleSmallOpinionArrayList);
                    createActionBarMenu();
                }
            }
        };
        createpictures.execute();
    }

    @Override
    protected void onStart() {
        Log.e("alex", "isMainPageLoading " + isMainPageLoading);
        if (isMainPageLoading && AppRegisterForPushApps.enableTensera) {
            TenseraApi.reportFeedView();
        }
        // EasyTracker.getInstance(this).activityStart(this);
        super.onStart();

        String sCurrentVersion = Utils.getVersionName(getApplicationContext());
        String sLastProductionVersion = DataStore.getInstance().getAppVersion();
        //mWoorldsSDK = new WoorldsSDKInit(getApplicationContext()).getInstance();

        // new WoorldsSDKInit(getApplicationContext()).getInstance();

        //Log.e("alex", "TTTTTTTTTTTTT " + sCurrentVersion + "===" + sLastProductionVersion);

        if (isMainPageLoading && !sCurrentVersion.equals(sLastProductionVersion)) {
            try {
                if (!isFirstLoading) {
                    isFirstLoading = true;

                    //Log.e("alex", "MandatoryUpdate 1: " + sCurrentVersion + "===" + sLastProductionVersion);

                    double dCurrentVersion = 0;
                    double dLastProductionVersion = 0;

                    if (Utils.tryParseDouble(sCurrentVersion) && Utils.tryParseDouble(sLastProductionVersion)) {
                        dCurrentVersion = Double.parseDouble(sCurrentVersion) * 1000;
                        dLastProductionVersion = Double.parseDouble(sLastProductionVersion) * 1000;

                        Log.e("alex", "MandatoryUpdate 2: " + dCurrentVersion + "===" + dLastProductionVersion);

                        if (dLastProductionVersion > dCurrentVersion) {
                            showMandatoryUpdateDialog();
                        }
                    }

                    Log.e("alex", "MandatoryUpdate15: " + sCurrentVersion + "===" + DataStore.getInstance().getAppVersion());
                }
            } catch (Exception ex) {
            }
        }

        try {
//				String campaignId = mWoorldsSDK.getCampaign();
//				long sWVersion = mWoorldsSDK.WOORLDS_SDK_IMPLEMENTATION_VERSION;

//			    String campaignId = new WoorldsSDKInit(getApplicationContext()).getGlobalCampaingID();
//			
//				Log.e("alex", "WoorldsSDK onStart CampaignId: " + campaignId);
            //List<Segment> segmentations = mWoorldsSDK.getSegmentations(campaignId);

            //Log.e("alex", "WoorldsSDK onStart segmentations: " + segmentations.size());

        } catch (Exception ex) {
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
//	     if (mWoorldsSDK == null) {
//	    	 Log.e("alex","mWoorldsSDK MainSliding onResume");
//	         mWoorldsSDK = new WoorldsSDK(this);
//	     }
    }

    @Override
    protected void onStop() {
        // EasyTracker.getInstance(this).activityStop(this);
        super.onStop();
        //Log.e("alex", "MainSlidingActivity Stop");

        //String campaignId = mWoorldsSDK.getCampaign();
        //long sWVersion = mWoorldsSDK.WOORLDS_SDK_IMPLEMENTATION_VERSION;

        //Log.e("alex", "WoorldsSDK onStop CampaignId: " + campaignId + "=== Version: " + sWVersion);
    }

    @Override
    public String getActionBarTitle() {
        // TODO Auto-generated method stub
        return mActionBarTitle;
    }

    @Override
    public void setRegularMador(boolean b) {
        isRegularMador = b;
    }

    @Override
    public boolean getIsRegularMador() {
        return isRegularMador || checkIsRegularMadorWhenBackpressed();
    }

    public boolean getIsMainPageLoading() {
        return isMainPageLoading;
    }

    private Thread checkVersionUpdates = new Thread() {
        public void run() {
            mUpdateHandler.post(showUpdate);
        }
    };

    private Runnable showUpdate = new Runnable() {
        public void run() {
            new AlertDialog.Builder(MainSlidingActivity.this)
                    .setIcon(R.drawable.edit_icon)
                    .setTitle("עדכון גירסה")
                    .setMessage("נמצאה גרסה חדשה. האם לעדכן?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getApplicationContext().getPackageName()));
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                        }
                    })
                    .show();
        }
    };

    private String getVersionName() {
        String versionName = "";
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            versionName = pInfo.versionName;
        } catch (NameNotFoundException e) {
            versionName = "";
        }
        return versionName;
    }

    private void checkIfShowMandatoryUpdateDialog_Old() {
        Log.e("alex", "checkIfShowMandatoryUpdateDialog");

        Date dLastMandatoryUpdateDialogShown = getLastMandatoryUpdateDialogShown();

        if (dLastMandatoryUpdateDialogShown == null) {
            showMandatoryUpdateDialog();
        } else {
            long diff = Math.abs(new Date().getTime() - dLastMandatoryUpdateDialogShown.getTime());
            long diffDays = diff / (24 * 60 * 60 * 1000);
            long diffHours = diff / (60 * 60 * 1000);
            long diffMin = diff / (60 * 1000);

            Log.e("alex", "Mandatory2 get HOURS: " + diffHours + "=== MINS: " + diffMin);

            if (diffMin > Definitions.MANDATORY_UPDATE_INTERVAL) {
                showMandatoryUpdateDialog();
            }
        }
    }

    private Date getLastMandatoryUpdateDialogShown() {

        String key = Definitions.MANDATORY_UPDATE_KEY;
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MainSlidingActivity.this.getApplicationContext());
        if (!prefs.contains(key + "_value")) {
            return null;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(prefs.getLong(key + "_value", 0));

        Log.e("alex", "Mandatory get from pref: " + calendar.getTime());

        return calendar.getTime();
    }

    private void putLastMandatoryUpdateDialogShownDate() {

        String key = Definitions.MANDATORY_UPDATE_KEY;
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MainSlidingActivity.this.getApplicationContext());
        Date date = new Date();

        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong(key + "_value", date.getTime());

        //Log.e("alex", "Mandatory set to pref: " + date.getTime());

        editor.commit();
    }

    private void showMandatoryUpdateDialog() {
        Log.e("alex", "showMandatoryUpdateDialog");

        AlertDialog.Builder alertadd = new AlertDialog.Builder(
                MainSlidingActivity.this);
        LayoutInflater factory = LayoutInflater.from(this);
        final View view = factory.inflate(R.layout.mandatory_update, null);
        alertadd.setView(view);
        alertadd.setNeutralButton("עדכן עכשיו", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dlg, int sumthin) {
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + MainSlidingActivity.this.getPackageName())));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id="
                            + MainSlidingActivity.this.getPackageName())));
                }
            }
        });

        alertadd.show();
    }

    private void showMandatoryUpdateDialog2() {
        putLastMandatoryUpdateDialogShownDate();
        AlertDialog.Builder builder = new AlertDialog.Builder(MainSlidingActivity.this);
        builder.setTitle("עדכון דחוף");
        builder.setMessage(Definitions.MANDATORY_UPDATE_MESSAGE).setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        try {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + MainSlidingActivity.this.getPackageName())));
                        } catch (android.content.ActivityNotFoundException anfe) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id="
                                    + MainSlidingActivity.this.getPackageName())));
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        return;
                    }
                });
        ;
        AlertDialog alert = builder.create();
        alert.show();

    }
}
