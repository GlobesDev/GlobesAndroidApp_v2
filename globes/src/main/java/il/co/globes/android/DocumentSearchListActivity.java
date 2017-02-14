package il.co.globes.android;

import il.co.globes.android.objects.GlobesURL;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import tv.justad.sdk.view.JustAdView;
import tv.justad.sdk.view.JustAdViewDelegate;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.android.apps.analytics.GoogleAnalyticsTracker;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.doubleclick.PublisherAdView;

public class DocumentSearchListActivity extends MainActivity
{
	// Context context = this.context;
	final Activity documentSearchListActivity = this;
	EditText Et;
	boolean isFirstLoad = true;

	private final String TAG = "DocumentSearchListActivity";

	// DfpAdView dfpAdView;
	PublisherAdView dfpAdViewNew;
	JustAdView jaAdView;
	ListView listView;

	@Override
	public void onCreate(Bundle icicle)
	{
		super.onCreate(icicle);
		// remove the pop-up keyboard at start
		this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		tracker = GoogleAnalyticsTracker.getInstance();
		createSearchLayout();
		isFirstLoad = false;
		Button buttonSearch = (Button) findViewById(R.id.button_search);
		buttonSearch.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				searchUsingQueryFromEditText();
			}
		});
	}

	@Override
	void setContentView()
	{
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.pull_to_refresh_with_search_button);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.globes_title);
		Et = (EditText) findViewById(R.id.editTextSearchStock);
	}

	@Override
	void initAdView()
	{

		dfpAdViewNew = (PublisherAdView) this.findViewById(R.id.adViewDocumentSearchListActivity);

		dfpAdViewNew.setAdListener(new AdListener()
		{
			public void onAdLoaded()
			{
				// Ad received
				Log.d(TAG, "Ad received: ");
				dfpAdViewNew.setBackgroundColor(Color.TRANSPARENT);
				if (dfpAdViewNew.getVisibility() != View.VISIBLE)
				{
					dfpAdViewNew.setVisibility(View.VISIBLE);
				}
			}
			public void onAdFailedToLoad(int errorCode)
			{
				// Didnt receive Ad
				Log.d(TAG, "Ad Failed: " + errorCode);

				dfpAdViewNew.setVisibility(View.GONE);

				if (tracker != null)
				{
					tracker.trackPageView("/" + TAG + Definitions.AD_FAILURE_TAG_BOTTOM_BANNER_PORTRAIT + errorCode);
				}
			}
		});

		PublisherAdRequest request = new PublisherAdRequest.Builder().build();
		dfpAdViewNew.loadAd(request);

	
	}

	@Override
	void setInitialData(final String uriToParse, final String parser)
	{
		if (!isFirstLoad)
		{
			super.setInitialData(uriToParse, parser);
		}
	}

	void createSearchLayout()
	{
		Et.setOnKeyListener(new OnKeyListener()
		{

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event)
			{

				if (event.getAction() != KeyEvent.ACTION_DOWN) return true;

				boolean isKeyCodeEnter = false;
				if (keyCode == KeyEvent.KEYCODE_ENTER)
				{
					isKeyCodeEnter = true;
					searchUsingQueryFromEditText();
					return isKeyCodeEnter;
				}

				if (keyCode == KeyEvent.KEYCODE_BACK) onBackPressed();

				return true;

			}
		});
	}

	void searchUsingQueryFromEditText()
	{
		if (Et.getText().toString().length() >= 2)
		{
			try
			{
				uriToParse = GlobesURL.URLSearchArticle.replaceAll("XXXX", URLEncoder.encode(Et.getText().toString(), "windows-1255"));
				setInitialData(uriToParse, Definitions.SEARCH);
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(Et.getWindowToken(), 0);
			}
			catch (UnsupportedEncodingException e)
			{

			}
		}
	}

	@Override
	void createBannerData()
	{
		// URL listBannerURL;
		// int numOfBanners = parsedNewsSet.itemHolder.size() / 5 + 1;
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
		// }
		// }
		// // banner data for MainScreen
		// try
		// {
		// int bannerIndex = 0;
		// if (parsedNewsSet.itemHolder.size() >= 3 &&
		// banners.elementAt(0).getBannerURL() != null)
		// parsedNewsSet.itemHolder.add(3, banners.elementAt(0));
		// for (int i = 9; i < parsedNewsSet.itemHolder.size(); i += 5)
		// {
		// bannerIndex++;
		// if (banners.elementAt(bannerIndex).getBannerURL() != null)
		// parsedNewsSet.itemHolder.add(i, banners.elementAt(bannerIndex));
		// }
		// }
		// catch (ArrayIndexOutOfBoundsException e)
		// {
		//
		// }

	}

	@Override
	public void onBackPressed()
	{
		super.onBackPressed();
		Log.d(TAG, "Clicked BackBtn");

	}

	/**
	 * load the richmedia Ad
	 */
	@Override
	protected void loadRichMedia()
	{
		jaAdView.initAd();
	}

	/**
	 * init the rich media Ad
	 */
	@Override
	protected void initRichMedia()
	{
		jaAdView = (JustAdView) findViewById(R.id.justAdView_RICH_MEDIA_pull_to_refresh_with_search_button);

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
				// dfpAdView.setVisibility(View.GONE);
				dfpAdViewNew.setVisibility(View.GONE);
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
				// dfpAdView.setVisibility(View.VISIBLE);
				dfpAdViewNew.setVisibility(View.VISIBLE);
			}

		});
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
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		float logicalDensity = dm.density;
		return ((int) (dp * logicalDensity + 0.5));
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
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		float logicalDensity = dm.density;
		return (int) ((px + 0.5) / logicalDensity);
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
	protected void onStart()
	{
//		EasyTracker.getInstance(this).activityStart(this);
		super.onStart();
	}
	@Override
	protected void onStop()
	{
//		EasyTracker.getInstance(this).activityStop(this);
		super.onStop();
	}

}