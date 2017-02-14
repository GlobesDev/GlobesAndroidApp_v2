package il.co.globes.android.fragments;

import il.co.globes.android.DataStore;
import il.co.globes.android.Definitions;
import il.co.globes.android.LazyAdapter;
import il.co.globes.android.MainSlidingActivity;
import il.co.globes.android.NewLoginActivity;
import il.co.globes.android.R;
import il.co.globes.android.Utils;
import il.co.globes.android.adapters.AdapterRightMenuItems;
import il.co.globes.android.interfaces.GlobesListener;
import il.co.globes.android.objects.CustomMenuItem;
import il.co.globes.android.objects.DataContext;
import il.co.globes.android.objects.GlobesURL;
import il.co.globes.android.objects.NewCustomMenuItem;
import il.co.globes.android.objects.NewsSet;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockListFragment;
//import com.google.android.gms.internal.fa;

public class MenuListFragment extends SherlockListFragment
{

	// KEY BUNDLES FOR MENU TYPES
	public static final String KEY_BUNDLE_MENU_TYPE = "menu type";
	private static final String PREFS_NAME = "preferences";
	private String globesAccessKey = "";

	public static final int MENU_TYPE_MAIN_RIGHT_MENU = 0;
	// UI - search functionality
	private ImageView imageViewSearch;
	private EditText editTextSearchQuery;
	private ImageView Img_portal, Img_globes;
	private RelativeLayout tikIchiLayout;
	private Button button_LogIn;
	// data members
	private DataStore dataStore;
	private BaseAdapter mAdapter;

	// list items
	List<CustomMenuItem> customMenuItems = new ArrayList<CustomMenuItem>();
	List<CustomMenuItem> customFinancialMenuItems = new ArrayList<CustomMenuItem>();
	private boolean fromGlobesButton;
	private LazyAdapter lazyAdapter;
	Boolean isSeparatorAdded = false;
	// callback
	private GlobesListener mCallback;
	private RelativeLayout header_layout_right, header_layout_left;

