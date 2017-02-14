package il.co.globes.android;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.webkit.WebView;
import android.webkit.WebViewClient;

@SuppressLint("SetJavaScriptEnabled")
public class VideoWebViewActivity extends Activity {

    WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_web_view);
        mWebView = (WebView) findViewById(R.id.webView_activity_webview_video);

        //webview settings
        mWebView.setWebViewClient(new MyWebClient());
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        mWebView.loadUrl("http://www.cast-tv.biz/play/?media=yes&clid=22753&movId=dpfife&AndroidType=html5&autoplay=true");

    }

    private class MyWebClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.video_web_view, menu);
        return true;
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
