package il.co.globes.android;

import il.co.globes.android.fragments.AboutFragment;
import il.co.globes.android.fragments.PortFolioFragment;
import il.co.globes.android.fragments.TickerPreferencesFragment;
import il.co.globes.android.fragments.WebViewFragment;
import il.co.globes.android.interfaces.GlobesListener;
import il.co.globes.android.objects.CustomMenuItem;
import il.co.globes.android.objects.DataContext;
import il.co.globes.android.objects.GlobesURL;
import il.co.globes.android.objects.NewCustomMenuItem;
import il.co.globes.android.objects.NewsSet;
import il.co.globes.android.objects.TickerItem;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class TickerItemsAdapter extends BaseAdapter
{
	
	
	private ArrayList<TickerItem> items;
	private LayoutInflater inflater;
	private Context context;
	private TickerPreferencesFragment tickerPreferencesFragment;
	// callback
	private GlobesListener mCallback;
	// add for finance 
	private ArrayList<CustomMenuItem> customMenuItems;
	private ArrayList<CustomMenuItem> customFinancialMenuItems;
	private DataStore dataStore;
	private boolean isSeparatorAdded = false;
	private Resources resources;
	private static final String PREFS_NAME = "preferences";
	private String globesAccessKey = "";


	public TickerItemsAdapter(ArrayList<TickerItem> items, Context context, TickerPreferencesFragment tickerPreferencesFragment, GlobesListener mCallback)
	{
		this.items = items;
		this.context = context;
		this.inflater = LayoutInflater.from(context);
		this.tickerPreferencesFragment = tickerPreferencesFragment ;
		this.mCallback = mCallback;
		dataStore = DataStore.getInstance();
		resources = tickerPreferencesFragment.getResources();
		getDynamicMenuItems();
	}

	@Override
	public int getCount()
	{
		if (items == null)
			return 0;
		else return items.size() + 1;
	}

	@Override
	public Object getItem(int idx)
	{
		if (items != null)
			return items.get(idx);
		else return null;
	}

	@Override
	public long getItemId(int arg0)
	{
		return 0;
	}
	
	public void getGlobesAccessKey()
	{
		// SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		// this.globesAccessKey = settings.getString("globesAccessKey", "");

		globesAccessKey = DataContext.Instance().getAccessKey();

		SharedPreferences settings = getActivity().getSharedPreferences(PREFS_NAME, 0);
		if (globesAccessKey.length() < 2)
		{
			globesAccessKey = settings.getString("globesAccessKey", "");
			DataContext.Instance().setAccessKey(globesAccessKey);
		}
	}
	
	private Activity getActivity()
	{
		return (Activity)mCallback;
	}

	private void requestLogin()
	{
		 getActivity().startActivity(new Intent(getActivity(), NewLoginActivity.class));
	}
	
	protected void logOutUser()
	{

		SharedPreferences settings = getActivity().getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString("globesAccessKey", "");
		editor.commit();
		DataContext.Instance().setAccessKey("");

	}

	@Override
	public View getView(int pos, View convertView, ViewGroup arg2)
	{
		// TODO eli
		// in last place , inflate last element
		if (pos == getCount() - 1)
		{
			convertView = inflater.inflate(R.layout.layout_about_globes_looper, null);
			Button btnShareList, btnMarketsList, btnPortfolio;
			btnMarketsList = (Button) convertView.findViewById(R.id.Button_markets_list_from_about_looper);
			btnPortfolio = (Button) convertView.findViewById(R.id.Button_portfolio_list_from_about_looper);
			btnShareList = (Button) convertView.findViewById(R.id.Button_shares_list_from_about_looper);
			btnMarketsList.setOnClickListener(new OnClickListener()
			{

				@Override
				public void onClick(View v)
				{
					//1 TODO eli = ok
					CustomMenuItem item =customFinancialMenuItems.get(3);
					WebViewFragment fragment = new WebViewFragment();
					fragment.setInitialURL(item.uriToParse);
					Bundle args = new Bundle();

					args.putString(Definitions.HEADER, item.name);
					fragment.setArguments(args);
					mCallback.onSwitchContentFragment(fragment, AboutFragment.class.getSimpleName(), true, false, false);
				}
			});
			btnPortfolio.setOnClickListener(new OnClickListener()
			{

				@Override
				public void onClick(View v)
				{
					//2 TODO eli - 
					getGlobesAccessKey();
					if (globesAccessKey.length() < 2)
					{
						requestLogin();

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
					}
				}
			});
			btnShareList.setOnClickListener(new OnClickListener()
			{

				@Override
				public void onClick(View v)
				{
					//3 TODO eli - ok

					CustomMenuItem item =customFinancialMenuItems.get(1);
					WebViewFragment fragment = new WebViewFragment();
					fragment.setInitialURL(item.uriToParse);
					Bundle args = new Bundle();

					args.putString(Definitions.HEADER, item.name);
					fragment.setArguments(args);
					mCallback.onSwitchContentFragment(fragment, AboutFragment.class.getSimpleName(), true, false, false);
				}
			});
		}
		else
		{
			final int postion = pos;
			convertView = inflater.inflate(R.layout.row_layout_about_looper_item, null);
			ImageButton imageButtonRemoveItem = (ImageButton) convertView
					.findViewById(R.id.imageButton_remove_item_from_looper_about_activity);
			if (tickerPreferencesFragment.isEditMode() && (getCount() - 1) > 3)
			{
				imageButtonRemoveItem.setVisibility(View.VISIBLE);
			}
			else
			{
				imageButtonRemoveItem.setVisibility(View.GONE);
			}
			imageButtonRemoveItem.setOnClickListener(new OnClickListener()
			{

				@Override
				public void onClick(View v)
				{
					if (items.contains(items.get(postion)))
					{
						items.remove(postion);
						if (tickerPreferencesFragment.isEditMode() && (getCount() - 1) == 3)
						{
							tickerPreferencesFragment.btnEditMode.setBackgroundResource(R.drawable.selector_button_edit_looper_items);
							tickerPreferencesFragment.setEditMode(!tickerPreferencesFragment.isEditMode());
						}
						notifyDataSetChanged();
					}
				}
			});
			TextView textViewLooperItemName = (TextView) convertView.findViewById(R.id.textView_looper_item_name);
			textViewLooperItemName.setText(items.get(pos).getNameHE());
			convertView.setClickable(false);
			convertView.setFocusable(false);
		}

		return convertView;
	}



	private void getDynamicMenuItems()
	{
		// TODO eli
		customMenuItems = new ArrayList<CustomMenuItem>();
		customFinancialMenuItems = new ArrayList<CustomMenuItem>();
		// get the holder
		NewsSet newsSet = Definitions.MapURLMode == Definitions.MAP_URL_MODE_DEBUG
				? dataStore.getDynamicMenuItemsHolderDebugMap()
						: dataStore.getDynamicMenuItemsHolderProductionMap();

				for (Object item : newsSet.itemHolder)
				{
					if (item.getClass() == NewCustomMenuItem.class)
					{

						NewCustomMenuItem newCustomMenuItem = (NewCustomMenuItem) item;
						// name
						String name = newCustomMenuItem.getName().replaceAll("null", "");
						// section id
						String sectionId = newCustomMenuItem.getId().replaceAll("null", "");
						String uri = "";
						// parser
						String parser = "";
						// set URI
						if (sectionId.equals(GlobesURL.TVNode))
						{
							uri = GlobesURL.URLClips;
						}
						else
						{
							uri = GlobesURL.URLSections + sectionId;
						}

						if (sectionId.equals("845"))
						{
							parser = Definitions.OPINIONS;
						}
						else
						{
							parser = Definitions.MAINSCREEN;
						}

						CustomMenuItem menuItem;

						if (newCustomMenuItem.isIsNodeWebItem() || newCustomMenuItem.isFinancialLinkItem())
						{

							if (newCustomMenuItem.isIsNodeWebItem() && !isSeparatorAdded)
							{
								menuItem = new CustomMenuItem("separator");
								customMenuItems.add(menuItem);
								isSeparatorAdded  = true;
							}

							if (newCustomMenuItem.getHref().replaceAll("null", "").equals("duns100"))
							{

								menuItem = new CustomMenuItem(name, 0, -1, -1, GlobesURL.URLDunsArticles, Definitions.DUNS_100,
										Definitions.SECTIONS, true, !TextUtils.isEmpty(newCustomMenuItem.getImgURL().replaceAll("null", ""))
										? newCustomMenuItem.getImgURL().replaceAll("null", "")
												: "", newCustomMenuItem.isIsNodeWebItem() ? true : false, newCustomMenuItem.isFinancialLinkItem()
														? true
																: false);
							}
							else if (newCustomMenuItem.getHref().replaceAll("null", "").equals("redMail"))
							{

								menuItem = new CustomMenuItem(name, 0, -1, getTextColor(name, false), "", "", Definitions.SECTIONS, true,
										!TextUtils.isEmpty(newCustomMenuItem.getImgURL().replaceAll("null", "")) ? newCustomMenuItem.getImgURL()
												.replaceAll("null", "") : "", newCustomMenuItem.isIsNodeWebItem() ? true : false,
														newCustomMenuItem.isFinancialLinkItem() ? true : false);
							}
							else
							{

								menuItem = new CustomMenuItem(name, 0, -1, getTextColor(name, newCustomMenuItem.isFinancialLinkItem()
										? true
												: false), newCustomMenuItem.getHref().replaceAll("null", ""), false, !TextUtils.isEmpty(newCustomMenuItem
														.getImgURL().replaceAll("null", "")) ? newCustomMenuItem.getImgURL().replaceAll("null", "") : "",
																newCustomMenuItem.isIsNodeWebItem() ? true : false, newCustomMenuItem.isFinancialLinkItem() ? true : false);
							}

						}
						else
						{

							if (newCustomMenuItem.getId().replaceAll("null", "").equals("ALL"))
							{
								menuItem = new CustomMenuItem(name, -1, -1, getTextColor(name, false), GlobesURL.URLMainScreen,
										Definitions.MAINSCREEN, Definitions.MAINTABACTVITY, null, false, !TextUtils.isEmpty(newCustomMenuItem
												.getImgURL().replaceAll("null", "")) ? newCustomMenuItem.getImgURL().replaceAll("null", "") : "",
														newCustomMenuItem.isIsNodeWebItem() ? true : false, newCustomMenuItem.isFinancialLinkItem() ? true : false);
							}
							else
							{

								menuItem = new CustomMenuItem(name, -1, -1, getTextColor(name, newCustomMenuItem.isFinancialLinkItem()
										? true
												: false), uri, parser, Definitions.SECTIONS, newCustomMenuItem.getId().replaceAll("null", ""), true,
												!TextUtils.isEmpty(newCustomMenuItem.getImgURL().replaceAll("null", "")) ? newCustomMenuItem.getImgURL()
														.replaceAll("null", "") : "", newCustomMenuItem.isIsNodeWebItem() ? true : false,
																newCustomMenuItem.isFinancialLinkItem() ? true : false);
							}
						}

						if (newCustomMenuItem.isFinancialLinkItem())
						{
							customFinancialMenuItems.add(menuItem);
						}
						else
						{
							customMenuItems.add(menuItem);
						}
					}
				}

	}

	/**
	 * Returns the text color as resource for the given name item
	 * 
	 * @param item
	 *            {@link CustomMenuItem}
	 * @return resID of the color
	 */
	private int getTextColor(String itemName, boolean isfinancialItem)
	{
		if (isfinancialItem)
		{

			return resources.getColor(R.color.white);
		}
		else if (itemName.equals("redMail"))
		{
			return resources.getColor(R.color.red);
		}
		else
		{
			return resources.getColor(R.color.right_menu_wall_street_item_color);
		}
	}
	
	

}