	// resources
	private Resources resources;
	private String uriToParse;
	private ProgressDialog pd;
	private String searchString;
	NewsSet parsedNewsSet;
	int prevTextLen = 0;

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
	}

	private void setSearchFunctionality(View v)
	{

		imageViewSearch = (ImageView) v.findViewById(R.id.imageView_menu_item_button_search);
		imageViewSearch.setBackgroundDrawable(getResources().getDrawable(R.drawable.globes_search));
		editTextSearchQuery = (EditText) v.findViewById(R.id.editText_menu_item_search_view);
		editTextSearchQuery.setOnEditorActionListener(new TextView.OnEditorActionListener() 
		{
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) 
			{
				if (actionId == EditorInfo.IME_ACTION_SEARCH) 
				{
					if (fromGlobesButton)
					{
						//						Log.i("eli", "ביצוע חיפוש");
						Utils.writeEventToGoogleAnalytics(getActivity(), "תפריט", "גלובס", "ביצוע חיפוש");
						String searchQuery = editTextSearchQuery.getText().toString();
						if (!TextUtils.isEmpty(searchQuery))
						{
							performSearch(searchQuery);
						}
					}else {
						// eli search from financi
						//						Log.i("eli", "ביצוע חיפוש בפיננסי");
						Utils.writeEventToGoogleAnalytics(getActivity(), "תפריט", "פורטל פיננסי", "ביצוע חיפוש");
						searchString = editTextSearchQuery.getText().toString();
						if (searchString.equals(""))
						{
							mAdapter = new AdapterRightMenuItems(getActivity(), customFinancialMenuItems, getActivity());
							setListAdapter(mAdapter);
							return true;
						}
						if (searchString.length() >= 1)
						{
							try
							{
								uriToParse = GlobesURL.URLStocksContains.replaceAll("<query>", URLEncoder.encode(searchString, "UTF-8"));
								setInitialData(uriToParse, true);
							}
							catch (UnsupportedEncodingException e)
							{
							}
						}
					}
					return true;
				}
				return false;
			}
		});
		imageViewSearch.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if (fromGlobesButton)
				{
					//					Log.i("eli", "ביצוע חיפוש");
					Utils.writeEventToGoogleAnalytics(getActivity(), "תפריט", "גלובס", "ביצוע חיפוש");
					String searchQuery = editTextSearchQuery.getText().toString();
					// if not empty perform the search
					if (!TextUtils.isEmpty(searchQuery))
					{
						performSearch(searchQuery);
					}
				}
				else
				{
					// eli search is from finnanci
					//					Log.i("eli", "ביצוע חיפוש בפיננסי");
					Utils.writeEventToGoogleAnalytics(getActivity(), "תפריט", "פורטל פיננסי", "ביצוע חיפוש");
					searchString = editTextSearchQuery.getText().toString();
					if (searchString.equals(""))
					{
						mAdapter = new AdapterRightMenuItems(getActivity(), customFinancialMenuItems, getActivity());
						setListAdapter(mAdapter);
						return;
					}
					if (searchString.length() >= 1)
					{
						try
						{
							uriToParse = GlobesURL.URLStocksContains.replaceAll("<query>", URLEncoder.encode(searchString, "UTF-8"));
							setInitialData(uriToParse, true);
						}
						catch (UnsupportedEncodingException e)
						{
						}
					}
				}
			}
		});

		//		editTextSearchQuery.setOnKeyListener(new OnKeyListener()
		//		{
		//			@Override
		//			public boolean onKey(View v, int keyCode, KeyEvent event)
		//			{
		//
		//				if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN)
		//				{
		//					String searchQuery = editTextSearchQuery.getText().toString();
		//					if (fromGlobesButton)
		//					{
		//						// if not empty perform the search
		//						if (!TextUtils.isEmpty(searchQuery))
		//						{
		//							performSearch(searchQuery);
		//						}
		//						else
		//						{
		//							// hide keyboard
		//							InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		//							imm.hideSoftInputFromWindow(editTextSearchQuery.getWindowToken(), 0);
		//						}
		//					}
		//					else
		//					{
		//						try
		//						{
		//							uriToParse = GlobesURL.URLStocksContains.replaceAll("<query>", URLEncoder.encode(searchQuery, "UTF-8"));
		//							setInitialData(uriToParse);
		//						}
		//						catch (UnsupportedEncodingException e)
		//						{
		//							e.printStackTrace();
		//						}
		//					}
		//
		//					return true;
		//				}
		//				else
		//				{
		//					return false;
		//				}
		//
		//			}
		//		});
		//		editTextSearchQuery.setOnFocusChangeListener(new OnFocusChangeListener()
		//		{
		//			@Override
		//			public void onFocusChange(View v, boolean hasFocus)
		//			{
		//				if (hasFocus)
		//				{
		//					if(!fromGlobesButton)
		//					{
		//						Log.e("eli", " 22222  ביצוע חיפוש בפיננסי  " + hasFocus);
		//						Utils.writeEventToGoogleAnalytics(getActivity(), "תפריט", "פורטל פיננסי", "ביצוע חיפוש");
		//					}
		//				}
		//			}
		//		});
		editTextSearchQuery.addTextChangedListener(new TextWatcher()
		{

			@Override
			public void afterTextChanged(Editable s)
			{

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after)
			{

			}

			@Override
			public void onTextChanged(CharSequence inputCharSeq, int start, int before, int count)
			{
				// eli - on text search change
				if (!fromGlobesButton)
				{
					searchString = inputCharSeq.toString();
					if (searchString.equals(""))
					{
						mAdapter = new AdapterRightMenuItems(getActivity(), customFinancialMenuItems, getActivity());
						setListAdapter(mAdapter);
						return;
					}
					if (searchString.length() >= 1)
					{
						try
						{
							uriToParse = GlobesURL.URLStocksStartWith.replaceAll("<query>", URLEncoder.encode(searchString, "UTF-8"));
							setInitialData(uriToParse, false);
							// uriToParse =
							// Definitions.URLStocksOld.replaceAll("XXXX",
							// URLEncoder.encode(searchString, "UTF-8"));
							// ((PullToRefreshListView)
							// getListView()).onRefresh();
							// ((SwipeListView) getListView()).onRefresh();
						}
						catch (UnsupportedEncodingException e)
						{
						}
					}
				}
			}

		});

	}

	void setInitialData(final String uriToParse, final boolean isProgressEnable)
	{
		if (isProgressEnable)
			pd = ProgressDialog.show(getActivity(), "", Definitions.Loading, true, false);
		new AsyncTask<Void, Void, Void>()
		{
			@Override
			protected Void doInBackground(Void... params)
			{
				try
				{
					URL urlToParse = new URL(uriToParse);
					parsedNewsSet = NewsSet.parseURL(urlToParse, Definitions.MAINSCREEN);
				}
				catch (Exception e)
				{
				}
				return null;
			}
			@Override
			protected void onPostExecute(Void result)
			{
				if (isProgressEnable)
					pd.dismiss();
				//				Log.e("eli", "281");
				lazyAdapter = new LazyAdapter(getActivity(), parsedNewsSet.itemHolder);
				setListAdapter(lazyAdapter);
			}
		}.execute();
	}

	@Override
	public void onResume()
	{
		super.onResume();
		button_LogIn = (Button) getView().findViewById(R.id.Button_LogIn);
		//		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
		//		boolean b = prefs.getBoolean("rememberUser", false);
		getGlobesAccessKey();
		if (globesAccessKey.length() < 2 )
		{
			button_LogIn.setText(R.string.logIn);
		}
		else
		{
			button_LogIn.setText(R.string.logOut);
		}
	}
	/**
	 * Performs the search with query by replacing {@code MenuListFragment} on
	 * the right with {@code GeneralSearchFragment} and setting the query inside
	 * this fragment.
	 * 
	 * @param searchQuery
	 *            the String query
	 */
	GeneralSearchFragment fragment;
	private void performSearch(String searchQuery)
	{
		fragment = new GeneralSearchFragment();
		fragment.setSearchQuery(searchQuery);
		getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.list_frame_container, fragment).commit();
		//		editTextSearchQuery.setText(getResources().getString(R.string.search_article));
		editTextSearchQuery.setHint(R.string.search_article);
