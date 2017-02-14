package il.co.globes.android.native_video_player;



import il.co.globes.android.Definitions;
import il.co.globes.android.R;
import il.co.globes.android.Utils;
import il.co.globes.android.native_video_player.TrackingVideoView.CompleteCallback;
import il.co.globes.android.objects.GlobesURL;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnInfoListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.MediaController;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.VideoView;

import com.google.ads.interactivemedia.v3.api.Ad;
import com.google.ads.interactivemedia.v3.api.AdDisplayContainer;
import com.google.ads.interactivemedia.v3.api.AdErrorEvent;
import com.google.ads.interactivemedia.v3.api.AdErrorEvent.AdErrorListener;
import com.google.ads.interactivemedia.v3.api.AdEvent;
import com.google.ads.interactivemedia.v3.api.AdEvent.AdEventListener;
import com.google.ads.interactivemedia.v3.api.AdsLoader;
import com.google.ads.interactivemedia.v3.api.AdsLoader.AdsLoadedListener;
import com.google.ads.interactivemedia.v3.api.AdsManager;
import com.google.ads.interactivemedia.v3.api.AdsManagerLoadedEvent;
import com.google.ads.interactivemedia.v3.api.AdsRenderingSettings;
import com.google.ads.interactivemedia.v3.api.AdsRequest;
import com.google.ads.interactivemedia.v3.api.CompanionAdSlot;
import com.google.ads.interactivemedia.v3.api.ImaSdkFactory;
import com.google.ads.interactivemedia.v3.api.ImaSdkSettings;
//import com.google.ads.interactivemedia.v3.api.UiElement;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

