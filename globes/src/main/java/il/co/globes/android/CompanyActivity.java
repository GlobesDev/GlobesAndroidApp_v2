package il.co.globes.android;

import il.co.globes.android.objects.CompanyDuns100;
import il.co.globes.android.objects.CustomWebView;
import il.co.globes.android.objects.GlobesURL;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.viewpagerindicator.CirclePageIndicator;

public class CompanyActivity extends Activity
{
	Typeface almoni_aaa_regular, almoni_aaa_light, almoni_aaa_blod, almoni_aaa_black;
	private HorizontalScrollView scrollView_managers;
	private LinearLayout ll_adress, ll_website, ll_fax, ll_pelehone, ll_area, ll_years;
	private LayoutInflater layoutInflater;
	String uriToParse, CompanyDocId;
	private String articleTextHtml = "";

	AlertDialog alertDialog;
	int baseTextSize = 15;
	int position = 0;

	ViewPager pager;
	CirclePageIndicator circlePageIndicator;
	ScrollView myScrollView;
	private boolean isFontReduced = false;
	static final int ENLARGETEXTSIZE = 0;
	static final int REDUCETEXTSIZE = 1;
	static final int RESETTEXTSIZE = 2;

	static final int MAXFONTSIZE = 18;
	static final int MINFONTSIZE = 10;
	private ImageView imageView_back_actionBar_documentN, imageView_textSize_actionBar_documentN, img_mark;

	private ImageView img_ball_duns100;
	private TextView text_company_rank_title, text_adress_ofcompany, text_pelephone_ofcompany, text_name_of_web_site, text_fax_ofcompany,
			text_area_ofcompany, text_years_ofcompany;

	/** Font size key in shared prefs */
	SharedPreferences prefs;
	private final String FONT_SIZE_KEY_PREF = "fontSizePref";

	private ProgressDialog pd;
	static final int DONE_PARSING = 0;
	static final int ERROR_PARSING = 1;
	static final int DONE_GETTING_BANNER = 2;
	static final int ERROR_GETTING_BANNER = 3;
	static final int CLOSE_SPLASH = 5;
	static final int SPLASH_DISPLAY_LENGHT = 2000;

	Context appContext;
	ImageView buttonShare;

	Button buttonPlayDocumentVideo;
	private LinearLayout managerContenair;
	CustomWebView wvViewCompanyText;
	CompanyDuns100 companyDuns100;
	private enum EncodeType
	{
		TYPE_TITLE, TYPE_SUBTITLE, TYPE_BODY
	};

