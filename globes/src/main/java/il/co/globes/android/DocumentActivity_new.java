package il.co.globes.android;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.*;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.opengl.Visibility;
import android.os.*;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.*;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.AnimationUtils;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.*;
import android.widget.ImageView.ScaleType;
import com.facebook.*;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.doubleclick.PublisherAdView;
import com.google.android.gms.ads.doubleclick.PublisherInterstitialAd;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;
import com.outbrain.OBSDK.Entities.OBRecommendation;
import com.outbrain.OBSDK.Entities.OBRecommendationsResponse;
import com.outbrain.OBSDK.FetchRecommendations.OBRequest;
import com.outbrain.OBSDK.FetchRecommendations.RecommendationsListener;
import com.outbrain.OBSDK.Outbrain;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.viewpagerindicator.CirclePageIndicator;
import il.co.globes.android.Definitions.ArticleType;
import il.co.globes.android.fragments.SectionListFragment;
import il.co.globes.android.ima.v3.player.PlayerImaActivity;
import il.co.globes.android.interfaces.WallaAdvTaskCompleted;
import il.co.globes.android.objects.*;
import il.co.globes.android.objects.Response;
import il.co.globes.android.parsers.WallaAdvParser;
import im.spot.sdk.SpotIm;
import im.spot.sdk.SpotImView;
import net.tensera.sdk.api.TenseraApi;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

@SuppressLint(
        {"SetJavaScriptEnabled", "UseValueOf"})
public class DocumentActivity_new extends Activity implements WallaAdvTaskCompleted {
    private static final String WWW_OUTBRAIN_COM = "http://www.outbrain.com/what-is/default/en-mobile/";
    private static final int ATTACHMENT_CODE = 1010;

    // private String sakindoURL =
    // "<div style=\"text-align:center;\"><iframe scrolling=no frameborder=0 width=300 height=250 marginheight=0  marginwidth=0 src=http://live.sekindo.com/live/liveView.php?s=52471&njs=1&subId=DEFAULT></iframe></div>";

    private String sakindoURL = "<div style=\"text-align:center;\"><script src=\"http://live.sekindo.com/live/liveView.php?s=52471&subId=DEFAULT&nofr=1\"></script></div>";
    //private String sakindoURL = "<div style=\"text-align:center;\"><script src=\"http://live.sekindo.com/live/liveView.php?s=52471&pubUrl=%%REFERRER_URL_ESC_ESC%%\"></script></div>";
    private LinearLayout list_extra_document_by_outbrain;
    private LinearLayout layoutSpotIm;
    private String globaldocumentUrl;
    private String articleId;
    private boolean isFromPUSH;
    private WebView webView_iframe_ad;
    private WebView webView_iframe_spotIm;
    private Typeface almoni_aaa_regular;
    private Runnable closeRunable;
    private String[] DocTypes =
            {"reg", "opi", "dun"};
    private String docType = DocTypes[0];
    private Session.StatusCallback statusCallback = new SessionStatusCallback();
    private static final List<String> PERMISSIONS = Arrays.asList("publish_actions", "publish_stream", "public_profile"); // ,"publish_stream",
    // "read_stream",
    // "offline_access"
    // private static final String PENDING_PUBLISH_KEY =
    // "pendingPublishReauthorization";
    private boolean pendingPublishReauthorization = false;
    private boolean isFromShareButton;

    private ProgressDialog progressDialogShareSettings;

    private boolean isCommentOpen = false;
    private TextView ViewPrev;

    private static final String TAG = "DocumentActivity";
    private int commentsCount;
    private Typeface almoni_black, almoni_regular;

    private LinearLayout test_layout_Responses;
    /*
     * Used when decreasing font size in article text , first need to load an
	 * empty HTML than load the real one
	 */
    private static final String EMPTY_HTML = "<html><body/></html>";
    private String articleTextHtml = "";
    private String titleTextHtml = "";
    private String subtitleTextHtml;

    private static final java.util.regex.Pattern REGEX_SRC_PATTERN = java.util.regex.Pattern.compile("(?<=src=\")[^\"]*(?<!\")");

    // used to tell us to load an empty HTML first before loading the data again
    private boolean isFontReduced = false;
    private AtomicBoolean setDataToPageCalled = new AtomicBoolean(false);

    private enum EncodeType {
        TYPE_TITLE,
        TYPE_SUBTITLE,
        TYPE_BODY
    }

    ;

    static final int ENLARGETEXTSIZE = 0;
    static final int REDUCETEXTSIZE = 1;
    static final int RESETTEXTSIZE = 2;

    static final int MAXFONTSIZE = 18;
    static final int MINFONTSIZE = 10;

    /**
     * Font size key in shared prefs
     */
    SharedPreferences prefs;
    private final String FONT_SIZE_KEY_PREF = "fontSizePref";

    public static Boolean isActive = false;
    int responsesCount = 20;
    int baseTextSize = 15;
    int position = 0;

    // push notifications & Scheme
    String pushNotificationDocID;
    private ImageView imageView_textSize_actionBar_documentN;
    ListView list;
    LazyAdapter adapter;
    Document parsedDocument;
    DocumentResponses parsedDocumentResponses;
    Activity documentActivity = this;
    Bitmap documentMainBitmap;
    Bitmap documentVideoBitmap;
    Banner bannerBottom;
    static final int DONE_PARSING = 0;
    static final int ERROR_PARSING = 1;
    static final int DONE_GETTING_BANNER = 2;
    static final int ERROR_GETTING_BANNER = 3;
    static final int CLOSE_SPLASH = 5;
    static final int SPLASH_DISPLAY_LENGHT = 2000;

    boolean CheckboxPreferenceFlipAlignment;
    boolean CheckboxPreferenceMediaPlayer;
    Button buttonShare;
    Button buttonEnlargeFontSize;
    Button buttonReduceFontSize;
    ImageView buttonBrowseBack;
    ImageView buttonBrowseForward;
    Button buttonAddResponse;
    Button buttonGoToResponses;
    Button buttonPlayDocumentVideo;
    private Button buttonFacebook;
    LinearLayout layoutDocumentResponsesHeader, contenair_tagiot;
    CustomWebView webViewDocumentTitle;
    CustomWebView webViewDocumentSubTitle;
    WebView wvViewDocumentText;
    WebView wvEmbeddedClip;
    WebView webView_live_box;
    TextView textViewDocumentModifiedOn;
    TextView textViewDocumentWriter;
    TextView textViewDocumentImageAuthor;
    TextView responseCount, textView_comments_actionBar_documentN;
    ImageView imageViewDocumentVideo;
    FrameLayout frameLayoutDocumentVideo;
    NewsSet parsedNewsSet;
    String parentId, caller;
    Bitmap bitmap;
    RelativeLayout layoutMain;
    /**
     * Pager and indicator
     */
    ViewPager pager;
    CirclePageIndicator circlePageIndicator;
    ScrollView myScrollView;

    /**
     * Adview portrait
     */
    PublisherAdView adViewPublisherPortrait;

    /**
     * .tag dialog
     */
    AlertDialog alertDialog;

    /**
     * Adview portrait
     */
    PublisherAdView adViewPublisherLandscape;

    /**
     * SplashArticle when no Ad is available dont need to start the
     */
    PublisherAdView adViewSplashArticle;

    /**
     * FrameLayout AdView container
     */
    FrameLayout frameLayoutContainer;

    /**
     * Gallery objects arr - from parsed document
     */
    ArrayList<ArticleGalleryObject> galleryObjectsArr;

    /**
     * Article opnion imageView
     */
    ImageView imageOpnion;
    Bitmap opnionBitmap;
    RelativeLayout mediaContainer;

    // resources parameter
    private Resources myResources;

    public String linkToVideo;
    boolean isFinishedParsing = false;
    // private GestureDetector swipeDetector;
    // enable/disable the back button
    private boolean onBackPressedEnabled;

    Spanned articleText;

    View.OnTouchListener gestureListener;

    /**
     * Splash related
     */
    FrameLayout splashArticleContainer;
    RelativeLayout relativeLayout_splashArticle_container_layout_splash_ad;
    LinearLayout title_globes_test;
    LinearLayout LinearLayoutBottomBarAndBanner;

    //    PublisherAdView adViewPublisheRichMedia;
    FrameLayout frameLayoutRichMediaContainer;

    String RowType = "";
    private ProgressBar progressBar_parse;
    // private FrameLayout frameAdViewContainer_Pull_to_refresh;
    private RelativeLayout adView_frame_bottom_banner;
    private RelativeLayout frameAdViewContainer_Pull_to_refresh;
    private RelativeLayout frameAdViewContainer_Pull_to_refresh_top;
    private RelativeLayout frameAdViewContainer_Pull_to_refresh_bottom;
    private RelativeLayout row_layout_livebox_adview_frame_layout_container;
    private RelativeLayout adSekindoBottomBanner;
    private Map<String, Map<String, String>> wallaFusionDataMap = null;
    private boolean wallaFusionArticleInterstitialShown = false; // Article Maavaron
    private boolean interstitialStarted = false;
    private boolean articleIsReady = false;

    private boolean bShowWallaRichMedia = false;
    private Map<String, Map<String, String>> mapWallaFusionData = null;
    private RelativeLayout frameWallaFusionDCScripts;
    private RelativeLayout adView_frame_bottom_phone_banner;

    @Override
    public void onPause() {
        super.onPause();

        Log.e("alex", "DocumentActivity_new onPause");
        wvEmbeddedClip.loadUrl("about:blank");
        wvViewDocumentText.loadUrl("about:blank");
    }

        @Override
    public void onCreate(Bundle icicle) {
        // requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(icicle);
        Log.e("alex", "Document");

        setContentView(R.layout.new_layout_document_portrait);

        // eli outbrain init
        // ***************************************************************
        //Outbrain.setTestMode(true);
        Outbrain.register(this);

        try {
            Log.e("alex", "buildSpotIM ID: " + Definitions.spotImID);
            SpotIm.sharedInstance().init(this, Definitions.spotImID);
        } catch (Exception ex) {
            Log.e("alex", "buildSpotIM Error: " + ex);
        }

        initFindViews();

        almoni_aaa_regular = Typeface.createFromAsset(getAssets(), "almoni-dl-aaa-regular.otf");

        onBackPressedEnabled = true;

        myResources = getResources();
        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        ViewPrev = new TextView(this);

        Intent intent = getIntent();
        String caller = intent.getStringExtra(Definitions.CALLER);
        RowType = intent.getStringExtra("RowType");

        pushNotificationDocID = intent.getStringExtra("pushNotificationDocID");

            Log.e("alex", "pushNotificationDocID in DocumentActivity_new: " + pushNotificationDocID);

        if (caller != null && caller.equals(Definitions.WEBVIEW)) {
            position = 0;
            articleId = intent.getStringExtra("articleId");
            parsedNewsSet = createFakeNewsSet(articleId);
        } else if (caller != null && caller.equals(Definitions.SHARES)) {
            position = 0;
            articleId = intent.getStringExtra("articleId");
            parsedNewsSet = createFakeNewsSet(articleId);
        } else if (pushNotificationDocID == null) // not coming from notifications
        {
            position = intent.getIntExtra("position", 1);
            parentId = intent.getStringExtra("parentId");
            parsedNewsSet = DataContext.Instance().GetArticleList(parentId);
        } else {
            Log.e("alex", "DocumentActivity_new set isFromPUSH: " + pushNotificationDocID);
            Log.e("alex", "DocumentActivity_new set isFromPUSH: " + pushNotificationDocID);
            isFromPUSH = true;
            position = 0;
            articleId = pushNotificationDocID;
            parsedNewsSet = createFakeNewsSet(pushNotificationDocID);
        }

        String nameType = parsedNewsSet.itemHolder.get(0).getClass().getSimpleName();
        if (nameType.compareTo("RowArticleDuns100") == 0 || nameType.compareTo("ArticleDuns100") == 0) {
            docType = DocTypes[2];
        } else if (nameType.compareTo("ArticleOpinion") == 0 || nameType.compareTo("ArticleOpinionLeftMenu") == 0
                || nameType.compareTo("ArticleSmallOpinion") == 0) {
            docType = DocTypes[1];
        } else if (RowType != null && RowType.compareTo("Opinion") == 0) {
            docType = DocTypes[1];
        } else if (RowType != null && RowType.compareTo("Article") == 0) {
            docType = DocTypes[0];
        }
        if (true || DataStore.getInstance().showAdMob()) {
            // eli dfp
            // eli Ajillion

            if (Definitions.wallafusion) {
                Log.e("alex", "DocumentActNew : 0");

                wallaFusionArticleInterstitialShown = true;
                /////////////////////WALLA TEST///////////////////////////////////
                WallaAdvParser wap = new WallaAdvParser(getApplicationContext(), getApplication(), DocumentActivity_new.this, "article", "l_globes_app_a", "", 0);
                wap.execute();

                //////////////////////////////////////////////////////////////////
            } else {
                if (Definitions.toUseAjillion) {
                    new AjillionHandle().execute();

                    //Log.e("alex", "AAAAAAAAAAAAAAAAA : 3");
                } else {
                    loadOpeningSplash();
                }
            }
        } else {

            setDataToPage();
        }
    }

    private void initFindViews() {
        frameAdViewContainer_Pull_to_refresh = (RelativeLayout) findViewById(R.id.frameAdViewContainer_Pull_to_refresh);
        frameAdViewContainer_Pull_to_refresh_bottom = (RelativeLayout) findViewById(R.id.frameAdViewContainer_Pull_to_refresh_bottom);
        frameAdViewContainer_Pull_to_refresh_top = (RelativeLayout) findViewById(R.id.frameAdViewContainer_Pull_to_refresh_top);
        row_layout_livebox_adview_frame_layout_container = (RelativeLayout) findViewById(R.id.row_layout_livebox_adview_frame_layout_container);
        frameWallaFusionDCScripts = (RelativeLayout) findViewById(R.id.frameWallaFusionDCScripts);
        adView_frame_bottom_phone_banner = (RelativeLayout) findViewById(R.id.adView_frame_bottom_phone_banner);
        adSekindoBottomBanner = (RelativeLayout) findViewById(R.id.adSekindoBottomBanner);

        adView_frame_bottom_banner = (RelativeLayout) findViewById(R.id.adView_frame_bottom_banner);
        myScrollView = (ScrollView) findViewById(R.id.documentScrollView);
        pager = (ViewPager) findViewById(R.id.viewPager);
        circlePageIndicator = (CirclePageIndicator) findViewById(R.id.titles);
        imageOpnion = (ImageView) findViewById(R.id.imageOpnion);
        webViewDocumentTitle = (CustomWebView) findViewById(R.id.textViewDocumentTitle);
        webViewDocumentSubTitle = (CustomWebView) findViewById(R.id.textViewDocumentSubTitle);
        frameLayoutDocumentVideo = (FrameLayout) findViewById(R.id.frameLayoutDocumentVideo);
        imageViewDocumentVideo = (ImageView) findViewById(R.id.imageViewDocumentVideo);
        textViewDocumentModifiedOn = (TextView) findViewById(R.id.textViewDocumentModifiedOn);
        textViewDocumentWriter = (TextView) findViewById(R.id.textViewDocumentWriter);
        webView_live_box = (WebView) findViewById(R.id.webView_live_box);
        wvViewDocumentText = (WebView) findViewById(R.id.textViewDocumentText);
        wvEmbeddedClip = (WebView) findViewById(R.id.wvEmbeddedClip);
        responseCount = (TextView) findViewById(R.id.textViewResponsesCount);
        layoutDocumentResponsesHeader = (LinearLayout) findViewById(R.id.layoutDocumentResponsesHeader);
        textView_comments_actionBar_documentN = (TextView) findViewById(R.id.textView_comments_actionBar_documentN);
        test_layout_Responses = (LinearLayout) findViewById(R.id.test_layout_Responses);
        layoutMain = (RelativeLayout) findViewById(R.id.documentMainLayout);
        contenair_tagiot = (LinearLayout) findViewById(R.id.contenair_tagiot);
        webView_iframe_ad = (WebView) findViewById(R.id.webView_iframe_ad);
        //webView_iframe_spotIm = (WebView) findViewById(R.id.webView_iframe_spotIm);
        splashArticleContainer = (FrameLayout) findViewById(R.id.relativeLayout_splashArticle_container_test);
        list_extra_document_by_outbrain = (LinearLayout) findViewById(R.id.list_extra_document_by_outbrain);
        layoutSpotIm = (LinearLayout) findViewById(R.id.layoutSpotIm);
        progressBar_parse = (ProgressBar) findViewById(R.id.progressBar_parse);
    }

