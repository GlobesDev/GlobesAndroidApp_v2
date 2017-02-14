package il.co.globes.android.fragments;

import il.co.globes.android.Definitions;
import il.co.globes.android.DocumentActivity_new;
import il.co.globes.android.MainSlidingActivity;
import il.co.globes.android.R;
import il.co.globes.android.ShareActivity;
import il.co.globes.android.Utils;
import il.co.globes.android.interfaces.GlobesListener;
import il.co.globes.android.objects.ArticleDuns100;
import il.co.globes.android.objects.ArticleGalleryObject;
import il.co.globes.android.objects.CustomWebView;
import il.co.globes.android.objects.Document;
import il.co.globes.android.objects.GlobesURL;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragment;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.viewpagerindicator.CirclePageIndicator;

public class ArticleDuns100Fragment extends SherlockFragment
{
	// UI 

	ArrayList<ArticleGalleryObject> galleryObjectsArr;
	Bitmap documentMainBitmap;
	Bitmap documentVideoBitmap;
	/*
	 * Used when decreasing font size in article text , first need to load an
	 * empty HTML than load the real one
	 */
	private static final String EMPTY_HTML = "<html><body/></html>";
	private String articleTextHtml = "";
	private String titleTextHtml = "";
	private String subtitleTextHtml;
	AlertDialog alertDialog;
	ImageView imageOpnion;
	Bitmap opnionBitmap;
	int baseTextSize = 15;
	int position = 0;

	ViewPager pager;
	CirclePageIndicator circlePageIndicator;
	ScrollView myScrollView;

	static final int ENLARGETEXTSIZE = 0;
	static final int REDUCETEXTSIZE = 1;
	static final int RESETTEXTSIZE = 2;

	static final int MAXFONTSIZE = 18;
	static final int MINFONTSIZE = 10;

	/** Font size key in shared prefs */
	SharedPreferences prefs;
	private final String FONT_SIZE_KEY_PREF = "fontSizePref";

	Document parsedDocument;
	private ProgressDialog pd;
	static final int DONE_PARSING = 0;
	static final int ERROR_PARSING = 1;
	static final int DONE_GETTING_BANNER = 2;
	static final int ERROR_GETTING_BANNER = 3;
	static final int CLOSE_SPLASH = 5;
	static final int SPLASH_DISPLAY_LENGHT = 2000;

	Context appContext;
	Button buttonShare;
	Button buttonEnlargeFontSize;
	Button buttonReduceFontSize;
	Button buttonBrowseBack;
	Button buttonBrowseForward;
	Button buttonAddResponse;
	Button buttonGoToResponses;
	Button buttonPlayDocumentVideo;

	CustomWebView wvViewDocumentText, wvViewDocumentTitle, wvViewDocumentSubTitle;
	ArticleDuns100 articleDuns100;
	String parentId, calle, uriToParse;

	private enum EncodeType
	{
		TYPE_TITLE, TYPE_SUBTITLE, TYPE_BODY
	};

	// callback
	private GlobesListener mCallback;

	// sliding menu/actionbar
	private ActionBar actionBar;
	private SlidingMenu sm;

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
		Log.e("eli", "dun 100");
		// sliding menu / ActionBar
		sm = ((MainSlidingActivity) getActivity()).getSlidingMenu();
		actionBar = ((MainSlidingActivity) getActivity()).getSupportActionBar();
		actionBar.hide();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		return inflater.inflate(R.layout.article_duns_100_layout, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);

		// ActionBar
		// createActionBarMenu();

		// UI
		// initUI();

		uriToParse = getArguments().getString(Definitions.URI_TO_PARSE);

