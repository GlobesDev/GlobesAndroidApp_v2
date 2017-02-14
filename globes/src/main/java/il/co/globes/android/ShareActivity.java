package il.co.globes.android;

//import il.co.globes.android.LazyAdapter.ViewHolder;
//import il.co.globes.android.R.layout;
//import il.co.globes.android.objects.Article;
import il.co.globes.android.fragments.PortFolioFragment;
import il.co.globes.android.fragments.SectionListFragment;
import il.co.globes.android.interfaces.GlobesListener;
import il.co.globes.android.objects.AjillionObj;
import il.co.globes.android.objects.Article;
import il.co.globes.android.objects.CompanyDuns100;
import il.co.globes.android.objects.CustomMenuItem;
import il.co.globes.android.objects.DataContext;
import il.co.globes.android.objects.GlobesURL;
import il.co.globes.android.objects.Instrument;
import il.co.globes.android.objects.NewCustomMenuItem;
import il.co.globes.android.objects.NewsSet;
import il.co.globes.android.objects.Ticker;
import il.co.globes.android.objects.ShareObjects.ShareData;
import il.co.globes.android.objects.ShareObjects.ShareNewsArticle;
import il.co.globes.android.swipeListView.SwipeListView;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.xmlpull.v1.XmlPullParser;

import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebSettings.PluginState;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageView.ScaleType;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.doubleclick.PublisherAdView;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.squareup.picasso.Picasso;

