package il.co.globes.android.fragments;

import il.co.globes.android.AttachmentPicker;
import il.co.globes.android.CompanyActivity;
import il.co.globes.android.DataStore;
import il.co.globes.android.Definitions;
import il.co.globes.android.DocumentActivity_new;
import il.co.globes.android.LazyAdapter;
import il.co.globes.android.MainActivity;
import il.co.globes.android.MainSlidingActivity;
import il.co.globes.android.R;
import il.co.globes.android.RowAdActivity;
import il.co.globes.android.ShareActivity;
import il.co.globes.android.SettingsActivity;
import il.co.globes.android.SplashScreen;
import il.co.globes.android.Utils;
import il.co.globes.android.UtilsWebServices;
import il.co.globes.android.ima.v3.player.PlayerImaActivity;
import il.co.globes.android.interfaces.AbstractGlobesListFragment;
import il.co.globes.android.interfaces.GlobesListener;
import il.co.globes.android.interfaces.WallaAdvTaskCompleted;
import il.co.globes.android.native_video_player.PlayVideoActivity;
import il.co.globes.android.objects.AjillionObj;
import il.co.globes.android.objects.Article;
import il.co.globes.android.objects.Banner;
import il.co.globes.android.objects.CustomMenuItem;
import il.co.globes.android.objects.DataContext;
import il.co.globes.android.objects.GlobesURL;
import il.co.globes.android.objects.Group;
import il.co.globes.android.objects.InnerGroup;
import il.co.globes.android.objects.LiveBoxModel;
import il.co.globes.android.objects.NewsSet;
import il.co.globes.android.objects.RowArticleDuns100;
import il.co.globes.android.objects.RowCompanyDuns100;
import il.co.globes.android.objects.Ticker;
import il.co.globes.android.objects.TickerItem;
import il.co.globes.android.parsers.WallaAdvParser;
import il.co.globes.android.swipeListView.CopyOfSwipeListViewListener;
import il.co.globes.android.swipeListView.SwipeListView;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import net.tensera.sdk.api.TenseraApi;
import net.tensera.sdk.utils.Logger;
import org.apache.http.client.ClientProtocolException;

import tv.justad.sdk.view.JustAdView;
import tv.justad.sdk.view.JustAdViewDelegate;
//import uk.co.senab.actionbarpulltorefresh.extras.actionbarsherlock.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.doubleclick.PublisherAdView;
import com.google.android.gms.wearable.NodeApi.GetConnectedNodesResult;
import com.squareup.picasso.Picasso;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;

public class MainFragment extends AbstractGlobesListFragment
		implements
			uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener, WallaAdvTaskCompleted
{

	int testdayofYear;
	int tesatmainpage;
	boolean okDoLiveBoxe = false;

	private final static long REFRESH_LOOPER_ITEMS_INTERVAL = 7 * 60 * 1000; // 7
																				// minutes
	// private final static long REFRESH_LOOPER_ITEMS_INTERVAL = 10 * 1000; //
	// 10 sec
	private String name;
	private RelativeLayout frameAdViewContainer_Pull_to_refresh;
	private RelativeLayout frameTopAdViewContainer_Pull_to_refresh;
	public SwipeListView list;
	// ListView list;
	public LazyAdapter adapter;
	public NewsSet parsedNewsSet;
	public Activity activity;
	public String uriToParse;
	public String caller;
	public String parser;
	public Context context;
	public UUID unique;
	public Intent intent;
	public Bundle savedInstance;
	// google analytics tracker
	// GoogleAnalyticsTracker tracker;
	protected ProgressDialog pd;
	/** The view to show the ad. */
	// DfpAdView dfpAdView;
	public PublisherAdView dfpAdView;
	/** richMedia */
	public JustAdView jaAdView;
	/** The TAG. */
	private final String TAG = "MainActivity";
	/** Push dialog - first time showing */
	public AlertDialog alertDialog;
	/**
	 * Shared prefs and boolean for first time app start need to show push
	 * dialog
	 */
	public SharedPreferences sharedPrefs;
	/** push notifications & scheme */
	public String pushNotificationDocID, schemeURIShare;
	public boolean isFromScheme;
	public int showPushDialog;
	public int showMuteNotificationsDialog;
	public final int SHOW = 1;
	public final int DONTSHOW = 2;
	public static final int DONE_PARSING = 0;
	public static final int ERROR_PARSING = 1;
	public static final int DONE_GETTING_BANNER = 2;
	public static final int ERROR_GETTING_BANNER = 3;

	public Uri uriVideo = null;
	/** Ticker fields */
	private Ticker looper;
	private LayoutInflater mInflater;
	private LinearLayout horizontalOuterLayout;
	private HorizontalScrollView horizontalScrollview;
	private ImageButton btnOpenLooperPrefsActivity;
	private LinearLayout linearLayoutTickerContainer;
	private int scrollMax;
	private int scrollPos = 0;
	private TimerTask clickSchedule;
	private TimerTask scrollerSchedule;
	private TimerTask faceAnimationSchedule;
	private TimerTask refreshSchedule;
	private Timer refreshLooperItemsTimer = null;
	private Timer scrollTimer = null;
	private Timer clickTimer = null;
	private Timer faceTimer = null;
	private int reductionParam = 3;
	private Article articleGlobal;
	// callback
	public GlobesListener mCallback;
	protected PullToRefreshLayout mPullToRefreshLayout;
	LiveBoxModel liveBox = null;
	
	private RelativeLayout frameRichMedia;
	
	private String sWallaPage = "homepage";
	private String sWallaLayout = "l_globes_app_h";
	private boolean bRichMediaShown = false;
	public RelativeLayout rlTopHeaderBanner;
	
	private RelativeLayout frameWallaFusionDCScripts;
	
	private enum EncodeType
	{
		TYPE_BODY
	};
	protected Handler handler = new Handler()
	{
		@SuppressLint("NewApi")
		@Override
		public void handleMessage(Message msg)
		{
			if (msg.what == DONE_PARSING)
			{

				adapter = new LazyAdapter(activity, parsedNewsSet.itemHolder);
				adapter.setMainFragment(MainFragment.this);
				ArrayList<Article> documents = new ArrayList<Article>();
							
				for (int i = 0; i < parsedNewsSet.itemHolder.size(); i++)

				{
					if (parsedNewsSet.itemHolder.get(i) instanceof Article)
					{

						documents.add((Article) parsedNewsSet.itemHolder.get(i));
					}

				}
				DataStore.getInstance().setDocuments(documents);
				try
				{

					if (Definitions.toUseLiveBoxMain)
					{

						if (isMainMador())
						{

							Log.e("alex", "LLLLLLLLLLL1");
							
							liveBox = new LiveBoxModel();
							liveBox.setUrlLiveBoxMain(DataStore.getInstance().getLiveBoxMainURL());							
							
							View view = LayoutInflater.from(getActivity()).inflate(R.layout.row_layout_live_box, null);

							String articleTextHtml = liveBox.getUrlLiveBoxMain();
							try
							{
								articleTextHtml = encodeTextForWebView(articleTextHtml, EncodeType.TYPE_BODY);
							}
							catch (UnsupportedEncodingException e)
							{
								Log.e("eli", "erorr in setAllViews", e);
							}
							WebView webView = (WebView) view.findViewById(R.id.webView_live_box);
							WebSettings webViewSettings = webView.getSettings();
							webViewSettings.setJavaScriptCanOpenWindowsAutomatically(false);
							webViewSettings.setJavaScriptEnabled(true);
							webView.setWebChromeClient(new WebChromeClient());
							webViewSettings.setUseWideViewPort(false);
							webViewSettings.setBuiltInZoomControls(false);
							webViewSettings.setLoadWithOverviewMode(false);
							webViewSettings.setSupportZoom(false);
							webViewSettings.setPluginState(PluginState.ON);
							webView.setLayerType(WebView.LAYER_TYPE_SOFTWARE, null);

							webView.loadData(articleTextHtml, "text/html; charset=utf-8", "UTF-8");
							
							//Log.e("alex", "AAAAAAAAAAAAAAAAA : 10");
							
							if (list.getHeaderViewsCount() > 0)
							{
								list.removeViewAt(0);
							}
							list.addHeaderView(view);
						}
					}
				}
				catch (Exception e)
				{
				}
				list.setAdapter(adapter);
				/**
				 * Set a listener to be invoked when the list should be *
				 * refreshed -rotemm.
				 */
				try
				{
					pd.dismiss();
				}
				catch (Exception e)
				{
				}
			}
			else if (msg.what == ERROR_PARSING)
			{
				try
				{
					pd.dismiss();
				}
				catch (Exception e)
				{
					Log.e("eli", "error", e);
				}
				Toast toast = Toast.makeText(activity, Definitions.ErrorLoading, Toast.LENGTH_LONG);
				toast.show();
			}
		}
	};
	private String encodeTextForWebView(String html, EncodeType type) throws UnsupportedEncodingException
	{
		try
		{
			html = URLEncoder.encode(html, "utf-8").replaceAll("\\+", " ");
		}
		catch (UnsupportedEncodingException e)
		{
			Log.e("eli", "error 3097", e);
			html = "";
		}
		StringBuilder sb = new StringBuilder();
		sb.append("<html>");
		sb.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">");
		sb.append("<body style=\"direction:rtl;\">");
		switch (type)
		{

			case TYPE_BODY :
				sb.append("<span style=\"line-height:23px\">" + html.trim() + "</span>");
				break;
		}
		sb.append("</body>");
		sb.append("</html>");
		return sb.toString();
	}
	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);
		// This makes sure that the container activity has implemented
		// the callback interface. If not, it throws an exception
		try
		{
			mCallback = (GlobesListener) activity;
			this.activity = activity;
		}
		catch (ClassCastException e)
		{
			throw new ClassCastException(activity.toString() + " must implement " + GlobesListener.class.getSimpleName());
		}
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);		
		
		// get arguments
		Bundle args = getArguments();
		caller = args.getString(Definitions.CALLER);
		parser = args.getString(Definitions.PARSER);
		uriToParse = args.getString(Definitions.URI_TO_PARSE);
		
		//uriToParse = "http://www.globes.co.il/apps/apps.asmx/NodeDocumentsHeaders?node_id=4621"; //8297
	
		Log.e("alex", "CheckMador switchContent (MainFragment):" + uriToParse);

		try
		{
			if (!isMainMador() && (true || DataStore.getInstance().showAdMob()))
			{
					String[] arrWallaData = Utils.getWallaDataForFolderL(uriToParse);

					Log.e("alex", "SplashScreenShowInterstatial Before Starting Folder Splash..." + arrWallaData + "===" + arrWallaData[0]);

					if(arrWallaData[0] != null && !arrWallaData[0].equals(""))
					{
						sWallaPage = arrWallaData[0];
					}

					if(arrWallaData[1] != null && !arrWallaData[1].equals(""))
					{
						sWallaLayout = arrWallaData[1];
					}

					//Log.e("alex", "Walla Page: " +  sWallaPage + "=== Layout:" + sWallaLayout);

					Intent splashScreenIntent = new Intent(activity, SplashScreen.class);
					splashScreenIntent.putExtra("fromFolder", true);
					splashScreenIntent.putExtra("wallaPage", sWallaPage);
					splashScreenIntent.putExtra("wallaLayout", sWallaLayout);
					splashScreenIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(splashScreenIntent);
			}
		}
		catch(Exception ex){Log.e("alex", "MainFragment onCreate Error: " + ex);}
		
		Log.e("alex", "MainFragment: ");
		((SherlockFragmentActivity) activity).getSupportActionBar().setDisplayShowTitleEnabled(false);
		// TODO eli eli
		// setRetainInstance(true);
		// setHasOptionsMenu(true);

		unique = UUID.randomUUID();
		context = activity.getApplicationContext();
		looper = Ticker.getInstance(context);
		intent = activity.getIntent();
		// tracker = GoogleAnalyticsTracker.getInstance();

		// ****** shared prefs with dialogs ****//
		sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
		// ***************//
		/*
		 * push , get the pushNotificationDocID from intent will not be null
		 * only if pushed from splash
		 * 
		 * get values from scheme as well
		 */
		pushNotificationDocID = intent.getStringExtra(Definitions.KEY_BUNDLE_PUSH_NOTIFICATIONS_DOC_ID);
		schemeURIShare = intent.getStringExtra(Definitions.KEY_BUNDLE_SCHEME_URI_SHARE);
		isFromScheme = intent.getBooleanExtra(Definitions.SCHEME_COMING_FROM_SCHEME, false);
		
		
        //Log.e("alex","Parser3: " + uriToParse);
		
