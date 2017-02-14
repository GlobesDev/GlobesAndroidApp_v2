package il.co.globes.android.fragments;

import il.co.globes.android.DataStore;
import il.co.globes.android.Definitions;
import il.co.globes.android.DocumentActivity_new;
import il.co.globes.android.R;
import il.co.globes.android.Utils;
import il.co.globes.android.adapters.AdapterGeneralSearchItems;
import il.co.globes.android.ima.v3.player.PlayerImaActivity;
import il.co.globes.android.interfaces.GlobesListener;
import il.co.globes.android.native_video_player.PlayVideoActivity;
import il.co.globes.android.objects.Article;
import il.co.globes.android.objects.DataContext;
import il.co.globes.android.objects.GlobesURL;
import il.co.globes.android.objects.NewsSet;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.UUID;

import org.apache.http.client.ClientProtocolException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockListFragment;

/**
 * 
 * @author Eviatar<br>
 * <br>
 *         Overall server search as fragment with items , replaces right menu
 *         upon search.
 */
public class PortalFinancialSearchFragment extends SherlockListFragment
{
	// consts
	private static final int DONE_PARSING = 0;
	private static final int ERROR_PARSING = 1;

	// data
	private DataStore dataStore;
	private UUID unique;
	private AdapterGeneralSearchItems mAdapter;
	private NewsSet parsedSearchNewsSet;
	private String uriToParse;

	// the search query
	private String searchQuery;

	// callback
	private GlobesListener mCallback;

	// UI
	private ProgressBar progressBar;
	private ImageView imageViewCloseSearch;
	private Article articleGlobal;

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

		// data store
		dataStore = DataStore.getInstance();

