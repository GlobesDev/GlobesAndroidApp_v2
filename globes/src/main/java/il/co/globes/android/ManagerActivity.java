package il.co.globes.android;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.*;
import com.squareup.picasso.Picasso;
import il.co.globes.android.objects.CustomWebView;
import il.co.globes.android.objects.GlobesURL;
import il.co.globes.android.objects.ManagerDuns100;
import net.tensera.sdk.utils.Logger;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;

public class ManagerActivity extends Activity

{
    Typeface almoni_aaa_regular, almoni_aaa_light, almoni_aaa_blod, almoni_aaa_black;
    String uriToParse;
    ManagerDuns100 managerDuns100;

    private TextView textViewManagerText, textView_name_of_manage, text_email_of_manager, text_studies_of_manager, text_role_of_manager,
            text_old_role_of_manager, text_experience_of_manager;
    private LinearLayout ll_email, ll_studies, ll_role, ll_old_roles, ll_manager_experience;
    private ImageView img_ofManager, img_ofcompany;

    CustomWebView wvViewManagerText;

    SharedPreferences prefs;
    private final String FONT_SIZE_KEY_PREF = "fontSizePref";
    AlertDialog alertDialog;
    int baseTextSize = 15;
    int position = 0;
    private ProgressDialog pd;
    static final int DONE_PARSING = 0;
    static final int ERROR_PARSING = 1;
    static final int DONE_GETTING_BANNER = 2;
    static final int ERROR_GETTING_BANNER = 3;
    static final int CLOSE_SPLASH = 5;
    static final int SPLASH_DISPLAY_LENGHT = 2000;
    private String articleTextHtml = "";
    static final int MAXFONTSIZE = 18;
    static final int MINFONTSIZE = 10;
    ImageView buttonShare;
    private ImageView imageView_back_actionBar_documentN, imageView_textSize_actionBar_documentN, img_mark;
    ScrollView myScrollView;
    private boolean isFontReduced = false;
    private String managerDocID;