//		editTextSearchQuery.setGravity(Gravity.RIGHT);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		// TODO eli
		// View view = inflater.inflate(R.layout.list_with_search, null);
		View view = inflater.inflate(R.layout.a_menu_right, null);
		setSearchFunctionality(view);
		header_layout_left = (RelativeLayout) view.findViewById(R.id.header_layout_left);
		header_layout_right = (RelativeLayout) view.findViewById(R.id.header_layout_right);
		Img_globes = (ImageView) view.findViewById(R.id.Img_globes);
		fromGlobesButton = true;
		header_layout_right.setBackgroundColor(getResources().getColor(R.color.Transparent));
		header_layout_left.setBackgroundColor(getResources().getColor(R.color.right_menu_search_background));
		tikIchiLayout = (RelativeLayout) view.findViewById(R.id.tikIchiLayout);

		tikIchiLayout.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{

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

		button_LogIn = (Button) view.findViewById(R.id.Button_LogIn);
		button_LogIn.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)

			{

				if (!TextUtils.isEmpty(DataContext.Instance().getAccessKey()))
				{
					logOutUser();
					button_LogIn.setText(R.string.logIn);

				}
				else
				{
					Intent intent = new Intent(getActivity(), NewLoginActivity.class);
					startActivity(intent);
				}

			}
		});

		header_layout_right.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				//				Log.i("eli", "לא לאנליטיקס - פתיחת טאב גלובס");
				editTextSearchQuery.setText("");
				mAdapter = new AdapterRightMenuItems(getActivity(), customMenuItems, getActivity());
				setListAdapter(mAdapter);
				mAdapter.notifyDataSetChanged();
				tikIchiLayout.setVisibility(View.GONE);
				imageViewSearch.setBackgroundDrawable(getResources().getDrawable(R.drawable.globes_search));
//				editTextSearchQuery.setGravity(Gravity.RIGHT);
				editTextSearchQuery.setHint(R.string.search_article);
				header_layout_right.setBackgroundColor(getResources().getColor(R.color.Transparent));
				header_layout_left.setBackgroundColor(getResources().getColor(R.color.right_menu_search_background));
				fromGlobesButton = true;

			}
		});
		Img_portal = (ImageView) view.findViewById(R.id.Img_portal);
		header_layout_left.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// eli - in portal financi
				//				Log.i("eli", "פתיחת טאב פורטל פיננסי");
				Utils.writeEventToGoogleAnalytics(getActivity(),"תפריט", "פורטל פיננסי", "פתיחת טאב פורטל פיננסי");
				editTextSearchQuery.setText("");

				//tikIchiLayout.setVisibility(View.VISIBLE);
				tikIchiLayout.setVisibility(View.GONE);

				mAdapter = new AdapterRightMenuItems(getActivity(), customFinancialMenuItems, getActivity());
				setListAdapter(mAdapter);
				mAdapter.notifyDataSetChanged();