		// create unique
		unique = UUID.randomUUID();

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_general_search, null);

		// find views
		progressBar = (ProgressBar) view.findViewById(R.id.progressBar_general_search_fragment);
		imageViewCloseSearch = (ImageView) view.findViewById(R.id.imageView_general_search_fragment_close_search);

		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);

		try
		{
			uriToParse = GlobesURL.URLStocksContains.replaceAll("XXXX", URLEncoder.encode(searchQuery, "windows-1255"));
		}
		catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
		}

		// UI elements
		initUI();

		// perform search
		performSearch();

	}

	private void initUI()
	{

		// listeners
		imageViewCloseSearch.setOnClickListener(new View.OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				// close the search view
				mCallback.onCloseGeneralSearch();
			}
		});
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id)
	{
		super.onListItemClick(l, v, position, id);

		// Article
		Article article = (Article) parsedSearchNewsSet.itemHolder.elementAt(position);
		articleGlobal = (Article) parsedSearchNewsSet.itemHolder.elementAt(position);

		switch (article.getArticleSearchType())
		{
			case ARTICLE :

				Intent intent = new Intent(getActivity(), DocumentActivity_new.class);

				DataContext.Instance().SetArticleList(unique.toString(), parsedSearchNewsSet);
				intent.putExtra("position", position);
				intent.putExtra("parentId", unique.toString());
				startActivity(intent);

				break;
			case CLIP :
				if (article.isHTML5video())
				{
					openVideoInMediaPlayerOrBrowser(article.getUrlHTML5(), true, article.getDoc_id());
				}
				else
				{
					openVideoInMediaPlayerOrBrowser(article.getUrl(), false, article.getDoc_id());
				}
				break;
			case TAGIT :

				break;
			default :
				break;
		}

	}

	/**
	 * Open the Video in a Thread
	 * 
	 * @param url
	 *            the video URL
	 * @param isHTML5
	 *            if true than this video is HTML5
	 */
	private void openVideoInMediaPlayerOrBrowser(final String url, final boolean isHTML5, final String docId)
	{
		new AsyncTask<Void, Void, Void>()
		{

			@Override
			protected Void doInBackground(Void... params)
			{
				openVideoInMediaPlayerOrBrowserNoThread(url, isHTML5, docId);
				return null;
			}

		}.execute();
	}

	/**
	 * Perform search and set results with adapter
	 */
	private void performSearch()
	{

		new AsyncTask<Void, Void, Integer>()
		{

			protected void onPreExecute()
			{
				// hide keyboard
				InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(imageViewCloseSearch.getWindowToken(), 0);
			};

			@Override
			protected Integer doInBackground(Void... params)
			{
				int res;

				try
				{
					URL url = new URL(uriToParse);
					parsedSearchNewsSet = NewsSet.parseURL(url, Definitions.SEARCH);
					res = DONE_PARSING;

				}
				catch (Exception e)
				{
					e.printStackTrace();
					res = ERROR_PARSING;
				}

				return res;
			}

			protected void onPostExecute(Integer result)
			{
				// remove progress bar
				progressBar.setVisibility(View.GONE);

				// set data to list
				switch (result)
				{
					case DONE_PARSING :
						// set data to list
						if (mAdapter == null)
						{
							mAdapter = new AdapterGeneralSearchItems(getActivity(), parsedSearchNewsSet.itemHolder);
						}
						else
						{
							mAdapter.setItems(parsedSearchNewsSet.itemHolder);
						}
						setListAdapter(mAdapter);
						break;
					case ERROR_PARSING :
						Toast.makeText(getActivity(), Definitions.ErrorLoading, Toast.LENGTH_SHORT).show();
						mCallback.onCloseGeneralSearch();
						break;
					default :
						break;
				}
			};

		}.execute();
	}

	/**
	 * Play a video whether HTML5 or regular
	 * 
	 * @param url
	 *            the video URL
	 * @param isHTML5
	 *            if true it is an HTML5 video
	 * @param docId 
	 */
	private void openVideoInMediaPlayerOrBrowserNoThread(String url, boolean isHTML5, String docId)
	{

		String videoUrl;

		try
		{
			if (isHTML5)
			{
				videoUrl = url;
				// remove the apple type from url
				if (videoUrl.contains("&AppleType=html"))
				{
					videoUrl = videoUrl.replace("&AppleType=html", "");
				}
			}
			else
			{
				if (!Definitions.mediaPlayerInBrowser)
				{
					/** get the device info and add it to the URL */
					url = getDeviceDetailsForVideoUrl(url);

					if (url.contains("http://www.cast-tv.biz/play/"))
					{
						if (Definitions.toUseGIMA)
						{
							videoUrl = Utils.resolveRedirect("http://www.cast-tv.biz/play/" + url/*+ Utils.resolveRedirect(url, true)*/,false);
						}else 
						{
							videoUrl = Utils.resolveRedirect("http://www.cast-tv.biz/play/" +  Utils.resolveRedirect(url, true),false);
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

			if (videoUrl != null && !videoUrl.contains("Failed_To_Get_Url"))
			{
				String name = articleGlobal.getTitle();
				String mador = "כללי";
				Utils.writeEventToGoogleAnalytics(getActivity(), "הפעלת וידאו", mador, name);
				
				Uri uri = Uri.parse(videoUrl);				
				if (Definitions.toUseGIMA)
				{
					// pre roll eli
					//Intent i = new Intent(getActivity(), PlayVideoActivity.class);
					Intent i = new Intent(getActivity(), PlayerImaActivity.class);
					i.putExtra("videoURL", uri.toString());
					i.putExtra("docId", docId);

					startActivity(i);
				}else {
					Intent intent = new Intent(Intent.ACTION_VIEW, uri);
					// intent.setDataAndType(data, "video/mp4");
					startActivity(intent);
				}
			}
		}
		catch (ClientProtocolException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Attaches Device details to the URL
	 * 
	 * @param url
	 *            the video url
	 * @return url with device details
	 */
	private String getDeviceDetailsForVideoUrl(String url)
	{
		String MO = dataStore.getDeviceModel();
		MO = MO.replace(" ", "");
		MO = MO.replace(" ", "");
		MO = MO.replace(" ", "");
		return url + "&w=" + dataStore.getScreenWidth() + "&h=" + dataStore.getScreenHeight() + "&dt=" + MO;
	}

	public String getSearchQuery()
	{
		return searchQuery;
	}

	public void setSearchQuery(String searchQuery)
	{
		this.searchQuery = searchQuery;
	}



	@Override
	public void onStart()
	{
		super.onStart();
		//        EasyTracker tracker = EasyTracker.getInstance(getActivity());
		//        tracker.set(Fields.SCREEN_NAME, getActivity().getResources().getString(R.string.il_co_globes_android_fragments_PortalFinancialSearchFragment));
		//        tracker.send(MapBuilder.createAppView().build());
	}

}
