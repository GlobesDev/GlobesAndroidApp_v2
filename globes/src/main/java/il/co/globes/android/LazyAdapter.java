package il.co.globes.android;


import il.co.globes.android.fragments.MainFragment;
import il.co.globes.android.fragments.SectionListFragment;
import il.co.globes.android.interfaces.GlobesListener;
import il.co.globes.android.interfaces.WallaAdvTaskCompleted;
import il.co.globes.android.objects.AjillionObj;
import il.co.globes.android.objects.Article;
import il.co.globes.android.objects.ArticleEmphasized;
import il.co.globes.android.objects.ArticleOpinion;
import il.co.globes.android.objects.Banner;
import il.co.globes.android.objects.DataContext;
import il.co.globes.android.objects.DfpAdHolder;
import il.co.globes.android.objects.GlobesURL;
import il.co.globes.android.objects.Group;
import il.co.globes.android.objects.GroupPortfolio;
import il.co.globes.android.objects.HeaderBanner;
import il.co.globes.android.objects.HeaderForex;
import il.co.globes.android.objects.HeaderFutures;
import il.co.globes.android.objects.HeaderInstruments;
import il.co.globes.android.objects.HeaderShares;
import il.co.globes.android.objects.InnerGroup;
import il.co.globes.android.objects.InnerGroupSections;
import il.co.globes.android.objects.Instrument;
import il.co.globes.android.objects.LiveBoxModel;
import il.co.globes.android.objects.PortfolioInstrument;
import il.co.globes.android.objects.RowArticleDuns100;
import il.co.globes.android.objects.RowCompanyDuns100;
import il.co.globes.android.objects.Stock;
import il.co.globes.android.objects.Tagit;
import il.co.globes.android.objects.Ticker;
import il.co.globes.android.objects.ShareObjects.ShareAnalystRecommendation;
import il.co.globes.android.objects.ShareObjects.ShareCompanyDescription;
import il.co.globes.android.objects.ShareObjects.ShareData;
import il.co.globes.android.objects.ShareObjects.ShareNewsArticle;
import il.co.globes.android.parsers.WallaAdvParser;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ExecutionException;

import net.tensera.sdk.utils.Logger;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.xmlpull.v1.XmlPullParser;

import android.R.color;
import android.annotation.SuppressLint;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Canvas.EdgeType;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Xml;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebViewClient;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.apps.analytics.GoogleAnalyticsTracker;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.doubleclick.PublisherAdView;
import com.squareup.picasso.Picasso;

import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.formats.NativeCustomTemplateAd;
import com.google.android.gms.ads.formats.NativeAppInstallAd;
import com.google.android.gms.ads.formats.NativeContentAd;

public class LazyAdapter extends BaseAdapter implements WallaAdvTaskCompleted
{
	
	private enum EncodeType
	{
		 TYPE_BODY
	};

	private int pinkColor;
	private int blackColor;
	private int grayColor;
	private int blueColor;
	private int colorGreyInstrumentText;
	private int colorGreyInstrumentBackground;
	private int colorLightRedInstrumentBackground;
	private int colorLightRedInstrumentText;
	private int colorLightGreenInstrumentBackground;
	private int colorLightGreenInstrumentText;
	private int colorDarkRedInstrumentBackground;
	private int colorLDarkRedInstrumentText;
	private int colorLDarkGreenInstrumentBackground;
	private int colorDarktGreenInstrumentText;

	private static final int TYPE_ARTICLE = 0;
	private static final int TYPE_ARTICLE_OPINION = 1;
	private static final int TYPE_ARTICLE_EMPHASIZED = 2;
	private static final int TYPE_GROUP = -3;
	private static final int TYPE_INNERGROUP = -4;
	private static final int TYPE_INNERGROUP_SECTIONS = -5;
	private static final int TYPE_INSTRUMENT = -6;
	private static final int TYPE_STOCK = -7;
	// Share Object types...
	private static final int TYPE_SHARE_ANALYST_REC = -8;
	private static final int TYPE_SHARE_COMPANY_DESC = -9;
	private static final int TYPE_SHARE_DATA = -10;
	private static final int TYPE_SHARE_NEWS_ART = -11;

	private static final int TYPE_HEADER_INSTRUMENTS = 12;
	private static final int TYPE_HEADER_SHARES = 13;
	private static final int TYPE_HEADER_FUTURES = 14;
	private static final int TYPE_HEADER_FOREX = 15;

	private static final int TYPE_BANNER = 16;
	private static final int TYPE_PORTFOLIO_INSTRUMENT = -17;
	private static final int TYPE_DFP_AD_VIEW_BETWEEN_ARTICLES = -18;
	private static final int TYPE_DFP_AD_VIEW_KATAVA_PIRSUMIT_MAIN_LIST = -19;
	private static final int TYPE_DFP_AD_VIEW_KATAVA_PIRSUMIT_NADLAN = -20;
	private static final int TYPE_DFP_AD_VIEW_KATAVA_PIRSUMIT_MAIN_LIST_2 = -211;
	private static final int TYPE_DFP_AD_VIEW_KATAVA_PIRSUMIT_HOT_STORY = -222;
	
	private static int KATAVA_PIRSUMIT_TYPE_ID = -1;
	
	private static final int Ajillion_Obj_Width = 320;
	private static final int Ajillion_Obj_Height = 50;
	
	private static final int TYPE_ARTICLE_BIG = 21;
	private static final int TYPE_ARTICLE_DUNS_100 = 22;
	private static final int TYPE_COMPANY_DUNS_100 = 23;
	private static final int TYPE_GROUP_PORTFOLIO = -24;
	
	private static final int TYPE_LIVE_BOX = 25;
	private static final int TYPE_ARTICLE_BIG_GLOBES_TV = 26;
	private static final int TYPE_MAX_COUNT = TYPE_ARTICLE_BIG_GLOBES_TV + 1;

	
	private static final int HEADER_BANNER = 313;
	
	// Ticker object
	private Ticker looper;

	private Activity activity;

	private Vector<Object> newsSet;
	private static LayoutInflater inflater = null;
	public ImageLoader imageLoader;

	/** the TAG */
	private final String TAG = "LazyAdapter";

	/** alert dialog for looper Add/Remove */
	private AlertDialog alertDialog;
	private final static int DIALOG_TYPE_ADD_ITEM = 23;
	private final static int DIALOG_TYPE_REMOVE_ITEM = 24;
	private final static int DIALOG_TYPE_CANNOT_REMOVE_ITEM = 25;

	/* google analytics tracker */
	GoogleAnalyticsTracker tracker;
	Typeface almoni_aaa_regular, almoni_aaa_light, almoni_aaa_blod, almoni_aaa_black;

	// CallBack from Activity
	private GlobesListener mCallback;
	private MainFragment mainFragment;
	private ShareActivity shareActivity;
	private boolean doOnce;
	private int walla_adv_items_counter = 0;
	private int walla_adv_position = 0;
	private List walla_adv_items = new ArrayList();
	private List walla_adv_vertical_strip_items = new ArrayList();
	private List walla_adv_all_sponsored_articles_items = new ArrayList();
	private RelativeLayout walla_adv_current_frame;
	private boolean isSponsoredArticles = false;
	private boolean isTopHeaderBanner = false;
	private boolean isVerticalStripStoryBanner = false;
	private View rlSponsoredArticles;
	private int globalItemPosition = -1;
	
	private boolean isURLSaved = false;
	private String sWallaPage = "homepage";
	private String sWallaLayout = "l_globes_app_h";
	private HashMap<String, View> mapAllItems = new HashMap<String, View>();
	private boolean bAAA = false;
	
	private Map<String, Map<String, String>> globalMap;
		
	private String removeOneAndZero(String doc_id)
	{
		if (doc_id.length() > 1 && doc_id.startsWith("1"))
		{
			int howManyZeros = 0;
			for (int i = 1; i < doc_id.length(); i++)
			{
				if(doc_id.charAt(i) != '0')break;
				else  howManyZeros++;
			}
			return doc_id.substring(howManyZeros+1);
		}
		else
		{
			return doc_id;
		}
	}

	public LazyAdapter(Activity a, Vector<Object> groups)
	{
		activity = a;

		// callback
		try
		{
			mCallback = (GlobesListener) activity;
		}
		catch (ClassCastException e)
		{
			throw new ClassCastException(activity.toString() + " must implement " + GlobesListener.class.getSimpleName());
		}

		this.newsSet = groups;
		// inflater = (LayoutInflater)
		// activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater = a.getLayoutInflater();
		imageLoader = new ImageLoader(activity.getApplicationContext());
		looper = Ticker.getInstance(a.getApplicationContext());
		tracker = GoogleAnalyticsTracker.getInstance();
		pinkColor = activity.getResources().getColor(R.color.pink_globes);
		blackColor = activity.getResources().getColor(R.color.Black);
		grayColor = activity.getResources().getColor(R.color.grey_rate);
		blueColor = activity.getResources().getColor(R.color.blue_light);
		colorGreyInstrumentText = activity.getResources().getColor(R.color.grey_instrument_text);
		colorGreyInstrumentBackground = activity.getResources().getColor(R.color.grey_instrument_bg);

		colorLightRedInstrumentBackground = activity.getResources().getColor(R.color.light_red_instrument_bg);
		colorLightRedInstrumentText = activity.getResources().getColor(R.color.light_red_instrument_text);
		colorLightGreenInstrumentBackground = activity.getResources().getColor(R.color.light_green_instrument_bg);
		colorLightGreenInstrumentText = activity.getResources().getColor(R.color.light_green_instrument_text);
		colorDarkRedInstrumentBackground = activity.getResources().getColor(R.color.dark_red_instrument_bg);
		colorLDarkRedInstrumentText = activity.getResources().getColor(R.color.dark_red_instrument_text);
		colorLDarkGreenInstrumentBackground = activity.getResources().getColor(R.color.dark_green_instrument_bg);
		colorDarktGreenInstrumentText = activity.getResources().getColor(R.color.dark_green_instrument_text);

		almoni_aaa_regular = Typeface.createFromAsset(activity.getAssets(), "almoni-dl-aaa-regular.otf");
		almoni_aaa_blod = Typeface.createFromAsset(activity.getAssets(), "almoni-dl-aaa-bold.otf");
		
	}

	public int getCount()
	{
		//Log.e("alex", "Lazy count: " + newsSet.size());
		return newsSet.size();
	}

	public Object getItem(int position)
	{
		return position;
	}

	
	public long getItemId(int position)
	{
        Log.e("alex", "OOOOO getItemId:" + position);
		bAAA = true;
		return position;
	}

	@Override
	public int getViewTypeCount()
	{
		return TYPE_MAX_COUNT;
	}

	@Override
	public int getItemViewType(int position)
	{
		//Log.e("alex" , "ADDDDDDD 0: " + position + "===" +  newsSet.elementAt(position).getClass().getSimpleName());
		
		if(!isURLSaved)
		{
			isURLSaved = true;
			MainFragment mainFragment = LazyAdapter.this.getMainFragment();
			String parentURL = "";
			if (mainFragment != null) {
				parentURL = mainFragment.uriToParse;
			}
			if((!parentURL.equals("")))
			{
				String[] arrWallaData = Utils.getWallaDataForFolderL(parentURL);
				
				if(arrWallaData[0] != null && (!arrWallaData[0].equals("")))
				{
					sWallaPage = arrWallaData[0];
				}
				
				if(arrWallaData[1] != null && (!arrWallaData[1].equals("")))
				{
					sWallaLayout = arrWallaData[1];
				}
			}
			//Log.e("alex", "HHHHH Start LazyAdapter: " + parentURL);
		}
	
		if(newsSet.elementAt(position).getClass() == HeaderBanner.class)
		{
			return HEADER_BANNER;
		}
		
		
		if (newsSet.elementAt(position).getClass() == LiveBoxModel.class)
		{
			return TYPE_LIVE_BOX;
		}
		else if
		
		 (newsSet.elementAt(position).getClass() == Article.class)
		{
			Article article = ((Article) newsSet.elementAt(position));
			if (article.isBigArticle())
			{

				return article.getTagiot().size() > 0 ? TYPE_ARTICLE_BIG : -TYPE_ARTICLE_BIG;
			}
			else if (article.isBigTVArticle())
			{
				return article.getTagiot().size() > 0 ? TYPE_ARTICLE_BIG_GLOBES_TV : -TYPE_ARTICLE_BIG_GLOBES_TV;
			}
			else
			{
				return article.getTagiot().size() > 0 ? TYPE_ARTICLE : -TYPE_ARTICLE;

			}
		}
		else if (newsSet.elementAt(position).getClass() == ArticleOpinion.class)
		{
			ArticleOpinion article = ((ArticleOpinion) newsSet.elementAt(position));

			return article.getTagiot().size() > 0 ? TYPE_ARTICLE_OPINION : -TYPE_ARTICLE_OPINION;

		}
		else if (newsSet.elementAt(position).getClass() == ArticleEmphasized.class)
		{

			ArticleEmphasized article = ((ArticleEmphasized) newsSet.elementAt(position));

			return article.getTagiot().size() > 0 ? TYPE_ARTICLE_EMPHASIZED : -TYPE_ARTICLE_EMPHASIZED;

		}

		else if (newsSet.elementAt(position).getClass() == GroupPortfolio.class)
		{
			return TYPE_GROUP_PORTFOLIO;
		}

		else if (newsSet.elementAt(position).getClass() == Group.class)
		{
			return TYPE_GROUP;
		}
		else if (newsSet.elementAt(position).getClass() == InnerGroup.class)
		{
			return TYPE_INNERGROUP;
		}
		else if (newsSet.elementAt(position).getClass() == InnerGroupSections.class)
		{
			return TYPE_INNERGROUP_SECTIONS;
		}
		else if (newsSet.elementAt(position).getClass() == Instrument.class)
		{
			return TYPE_INSTRUMENT;
		}
		else if (newsSet.elementAt(position).getClass() == Stock.class)
		{
			return TYPE_STOCK;
		}
		else if (newsSet.elementAt(position).getClass() == ShareAnalystRecommendation.class)
		{
			return TYPE_SHARE_ANALYST_REC;
		}
		else if (newsSet.elementAt(position).getClass() == ShareCompanyDescription.class)
		{
			return TYPE_SHARE_COMPANY_DESC;
		}
		else if (newsSet.elementAt(position).getClass() == ShareData.class)
		{
			return TYPE_SHARE_DATA;
		}
		else if (newsSet.elementAt(position).getClass() == ShareNewsArticle.class)
		{
			return TYPE_SHARE_NEWS_ART;
		}
		else if (newsSet.elementAt(position).getClass() == HeaderInstruments.class)
		{
			return TYPE_HEADER_INSTRUMENTS;
		}
		else if (newsSet.elementAt(position).getClass() == HeaderShares.class)
		{
			return TYPE_HEADER_SHARES;
		}
		else if (newsSet.elementAt(position).getClass() == HeaderFutures.class)
		{
			return TYPE_HEADER_FUTURES;
		}
		else if (newsSet.elementAt(position).getClass() == HeaderForex.class)
		{
			return TYPE_HEADER_FOREX;
		}
		else if (newsSet.elementAt(position).getClass() == Banner.class)
		{
			return TYPE_BANNER;
		}
		else if (newsSet.elementAt(position).getClass() == PortfolioInstrument.class)
		{
			return TYPE_PORTFOLIO_INSTRUMENT;
		}
		else if (newsSet.elementAt(position).getClass() == DfpAdHolder.class)
		{
			DfpAdHolder adHolder = (DfpAdHolder) (newsSet.elementAt(position));
		    Log.e("alex", "dfpAdHandle111:" + position + "===" + adHolder.getAdType());
		    
		    //KATAVA_PIRSUMIT_TYPE_ID = adHolder.getAdType();
		    
			switch (adHolder.getAdType())
			{
				// katava pirsumit
				case DfpAdHolder.TYPE_DFP_KATAVA_PIRSUMIT_MAIN_LIST :
					KATAVA_PIRSUMIT_TYPE_ID = DfpAdHolder.TYPE_DFP_KATAVA_PIRSUMIT_MAIN_LIST;
					return TYPE_DFP_AD_VIEW_KATAVA_PIRSUMIT_MAIN_LIST;
				
					// katava pirsumit 2
				case DfpAdHolder.TYPE_DFP_KATAVA_PIRSUMIT_MAIN_LIST_2 :
					KATAVA_PIRSUMIT_TYPE_ID = DfpAdHolder.TYPE_DFP_KATAVA_PIRSUMIT_MAIN_LIST_2;
					return TYPE_DFP_AD_VIEW_KATAVA_PIRSUMIT_MAIN_LIST_2;
					
					// hot story
				case DfpAdHolder.TYPE_DFP_KATAVA_PIRSUMIT_HOT_STORY :
					KATAVA_PIRSUMIT_TYPE_ID = DfpAdHolder.TYPE_DFP_KATAVA_PIRSUMIT_HOT_STORY;
					return TYPE_DFP_AD_VIEW_KATAVA_PIRSUMIT_HOT_STORY;

					// regular banner between articles
				case DfpAdHolder.TYPE_DFP_MAIN_LIST_BETWEEN_ARTICLES :
					return TYPE_DFP_AD_VIEW_BETWEEN_ARTICLES;

					// regular banner between articles
				case DfpAdHolder.TYPE_DFP_KATAVA_PIRSUMIT_NADLAN :
					KATAVA_PIRSUMIT_TYPE_ID = DfpAdHolder.TYPE_DFP_KATAVA_PIRSUMIT_NADLAN;
					return TYPE_DFP_AD_VIEW_KATAVA_PIRSUMIT_NADLAN;
				default :
					break;
			}
		}
		else if (newsSet.elementAt(position).getClass() == RowArticleDuns100.class)
		{
			RowArticleDuns100 articleDuns100 = ((RowArticleDuns100) newsSet.elementAt(position));

			return articleDuns100.getTagiot().size() > 0 ? TYPE_ARTICLE_DUNS_100 : -TYPE_ARTICLE_DUNS_100;

		}
		else if (newsSet.elementAt(position).getClass() == RowCompanyDuns100.class)
		{
			return -TYPE_COMPANY_DUNS_100;

		}

		return position;
	}

	public static class ViewHolder
	{
		// test swipe//
		public Button swipe_button1, swipe_button2, swipe_button3;
		public LinearLayout LinearLayoutBack;

		// end test swipe
		public TextView text, textViewGroupTitle, textViewInnerGroupTitle, textViewBigArticle, textViewBigGlobesTVarticleTitle;
		public TextView createdOn;
		public TextView authorName;

		/* TextViews for instruments */
		public TextView instrumentLast;
		public TextView instrumentPercentageChange;

		/* TextViews for instruments in portfolios */
		public TextView pfPercentageChangeSinceAdded;

		/* TextViews for shares */
		public TextView textViewAnalystName;
		public TextView textViewRecommendationDate;
		public TextView textViewRecommendation;
		public TextView stockPercentageChange;

		public TextView textViewArticleDate;
		public TextView textViewArticleHeader;
		public TextView textViewShareSymbol;
		public TextView textViewTrueForDate;
		public TextView textViewPrecentageChangeInHeader;
		public TextView textViewLastPrice;
		public TextView layoutShareData;
		public TextView textViewPrecentageChange;
		public TextView textViewDailyPointsChange;
		public TextView textViewShareVolume;
		public TextView textViewDailyHigh;
		public TextView textViewDailyLow;
		public TextView textViewShareMarketCap;

		public ImageView image, imageViewBigArticle, imageViewBigGlobesTVArticle;
		public ImageView imageViewUpDownArrow, imageAfterTitle;

		public Button buttonShareVideo;
		public Button buttonStockAddCncl;

