package il.co.globes.android;

import il.co.globes.android.objects.GlobesURL;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.facebook.AppEventsLogger;
import com.google.android.apps.analytics.GoogleAnalyticsTracker;

public class FinancialPortalActivity extends Activity
{

	WebView mWebView;
	Resources myResources;
	Context context;
	GoogleAnalyticsTracker tracker;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		final Activity financialPortalActivity = this;
		super.onCreate(savedInstanceState);
		context = getApplicationContext();
		myResources = getResources();

		// Adds Progrss bar Support
		this.getWindow().requestFeature(Window.FEATURE_PROGRESS);
		setContentView(R.layout.layout_financialportal);

		// google analytics tracker
		tracker = GoogleAnalyticsTracker.getInstance();
		// Makes Progress bar Visible
		getWindow().setFeatureInt(Window.FEATURE_PROGRESS, Window.PROGRESS_VISIBILITY_ON);

		mWebView = (WebView) findViewById(R.id.webview);
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.loadUrl(GlobesURL.URLFinancialPortal);
		
		mWebView.setWebChromeClient(new WebChromeClient()
		{
			public void onProgressChanged(WebView view, int progress)
			{
				// Make the bar disappear after URL is loaded, and changes
				// string to Loading...
				financialPortalActivity.setTitle(myResources.getString(R.string.tof_loading));
				financialPortalActivity.setProgress(progress * 100); // Make the
																		// bar
																		// disappear
																		// after
																		// URL
																		// is
																		// loaded

				// Return the app name after finish loading
				if (progress == 100)
				{
					financialPortalActivity.setTitle(R.string.financial_portal);
				}
			}
		});

		// set the webviewclient inorder to prevent app exit on click
		 mWebView.setWebViewClient(new myWebViewClient());

		if (tracker != null)
		{
			//tracker.trackPageView(Definitions.URLprefix + "PortalMobileSite/FinanceNoHeader.aspx");
		}

	}
	
	
	

	private class myWebViewClient extends WebViewClient
	{
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url)
		{
			// check if it is share page to view
			if (url.contains("iid="))
			{
				String feeder,  rawIidAndFeeder,uri;
				feeder = "0";
				int feederStartIndex;
				rawIidAndFeeder = new String(url.substring(url.indexOf("iid=") + 4));
				feederStartIndex = rawIidAndFeeder.indexOf("&f");
				if (feederStartIndex != -1)
				{
					feeder = new String(rawIidAndFeeder.substring(feederStartIndex + 3));
					rawIidAndFeeder = new String ( rawIidAndFeeder.substring(0,feederStartIndex));
				}
				uri = GlobesURL.URLSharePage.replaceAll("XXXX", feeder).replaceAll("YYYY", rawIidAndFeeder);
				Intent intent = new Intent(FinancialPortalActivity.this, ShareActivity.class);
				intent.putExtra(Definitions.CALLER, Definitions.SHARES);
				intent.putExtra(Definitions.PARSER, Definitions.SHARES);
				intent.putExtra("shareId", rawIidAndFeeder);
				intent.putExtra("feederId", feeder);
				intent.putExtra("URI", uri);
				startActivity(intent);
				return true;
			}
			else if (!url.contains("http")){
				url = GlobesURL.URLFinancialPortal;
				view.loadUrl(url);
				return true;
			}
			return false;
		}
	}

	@Override
	public void onBackPressed()
	{
		if (mWebView.canGoBack())
		{
			mWebView.goBack();
		}
		else
		{
			super.onBackPressed();
		}
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