	private TextView textView_name_of_company;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.company_activity_layout);
		almoni_aaa_regular = Typeface.createFromAsset(this.getAssets(), "almoni-dl-aaa-regular.otf");
		almoni_aaa_blod = Typeface.createFromAsset(this.getAssets(), "almoni-dl-aaa-bold.otf");
		layoutInflater = getLayoutInflater();
		uriToParse = getIntent().getStringExtra(Definitions.URI_TO_PARSE);
		prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		baseTextSize = loadFontPref(FONT_SIZE_KEY_PREF);
		CompanyDocId = getIntent().getStringExtra("CompanyDocId");
		setInitialData(uriToParse);
	}
	
	void createActionBarButtons()
	{
		buttonShare = (ImageView) findViewById(R.id.buttonShare);
		buttonShare.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// if (parsedDocument != null && parsedNewsSet != null)
				// {
				//
				// Intent shareArticle = new Intent();
				// shareArticle.setAction(Intent.ACTION_SEND);
				// String mimeType = "text/plain";
				// shareArticle.setType(mimeType);
				// shareArticle.putExtra(Intent.EXTRA_SUBJECT,
				// parsedDocument.getTitleText());
				// shareArticle.putExtra(Intent.EXTRA_TEXT,
				// parsedDocument.getSubTitleText() + "\n\n" +
				// Definitions.watchArticle + "\n\n"
				// + GlobesURL.URLDocumentToShare + ((Article)
				// parsedNewsSet.itemHolder.get(position)).getDoc_id()
				// + Definitions.fromPlatformAppAnalytics + "\n\n" +
				// Definitions.ShareString + " "
				// + Definitions.shareGlobesWithQuates + "\n\n" +
				// Definitions.shareCellAppsPart1 + " "
				// + Definitions.shareGlobesWithQuates + "\n" +
				// Definitions.shareCellAppsPart2 + "\n");
				// tracker.trackPageView(GlobesURL.URLDocumentToShare +
				// ((Article)
				// parsedNewsSet.itemHolder.get(position)).getDoc_id()
				// + "from=" + Definitions.SHAREsuffix);
				// shareArticle.putExtra(Intent.EXTRA_TITLE,
				// Definitions.ShareString);
				// startActivity(Intent.createChooser(shareArticle,
				// Definitions.Share));
				// }
			}
		});

		imageView_back_actionBar_documentN = (ImageView) findViewById(R.id.imageView_back_actionBar_documentN);
		imageView_back_actionBar_documentN.setOnClickListener(new View.OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				CompanyActivity.this.finish();

			}
		});
		imageView_textSize_actionBar_documentN = (ImageView) findViewById(R.id.imageView_textSize_actionBar_documentN);
		imageView_textSize_actionBar_documentN.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				changeSizeFont();
			}
		});

	}

	/**
	 * save the font size for next time
	 * 
	 * @param key
	 *            FONT_SIZE_KEY_PREF
	 * @param size
	 *            the size we want to save
	 */
	private void saveFontPref(String key, int size)
	{
		SharedPreferences.Editor editor = prefs.edit();
		editor.putInt(key, size);
		editor.commit();
	}

	/**
	 * Load the saved font size , 12 is default
	 * 
	 * @param key
	 *            FONT_SIZE_KEY_PREF
	 * @return the saved font size / 12 for default
	 */
	private int loadFontPref(String key)
	{
		return prefs.getInt(FONT_SIZE_KEY_PREF, 15);
	}

	synchronized void changeSizeFont()
	{
		baseTextSize++;
		if (baseTextSize >= MAXFONTSIZE)
		{
			baseTextSize = MINFONTSIZE;
			isFontReduced = true;

		}
		saveFontPref(FONT_SIZE_KEY_PREF, baseTextSize);

		// textViewDocumentModifiedOn.setTextSize((float) baseTextSize + 2);
		// textViewDocumentWriter.setTextSize((float) baseTextSize + 2);
		wvViewCompanyText.getSettings().setDefaultFontSize((int) baseTextSize + 2);
		if (isFontReduced)
		{
			isFontReduced = false;
			wvViewCompanyText.loadData(articleTextHtml, "text/html; charset=utf-8", "UTF-8");

		}

	}

	void setInitialData(final String uriToParse)
	{
		pd = ProgressDialog.show(CompanyActivity.this, "", Definitions.Loading, true, false);

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
					URL url = new URL(uriToParse.replaceAll("null", ""));

					companyDuns100 = CompanyDuns100.parseCompanyDuns100(url);

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
					createActionBarButtons();

				}
				else if (result == ERROR_PARSING)
				{
					Toast toast = Toast.makeText(CompanyActivity.this, Definitions.ErrorLoading, Toast.LENGTH_LONG);
					toast.show();
				}
			}

		}.execute();
	}
	@SuppressLint({ "InflateParams", "SetJavaScriptEnabled" })
	@SuppressWarnings("deprecation")
	private void initUI()
	{
		myScrollView = (ScrollView) findViewById(R.id.documentScrollView);

		wvViewCompanyText = (CustomWebView) findViewById(R.id.textViewCompanyText);
		wvViewCompanyText.setGestureDetector(new GestureDetector(new CustomeGestureDetector()));
		articleTextHtml = companyDuns100.getText();
		try
		{
			articleTextHtml = encodeTextForWebView(articleTextHtml, EncodeType.TYPE_BODY);
		}
		catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
		}

		// loading all pages inside the webview
		wvViewCompanyText.setWebViewClient(new myWebViewClient());
		wvViewCompanyText.loadData(articleTextHtml.trim(), "text/html; charset=utf-8", "UTF-8");
		wvViewCompanyText.getSettings().setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
		wvViewCompanyText.getSettings().setPluginState(PluginState.ON);

		wvViewCompanyText.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
		// TODO added for FB test
		wvViewCompanyText.getSettings().setJavaScriptEnabled(true);

		img_mark = (ImageView) findViewById(R.id.img_mark);
		Picasso.with(getApplicationContext()).load(companyDuns100.getLogo().replaceAll("null", "").trim()).into(img_mark);
		LinearLayout rank_ll = (LinearLayout) findViewById(R.id.rank_ll);

		if (!TextUtils.isEmpty(companyDuns100.getRank_ball().replaceAll("null", "").trim()))
		{

			img_ball_duns100 = (ImageView) findViewById(R.id.img_ball_duns100);
			Picasso.with(getApplicationContext()).load(companyDuns100.getRank_ball().replaceAll("null", "").trim()).into(img_ball_duns100);
			text_company_rank_title = (TextView) findViewById(R.id.text_company_rank_title);
			text_company_rank_title.setText(companyDuns100.getRank_title().replaceAll("null", "").trim());
			text_company_rank_title.setTypeface(almoni_aaa_regular);
		}
		else
		{
			rank_ll.setVisibility(View.GONE);
		}

		ll_adress = (LinearLayout) findViewById(R.id.ll_adress);
		ll_website = (LinearLayout) findViewById(R.id.ll_website);
		ll_fax = (LinearLayout) findViewById(R.id.ll_fax);
		ll_pelehone = (LinearLayout) findViewById(R.id.ll_pelehone);
		ll_area = (LinearLayout) findViewById(R.id.ll_area);
		ll_years = (LinearLayout) findViewById(R.id.ll_years);

		textView_name_of_company = (TextView) findViewById(R.id.textView_name_of_company);
		textView_name_of_company.setText(companyDuns100.getName().replaceAll("null", "").trim());
		textView_name_of_company.setTypeface(almoni_aaa_blod);

		if (!TextUtils.isEmpty(companyDuns100.getAddress().replaceAll("null", "").trim()))
		{

			text_adress_ofcompany = (TextView) findViewById(R.id.text_adress_ofcompany);
			text_adress_ofcompany.setText(companyDuns100.getAddress().replaceAll("null", "").trim());
			text_adress_ofcompany.setTypeface(almoni_aaa_regular);
		}
		else
		{
			ll_adress.setVisibility(View.GONE);
		}

		if (!TextUtils.isEmpty(companyDuns100.getPhone().replaceAll("null", "").trim()))
		{

			text_pelephone_ofcompany = (TextView) findViewById(R.id.text_pelephone_ofcompany);
			text_pelephone_ofcompany.setTypeface(almoni_aaa_regular);
			text_pelephone_ofcompany.setText(companyDuns100.getPhone().replaceAll("null", "").trim());
		}
		else
		{
			ll_pelehone.setVisibility(View.GONE);
		}

		if (!TextUtils.isEmpty(companyDuns100.getWeb_site().replaceAll("null", "").trim()))
		{
			text_name_of_web_site = (TextView) findViewById(R.id.text_name_of_web_site);
			text_name_of_web_site.setTypeface(almoni_aaa_regular);
			text_name_of_web_site.setText(companyDuns100.getWeb_site().replaceAll("null", "").trim());
		}
		else
		{
			ll_website.setVisibility(View.GONE);
		}

		if (!TextUtils.isEmpty(companyDuns100.getFax().replaceAll("null", "").trim()))
		{
			text_fax_ofcompany = (TextView) findViewById(R.id.text_fax_ofcompany);
			text_fax_ofcompany.setText(companyDuns100.getFax().replaceAll("null", "").trim());
			text_fax_ofcompany.setTypeface(almoni_aaa_regular);
		}
		else
		{
			ll_fax.setVisibility(View.GONE);
		}

		if (!TextUtils.isEmpty(companyDuns100.getArea().replaceAll("null", "").trim()))
		{
			text_area_ofcompany = (TextView) findViewById(R.id.text_area_ofcompany);
			text_area_ofcompany.setText(companyDuns100.getArea().replaceAll("null", "").trim());
			text_area_ofcompany.setTypeface(almoni_aaa_regular);
		}
		else
		{
			ll_area.setVisibility(View.GONE);
		}

		if (!TextUtils.isEmpty(companyDuns100.getYear().replaceAll("null", "").trim()))
		{
			text_years_ofcompany = (TextView) findViewById(R.id.text_years_ofcompany);
			text_years_ofcompany.setText(companyDuns100.getYear().replaceAll("null", "").trim());
			text_years_ofcompany.setTypeface(almoni_aaa_regular);
		}
		else
		{
			ll_years.setVisibility(View.GONE);
		}

		scrollView_managers = (HorizontalScrollView) findViewById(R.id.scrollView_managers);
		if (companyDuns100.getManagerDuns100s().size() > 0)
		{

			managerContenair = (LinearLayout) findViewById(R.id.managerContenair);

			for (int i = 0; i < companyDuns100.getManagerDuns100s().size(); i++)
			{

				View view = layoutInflater.inflate(R.layout.manager_item_layout, null);
				ImageView imageView = (ImageView) view.findViewById(R.id.managerImg);
				TextView nameOfManager = (TextView) view.findViewById(R.id.txt_name_of_manager);
				nameOfManager.setTypeface(almoni_aaa_blod);
				TextView txt_role_of_manager = (TextView) view.findViewById(R.id.txt_role_of_manager);
				txt_role_of_manager.setTypeface(almoni_aaa_regular);
				Picasso.with(getApplicationContext()).load(companyDuns100.getManagerDuns100s().get(i).getManagerImg().replaceAll("null", "").trim()).into(imageView);
				nameOfManager.setText(companyDuns100.getManagerDuns100s().get(i).getName().replaceAll("null", "").trim());
				txt_role_of_manager.setText(companyDuns100.getManagerDuns100s().get(i).getRole().replaceAll("null", "").trim());
				final String managerDocID = companyDuns100.getManagerDuns100s().get(i).getManager_doc_id();
				view.setOnClickListener(new OnClickListener()
				{

					@Override
					public void onClick(View v)
					{
						Intent intent = new Intent(CompanyActivity.this, ManagerActivity.class);
						intent.putExtra(Definitions.URI_TO_PARSE,"http://stage.globes.co.il/data/webservices/duns100.asmx/duns_manager?doc_id=" + managerDocID);
						intent.putExtra("managerDocID", managerDocID);
						startActivity(intent);
					}
				});

				managerContenair.addView(view);
			}
		}
		else
		{
			scrollView_managers.setVisibility(View.GONE);
		}

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

	private class CustomeGestureDetector extends SimpleOnGestureListener
	{

		

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
				Intent intent = new Intent(getApplicationContext(), ShareActivity.class);
				intent.putExtra(Definitions.CALLER, Definitions.SHARES);
				intent.putExtra(Definitions.PARSER, Definitions.SHARES);
				intent.putExtra("feederId", feeder);
				intent.putExtra("shareId", insturmentID);
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
				Intent intent = new Intent(getApplicationContext(), DocumentActivity_new.class);
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
	private void showDialog(String title, String msg, String neutBtn, String negBtn, final String url)
	{

		AlertDialog.Builder ad = new AlertDialog.Builder(getApplicationContext());
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
	
	@Override
	protected void onStart()
	{
//		Log.i("eli", "Android_Duns100CompanyPage_#"+CompanyDocId);
		Utils.writeScreenToGoogleAnalytics(this, "Android_Duns100CompanyPage_#"+CompanyDocId);
		super.onStart();
	}
//	@Override
//	protected void onStop()
//	{
//		EasyTracker.getInstance(this).activityStop(this);
//		super.onStop();
//	}
	
}