    @SuppressLint("NewApi")
    private void setAllViews() {
        myScrollView.fullScroll(View.FOCUS_UP);
        if (parsedDocument.getClipURL() != null) {
            this.linkToVideo = parsedDocument.getClipURL();
        }
        // webViewDocumentTitle.setGestureDetector(new GestureDetector(new
        // CustomeGestureDetector()));
        titleTextHtml = parsedDocument.getTitle();
        try {
            titleTextHtml = encodeTextForWebView(titleTextHtml, EncodeType.TYPE_TITLE);
        } catch (UnsupportedEncodingException e) {
            Log.e("eli", "erorr in setAllViews", e);
        }

//		if (Definitions.toUseLiveBoxMain)
//		{
//			LiveBoxModel liveBox = new LiveBoxModel();
//			liveBox.setUrlLiveBoxDocument(DataStore.getInstance().getLiveBoxArticleURL());
//
//			if (!TextUtils.isEmpty(DataStore.getInstance().getLiveBoxArticleURL()))
//			{
//
//				String articleTextHtml = liveBox.getUrlLiveBoxDocument();
//				try
//				{
//					articleTextHtml = encodeTextForWebView(articleTextHtml, EncodeType.TYPE_BODY);
//				}
//				catch (UnsupportedEncodingException e)
//				{
//					Log.e("eli", "erorr in setAllViews", e);
//				}
//				WebSettings webViewSettings = webView_live_box.getSettings();
//				webViewSettings.setJavaScriptCanOpenWindowsAutomatically(false);
//				webViewSettings.setJavaScriptEnabled(true);
//				webView_live_box.setWebChromeClient(new WebChromeClient());
//				webViewSettings.setUseWideViewPort(false);
//				webViewSettings.setBuiltInZoomControls(false);
//				webViewSettings.setLoadWithOverviewMode(false);
//				webViewSettings.setSupportZoom(false);
//				webViewSettings.setPluginState(PluginState.ON);
//				webView_live_box.setLayerType(WebView.LAYER_TYPE_SOFTWARE, null);
//
//				webView_live_box.loadData(articleTextHtml, "text/html; charset=utf-8", "UTF-8");
//		        webView_live_box.setVisibility(View.VISIBLE);
//			}
//			else
//			{
//				webView_live_box.setVisibility(View.GONE);
//				//Log.e("alex", "webView_live_box error");
//			}
//
//			//
//			// // WebSettings webViewSettings = webView_live_box.getSettings();
//			// //
//			// webViewSettings.setJavaScriptCanOpenWindowsAutomatically(false);
//			// // webViewSettings.setJavaScriptEnabled(true);
//			// // webView_live_box.setWebChromeClient(new WebChromeClient());
//			// // webViewSettings.setUseWideViewPort(false);
//			// // webViewSettings.setBuiltInZoomControls(false);
//			// // webViewSettings.setLoadWithOverviewMode(false);
//			// // webViewSettings.setSupportZoom(false);
//			// // webViewSettings.setPluginState(PluginState.ON);
//			// //
//			// //webView_live_box.loadUrl("file:///android_asset/liveboxmain.html");
//
//		}

        webViewDocumentTitle.setWebViewClient(new MyWebViewClient());
        webViewDocumentTitle.loadData(titleTextHtml, "text/html; charset=utf-8", "UTF-8");
        webViewDocumentSubTitle.setGestureDetector(new GestureDetector(new CustomeGestureDetector()));
        subtitleTextHtml = parsedDocument.getSubTitle();
        try {
            subtitleTextHtml = encodeTextForWebView(subtitleTextHtml, EncodeType.TYPE_SUBTITLE);
        } catch (UnsupportedEncodingException e) {
            Log.e("eli", "erorr in setAllViews", e);
        }

        webViewDocumentSubTitle.setWebViewClient(new MyWebViewClient());
        webViewDocumentSubTitle.loadData(subtitleTextHtml, "text/html; charset=utf-8", "UTF-8");

        if(false && !parsedDocument.getEmbeddedClip().equals(""))
        {
            Log.e("alex", "DocumentEmbeddedClip: " + parsedDocument.getEmbeddedClip().replace("'", "\""));

            java.util.regex.Matcher matcher = REGEX_SRC_PATTERN.matcher(parsedDocument.getEmbeddedClip().replace("'", "\""));
            if (matcher.find()) {
                String src = matcher.group();

                if(!src.equals("")) {

                    wvEmbeddedClip.setVisibility(View.VISIBLE);
                    wvEmbeddedClip.setWebChromeClient(new WebChromeClient());
                    wvEmbeddedClip.setWebViewClient(new WebViewClient());
                    wvEmbeddedClip.getSettings().setJavaScriptEnabled(true);
                    wvEmbeddedClip.loadUrl(src);

                    //wvEmbeddedClip.loadData(parsedDocument.getEmbeddedClip(), "text/html; charset=utf-8", "UTF-8");
                    //wvEmbeddedClip.loadData("<html><body>" + parsedDocument.getEmbeddedClip().replace("'", "\"") + "</body></html>", "text/html; charset=utf-8", "UTF-8");
                    //wvEmbeddedClip.loadDataWithBaseURL(null, parsedDocument.getEmbeddedClip().replace("'", "\""), "text/html", "UTF-8", null);


                    //documentVideoBitmap.setImageBitmap(Utils.DownloadImage(parsedDocument.getImageFromF3()));
                    Log.e("alex", "DocumentEmbeddedClip src: " + src + " parsedDocument.getImageFromF3() v3:" + parsedDocument.getEmbeddedClip());

                    galleryObjectsArr = parsedDocument.getGalleryObjects();
                    if (galleryObjectsArr.size() > 0) {
                        for (ArticleGalleryObject item : galleryObjectsArr) {

                            Bitmap bitmapTest = Utils.DownloadImage(parsedDocument.getImageFromF9().replace("\\/", "/"));
                            if (bitmapTest != null) {
                                item.setBitmap(bitmapTest);
                            } else {
                                item.setImageURL("dummy");
                                item.setBitmap(BitmapFactory.decodeResource(myResources, R.drawable.stub_image_not_found));
                            }
                        }
                    }
                }
            }
        }

       // if (parsedDocument.getEmbeddedClip().equals("") && documentVideoBitmap != null) {
        if (documentVideoBitmap != null) {
            Log.e("alex", "DocumentVideoBitmap: " + documentVideoBitmap);

            imageViewDocumentVideo.setImageBitmap(documentVideoBitmap);
            frameLayoutDocumentVideo.setVisibility(android.view.View.VISIBLE);

            if (galleryObjectsArr != null && pager != null) {
                pager.setAdapter(new galleryPagerAdapter(DocumentActivity_new.this, galleryObjectsArr));
                circlePageIndicator.setViewPager(pager);
                pager.setOnTouchListener(new OnTouchListener() {
                    public boolean onTouch(View v, MotionEvent event) {
                        if (event.getAction() == MotionEvent.ACTION_DOWN) {
                            return true;
                        }
                        if (event.getAction() == MotionEvent.ACTION_MOVE && myScrollView != null) {
                            myScrollView.requestDisallowInterceptTouchEvent(true);
                            return pager.onTouchEvent(event);
                        }
                        return false;
                    }
                });
                // mediaContainer.setVisibility(View.VISIBLE);
                pager.setVisibility(View.VISIBLE);
            }
        } else if (opnionBitmap != null) {
            // DocType = DocTypes[1];
            imageOpnion.setImageBitmap(opnionBitmap);
            imageOpnion.setVisibility(View.VISIBLE);
        } else {
            pager.setAdapter(new galleryPagerAdapter(DocumentActivity_new.this, galleryObjectsArr));
            circlePageIndicator.setViewPager(pager);
            pager.setOnTouchListener(new OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) return true;
                    if (event.getAction() == MotionEvent.ACTION_MOVE && myScrollView != null) {
                        myScrollView.requestDisallowInterceptTouchEvent(true);
                        return pager.onTouchEvent(event);
                    }
                    return false;
                }
            });
            // mediaContainer.setVisibility(View.VISIBLE);
            pager.setVisibility(View.VISIBLE);
        }

        textViewDocumentModifiedOn.setText(parsedDocument.getModifiedOn());
        textViewDocumentWriter.setText(Html.fromHtml(parsedDocument.getAuthorName()));
        // wvViewDocumentText.setGestureDetector(new GestureDetector(new
        // CustomeGestureDetector()));

        articleTextHtml = parsedDocument.getText();
        //articleTextHtml += "<script src=\"http://cdn.playbuzz.com/widget/feed.js\"></script><div class=\"pb_feed\" data-embed-by=\"4302ca9d-d2cc-4bb2-a03c-1ea25faa71e1\" data-game=\"/yaelsk10/3-9-2016-8-47-02-am\" data-recommend=\"false\" data-game-info=\"false\" data-comments=\"false\" data-async=\"true\"></div> <script>if (!window.PlayBuzz) { window.addEventListener(\"PlaybuzzScriptReady\", function () { PlayBuzz.Feed.renderFeed(); }); } else { PlayBuzz.Feed.renderFeed(); }</script>";
        //"<iframe src=\"http://www.globes.co.il/test/alex/test.htm\" width=\"100%\" height=\"890\" frameborder=\"0\"></iframe>";

        //Log.e("alex","PlayBuzzz: " + parsedDocument.getPlayBuzz());

        try {
            articleTextHtml = encodeTextForWebView(articleTextHtml, EncodeType.TYPE_BODY);
        } catch (UnsupportedEncodingException e) {
            Log.e("eli", "erorr in setAllViews", e);
        }

        MyWebViewClient myWebViewClient = new MyWebViewClient();

        wvViewDocumentText.setWebChromeClient(new WebChromeClient());
        if (AppRegisterForPushApps.enableTensera) {
            TenseraApi.tenserifyWebview(wvViewDocumentText, myWebViewClient);
        }
        wvViewDocumentText.loadData(articleTextHtml, "text/html; charset=utf-8", "UTF-8");
        wvViewDocumentText.getSettings().setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
        wvViewDocumentText.getSettings().setPluginState(PluginState.ON);
        wvViewDocumentText.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        wvViewDocumentText.getSettings().setJavaScriptEnabled(true);

        almoni_regular = Typeface.createFromAsset(getAssets(), "almoni-dl-aaa-regular.otf");
        responseCount.setTypeface(almoni_regular);
        responseCount.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        String strNumRes = parsedDocumentResponses.getTotalNumberOfResponses().replaceAll("לכתבה", "").replaceAll("זו", "");
        layoutDocumentResponsesHeader.setVisibility(android.view.View.VISIBLE);
        textView_comments_actionBar_documentN.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                myScrollView.smoothScrollTo(0, layoutDocumentResponsesHeader.getTop());
            }
        });

        if (parsedDocumentResponses.getResponsesStat() > 0) {
            responseCount.setText(strNumRes);
            textView_comments_actionBar_documentN.setText(parsedDocumentResponses.getResponsesStat() + "");
        } else {
            responseCount.setText(strNumRes);
            textView_comments_actionBar_documentN.setText(parsedDocumentResponses.getResponsesStat() + "");
        }

        ViewPrev.setId(-1);

        if (Definitions.spotImEnabled) {
            buildSpotIM();
        } else {
            for (int i = 0; i < parsedDocumentResponses.responses.size(); i++) {
                View child = View.inflate(getApplicationContext(), R.layout.new_row_layout_response, null);
                String commentStr = parsedDocumentResponses.responses.get(i).getResponseText().trim();
                if (!TextUtils.isEmpty(commentStr) && commentStr != "") {
                    ((ImageView) child.findViewById(R.id.have_comment_symbole)).setVisibility(View.VISIBLE);
                }
                final TextView txt_comment = (TextView) child.findViewById(R.id.txt_comment);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                params.weight = 1;
                child.setLayoutParams(params);
                TextView textViewResponseSerialNumber = (TextView) child.findViewById(R.id.textViewResponseSerialNumber);
                TextView textViewResponseSubject = (TextView) child.findViewById(R.id.textViewResponseSubject);
                TextView textViewResponseDate = (TextView) child.findViewById(R.id.textViewResponseDate);
                TextView textViewResponseUserName = (TextView) child.findViewById(R.id.textViewResponseUserName);
                textViewResponseSerialNumber.setText("  ." + parsedDocumentResponses.responses.get(i).getResponseSerialNumber().trim());
                textViewResponseSubject.setText(parsedDocumentResponses.responses.get(i).getResponseSubject().trim());
                textViewResponseDate.setText(parsedDocumentResponses.responses.get(i).getResponseDate().trim());
                textViewResponseUserName.setText(parsedDocumentResponses.responses.get(i).getResponseUserName().trim());
                if (Definitions.flipAlignment) textViewResponseSubject.setGravity(android.view.Gravity.RIGHT);
                final int y = i;
                txt_comment.setId(i);
                if (!TextUtils.isEmpty(commentStr) && commentStr != "") {
                    child.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String commentStr = parsedDocumentResponses.responses.get(y).getResponseText().trim();
                            commentStr = commentStr.replaceAll("nbsp", " ");
                            commentStr = commentStr.replaceAll(";", "");
                            commentStr = commentStr.replaceAll("&", "");
                            if (!TextUtils.isEmpty(commentStr) && commentStr != "") {
                                if (ViewPrev.getId() == txt_comment.getId() || ViewPrev.getId() == -1) {
                                    if (!isCommentOpen) {
                                        txt_comment.setText(commentStr);
                                        txt_comment.startAnimation(AnimationUtils.loadAnimation(DocumentActivity_new.this,
                                                android.R.anim.fade_in));
                                        txt_comment.setVisibility(View.VISIBLE);
                                    } else {
                                        txt_comment.startAnimation(AnimationUtils.loadAnimation(DocumentActivity_new.this,
                                                android.R.anim.fade_out));
                                        txt_comment.setVisibility(View.GONE);
                                    }
                                } else {
                                    if (!TextUtils.isEmpty(commentStr) && commentStr != "") {
                                        ViewPrev.startAnimation(AnimationUtils
                                                .loadAnimation(DocumentActivity_new.this, android.R.anim.fade_out));
                                        ViewPrev.setVisibility(View.GONE);
                                        txt_comment.setText(commentStr);
                                        txt_comment.startAnimation(AnimationUtils.loadAnimation(DocumentActivity_new.this,
                                                android.R.anim.fade_in));
                                        txt_comment.setVisibility(View.VISIBLE);
                                    } else {
                                        ViewPrev.startAnimation(AnimationUtils
                                                .loadAnimation(DocumentActivity_new.this, android.R.anim.fade_out));
                                        ViewPrev.setVisibility(View.GONE);
                                    }
                                }
                                isCommentOpen = !isCommentOpen;
                                ViewPrev = txt_comment;
                            }
                        }
                    });
                }
                test_layout_Responses.addView(child, i);
            }

            // if we need more comment button
            if (parsedDocumentResponses.responses.size() > 0 && parsedDocumentResponses.responses.size() >= commentsCount) {
                test_layout_Responses.addView(buildAddCommentsBtn());
            }

        }
        changeFontSize(RESETTEXTSIZE);

        // tagiot
        for (int i = 0; i < parsedDocument.getTagiot().size(); i++) {
            buildTagit(i);
        }

        buildBottomDFPBanners();
        buildDFPSekindo();
		//buildSakindo();
		buildAnalitycs();
	}

	private void buildSpotIM()
	{
        Log.e("alex", "buildSpotIM: " + parsedDocument.getDoc_id());


		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

		SpotImView spotimView = SpotIm.sharedInstance().getSpotimView();
		spotimView.setFocusable(true);
		spotimView.setFocusableInTouchMode(true);
		spotimView.setLayoutParams(params);
		spotimView.setPostId(parsedDocument.getDoc_id());

		layoutSpotIm.addView(spotimView);
		layoutSpotIm.setVisibility(View.VISIBLE);

		layoutDocumentResponsesHeader.setVisibility(View.GONE);

		//webView_iframe_spotIm.addView(spotimView);
		//webView_iframe_spotIm.setVisibility(View.VISIBLE);
	}

	private void buildSpotIM_test()
	{
		Log.e("alex", "buildSpotIM test...");
		//WebView wv= new WebView(getApplicationContext());
		webView_iframe_ad.setWebChromeClient(new WebChromeClient());
		webView_iframe_ad.setPadding(0, 0, 0, 0);
		WebSettings webViewSettings = webView_iframe_ad.getSettings();
		//webViewSettings.setJavaScriptCanOpenWindowsAutomatically(false);
		webViewSettings.setJavaScriptEnabled(true);
		//webViewSettings.setUseWideViewPort(false);
		//webViewSettings.setBuiltInZoomControls(false);
		//webViewSettings.setLoadWithOverviewMode(false);
		//webViewSettings.setSupportZoom(false);
		//webViewSettings.setPluginState(PluginState.ON);

        String spScript = "<!DOCTYPE html><div class=\"spot-im-frame-inpage\" data-post-id=\"1001159577\">2222222</div><script type=\"text/javascript\">window.SPOTIM = { features: { REGISTRATION_CTA: false}, spotId: \"sp_8BE2orzs\" }</script><script type=\"text/javascript\" src=\"http://www.spot.im/launcher/bundle.js\"></script></html>";


        webView_iframe_ad.loadUrl("http://www.globes.co.il/news/test/alex/spotim.html");

        //webView_iframe_ad.loadData(spScript, "text/html; charset=utf-8", "UTF-8");
        //layoutSpotIm.addView(wv, 0);
        //layoutSpotIm.setVisibility(View.VISIBLE);

        //wv.loadDataWithBaseURL(null, sakindoURL, "text/html", "utf-8", null);
    }

    private void buildSakindo() {
        try {
            Log.e("alex", "Start Sekindo...");
            webView_iframe_ad.setOnTouchListener(new View.OnTouchListener() {
                @SuppressLint("ClickableViewAccessibility")
                public boolean onTouch(View v, MotionEvent event) {
                    return (event.getAction() == MotionEvent.ACTION_MOVE);
                }
            });
            webView_iframe_ad.setVerticalScrollBarEnabled(false);
            webView_iframe_ad.setHorizontalScrollBarEnabled(false);
            webView_iframe_ad.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    try {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        startActivity(browserIntent);
                        return true;
                    } catch (Exception ex) {
                        Log.e("alex", "Sekindo Error OverrideUrlLoading: " + ex.getMessage());
                        return false;
                    }
                }

            });
            webView_iframe_ad.setWebChromeClient(new WebChromeClient());
            // webView_iframe_ad.setInitialScale(getScale());
            webView_iframe_ad.setPadding(0, 0, 0, 0);
            WebSettings webViewSettings = webView_iframe_ad.getSettings();
            webViewSettings.setJavaScriptCanOpenWindowsAutomatically(false);
            webViewSettings.setJavaScriptEnabled(true);
            // webViewSettings.setPluginsEnabled(true);
            webViewSettings.setUseWideViewPort(false);
            webViewSettings.setBuiltInZoomControls(false);
            webViewSettings.setLoadWithOverviewMode(false);
            webViewSettings.setSupportZoom(false);
            // webViewSettings.setSupportZoom(false);
            webViewSettings.setPluginState(PluginState.ON);
            webView_iframe_ad.loadData(sakindoURL, "text/html; charset=utf-8", "UTF-8");
            webView_iframe_ad.setVisibility(View.VISIBLE);
            Log.e("alex", "Load Sekindo: " + sakindoURL);

            //webView_iframe_ad.loadDataWithBaseURL(null, sakindoURL, "text/html", "utf-8", null);
        } catch (Exception ex) {
            Log.e("alex", "Sekindo Error Main: " + ex.getMessage());
        }
    }

    private void buildAnalitycs() {
        // TODO eli ANALITICS
        if (docType.compareTo(DocTypes[2]) == 0) {
            Utils.writeScreenToGoogleAnalytics(this, "Android_Duns100ArticlePage_#" + parsedDocument.getDoc_id());
        } else if (docType.compareTo(DocTypes[1]) == 0) {// opinion
            if (parsedDocument != null) {
                if (isFromPUSH) {
                    Utils.writeScreenToGoogleAnalytics(this, "Android_ArticleDeotPage_#" + parsedDocument.getDoc_id() + "_from=push");
                } else {
                    Utils.writeScreenToGoogleAnalytics(this, "Android_ArticleDeotPage_#" + parsedDocument.getDoc_id());
                }
            }
        } else {// regular
            if (parsedDocument != null) {
                if (isFromPUSH) {
                    Utils.writeScreenToGoogleAnalytics(this, "Android_ArticlePage_#" + parsedDocument.getDoc_id() + "_from=push");
                } else {
                    Utils.writeScreenToGoogleAnalytics(this, "Android_ArticlePage_#" + parsedDocument.getDoc_id());
                }
            }
        }
    }

    private void buildTagit(int i) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(150, LinearLayout.LayoutParams.MATCH_PARENT);
        // params.setMargins(20, 20, 20, 20);
        params.weight = 1;

        TextView textView = new TextView(DocumentActivity_new.this);
        String tagTitle = parsedDocument.getTagiot().get(i).getSimplified();
        tagTitle = tagTitle.replaceAll("_", " ");
        textView.setText(tagTitle);
        textView.setBackgroundColor(getResources().getColor(R.color.pink_globes));
        textView.setTextColor(getResources().getColor(R.color.Black));
        textView.setTypeface(almoni_aaa_regular);

        textView.setLayoutParams(params);
        textView.setGravity(Gravity.CENTER);
        textView.setPadding(10, 0, 10, 0);

        // add Tagit as tag
        final Tagit tagittest = parsedDocument.getTagiot().get(i);
        // add listener
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickToTagit(tagittest);
            }
        });
        LinearLayout.LayoutParams paramsForView = new LinearLayout.LayoutParams(1, LinearLayout.LayoutParams.MATCH_PARENT);
        paramsForView.weight = 0;

        View view = new View(DocumentActivity_new.this);
        view.setBackgroundColor(getResources().getColor(R.color.grey_rate));
        view.setLayoutParams(paramsForView);
        contenair_tagiot.addView(textView);
        contenair_tagiot.addView(view);
    }

    private View buildAddCommentsBtn() {
        final TextView viewAddC0mments = new TextView(getApplicationContext());
        LinearLayout.LayoutParams paramsForViewAddC0mments = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 110);
        paramsForViewAddC0mments.setMargins(20, 10, 20, 30);

        viewAddC0mments.setBackgroundDrawable(getResources().getDrawable(R.drawable.white_with_black_border));
        viewAddC0mments.setLayoutParams(paramsForViewAddC0mments);
        viewAddC0mments.setText("הצג תגובות נוספות");
        viewAddC0mments.setTextSize(19);
        viewAddC0mments.setTextColor(getResources().getColor(R.color.RedForeground));
        viewAddC0mments.setGravity(Gravity.CENTER);

        viewAddC0mments.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowMoreComments();
            }
        });
        return viewAddC0mments;
    }

    @SuppressLint("UseValueOf")
    private int getScale() {
        Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        int width = display.getWidth();
        Double val = new Double(width) / new Double(300);
        val = val * 100d;
        return val.intValue();
    }

    public void clickToTagit(String nameHe) {
        startActivity(new Intent(this, MainSlidingActivity.class));
        // create the fragment
        String name = nameHe;
        SectionListFragment fragment = new SectionListFragment();
        try {
            name = URLEncoder.encode(nameHe, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            Log.e("eli", "erorr in clickToTagit", e);
        }
        // set uri
        String uri = GlobesURL.URLTagitArticlesByCleanName.replace("<TAGIT_NAME>", name);
        // Log.e("eli", uri);
        String parser = Definitions.MAINSCREEN;
        String caller = Definitions.SECTIONS;
        String header = nameHe;

        // set bundle to fragment
        Bundle args = new Bundle();
        args.putString(Definitions.URI_TO_PARSE, uri);
        args.putString(Definitions.CALLER, caller);
        args.putString("isFromTagit", "0");
        args.putString("isFromDocument", "1");
        args.putString(Definitions.PARSER, parser);
        args.putString(Definitions.HEADER, header.replace("_", " "));
        fragment.setArguments(args);
        MainSlidingActivity.getMainSlidingActivity().onSwitchContentFragment(fragment, SectionListFragment.class.getSimpleName(), true,
                false, false);
        // DocumentActivity.this.finish();
    }

    public void clickToTagit(Tagit tagit) {
        // TODO eli - tagit was click in document
        startActivity(new Intent(this, MainSlidingActivity.class));

        SectionListFragment fragment = new SectionListFragment();
        String uri = GlobesURL.URLTagitArticlesByID.replace("<TAGIT_ID>", tagit.getId());
        uri = uri.startsWith("http://") ? uri : GlobesURL.DOC_PREFIX + uri;

        Log.e("alex", "clickToTagit: " + uri);
        String parser = Definitions.MAINSCREEN;
        String caller = Definitions.SECTIONS;
        String header = tagit.getSimplified();
        // set bundle to fragment
        Bundle args = new Bundle();
        args.putString(Definitions.URI_TO_PARSE, uri);
        args.putString(Definitions.CALLER, caller);
        args.putString("isFromTagit", "1");
        args.putString("isFromDocument", "0");
        args.putString(Definitions.PARSER, parser);
        args.putString(Definitions.HEADER, header.replace("_", " "));
        fragment.setArguments(args);
        if (MainSlidingActivity.getMainSlidingActivity() != null) {
            MainSlidingActivity.getMainSlidingActivity().onSwitchContentFragment(fragment, SectionListFragment.class.getSimpleName(), true,
                    false, false);
            // DocumentActivity.this.finish();
        }
    }

    protected synchronized void ShowMoreComments() {
        (new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... params) {
                try {
                    commentsCount = commentsCount + 20;
                    URL documentResponesUrl;
                    documentResponesUrl = new URL(GlobesURL.APILocation_ArticleResponses.replace("XXXX",
                            ((Article) parsedNewsSet.itemHolder.get(position)).getDoc_id())
                            .replace("YYYY", Integer.toString(commentsCount)));
                    parsedDocumentResponses = DocumentResponses.parseDocumentResponses(documentResponesUrl);
                    return "";

                } catch (Exception e1) {
                    commentsCount = commentsCount - 20;
                    Log.e("eli", "erorr in ShowMoreComments", e1);
                    return e1.getMessage();
                }
            }

            protected void onPostExecute(String result) {

                if ((!result.equals(""))) {
                    commentsCount = commentsCount - 20;
                    Toast.makeText(getApplicationContext(), "An error occured.Error details: " + result, Toast.LENGTH_LONG).show();
                } else {
                    // eli implement like iphone
                    test_layout_Responses.removeAllViews();
                    for (int i = 0; i < parsedDocumentResponses.responses.size(); i++) {
                        View child = View.inflate(getApplicationContext(), R.layout.new_row_layout_response, null);

                        String commentStr = parsedDocumentResponses.responses.get(i).getResponseText().trim();
                        if (!TextUtils.isEmpty(commentStr) && (!commentStr.equals(""))) {
                            ((ImageView) child.findViewById(R.id.have_comment_symbole)).setVisibility(View.VISIBLE);
                        }

                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT);
                        params.weight = 1;
                        child.setLayoutParams(params);
                        TextView textViewResponseSerialNumber = (TextView) child.findViewById(R.id.textViewResponseSerialNumber);
                        TextView textViewResponseSubject = (TextView) child.findViewById(R.id.textViewResponseSubject);
                        TextView textViewResponseDate = (TextView) child.findViewById(R.id.textViewResponseDate);
                        TextView textViewResponseUserName = (TextView) child.findViewById(R.id.textViewResponseUserName);
                        final TextView txt_comment = (TextView) child.findViewById(R.id.txt_comment);
                        textViewResponseSerialNumber.setText("  ."
                                + parsedDocumentResponses.responses.get(i).getResponseSerialNumber().trim());
                        textViewResponseSubject.setText(parsedDocumentResponses.responses.get(i).getResponseSubject().trim());
                        textViewResponseDate.setText(parsedDocumentResponses.responses.get(i).getResponseDate().trim());
                        textViewResponseUserName.setText(parsedDocumentResponses.responses.get(i).getResponseUserName().trim());

                        if (Definitions.flipAlignment) {
                            textViewResponseSubject.setGravity(android.view.Gravity.RIGHT);

                        }
                        final int y = i;
                        txt_comment.setId(i);
                        if (!TextUtils.isEmpty(commentStr) && (!commentStr.equals(""))) {
                            child.setOnClickListener(new OnClickListener() {

                                @Override
                                public void onClick(View v) {
                                    String commentStr = parsedDocumentResponses.responses.get(y).getResponseText().trim();
                                    commentStr = commentStr.replaceAll("nbsp", " ");
                                    commentStr = commentStr.replaceAll(";", "");
                                    commentStr = commentStr.replaceAll("&", "");
                                    if (!TextUtils.isEmpty(commentStr) && (!commentStr.equals(""))) {

                                        if (ViewPrev.getId() == txt_comment.getId() || ViewPrev.getId() == -1) {
                                            if (!isCommentOpen) {
                                                txt_comment.setText(commentStr);
                                                txt_comment.startAnimation(AnimationUtils.loadAnimation(DocumentActivity_new.this,
                                                        android.R.anim.fade_in));
                                                txt_comment.setVisibility(View.VISIBLE);
                                            } else {
                                                txt_comment.startAnimation(AnimationUtils.loadAnimation(DocumentActivity_new.this,
                                                        android.R.anim.fade_out));
                                                txt_comment.setVisibility(View.GONE);
                                            }
                                        } else {
                                            if (!TextUtils.isEmpty(commentStr) && (!commentStr.equals(""))) {
                                                ViewPrev.startAnimation(AnimationUtils.loadAnimation(DocumentActivity_new.this,
                                                        android.R.anim.fade_out));
                                                ViewPrev.setVisibility(View.GONE);
                                                txt_comment.setText(commentStr);
                                                txt_comment.startAnimation(AnimationUtils.loadAnimation(DocumentActivity_new.this,
                                                        android.R.anim.fade_in));
                                                txt_comment.setVisibility(View.VISIBLE);
                                            } else {
                                                ViewPrev.startAnimation(AnimationUtils.loadAnimation(DocumentActivity_new.this,
                                                        android.R.anim.fade_out));
                                                ViewPrev.setVisibility(View.GONE);
                                            }
                                        }
                                        isCommentOpen = !isCommentOpen;
                                        ViewPrev = txt_comment;
                                    }
                                }
                            });
                        }
                        test_layout_Responses.addView(child, i);
                    }
                    if (parsedDocumentResponses.responses.size() > 0 && parsedDocumentResponses.responses.size() >= commentsCount) {
                        final TextView viewAddC0mments = new TextView(DocumentActivity_new.this);
                        LinearLayout.LayoutParams paramsForViewAddC0mments = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT, 80);
                        paramsForViewAddC0mments.setMargins(20, 10, 20, 30);
                        viewAddC0mments.setBackgroundDrawable(getResources().getDrawable(R.drawable.white_with_black_border));
                        viewAddC0mments.setLayoutParams(paramsForViewAddC0mments);
                        viewAddC0mments.setText("הצג תגובות נוספות");
                        viewAddC0mments.setTextSize(19);
                        viewAddC0mments.setTextColor(getResources().getColor(R.color.RedForeground));
                        viewAddC0mments.setGravity(Gravity.CENTER);

                        viewAddC0mments.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ShowMoreComments();
                            }
                        });
                        test_layout_Responses.addView(viewAddC0mments);
                    }
                }
            }
        }).execute();
    }

    void setPageToArticle(final String docId) {
        Log.e("alex", "setPageToArticle: " + docId);

        new AsyncTask<Void, Void, Boolean>() {
            protected void onPreExecute() {
                if (!interstitialStarted) {
                    progressBar_parse.setVisibility(View.VISIBLE);
                }
            }

            @Override
            protected Boolean doInBackground(Void... params) {
                try {
                    documentVideoBitmap = null;
                    documentMainBitmap = null;
                    opnionBitmap = null;
                    commentsCount = 20;

                    //String urlstr = GlobesURL.URLDocument.replace("XXXX", docId);
                    //urlstr = urlstr.startsWith("http://") ? urlstr : GlobesURL.DOC_PREFIX + urlstr;
                    //URL documentUrl = new URL(urlstr);
                    //parsedDocument = Document.parseDocument(documentUrl);

                    //Log.e("alex", "setPageToArticle: " + urlstr);

                    /************************************JSON*******************************************/

                    //String documentUrlJsonOnError = "http://www.globes.co.il/apps/apps_json.asmx/DocumentJson?doc_id=1001060719&ied=true&from=app_android&v=9";

                    String documentUrlJson = Definitions.DocumentUrlJson.replace("XXXX", docId);

                    if (documentUrlJson.equals("") && (!docId.equals(""))) // on error
                    {
                        //Log.e("alex", "URL to parsing (error): " + documentUrlJson);
                        documentUrlJson = GlobesURL.UrlDocumentJsonOnError.replace("XXXX", docId);
                    }

                    Log.e("alex", "URL to parsing: " + documentUrlJson);
                    if (AppRegisterForPushApps.enableTensera) {
                        TenseraApi.reportArticleClick(documentUrlJson, position);
                    }
                    parsedDocument = Document.parseDocumentJson(documentUrlJson);

                    //Log.e("alex","ParseDocument!!!:" + parsedDocument.getText().indexOf("live.sekindo.com"));
                    /************************************END JSON*******************************************/

                    String urlstr2 = GlobesURL.APILocation_ArticleResponses.replace("XXXX", docId).replace("YYYY", Integer.toString(commentsCount));
                    urlstr2 = urlstr2.startsWith("http://") ? urlstr2 : GlobesURL.DOC_RESPONSESS_PREFIX + urlstr2;
                    URL documentResponesUrl = new URL(urlstr2);

                    Log.e("alex", "parsedDocumentResponses: " + documentResponesUrl);

                    // parse responses
                    parsedDocumentResponses = DocumentResponses.parseDocumentResponses(documentResponesUrl);
                    // opinion article
                    if (parsedDocument.getDoctype() == ArticleType.TEXT_ARTICLE && parsedDocument.getImageFromF3().equals("")) {
                        Log.e("alex", "imageToGet0");
                        opnionBitmap = Utils.DownloadImage(parsedDocument.getImageFromF2());
                    } else if (parsedDocument.getDoctype() == ArticleType.TEXT_ARTICLE) {
                        if (parsedDocument.getClipURLFromF19().length() > 1) {
                            documentVideoBitmap = Utils.DownloadImage(parsedDocument.getImageFromF3());
                        }

                        galleryObjectsArr = parsedDocument.getGalleryObjects();
                        if (galleryObjectsArr.size() > 0) {
                            for (ArticleGalleryObject item : galleryObjectsArr) {
                                //Log.e("alex", "imageToGet1: " + item.getImageURL());
                                //Log.e("alex", "imageToGet2: " + item.getImageURL().replace("\\/", "/"));

                                Bitmap bitmapTest = Utils.DownloadImage(item.getImageURL().replace("\\/", "/"));
                                if (bitmapTest != null) {
                                    item.setBitmap(bitmapTest);
                                } else {
                                    item.setImageURL("dummy");
                                    item.setBitmap(BitmapFactory.decodeResource(myResources, R.drawable.stub_image_not_found));
                                }
                            }
                        } else {
                            // case no photos in F3
                            ArticleGalleryObject dummy = new ArticleGalleryObject("dummy", "dummy");
                            dummy.setBitmap(BitmapFactory.decodeResource(myResources, R.drawable.stub_image_not_found));
                            galleryObjectsArr.add(dummy);
                        }
                    } else {
                        // video article
                        documentVideoBitmap = Utils.DownloadImage(parsedDocument.getImageFromF9());
                    }
                    isFinishedParsing = true;
                } catch (Exception e) {
                    if (e != null) {
                        Log.e("eli", "erorr in setPageToArticle", e);
                    }
                    return false;
                }
                return true;
            }

            @Override
            protected void onPostExecute(Boolean success) {
                 articleIsReady = true;
                progressBar_parse.setVisibility(View.GONE);

                if ((!parsedDocument.getCanonicalDynastyIds().equals("")) && parsedDocument.getCanonicalDynastyIds().contains(String.valueOf(Definitions.MarketingContentFolder))) {
                    //Log.e("alex", "EEEEEEEEEEEEEEEE: " + parsedDocument.getCanonicalDynastyIds());

                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(GlobesURL.URLDocumentToShare + parsedDocument.getDoc_id()));
                    startActivity(i);
                    DocumentActivity_new.this.finish();
                    return;
                }

                if (success) {
                    // onConfigurationChanged();
                    // loadRichMedia();

                    // eli dfp
                    // eli ajillion

                    if (Definitions.wallafusion) {
                        if (wallaFusionDataMap != null) // Don't parse JSON one more time if article maavaron is shown
                        {
                            Log.e("alex", "AAAAAAAAAAAAAAAAA : wallaFusionDataMap != null");
                            if (!interstitialStarted) {
                                showTopBottomBanners(wallaFusionDataMap);
                            }
                        } else {
                            Log.e("alex", "DocumentActNew : wallaFusionDataMap == null");
                            /////////////////////WALLA TEST///////////////////////////////////
                            WallaAdvParser wap = new WallaAdvParser(getApplicationContext(), getApplication(), DocumentActivity_new.this, "article", "l_globes_app_a", "", 0);
                            wap.execute();
                            //////////////////////////////////////////////////////////////////
                        }
                    } else {
                        if (Definitions.toUseAjillion) {
                            new initAjillionView(frameAdViewContainer_Pull_to_refresh, Definitions.AJILLION_AD_ID_HEAD_BANNER).execute();
                            new initAjillionView(adView_frame_bottom_banner, Definitions.AJILLION_AD_ID_INNER_BANNER).execute();
                            //Log.e("alex", "AAAAAAAAAAAAAAAAA : 1");
                        } else {
                            initAdView();
                        }
                    }
                    setAllViews();
                    createActionBarButtons();

                    //Log.e("alex","Definitions.toUseOutbrain: " + Definitions.toUseOutbrain);
                    //if (1==1 || Definitions.toUseOutbrain) outBrainFetching();  //1==1

                    if (!parsedDocument.getPlayBuzz().equals("")) {
                        showPlayBuzzAfterArticle(parsedDocument.getPlayBuzz());
                    }

                    if (Definitions.toUseTaboola) {
                        showTaboolaAfterArticle();
                    }
                    //showSpotIMResponses();
                } else {
                    documentActivity.finish();
                }
            }

            ;
        }.execute();
    }

    /**
     * @author Eviatar Handles
     */
    private class faceBookWebClient extends WebViewClient {
        @Override
        public void onPageFinished(WebView view, String url) {
            Log.d(TAG, "onPageFinished url: " + url);
            // Facebook redirects to this url once a user has logged in, this is
            // a blank page so we override this
            // http://www.facebook.com/connect/connect_to_external_page_widget_loggedin.php?............
            if (url.startsWith("http://www.facebook.com/connect/connect_to_external_page_widget_loggedin.php")) {
                FaceBookFollowObject object = FaceBookFollowObject.getInstance(DocumentActivity_new.this);
                view.loadData(object.getHtmlFollow(), "text/html", "utf-8");
                return;
            }
            super.onPageFinished(view, url);
        }
    }

    private void showPlayBuzzAfterArticle(String pbuzz) {
        try {
            Log.e("alex", "showPlayBuzzAfterArticle7:" + pbuzz);

            //StringBuilder tmp = new StringBuilder();
            //tmp.append("<script src=\"http://cdn.playbuzz.com/widget/feed.js\"></script> <div class=\"pb_feed\" data-embed-by=\"4302ca9d-d2cc-4bb2-a03c-1ea25faa71e1\" data-game=\"/yaelsk10/3-9-2016-8-47-02-am\" data-recommend=\"false\" data-game-info=\"false\" data-comments=\"false\" data-async=\"true\"></div> <script>if (!window.PlayBuzz) { window.addEventListener(\"PlaybuzzScriptReady\", function () { PlayBuzz.Feed.renderFeed(); }); } else { PlayBuzz.Feed.renderFeed(); }</script>");
            //tmp.append(pbuzz.trim());

            WebView w = new WebView(this);
            w.getSettings().setJavaScriptEnabled(true);
            w.getSettings().setAppCacheEnabled(false);
            w.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
            w.loadDataWithBaseURL(null, pbuzz.trim(), "text/html", "UTF-8", null);

            list_extra_document_by_outbrain.addView(w);
        } catch (Exception ex) {
        }
    }

    private void showSpotIMResponses() {
        Log.e("alex", "showSpotIMResponses3");

        WebView w = new WebView(this);
        w.setWebChromeClient(new WebChromeClient());
        w.setWebViewClient(new MyWebViewClient());
        w.getSettings().setJavaScriptEnabled(true);
        w.getSettings().setAppCacheEnabled(false);
        w.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);

        String test = "<div class=\"spot-im-frame-inpage\" data-post-id=\"1001116413\"></div><script type=\"text/javascript\"> !function (t, e, n) { function a(t) { var a = e.createElement(\"script\"); a.type = \"text/javascript\", a.async = !0, a.src = (\"https:\" === e.location.protocol ? \"https\" : \"http\") + \":\" + n, (t || e.body || e.head).appendChild(a) } function o() { var t = e.getElementsByTagName(\"script\"), n = t[t.length - 1]; return n.parentNode } var p = o(); t.spotId = \"sp_8BE2orzs\", t.parentElement = p, a(p) } (window.SPOTIM = { features: { REGISTRATION_CTA: false} }, document, \"http://www.spot.im/launcher/bundle.js\");</script>";

        w.loadDataWithBaseURL(null, test, "text/html", "UTF-8", null);
        list_extra_document_by_outbrain.addView(w);

    }

    private void showTaboolaAfterArticle() {
        //Log.e("alex","Tabola");
        final LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View groupTitle = layoutInflater.inflate(R.layout.row_layout_group_for_out_brain, list_extra_document_by_outbrain, false);
        ((TextView) groupTitle.findViewById(R.id.grouptitle)).setText("כתבות נוספות");
        list_extra_document_by_outbrain.addView(groupTitle);

        StringBuilder tmp = new StringBuilder();
        tmp.append("<html dir=\"rtl\" ><meta charset=\"utf-8\" />");
        tmp.append("<head><script type=\"text/javascript\">window._taboola = window._taboola || [];_taboola.push({article:'auto'});!function (e, f, u) {e.async = 1;e.src = u;f.parentNode.insertBefore(e, f);}(document.createElement('script'),document.getElementsByTagName('script')[0],'http://cdn.taboola.com/libtrc/globes-il/loader.js');</script></head>");
        tmp.append("<div id=\"taboola-below-article-thumbnails\"></div><script type=\"text/javascript\">window._taboola = window._taboola || [];_taboola.push({mode: 'organic-thumbnails-d',container: 'taboola-below-article-thumbnails',placement: 'Below Article Thumbnails Android',target_type: 'mix'});</script>");
        tmp.append("<script type=\"text/javascript\">window._taboola = window._taboola || []; _taboola.push({flush: true});</script>");
        tmp.append("</html>");

        WebView w = new WebView(this);
        w.getSettings().setJavaScriptEnabled(true);
        w.getSettings().setAppCacheEnabled(false);
        w.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        w.loadDataWithBaseURL(null, tmp.toString(), "text/html", "UTF-8", null);

        list_extra_document_by_outbrain.addView(w);

        //Log.e("alex", "showTaboolaAfterArticle123");

        w.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                //Log.e("alex", "showTaboolaAfterArticle: " + url);

                if (url != "") {
                    try {
                        sendAsyncPing(url);
                    } catch (Exception ex) {
                    }
                }

                String sDecodedURL = "";
                Uri uri = null;

                try {
                    sDecodedURL = URLDecoder.decode(url, "UTF-8");
                    uri = Uri.parse(sDecodedURL);

                    String sRedirectURL = uri.getQueryParameter("redir");

                    if (sRedirectURL != "" && sRedirectURL != null) {
                        //Log.e("alex", "showTaboolaAfterArticle OUT: " + sRedirectURL);

                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(sRedirectURL));
                        startActivity(i);
                        return true;
                    }
                } catch (UnsupportedEncodingException e1) {
                }

                String taboolaFid = Utils.getSubStringByValueAd(url + "&", "fid=", "#");
                String taboolaDid = Utils.getSubStringByValueAd(url + "&", "did=", "#");

                if (taboolaDid == null || taboolaDid.equals("unknown")) {
                    //Log.e("alex", "showTaboolaAfterArticle0.1: " + sDecodedURL);

                    String sGlobesURL = uri.getQueryParameter("url");

                    if (sGlobesURL != null && (!sGlobesURL.equals(""))) {
                        uri = Uri.parse(sGlobesURL);
                        taboolaDid = uri.getQueryParameter("did");

                        if (taboolaDid == null || taboolaDid.equals("")) {
                            taboolaDid = uri.getQueryParameter("fbdid");
                        }
                    }
                }

                //Log.e("alex", "showTaboolaAfterArticle url: " + url + " taboolaFid: " + taboolaFid + " taboolaDid: " + taboolaDid);

                if (!taboolaFid.equals("unknown")) // redirect to folder
                {
                    RedirectToFolderID(url, taboolaFid);
                    DocumentActivity_new.this.finish();
                } else if (taboolaDid != null && (!taboolaDid.equals("unknown")) && (!taboolaDid.equals(""))) // redirect to article
                {
                    Intent intent = new Intent(DocumentActivity_new.this, DocumentActivity_new.class);
                    intent.putExtra(Definitions.CALLER, Definitions.WEBVIEW);
                    intent.putExtra("articleId", taboolaDid);
                    startActivity(intent);
                } else // open in browser (taboola link)
                {
                    Log.e("alex", "showTaboolaAfterArticle OUT: " + url);

                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);
                }

                //Log.e("alex","Tabola: did=" + taboolaDid + " fid=" + taboolaFid + " url === " + url);

                return true;
            }
        });
    }

    private void RedirectToFolderID(String url, String fid) {
        try {
            String uri;
            String parser;
            String caller;
            if (fid.equals(GlobesURL.TVNode)) {
                uri = GlobesURL.URLClips;
            } else {
                uri = GlobesURL.URLSections + fid;
            }
            caller = Definitions.SECTIONS;
            if (fid.equals("10002"))// opinions
            {
                parser = Definitions.OPINIONS;
            } else {
                parser = Definitions.MAINSCREEN;
            }

            // move to fragment
            SectionListFragment fragment = new SectionListFragment();
            Bundle args = new Bundle();
            args.putString(Definitions.URI_TO_PARSE, uri);
            args.putString(Definitions.CALLER, caller);
            args.putString(Definitions.PARSER, parser);
            //args.putString(Definitions.HEADER, ((InnerGroup) parsedNewsSet.itemHolder.get(position)).getTitle());
            fragment.setArguments(args);

            MainSlidingActivity.getMainSlidingActivity().onSwitchContentFragment(fragment, SectionListFragment.class.getSimpleName(), true, false, false);

        } catch (Exception ex) {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        }
    }

    private void sendAsyncPing(String url) {
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(url, new TextHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String res) {
                        Log.e("alex", "showTaboolaAfterArticle sendAsyncPing onSuccess: " + statusCode + "===" + res);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                        Log.e("alex", "showTaboolaAfterArticle sendAsyncPing onFailure: " + res);
                    }
                }
        );

    }

    private void outBrainFetching() {
        try {
            int widgetIndex = 0;
            String widgetId = "sdk_3";
            String url = globaldocumentUrl;
            //url = url.startsWith("http://") ? url : GlobesURL.DOC_PREFIX + url;
            Log.e("alex", "OutBrainFetching globaldocumentUrl: " + url);

            OBRequest request = new OBRequest(url, widgetIndex, widgetId);

            //Log.e("alex", "OBRequest: " + String.valueOf(widgetIndex) + "|||" + widgetId);

            //request.setMobileId(url);

            try {
                if (request != null) {
                    Outbrain.fetchRecommendations(request, new RecommendationsListener() {
                        @Override
                        public void onOutbrainRecommendationsSuccess(final OBRecommendationsResponse response) {
                            try {
                                if (response.getAll() != null) {
                                    if (response.getAll().size() >= 4) {
                                        Log.e("alex", "OutbrainRecommendation size1: " + String.valueOf(response.getAll().size()));
                                        ArrayList<OBRecommendation> recommendations = new ArrayList<OBRecommendation>();
                                        recommendations.add(response.get(0));
                                        recommendations.add(response.get(1));
                                        recommendations.add(response.get(2));
                                        recommendations.add(response.get(3));
                                        buildOutBrainView(recommendations, 4);
                                    } else {
                                        Log.e("alex", "OutbrainRecommendation size2: " + String.valueOf(response.getAll().size()));
                                        buildOutBrainView(response.getAll(), response.getAll().size());
                                    }
                                } else {
                                    Log.e("alex", "OutbrainRecommendation count 0!");

                                }
                            } catch (Exception ex) {
                                Log.e("alex", "onOutbrainRecommendations Error: " + ex);
                            }
                        }

                        @Override
                        public void onOutbrainRecommendationsFailure(Exception e) {
                            Log.e("alex", "Error outbrain", e);
                        }
                    });
                }
            } catch (Exception ex) {
                Log.e("alex", "Error outbrain fetching", ex);
            }
        } catch (Exception exGlobal) {
            Log.e("alex", "Error outbrain global", exGlobal);
        }
    }

    private void buildOutBrainView(ArrayList<OBRecommendation> recommendations, int size) {
        final LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View groupTitle = layoutInflater.inflate(R.layout.row_layout_group_for_out_brain, list_extra_document_by_outbrain, false);
        ((TextView) groupTitle.findViewById(R.id.grouptitle)).setText("כתבות נוספות");
        list_extra_document_by_outbrain.addView(groupTitle);
        for (int i = 0; i < size; i++) {
            final int sizeFinal = size;
            final int iFinal = i;
            final OBRecommendation recommendation = recommendations.get(i);
            new AsyncTask<Void, Void, String>() {
                @Override
                protected String doInBackground(Void... params) {
                    String docNewId = "unknown";

                    try {
                        HttpClient httpclient = new DefaultHttpClient();
                        HttpGet request = new HttpGet();
                        URI url = null;
                        try {
                            url = new URI(recommendation.getUrl());
                            //Log.e("alex", "OutBrainFetching url: " + url);
                        } catch (URISyntaxException e) {
                        }
                        request.setURI(url);
                        HttpResponse response = null;
                        try {
                            response = httpclient.execute(request);
                        } catch (ClientProtocolException e) {
                        } catch (IOException e) {
                        }
                        if (response.getStatusLine().getStatusCode() == 200) {
                            //Log.e("alex", "OutBrainFetching getStatusCode: " + response.getStatusLine().getStatusCode());

                            BufferedReader in = null;
                            try {
                                in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                            } catch (IllegalStateException e1) {
                            } catch (IOException e1) {
                            }
                            String line = "";
                            StringBuilder sbReturned = new StringBuilder();
                            try {
                                while ((line = in.readLine()) != null) {
                                    sbReturned.append(line);
                                }

                                //Log.e("alex", "OutBrainFetching sbReturned: " + sbReturned);
                            } catch (IOException e) {
                            }
                            docNewId = Utils.getSubStringByValueAd(sbReturned.toString(), "did=", "&");

                            if (docNewId.equals("unknown")) {
                                docNewId = Utils.getSubStringByValueAd(sbReturned.toString(), "doc_id=", "&");
                            }

                            Log.e("alex", "OutBrainFetching docNewId=: " + docNewId);
                            //Log.e("alex", "OutBrainFetching sbReturned: " + sbReturned);
                        } else {
                        }
                    } catch (Exception ex) {
                        Log.e("alex", "OutBrainFetching Exception: " + ex);
                    }
                    return docNewId;
                }

                protected void onPostExecute(final String result) {
                    View rowArticle = layoutInflater.inflate(R.layout.row_layout_new, list_extra_document_by_outbrain, false);
                    TextView text = (TextView) rowArticle.findViewById(R.id.title);
                    ImageView image = (ImageView) rowArticle.findViewById(R.id.image);
                    ImageView from_the_net = (ImageView) rowArticle.findViewById(R.id.from_the_net);

                    TextView createdOn = (TextView) rowArticle.findViewById(R.id.createdOn);
                    RelativeLayout front = (RelativeLayout) rowArticle.findViewById(R.id.front);

                    final Article article = new Article();
                    article.setCreatedOn(new SimpleDateFormat("dd/MM/yy").format(recommendation.getPublishDate()));
                    article.setTitle(recommendation.getContent());
                    article.setImage(recommendation.getThumbnail().getUrl());

                    Log.e("alex", "OutBrainFetching recommendation.getThumbnail().getUrl(): " + recommendation.getThumbnail().getUrl());

                    text.setText(article.getTitle());
                    text.setTypeface(almoni_aaa_regular);
                    text.setTextSize(22.5f);
                    text.setTextColor(Color.BLACK);

                    String imageToGet = (article.getImage());
                    image.setTag(article.getImage());
                    if (!imageToGet.equals(""))
                        Picasso.with(DocumentActivity_new.this).load(imageToGet.replaceAll("null", "").trim()).fit().into(image);
                    else image.setImageResource(R.drawable.stub_image_not_found);
                    OnClickListener listener = new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //Log.e("alex","Outbraing onClick!!!!!");
                            Utils.writeEventToGoogleAnalytics(DocumentActivity_new.this, "כתבות על הקלקות", "article_article_outbrain",
                                    null);
                            Intent intent = new Intent(DocumentActivity_new.this, DocumentActivity_new.class);
                            intent.putExtra(Definitions.CALLER, Definitions.WEBVIEW);
                            intent.putExtra("articleId", result);

                            if (recommendation != null) {
                                try {
                                    String sOutbrainLinkRegular = Outbrain.getOriginalContentURLAndRegisterClick(recommendation);
                                    Log.e("alex", "Outbrain onClick11111!!!!!: " + sOutbrainLinkRegular);
                                } catch (Exception exx) {
                                    Log.e("alex", "Outbrain onClick Error!!!!!: " + exx);
                                }
                            }

                            startActivity(intent);
                        }
                    };
                    OnClickListener listenerWeb = new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (recommendation != null) {
                                try {
                                    String sOutbrainLinkPayed = Outbrain.getOriginalContentURLAndRegisterClick(recommendation);

                                    Intent i = new Intent(Intent.ACTION_VIEW);
                                    //i.setData(Uri.parse(recommendation.getUrl()));
                                    i.setData(Uri.parse(sOutbrainLinkPayed));

                                    Log.e("alex", "Outbrain onClick222222!!!!!: " + sOutbrainLinkPayed);
                                    startActivity(i);
                                } catch (Exception exx) {
                                    Log.e("alex", "Outbrain onClick Error!!!!!: " + exx);
                                }
                            }
                        }
                    };
                    if (result.compareTo("unknown") != 0) {
                        text.setOnClickListener(listener);
                        image.setOnClickListener(listener);
                        createdOn.setOnClickListener(listener);
                        createdOn.setText(article.getCreatedOn());
                    } else {
                        text.setOnClickListener(listenerWeb);
                        image.setOnClickListener(listenerWeb);
                        createdOn.setOnClickListener(listenerWeb);
                        createdOn.setText(recommendation.getSourceName());
                        from_the_net.setVisibility(View.VISIBLE);
                    }
                    // front.setOnClickListener(listener);
                    rowArticle.setPadding(0, 10, 0, 10);
                    list_extra_document_by_outbrain.addView(rowArticle);
                    if (iFinal == sizeFinal - 1) {
                        // eli add the out brain logo
                        ImageView logo = new ImageView(DocumentActivity_new.this);
                        logo.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, 50));
                        image.setScaleType(ImageView.ScaleType.FIT_START);
                        // image.setMaxHeight(50);
                        Picasso.with(DocumentActivity_new.this).load(R.drawable.outbrain_logo).into(logo);
                        logo.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // http://www.outbrain.com/what-is/default/en-mobile/
                                Intent i = new Intent(Intent.ACTION_VIEW);
                                i.setData(Uri.parse(WWW_OUTBRAIN_COM));
                                startActivity(i);
                            }
                        });
                        list_extra_document_by_outbrain.addView(logo, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT));
                    }
                }
            }.execute();
        }
    }

