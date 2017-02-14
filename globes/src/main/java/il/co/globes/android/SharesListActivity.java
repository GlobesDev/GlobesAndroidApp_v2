package il.co.globes.android;

//import il.co.globes.android.objects.Instrument;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import android.widget.TextView.OnEditorActionListener;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.doubleclick.PublisherAdView;
import il.co.globes.android.objects.GlobesURL;
import il.co.globes.android.objects.Stock;
import tv.justad.sdk.view.JustAdView;
import tv.justad.sdk.view.JustAdViewDelegate;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class SharesListActivity extends MainActivity {
    private final String TAG = "SharesListActivity";
    // Context context = this.context;
    final Activity sharesListActivity = this;
    static final int SHARE_PAGE = 5;

    /**
     * Ads
     */
    // DfpAdView dfpAdView;
    PublisherAdView dfpAdView;
    JustAdView jaAdView;
    ListView listView;
    ImageView buttonSearchStocks;
    RelativeLayout.LayoutParams params;
    InputMethodManager imm;

    @Override
    public void onCreate(Bundle icicle) {

        super.onCreate(icicle);
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        createStockSearchLayout();

    }

    @Override
    void setContentView() {
        setContentView(R.layout.pull_to_refresh_with_search);
    }

    @Override
    void initAdView() {
        // Look up the DfpAdView as a resource and load a request.
        dfpAdView = (PublisherAdView) this.findViewById(R.id.adView2);

        listView = (ListView) findViewById(android.R.id.list);

        // layout params
        params = (RelativeLayout.LayoutParams) listView.getLayoutParams();

        dfpAdView.setAdListener(new AdListener() {
            public void onAdLoaded() {
                // Ad received
                Log.d(TAG, "Ad received: ");

                // set the margin if Ad received
                // left,top,right,bottom
                params.setMargins(0, 0, 0, converDpToPx(50));
                listView.setLayoutParams(params);

                dfpAdView.setBackgroundColor(Color.TRANSPARENT);
                if (dfpAdView.getVisibility() != View.VISIBLE) {
                    dfpAdView.setVisibility(View.VISIBLE);
                }
            }

            public void onAdFailedToLoad(int errorCode) {
                // Didn't receive Ad
                Log.d(TAG, "Ad Failed: " + errorCode);

                // left,top,right,bottom
                params.setMargins(0, 0, 0, 0);
                listView.setLayoutParams(params);

                dfpAdView.setVisibility(View.GONE);

                if (tracker != null) {
                    tracker.trackPageView("/" + TAG + Definitions.AD_FAILURE_TAG_BOTTOM_BANNER_PORTRAIT + errorCode);
                }
            }
        });
        PublisherAdRequest request = new PublisherAdRequest.Builder().build();
        dfpAdView.loadAd(request);

    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        int newPosition = position - 1;

        if (parsedNewsSet.itemHolder.get(newPosition).getClass() == Stock.class) {
            String uri = GlobesURL.URLSharePage.replace("XXXX", "tase").replace("ZZZZ", "d")
                    .replace("YYYY", ((Stock) parsedNewsSet.itemHolder.get(newPosition)).getId());

            Intent intent = new Intent(this, ShareActivity.class);
            intent.putExtra("URI", uri);
            intent.putExtra("symbol", ((Stock) parsedNewsSet.itemHolder.get(newPosition)).getSymbol());
            intent.putExtra(Definitions.CALLER, Definitions.SHARES);
            intent.putExtra(Definitions.PARSER, Definitions.SHARES);
            intent.putExtra("name", ((Stock) parsedNewsSet.itemHolder.get(newPosition)).getName_he());
            intent.putExtra("shareId", ((Stock) parsedNewsSet.itemHolder.get(newPosition)).getId());
            intent.putExtra("instrumentIdForSharing", ((Stock) parsedNewsSet.itemHolder.get(newPosition)).getId());
            // indicate we are from ShareListActivity
            intent.putExtra(ShareActivity.KEY_FROM_MARKETS_OR_STOCKS_LIST, true);
            startActivityForResult(intent, SHARE_PAGE);
            return;
        }
        super.onListItemClick(l, v, newPosition, id);
    }

    void createStockSearchLayout() {
        final EditText Et = (EditText) findViewById(R.id.editTextSearchStock);

        Et.setOnEditorActionListener(new OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {

                    imm.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    return true;
                }
                return false;

            }

        });

        Et.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onTextChanged(CharSequence inputCharSeq, int start, int before, int count) {

                String searchString = inputCharSeq.toString();
                if (searchString.length() >= 2) {
                    try {
                        uriToParse = GlobesURL.URLStocksContains.replaceAll("<query>", URLEncoder.encode(searchString, "UTF-8"));
                        // uriToParse =
                        // Definitions.URLStocksOld.replaceAll("XXXX",
                        // URLEncoder.encode(searchString, "UTF-8"));
                        // ((PullToRefreshListView) getListView()).onRefresh();

                        // ((SwipeListView) getListView()).onRefresh();
                    } catch (UnsupportedEncodingException e) {

                    }
                }
            }

        });

        buttonSearchStocks = (ImageView) findViewById(R.id.buttonSearchStocks);
        buttonSearchStocks.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO handle the touch event
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                return true;
            }
        });

    }

    @Override
    void createBannerData() {
        // do nothing.
    }

    /**
     * load the rich media jaAdView
     */
    @Override
    protected void loadRichMedia() {
        jaAdView.initAd();
    }

    /**
     * init the rich media
     */
    @Override
    protected void initRichMedia() {
        jaAdView = (JustAdView) findViewById(R.id.justAdView_RICH_MEDIA_pull_to_refresh_with_search);

        /**
         * Be sure to call setAd() before using the ad. example -
         * http://sdk.mobiyield.com/TN/tn_mobile_android_richmedia_320X480.html
         * globes -
         * http://sdk.mobiyield.com/Globes/richmedia_320_480_globes_android.html
         *
         */
        jaAdView.selectAd("http://sdk.mobiyield.com/Globes/richmedia_320_480_globes_android.html", null);

        /**
         * Set Delegate for jaAdView1
         */
        jaAdView.setDelegate(new JustAdViewDelegate() {

            @Override
            public void AdError(JustAdView adView, String message) {
                super.AdError(adView, message);
                Log.d(TAG, "Rich Ad Error: " + message);

                /**
                 * Error occured on loading the Ad 1.Log it 2.finish the
                 * Activity and resume the App
                 */
                jaAdView.setVisibility(View.GONE);
            }

            @Override
            public void AdLoaded(JustAdView adView) {
                super.AdLoaded(adView);
                Log.d(TAG, "Rich Ad loaded success: " + adView.toString());

                /** Ad loaded now we need to start it */
                dfpAdView.setVisibility(View.GONE);
                jaAdView.setVisibility(View.VISIBLE);
                jaAdView.startAd();
            }

            @Override
            public void AdStarted(JustAdView adView) {
                super.AdStarted(adView);
                Log.d(TAG, "Rich Ad started displaying: " + adView.toString());

            }

            @Override
            public void AdStopped(JustAdView adView) {
                super.AdStopped(adView);
                Log.d(TAG, "Ad stopped");

                /** Ad stopped from playing we finish the activity */
                jaAdView.setVisibility(View.GONE);
                dfpAdView.setVisibility(View.VISIBLE);
            }

        });

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_CANCELED) {
            if (requestCode == SHARE_PAGE) {
                if (super.adapter != null) {
                    super.adapter.notifyDataSetChanged();
                }
            }
        }
    }

    /**
     * Ugly fix for in order to prevent from the push dialog to be invoked few
     * times
     */
    @Override
    protected void savePrefsIntValue(String key, int value) {
    }

    /**
     * Gets the StockID by pos , called from lazyAdapter upon clicking the check
     * box inside a row
     *
     * @param pos - position clicked from the adapter
     * @return - stock id to use in Ticker
     */
    public String getStockIDbyPosition(int pos) {
        int newPosition = pos - 1;

        if (parsedNewsSet != null && parsedNewsSet.itemHolder.get(newPosition).getClass() == Stock.class) {
            return ((Stock) parsedNewsSet.itemHolder.get(newPosition)).getId();
        } else {
            return null;
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