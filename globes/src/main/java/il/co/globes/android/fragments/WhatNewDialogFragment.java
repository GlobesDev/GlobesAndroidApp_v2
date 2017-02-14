package il.co.globes.android.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import il.co.globes.android.Definitions;
import il.co.globes.android.R;
import il.co.globes.android.ShareActivity;
import il.co.globes.android.interfaces.GlobesListener;
import il.co.globes.android.objects.GlobesURL;

public class WhatNewDialogFragment extends DialogFragment {
    // UI
    private WebView webView;
    private ProgressBar progressBar;
    // header
    private String header;
    // data
    private String initialURL;

    // callback
    private GlobesListener mCallback;
    private ImageView close_web_view;

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
        View view = inflater.inflate(R.layout.fragment_dialog_what_new, null);
        // find views
        webView = (WebView) view.findViewById(R.id.webView_webview_fragment);
        close_web_view = (ImageView) view.findViewById(R.id.close_web_view);
        close_web_view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                WhatNewDialogFragment.this.dismiss();
            }
        });
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar_webview_fragment);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        header = getArguments().getString(Definitions.HEADER);
        if (header != null) {
            // set custom action Bar with the header
            //				mCallback.onSetActionBarWithTextTitle(header, R.color.blue_light);
            mCallback.getsSlidingMenu().toggle();
        }
        mCallback.onSetFragmentWebview(webView);
        initUI();
    }

    /**
     * Initialize UI elements with data
     */
    private void initUI() {
        // set the webview client in order to prevent app exit on click
        webView.setWebViewClient(new myWebViewClient());
        // java script
        webView.getSettings().setJavaScriptEnabled(true);
        // load URL
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
//		EasyTracker tracker = EasyTracker.getInstance(getActivity());
//		tracker.set(Fields.SCREEN_NAME, getActivity().getResources().getString(R.string.il_co_globes_android_fragments_WebViewFragment));
//		tracker.send(MapBuilder.createAppView().build());
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);

        // request a window without the title
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

}