//    private void loadRichMedia() {
//        adViewPublisheRichMedia = new PublisherAdView(this);
//        adViewPublisheRichMedia.setAdUnitId(Definitions.RICH_MEDIA_AD_UNIT);
//        AdSize[] adSizes =
//                {new AdSize(320, 533), new AdSize(320, 480)};
//        adViewPublisheRichMedia.setAdSizes(adSizes);
//
//        frameLayoutRichMediaContainer = (FrameLayout) findViewById(R.id.rich_media_container_document_portrait);
//        frameLayoutRichMediaContainer.addView(adViewPublisheRichMedia, new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT,
//                LayoutParams.MATCH_PARENT));
//        adViewPublisheRichMedia.setAdListener(new AdListener() {
//            public void onAdLoaded() {
//                Log.i(TAG, "Rich media SUCCESS ad: ");
//                adViewPublisheRichMedia.setVisibility(View.VISIBLE);
//                frameLayoutRichMediaContainer.setVisibility(View.VISIBLE);
//                // remove the rich media after RICH_MEDIA_VIEW_TIME
//                adViewPublisheRichMedia.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        adViewPublisheRichMedia.setVisibility(View.GONE);
//                        frameLayoutRichMediaContainer.setVisibility(View.GONE);
//                    }
//                }, Definitions.RICH_MEDIA_VIEW_TIME);
//            }
//
//            public void onAdFailedToLoad(int errorCode) {
//                // log
//                Log.i(TAG, "Rich media FAILED: " + errorCode);
//                // gone the views
//                adViewPublisheRichMedia.setVisibility(View.GONE);
//                frameLayoutRichMediaContainer.setVisibility(View.GONE);
//            }
//        });
//        PublisherAdRequest request = new PublisherAdRequest.Builder().build();
//        adViewPublisheRichMedia.loadAd(request);
//    }

    private void loadOpeningSplash() {
        Log.e("alex", "loadOpeningSplash v2 start..." + Definitions.dfp_interstitial_article);
        progressBar_parse.setVisibility(View.VISIBLE);

        final PublisherInterstitialAd mPublisherInterstitialAd =  new PublisherInterstitialAd(this);
        mPublisherInterstitialAd.setAdUnitId("/43010785/globes/Interstitial.globes");

        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
       // splashArticleContainer.addView(mPublisherInterstitialAd, lp);

        mPublisherInterstitialAd.setAdListener(new AdListener() {
            public void onAdLoaded() {

                Log.e("alex", "loadOpeningSplash v2 loaded...");

                mPublisherInterstitialAd.show();
                progressBar_parse.setVisibility(View.GONE);
                onBackPressedEnabled = false;
            }

            public void onAdFailedToLoad(int errorCode) {

                Log.e("alex", "loadOpeningSplash v2: " + Definitions.dfp_interstitial_main + " the error: " + errorCode);

                progressBar_parse.setVisibility(View.GONE);
                onBackPressedEnabled = true;
                splashArticleContainer.setVisibility(View.GONE);
                setDataToPage();

            }

            public void onAdOpened() {

            }

            public void onAdClosed() {
                Log.e("alex", "dfpAdHandle splash screen onAdClosed");
                onBackPressedEnabled = true;
                setDataToPage();

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
    /**
     * Called from initSplash once the time has elapsed and it is the right
     * moment to setPageToArticle
     */
    protected void setDataToPage() {
        Log.e("alex", "setDataToPage function!!!");
        if (setDataToPageCalled.compareAndSet(false, true)) {
            if (parsedNewsSet != null && parsedNewsSet.itemHolder != null && parsedNewsSet.itemHolder.elementAt(position) != null) {
                if (parsedDocument == null) {
                    String docId = ((Article) parsedNewsSet.itemHolder.elementAt(position)).getDoc_id();
                    globaldocumentUrl = GlobesURL.URLDocument.replace("XXXX", docId);
                    setPageToArticle(docId);

                    Log.e("alex", "showRichMedia ver2..." + mapWallaFusionData);
                    Handler handler1 = new Handler();
                    for (int a = 1; a <= 10; a++) {
                        handler1.postDelayed(new Runnable() {

                            @Override
                            public void run() {
                                if (!interstitialStarted) {
                                    if (!bShowWallaRichMedia && mapWallaFusionData != null) {
                                        Log.e("alex", "setDataToPage11");
                                        bShowWallaRichMedia = true;

                                        Handler handler2 = new Handler();
                                        handler2.postDelayed(new Runnable() {

                                            @Override
                                            public void run() {
                                                Log.e("alex", "showInterstitial 13:" + mapWallaFusionData);
                                                splashArticleContainer.removeCallbacks(closeRunable);
                                                showInterstitial(mapWallaFusionData); // Show RichMedia
                                            }
                                        }, 2000);
                                    }
                                }
                            }
                        }, 1000 * a);
                    }

                }
            } else {
                documentActivity.finish();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppEventsLogger.activateApp(DocumentActivity_new.this, Definitions.FACEBOOK_APP_ID);
    }

    @Override
    protected void onStart() {
        if (Session.getActiveSession() != null) {
            Session.getActiveSession().addCallback(statusCallback);
        }
        super.onStart();
        isActive = true;
    }

    @Override
    protected void onStop() {
        if (Session.getActiveSession() != null) {
            Session.getActiveSession().removeCallback(statusCallback);
        }

        Log.e("alex", "DocumentActivity_new onStop...");

        super.onStop();
        isActive = false;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        // The push
        // set true to coming from notifications on Definitions
        boolean isFromScheme = intent.getBooleanExtra(Definitions.SCHEME_COMING_FROM_SCHEME, false);
        if (!isFromScheme) {
            Definitions.setComingFromPush(true);
        }
        pushNotificationDocID = intent.getStringExtra("pushNotificationDocID");
        articleId = pushNotificationDocID;
        parsedNewsSet = createFakeNewsSet(pushNotificationDocID);
        position = 0;
        parsedDocument = null;
        setDataToPage();
    }



    void initAdView() {
        // linearLayoutBottomBarAndBanner the linear layout
        adViewPublisherPortrait = (PublisherAdView) this.findViewById(R.id.adView3);

       // adViewPublisherPortrait.setAdUnitId("/43010785/globes/king//slider.globes.ros");
        adViewPublisherPortrait.setAdSizes(new AdSize(320, 50), new AdSize(320, 170),AdSize.BANNER);

        //adViewPublisherPortrait.setAdSizes(new AdSize(320, 50),new AdSize(360, 50), new AdSize(360, 170));



        adViewPublisherPortrait.setAdListener(new AdListener() {
            public void onAdLoaded() {
                // Ad received
                Log.d(TAG, "Ad portrait received: ");
                Log.e("alex", "King/Slider onAdLoaded Banner data: " + adViewPublisherPortrait.getAdUnitId() + "===" + adViewPublisherPortrait.getAdSize());
                // adViewPublisherPortrait.setBackgroundColor(Color.WHITE);
                adViewPublisherPortrait.setVisibility(View.VISIBLE);
                frameAdViewContainer_Pull_to_refresh_top.setVisibility(View.VISIBLE);
            }

            public void onAdFailedToLoad(int errorCode) {
                Log.d(TAG, "Ad portrait failed to receive: " + errorCode);

                Log.e("alex", "King/Slider onAdFailedToLoad: " + errorCode + " Banner data: " + adViewPublisherPortrait.getAdUnitId() + "===" + adViewPublisherPortrait.getAdSize());

                adViewPublisherPortrait.setVisibility(View.GONE);
                frameAdViewContainer_Pull_to_refresh_top.setVisibility(View.GONE);
            }
        });
        PublisherAdRequest request = new PublisherAdRequest.Builder().build();
        adViewPublisherPortrait.loadAd(request);
    }

    void buildDFPSekindo()
    {
        final PublisherAdView adView = new PublisherAdView(this);
        adView.setAdSizes(new AdSize(300, 250));
        adView.setAdUnitId(Definitions.dfp_sekindo); // /43010785/globes/cube.globes  //"/7263/cube.globes"

        Log.e("alex", "buildDFPSekindo start:");

        adView.setAdListener(new AdListener() {
            public void onAdLoaded() {
                Log.e("alex", "buildDFPSekindo onAdLoaded:");
                adSekindoBottomBanner.addView(adView);
                adSekindoBottomBanner.setVisibility(View.VISIBLE);
            }

            public void onAdFailedToLoad(int errorCode) {
                Log.e("alex", "buildDFPSekindo onAdFailedToLoad:" + errorCode);
                adSekindoBottomBanner.setVisibility(View.GONE);
            }
        });
        PublisherAdRequest request = new PublisherAdRequest.Builder().build();
        adView.loadAd(request);
    }

    void buildBottomDFPBanners()
    {
        final PublisherAdView adView = new PublisherAdView(this);
        adView.setAdSizes(new AdSize(320, 50));

        //adView.setAdSizes(AdSize.SMART_BANNER);

        //adView.setAdSizes(AdSize.SMART_BANNER);

        adView.setAdUnitId(Definitions.dfp_floating);  //"/43010785/globes/floating.globes.ros"

        Log.e("alex", "buildBottomDFPBanners start:");

        adView.setAdListener(new AdListener() {
            public void onAdLoaded() {
                Log.e("alex", "buildBottomDFPBanners onAdLoaded:");
                frameAdViewContainer_Pull_to_refresh_bottom.addView(adView);
                frameAdViewContainer_Pull_to_refresh_bottom.setVisibility(View.VISIBLE);
            }

            public void onAdFailedToLoad(int errorCode) {
                Log.e("alex", "buildBottomDFPBanners onAdFailedToLoad:" + errorCode);
                frameAdViewContainer_Pull_to_refresh_bottom.setVisibility(View.GONE);
            }
        });
        PublisherAdRequest request = new PublisherAdRequest.Builder().build();
        adView.loadAd(request);
    }

    synchronized void changeSizeFont() {
        baseTextSize++;
        if (baseTextSize >= MAXFONTSIZE) {
            baseTextSize = MINFONTSIZE;
            isFontReduced = true;

        }
        saveFontPref(FONT_SIZE_KEY_PREF, baseTextSize);

        webViewDocumentTitle.getSettings().setDefaultFontSize((int) baseTextSize + 7);
        webViewDocumentSubTitle.getSettings().setDefaultFontSize((int) baseTextSize + 3);
        textViewDocumentModifiedOn.setTextSize((float) baseTextSize + 2);
        textViewDocumentWriter.setTextSize((float) baseTextSize + 2);
        wvViewDocumentText.getSettings().setDefaultFontSize((int) baseTextSize + 2);
        if (isFontReduced) {
            isFontReduced = false;
            wvViewDocumentText.loadData(articleTextHtml, "text/html; charset=utf-8", "UTF-8");
            webViewDocumentTitle.loadData(titleTextHtml, "text/html; charset=utf-8", "UTF-8");
            webViewDocumentSubTitle.loadData(subtitleTextHtml, "text/html; charset=utf-8", "UTF-8");
        }
    }

    void changeFontSize(int what) {
        switch (what) {
            case ENLARGETEXTSIZE:
                if (baseTextSize <= MAXFONTSIZE) {
                    baseTextSize++;
                    saveFontPref(FONT_SIZE_KEY_PREF, baseTextSize);
                }
                break;
            case REDUCETEXTSIZE:
                if (baseTextSize >= MINFONTSIZE) {
                    wvViewDocumentText.loadData(EMPTY_HTML, "text/html; charset=utf-8", "UTF-8");
                    webViewDocumentTitle.loadData(EMPTY_HTML, "text/html; charset=utf-8", "UTF-8");
                    webViewDocumentSubTitle.loadData(EMPTY_HTML, "text/html; charset=utf-8", "UTF-8");
                    isFontReduced = true;
                    baseTextSize--;
                    saveFontPref(FONT_SIZE_KEY_PREF, baseTextSize);
                }
                break;

            case RESETTEXTSIZE:
                // TODO save the font size here
                // baseTextSize = 12;
                baseTextSize = loadFontPref(FONT_SIZE_KEY_PREF);
                break;
        }
        webViewDocumentTitle.getSettings().setDefaultFontSize((int) baseTextSize + 7);
        webViewDocumentSubTitle.getSettings().setDefaultFontSize((int) baseTextSize + 3);
        textViewDocumentModifiedOn.setTextSize((float) baseTextSize + 2);
        textViewDocumentWriter.setTextSize((float) baseTextSize + 2);

        wvViewDocumentText.getSettings().setDefaultFontSize((int) baseTextSize + 2);
        if (isFontReduced) {
            isFontReduced = false;
            wvViewDocumentText.loadData(articleTextHtml, "text/html; charset=utf-8", "UTF-8");
            webViewDocumentTitle.loadData(titleTextHtml, "text/html; charset=utf-8", "UTF-8");
            webViewDocumentSubTitle.loadData(subtitleTextHtml, "text/html; charset=utf-8", "UTF-8");
        }
    }

    private class ImageGetter implements Html.ImageGetter {
        int ScreenWidth = getWindowManager().getDefaultDisplay().getWidth();

        @Override
        public Drawable getDrawable(final String source) {
            if (source != null && source.contains("http:")) {
                // bitmap = Utils.DownloadImage(source);
                getBitmap(source);
                BitmapDrawable bitmapDrawable = new BitmapDrawable(bitmap);
                if (bitmap != null) {
                    double bmpWidth = bitmap.getWidth();
                    double bmpHeight = bitmap.getHeight();
                    double newImageHeight, newImageWidth;

                    if (bitmap.getWidth() > ScreenWidth - 35) {
                        newImageWidth = ScreenWidth - 35;
                        newImageHeight = ((ScreenWidth - 35) * (bmpHeight / bmpWidth));
                    } else {
                        newImageWidth = bmpWidth;
                        newImageHeight = bmpHeight;
                    }
                    bitmapDrawable.setBounds(0, 0, (int) newImageWidth, (int) newImageHeight);
                    return bitmapDrawable;
                } else return new BitmapDrawable();
            }
            return new BitmapDrawable();
        }

        public void getBitmap(final String source) {
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    try {
                        bitmap = Utils.DownloadImage(source);
                    } catch (Exception e) {
                        Log.e("eli", "error 2076", e);
                    }
                }
            };
            new Thread(runnable).start();
        }
    }

    ;

    @SuppressLint("NewApi")
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case Definitions.DIALOGADDRESPONSE:
                LayoutInflater factory = LayoutInflater.from(this);
                final View textEntryView = factory.inflate(R.layout.a_new_alert_dialog_add_response, null);

                // final View textEntryView =
                // factory.inflate(R.layout.alert_dialog_add_response, null);
                final EditText responseUserName = (EditText) textEntryView.findViewById(R.id.editTextResponseUserName);
                final EditText responseSubject = (EditText) textEntryView.findViewById(R.id.editTextResponseSubject);
                final EditText responseText = (EditText) textEntryView.findViewById(R.id.editTextResponseText);

                TextView textview_add_comment = (TextView) textEntryView.findViewById(R.id.textview_add_comment);
                AlertDialog.Builder builder;
                try {
                    builder = new AlertDialog.Builder(DocumentActivity_new.this, android.R.style.Theme_Translucent);
                } catch (NoSuchMethodError e) {
                    builder = new AlertDialog.Builder(DocumentActivity_new.this);
                }
                final Dialog addResponse = builder.setView(textEntryView).create();

                addResponse.getWindow().setGravity(Gravity.FILL);
                ImageView Img_close_add_comment = (ImageView) textEntryView.findViewById(R.id.Img_close_add_comment);
                Img_close_add_comment.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        addResponse.dismiss();
                    }
                });
                textview_add_comment.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String responseUserNameString = responseUserName.getText().toString();
                        String responseSubjectString = responseSubject.getText().toString();
                        String responseTextString = responseText.getText().toString();
                        if (responseUserNameString.length() == 0 || responseSubjectString.length() == 0) {
                            Toast.makeText(DocumentActivity_new.this, Definitions.MissingItemsInResponse, Toast.LENGTH_SHORT).show();
                        } else {
                            createAndSendResponse(responseUserNameString, responseSubjectString, responseTextString);
                            // dismissDialog(Definitions.DIALOGADDRESPONSE);
                            addResponse.dismiss();
                        }
                    }
                });
                return addResponse;

            default:
                final Dialog dialog = new Dialog(this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.getWindow().setGravity(Gravity.BOTTOM);
                dialog.setContentView(R.layout.red_email_dialog);
                TextView tvEmailTitle = (TextView) dialog.findViewById(R.id.tvEmailTitle);
                Button btnEmailWrite = (Button) dialog.findViewById(R.id.btnEmailWrite);
                Button btnRedEmailClip = (Button) dialog.findViewById(R.id.btnRedEmailClip);
                Button btnRedEmailPic = (Button) dialog.findViewById(R.id.btnRedEmailPic);

                if (id == Definitions.RED_EMAIL_DIALOG) {
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
                } else if (id == Definitions.SEND_CLIP_DIALOG) {
                    btnEmailWrite.setVisibility(View.GONE);
                    tvEmailTitle.setText(myResources.getString(R.string.dialog_title_choose_video));
                    btnRedEmailClip.setText(myResources.getString(R.string.dialog_btn_film_video));
                    btnRedEmailClip.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            getAttachment(Definitions.CLIP_ATTACHMENT, Definitions.MAKE_ATTACHMENT);
                            dialog.dismiss();
                        }
                    });
                    btnRedEmailPic.setText(myResources.getString(R.string.dialog_btn_choose_pic));
                    btnRedEmailPic.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            getAttachment(Definitions.CLIP_ATTACHMENT, Definitions.CHOOSE_ATTACHMENT);
                            dialog.dismiss();
                        }
                    });
                } else if (id == Definitions.SEND_PIC_DIALOG) {
                    tvEmailTitle.setText(myResources.getString(R.string.dialog_btn_choose_pic));
                    btnEmailWrite.setVisibility(View.GONE);
                    btnRedEmailClip.setText(myResources.getString(R.string.dialog_btn_take_pic));
                    btnRedEmailClip.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            getAttachment(Definitions.PIC_ATTACHMENT, Definitions.MAKE_ATTACHMENT);
                            dialog.dismiss();
                        }
                    });
                    btnRedEmailPic.setText(myResources.getString(R.string.dialog_btn_choose_pic));
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
    }

    protected void startRedEmail(String attachmentUri) {
        // String mailBody =
        // "àðà îìà àú äôøèéí äáàéí:\nùí:\nèìôåï:\nãåàø àì÷èøåðé:\núåëï:\n\n\n\n";

        // set the redEmail body
        StringBuilder mailBody = new StringBuilder();
        mailBody.append(myResources.getString(R.string.red_email_body_title));
        mailBody.append(":\n");
        mailBody.append(myResources.getString(R.string.red_email_body_name));
        mailBody.append(":\n");
        mailBody.append(myResources.getString(R.string.red_email_body_phone));
        mailBody.append(":\n");
        mailBody.append(myResources.getString(R.string.red_email_body_email));
        mailBody.append(":\n");
        mailBody.append(myResources.getString(R.string.red_email_body_content));
        mailBody.append(":\n");

        Intent emailSend = new Intent();
        emailSend.setAction(Intent.ACTION_SEND);
        String mimeType = "message/rfc822";

        emailSend.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]
                {"redemail@globes.co.il"});

        emailSend.putExtra(Intent.EXTRA_SUBJECT, myResources.getString(R.string.red_email));

        emailSend.putExtra(Intent.EXTRA_TEXT, mailBody.toString());
        emailSend.putExtra(Intent.EXTRA_TITLE, R.string.red_email);
        if (attachmentUri != null && attachmentUri.contains(".")) {
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
                Log.e("eli", "error 2391", ex);
                Toast.makeText(this, "Your device does not contain an application to run this action", Toast.LENGTH_LONG).show();
            }
        } else {
            Intent getImage = new Intent(this, AttachmentPicker.class);
            getImage.putExtra("attachmentType", attachmentType);
            getImage.putExtra("getAttachmentMode", getAttacmentMode);

            startActivityForResult(getImage, ATTACHMENT_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        Log.e("eli", "requestCode " + requestCode + "");
        Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
        if ((resultCode == RESULT_OK)) {
            if (requestCode == Definitions.REQUEST_CLIP_CAPTURE) {
                Uri uriVideo1 = data.getData();
                String test = getVideoAttachmentPath(uriVideo1);
                startRedEmail(test);
            } else if (requestCode == ATTACHMENT_CODE) {
                String attachmentUri = data.getStringExtra("imageUri");
                startRedEmail(attachmentUri);
            }
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
            Log.e("eli", "error 2451", e);
        }

        return filename;
    }

    void createAndSendResponse(String responseUserName, String responseSubjectString, String responseTextString) {
        ResponseAsyncTask task = new ResponseAsyncTask(DocumentActivity_new.this);
        // Encoding the URL
        try {
            String responseToSend = GlobesURL.APIlocation_ArticleResponsesAddNew
                    .replaceAll("XXXX", URLEncoder.encode(((Article) parsedNewsSet.itemHolder.get(position)).getDoc_id(), "UTF-8"))
                    .replace("ZZZZ", URLEncoder.encode(responseUserName, "UTF-8"))
                    .replace("KKKK", URLEncoder.encode(responseSubjectString, "UTF-8"))
                    .replace("EEEE", URLEncoder.encode(responseTextString, "UTF-8"));

            // Send data
            URL url = new URL(responseToSend);
            task.execute(url);
        } catch (Exception e) {
            Log.e("eli", "error 2475", e);

            Toast.makeText(this, Definitions.SendingResponseError, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Browsing forward and backwards requires a valid newsSet. When there isn't
     * one (getting to a document from ShareActivity) it must be mocked up.
     */
    NewsSet createFakeNewsSet(String articleId) {
        NewsSet fakeNewsSet = new NewsSet();
        Article fakeArticle = new Article();
        fakeArticle.setDoc_id(articleId);
        fakeNewsSet.itemHolder.add(fakeArticle);
        return fakeNewsSet;
    }

    // TODO Action bar buttons .. share / enlarge text
    void createActionBarButtons() {
        buttonFacebook = (Button) findViewById(R.id.buttonFacebook);
        buttonFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareOnFacebook();
            }
        });
        buttonShare = (Button) findViewById(R.id.buttonShare);
        buttonShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (parsedDocument != null && parsedNewsSet != null) {
                    Intent shareArticle = new Intent();
                    shareArticle.setAction(Intent.ACTION_SEND);
                    String mimeType = "text/plain";
                    shareArticle.setType(mimeType);
                    shareArticle.putExtra(Intent.EXTRA_SUBJECT, (parsedDocument.getTitle()));
                    Log.e("eli", "getDoc_id" + parsedDocument.getDoc_id());
                    shareArticle.putExtra(Intent.EXTRA_TEXT, parsedDocument.getTitle() + "\n\n" + "http://iglob.es/?"
                            + removeOneAndZero(parsedDocument.getDoc_id()));
                    shareArticle.putExtra(Intent.EXTRA_TITLE, Definitions.ShareString);
                    startActivity(Intent.createChooser(shareArticle, Definitions.Share));
                }
            }
        });

        imageView_textSize_actionBar_documentN = (ImageView) findViewById(R.id.imageView_textSize_actionBar_documentN);
        imageView_textSize_actionBar_documentN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeSizeFont();
            }
        });

        buttonBrowseBack = (ImageView) findViewById(R.id.buttonBrowseBack);
        buttonBrowseBack.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Utils.writeEventToGoogleAnalytics(DocumentActivity_new.this, "כתבות על הקלקות", "article_article_swipe", null);
                browseBack();
            }
        });

        buttonBrowseForward = (ImageView) findViewById(R.id.buttonBrowseForward);
        buttonBrowseForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.writeEventToGoogleAnalytics(DocumentActivity_new.this, "כתבות על הקלקות", "article_article_swipe", null);
                browseForward();
            }
        });

        almoni_black = Typeface.createFromAsset(getAssets(), "almoni-dl-aaa-black.otf");
        buttonAddResponse = (Button) findViewById(R.id.buttonAddResponse);
        buttonAddResponse.setTypeface(almoni_black);
        buttonAddResponse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(Definitions.DIALOGADDRESPONSE);
            }
        });
        if (parsedDocumentResponses.getResponsesStat() > 0) {
            layoutDocumentResponsesHeader = (LinearLayout) findViewById(R.id.layoutDocumentResponsesHeader);
            layoutDocumentResponsesHeader.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent goToResponsesActivity = new Intent(documentActivity, DocumentResponsesActivity.class);

                    goToResponsesActivity.putExtra("documentId", ((Article) parsedNewsSet.itemHolder.get(position)).getDoc_id());
                    startActivity(goToResponsesActivity);
                }
            });
        }

        buttonPlayDocumentVideo = (Button) findViewById(R.id.buttonPlayDocumentVideo);

        // regular article with video inside it , video from f19 or HTML5
        if (parsedDocument != null && parsedDocument.getDoctype() == ArticleType.VIDEO_ARTICLE
                || (parsedDocument.getClipURLFromF19() != null && parsedDocument.getClipURLFromF19().length() > 1)
                || (parsedDocument.getClipUrlHTML5() != null && parsedDocument.getClipUrlHTML5().length() > 1)) {
            buttonPlayDocumentVideo.setVisibility(android.view.View.VISIBLE);

            // if there is content from f19 , regular or HTML5
            if ((parsedDocument.getClipURLFromF19() != null && parsedDocument.getClipURLFromF19().length() > 1)
                    || (parsedDocument.getClipUrlHTML5() != null && parsedDocument.getClipUrlHTML5().length() > 1)) {
                // set the url from getClipURL()
                buttonPlayDocumentVideo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.e("alex", "buttonPlayDocumentVideo: " + parsedDocument.isClipHTML5() + "===" + parsedDocument.getClipURLFromF19());

                        if (parsedDocument.isClipHTML5()) {
                            openVideoInMediaPlayerOrBrowser(parsedDocument.getClipUrlHTML5(), true, parsedDocument.getDoc_id());
                            Utils.writeEventToGoogleAnalytics(DocumentActivity_new.this, "כתבה", "סרטון",
                                    Utils.getSubStringByValue(parsedDocument.getClipUrlHTML5(), "movId"));
                        } else {
                            openVideoInMediaPlayerOrBrowser(parsedDocument.getClipURLFromF19(), false, parsedDocument.getDoc_id());
                            Utils.writeEventToGoogleAnalytics(DocumentActivity_new.this, "כתבה", "סרטון",
                                    Utils.getSubStringByValue(parsedDocument.getClipURLFromF19(), "movId"));
                        }
                    }
                });
            } else {
                // set the url from getClipURL()
                buttonPlayDocumentVideo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openVideoInMediaPlayerOrBrowser(parsedDocument.getClipURL(), false, parsedDocument.getDoc_id());
                    }
                });
            }

        }
    }

    // // called whenever an article was parsed
    // public void onConfigurationChanged()
    // {
    // if (isFinishedParsing)
    // {
    // if (pd != null)
    // {
    // try
    // {
    // pd.dismiss();
    // }
    // catch (Exception e)
    // {
    // Log.e("eli", "error", e);
    // }
    // }
    // loadRichMedia();
    // initAdView();
    // // createActionBarButtons();
    // setAllViews();
    // if (Definitions.toUseOutbrain)
    // {
    // outBrainFetching();
    // }
    // }
    // }

    /**
     * @param url   video url to open
     * @param docId
     */
    void openVideoInMediaPlayerOrBrowserNoThread(String url, boolean isHTML5, String docId) {
        String videoUrl;

        try {
            if (isHTML5) {
                videoUrl = url;
                // remove the apple type from url
                if (videoUrl.contains("&AppleType=html")) {
                    videoUrl = videoUrl.replace("&AppleType=html", "");
                }
            } else {
                Log.e("alex", "Definitions.toUseGIMA: " + Definitions.toUseGIMA);

                if (!Definitions.mediaPlayerInBrowser) {

                    /** get the device info and add it to the URL */
                    url = getDeviceDetailsForVideoUrl(url);

                    if (url.contains("http://www.cast-tv.biz/play/")) {
                        if (Definitions.toUseGIMA) {
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
                        } else {
                            videoUrl = Utils.resolveRedirect("http://www.cast-tv.biz/play/" + Utils.resolveRedirect(url, true), false);

                            //videoUrl =  Utils.resolveRedirect(url, true);

                            //Log.e("alex", "TV videoUrl: " + Utils.resolveRedirect(url, true));

                        }
                    } else {
                        videoUrl = Utils.resolveRedirect("http://www.globes.co.il" + Utils.resolveRedirect(url, true), false);
                    }
                } else {
                    videoUrl = url;
                }
            }

            //Log.e("alex", "TV videoUrl: " + videoUrl);
            //Log.e("alex", "TV videoUrl2 : " + url);
            //Log.e("alex", "videoUrl35: " + videoUrl + "===" + Definitions.toUseGIMA);

            if (videoUrl != null && !videoUrl.contains("Failed_To_Get_Url")) {
                String name = parsedDocument.getTitle();
                String mador = "כללי";
                Utils.writeEventToGoogleAnalytics(this, "הפעלת וידאו", mador, name);

                Uri uri = Uri.parse(videoUrl);
                if (Definitions.toUseGIMA) {
                    Log.e("alex", "TV videoUrl2: " + videoUrl);

                    // pre roll eli
                    //Intent i = new Intent(this, PlayVideoActivity.class);
                    Intent i = new Intent(this, PlayerImaActivity.class);
                    i.putExtra("docId", docId);
                    i.putExtra("videoURL", uri.toString());
                    startActivity(i);
                } else {
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    // intent.setDataAndType(data, "video/mp4");
                    startActivity(intent);
                }
            }
        } catch (ClientProtocolException e) {
            Log.e("eli", "error 2816", e);
        } catch (IOException e) {
            Log.e("eli", "error 2820", e);

        }
    }

    public void openVideoInMediaPlayerOrBrowser(final String url, final boolean isHTML5, final String docId) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    openVideoInMediaPlayerOrBrowserNoThread(url, isHTML5, docId);
                } catch (Exception e) {
                    Log.e("eli", "error 2838", e);
                }
            }
        };
        new Thread(runnable).start();
    }

    private class GestureListener extends SimpleOnGestureListener {

        private final int SWIPE_MIN_DISTANCE = 100;// 120;
        private static final int SWIPE_MAX_OFF_PATH = 350;
        private final int SWIPE_THRESHOLD_VELOCITY = 200;// 150;

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            try {
                if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH) {
                    return false;
                }
                if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    // browseForward();
                } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    // browseBack();
                }
            } catch (Exception e) {
                Log.e("eli", "error 2887", e);
            }
            return true;
        }
    }

    private class CustomeGestureDetector extends SimpleOnGestureListener {
        private final int SWIPE_MIN_DISTANCE = 100;// 120;
        private static final int SWIPE_MAX_OFF_PATH = 350;
        private final int SWIPE_THRESHOLD_VELOCITY = 200;// 150;

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            myScrollView.requestDisallowInterceptTouchEvent(true);
            if (e1 == null || e2 == null) return false;
            if (e1.getPointerCount() > 1 || e2.getPointerCount() > 1)
                return false;
            else {
                try { // right to left swipe .. go to prev page
                    if (e1.getX() - e2.getX() > 100 && Math.abs(velocityX) > 800) {// 200

                        // browseBack();
                        return true;
                    } // left to right swipe .. go to n page
                    else if (e2.getX() - e1.getX() > 100 && Math.abs(velocityX) > 800) {
                        // browseForward();

                        return true;
                    }
                } catch (Exception e) {
                    Log.e("eli", "error 2946", e);
                }
                return false;
            }
        }
    }

    // @Override
    // public boolean onTouchEvent(MotionEvent event)
    // {
    // if (swipeDetector != null && swipeDetector.onTouchEvent(event))
    // return true;
    // else return false;
    // }

    public void browseBack() {
        position--;
        if (position <= -1) {
            position = parsedNewsSet.itemHolder.size() - 1;
        }
        if (parsedNewsSet.itemHolder.get(position) instanceof Article
                && ((Article) parsedNewsSet.itemHolder.get(position)).getUrl() == null) {
            Intent intent = new Intent(documentActivity, DocumentActivity_new.class);
            Object unique = UUID.randomUUID();
            DataContext.Instance().SetArticleList(unique.toString(), parsedNewsSet);
            intent.putExtra("position", position);
            intent.putExtra("parentId", unique.toString());
            intent.putExtra("articleId", ((Article) parsedNewsSet.itemHolder.get(position)).getDoc_id());
            startActivity(intent);
        }
    }

    public void browseForward() {
        position++;
        if (position >= parsedNewsSet.itemHolder.size()) {
            position = 0;
        }
        if (parsedNewsSet.itemHolder.get(position) instanceof Article
                && ((Article) parsedNewsSet.itemHolder.get(position)).getUrl() == null) {
            Intent intent = new Intent(documentActivity, DocumentActivity_new.class);
            Object unique = UUID.randomUUID();
            DataContext.Instance().SetArticleList(unique.toString(), parsedNewsSet);
            intent.putExtra("position", position);
            intent.putExtra("parentId", unique.toString());
            intent.putExtra("articleId", ((Article) parsedNewsSet.itemHolder.get(position)).getDoc_id());
            startActivity(intent);
        }
    }

    // /**
    // * Created in order to load Ads Before the document Data is to be parsed
    // and
    // * shown
    // */
    // private void loadAdFromBrowseButtonsAndSetPageToArticle()
    // {
    // // Relative layout params
    // RelativeLayout.LayoutParams lp = new
    // RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
    // RelativeLayout.LayoutParams.MATCH_PARENT);
    // adViewSplashArticle = new PublisherAdView(this);
    // adViewSplashArticle.setAdUnitId(Definitions.ADUNIT_SPLASH_BETWEEN_ARTICLES);
    // adViewSplashArticle.setAdSizes(new AdSize(320, 507));
    // adViewSplashArticle.setAdListener(new AdListener()
    // {
    // public void onAdLoaded()
    // {
    // if (DataStore.getInstance().showAdMob())
    // {
    // DataStore.getInstance().setShowAdMob(false);
    // Log.d(TAG, "splash BrowsingButtonsSplashListener onReceiveAd succesful "
    // + Definitions.ADUNIT_SPLASH_BETWEEN_ARTICLES);
    // onBackPressedEnabled = false;
    // splashArticleContainer.setBackgroundColor(Color.BLACK);
    // splashArticleContainer.setVisibility(View.VISIBLE);
    // initBrowsingSplash(Definitions.ARTICLE_SPLASH_DISPLAY_LENGTH);
    // }
    // else
    // {
    // onBackPressedEnabled = true;
    // setPageToArticle(((Article)
    // parsedNewsSet.itemHolder.get(position)).getDoc_id());
    // }
    // }
    // public void onAdFailedToLoad(int errorCode)
    // {
    // Log.d(TAG, "splash BrowsingButtonsSplashListener onFailedToReceiveAd " +
    // "Ad is: "
    // + Definitions.ADUNIT_SPLASH_BETWEEN_ARTICLES + " Error is: " +
    // errorCode);
    // onBackPressedEnabled = true;
    // setPageToArticle(((Article)
    // parsedNewsSet.itemHolder.get(position)).getDoc_id());
    // }
    // });
    // splashArticleContainer.addView(adViewSplashArticle, lp);
    // PublisherAdRequest request = new PublisherAdRequest.Builder().build();
    // adViewSplashArticle.loadAd(request);
    // }

    void startTVActivity() {
        /*
         * Intent openClipsList = new Intent(this, SectionListActivity.class);
		 * openClipsList.putExtra("URI", GlobesURL.URLClips);
		 * openClipsList.putExtra(Definitions.CALLER, Definitions.SECTIONS);
		 * openClipsList.putExtra(Definitions.PARSER, Definitions.MAINSCREEN);
		 * openClipsList.putExtra(Definitions.HEADER, "????? TV");
		 * startActivity(openClipsList);
		 */
    }

    private String encodeTextForWebView(String html, EncodeType type) throws UnsupportedEncodingException {
        try {
            html = URLEncoder.encode(html, "utf-8").replaceAll("\\+", " ");
        } catch (UnsupportedEncodingException e) {
            Log.e("eli", "error 3097", e);
            html = "";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("<html>");
        sb.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">");
        sb.append("<body style=\"direction:rtl;\">");
        switch (type) {
            case TYPE_TITLE:
                sb.append("<strong style=\"line-height:30px\">" + html.trim() + "</strong>");
                break;
            case TYPE_SUBTITLE:
                sb.append("<strong style=\"line-height:25px\">" + html.trim() + "</strong>");
                break;
            case TYPE_BODY:
                sb.append("<span style=\"line-height:23px\">" + html.trim() + "</span>");
                break;
        }
        sb.append("</body>");
        sb.append("</html>");
        return sb.toString();
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
     * @author Eviatar Created to Avoid network operations on UI thread
     */
    private class ResponseAsyncTask extends AsyncTask<URL, Void, Boolean> {
        private Context contextAsync;

        public ResponseAsyncTask(Context contextAsync) {
            this.contextAsync = contextAsync;
        }

        @Override
        protected Boolean doInBackground(URL... urls) {
            try {
                boolean success;
                HttpURLConnection con = (HttpURLConnection) urls[0].openConnection();

                // Get the response
                BufferedReader readerForHTTPResponse = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String line = "";
                String buffer;
                while ((buffer = readerForHTTPResponse.readLine()) != null) {
                    line += buffer;
                }
                if (line.contains("500")) // error on response...
                {
                    success = false;
                } else {
                    success = true;
                }
                readerForHTTPResponse.close();
                return success;
            } catch (Exception e) {
                Log.e("eli", "error 3254", e);
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                Toast.makeText(DocumentActivity_new.this, Definitions.SendingResponseSuccess, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(DocumentActivity_new.this, Definitions.SendingResponseError, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void saveFontPref(String key, int size) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(key, size);
        editor.commit();
    }

    private int loadFontPref(String key) {
        return prefs.getInt(FONT_SIZE_KEY_PREF, 15);
    }

    private class galleryPagerAdapter extends PagerAdapter {

        ArrayList<ArticleGalleryObject> myGallery;
        LayoutInflater inflater;

        public galleryPagerAdapter(Context contextGal, ArrayList<ArticleGalleryObject> myGallery) {
            inflater = (LayoutInflater) contextGal.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.myGallery = myGallery;
        }

        public int getCount() {
            if (myGallery == null) {
                return 0;
            } else {
                return myGallery.size();
            }
        }

        public Object instantiateItem(View collection, int position) {
            // Log.e("eli", myGallery.get(position).getImageURL());
            if (getCount() > 1) {
                circlePageIndicator.setVisibility(View.VISIBLE);
            }
            View view = inflater.inflate(R.layout.gallery_object_layout, null);
            TextView nameAuthor = (TextView) view.findViewById(R.id.textViewAuthor);
            ImageView imageView = (ImageView) view.findViewById(R.id.imageViewPic);
            nameAuthor.setText(myGallery.get(position).getImageAuthor());
            imageView.setImageBitmap(myGallery.get(position).getBitmap());
            // check if it is a dummy picture than hide the author name , and
            // change scale type to not XY , center inside
            if (myGallery.get(position).getImageURL().equals("dummy")) {
                nameAuthor.setVisibility(View.GONE);
                imageView.setScaleType(ScaleType.CENTER_INSIDE);
                imageView.setBackgroundColor(Color.TRANSPARENT);
            }
            ((ViewPager) collection).addView(view, 0);

            return view;
        }

        @Override
        public void destroyItem(View arg0, int arg1, Object arg2) {
            ((ViewPager) arg0).removeView((View) arg2);
        }

        @Override
        public void finishUpdate(View arg0) {
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == ((View) arg1);
        }

        @Override
        public void restoreState(Parcelable arg0, ClassLoader arg1) {
        }

        @Override
        public Parcelable saveState() {
            return null;
        }

        @Override
        public void startUpdate(View arg0) {
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "";
        }
    }

    private void onInterstitialEnd() {
        if (!articleIsReady) {
            progressBar_parse.setVisibility(View.VISIBLE);
        }
        if (wallaFusionDataMap != null) {
            showTopBottomBanners(wallaFusionDataMap);
        }
    }

    private void initOpeningFromOnCreateSplash(long delay) {
        Log.e("alex", "initOpeningFromOnCreateSplash delay:" + delay);
        interstitialStarted = true;
        setDataToPage();

        closeRunable = new Runnable() {
            public void run() {
                interstitialStarted = false;
                onInterstitialEnd();
                splashArticleContainer.setVisibility(View.GONE);
                onBackPressedEnabled = true;
                setDataToPage();
            }
        };
        splashArticleContainer.postDelayed(closeRunable, delay);
    }

    // private void initBrowsingSplash(long delay)
    // {
    // splashArticleContainer.postDelayed(new Runnable()
    // {
    // public void run()
    // {
    // splashArticleContainer.setBackgroundColor(Color.TRANSPARENT);
    // onBackPressedEnabled = true;
    // setPageToArticle(((Article)
    // parsedNewsSet.itemHolder.get(position)).getDoc_id());
    //
    // }
    // }, delay);
    // }

    /**
     * Overriding the regular android behavior and loading all pages inside the
     * webview
     *
     * @author Eviatar TODO eli
     */
    private class MyWebViewClient extends WebViewClient {

        @Override
        public void onLoadResource(WebView view, String url) {
            if (url.contains("redirect")) {
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(i);
            } else {
                super.onLoadResource(view, url);
            }
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // check if it is share page to view
            // was:
            // "http://m.globes.co.il/news/m/..."

            if (url.contains("m.globes.co.il/")) {
                Uri uriUrl = Uri.parse(url);
                Intent launchMBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
                startActivity(launchMBrowser);
                return true;
            } else if (url.contains("instrumentid")) // "http://www.globes.co.il/globessites/globes/finance/instruments/instrument.aspx?instrumentid"
            {
                if (url.contains("feeder")) {
                    String feeder, insturmentID, uri;
                    insturmentID = new String(url.substring(url.indexOf("instrumentid=") + 13, url.indexOf("&feeder")));
                    feeder = new String(url.substring(url.indexOf("&feeder=") + 8, url.length()));
                    int endFeederIndex = feeder.indexOf("&");
                    if (endFeederIndex != -1) {
                        feeder = new String(feeder.substring(0, feeder.indexOf("&")));
                    }
                    uri = GlobesURL.URLSharePage.replaceAll("XXXX", feeder).replaceAll("YYYY", insturmentID);
                    Intent intent = new Intent(documentActivity, ShareActivity.class);
                    intent.putExtra("shareId", insturmentID);
                    intent.putExtra("feederId", feeder);
                    intent.putExtra(Definitions.CALLER, Definitions.SHARES);
                    intent.putExtra(Definitions.PARSER, Definitions.SHARES);
                    intent.putExtra("URI", uri);
                    startActivity(intent);
                    return true;
                } else {
                    String feeder, insturmentID, uri;
                    insturmentID = new String(url.substring(url.indexOf("instrumentid=") + 13, url.length()));
                    feeder = "0";
                    uri = GlobesURL.URLSharePage.replaceAll("XXXX", feeder).replaceAll("YYYY", insturmentID);
                    Intent intent = new Intent(documentActivity, ShareActivity.class);
                    intent.putExtra("shareId", insturmentID);
                    intent.putExtra("feederId", feeder);

                    intent.putExtra(Definitions.CALLER, Definitions.SHARES);
                    intent.putExtra(Definitions.PARSER, Definitions.SHARES);
                    intent.putExtra("URI", uri);
                    startActivity(intent);
                    return true;
                }
            } // this is a document article
            // was Definitions.URLDocumentToShare
            else if (url.contains("did=")) {
                Utils.writeEventToGoogleAnalytics(DocumentActivity_new.this, "כתבות על הקלקות", "article_article_link", null);
                String articleID = new String(url.substring(url.indexOf("did=") + 4, url.length()));
                int endArticleIDIndex = articleID.indexOf("#");
                if (endArticleIDIndex != -1) {
                    articleID = new String(articleID.substring(0, endArticleIDIndex));
                }
                Intent intent = new Intent(documentActivity, DocumentActivity_new.class);
                intent.putExtra(Definitions.CALLER, Definitions.WEBVIEW);

                intent.putExtra("articleId", articleID);
                startActivity(intent);
                // DocumentActivity.this.finish();
                return true;
            } else if (url.contains(".tag") || url.contains(".TAG")) {
                try {
                    String urlUtf8 = java.net.URLDecoder.decode(url, "UTF-8");
                    String[] urlUtf8Elements = urlUtf8.split("/");
                    String tagElement = urlUtf8Elements[urlUtf8Elements.length - 1];
                    String tagNameElement = tagElement.split("\\.")[0];
                    // Log.e("eli","mador from doc: "+ tagNameElement);
                    Utils.writeEventToGoogleAnalytics(DocumentActivity_new.this, "תגיות על הקלקות", "tagit_article_link", null);

                    clickToTagit(tagNameElement);
                } catch (UnsupportedEncodingException e) {
                    Log.e("eli", "error 3535", e);
                }
                return true;
            }
            // else if (url.contains("aniview.com") ||
            // url.contains("ani-view.com") || url.contains("liverail.com"))
            // {
            // Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            // startActivity(i);
            // return false;
            //
            // }
            else {
                if (!url.contains("http://")) {
                    url = "http://www.globes.co.il";
                }
                Resources resources = getResources();
                String tagiotTitle = resources.getString(R.string.dialog_link_title);
                String tagiotBody = resources.getString(R.string.dialog_link_body);
                String tagiotContinue = resources.getString(R.string.dialog_continue);
                String tagiotCancel = resources.getString(R.string.dialog_cancel);
                showDialog(tagiotTitle, tagiotBody, tagiotContinue, tagiotCancel, url);
                return true;
            }

        }

    }

    public void showDialog(String title, String msg, String neutBtn, String negBtn, final String url) {
        AlertDialog.Builder ad = new AlertDialog.Builder(this);
        ad.setTitle(title);
        ad.setMessage(msg);

        ad.setPositiveButton(neutBtn, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
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

    @Override
    public void onBackPressed() {
        if (onBackPressedEnabled) {
            super.onBackPressed();
        }
    }

    private void showResponseDialog(Response response) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        AlertDialog alert;
        if (parsedDocumentResponses != null) {
            builder.setMessage(response.getResponseText()).setCancelable(true);
            builder.setTitle(response.getResponseSubject());
            alert = builder.create();
        } else {
            builder.setMessage("ללא תוכן אנא נסה/י שנית").setCancelable(true).setTitle("שגיאת תוכן");
            alert = builder.create();
        }
        alert.show();
    }

    private class SessionStatusCallback implements Session.StatusCallback {
        public void call(Session session, SessionState state, Exception exception) {
            // Log.e("eli", "state " + state);
            Log.e("eli", "error 3716", exception);
            if (pendingPublishReauthorization && state == SessionState.OPENED_TOKEN_UPDATED) {
                Log.e("eli", "call 1");
                pendingPublishReauthorization = false;
                publishStory();
            }
            if (isFromShareButton && state == SessionState.OPENED) {
                isFromShareButton = false;
                publishStory();
            }
            if (state == SessionState.CLOSED_LOGIN_FAILED) {
                Log.e("eli", "error in facebook", exception);
            }
        }
    }

    // share on our wall in FB
    private void publishStory() {
        Log.e("eli", "in publishStory");
        Session session = Session.getActiveSession();
        progressDialogShareSettings = new ProgressDialog(this);
        progressDialogShareSettings.setCancelable(false);
        progressDialogShareSettings.setMessage("Sending");
        try {
            progressDialogShareSettings.show();
        } catch (Exception e) {
            Log.e("eli", "error 3751", e);

        }
        if (session != null) {
            List<String> permissions = session.getPermissions();
            Bundle postParams = new Bundle();
            postParams.putString("name", getResources().getString(R.string.dialog_link_title));
            postParams.putString("message", parsedDocument.getTitleText());
            postParams.putString("link", GlobesURL.URLDocumentToShare + ((Article) parsedNewsSet.itemHolder.get(position)).getDoc_id());
            Request.Callback callback = new Request.Callback() {
                public void onCompleted(com.facebook.Response response) {
                    try {
                        progressDialogShareSettings.dismiss();
                    } catch (Exception e) {
                        Log.e("eli", "error 3783", e);
                    }
                    FacebookRequestError error = response.getError();
                    if (error != null) {
                        Toast.makeText(DocumentActivity_new.this, error.getErrorMessage(), Toast.LENGTH_SHORT).show();
                    } else {
                        JSONObject graphResponse = response.getGraphObject().getInnerJSONObject();
                        String postId = null;
                        try {
                            postId = graphResponse.getString("id");
                            // Log.e("eli", "postId " + postId);
                        } catch (JSONException e) {
                            Log.e("eli", "error 3801", e);
                        }
                        DisplayShareDialogSuccess();
                    }
                }
            };
            Request request = new Request(session, "me/feed", postParams, HttpMethod.POST, callback);
            RequestAsyncTask task = new RequestAsyncTask(request);
            task.execute();
        }
    }

    private void DisplayShareDialogSuccess() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(R.string.app_name);
        builder.setMessage(R.string.FB_success_post);

        builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();

    }

    private boolean isSubsetOf(Collection<String> subset, Collection<String> superset) {
        for (String string : subset) {
            if (!superset.contains(string)) {
                return false;
            }
        }
        return true;
    }

    public void shareOnFacebook() {
        Session session = Session.getActiveSession();
        if (session != null) {
            // Log.e("eli", "in shareOnFacebook");
            if (session.isOpened()) {
                publishStory();
                // Log.e("eli", "session isOpened");
            } else {
                // Log.e("eli", "session isClosed");
                isFromShareButton = true;
                if (!session.isOpened() && !session.isClosed()) {
                    Log.e("eli", "session " + session.getState().name());
                    session.openForPublish(new Session.OpenRequest(this).setPermissions(PERMISSIONS).setCallback(statusCallback));
                } else {
                    Log.e("eli", "session " + session.getState().name());
                    Session.openActiveSession(this, true, statusCallback);
                }
            }
        } else {
            Session.openActiveSession(this, true, statusCallback);
        }
    }

    class AjillionHandle extends AsyncTask<Void, Void, Void> {
        private ImageView ajillionObjImage;
        private AjillionObj ajillionObj;
        private ImageView imageCloseView;

        @Override
        protected void onPreExecute() {
            Log.e("alex", "AjillionObj 1");

            DataStore.getInstance().setShowAdMob(false);
            progressBar_parse.setVisibility(View.VISIBLE);
            ajillionObjImage = new ImageView(DocumentActivity_new.this);
            imageCloseView = new ImageView(DocumentActivity_new.this);
            imageCloseView.setImageResource(R.drawable.close_ad);
            imageCloseView.setPadding(32, 32, 32, 32);
            imageCloseView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    splashArticleContainer.setVisibility(View.GONE);
                    interstitialStarted = false;
                    onInterstitialEnd();
                    closeAjillionAd();
                }
            });
            imageCloseView.setVisibility(View.GONE);
            splashArticleContainer.addView(ajillionObjImage, FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
            splashArticleContainer.addView(imageCloseView, FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                ajillionObj = Utils.getAjillionObj(DocumentActivity_new.this, Definitions.AJILLION_AD_ID_BTWEEN_ARTICLE, 320, 480, false);
            } catch (Exception ex) {
                Log.e("alex", "AjillionHandle doInBackground Error: " + ex);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            progressBar_parse.setVisibility(View.GONE);

            if (ajillionObj == null || ajillionObj.getSuccess() == null || !ajillionObj.getSuccess()
                    || ajillionObj.getCreative_url() == null || ajillionObj.getCreative_url().equals("")) {
                closeAjillionAd();
                return;
            } else if (ajillionObj.getCreative_type().equals("image")) {
                Picasso.with(DocumentActivity_new.this).load(ajillionObj.getCreative_url()).fit().into(ajillionObjImage, new Callback() {
                    @Override
                    public void onSuccess() {
                        Log.e("eli", "yyyyyyyyyyyy");
                        onBackPressedEnabled = false;
                        imageCloseView.setVisibility(View.VISIBLE);
                        ajillionObjImage.setVisibility(View.VISIBLE);
                        splashArticleContainer.setVisibility(View.VISIBLE);
                        ajillionObjImage.setOnClickListener(new OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(ajillionObj.getClick_url()));
                                startActivity(browserIntent);
                                closeAjillionAd();
                            }
                        });
                        initOpeningFromOnCreateSplash(Definitions.ARTICLE_SPLASH_DISPLAY_LENGTH);
                    }

                    @Override
                    public void onError() {
                        Log.e("eli", "nnnnnnnnnnn");
                        closeAjillionAd();
                    }
                });
            }
        }

        private void closeAjillionAd() {
            onBackPressedEnabled = true;
            interstitialStarted = false;
            onInterstitialEnd();
            splashArticleContainer.setVisibility(View.GONE);
            splashArticleContainer.removeAllViews();
            setDataToPage();
        }
    }

    private String removeOneAndZero(String doc_id) {
        if (doc_id.length() > 1 && doc_id.startsWith("1")) {
            int howManyZeros = 0;
            for (int i = 1; i < doc_id.length(); i++) {
                if (doc_id.charAt(i) != '0')
                    break;
                else howManyZeros++;
            }
            return doc_id.substring(howManyZeros + 1);
        } else {
            return doc_id;
        }
    }

    class initAjillionView extends AsyncTask<Void, Void, Void> {
        private ImageView ajillionObjImage;
        private WebView ajillionObjImageGif;
        private AjillionObj ajillionObj;
        private RelativeLayout frame;
        private String id;

        public initAjillionView(RelativeLayout frame, String id) {
            super();
            this.frame = frame;
            this.id = id;

            Log.e("alex", "AjillionObj 2");
        }

        @Override
        protected void onPreExecute() {
            Log.e("alex", "AjillionObj 3");

            ajillionObjImage = new ImageView(DocumentActivity_new.this);
            ajillionObjImageGif = new WebView(DocumentActivity_new.this);

            Utils.centerViewInRelative(ajillionObjImageGif, false);
            Utils.centerViewInRelative(ajillionObjImage, true);
            ajillionObjImage.setScaleType(ScaleType.FIT_XY);

            frame.setVisibility(View.VISIBLE);
            frame.removeAllViews();
            frame.addView(ajillionObjImage);
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                ajillionObj = Utils.getAjillionObj(DocumentActivity_new.this, id, 320, 50, false);
            } catch (Exception ex) {
                Log.e("alex", "AjillionHandle doInBackground Error: " + ex);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            Log.e("eli", ajillionObj.toString());
            if (ajillionObj == null || ajillionObj.getSuccess() == null || !ajillionObj.getSuccess()
                    || ajillionObj.getCreative_url() == null || ajillionObj.getCreative_url().equals("")) {
                return;
            } else if (ajillionObj.getCreative_type().equals("image")) {
                if (!ajillionObj.getCreative_url().endsWith(".gif")) {
                    Picasso.with(DocumentActivity_new.this).load(ajillionObj.getCreative_url()).into(ajillionObjImage);
                    ajillionObjImage.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(ajillionObj.getClick_url()));
                            startActivity(browserIntent);
                        }
                    });
                } else {
                    frame.addView(ajillionObjImageGif);
                    ajillionObjImageGif.getSettings().setJavaScriptEnabled(true);
                    ajillionObjImageGif.getSettings().setAppCacheEnabled(false);
                    ajillionObjImageGif
                            .loadData(Utils.centerHTML(ajillionObj.getCreative_url(), true), "text/html; charset=utf-8", "UTF-8");
                    ajillionObjImageGif.setOnTouchListener(new OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(ajillionObj.getClick_url()));
                            startActivity(browserIntent);
                            return true;
                        }
                    });
                }
            } else {
                frame.addView(ajillionObjImageGif);
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
                ajillionObjImageGif.setOnTouchListener(new OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(ajillionObj.getClick_url()));
                        startActivity(browserIntent);
                        return true;
                    }
                });
            }
        }
    }

    @Override
    public void onTaskCompleted(Map<String, Map<String, String>> map) {
        if(Definitions.wallafusion) {
            Log.e("alex", "setDataToPage: onTaskCompleted" + "===" + wallaFusionArticleInterstitialShown + "===" + Definitions.wallafusion);

            if (map != null) {
                mapWallaFusionData = map;
            }

            if (wallaFusionArticleInterstitialShown) // show maavaron
            {
                Log.e("alex", "AAAAAAAAAAAAAAAAA : onTaskCompleted Show Maavaron");
                wallaFusionDataMap = map;
                showInterstitial(wallaFusionDataMap);
            } else {
                //Log.e("alex", "AAAAAAAAAAAAAAAAA : onTaskCompleted Show Floating Banners");
                showTopBottomBanners(map);
            }
        }
    }

    private void showTopBottomBanners(Map<String, Map<String, String>> map) {
        wallaFusionDataMap = null;
        wallaFusionArticleInterstitialShown = false;

        if (Definitions.toUseLiveBoxMain) {
            String sKingSpace = Definitions.WALLA_SPACE_KING;

            Log.e("alex", "DocumentActNew: showLiveBox: " + map.get(sKingSpace));
            if (map.containsKey(sKingSpace) && map.get(sKingSpace) != null) {
                Log.e("alex", "DocumentActNew: starting showLiveBox..." + map.get(sKingSpace));

                WallaAdvParser.showLiveBox(map.get(sKingSpace), frameAdViewContainer_Pull_to_refresh_top, getApplicationContext(), DocumentActivity_new.this);
                //return;
            } else {
                Log.e("alex", "DocumentActNew: showTopBanner");
                showTopBanner(map);
            }
        }

        showBottomBannerBeforeTaboola(map);
        showBottomFloatingBanner(map);
        showBottomBannerAfterResponses(map);
        loadWallaFusionDCScripts(map);
    }

    private void showInterstitial(Map<String, Map<String, String>> map) {

        Log.e("alex", "showInterstitial_document 2");

        String space = Definitions.WALLA_SPACE_MAAVARON;

        if (bShowWallaRichMedia) {
            space = Definitions.WALLA_SPACE_N_RM1;
        }

        if (map == null || !map.containsKey(space)) {
            closeSplashAd();
            return;
        }

        Map<String, String> m = map.get(space);
        if (m == null) {
            closeSplashAd();
            return;
        }

        String sViewReportURL = m.get("ADSRV") + m.get("VIEWREPORT");
        final String sClickReportURL = m.get("ADSRV") + m.get("CLICKREPORT");

        if (sClickReportURL == "") {
            closeSplashAd();
            return;
        }

        if (sViewReportURL == "") {
            closeSplashAd();
            return;
        } else {
            try {
                WallaAdvParser.sendWallaReportEvent(sViewReportURL);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }

        String sContentType = m.get("CONTENTTYPE").toLowerCase();

        //sContentType = "html";

        Log.e("alex", "AAAAAAAAAAAAAAAAA showInterstitial: " + sContentType);

        final ImageView wallaImage;
        final ImageView imageCloseView;
        final WebView webView_iframe_ad;
        DataStore.getInstance().setShowAdMob(false);

        //splashArticleContainer.setVisibility(View.VISIBLE);

        webView_iframe_ad = new WebView(DocumentActivity_new.this);
        wallaImage = new ImageView(DocumentActivity_new.this);
        imageCloseView = new ImageView(DocumentActivity_new.this);
        imageCloseView.setImageResource(R.drawable.close_ad);
        imageCloseView.setPadding(32, 32, 32, 32);
        imageCloseView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                splashArticleContainer.setVisibility(View.GONE);
                closeSplashAd();
            }
        });
        imageCloseView.setVisibility(View.GONE);

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
        webView_iframe_ad.setVisibility(View.GONE);
        webViewSettings.setSupportMultipleWindows(true);

        //splashArticleContainer.removeAllViews();
        splashArticleContainer.addView(webView_iframe_ad, FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        splashArticleContainer.addView(wallaImage, FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        splashArticleContainer.addView(imageCloseView, FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);

        imageCloseView.setVisibility(View.VISIBLE);
        wallaImage.setVisibility(View.VISIBLE);
        splashArticleContainer.setVisibility(View.VISIBLE);

        final int iDelayInterstitialTime = Integer.parseInt(m.get("DELAYTIME"));

        if (sContentType.equals("image") || sContentType.equals("anigif")) {
            String sAdUrl = m.get("ADURL_01");
            final String sClickUrl = m.get("CLICKURL");

            //if(iDelayInterstitialTime == 0){iDelayInterstitialTime = 6;} //default

            //Log.e("alex", "AAAAAAAAAAAAAAAAA Loading Image 0: " + sAdUrl + "===" + sClickUrl);

            //Log.e("alex", "DelayInterstitialTime: " + iDelayInterstitialTime);

            if (sAdUrl == "" || sClickUrl == "") {
                closeSplashAd();
                return;
            }

            //final Uri lnkToRedirect = Uri.parse(sClickUrl);

            if (!sAdUrl.endsWith(".gif") && !sAdUrl.endsWith(".png")) {
                Picasso.with(DocumentActivity_new.this).load(sAdUrl).fit().into(wallaImage, new Callback() {
                    @Override
                    public void onSuccess() {
                        onBackPressedEnabled = false;

                        //initOpeningFromOnCreateSplash(Definitions.ARTICLE_SPLASH_DISPLAY_LENGTH);

                        //initOpeningFromOnCreateSplash(iDelayInterstitialTime * 1000);

                        imageCloseView.setVisibility(View.VISIBLE);
                        wallaImage.setVisibility(View.VISIBLE);
                        splashArticleContainer.setVisibility(View.VISIBLE);
                        wallaImage.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                try {
                                    WallaAdvParser.sendWallaReportEvent(sClickReportURL);
                                } catch (MalformedURLException e) {
                                    e.printStackTrace();
                                }
                                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(sClickUrl));
                                startActivity(browserIntent);
                                closeSplashAd();
                            }
                        });
                        //initOpeningFromOnCreateSplash(Definitions.ARTICLE_SPLASH_DISPLAY_LENGTH);
                        initOpeningFromOnCreateSplash(iDelayInterstitialTime * 1000);
                    }

                    @Override
                    public void onError() {
                        closeSplashAd();
                    }
                });
            } else {
                wallaImage.setVisibility(View.GONE);
                imageCloseView.setVisibility(View.VISIBLE);
                webView_iframe_ad.setVisibility(View.VISIBLE);

                webView_iframe_ad.getSettings().setJavaScriptEnabled(true);
                webView_iframe_ad.getSettings().setAppCacheEnabled(false);
                webView_iframe_ad.loadDataWithBaseURL(null, "<!DOCTYPE html><html><body style = \"text-align:center\"><img style=\"width:99%;\" src= " + sAdUrl + " alt=\"page Not Found\"></body></html>", "text/html", "UTF-8", null);

                //initOpeningFromOnCreateSplash(Definitions.ARTICLE_SPLASH_DISPLAY_LENGTH);
                initOpeningFromOnCreateSplash(iDelayInterstitialTime * 1000);

                webView_iframe_ad.setOnTouchListener(new View.OnTouchListener() {
                    @SuppressLint("ClickableViewAccessibility")
                    public boolean onTouch(View v, MotionEvent event) {
                        if (event.getAction() == MotionEvent.ACTION_DOWN) {
                            closeSplashAd();

                            try {
                                WallaAdvParser.sendWallaReportEvent(sClickReportURL);
                            } catch (MalformedURLException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }

                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(sClickUrl));
                            startActivity(browserIntent);
                            return true;
                        }
                        return false;
                        // return (event.getAction() == MotionEvent.ACTION_MOVE);
                    }
                });
            }
        }

        if (sContentType.equals("html") || sContentType.equals("url")) {
            String Payload = m.get("Payload");

            Log.e("alex", "DocumentActNew Payload: " + Payload);

            wallaImage.setVisibility(View.GONE);
            imageCloseView.setVisibility(View.VISIBLE);
            webView_iframe_ad.setVisibility(View.VISIBLE);

            //Payload = "<!doctype html><html><head><meta content=\"width=device-width,minimum-scale=1.0,maximum-scale=1.0,user-scalable=no\" name=\"viewport\"><style type=\"text/css\">html,body{    height:100%;}body{margin:0;}.center{display:table; overflow:hidden; width:100%; height:100%; text-align:center; vertical-align:middle;}.center>.wrapper{display:table-cell; vertical-align:middle;}.wrapper:before{}.wrapper:before,.wrapper:after{content:\"\"; display:block; height:50%;}</style></head>  <body> <div id=\"centerdiv\" class=\"center\">   <div class=\"wrapper\"><script type=\"text/javascript\">var fusionVars = {'ADID': '3830026636','ADTYPE': 'Interstitial','CONTENTTYPE':'Html','ADURL_01':'','WIDTH_01': '320','HEIGHT_01': '416','ORIENTATION':'Portrait','ALIGNMENT':'Center','CLICKURL':'','CLICKREPORT': 'event/xyvnd/mint.globes_app.capitalmarket/3830026636/click','ADTRANSITION': 'None','ADSERVER': 'http://fus.walla.co.il:80/','ADSRV': 'http://fus.walla.co.il/','VIEWREPORT': 'impression/xyvnd/mint.globes_app.capitalmarket/l_globes_app_c/3830026637','CLOSEBTNSHOW':'True','CLOSEBTNALIGNMENT':'TopLeft','CLOSEBTNDELAY':'0','CLOSEBTNURL':'','BGCOLOR':'000000','DELAYTIME':'6','EXTERNALBROWSER':'False','FULLSCREEN':'False'}if (fusionVars['CONTENTTYPE'] == \"Mraid\"){  document.getElementById(\"centerdiv\").style.height = \"0px\";}</script><!--advertiser tag goes here--><IFRAME src=\"http://ad.doubleclick.net/adi/N9940.109338WALLA.CO.IL/B8193802.110694681;sz=320x460;ord=[timestamp]?\" WIDTH=320 HEIGHT=460 MARGINWIDTH=0 MARGINHEIGHT=0 HSPACE=0 VSPACE=0 FRAMEBORDER=0 SCROLLING=no BORDERCOLOR='#cc0000'><SCRIPT language='JavaScript1.1' SRC=\"http://ad.doubleclick.net/adj/N9940.109338WALLA.CO.IL/B8193802.110694681;abr=!ie;sz=320x460;ord=[timestamp]?\"></SCRIPT><NOSCRIPT><A HREF=\"http://ad.doubleclick.net/jump/N9940.109338WALLA.CO.IL/B8193802.110694681;abr=!ie4;abr=!ie5;sz=320x460;ord=[timestamp]?\"><IMG SRC=\"http://ad.doubleclick.net/ad/N9940.109338WALLA.CO.IL/B8193802.110694681;abr=!ie4;abr=!ie5;sz=320x460;ord=[timestamp]?\" BORDER=0 WIDTH=320 HEIGHT=460 ALT=\"Advertisement\"></A></NOSCRIPT></IFRAME><!--ends here--></div></div></body></html>";

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
                                    closeSplashAd();
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

            //Log.e("alex", "DocumentActNew before loading into webview: " + iDelayInterstitialTime);
            webView_iframe_ad.loadDataWithBaseURL(null, Payload, "text/html", "utf-8", null);

            //initOpeningFromOnCreateSplash(Definitions.ARTICLE_SPLASH_DISPLAY_LENGTH);
            initOpeningFromOnCreateSplash(iDelayInterstitialTime * 1000);

        }

        if (sContentType.equals("mraid")) {
            closeSplashAd();
            return;
        }

    }

    private void closeSplashAd() {
        onBackPressedEnabled = true;
        interstitialStarted = false;
        onInterstitialEnd();
        splashArticleContainer.setVisibility(View.GONE);
        splashArticleContainer.removeAllViews();
        setDataToPage();
    }

    private void loadWallaFusionDCScripts(Map<String, Map<String, String>> map) {
        String space = Definitions.WALLA_SPACE_DC1;

        if (map == null || !map.containsKey(space)) {
            return;
        }

        Map<String, String> m = map.get(space);

        if (m == null) {
            return;
        }

        String Payload = m.get("Payload");

        WallaAdvParser.loadSimplyDataIntoWebView(frameWallaFusionDCScripts, getApplicationContext(), Payload);
        frameWallaFusionDCScripts.setVisibility(View.VISIBLE);
        Log.e("alex", "loadWallaFusionDCScripts done");
    }

    private void showTopBanner(Map<String, Map<String, String>> map) {
        try {
            String space = Definitions.WALLA_SPACE_TOP_FLOAT; //"n_slider";
            if (map == null || !map.containsKey(space)) {
                return;
            }

            Map<String, String> m = map.get(space);

            if (m == null) {
                //frameAdViewContainer_Pull_to_refresh.setVisibility(View.GONE);
                frameAdViewContainer_Pull_to_refresh_top.setVisibility(View.GONE);
                return;
            }

            //frameAdViewContainer_Pull_to_refresh.setVisibility(View.VISIBLE);
            frameAdViewContainer_Pull_to_refresh_top.setVisibility(View.VISIBLE);

            Log.e("alex", "showTopBanner!!!!!!! " + m);

            WallaAdvParser.drawWallaBanner(frameAdViewContainer_Pull_to_refresh_top, getApplicationContext(), m);
        } catch (Exception ex) {
            Log.e("alex", "Error Walla Bottom Floating Banner");
        }
    }

    private void showBottomBannerBeforeTaboola(Map<String, Map<String, String>> map) {
        try {
            String space = Definitions.WALLA_SPACE_BOTTOM_PHONE;

            if (map == null || !map.containsKey(space)) {
                return;
            }

            Map<String, String> m = map.get(space);

            if (m == null) {
                adView_frame_bottom_phone_banner.setVisibility(View.GONE);
                return;
            }

            adView_frame_bottom_phone_banner.setVisibility(View.VISIBLE);

            WallaAdvParser.drawWallaBanner(adView_frame_bottom_phone_banner, getApplicationContext(), m);

            //String Payload = m.get("Payload");

            //WallaAdvParser.loadSimplyDataIntoWebView(adView_frame_bottom_phone_banner, getApplicationContext(), Payload);
            //frameWallaFusionDCScripts.setVisibility(View.VISIBLE);

        } catch (Exception ex) {
            Log.e("alex", "Error Walla Bottom Banner After Taboola");
        }
    }

    private void showBottomFloatingBanner(Map<String, Map<String, String>> map) {
        try {
            String space = Definitions.WALLA_SPACE_FLOATING_BOTTOM;
            if (map == null || !map.containsKey(space)) {
                return;
            }

            Map<String, String> m = map.get(space);

            if (m == null) {
                frameAdViewContainer_Pull_to_refresh_bottom.setVisibility(View.GONE);
                return;
            }

            frameAdViewContainer_Pull_to_refresh_bottom.setVisibility(View.VISIBLE);

            //Log.e("alex", "GGGGG showBottomFloatingBanner: " + sContentType + "===" + sAdUrl);

            WallaAdvParser.drawWallaBanner(frameAdViewContainer_Pull_to_refresh_bottom, getApplicationContext(), m);
        } catch (Exception ex) {
            Log.e("alex", "Error Walla Bottom Floating Banner");
        }
    }

    private void showBottomBannerAfterResponses(Map<String, Map<String, String>> map) {
        try {
            String space = Definitions.WALLA_SPACE_ARTICLE_AFTER_RESPONSES; //"item_bottom";

            if (map == null || !map.containsKey(space)) {
                return;
            }

            Map<String, String> m = map.get(space);

            if (m == null) {
                adView_frame_bottom_banner.setVisibility(View.GONE);
                return;
            }

            adView_frame_bottom_banner.setVisibility(View.VISIBLE);

            WallaAdvParser.drawWallaBanner(adView_frame_bottom_banner, getApplicationContext(), m);
        } catch (Exception ex) {
            Log.e("alex", "Error Walla Bottom Floating Banner");
        }
    }