//		if (isMainMador() && !getVersionName().equals(DataStore.getInstance().getAppVersion()))
//		{
//			Log.e("alex", "MandatoryUpdate: " + getVersionName() + "===" + DataStore.getInstance().getAppVersion());
//			checkIfShowMandatoryUpdateDialog();
//		}
	};

	/**
	 * initializing the AdView + AdListener
	 */

	void initAdView(final View v)
	{
		Log.e("alex", "initAdView 111...");

		// Look up the DfpAdView as a resource and load a request.
	dfpAdView = (PublisherAdView) v.findViewById(R.id.adView);


		//dfpAdView.setAdUnitId("/43010785/globes/floating.globes.ros");

		dfpAdView.setAdSizes(new AdSize(320, 50),new AdSize(360, 50), AdSize.BANNER);

		//dfpAdView.setAdSizes(AdSize.SMART_BANNER);

		//frameAdViewContainer_Pull_to_refresh
		//dfpAdView.setAdSizes(AdSize.SMART_BANNER);

		dfpAdView.setAdListener(new AdListener()
		{
			public void onAdLoaded()
			{
				// Ad received
				Log.d(TAG, "Ad on main Activityreceived: ");
				dfpAdView.setBackgroundColor(Color.TRANSPARENT);
				if (dfpAdView.getVisibility() != View.VISIBLE)
				{
					dfpAdView.setVisibility(View.VISIBLE);
				}


				Log.e("alex", "MainPage Bottom DFP Banner data:" + dfpAdView.getAdUnitId() + "===" + dfpAdView.getAdSize());
			}
			public void onAdFailedToLoad(int errorCode)
			{
				// Didnt receive Ad
				Log.d(TAG, "Ad on main Activity Failed: " + errorCode);

				// remove the banner Ad
				dfpAdView.setVisibility(View.GONE);

				// if (tracker != null)
				// {
				// tracker.trackPageView("/" + TAG +
				// Definitions.AD_FAILURE_TAG_BOTTOM_BANNER_PORTRAIT +
				// errorCode);
				// }
			}
		});

		//int heightPixels = AdSize.SMART_BANNER.getHeightInPixels(context);
		//int widthPixels = AdSize.SMART_BANNER.getWidthInPixels(context);

		Log.e("alex","Dfp Bottom Floating Banner");

		PublisherAdRequest request = new PublisherAdRequest.Builder().build();
		//PublisherAdRequest request = new PublisherAdRequest.Builder().addTestDevice("7BBA10612964F988FEC2CB656AD07570").build();
		dfpAdView.loadAd(request);

		/**
		 * /7263/Android.462_100 /6253334/dfp_example_ad
		 * /7263/IPhone.middle_Banner
		 */
	}

		private void showBottomDFPBanner(final View v)
	{
		try {
			Log.e("alex", "showBottomDFPBanner start...");

			dfpAdView = (PublisherAdView) v.findViewById(R.id.adViewSectionListActivity);
			//dfpAdView.setAdSizes(new AdSize(320, 50),AdSize.BANNER, AdSize.SMART_BANNER);

			dfpAdView.setAdSizes(AdSize.BANNER);

			dfpAdView.setAdListener(new AdListener() {
				public void onAdLoaded() {
					Log.d(TAG, "Ad on main Activityreceived: ");

					Log.e("alex", "showBottomDFPBanner onAdLoaded");

					dfpAdView.setBackgroundColor(Color.TRANSPARENT);
					if (dfpAdView.getVisibility() != View.VISIBLE) {
						dfpAdView.setVisibility(View.VISIBLE);
					}
				}

				public void onAdFailedToLoad(int errorCode) {
					Log.d(TAG, "Ad on main Activity Failed: " + errorCode);

					Log.e("alex", "showBottomDFPBanner onAdFailedToLoad Error:" + errorCode + " Banner data: " + dfpAdView.getAdUnitId() + "===" + dfpAdView.getAdSize());

					frameAdViewContainer_Pull_to_refresh = (RelativeLayout) getView().findViewById(R.id.frameAdViewContainer_Pull_to_refresh);
					frameAdViewContainer_Pull_to_refresh.setVisibility(View.GONE);

					dfpAdView.setVisibility(View.GONE);
				}
			});

			PublisherAdRequest request = new PublisherAdRequest.Builder().build();
			dfpAdView.loadAd(request);
		}
		catch(Exception ex){
			frameAdViewContainer_Pull_to_refresh = (RelativeLayout) getView().findViewById(R.id.frameAdViewContainer_Pull_to_refresh);
			frameAdViewContainer_Pull_to_refresh.setVisibility(View.GONE);
		}
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();
		if (adapter != null && list != null)
		{
			adapter.imageLoader.clearCache();
			adapter.imageLoader.stopThread();
		}

		if (this.getClass().equals(MainActivity.class))
		{
			// Clear timers and Timer Tasks
			clearTimersAndTasks();
		}

		handler.removeCallbacksAndMessages(null);
	}

	private void clearTimersAndTasks()
	{
		// Ticker params
		clearTimerTaks(refreshSchedule);
		clearTimerTaks(clickSchedule);
		clearTimerTaks(scrollerSchedule);
		clearTimerTaks(faceAnimationSchedule);
		clearTimers(scrollTimer);
		clearTimers(clickTimer);
		clearTimers(faceTimer);
		clearTimers(refreshLooperItemsTimer);

		refreshSchedule = null;
		clickSchedule = null;
		scrollerSchedule = null;
		faceAnimationSchedule = null;
		scrollTimer = null;
		clickTimer = null;
		faceTimer = null;
	}

	private void clearTimers(Timer timer)
	{
		if (timer != null)
		{
			timer.cancel();
			timer = null;
		}
	}

	private void clearTimerTaks(TimerTask timerTask)
	{
		if (timerTask != null)
		{
			timerTask.cancel();
			timerTask = null;
		}
	}

	// protected void startDocumentSearchListActivity()
	// {
	// Intent documentSearchListActivityIntent = new Intent(activity,
	// DocumentSearchListActivity.class);
	// documentSearchListActivityIntent.putExtra("URI",
	// GlobesURL.URLSearchArticle);
	// documentSearchListActivityIntent.putExtra(Definitions.CALLER,
	// Definitions.MAINSCREEN);
	// documentSearchListActivityIntent.putExtra(Definitions.PARSER,
	// Definitions.SEARCH);
	// startActivity(documentSearchListActivityIntent);
	// }
	//
	// protected void startTVActivity()
	// {
	// Intent openClipsList = new Intent(activity, SectionListActivity.class);
	// Log.e("eli", "startTVActivity");
	// openClipsList.putExtra("URI", GlobesURL.URLClips);
	// openClipsList.putExtra(Definitions.CALLER, Definitions.SECTIONS);
	// openClipsList.putExtra(Definitions.PARSER, Definitions.MAINSCREEN);
	// openClipsList.putExtra(Definitions.HEADER, "×’×œ×•×‘×¡ TV");
	// startActivity(openClipsList);
	// }
	
	@Override
	public android.view.View onCreateView(LayoutInflater inflater, android.view.ViewGroup container, Bundle savedInstanceState)
	{
		mInflater = inflater;
		View v = inflater.inflate(il.co.globes.android.R.layout.copy_of_pull_to_refresh, container, false);
		return v;
	};

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState)
	{
		Log.e("eli", "onViewCreated");
		super.onViewCreated(view, savedInstanceState);
		// This is the View which is created by ListFragment
		ViewGroup viewGroup = (ViewGroup) view;

		// We need to create a PullToRefreshLayout manually
		mPullToRefreshLayout = new PullToRefreshLayout(viewGroup.getContext());

		// We can now setup the PullToRefreshLayout
		ActionBarPullToRefresh.from(activity)

		// We need to insert the PullToRefreshLayout into the Fragment's
		// ViewGroup
				.insertLayoutInto(viewGroup)

				// We need to mark the ListView and it's Empty View as pullable
				// This is because they are not dirent children of the ViewGroup
				.theseChildrenArePullable(getListView(), getListView().getEmptyView())

				// Set the OnRefreshListener
				.listener(this)
				// Finally commit the setup to our PullToRefreshLayout
				.setup(mPullToRefreshLayout);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{

		super.onActivityCreated(savedInstanceState);
		Log.e("eli", "onActivityCreated");

		// activity = activity;
		// push dialog values

		// case first time , no value so it will receive SHOW
		showPushDialog = sharedPrefs.getInt("showPushDialog", SHOW);
		if (showPushDialog == SHOW)
		{
			// save the value for DONTSHOW for next time
			savePrefsIntValue("showPushDialog", DONTSHOW);

		}
		// handling the mutNotifications dialog
		showMuteNotificationsDialog = sharedPrefs.getInt("showMuteNotificationsDialog", SHOW);
		if (showMuteNotificationsDialog == SHOW)
		{
			// save the value for DONTSHOW for next time
			savePrefsIntValue("showMuteNotificationsDialog", DONTSHOW);
		}

		if (this.getClass().equals(MainFragment.class))
		{
			// set default action Bar
			mCallback.onSetDefaultActionBar();

			initLooperUi(getView());
		}
		
		setInitialData(uriToParse, parser, getView());
		
		// if (Definitions.toUseLiveBoxMain)
		// {
		//
		// new AjillionHandle().execute();
		// }
		// else
		// {
		//
		// setInitialData(uriToParse, parser, getView());
		// }
		if (isMainMador())
		{

			Calendar now = Calendar.getInstance();
			testdayofYear = sharedPrefs.getInt("dayofYear", 0);
			tesatmainpage = sharedPrefs.getInt("mainpage", 0);
			if (tesatmainpage != 3)
			{
				tesatmainpage = tesatmainpage + 1;
				sharedPrefs.edit().putInt("mainpage", tesatmainpage).commit();

			}
			else
			{
				if (now.get(Calendar.DAY_OF_YEAR) != testdayofYear)
				{
					okDoLiveBoxe = true;
					sharedPrefs.edit().putInt("dayofYear", now.get(Calendar.DAY_OF_YEAR)).commit();

					sharedPrefs.edit().putBoolean("okDoLiveBoxe", true).commit();

				}
			}
		}

		okDoLiveBoxe = sharedPrefs.getBoolean("okDoLiveBoxe", false);
		if (Definitions.toUseLiveBoxMain && !okDoLiveBoxe)
		{			
			Log.e("alex", "LiveBoxMain: " + Definitions.toUseLiveBoxMain);
			//setInitialData(uriToParse, parser, getView());
		}
		else
		{
			if (Definitions.toUseAjillion)
			{
				new AjillionHandle().execute();
				Log.e("alex", "AAAAAAAAAAAAAAAAAB : 1");
			}
		}
		
		if(Definitions.wallafusion)
		{
			/////////////////////WALLA TEST///////////////////////////////////			
					
			try
			{
				String[] arrWallaData = Utils.getWallaDataForFolderL(uriToParse);
				
				if(arrWallaData[0] != null && arrWallaData[0] != "")
				{
					sWallaPage = arrWallaData[0];
				}
				
				if(arrWallaData[1] != null && arrWallaData[1] != "")
				{
					sWallaLayout = arrWallaData[1];
				}			
				
				//Log.e("alex", "MainFolderWalla:" + sWappaPage + "===" + sWallaLayout);
							
			}
			catch (Exception e)
			{
				Log.e("alex", "FOLDERID Error:" + e);
				e.printStackTrace();
			}			
				
			WallaAdvParser wap = new WallaAdvParser(context, activity.getApplication(), this, sWallaPage, sWallaLayout, "", 0);
			wap.execute();
			//////////////////////////////////////////////////////////////////
		}
		else
		{
			if (Definitions.toUseAjillion)
			{
				new AjillionHandleDocument().execute();
				new initAjillionView(activity).execute();
			}
			else
			{
				initAdView(getView());

				if(!isMainMador())
				{
					showBottomDFPBanner(getView());
				}
			}
		}
		
		

		//new AjillionHandleDocument().execute();
		
		//Log.e("alex", "AAAAAAAAAAAAAAAAA : 2");

		/** init banner dfpAdView */
		// eli dfp

//		if (Definitions.toUseAjillion)
//		{
//			new initAjillionView(activity).execute();
//		}
//		else
//		{
//
//			initAdView(getView());
//		}

		// a call to init the rich media has been canceld due to Alon's request
		// on the 25.8.13
		// initRichMedia();
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id)
	{
		super.onListItemClick(l, v, position, id);        
	}

	@Override
	public void onPause()
	{
		super.onPause();

	}

	private void stopAutoScrolling()
	{
		if (scrollTimer != null)
		{
			scrollTimer.cancel();
			scrollTimer = null;
		}
	}

	@Override
	public void onStart()
	{
		if (mCallback.getActionBarTitle().compareTo("Globes Default") == 0)
			Utils.writeScreenToGoogleAnalytics(activity,
					activity.getResources().getString(R.string.il_co_globes_android_fragments_MainFragment));

		super.onStart();

	}

	@Override
	public void onStop()
	{
		super.onStop();
		if (this.getClass().equals(MainFragment.class))
		{
			stopAutoScrolling();
		}
	}

	@SuppressLint("NewApi")
	@Override
	public void onResume()
	{
		super.onResume();
		Log.e("eli", "onResume");

		if (this.getClass().equals(MainFragment.class))
		{
			addLooperItemsToView();
		}

		if (getSherlockActivity().getSupportActionBar() != null && !getSherlockActivity().getSupportActionBar().isShowing())
		{
			getSherlockActivity().getSupportActionBar().show();
		}
	}

	void setInitialData(final String uriToParse, final String parser, final View v)
	{
		Log.e("alex", "setInitialData: " + uriToParse);
		//Log.e("alex", "parser: " + parser);
		
		new AsyncTask<Void, Void, Integer>()
		{
			@Override
			protected void onPreExecute()
			{
				pd = ProgressDialog.show(activity, "", Definitions.Loading, true, false);
				list = (SwipeListView) v.findViewById(android.R.id.list);
			};
			@Override
			protected Integer doInBackground(Void... params)
			{
				int res;
				try
				{
					URL url = new URL(uriToParse);
										
					parsedNewsSet = NewsSet.parseURL(url, parser);
					//Log.e("alex", "ParsedNewsSet size:" + uriToParse);
					res = DONE_PARSING;
				}
				catch (Exception e)
				{
					Log.e("alex", "Error Parsing: " + uriToParse + " ERROR: " + e.toString());
					res = ERROR_PARSING;
				}
				return res;
			}

			@SuppressLint("NewApi")
			@Override
			protected void onPostExecute(Integer result)
			{
				try
				{
					pd.dismiss();
					Log.e("eli", "   dismiss....");
				}
				catch (Exception e)
				{
					Log.e("eli", "error", e);
				}
				if (result == DONE_PARSING)
				{
					/*
					 * on parsing success set a chance to manipulate data before
					 * showing it
					 */								
					
					onNewsSetParsed(uriToParse, parser);
					if (caller.compareTo("Sections") == 0 && getFirstEmpty() != -1)
					{
						parsedNewsSet.itemHolder.add(getFirstEmpty(), new Group("חברות מובילות"));
					}
					// Log.e("eli", "main fragment 806");

					adapter = new LazyAdapter(activity, parsedNewsSet.itemHolder);
					ArrayList<Article> documents = new ArrayList<Article>();
					for (int i = 0; i < parsedNewsSet.itemHolder.size(); i++)

					{
						if (parsedNewsSet.itemHolder.get(i) instanceof Article)
						{
							documents.add((Article) parsedNewsSet.itemHolder.get(i));
						}

					}
					
					//Log.e("alex", "Documents count: " + documents.size());

					DataStore.getInstance().setDocuments(documents);
					adapter.setMainFragment(MainFragment.this);
					try
					{
//						if(Definitions.wallafusion && isMainMador())
//						{
//							Log.e("alex", "AAAB000122");
//							//View view = LayoutInflater.from(getActivity()).inflate(R.layout.row_layout_live_box, null);
//							
//							View view = LayoutInflater.from(getActivity()).inflate(R.layout.layout_splash_ad_in_document_activity, null);
//							
//							//rlTopHeaderBanner = (RelativeLayout)view.findViewById(R.id.topHeaderBanner);
//							rlTopHeaderBanner = (RelativeLayout)view.findViewById(R.id.relativeLayout_splashArticle_container_layout_splash_ad);
//							rlTopHeaderBanner.setVisibility(View.GONE);
////							if (list.getHeaderViewsCount() > 0)
////							{
////								list.removeViewAt(0);
////							}
//							
//							parsedNewsSet.itemHolder.add(1, new Banner());
//							list.addHeaderView(view);
//							parsedNewsSet.itemHolder.add(getFirstEmpty(), new Banner());
//							
//						}
						
						if (Definitions.toUseLiveBoxMain)
						{
							if (isMainMador() && okDoLiveBoxe)

							{
								Calendar now = Calendar.getInstance();

								sharedPrefs.edit().putInt("dayofYear", now.get(Calendar.DAY_OF_YEAR)).commit();
								sharedPrefs.edit().putInt("mainpage", 0).commit();
								okDoLiveBoxe = false;
								sharedPrefs.edit().putBoolean("okDoLiveBoxe", false).commit();

								liveBox = new LiveBoxModel();
								liveBox.setUrlLiveBoxMain(DataStore.getInstance().getLiveBoxMainURL());
								View view = LayoutInflater.from(getActivity()).inflate(R.layout.row_layout_live_box, null);								
								
								String articleTextHtml = liveBox.getUrlLiveBoxMain();
								try
								{
									articleTextHtml = encodeTextForWebView(articleTextHtml, EncodeType.TYPE_BODY);
								}
								catch (UnsupportedEncodingException e)
								{
									Log.e("eli", "erorr in setAllViews", e);
								}
								WebView webView = (WebView) view.findViewById(R.id.webView_live_box);
								WebSettings webViewSettings = webView.getSettings();
								webViewSettings.setJavaScriptCanOpenWindowsAutomatically(false);
								webViewSettings.setJavaScriptEnabled(true);
								webView.setWebChromeClient(new WebChromeClient());
								webViewSettings.setUseWideViewPort(false);
								webViewSettings.setBuiltInZoomControls(false);
								webViewSettings.setLoadWithOverviewMode(false);
								webViewSettings.setSupportZoom(false);
								webViewSettings.setPluginState(PluginState.ON);
								webView.setLayerType(WebView.LAYER_TYPE_SOFTWARE, null);

								webView.loadData(articleTextHtml, "text/html; charset=utf-8", "UTF-8");
								if (list.getHeaderViewsCount() > 0)
								{

									list.removeViewAt(0);
								}
								list.addHeaderView(view);
								//Log.e("alex", "AAAAAAAAAAAAAAAAA : 11");
							}
						}
					}
					catch (Exception e)
					{
						// TODO: handle exception
					}

					list.setAdapter(adapter);
					list.setSwipeListViewListener(new CopyOfSwipeListViewListener()
					{

						@Override
						public void onClickFrontView(int position)
						{							
							// position--;
							
							if (parsedNewsSet.itemHolder.get(position) instanceof RowArticleDuns100)
							{
								// RowArticleDuns100 articleDuns100 =
								// (RowArticleDuns100)
								// parsedNewsSet.itemHolder.get(position);
								// ArticleDuns100Fragment fragment = new
								// ArticleDuns100Fragment();
								// Bundle args = new Bundle();
								// //
								// "http://stage.globes.co.il/data/webservices/duns100.asmx/duns_Article?doc_id="
								// args.putString(Definitions.URI_TO_PARSE,
								// GlobesURL.URLDunsSingleArticle + "doc_id=" +
								// articleDuns100.getDoc_id());
								// args.putString(Definitions.CALLER, caller);
								// args.putString(Definitions.PARSER,
								// Definitions.ARTICLE_DUNS_100);
								// // args.putString(Definitions.HEADER,
								// // ((InnerGroup)
								// //
								// parsedNewsSet.itemHolder.get(position)).getTitle());
								// fragment.setArguments(args);
								// // switch content
								// mCallback.onSwitchContentFragment(fragment,
								// ArticleDuns100Fragment.class.getSimpleName(),
								// true, false);

								Intent intent = new Intent(activity, DocumentActivity_new.class);
								DataContext.Instance().SetArticleList(unique.toString(), parsedNewsSet);
								intent.putExtra("RowType", "RowArticleDuns100");
								intent.putExtra("position", position);
								intent.putExtra("parentId", unique.toString());
								startActivity(intent);
							}
							else if (parsedNewsSet.itemHolder.get(position) instanceof Article)
							{
								// get instance of the article
								Article article = (Article) parsedNewsSet.itemHolder.get(position);
								articleGlobal = (Article) parsedNewsSet.itemHolder.get(position);

								Log.e("alex", "article click *******");
								//Log.e("alex", "DynastyIDs: " + article.getCanonicalDynastyIds());
								
								if(article.isGlobesMetzig || article.isSponseredArticle)
								{
									//Log.e("alex", "metzig get url: " + article.getExternalURL());
									
									Intent i = new Intent(Intent.ACTION_VIEW);
									i.setData(Uri.parse(article.getExternalURL()));
									startActivity(i);
									return;
								}
								
								if(article.getCanonicalDynastyIds() != null && article.getCanonicalDynastyIds().contains(String.valueOf(Definitions.MarketingContentFolder)))
								{									
									Intent i = new Intent(Intent.ACTION_VIEW);								
									i.setData(Uri.parse(GlobesURL.URLDocumentToShare + article.getDoc_id()));
									startActivity(i);
									Utils.writeEventToGoogleAnalytics(getActivity(), "כתבות על הקלקות", "article_node_" + getTitleActionBar(), null);
									return;
								}
								
								// Log.e("eli", "is regular mdor = " +
								// isRegularMador());
								if (isRegularMador())
								{
									Utils.writeEventToGoogleAnalytics(getActivity(), "כתבות על הקלקות", "article_node_"
											+ getTitleActionBar(), null);
								}
								else if (isTagitMador())
								{
									Utils.writeEventToGoogleAnalytics(getActivity(), "כתבות על הקלקות", "article_tagit_"
											+ getTitleActionBar(), null);
								}
								else if (isMainMador())
								{
									if (article.getGroup() != null)
									{
										if (!article.getGroup().equals(""))
										{
											Utils.writeEventToGoogleAnalytics(getActivity(), "כתבות על הקלקות", "article_section_"
													+ article.getGroup(), null);
										}
										else
										{
											Utils.writeEventToGoogleAnalytics(getActivity(), "כתבות על הקלקות", "article_section_ראשי",
													null);
										}
									}
								}

								// check here if article has LINK field and open
								// the link in
								// webView or browser. the LINK field should be
								// set via MAINParser
								// at the article object
								// This is katava pirsumit and need to be opened
								// in Browser
								String url = article.getUrlMarketingArticle();
								// url from regular video & html5 video
								String videoURL = article.getUrl();
								String videoURLhtml5 = article.getUrlHTML5();


								//Log.e("alex", "900 videoURL  http://globes.co.il/news/article.aspx?did=" + article.getDoc_id());
								//Log.e("alex", "Video url: " +videoURL);
								
								
								if (url != null && url.length() > 0)
								{
									if (!(url.contains("http")) && !(url.contains("HTTP")))
									{
										url = "http://www.globes.co.il";
									}
									String openInWeb = "";
									int indexOfOpenWeb = url.indexOf("Open_web=");
									
									if (indexOfOpenWeb != -1) openInWeb = new String(url.substring(indexOfOpenWeb + 9));

									if (openInWeb.equals("true"))
									{
										// open in browser
										Intent i = new Intent(Intent.ACTION_VIEW);
										i.setData(Uri.parse(url));
										startActivity(i);
										Log.e("eli", "video 1");

									}
									else if (openInWeb.equals("false"))
									{
										// open in WebView via RowAdActivity
										Intent i = new Intent(activity, RowAdActivity.class);
										i.putExtra(RowAdActivity.KEY_URL_AD, url);
										startActivity(i);
									}
								}
								// if article is a video/video HTML5 article
								// open straight ahead
								else if (!Definitions.isOpenVideoAsArticle && ((videoURL != null && videoURL.length() > 0)
										|| (videoURLhtml5 != null && videoURLhtml5.length() > 0)))
								{
									boolean isHTML5Video = article.isHTML5video();
									
									Log.e("alex", "isHTML5Video: " +isHTML5Video + " Definitions.isOpenVideoAsArticle: " + Definitions.isOpenVideoAsArticle);
									
									if (isHTML5Video)
									{
										openVideoInMediaPlayerOrBrowser(videoURLhtml5, true, article.getDoc_id());
										Log.e("eli", "video 2");
									}
									else
									{
										openVideoInMediaPlayerOrBrowser(videoURL, false, article.getDoc_id());
										if (MainSlidingActivity.getMainSlidingActivity().getActionBarTitle().compareTo("Globes Default") == 0)
										{
											Utils.writeEventToGoogleAnalytics(getActivity(), "ראשי עמוד", "סרטון",
													Utils.getSubStringByValue(videoURL, "movId"));
										}
										else if (MainSlidingActivity.getMainSlidingActivity().getActionBarTitle().compareTo("גלובס TV") == 0)
										{
											// pre roll eli										
											Utils.writeEventToGoogleAnalytics(getActivity(), "גלובס TV", "סרטון",
													Utils.getSubStringByValue(videoURL, "movId"));
										}
									}
								}
								else
								{
									// if
									// (MainSlidingActivity.getMainSlidingActivity().getIsRegularMador()
									// &&
									// !MainSlidingActivity.getMainSlidingActivity().getIsMainPageLoading())
									// {
									// Log.e("eli",
									// "regular mdor ????????????");
									// }
									Log.e("alex", "MainFragment Load Document");
									Intent intent = new Intent(activity, DocumentActivity_new.class);
									// Intent intent = new Intent(activity,
									// Pager_test.class);
									DataContext.Instance().SetArticleList(unique.toString(), parsedNewsSet);
									intent.putExtra("RowType", "Article" + parser);

									intent.putExtra("position", position);
									intent.putExtra("parentId", unique.toString());
									startActivity(intent);
									// startActivity(intent);
								}
							}

							else if (parsedNewsSet.itemHolder.get(position).getClass() == InnerGroup.class)
							{
								InnerGroup ig = (InnerGroup) parsedNewsSet.itemHolder.get(position);
								String sectionId = ig.getNode_id();

								String uri;
								String parser;
								String caller;
								if (sectionId.equals(GlobesURL.TVNode))
								{
									uri = GlobesURL.URLClips;
								}
								else
								{
									uri = GlobesURL.URLSections + sectionId;
								}
								caller = Definitions.SECTIONS;
								if (sectionId.equals("10002"))// opinions
								{
									parser = Definitions.OPINIONS;
								}
								else
								{
									parser = Definitions.MAINSCREEN;
								}

								// move to fragment
								SectionListFragment fragment = new SectionListFragment();
								Bundle args = new Bundle();
								args.putString(Definitions.URI_TO_PARSE, uri);
								args.putString(Definitions.CALLER, caller);
								args.putString(Definitions.PARSER, parser);
								args.putString(Definitions.HEADER, ((InnerGroup) parsedNewsSet.itemHolder.get(position)).getTitle());
								fragment.setArguments(args);

								// switch content
								
								Log.e("alex", "onSwitchContentFragment v1");
								
								mCallback.onSwitchContentFragment(fragment, SectionListFragment.class.getSimpleName(), true, false, false);
							}
							else if (parsedNewsSet.itemHolder.get(position) instanceof Banner)
							{
								if (((Banner) parsedNewsSet.itemHolder.get(position)).getLinkURL() != null)
								{
									Intent intent = new Intent();
									intent.setAction(Intent.ACTION_VIEW);
									intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
									intent.setData(android.net.Uri.parse(((Banner) parsedNewsSet.itemHolder.get(position)).getLinkURL()));
									context.startActivity(intent);
								}
							}
						}
					});
					/**
					 * Set a listener to be invoked when the list should be *
					 * refreshed -rotemm.
					 */
					// TODO
					// ((PullToRefreshListView)
					// getListView()).setOnRefreshListener(new
					// OnRefreshListener()
					// {
					// @Override
					// public void onRefresh()
					// {
					// new GetDataTask().execute();
					// }
					// });

					// coming from push
					if (pushNotificationDocID != null && !Definitions.PUSH_WAS_HANDLE)
					{				
						// Return true if the fragment is currently added to its
						// activity.
						if (isAdded())
						{
							Activity a = getActivity();
							if (a != null)
							{
								if(pushNotificationDocID.length() < 6)
								{
									Log.e("alex", "PushAppsTest From Folder pushNotificationDocID:" + pushNotificationDocID + " parser:" + parser);
									
									String uri = GlobesURL.URLSections + pushNotificationDocID;  //node_id=4621
									SectionListFragment fragment = new SectionListFragment();
									Bundle args = new Bundle();
									args.putString(Definitions.URI_TO_PARSE, uri);
									args.putString(Definitions.CALLER, caller);
									args.putString(Definitions.PARSER, parser);
									//args.putString(Definitions.HEADER, ((InnerGroup) parsedNewsSet.itemHolder.get(position)).getTitle());
									args.putString(Definitions.HEADER, Definitions.SPECIAL_FOLDER_FOR_PUSH);
									fragment.setArguments(args);
                                    // switch content
									mCallback.onSwitchContentFragment(fragment, SectionListFragment.class.getSimpleName(), true, false, false);
								}
								else
								{
									Log.e("alex", "PushAppsTest v5 pushNotificationDocID:" + pushNotificationDocID);
									Intent i = new Intent(a, DocumentActivity_new.class);
									i.putExtra("pushNotificationDocID", pushNotificationDocID);
									i.putExtra(Definitions.SCHEME_COMING_FROM_SCHEME, isFromScheme);
									i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
									startActivity(i);
								}
							}
							else
							{
								a = MainSlidingActivity.getMainSlidingActivity();
								if (a != null)
								{
									Log.e("alex", "PushAppsTest v6 pushNotificationDocID:" + pushNotificationDocID);
									Intent i = new Intent(a, DocumentActivity_new.class);
									i.putExtra("pushNotificationDocID", pushNotificationDocID);
									i.putExtra(Definitions.SCHEME_COMING_FROM_SCHEME, isFromScheme);
									i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
									startActivity(i);
								}
							}
						}
						pushNotificationDocID = null;
						Definitions.PUSH_WAS_HANDLE = true;
					}
					else if (schemeURIShare != null)
					{
						TickerItem item = (TickerItem) v.getTag();
						Intent i = new Intent(activity, ShareActivity.class);
						i.putExtra(Definitions.CALLER, Definitions.SHARES);
						i.putExtra(Definitions.PARSER, Definitions.SHARES);
						i.putExtra("feeder", item.getFeeder());
						i.putExtra("shareId", item.getInsturmentID());

						i.putExtra("URI", schemeURIShare);

						// TODO eli
						Log.e("eli", "Feeder from Main fragment " + item.getFeeder());
						Log.e("eli", "shareId Main fragment " + item.getInsturmentID());

						startActivity(i);

					}

					// if (showPushDialog == SHOW && uriToParse != null &&
					// uriToParse.equals(Definitions.URLMainScreen) && parser !=
					// null
					// && parser.equals(Definitions.MAINSCREEN))
					// {
					// showDialog("×©×™×¨×•×ª ×”×ª×¨×�×•×ª",
					// getMsgForPushDialog(),
					// "×”×’×“×¨×•×ª", "×”×ž×©×š");
					// }

					if (uriToParse != null && uriToParse.equals(GlobesURL.URLMainScreen) && parser != null
							&& parser.equals(Definitions.MAINSCREEN))
					{
						// If we need to show the first time push dialog , we
						// dont show the second dialog
						if (showPushDialog == SHOW)
						{
							showDialog("שירות התראות", getMsgForPushDialog(), "הגדרות", "המשך");

						}
						else if (showPushDialog == DONTSHOW && showMuteNotificationsDialog == SHOW)
						{ // here we show the second dialog only if the first
							// push has already been shown

							showDialog("שירות צלילי התראות", getMsgForMuteNotifications(), "הגדרות", "המשך");
						}

					}
				}
				else if (result == ERROR_PARSING)
				{
					Toast toast = Toast.makeText(context, Definitions.ErrorLoading, Toast.LENGTH_LONG);
					toast.show();
				}
			}

			private int getFirstEmpty()
			{
				for (int j = 0; j < parsedNewsSet.itemHolder.size(); j++)
				{
					if (parsedNewsSet.itemHolder.get(j) instanceof RowCompanyDuns100)
					{
						return j;
					}
				}
				return -1;
			}

		}.execute();
	}
	private boolean isRegularMador()
	{
		return MainSlidingActivity.getMainSlidingActivity().getIsRegularMador()
				&& !MainSlidingActivity.getMainSlidingActivity().getIsMainPageLoading();
	}

	private String getTitleActionBar()
	{
		return MainSlidingActivity.getMainSlidingActivity().getActionBarTitle();
	}

	private boolean isTagitMador()
	{
		return !MainSlidingActivity.getMainSlidingActivity().getIsRegularMador()
				&& !MainSlidingActivity.getMainSlidingActivity().getIsMainPageLoading();
	}

	private boolean isMainMador()
	{
		try {
			return MainSlidingActivity.getMainSlidingActivity().getIsMainPageLoading();
		}
		catch(Exception ex){}
		return false;
	}

	public void onclickFrontWihtoutTAGIT(int position)
	{
		// position--;
		if (parsedNewsSet.itemHolder.get(position) instanceof RowCompanyDuns100)
		{
			RowCompanyDuns100 articleDuns100 = (RowCompanyDuns100) parsedNewsSet.itemHolder.get(position);
			Intent intent = new Intent(activity, CompanyActivity.class);
			intent.putExtra(Definitions.URI_TO_PARSE, GlobesURL.URLDunsCompany + "doc_id=" + articleDuns100.getCompanyDocId());
			intent.putExtra("CompanyDocId", articleDuns100.getCompanyDocId());
			startActivity(intent);
		}
		else if (parsedNewsSet.itemHolder.get(position) instanceof RowArticleDuns100)
		{
			// RowArticleDuns100 articleDuns100 = (RowArticleDuns100)
			// parsedNewsSet.itemHolder.get(position);
			// ArticleDuns100Fragment fragment = new ArticleDuns100Fragment();
			// Bundle args = new Bundle();
			// args.putString(Definitions.URI_TO_PARSE,
			// GlobesURL.URLDunsSingleArticle + "doc_id=" +
			// articleDuns100.getDoc_id());
			// args.putString(Definitions.CALLER, caller);
			// args.putString(Definitions.PARSER, Definitions.ARTICLE_DUNS_100);
			// fragment.setArguments(args);
			// // switch content
			// mCallback.onSwitchContentFragment(fragment,
			// ArticleDuns100Fragment.class.getSimpleName(), true, false);
			Intent intent = new Intent(activity, DocumentActivity_new.class);
			DataContext.Instance().SetArticleList(unique.toString(), parsedNewsSet);
			intent.putExtra("RowType", "RowArticleDuns100 1106");
			intent.putExtra("position", position);
			intent.putExtra("parentId", unique.toString());
			startActivity(intent);
		}
		else if (parsedNewsSet.itemHolder.get(position) instanceof Article)
		{
			// get instance of the article
			Article article = (Article) parsedNewsSet.itemHolder.get(position);
			articleGlobal = (Article) parsedNewsSet.itemHolder.get(position);
			
			// check here if article has LINK field and open
			// the link in
			// webView or browser. the LINK field should be
			// set via MAINParser
			// at the article object
			// This is katava pirsumit and need to be opened
			// in Browser
			String url = article.getUrlMarketingArticle();
			// url from regular video & html5 video
			String videoURL = article.getUrl();
			Log.e("eli", "1205 videoURL  http://stage.globes.co.il/news/article.aspx?did=" + article.getDoc_id());

			String videoURLhtml5 = article.getUrlHTML5();
			if (url != null && url.length() > 0)
			{
				if (!(url.contains("http")) && !(url.contains("HTTP")))
				{
					url = "http://www.globes.co.il";
				}
				String openInWeb = "";
				int indexOfOpenWeb = url.indexOf("Open_web=");

				if (indexOfOpenWeb != -1) openInWeb = new String(url.substring(indexOfOpenWeb + 9));

				if (openInWeb.equals("true"))
				{
					// open in browser
					Intent i = new Intent(Intent.ACTION_VIEW);
					i.setData(Uri.parse(url));
					startActivity(i);
					Log.e("eli", "video 4");
				}
				else if (openInWeb.equals("false"))
				{
					// open in WebView via RowAdActivity
					Intent i = new Intent(activity, RowAdActivity.class);
					i.putExtra(RowAdActivity.KEY_URL_AD, url);
					startActivity(i);
				}
			}
			// if article is a video/video HTML5 article
			// open straight ahead
			else if (!Definitions.isOpenVideoAsArticle && ((videoURL != null && videoURL.length() > 0) || (videoURLhtml5 != null && videoURLhtml5.length() > 0)))
			{
				boolean isHTML5Video = article.isHTML5video();
				if (isHTML5Video)
				{
					openVideoInMediaPlayerOrBrowser(videoURLhtml5, true, article.getDoc_id());
					Log.e("eli", "video 5");
				}
				else
				{
					openVideoInMediaPlayerOrBrowser(videoURL, false, article.getDoc_id());
					Log.e("eli", "video 6");
				}
			}
			else
			{
				Intent intent = new Intent(activity, DocumentActivity_new.class);
				DataContext.Instance().SetArticleList(unique.toString(), parsedNewsSet);
				intent.putExtra("RowType", "Opinion");
				intent.putExtra("position", position);
				intent.putExtra("parentId", unique.toString());
				startActivity(intent);
			}

		}
		else if (parsedNewsSet.itemHolder.get(position).getClass() == InnerGroup.class)
		{
			InnerGroup ig = (InnerGroup) parsedNewsSet.itemHolder.get(position);
			if (isMainMador() && ig != null)
			{
				Utils.writeEventToGoogleAnalytics(getActivity(), "עמוד הבית", "מדורים כותרות על לחיצה", ig.getTitle());
			}

			String sectionId = ig.getNode_id();
			String uri;
			String parser;
			String caller;
			if (sectionId.equals(GlobesURL.TVNode))
			{
				uri = GlobesURL.URLClips;
			}
			else
			{
				uri = GlobesURL.URLSections + sectionId;
			}
			caller = Definitions.SECTIONS;
			if (sectionId.equals("10002"))// opinions
			{
				parser = Definitions.OPINIONS;
			}
			else
			{
				parser = Definitions.MAINSCREEN;
			}

			if (sectionId.equals("5256"))
			{
				uri = GlobesURL.URLDunsArticles;
				parser = Definitions.DUNS_100;
				caller = Definitions.SECTIONS;
			}
			// TODO eli group in main screen click
			// move to fragment
			
			SectionListFragment fragment = new SectionListFragment();
			Bundle args = new Bundle();
			args.putString(Definitions.URI_TO_PARSE, uri);
			args.putString(Definitions.CALLER, caller);
			args.putString(Definitions.PARSER, parser);
			args.putString(Definitions.HEADER, ((InnerGroup) parsedNewsSet.itemHolder.get(position)).getTitle());
			fragment.setArguments(args);
			// mCallback.setRegularMador(true);
			
			Log.e("alex", "onSwitchContentFragment v2");
			
			mCallback.onSwitchContentFragment(fragment, SectionListFragment.class.getSimpleName(), true, false, true);
		}
		else if (parsedNewsSet.itemHolder.get(position) instanceof Banner)
		{
			if (((Banner) parsedNewsSet.itemHolder.get(position)).getLinkURL() != null)
			{
				Intent intent = new Intent();
				intent.setAction(Intent.ACTION_VIEW);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.setData(android.net.Uri.parse(((Banner) parsedNewsSet.itemHolder.get(position)).getLinkURL()));
				context.startActivity(intent);
			}
		}
	}

	/**
	 * Called upon {@link MainFragment#setInitialData(String, String, View)}
	 * successfully parsed data , this gives us a chance to manipulate data
	 * before it is shown
	 * 
	 * @param uriToParse
	 *            the URL to parse from
	 * @param parser
	 *            the parser that was used
	 */
	protected void onNewsSetParsed(String uriToParse, String parser)
	{
		// does nothing here , implementation goes at inheriting classes
	}

	protected class GetDataTask extends AsyncTask<Void, Void, Void>
	{
		@Override
		protected Void doInBackground(Void... params)
		{
			try
			{
				URL url = new URL(uriToParse);
				parsedNewsSet = NewsSet.parseURL(url, parser);

				addStaticItemsToNewsSet();
			}
			catch (Exception e)
			{
			}
			return null;

		}

		@SuppressLint("NewApi")
		@Override
		protected void onPostExecute(Void result)
		{
			super.onPostExecute(result);
			onNewsSetParsed(uriToParse, parser);
			Log.e("eli", "1257");
			
			if (parsedNewsSet != null)
			{

				adapter = new LazyAdapter(activity, parsedNewsSet.itemHolder);
				adapter.setMainFragment(MainFragment.this);

				ArrayList<Article> documents = new ArrayList<Article>();
				for (int i = 0; i < parsedNewsSet.itemHolder.size(); i++)
				{

					if (parsedNewsSet.itemHolder.get(i) instanceof Article)
					{

						documents.add((Article) parsedNewsSet.itemHolder.get(i));
					}

				}
				DataStore.getInstance().setDocuments(documents);
				try
				{

					if (Definitions.toUseLiveBoxMain)
					{

						// if (isMainMador() && okDoLiveBoxe)

						if (isMainMador())
						{
							liveBox = new LiveBoxModel();
							liveBox.setUrlLiveBoxMain(DataStore.getInstance().getLiveBoxMainURL());
							View view = LayoutInflater.from(getActivity()).inflate(R.layout.row_layout_live_box, null);

							String articleTextHtml = liveBox.getUrlLiveBoxMain();
							try
							{
								articleTextHtml = encodeTextForWebView(articleTextHtml, EncodeType.TYPE_BODY);
							}
							catch (UnsupportedEncodingException e)
							{
								Log.e("eli", "erorr in setAllViews", e);
							}
							WebView webView = (WebView) view.findViewById(R.id.webView_live_box);
							WebSettings webViewSettings = webView.getSettings();
							webViewSettings.setJavaScriptCanOpenWindowsAutomatically(false);
							webViewSettings.setJavaScriptEnabled(true);
							webView.setWebChromeClient(new WebChromeClient());
							webViewSettings.setUseWideViewPort(false);
							webViewSettings.setBuiltInZoomControls(false);
							webViewSettings.setLoadWithOverviewMode(false);
							webViewSettings.setSupportZoom(false);
							webViewSettings.setPluginState(PluginState.ON);
							webView.setLayerType(WebView.LAYER_TYPE_SOFTWARE, null);

							webView.loadData(articleTextHtml, "text/html; charset=utf-8", "UTF-8");

							if (list.getHeaderViewsCount() > 0)
							{

								list.removeViewAt(0);
							}

							list.addHeaderView(view);
							
							//Log.e("alex", "KKKKKKK1 : 12");
						}
					}
				}
				catch (Exception e)
				{
					// TODO: handle exception
				}

				setListAdapter(adapter);
			}
			// open menus
			mCallback.getsSlidingMenu().setSlidingEnabled(true);
			mPullToRefreshLayout.setRefreshComplete();

		}

		@Override
		protected void onCancelled()
		{
			mPullToRefreshLayout.setRefreshComplete();
		}
	}

	public void addStaticItemsToNewsSet()
	{

	}

	/**
	 * 
	 * @param title
	 *            - dialog title
	 * @param msg
	 *            - dialog msg
	 * @param neutBtn
	 *            - the neutral btn
	 */
	private void showDialog(String title, String msg, String neutBtn, String negBtn)
	{

		AlertDialog.Builder ad = new AlertDialog.Builder(activity);
		ad.setCancelable(false);
		ad.setTitle(title);
		ad.setMessage(msg);

		ad.setPositiveButton(neutBtn, new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int which)
			{
				Intent i = new Intent(activity, SettingsActivity.class);
				startActivity(i);
				alertDialog.dismiss();
				return;
			}
		});

		ad.setNegativeButton(negBtn, new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int which)
			{
				alertDialog.dismiss();
				return;
			}
		});

		alertDialog = ad.create();
		alertDialog.show();
	}

	/**
	 * Sets the msg for the push dialog
	 * 
	 * @return the msg to be shown in the dialog
	 */
	private String getMsgForPushDialog()
	{

		StringBuffer msgDialog = new StringBuffer();
		msgDialog.append("  גלובס שמח לעדכן אתכם על שירות חדש שהושק באפליקציה זו - שירות עדכוני כתבות");
		msgDialog.append("(Push notification)");
		msgDialog.append("\n");
		msgDialog.append("\n");
		msgDialog.append("שירות זה מפועל אוטומטית ומאפשר לכם להתעדכן מדי יום");
		msgDialog.append("\n");
		msgDialog.append("במספר מצומצם של ידיעות חדשות ואטרקטיביות מהזירה העסקית.");
		msgDialog.append("\n");
		msgDialog.append("\n");
		msgDialog.append("  ניתן להסיר ו/או להצטרף מחדש לשירות");
		msgDialog.append("\n");
		msgDialog.append("  וקיימת אפשרות לכיבוי/הפעלת צליל ההתראה");
		msgDialog.append("\n");
		msgDialog.append("על-ידי מעבר לתפריט ההגדרות ובהתאם לשנות את ההגדרה למצב המבוקש.");
		msgDialog.append("\n\n");
		msgDialog.append("בעת נדידה ייתכנו עלויות סלולריות.");
		msgDialog.append("\n");
		return msgDialog.toString();
	}

	/**
	 * Sets the msg for the MuteNotifications dialog
	 * 
	 * @return the msg to be shown in the dialog
	 */
	private String getMsgForMuteNotifications()
	{
		StringBuffer msgDialog = new StringBuffer();
		msgDialog.append("  ×¢×“×›×•×Ÿ ×œ×©×™×¨×•×ª ×”×”×ª×¨×�×•×ª ×©×œ");
		msgDialog.append(Definitions.shareGlobesWithQuates);
		msgDialog.append("\n");
		msgDialog
				.append("×œ×©×™×¨×•×ª×š ×”×ª×•×•×¡×¤×” ×�×¤×©×¨×•×ª ×œ×‘×™×˜×•×œ ×¦×œ×™×œ×™ ×”×”×ª×¨×�×•×ª ×‘×ª×¤×¨×™×˜ ×”×”×’×“×¨×•×ª ×©×œ ×”×�×¤×œ×™×§×¦×™×”");
		msgDialog.append("\n");
		msgDialog.append("\n");
		msgDialog.append("×œ×”×ž×©×™×š ×‘×§×‘×œ×ª ×”×ª×¨×�×•×ª  ×œ×œ×� ×©×™× ×•×™ ×œ×—×¥ ×”×ž×©×š");
		msgDialog.append("\n");
		msgDialog.append("×œ×©×™× ×•×™ ×”×’×“×¨×•×ª×™×š ×”×�×™×©×™×•×ª ×•×ž×¢×‘×¨ ×œ×ª×¤×¨×™×˜ ×”×”×’×“×¨×•×ª - ×œ×—×¥ ×”×’×“×¨×•×ª");
		msgDialog.append("\n");
		msgDialog.append("\n");
		msgDialog.append("  ×‘×‘×¨×›×”");
		msgDialog.append(Definitions.shareGlobesWithQuates);
		msgDialog.append("\n");
		return msgDialog.toString();
	}

	/**
	 * adding params for matrix server video request
	 * 
	 * @param url
	 *            - video url before adding the w=width h=height DT=device type
	 * @return video url to process with the new params for matrix
	 */
	private String getDeviceDetailsForVideoUrl(String url)
	{

		DisplayMetrics dm = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
		int height = dm.heightPixels;
		int width = dm.widthPixels;

		String MO = Build.MODEL;

		MO = MO.replace(" ", "");
		MO = MO.replace(" ", "");
		MO = MO.replace(" ", "");
		return url + "&w=" + width + "&h=" + height + "&dt=" + MO;
	}

	/**
	 * Set the value of showPushDialog in sharedPrefs to DONTSHOW
	 */
	protected void savePrefsIntValue(String key, int value)
	{
		SharedPreferences.Editor editor = sharedPrefs.edit();
		editor.putInt(key, value);
		editor.commit();
	}

	/**
	 * 
	 * @param dp
	 *            int amount of dp to be converted to pixels
	 * @return px by the given dp depending on system info
	 */
	protected int converDpToPx(int dp)
	{

		DisplayMetrics dm = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
		float logicalDensity = dm.density;
		int res = ((int) (dp * logicalDensity + 0.5));
		return res;
	}

	/**
	 * 
	 * @param px
	 *            int amount of px to be converted to dp
	 * @return dp by the given px depending on system info
	 */
	protected int converPxToDp(int px)
	{

		DisplayMetrics dm = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
		float logicalDensity = dm.density;
		int res = (int) ((px + 0.5) / logicalDensity);
		return res;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		Log.e("eli", "onActivityResult in MainFragment " + resultCode);
		// adapter.notifyDataSetChanged();
		// activity;
		if ((resultCode == Activity.RESULT_OK))
		{
			if (requestCode == Definitions.REQUEST_CLIP_CAPTURE)
			{
				Uri uriVideo1 = data.getData();

				String test = getVideoAttachmentPath(uriVideo1);

				startRedEmail(test);
			}
			else
			{
				String attachmentUri = data.getStringExtra("imageUri");
				startRedEmail(attachmentUri);
			}
		}
	}

	protected void showDialog(int dialogID)
	{
		final Dialog dialog = new Dialog(activity);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.getWindow().setGravity(Gravity.BOTTOM);
		dialog.setContentView(R.layout.red_email_dialog);
		TextView tvEmailTitle = (TextView) dialog.findViewById(R.id.tvEmailTitle);
		Button btnEmailWrite = (Button) dialog.findViewById(R.id.btnEmailWrite);
		Button btnRedEmailClip = (Button) dialog.findViewById(R.id.btnRedEmailClip);
		Button btnRedEmailPic = (Button) dialog.findViewById(R.id.btnRedEmailPic);

		if (dialogID == Definitions.RED_EMAIL_DIALOG)
		{
			btnEmailWrite.setOnClickListener(new View.OnClickListener()
			{
				public void onClick(View v)
				{
					startRedEmail("");
					dialog.dismiss();
				}
			});

			btnRedEmailClip.setOnClickListener(new View.OnClickListener()
			{
				public void onClick(View v)
				{
					dialog.dismiss();
					showDialog(Definitions.SEND_CLIP_DIALOG);
				}
			});

			btnRedEmailPic.setOnClickListener(new View.OnClickListener()
			{
				public void onClick(View v)
				{
					dialog.dismiss();
					showDialog(Definitions.SEND_PIC_DIALOG);
				}
			});
		}
		else if (dialogID == Definitions.SEND_CLIP_DIALOG)
		{
			btnEmailWrite.setVisibility(View.GONE);

			tvEmailTitle.setText("×‘×—×¨ ×•×™×“×�×•");
			btnRedEmailClip.setText("×¦×œ×� ×•×™×“×�×•");

			btnRedEmailClip.setOnClickListener(new View.OnClickListener()
			{
				public void onClick(View v)
				{
					getAttachment(Definitions.CLIP_ATTACHMENT, Definitions.MAKE_ATTACHMENT);
					dialog.dismiss();
				}
			});

			btnRedEmailPic.setText("×‘×—×¨ ×ž×’×œ×¨×™×”");
			btnRedEmailPic.setOnClickListener(new View.OnClickListener()
			{
				public void onClick(View v)
				{
					getAttachment(Definitions.CLIP_ATTACHMENT, Definitions.CHOOSE_ATTACHMENT);
					dialog.dismiss();
				}
			});
		}
		else if (dialogID == Definitions.SEND_PIC_DIALOG)
		{
			tvEmailTitle.setText("×‘×—×¨ ×ª×ž×•× ×”");

			btnEmailWrite.setVisibility(View.GONE);
			btnRedEmailClip.setText("×¦×œ×� ×ª×ž×•× ×”");
			btnRedEmailClip.setOnClickListener(new View.OnClickListener()
			{
				public void onClick(View v)
				{
					getAttachment(Definitions.PIC_ATTACHMENT, Definitions.MAKE_ATTACHMENT);
					dialog.dismiss();
				}
			});

			btnRedEmailPic.setText("×‘×—×¨ ×ž×’×œ×¨×™×”");
			btnRedEmailPic.setOnClickListener(new View.OnClickListener()
			{
				public void onClick(View v)
				{
					getAttachment(Definitions.PIC_ATTACHMENT, Definitions.CHOOSE_ATTACHMENT);
					dialog.dismiss();
				}
			});
		}

		Button btnCancelDialog = (Button) dialog.findViewById(R.id.btnCancel);
		btnCancelDialog.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View v)
			{
				dialog.dismiss();
			}
		});
		dialog.show();
	}

	public void openVideoInMediaPlayerOrBrowser(final String url, final boolean isHTML5, final String docId)
	{
		Runnable runnable = new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					openVideoInMediaPlayerOrBrowserNoThread(url, isHTML5, docId);
				}
				catch (Exception e)
				{
					// e.printStackTrace();
				}

				handler.post(new Runnable()
				{
					@Override
					public void run()
					{

					}
				});
			}
		};
		new Thread(runnable).start();
	}

	protected void getAttachment(int attachmentType, int getAttacmentMode)
	{
		if ((attachmentType == Definitions.CLIP_ATTACHMENT) && (getAttacmentMode == Definitions.MAKE_ATTACHMENT))
		{
			Intent i = new Intent(android.provider.MediaStore.ACTION_VIDEO_CAPTURE);
			try
			{
				startActivityForResult(i, Definitions.REQUEST_CLIP_CAPTURE);
			}
			catch (ActivityNotFoundException ex)
			{
				Toast.makeText(activity, "Your device does not contain an application to run this action", Toast.LENGTH_LONG).show();
			}
		}
		else
		{
			Intent getImage = new Intent(activity, AttachmentPicker.class);
			getImage.putExtra("attachmentType", attachmentType);
			getImage.putExtra("getAttachmentMode", getAttacmentMode);

			startActivityForResult(getImage, 1010);
		}
	}

	void openVideoInMediaPlayerOrBrowserNoThread(String url, boolean isHTML5, String docId)
	{
		//Log.e("alex", "openVideoInMediaPlayerOrBrowserNoThread: " + url);
		String videoUrl;
		try
		{
			if (isHTML5)
			{
				videoUrl = url;
				// remove the apple type from url
				if (videoUrl.contains("&AppleType=html5"))
				{
					videoUrl = videoUrl.replace("&AppleType=html5iframe", "");
				}
			}
			else
			{
				//Log.e("alex", "Definitions.mediaPlayerInBrowser: " + Definitions.mediaPlayerInBrowser);
				if (!Definitions.mediaPlayerInBrowser)
				{
					/** get the device info and add it to the URL */
					url = getDeviceDetailsForVideoUrl(url);
				
					//Log.e("alex", "getDeviceDetailsForVideoUrl111: " + Utils.resolveRedirect("http://www.cast-tv.biz/play/" + url,false));
					
					if (url.contains("http://www.cast-tv.biz/play/"))
					{
						Log.e("alex", "Definitions.toUseGIMA: " + Definitions.toUseGIMA);
						
						if (Definitions.toUseGIMA)
						{
							videoUrl = Utils.resolveRedirect("http://www.cast-tv.biz/play/" + url/*
																								 * +
																								 * Utils
																								 * .
																								 * resolveRedirect
																								 * (
																								 * url
																								 * ,
																								 * true
																								 * )
																								 */, false);
							//Log.e("alex", "videoUrl3: " + videoUrl);
						}
						else
						{
							//Log.e("alex", "videoUrl2: " + "http://www.cast-tv.biz/play/" + Utils.resolveRedirect(url, true));
							videoUrl = Utils.resolveRedirect("http://www.cast-tv.biz/play/" + Utils.resolveRedirect(url, true), false);	
							//Log.e("alex", "videoUrl1: " + videoUrl);
						}
					}
					else
					{
						videoUrl = Utils.resolveRedirect("http://www.globes.co.il" + Utils.resolveRedirect(url, true), false);
					}
				}
				else
				{
					videoUrl = "http://www.cast-tv.biz/play/" + Utils.resolveRedirect(url, true);
				}
			}

			Log.e("alex", "videoUrl33: " + videoUrl + "===" + Definitions.toUseGIMA);
			
			if (videoUrl != null && !videoUrl.contains("Failed_To_Get_Url"))
			{
				String name = articleGlobal.getTitle();
				String mador = MainSlidingActivity.getMainSlidingActivity().getActionBarTitle();
				mador = mador.compareTo("Globes Default") == 0 ? "ראשי" : mador;
				Utils.writeEventToGoogleAnalytics(getActivity(), "הפעלת וידאו", mador, name);

				Uri uri = Uri.parse(videoUrl);
				// pre roll eli
				if (Definitions.toUseGIMA)
				{
					//Intent i = new Intent(getActivity(), PlayVideoActivity.class);
					Intent i = new Intent(getActivity(), PlayerImaActivity.class);
					i.putExtra("videoURL", uri.toString());
					i.putExtra("docId", docId);

					startActivity(i);
				}
				else
				{
					Intent intent = new Intent(Intent.ACTION_VIEW, uri);
					// intent.setDataAndType(data, "video/mp4");
					startActivity(intent);
				}
			}
		}
		catch (ClientProtocolException e)
		{
		}
		catch (IOException e)
		{
		}
	}

	public void btnShareVideo_onClick(View v)
	{
		FrameLayout vwParent = (FrameLayout) v.getParent();
		LinearLayout vwGrandparentRow = (LinearLayout) vwParent.getParent();

		int position = getListView().getPositionForView(vwGrandparentRow);
		position--;

		if (position >= 0)
		{
			if (parsedNewsSet.itemHolder.get(position) instanceof Article)
			{
				Intent shareArticle = new Intent();
				shareArticle.setAction(Intent.ACTION_SEND);
				String mimeType = "text/plain";
				shareArticle.setType(mimeType);
				shareArticle.putExtra(Intent.EXTRA_SUBJECT, ((Article) parsedNewsSet.itemHolder.get(position)).getTitle());

				shareArticle.putExtra(Intent.EXTRA_TEXT, Definitions.watchVideo + "\n\n" + GlobesURL.URLDocumentToShare
						+ ((Article) parsedNewsSet.itemHolder.get(position)).getDoc_id() + Definitions.fromPlatformAppAnalytics + "\n\n"
						+ Definitions.ShareString + " " + Definitions.shareGlobesWithQuates + "\n\n" + Definitions.shareCellAppsPart1 + " "
						+ Definitions.shareGlobesWithQuates + " " + Definitions.shareCellAppsPart2 + "\n");
				// tracker.trackPageView(GlobesURL.URLDocumentToShare +
				// ((Article)
				// parsedNewsSet.itemHolder.get(position)).getDoc_id()
				// + "from=" + Definitions.SHAREsuffix);
				shareArticle.putExtra(Intent.EXTRA_TITLE, Definitions.ShareString);
				startActivity(Intent.createChooser(shareArticle, Definitions.Share));
			}
		}

		// String clipUrl = "";
		// String clipTitle = "";

		// if (((Article) parsedNewsSet.itemHolder.get(position)).getUrl()
		// != null)
		// {
		//
		// clipUrl = ((Article)
		// parsedNewsSet.itemHolder.get(position)).getUrl();
		// clipTitle = ((Article)
		// parsedNewsSet.itemHolder.get(position)).getTitle();
		// Intent shareClip = new Intent();
		// shareClip.setAction(Intent.ACTION_SEND);
		// String mimeType = "text/plain";
		// shareClip.setType(mimeType);
		// shareClip.putExtra(Intent.EXTRA_SUBJECT, clipTitle);
		// shareClip.putExtra(Intent.EXTRA_TEXT, clipUrl);
		// shareClip.putExtra(Intent.EXTRA_TITLE, Definitions.ShareString);
		// startActivity(Intent.createChooser(shareClip,
		// Definitions.ShareClip));
		// }
	}

	void openRedEmailDialog()
	{
		showDialog(Definitions.RED_EMAIL_DIALOG);
	}

	protected void startRedEmail(String attachmentUri)
	{
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
		if (attachmentUri.contains("."))
		{
			emailSend.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + attachmentUri));
			mimeType = "image/jpeg";
		}
		emailSend.setType(mimeType);
		startActivity(Intent.createChooser(emailSend, getString(R.string.do_you_have_news)));
	}

	// get the name of the media from the Uri
	protected String getVideoAttachmentPath(Uri uri)
	{
		String filename = null;

		try
		{
			String[] projection =
			{MediaStore.Images.Media.DATA};// DISPLAY_NAME
			Cursor cursor = activity.managedQuery(uri, projection, null, null, null);

			if (cursor != null && cursor.moveToFirst())
			{
				int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);// DISPLAY_NAME
				filename = cursor.getString(column_index);
			}
			else
			{
				filename = null;
			}
		}
		catch (Exception e)
		{
		}

		return filename;
	}

	/**
	 * Inits the RichMediaAd ...but doesn't load it needs to be loaded from
	 * OnResume when necessary
	 */
	protected void initRichMedia(View v)
	{
		jaAdView = (JustAdView) v.findViewById(R.id.justAdView_RICH_MEDIA_pull_to_refresh);

		/**
		 * Be sure to call setAd() before using the ad. example -
		 * http://sdk.mobiyield.com/TN/tn_mobile_android_richmedia_320X480.html
		 * globes -
		 * http://sdk.mobiyield.com/Globes/richmedia_320_480_globes_android.html
		 * 
		 */
		jaAdView.selectAd("http://sdk.mobiyield.com/Globes/richmedia_320_480_globes_android.html", null);

		/**
		 * Set Delegate for jaAdView1
		 */
		jaAdView.setDelegate(new JustAdViewDelegate()
		{

			@Override
			public void AdError(JustAdView adView, String message)
			{
				super.AdError(adView, message);
				Log.d(TAG, "Rich Ad Error: " + message);

				/**
				 * Error occured on loading the Ad 1.Log it 2.finish the
				 * Activity and resume the App
				 */
				jaAdView.setVisibility(View.GONE);
			}

			@Override
			public void AdLoaded(JustAdView adView)
			{
				super.AdLoaded(adView);
				Log.d(TAG, "Rich Ad loaded success: " + adView.toString());

				/** Ad loaded now we need to start it */
				dfpAdView.setVisibility(View.GONE);
				jaAdView.setVisibility(View.VISIBLE);
				jaAdView.startAd();
			}

			@Override
			public void AdStarted(JustAdView adView)
			{
				super.AdStarted(adView);
				Log.d(TAG, "Rich Ad started displaying: " + adView.toString());

			}

			@Override
			public void AdStopped(JustAdView adView)
			{
				super.AdStopped(adView);
				Log.d(TAG, "Ad stopped");

				/** Ad stopped from playing we finish the activity */
				jaAdView.setVisibility(View.GONE);
				dfpAdView.setVisibility(View.VISIBLE);
			}

		});

	}

	// ******* Ticker ************
	/**
	 * Inits the Ticker UI & listeners also populates the text with given looper
	 * items
	 */
	private void initLooperUi(final View v)
	{
		linearLayoutTickerContainer = (LinearLayout) v.findViewById(R.id.linearLayout_Ticker_container);
		linearLayoutTickerContainer.setVisibility(View.VISIBLE);
		horizontalScrollview = (HorizontalScrollView) v.findViewById(R.id.horiztonal_scrollview_id);
		horizontalOuterLayout = (LinearLayout) v.findViewById(R.id.horiztonal_outer_layout_id);
		horizontalScrollview.setHorizontalScrollBarEnabled(false);

		btnOpenLooperPrefsActivity = (ImageButton) v.findViewById(R.id.Button_open_looper_preferences);

		btnOpenLooperPrefsActivity.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				// // eli lunch financial_portal
				FinancialPortalFragment fragment = new FinancialPortalFragment();
				// // load fragment
				fragment.setInitialURL("http://m.globes.co.il/news/m/finance.aspx?h=3&from=app");
				Bundle args = new Bundle();
				args.putString(Definitions.HEADER, "פורטל פיננסי");
				fragment.setArguments(args);
				mCallback.onSwitchContentFragment(fragment, FinancialPortalFragment.class.getSimpleName(), true, false, true);
				//

				// TODO eli - open the prefrencess
				// move to fragment
				// TickerPreferencesFragment fragment = new
				// TickerPreferencesFragment();
				// switch content
				// mCallback.onSwitchContentFragment(fragment,
				// TickerPreferencesFragment.class.getSimpleName(), true, false,
				// false);
			}
		});

		// init refresh timer task

		if (refreshLooperItemsTimer == null)
		{
			refreshLooperItemsTimer = new Timer();
			final Runnable refresh_Tick = new Runnable()
			{
				public void run()
				{
					new AsyncTask<Void, Void, Void>()
					{
						protected Void doInBackground(Void... params)
						{
							looper.refreshLooperItemsFromURL();
							return null;
						}
						protected void onPostExecute(Void result)
						{
							addLooperItemsToView();
						}
					}.execute();
				}
			};

			if (refreshSchedule != null)
			{
				refreshSchedule.cancel();
				refreshSchedule = null;
			}
			refreshSchedule = new TimerTask()
			{
				@Override
				public void run()
				{
					if (activity != null && refresh_Tick != null)
					{
						activity.runOnUiThread(refresh_Tick);
					}
				}
			};
			refreshLooperItemsTimer.schedule(refreshSchedule, 0, REFRESH_LOOPER_ITEMS_INTERVAL);
		}
	}

	private void getScrollMaxAmount()
	{
		int minus = -(reductionParam * (converDpToPx(192)));
		int actualWidth = (horizontalOuterLayout.getMeasuredWidth() + minus);
		scrollMax = actualWidth;
	}

	private void startAutoScrolling()
	{
		if (scrollTimer == null)
		{
			scrollTimer = new Timer();
			final Runnable Timer_Tick = new Runnable()
			{
				public void run()
				{
					moveScrollView();
				}
			};

			if (scrollerSchedule != null)
			{
				scrollerSchedule.cancel();
				scrollerSchedule = null;
			}
			scrollerSchedule = new TimerTask()
			{
				@Override
				public void run()
				{
					activity.runOnUiThread(Timer_Tick);
				}
			};

			scrollTimer.schedule(scrollerSchedule, 30, 30);

		}
	}

	private void moveScrollView()
	{
		scrollPos = (int) (horizontalScrollview.getScrollX() + 1.0);
		if (scrollPos >= scrollMax)
		{
			scrollPos = 0;
		}
		horizontalScrollview.scrollTo(scrollPos, 0);

	}

	/** Adds the TextViewsContainer to view. */
	public void addLooperItemsToView()
	{
		horizontalOuterLayout.removeAllViews();
		ArrayList<TickerItem> items = looper.getItems();
		if (items != null && items.size() > 0)
		{
			for (int i = 0; i < items.size(); i++)
			{
				try
				{
					final LinearLayout layout = getLooperItemView(items.get(i));
					LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(converDpToPx(192),
							LinearLayout.LayoutParams.WRAP_CONTENT);
					layout.setLayoutParams(params);
					horizontalOuterLayout.addView(layout);
				}
				catch (Exception e)
				{
					if (e != null)
					{
						Log.e(TAG, "addLooperItemsToView() : " + e.toString(), e);
					}
				}
			}
			// Add first items in list to match the size of the screen frame
			for (int j = 0; j < 3; j++)
			{
				try
				{
					final LinearLayout layout = getLooperItemView(items.get(j));
					LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(converDpToPx(192),
							LinearLayout.LayoutParams.WRAP_CONTENT);
					layout.setLayoutParams(params);
					horizontalOuterLayout.addView(layout);
				}
				catch (Exception e)
				{
					if (e != null)
					{
						Log.e(TAG, "addLooperItemsToView() : " + e.toString(), e);
					}
				}
			}
			getScrollMaxAndStartAutoScroll();
		}
	}
	private void getScrollMaxAndStartAutoScroll()
	{
		ViewTreeObserver vto = horizontalOuterLayout.getViewTreeObserver();
		vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener()
		{
			@Override
			public void onGlobalLayout()
			{
				horizontalOuterLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
				getScrollMaxAmount();
				startAutoScrolling();
			}
		});
	}
	/**
	 * Factory pattern for
	 * 
	 * @param item
	 *            - the item to create the view from
	 * @return LinearLayout to be added to horizontalOuterLayout
	 */
	private LinearLayout getLooperItemView(TickerItem item)
	{
		TickerItem looperItem = item;
		final LinearLayout layout = (LinearLayout) mInflater.inflate(R.layout.ticker_horizontal_scrollview_item, null);
		TextView textViewPrecentageChanged = (TextView) layout.findViewById(R.id.textView_looper_scrollview_item_precentage_changed);
		TextView textViewLast = (TextView) layout.findViewById(R.id.textView_looper_scrollview_item_last);
		TextView textViewNameHe = (TextView) layout.findViewById(R.id.textView_looper_scrollview_item_name_he);

		// set precentage change
		textViewPrecentageChanged.setText(looperItem.getPercentage_change());
		if (looperItem.getPercentage_change().contains("-"))
			textViewPrecentageChanged.setTextColor(0xffce0f0f);
		else textViewPrecentageChanged.setTextColor(0xff1aad20);

		// TODO eli
		if (looperItem.getPercentage_change().equals("0%") || looperItem.getPercentage_change().equals("0 %")
				|| looperItem.getPercentage_change().equals("%")) textViewPrecentageChanged.setVisibility(View.INVISIBLE);

		// set last & nameHE
		textViewLast.setText(looperItem.getLast());
		textViewNameHe.setText(looperItem.getNameHE());
		layout.setTag(looperItem);
		layout.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				TickerItem item = (TickerItem) v.getTag();
				Intent intent = new Intent(activity, ShareActivity.class);
				intent.putExtra(Definitions.CALLER, Definitions.SHARES);
				intent.putExtra(Definitions.PARSER, Definitions.SHARES);
				// TODO eli
				Log.e("eli", "1 Feeder from Main fragment " + item.getFeeder());
				Log.e("eli", "1 shareId Main fragment " + item.getInsturmentID());
				intent.putExtra("feeder", item.getFeeder());
				intent.putExtra("shareId", item.getInsturmentID());
				intent.putExtra("URI", GlobesURL.URLSharePage.replace("XXXX", item.getFeeder()).replace("YYYY", item.getInsturmentID()));
				startActivity(intent);
			}
		});

		return layout;
	}

	@Override
	public void addArguments(Object object)
	{
		CustomMenuItem menuItem = (CustomMenuItem) object;

		Bundle args = new Bundle();
		// name = menuItem.name;
		args.putString(Definitions.CALLER, menuItem.caller);
		args.putString(Definitions.PARSER, menuItem.parser);
		args.putString(Definitions.URI_TO_PARSE, menuItem.uriToParse);

		setArguments(args);
	}

	@Override
	public void onRefreshStarted(View view)
	{
		if (caller.compareTo("MainTabActivity") == 0)
		{
			// Log.i("eli", "Refresh עמוד הבית");
			Utils.writeEventToGoogleAnalytics(activity, "Refresh", "עמוד הבית", null);
		}

		// lock menus
		mCallback.getsSlidingMenu().setSlidingEnabled(false);
		// Toast.makeText(activity, "refresh", Toast.LENGTH_LONG).show();
		// start data task
		new GetDataTask().execute();
	}

	public SwipeListView getListViewFromMainFragment()
	{
		return list;
	}

	class AjillionHandle extends AsyncTask<Void, Void, Void>
	{
		private AjillionObj ajillionObjMain;

		@Override
		protected Void doInBackground(Void... params)
		{
			ajillionObjMain = Utils.getAjillionObj(getActivity(), "4110", 650, 650, true);
			//Log.e("alex", "ajillionObjMain1");
			return null;
		}

		@Override
		protected void onPostExecute(Void result)
		{
			super.onPostExecute(result);
			//Log.e("alex", "!!!!!!!!!!!!!!!!!!!!!!!!! + " + uriToParse);
			setInitialData(uriToParse, parser, getView());

			if (ajillionObjMain != null && ajillionObjMain.getSuccess())
			{
				DataStore.getInstance().setLiveBoxMainURL(ajillionObjMain.getCreative_url());

			}

		}
	}

	class AjillionHandleDocument extends AsyncTask<Void, Void, Void>
	{
		private AjillionObj ajillionObjArticle;

		@Override
		protected Void doInBackground(Void... params)
		{
			ajillionObjArticle = Utils.getAjillionObj(getActivity(), "3378", 650, 650, true);
			//Log.e("alex", "ajillionObjMain2");
			return null;
		}

		@Override
		protected void onPostExecute(Void result)
		{
			super.onPostExecute(result);

			if (ajillionObjArticle.getSuccess())
			{
				//Log.e("alex", "BBBBBBBBBBB: " + ajillionObjArticle.getCreative_url());
				DataStore.getInstance().setLiveBoxArticleURL(ajillionObjArticle.getCreative_url());

			}

		}
	}
	class initAjillionView extends AsyncTask<Void, Void, Void>
	{
		private ImageView ajillionObjImage;
		private WebView ajillionObjImageGif;
		private AjillionObj ajillionObj;
		private Context myCtx;

		public initAjillionView(Context context)
		{
			this.myCtx = context;
		}

		@Override
		protected void onPreExecute()
		{
			try
			{
				ajillionObjImage = new ImageView(myCtx);
				ajillionObjImageGif = new WebView(myCtx);
	
				Utils.centerViewInRelative(ajillionObjImageGif, false);
				Utils.centerViewInRelative(ajillionObjImage, true);
				ajillionObjImage.setScaleType(ScaleType.FIT_XY);
	
				frameAdViewContainer_Pull_to_refresh = (RelativeLayout) getView().findViewById(R.id.frameAdViewContainer_Pull_to_refresh);
				frameAdViewContainer_Pull_to_refresh.removeAllViews();
	
				frameAdViewContainer_Pull_to_refresh.addView(ajillionObjImage);
			}
			catch(Exception ex)
			{
				Log.e("alex", "MainFragment initAjillionView onPreExecute error: " + ex);
			}
		}

		@Override
		protected Void doInBackground(Void... params)
		{
			//Log.e("alex", "AAAAAAAAAAAAAAAAA : 13");
			if (MainSlidingActivity.getMainSlidingActivity().getIsMainPageLoading())
			{
				Log.e("eli", "HEAD_BANNER");
				ajillionObj = Utils.getAjillionObj(myCtx, Definitions.AJILLION_AD_ID_HEAD_BANNER, 320, 50, false);
			}
			else
			{
				Log.e("eli", "INNER_BANNER");
				ajillionObj = Utils.getAjillionObj(myCtx, Definitions.AJILLION_AD_ID_INNER_BANNER, 320, 50, false);
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result)
		{
			super.onPostExecute(result);
			Log.e("eli", ajillionObj.toString());
			if (ajillionObj == null || ajillionObj.getSuccess() == null || !ajillionObj.getSuccess()
					|| ajillionObj.getCreative_url() == null || ajillionObj.getCreative_url().equals(""))
			{
				return;
			}
			else if (ajillionObj.getCreative_type().equals("image"))
			{
				if (!ajillionObj.getCreative_url().endsWith(".gif"))
				{
					Picasso.with(getActivity()).load(ajillionObj.getCreative_url()).into(ajillionObjImage);
					ajillionObjImage.setOnClickListener(new OnClickListener()
					{
						@Override
						public void onClick(View v)
						{
							Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(ajillionObj.getClick_url()));
							startActivity(browserIntent);
						}
					});
				}
				else
				{
					frameAdViewContainer_Pull_to_refresh.addView(ajillionObjImageGif);
					ajillionObjImageGif.getSettings().setJavaScriptEnabled(true);
					ajillionObjImageGif.getSettings().setAppCacheEnabled(false);
					ajillionObjImageGif
							.loadData(Utils.centerHTML(ajillionObj.getCreative_url(), true), "text/html; charset=utf-8", "UTF-8");
					ajillionObjImageGif.setOnTouchListener(new OnTouchListener()
					{
						@Override
						public boolean onTouch(View v, MotionEvent event)
						{
							Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(ajillionObj.getClick_url()));
							startActivity(browserIntent);
							return true;
						}
					});
				}
			}
			else
			{
				frameAdViewContainer_Pull_to_refresh.addView(ajillionObjImageGif);
				ajillionObjImageGif.setPadding(0, 0, 0, 0);
				WebSettings webViewSettings = ajillionObjImageGif.getSettings();
				webViewSettings.setJavaScriptCanOpenWindowsAutomatically(false);
				webViewSettings.setJavaScriptEnabled(true);
				webViewSettings.setUseWideViewPort(false);
				webViewSettings.setBuiltInZoomControls(false);
				webViewSettings.setLoadWithOverviewMode(false);
				webViewSettings.setSupportZoom(false);
				webViewSettings.setPluginState(PluginState.ON);
				ajillionObjImageGif.loadData(Utils.centerHTML(ajillionObj.getCreative_url(), false), "text/html; charset=utf-8", "UTF-8");
				ajillionObjImageGif.setOnTouchListener(new OnTouchListener()
				{
					@Override
					public boolean onTouch(View v, MotionEvent event)
					{
						Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(ajillionObj.getClick_url()));
						Log.e("eli", "URI = " + Uri.parse(ajillionObj.getClick_url()));
						Log.e("eli", "STR URI = " + ajillionObj.getClick_url());

						startActivity(browserIntent);
						return true;
					}
				});
			}

		}

	}

