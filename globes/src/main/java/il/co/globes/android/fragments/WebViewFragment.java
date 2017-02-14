package il.co.globes.android.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import com.actionbarsherlock.app.SherlockFragment;
import il.co.globes.android.Definitions;
import il.co.globes.android.R;
import il.co.globes.android.ShareActivity;
import il.co.globes.android.interfaces.GlobesListener;
import il.co.globes.android.objects.GlobesURL;

/**
 * @author Eviatar<br>
 *         <br>
 *         General purpose WebView Fragment with extensible WebClient
 */
@SuppressLint("SetJavaScriptEnabled")
public class WebViewFragment extends SherlockFragment {
    // UI
    private WebView webView;
    private ProgressBar progressBar;
    // header
    private String header;
    // data
    private String initialURL;

    // callback
    private GlobesListener mCallback;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (GlobesListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement " + GlobesListener.class.getSimpleName());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_webview_fragment, null);
        Log.e("eli", "WebViewFragment onCreateView menayot");
        // find views
        webView = (WebView) view.findViewById(R.id.webView_webview_fragment);

        progressBar = (ProgressBar) view.findViewById(R.id.progressBar_webview_fragment);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // set webview as current webview at host

        // get header from bundle
        header = getArguments().getString(Definitions.HEADER);

        if (header != null) {
            // TextView textViewHeader = (TextView)
            // getView().findViewById(R.id.grouptitle);
            // textViewHeader.setText(header);
            // if (Definitions.flipAlignment)
            // {
            // textViewHeader.setGravity(android.view.Gravity.RIGHT);
            // }

            // set custom action Bar with the header
            mCallback.onSetActionBarWithTextTitle(header, R.color.blue_light);

        }

        mCallback.onSetFragmentWebview(webView);
        initUI();

    }

    /**
     * Initialize UI elements with data
     */
    private void initUI() {
        // set the webview client in order to prevent app exit on click
        //webView.setWebViewClient(new myWebViewClient());

        // java script
        webView.getSettings().setJavaScriptEnabled(true);

        // load URL

        //webView.setWebChromeClient(new WebChromeClient());
        //webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);

        //webView.getSettings().setDomStorageEnabled(true);

        //if(initialURL.indexOf("/emagazine") > 0)
        //{
        //	initialURL = "http://stage.globes.co.il/news/emagazine/?v=5";
        //}

        Log.e("alex", "initUI_11: " + initialURL);
        webView.loadUrl(initialURL);

    }

    private class myWebViewClient extends WebViewClient {

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            progressBar.setVisibility(View.GONE);

        }
//		@Override
//		public boolean shouldOverrideUrlLoading(WebView view, String url)
//		{
//			return false;
//		}

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // check if it is share page to view
            Log.e("eli", "shouldOverrideUrlLoading " + url);
            if (url.contains("m.globes.co.il/news/m/instrument.aspx?h=3&iid="))
            //if (url.contains("http://m.globes.co.il/news/m/instrument.aspx?h=2&iid="))
            {
                String feeder, rawIidAndFeeder, uri;
                feeder = "0";
                int feederStartIndex;
                rawIidAndFeeder = new String(url.substring(url.indexOf("iid=") + 4));
                feederStartIndex = rawIidAndFeeder.indexOf("&f");
                if (feederStartIndex != -1) {
                    feeder = new String(rawIidAndFeeder.substring(feederStartIndex + 3));
                    rawIidAndFeeder = new String(rawIidAndFeeder.substring(0, feederStartIndex));
                }
                uri = GlobesURL.URLSharePage.replaceAll("XXXX", feeder).replaceAll("YYYY", rawIidAndFeeder);
                Intent intent = new Intent(getActivity(), ShareActivity.class);
                intent.putExtra("shareId", rawIidAndFeeder);
                intent.putExtra("feederId", feeder);
                intent.putExtra(Definitions.CALLER, Definitions.SHARES);
                intent.putExtra(Definitions.PARSER, Definitions.SHARES);
                intent.putExtra("feederId", feeder);
                intent.putExtra("shareId", rawIidAndFeeder);
                intent.putExtra("URI", uri);
                startActivity(intent);
                return true;
            } else if (!url.contains("http")) {

                url = GlobesURL.URLFinancialPortal;
                view.loadUrl(url);
                return true;
            }
            return false;
        }
    }

    public String getInitialURL() {
        return initialURL;
    }

    public void setInitialURL(String initialURL) {
        this.initialURL = initialURL;
    }

    @Override
    public void onStart() {
//		Log.e("eli", header);
        super.onStart();
//        EasyTracker tracker = EasyTracker.getInstance(getActivity());
//        tracker.set(Fields.SCREEN_NAME, getActivity().getResources().getString(R.string.il_co_globes_android_fragments_WebViewFragment));
//        tracker.send(MapBuilder.createAppView().build());
    }

}
