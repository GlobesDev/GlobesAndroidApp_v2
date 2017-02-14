package il.co.globes.android;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.facebook.AppEventsLogger;
import com.google.android.apps.analytics.GoogleAnalyticsTracker;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.doubleclick.PublisherAdView;

public class SplashArticle extends Activity
{
	FrameLayout frameLayAdContainer;

	/** TAG */
	private final static String TAG = "SplashArticle";

	static final int CLOSESPLASH = 1;

	Context context;

	/** the dfpAdview Ad */
	// DfpAdView dfpAdView;
	PublisherAdView dfpAdView;

	/** the frame layout for the dfpAdview container */
//	LinearLayout linearLayAdContainer;

	/**
	 * Boolean to check if Ad was clicked so we wait and not move to the
	 * MainTabActivity
	 */
	private boolean isAdClicked;

	/* google analytics tracker */
	GoogleAnalyticsTracker tracker;

	// splash time im mil sec
	private final int SPLASH_DISPLAY_LENGHT = 3000;

	private Handler splashArticleHandler = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{

			if (msg.what == CLOSESPLASH)
			{
				// if Ad not clicked we move to the MainTabActivity
				if (!isAdClicked)
				{
					// finish the activity
					finish();
				}

			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		context = getApplicationContext();

		tracker = GoogleAnalyticsTracker.getInstance();

		setContentView(R.layout.splash_article_activity);
		Log.e("eli", "Splash Article onCreate");
		initSplash();

	}

	/**
	 * inits the splash Ad and loads it
	 */
	private void initSplash()
	{
//		linearLayAdContainer = (LinearLayout) findViewById(R.id.layout_container_splash_article_activity);
		frameLayAdContainer = (FrameLayout) findViewById(R.id.frame_layout_splash_ad_container);

		/** /7263/Android.mavaron - 480x800 */
		dfpAdView = new PublisherAdView(SplashArticle.this);
		dfpAdView.setAdUnitId(Definitions.dfp_interstitial_article);
		dfpAdView.setAdSizes(new AdSize(320, 533));
//		linearLayAdContainer.addView(dfpAdView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

		dfpAdView.setAdListener(new AdListener()
		{
			public void onAdLoaded()
			{
				/**
				 * // * Log the Ad received , change background for black and
				 * start // * countDown for closing the Ad Ad clicked boolean
				 * for notifying // * if Ad was clicked //
				 */
				Log.d(TAG, "splash article activity  received Ad :");
				isAdClicked = false;
				dfpAdView.setBackgroundColor(Color.BLACK);
				countDownAndCloseSplash();
				
				
				frameLayAdContainer.addView(dfpAdView);
				ImageView imageView = new ImageView(SplashArticle.this);
				frameLayAdContainer.addView(imageView, FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
				imageView.setImageResource(R.drawable.close_ad);
				imageView.setPadding(32, 32, 32, 32);
				imageView.setOnClickListener(new View.OnClickListener()
				{
					@Override
					public void onClick(View v)
					{
						// TODO eli add button close
						frameLayAdContainer.setVisibility(View.GONE);
					}
				});
			}
			public void onAdFailedToLoad(int errorCode)
			{
				/**
				 * // * Log the Ad Error and close the Activity move to // *
				 * MainTabActivity //
				 */
				Log.d(TAG, "splash article activity failed to receive Ad " + errorCode);
				isAdClicked = false;
				splashArticleHandler.sendEmptyMessage(CLOSESPLASH);

				if (tracker != null)
				{
					tracker.trackPageView("/" + TAG + Definitions.AD_FAILURE_TAG_BOTTOM_BANNER_PORTRAIT + errorCode);
				}
			}
		});

		PublisherAdRequest request = new PublisherAdRequest.Builder().build();
		dfpAdView.loadAd(request);
	
	}

	/**
	 * counts the requested time in mil secs and closes splash
	 */
	void countDownAndCloseSplash()
	{
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					Thread.sleep(SPLASH_DISPLAY_LENGHT);

				}
				catch (Exception e)
				{
				}
				finally
				{
					if (!isAdClicked)
					{
						splashArticleHandler.sendEmptyMessage(CLOSESPLASH);
					}

				}
			}
		}).start();
	}
	/**
	 * Disable back button.
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		return false;
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		if (UtilsWebServices.checkInternetConnection(context))
		{
			AppEventsLogger.activateApp(context, Definitions.FACEBOOK_APP_ID);
		}
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