//				editTextSearchQuery.setGravity(Gravity.RIGHT);
				editTextSearchQuery.setHint(R.string.searchFinancial);

				imageViewSearch.setBackgroundDrawable(getResources().getDrawable(R.drawable.portal_financial_search));
				header_layout_left.setBackgroundColor(getResources().getColor(R.color.Transparent));
				header_layout_right.setBackgroundColor(getResources().getColor(R.color.right_menu_search_background));
				fromGlobesButton = false;
				if (fragment!= null)
				{
					getActivity().getSupportFragmentManager().beginTransaction().remove(fragment).commit();
				}
			}
		});

		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);

		resources = getResources();

		Bundle args = getArguments();

		int menuType;
		if (args != null)
		{
			menuType = args.getInt(KEY_BUNDLE_MENU_TYPE);

		}
		else
		{
			menuType = MENU_TYPE_MAIN_RIGHT_MENU;
		}

		/*
		 * string values for item menu names , from these names we create the
		 * CustomMenuItem
		 */

		switch (menuType)
		{
			case MENU_TYPE_MAIN_RIGHT_MENU :

				getDynamicMenuItems();
				mAdapter = new AdapterRightMenuItems(getActivity(), customMenuItems, getActivity());
				break;

			default :
				break;
		}

		setListAdapter(mAdapter);

	}
	/**
	 * Fetch data already parsed from
	 * {@code DataStore#getDynamicMenuItemsHolder()} and add items to list.
	 */

	private void getDynamicMenuItems()
	{
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
								isSeparatorAdded = true;
							}

							if (newCustomMenuItem.getHref().replaceAll("null", "").equals("duns100"))
							{
								menuItem = new CustomMenuItem(name, 0, -1, -1, GlobesURL.URLDunsArticles, Definitions.DUNS_100,
										Definitions.SECTIONS, true, 
										!TextUtils.isEmpty(newCustomMenuItem.getImgURL().replaceAll("null", ""))
										? newCustomMenuItem.getImgURL().replaceAll("null", ""): "", 
												newCustomMenuItem.isIsNodeWebItem() ? true : false,
														newCustomMenuItem.isFinancialLinkItem()	? true : false);
							}
							else if (newCustomMenuItem.getHref().replaceAll("null", "").equals("redMail"))
							{

								menuItem = new CustomMenuItem(name, 0, -1, getTextColor(name, false), "", "", Definitions.SECTIONS, true,
										!TextUtils.isEmpty(newCustomMenuItem.getImgURL().replaceAll("null", "")) ? newCustomMenuItem.getImgURL()
												.replaceAll("null", "") : "", newCustomMenuItem.isIsNodeWebItem() ? true : false,
														newCustomMenuItem.isFinancialLinkItem() ? true : false);
							}
							
							else if (newCustomMenuItem.getHref().replaceAll("null", "").equals("config"))		{

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

	@Override
	public void onListItemClick(ListView l, View v, int position, long id)
	{

		if (fromGlobesButton)
		{
			mCallback.onMainRightMenuItemSelected(customMenuItems.get(position));
		}
		else
		{
			// TODO eli
			mCallback.onMainRightMenuItemSelected(customFinancialMenuItems.get(position));

		}
	}

	protected void logOutUser()
	{

		SharedPreferences settings = getActivity().getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString("globesAccessKey", "");
		editor.commit();


		DataContext.Instance().setAccessKey("");

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

	private void requestLogin()
	{
		startActivity(new Intent(getActivity(), NewLoginActivity.class));
	}


	@Override
	public void onStart()
	{
		super.onStart();
		//		EasyTracker tracker = EasyTracker.getInstance(getActivity());
		//		tracker.set(Fields.SCREEN_NAME, getActivity().getResources().getString(R.string.il_co_globes_android_fragments_MenuListFragment));
		//		tracker.send(MapBuilder.createAppView().build());
	}
}
