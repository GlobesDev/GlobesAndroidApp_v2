package il.co.globes.android;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ListView;
import android.widget.TextView;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.doubleclick.PublisherAdView;
import tv.justad.sdk.view.JustAdView;
import tv.justad.sdk.view.JustAdViewDelegate;

public class SectionListActivity extends MainActivity {

    String header;
    // Context context = this.context;
    final Activity sectionsListActivity = this;

    private final String TAG = "SectionListActivity";

    /**
     * Ads
     */
    // DfpAdView dfpAdView;
    JustAdView jaAdView;
    ListView listView;
    // DfpAdView dfpAdViewOptional;
    PublisherAdView dfpAdView;
    PublisherAdView dfpAdViewOptional;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        header = intent.getStringExtra(Definitions.HEADER);

        if (header != null) {
            TextView textViewHeader = (TextView) findViewById(R.id.grouptitle);
            textViewHeader.setText(header);
            if (Definitions.flipAlignment) {
                textViewHeader.setGravity(android.view.Gravity.RIGHT);
            }
            if (header.equals("שוק ההון")) {
                initOptionalDfpAdView();
            }
        }
    }

    @Override
    void initAdView() {
//        // Look up the DfpAdView as a resource and load a request.
//        dfpAdView = (PublisherAdView) this.findViewById(R.id.adViewSectionListActivity);
//
//        dfpAdView.setAdListener(new AdListener() {
//            public void onAdLoaded() {
//                // Ad received
//                Log.d(TAG, "Ad received: ");
//                dfpAdView.setBackgroundColor(Color.TRANSPARENT);
//                dfpAdView.setVisibility(View.VISIBLE);
//            }
//
//            public void onAdFailedToLoad(int errorCode) {
//                Log.d(TAG, "Ad Failed: " + errorCode);
//
//                dfpAdView.setVisibility(View.GONE);
//
//                if (tracker != null) {
//                    tracker.trackPageView("/" + TAG + Definitions.AD_FAILURE_TAG_BOTTOM_BANNER_PORTRAIT + errorCode);
//                }
//            }
//        });
//        PublisherAdRequest request = new PublisherAdRequest.Builder().build();
//        dfpAdView.loadAd(request);

    }

    private void initOptionalDfpAdView() {
        // optional DFP adview for שוק ההון only
//        dfpAdViewOptional = (PublisherAdView) findViewById(R.id.dfpAdView_pull_to_refresh_with_header_optional);
//
//        dfpAdViewOptional.setAdListener(new AdListener() {
//            public void onAdLoaded() {
//                Log.d(TAG, "adViewOptional received on SectionListActivity Shook Hahon: ");
//                dfpAdViewOptional.setVisibility(View.VISIBLE);
//            }
//
//            public void onAdFailedToLoad(int errorCode) {
//                Log.e(TAG, "adViewOptional failed on SectionListActivity Shook Hahon error is: " + errorCode);
//                dfpAdViewOptional.setVisibility(View.GONE);
//
//                if (tracker != null) {
//                    tracker.trackPageView("/" + TAG + Definitions.AD_FAILURE_TAG_OPTIONAL_PORTAL_FINANCY + errorCode);
//                }
//
//            }
//        });
//        PublisherAdRequest requestdfpAdViewOptional = new PublisherAdRequest.Builder().build();
//        dfpAdViewOptional.loadAd(requestdfpAdViewOptional);
    }

    @Override
    void setContentView() {
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.pull_to_refresh_with_header);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.globes_title);

    }

    /**
     * load the rich media jaAdview
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
        jaAdView = (JustAdView) findViewById(R.id.justAdView_RICH_MEDIA_pull_to_refresh_with_header);

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

    // @Override
    // void createBannerData()
    // {
    // URL listBannerURL;
    //
    // Vector<Banner> banners = new Vector<Banner>();
    // for (int i=0; i<5; i++)
    // {
    // try
    // {
    // listBannerURL = new
    // URL(Utils.getBannerParameters(context,Definitions.URLBannerSmall,462,100));
    // Banner bannerSplashForList = Banner.getBanner(listBannerURL);
    // banners.add(bannerSplashForList);
    // }
    // catch (Exception e) {}
    // }
    //
    // try
    // {
    // //if
    // (banners.elementAt(0).getBannerURL()!=null)parsedNewsSet.itemHolder.add(2,
    // banners.elementAt(0));
    // if
    // (banners.elementAt(1).getBannerURL()!=null)parsedNewsSet.itemHolder.add(7,
    // banners.elementAt(1));
    // if
    // (banners.elementAt(2).getBannerURL()!=null)parsedNewsSet.itemHolder.add(17,
    // banners.elementAt(2));
    // if
    // (banners.elementAt(3).getBannerURL()!=null)parsedNewsSet.itemHolder.add(27,
    // banners.elementAt(3));
    // if
    // (banners.elementAt(4).getBannerURL()!=null)parsedNewsSet.itemHolder.add(parsedNewsSet.itemHolder.size()-3,
    // banners.elementAt(4));
    // }
    // catch(ArrayIndexOutOfBoundsException e)
    // {
    //
    // }
    // }

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