		// currently for katava pisrsumit in main list
		// public DfpAdView AdViewKatavaPirsumitMainList;
		public PublisherAdView AdViewKatavaPirsumitMainListPublisher;

		// currently for katava pisrsumit in Nadaln list
		// public DfpAdView AdViewKatavaPirsumitNadlan;
		public PublisherAdView AdViewKatavaPirsumitNadlanPublisher;

		// destined for ad unit
		// public DfpAdView adViewBetweenArticles;
		public PublisherAdView adViewBetweenArticlesPublisher;

		/* ShareList checkBox AddtoLooper */
		public CheckBox checkBoxAddToLooperFromSharesList;

		/* MarketList checkBox AddtoLooper */
		public CheckBox checkBoxAddToLooperFromMarketsList;

		/* Portfolio checkBox AddtoLooper */
		public CheckBox checkBoxAddToLooperFromPortfolioList;
		
		
		public WebView webView_live_box;
		
		public ImageView imgPromoIcon;
	}

	public View getView(final int position, View convertView, ViewGroup parent)
	{
		//		NewsSet dummyNewSet = new NewsSet();
		//		dummyNewSet.itemHolder.addAll(newsSet);
		//		DataContext.Instance().SetArticleList(UUID.randomUUID().toString(), dummyNewSet );
		//		Log.e("eli", getItemViewType(position)+"");
		// eli importent
		// View rowView = convertView;
		
		//if(bAAA){return convertView;}
		
		View rowView = null;
		// final ViewHolder holder;	
						
		Log.e("alex", "HHHHH GetView:" + position + "===" + getItemViewType(position));
		
		if (convertView == null)
		{			
			if (getItemViewType(position) == TYPE_ARTICLE_DUNS_100 || getItemViewType(position) == -TYPE_ARTICLE_DUNS_100)
			{
				rowView = inflater.inflate(R.layout.row_layout_new, null);
				final ViewHolder holder = new ViewHolder();
				// title
				holder.text = (TextView) rowView.findViewById(R.id.title);
				// image
				holder.image = (ImageView) rowView.findViewById(R.id.image);
				// TODO add on reuse and other aticle states created on
				holder.createdOn = (TextView) rowView.findViewById(R.id.createdOn);
				holder.LinearLayoutBack = (LinearLayout) rowView.findViewById(R.id.contenair_tagiot);
				rowView.setTag(holder);
				holder.LinearLayoutBack.setTag((RowArticleDuns100) newsSet.elementAt(position));
			}
			else if (getItemViewType(position) == -TYPE_COMPANY_DUNS_100)
			{
				rowView = inflater.inflate(R.layout.row_layout_new, null);
				final ViewHolder holder = new ViewHolder();
				// title
				holder.text = (TextView) rowView.findViewById(R.id.title);
				// image
				holder.image = (ImageView) rowView.findViewById(R.id.image);
				// TODO add on reuse and other aticle states created on
				holder.createdOn = (TextView) rowView.findViewById(R.id.createdOn);
				rowView.setTag(holder);
			}
			// TODO eli
			else if (getItemViewType(position) == TYPE_GROUP)
			{
				rowView = inflater.inflate(R.layout.row_layout_group, null);
				final ViewHolder holder = new ViewHolder();
				holder.textViewGroupTitle = (TextView) rowView.findViewById(R.id.grouptitle);
				rowView.setTag(holder);
			}

			else if (getItemViewType(position) == TYPE_INNERGROUP)
			{
				rowView = inflater.inflate(R.layout.row_layout_inner_group, null);
				final ViewHolder holder = new ViewHolder();
				holder.textViewInnerGroupTitle = (TextView) rowView.findViewById(R.id.grouptitle);
				rowView.setTag(holder);
			}
			//
			else if (getItemViewType(position) == TYPE_ARTICLE)
			{
				rowView = inflater.inflate(R.layout.row_layout_new, null);
				final ViewHolder holder = new ViewHolder();
				//				holder.authorName = (TextView) rowView.findViewById(R.id.authorName);
				// title
				holder.text = (TextView) rowView.findViewById(R.id.title);
				// image
				holder.image = (ImageView) rowView.findViewById(R.id.image);
				holder.imageAfterTitle = (ImageView) rowView.findViewById(R.id.imageAfterTitle);
				// TODO add on reuse and other aticle states created on
				holder.createdOn = (TextView) rowView.findViewById(R.id.createdOn);
				holder.LinearLayoutBack = (LinearLayout) rowView.findViewById(R.id.contenair_tagiot);
				holder.buttonShareVideo = (Button) rowView.findViewById(R.id.buttonShareVideo);
				//holder.imageViewFirstToTell = (ImageView) rowView.findViewById(R.id.ImageView_first_to_tell);
				
				holder.imgPromoIcon = (ImageView) rowView.findViewById(R.id.imgPromoIcon);
				
				rowView.setTag(holder);
				holder.LinearLayoutBack.setTag((Article) newsSet.elementAt(position));
	
			}

			else if (getItemViewType(position) == -TYPE_ARTICLE)
			{
				rowView = inflater.inflate(R.layout.row_layout_new, null);
				final ViewHolder holder = new ViewHolder();
				// title
				holder.text = (TextView) rowView.findViewById(R.id.title);
				// image
				holder.image = (ImageView) rowView.findViewById(R.id.image);
				// TODO add on reuse and other aticle states created on
				holder.createdOn = (TextView) rowView.findViewById(R.id.createdOn);
				holder.LinearLayoutBack = (LinearLayout) rowView.findViewById(R.id.contenair_tagiot);
				holder.buttonShareVideo = (Button) rowView.findViewById(R.id.buttonShareVideo);
				//	holder.imageViewFirstToTell = (ImageView) rowView.findViewById(R.id.ImageView_first_to_tell);
				rowView.setTag(holder);
				holder.LinearLayoutBack.setTag((Article) newsSet.elementAt(position));
			}
			// Big Article
			else if (getItemViewType(position) == TYPE_ARTICLE_BIG)
			{
				rowView = inflater.inflate(R.layout.row_layout_big_article, null);
				final ViewHolder holder = new ViewHolder();
				holder.textViewBigArticle = (TextView) rowView.findViewById(R.id.textView_big_article_title);
				holder.imageViewBigArticle = (ImageView) rowView.findViewById(R.id.imageView_big_article);
				holder.buttonShareVideo = (Button) rowView.findViewById(R.id.buttonShareVideo);
				holder.LinearLayoutBack = (LinearLayout) rowView.findViewById(R.id.contenair_tagiot);
				rowView.setTag(holder);
				holder.LinearLayoutBack.setTag((Article) newsSet.elementAt(position));
			}
			// Big Article
			else if (getItemViewType(position) == -TYPE_ARTICLE_BIG)
			{
				rowView = inflater.inflate(R.layout.row_layout_big_article, null);
				final ViewHolder holder = new ViewHolder();
				holder.textViewBigArticle = (TextView) rowView.findViewById(R.id.textView_big_article_title);
				holder.imageViewBigArticle = (ImageView) rowView.findViewById(R.id.imageView_big_article);
				holder.buttonShareVideo = (Button) rowView.findViewById(R.id.buttonShareVideo);
				holder.LinearLayoutBack = (LinearLayout) rowView.findViewById(R.id.contenair_tagiot);
				rowView.setTag(holder);
				holder.LinearLayoutBack.setTag((Article) newsSet.elementAt(position));
			}
			// Big Globes TV article
			else if (getItemViewType(position) == TYPE_ARTICLE_BIG_GLOBES_TV)
			{
				rowView = inflater.inflate(R.layout.row_layout_big_articel_globes_tv, null);
				final ViewHolder holder = new ViewHolder();
				holder.textViewBigGlobesTVarticleTitle = (TextView) rowView.findViewById(R.id.textView_big_article_title_globes_tv);
				holder.imageViewBigGlobesTVArticle = (ImageView) rowView.findViewById(R.id.imageView_big_article_globes_tv);
				holder.LinearLayoutBack = (LinearLayout) rowView.findViewById(R.id.contenair_tagiot);
				holder.buttonShareVideo = (Button)rowView.findViewById(R.id.face_btn_logo_transperent);
				rowView.setTag(holder);
				holder.LinearLayoutBack.setTag((Article) newsSet.elementAt(position));
			}

			else if (getItemViewType(position) == -TYPE_ARTICLE_BIG_GLOBES_TV)
			{

				rowView = inflater.inflate(R.layout.row_layout_big_articel_globes_tv, null);
				final ViewHolder holder = new ViewHolder();
				holder.textViewBigGlobesTVarticleTitle = (TextView) rowView.findViewById(R.id.textView_big_article_title_globes_tv);
				holder.imageViewBigGlobesTVArticle = (ImageView) rowView.findViewById(R.id.imageView_big_article_globes_tv);
				holder.LinearLayoutBack = (LinearLayout) rowView.findViewById(R.id.contenair_tagiot);
				holder.buttonShareVideo = (Button)rowView.findViewById(R.id.face_btn_logo_transperent);

				rowView.setTag(holder);
				holder.LinearLayoutBack.setTag((Article) newsSet.elementAt(position));

			}

			else if (getItemViewType(position) == TYPE_ARTICLE_OPINION)
			{				
				rowView = inflater.inflate(R.layout.row_layout_new, null);
				final ViewHolder holder = new ViewHolder();
				holder.text = (TextView) rowView.findViewById(R.id.title);
				holder.authorName = (TextView) rowView.findViewById(R.id.authorName);
				holder.image = (ImageView) rowView.findViewById(R.id.image);
				holder.createdOn = (TextView) rowView.findViewById(R.id.createdOn);
				holder.LinearLayoutBack = (LinearLayout) rowView.findViewById(R.id.contenair_tagiot);
				//	holder.imageViewFirstToTell = (ImageView) rowView.findViewById(R.id.ImageView_first_to_tell);
				rowView.setTag(holder);
				holder.LinearLayoutBack.setTag((ArticleOpinion) newsSet.elementAt(position));
			}
			else if (getItemViewType(position) == -TYPE_ARTICLE_OPINION)
			{
				//Log.e("alex", "DEOT");
				
				rowView = inflater.inflate(R.layout.row_layout_new, null);
				final ViewHolder holder = new ViewHolder();
				holder.text = (TextView) rowView.findViewById(R.id.title);
				holder.authorName = (TextView) rowView.findViewById(R.id.authorName);
				holder.image = (ImageView) rowView.findViewById(R.id.image);
				holder.createdOn = (TextView) rowView.findViewById(R.id.createdOn);
				holder.LinearLayoutBack = (LinearLayout) rowView.findViewById(R.id.contenair_tagiot);
				//	holder.imageViewFirstToTell = (ImageView) rowView.findViewById(R.id.ImageView_first_to_tell);
				rowView.setTag(holder);
				holder.LinearLayoutBack.setTag((ArticleOpinion) newsSet.elementAt(position));
			}
			else if (getItemViewType(position) == TYPE_ARTICLE_EMPHASIZED)
			{
				// TODO regular article
				// TODO big article
				rowView = inflater.inflate(R.layout.row_layout_emphasized, null);
				final ViewHolder holder = new ViewHolder();
				holder.text = (TextView) rowView.findViewById(R.id.title);
				holder.image = (ImageView) rowView.findViewById(R.id.image);
				holder.buttonShareVideo = (Button) rowView.findViewById(R.id.buttonShareVideo);
				holder.LinearLayoutBack = (LinearLayout) rowView.findViewById(R.id.contenair_tagiot);
				//	holder.imageViewFirstToTell = (ImageView) rowView.findViewById(R.id.ImageView_first_to_tell);
				rowView.setTag(holder);
				holder.LinearLayoutBack.setTag((Article) newsSet.elementAt(position));
			}

			else if (getItemViewType(position) == -TYPE_ARTICLE_EMPHASIZED)
			{
				// TODO regular article
				// TODO big article

				rowView = inflater.inflate(R.layout.row_layout_emphasized, null);
				final ViewHolder holder = new ViewHolder();
				holder.text = (TextView) rowView.findViewById(R.id.title);
				holder.image = (ImageView) rowView.findViewById(R.id.image);
				holder.buttonShareVideo = (Button) rowView.findViewById(R.id.buttonShareVideo);
				holder.LinearLayoutBack = (LinearLayout) rowView.findViewById(R.id.contenair_tagiot);
				//	holder.imageViewFirstToTell = (ImageView) rowView.findViewById(R.id.ImageView_first_to_tell);

				rowView.setTag(holder);
				holder.LinearLayoutBack.setTag((Article) newsSet.elementAt(position));

			}

			else if (getItemViewType(position) == TYPE_GROUP_PORTFOLIO)
			{
				rowView = inflater.inflate(R.layout.row_layout_group, null);
				final ViewHolder holder = new ViewHolder();
				holder.textViewGroupTitle = (TextView) rowView.findViewById(R.id.grouptitle);

				rowView.setTag(holder);
			}
			else if (getItemViewType(position) == TYPE_LIVE_BOX)
			{
				rowView = inflater.inflate(R.layout.row_layout_live_box, null);
				final ViewHolder holder = new ViewHolder();
				
				
				holder.webView_live_box = (WebView) rowView.findViewById(R.id.webView_live_box);
				rowView.setTag(holder);
			}
			else if (getItemViewType(position) == TYPE_GROUP)
			{
				rowView = inflater.inflate(R.layout.row_layout_group, null);
				final ViewHolder holder = new ViewHolder();
				holder.textViewGroupTitle = (TextView) rowView.findViewById(R.id.grouptitle);
				rowView.setTag(holder);
			}
			else if (getItemViewType(position) == TYPE_INNERGROUP)
			{
				rowView = inflater.inflate(R.layout.row_layout_inner_group, null);
				final ViewHolder holder = new ViewHolder();
				holder.textViewInnerGroupTitle = (TextView) rowView.findViewById(R.id.grouptitle);
				rowView.setTag(holder);
			}
			else if (getItemViewType(position) == TYPE_INNERGROUP_SECTIONS)
			{
				rowView = inflater.inflate(R.layout.row_layout_inner_group_white, null);
				final ViewHolder holder = new ViewHolder();
				holder.text = (TextView) rowView.findViewById(R.id.grouptitle);
				rowView.setTag(holder);
			}
			else if (getItemViewType(position) == TYPE_INSTRUMENT)
			{
				rowView = inflater.inflate(R.layout.row_layout_instrument, null);
				final ViewHolder holder = new ViewHolder();
				holder.text = (TextView) rowView.findViewById(R.id.isntrumentText);
				holder.instrumentLast = (TextView) rowView.findViewById(R.id.instrumentLast);
				holder.instrumentPercentageChange = (TextView) rowView.findViewById(R.id.isntrumentPercentageChange);
				holder.image = (ImageView) rowView.findViewById(R.id.instrumentImage);

				holder.checkBoxAddToLooperFromMarketsList = (CheckBox) rowView.findViewById(R.id.checkBoxAddToLooperFromMarketsList);
				holder.checkBoxAddToLooperFromMarketsList.setTag(((Instrument) newsSet.elementAt(position)));
				holder.checkBoxAddToLooperFromMarketsList.setOnCheckedChangeListener(new OnCheckedChangeListener()
				{

					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
					{
						Instrument instrument = ((Instrument) holder.checkBoxAddToLooperFromMarketsList.getTag());
						if (isChecked)
						{
							if (!looper.isInLooper(instrument))
							{
								looper.addLooperItem(instrument);
								showDialog(DIALOG_TYPE_ADD_ITEM, instrument.getHe());
							}
						}
						else
						{
							if (looper.isInLooper(instrument))
							{
								// minimun items in looper is 3
								if (looper.getLooperItemsCount() == Ticker.MIN_LOOPER_ITEMS)
								{
									showDialogCannotRemoveItem();
								}
								else
								{
									looper.removeLooperItem(instrument);
									showDialog(DIALOG_TYPE_REMOVE_ITEM, instrument.getHe());
								}
							}
						}
					}
				});

				rowView.setTag(holder);
			}
			else if (getItemViewType(position) == TYPE_STOCK)
			{
				// eli STOCK from search
				rowView = inflater.inflate(R.layout.row_layout_stock, null);
				final ViewHolder holder = new ViewHolder();
				holder.text = (TextView) rowView.findViewById(R.id.stockText);
				holder.stockPercentageChange = (TextView) rowView.findViewById(R.id.stockPercentageChange);
				holder.image = (ImageView) rowView.findViewById(R.id.stockChangeImage);
				// holder.checkBoxAddToLooperFromSharesList = (CheckBox)
				// rowView.findViewById(R.id.checkBoxAddToLooperFromSharesList);
				// holder.checkBoxAddToLooperFromSharesList.setTag(((Stock)
				// newsSet.elementAt(position)));
				// holder.checkBoxAddToLooperFromSharesList.setOnCheckedChangeListener(new
				// OnCheckedChangeListener()
				// {
				//
				// public void onCheckedChanged(CompoundButton buttonView,
				// boolean isChecked)
				// {
				// Stock stock = ((Stock)
				// holder.checkBoxAddToLooperFromSharesList.getTag());
				// if (isChecked)
				// {
				// if (!looper.isInLooper(stock))
				// {
				//
				// looper.addLooperItem(stock);
				// showDialog(DIALOG_TYPE_ADD_ITEM, stock.getName_he());
				// }
				// }
				// else
				// {
				// if (looper.isInLooper(stock))
				// {
				// // minimun items in looper is 3
				// if (looper.getLooperItemsCount() == Ticker.MIN_LOOPER_ITEMS)
				// {
				// showDialogCannotRemoveItem();
				// }
				// else
				// {
				// looper.removeLooperItem(stock);
				// showDialog(DIALOG_TYPE_REMOVE_ITEM, stock.getName_he());
				// }
				// }
				// }
				//
				// }
				// });

				rowView.setTag(holder);
			}
			else if (getItemViewType(position) == TYPE_SHARE_ANALYST_REC)
			{
				rowView = inflater.inflate(R.layout.row_layout_share_analyst_rec, null);
				final ViewHolder holder = new ViewHolder();
				holder.textViewAnalystName = (TextView) rowView.findViewById(R.id.textViewAnalystName);
				holder.textViewRecommendationDate = (TextView) rowView.findViewById(R.id.textViewRecommendationDate);
				holder.textViewRecommendation = (TextView) rowView.findViewById(R.id.textViewRecommendation);
				rowView.setTag(holder);
			}
			else if (getItemViewType(position) == TYPE_SHARE_COMPANY_DESC)
			{
				rowView = inflater.inflate(R.layout.row_layout_share_company_desc, null);
				final ViewHolder holder = new ViewHolder();
				holder.text = (TextView) rowView.findViewById(R.id.text);
				rowView.setTag(holder);
			}

			// else if(getItemViewType(position)==TYPE_SHARE_DATA)
			// {
			// rowView = inflater.inflate(R.layout.row_layout_share_data, null);
			// holder=new ViewHolder();
			// // holder.textViewShareSymbol =
			// (TextView)rowView.findViewById(R.id.textViewShareSymbol);
			// // holder.textViewTrueForDate =
			// (TextView)rowView.findViewById(R.id.textViewTrueForDate);
			// // holder.textViewPrecentageChangeInHeader =
			// (TextView)rowView.findViewById(R.id.textViewPrecentageChangeInHeader);
			// // holder.textViewLastPrice =
			// (TextView)rowView.findViewById(R.id.textViewLastPrice);
			// // holder.textViewPrecentageChange =
			// (TextView)rowView.findViewById(R.id.textViewPrecentageChange);
			// // holder.textViewDailyPointsChange =
			// (TextView)rowView.findViewById(R.id.textViewDailyPointsChange);
			// // holder.textViewShareVolume =
			// (TextView)rowView.findViewById(R.id.textViewShareVolume);
			// // holder.textViewDailyHigh =
			// (TextView)rowView.findViewById(R.id.textViewDailyHigh);
			// // holder.textViewDailyLow =
			// (TextView)rowView.findViewById(R.id.textViewDailyLow);
			// // holder.textViewShareMarketCap =
			// (TextView)rowView.findViewById(R.id.textViewShareMarketCap);
			// //
			// // holder.imageViewUpDownArrow = (ImageView)rowView
			// .findViewById(R.id.imageViewUpDownArrow);
			// // holder.image = (ImageView)rowView
			// .findViewById(R.id.imageViewGraph);
			// //
			// // holder.buttonStockAddCncl =
			// (Button)rowView.findViewById(R.id.buttonStockAddCncl);
			//
			// //rowView.setTag(holder);
			// }

			else if (getItemViewType(position) == TYPE_SHARE_NEWS_ART)
			{
				rowView = inflater.inflate(R.layout.row_layout_share_news_art, null);
				final ViewHolder holder = new ViewHolder();
				holder.textViewArticleDate = (TextView) rowView.findViewById(R.id.textViewArticleDate);
				holder.textViewArticleHeader = (TextView) rowView.findViewById(R.id.textViewArticleHeader);
				rowView.setTag(holder);
			}
			else if (getItemViewType(position) == TYPE_HEADER_INSTRUMENTS)
			{
				rowView = inflater.inflate(R.layout.row_layout_header_instruments, null);
				final ViewHolder holder = new ViewHolder();
				holder.text = (TextView) rowView.findViewById(R.id.isntrumentText);
				holder.instrumentLast = (TextView) rowView.findViewById(R.id.instrumentLast);
				holder.instrumentPercentageChange = (TextView) rowView.findViewById(R.id.isntrumentPercentageChange);
				rowView.setTag(holder);
			}
			else if (getItemViewType(position) == TYPE_HEADER_SHARES)
			{
				rowView = inflater.inflate(R.layout.row_layout_header_shares, null);
				final ViewHolder holder = new ViewHolder();
				holder.text = (TextView) rowView.findViewById(R.id.isntrumentText);
				holder.instrumentLast = (TextView) rowView.findViewById(R.id.instrumentLast);
				holder.instrumentPercentageChange = (TextView) rowView.findViewById(R.id.isntrumentPercentageChange);
				rowView.setTag(holder);
			}

			else if (getItemViewType(position) == TYPE_HEADER_FUTURES)
			{
				rowView = inflater.inflate(R.layout.row_layout_header_futures, null);
				final ViewHolder holder = new ViewHolder();
				holder.text = (TextView) rowView.findViewById(R.id.isntrumentText);
				holder.instrumentLast = (TextView) rowView.findViewById(R.id.instrumentLast);
				holder.instrumentPercentageChange = (TextView) rowView.findViewById(R.id.isntrumentPercentageChange);
				rowView.setTag(holder);
			}
			else if (getItemViewType(position) == TYPE_HEADER_FOREX)
			{
				rowView = inflater.inflate(R.layout.row_layout_header_forex, null);
				final ViewHolder holder = new ViewHolder();
				holder.text = (TextView) rowView.findViewById(R.id.isntrumentText);
				holder.instrumentLast = (TextView) rowView.findViewById(R.id.instrumentLast);
				holder.instrumentPercentageChange = (TextView) rowView.findViewById(R.id.isntrumentPercentageChange);
				rowView.setTag(holder);
			}
			else if (getItemViewType(position) == TYPE_PORTFOLIO_INSTRUMENT)
			{
				// eli - screen of portfolio
				//				Log.e("eli", "get view screen of portfolio");
				rowView = inflater.inflate(R.layout.a_test_row_layout_portfolio, null);
				final ViewHolder holder = new ViewHolder();
				holder.text = (TextView) rowView.findViewById(R.id.pfInstrumentText);
				holder.instrumentLast = (TextView) rowView.findViewById(R.id.pfInstrumentLast);
				holder.instrumentPercentageChange = (TextView) rowView.findViewById(R.id.pfInstrumentPercentageChange);
				holder.pfPercentageChangeSinceAdded = (TextView) rowView.findViewById(R.id.pfPercentageChangeSinceAdded);
				holder.checkBoxAddToLooperFromPortfolioList = (CheckBox) rowView.findViewById(R.id.checkBoxAddToLooperFromPortfolioList);
				holder.checkBoxAddToLooperFromPortfolioList.setTag(((PortfolioInstrument) newsSet.elementAt(position)));
				holder.checkBoxAddToLooperFromPortfolioList.setOnCheckedChangeListener(new OnCheckedChangeListener()
				{

					@Override
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
					{
						PortfolioInstrument portfolioInstrument = ((PortfolioInstrument) holder.checkBoxAddToLooperFromPortfolioList
								.getTag());
						if (isChecked)
						{
							if (!looper.isInLooper(portfolioInstrument))
							{
								looper.addLooperItem(portfolioInstrument);
								showDialog(DIALOG_TYPE_ADD_ITEM, portfolioInstrument.getHe());
							}
						}
						else
						{
							if (looper.isInLooper(portfolioInstrument))
							{
								// minimun items in looper is 3
								if (looper.getLooperItemsCount() == Ticker.MIN_LOOPER_ITEMS)
								{
									showDialogCannotRemoveItem();
								}
								else
								{
									looper.removeLooperItem(portfolioInstrument);
									showDialog(DIALOG_TYPE_REMOVE_ITEM, portfolioInstrument.getHe());
								}
							}
						}
					}
				});
				rowView.setTag(holder);
			}
			// regular banner between articles
			else if (getItemViewType(position) == TYPE_DFP_AD_VIEW_BETWEEN_ARTICLES)
			{			
				Log.e("alex", "HHHHH TYPE_DFP_AD_VIEW_BETWEEN_ARTICLES: " + position);
				
				rowView = inflater.inflate(R.layout.row_layout_dfp_ad_view, null);
				RelativeLayout frameLayout = (RelativeLayout) rowView.findViewById(R.id.row_layout_dfp_adview_frame_layout_container);
				frameLayout.setGravity(Gravity.CENTER);
				//				frameLayout.setBackgroundColor(Color.RED);

				if(Definitions.wallafusion)
				{
					/////////////////////WALLA TEST///////////////////////////////////
					
					//isSponsoredArticles = false;
					isTopHeaderBanner = false;
					isVerticalStripStoryBanner = false;
					walla_adv_current_frame = frameLayout;
					
//					if(position == 0 && isTopHeaderBanner){
//						isTopHeaderBanner = false;
//						globalItemPosition = -1;
//						Log.e("alex", "HHHHH CHANGE!!!");
//					}					
					
					Log.e("alex", "HHHHH Positions: " + globalItemPosition + "===" + position + "=== isTopHeaderBanner: " + isTopHeaderBanner);
										
					WallaAdvParser wap;
					
					if(globalItemPosition != position)
					{
						if(position != 11)
						{
							globalItemPosition = position;						
						}
						
						if(position == 0)
						{
							Log.e("alex", "HHHHH TopHeaderBanner globalItemPosition: " + position);
							
							isTopHeaderBanner = true;							
							wap = new WallaAdvParser(activity.getApplicationContext(), activity.getApplication(), this, sWallaPage, sWallaLayout, Definitions.WALLA_SPACE_TOP_FLOAT, position);
							wap.execute();
						}
						else if(position == 11) //WALLA_SPACE_VERTICAL_STRIP_STORY
						{
							//Log.e("alex", "HHHHH isVerticalStripStoryBanner: " + position);
							
							isVerticalStripStoryBanner = true;
							String space11 = Definitions.WALLA_SPACE_VERTICAL_STRIP_STORY;
							if (globalMap != null) {
								// avoid exception if globalMap is not ready
								Map<String, String> m = globalMap.get(space11);
								WallaAdvParser.drawWallaBanner(walla_adv_current_frame, activity.getApplicationContext(), m);
							}
							//wap = new WallaAdvParser(activity.getApplicationContext(), activity.getApplication(), this, sWallaPage, sWallaLayout, Definitions.WALLA_SPACE_VERTICAL_STRIP_STORY, position);
							//wap.execute();
						}
						else
						{
						    //isTopHeaderBanner = false;						    
						    
						    if(!walla_adv_vertical_strip_items.contains(position))
							{
						    	walla_adv_vertical_strip_items.add(position);
							}
						    
						    //Log.e("alex", "WALLA_SPACE_VERTICAL_STRIP position: " + position + "=== index: " +  walla_adv_vertical_strip_items.indexOf(position));
						    
						    int iIndexOfVerticalStripBanner = walla_adv_vertical_strip_items.indexOf(position);
						    
							//wap = new WallaAdvParser(activity.getApplicationContext(), activity.getApplication(),this, sWallaPage, sWallaLayout, Definitions.WALLA_SPACE_VERTICAL_STRIP, position);
						    wap = new WallaAdvParser(activity.getApplicationContext(), activity.getApplication(),this, sWallaPage, sWallaLayout, Definitions.WALLA_SPACE_VERTICAL_STRIP, iIndexOfVerticalStripBanner);
						    wap.execute();
						}
						
						Log.e("alex", "HHHHH Katava Pirsumit 3: " + position);
						
						
					}
					else if(globalItemPosition == 0 && position == 0 && !isTopHeaderBanner)
					{
						Log.e("alex", "HHHHH CHANGE: " + isTopHeaderBanner);
						isTopHeaderBanner = true;							
						wap = new WallaAdvParser(activity.getApplicationContext(), activity.getApplication(), this, sWallaPage, sWallaLayout, Definitions.WALLA_SPACE_TOP_FLOAT, position);
						wap.execute();
					}
				}
				else
				{
					
					
					
					if (!Definitions.toUseAjillion)
					{						
						final ViewHolder holder = new ViewHolder();
						DfpAdHolder adHolder = ((DfpAdHolder) (newsSet.elementAt(position)));

						MainSlidingActivity msa = (MainSlidingActivity)mCallback;
						String spaceName = (msa.getIsMainPageLoading()) ? Definitions.ADUNIT_MAIN_LIST_BETWEEN_ARTICELS_HP : adHolder.getAdUnitID();
						if(position == 27) {spaceName = "test";}
						Log.e("alex", "DDFFPP START..." + position + "=== unitID:" + spaceName + "=== AdSize: " + adHolder.getAdSize());
									
						holder.adViewBetweenArticlesPublisher = new PublisherAdView(activity);
						//holder.adViewBetweenArticlesPublisher.setAdUnitId(adHolder.getAdUnitID());
						//holder.adViewBetweenArticlesPublisher.setAdSizes(adHolder.getAdSize());


						if(position == 11)
						{
							spaceName = Definitions.dfp_strip_vertical;
							//holder.adViewBetweenArticlesPublisher.setAdSizes(AdSize.SMART_BANNER);
							holder.adViewBetweenArticlesPublisher.setAdSizes(new AdSize(320, 50),new AdSize(360, 50), AdSize.BANNER);
						}
						else
						{
							holder.adViewBetweenArticlesPublisher.setAdSizes(new AdSize(320, 170), new AdSize(320, 50));
							//holder.adViewBetweenArticlesPublisher.setAdSizes(new AdSize(320, 50),new AdSize(360, 50), new AdSize(320, 170));

							//holder.adViewBetweenArticlesPublisher.setAdSizes(AdSize.BANNER, new AdSize(320, 170));
							//holder.adViewBetweenArticlesPublisher.setAdSizes(new AdSize(360, 50), new AdSize(360, 170));

							//holder.adViewBetweenArticlesPublisher.setAdSizes(new AdSize(360, 50));

							//holder.adViewBetweenArticlesPublisher.setAdSizes(new AdSize(360, 50), new AdSize(360, 170));
						}

						holder.adViewBetweenArticlesPublisher.setAdUnitId(spaceName);

						//holder.adViewBetweenArticlesPublisher.setAdSizes(adHolder.getAdSize(),new AdSize(320, 50), new AdSize(320, 170), AdSize.SMART_BANNER);
						//holder.adViewBetweenArticlesPublisher.setAdSizes(new AdSize(360, 170), new AdSize(360, 50));
						//holder.adViewBetweenArticlesPublisher.setAdSizes(AdSize.SMART_BANNER);

						//frameLayout.addView(holder.adViewBetweenArticlesPublisher, RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

						frameLayout.addView(holder.adViewBetweenArticlesPublisher, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

						//frameLayout.setBackgroundColor(Color.RED);
												
						holder.adViewBetweenArticlesPublisher.setAdListener(new AdListener()
						{
							public void onAdLoaded()
							{
								Log.e("alex", "Lazy Adapter DDFFPP onAdLoaded()" +  holder.adViewBetweenArticlesPublisher.getAdSize() + " uid:" + holder.adViewBetweenArticlesPublisher.getAdUnitId());

								holder.adViewBetweenArticlesPublisher.setVisibility(View.VISIBLE);
							}
							public void onAdFailedToLoad(int errorCode)
							{
								Log.e("alex", "Lazy Adapter DDFFPP onAdFailedToLoad()" +  holder.adViewBetweenArticlesPublisher.getAdSize() + " uid:" + holder.adViewBetweenArticlesPublisher.getAdUnitId() + " Error:" + errorCode);

								holder.adViewBetweenArticlesPublisher.setVisibility(View.GONE);
							}
						});
						rowView.setTag(holder);

						PublisherAdRequest request = new PublisherAdRequest.Builder().build();
						holder.adViewBetweenArticlesPublisher.loadAd(request);
					}
					else
					{
						new initAjillionView(frameLayout, Definitions.AJILLION_AD_ID_INNER_BANNER).execute();
					}
				}
			}
			else if (getItemViewType(position) == TYPE_DFP_AD_VIEW_KATAVA_PIRSUMIT_NADLAN)
			{
				final ViewHolder holder = new ViewHolder();
				holder.AdViewKatavaPirsumitNadlanPublisher = new PublisherAdView(activity);
				final DfpAdHolder adHolder = ((DfpAdHolder) (newsSet.elementAt(position)));
				rowView = inflater.inflate(R.layout.row_layout_dfp_ad_view, null);
				final RelativeLayout frameLayout = (RelativeLayout) rowView.findViewById(R.id.row_layout_dfp_adview_frame_layout_container);
				AdLoader adLoader = new AdLoader.Builder(activity.getApplicationContext(), adHolder.getAdUnitID())  // /6499/example/native" === 10063170
						.forCustomTemplateAd(Definitions.dfp_articles_template_id,
								new NativeCustomTemplateAd.OnCustomTemplateAdLoadedListener() {
									@Override
									public void onCustomTemplateAdLoaded(final NativeCustomTemplateAd ad) {

										View rowView = drawDFPSponsoredArticle(ad);

//										List<String> lst = ad.getAvailableAssetNames();
//
//										for (String s:lst) {
//											Log.e("alex", "onCustomTemplateAdLoaded!!! Field: " + s + "===" + ad.getText(s));
//										}
//
//
//										View rowView = inflater.inflate(R.layout.row_layout_new, null);
//
//										holder.text = (TextView) rowView.findViewById(R.id.title);
//										holder.image = (ImageView) rowView.findViewById(R.id.image);
//										holder.createdOn = (TextView) rowView.findViewById(R.id.createdOn);
//										rowView.setTag(holder);
//
//										holder.text.setText(ad.getText("headline"));
//										holder.text.setTypeface(almoni_aaa_regular);
//										holder.text.setTextSize(22.5f);
//										holder.text.setTextColor(Color.BLACK);
//										holder.createdOn.setText("");
//
//										holder.image.setImageDrawable(ad.getImage("MainImage").getDrawable());
//
//										holder.image.setOnClickListener(new View.OnClickListener() {
//											@Override
//											public void onClick(View v) {
//												ad.performClick("MainImage");
//											}
//										});
//
//										holder.text.setOnClickListener(new View.OnClickListener() {
//											@Override
//											public void onClick(View v) {
//												ad.performClick("headline");
//											}
//										});

										ad.recordImpression();

										rlSponsoredArticles = frameLayout;
										((ViewGroup) rlSponsoredArticles).addView(rowView);
									}
								},
								null)
//								.forContentAd(new NativeContentAd.OnContentAdLoadedListener() {
//									@Override
//									public void onContentAdLoaded(NativeContentAd ad) {
//										Log.e("alex", "onCustomTemplateAdLoaded 2!!!");
//									}
//								})
						.withAdListener(new AdListener() {
							@Override
							public void onAdFailedToLoad(int errorCode) {
								Log.e("alex", "onCustomTemplateAdLoaded onAdFailedToLoad:" + errorCode);
							}
						})
						.build();

				adLoader.loadAd(new PublisherAdRequest.Builder().build());



//				final ViewHolder holder = new ViewHolder();
//				rowView = inflater.inflate(R.layout.row_layout_dfp_ad_view, null);
//				RelativeLayout frameLayout = (RelativeLayout) rowView.findViewById(R.id.row_layout_dfp_adview_frame_layout_container);
//				//				frameLayout.setBackgroundColor(Color.YELLOW);
//				DfpAdHolder adHolder = ((DfpAdHolder) (newsSet.elementAt(position)));
//				holder.AdViewKatavaPirsumitNadlanPublisher = new PublisherAdView(activity);
//				holder.AdViewKatavaPirsumitNadlanPublisher.setAdUnitId(adHolder.getAdUnitID());
//				holder.AdViewKatavaPirsumitNadlanPublisher.setAdSizes(adHolder.getAdSize());
//				frameLayout.addView(holder.AdViewKatavaPirsumitNadlanPublisher, RelativeLayout.LayoutParams.WRAP_CONTENT,	RelativeLayout.LayoutParams.WRAP_CONTENT);
//
//				Log.e("alex", "TYPE_DFP_AD_VIEW_KATAVA_PIRSUMIT_NADLAN:" + adHolder.getAdUnitID());
//
//				holder.AdViewKatavaPirsumitNadlanPublisher.setAdListener(new AdListener()
//				{
//					public void onAdLoaded()
//					{
//						Log.d(TAG,	"LazyAdapter Ad received from TYPE_DFP_AD_VIEW_KATAVA_PIRSUMIT_NADLAN at Activity : " + activity.toString());
//						holder.AdViewKatavaPirsumitNadlanPublisher.setVisibility(View.VISIBLE);
//					}
//					public void onAdFailedToLoad(int errorCode)
//					{
//						Log.d(TAG, "Ad failed to receive on TYPE_DFP_AD_VIEW_KATAVA_PIRSUMIT_NADLAN at Activity : " + activity.toString()+ " error is: " + errorCode);
//						holder.AdViewKatavaPirsumitNadlanPublisher.setVisibility(View.GONE);
//					}
//				});
//				rowView.setTag(holder);
			}
			// katava pirsumit in main list
			else if (getItemViewType(position) == TYPE_DFP_AD_VIEW_KATAVA_PIRSUMIT_MAIN_LIST 
						|| getItemViewType(position) == TYPE_DFP_AD_VIEW_KATAVA_PIRSUMIT_MAIN_LIST_2
						|| getItemViewType(position) == TYPE_DFP_AD_VIEW_KATAVA_PIRSUMIT_HOT_STORY)
			{
				//Log.e("alex", "AAA: " + String.valueOf(position) + "===" + getItemViewType(position));
				
				boolean bLoadSposorArticleFromExternalSource = true;
				
				if(
					(Definitions.isSponsoredArticleMainMadorFromGlobes_1 && getItemViewType(position) == TYPE_DFP_AD_VIEW_KATAVA_PIRSUMIT_MAIN_LIST) ||
					(Definitions.isSponsoredArticleMainMadorFromGlobes_2 && getItemViewType(position) == TYPE_DFP_AD_VIEW_KATAVA_PIRSUMIT_MAIN_LIST_2) ||
					(Definitions.isSponsoredArticleStoryFromGlobes && getItemViewType(position) == TYPE_DFP_AD_VIEW_KATAVA_PIRSUMIT_HOT_STORY)
				)
				{
					bLoadSposorArticleFromExternalSource = false;
				}				
				
				final ViewHolder holder = new ViewHolder();
				rowView = inflater.inflate(R.layout.row_layout_dfp_ad_view, null);
				final RelativeLayout frameLayout = (RelativeLayout) rowView.findViewById(R.id.row_layout_dfp_adview_frame_layout_container);
				//				frameLayout.setBackgroundColor(Color.GREEN);

				final DfpAdHolder adHolder = ((DfpAdHolder) (newsSet.elementAt(position)));

				Log.e("alex", "katava pirsumit 1: size-" + adHolder.getAdSize() + " space-" + adHolder.getAdUnitID() + " bLoadSposorArticleFromExternalSource:" + bLoadSposorArticleFromExternalSource
						+ " Definitions.show_dfp_banners_on_splash_screen=" + Definitions.show_dfp_banners_on_splash_screen);
				
				//if(bLoadSposorArticleFromExternalSource)
				{								
	                if(Definitions.show_dfp_banners_on_splash_screen)
	                {
						Log.e("alex", "onCustomTemplateAdLoaded before start!!!");

						//=====================================TEST CUSTOM DFP BANNER!!!=======================================//

						AdLoader adLoader = new AdLoader.Builder(activity.getApplicationContext(), adHolder.getAdUnitID())  // /6499/example/native" === 10063170
								.forCustomTemplateAd(Definitions.dfp_articles_template_id,
										new NativeCustomTemplateAd.OnCustomTemplateAdLoadedListener() {
											@Override
											public void onCustomTemplateAdLoaded(final NativeCustomTemplateAd ad) {

												View rowView = drawDFPSponsoredArticle(ad);

//												View rowView = inflater.inflate(R.layout.row_layout_new, null);
//												final ViewHolder holder = new ViewHolder();
//												holder.text = (TextView) rowView.findViewById(R.id.title);
//												holder.image = (ImageView) rowView.findViewById(R.id.image);
//												holder.createdOn = (TextView) rowView.findViewById(R.id.createdOn);
//												rowView.setTag(holder);
//
//												holder.text.setText(ad.getText("headline"));
//												holder.text.setTypeface(almoni_aaa_regular);
//												holder.text.setTextSize(22.5f);
//												holder.text.setTextColor(Color.BLACK);
//												holder.createdOn.setText("");
//
//												holder.image.setImageDrawable(ad.getImage("MainImage").getDrawable());
//
//												holder.image.setOnClickListener(new View.OnClickListener() {
//													@Override
//													public void onClick(View v) {
//														ad.performClick("MainImage");
//													}
//												});
//
//												holder.text.setOnClickListener(new View.OnClickListener() {
//													@Override
//													public void onClick(View v) {
//														ad.performClick("headline");
//													}
//												});

												ad.recordImpression();

												rlSponsoredArticles = frameLayout;
												((ViewGroup) rlSponsoredArticles).addView(rowView);
											}
										},
										null)
//								.forContentAd(new NativeContentAd.OnContentAdLoadedListener() {
//									@Override
//									public void onContentAdLoaded(NativeContentAd ad) {
//										Log.e("alex", "onCustomTemplateAdLoaded 2!!!");
//									}
//								})
								.withAdListener(new AdListener() {
									@Override
									public void onAdFailedToLoad(int errorCode) {
										Log.e("alex", "onCustomTemplateAdLoaded onAdFailedToLoad:" + errorCode);
									}
								})
								.build();

						adLoader.loadAd(new PublisherAdRequest.Builder().build());



						//=====================================END TEST CUSTOM DFP BANNER!!!=======================================//






//						holder.AdViewKatavaPirsumitMainListPublisher = new PublisherAdView(activity);
//						//holder.AdViewKatavaPirsumitMainListPublisher.setAdUnitId(adHolder.getAdUnitID());
//						//holder.AdViewKatavaPirsumitMainListPublisher.setAdSizes(adHolder.getAdSize());
//
//						Log.e("alex", "GGGGGGGGGGGGGGG:" + adHolder.getAdUnitID() + "===" + adHolder.getAdSize());
//
//						holder.AdViewKatavaPirsumitMainListPublisher.setAdUnitId(adHolder.getAdUnitID()); //"/43010785/globes/catava_mobile.globes.rashi1");
//						holder.AdViewKatavaPirsumitMainListPublisher.setAdSizes(adHolder.getAdSize());
//						//holder.AdViewKatavaPirsumitMainListPublisher.setAdSizes(AdSize.SMART_BANNER);
//
//						frameLayout.addView(holder.AdViewKatavaPirsumitMainListPublisher, RelativeLayout.LayoutParams.MATCH_PARENT,	RelativeLayout.LayoutParams.WRAP_CONTENT);
//						holder.AdViewKatavaPirsumitMainListPublisher.setAdListener(new AdListener()
//						{
//							public void onAdLoaded()
//							{
//								Log.e("alex", "KatavaPirsumit onAdLoaded");
//
//								Log.d(TAG,	"LazyAdapter Ad received from TYPE_DFP_AD_VIEW_KATAVA_PIRSUMIT_MAIN_LIST at Activity : " + activity.toString());
//								holder.AdViewKatavaPirsumitMainListPublisher.setVisibility(View.VISIBLE);
//							}
//							public void onAdFailedToLoad(int errorCode)
//							{
//								Log.e("alex", "KatavaPirsumit onAdFailedToLoad:" + errorCode + "===" + adHolder.getAdUnitID());
//
//								Log.d(TAG,"Ad failed to receive on TYPE_DFP_AD_VIEW_KATAVA_PIRSUMIT_MAIN_LIST at Activity : " + activity.toString()+ " error is: " + errorCode);
//								holder.AdViewKatavaPirsumitMainListPublisher.setVisibility(View.GONE);
//								//frameLayout.removeView(holder.AdViewKatavaPirsumitMainListPublisher);
//							}
//						});
//
//						PublisherAdRequest request = new PublisherAdRequest.Builder().build();
//						holder.AdViewKatavaPirsumitMainListPublisher.loadAd(request);
	                }
	                else
	                {					
	                	if(Definitions.wallafusion)
	                	{
		                	/////////////////////WALLA TEST///////////////////////////////////				
	                		//isSponsoredArticles = true;
	                		isTopHeaderBanner = false;
	                		rlSponsoredArticles = frameLayout;
	                		
	                		WallaAdvParser wap = new WallaAdvParser(activity.getApplicationContext(), activity.getApplication(),this, sWallaPage, sWallaLayout, "", 0);
							wap.execute();
	                	}
	                	else
	                	{
	                		String sPositiveMobileURL = Definitions.AD_POSITIVEMOBILE_MAIN_SPLASH;
	                    	
	                    	if(adHolder.getAdUnitID() == Definitions.ADUNIT_KATAVA_PIRSUMIT_MAIN_LIST_HOT_STORY)
	                    	{
	                    		sPositiveMobileURL = Definitions.AD_POSITIVEMOBILE_HOT_STORIES;
	                    		//Log.e("alex", "katava pirsumit: ADUNIT_KATAVA_PIRSUMIT_MAIN_LIST_HOT_STORY");
	                    	}
	                    	else if(adHolder.getAdUnitID() == Definitions.ADUNIT_KATAVA_PIRSUMIT_NADLAN_LIST)
	                    	{
	                    		sPositiveMobileURL = Definitions.AD_POSITIVEMOBILE_NADLAN;               	
	                    	}
	                    	
	                    	WebView w = new WebView(activity.getApplicationContext());
	                    	showPositiveModileAd(w, sPositiveMobileURL, frameLayout, position);
	                	}                	                	
	                }
				}
				
				rowView.setTag(holder);
			}
			// ??????????????????????????????????????????????????????????????????????????????????
			// TODO removed the Banner type and added the DFPAdView inst
			else
				// if(getItemViewType(position)==TYPE_BANNER)
			{
				final ViewHolder holder = new ViewHolder();
				rowView = new View(activity);
				rowView.setTag(holder);
				// // TODO DfpAdView is null need to create
			}
		}
		else
		{
			rowView = convertView;
			if (((ViewHolder) rowView.getTag()).LinearLayoutBack != null)
			{
				if (newsSet.elementAt(position) instanceof Article)
				{
					// eli in here
					((ViewHolder) rowView.getTag()).LinearLayoutBack.setTag(((Article) newsSet.elementAt(position)));
				}
				else if (newsSet.elementAt(position) instanceof RowArticleDuns100)
				{

					((ViewHolder) rowView.getTag()).LinearLayoutBack.setTag(((RowArticleDuns100) newsSet.elementAt(position)));
				}

				// ((ViewHolder)
				// rowView.getTag()).LinearLayoutBack.setTag(((Article)
				// newsSet.elementAt(position)));
			}
			// TODO REUSE starts here

		}
		ViewHolder holder = (ViewHolder) rowView.getTag();

		if (getItemViewType(position) == TYPE_STOCK)
		{
			// holder.checkBoxAddToLooperFromSharesList.setTag(((Stock)
			// newsSet.elementAt(position)));
		}
		if (getItemViewType(position) == TYPE_INSTRUMENT)
		{
			holder.checkBoxAddToLooperFromMarketsList.setTag(((Instrument) newsSet.elementAt(position)));
		}
		if (getItemViewType(position) == TYPE_PORTFOLIO_INSTRUMENT)
		{
			holder.checkBoxAddToLooperFromPortfolioList.setTag(((PortfolioInstrument) newsSet.elementAt(position)));
		}

		if (getItemViewType(position) == TYPE_ARTICLE_DUNS_100 || getItemViewType(position) == -TYPE_ARTICLE_DUNS_100)
		{
			RowArticleDuns100 articleDuns100 = (RowArticleDuns100) newsSet.elementAt(position);

			holder.text.setText(articleDuns100.getTitle());
			holder.text.setTypeface(almoni_aaa_regular);
			holder.createdOn.setText(articleDuns100.getCreatedOn());
			String imageToGet = (articleDuns100.getF4());
			holder.image.setTag(articleDuns100.getF4());

			if (!imageToGet.equals(""))
			{
				Picasso.with(activity).load(imageToGet.replaceAll("null", "").trim()).into(holder.image);
			}
			else
			{
				holder.image.setImageResource(R.drawable.stub_image_not_found);
			}

			if (((RowArticleDuns100) holder.LinearLayoutBack.getTag()).getTagiot().size() > 0)
			{
				if (holder.LinearLayoutBack.getChildCount() > 0)
				{
					holder.LinearLayoutBack.removeAllViews();
				}

				for (int i = 0; i < ((RowArticleDuns100) holder.LinearLayoutBack.getTag()).getTagiot().size(); i++)
				{
					LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(150, LinearLayout.LayoutParams.MATCH_PARENT);
					// params.setMargins(20, 20, 20, 20);
					params.weight = 1;

					TextView textView = new TextView(activity);
					String tagTitle = ((RowArticleDuns100) holder.LinearLayoutBack.getTag()).getTagiot().get(i).getSimplified();
					tagTitle = tagTitle.replaceAll("_", " ");
					textView.setText(tagTitle);
					textView.setBackgroundColor(pinkColor);
					textView.setTextColor(blackColor);

					textView.setLayoutParams(params);
					textView.setGravity(Gravity.CENTER);
					textView.setPadding(10, 0, 10, 0);
					textView.setTypeface(almoni_aaa_regular);

					// add Tagit as tag
					textView.setTag(((RowArticleDuns100) holder.LinearLayoutBack.getTag()).getTagiot().get(i));

					// add listener
					textView.setOnClickListener(new View.OnClickListener()
					{

						@Override
						public void onClick(View v)
						{
							onTagitClick(v);

						}
					});

					LinearLayout.LayoutParams paramsForView = new LinearLayout.LayoutParams(1, LinearLayout.LayoutParams.MATCH_PARENT);
					paramsForView.weight = 0;

					View view = new View(activity);
					view.setBackgroundColor(grayColor);
					view.setLayoutParams(paramsForView);
					holder.LinearLayoutBack.addView(textView);
					holder.LinearLayoutBack.addView(view);
				}
			}
			if (getItemViewType(position) == -TYPE_ARTICLE_DUNS_100)
			{
				holder.text.setOnClickListener(new OnClickListener()
				{
					@Override
					public void onClick(View v)
					{
						LazyAdapter.this.getMainFragment().onclickFrontWihtoutTAGIT(position);
					}
				});
				holder.createdOn.setOnClickListener(new OnClickListener()
				{
					@Override
					public void onClick(View v)
					{
						LazyAdapter.this.getMainFragment().onclickFrontWihtoutTAGIT(position);
					}
				});
				holder.image.setOnClickListener(new OnClickListener()
				{
					@Override
					public void onClick(View v)
					{
						LazyAdapter.this.getMainFragment().onclickFrontWihtoutTAGIT(position);
					}
				});
			}
		}
		else if (getItemViewType(position) == -TYPE_COMPANY_DUNS_100)
		{
			RowCompanyDuns100 company = (RowCompanyDuns100) newsSet.elementAt(position);

			holder.text.setText(company.getName().replaceAll("null", "").trim());
			holder.text.setTypeface(almoni_aaa_regular);
			holder.createdOn.setText("");
			String imageToGet = (company.getLogo().replaceAll("null", "").trim());
			holder.image.setTag(company.getLogo().replaceAll("null", "").trim());
			if (!imageToGet.equals(""))
			{
				Picasso.with(activity).load(imageToGet.replaceAll("null", "").trim()).into(holder.image);
			}
			else
			{
				holder.image.setImageResource(R.drawable.stub_image_not_found);
			}

			holder.text.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					LazyAdapter.this.getMainFragment().onclickFrontWihtoutTAGIT(position);
				}
			});
			holder.createdOn.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					LazyAdapter.this.getMainFragment().onclickFrontWihtoutTAGIT(position);
				}
			});
			holder.image.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					LazyAdapter.this.getMainFragment().onclickFrontWihtoutTAGIT(position);
				}
			});

		}
		else if (getItemViewType(position) == -TYPE_ARTICLE_OPINION || getItemViewType(position) == TYPE_ARTICLE_OPINION)
		{
			//Log.e("alex", "DEOT1");
			
			final ArticleOpinion articleOpinion = (ArticleOpinion) newsSet.elementAt(position);
			TextView author = holder.authorName;
			author.setVisibility(View.VISIBLE);
			holder.createdOn.setVisibility(View.VISIBLE);
			author.setText(articleOpinion.getAuthorName());
			holder.text.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					LazyAdapter.this.getMainFragment().onclickFrontWihtoutTAGIT(position);
				}
			});
			holder.createdOn.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					LazyAdapter.this.getMainFragment().onclickFrontWihtoutTAGIT(position);
				}
			});
			holder.image.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					LazyAdapter.this.getMainFragment().onclickFrontWihtoutTAGIT(position);
				}
			});
			if (articleOpinion.getTagiot().size() > 0)
			{
				if (holder.LinearLayoutBack.getChildCount() > 0)
				{
					holder.LinearLayoutBack.removeAllViews();
				}
				for (int i = 0; i < articleOpinion.getTagiot().size(); i++)
				{
					LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(150, LinearLayout.LayoutParams.MATCH_PARENT);
					params.weight = 1;
					TextView textView = new TextView(activity);
					String tagTitle = ((ArticleOpinion) holder.LinearLayoutBack.getTag()).getTagiot().get(i).getSimplified();
					tagTitle = tagTitle.replaceAll("_", " ");
					textView.setText(tagTitle);
					textView.setBackgroundColor(pinkColor);
					textView.setTextColor(blackColor);

					textView.setLayoutParams(params);
					textView.setGravity(Gravity.CENTER);
					textView.setPadding(10, 0, 10, 0);
					textView.setTypeface(almoni_aaa_regular);

					// add Tagit as tag
					textView.setTag(((ArticleOpinion) holder.LinearLayoutBack.getTag()).getTagiot().get(i));

					// add listener
					textView.setOnClickListener(new View.OnClickListener()
					{
						@Override
						public void onClick(View v)
						{
							onTagitClick(v);
						}
					});

					LinearLayout.LayoutParams paramsForView = new LinearLayout.LayoutParams(1, LinearLayout.LayoutParams.MATCH_PARENT);
					paramsForView.weight = 0;

					View view = new View(activity);
					view.setBackgroundColor(grayColor);
					view.setLayoutParams(paramsForView);
					holder.LinearLayoutBack.addView(textView);
					holder.LinearLayoutBack.addView(view);
				}
			}
			holder.text.setText(articleOpinion.getTitle());
			holder.text.setTypeface(almoni_aaa_regular);
			holder.text.setTextSize(22.5f);
			holder.text.setTextColor(Color.BLACK);
			holder.createdOn.setText(articleOpinion.getCreatedOn());
			String imageToGet = (articleOpinion.getImage());
			holder.image.setTag(articleOpinion.getImage());
			
			//Log.e("alex", "DEOT2: " + articleOpinion.getImage());
			
			if (!imageToGet.equals(""))
			{
				Picasso.with(activity).load(imageToGet.replaceAll("null", "").trim()).into(holder.image);
			}
			else
			{
				holder.image.setImageResource(R.drawable.stub_image_not_found);
			}
			//
			//			if (imageToGet.indexOf("globesTV") > 0)
			//			{
			//				if (holder.buttonShareVideo != null)
			//				{
			//					// TODO test remove btn share video from mainactivity
			//					// list
			//					holder.buttonShareVideo.setVisibility(View.VISIBLE);
			//					holder.buttonShareVideo.setOnClickListener(new OnClickListener()
			//					{
			//
			//						@Override
			//						public void onClick(View v)
			//						{
			//							Intent shareArticle = new Intent();
			//							shareArticle.setAction(Intent.ACTION_SEND);
			//							String mimeType = "text/plain";
			//							shareArticle.setType(mimeType);
			//							shareArticle.putExtra(Intent.EXTRA_SUBJECT, (article.getTitle()));
			//
			//							shareArticle.putExtra(Intent.EXTRA_TEXT, Definitions.watchVideo
			//									+ "\n\n"
			//									+ GlobesURL.URLDocumentToShare
			//									+ (article.getDoc_id() + Definitions.fromPlatformAppAnalytics + "\n\n" + Definitions.ShareString + " "
			//											+ Definitions.shareGlobesWithQuates + "\n\n" + Definitions.shareCellAppsPart1 + " "
			//											+ Definitions.shareGlobesWithQuates + " " + Definitions.shareCellAppsPart2 + "\n"));
			//
			//							
			//							shareArticle.putExtra(Intent.EXTRA_TITLE, Definitions.ShareString);
			//							activity.startActivity(Intent.createChooser(shareArticle, Definitions.Share));
			//						}
			//					});
			//				}
			//			}
			//			else if (holder.buttonShareVideo != null)
			//					holder.buttonShareVideo.setVisibility(View.INVISIBLE);

		}
		else if (getItemViewType(position) == TYPE_ARTICLE || getItemViewType(position) == TYPE_ARTICLE_EMPHASIZED || getItemViewType(position) == -TYPE_ARTICLE_EMPHASIZED)
		{
			final Article article = (Article) newsSet.elementAt(position);
			if ( getItemViewType(position) == -TYPE_ARTICLE_EMPHASIZED)
			{
				holder.text.setOnClickListener(new OnClickListener()
				{
					@Override
					public void onClick(View v)
					{
						LazyAdapter.this.getMainFragment().onclickFrontWihtoutTAGIT(position);
					}
				});
				holder.createdOn.setOnClickListener(new OnClickListener()
				{
					@Override
					public void onClick(View v)
					{	
						LazyAdapter.this.getMainFragment().onclickFrontWihtoutTAGIT(position);
						
						Log.e("alex", "OnArticleClicked...");
					}
				});
				holder.image.setOnClickListener(new OnClickListener()
				{
					@Override
					public void onClick(View v)
					{					
						LazyAdapter.this.getMainFragment().onclickFrontWihtoutTAGIT(position);
					}
				});
			}
			if (((Article) holder.LinearLayoutBack.getTag()).getTagiot().size() > 0)
			{
				if (holder.LinearLayoutBack.getChildCount() > 0)
				{
					holder.LinearLayoutBack.removeAllViews();
				}
				for (int i = 0; i < ((Article) holder.LinearLayoutBack.getTag()).getTagiot().size(); i++)
				{
					LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(150, LinearLayout.LayoutParams.MATCH_PARENT);
					// params.setMargins(20, 20, 20, 20);
					params.weight = 1;
					TextView textView = new TextView(activity);
					String tagTitle = ((Article) holder.LinearLayoutBack.getTag()).getTagiot().get(i).getSimplified();
					tagTitle = tagTitle.replaceAll("_", " ");
					textView.setText(tagTitle);
					textView.setBackgroundColor(pinkColor);
					textView.setTextColor(blackColor);

					textView.setLayoutParams(params);
					textView.setGravity(Gravity.CENTER);
					textView.setPadding(10, 0, 10, 0);
					textView.setTypeface(almoni_aaa_regular);

					// add Tagit as tag
					textView.setTag(((Article) holder.LinearLayoutBack.getTag()).getTagiot().get(i));

					// add listener
					textView.setOnClickListener(new View.OnClickListener()
					{
						@Override
						public void onClick(View v)
						{
							onTagitClick(v);
						}
					});

					LinearLayout.LayoutParams paramsForView = new LinearLayout.LayoutParams(1, LinearLayout.LayoutParams.MATCH_PARENT);
					paramsForView.weight = 0;

					View view = new View(activity);
					view.setBackgroundColor(grayColor);
					view.setLayoutParams(paramsForView);
					holder.LinearLayoutBack.addView(textView);
					holder.LinearLayoutBack.addView(view);
				}
			}

			holder.text.setText(article.getTitle());
			holder.text.setTypeface(almoni_aaa_regular);
			holder.text.setTextSize(22.5f);
			holder.text.setTextColor(Color.BLACK);
			holder.createdOn.setText(article.getCreatedOn());
			String imageToGet = (article.getImage());
			holder.image.setTag(article.getImage());
			holder.imgPromoIcon.setVisibility(View.GONE);
			
			if(article.isGlobesMetzig)
			{
				holder.createdOn.setVisibility(View.GONE);
				holder.imageAfterTitle.setVisibility(View.VISIBLE);				
				Picasso.with(activity.getApplicationContext()).load(article.getGlobesMetzigImg()).into(holder.imageAfterTitle);
			}				
			else
			{
				holder.createdOn.setVisibility(View.VISIBLE);
				holder.imageAfterTitle.setVisibility(View.GONE);	
			}
			
			//TTT
			if(article.hasClip() && imageToGet.indexOf("globesTV") == -1)
			{
				holder.imgPromoIcon.setImageResource(R.drawable.tv_play);
				holder.imgPromoIcon.setVisibility(View.VISIBLE);
			}
			if (!imageToGet.equals(""))
			{
				//				Log.e("eli", imageToGet.replaceAll("null", "").trim());
				Picasso.with(activity).load(imageToGet.replaceAll("null", "").trim()).into(holder.image);
			}
			else
			{
				holder.image.setImageResource(R.drawable.stub_image_not_found);
			}

			if (imageToGet.indexOf("globesTV") > 0)
			{
				if (holder.buttonShareVideo != null)
				{
					// TODO test remove btn share video from mainactivity
					// list
					holder.buttonShareVideo.setVisibility(View.VISIBLE);
					holder.buttonShareVideo.setOnClickListener(new OnClickListener()
					{
						@Override
						public void onClick(View v)
						{
							Intent shareArticle = new Intent();
							shareArticle.setAction(Intent.ACTION_SEND);
							String mimeType = "text/plain";
							shareArticle.setType(mimeType);
							shareArticle.putExtra(Intent.EXTRA_SUBJECT, (article.getTitle()));

							Log.e("eli", "getDoc_id" + article.getDoc_id());
							shareArticle.putExtra(Intent.EXTRA_TEXT, article.getTitle() + "\n\n" +
									"http://iglob.es/?" + removeOneAndZero( article.getDoc_id())
									);
							//									Definitions.watchVideo	+ "\n\n"
							//									+ GlobesURL.URLDocumentToShare	+ (article.getDoc_id() + Definitions.fromPlatformAppAnalytics + "\n\n" 
							//									+ Definitions.ShareString + " "	+ Definitions.shareGlobesWithQuates + "\n\n"
							//									+ Definitions.shareCellAppsPart1 + " "+ Definitions.shareGlobesWithQuates + " " + Definitions.shareCellAppsPart2 + "\n"));

							// TODO add tracking later
							// tracker.trackPageView(GlobesURL.URLDocumentToShare
							// +
							// ((Article)
							// parsedNewsSet.itemHolder.get(position)).getDoc_id()
							// + "from=" + Definitions.SHAREsuffix);
							shareArticle.putExtra(Intent.EXTRA_TITLE, Definitions.ShareString);
							activity.startActivity(Intent.createChooser(shareArticle, Definitions.Share));
						}
					});
				}
			}
			else
			{
				if (holder.buttonShareVideo != null)
				{
					// TODO test remove btn share video from mainactivity
					// list
					holder.buttonShareVideo.setVisibility(View.INVISIBLE);
				}
			}

			//			if (!TextUtils.isEmpty(article.getImageUrlFromF11().replaceAll("null", "").trim()))
			//			{
			//				holder.imageViewFirstToTell.setVisibility(View.VISIBLE);
			//				Picasso.with(activity).load(article.getImageUrlFromF11().replaceAll("null", "").trim()).into(holder.imageViewFirstToTell);
			//
			//			}
		}
		else if (getItemViewType(position) == TYPE_ARTICLE_BIG || getItemViewType(position) == -TYPE_ARTICLE_BIG)
		{
			// article data
			final Article article = (Article) newsSet.elementAt(position);

			holder.textViewBigArticle.setText(article.getTitle());
			//			holder.textViewBigArticle.setTypeface(almoni_aaa_regular);
			holder.textViewBigArticle.setTypeface(almoni_aaa_blod);


			String imageToGet = (article.getFilteredBigImage());
			holder.imageViewBigArticle.setTag(article.getFilteredBigImage());

			if (getItemViewType(position) == -TYPE_ARTICLE_BIG)
			{
				holder.textViewBigArticle.setOnClickListener(new OnClickListener()
				{

					@Override
					public void onClick(View v)
					{

						LazyAdapter.this.getMainFragment().onclickFrontWihtoutTAGIT(position);
					}
				});

				holder.imageViewBigArticle.setOnClickListener(new OnClickListener()
				{

					@Override
					public void onClick(View v)
					{

						LazyAdapter.this.getMainFragment().onclickFrontWihtoutTAGIT(position);
					}
				});

			}

			if (((Article) holder.LinearLayoutBack.getTag()).getTagiot().size() > 0)
			{
				if (holder.LinearLayoutBack.getChildCount() > 0)
				{
					holder.LinearLayoutBack.removeAllViews();

				}
				for (int i = 0; i < ((Article) holder.LinearLayoutBack.getTag()).getTagiot().size(); i++)

				{
					LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(150, LinearLayout.LayoutParams.MATCH_PARENT);
					// params.setMargins(20, 20, 20, 20);
					params.weight = 1;

					TextView textView = new TextView(activity);
					String tagTitle = ((Article) holder.LinearLayoutBack.getTag()).getTagiot().get(i).getSimplified();
					tagTitle = tagTitle.replaceAll("_", " ");
					textView.setText(tagTitle);
					textView.setBackgroundColor(pinkColor);
					textView.setTextColor(blackColor);
					textView.setTypeface(almoni_aaa_regular);

					textView.setLayoutParams(params);
					textView.setGravity(Gravity.CENTER);
					textView.setPadding(10, 0, 10, 0);

					// add Tagit as tag
					textView.setTag(((Article) holder.LinearLayoutBack.getTag()).getTagiot().get(i));

					// add listener
					textView.setOnClickListener(new View.OnClickListener()
					{

						@Override
						public void onClick(View v)
						{
							onTagitClick(v);

						}
					});

					LinearLayout.LayoutParams paramsForView = new LinearLayout.LayoutParams(1, LinearLayout.LayoutParams.MATCH_PARENT);
					paramsForView.weight = 0;

					View view = new View(activity);
					view.setBackgroundColor(grayColor);
					view.setLayoutParams(paramsForView);
					holder.LinearLayoutBack.addView(textView);
					holder.LinearLayoutBack.addView(view);
				}
			}
			if (!imageToGet.equals(""))
			{
				Log.e("eli", "img load big " + imageToGet.replaceAll("null", "").trim());
				Picasso.with(activity).load(imageToGet.replaceAll("null", "").trim()).into(holder.imageViewBigArticle);
				//				Bitmap bitmapTest = Utils.DownloadImage(imageToGet.replaceAll("null", "").trim());
				//				if (bitmapTest != null)
				//				{
				//					holder.imageViewBigArticle.setImageBitmap(bitmapTest);
				//				}

			}
			else
			{
				holder.imageViewBigArticle.setImageResource(R.drawable.stub_image_not_found);
			}
			if (imageToGet.indexOf("globesTV") > 0)
			{
				if (holder.buttonShareVideo != null)
				{
					holder.buttonShareVideo.setVisibility(View.VISIBLE);
					holder.buttonShareVideo.setOnClickListener(new OnClickListener()
					{
						@Override
						public void onClick(View v)
						{
							Intent shareArticle = new Intent();
							shareArticle.setAction(Intent.ACTION_SEND);
							String mimeType = "text/plain";
							shareArticle.setType(mimeType);
							shareArticle.putExtra(Intent.EXTRA_SUBJECT, (article.getTitle()));

							Log.e("eli", "getDoc_id" + article.getDoc_id());
							shareArticle.putExtra(Intent.EXTRA_TEXT, article.getTitle() + "\n\n" +
									"http://iglob.es/?" + removeOneAndZero( article.getDoc_id())
									);

							shareArticle.putExtra(Intent.EXTRA_TITLE, Definitions.ShareString);
							activity.startActivity(Intent.createChooser(shareArticle, Definitions.Share));
						}
					});
				}
			}
			else
			{
				if (holder.buttonShareVideo != null)
				{
					holder.buttonShareVideo.setVisibility(View.INVISIBLE);
				}
			}
		}
		// Big article Globes TV
		else if (getItemViewType(position) == TYPE_ARTICLE_BIG_GLOBES_TV || getItemViewType(position) == -TYPE_ARTICLE_BIG_GLOBES_TV)
		{
			// article data
			final Article article = (Article) newsSet.elementAt(position);

			if (getItemViewType(position) == -TYPE_ARTICLE_BIG_GLOBES_TV)
			{
				holder.textViewBigGlobesTVarticleTitle.setOnClickListener(new OnClickListener()
				{
					@Override
					public void onClick(View v)
					{
						LazyAdapter.this.getMainFragment().onclickFrontWihtoutTAGIT(position);
					}
				});
				holder.imageViewBigGlobesTVArticle.setOnClickListener(new OnClickListener()
				{
					@Override
					public void onClick(View v)
					{
						LazyAdapter.this.getMainFragment().onclickFrontWihtoutTAGIT(position);
					}
				});
			}
			if (((Article) holder.LinearLayoutBack.getTag()).getTagiot().size() > 0)
			{
				if (holder.LinearLayoutBack.getChildCount() > 0)
				{
					holder.LinearLayoutBack.removeAllViews();
				}
				for (int i = 0; i < ((Article) holder.LinearLayoutBack.getTag()).getTagiot().size(); i++)
				{
					LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(150, LinearLayout.LayoutParams.MATCH_PARENT);
					// params.setMargins(20, 20, 20, 20);
					params.weight = 1;
					TextView textView = new TextView(activity);
					String tagTitle = ((Article) holder.LinearLayoutBack.getTag()).getTagiot().get(i).getSimplified();
					tagTitle = tagTitle.replaceAll("_", " ");
					textView.setText(tagTitle);
					textView.setBackgroundColor(pinkColor);
					textView.setTextColor(blackColor);
					textView.setTypeface(almoni_aaa_regular);

					textView.setLayoutParams(params);
					textView.setGravity(Gravity.CENTER);
					textView.setPadding(10, 0, 10, 0);

					// add Tagit as tag
					textView.setTag(((Article) holder.LinearLayoutBack.getTag()).getTagiot().get(i));

					// add listener
					textView.setOnClickListener(new View.OnClickListener()
					{
						@Override
						public void onClick(View v)
						{
							onTagitClick(v);
						}
					});

					LinearLayout.LayoutParams paramsForView = new LinearLayout.LayoutParams(1, LinearLayout.LayoutParams.MATCH_PARENT, 1);
					paramsForView.weight = 0;

					View view = new View(activity);
					view.setBackgroundColor(grayColor);
					view.setLayoutParams(paramsForView);
					holder.LinearLayoutBack.addView(textView);
					holder.LinearLayoutBack.addView(view);;
				}
			}
			holder.textViewBigGlobesTVarticleTitle.setText(article.getTitle());
			holder.textViewBigGlobesTVarticleTitle.setTypeface(almoni_aaa_blod);
			String imageToGet = (article.getFilteredBigImage());
			holder.imageViewBigGlobesTVArticle.setTag(article.getFilteredBigImage());
			// Picasso.with(activity).load(imageToGet.replaceAll("null",
			// "").trim()).into(holder.imageViewBigGlobesTVArticle);

			if (!imageToGet.equals(""))
			{

				Picasso.with(activity).load(imageToGet.replaceAll("null", "").trim()).into(holder.imageViewBigGlobesTVArticle);
			}
			else
			{
				holder.imageViewBigGlobesTVArticle.setImageResource(R.drawable.stub_image_not_found);
			}

			if (imageToGet.indexOf("globesTV") > 0)
			{
				if (holder.buttonShareVideo != null)
				{
					// TODO test remove btn share video from mainactivity
					// list
					holder.buttonShareVideo.setVisibility(View.VISIBLE);
					holder.buttonShareVideo.setOnClickListener(new OnClickListener()
					{
						@Override
						public void onClick(View v)
						{
							Intent shareArticle = new Intent();
							shareArticle.setAction(Intent.ACTION_SEND);
							String mimeType = "text/plain";
							shareArticle.setType(mimeType);
							shareArticle.putExtra(Intent.EXTRA_SUBJECT, (article.getTitle()));

							Log.e("eli", "getDoc_id" + article.getDoc_id());
							shareArticle.putExtra(Intent.EXTRA_TEXT, article.getTitle() + "\n\n" +
									"http://iglob.es/?" + removeOneAndZero( article.getDoc_id())
									);
							//									Definitions.watchVideo	+ "\n\n"
							//									+ GlobesURL.URLDocumentToShare	+ (article.getDoc_id() + Definitions.fromPlatformAppAnalytics + "\n\n" 
							//									+ Definitions.ShareString + " "	+ Definitions.shareGlobesWithQuates + "\n\n"
							//									+ Definitions.shareCellAppsPart1 + " "+ Definitions.shareGlobesWithQuates + " " + Definitions.shareCellAppsPart2 + "\n"));

							// TODO add tracking later
							// tracker.trackPageView(GlobesURL.URLDocumentToShare
							// +
							// ((Article)
							// parsedNewsSet.itemHolder.get(position)).getDoc_id()
							// + "from=" + Definitions.SHAREsuffix);
							shareArticle.putExtra(Intent.EXTRA_TITLE, Definitions.ShareString);
							activity.startActivity(Intent.createChooser(shareArticle, Definitions.Share));
						}


					});
				}
			}
			else
			{
				if (holder.buttonShareVideo != null)
				{
					// TODO test remove btn share video from mainactivity
					// list
					holder.buttonShareVideo.setVisibility(View.INVISIBLE);
				}
			}
		}
		//		else if (getItemViewType(position) == TYPE_ARTICLE_OPINION || getItemViewType(position) == -TYPE_ARTICLE_OPINION)
		//		{
		//			Log.e("eli", "in get view opinion");
		//			holder.text.setTypeface(almoni_aaa_regular);
		//			holder.text.setText(((ArticleOpinion) newsSet.elementAt(position)).getTitle());
		//			holder.createdOn.setText(((ArticleOpinion) newsSet.elementAt(position)).getCreatedOn());
		//			//			holder.authorName.setText(((ArticleOpinion)newsSet.elementAt(position)).getAuthorName());
		//			holder.authorName.setText("eli");
		//			holder.image.setTag(((Article) newsSet.elementAt(position)).getImage());
		//			String imageToGet = ((Article) newsSet.elementAt(position)).getImage();
		//			if (getItemViewType(position) == -TYPE_ARTICLE_OPINION)
		//			{
		//				holder.text.setOnClickListener(new OnClickListener()
		//				{
		//					@Override
		//					public void onClick(View v)
		//					{
		//						LazyAdapter.this.getMainFragment().onclickFrontWihtoutTAGIT(position);
		//					}
		//				});
		//				holder.authorName.setOnClickListener(new OnClickListener()
		//				{
		//					@Override
		//					public void onClick(View v)
		//					{
		//
		//						LazyAdapter.this.getMainFragment().onclickFrontWihtoutTAGIT(position);
		//					}
		//				});
		//				holder.image.setOnClickListener(new OnClickListener()
		//				{
		//					@Override
		//					public void onClick(View v)
		//					{
		//						LazyAdapter.this.getMainFragment().onclickFrontWihtoutTAGIT(position);
		//					}
		//				});
		//			}
		//			if (((Article) holder.LinearLayoutBack.getTag()).getTagiot().size() > 0)
		//			{
		//
		//				if (holder.LinearLayoutBack.getChildCount() > 0)
		//				{
		//					holder.LinearLayoutBack.removeAllViews();
		//
		//				}
		//
		//				for (int i = 0; i < ((Article) holder.LinearLayoutBack.getTag()).getTagiot().size(); i++)
		//
		//				{
		//					LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(150, LinearLayout.LayoutParams.MATCH_PARENT);
		//					// params.setMargins(20, 20, 20, 20);
		//					params.weight = 1;
		//
		//					TextView textView = new TextView(activity);
		//					String tagTitle = ((Article) holder.LinearLayoutBack.getTag()).getTagiot().get(i).getSimplified();
		//					tagTitle = tagTitle.replaceAll("_", " ");
		//					textView.setText(tagTitle);
		//					textView.setBackgroundColor(pinkColor);
		//					textView.setTextColor(blackColor);
		//					textView.setTypeface(almoni_aaa_regular);
		//
		//					textView.setLayoutParams(params);
		//					textView.setGravity(Gravity.CENTER);
		//					textView.setPadding(10, 0, 10, 0);
		//
		//					// add Tagit as tag
		//					textView.setTag(((Article) holder.LinearLayoutBack.getTag()).getTagiot().get(i));
		//
		//					// add listener
		//					textView.setOnClickListener(new View.OnClickListener()
		//					{
		//
		//						@Override
		//						public void onClick(View v)
		//						{
		//							onTagitClick(v);
		//
		//						}
		//					});
		//
		//					LinearLayout.LayoutParams paramsForView = new LinearLayout.LayoutParams(1, LinearLayout.LayoutParams.MATCH_PARENT);
		//					paramsForView.weight = 0;
		//					View view = new View(activity);
		//					view.setBackgroundColor(grayColor);
		//					view.setLayoutParams(paramsForView);
		//					holder.LinearLayoutBack.addView(textView);
		//					holder.LinearLayoutBack.addView(view);
		//				}
		//
		//			}
		//
		//			if (!imageToGet.equals(""))
		//			{
		//
		//
		//				Picasso.with(activity).load(imageToGet.replaceAll("null", "").trim()).into(holder.image);
		//
		//				imageLoader.DisplayImage(imageToGet, activity, holder.image);
		//			}
		//			else
		//			{
		//				holder.image.setImageResource(R.drawable.stub_image_not_found);
		//			}
		//		}

		else if (getItemViewType(position) == TYPE_GROUP_PORTFOLIO)
		{
			holder.textViewGroupTitle.setTypeface(almoni_aaa_blod);
			holder.textViewGroupTitle.setText(((GroupPortfolio) newsSet.elementAt(position)).getTitle());

			holder.textViewGroupTitle.setTextColor(blueColor);

			holder.textViewGroupTitle.setOnClickListener(new OnClickListener()
			{

				@Override
				public void onClick(View v)
				{

					LazyAdapter.this.getMainFragment().onclickFrontWihtoutTAGIT(position);
				}
			});

		}
		else if (getItemViewType(position) == TYPE_GROUP)
		{
			holder.textViewGroupTitle.setTypeface(almoni_aaa_blod);
			holder.textViewGroupTitle.setText(((Group) newsSet.elementAt(position)).getTitle());
			if (((Group) newsSet.elementAt(position)).getTitle().equals("חדשות אחרונות")
					|| ((Group) newsSet.elementAt(position)).getTitle().equals("המלצות אנליסטים")
					|| ((Group) newsSet.elementAt(position)).getTitle().equals("על החברה"))
			{
				holder.textViewGroupTitle.setTextColor(blueColor);
			}

			holder.textViewGroupTitle.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					if (mainFragment != null)
					{
						mainFragment.onclickFrontWihtoutTAGIT(position);
					}
				}
			});

		}
		else if (getItemViewType(position) == TYPE_INNERGROUP || getItemViewType(position) == TYPE_INNERGROUP_SECTIONS)
		{
			holder.textViewInnerGroupTitle.setTypeface(almoni_aaa_blod);
			holder.textViewInnerGroupTitle.setText(((InnerGroup) newsSet.elementAt(position)).getTitle());

			if (((InnerGroup) newsSet.elementAt(position)).getTitle().equals("חדשות אחרונות")
					|| ((InnerGroup) newsSet.elementAt(position)).getTitle().equals("המלצות אנליסטים")
					|| ((InnerGroup) newsSet.elementAt(position)).getTitle().equals("על החברה"))
			{
				holder.textViewInnerGroupTitle.setTextColor(blueColor);

			}
			final TextView finalTextViewInnerGroupTitle = holder.textViewInnerGroupTitle;
			holder.textViewInnerGroupTitle.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					// TODO eli - click on mador
					//					Log.i("eli", finalTextViewInnerGroupTitle.getText().toString());
					Utils.writeEventToGoogleAnalytics(activity, "עמוד הבית", "לחיצה על כותרות מדורים", finalTextViewInnerGroupTitle.getText().toString());
					LazyAdapter.this.getMainFragment().onclickFrontWihtoutTAGIT(position);
				}
			});
		}

		else if (getItemViewType(position) == TYPE_INSTRUMENT)
		{
			Instrument instrument = ((Instrument) holder.checkBoxAddToLooperFromMarketsList.getTag());
			if ((instrument.getHe()).equals("הרכב המדד"))
			{
				holder.checkBoxAddToLooperFromMarketsList.setVisibility(View.INVISIBLE);
				holder.checkBoxAddToLooperFromMarketsList.setClickable(false);
			}
			else
			{
				holder.checkBoxAddToLooperFromMarketsList.setVisibility(View.VISIBLE);
				holder.checkBoxAddToLooperFromMarketsList.setClickable(true);

				if (looper.isInLooper(instrument))
				{
					holder.checkBoxAddToLooperFromMarketsList.setChecked(true);
				}
				else
				{
					holder.checkBoxAddToLooperFromMarketsList.setChecked(false);
				}
			}

			holder.text.setText(((Instrument) newsSet.elementAt(position)).getHe());
			holder.instrumentLast.setText(((Instrument) newsSet.elementAt(position)).getLast());
			holder.image.setTag((Instrument) newsSet.elementAt(position));

			if (((Instrument) newsSet.elementAt(position)).getPercentage_change().length() <= 2)
			{
				holder.image.setImageResource(R.drawable.transparent);
			}
			else if (((Instrument) newsSet.elementAt(position)).getPercentage_change().contains("-"))
			{
				holder.image.setImageResource(R.drawable.stockdown);
				holder.instrumentPercentageChange.setTextColor(0xffce0f0f); /* Red */
			}
			else
			{
				holder.image.setImageResource(R.drawable.stockup);
				holder.instrumentPercentageChange.setTextColor(0xff1aad20); /* green */
			}

			holder.instrumentPercentageChange.setText(((Instrument) newsSet.elementAt(position)).getPercentage_change());

			holder.image.setOnClickListener(new OnClickListener()
			{

				@Override
				public void onClick(View v)
				{

					if (LazyAdapter.this.getShareActivity() != null)
					{

						LazyAdapter.this.getShareActivity().onclickFrontWihtoutTAGIT(position);
					}
				}
			});

			holder.text.setOnClickListener(new OnClickListener()
			{

				@Override
				public void onClick(View v)
				{

					if (LazyAdapter.this.getShareActivity() != null)
					{

						LazyAdapter.this.getShareActivity().onclickFrontWihtoutTAGIT(position);
					}
				}
			});

			holder.image.setOnClickListener(new OnClickListener()
			{

				@Override
				public void onClick(View v)
				{

					if (LazyAdapter.this.getShareActivity() != null)
					{

						LazyAdapter.this.getShareActivity().onclickFrontWihtoutTAGIT(position);
					}
				}
			});

		}
		else if (getItemViewType(position) == TYPE_STOCK)
		{
			Stock stock = ((Stock) newsSet.elementAt(position));
			// if (looper.isInLooper(stock))
			// {
			// holder.checkBoxAddToLooperFromSharesList.setChecked(true);
			// }
			// else
			// {
			// holder.checkBoxAddToLooperFromSharesList.setChecked(false);
			// }
			holder.text.setText(((Stock) newsSet.elementAt(position)).getName_he());

			if (((Stock) newsSet.elementAt(position)).getPercentage_change().length() <= 2)
				holder.image.setImageResource(R.drawable.transparent);
			else if (((Stock) newsSet.elementAt(position)).getPercentage_change().contains("-"))
			{
				// eli delet image - same in iphone
				//				holder.image.setImageResource(R.drawable.stockdown);
				holder.stockPercentageChange.setTextColor(0xffce0f0f);// red
			}
			else
			{
				// eli delet image - same in iphone
				//				holder.image.setImageResource(R.drawable.stockup);
				holder.stockPercentageChange.setTextColor(0xff1aad20);// green
			}
			// eli percent
			String percentChange = ((Stock) newsSet.elementAt(position)).getPercentage_change();
			percentChange = percentChange.compareTo("")==0 ? "0%" : percentChange;
			holder.stockPercentageChange.setText(percentChange);

			holder.stockPercentageChange.setOnClickListener(new OnClickListener()
			{

				@Override
				public void onClick(View v)
				{
					String uri = GlobesURL.URLSharePage.replace("XXXX", "tase").replace("ZZZZ", "d")
							.replace("YYYY", (((Stock) newsSet.elementAt(position)).getId()));
					// eli
					//					Log.e("eli", uri);
					Intent intent = new Intent(activity, ShareActivity.class);
					intent.putExtra("URI", uri);
					intent.putExtra("symbol", (((Stock) newsSet.elementAt(position)).getSymbol()));
					intent.putExtra(Definitions.CALLER, Definitions.SHARES);
					intent.putExtra(Definitions.PARSER, Definitions.SHARES);
					intent.putExtra("name", (((Stock) newsSet.elementAt(position)).getName_he()));
					intent.putExtra("shareId", (((Stock) newsSet.elementAt(position)).getId()));
					intent.putExtra("instrumentIdForSharing", (((Stock) newsSet.elementAt(position)).getId()));
					// indicate we are from ShareListActivity
					intent.putExtra(ShareActivity.KEY_FROM_MARKETS_OR_STOCKS_LIST, true);
					final int SHARE_PAGE = 5;

					activity.startActivityForResult(intent, SHARE_PAGE);
				}
			});

			holder.text.setOnClickListener(new OnClickListener()
			{

				@Override
				public void onClick(View v)
				{
					//					Log.i("eli",  "לחיצה על תוצאת חיפוש");
					Utils.writeEventToGoogleAnalytics(activity, "תפריט", "פורטל פיננסי", "לחיצה על תוצאת חיפוש");

					String uri = GlobesURL.URLSharePage.replace("XXXX", "tase").replace("ZZZZ", "d")
							.replace("YYYY", (((Stock) newsSet.elementAt(position)).getId()));

					Intent intent = new Intent(activity, ShareActivity.class);
					intent.putExtra("URI", uri);
					intent.putExtra("symbol", (((Stock) newsSet.elementAt(position)).getSymbol()));
					intent.putExtra(Definitions.CALLER, Definitions.SHARES);
					intent.putExtra(Definitions.PARSER, Definitions.SHARES);
					intent.putExtra("name", (((Stock) newsSet.elementAt(position)).getName_he()));
					intent.putExtra("shareId", (((Stock) newsSet.elementAt(position)).getId()));
					intent.putExtra("instrumentIdForSharing", (((Stock) newsSet.elementAt(position)).getId()));
					// indicate we are from ShareListActivity
					intent.putExtra(ShareActivity.KEY_FROM_MARKETS_OR_STOCKS_LIST, true);
					final int SHARE_PAGE = 5;

					activity.startActivityForResult(intent, SHARE_PAGE);
				}
			});

		}
		else if (getItemViewType(position) == TYPE_SHARE_ANALYST_REC)
		{
			holder.textViewAnalystName.setText(((ShareAnalystRecommendation) newsSet.elementAt(position)).getAnalystName());
			holder.textViewRecommendationDate.setText(((ShareAnalystRecommendation) newsSet.elementAt(position)).getDate());
			holder.textViewRecommendation.setText(((ShareAnalystRecommendation) newsSet.elementAt(position)).getRank());

			holder.textViewAnalystName.setOnClickListener(new OnClickListener()
			{

				@Override
				public void onClick(View v)
				{

					if (LazyAdapter.this.getShareActivity() != null)
					{

						LazyAdapter.this.getShareActivity().onclickFrontWihtoutTAGIT(position);
					}
				}
			});
			holder.textViewRecommendationDate.setOnClickListener(new OnClickListener()
			{

				@Override
				public void onClick(View v)
				{

					if (LazyAdapter.this.getShareActivity() != null)
					{

						LazyAdapter.this.getShareActivity().onclickFrontWihtoutTAGIT(position);
					}
				}
			});
			holder.textViewRecommendation.setOnClickListener(new OnClickListener()
			{

				@Override
				public void onClick(View v)
				{

					if (LazyAdapter.this.getShareActivity() != null)
					{

						LazyAdapter.this.getShareActivity().onclickFrontWihtoutTAGIT(position);
					}
				}
			});

		}
		else if (getItemViewType(position) == TYPE_SHARE_COMPANY_DESC)
		{
			holder.text.setText(((ShareCompanyDescription) newsSet.elementAt(position)).getCompanyDescription());
		}
		// else if (getItemViewType(position)==TYPE_SHARE_DATA)
		// {
		// ShareData tempShareData = ((ShareData)newsSet.elementAt(position));
		// holder.textViewShareSymbol.setText("îñ' ðé\"ò:	" +
		// tempShareData.getShareSymbol());
		// holder.textViewTrueForDate.setText(" ðëåï ìúàøéê  " +
		// tempShareData.getTimestamp());
		// holder.textViewPrecentageChangeInHeader.setText(tempShareData.getDailyPercentageChange());
		// holder.textViewLastPrice.setText("ùòø àçøåï:	" +
		// tempShareData.getLastPrice());
		// holder.textViewPrecentageChange.setText(tempShareData.getDailyPercentageChange());
		// holder.textViewDailyPointsChange.setText(tempShareData.getDailyPointsChange());
		// holder.textViewShareVolume.setText(tempShareData.getShareVolume());
		// holder.textViewDailyHigh.setText(tempShareData.getDailyHigh());
		// holder.textViewDailyLow.setText(tempShareData.getDailyLow());
		// holder.textViewShareMarketCap.setText(tempShareData.getShareMarketCap());
		//
		// //
		// //holder.buttonStockAddCncl.setBackgroundResource(R.color.button_pf_share_add);
		// setBtnAddCnclPfShare(holder.buttonStockAddCncl);
		//
		// String imageToGet;
		// if
		// (((ShareData)newsSet.elementAt(position)).getShareType().equals("currency"))
		// {
		// imageToGet = Definitions.URLForexGraph;
		// }
		// else
		// {
		// imageToGet = Definitions.URLShareGraph;
		// }
		// try
		// {
		// imageToGet = imageToGet
		// .replace("EEEE",
		// ((ShareData)newsSet.elementAt(position)).getExchangeInitials())
		// .replace("XXXX", "d")
		// .replace("SSSS",
		// URLEncoder.encode(tempShareData.getShareSymbol(),"windows-1255"))
		// .replace("WWWW","155")
		// .replace("HHHH", "118");
		// Bitmap bitmap = Utils.DownloadImage(imageToGet);
		// holder.image.setImageBitmap(bitmap);
		// }
		// catch (UnsupportedEncodingException e)
		// {}
		// //imageLoader.DisplayImage(imageToGet, activity, holder.image);
		//
		// if (tempShareData.getDailyPercentageChange().length()<=2)
		// holder.imageViewUpDownArrow.setImageResource(R.drawable.transparent);
		// else if (tempShareData.getDailyPercentageChange().contains("-"))
		// {
		// holder.imageViewUpDownArrow.setImageResource(R.drawable.stockdown);
		// holder.textViewPrecentageChangeInHeader.setTextColor(0xffce0f0f);
		// /*Set color to Custom Red*/
		// }
		// else
		// {
		// holder.imageViewUpDownArrow.setImageResource(R.drawable.stockup);
		// holder.textViewPrecentageChangeInHeader.setTextColor(0xff1aad20);
		// /*Set color to Custom Green*/
		// }
		// }

		else if (getItemViewType(position) == TYPE_SHARE_NEWS_ART)
		{
			holder.textViewArticleDate.setText(((ShareNewsArticle) newsSet.elementAt(position)).getCreatedOn());
			holder.textViewArticleHeader.setText(((ShareNewsArticle) newsSet.elementAt(position)).getTitle());
			holder.textViewArticleDate.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					if (LazyAdapter.this.getShareActivity() != null)
						LazyAdapter.this.getShareActivity().onclickFrontWihtoutTAGIT(position);
				}
			});
			holder.textViewArticleHeader.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					if (LazyAdapter.this.getShareActivity() != null)
						LazyAdapter.this.getShareActivity().onclickFrontWihtoutTAGIT(position);
				}
			});
		}

		else if (getItemViewType(position) == TYPE_PORTFOLIO_INSTRUMENT)
		{

			PortfolioInstrument portfolioInstrument = ((PortfolioInstrument) holder.checkBoxAddToLooperFromPortfolioList.getTag());
			if (looper.isInLooper(portfolioInstrument))
			{
				holder.checkBoxAddToLooperFromPortfolioList.setChecked(true);
			}
			else
			{
				holder.checkBoxAddToLooperFromPortfolioList.setChecked(false);
			}
			holder.text.setText(((PortfolioInstrument) newsSet.elementAt(position)).getHe());
			holder.instrumentLast.setText(((PortfolioInstrument) newsSet.elementAt(position)).getLast());

			if (((PortfolioInstrument) newsSet.elementAt(position)).getPercentage_change().length() <= 2)
			{
				// eli change
				holder.instrumentPercentageChange.setTextColor(colorGreyInstrumentText);
			}
			else if (((PortfolioInstrument) newsSet.elementAt(position)).getPercentage_change().contains("-"))
			{
				holder.instrumentPercentageChange.setTextColor(0xffce0f0f);//red
			}
			else
			{
				holder.instrumentPercentageChange.setTextColor(0xff1aad20);//green
			}
			holder.instrumentPercentageChange.setText(((PortfolioInstrument) newsSet.elementAt(position)).getPercentage_change());


			// eli change
			String thePercentChange = ((PortfolioInstrument) newsSet.elementAt(position)).getChangeSinceAdded();
			thePercentChange = !TextUtils.isEmpty(thePercentChange) ? thePercentChange : "0" ;
			thePercentChange = thePercentChange.replace("%", "");
			thePercentChange = thePercentChange.trim();
			Double f = Double.parseDouble(thePercentChange);
			if (f == 0)
			{
				holder.pfPercentageChangeSinceAdded.setTextColor(colorGreyInstrumentText);
				holder.pfPercentageChangeSinceAdded.setBackgroundColor(colorGreyInstrumentBackground);
			}
			else if (f > 0 && f < 5)
			{
				holder.pfPercentageChangeSinceAdded.setTextColor(colorLightGreenInstrumentText);
				holder.pfPercentageChangeSinceAdded.setBackgroundColor(colorLightGreenInstrumentBackground);
				// light green
			}
			else if (f >= 5)
			{
				// dark green
				holder.pfPercentageChangeSinceAdded.setTextColor(colorDarktGreenInstrumentText);
				holder.pfPercentageChangeSinceAdded.setBackgroundColor(colorLDarkGreenInstrumentBackground);
			}
			else if (f < -5)
			{
				// dark red
				holder.pfPercentageChangeSinceAdded.setTextColor(colorLDarkRedInstrumentText);
				holder.pfPercentageChangeSinceAdded.setBackgroundColor(colorDarkRedInstrumentBackground);
			}
			else if (f < 0 && f > -5)
			{
				// red light
				holder.pfPercentageChangeSinceAdded.setTextColor(colorLightRedInstrumentText);
				holder.pfPercentageChangeSinceAdded.setBackgroundColor(colorLightRedInstrumentBackground);

			}


			// // percentage changed since added
			// if (((PortfolioInstrument)
			// newsSet.elementAt(position)).getChangeSinceAdded().length() <= 2)
			// {
			// // holder.image.setImageResource(R.drawable.transparent);
			// }
			// else if (((PortfolioInstrument)
			// newsSet.elementAt(position)).getChangeSinceAdded().contains("-"))
			// {
			// holder.pfPercentageChangeSinceAdded.setTextColor(0xffce0f0f); /*
			// * Set
			// * color
			// * to
			// * Custom
			// * Red
			// */
			// }
			// else
			// {
			// holder.pfPercentageChangeSinceAdded.setTextColor(0xff1aad20); /*
			// * Set
			// * color
			// * to
			// * Custom
			// * Green
			// */
			// }
			// eli change
			thePercentChange = ((PortfolioInstrument) newsSet.elementAt(position)).getChangeSinceAdded();
			thePercentChange = !TextUtils.isEmpty(thePercentChange) ? thePercentChange : "0%" ;
			holder.pfPercentageChangeSinceAdded.setText(thePercentChange);

			final PortfolioInstrument instrument = ((PortfolioInstrument) newsSet.elementAt(position));

			holder.pfPercentageChangeSinceAdded.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					// if(parsedNewsSet.itemHolder.get(newPosition).getClass()
					// ==
					// PortfolioInstrument.class)
					// {
					String uri = GlobesURL.URLSharePage.replace("XXXX", instrument.getFeeder()).replace("YYYY", instrument.getId());

					Intent showInstrument = new Intent(activity, ShareActivity.class);
					showInstrument.putExtra("URI", uri);

					showInstrument.putExtra(Definitions.CALLER, Definitions.PORTFOLIO);
					showInstrument.putExtra(Definitions.PARSER, Definitions.SHARES);
					showInstrument.putExtra(Definitions.ISINSTRUMENT, instrument.isIndexWithInstruments());
					showInstrument.putExtra("feederId", instrument.getFeeder());
					showInstrument.putExtra("shareId", instrument.getId());
					showInstrument.putExtra("name", instrument.getHe());
					showInstrument.putExtra("isPfItem", true);
					// activity.startActivity(showInstrument);
					final int SHARE_PAGE = 5;

					activity.startActivityForResult(showInstrument, SHARE_PAGE);
				}
			});

			holder.instrumentPercentageChange.setOnClickListener(new OnClickListener()
			{

				@Override
				public void onClick(View v)
				{
					// if(parsedNewsSet.itemHolder.get(newPosition).getClass()
					// ==
					// PortfolioInstrument.class)
					// {
					String uri = GlobesURL.URLSharePage.replace("XXXX", instrument.getFeeder()).replace("YYYY", instrument.getId());

					Intent showInstrument = new Intent(activity, ShareActivity.class);
					showInstrument.putExtra("URI", uri);

					showInstrument.putExtra(Definitions.CALLER, Definitions.PORTFOLIO);
					showInstrument.putExtra(Definitions.PARSER, Definitions.SHARES);
					showInstrument.putExtra(Definitions.ISINSTRUMENT, instrument.isIndexWithInstruments());
					showInstrument.putExtra("feederId", instrument.getFeeder());
					showInstrument.putExtra("shareId", instrument.getId());
					showInstrument.putExtra("name", instrument.getHe());
					showInstrument.putExtra("isPfItem", true);
					// activity.startActivity(showInstrument);
					final int SHARE_PAGE = 5;

					activity.startActivityForResult(showInstrument, SHARE_PAGE);
				}
			});
			holder.text.setOnClickListener(new OnClickListener()
			{

				@Override
				public void onClick(View v)
				{
					// if(parsedNewsSet.itemHolder.get(newPosition).getClass()
					// ==
					// PortfolioInstrument.class)
					// {

					// TODO eli - lunch meniot
					//					Log.e("eli", "new method " + getFeederNew(instrument.getId()));


					String uri = GlobesURL.URLSharePage.replace("XXXX", instrument.getFeeder()).replace("YYYY", instrument.getId());

					Intent showInstrument = new Intent(activity, ShareActivity.class);
					showInstrument.putExtra("URI", uri);

					showInstrument.putExtra(Definitions.CALLER, Definitions.PORTFOLIO);
					showInstrument.putExtra(Definitions.PARSER, Definitions.SHARES);
					showInstrument.putExtra(Definitions.ISINSTRUMENT, instrument.isIndexWithInstruments());
					showInstrument.putExtra("feederId",  getFeederNew(instrument.getId()));
					showInstrument.putExtra("shareId", instrument.getId());
					showInstrument.putExtra("name", instrument.getHe());
					showInstrument.putExtra("isPfItem", true);
					// activity.startActivity(showInstrument);
					final int SHARE_PAGE = 5;

					//					Toast.makeText(activity, instrument.getFeeder(), Toast.LENGTH_LONG).show();
					activity.startActivityForResult(showInstrument, SHARE_PAGE);
				}

				//				TODO eli
				private String getFeederNew(final String shareId)
				{
					//					Log.e("eli", "getFeederNew shareId " + shareId);
					String globesAccessKey = DataContext.Instance().getAccessKey();
					final String URLPortfolioLink="http://www.globes.co.il/apps/portfolios.asmx/GetPortfolios?Mode=4&PortfolioId=&accessKey="+globesAccessKey;
					try
					{
						return new AsyncTask<Void, Void, String>()
								{
							@Override
							protected String doInBackground(Void... params)
							{
								HttpGet uri = new HttpGet(URLPortfolioLink);    
								DefaultHttpClient client = new DefaultHttpClient();
								HttpResponse resp = null;
								try	{resp = client.execute(uri);}
								catch (ClientProtocolException e){e.printStackTrace();	}
								catch (IOException e){	e.printStackTrace();}
								InputStream is = null;
								try
								{
									is = resp.getEntity().getContent();
								}
								catch (IllegalStateException e1)
								{
									e1.printStackTrace();
								}
								catch (IOException e1)
								{
									e1.printStackTrace();
								}
								final String INSTRUMENT_ID = "InstrumentId";
								final String FEEDER = "feeder";
								final String ERROR_MESSAGE = "Error";

								try
								{
									XmlPullParser parser = Xml.newPullParser();
									parser.setInput(is, null);
									int eventType = parser.getEventType();
									boolean done = false;
									String prevFeedder = "";
									while (eventType != XmlPullParser.END_DOCUMENT && !done)
									{
										switch (eventType)
										{
											case XmlPullParser.START_TAG :
												String name = parser.getName();
												if (name.equalsIgnoreCase(FEEDER))
												{
													prevFeedder = parser.nextText();
													//													Log.e("eli","prevFeedder " +  prevFeedder);
												}
												if (name.equalsIgnoreCase(INSTRUMENT_ID))
												{
													if ( parser.nextText().equalsIgnoreCase(shareId))
													{
														//														Log.e("eli","prevFeedder returned " +  prevFeedder);
														done = true;
														return prevFeedder;
													}
												}
												if (name.equalsIgnoreCase(ERROR_MESSAGE))
												{
													done = true;
												}
											case XmlPullParser.END_TAG :
												break;
										}
										eventType = parser.next();
									}
								}
								catch (Exception e)
								{
									throw new RuntimeException(e);
								}
								return "";
							}
								}.execute().get();
					}
					catch (InterruptedException e)
					{
						e.printStackTrace();
					}
					catch (ExecutionException e)
					{
						e.printStackTrace();
					}
					return "";

				}
			});

		}
		// else if (getItemViewType(position) == TYPE_BANNER) {

		// {
		// // TODO load the Ad
		// // return holder.AdView;
		// holder.AdView.loadAd(new AdRequest());
		// // holder.image.setTag(((Banner)
		// // newsSet.elementAt(position)).getBannerURL());
		// // String imageToGet = ((Banner)
		// // newsSet.elementAt(position)).getBannerURL();
		// // imageLoader.DisplayImage(imageToGet, activity, holder.image);
		// // return adView;
		// }
		else if (getItemViewType(position) == TYPE_DFP_AD_VIEW_BETWEEN_ARTICLES)
		{
			if (!Definitions.toUseAjillion)
			{
				if (holder.adViewBetweenArticlesPublisher != null)
				{
					Log.e("alex", "YYYYYYYYYYYYYYYYYY 1");
					
					//PublisherAdRequest request = new PublisherAdRequest.Builder().addTestDevice("7BBA10612964F988FEC2CB656AD07570").build();

					//PublisherAdRequest request = new PublisherAdRequest.Builder().build();
					//holder.adViewBetweenArticlesPublisher.loadAd(request);

					// holder.adViewBetweenArticles.loadAd(new AdRequest());
				}
			}
		}
		else if (getItemViewType(position) == TYPE_DFP_AD_VIEW_KATAVA_PIRSUMIT_MAIN_LIST)
		{
			if (holder.AdViewKatavaPirsumitMainListPublisher != null)
			{
				Log.e("alex", "YYYYYYYYYYYYYYYYYY 2: " + holder.AdViewKatavaPirsumitMainListPublisher.toString());
				
				//PublisherAdRequest request = new PublisherAdRequest.Builder().addTestDevice("7BBA10612964F988FEC2CB656AD07570").build();


//				PublisherAdRequest request = new PublisherAdRequest.Builder().build();
//				holder.AdViewKatavaPirsumitMainListPublisher.loadAd(request);


				// holder.AdViewKatavaPirsumitMainList.loadAd(new AdRequest());
			}
		}
		else if (getItemViewType(position) == TYPE_DFP_AD_VIEW_KATAVA_PIRSUMIT_NADLAN)
		{			
			//Log.e("alex", "position");
			
			if(Definitions.show_dfp_banners_on_splash_screen)
			{			
				//if (holder.AdViewKatavaPirsumitNadlanPublisher != null)
				{	
					Log.e("alex", "YYYYYYYYYYYYYYYYYY 3");
					
					//PublisherAdRequest request = new PublisherAdRequest.Builder().addTestDevice("7BBA10612964F988FEC2CB656AD07570").build();

					//PublisherAdRequest request = new PublisherAdRequest.Builder().build();
					//holder.AdViewKatavaPirsumitNadlanPublisher.loadAd(request);

				}
			}
			else
			{
				if(Definitions.wallafusion)
            	{					
					//isSponsoredArticles = true;
            		isTopHeaderBanner = false;
            		rlSponsoredArticles = holder.AdViewKatavaPirsumitNadlanPublisher;
            		
            	//	WallaAdvParser wap = new WallaAdvParser(activity.getApplicationContext(), activity.getApplication(), this, sWallaPage, sWallaLayout, "", 0);
				//	wap.execute();
					
            		/*
            		if(globalItemPosition != position)
            		{
            			Log.e("alex", "HHHHH Katava Pirsumit 2: " + position);
            			globalItemPosition = position;
            			WallaAdvParser wap = new WallaAdvParser(activity.getApplicationContext(), activity.getApplication(), this, sWallaPage, sWallaLayout, "", 0);
						wap.execute();
            		}
					*/
					
//					isSponsoredArticles = true;
//					rlSponsoredArticles = holder.AdViewKatavaPirsumitNadlanPublisher;
//					
//	            	if(!walla_adv_all_sponsored_articles_items.contains(position))
//					{
//	            		walla_adv_all_sponsored_articles_items.add(position);
//					}
//					
//					if(!walla_adv_items.contains(position)) //for fast scrolling to avoid double calling to walla
//					{												
//						walla_adv_items_counter++;
//						walla_adv_position = position;
//						walla_adv_items.add(position);
//						WallaAdvParser wap = new WallaAdvParser(activity.getApplicationContext(), activity.getApplication(), this, "homepage", "l_globes_app_h", "", 0);
//						wap.execute();
//					}
//	            	
//					removeItemFromWallaList();
            	}
				else
				{
					WebView w = new WebView(activity.getApplicationContext());
					showPositiveModileAd(w, Definitions.AD_POSITIVEMOBILE_NADLAN, holder.AdViewKatavaPirsumitNadlanPublisher, position);
				}
			}
			
		}else if(getItemViewType(position) == TYPE_LIVE_BOX){
			
			
			Log.e("alex", "LiveBox on Lazy!!!!!");
			
//			LiveBoxModel boxModel = (LiveBoxModel)newsSet.elementAt(position);
//			
//			String articleTextHtml = boxModel.getUrlLiveBoxMain();
//			try
//			{
//				articleTextHtml = encodeTextForWebView(articleTextHtml, EncodeType.TYPE_BODY);
//			}
//			catch (UnsupportedEncodingException e)
//			{
//				Log.e("eli", "erorr in setAllViews", e);
//			}
//			WebSettings webViewSettings = holder.webView_live_box.getSettings();
//			webViewSettings.setJavaScriptCanOpenWindowsAutomatically(false);
//			webViewSettings.setJavaScriptEnabled(true);
//			holder.webView_live_box.setWebChromeClient(new WebChromeClient());
//			webViewSettings.setUseWideViewPort(false);
//			webViewSettings.setBuiltInZoomControls(false);
//			webViewSettings.setLoadWithOverviewMode(false);
//			webViewSettings.setSupportZoom(false);
//			webViewSettings.setPluginState(PluginState.ON);
//
//			
//			holder.webView_live_box.loadData(articleTextHtml, "text/html; charset=utf-8", "UTF-8");
//			
			
			

			
		}

		if (holder != null && Definitions.flipAlignment)
		{
			if (holder.text != null) holder.text.setGravity(android.view.Gravity.RIGHT);
			if (holder.authorName != null) holder.authorName.setGravity(android.view.Gravity.RIGHT);

			if (holder.textViewAnalystName != null) holder.textViewAnalystName.setGravity(android.view.Gravity.RIGHT);
			if (holder.textViewRecommendation != null) holder.textViewRecommendation.setGravity(android.view.Gravity.RIGHT);
			if (holder.textViewShareSymbol != null) holder.textViewShareSymbol.setGravity(android.view.Gravity.RIGHT);
			if (holder.textViewTrueForDate != null) holder.textViewTrueForDate.setGravity(android.view.Gravity.RIGHT);
			if (holder.textViewLastPrice != null) holder.textViewLastPrice.setGravity(android.view.Gravity.RIGHT);
			if (holder.textViewPrecentageChange != null) holder.textViewPrecentageChange.setGravity(android.view.Gravity.RIGHT);
			if (holder.textViewDailyPointsChange != null) holder.textViewDailyPointsChange.setGravity(android.view.Gravity.RIGHT);
			if (holder.textViewShareVolume != null) holder.textViewShareVolume.setGravity(android.view.Gravity.RIGHT);
			if (holder.textViewDailyHigh != null) holder.textViewDailyHigh.setGravity(android.view.Gravity.RIGHT);
			if (holder.textViewDailyLow != null) holder.textViewDailyLow.setGravity(android.view.Gravity.RIGHT);
			if (holder.textViewShareMarketCap != null) holder.textViewShareMarketCap.setGravity(android.view.Gravity.RIGHT);
			if (holder.textViewArticleHeader != null) holder.textViewArticleHeader.setGravity(android.view.Gravity.RIGHT);
			if (holder.instrumentLast != null)
				holder.instrumentLast.setGravity(android.view.Gravity.CENTER_VERTICAL | android.view.Gravity.RIGHT);
			if (holder.instrumentPercentageChange != null)
				holder.instrumentPercentageChange.setGravity(android.view.Gravity.CENTER_VERTICAL | android.view.Gravity.RIGHT);

			if (getItemViewType(position) == TYPE_INSTRUMENT || getItemViewType(position) == TYPE_STOCK
					|| getItemViewType(position) == TYPE_HEADER_FOREX || getItemViewType(position) == TYPE_HEADER_FUTURES
					|| getItemViewType(position) == TYPE_HEADER_INSTRUMENTS || getItemViewType(position) == TYPE_HEADER_SHARES
					|| getItemViewType(position) == TYPE_INNERGROUP_SECTIONS || getItemViewType(position) == TYPE_PORTFOLIO_INSTRUMENT)
			{
				if (holder.text != null) holder.text.setGravity(android.view.Gravity.CENTER_VERTICAL | android.view.Gravity.RIGHT);
			}
		}
		//		Log.e("eli", "tag=" + getItemViewType(position));
		
		return rowView;
	}
	/**
	 * 
	 * @param type
	 *            - Dialog type Add/Remove item
	 * @param itemNameHE
	 *            the added/removed item nameHE
	 */
	private void showDialog(int type, String itemNameHE)
	{

		AlertDialog.Builder ad = new AlertDialog.Builder(activity);

		ad.setCancelable(false);
		ad.setTitle(R.string.title_update_looper_dialog);
		StringBuilder msg = new StringBuilder();
		msg.append("נייר ערך ");
		msg.append(itemNameHE);
		msg.append(" ");

		switch (type)
		{
			case DIALOG_TYPE_ADD_ITEM :
				msg.append("נוסף ל");
				break;
			case DIALOG_TYPE_REMOVE_ITEM :
				msg.append("הוסר מ");
				break;
		}
		msg.append("פס המדדים - פסנוע המדדים יעודכן אוטומטית");

		ad.setMessage(msg.toString());

		ad.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int which)
			{
				
				alertDialog.dismiss();
				return;
			}
		});
		alertDialog = ad.create();
		alertDialog.show();
	}

	private void showDialogCannotRemoveItem()
	{
		AlertDialog.Builder ad = new AlertDialog.Builder(activity);
		ad.setCancelable(false);
		ad.setTitle(R.string.title_update_looper_dialog);
		StringBuilder msg = new StringBuilder();
		msg.append("נייר ערך לא הוסר מפס המדדים");
		msg.append("מספר ניירות ערך מינימלי בפס מדדים הוא שלושה ");
		ad.setMessage(msg.toString());

		ad.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int which)
			{
				alertDialog.dismiss();
				notifyDataSetChanged();
				return;
			}
		});
		alertDialog = ad.create();
		alertDialog.show();
	}

	private synchronized void onTagitClick(View v)
	{
		Tagit tagit = (Tagit) v.getTag();
		
		try
		{
			MainSlidingActivity msa = (MainSlidingActivity)mCallback;
			if (msa.getIsRegularMador() && !msa.getIsMainPageLoading())
			{
				Utils.writeEventToGoogleAnalytics(activity, "תגיות על הקלקות", "tagit_node_"+msa.getActionBarTitle(), null);
			}
			else if (msa.getIsMainPageLoading()) 
			{
				Utils.writeEventToGoogleAnalytics(activity, "תגיות על הקלקות", "tagit_section_"+tagit.getSection(), null);
			}
			else 
			{
				Utils.writeEventToGoogleAnalytics(activity, "תגיות על הקלקות", "tagit_tagit_"+msa.getActionBarTitle(), null);
			}
		}
		catch (Exception e)
		{
			Log.e("eli", "error", e);
		}
		// eli tagit click
		// get the correct tagit

		// create the fragment
		SectionListFragment fragment = new SectionListFragment();

		// set uri
		String uri = GlobesURL.URLTagitArticlesByID.replace("<TAGIT_ID>", tagit.getId());
		String parser = Definitions.MAINSCREEN;
		String caller = Definitions.SECTIONS;
		String header = tagit.getSimplified();

		// set bundle to fragment
		Bundle args = new Bundle();
		args.putString("isFromTagit", "1");
		args.putString(Definitions.URI_TO_PARSE, uri);
		args.putString(Definitions.CALLER, caller);
		args.putString(Definitions.PARSER, parser);
		args.putString(Definitions.HEADER, header);
		fragment.setArguments(args);

		// switch content
		mCallback.onSwitchContentFragment(fragment, SectionListFragment.class.getSimpleName(), true, false, false);
	}

	public MainFragment getMainFragment()
	{
		return mainFragment;
	}

	public void setMainFragment(MainFragment mainFragment)
	{
		this.mainFragment = mainFragment;
	}

	public ShareActivity getShareActivity()
	{
		return shareActivity;
	}

	public void setShareActivity(ShareActivity shareActivity)
	{
		this.shareActivity = shareActivity;
	}

	class initAjillionView extends AsyncTask<Void, Void, Void>
	{
		private ImageView ajillionObjImage;
		private WebView ajillionObjImageGif;
		private AjillionObj ajillionObj;
		private RelativeLayout frame;
		private String id;

		public initAjillionView(RelativeLayout frame, String id)
		{
			super();
			this.frame = frame;
			this.id = id;
		}

		@Override
		protected void onPreExecute() 
		{
			ajillionObjImage = new ImageView(activity);
			ajillionObjImageGif = new WebView(activity);
			ajillionObjImage.setScaleType(ScaleType.FIT_XY);

			frame.setVisibility(View.VISIBLE);
			frame.removeAllViews();
			frame.setGravity(Gravity.CENTER);
			//frame.addView(ajillionObjImage, RelativeLayout.LayoutParams.MATCH_PARENT, 60);
			
			//frame.setBackgroundColor(Color.RED);		
	/*
			String data = "<html dir=\"rtl\" ><meta charset=\"utf-8\" />";
		    data += "<head><img src=\"http://stage.globes.co.il/news/test/alex/1.jpeg\" width=\"320\" height=\"50\"/></head>";
		    data += "</html>";
		    WebView wv = new WebView(activity.getApplicationContext());
		    wv.loadData(data,"text/html; charset=utf-8", "UTF-8");
		    frame.addView(wv);
		    
	*/
			//frame.addView(ajillionObjImage, RelativeLayout.LayoutParams.MATCH_PARENT, 120);

						
			try
			{
				DisplayMetrics metrics = activity.getApplicationContext().getResources().getDisplayMetrics();
				int iDensity = (int)metrics.density;
				int iBannerHeight = (iDensity > 0) ? (int)(Ajillion_Obj_Height * iDensity) : Ajillion_Obj_Height;
				int iBannerWidth = (iDensity > 0) ? (int)(Ajillion_Obj_Width * iDensity) : Ajillion_Obj_Width;
				
				int iBannerHeight_v2 = (int)((metrics.widthPixels * Ajillion_Obj_Height)/Ajillion_Obj_Width); //לפי פרופורציה
				
				frame.setPadding(0, 0, 0, 10);
				
				//frame.addView(ajillionObjImage, iBannerWidth, iBannerHeight);
				frame.addView(ajillionObjImage, RelativeLayout.LayoutParams.MATCH_PARENT, iBannerHeight_v2);
							
				//Log.e("alex","URLDocument: " + GlobesURL.URLDocument);
				
				//Log.e("alex", "BannerWidth: " + iBannerWidth + "x" + iBannerHeight + "===" + metrics.widthPixels + "x" + metrics.heightPixels);
			}
			catch(Exception ex)
			{
			    Log.e("alex", "Error onPreExecute Ajillion: " + ex);
			}
		}

		@Override
		protected Void doInBackground(Void... params)
		{			
			//Log.e("alex", "BANNER1:" + id);
			ajillionObj = Utils.getAjillionObj(activity, id, Ajillion_Obj_Width, Ajillion_Obj_Height, false);
			return null;
		}

		@Override
		protected void onPostExecute(Void result)
		{
			super.onPostExecute(result);
			//Log.e("alex", "BANNER:" + ajillionObj.getCreative_type());
			if (ajillionObj == null || ajillionObj.getSuccess()==null ||!ajillionObj.getSuccess() || ajillionObj.getCreative_url() == null || ajillionObj.getCreative_url().equals(""))
			{
				return;
			}
			else if(ajillionObj.getCreative_type().equals("image"))
			{
				if (!ajillionObj.getCreative_url().endsWith(".gif"))
				{
					Log.e("alex", "ajillionObj jpeg: + " + ajillionObj.getCreative_url());					
					Picasso.with(activity).load(ajillionObj.getCreative_url()).into(ajillionObjImage);									
					//Picasso.with(activity).load("http://stage.globes.co.il/news/test/alex/1.jpeg").into(ajillionObjImage);  // TEST!!!
					//ajillionObjImage.setAdjustViewBounds(true);
					
					ajillionObjImage.setOnClickListener(new OnClickListener()
					{
						@Override
						public void onClick(View v)
						{
							Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(ajillionObj.getClick_url()));
							activity.startActivity(browserIntent);
						}
					});
				}
				else 
				{
					Log.e("alex", "ajillionObj gif");
					frame.removeAllViews();

					frame.addView(ajillionObjImageGif, RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
					ajillionObjImageGif.getSettings().setJavaScriptEnabled(true);
					ajillionObjImageGif.getSettings().setAppCacheEnabled(false);
					ajillionObjImageGif.loadData(Utils.centerHTML(ajillionObj.getCreative_url(), true), "text/html; charset=utf-8", "UTF-8");
					ajillionObjImageGif.setOnTouchListener(new OnTouchListener()
					{
						@Override
						public boolean onTouch(View v, MotionEvent event)
						{
							Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(ajillionObj.getClick_url()));
							activity.startActivity(browserIntent);
							return true;
						}
					});
				}
			}
			else
			{
				//Log.e("alex", "ajillionObj iframe");
				frame.removeAllViews();

				frame.addView(ajillionObjImageGif, RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
				ajillionObjImageGif.setPadding(0, 0, 0, 0);
				WebSettings webViewSettings = ajillionObjImageGif.getSettings();
				webViewSettings.setJavaScriptCanOpenWindowsAutomatically(false);
				webViewSettings.setJavaScriptEnabled(true);
				webViewSettings.setUseWideViewPort(false);
				webViewSettings.setBuiltInZoomControls(false);
				webViewSettings.setLoadWithOverviewMode(false);
				webViewSettings.setSupportZoom(false);
				webViewSettings.setPluginState(PluginState.ON);
				ajillionObjImageGif.loadData(Utils.centerHTML(ajillionObj.getCreative_url(), false), "text/html; charset=utf-8", "UTF-8");
				ajillionObjImageGif.setOnTouchListener(new OnTouchListener()
				{
					@Override
					public boolean onTouch(View v, MotionEvent event)
					{
						Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(ajillionObj.getClick_url()));
						activity.startActivity(browserIntent);
						return true;
					}
				});
			}
		}
	}

	private String encodeTextForWebView(String html, EncodeType type) throws UnsupportedEncodingException
	{
		try
		{
			html = URLEncoder.encode(html, "utf-8").replaceAll("\\+", " ");
		}
		catch (UnsupportedEncodingException e)
		{
			Log.e("eli", "error 3097", e);
			html = "";
		}
		StringBuilder sb = new StringBuilder();
		sb.append("<html>");
		sb.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">");
		sb.append("<body style=\"direction:rtl;\">");
		switch (type)
		{
			
			case TYPE_BODY :
				sb.append("<span style=\"line-height:23px\">" + html.trim() + "</span>");
				break;
		}
		sb.append("</body>");
		sb.append("</html>");
		return sb.toString();
	}
	
	HashMap<Integer, WebView> hmPositive = new HashMap<Integer, WebView>();
	
	private void showPositiveModileAd(WebView w, String PositiveMobileURL, View parent, int pos)
	{				
//		if(hmPositive.containsKey(pos))
//		{
//			WebView w1 = (WebView)hmPositive.get(pos);
//			View parent1 = (View)w1.getParent();
//			((ViewGroup) parent1).removeAllViews();
//			((ViewGroup) parent).addView(w1);
//			Log.e("alex", "WebView from List: " + pos);
//			return;
//		}
		
		try
		{
		    w.setScrollContainer(false);		    
		    
            ////////////////////////////////////////////////////////////////////////////////////////
		    
			w.setVerticalScrollBarEnabled(false);
			w.setHorizontalScrollBarEnabled(false);
			w.setWebViewClient(new WebViewClient()
			{
				@Override
				public boolean shouldOverrideUrlLoading(WebView view, String url)
				{
					Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
					activity.startActivity(browserIntent);
					return true;
				}
				
				@Override
	            public void onLoadResource (WebView view, String url) {
					try
					{
		                if (url.contains("track.mobiyield.com/traffic/")) {
		                    if(view.getHitTestResult().getType() > 0){
		                    	Log.e("alex", "PositiveMobile Redirect:" + url);
		                    	Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
								activity.startActivity(browserIntent);
		                        view.stopLoading();
		                    }
		                }
					}
					catch(Exception ex)
					{
						Log.e("alex", "Error onLoadResource webview" + url);
					}
	            }
			});
		    /////////////////////////////////////////////////////////////////////////////////////////
		    
			WebSettings webSettings = w.getSettings();
		    webSettings.setJavaScriptEnabled(true);
		    
		    //webSettings.setTextZoom((int)(webSettings.getTextZoom() * 1.1));
		    //w.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
		    
		    //PositiveMobileURL = "http://stage.globes.co.il/news/test/alex/tag.js?ver=2";
		    
		    String data = "<html dir=\"rtl\" ><meta charset=\"utf-8\" />";
		    data += "<head><!-- YBrant  AD tag --><script src=\"" + PositiveMobileURL + "\"></script><!-- end AD tag --></head>";
		    data += "</html>";
		    
		    //w.loadData(data, "text/html", "utf-8");		    
		    
//		    w.addJavascriptInterface(new JSInterface(activity.getApplicationContext()), "HtmlViewer");
//
//	        w.setWebViewClient(new WebViewClient() {
//	            @Override
//	            public void onPageFinished(WebView view, String url) {
//	            	 w.loadUrl("javascript:window.HtmlViewer.showHTML" +
//	                         "('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');");
//
//	            }
//	        });		    
		   
		    w.loadDataWithBaseURL(null, data, "text/html", "UTF-8", null);
		    
		    Log.e("alex", "PositiveMobileURL: " + PositiveMobileURL);
		    
		    //w.setBackgroundColor(Color.RED);
		    
		    //w.loadUrl("http://stage.globes.co.il/news/test/alex/test3.htm?=2"); // test		    
		    
		    if(!hmPositive.containsKey(pos))
			{
		    	//hmPositive.put(pos, w);
			}
		    
		    ((ViewGroup) parent).addView(w);
		}
		catch(Exception ex){
			Log.e("alex", "Error showPositiveModileAd()");			
		}
	}

	@Override
	public void onTaskCompleted(Map<String, Map<String, String>> map)
	{
		//if(globalItemPosition==11){return;}
		if(Definitions.wallafusion) {
			try {
				if (KATAVA_PIRSUMIT_TYPE_ID == DfpAdHolder.TYPE_DFP_KATAVA_PIRSUMIT_MAIN_LIST || KATAVA_PIRSUMIT_TYPE_ID == DfpAdHolder.TYPE_DFP_KATAVA_PIRSUMIT_MAIN_LIST_2
						|| KATAVA_PIRSUMIT_TYPE_ID == DfpAdHolder.TYPE_DFP_KATAVA_PIRSUMIT_HOT_STORY || KATAVA_PIRSUMIT_TYPE_ID == DfpAdHolder.TYPE_DFP_KATAVA_PIRSUMIT_NADLAN) {
					isSponsoredArticles = true;
				}

				Log.e("alex", "AAA2:" + KATAVA_PIRSUMIT_TYPE_ID + "===isSponsoredArticles:" + isSponsoredArticles);


				globalMap = map;
				String space = Definitions.WALLA_SPACE_VERTICAL_STRIP; //"vertical_strip";

				if (isTopHeaderBanner) {
					isTopHeaderBanner = false;
					String sKingSpace = Definitions.WALLA_SPACE_KING;
					String android_id = Secure.getString(activity.getApplicationContext().getContentResolver(), Secure.ANDROID_ID);

					//if(android_id.equals("aa92e462f725108c") || (map.containsKey(sKingSpace) && map.get(sKingSpace) != null))
					if ((map.containsKey(sKingSpace) && map.get(sKingSpace) != null)) {
						Log.e("alex", "showLiveBox: starting showLiveBox 11...");
						WallaAdvParser.showLiveBox(map.get(sKingSpace), walla_adv_current_frame, activity.getApplicationContext(), activity);
						return;
					}

					space = Definitions.WALLA_SPACE_TOP_FLOAT; //"n_slider";
				}

				if (isVerticalStripStoryBanner) {
					Log.e("alex", "isVerticalStripStoryBanner!!!!!");
					isVerticalStripStoryBanner = false;
					space = Definitions.WALLA_SPACE_VERTICAL_STRIP_STORY; //"vertical_strip_story";
				}

				if (isSponsoredArticles) {
					switch (KATAVA_PIRSUMIT_TYPE_ID) {
						case DfpAdHolder.TYPE_DFP_KATAVA_PIRSUMIT_MAIN_LIST_2:
							space = Definitions.WALLA_SPACE_KATAVA_PIRSUMIT_MAIN_2;
							break;
						case DfpAdHolder.TYPE_DFP_KATAVA_PIRSUMIT_HOT_STORY:
							space = Definitions.WALLA_SPACE_KATAVA_PIRSUMIT_STORY;
							break;
						case DfpAdHolder.TYPE_DFP_KATAVA_PIRSUMIT_NADLAN:
							space = Definitions.WALLA_SPACE_KATAVA_PIRSUMIT_REALESTATE;
							break;
						default:
							space = Definitions.WALLA_SPACE_KATAVA_PIRSUMIT_MAIN; //"n_spons_article_main";
							break;
					}

					Log.e("alex", "KatavaPirsumitSpace:" + space);
				}

				if (map == null || !map.containsKey(space)) {
					return;
				}

				Map<String, String> m = map.get(space);

				if (m == null) {
					return;
				}

				if (isSponsoredArticles) {
					drawSponsoredArticle(m);
				} else {
					Log.e("alex", "Loading Horizontal Banner!!! Position: " + globalItemPosition);
					WallaAdvParser.drawWallaBanner(walla_adv_current_frame, activity.getApplicationContext(), m);
					//walla_adv_current_frame.setVisibility(View.VISIBLE);
				}
			} catch (Exception ex) {
			}
		}
	}
	
