package il.co.globes.android.fragments;

import il.co.globes.android.Definitions;
import il.co.globes.android.R;
import il.co.globes.android.ShareActivity;
import il.co.globes.android.interfaces.GlobesListener;
import il.co.globes.android.objects.GlobesURL;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.actionbarsherlock.app.SherlockFragment;
import com.google.android.apps.analytics.GoogleAnalyticsTracker;

/**
 * 
 * @author Eviatar<br>
 * <br>
 *         Financial Portal as fragment
 */
@SuppressLint("SetJavaScriptEnabled")
public class FinancialPortalFragment extends SherlockFragment
{
	// data members
	private GoogleAnalyticsTracker tracker;
	// callback
	private GlobesListener mCallback;
	// UI
	private WebView webView;
	private ProgressBar progressBar;
	private String initialURL;
	
	private String header;

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
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		// tracker
		tracker = GoogleAnalyticsTracker.getInstance();
		//Log.e("alex","FinancialPortalFragment create");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_financial_portal, null);

		// find views
		progressBar = (ProgressBar) view.findViewById(R.id.progressBar_financial_portal_fragment);
		webView = (WebView) view.findViewById(R.id.webview_financial_portal_fragment);

		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);

		// set webview as current webview at host
		mCallback.onSetFragmentWebview(webView);

		// TODO problem with tracker
		// if (tracker != null)
		// {
		// tracker.trackPageView(Definitions.URLprefix +
		// "PortalMobileSite/FinanceNoHeader.aspx");
		// }

		header = getArguments().getString(Definitions.HEADER);

		if (header != null)
		{
			// TextView textViewHeader = (TextView)
			// getView().findViewById(R.id.grouptitle);
			// textViewHeader.setText(header);
			// if (Definitions.flipAlignment)
			// {
			// textViewHeader.setGravity(android.view.Gravity.RIGHT);
			// }

			// set custom action Bar with the header
			mCallback.onSetActionBarWithTextTitle(header, R.color.blue_light);

		}
		
		initUI();
	}

	/**
	 * Init UI elements
	 */
	private void initUI()
	{
		// set the webview client in order to prevent app exit on click
		webView.setWebViewClient(new myWebViewClient());

		// java script
		webView.getSettings().setJavaScriptEnabled(true);
		mCallback.onSetFragmentWebview(webView);

		// load URL
		// webView.loadUrl(GlobesURL.URLFinancialPortal);
		
		//Log.e("alex"," FinancialPortalFragment load url : " + initialURL);
		
		webView.loadUrl(initialURL);

	}

	private class myWebViewClient extends WebViewClient
	{

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon)
		{
			super.onPageStarted(view, url, favicon);
			progressBar.setVisibility(View.VISIBLE);
		}

		@Override
		public void onPageFinished(WebView view, String url)
		{
			super.onPageFinished(view, url);
			progressBar.setVisibility(View.GONE);

		}
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url)
		{
			// check if it is share page to view
			//Log.e("alex"," FinancialPortalFragment load url2 : " + url);
			
			if (url.contains("m.globes.co.il/news/m/instrument.aspx?h=3&iid="))

			// if
			// (url.contains("http://m.globes.co.il/news/m/instrument.aspx?h=2&iid="))
			{
				String feeder, rawIidAndFeeder, uri;
				feeder = "0";
				int feederStartIndex;
				rawIidAndFeeder = new String(url.substring(url.indexOf("iid=") + 4));
				feederStartIndex = rawIidAndFeeder.indexOf("&f");
				if (feederStartIndex != -1)
				{
					feeder = new String(rawIidAndFeeder.substring(feederStartIndex + 3));
					rawIidAndFeeder = new String(rawIidAndFeeder.substring(0, feederStartIndex));
				}
				uri = GlobesURL.URLSharePage.replaceAll("XXXX", feeder).replaceAll("YYYY", rawIidAndFeeder);
				Intent intent = new Intent(getActivity(), ShareActivity.class);
				intent.putExtra(Definitions.CALLER, Definitions.SHARES);
				intent.putExtra(Definitions.PARSER, Definitions.SHARES);
				intent.putExtra("feederId", feeder);
				intent.putExtra("shareId", rawIidAndFeeder);
				intent.putExtra("URI", uri);
				startActivity(intent);
				return true;
			}
			else if (!url.contains("http"))
			{

				url = GlobesURL.URLFinancialPortal;
				view.loadUrl(url);
				return true;
			}
			return false;
		}
	}
	public String getInitialURL()
	{
		return initialURL;
	}

	public void setInitialURL(String initialURL)
	{
		this.initialURL = initialURL;
	}
	
	
	@Override
    public void onStart()
    {
        super.onStart();
//        EasyTracker tracker = EasyTracker.getInstance(getActivity());
//        tracker.set(Fields.SCREEN_NAME, getActivity().getResources().getString(R.string.il_co_globes_android_fragments_FinancialPortalFragment));
//        tracker.send(MapBuilder.createAppView().build());
    }
}