//	private void showLiveBox(Map<String, Map<String, String>> map)
//	{
    //String Payload = "<!doctype html><html><head><meta content=\"width=device-width,minimum-scale=1.0,maximum-scale=1.0,user-scalable=no\" name=\"viewport\"><style type=\"text/css\">html,body{    height:100%;}body{margin:0;}.center{display:table; overflow:hidden; width:100%; height:100%; text-align:center; vertical-align:middle;}.center>.wrapper{display:table-cell; vertical-align:middle;}.wrapper:before{}.wrapper:before,.wrapper:after{content:\"\"; display:block; height:50%;}</style></head>  <body> <div id=\"centerdiv\" class=\"center\">   <div class=\"wrapper\"><script type=\"text/javascript\">var fusionVars = {'ADID': '3830026636','ADTYPE': 'Interstitial','CONTENTTYPE':'Html','ADURL_01':'','WIDTH_01': '320','HEIGHT_01': '416','ORIENTATION':'Portrait','ALIGNMENT':'Center','CLICKURL':'','CLICKREPORT': 'event/xyvnd/mint.globes_app.capitalmarket/3830026636/click','ADTRANSITION': 'None','ADSERVER': 'http://fus.walla.co.il:80/','ADSRV': 'http://fus.walla.co.il/','VIEWREPORT': 'impression/xyvnd/mint.globes_app.capitalmarket/l_globes_app_c/3830026637','CLOSEBTNSHOW':'True','CLOSEBTNALIGNMENT':'TopLeft','CLOSEBTNDELAY':'0','CLOSEBTNURL':'','BGCOLOR':'000000','DELAYTIME':'6','EXTERNALBROWSER':'False','FULLSCREEN':'False'}if (fusionVars['CONTENTTYPE'] == \"Mraid\"){  document.getElementById(\"centerdiv\").style.height = \"0px\";}</script><!--advertiser tag goes here--><IFRAME src=\"http://ad.doubleclick.net/adi/N9940.109338WALLA.CO.IL/B8193802.110694681;sz=320x460;ord=[timestamp]?\" WIDTH=320 HEIGHT=460 MARGINWIDTH=0 MARGINHEIGHT=0 HSPACE=0 VSPACE=0 FRAMEBORDER=0 SCROLLING=no BORDERCOLOR='#cc0000'><SCRIPT language='JavaScript1.1' SRC=\"http://ad.doubleclick.net/adj/N9940.109338WALLA.CO.IL/B8193802.110694681;abr=!ie;sz=320x460;ord=[timestamp]?\"></SCRIPT><NOSCRIPT><A HREF=\"http://ad.doubleclick.net/jump/N9940.109338WALLA.CO.IL/B8193802.110694681;abr=!ie4;abr=!ie5;sz=320x460;ord=[timestamp]?\"><IMG SRC=\"http://ad.doubleclick.net/ad/N9940.109338WALLA.CO.IL/B8193802.110694681;abr=!ie4;abr=!ie5;sz=320x460;ord=[timestamp]?\" BORDER=0 WIDTH=320 HEIGHT=460 ALT=\"Advertisement\"></A></NOSCRIPT></IFRAME><!--ends here--></div></div></body></html>";