//	private void removeItemFromWallaList()
//	{
//		// Remove from list after display
//		Iterator<Integer> it = walla_adv_items.iterator();
//		while (it.hasNext()) {
//		     Integer item = it.next();
//		     if (item == walla_adv_position) {
//		         it.remove();
//		         walla_adv_items_counter--;
//		     }
//		 }
//	}

	private View drawDFPSponsoredArticle(final NativeCustomTemplateAd ad)
	{
		List<String> lst = ad.getAvailableAssetNames();

		for (String s:lst) {
			Log.e("alex", "onCustomTemplateAdLoaded!!! Field: " + s + "===" + ad.getText(s));
		}

		View rowView = inflater.inflate(R.layout.row_layout_new, null);
		final ViewHolder holder = new ViewHolder();
		holder.text = (TextView) rowView.findViewById(R.id.title);
		holder.image = (ImageView) rowView.findViewById(R.id.image);
		holder.createdOn = (TextView) rowView.findViewById(R.id.createdOn);
		rowView.setTag(holder);

		holder.text.setText(ad.getText("headline"));
		holder.text.setTypeface(almoni_aaa_regular);
		holder.text.setTextSize(22.5f);
		holder.text.setTextColor(Color.BLACK);
		holder.createdOn.setText("");

		holder.image.setImageDrawable(ad.getImage("MainImage").getDrawable());

		holder.image.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ad.performClick("MainImage");
			}
		});

		holder.text.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ad.performClick("headline");
			}
		});

		return rowView;
	}

	private void drawSponsoredArticle(Map<String, String> m)
	{
		try
		{
			String sContentType = m.get("CONTENTTYPE").toLowerCase();
			String sAdUrl = m.get("ADURL_01");
			String sClickURL =  m.get("CLICKURL");		
			String sViewReportURL = m.get("ADSRV") + m.get("VIEWREPORT");
			final String sClickReportURL = m.get("ADSRV") + m.get("CLICKREPORT");
			String sAddTitle = m.get("ADTITLE");
			
			//Log.e("alex", "01.12.2015 - drawSponsoredArticle as postion: " + globalItemPosition);
			
			if(sAdUrl == "" || sClickURL == "" || sViewReportURL == "" || sClickReportURL == ""){
				Log.e("alex", "HHHHH drawSponsoredArticle Empty Vals: sContentType=" + sContentType + " sAdUrl=" + sAdUrl + " sClickURL=" + sClickURL + " sViewReportURL=" + sViewReportURL + " sClickReportURL=" + sClickReportURL);
				return;
			}
			
	//		String sSponsArticleType = "n_spons_article_main";
			
			//switch (walla_adv_position)
	//		switch(globalItemPosition)
	//		{
	//			case 15://after hot story
	//				sSponsArticleType = "n_spons_article_story";
	//			case 42://after nadlan
	//				sSponsArticleType = "n_spons_article_realestate";
	//		}
			
			final Uri lnkToRedirect = Uri.parse(sClickURL);
			//final Uri lnkToReport = Uri.parse(sReportURL);
			
		    View rowView = inflater.inflate(R.layout.row_layout_new, null);
		    final ViewHolder holder = new ViewHolder();
		    holder.text = (TextView) rowView.findViewById(R.id.title);
		    holder.image = (ImageView) rowView.findViewById(R.id.image);
		    holder.createdOn = (TextView) rowView.findViewById(R.id.createdOn);
		    rowView.setTag(holder);
		    				
		    holder.text.setText(sAddTitle);
		    holder.text.setTypeface(almoni_aaa_regular);
		    holder.text.setTextSize(22.5f);
			holder.text.setTextColor(Color.BLACK);
			holder.createdOn.setText("");
			
		    if (!sAdUrl.equals(""))
			{
				Picasso.with(activity).load(sAdUrl).into(holder.image);
				try
				{
					WallaAdvParser.sendWallaReportEvent(sViewReportURL);
				}
				catch (MalformedURLException e)
				{
					e.printStackTrace();
				}
			}
			else
			{
				holder.image.setImageResource(R.drawable.stub_image_not_found);
			}
		    
		    holder.text.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					try
					{
						WallaAdvParser.sendWallaReportEvent(sClickReportURL);
					}
					catch (MalformedURLException e)
					{
						e.printStackTrace();
					}
					
					Intent browserIntent = new Intent(Intent.ACTION_VIEW, lnkToRedirect);
					activity.startActivity(browserIntent);
				}
			});
		
			holder.image.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{			
					try
					{
						WallaAdvParser.sendWallaReportEvent(sClickReportURL);
					}
					catch (MalformedURLException e)
					{
						e.printStackTrace();
					}
					
					Intent browserIntent = new Intent(Intent.ACTION_VIEW, lnkToRedirect);
					activity.startActivity(browserIntent);
				}
			});	    
			
		    ((ViewGroup) rlSponsoredArticles).addView(rowView);
		    
			//Log.e("alex", "HHHHH: " + walla_adv_position);
		}
		catch(Exception ex){}
	
	}
		
	
	// public void btnShareVideo_onClick(View v)
	// {
	// FrameLayout vwParent = (FrameLayout) v.getParent();
	// LinearLayout vwGrandparentRow = (LinearLayout) vwParent.getParent();
	//
	// int position = getListView().getPositionForView(vwGrandparentRow);
	// position--;
	//
	// if (position >= 0)
	// {
	// if (article.get(position) instanceof Article)
	// {
	// Intent shareArticle = new Intent();
	// shareArticle.setAction(Intent.ACTION_SEND);
	// String mimeType = "text/plain";
	// shareArticle.setType(mimeType);
	// shareArticle.putExtra(Intent.EXTRA_SUBJECT, ((Article)
	// parsedNewsSet.itemHolder.get(position)).getTitle());
	//
	// shareArticle.putExtra(Intent.EXTRA_TEXT, Definitions.watchVideo + "\n\n"
	// + GlobesURL.URLDocumentToShare
	// + ((Article) parsedNewsSet.itemHolder.get(position)).getDoc_id() +
	// Definitions.fromPlatformAppAnalytics + "\n\n"
	// + Definitions.ShareString + " " + Definitions.shareGlobesWithQuates +
	// "\n\n" + Definitions.shareCellAppsPart1 + " "
	// + Definitions.shareGlobesWithQuates + " " +
	// Definitions.shareCellAppsPart2 + "\n");
	//
	// // TODO add tracking later
	// // tracker.trackPageView(GlobesURL.URLDocumentToShare +
	// // ((Article)
	// // parsedNewsSet.itemHolder.get(position)).getDoc_id()
	// // + "from=" + Definitions.SHAREsuffix);
	// shareArticle.putExtra(Intent.EXTRA_TITLE, Definitions.ShareString);
	// activity.startActivity(Intent.createChooser(shareArticle,
	// Definitions.Share));
	// }
	// }
	// }	
}


class JSInterface {

    private Context ctx;

    JSInterface(Context ctx) {
        this.ctx = ctx;
    }

    public void showHTML(String html) {
        Log.e("alex", "EEEEEEE: " + html);
    }

}
