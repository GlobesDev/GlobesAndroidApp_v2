package il.co.globes.android;

import android.app.*;
import android.content.*;
import android.database.Cursor;
import android.net.Uri;
import android.os.*;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.*;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.webkit.WebView;
import android.widget.*;
import com.facebook.AppEventsLogger;
import com.google.android.apps.analytics.GoogleAnalyticsTracker;
import com.google.android.gms.ads.doubleclick.PublisherAdView;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import il.co.globes.android.fragments.TickerPreferencesFragment;
import il.co.globes.android.ima.v3.player.PlayerImaActivity;
import il.co.globes.android.interfaces.GlobesListener;
import il.co.globes.android.objects.*;
import il.co.globes.android.swipeListView.SwipeListView;
import org.apache.http.client.ClientProtocolException;
import tv.justad.sdk.view.JustAdView;
import tv.justad.sdk.view.JustAdViewDelegate;
import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

public class MainActivity extends ListActivity implements
        uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener,
        GlobesListener {
    public static Boolean isActive = false;
    private final static long REFRESH_LOOPER_ITEMS_INTERVAL = 7 * 60 * 1000;
    SwipeListView list;

    // ListView list;
    LazyAdapter adapter;
    NewsSet parsedNewsSet;
    Activity activity = this;
    String uriToParse, caller, parser;
    Context context;
    UUID unique;
    Intent intent;
    Bundle savedInstance;
    // google analytics tracker
    GoogleAnalyticsTracker tracker;
    protected ProgressDialog pd;

    /**
     * The view to show the ad.
     */
    // DfpAdView dfpAdView;

    PublisherAdView adView;

    /**
     * richMedia
     */
    JustAdView jaAdView;

    /**
     * The TAG.
     */
    private final String TAG = "MainActivity";

    /**
     * Push dialog - first time showing
     */
    AlertDialog alertDialog;

    /**
     * Shared prefs and boolean for first time app start need to show push
     * dialog
     */
    SharedPreferences sharedPrefs;

    /**
     * push notifications & scheme
     */
    String pushNotificationDocID, schemeURIShare;
    boolean isFromScheme;

    int showPushDialog;
    int showMuteNotificationsDialog;
    final int SHOW = 1;
    final int DONTSHOW = 2;

    static final int DONE_PARSING = 0;
    static final int ERROR_PARSING = 1;
    static final int DONE_GETTING_BANNER = 2;
    static final int ERROR_GETTING_BANNER = 3;

    public Uri uriVideo = null;

    final Activity mainActivity = this;

    /**
     * Ticker fields
     */
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
    private PullToRefreshLayout mPullToRefreshLayout;

    protected Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            if (msg.what == DONE_PARSING) {
                adapter = new LazyAdapter(activity, parsedNewsSet.itemHolder);
                list.setAdapter(adapter);

                /**
                 * Set a listener to be invoked when the list should be *
                 * refreshed -rotemm.
                 */
                // ((PullToRefreshListView)
                // getListView()).setOnRefreshListener(new OnRefreshListener()
                // ((SwipeListView) getListView()).setOnRefreshListener(new
                // OnRefreshListener()
                // {
                // @Override
                // public void onRefresh()
                // {
                // new GetDataTask().execute();
                // }
                // });

                try {
                    pd.dismiss();
                } catch (Exception e) {
                }
            } else if (msg.what == ERROR_PARSING) {
                try {
                    pd.dismiss();
                } catch (Exception e) {
                }
                Toast toast = Toast.makeText(context, Definitions.ErrorLoading, Toast.LENGTH_LONG);
                toast.show();
            }

        }

    };
    private Article articleGlobal;

    @Override
    public void onCreate(Bundle icicle) {

        super.onCreate(icicle);
        savedInstance = icicle;

        unique = UUID.randomUUID();
        context = getApplicationContext();
        looper = Ticker.getInstance(context);
        intent = getIntent();
        mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // TODO add tracking later
        tracker = GoogleAnalyticsTracker.getInstance();

        // ****** shared prefs with dialogs ****//
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        // case first time , no value so it will receive SHOW
        showPushDialog = sharedPrefs.getInt("showPushDialog", SHOW);
        if (showPushDialog == SHOW) {
            // save the value for DONTSHOW for next time
            savePrefsIntValue("showPushDialog", DONTSHOW);
        }
        // handling the mutNotifications dialog
        showMuteNotificationsDialog = sharedPrefs.getInt("showMuteNotificationsDialog", SHOW);
        if (showMuteNotificationsDialog == SHOW) {
            // save the value for DONTSHOW for next time
            savePrefsIntValue("showMuteNotificationsDialog", DONTSHOW);
        }
        // ***************//

		/*
         * push , get the pushNotificationDocID from intent will not be null
		 * only if pushed from splash
		 * 
		 * get values from scheme as well
		 */
        pushNotificationDocID = intent.getStringExtra("pushNotificationDocID");
        schemeURIShare = intent.getStringExtra(Definitions.KEY_BUNDLE_SCHEME_URI_SHARE);
        isFromScheme = intent.getBooleanExtra(Definitions.SCHEME_COMING_FROM_SCHEME, false);

        caller = intent.getStringExtra(Definitions.CALLER);
        parser = intent.getStringExtra(Definitions.PARSER);
        uriToParse = intent.getStringExtra("URI");

        setContentView();
        //Log.e("alex", "!!!!!!!!!!!!!!!!!!!!!!!!! + " + uriToParse);
        setInitialData(uriToParse, parser);

        /** init banner dfpAdView */
//		initAdView();

        // a call to init the rich media has been canceld due to Alon's request
        // on the 25.8.13
        // initRichMedia();

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (this.getClass().equals(MainActivity.class)) {
            stopAutoScrolling();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (this.getClass().equals(MainActivity.class)) {
            addLooperItemsToView();
        }
        if (UtilsWebServices.checkInternetConnection(context)) {
            AppEventsLogger.activateApp(context, Definitions.FACEBOOK_APP_ID);

            /**
             * init JustAdTvRichMedia if needed , checked Via
             * checkNeedForRichMediaAd@RichMediaAd.class
             */
            // if (RichMediaAd.getInstance(context).checkNeedForRichMediaAd())
            // {
            // loadRichMedia();
            // }
        }

    }

    /**
     * Created to init the rich Media jaAdview more efficient protected because
     * need to override at other activities
     */
    protected void loadRichMedia() {
        jaAdView.initAd();

    }

    @Override
    protected void onStart() {
        //		EasyTracker.getInstance(this).activityStart(this);

        super.onStart();
        isActive = true;

    }

    @Override
    protected void onStop() {
        //		EasyTracker.getInstance(this).activityStop(this);

        super.onStop();
        isActive = false;

    }

    void setContentView() {
        setContentView(R.layout.pull_to_refresh);

//		if (this.getClass().equals(MainActivity.class)) initLooperUi();

        ViewGroup viewGroup = (ViewGroup) getWindow().getDecorView().getRootView();
        // We need to create a PullToRefreshLayout manually
        mPullToRefreshLayout = new PullToRefreshLayout(this);

        // We can now setup the PullToRefreshLayout
        ActionBarPullToRefresh.from(this)

                // We need to insert the PullToRefreshLayout into the Fragment's
                // ViewGroup
                .insertLayoutInto(viewGroup)

                // We need to mark the ListView and it's Empty View as pullable
                // This is because they are not dirent children of the ViewGroup
                .theseChildrenArePullable(getListView(), getListView().getEmptyView(), viewGroup.getChildAt(0))

                // Set the OnRefreshListener
                .listener(this)
                // Finally commit the setup to our PullToRefreshLayout
                .setup(mPullToRefreshLayout);
    }

    private void getScrollMaxAndStartAutoScroll() {
        ViewTreeObserver vto = horizontalOuterLayout.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                horizontalOuterLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                getScrollMaxAmount();
                startAutoScrolling();
            }
        });
    }

    /**
     * Inits the Ticker UI & listeners also populates the text with given looper
     * items
     */
    private void initLooperUi() {
        linearLayoutTickerContainer = (LinearLayout) findViewById(R.id.linearLayout_Ticker_container);
        linearLayoutTickerContainer.setVisibility(View.VISIBLE);
        horizontalScrollview = (HorizontalScrollView) findViewById(R.id.horiztonal_scrollview_id);
        horizontalOuterLayout = (LinearLayout) findViewById(R.id.horiztonal_outer_layout_id);

        horizontalScrollview.setHorizontalScrollBarEnabled(false);

        btnOpenLooperPrefsActivity = (ImageButton) findViewById(R.id.Button_open_looper_preferences);
        btnOpenLooperPrefsActivity.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, TickerPreferencesFragment.class);
                startActivity(intent);
            }
        });

        // init refresh timer task

        if (refreshLooperItemsTimer == null) {
            refreshLooperItemsTimer = new Timer();
            final Runnable refresh_Tick = new Runnable() {
                public void run() {
                    Log.i(TAG, "Time to refresh looper items ");
                    new AsyncTask<Void, Void, Void>() {

                        @Override
                        protected Void doInBackground(Void... params) {
                            looper.refreshLooperItemsFromURL();
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Void result) {
                            addLooperItemsToView();
                        }
                    }.execute();

                }
            };

            if (refreshSchedule != null) {
                refreshSchedule.cancel();
                refreshSchedule = null;
            }
            refreshSchedule = new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(refresh_Tick);
                }
            };

            refreshLooperItemsTimer.schedule(refreshSchedule, REFRESH_LOOPER_ITEMS_INTERVAL, REFRESH_LOOPER_ITEMS_INTERVAL);
        }
    }

    private void getScrollMaxAmount() {
        int minus = -(reductionParam * (converDpToPx(192)));
        int actualWidth = (horizontalOuterLayout.getMeasuredWidth() + minus);
        scrollMax = actualWidth;
    }

    private void startAutoScrolling() {
        if (scrollTimer == null) {
            scrollTimer = new Timer();
            final Runnable Timer_Tick = new Runnable() {
                public void run() {
                    moveScrollView();
                }
            };

            if (scrollerSchedule != null) {
                scrollerSchedule.cancel();
                scrollerSchedule = null;
            }
            scrollerSchedule = new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(Timer_Tick);
                }
            };

            scrollTimer.schedule(scrollerSchedule, 30, 30);

        }
    }

    private void moveScrollView() {
        scrollPos = (int) (horizontalScrollview.getScrollX() + 1.0);
        if (scrollPos >= scrollMax) {
            scrollPos = 0;
        }
        horizontalScrollview.scrollTo(scrollPos, 0);

    }

    private void stopAutoScrolling() {
        if (scrollTimer != null) {
            scrollTimer.cancel();
            scrollTimer = null;
        }
    }

    /**
     * Adds the TextViewsContainer to view.
     */
    public void addLooperItemsToView() {
        horizontalOuterLayout.removeAllViews();
        ArrayList<TickerItem> items = looper.getItems();
        if (items != null && items.size() > 0) {
            for (int i = 0; i < items.size(); i++) {
                try {
                    final LinearLayout layout = getLooperItemView(items.get(i));
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(converDpToPx(192), LinearLayout.LayoutParams.WRAP_CONTENT);
                    layout.setLayoutParams(params);
                    horizontalOuterLayout.addView(layout);
                } catch (Exception e) {
                    if (e != null) {
                        Log.e(TAG, "addLooperItemsToView", e);
                    }
                }
            }
            // Add first items in list to match the size of the screen frame
            for (int j = 0; j < 3; j++) {
                try {
                    if (j < items.size()) {
                        final LinearLayout layout = getLooperItemView(items.get(j));
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(converDpToPx(192), LinearLayout.LayoutParams.WRAP_CONTENT);
                        layout.setLayoutParams(params);
                        horizontalOuterLayout.addView(layout);
                    }
                } catch (Exception e) {
                    if (e != null) {
                        Log.e(TAG, "addLooperItemsToView()", e);
                    }
                }
            }
            getScrollMaxAndStartAutoScroll();
        }
    }

    /**
     * Factory pattern for
     *
     * @param item - the item to create the view from
     * @return LinearLayout to be added to horizontalOuterLayout
     */
    private LinearLayout getLooperItemView(TickerItem item) {
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
                || looperItem.getPercentage_change().equals("%"))
            textViewPrecentageChanged.setVisibility(View.INVISIBLE);

        // set last & nameHE
        textViewLast.setText(looperItem.getLast());
        textViewNameHe.setText(looperItem.getNameHE());
        layout.setTag(looperItem);
        layout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                TickerItem item = (TickerItem) v.getTag();
                Intent intent = new Intent(MainActivity.this, ShareActivity.class);
                intent.putExtra(Definitions.CALLER, Definitions.SHARES);
                intent.putExtra(Definitions.PARSER, Definitions.SHARES);
                intent.putExtra("shareId", item.getInsturmentID());
                intent.putExtra("feederId", item.getFeeder());
                intent.putExtra("name", item.getNameHE());
                intent.putExtra("URI", GlobesURL.URLSharePage.replace("XXXX", item.getFeeder()).replace("YYYY", item.getInsturmentID()));
                startActivity(intent);
            }
        });

        return layout;
    }

    /**
     * initializing the AdView + AdListener
     */
    void initAdView() {
        // Look up the DfpAdView as a resource and load a request.

//		adView = (PublisherAdView) this.findViewById(R.id.adView);
//
//		adView.setAdListener(new AdListener()
//		{
//			public void onAdLoaded()
//			{
//				adView.setBackgroundColor(Color.TRANSPARENT);
//				if (adView.getVisibility() != View.VISIBLE)
//				{
//					adView.setVisibility(View.VISIBLE);
//				}
//			}
//			public void onAdFailedToLoad(int errorCode)
//			{
//				// Didnt receive Ad
//				Log.d(TAG, "Ad on main Activity Failed: " + errorCode);
//
//				// remove the banner Ad
//				adView.setVisibility(View.GONE);
//
//				if (tracker != null)
//				{
//					// tracker.trackPageView("/" + TAG +
//					// Definitions.AD_FAILURE_TAG_BOTTOM_BANNER_PORTRAIT +
//					// errorCode);
//				}
//			}
//		});
//
//		PublisherAdRequest request = new PublisherAdRequest.Builder().build();
//		adView.loadAd(request);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (adapter != null && list != null) {
            adapter.imageLoader.clearCache();
            adapter.imageLoader.stopThread();
            // list.setAdapter(null);
        }

        // Clear timers and Timer Tasks
        clearTimersAndTasks();

        handler.removeCallbacksAndMessages(null);
    }

    private void clearTimersAndTasks() {
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

    private void clearTimers(Timer timer) {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    private void clearTimerTaks(TimerTask timerTask) {
        if (timerTask != null) {
            timerTask.cancel();
            timerTask = null;
        }
    }

    // TODO eli no need the menu
    //	@Override
    //	public boolean onCreateOptionsMenu(Menu menu)
    //	{
    //		MenuInflater inflater = getMenuInflater();
    //		inflater.inflate(R.layout.main_screen_menu, menu);
    //		return true;
    //	}
    //
    //	@Override
    //	public boolean onOptionsItemSelected(MenuItem item)
    //	{
    //		Intent i;
    //		switch (item.getItemId())
    //		{
    //			case R.id.menuItemFinancialPortal :
    //				Intent financialPortalIntent = new Intent(mainActivity, FinancialPortalActivity.class);
    //				startActivity(financialPortalIntent);
    //				return true;
    //			case R.id.menuItemPreferences :
    //				Intent preferencesIntent = new Intent(mainActivity, GlobesPreferences.class);
    //				startActivity(preferencesIntent);
    //				return true;
    //			case R.id.menuItemAbout :
    //				Intent aboutActivityIntent = new Intent(mainActivity, AboutActivity.class);
    //				startActivity(aboutActivityIntent);
    //				return true;
    //			case R.id.menuItemSearch :
    //				startDocumentSearchListActivity();
    //				return true;
    //			case R.id.menuItemRedEmail :
    //				openRedEmailDialog();
    //				return true;
    //			case R.id.menuItemTV :
    //				startTVActivity();
    //				return true;
    //			default :
    //				return super.onOptionsItemSelected(item);
    //		}
    //	}

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_SEARCH) {
            startDocumentSearchListActivity();
            return false;
        } else {
            return super.onKeyUp(keyCode, event);
        }
    }

    // private void sendGemiusData()
    // {
    // Intent intent = new Intent("com.gemius.sdk.MOBILEPLUGIN");
    // intent.putExtra(MobilePlugin.IDENTIFIER, Definitions.GEMIUS_ID);
    // startService(intent);
    // }

    void startDocumentSearchListActivity() {
        Intent documentSearchListActivityIntent = new Intent(mainActivity, DocumentSearchListActivity.class);
        documentSearchListActivityIntent.putExtra("URI", GlobesURL.URLSearchArticle);
        documentSearchListActivityIntent.putExtra(Definitions.CALLER, Definitions.MAINSCREEN);
        documentSearchListActivityIntent.putExtra(Definitions.PARSER, Definitions.SEARCH);
        startActivity(documentSearchListActivityIntent);
    }