		setInitialData(uriToParse);
	}

	@Override
	public void onDestroyView()
	{
		super.onDestroyView();

		// return activity's default actionBar
		mCallback.onSetDefaultActionBar();
	}
	private String encodeTextForWebView(String html, EncodeType type) throws UnsupportedEncodingException
	{
		try
		{
			html = URLEncoder.encode(html, "utf-8").replaceAll("\\+", " ").replaceAll("null", "");
		}
		catch (UnsupportedEncodingException e)
		{
			html = "";
		}

		StringBuilder sb = new StringBuilder();
		sb.append("<html>");
		sb.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">");
		sb.append("<body style=\"direction:rtl;\">");

		switch (type)
		{
			case TYPE_TITLE :
				sb.append("<strong style=\"line-height:30px\">" + html.trim() + "</strong>");
				break;
			case TYPE_SUBTITLE :
				sb.append("<strong style=\"line-height:25px\">" + html.trim() + "</strong>");
				break;
			case TYPE_BODY :
				sb.append("<span style=\"line-height:23px\">" + html.trim() + "</span>");
				break;

		}
		sb.append("</body>");
		sb.append("</html>");

		return sb.toString();
	}

	private class myWebViewClient extends WebViewClient
	{
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url)
		{
			// check if it is share page to view
			// was:
			// "http://www.globes.co.il/globessites/globes/finance/instruments/instrument.aspx?instrumentid"
			if (url.contains("instrumentid"))
			{
				String feeder, insturmentID, uri;
				insturmentID = new String(url.substring(url.indexOf("instrumentid=") + 13, url.indexOf("&feeder")));
				feeder = new String(url.substring(url.indexOf("&feeder=") + 8, url.length()));
				int endFeederIndex = feeder.indexOf("&");
				if (endFeederIndex != -1)
				{
					feeder = new String(feeder.substring(0, feeder.indexOf("&")));
				}
				uri = GlobesURL.URLSharePage.replaceAll("XXXX", feeder).replaceAll("YYYY", insturmentID);
				Intent intent = new Intent(getActivity(), ShareActivity.class);
				intent.putExtra(Definitions.CALLER, Definitions.SHARES);
				intent.putExtra(Definitions.PARSER, Definitions.SHARES);
				intent.putExtra("URI", uri);
				startActivity(intent);
				return true;
			} // this is a document article
				// was Definitions.URLDocumentToShare
			else if (url.contains("did="))
			{
				String articleID = new String(url.substring(url.indexOf("did=") + 4, url.length()));
				int endArticleIDIndex = articleID.indexOf("#");
				if (endArticleIDIndex != -1)
				{
					articleID = new String(articleID.substring(0, endArticleIDIndex));
				}
				Intent intent = new Intent(getActivity(), DocumentActivity_new.class);
				intent.putExtra(Definitions.CALLER, Definitions.WEBVIEW);
				intent.putExtra("articleId", articleID);
				startActivity(intent);
				return true;
			}
			else if (url.contains(".tag") || url.contains(".TAG"))
			{
				if (!url.contains("http://"))
				{
					url = "http://www.globes.co.il";

				}
				Resources resources = getResources();
				String linkTitle = resources.getString(R.string.dialog_tagiot_title);
				String linkBody = resources.getString(R.string.dialog_tagiot_body);
				String linkContinue = resources.getString(R.string.dialog_continue);
				String linkCancel = resources.getString(R.string.dialog_cancel);
				showDialog(linkTitle, linkBody, linkContinue, linkCancel, url);
				return true;
			}
			else
			{
				if (!url.contains("http://"))
				{
					url = "http://www.globes.co.il";

				}
				Resources resources = getResources();
				String tagiotTitle = resources.getString(R.string.dialog_link_title);
				String tagiotBody = resources.getString(R.string.dialog_link_body);
				String tagiotContinue = resources.getString(R.string.dialog_continue);
				String tagiotCancel = resources.getString(R.string.dialog_cancel);
				showDialog(tagiotTitle, tagiotBody, tagiotContinue, tagiotCancel, url);
				return true;
			}

		}

	}

	/**
	 * 
	 * @param title
	 *            - dialog title
	 * @param msg
	 *            - dialog msg
	 * @param neutBtn
	 *            - the neutral btn
	 */
	private void showDialog(String title, String msg, String neutBtn, String negBtn, final String url)
	{

		AlertDialog.Builder ad = new AlertDialog.Builder(getActivity());
		ad.setTitle(title);
		ad.setMessage(msg);

		ad.setPositiveButton(neutBtn, new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int which)
			{
				Intent i = new Intent(Intent.ACTION_VIEW);
				i.setData(Uri.parse(url));
				startActivity(i);
				alertDialog.dismiss();

				return;
			}
		});

		ad.setNegativeButton(negBtn, new DialogInterface.OnClickListener()
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

	// public void browseBack()
	// {
	// if (position > 0 && position < parsedNewsSet.itemHolder.size())
	// {
	// position--;
	// if (parsedNewsSet.itemHolder.get(position) instanceof Article
	// && ((Article) parsedNewsSet.itemHolder.get(position)).getUrl() == null)
	// {
	// // if we are in portrait mode load the Ad
	// if (getResources().getConfiguration().orientation ==
	// android.content.res.Configuration.ORIENTATION_PORTRAIT)
	// {
	//
	// setPageToArticle(((Article)
	// parsedNewsSet.itemHolder.get(position)).getDoc_id());
	//
	// // loadAdFromBrowseButtonsAndSetPageToArticle();
	// }
	// else
	// {
	// setPageToArticle(((Article)
	// parsedNewsSet.itemHolder.get(position)).getDoc_id());
	// }
	//
	// }
	// else
	// {
	// browseBack();
	// }
	// }
	// }

	// public void browseForward()
	// {
	// position++;
	// if (position < parsedNewsSet.itemHolder.size())
	// {
	// if (parsedNewsSet.itemHolder.get(position) instanceof Article
	// && ((Article) parsedNewsSet.itemHolder.get(position)).getUrl() == null)
	// {
	// // if we are in portrait mode load the Ad
	// if (getResources().getConfiguration().orientation ==
	// android.content.res.Configuration.ORIENTATION_PORTRAIT)
	// {
	// loadAdFromBrowseButtonsAndSetPageToArticle();
	//
	// }
	// else
	// {
	// setPageToArticle(((Article)
	// parsedNewsSet.itemHolder.get(position)).getDoc_id());
	// }
	// }
	// else
	// {
	// browseForward();
	// }
	// }
	// }
	//
	private class CustomeGestureDetector extends SimpleOnGestureListener
	{

		private final int SWIPE_MIN_DISTANCE = 100;// 120;
		private static final int SWIPE_MAX_OFF_PATH = 350;
		private final int SWIPE_THRESHOLD_VELOCITY = 200;// 150;

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY)
		{
			myScrollView.requestDisallowInterceptTouchEvent(true);
			if (e1 == null || e2 == null) return false;
			if (e1.getPointerCount() > 1 || e2.getPointerCount() > 1)
				return false;
			else
			{
				try
				{ // right to left swipe .. go to prev page
					if (e1.getX() - e2.getX() > 50 && Math.abs(velocityX) > 100)
					{// 200

						// browseBack();
						return true;
					} // left to right swipe .. go to n page
					else if (e2.getX() - e1.getX() > 50 && Math.abs(velocityX) > 100)
					{
						// browseForward();

						return true;
					}

				}
				catch (Exception e)
				{ // nothing
				}
				return false;
			}
		}
	}

	void setInitialData(final String uriToParse)
	{
		pd = ProgressDialog.show(getActivity(), "", Definitions.Loading, true, false);

		new AsyncTask<Void, Void, Integer>()
		{

			@Override
			protected void onPreExecute()
			{
				// list = (SwipeListView) v.findViewById(android.R.id.list);

			};

			@Override
			protected Integer doInBackground(Void... params)
			{
				int res;
				try
				{
					URL url = new URL(uriToParse);
					articleDuns100 = ArticleDuns100.parseArticleDuns100(url);

					opnionBitmap = null;
					opnionBitmap = Utils.DownloadImage(articleDuns100.getF3());
					res = DONE_PARSING;

				}
				catch (Exception e)
				{
					e.printStackTrace();
					res = ERROR_PARSING;
				}
				return res;
			}

			@Override
			protected void onPostExecute(Integer result)
			{
				try
				{
					pd.dismiss();
				}
				catch (Exception e)
				{
				}
				if (result == DONE_PARSING)
				{

					initUI();

				}
				else if (result == ERROR_PARSING)
				{
					Toast toast = Toast.makeText(getActivity(), Definitions.ErrorLoading, Toast.LENGTH_LONG);
					toast.show();
				}
			}

		}.execute();
	}

	private void initUI()
	{
		View view = getView();
		myScrollView = (ScrollView) view.findViewById(R.id.documentScrollView);
		imageOpnion = (ImageView) view.findViewById(R.id.imageOpnion);
		wvViewDocumentTitle = (CustomWebView) view.findViewById(R.id.textViewDocumentTitle);
		wvViewDocumentSubTitle = (CustomWebView) view.findViewById(R.id.textViewDocumentSubTitle);
		wvViewDocumentText = (CustomWebView) view.findViewById(R.id.textViewDocumentText);
		wvViewDocumentText.setGestureDetector(new GestureDetector(new CustomeGestureDetector()));
		wvViewDocumentSubTitle.setGestureDetector(new GestureDetector(new CustomeGestureDetector()));
		wvViewDocumentTitle.setGestureDetector(new GestureDetector(new CustomeGestureDetector()));
		TextView textViewDocumentModifiedOn = (TextView) view.findViewById(R.id.textViewDocumentModifiedOn);
		textViewDocumentModifiedOn.setText(articleDuns100.getCreatedOn().replaceAll("null", ""));
		TextView textViewDocumentWriter = (TextView) view.findViewById(R.id.textViewDocumentWriter);
		textViewDocumentWriter.setText(articleDuns100.getAuthor().replaceAll("null", ""));

		imageOpnion.setImageBitmap(opnionBitmap);

		articleTextHtml = articleDuns100.getText();
		try
		{
			articleTextHtml = encodeTextForWebView(articleTextHtml, EncodeType.TYPE_BODY);
		}
		catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
		}

		// loading all pages inside the webview
		wvViewDocumentText.setWebViewClient(new myWebViewClient());
		wvViewDocumentText.loadData(articleTextHtml.trim(), "text/html; charset=utf-8", "UTF-8");
		wvViewDocumentText.getSettings().setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
		wvViewDocumentText.getSettings().setPluginState(PluginState.ON);

		wvViewDocumentText.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
		// TODO added for FB test
		wvViewDocumentText.getSettings().setJavaScriptEnabled(true);

		subtitleTextHtml = articleDuns100.getSubTitle();

		try
		{
			subtitleTextHtml = encodeTextForWebView(subtitleTextHtml.trim(), EncodeType.TYPE_SUBTITLE);
		}
		catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
		}

		// wvViewDocumentText.setWebChromeClient(new WebChromeClient()
		// {
		// });
		// wvViewDocumentText.getSettings().setJavaScriptEnabled(true);
		// loading all pages inside the webview
		wvViewDocumentSubTitle.setWebViewClient(new myWebViewClient());
		wvViewDocumentSubTitle.loadData(subtitleTextHtml.trim(), "text/html; charset=utf-8", "UTF-8");
		wvViewDocumentSubTitle.getSettings().setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
		wvViewDocumentSubTitle.getSettings().setPluginState(PluginState.ON);

		wvViewDocumentSubTitle.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
		// TODO added for FB test
		wvViewDocumentSubTitle.getSettings().setJavaScriptEnabled(true);

		titleTextHtml = articleDuns100.getTitle();

		try
		{
			titleTextHtml = encodeTextForWebView(titleTextHtml.trim(), EncodeType.TYPE_TITLE);
		}
		catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
		}

		// wvViewDocumentText.setWebChromeClient(new WebChromeClient()
		// {
		// });
		// wvViewDocumentText.getSettings().setJavaScriptEnabled(true);
		// loading all pages inside the webview
		wvViewDocumentTitle.setWebViewClient(new myWebViewClient());
		wvViewDocumentTitle.loadData(titleTextHtml.trim(), "text/html; charset=utf-8", "UTF-8");
		wvViewDocumentTitle.getSettings().setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
		wvViewDocumentTitle.getSettings().setPluginState(PluginState.ON);

		wvViewDocumentTitle.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
		// TODO added for FB test
		wvViewDocumentTitle.getSettings().setJavaScriptEnabled(true);

		// FrameLayout frameLayout = (FrameLayout)
		// view.findViewById(R.id.frameTest);

	}

	/**
	 * Creates a Custom ActionBar view
	 */