public class PlayVideoActivity extends Activity implements
AdErrorListener,
AdsLoadedListener,
AdEventListener,
CompleteCallback
{

	private static String CONTENT_URL = "http://www.cast-tv.biz/play/?movId=iiflib&clid=22753&media=yes&autoplay=true";
	//			"http://rmcdn.2mdn.net/MotifFiles/html/1248596/" + "android_1330378998288.mp4";
	protected String[] defaultTagUrls;
	protected FrameLayout videoHolder;
	protected NativePlayer videoPlayerAd;
	protected VideoView videoPlayerVideo;
	protected ScrollView logScroll;
	protected TextView log;
	protected ViewGroup companionView;
	protected ViewGroup leaderboardCompanionView;
	protected AdsLoader adsLoader;
	protected AdsManager adsManager;
	protected AdsRenderingSettings adsSetting;
	protected AdDisplayContainer container;
	protected ImaSdkFactory sdkFactory;
	protected ImaSdkSettings sdkSettings;

	protected Button languageButton;
	protected Button requestAdButton;
	protected boolean isAdStarted;
	protected boolean isAdPlaying;

	protected boolean contentStarted = false;
	private ProgressDialog pd;
	private TrackingVideoView video;
	private boolean isJELLY_BEAN;	
	private MediaController mc;
	private boolean isUserTouchAd;
	private String docId;
		
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
			
		Log.e("alex", "PlayVideoActivity Start = " + Build.VERSION.SDK_INT);
		
		if (Build.VERSION.SDK_INT == 16)
		{
			isJELLY_BEAN = true;
			setContentView(R.layout.play_video_activity_layout_4_1);
		}
		else
		{
			isJELLY_BEAN = false;
			setContentView(R.layout.play_video_activity_layout);
		}
		defaultTagUrls = new String[1];
		Log.e("eli", "onCreate");
		show_ProgreeBar();
		// CONTENT_URL = this is playing after the ad
		String videoURL = getIntent().getExtras().getString("videoURL");
		docId = getIntent().getExtras().getString("docId");

		if (videoURL != null)
		{
			log("videoURL = " + videoURL);
			CONTENT_URL = videoURL ;
		}
		initUi();

		if (videoPlayerAd == null) 
		{
			videoPlayerAd = new NativePlayer(this);
			videoPlayerAd.setCompletionCallback(this);
		}
	    
		video = videoPlayerAd.getVideo();
		video.setOnPreparedListener(new OnPreparedListener()
		{
			@Override
			public void onPrepared(MediaPlayer mp)
			{
				video.start();
			}
		});
		video.setOnTouchListener(new OnTouchListener()
		{
			@Override
			public boolean onTouch(View v, MotionEvent event)
			{
				Ad ad = adsManager.getCurrentAd();
				isAdStarted = false;
				isAdPlaying = false;
				isUserTouchAd = true;
				//				videoHolder.removeView(videoPlayerAd);
				//				adsLoader.contentComplete();
				adsManager.destroy();
				video.setOnPreparedListener(null);

				String clickThroughUrl = Utils.getSubStringByValueAd(ad.toString(),", clickThroughUrl=", ",");
				Intent i = new Intent(Intent.ACTION_VIEW);
				i.setData(Uri.parse(clickThroughUrl));
				startActivity(i);
				return false;
			}
		});

		videoHolder.addView(videoPlayerAd);
		//		videoHolder.bringToFront();
		Log.e("eli", " ** * ** " + GlobesURL.TV_PREFIX+docId);
		//defaultTagUrls[0] = String.format(
		//		getResources().getString(R.string.url_simple_ad_pre_roll), CONTENT_URL,
		//		docId==null ? CONTENT_URL:(GlobesURL.TV_PREFIX+docId),
		//				System.currentTimeMillis()+"");

		String sPrerollURL = Definitions.AdsPreRollUrl; //(Definitions.AdsPreRollUrl != "") ? Definitions.AdsPreRollUrl : "http://lb.advsnx.net/asa/a.aspx?zi=888&t=Get&si=72&url=%1$s&description_url=%2$s&correlator=%3$s";
		
		if(!Definitions.toUseGIMA)
		{
			sPrerollURL = "";
		}
		
		sPrerollURL = "http://lb.advsnx.net/asa/a.aspx?zi=888&t=Get&si=72&tags=eNpLz8lPSi0GAAirAn0&cb=%1$s";
		
		try
		{
			sPrerollURL =  String.format(sPrerollURL, String.valueOf(System.currentTimeMillis()));
		}
		catch(Exception ex)
		{			
			Log.e("alex", "sPrerollURL Error:" + ex);
		}
		
		Log.e("alex", "sPrerollURL:" + sPrerollURL);
		
		//sPrerollURL = "https://lb.advsnx.net/asa/a.aspx?zi=888&t=Get&si=72&tags=eNpLz8lPSi0GAAirAn0%3D";
		
		//sPrerollURL ="https://pubads.g.doubleclick.net/gampad/ads?sz=640x480&iu=/124319096/external/single_ad_samples&ciu_szs=300x250&impl=s&gdfp_req=1&env=vp&output=vast&unviewed_position_start=1&cust_params=deployment%3Ddevsite%26sample_ct%3Dskippablelinear&correlator=";
	
		//sPrerollURL = "https://pubads.g.doubleclick.net/gampad/ads?sz=640x960|640x1136|320x480&iu=/7263/Preroll_Android_test&impl=s&gdfp_req=1&env=vp&output=vast&unviewed_position_start=1&url=%1$s&description_url=%2$s&correlator=%3$s";
				//"!!!!!!!!!!!!!http://lb.advsnx.net/asa/a.aspx?zi=888&t=Get&si=72&tags=eNpzz8lPSi0GAAfrAl0%3D&cb=%25%CACHEBUSTER%25%25&aid=%25%ADVERTISING_IDENTIFIER_PLAIN%25%25&vid=%25%25VIDEO_ID%25%25"; 
				//"http://lb.advsnx.net/asa/a.aspx?zi=888&t=Get&si=72&tags=eNpzz8lPSi0GAAfrAl0"; 
				//"https://pubads.g.doubleclick.net/gampad/ads?sz=640x960|640x1136|320x480&iu=/7263/Preroll_Android_test&impl=s&gdfp_req=1&env=vp&output=vast&unviewed_position_start=1&url=%1$s&description_url=%2$s&correlator=%3$s"; 
				//"https://lb.advsnx.net/asa/a.aspx?zi=888&t=Get&si=72&tags=eNpzz8lPSi0GAAfrAl0%3D&cb="; 
		//"http://www.adotube.com/php/services/player/OMLService.php?avpid=oRYYzvQ&platform_version=vast20&ad_type=linear&groupbypass=1&HTTP_REFERER=http://www.longtailvideo.com&video_identifier=longtailvideo.com,test"; 
		
		defaultTagUrls[0] = sPrerollURL;		
		
		/*
		defaultTagUrls[0] = String.format(
				sPrerollURL, CONTENT_URL,
				docId==null ? CONTENT_URL:(GlobesURL.TV_PREFIX+docId),
						System.currentTimeMillis()+"");
		*/
				
		sdkFactory = ImaSdkFactory.getInstance();

		setVideoPath(videoPlayerVideo, CONTENT_URL);
		videoPlayerVideo.requestFocus();
		videoPlayerVideo.setKeepScreenOn(true);

		createAdsLoader();
		requestAd();
	}

	@Override
	protected void onNewIntent(Intent intent)
	{
		super.onNewIntent(intent);
		Log.e("eli", "onNewIntent...");
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		isAdStarted = false;
		isAdPlaying = false;
		isUserTouchAd = true;
		show_progressBar_andPlay();
	}

	private void show_progressBar_andPlay()
	{
		show_ProgreeBar();
		//		pd.setOnShowListener(new OnShowListener()
		//		{
		//			@Override
		//			public void onShow(DialogInterface dialog)
		//			{
		playVideo();
		//			}
		//		});
	}


	@SuppressLint("NewApi")
	protected void initUi()
	{
		mc = new MediaController(this);

		videoHolder = (FrameLayout) findViewById(R.id.videoHolder);

		videoPlayerVideo = (VideoView) findViewById(R.id.videoHoldervideo);

		videoPlayerVideo.setOnErrorListener(new OnErrorListener() 
		{
			@Override
			public boolean onError(MediaPlayer mp, int what, int extra)
			{
				log("videoPlayerVideo error " + what);
				stop_ProgreeBar();

				return false;
			}
		});
		videoPlayerVideo.setOnPreparedListener(new OnPreparedListener()
		{
			@Override
			public void onPrepared(MediaPlayer mp) 
			{
				log("videoPlayerVideo Prepared");
				if (isJELLY_BEAN)
				{
					stop_ProgreeBar();
				}
				if (isUserTouchAd)
				{
					playVideo();
				}
				mp.setOnInfoListener(new OnInfoListener()
				{
					@Override
					public boolean onInfo(MediaPlayer mp, int what, int extra)
					{
						stop_ProgreeBar();

						switch (what)
						{
							case MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK :
								log("videoPlayerVideo OnInfoListener MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK");
								break;
							case MediaPlayer.MEDIA_ERROR_SERVER_DIED :
								log("videoPlayerVideo OnInfoListener MEDIA_ERROR_SERVER_DIED");
								break;
							case MediaPlayer.MEDIA_ERROR_UNKNOWN :
								log("videoPlayerVideo OnInfoListener MEDIA_ERROR_UNKNOWN");
								break;
							case MediaPlayer.MEDIA_INFO_BAD_INTERLEAVING :
								log("videoPlayerVideo OnInfoListener MEDIA_INFO_BAD_INTERLEAVING");
								break;
							case MediaPlayer.MEDIA_INFO_BUFFERING_END :
								log("videoPlayerVideo OnInfoListener MEDIA_INFO_BUFFERING_END");
								break;
							case MediaPlayer.MEDIA_INFO_BUFFERING_START :
								log("videoPlayerVideo OnInfoListener MEDIA_INFO_BUFFERING_START");
								break;
							case MediaPlayer.MEDIA_INFO_METADATA_UPDATE :
								log("videoPlayerVideo OnInfoListener MEDIA_INFO_METADATA_UPDATE");
								break;
							case MediaPlayer.MEDIA_INFO_NOT_SEEKABLE :
								log("videoPlayerVideo OnInfoListener MEDIA_INFO_NOT_SEEKABLE");
								break;
							case MediaPlayer.MEDIA_INFO_VIDEO_TRACK_LAGGING :
								log("videoPlayerVideo OnInfoListener MEDIA_INFO_VIDEO_TRACK_LAGGING");
								break;
							default: // MEDIA_INFO_VIDEO_RENDERING_START
								log("videoPlayerVideo OnInfoListener default MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK" + what );
								break;
						}
						return false;
					}
				});
				mp.setOnBufferingUpdateListener(new OnBufferingUpdateListener()
				{
					@Override
					public void onBufferingUpdate(MediaPlayer mp, int percent)
					{
						Log.e("eli", "percent = " + percent);
						if (percent > 80)
						{
							stop_ProgreeBar();
						}
						else 
						{
							//							mc.show();
							//							show_ProgreeBar();
						}
						if (percent < 10)
						{
							mc.show();
						}
					}
				});
			}
		});

		videoPlayerVideo.setOnCompletionListener(new OnCompletionListener()
		{
			@Override
			public void onCompletion(MediaPlayer mp)
			{
				log("videoPlayerVideo onCompletion ");
				finish();
			}
		});
	}

	protected void requestAd()
	{
		try
		{
			adsLoader.requestAds(buildAdsRequest());
		}
		catch(Exception ex)
		{
			Log.e("alex", "requestAd Error:" + ex.getMessage());
		}
	}	
	

	protected AdsRequest buildAdsRequest()
	{
		Log.e("alex", "IMA SDK buildAdsRequest() ");
		
		DisplayMetrics displaymetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
		int height = displaymetrics.heightPixels;
		int width = displaymetrics.widthPixels;
		//				height = 90;//728, 90
		//				width = 728;
		log( "height "+height+" width "+width);
		container = sdkFactory.createAdDisplayContainer();
		container.setPlayer(videoPlayerAd);
		container.setAdContainer(videoPlayerAd.getUiContainer());
		log("Requesting ads 1");
		
		AdsRequest request = sdkFactory.createAdsRequest();
		//request.setAdTagUrl(defaultTagUrls[0]);
			
		
		//request.setAdTagUrl("http://lb.advsnx.net/asa/a.aspx?zi=888&t=Get&si=72&tags=eNpLz8lPSi0GAAirAn0%3D&cb=4234234324325");//"http://sitedesign.co.il/vast/index2.php"
		
		request.setAdTagUrl("http://pubads.g.doubleclick.net/gampad/ads?sz=640x360&iu=/6062/iab_vast_samples/skippable&ciu_szs=300x250,728x90&impl=s&gdfp_req=1&env=vp&output=xml_vast2&unviewed_position_start=1&url=[referrer_url]&correlator=[timestamp]");
		                     
		//request.setAdTagUrl("https://pubads.g.doubleclick.net/gampad/ads?sz=640x960|640x1136|320x480&iu=/7263/Preroll_Android_test&impl=s&gdfp_req=1&env=vp&output=vast&unviewed_position_start=1&url=[referrer_url]&description_url=[description_url]&correlator=45465");
		
		//request.setAdTagUrl("https://lb.advsnx.net/asa/a.aspx?zi=888&t=Get&si=72&tags=eNpLz8lPSi0GAAirAn0%3D");
		//++request.setAdTagUrl("https://pubads.g.doubleclick.net/gampad/ads?sz=640x480&iu=/124319096/external/single_ad_samples&ciu_szs=300x250&impl=s&gdfp_req=1&env=vp&output=vast&unviewed_position_start=1&cust_params=deployment%3Ddevsite%26sample_ct%3Dskippablelinear&correlator=");
		
		//String sVast = "<VAST version=\"3.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"vast.xsd\"><Ad id=\"970\"><InLine><AdSystem>Advision</AdSystem><AdTitle><![CDATA[AdOpsTest -Mobile-VideoAd- PARA,BIG- Pre]]></AdTitle><Impression><![CDATA[http://lb.advsnx.net/AdServer/Service.svc/TrackEREvent?strTask=START&ZoneID=888&SiteID=72&CampaignID=970&BannerID=1356&AdvertiserID=89&PingId=5f8740c3-552d-4147-9b6d-195e356a0f3e&uuid=vxEIJXydoB%2b3wp49Kx%2fvWlAjnA6H0uxbEF7eBWcr1dNoLIFjGitKjHtkBqP%2fLqcz]]></Impression><Creatives><Creative sequence=\"0\" BannerID=\"1356\" skippable=\"1\" GroupID=\"-1\"><Linear skipoffset=\"00:00:10\"><VideoClicks><ClickThrough><![CDATA[http://lb.advsnx.net/asa/a.aspx?Task=Click&ZoneID=888&CampaignID=970&AdvertiserID=89&BannerID=1356&SiteID=72&RandomNumber=1624605332&uuid=vxEIJXydoB%2b3wp49Kx%2fvWlAjnA6H0uxbEF7eBWcr1dNoLIFjGitKjHtkBqP%2fLqcz]]></ClickThrough></VideoClicks><Duration>00:00:21.000</Duration><MediaFiles><MediaFile delivery=\"progressive\" width=\"640\" height=\"480\" type=\"video/mp4\" bitrate=\"0\" ><![CDATA[http://banners.advsnx.net/a/89/b/30fce132-f785-4f66-b5df-8ab48a05007f.mp4]]></MediaFile></MediaFiles><TrackingEvents><Tracking event=\"start\"><![CDATA[http://lb.advsnx.net/AdServer/Service.svc/TrackEREvent?strTask=START&ZoneID=888&SiteID=72&CampaignID=970&BannerID=1356&AdvertiserID=89&PingId=5f8740c3-552d-4147-9b6d-195e356a0f3e&uuid=vxEIJXydoB%2b3wp49Kx%2fvWlAjnA6H0uxbEF7eBWcr1dNoLIFjGitKjHtkBqP%2fLqcz]]></Tracking><Tracking event=\"firstQuartile\"><![CDATA[http://lb.advsnx.net/AdServer/Service.svc/TrackEREvent?strTask=FIRSTQUARTILE&ZoneID=888&SiteID=72&CampaignID=970&BannerID=1356&AdvertiserID=89&PingId=5f8740c3-552d-4147-9b6d-195e356a0f3e&uuid=vxEIJXydoB%2b3wp49Kx%2fvWlAjnA6H0uxbEF7eBWcr1dNoLIFjGitKjHtkBqP%2fLqcz]]></Tracking><Tracking event=\"midpoint\"><![CDATA[http://lb.advsnx.net/AdServer/Service.svc/TrackEREvent?strTask=MIDPOINT&ZoneID=888&SiteID=72&CampaignID=970&BannerID=1356&AdvertiserID=89&PingId=5f8740c3-552d-4147-9b6d-195e356a0f3e&uuid=vxEIJXydoB%2b3wp49Kx%2fvWlAjnA6H0uxbEF7eBWcr1dNoLIFjGitKjHtkBqP%2fLqcz]]></Tracking><Tracking event=\"thirdQuartile\"><![CDATA[http://lb.advsnx.net/AdServer/Service.svc/TrackEREvent?strTask=THIRDQUARTILE&ZoneID=888&SiteID=72&CampaignID=970&BannerID=1356&AdvertiserID=89&PingId=5f8740c3-552d-4147-9b6d-195e356a0f3e&uuid=vxEIJXydoB%2b3wp49Kx%2fvWlAjnA6H0uxbEF7eBWcr1dNoLIFjGitKjHtkBqP%2fLqcz]]></Tracking><Tracking event=\"complete\"><![CDATA[http://lb.advsnx.net/AdServer/Service.svc/TrackEREvent?strTask=COMPLETE&ZoneID=888&SiteID=72&CampaignID=970&BannerID=1356&AdvertiserID=89&PingId=5f8740c3-552d-4147-9b6d-195e356a0f3e&uuid=vxEIJXydoB%2b3wp49Kx%2fvWlAjnA6H0uxbEF7eBWcr1dNoLIFjGitKjHtkBqP%2fLqcz]]></Tracking><Tracking event=\"ping\"><![CDATA[http://lb.advsnx.net/AdServer/Service.svc/TrackEREvent?strTask=PING&ZoneID=888&SiteID=72&CampaignID=970&BannerID=1356&AdvertiserID=89&PingId=5f8740c3-552d-4147-9b6d-195e356a0f3e&uuid=vxEIJXydoB%2b3wp49Kx%2fvWlAjnA6H0uxbEF7eBWcr1dNoLIFjGitKjHtkBqP%2fLqcz]]></Tracking></TrackingEvents></Linear></Creative></Creatives></InLine></Ad></VAST>";
				
		
		//String sVast = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><VAST version=\"2.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"vast.xsd\"><Ad id=\"970\"><InLine><AdSystem>Advision</AdSystem><AdTitle><![CDATA[AdOpsTest -Mobile-VideoAd- PARA,BIG- Pre]]></AdTitle><Impression><![CDATA[https://lb.advsnx.net/AdServer/Service.svc/TrackEREvent?strTask=START&ZoneID=888&SiteID=72&CampaignID=970&BannerID=1345&AdvertiserID=89&PingId=5e375487-4da5-44e1-9df8-16292e1dee16&uuid=vxEIJXydoB%2b3wp49Kx%2fvWlAjnA6H0uxbEF7eBWcr1dNoLIFjGitKjHtkBqP%2fLqcz]]></Impression><Creatives><Creative sequence=\"0\" BannerID=\"1345\" skippable=\"0\" GroupID=\"-1\"><Linear><VideoClicks><ClickThrough><![CDATA[https://lb.advsnx.net/asa/a.aspx?Task=Click&ZoneID=888&CampaignID=970&AdvertiserID=89&BannerID=1345&SiteID=72&RandomNumber=187192320&uuid=vxEIJXydoB%2b3wp49Kx%2fvWlAjnA6H0uxbEF7eBWcr1dNoLIFjGitKjHtkBqP%2fLqcz]]></ClickThrough></VideoClicks><Duration>00:00:16.000</Duration><MediaFiles><MediaFile delivery=\"progressive\" width=\"640\" height=\"480\" type=\"video/mp4\" bitrate=\"0\" ><![CDATA[https://banners.advsnx.net/a/89/b/9c16db9a-153b-44c3-adf6-27a2af73d323.mp4]]></MediaFile></MediaFiles><TrackingEvents><Tracking event=\"start\"><![CDATA[https://lb.advsnx.net/AdServer/Service.svc/TrackEREvent?strTask=START&ZoneID=888&SiteID=72&CampaignID=970&BannerID=1345&AdvertiserID=89&PingId=5e375487-4da5-44e1-9df8-16292e1dee16&uuid=vxEIJXydoB%2b3wp49Kx%2fvWlAjnA6H0uxbEF7eBWcr1dNoLIFjGitKjHtkBqP%2fLqcz]]></Tracking><Tracking event=\"firstQuartile\"><![CDATA[https://lb.advsnx.net/AdServer/Service.svc/TrackEREvent?strTask=FIRSTQUARTILE&ZoneID=888&SiteID=72&CampaignID=970&BannerID=1345&AdvertiserID=89&PingId=5e375487-4da5-44e1-9df8-16292e1dee16&uuid=vxEIJXydoB%2b3wp49Kx%2fvWlAjnA6H0uxbEF7eBWcr1dNoLIFjGitKjHtkBqP%2fLqcz]]></Tracking><Tracking event=\"midpoint\"><![CDATA[https://lb.advsnx.net/AdServer/Service.svc/TrackEREvent?strTask=MIDPOINT&ZoneID=888&SiteID=72&CampaignID=970&BannerID=1345&AdvertiserID=89&PingId=5e375487-4da5-44e1-9df8-16292e1dee16&uuid=vxEIJXydoB%2b3wp49Kx%2fvWlAjnA6H0uxbEF7eBWcr1dNoLIFjGitKjHtkBqP%2fLqcz]]></Tracking><Tracking event=\"thirdQuartile\"><![CDATA[https://lb.advsnx.net/AdServer/Service.svc/TrackEREvent?strTask=THIRDQUARTILE&ZoneID=888&SiteID=72&CampaignID=970&BannerID=1345&AdvertiserID=89&PingId=5e375487-4da5-44e1-9df8-16292e1dee16&uuid=vxEIJXydoB%2b3wp49Kx%2fvWlAjnA6H0uxbEF7eBWcr1dNoLIFjGitKjHtkBqP%2fLqcz]]></Tracking><Tracking event=\"complete\"><![CDATA[https://lb.advsnx.net/AdServer/Service.svc/TrackEREvent?strTask=COMPLETE&ZoneID=888&SiteID=72&CampaignID=970&BannerID=1345&AdvertiserID=89&PingId=5e375487-4da5-44e1-9df8-16292e1dee16&uuid=vxEIJXydoB%2b3wp49Kx%2fvWlAjnA6H0uxbEF7eBWcr1dNoLIFjGitKjHtkBqP%2fLqcz]]></Tracking><Tracking event=\"ping\"><![CDATA[https://lb.advsnx.net/AdServer/Service.svc/TrackEREvent?strTask=PING&ZoneID=888&SiteID=72&CampaignID=970&BannerID=1345&AdvertiserID=89&PingId=5e375487-4da5-44e1-9df8-16292e1dee16&uuid=vxEIJXydoB%2b3wp49Kx%2fvWlAjnA6H0uxbEF7eBWcr1dNoLIFjGitKjHtkBqP%2fLqcz]]></Tracking></TrackingEvents></Linear></Creative></Creatives></InLine></Ad></VAST>";
		//request.setAdsResponse(sVast);
		
		//request.setAdTagUrl("https://pubads.g.doubleclick.net/gampad/ads?sz=640x480&iu=/124319096/external/single_ad_samples&ciu_szs=300x250&impl=s&gdfp_req=1&env=vp&output=vast&unviewed_position_start=1&cust_params=deployment%3Ddevsite%26sample_ct%3Dskippablelinear&correlator=");
		//request.setAdTagUrl("http://lb.advsnx.net/asa/a.aspx?zi=888&t=Get&si=72&tags=eNpzz8lPSi0GAAfrAl0%3D");
		//++request.setAdTagUrl("http://pubads.g.doubleclick.net/gampad/ads?sz=640x360&iu=/6062/iab_vast_samples/skippable&ciu_szs=300x250,728x90&impl=s&gdfp_req=1&env=vp&output=xml_vast3&unviewed_position_start=1&url=[referrer_url]&correlator=23423422149");
		//request.setAdTagUrl("https://pubads.g.doubleclick.net/gampad/ads?sz=640x960|640x1136|320x480&iu=/7263/Preroll_Android_test&impl=s&gdfp_req=1&env=vp&output=vast&unviewed_position_start=1&url=http://www.globes.co.il&description_url=test&correlator=1467802127729");
		//request.setAdTagUrl("http://binny1989.github.io/Skipadtag.xml");
	
		
		/*
		try
		{
			int v = getPackageManager().getPackageInfo("com.google.android.gms", 0 ).versionCode;
			String vn = getPackageManager().getPackageInfo("com.google.android.gms", 0 ).versionName;
			
			int v1= getPackageManager().getPackageInfo("com.google.ads.interactivemedia", 0 ).versionCode;
			String vn1 = getPackageManager().getPackageInfo("com.google.ads.interactivemedia", 0 ).versionName;
			
			Log.e("alex", "Google IMA Play Version:" + v1 + "===" + vn1);
		}
		catch (NameNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
		
		//request.setAdTagUrl("https://loopme.me/api/vast/ads?appId=e18c19fa43&vast=2&uid=1234&ip=8.8.8.8&bundleid=com.loopme&appname=my_talking_pet&sdk=16.2&exchange=admarvel");

		//Log.e("alex", "Ima SDK Vast: " + sVast);
		
		//request.setAdTagUrl("http://lb.advsnx.net/asa/a.aspx?zi=888&t=Get&si=72&tags=eNpzz8lPSi0GAAfrAl0");
		
		ArrayList<CompanionAdSlot> companionAdSlots = new ArrayList<CompanionAdSlot>();

		CompanionAdSlot companionAdSlot = sdkFactory.createCompanionAdSlot();
		companionAdSlot.setContainer(companionView);
		companionAdSlot.setSize(width, height);
		companionAdSlots.add(companionAdSlot);

		if (leaderboardCompanionView != null)
		{
			CompanionAdSlot leaderboardCompanionAdSlot = sdkFactory.createCompanionAdSlot();
			leaderboardCompanionAdSlot.setContainer(leaderboardCompanionView);
			leaderboardCompanionAdSlot.setSize(width, height);
			companionAdSlots.add(leaderboardCompanionAdSlot);
		}
		container.setCompanionSlots(companionAdSlots);
		request.setAdDisplayContainer(container);
		
		Log.e("alex", "IMA SDK END buildAdsRequest() ");
		
		return request;		
	}
	
	
	public String readFile(String path) throws IOException{
        StringBuilder sb = new StringBuilder();
        
        URL url = new URL(path);		
try (BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()))){
	String inputLine; 
    while ((inputLine = br.readLine()) != null) {
        sb.append(inputLine);
    }

}
catch(Exception ex)
{
	Log.e("alex", "ParseXML Error1: " + ex);
}

      return sb.toString();
}
	
	private String ParseXML(String urlXML) throws IOException
	{
		String strOutput = "";
		try
		{
		    URL url = new URL(urlXML);		
		
			BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
			String inputLine;          
			while ((inputLine = in.readLine()) != null) {
			       strOutput = strOutput + inputLine;
			}
	
			in.close();
		}
		catch (MalformedURLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return strOutput;
	}
	
	private boolean checkGooglePlayServices() {
        final int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (status != ConnectionResult.SUCCESS) {
            
			Log.e("alex", "Google Play Version 1:" + GooglePlayServicesUtil.getErrorString(status));
            return false;
        } else {
           
            Log.e("alex", "Google Play Version 2:" + GooglePlayServicesUtil.getErrorString(status));
            return true;
        }
    }

	protected AdsRequest buildAdsRequest_old()
	{
		DisplayMetrics displaymetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
		int height = displaymetrics.heightPixels;
		int width = displaymetrics.widthPixels;
		//				height = 90;//728, 90
		//				width = 728;
		log("height "+height+" width "+width);
		container = sdkFactory.createAdDisplayContainer();
		container.setPlayer(videoPlayerAd);
		container.setAdContainer(videoPlayerAd.getUiContainer());
		log("Requesting ads");
		AdsRequest request = sdkFactory.createAdsRequest();
		request.setAdTagUrl(defaultTagUrls[0]);
		
		ArrayList<CompanionAdSlot> companionAdSlots = new ArrayList<CompanionAdSlot>();

		CompanionAdSlot companionAdSlot = sdkFactory.createCompanionAdSlot();
		companionAdSlot.setContainer(companionView);
		companionAdSlot.setSize(width, height);
		companionAdSlots.add(companionAdSlot);

		if (leaderboardCompanionView != null)
		{
			CompanionAdSlot leaderboardCompanionAdSlot = sdkFactory.createCompanionAdSlot();
			leaderboardCompanionAdSlot.setContainer(leaderboardCompanionView);
			leaderboardCompanionAdSlot.setSize(width, height);
			companionAdSlots.add(leaderboardCompanionAdSlot);
		}
		container.setCompanionSlots(companionAdSlots);
		request.setAdDisplayContainer(container);
		return request;
	}

	protected void createAdsLoader() 
	{
		if (adsManager != null) 
		{
			adsManager.destroy();
		}
		
		Log.e("alex", "IMA SDK createAdsLoader() ");
		
		adsLoader = sdkFactory.createAdsLoader(this, getImaSdkSettings());
		adsLoader.addAdErrorListener(this);
		adsLoader.addAdsLoadedListener(this);
	}
	
	protected ImaSdkSettings getImaSdkSettings()
	{
		if (sdkSettings == null)
		{
			sdkSettings = sdkFactory.createImaSdkSettings();
			sdkSettings.setLanguage(Locale.getDefault().getLanguage());
		}
		
		Log.e("alex", "IMA SDK getImaSdkSettings() ");
		
		return sdkSettings;
	}

	@Override
	public void onAdError(AdErrorEvent event)
	{
		Log.e("eli", "erreo", event.getError());
		log(event.getError().getMessage() + "\n");
//		stop_ProgreeBar();
//		playVideo() ;
		
//		Log.e("alex", "IMA SDK onAdError() " + event.getError().getMessage() + " Error code: " + event.getError().getErrorCode() + "  Error code number: " + event.getError().getErrorCodeNumber());
		show_progressBar_andPlay();
	}

	@Override
	public void onAdsManagerLoaded(AdsManagerLoadedEvent event)
	{
		log("Ads loaded!");
		adsManager = event.getAdsManager();
		adsManager.addAdErrorListener(this);
		adsManager.addAdEventListener(this);
		log("Calling init.");
			
		Log.e("alex", "Check Adds adsManager.init() Before");
		
		/*
		Set<UiElement> stringSet = new HashSet<UiElement>();
		stringSet.add(UiElement.AD_ATTRIBUTION);
		stringSet.add(UiElement.COUNTDOWN);
		adsSetting.setUiElements(stringSet);
		*/
		
		adsManager.init();
		Log.e("alex", "Check Adds adsManager.init() After");
		
	}

	@Override
	public void onComplete()
	{
		Log.e("eli", "onComplete");//stopProgreeBar
		//		if (videoPlayer.isContentPlaying()) 
		//		{
		adsLoader.contentComplete();
		
		//			finish();
		//		}
	}

	@Override
	public void onAdEvent(AdEvent event)
	{
		log("Event:" + event.getType());

		switch (event.getType())
		{
			case LOADED:
				log("Calling start.");
				
				if (!isUserTouchAd)
				{
					Log.e("alex", "Check Adds adsManager.start() Before");
					adsManager.start();
					Log.e("alex", "Check Adds adsManager.start() After");
				}
				break;
			case CONTENT_PAUSE_REQUESTED:
				//				if (contentStarted) 
				//				{
				//					videoPlayer.pauseContent();
				//				}
				break;
			case CONTENT_RESUME_REQUESTED:
				if (!isUserTouchAd)
					adsManager.destroy();
				break;
			case STARTED:
				stop_ProgreeBar();
				contentStarted = false;
				isAdStarted = true;
				isAdPlaying = true;				
				
				//Ad ad = adsManager.getCurrentAd();				
				//Log.e("alex", "Check Adds isSkippable: " + ad.isSkippable());
								
				break;
			case COMPLETED:
				isAdStarted = false;
				isAdPlaying = false;

				break;
			case ALL_ADS_COMPLETED:

				isAdStarted = false;
				isAdPlaying = false;
				adsManager.destroy();
				show_progressBar_andPlay();
				break;
			case PAUSED:
				isAdPlaying = false;
				break;
			case RESUMED:
				isAdPlaying = true;
				break;
		//	case AD_BREAK_READY:
		//		Log.e("alex", "Add AD_BREAK_READY!!!");
		//		break;
			case SKIPPED:				
				Log.e("alex", "Add Skipped!!!");
				break;
			default:
				break;
		}
	}

	protected void playVideo() 
	{
		//		if (!contentStarted)
		//		{
		Log.e("eli", "playVideo");
		mc.show();
		videoPlayerVideo.setVisibility(View.VISIBLE);
		videoPlayerAd.setVisibility(View.GONE);
		videoPlayerVideo.requestFocus();
		mc.setAnchorView(videoPlayerVideo);
		videoPlayerVideo.setMediaController(mc);
		log("Playing video");
		videoHolder.removeView(videoPlayerAd);
		videoPlayerVideo.start();
		contentStarted = true;
		//		}
	}

	protected void log(String message) 
	{
		Log.e("eli", message);
	}

	public void stop_ProgreeBar()
	{
		Log.e("eli", "stop_ProgreeBar");

		if (pd != null && pd.isShowing())
		{
			pd.dismiss();
		}
	}

	public void show_ProgreeBar()
	{
		Log.e("eli", "show_ProgreeBar");
		if (!isFinishing())
		{
			if (pd == null)
			{
				pd = ProgressDialog.show(this, "", Definitions.Loading, true, true);
			}
			else
			{
				if (!pd.isShowing())
				{
					pd.show();
				}
			}
		}
	}

	@Override
	public void onBackPressed()
	{
		PlayVideoActivity.this.finish();
		//		super.onBackPressed();
	}

	@Override
	protected void onDestroy()
	{
		log( "on destroy play video activity");
		stop_ProgreeBar();
		super.onDestroy();
	}

	public void setVideoPath(final VideoView toShow, String urlP)
	{
		WebView mWebView = new WebView(this);
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.setWebChromeClient(new WebChromeClient());
		mWebView.loadUrl(urlP); 
				
		mWebView.setWebViewClient(new WebViewClient()
		{
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url)
			{
				Log.e("eli", url);
				toShow.setVideoPath(url);
				//				toShow.start();
				return true;
			}
		});  
	}

}