//		String Payload = "<!doctype html> <html> <head> <meta content=\"width=device-width,minimum-scale=1.0,maximum-scale=1.0,user-scalable=no\" name=\"viewport\"> </head> <body style=\"text-align: center;font-family:Arial,Helvetica,sans-serif;font-size:10pt;margin:0\"> <script type=\"text/javascript\"> var fusionVars = { 'ADID': '3830059881', 'ADTYPE': 'Inline', 'CONTENTTYPE':Html, 'ADURL_01':'', 'WIDTH_01': '320', 'HEIGHT_01': '250', 'CLICKURL':'', 'CLICKREPORT': 'event/rucmm/mint.globes_app_android.article/3830059881/click', 'ADSERVER': 'http://fus.walla.co.il:80/', 'ADSRV': 'http://fus.walla.co.il/', 'VIEWREPORT': 'impression/rucmm/mint.globes_app_android.article/l_globes_app_a/3830059882', 'BGCOLOR':000000, 'CONTENTID':, 'DELAYTIME':'100', 'EXTERNALBROWSER':'True' } </script> <!--advertiser tag goes here--> <center> <div id='aniplayer'></div> <script type=\"text/javascript\" id=\"aniviewJS\"> var adConfig = { publisherID : '41751', channelID : '775986', GetVASTRetry :5, pause : false, loop : true, logo : false, loopVideo : true, autoClose : false, contentID : '', closeButton : false, showCloseButon :5, skipText : '', pauseButton : true, soundButton : true, fullScreenButton : false, passBackUrl : '', isSmart : false, playerType : 2, clickTracking : 'https://www.tapuchips.co.il/?utm_source=walla_mobile&utm_medium=cpm&utm_campaign=king', subExternalID :'', ref1 :'', cdnUrl :'http://d41751.ani-view.com/', backgroundImageUrl:'', backgroundColor:'#FFF', position :'aniplayer' }; var PlayerUrl = 'http://eu.ani-view.com/script/1/aniview.js'; function downloadScript(src,adData) { var scp = document.createElement('script'); scp.src = src; scp.onload = function() { var myPlayer= new aniviewPlayer; myPlayer.onClick = function() {(new Image).src ='event/rucmm/mint.globes_app_android.article/3830059881/click';}; myPlayer.onPlay= function () { }; myPlayer.play(adConfig); }; document.getElementsByTagName('head')[0].appendChild(scp); } downloadScript(PlayerUrl,adConfig); </script> </center> <!--ends here--> </body> </html>";
//
//		webView_live_box.setVisibility(View.VISIBLE);
//		Log.e("alex", "showLiveBox");
//
//		WallaAdvParser.loadDataIntoWebView(webView_live_box, getApplicationContext(), Payload, "", "http://fus.walla.co.il/impression/rucmm/mint.globes_app_android.article/l_globes_app_a/3830059882", "http://fus.walla.co.il/impression/rucmm/mint.globes_app_android.article/l_globes_app_a/3830059882");

    //}

}