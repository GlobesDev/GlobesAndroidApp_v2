package il.co.globes.android;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

/**
 * @author Eviatar <br>This class is the Activity that get opened when a user
 *         clicked a katava pirsumit from main activity
 */
public class RowAdActivity extends Activity {
    WebView webView;
    Button btnClose;
    ProgressDialog progressDialog;
    boolean isMainUrlLoad;

    public final static String KEY_URL_AD = "url";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_row_ad);

        isMainUrlLoad = true;
        progressDialog = ProgressDialog.show(this, "", Definitions.Loading, true, false);

        initUI();

        initWebView();

        Intent intent = getIntent();

        if (intent != null) {
            String urlAd = intent.getStringExtra(KEY_URL_AD);

            if (urlAd != null && urlAd.length() > 0) {
                webView.loadUrl(urlAd);
            }
        }

    }

    private void initUI() {
        btnClose = (Button) findViewById(R.id.Button_close_ad);
        btnClose.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        webView = (WebView) findViewById(R.id.webView_katava_pirsumit);
    }

    @SuppressLint(
            {"NewApi", "SetJavaScriptEnabled"})
    private void initWebView() {
        WebSettings settings = webView.getSettings();

        // remove zoom controls for API > 3.0
        if (android.os.Build.VERSION.SDK_INT >= VERSION_CODES.HONEYCOMB) {
            settings.setDisplayZoomControls(false);
        }

        // enable plugins
        if (android.os.Build.VERSION.SDK_INT > VERSION_CODES.FROYO) {
            settings.setPluginState(PluginState.ON);
        } else {
            //settings.setPluginsEnabled(true);
        }

        // enable java script
        settings.setJavaScriptEnabled(true);

        // webView.setWebViewClient(new MyWebViewClient());

        webView.setWebViewClient(new MyWebViewClient());

    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);

            if (isMainUrlLoad && progressDialog != null && progressDialog.isShowing()) {
                isMainUrlLoad = false;
                try {
                    progressDialog.dismiss();

                } catch (Exception e) {

                }
            }

        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url != null && url.contains("tel:")) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
                return true;
            }
            return false;
        }
    }

    @Override
    protected void onStart() {
//		EasyTracker.getInstance(this).activityStart(this);
        super.onStart();
    }

    @Override
    protected void onStop() {
//		EasyTracker.getInstance(this).activityStop(this);
        super.onStop();
    }

}
