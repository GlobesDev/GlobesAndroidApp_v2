package il.co.globes.android.fragments;

import il.co.globes.android.DataStore;
import il.co.globes.android.Definitions;
import il.co.globes.android.DocumentActivity_new;
import il.co.globes.android.adapters.AdapterSmallopinions;
import il.co.globes.android.interfaces.GlobesListener;
import il.co.globes.android.objects.ArticleSmallOpinion;
import il.co.globes.android.objects.DataContext;
import il.co.globes.android.objects.GlobesURL;
import il.co.globes.android.objects.NewsSet;

import java.net.URL;
import java.util.ArrayList;
import java.util.UUID;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

/**
 * 
 * @author Eviatar<br>
 * <br>
 * 
 *         Used on left menu shows a List of opinions from
 *         {@code GlobesURL#URLTop50SpokenIdeas}
 * 
 */
public class ListOpinionsFragment extends ListFragment
{

	private AdapterSmallopinions mAdapter;
	private NewsSet newsSet;
	private GlobesListener mCallback;
	private UUID unique;
	private DataStore dataStore;

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

		unique = UUID.randomUUID();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
		// data store
		Log.e("eli", "ListOpinionsFragment onActivityCreated");

		dataStore = DataStore.getInstance();
		new AsyncTask<Void, Void, Void>()
		{
			@Override
			protected Void doInBackground(Void... params)
			{
				URL url = null;
				try
				{
					url = new URL(GlobesURL.URLTop50SpokenIdeas);
					newsSet = NewsSet.parseURL(url, Definitions.LIST_OPINIONS_LEFT_MENU);
				}
				catch (Exception e)
				{
					Log.e("eli", "error", e);
				}
				return null;
			}

			@Override
			protected void onPostExecute(Void result)
			{
				mAdapter = new AdapterSmallopinions((Activity)mCallback, newsSet);
				setListAdapter(mAdapter);

				ArrayList<ArticleSmallOpinion> articleSmallOpinionArrayList = new ArrayList<ArticleSmallOpinion>();

				if (newsSet != null && newsSet.itemHolder!=null)
				{
					for (int i = 0; i < newsSet.itemHolder.size(); i++)
					{
						articleSmallOpinionArrayList.add((ArticleSmallOpinion) newsSet.itemHolder.get(i));
					}
				}
				dataStore.setArticleSmallOpinion(articleSmallOpinionArrayList);

			}
		}.execute();
	}
	public void refreshList()
	{
		Log.e("eli", "ListOpinionsFragment refreshList");

		new AsyncTask<Void, Void, Void>()
		{
			@Override
			protected Void doInBackground(Void... params)
			{
				URL url;
				try
				{
					url = new URL(GlobesURL.URLTop50SpokenIdeas);
					newsSet = NewsSet.parseURL(url, Definitions.LIST_OPINIONS_LEFT_MENU);
				}
				catch (Exception e)
				{
					Log.e("eli", "error", e);
				}
				return null;
			}

			@Override
			protected void onPostExecute(Void result)
			{
				mAdapter.setNewsSet(newsSet);
				mAdapter.notifyDataSetChanged();
				ArrayList<ArticleSmallOpinion> articleSmallOpinionArrayList = new ArrayList<ArticleSmallOpinion>();
				if (newsSet != null && newsSet.itemHolder!=null)
				{
					for (int i = 0; i < newsSet.itemHolder.size(); i++)
					{
						articleSmallOpinionArrayList.add((ArticleSmallOpinion) newsSet.itemHolder.get(i));
					}
				}
				dataStore.setArticleSmallOpinion(articleSmallOpinionArrayList);
			}
		}.execute();
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id)
	{
		super.onListItemClick(l, v, position, id);

		Intent intent = new Intent(getActivity(), DocumentActivity_new.class);
		DataContext.Instance().SetArticleList(unique.toString(), newsSet);
		intent.putExtra("position", position);
		intent.putExtra("parentId", unique.toString());
		startActivity(intent);
	}

//	@Override
//	public void onStart()
//	{
//		super.onStart();
//		//        EasyTracker tracker = EasyTracker.getInstance(getActivity());
//		//        tracker.set(Fields.SCREEN_NAME, getActivity().getResources().getString(R.string.il_co_globes_android_fragments_ListOpinionsFragment));
//		//        tracker.send(MapBuilder.createAppView().build());
//	}
}
