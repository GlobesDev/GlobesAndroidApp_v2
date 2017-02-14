package il.co.globes.android;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ListView;
import com.google.android.apps.analytics.GoogleAnalyticsTracker;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.doubleclick.PublisherAdView;
import il.co.globes.android.objects.GlobesURL;
import il.co.globes.android.objects.Instrument;

public class MarketsListActivity extends MainActivity {
    protected static final String TAG = "MarketsListActivity";

    final Activity marketsListActivity = this;

    static final int SHARE_PAGE = 5;

    // DfpAdView adViewOptional;
    PublisherAdView adViewOptional;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        // /////////////////////
        Rect rectgle = new Rect();
        Window window = getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(rectgle);

        tracker = GoogleAnalyticsTracker.getInstance();
        int StatusBarHeight = rectgle.top;
        int contentViewTop = window.findViewById(Window.ID_ANDROID_CONTENT).getTop();
        int TitleBarHeight = contentViewTop - StatusBarHeight;

        Log.i(TAG, "onCreate() at full screen no title bar : StatusBar Height= " + StatusBarHeight + " , TitleBar Height = "
                + TitleBarHeight);
        // ////////////////////
    }

    @Override
    void setContentView() {
        if (!caller.equals(Definitions.MAINTABACTVITY)) {
            requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        }

        setContentView(R.layout.pull_to_refresh);

        // the optional dfp
        adViewOptional = (PublisherAdView) findViewById(R.id.dfpAdView_pull_to_refresh_optional);
        adViewOptional.setAdListener(new AdListener() {
            public void onAdLoaded() {
                Log.d(TAG, "adViewOptional received on marketsListActivity: ");
                adViewOptional.setVisibility(View.VISIBLE);
            }

            public void onAdFailedToLoad(int errorCode) {
                Log.e(TAG, "adViewOptional failed on marketsListActivity error is: " + errorCode);
                adViewOptional.setVisibility(View.GONE);

                if (tracker != null) {
                    tracker.trackPageView("/" + TAG + Definitions.AD_FAILURE_TAG_OPTIONAL_PORTAL_FINANCY + errorCode);
                }
            }
        });
        PublisherAdRequest request = new PublisherAdRequest.Builder().build();
        adViewOptional.loadAd(request);

        if (!caller.equals(Definitions.MAINTABACTVITY)) {
            getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.globes_title);
        }
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        int newPosition = position - 1;
        if (parsedNewsSet.itemHolder.get(newPosition).getClass() == Instrument.class) {
            String uri = GlobesURL.URLSharePage.replace("XXXX", ((Instrument) parsedNewsSet.itemHolder.get(newPosition)).getFeeder())
                    .replace("YYYY", ((Instrument) parsedNewsSet.itemHolder.get(newPosition)).getId());

            Intent intent = new Intent(this, ShareActivity.class);
            intent.putExtra("URI", uri);

            intent.putExtra(Definitions.CALLER, Definitions.SHARES);
            intent.putExtra(Definitions.PARSER, Definitions.SHARES);
            intent.putExtra(Definitions.ISINSTRUMENT, ((Instrument) parsedNewsSet.itemHolder.get(newPosition)).isIndexWithInstruments());
            intent.putExtra("feederId", ((Instrument) parsedNewsSet.itemHolder.get(newPosition)).getFeeder());
            intent.putExtra("shareId", ((Instrument) parsedNewsSet.itemHolder.get(newPosition)).getId());
            intent.putExtra("name", ((Instrument) parsedNewsSet.itemHolder.get(newPosition)).getHe());

            // indicate we are from MarketsListActivity
            intent.putExtra(ShareActivity.KEY_FROM_MARKETS_OR_STOCKS_LIST, true);
            startActivityForResult(intent, SHARE_PAGE);
            return;
        }
        super.onListItemClick(l, v, position, id);

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