    private enum EncodeType {
        TYPE_TITLE,
        TYPE_SUBTITLE,
        TYPE_BODY
    }

    ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manager_activity_layout);

        almoni_aaa_regular = Typeface.createFromAsset(this.getAssets(), "almoni-dl-aaa-regular.otf");
        almoni_aaa_blod = Typeface.createFromAsset(this.getAssets(), "almoni-dl-aaa-bold.otf");
        uriToParse = getIntent().getStringExtra(Definitions.URI_TO_PARSE);
        managerDocID = getIntent().getStringExtra("managerDocID");

        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        baseTextSize = loadFontPref(FONT_SIZE_KEY_PREF);

        setInitialData(uriToParse);

    }

    void createActionBarButtons() {
        buttonShare = (ImageView) findViewById(R.id.buttonShare);
        buttonShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        imageView_back_actionBar_documentN.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ManagerActivity.this.finish();

            }
        });
        imageView_textSize_actionBar_documentN = (ImageView) findViewById(R.id.imageView_textSize_actionBar_documentN);
        imageView_textSize_actionBar_documentN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeSizeFont();
            }
        });

    }

    /**
     * save the font size for next time
     *
     * @param key  FONT_SIZE_KEY_PREF
     * @param size the size we want to save
     */
    private void saveFontPref(String key, int size) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(key, size);
        editor.commit();
    }

    /**
     * Load the saved font size , 12 is default
     *
     * @param key FONT_SIZE_KEY_PREF
     * @return the saved font size / 12 for default
     */
    private int loadFontPref(String key) {
        return prefs.getInt(FONT_SIZE_KEY_PREF, 15);
    }

    synchronized void changeSizeFont() {
        baseTextSize++;
        if (baseTextSize >= MAXFONTSIZE) {
            baseTextSize = MINFONTSIZE;
            isFontReduced = true;

        }
        saveFontPref(FONT_SIZE_KEY_PREF, baseTextSize);

        // textViewDocumentModifiedOn.setTextSize((float) baseTextSize + 2);
        // textViewDocumentWriter.setTextSize((float) baseTextSize + 2);
        wvViewManagerText.getSettings().setDefaultFontSize((int) baseTextSize + 2);
        if (isFontReduced) {
            isFontReduced = false;
            wvViewManagerText.loadData(articleTextHtml, "text/html; charset=utf-8", "UTF-8");

        }

    }

    void setInitialData(final String uriToParse) {
        pd = ProgressDialog.show(ManagerActivity.this, "", Definitions.Loading, true, false);

        new AsyncTask<Void, Void, Integer>() {

            @Override
            protected Integer doInBackground(Void... params) {
                int res;
                try {
                    URL url = new URL(uriToParse.replaceAll("null", ""));
                    Log.e("eli", url.toString());
                    managerDuns100 = ManagerDuns100.parseManagerDuns100(url);

                    res = DONE_PARSING;

                } catch (Exception e) {
                    e.printStackTrace();
                    res = ERROR_PARSING;
                }
                return res;
            }

            @Override
            protected void onPostExecute(Integer result) {
                try {
                    pd.dismiss();
                } catch (Exception e) {
                }
                if (result == DONE_PARSING) {

                    initUI();
                    createActionBarButtons();

                } else if (result == ERROR_PARSING) {
                    Toast toast = Toast.makeText(ManagerActivity.this, Definitions.ErrorLoading, Toast.LENGTH_LONG);
                    toast.show();
                }
            }

        }.execute();
    }

    private void initUI() {
        Log.e("eli", managerDuns100.getRole());
        Log.e("eli", managerDuns100.getName());
        myScrollView = (ScrollView) findViewById(R.id.documentScrollView);

        wvViewManagerText = (CustomWebView) findViewById(R.id.textViewManagerText);
        wvViewManagerText.setGestureDetector(new GestureDetector(new CustomeGestureDetector()));
        articleTextHtml = managerDuns100.getSub_title();
        try {
            articleTextHtml = encodeTextForWebView(articleTextHtml, EncodeType.TYPE_BODY);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        // loading all pages inside the webview
        wvViewManagerText.setWebViewClient(new myWebViewClient());
        wvViewManagerText.loadData(articleTextHtml.trim(), "text/html; charset=utf-8", "UTF-8");
        wvViewManagerText.getSettings().setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
        wvViewManagerText.getSettings().setPluginState(PluginState.ON);

        wvViewManagerText.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        // TODO added for FB test
        wvViewManagerText.getSettings().setJavaScriptEnabled(true);

        img_ofManager = (ImageView) findViewById(R.id.img_ofManager);
        img_ofcompany = (ImageView) findViewById(R.id.img_ofcompany);

        Picasso.with(getApplicationContext()).load(managerDuns100.getImageOfManager().replaceAll("null", "").trim()).into(img_ofManager);
        Picasso.with(getApplicationContext()).load(managerDuns100.getImageLogoOfCompany().replaceAll("null", "").trim())
                .into(img_ofcompany);

        ll_email = (LinearLayout) findViewById(R.id.ll_email);
        ll_studies = (LinearLayout) findViewById(R.id.ll_studies);
        ll_role = (LinearLayout) findViewById(R.id.ll_role);
        // ll_old_roles = (LinearLayout) findViewById(R.id.ll_old_roles);
        // ll_manager_experience = (LinearLayout)
        // findViewById(R.id.ll_manager_experience);

        textView_name_of_manage = (TextView) findViewById(R.id.textView_name_of_manager);
        textView_name_of_manage.setText(managerDuns100.getName().replaceAll("null", "").trim());

        if (!TextUtils.isEmpty(managerDuns100.getEmail().replaceAll("null", "").trim())) {

            text_email_of_manager = (TextView) findViewById(R.id.text_email_of_manager);
            text_email_of_manager.setText(managerDuns100.getEmail().replaceAll("null", "").trim());
            text_email_of_manager.setTypeface(almoni_aaa_regular);
        } else {
            ll_email.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(managerDuns100.getEducation().replaceAll("null", "").trim())) {

            text_studies_of_manager = (TextView) findViewById(R.id.text_studies_of_manager);
            text_studies_of_manager.setText(managerDuns100.getEducation().replaceAll("null", "").trim());
            text_studies_of_manager.setTypeface(almoni_aaa_regular);
        } else {
            ll_studies.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(managerDuns100.getRole().replaceAll("null", "").trim())) {

            text_role_of_manager = (TextView) findViewById(R.id.text_role_of_manager);
            text_role_of_manager.setText(managerDuns100.getRole().replaceAll("null", "").trim());
            text_role_of_manager.setTypeface(almoni_aaa_regular);
        } else {
            ll_role.setVisibility(View.GONE);
        }

    }

    private String encodeTextForWebView(String html, EncodeType type) throws UnsupportedEncodingException {
        try {
            html = URLEncoder.encode(html, "utf-8").replaceAll("\\+", " ").replaceAll("null", "");
        } catch (UnsupportedEncodingException e) {
            html = "";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("<html>");
        sb.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">");
        sb.append("<body style=\"direction:rtl;\">");

        switch (type) {
            case TYPE_TITLE:
                sb.append("<strong style=\"line-height:30px\">" + html.trim() + "</strong>");
                break;
            case TYPE_SUBTITLE:
                sb.append("<strong style=\"line-height:25px\">" + html.trim() + "</strong>");
                break;
            case TYPE_BODY:
                sb.append("<span style=\"line-height:23px\">" + html.trim() + "</span>");
                break;

        }
        sb.append("</body>");
        sb.append("</html>");

        return sb.toString();
    }

    private class CustomeGestureDetector extends SimpleOnGestureListener {

        private final int SWIPE_MIN_DISTANCE = 100;// 120;
        private static final int SWIPE_MAX_OFF_PATH = 350;
        private final int SWIPE_THRESHOLD_VELOCITY = 200;// 150;

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            myScrollView.requestDisallowInterceptTouchEvent(true);
            if (e1 == null || e2 == null) return false;
            if (e1.getPointerCount() > 1 || e2.getPointerCount() > 1)
                return false;
            else {
                try { // right to left swipe .. go to prev page
                    if (e1.getX() - e2.getX() > 50 && Math.abs(velocityX) > 100) {// 200

                        // browseBack();
                        return true;
                    } // left to right swipe .. go to n page
                    else if (e2.getX() - e1.getX() > 50 && Math.abs(velocityX) > 100) {
                        // browseForward();

                        return true;
                    }

                } catch (Exception e) { // nothing
                }
                return false;
            }
        }
    }

    private class myWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // check if it is share page to view
            // was:
            // "http://www.globes.co.il/globessites/globes/finance/instruments/instrument.aspx?instrumentid"

            Log.e("alex", "PushAppsTest v1 url:" + url);

            if (url.contains("instrumentid")) {
                String feeder, insturmentID, uri;
                insturmentID = new String(url.substring(url.indexOf("instrumentid=") + 13, url.indexOf("&feeder")));
                feeder = new String(url.substring(url.indexOf("&feeder=") + 8, url.length()));
                int endFeederIndex = feeder.indexOf("&");
                if (endFeederIndex != -1) {
                    feeder = new String(feeder.substring(0, feeder.indexOf("&")));
                }
                uri = GlobesURL.URLSharePage.replaceAll("XXXX", feeder).replaceAll("YYYY", insturmentID);
                Intent intent = new Intent(getApplicationContext(), ShareActivity.class);
                intent.putExtra(Definitions.CALLER, Definitions.SHARES);
                intent.putExtra(Definitions.PARSER, Definitions.SHARES);
                intent.putExtra("shareId", insturmentID);
                intent.putExtra("feederId", feeder);
                intent.putExtra("URI", uri);
                startActivity(intent);
                return true;
            } // this is a document article
            // was Definitions.URLDocumentToShare
            else if (url.contains("did=")) {
                String articleID = new String(url.substring(url.indexOf("did=") + 4, url.length()));
                int endArticleIDIndex = articleID.indexOf("#");
                if (endArticleIDIndex != -1) {
                    articleID = new String(articleID.substring(0, endArticleIDIndex));
                }
                Intent intent = new Intent(getApplicationContext(), DocumentActivity_new.class);
                intent.putExtra(Definitions.CALLER, Definitions.WEBVIEW);
                intent.putExtra("articleId", articleID);
                startActivity(intent);
                return true;
            } else if (url.contains(".tag") || url.contains(".TAG")) {
                if (!url.contains("http://")) {
                    url = "http://www.globes.co.il";

                }
                Resources resources = getResources();
                String linkTitle = resources.getString(R.string.dialog_tagiot_title);
                String linkBody = resources.getString(R.string.dialog_tagiot_body);
                String linkContinue = resources.getString(R.string.dialog_continue);
                String linkCancel = resources.getString(R.string.dialog_cancel);
                showDialog(linkTitle, linkBody, linkContinue, linkCancel, url);
                return true;
            } else {
                if (!url.contains("http://")) {
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

    private void showDialog(String title, String msg, String neutBtn, String negBtn, final String url) {

        AlertDialog.Builder ad = new AlertDialog.Builder(getApplicationContext());
        ad.setTitle(title);
        ad.setMessage(msg);

        ad.setPositiveButton(neutBtn, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
                alertDialog.dismiss();

                return;
            }
        });

        ad.setNegativeButton(negBtn, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.dismiss();
                return;
            }
        });

        alertDialog = ad.create();
        alertDialog.show();
    }

    @Override
    protected void onStart() {
//		Log.i("eli", "Android_Duns100ManagerPage_#"+managerDocID);
        Utils.writeScreenToGoogleAnalytics(this, "Android_Duns100ManagerPage_#" + managerDocID);
        super.onStart();
    }
//	@Override
//	protected void onStop()
//	{
//		EasyTracker.getInstance(this).activityStop(this);
//		super.onStop();
//	}

}