//	private String getVersionName()
//	{
//		PackageManager manager = getActivity().getPackageManager();
//		String versionName = "";
//		try
//		{
//			PackageInfo info = manager.getPackageInfo(getActivity().getPackageName(), 0);
//
//			versionName = info.versionName;
//		}
//		catch (NameNotFoundException e)
//		{
//			versionName = "";
//		}
//		return versionName;
//	}
	
//	private void checkIfShowMandatoryUpdateDialog()
//	{
//		Date dLastMandatoryUpdateDialogShown = getLastMandatoryUpdateDialogShown();
//		
//		if(dLastMandatoryUpdateDialogShown == null){
//			showMandatoryUpdateDialog();			
//		}
//		else
//		{
//			long diff = Math.abs(new Date().getTime() - dLastMandatoryUpdateDialogShown.getTime());
//			long diffDays = diff / (24 * 60 * 60 * 1000);
//			long diffHours = diff / (60 * 60 * 1000);
//			long diffMin = diff / (60 * 1000);
//			
//			
//			Log.e("alex", "Mandatory get HOURS: " + diffHours);
//			
//			if(diffHours > Definitions.MANDATORY_UPDATE_INTERVAL)
//			{
//				showMandatoryUpdateDialog();
//			}
//		}
//	}
//	
//	private Date getLastMandatoryUpdateDialogShown() {
//	    
//		String key = Definitions.MANDATORY_UPDATE_KEY;
//		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
//		if (!prefs.contains(key + "_value")) {
//	        return null;
//	    }
//	    Calendar calendar = Calendar.getInstance();
//	    calendar.setTimeInMillis(prefs.getLong(key + "_value", 0));
//	    
//	    Log.e("alex", "Mandatory get from pref: " + calendar.getTime());
//	    
//	    return calendar.getTime();
//	}
//
//	private void putLastMandatoryUpdateDialogShownDate() {
//		
//		String key = Definitions.MANDATORY_UPDATE_KEY;
//		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);		
//		Date date = new Date();		
//		
//	    SharedPreferences.Editor editor = prefs.edit();
//	    editor.putLong(key + "_value", date.getTime());
//	    
//	    //Log.e("alex", "Mandatory set to pref: " + date.getTime());
//	    
//	    editor.commit();
//	}
//	
//	private void showMandatoryUpdateDialog()
//	{
//		putLastMandatoryUpdateDialogShownDate();
//		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//		builder.setTitle("עדכון דחוף");
//		builder.setMessage(Definitions.MANDATORY_UPDATE_MESSAGE).setCancelable(false)
//				.setPositiveButton("OK", new DialogInterface.OnClickListener()
//				{
//					public void onClick(DialogInterface dialog, int id)
//					{
//						try
//						{
//							startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getActivity().getPackageName())));
//						}
//						catch (android.content.ActivityNotFoundException anfe)
//						{
//							startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id="
//									+ getActivity().getPackageName())));
//						}
//					}
//				})
//				.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
//				{
//					public void onClick(DialogInterface dialog, int which)
//					{
//						dialog.dismiss();
//						return;
//					}
//				});
//				;
//		AlertDialog alert = builder.create();
//		alert.show();
//
//	}
	
	@Override
	public void onTaskCompleted(Map<String, Map<String, String>> map)
	{
		if(Definitions.wallafusion) {
			try {
				//showTopBanner(map);

				showBottomBanner(map);
				showRichMedia(map);
				loadWallaFusionDCScripts(map);
			} catch (Exception ex) {
				Log.e("alex", "Error Walla Bottom Floating Banner");
			}
		}
	}
	
	
	private void showRichMedia(final Map<String, Map<String, String>> map)
	{		
		if(map == null || !map.containsKey(Definitions.WALLA_SPACE_N_RM1)){
			return;
		}
		
		Map<String, String> m = map.get(Definitions.WALLA_SPACE_N_RM1);
		
		if(m == null){
			return;
		}
		
		Log.e("alex", "showRichMedia...");
		
		Handler handler1 = new Handler(); // Show RichMedia
		for (int a = 1; a<=10 ;a++) {
		    handler1.postDelayed(new Runnable() {

		         @Override
		         public void run() {
		             if(DataStore.getInstance().getSplashScreenClosed() && !bRichMediaShown)
		             {
		            	DataStore.getInstance().setSplashScreenClosed(false);
		            	bRichMediaShown = true;
		            	String[] arrWallaData = Utils.getWallaDataForFolderL(uriToParse);
		            	 
    	 				if(arrWallaData[0] != null && arrWallaData[0] != "")
    	 				{
    	 					sWallaPage = arrWallaData[0];
    	 				}
    	 				
    	 				if(arrWallaData[1] != null && arrWallaData[1] != "")
    	 				{
    	 					sWallaLayout = arrWallaData[1];
    	 				}
    	 				
    	 				Log.e("alex", "showRichMedia sWallaPage=" + sWallaPage + " sWallaLayout=" + sWallaLayout);
    	 								
    	 				Intent splashScreenIntent = new Intent(activity, SplashScreen.class);
    	 				splashScreenIntent.putExtra("fromFolderRichMedia", true);
    	 				splashScreenIntent.putExtra("wallaPage", sWallaPage);
    	 				splashScreenIntent.putExtra("wallaLayout", sWallaLayout);
    	 				splashScreenIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
    	 								
    	 				startActivity(splashScreenIntent);	
		             }
		        	 
		         }
		         }, 1000 * a);
		} 
			
		
//		ScheduledExecutorService sesSplash =  Executors.newSingleThreadScheduledExecutor();
		
//		Runnable task = new Runnable() {
//		    public void run() {
//		    	Log.e("alex", "showRichMedia...");
//				String[] arrWallaData = Utils.getWallaDataForFolderL(uriToParse);
//
//				if(arrWallaData[0] != null && arrWallaData[0] != "")
//				{
//					sWallaPage = arrWallaData[0];
//				}
//				
//				if(arrWallaData[1] != null && arrWallaData[1] != "")
//				{
//					sWallaLayout = arrWallaData[1];
//				}
//				
//				Log.e("alex", "showRichMedia sWallaPage=" + sWallaPage + " sWallaLayout=" + sWallaLayout);
//								
//				Intent splashScreenIntent = new Intent(activity, SplashScreen.class);
//				splashScreenIntent.putExtra("fromFolderRichMedia", true);
//				splashScreenIntent.putExtra("wallaPage", sWallaPage);
//				splashScreenIntent.putExtra("wallaLayout", sWallaLayout);
//				splashScreenIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//								
//				startActivity(splashScreenIntent);		
//		    }
//		  };
//		  sesSplash.schedule(task, 10, TimeUnit.SECONDS);
		
	}
	
	private void showBottomBanner(Map<String, Map<String, String>> map)
	{
		String space = Definitions.WALLA_SPACE_FLOATING_BOTTOM;// "n_floating_banner";
		
		//Log.e("alex", "showBottomBanner: " + getView());
		
		frameAdViewContainer_Pull_to_refresh = (RelativeLayout) getView().findViewById(R.id.frameAdViewContainer_Pull_to_refresh);	
		
		if(map == null || !map.containsKey(space)){return;}
		
		Map<String, String> m = map.get(space);
		
		if(m == null){
			frameAdViewContainer_Pull_to_refresh.setVisibility(View.GONE);
			return;
		}

		Log.e("alex", "GGGGG Loading Bottom Banner!!!");
		
		WallaAdvParser.drawWallaBanner(frameAdViewContainer_Pull_to_refresh, context, m);
	}	
	
	private void showTopBanner(Map<String, Map<String, String>> map)
	{
		if (isMainMador())
		{
			try
			{
				String space = Definitions.WALLA_SPACE_TOP_FLOAT; //"n_slider";
				if(map == null || !map.containsKey(space)){return;}
				
				Map<String, String> m = map.get(space);
				if(m == null){
					frameTopAdViewContainer_Pull_to_refresh.setVisibility(View.GONE);
					return;
				}				
				
				WebView webView = new WebView(context);
				webView.loadData("<html><h1>FFFFFFFF</h1></html>", "text/html; charset=utf-8", "UTF-8");
				rlTopHeaderBanner.removeAllViews();
				rlTopHeaderBanner.addView(webView);
				rlTopHeaderBanner.setVisibility(View.VISIBLE);
				
				Log.e("alex", "AAAB1");
				

			}
			catch(Exception ex)
			{
				Log.e("alex", "AAAB Error: " + ex);
			}
		}
	}
	
	private void loadWallaFusionDCScripts(Map<String, Map<String, String>> map)
	{
		String space = "dc1";
		
		if(map == null || !map.containsKey(space)){return;}

		Map<String, String> m = map.get(space);
		
		if(m == null){return;}
	
		String Payload = m.get("Payload");
		
		Log.e("alex", "loadWallaFusionDCScripts1: " + Payload);
		
		frameWallaFusionDCScripts = (RelativeLayout) getView().findViewById(R.id.frameWallaFusionDCScripts);
		WallaAdvParser.loadSimplyDataIntoWebView(frameWallaFusionDCScripts, context, Payload);
		frameWallaFusionDCScripts.setVisibility(View.VISIBLE);
		Log.e("alex", "loadWallaFusionDCScripts1 done");
	}
}