public class ShareActivity extends MainActivity implements 
GlobesListener, uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener
{

	private LayoutInflater inflater;

	private String globesAccessKey = "";
	private static final String PREFS_NAME = "preferences";

	TextView textViewDailyHigh;
	LinearLayout layout;
	protected static final String TAG = "ShareActivity";
	static final int NEED_TO_NOTIFY_ADAPTER = 8;
	boolean isInstrument;
	ImageView imageViewBigGraph, imageViewSearch, imageViewBack;
	TextView textViewShareNameInHeader;
	String shareName, feederId, shareId;
	String linkImageBigGraph, tempLinkImageBigGraph;
	Configuration config;
	Bitmap bitmap;
	final Activity shareActivity = this;
	boolean isFinishedParsing = false;
	String portfolioAction = "";
	boolean isPortfolioItem = false;
	Resources myResources;
	private View shareData;

	Bitmap stockGraph;
	// TODO add tracking google analytics tracker
	// GoogleAnalyticsTracker tracker;

	/** alert dialog for looper Add/Remove */
	private AlertDialog alertDialog;
	private final static int DIALOG_TYPE_ADD_ITEM = 23;
	private final static int DIALOG_TYPE_REMOVE_ITEM = 24;

	// private String pfActionResult = "";

	int imageHeight;
	int imageWidth;

	// dfp optional dfp AdView
	// DfpAdView adViewOptional;
	PublisherAdView adViewOptional;

	private Ticker looper;
	private PullToRefreshLayout mPullToRefreshLayout;
	private boolean isTickerChecked;
	private GlobesListener mCallback;

	protected Handler handler = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			if (msg.what == DONE_PARSING)
			{
				if (getResources().getConfiguration().orientation == android.content.res.Configuration.ORIENTATION_PORTRAIT)
				{

					setPullToRefreshList();
				}
				else
				{
					setLandscapeModeView();
				}
				try
				{
					if (pd != null && pd.isShowing())
					{
						pd.dismiss();
					}
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
				}
				Toast toast = Toast.makeText(context, Definitions.ErrorLoading, Toast.LENGTH_LONG);
				toast.show();
				// mainActivity.finish();
			}
			else
			{
				if (getResources().getConfiguration().orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE)
				{
					findViewById(R.id.progressBarBigGraph).setVisibility(android.view.View.GONE);
					bitmap = Utils.DownloadImage(tempLinkImageBigGraph);
					imageViewBigGraph.setImageBitmap(bitmap);
				}
			}
		}
	};

	// indicate we come from markets list
	public final static String KEY_FROM_MARKETS_OR_STOCKS_LIST = "from markets stocks";
	private boolean isFromMarketsSharesListActivity;

	private String feeder = "";

	private WebView webView_iframe_ad;

	//	private String sakindoURL = "<div style=\"text-align:center;\"><iframe scrolling=no frameborder=0 width=300 height=250 marginheight=0  marginwidth=0 src=http://live.sekindo.com/live/liveView.php?s=52471&njs=1&subId=DEFAULT></iframe></div>";
	private String sakindoURL = "<div style=\"text-align:center;\"><script src=\"http://live.sekindo.com/live/liveView.php?s=52471&subId=DEFAULT&nofr=1\"></script></div>";

	private View row_footer_sakindo_for_share;

	private RelativeLayout frameAdViewContainer_Pull_to_refresh;
	private RelativeLayout ad_ajillion_below_tikishi;

	@Override
	public void onCreate(Bundle icicle)
	{
		super.onCreate(icicle);
		
		//Log.e("alex", "ShareActivity.java");
		
		// TODO add tracking
		// tracker = GoogleAnalyticsTracker.getInstance();
		looper = Ticker.getInstance(getApplicationContext());
		intent = getIntent();
		// onConfigurationChanged(getResources().getConfiguration()); /*start
		// activity at the desired orientation -rotemm*/
		//		showInstrument.putExtra("feederId",  getFeederNew(instrument.getId()));
		//		showInstrument.putExtra("shareId", instrument.getId());
		myResources = getResources();
		shareId = intent.getStringExtra("shareId");
		shareName = intent.getStringExtra("name");
		feeder = intent.getStringExtra("feederId");
		if (feeder==null || feeder.compareTo("") == 0)
		{
			feeder = intent.getStringExtra("feeder");
		}
		// intent.putExtra("feeder",  item.getFeeder());

		// textViewShareNameInHeader = (TextView)
		// findViewById(R.id.textViewShareNameInHeader);
		// textViewShareNameInHeader.setText(shareName);

		// check if we are from marketslist activity
		Intent intentisFromMarketsSharesListActivity = getIntent();
		if (intentisFromMarketsSharesListActivity != null)
		{
			isFromMarketsSharesListActivity = intent.getBooleanExtra(KEY_FROM_MARKETS_OR_STOCKS_LIST, false);
		}

	}

	@Override
	protected void onResume()
	{
		super.onResume();
		if (looper == null)
		{
			looper = Ticker.getInstance(this);
		}
	}

	@Override
	public void onBackPressed()
	{
		/*
		 * if we are from marketsListActivity we set the result for
		 * MarketsListActivity onActivityResult
		 */
		if (isFromMarketsSharesListActivity)
		{
			setResult(RESULT_OK);
			ShareActivity.this.finish();
		}
		else
		{
			super.onBackPressed();
		}
	}

	@Override
	public void onDestroy()
	{
		if (adapter != null && list != null)
		{
			adapter.imageLoader.stopThread();
			// list.setAdapter(null);
		}
		super.onDestroy();

	}

	@Override
	void setContentView()
	{
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.share_activity_pull_to_refresh);
		frameAdViewContainer_Pull_to_refresh = (RelativeLayout)findViewById(R.id.frameAdViewContainer_Pull_to_refresh);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.a_test_new_share_title);
		textViewShareNameInHeader = (TextView) findViewById(R.id.textViewShareNameInHeader);
		imageViewBack = (ImageView) findViewById(R.id.btn_back);
		imageViewBack.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if (isFromMarketsSharesListActivity)
				{
					setResult(RESULT_OK);
				}
				ShareActivity.this.finish();

			}
		});
		textViewShareNameInHeader.setText(shareName);
		// the optional Adview
		// eli dfp
		if (Definitions.toUseAjillion)
		{
			new  initAjillionView(frameAdViewContainer_Pull_to_refresh , Definitions.AJILLION_AD_ID_HEAD_BANNER).execute();
		}
		else
		{
			setAdView();
		}
	}

	private void setAdView()
	{
//		adViewOptional = (PublisherAdView) findViewById(R.id.adView);
//		//		adViewOptional.setVisibility(View.GONE);
//		adViewOptional.setAdListener(new AdListener()
//		{
//			public void onAdLoaded()
//			{
//				Log.d(TAG, "adViewOptional received on ShareActivity: ");
//				adViewOptional.setVisibility(View.VISIBLE);
//				frameAdViewContainer_Pull_to_refresh.setVisibility(View.VISIBLE);
//
//			}
//			public void onAdFailedToLoad(int errorCode)
//			{
//				Log.e(TAG, "adViewOptional failed on ShareActivity error is: " + errorCode);
//				adViewOptional.setVisibility(View.GONE);
//				if (tracker != null)
//				{
//					tracker.trackPageView("/" + TAG + Definitions.AD_FAILURE_TAG_OPTIONAL_PORTAL_FINANCY + errorCode);
//				}
//			}
//		});
//		PublisherAdRequest requestdfpAdViewOptional = new PublisherAdRequest.Builder().build();
//		adViewOptional.loadAd(requestdfpAdViewOptional);
	}

	public void onclickFrontWihtoutTAGIT(int position)
	{
		int newPosition = position ;
		//		Toast.makeText(context, " " + parsedNewsSet.itemHolder.get(newPosition).getClass().getSimpleName(), Toast.LENGTH_LONG).show();

		if (parsedNewsSet.itemHolder.get(newPosition).getClass() == ShareNewsArticle.class)
		{
			String articleId = ((ShareNewsArticle) parsedNewsSet.itemHolder.get(newPosition)).getDocId();
			Intent intent = new Intent(this, DocumentActivity_new.class);
			intent.putExtra(Definitions.CALLER, Definitions.SHARES);
			intent.putExtra("articleId", articleId);
			startActivity(intent);
		}
		else if (parsedNewsSet.itemHolder.get(newPosition).getClass() == Instrument.class)
		{
			if (((Instrument) parsedNewsSet.itemHolder.get(position)).isIndexWithInstruments())
			{
				String uri = GlobesURL.URLMarkets + GlobesURL.URLMarketsIndex;
				uri = uri.replaceAll("XXXX", ((Instrument) parsedNewsSet.itemHolder.get(newPosition)).getFeeder()).replaceAll("YYYY",((Instrument) parsedNewsSet.itemHolder.get(newPosition)).getId());
				Intent intent = new Intent(this, MarketsListActivity.class);
				intent.putExtra("URI", uri);
				intent.putExtra(Definitions.CALLER, Definitions.MAINSCREEN);
				intent.putExtra(Definitions.PARSER, Definitions.MARKETS);
				startActivity(intent);
			}
		}
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id)
	{

		// int newPosition = position - 1;
		//
		// if (parsedNewsSet.itemHolder.get(newPosition).getClass() ==
		// ShareNewsArticle.class)
		// {
		// String articleId = ((ShareNewsArticle)
		// parsedNewsSet.itemHolder.get(newPosition)).getDocId();
		// Intent intent = new Intent(this, DocumentActivity_new.class);
		// intent.putExtra(Definitions.CALLER, Definitions.SHARES);
		// intent.putExtra("articleId", articleId);
		// startActivity(intent);
		// }
		// else if (parsedNewsSet.itemHolder.get(newPosition).getClass() ==
		// Instrument.class)
		// {
		// if
		//
		// (((Instrument)
		// parsedNewsSet.itemHolder.get(position)).isIndexWithInstruments())
		// {
		// String uri = GlobesURL.URLMarkets + GlobesURL.URLMarketsIndex;
		// uri = uri.replaceAll("XXXX", ((Instrument)
		// parsedNewsSet.itemHolder.get(newPosition)).getFeeder()).replaceAll("YYYY",
		// ((Instrument) parsedNewsSet.itemHolder.get(newPosition)).getId());
		// Intent intent = new Intent(this, MarketsListActivity.class);
		// intent.putExtra("URI", uri);
		// intent.putExtra(Definitions.CALLER, Definitions.MAINSCREEN);
		// intent.putExtra(Definitions.PARSER, Definitions.MARKETS);
		// startActivity(intent);
		// }
		// }
		// super.onListItemClick(l, v, position, id);
	}

	void setRefreshData(final String uriToParse, final String parser)
	{
		pd = ProgressDialog.show(this, "", Definitions.Loading, true, false);
		isInstrument = intent.getBooleanExtra(Definitions.ISINSTRUMENT, false);

		if (isInstrument)
		{
			feederId = intent.getStringExtra("feederId");
			shareId = intent.getStringExtra("shareId");
		}

		new AsyncTask<Void, Void, Integer>()
		{

			@Override
			protected void onPreExecute()
			{
				list = (SwipeListView) findViewById(android.R.id.list);

			};

			@Override
			protected Integer doInBackground(Void... params)
			{
				int res;
				try
				{
					URL url = new URL(uriToParse);
					parsedNewsSet = NewsSet.parseURL(url, parser);

					addInstrumentComposition();

					res = DONE_PARSING;

				}
				catch (Exception e)
				{
					e.printStackTrace();
					res = ERROR_PARSING;
				}
				return res;
			}

			@Override
			protected void onPostExecute(Integer result)
			{
				try
				{
					pd.dismiss();
				}
				catch (Exception e)
				{
				}
				if (result == DONE_PARSING)
				{
					shareData.invalidate();
					adapter = new LazyAdapter(activity, parsedNewsSet.itemHolder);
					adapter.setShareActivity(ShareActivity.this);

					list.setAdapter(adapter);
					//setContentView();

				}
				else if (result == ERROR_PARSING)
				{
					Toast toast = Toast.makeText(ShareActivity.this, Definitions.ErrorLoading, Toast.LENGTH_LONG);
					toast.show();
				}
			}

		}.execute();
	}

	void setRefreshDataFromPortrait(final String uriToParse, final String parser)
	{
		pd = ProgressDialog.show(this, "", Definitions.Loading, true, false);
		isInstrument = intent.getBooleanExtra(Definitions.ISINSTRUMENT, false);

		if (isInstrument)
		{
			feederId = intent.getStringExtra("feederId");
			shareId = intent.getStringExtra("shareId");
		}

		new AsyncTask<Void, Void, Integer>()
		{

			@Override
			protected void onPreExecute()
			{
				// list = (SwipeListView) v.findViewById(android.R.id.list);

			};

			@Override
			protected Integer doInBackground(Void... params)
			{
				int res;
				try
				{
					URL url = new URL(uriToParse);
					parsedNewsSet = NewsSet.parseURL(url, parser);

					addInstrumentComposition();

					res = DONE_PARSING;

				}
				catch (Exception e)
				{
					e.printStackTrace();
					res = ERROR_PARSING;
				}
				return res;
			}

			@Override
			protected void onPostExecute(Integer result)
			{

				try
				{
					pd.dismiss();
				}
				catch (Exception e)
				{
				}
				if (result == DONE_PARSING)
				{

					setPullToRefreshListFromPortrait();

				}
				else if (result == ERROR_PARSING)
				{
					Toast toast = Toast.makeText(ShareActivity.this, Definitions.ErrorLoading, Toast.LENGTH_LONG);
					toast.show();
				}
			}

		}.execute();
	}

	@Override
	void setInitialData(final String uriToParse, final String parser)
	{
		pd = ProgressDialog.show(this, "", Definitions.Loading, true, false);
		isInstrument = intent.getBooleanExtra(Definitions.ISINSTRUMENT, false);

		if (isInstrument)
		{
			feederId = intent.getStringExtra("feederId");
			shareId = intent.getStringExtra("shareId");
		}
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					URL url = new URL(uriToParse);
					parsedNewsSet = NewsSet.parseURL(url, parser);

					addInstrumentComposition();

					isFinishedParsing = true;
					handler.sendEmptyMessage(0);
				}
				catch (Exception e)
				{
					handler.sendEmptyMessage(1);
				}
			}
		}).start();
	}

	void setPullToRefreshList()
	{
		// setContentView(R.layout.share_activity_pull_to_refresh);
		list = (SwipeListView) findViewById(android.R.id.list);
		adapter = new LazyAdapter(activity, parsedNewsSet.itemHolder);
		adapter.setShareActivity(ShareActivity.this);
		ViewGroup viewGroup = (ViewGroup) getWindow().getDecorView().getRootView();
		try
		{
			mPullToRefreshLayout = new PullToRefreshLayout(this);
			View vTest = findViewById(android.R.id.content);
			ActionBarPullToRefresh.from(this).insertLayoutInto(viewGroup)
			.theseChildrenArePullable(getListView(), getListView().getEmptyView(), vTest).listener(this)
			.setup(mPullToRefreshLayout);
		}
		catch (Exception e)
		{
		}
		setStockDetailsPanel();
		list.setAdapter(adapter);
	}

	void setPullToRefreshListFromPortrait()
	{
		setContentView(R.layout.share_activity_pull_to_refresh);
		frameAdViewContainer_Pull_to_refresh = (RelativeLayout) findViewById(R.id.frameAdViewContainer_Pull_to_refresh);
		list = (SwipeListView) findViewById(android.R.id.list);
		adapter = new LazyAdapter(activity, parsedNewsSet.itemHolder);
		adapter.setShareActivity(ShareActivity.this);
		ViewGroup viewGroup = (ViewGroup) getWindow().getDecorView().getRootView();
		View vTest = findViewById(android.R.id.content);
		try
		{
			mPullToRefreshLayout = new PullToRefreshLayout(this);
			ActionBarPullToRefresh.from(this).insertLayoutInto(viewGroup)
			.theseChildrenArePullable(getListView(), getListView().getEmptyView(), vTest).listener(this)
			.setup(mPullToRefreshLayout);
		}
		catch (Exception e)
		{
		}
		setStockDetailsPanel();
		list.setAdapter(adapter);

		// the optional Adview
		// eli dfp
		if (Definitions.toUseAjillion)
		{
			new  initAjillionView(frameAdViewContainer_Pull_to_refresh , Definitions.AJILLION_AD_ID_HEAD_BANNER).execute();
		}
		else
		{
			setAdView();
		}
	}

	public void updateShareActivityContent()
	{
		// View v = findViewById(android.R.id.content);
		// textViewDailyHigh.setText("fdgsgsfg");
		// v.invalidate();
		// list = (SwipeListView) findViewById(android.R.id.list);
		// adapter = new LazyAdapter(activity, parsedNewsSet.itemHolder);
		// adapter.setShareActivity(ShareActivity.this);
		//
		// list.setAdapter(adapter);
		shareData.invalidate();
		Log.e("eli", "605");
		adapter = new LazyAdapter(activity, parsedNewsSet.itemHolder);
		adapter.setShareActivity(ShareActivity.this);

		list.setAdapter(adapter);
	}

	public void setStockDetailsPanel()
	{

		// View v = findViewById(android.R.id.content);
		// View v2 = ((ViewGroup) v).getChildAt(0);
		// LinearLayout rootLayout = (LinearLayout) v2;

		// LinearLayout rootLayout = (LinearLayout)
		// ((ViewGroup)findViewById(android.R.id.content)).getChildAt(0);

		// if (rootLayout != null)
		// {
		ShareData tempShareData = ((ShareData) parsedNewsSet.itemHolder.get(0));
		final ShareData finalShareData = tempShareData;
		if (shareName == null || shareName.length() < 1)
		{
			shareName = tempShareData.getShareNameHe();
			textViewShareNameInHeader.setText(shareName);

		}
		else
		{
			textViewShareNameInHeader.setText(shareName);

		}

		// View shareData =
		// getLayoutInflater().inflate(R.layout.row_layout_share_data,
		// rootLayout, false);
		shareData = getLayoutInflater().inflate(R.layout.a_test_new_row_layout, null);
		ad_ajillion_below_tikishi = (RelativeLayout)shareData.findViewById(R.id.ad_ajillion_below_tikishi);



		list.addHeaderView(shareData);
		row_footer_sakindo_for_share = getLayoutInflater().inflate(R.layout.row_footer_sakindo_for_share, null);

		webView_iframe_ad = (WebView)row_footer_sakindo_for_share.findViewById(R.id.webView_iframe_ad);
		webView_iframe_ad.setOnTouchListener(new View.OnTouchListener() 
		{
			@SuppressLint("ClickableViewAccessibility")
			public boolean onTouch(View v, MotionEvent event) 
			{
				return (event.getAction() == MotionEvent.ACTION_MOVE);
			}
		});
		webView_iframe_ad.setVerticalScrollBarEnabled(false);
		webView_iframe_ad.setHorizontalScrollBarEnabled(false);
		webView_iframe_ad.setWebChromeClient(new WebChromeClient() );
		//		webView_iframe_ad.setInitialScale(getScale());
		webView_iframe_ad.setPadding(0, 0, 0, 0);
		WebSettings webViewSettings = webView_iframe_ad.getSettings();
		webViewSettings.setJavaScriptCanOpenWindowsAutomatically(true);
		webViewSettings.setJavaScriptEnabled(true);
		//webViewSettings.setPluginsEnabled(true);
		webViewSettings.setUseWideViewPort(false);
		webViewSettings.setBuiltInZoomControls(false);
		webViewSettings.setLoadWithOverviewMode(false);
		webViewSettings.setSupportZoom(false);
		//		webViewSettings.setSupportZoom(false);
		webViewSettings.setPluginState(PluginState.ON);
		webView_iframe_ad.loadData(sakindoURL , "text/html; charset=utf-8", "UTF-8");
		list.addFooterView(row_footer_sakindo_for_share);
		//		("http://live.sekindo.com/live/liveView.php?s=52471&njs=1&subId=DEFAULT");
		// rootLayout.addView(shareData, 0);

		// checkBoxAddToLooperFromSharePage
		// final CheckBox checkBoxAddToLooperFromSharePage = (CheckBox)
		// findViewById(R.id.checkBoxAddToLooperFromSharePage);
		// checkBoxAddToLooperFromSharePage.setOnCheckedChangeListener(new
		// OnCheckedChangeListener()
		// {
		//
		// @Override
		// public void onCheckedChanged(CompoundButton buttonView, boolean
		// isChecked)
		// {
		// if (isChecked)
		// {
		//
		// if (!looper.isInLooper(finalShareData))
		// {
		// looper.addLooperItem(finalShareData);
		// showDialog(DIALOG_TYPE_ADD_ITEM,
		// finalShareData.getShareNameHe());
		// }
		// }
		// else
		// {
		// if (looper.isInLooper(finalShareData))
		// {
		//
		// // minimun items in looper is 3
		// if (looper.getLooperItemsCount() == Ticker.MIN_LOOPER_ITEMS)
		// {
		// showDialogCannotRemoveItem();
		// checkBoxAddToLooperFromSharePage.setChecked(true);
		//
		// }
		// else
		// {
		// looper.removeLooperItem(finalShareData);
		// showDialog(DIALOG_TYPE_REMOVE_ITEM,
		// finalShareData.getShareNameHe());
		// }
		// }
		// }
		// }
		// });
		//
		// if (looper.isInLooper(finalShareData))
		// {
		// checkBoxAddToLooperFromSharePage.setChecked(true);
		// }
		// else
		// {
		// checkBoxAddToLooperFromSharePage.setChecked(false);
		// }

		// //////////////////////////////////////// --------<< NEW LAYOUT
		// TEST >>-----------------///////////////////////////////// tempShareData.getDailyPercentageChange()
		TextView textViewPrecentageChangeInHeader = (TextView) findViewById(R.id.textViewPrecentageChangeInHeader);
		textViewPrecentageChangeInHeader.setText(tempShareData.getDailyPercentageChange());

		TextView textViewLastPrice = (TextView) findViewById(R.id.textViewLastPrice);
		textViewLastPrice.setText(tempShareData.getLastPrice());

		TextView textViewNameOfShare = (TextView) findViewById(R.id.textViewNameOfShare);
		textViewNameOfShare.setText(shareName);

		if (tempShareData.getDailyPercentageChange().length() <= 2)
		{
			textViewNameOfShare.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.transparent, 0);

			// imageViewUpDownArrow.setImageResource(R.drawable.transparent);
		}
		else if (tempShareData.getDailyPercentageChange().contains("-"))
		{
			textViewNameOfShare.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.fp_icon_down_off, 0);
			// imageViewUpDownArrow.setImageResource(R.drawable.stockdown);
			textViewPrecentageChangeInHeader.setTextColor(0xffce0f0f); /*
			 * Set
			 * color
			 * to
			 * Custom
			 * Red
			 */
		}
		else
		{
			textViewNameOfShare.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.fp_iconup_off, 0);

			// imageViewUpDownArrow.setImageResource(R.drawable.stockup);
			textViewPrecentageChangeInHeader.setTextColor(0xff1aad20); /*
			 * Set
			 * color
			 * to
			 * Custom
			 * Green
			 */
		}

		// graph
		TextView textViewShareNumber = (TextView) findViewById(R.id.textViewShareNumber);

		textViewShareNumber.setText(" מס  נ\"ע "+ tempShareData.getShareSymbol());
		TextView textViewTrueForDate = (TextView) findViewById(R.id.textViewTrueForDate);
		textViewTrueForDate.setText( tempShareData.getTimestamp().replaceAll("null", "").trim());

		// bottom values left
		TextView textView_shivoui_yomi_value = (TextView) findViewById(R.id.textView_shivoui_yomi_value);
		if (!TextUtils.isEmpty(tempShareData.getShareMarketCap().replaceAll("null", "").trim()))
		{
			textView_shivoui_yomi_value.setText(tempShareData.getShareMarketCap().replaceAll("null", "").trim());
		}

		TextView textView_tmoura_value = (TextView) findViewById(R.id.textView_tmoura_value);
		if (!TextUtils.isEmpty(tempShareData.getShareVolume().replaceAll("null", "").trim()))
		{
			textView_tmoura_value.setText(tempShareData.getShareVolume().replaceAll("null", "").trim());
		}
		TextView textView_shvouit_value = (TextView) findViewById(R.id.textView_shvouit_value);

		if (!TextUtils.isEmpty(tempShareData.getLastWeekClosePrice().replaceAll("null", "").trim()))
		{
			float value = Float.parseFloat(tempShareData.getLastWeekClosePrice().replaceAll("null", "").trim());
			value = (value - 1) * 100;
			textView_shvouit_value.setText(value + "");
		}

		TextView textView_hodshit_value = (TextView) findViewById(R.id.textView_hodshit_value);

		if (!TextUtils.isEmpty(tempShareData.getChangeFromLastMonth().replaceAll("null", "").trim()))
		{
			textView_hodshit_value.setText(tempShareData.getChangeFromLastMonth().replaceAll("null", "").trim());
		}

		TextView textView_12hodashim_value = (TextView) findViewById(R.id.textView_12hodashim_value);

		if (!TextUtils.isEmpty(tempShareData.getChangeFromLastYear().replaceAll("null", "").trim()))
		{
			textView_12hodashim_value.setText(tempShareData.getChangeFromLastYear().replaceAll("null", "").trim());
		}

		// bottom values right
		TextView textView_shar_yatsig_value = (TextView) findViewById(R.id.textView_shar_yatsig_value);
		textView_shar_yatsig_value.setText(tempShareData.getDailyPercentageChange());

		TextView textView_shinoui_bank_value = (TextView) findViewById(R.id.textView_shinoui_bank_value);

		if (!TextUtils.isEmpty(tempShareData.getDailyPointsChange().replaceAll("null", "").trim()))
		{
			textView_shinoui_bank_value.setText(tempShareData.getDailyPointsChange().replaceAll("null", "").trim());
		}
		TextView textView_shaar_petiha_value = (TextView) findViewById(R.id.textView_shaar_petiha_value);

		if (!TextUtils.isEmpty(tempShareData.getOpenPrice().replaceAll("null", "").trim()))
		{
			textView_shaar_petiha_value.setText(tempShareData.getOpenPrice().replaceAll("null", "").trim());
		}

		TextView textView_nefar_mishar_value = (TextView) findViewById(R.id.textView_nefar_mishar_value);

		if (!TextUtils.isEmpty(tempShareData.gettotVol().replaceAll("null", "").trim()))
		{
			textView_nefar_mishar_value.setText(tempShareData.gettotVol().replaceAll("null", "").trim());
		}

		TextView textView_yomi_gavoa_value = (TextView) findViewById(R.id.textView_yomi_gavoa_value);

		if (!TextUtils.isEmpty(tempShareData.getDailyHigh().replaceAll("null", "").trim()))
		{
			textView_yomi_gavoa_value.setText(tempShareData.getDailyHigh().replaceAll("null", "").trim());
		}

		TextView textView_yomi_namour_value = (TextView) findViewById(R.id.textView_yomi_namour_value);

		if (!TextUtils.isEmpty(tempShareData.getDailyLow().replaceAll("null", "").trim()))
		{
			textView_yomi_namour_value.setText(tempShareData.getDailyLow().replaceAll("null", "").trim());
		}
		ImageView imageViewGraph = (ImageView) findViewById(R.id.imageViewGraph);

		// TODO TEO URL GRAPH FOREX/REGULAR
		// if ((tempShareData).getShareType().equals("currency"))
		// {
		// imageToGet = GlobesURL.URLForexGraph;
		// }
		// else
		// {
		// imageToGet = GlobesURL.URLShareGraph;
		// }

		//		String imageToGet = "http://graph.globes.co.il/chartdirector/finance/orchart.aspx?graph=d&width=500&height=400&style=fp&chartcolor=edf4f9&days=1";
		//tempShareData.getShareSymbol()
		//		Log.e("eli", "ShareType   " + tempShareData.getShareType());
		String imageToGet = "http://graph.globes.co.il/chartdirector/finance/orchart.aspx?graph=d&width=500&height=200&symbol="
				+ tempShareData.getShareSymbol() + "&style=fp&chartcolor=edf4f9&days=1";
		if (tempShareData.getShareType().compareTo("currency")==0)
		{
			imageToGet = "http://graph.globes.co.il/graphs3.5/cchart.aspx?width=500&height=200&symbol="
					+tempShareData.getShareSymbol()+"&style=fp&chartcolor=edf4f9&days=1";
		}
		//		Log.e("eli","img to graph=    "+ imageToGet.replaceAll("null", "").trim());
		Picasso.with(getApplicationContext()).load(imageToGet.replaceAll("null", "").trim()).fit().into(imageViewGraph);

		setNewBtnManagePfItem();
		TextView textView_tikishi = (TextView) findViewById(R.id.textView_tikishi);
		textView_tikishi.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{

				getGlobesAccessKey();
				getGlobesAccessKey();
				if (globesAccessKey.length() < 2)
				{
					requestNewLogin();

				}
				else
				{

					// create the fragment
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
					ShareActivity.this.finish();
				}

				//
				//
				//
				//
				// Intent intent = new Intent(ShareActivity.this,
				// PortfolioActivity.class);
				// intent.putExtra("URI", GlobesURL.URLMainScreen);
				// intent.putExtra(Definitions.CALLER,
				// Definitions.MAINSCREENCREEN);
				// intent.putExtra(Definitions.PARSER, Definitions.PORTFOLIO);
				// startActivity(intent);
			}
		});
		//		RelativeLayout layout_share_to_FB = (RelativeLayout) findViewById(R.id.layout_share_to_FB);
		//		layout_share_to_FB.setOnClickListener(new OnClickListener()
		//		{
		//
		//			@Override
		//			public void onClick(View v)
		//			{
		//				// TODO SHARE ON FACEBOOK
		//
		//			}
		//		});
		// TODO eli - facebook
		RelativeLayout layout_face_book_post_with_intent = (RelativeLayout) findViewById(R.id.layout_face_book_post_with_intent);
		layout_face_book_post_with_intent.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				ShareData tempShareData = ((ShareData) parsedNewsSet.itemHolder.get(0));
				Intent share = new Intent();
				share.setAction(Intent.ACTION_SEND);
				String mimeType = "text/plain";
				share.setType(mimeType);
				share.putExtra(Intent.EXTRA_TEXT,  "http://www.globes.co.il/portal/instrument.aspx?instrumentid="+tempShareData.getInsturmentID()+"#from=android.app");
				share.putExtra(Intent.EXTRA_TITLE, "גלובס");
				startActivity(Intent.createChooser(share, Definitions.Share));
			}
		});
		RelativeLayout layout_add_or_remove_from_ticker = (RelativeLayout) findViewById(R.id.layout_add_or_remove_from_ticker);
		layout_add_or_remove_from_ticker.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// TODO ADD/REMOVE FROM TICKER SEE CHECKBOX
				if (isTickerChecked)
				{
					if (!looper.isInLooper(finalShareData))
					{
						looper.addLooperItem(finalShareData);
						showDialog(DIALOG_TYPE_ADD_ITEM, finalShareData.getShareNameHe());
					}
				}
				else
				{
					if (looper.isInLooper(finalShareData))
					{
						// minimun items in looper is 3
						if (looper.getLooperItemsCount() == Ticker.MIN_LOOPER_ITEMS)
						{
							showDialogCannotRemoveItem();
							// checkBoxAddToLooperFromSharePage.setChecked(true);
						}
						else
						{
							looper.removeLooperItem(finalShareData);
							showDialog(DIALOG_TYPE_REMOVE_ITEM, finalShareData.getShareNameHe());
						}
					}
				}
				isTickerChecked = !isTickerChecked;
			}
		});

		if (looper.isInLooper(finalShareData))
		{
			isTickerChecked = true;
		}
		else
		{
			isTickerChecked = false;

		}

		RelativeLayout layout_refresh_data = (RelativeLayout) findViewById(R.id.layout_refresh_data);
		layout_refresh_data.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				// TODO REFRESH DATA
				setRefreshData(uriToParse, parser);
				// setPullToRefreshList();

			}
		});

		// /////////////////////////////////////// --------<< END OF NEW
		// LAYOUT TEST >>-----------------/////////////////////////////////

		// // :מס' ני"ע
		// TextView textViewShareSymbol = (TextView)
		// findViewById(R.id.textViewShareSymbol);
		// textViewShareSymbol.setText(myResources.getString(R.string.share_activity_forex)
		// + " " + tempShareData.getShareSymbol());
		//
		// TextView textViewTrueForDate = (TextView)
		// findViewById(R.id.textViewTrueForDate);
		// textViewTrueForDate.setText(" נכון לתאריך  " +
		// tempShareData.getTimestamp());
		//
		// TextView textViewPrecentageChangeInHeader = (TextView)
		// findViewById(R.id.textViewPrecentageChangeInHeader);
		// textViewPrecentageChangeInHeader.setText(tempShareData.getDailyPercentageChange());
		//
		// TextView textViewLastPrice = (TextView)
		// findViewById(R.id.textViewLastPrice);
		// textViewLastPrice.setText("שער אחרון:" +
		// tempShareData.getLastPrice());
		//
		// TextView textViewPrecentageChange = (TextView)
		// findViewById(R.id.textViewPrecentageChange);
		// textViewPrecentageChange.setText(tempShareData.getDailyPercentageChange());
		//
		// TextView textViewDailyPointsChange = (TextView)
		// findViewById(R.id.textViewDailyPointsChange);
		// textViewDailyPointsChange.setText(tempShareData.getDailyPointsChange());
		//
		// TextView textViewShareVolume = (TextView)
		// findViewById(R.id.textViewShareVolume);
		// textViewShareVolume.setText(tempShareData.getShareVolume());
		//
		// textViewDailyHigh = (TextView)
		// findViewById(R.id.textViewDailyHigh);
		// textViewDailyHigh.setText(tempShareData.getDailyHigh());
		//
		// TextView textViewDailyLow = (TextView)
		// findViewById(R.id.textViewDailyLow);
		// textViewDailyLow.setText(tempShareData.getDailyLow());
		//
		// TextView textViewShareMarketCap = (TextView)
		// findViewById(R.id.textViewShareMarketCap);
		// textViewShareMarketCap.setText(tempShareData.getShareMarketCap());
		//
		// setBtnManagePfItem();
		//
		// setImageGraph(tempShareData);
		//
		// ImageView imageViewUpDownArrow = (ImageView)
		// findViewById(R.id.imageViewUpDownArrow);
		//
		// if (tempShareData.getDailyPercentageChange().length() <= 2)
		// {
		// imageViewUpDownArrow.setImageResource(R.drawable.transparent);
		// }
		// else if (tempShareData.getDailyPercentageChange().contains("-"))
		// {
		// imageViewUpDownArrow.setImageResource(R.drawable.stockdown);
		// textViewPrecentageChangeInHeader.setTextColor(0xffce0f0f); /*
		// * Set
		// * color
		// * to
		// * Custom
		// * Red
		// */
		// }
		// else
		// {
		// imageViewUpDownArrow.setImageResource(R.drawable.stockup);
		// textViewPrecentageChangeInHeader.setTextColor(0xff1aad20); /*
		// * Set
		// * color
		// * to
		// * Custom
		// * Green
		// */
		// }
		// }
		if (Definitions.toUseAjillion)
		{
			new initAjillionView(ad_ajillion_below_tikishi, Definitions.AJILLION_AD_ID_INNER_BANNER).execute();
		}
	}

	private void setNewBtnManagePfItem()
	{
		// TODO eli 
		RelativeLayout layout_add_to_portfolio = (RelativeLayout) findViewById(R.id.layout_add_to_portfolio);
		//		TextView textView__add_to_portfolio = (TextView) findViewById(R.id.textView__add_to_portfolio);
		//		TextView textView_portfolio_name = (TextView) findViewById(R.id.textView_portfolio_name);

		// Button buttonTogglePfItem = (Button)
		// findViewById(R.id.buttonTogglePfItem);

		isPortfolioItem = intent.getBooleanExtra("isPfItem", false);

		if (isPortfolioItem)
		{
			//			textView__add_to_portfolio.setText("חסר");
			//			textView_portfolio_name.setText("מתיק");
			//			// buttonTogglePfItem.setBackgroundResource(R.color.button_pf_share_cancel);
			portfolioAction = "delete";
		}
		else
		{
			//			textView__add_to_portfolio.setText("הוסף");
			//			textView_portfolio_name.setText("לתיק");
			//			// buttonTogglePfItem.setBackgroundResource(R.color.button_pf_share_add);
			portfolioAction = "add";
		}
		// buttonTogglePfItem.setOnClickListener(new View.OnClickListener()
		layout_add_to_portfolio.setOnClickListener(new View.OnClickListener()
		{
			String globesAccessKey = "";
			String portfolioID = "";

			@Override
			public void onClick(View v)
			{
				globesAccessKey = DataContext.Instance().getAccessKey();

				if (globesAccessKey.length() < 2)
				{
					requestLogin();
				}
				else
				{
					//					ShareData tempShareData = ((ShareData) parsedNewsSet.itemHolder.get(0));
					Intent ShareTikAishi = new Intent(ShareActivity.this ,AddRemoveShareTikAishiActivity.class);
					ShareTikAishi.putExtra("shareId", shareId);
					ShareTikAishi.putExtra("feederId", feeder);


					startActivity(ShareTikAishi);

					//					togglePfItem(portfolioAction);
				}
			}

			private void togglePfItem(final String Action)
			{
				Thread t = new Thread(new Runnable()
				{
					@Override
					public void run()
					{
						String pfActionResult = "";

						getPortfolioID();
						HttpClient client = new DefaultHttpClient();
						HttpPost post = new HttpPost("http://www.globes.co.il/data/webservices/portfolios.asmx/portfolioAction");

						try
						{
							List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
							nameValuePairs.add(new BasicNameValuePair("PortfolioID", portfolioID));
							ShareData tempShareData = ((ShareData) parsedNewsSet.itemHolder.get(0));
							nameValuePairs.add(new BasicNameValuePair("InstrumentID", tempShareData.getInsturmentID()));

							// nameValuePairs.add(new
							// BasicNameValuePair("InstrumentID",
							// intent.getStringExtra("shareId")));
							nameValuePairs.add(new BasicNameValuePair("Action", Action));
							nameValuePairs.add(new BasicNameValuePair("feeder", ""));
							nameValuePairs.add(new BasicNameValuePair("accessKey", globesAccessKey));

							post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
							HttpResponse response = client.execute(post);
							pfActionResult = extractActionResult(response.getEntity().getContent());

						}
						catch (IOException e)
						{
							e.printStackTrace();
						}
						catch (Exception e)
						{
							e.printStackTrace();
						}

						Message toMainProcess = new Message();
						toMainProcess.obj = pfActionResult;
						handler.sendMessage(toMainProcess);
					}
				});

				t.start();
			}

			public void getPortfolioID()
			{
				portfolioID = DataContext.Instance().getPortfolioID();

				if (portfolioID.length() < 1)
				{
					HttpClient client = new DefaultHttpClient();
					HttpPost post = new HttpPost("http://www.globes.co.il/data/webservices/portfolios.asmx/GetPortfolios");

					try
					{
						List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);

						nameValuePairs.add(new BasicNameValuePair("Mode", "2"));
						nameValuePairs.add(new BasicNameValuePair("PortfolioId", ""));
						nameValuePairs.add(new BasicNameValuePair("accessKey", globesAccessKey));

						post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
						HttpResponse response = client.execute(post);
						extractPortfolioID(response.getEntity().getContent());

					}
					catch (IOException e)
					{
						e.printStackTrace();
					}
				}
			}

			private void extractPortfolioID(InputStream is)
			{
				final String PORTFOLIO_ID = "portfolio_id";
				final String ERROR_MESSAGE = "Error";

				try
				{
					XmlPullParser parser = Xml.newPullParser();
					parser.setInput(is, null);
					int eventType = parser.getEventType();
					boolean done = false;

					while (eventType != XmlPullParser.END_DOCUMENT && !done)
					{
						switch (eventType)
						{
							case XmlPullParser.START_TAG :
								String name = parser.getName();
								if (name.equalsIgnoreCase(PORTFOLIO_ID))
								{
									portfolioID = parser.nextText();
									DataContext.Instance().setPortfolioID(portfolioID);
									done = true;
								}
								else if (name.equalsIgnoreCase(ERROR_MESSAGE))
								{
									done = true;
								}

								break;
							case XmlPullParser.END_TAG :

								break;
						}

						eventType = parser.next();
					}
				}
				catch (Exception e)
				{
					throw new RuntimeException(e);
				}
			}

			private Handler handler = new Handler()
			{
				public void handleMessage(Message msg)
				{
					String ActionResult = (String) msg.obj;

					// Button buttonTogglePfItem = (Button)
					// findViewById(R.id.buttonTogglePfItem);

					TextView textView__add_to_portfolio = (TextView) findViewById(R.id.textView__add_to_portfolio);
					TextView textView_portfolio_name = (TextView) findViewById(R.id.textView_portfolio_name);
					if (ActionResult.equalsIgnoreCase("true"))
					{
						isPortfolioItem = !isPortfolioItem;

						if (isPortfolioItem)

						{
							textView__add_to_portfolio.setText("חסר");
							textView_portfolio_name.setText("מתיק");
							// buttonTogglePfItem.setBackgroundResource(R.color.button_pf_share_cancel);
							// portfolioAction = "delete";
						}
						else
						{
							textView__add_to_portfolio.setText("הוסף");
							textView_portfolio_name.setText("לתיק");
							// buttonTogglePfItem.setBackgroundResource(R.color.button_pf_share_add);
							// portfolioAction = "add";
						}

						//
						// if (isPortfolioItem)
						// {
						// buttonTogglePfItem.setBackgroundResource(R.color.button_pf_share_cancel);
						// }
						// else
						// {
						// buttonTogglePfItem.setBackgroundResource(R.color.button_pf_share_add);
						// }
					}
				}
			};

			private String extractActionResult(InputStream is)
			{
				final String RESULT = "string";
				final String ERROR_MESSAGE = "Error";
				String message = "";

				try
				{
					XmlPullParser parser = Xml.newPullParser();
					parser.setInput(is, null);
					int eventType = parser.getEventType();
					boolean done = false;
					while (eventType != XmlPullParser.END_DOCUMENT && !done)
					{
						switch (eventType)
						{
							case XmlPullParser.START_TAG :
								String name = parser.getName();
								if (name.equalsIgnoreCase(RESULT))
								{
									message = parser.nextText();
									done = true;
								}
								else if (name.equalsIgnoreCase(ERROR_MESSAGE))
								{
									// loginErrorMessage = parser.nextText();
									// LoginErrorPage("ãåàø àì÷èøåðé/ñéñîä ùâåééí",
									// 0);
									// notifyExistingUser();

									done = true;
								}

								break;
							case XmlPullParser.END_TAG :
								done = true;

								break;
						}

						eventType = parser.next();
					}
				}
				catch (Exception e)
				{
					throw new RuntimeException(e);
				}

				return message;
			}
		});
	}

	private void setImageGraph(final ShareData tempShareData)
	{
		Runnable runnable = new Runnable()
		{
			@Override
			public void run()
			{
				String imageToGet;
				if ((tempShareData).getShareType().equals("currency"))
				{
					imageToGet = GlobesURL.URLForexGraph;
				}
				else
				{
					imageToGet = GlobesURL.URLShareGraph;
				}
				// imageToGet = imageToGet.replace("EEEE",
				// (tempShareData).getExchangeInitials()).replace("XXXX", "d")
				// .replace("SSSS",
				// URLEncoder.encode(tempShareData.getShareSymbol(),
				// "windows-1255")).replace("WWWW", "155")
				// .replace("HHHH", "118");
				ImageView imageViewGraph = (ImageView) findViewById(R.id.imageViewGraph);

				imageToGet = "http://graph.globes.co.il/graphs3.5/cchart.aspx";

				stockGraph = Utils.DownloadImage(imageToGet);

				handler.post(new Runnable()
				{
					@Override
					public void run()
					{
						ImageView imageViewGraph = (ImageView) findViewById(R.id.imageViewGraph);
						imageViewGraph.setImageBitmap(stockGraph);
					}
				});
			}
		};
		new Thread(runnable).start();
	}

	private void setBtnManagePfItem()
	{
		Button buttonTogglePfItem = (Button) findViewById(R.id.buttonTogglePfItem);

		isPortfolioItem = intent.getBooleanExtra("isPfItem", false);

		if (isPortfolioItem)
		{

			buttonTogglePfItem.setBackgroundResource(R.color.button_pf_share_cancel);
			portfolioAction = "delete";
		}
		else
		{
			buttonTogglePfItem.setBackgroundResource(R.color.button_pf_share_add);
			portfolioAction = "add";
		}

		buttonTogglePfItem.setOnClickListener(new View.OnClickListener()
		{
			String globesAccessKey = "";
			String portfolioID = "";

			@Override
			public void onClick(View v)
			{
				globesAccessKey = DataContext.Instance().getAccessKey();

				if (globesAccessKey.length() < 2)
				{
					requestLogin();
				}
				else
				{
					togglePfItem(portfolioAction);
				}
			}

			private void togglePfItem(final String Action)
			{
				Thread t = new Thread(new Runnable()
				{
					@Override
					public void run()
					{
						String pfActionResult = "";

						getPortfolioID();
						HttpClient client = new DefaultHttpClient();
						HttpPost post = new HttpPost("http://www.globes.co.il/data/webservices/portfolios.asmx/portfolioAction");

						try
						{
							List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
							nameValuePairs.add(new BasicNameValuePair("PortfolioID", portfolioID));
							nameValuePairs.add(new BasicNameValuePair("InstrumentID", intent.getStringExtra("shareId")));
							nameValuePairs.add(new BasicNameValuePair("Action", Action));
							nameValuePairs.add(new BasicNameValuePair("feeder", ""));
							nameValuePairs.add(new BasicNameValuePair("accessKey", globesAccessKey));

							post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
							HttpResponse response = client.execute(post);
							pfActionResult = extractActionResult(response.getEntity().getContent());

						}
						catch (IOException e)
						{
							e.printStackTrace();
						}
						catch (Exception e)
						{
							e.printStackTrace();
						}

						Message toMainProcess = new Message();
						toMainProcess.obj = pfActionResult;
						handler.sendMessage(toMainProcess);
					}
				});

				t.start();
			}

			public void getPortfolioID()
			{
				portfolioID = DataContext.Instance().getPortfolioID();

				if (portfolioID.length() < 1)
				{
					HttpClient client = new DefaultHttpClient();
					HttpPost post = new HttpPost("http://www.globes.co.il/data/webservices/portfolios.asmx/GetPortfolios");

					try
					{
						List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);

						// nameValuePairs.add(new BasicNameValuePair("Mode",
						// "2"));
						nameValuePairs.add(new BasicNameValuePair("Mode", "4"));

						nameValuePairs.add(new BasicNameValuePair("PortfolioId", ""));
						nameValuePairs.add(new BasicNameValuePair("accessKey", globesAccessKey));

						post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
						HttpResponse response = client.execute(post);
						extractPortfolioID(response.getEntity().getContent());

					}
					catch (IOException e)
					{
						e.printStackTrace();
					}
				}
			}

			private void extractPortfolioID(InputStream is)
			{
				final String PORTFOLIO_ID = "portfolio_id";
				final String ERROR_MESSAGE = "Error";

				try
				{
					XmlPullParser parser = Xml.newPullParser();
					parser.setInput(is, null);
					int eventType = parser.getEventType();
					boolean done = false;

					while (eventType != XmlPullParser.END_DOCUMENT && !done)
					{
						switch (eventType)
						{
							case XmlPullParser.START_TAG :
								String name = parser.getName();
								if (name.equalsIgnoreCase(PORTFOLIO_ID))
								{
									portfolioID = parser.nextText();
									DataContext.Instance().setPortfolioID(portfolioID);
									done = true;
								}
								else if (name.equalsIgnoreCase(ERROR_MESSAGE))
								{
									done = true;
								}

								break;
							case XmlPullParser.END_TAG :

								break;
						}

						eventType = parser.next();
					}
				}
				catch (Exception e)
				{
					throw new RuntimeException(e);
				}
			}

			private Handler handler = new Handler()
			{
				public void handleMessage(Message msg)
				{
					String ActionResult = (String) msg.obj;

					Button buttonTogglePfItem = (Button) findViewById(R.id.buttonTogglePfItem);
					if (ActionResult.equalsIgnoreCase("true"))
					{
						isPortfolioItem = !isPortfolioItem;

						if (isPortfolioItem)
						{
							buttonTogglePfItem.setBackgroundResource(R.color.button_pf_share_cancel);
						}
						else
						{
							buttonTogglePfItem.setBackgroundResource(R.color.button_pf_share_add);
						}
					}
				}
			};

			private String extractActionResult(InputStream is)
			{
				final String RESULT = "string";
				final String ERROR_MESSAGE = "Error";
				String message = "";

				try
				{
					XmlPullParser parser = Xml.newPullParser();
					parser.setInput(is, null);
					int eventType = parser.getEventType();
					boolean done = false;
					while (eventType != XmlPullParser.END_DOCUMENT && !done)
					{
						switch (eventType)
						{
							case XmlPullParser.START_TAG :
								String name = parser.getName();
								if (name.equalsIgnoreCase(RESULT))
								{
									message = parser.nextText();
									done = true;
								}
								else if (name.equalsIgnoreCase(ERROR_MESSAGE))
								{
									// loginErrorMessage = parser.nextText();
									// LoginErrorPage("ãåàø àì÷èøåðé/ñéñîä ùâåééí",
									// 0);
									// notifyExistingUser();

									done = true;
								}

								break;
							case XmlPullParser.END_TAG :
								done = true;

								break;
						}

						eventType = parser.next();
					}
				}
				catch (Exception e)
				{
					throw new RuntimeException(e);
				}

				return message;
			}
		});
	}

	private void requestLogin()
	{
		// ShareData tempShareData =
		// ((ShareData)parsedNewsSet.itemHolder.get(0));
		// isInstrument = intent.getBooleanExtra(Definitions.ISINSTRUMENT,
		// false);

		Intent presentLoginScreen = new Intent(this, NewLoginActivity.class);

		// Intent presentLoginScreen = new Intent(this, LoginActivity.class);
		presentLoginScreen.putExtra("URI", "");

		presentLoginScreen.putExtra(Definitions.ISINSTRUMENT, intent.getBooleanExtra(Definitions.ISINSTRUMENT, false));
		ShareData tempShareData = ((ShareData) parsedNewsSet.itemHolder.get(0));
		presentLoginScreen.putExtra("feederId", tempShareData.getFeeder());

		presentLoginScreen.putExtra("shareId", tempShareData.getInsturmentID());
		// presentLoginScreen.putExtra("feederId",
		// intent.getStringExtra("feederId"));
		// presentLoginScreen.putExtra("shareId",
		// intent.getStringExtra("shareId"));
		presentLoginScreen.putExtra("name", intent.getStringExtra("name"));
		presentLoginScreen.putExtra("isPfItem", isPortfolioItem);

		startActivity(presentLoginScreen);
	}

	void setLandscapeModeView()
	{
		setContentView(R.layout.layout_landscape_share_view);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);

		float screenDensity = this.getResources().getDisplayMetrics().density;
		imageHeight = getWindowManager().getDefaultDisplay().getHeight() - (int) (80 * screenDensity);
		imageWidth = getWindowManager().getDefaultDisplay().getWidth();

		final String shareSymbol = ((ShareData) parsedNewsSet.itemHolder.get(0)).getShareSymbol();
		imageViewBigGraph = (ImageView) findViewById(R.id.imageViewBigGraph);
		final RadioButton dailyGraphButton = (RadioButton) findViewById(R.id.dailyGraphButton);
		final RadioButton weeklyGraphButton = (RadioButton) findViewById(R.id.weeklyGraphButton);
		final RadioButton monthlyGraphButton = (RadioButton) findViewById(R.id.monthlyGraphButton);
		final RadioButton sixMonthsGraphButton = (RadioButton) findViewById(R.id.sixMonthsGraphButton);
		final RadioButton yearlyGraphButton = (RadioButton) findViewById(R.id.yearlyGraphButton);

		OnClickListener graphChoiceListener = new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				String dayString, weekString, monthString, halfYearString, YearString;

				imageViewBigGraph.setImageBitmap(null);
				findViewById(R.id.progressBarBigGraph).setVisibility(android.view.View.VISIBLE);

				if (((ShareData) parsedNewsSet.itemHolder.get(0)).getShareType().equals("currency"))
				{
					linkImageBigGraph = GlobesURL.URLForexGraph;
					dayString = "1";
					weekString = "5";
					monthString = "21";
					halfYearString = "126";
					YearString = "245";
				}
				else
				{
					linkImageBigGraph = GlobesURL.URLShareGraph;
					dayString = "d";
					weekString = "w";
					monthString = "m";
					halfYearString = "h";
					YearString = "y";
				}

				try
				{
					linkImageBigGraph = linkImageBigGraph
							.replace("EEEE", ((ShareData) parsedNewsSet.itemHolder.get(0)).getExchangeInitials())
							.replace("SSSS", URLEncoder.encode(shareSymbol, "windows-1255")).replace("WWWW", Integer.toString(imageWidth))
							.replace("HHHH", Integer.toString(imageHeight));
					// TODO tracking with analytics
					// tracker.trackPageView(linkImageBigGraph.replaceAll("http://graph.globes.co.il/",
					// Definitions.URLprefix));
				}
				catch (UnsupportedEncodingException e)
				{
				}

				if (v.equals(dailyGraphButton))
				{
					tempLinkImageBigGraph = linkImageBigGraph.replace("XXXX", dayString);
				}
				else if (v.equals(weeklyGraphButton))
				{
					tempLinkImageBigGraph = linkImageBigGraph.replace("XXXX", weekString);
				}
				else if (v.equals(monthlyGraphButton))
				{
					tempLinkImageBigGraph = linkImageBigGraph.replace("XXXX", monthString);
				}
				else if (v.equals(sixMonthsGraphButton))
				{
					tempLinkImageBigGraph = linkImageBigGraph.replace("XXXX", halfYearString);
				}
				else
				{
					tempLinkImageBigGraph = linkImageBigGraph.replace("XXXX", YearString);
				}

				Runnable runnable = new Runnable()
				{
					@Override
					public void run()
					{
						try
						{
							bitmap = Utils.DownloadImage(tempLinkImageBigGraph);
							// handler.sendEmptyMessage(2);
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
								imageViewBigGraph.setImageBitmap(bitmap);
								findViewById(R.id.progressBarBigGraph).setVisibility(android.view.View.GONE);
							}
						});
					}
				};
				new Thread(runnable).start();
			}
		};

		dailyGraphButton.setOnClickListener(graphChoiceListener);
		weeklyGraphButton.setOnClickListener(graphChoiceListener);
		monthlyGraphButton.setOnClickListener(graphChoiceListener);
		sixMonthsGraphButton.setOnClickListener(graphChoiceListener);
		yearlyGraphButton.setOnClickListener(graphChoiceListener);

		graphChoiceListener.onClick(dailyGraphButton);

	}

	void setLandscapeModeView1()
	{
		setContentView(R.layout.layout_landscape_share_view);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);

		float screenDensity = this.getResources().getDisplayMetrics().density;
		imageHeight = getWindowManager().getDefaultDisplay().getHeight() - (int) (80 * screenDensity);
		imageWidth = getWindowManager().getDefaultDisplay().getWidth();

		final String shareSymbol = ((ShareData) parsedNewsSet.itemHolder.get(0)).getShareSymbol();
		imageViewBigGraph = (ImageView) findViewById(R.id.imageViewBigGraph);
		final RadioButton dailyGraphButton = (RadioButton) findViewById(R.id.dailyGraphButton);
		final RadioButton weeklyGraphButton = (RadioButton) findViewById(R.id.weeklyGraphButton);
		final RadioButton monthlyGraphButton = (RadioButton) findViewById(R.id.monthlyGraphButton);
		final RadioButton sixMonthsGraphButton = (RadioButton) findViewById(R.id.sixMonthsGraphButton);
		final RadioButton yearlyGraphButton = (RadioButton) findViewById(R.id.yearlyGraphButton);

		OnClickListener graphChoiceListener = new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				String dayString, weekString, monthString, halfYearString, YearString;

				imageViewBigGraph.setImageBitmap(null);
				findViewById(R.id.progressBarBigGraph).setVisibility(android.view.View.VISIBLE);

				if (((ShareData) parsedNewsSet.itemHolder.get(0)).getShareType().equals("currency"))
				{
					linkImageBigGraph = GlobesURL.URLForexGraph;
					dayString = "1";
					weekString = "5";
					monthString = "21";
					halfYearString = "126";
					YearString = "245";
				}
				else
				{
					linkImageBigGraph = GlobesURL.URLShareGraph;
					dayString = "d";
					weekString = "w";
					monthString = "m";
					halfYearString = "h";
					YearString = "y";
				}

				try
				{
					linkImageBigGraph = linkImageBigGraph
							.replace("EEEE", ((ShareData) parsedNewsSet.itemHolder.get(0)).getExchangeInitials())
							.replace("SSSS", URLEncoder.encode(shareSymbol, "windows-1255")).replace("WWWW", Integer.toString(imageWidth))
							.replace("HHHH", Integer.toString(imageHeight));
				}
				catch (UnsupportedEncodingException e)
				{
				}

				if (v.equals(dailyGraphButton))
				{
					tempLinkImageBigGraph = linkImageBigGraph.replace("XXXX", dayString);
				}
				else if (v.equals(weeklyGraphButton))
				{
					tempLinkImageBigGraph = linkImageBigGraph.replace("XXXX", weekString);
				}
				else if (v.equals(monthlyGraphButton))
				{
					tempLinkImageBigGraph = linkImageBigGraph.replace("XXXX", monthString);
				}
				else if (v.equals(sixMonthsGraphButton))
				{
					tempLinkImageBigGraph = linkImageBigGraph.replace("XXXX", halfYearString);
				}
				else
				{
					tempLinkImageBigGraph = linkImageBigGraph.replace("XXXX", YearString);
				}

				Runnable runnable = new Runnable()
				{
					@Override
					public void run()
					{
						try
						{
							bitmap = Utils.DownloadImage(tempLinkImageBigGraph);
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
								imageViewBigGraph.setImageBitmap(bitmap);
								findViewById(R.id.progressBarBigGraph).setVisibility(android.view.View.GONE);
							}
						});
					}
				};
				new Thread(runnable).start();
			}
		};

		dailyGraphButton.setOnClickListener(graphChoiceListener);
		weeklyGraphButton.setOnClickListener(graphChoiceListener);
		monthlyGraphButton.setOnClickListener(graphChoiceListener);
		sixMonthsGraphButton.setOnClickListener(graphChoiceListener);
		yearlyGraphButton.setOnClickListener(graphChoiceListener);

		graphChoiceListener.onClick(dailyGraphButton);
	}

	@Override
	void createBannerData()
	{

	}

	@Override
	public void onConfigurationChanged(Configuration newConfig)
	{
		super.onConfigurationChanged(newConfig);

		if (isFinishedParsing)
		{
			if (newConfig.orientation == android.content.res.Configuration.ORIENTATION_PORTRAIT)
			{
				// TODO  eli
				//				finish();

				getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
				getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
				setRefreshDataFromPortrait(uriToParse, parser);

			}
			else if (newConfig.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE)
			{

				setLandscapeModeView();
			}
		}
	}

	void addInstrumentComposition()
	{
		if (isInstrument)
		{
			Instrument goToIndexComposition = new Instrument(feederId, shareId, "הרכב המדד", "", "", isInstrument);
			parsedNewsSet.addItem(goToIndexComposition);
		}
	}

	@Override
	void addStaticItemsToNewsSet()
	{
		addInstrumentComposition();
	}

	public void startAnimation()
	{
		// Create an animation
		RotateAnimation rotation = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		rotation.setDuration(1200);
		rotation.setInterpolator(new LinearInterpolator());
		rotation.setRepeatMode(Animation.RESTART);
		rotation.setRepeatCount(Animation.INFINITE);

		imageViewBigGraph.bringToFront();
		// and apply it to your imageview
		imageViewBigGraph.startAnimation(rotation);
	}

	public void buttonShare_onClick(View v)
	{
		String instrumentId = intent.getStringExtra("instrumentIdForSharing");
		Intent shareArticle = new Intent();
		shareArticle.setAction(Intent.ACTION_SEND);
		String mimeType = "text/plain";
		shareArticle.setType(mimeType);
		shareArticle.putExtra(Intent.EXTRA_SUBJECT, this.shareName);
		shareArticle.putExtra(Intent.EXTRA_TEXT, Definitions.watchShare + "\n\n" + GlobesURL.URLInstrumentToShare + instrumentId
				+ Definitions.fromPlatformAppAnalytics + "\n\n" + Definitions.ShareString + " " + Definitions.shareGlobesWithQuates
				+ "\n\n" + Definitions.shareCellAppsPart1 + " " + Definitions.shareGlobesWithQuates + " " + Definitions.shareCellAppsPart2
				+ "\n");
		// TODO tracking
		// tracker.trackPageView(GlobesURL.URLInstrumentToShare + instrumentId +
		// "from=" + Definitions.SHAREsuffix);
		shareArticle.putExtra(Intent.EXTRA_TITLE, Definitions.ShareString);
		startActivity(Intent.createChooser(shareArticle, Definitions.Share));
	}

	/**
	 * Ugly fix for in order to prevent from the push dialog to be invoked few
	 * times
	 */
	@Override
	protected void savePrefsIntValue(String key, int value)
	{
	}

	@Override
	protected void onPause()
	{
		super.onPause();
	}

	/**
	 * 
	 * @param type
	 *            - Dialog type Add/Remove item
	 * @param itemNameHE
	 *            the added/removed item nameHE
	 */
	private void showDialog(int type, String itemNameHE)
	{

		AlertDialog.Builder ad = new AlertDialog.Builder(activity);

		ad.setCancelable(false);
		ad.setTitle(R.string.title_update_looper_dialog);
		StringBuilder msg = new StringBuilder();
		msg.append("נייר ערך ");
		msg.append(itemNameHE);
		msg.append(" ");

		switch (type)
		{
			case DIALOG_TYPE_ADD_ITEM :
				msg.append("נוסף ל");
				break;
			case DIALOG_TYPE_REMOVE_ITEM :
				msg.append("הוסר מ");
				break;

		}
		msg.append("פס המדדים - פסנוע המדדים יעודכן אוטומטית");

		ad.setMessage(msg.toString());

		ad.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener()
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

	private void showDialogCannotRemoveItem()
	{
		AlertDialog.Builder ad = new AlertDialog.Builder(activity);
		ad.setCancelable(false);
		ad.setTitle(R.string.title_update_looper_dialog);
		StringBuilder msg = new StringBuilder();
		msg.append("נייר ערך לא הוסר מפס המדדים ");
		msg.append("כמות ניירות ערך מינימלי בפס מדדים הוא שלושה");
		ad.setMessage(msg.toString());

		ad.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener()
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

	public void getGlobesAccessKey()
	{
		// SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		// this.globesAccessKey = settings.getString("globesAccessKey", "");

		globesAccessKey = DataContext.Instance().getAccessKey();

		SharedPreferences settings = this.getSharedPreferences(PREFS_NAME, 0);
		if (globesAccessKey.length() < 2)
		{
			globesAccessKey = settings.getString("globesAccessKey", "");
			DataContext.Instance().setAccessKey(globesAccessKey);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		Log.e("eli", "onActivityResult in Share Activity " +resultCode);
		if (resultCode == -2)
		{
			ShareActivity.this.finish();

		}

	}

	private void requestNewLogin()
	{

		Intent intent = new Intent(ShareActivity.this, NewLoginActivity.class);
		startActivityForResult(intent, 0);
		// startActivity(new Intent(ShareActivity.this,
		// NewLoginActivity.class));
	}

	@Override
	public void onMainRightMenuItemSelected(CustomMenuItem item)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onSwitchContentFragment(Fragment fragment, String tag, boolean addToBackStack, boolean isLoadAfterSlidingMenuCloses, boolean b)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onSetFragmentWebview(WebView webView)
	{
		// TODO Auto-generated method stub

	}



	@Override
	public void onRedMailSendPicture()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onRedMailSendVideo()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onCloseGeneralSearch()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public SlidingMenu getsSlidingMenu()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void onStart()
	{
		//		Log.i("eli", "Android_StockPage_#"+shareId+"#_"+feeder);
		Utils.writeScreenToGoogleAnalytics(this, "Android_StockPage_#"+shareId+"_#"+feeder);
		super.onStart();
	}
	//	@Override
	//	protected void onStop()
	//	{
	//		EasyTracker.getInstance(this).activityStop(this);
	//		super.onStop();
	//	}


	@Override
	public void onRefreshStarted(View view)
	{
		//		Log.i("eli","Refresh " + shareName);
		Utils.writeEventToGoogleAnalytics(activity, "Refresh", "מניות", shareName);
		super.onRefreshStarted(view);
	}


	class initAjillionView extends AsyncTask<Void, Void, Void>
	{
		private ImageView ajillionObjImage;
		private WebView ajillionObjImageGif;
		private AjillionObj ajillionObj;
		private RelativeLayout frame;
		private String id;

		public initAjillionView(RelativeLayout frame, String id)
		{
			this.id = id;
			this.frame = frame ;
		}

		@Override
		protected void onPreExecute() 
		{
			ajillionObjImage = new ImageView(ShareActivity.this);
			ajillionObjImageGif = new WebView(ShareActivity.this);

			Utils.centerViewInRelative(ajillionObjImageGif, false);
			Utils.centerViewInRelative(ajillionObjImage, true);
			ajillionObjImage.setScaleType(ScaleType.FIT_XY);

			frame.removeAllViews();
			frame.addView(ajillionObjImage);
		}

		@Override
		protected Void doInBackground(Void... params)
		{
			ajillionObj = Utils.getAjillionObj(ShareActivity.this, id, 320, 50,false);
			Log.e("eli", ajillionObj.toString());
			return null;
		}

		@Override
		protected void onPostExecute(Void result)
		{
			super.onPostExecute(result);
			if (ajillionObj == null || ajillionObj.getSuccess()==null ||!ajillionObj.getSuccess() || ajillionObj.getCreative_url() == null || ajillionObj.getCreative_url().equals(""))
			{
				return;
			}
			frame.setVisibility(View.VISIBLE);
			if(ajillionObj.getCreative_type().equals("image"))
			{

				if (!ajillionObj.getCreative_url().endsWith(".gif"))
				{
					Picasso.with(ShareActivity.this).load(ajillionObj.getCreative_url()).into(ajillionObjImage);
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
					frame.addView(ajillionObjImageGif);
					ajillionObjImageGif.getSettings().setJavaScriptEnabled(true);
					ajillionObjImageGif.loadData(Utils.centerHTML(ajillionObj.getCreative_url(), true), "text/html; charset=utf-8", "UTF-8");
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
	}


}