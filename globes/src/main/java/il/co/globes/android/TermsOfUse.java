package il.co.globes.android;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.facebook.AppEventsLogger;

public class TermsOfUse extends Activity {
    private WebView mWebView;
    Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.terms_of_use);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.globes_title);

        mWebView = (WebView) findViewById(R.id.wvTerms);
        mWebView.getSettings().setJavaScriptEnabled(true);

        final ProgressDialog pd = ProgressDialog.show(this, "", getString(R.string.tof_loading), true);

        mWebView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                if (progress == 100) {
                    if (pd.isShowing()) {
                        try {
                            pd.dismiss();
                        } catch (Exception e) {
                            // TODO: handle exception
                        }
                    }
                }
            }
        });

        mWebView.setWebViewClient(new OB_Client(pd));

        mWebView.loadUrl("http://m.tapuz.co.il/touch/globes/tof_android.htm");
    }

    private class OB_Client extends WebViewClient {
        private ProgressDialog pd;
        private Boolean resourceLoaded = false;

        @Override
        public void onLoadResource(WebView view, String url) {
            if (!resourceLoaded) {
                resourceLoaded = true;
            }
            super.onLoadResource(view, url);
        }

        public OB_Client(ProgressDialog pd) {
            this.pd = pd;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            resourceLoaded = false;
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            if (pd.isShowing()) {
                try {
                    pd.dismiss();
                } catch (Exception e) {
                }
            }
            super.onReceivedError(view, errorCode, description, failingUrl);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (UtilsWebServices.checkInternetConnection(context)) {
            AppEventsLogger.activateApp(context, Definitions.FACEBOOK_APP_ID);
        }
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();

        if (mWebView.canGoBack()) {
            mWebView.goBack();

        } else {
            TermsOfUse.this.finish();
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