//	private void createActionBarMenu()
//	{
//
//		View view = getActivity().getLayoutInflater().inflate(R.layout.actionbar_custom_view_text_title, null);
//
//		ImageView imagViewLeftIcon = (ImageView) view.findViewById(R.id.imageView_actionBar_left_icon);
//		ImageView imagViewRightIcon = (ImageView) view.findViewById(R.id.imageView_actionBar_right_icon);
//		TextView textViewTitle = (TextView) view.findViewById(R.id.textView_actionBar_title);
//
//		// title
//		textViewTitle.setText(R.string.duns100);
//
//		imagViewLeftIcon.setOnClickListener(new View.OnClickListener()
//		{
//			@Override
//			public void onClick(View arg0)
//			{
//				sm.toggle();
//			}
//		});
//
//		imagViewRightIcon.setOnClickListener(new View.OnClickListener()
//		{
//			@Override
//			public void onClick(View arg0)
//			{
//				if (sm.isSecondaryMenuShowing())
//					sm.showContent(true);
//				else sm.showSecondaryMenu(true);
//			}
//		});
//
//		actionBar.setCustomView(view);
//	}
	
	
	
	@Override
    public void onStart()
    {
        super.onStart();
//        EasyTracker tracker = EasyTracker.getInstance(getActivity());
//        tracker.set(Fields.SCREEN_NAME, getActivity().getResources().getString(R.string.il_co_globes_android_fragments_ArticleDuns100Fragment));
//        tracker.send(MapBuilder.createAppView().build());
    }
	
}
