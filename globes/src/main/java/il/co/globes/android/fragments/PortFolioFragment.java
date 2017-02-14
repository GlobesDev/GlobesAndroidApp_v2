package il.co.globes.android.fragments;

import il.co.globes.android.Definitions;
import il.co.globes.android.NewLoginActivity;
import il.co.globes.android.R;
import il.co.globes.android.Utils;
import il.co.globes.android.interfaces.GlobesListener;
import il.co.globes.android.objects.DataContext;
import il.co.globes.android.objects.GlobesURL;
import tv.justad.sdk.view.JustAdView;
import tv.justad.sdk.view.JustAdViewDelegate;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.doubleclick.PublisherAdView;

public class PortFolioFragment extends MainFragment
{
	private static final String PREFS_NAME = "preferences";
	private String globesAccessKey = "";
	private final String TAG = "PortfolioActivity";
	static final int SHARE_PAGE = 5;
	private String header;

	/**
	 * Ads
	 */
	// DfpAdView dfpAdView;
	// DfpAdView dfpAdViewOptional;
	PublisherAdView dfpAdView;
	PublisherAdView dfpAdViewOptional;
	JustAdView jaAdView;
	boolean loggedIn;

	// callback
	private GlobesListener mCallback;
	private LayoutInflater mInflater;
	private boolean init = false;

	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);
		// This makes sure that the container activity has implemented
		// the callback interface. If not, it throws an exception
		try
		{
			mCallback = (GlobesListener) activity;
		}
		catch (ClassCastException e)
		{
			throw new ClassCastException(activity.toString() + " must implement " + GlobesListener.class.getSimpleName());
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		
		Log.e("eli", "onCreateView on PortFolioFragment");
		mInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View v = inflater.inflate(il.co.globes.android.R.layout.pull_to_refresh_portfolio, container, false);

		
		return v;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		init=false;
		super.onActivityCreated(savedInstanceState);

		header = getArguments().getString(Definitions.HEADER);

		if (header != null)
		{

			// set custom action Bar with the header

			mCallback.onSetActionBarWithTextTitle(header, R.color.blue_light);

		}

	}
	public void getGlobesAccessKey()
	{
		// SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		// this.globesAccessKey = settings.getString("globesAccessKey", "");

		this.globesAccessKey = DataContext.Instance().getAccessKey();

		SharedPreferences settings = getActivity().getSharedPreferences(PREFS_NAME, 0);
		if (this.globesAccessKey.length() < 2)
		{
			this.globesAccessKey = settings.getString("globesAccessKey", "");
			DataContext.Instance().setAccessKey(this.globesAccessKey);
		}
	}

	private void requestLogin()
	{
		startActivity(new Intent(getActivity(), NewLoginActivity.class));
	}

	@Override
	void initAdView(View view)
	{
		if (loggedIn)
		{
			// Look up the DfpAdView as a resource and load a request.
//			dfpAdView = (PublisherAdView) view.findViewById(R.id.adViewPortfolioActivity);

			// the optional DFPAdView
//			dfpAdViewOptional = (PublisherAdView) view.findViewById(R.id.dfpAdView_pull_to_refresh_portfolio_optional);

			// TODO list test , get the list and set margin params

			// load the Ad

//			dfpAdView.setAdListener(new AdListener()
//			{
//				public void onAdLoaded()
//				{
//					 Ad received
//					Log.d(TAG, "Ad received: ");
//					dfpAdView.setBackgroundColor(Color.TRANSPARENT);
//					if (dfpAdView.getVisibility() != View.VISIBLE)
//					{
//						dfpAdView.setVisibility(View.VISIBLE);
//					}
//				}
//				public void onAdFailedToLoad(int errorCode)
//				{
//					Log.d(TAG, "Ad Failed: " + errorCode);
//
//					dfpAdView.setVisibility(View.GONE);
//
					// if (tracker != null)
					// {
					// tracker.trackPageView("/" + TAG +
					// Definitions.AD_FAILURE_TAG_BOTTOM_BANNER_PORTRAIT +
					// errorCode);
					// }
//				}
//			});
//			PublisherAdRequest request = new PublisherAdRequest.Builder().build();
//			dfpAdView.loadAd(request);
//
//			dfpAdViewOptional.setAdListener(new AdListener()
//			{
//				public void onAdLoaded()
//				{
//					Log.d(TAG, "adViewOptional received on PortfolioActivity:");
//					dfpAdViewOptional.setVisibility(View.VISIBLE);
//				}
//				public void onAdFailedToLoad(int errorCode)
//				{
//					Log.e(TAG, "adViewOptional failed on PortfolioActivity error is: " + errorCode);
//					dfpAdViewOptional.setVisibility(View.GONE);
//
					// if (tracker != null)
					// {
					// tracker.trackPageView("/" + TAG +
					// Definitions.AD_FAILURE_TAG_OPTIONAL_PORTAL_FINANCY +
					// errorCode);
					// }

//				}
//			});
//			PublisherAdRequest requestdfpAdViewOptional = new PublisherAdRequest.Builder().build();
//			dfpAdViewOptional.loadAd(requestdfpAdViewOptional);

		}

	}

	/**
	 * init rich media
	 */
	@Override
	protected void initRichMedia(View view)
	{
		jaAdView = (JustAdView) view.findViewById(R.id.justAdView_RICH_MEDIA_pull_to_refresh_portfolio);

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

	@Override
	void setInitialData(String uriToParse, String parser, View view)
	{
		getGlobesAccessKey();
		if (this.globesAccessKey.length() < 2)
		{
			requestLogin();
			loggedIn = false;
		}

		if (this.globesAccessKey.length() > 2)
		{

			loggedIn = true;
			while (this.globesAccessKey.indexOf('+') > -1)
			{
				this.globesAccessKey = this.globesAccessKey.replace("+", "%2B");
			}
			uriToParse = GlobesURL.URLPortfolio.replace("XXXX", this.globesAccessKey);
			super.setInitialData(uriToParse, parser, view);
		}
	}

	@Override
	public void onRefreshStarted(View view)
	{
		if (this.globesAccessKey.length() > 2)
		{		
			mCallback.getsSlidingMenu().setSlidingEnabled(false);

			loggedIn = true;
			while (this.globesAccessKey.indexOf('+') > -1)
			{
				this.globesAccessKey = this.globesAccessKey.replace("+", "%2B");
			}
			uriToParse = GlobesURL.URLPortfolio.replace("XXXX", this.globesAccessKey);
			// open menus
			mCallback.getsSlidingMenu().setSlidingEnabled(true);
			mPullToRefreshLayout.setRefreshComplete();
			super.setInitialData(uriToParse, parser, view);
		}
	}
	
	@Override
    public void onStart()
    {
        super.onStart();
       Utils.writeScreenToGoogleAnalytics(activity, activity.getResources().getString(R.string.il_co_globes_android_fragments_PortFolioFragment));
    }
	
	@Override
	public void onResume()
	{
		Log.e("eli", "onResume " + init);

		if (loggedIn && init)
		{		
			setInitialData(uriToParse, parser, getView());
			Log.e("eli", "onResume port frag");
			mPullToRefreshLayout.setRefreshComplete();

		}else {
			init = true;
		}
		
		super.onResume();
	}

}
