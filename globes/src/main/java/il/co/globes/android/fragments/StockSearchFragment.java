package il.co.globes.android.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
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
 *         Stock search Fragment , used currently as left menu
 */
@SuppressLint("SetJavaScriptEnabled")
public class StockSearchFragment extends SherlockFragment {
    // callback
    private GlobesListener mCallback;

    // UI
    private WebView webView;
    private ProgressBar progressBar;

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
        View view = inflater.inflate(R.layout.fragment_stock_search, null);

        // find views
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar_stock_search_fragment);
        webView = (WebView) view.findViewById(R.id.webview_stock_search_fragment);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // set webview as current webview at host
        mCallback.onSetFragmentWebview(webView);

        initUI();
    }

    /**
     * Init UI elements
     */
    private void initUI() {
        // set the webview client in order to prevent app exit on click
        webView.setWebViewClient(new myWebViewClient());

        // java script
        webView.getSettings().setJavaScriptEnabled(true);

        // load URL
        webView.loadUrl(GlobesURL.URLStockSearch);
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

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            // check if it is share page to view
            if (url.contains("http://m.globes.co.il/news/m/instrument.aspx?h=3&iid=")) {
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
                intent.putExtra(Definitions.CALLER, Definitions.SHARES);
                intent.putExtra(Definitions.PARSER, Definitions.SHARES);
                intent.putExtra("feederId", feeder);
                intent.putExtra("shareId", rawIidAndFeeder);
                intent.putExtra("URI", uri);
                startActivity(intent);
                return true;
            } else if (!url.contains("http")) {
                url = GlobesURL.URLStockSearch;
                view.loadUrl(url);
                return true;
            }
            return false;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
//        EasyTracker tracker = EasyTracker.getInstance(getActivity());
//        tracker.set(Fields.SCREEN_NAME, getActivity().getResources().getString(R.string.il_co_globes_android_fragments_StockSearchFragment));
//        tracker.send(MapBuilder.createAppView().build());
    }

}