//	void startTVActivity()
//	{
//		Intent openClipsList = new Intent(this, SectionListActivity.class);
//
//		openClipsList.putExtra("URI", GlobesURL.URLClips);
//		openClipsList.putExtra(Definitions.CALLER, Definitions.SECTIONS);
//		openClipsList.putExtra(Definitions.PARSER, Definitions.MAINSCREEN);
//		openClipsList.putExtra(Definitions.HEADER, "גלובס TV");
//		startActivity(openClipsList);
//	}

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        //Log.e("alex","onListItemClick");
        super.onListItemClick(l, v, position, id);
        /* Align position, due to the use of PullToRefreshListView. -rotemm */
        position--;
        if (parsedNewsSet.itemHolder.get(position) instanceof Article) {

            // get instance of the article
            Article article = (Article) parsedNewsSet.itemHolder.get(position);
            articleGlobal = (Article) parsedNewsSet.itemHolder.get(position);

            // check here if article has LINK field and open the link in
            // webView or browser. the LINK field should be set via MAINParser
            // at the article object
            // This is katava pirsumit and need to be opened in Browser
            String url = article.getUrlMarketingArticle();
            // url from regular video & html5 video
            String videoURL = article.getUrl();
            String videoURLhtml5 = article.getUrlHTML5();
            if (url != null && url.length() > 0) {
                if (!(url.contains("http")) && !(url.contains("HTTP"))) {
                    url = "http://www.globes.co.il";
                }
                String openInWeb = "";
                int indexOfOpenWeb = url.indexOf("Open_web=");

                if (indexOfOpenWeb != -1) openInWeb = new String(url.substring(indexOfOpenWeb + 9));

                if (openInWeb.equals("true")) {
                    // open in browser
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);

                } else if (openInWeb.equals("false")) {
                    // open in WebView via RowAdActivity
                    Intent i = new Intent(MainActivity.this, RowAdActivity.class);
                    i.putExtra(RowAdActivity.KEY_URL_AD, url);
                    startActivity(i);
                }

            }
            // if article is a video/video HTML5 article open straight ahead
            else if ((videoURL != null && videoURL.length() > 0) || (videoURLhtml5 != null && videoURLhtml5.length() > 0)) {
                boolean isHTML5Video = article.isHTML5video();
                if (isHTML5Video) {
                    openVideoInMediaPlayerOrBrowser(videoURLhtml5, true, article.getDoc_id());
                } else {
                    openVideoInMediaPlayerOrBrowser(videoURL, false, article.getDoc_id());
                }
            } else {
                Log.e("alex", "PushAppsTest v3 url:" + url);
                Intent intent = new Intent(this, DocumentActivity_new.class);
                DataContext.Instance().SetArticleList(unique.toString(), parsedNewsSet);
                intent.putExtra("position", position);
                intent.putExtra("parentId", unique.toString());
                startActivity(intent);
            }

        } else if (parsedNewsSet.itemHolder.get(position).getClass() == InnerGroup.class) {
            String sectionId = ((InnerGroup) parsedNewsSet.itemHolder.get(position)).getNode_id();
            String uri;
            if (sectionId.equals(GlobesURL.TVNode)) {
                uri = GlobesURL.URLClips;
            } else {
                uri = GlobesURL.URLSections + sectionId;
            }
            Intent intent = new Intent(this, SectionListActivity.class);
            intent.putExtra("URI", uri);
            intent.putExtra(Definitions.CALLER, Definitions.SECTIONS);
            if (sectionId.equals("10002"))// opinions
            {
                intent.putExtra(Definitions.PARSER, Definitions.OPINIONS);
            } else {
                intent.putExtra(Definitions.PARSER, Definitions.MAINSCREEN);
            }
            intent.putExtra(Definitions.HEADER, ((InnerGroup) parsedNewsSet.itemHolder.get(position)).getTitle());
            startActivity(intent);
        } else if (parsedNewsSet.itemHolder.get(position) instanceof Banner) {
            if (((Banner) parsedNewsSet.itemHolder.get(position)).getLinkURL() != null) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setData(android.net.Uri.parse(((Banner) parsedNewsSet.itemHolder.get(position)).getLinkURL()));
                context.startActivity(intent);
            }
        }
    }

    void setInitialData(final String uriToParse, final String parser) {
        pd = ProgressDialog.show(this, "", Definitions.Loading, true, false);

        new AsyncTask<Void, Void, Integer>() {

            @Override
            protected void onPreExecute() {
                list = (SwipeListView) findViewById(android.R.id.list);
            }

            ;

            @Override
            protected Integer doInBackground(Void... params) {
                int res;
                try {
                    URL url = new URL(uriToParse);
                    parsedNewsSet = NewsSet.parseURL(url, parser);
                    // TODO test removing the creation of banner data
                    // createBannerData();
                    res = DONE_PARSING;

                } catch (Exception e) {
                    e.printStackTrace();
                    res = ERROR_PARSING;
                }
                return res;
            }

            @Override
            protected void onPostExecute(Integer result) {
                try {
                    pd.dismiss();
                } catch (Exception e) {
                }
                if (result == DONE_PARSING) {
                    adapter = new LazyAdapter(activity, parsedNewsSet.itemHolder);
                    list.setAdapter(adapter);

                    /**
                     * Set a listener to be invoked when the list should be *
                     * refreshed -rotemm.
                     */
                    // ((PullToRefreshListView)
                    // getListView()).setOnRefreshListener(new
                    // OnRefreshListener()
                    // ((SwipeListView) getListView()).setOnRefreshListener(new
                    // OnRefreshListener()
                    //
                    // {
                    // @Override
                    // public void onRefresh()
                    // {
                    // new GetDataTask().execute();
                    // }
                    // });

                    // TODO Test Push coming from push
                    if (pushNotificationDocID != null) {
                        Log.e("alex", "PushAppsTest v4 pushNotificationDocID:" + pushNotificationDocID);
                        Intent i = new Intent(MainActivity.this, DocumentActivity_new.class);
                        i.putExtra("pushNotificationDocID", pushNotificationDocID);
                        i.putExtra(Definitions.SCHEME_COMING_FROM_SCHEME, isFromScheme);
                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(i);
                    } else if (schemeURIShare != null) {
                        Intent i = new Intent(MainActivity.this, ShareActivity.class);
                        i.putExtra(Definitions.CALLER, Definitions.SHARES);
                        i.putExtra(Definitions.PARSER, Definitions.SHARES);
                        i.putExtra("URI", schemeURIShare);
                        startActivity(i);

                    }

                    // if (showPushDialog == SHOW && uriToParse != null &&
                    // uriToParse.equals(Definitions.URLMainScreen) && parser !=
                    // null
                    // && parser.equals(Definitions.MAINSCREEN))
                    // {
                    // showDialog("שירות התראות", getMsgForPushDialog(),
                    // "הגדרות", "המשך");
                    // }

                    if (uriToParse != null && uriToParse.equals(GlobesURL.URLMainScreen) && parser != null && parser.equals(Definitions.MAINSCREEN)) {
                        // If we need to show the first time push dialog , we
                        // dont show the second dialog
                        if (showPushDialog == SHOW) {
                            showDialog("שירות התראות", getMsgForPushDialog(), "הגדרות", "המשך");

                        } else if (showPushDialog == DONTSHOW && showMuteNotificationsDialog == SHOW) { // here we show the second dialog only if the first
                            // push has already been shown

                            showDialog("שירות צלילי התראות", getMsgForMuteNotifications(), "הגדרות", "המשך");
                        }

                    }

                } else if (result == ERROR_PARSING) {
                    Toast toast = Toast.makeText(context, Definitions.ErrorLoading, Toast.LENGTH_LONG);
                    toast.show();
                }
            }

        }.execute();
    }

    void createBannerData() {
        // URL listBannerURL;
        // int numOfBanners = 4;
        // Vector<Banner> banners = new Vector<Banner>();
        // for (int i = 0; i < numOfBanners; i++)
        // {
        // try
        // {
        // listBannerURL = new URL(Utils.getBannerParameters(context,
        // Definitions.URLBannerSmall, 462, 100));
        // Banner bannerSmallForList = Banner.getBanner(listBannerURL);
        // banners.add(bannerSmallForList);
        // }
        // catch (Exception e)
        // {
        // e.printStackTrace();
        // }
        // }
        // // banner data for MainScreen
        // int bannerRowIndex;
        // InnerGroup globesTV = new InnerGroup("גלובס TV");
        // InnerGroup realEstate = new InnerGroup("נדל\"ן ותשתיות");
        // InnerGroup transport = new InnerGroup("רכב");
        //
        // try
        // {
        // if (banners.elementAt(0) != null &&
        // banners.elementAt(0).getBannerURL() != null)
        // {
        // // parsedNewsSet.itemHolder.add(2, banners.elementAt(0));
        // }
        // bannerRowIndex = parsedNewsSet.itemHolder.indexOf(globesTV);
        // if (bannerRowIndex != -1 && banners.elementAt(1) != null &&
        // banners.elementAt(1).getBannerURL() != null)
        // {
        // parsedNewsSet.itemHolder.add(bannerRowIndex, banners.elementAt(1));
        // }
        // bannerRowIndex = parsedNewsSet.itemHolder.indexOf(realEstate);
        // if (bannerRowIndex != -1 && banners.elementAt(2) != null &&
        // banners.elementAt(2).getBannerURL() != null)
        // {
        // parsedNewsSet.itemHolder.add(bannerRowIndex, banners.elementAt(2));
        // }
        // bannerRowIndex = parsedNewsSet.itemHolder.indexOf(transport);
        // if (bannerRowIndex != -1 && banners.elementAt(3) != null &&
        // banners.elementAt(3).getBannerURL() != null)
        // {
        // parsedNewsSet.itemHolder.add(bannerRowIndex, banners.elementAt(3));
        // }
        // // /////////////////////*for testing
        // // purposes*//////////////////////////
        // // Banner bannerTest = new Banner();
        // //
        // bannerTest.setBannerURL("http://www.google.com/logos/classicplus.png");
        // // bannerTest.setLinkURL("http://www.google.com");
        // // parsedNewsSet.itemHolder.add(2, bannerTest);
        // // //////////////////////////////////////////
        // }
        // catch (ArrayIndexOutOfBoundsException e)
        // {
        //
        // }
    }

    void addStaticItemsToNewsSet() {

    }

    protected class GetDataTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {
                URL url = new URL(uriToParse);
                parsedNewsSet = NewsSet.parseURL(url, parser);

                addStaticItemsToNewsSet();
            } catch (Exception e) {
            }
            return null;

        }

        @Override
        protected void onPostExecute(Void result) {
            list = (SwipeListView) findViewById(android.R.id.list);
            Log.e("eli", "1124");
            adapter = new LazyAdapter(activity, parsedNewsSet.itemHolder);
            list.setAdapter(adapter);
            mPullToRefreshLayout = new PullToRefreshLayout(getApplicationContext());

            ViewGroup viewGroup = (ViewGroup) getWindow().getDecorView().getRootView();
            // We need to create a PullToRefreshLayout manually
            View vTest = findViewById(android.R.id.content);

            // We can now setup the PullToRefreshLayout
            ActionBarPullToRefresh.from(MainActivity.this)

                    // We need to insert the PullToRefreshLayout into the Fragment's
                    // ViewGroup
                    .insertLayoutInto(viewGroup)

                    // We need to mark the ListView and it's Empty View as
                    // pullable
                    // This is because they are not dirent children of the
                    // ViewGroup
                    .theseChildrenArePullable(getListView(), getListView().getEmptyView(), vTest)

                    // Set the OnRefreshListener
                    .listener(MainActivity.this)
                    // Finally commit the setup to our PullToRefreshLayout
                    .setup(mPullToRefreshLayout);
            mPullToRefreshLayout.setRefreshComplete();

            super.onPostExecute(result);
        }

        @Override
        protected void onCancelled() {
            // reset the UI
            mPullToRefreshLayout.setRefreshComplete();

        }
    }

    public void openVideoInMediaPlayerOrBrowser(final String url, final boolean isHTML5, final String docId) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    openVideoInMediaPlayerOrBrowserNoThread(url, isHTML5, docId);
                } catch (Exception e) {

                }

                handler.post(new Runnable() {
                    @Override
                    public void run() {

                    }
                });
            }
        };
        new Thread(runnable).start();
    }

    void openVideoInMediaPlayerOrBrowserNoThread(String url, boolean isHTML5, String docId) {

        String videoUrl;

        try {
            // http://www.cast-tv.biz/play/playandroid22753.asp?movId=gpfjgh&clid=22753&media=yes&autoplay=true&AndroidType=html5
            if (isHTML5) {
                videoUrl = url;
                // remove the apple type from url
                if (videoUrl.contains("&AppleType=html")) {
                    videoUrl = videoUrl.replace("&AppleType=html", "");
                }
            } else {
                if (!Definitions.mediaPlayerInBrowser) {
                    /** get the device info and add it to the URL */
                    url = getDeviceDetailsForVideoUrl(url);

                    if (url.contains("http://www.cast-tv.biz/play/")) {
                        if (Definitions.toUseGIMA) {
                            videoUrl = Utils.resolveRedirect("http://www.cast-tv.biz/play/" + url/*+ Utils.resolveRedirect(url, true)*/, false);
                        } else {
                            videoUrl = Utils.resolveRedirect("http://www.cast-tv.biz/play/" + Utils.resolveRedirect(url, true), false);
                        }
                    } else {
                        videoUrl = Utils.resolveRedirect("http://www.globes.co.il" + Utils.resolveRedirect(url, true), false);
                    }
                } else {
                    // no redirect after a talk with Rita & Yaniv
                    videoUrl = url;
                }
            }

            if (videoUrl != null && !videoUrl.contains("Failed_To_Get_Url")) {
                String name = articleGlobal.getTitle();
                String mador = MainSlidingActivity.getMainSlidingActivity().getActionBarTitle();
                mador = mador.compareTo("Globes Default") == 0 ? "ראשי" : mador;
                Utils.writeEventToGoogleAnalytics(this, "הפעלת וידאו", mador, name);

                Uri uri = Uri.parse(videoUrl);
                if (Definitions.toUseGIMA) {
                    // pre roll eli
                    //Intent i = new Intent(this, PlayVideoActivity.class);
                    Intent i = new Intent(this, PlayerImaActivity.class);
                    i.putExtra("videoURL", uri.toString());
                    i.putExtra("docId", docId);

                    startActivity(i);
                } else {
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    //					intent.setDataAndType(data, "video/mp4");
                    startActivity(intent);
                }
            }
        } catch (ClientProtocolException e) {
        } catch (IOException e) {
        }
    }

    public void btnShareVideo_onClick(View v) {
        FrameLayout vwParent = (FrameLayout) v.getParent();
        LinearLayout vwGrandparentRow = (LinearLayout) vwParent.getParent();

        int position = getListView().getPositionForView(vwGrandparentRow);
        position--;

        if (position >= 0) {
            if (parsedNewsSet.itemHolder.get(position) instanceof Article) {
                Intent shareArticle = new Intent();
                shareArticle.setAction(Intent.ACTION_SEND);
                String mimeType = "text/plain";
                shareArticle.setType(mimeType);
                shareArticle.putExtra(Intent.EXTRA_SUBJECT, ((Article) parsedNewsSet.itemHolder.get(position)).getTitle());

                shareArticle.putExtra(Intent.EXTRA_TEXT, Definitions.watchVideo + "\n\n" + GlobesURL.URLDocumentToShare
                        + ((Article) parsedNewsSet.itemHolder.get(position)).getDoc_id() + Definitions.fromPlatformAppAnalytics + "\n\n"
                        + Definitions.ShareString + " " + Definitions.shareGlobesWithQuates + "\n\n" + Definitions.shareCellAppsPart1 + " "
                        + Definitions.shareGlobesWithQuates + " " + Definitions.shareCellAppsPart2 + "\n");

                // TODO add tracking later
                //				tracker.trackPageView(GlobesURL.URLDocumentToShare + ((Article) parsedNewsSet.itemHolder.get(position)).getDoc_id()
                //						+ "from=" + Definitions.SHAREsuffix);
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

    void openRedEmailDialog() {
        showDialog(Definitions.RED_EMAIL_DIALOG);
    }

    @Override
    protected Dialog onCreateDialog(int dialogID) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        dialog.setContentView(R.layout.red_email_dialog);
        TextView tvEmailTitle = (TextView) dialog.findViewById(R.id.tvEmailTitle);
        Button btnEmailWrite = (Button) dialog.findViewById(R.id.btnEmailWrite);
        Button btnRedEmailClip = (Button) dialog.findViewById(R.id.btnRedEmailClip);
        Button btnRedEmailPic = (Button) dialog.findViewById(R.id.btnRedEmailPic);

        if (dialogID == Definitions.RED_EMAIL_DIALOG) {
            btnEmailWrite.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    startRedEmail("");
                    dialog.dismiss();
                }
            });

            btnRedEmailClip.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    dialog.dismiss();
                    showDialog(Definitions.SEND_CLIP_DIALOG);
                }
            });

            btnRedEmailPic.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    dialog.dismiss();
                    showDialog(Definitions.SEND_PIC_DIALOG);
                }
            });
        } else if (dialogID == Definitions.SEND_CLIP_DIALOG) {
            btnEmailWrite.setVisibility(View.GONE);

            tvEmailTitle.setText("בחר וידאו");
            btnRedEmailClip.setText("צלם וידאו");

            btnRedEmailClip.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    getAttachment(Definitions.CLIP_ATTACHMENT, Definitions.MAKE_ATTACHMENT);
                    dialog.dismiss();
                }
            });

            btnRedEmailPic.setText("בחר מגלריה");
            btnRedEmailPic.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    getAttachment(Definitions.CLIP_ATTACHMENT, Definitions.CHOOSE_ATTACHMENT);
                    dialog.dismiss();
                }
            });
        } else if (dialogID == Definitions.SEND_PIC_DIALOG) {
            tvEmailTitle.setText("בחר תמונה");

            btnEmailWrite.setVisibility(View.GONE);
            btnRedEmailClip.setText("צלם תמונה");
            btnRedEmailClip.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    getAttachment(Definitions.PIC_ATTACHMENT, Definitions.MAKE_ATTACHMENT);
                    dialog.dismiss();
                }
            });

            btnRedEmailPic.setText("בחר מגלריה");
            btnRedEmailPic.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    getAttachment(Definitions.PIC_ATTACHMENT, Definitions.CHOOSE_ATTACHMENT);
                    dialog.dismiss();
                }
            });
        }

        Button btnCancelDialog = (Button) dialog.findViewById(R.id.btnCancel);
        btnCancelDialog.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        return dialog;
    }

    protected void startRedEmail(String attachmentUri) {
        String mailBody = "אנא מלא את הפרטים הבאים:\nשם:\nטלפון:\nדואר אלקטרוני:\nתוכן:\n\n\n\n";
        Intent emailSend = new Intent();
        emailSend.setAction(Intent.ACTION_SEND);
        String mimeType = "message/rfc822";

        emailSend.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]
                {Definitions.RED_ALERT_MAIL_TARGET});

        // emailSend.putExtra(Intent.EXTRA_CC, R.string.redemail_globes_co_il);
        emailSend.putExtra(Intent.EXTRA_SUBJECT, "דואר אדום");

        emailSend.putExtra(Intent.EXTRA_TEXT, mailBody);
        emailSend.putExtra(Intent.EXTRA_TITLE, R.string.red_email);
        if (attachmentUri.contains(".")) {
            emailSend.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + attachmentUri));
            mimeType = "image/jpeg";
        }
        emailSend.setType(mimeType);
        startActivity(Intent.createChooser(emailSend, getString(R.string.do_you_have_news)));
    }

    protected void getAttachment(int attachmentType, int getAttacmentMode) {
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((resultCode == RESULT_OK)) {
            if (requestCode == Definitions.REQUEST_CLIP_CAPTURE) {
                Uri uriVideo1 = data.getData();

                String test = getVideoAttachmentPath(uriVideo1);

                startRedEmail(test);
            } else {
                String attachmentUri = data.getStringExtra("imageUri");
                startRedEmail(attachmentUri);
            }
        } else if ((resultCode == -2)) {

        }
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

    /**
     * Inits the RichMediaAd ...but doesn't load it needs to be loaded from
     * OnResume when necessary
     */
    protected void initRichMedia() {

        jaAdView = (JustAdView) findViewById(R.id.justAdView_RICH_MEDIA_pull_to_refresh);

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
        jaAdView.setDelegate(new JustAdViewDelegate() {

            @Override
            public void AdError(JustAdView adView, String message) {
                super.AdError(adView, message);
                Log.d(TAG, "Rich Ad Error: " + message);

                /**
                 * Error occured on loading the Ad 1.Log it 2.finish the
                 * Activity and resume the App
                 */
                jaAdView.setVisibility(View.GONE);
            }

            @Override
            public void AdLoaded(JustAdView adView) {
                super.AdLoaded(adView);
                Log.d(TAG, "Rich Ad loaded success: " + adView.toString());

                /** Ad loaded now we need to start it */
                // dfpAdView.setVisibility(View.GONE);
                adView.setVisibility(View.GONE);

                jaAdView.setVisibility(View.VISIBLE);
                jaAdView.startAd();
            }

            @Override
            public void AdStarted(JustAdView adView) {
                super.AdStarted(adView);
                Log.d(TAG, "Rich Ad started displaying: " + adView.toString());

            }

            @Override
            public void AdStopped(JustAdView adView) {
                super.AdStopped(adView);
                Log.d(TAG, "Ad stopped");

                /** Ad stopped from playing we finish the activity */
                jaAdView.setVisibility(View.GONE);
                // dfpAdView.setVisibility(View.VISIBLE);
                adView.setVisibility(View.VISIBLE);

            }

        });

    }

    /**
     * @param dp int amount of dp to be converted to pixels
     * @return px by the given dp depending on system info
     */
    protected int converDpToPx(int dp) {

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        float logicalDensity = dm.density;
        int res = ((int) (dp * logicalDensity + 0.5));
        return res;
    }

    /**
     * @param px int amount of px to be converted to dp
     * @return dp by the given px depending on system info
     */
    protected int converPxToDp(int px) {

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        float logicalDensity = dm.density;
        int res = (int) ((px + 0.5) / logicalDensity);
        return res;
    }

    /**
     * @param title   - dialog title
     * @param msg     - dialog msg
     * @param neutBtn - the neutral btn
     */
    private void showDialog(String title, String msg, String neutBtn, String negBtn) {

        AlertDialog.Builder ad = new AlertDialog.Builder(this);
        ad.setCancelable(false);
        ad.setTitle(title);
        ad.setMessage(msg);

        ad.setPositiveButton(neutBtn, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent i = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(i);
                alertDialog.dismiss();

                return;
            }
        });

        ad.setNegativeButton(negBtn, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
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
    private String getMsgForPushDialog() {

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
    private String getMsgForMuteNotifications() {

        StringBuffer msgDialog = new StringBuffer();
        msgDialog.append("  עדכון לשירות ההתראות של");
        msgDialog.append(Definitions.shareGlobesWithQuates);
        msgDialog.append("\n");
        msgDialog.append("לשירותך התווספה אפשרות לביטול צלילי ההתראות בתפריט ההגדרות של האפליקציה");
        msgDialog.append("\n");
        msgDialog.append("\n");
        msgDialog.append("להמשיך בקבלת התראות  ללא שינוי לחץ המשך");
        msgDialog.append("\n");
        msgDialog.append("לשינוי הגדרותיך האישיות ומעבר לתפריט ההגדרות - לחץ הגדרות");
        msgDialog.append("\n");
        msgDialog.append("\n");
        msgDialog.append("  בברכה");
        msgDialog.append(Definitions.shareGlobesWithQuates);
        msgDialog.append("\n");
        return msgDialog.toString();
    }

    /**
     * adding params for matrix server video request
     *
     * @param url - video url before adding the w=width h=height DT=device type
     * @return video url to process with the new params for matrix
     */
    private String getDeviceDetailsForVideoUrl(String url) {

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
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
    protected void savePrefsIntValue(String key, int value) {
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    @Override
    public void onRefreshStarted(View view) {
        // start data task
        new GetDataTask().execute();
    }

    @Override
    public void onMainRightMenuItemSelected(CustomMenuItem item) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onSwitchContentFragment(Fragment fragment, String tag, boolean addToBackStack, boolean isLoadAfterSlidingMenuCloses, boolean b) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onSetFragmentWebview(WebView webView) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onRedMailSendPicture() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onRedMailSendVideo() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onCloseGeneralSearch() {
        // TODO Auto-generated method stub

    }

    @Override
    public SlidingMenu getsSlidingMenu() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getActionBarTitle() {
        // TODO Auto-generated method stub
        return "";
    }

    @Override
    public void onSetDefaultActionBar() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onSetActionBarWithTextTitle(String title, int color) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setRegularMador(boolean b) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean getIsRegularMador() {
        // TODO Auto-generated method stub
        return false;
    }

}