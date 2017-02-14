package il.co.globes.android.fragments;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.doubleclick.PublisherAdView;

import il.co.globes.android.Definitions;
import il.co.globes.android.MainSlidingActivity;
import il.co.globes.android.R;
import il.co.globes.android.Utils;
import il.co.globes.android.interfaces.GlobesListener;
import il.co.globes.android.objects.Article;
import il.co.globes.android.objects.GlobesURL;
import il.co.globes.android.swipeListView.SwipeListView;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

/**
 * 
 * @author Eviatar<br>
 * <br>
 *         Section List Fragment
 */
public class SectionListFragment extends MainFragment
{
	private final String TAG = SectionListFragment.class.getSimpleName();

	// header
	private String header;

	// callback
	private GlobesListener mCallback;

	/**
	 * Ads
	 */
	// ListView listView;
	SwipeListView listView;

	// DfpAdView dfpAdView;
	// DfpAdView dfpAdViewOptional;
	PublisherAdView dfpAdView;
	PublisherAdView dfpAdViewOptional;

	private String isFromTagit = "0";

	private String isFromDocument;

	private String madorId;

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
		Log.e("eli", TAG);
		return inflater.inflate(R.layout.pull_to_refresh_with_header, null);
	}

	@Override
	public void onRefreshStarted(View view)
	{
		// eli on Refresh
		if (caller.compareTo("Sections")==0) 
		{
			if (isFromTagit.compareTo("1")==0)
			{
				Utils.writeEventToGoogleAnalytics(activity, "Refresh", "תגיות", mCallback.getActionBarTitle());
			}
			else 
			{
				Utils.writeEventToGoogleAnalytics(activity, "Refresh", "מדורים", mCallback.getActionBarTitle());
			}
		}
		super.onRefreshStarted(view);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{

		super.onActivityCreated(savedInstanceState);
		// TODO eli
		getListView().setDividerHeight(0);
		// get header from bundle
		header = getArguments().getString(Definitions.HEADER);
		//		args.putString("isFromTagit", "1");
		isFromTagit  = getArguments().getString("isFromTagit") != null ? getArguments().getString("isFromTagit"): "0";
		isFromDocument  = getArguments().getString("isFromDocument") != null ? getArguments().getString("isFromDocument"): "0";

		Log.e("eli", "isFromTagit="+isFromTagit+", caller="+caller+", isFromDocument="+isFromDocument+", "+isFromScheme);

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
			mCallback.onSetActionBarWithTextTitle(header, R.color.red);

			if (header.equals("שוק ההון"))
			{
				initOptionalDfpAdView(getView());
			}
		}
	}

	@Override
	void initAdView(View view)
	{
//		// Look up the DfpAdView as a resource and load a request.
//		dfpAdView = (PublisherAdView) view.findViewById(R.id.adViewSectionListActivity);
//
//		dfpAdView.setAdListener(new AdListener()
//		{
//			public void onAdLoaded()
//			{
//				// Ad received
//				Log.d(TAG, "Ad received: ");
//				dfpAdView.setBackgroundColor(Color.TRANSPARENT);
//				dfpAdView.setVisibility(View.VISIBLE);
//			}
//			public void onAdFailedToLoad(int errorCode)
//			{
//				Log.d(TAG, "Ad Failed: " + errorCode);
//				dfpAdView.setVisibility(View.GONE);
//
//				// if (tracker != null)
//				// {
//				// tracker.trackPageView("/" + TAG +
//				// Definitions.AD_FAILURE_TAG_BOTTOM_BANNER_PORTRAIT +
//				// errorCode);
//				// }
//			}
//		});
//		PublisherAdRequest request = new PublisherAdRequest.Builder().build();
//		dfpAdView.loadAd(request);


	}
	private void initOptionalDfpAdView(View view)
	{
//		// optional DFP adview for שוק ההון only
//		dfpAdViewOptional = (PublisherAdView) view.findViewById(R.id.dfpAdView_pull_to_refresh_with_header_optional);
//
//		dfpAdViewOptional.setAdListener(new AdListener()
//		{
//			public void onAdLoaded()
//			{
//				Log.d(TAG, "adViewOptional received on SectionListActivity Shook Hahon: ");
//				dfpAdViewOptional.setVisibility(View.VISIBLE);
//			}
//			public void onAdFailedToLoad(int errorCode)
//			{
//				Log.e(TAG, "adViewOptional failed on SectionListActivity Shook Hahon error is: " + errorCode);
//				dfpAdViewOptional.setVisibility(View.GONE);
//
//				// if (tracker != null)
//				// {
//				// tracker.trackPageView("/" + TAG +
//				// Definitions.AD_FAILURE_TAG_OPTIONAL_PORTAL_FINANCY +
//				// errorCode);
//				// }
//
//			}
//		});
//		PublisherAdRequest requestdfpAdViewOptional = new PublisherAdRequest.Builder().build();
//		dfpAdViewOptional.loadAd(requestdfpAdViewOptional);
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
	protected void onNewsSetParsed(String uriToParse, String parser)
	{
		if (!parser.equals(Definitions.OPINIONS))
		{

			// if we are from globes TV node , all are big TV
			if (uriToParse.equals(GlobesURL.URLClips) || MainSlidingActivity.getMainSlidingActivity().getActionBarTitle().compareTo("גלובס TV")==0)
			{
				for (int i = 0; i < parsedNewsSet.itemHolder.size(); i++)
				{
					if (((parsedNewsSet.itemHolder.get(i)).getClass()).equals(Article.class))
					{
						((Article) parsedNewsSet.itemHolder.get(i)).setBigTVArticle(true);
					}
				}
				// TODO eli - remove the line in globes TV
				//				getListView().setDividerHeight(0);
			}
			else
			{
				if (parsedNewsSet != null && parsedNewsSet.itemHolder != null)
				{
					for (int i = 0; i < parsedNewsSet.itemHolder.size(); i++)
					{
						if (((parsedNewsSet.itemHolder.get(i)).getClass()).equals(Article.class))
						{
							((Article) parsedNewsSet.itemHolder.get(i)).setBigArticle(true);
							break;
						}
					}
				}
			}

		}
	}

	@Override
	public void onStart()
	{
		super.onStart();
		if (isFromTagit!=null && caller!=null && isFromDocument!=null)
		{
			if (isFromTagit.compareTo("0") == 0 && caller.compareTo("Sections") == 0&& isFromDocument.compareTo("0") == 0)
			{
				String[] madorIdSpliter = uriToParse.split("=");
				madorId = madorIdSpliter[madorIdSpliter.length - 1];
				String toAnalytics = "Android_MadorPage_#" + madorId;
				//				Log.i("eli", toAnalytics);
				Utils.writeScreenToGoogleAnalytics(activity, toAnalytics);
			}
			else if (isFromTagit.compareTo("1") == 0 && caller.compareTo("Sections") == 0&& isFromDocument.compareTo("0") == 0)
			{
				String[] madorIdSpliter = uriToParse.split("=");
				madorId = madorIdSpliter[madorIdSpliter.length - 1];
				String toAnalytics = "Android_TagitResultByIdPage_#" + madorId;
				//				Log.i("eli", toAnalytics);
				Utils.writeScreenToGoogleAnalytics(activity, toAnalytics);
			}
			else if (isFromTagit.compareTo("0") == 0 && caller.compareTo("Sections") == 0&& isFromDocument.compareTo("1") == 0)
			{
				String[] madorIdSpliter = uriToParse.split("=");
				madorId = madorIdSpliter[madorIdSpliter.length - 1];
				try
				{
					madorId = URLDecoder.decode(madorId ,"UTF-8");
				}
				catch (UnsupportedEncodingException e){	}
				String toAnalytics = "Android_TagitResultByNamePage_#" + madorId;
				//				Log.i("eli", toAnalytics);
				Utils.writeScreenToGoogleAnalytics(activity, toAnalytics);
			}
		}
	}

}
