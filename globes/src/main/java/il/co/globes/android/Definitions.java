package il.co.globes.android;

import android.os.Build;

public class Definitions
{
	public static boolean toUseGIMA = true;
	public static boolean toUseOutbrain = false;
	public static boolean toUseTaboola = true;
	public static boolean toUseAjillion = true;
	public static boolean toUseLiveBoxMain = true;
	public static boolean wallafusion = false;	
	
	public static String spotImID = "";
	public static boolean spotImEnabled = false;	
	
	public static boolean isSponsoredArticleMainMadorFromGlobes_1 = false;
	public static boolean isSponsoredArticleMainMadorFromGlobes_2 = false;
	public static boolean isSponsoredArticleStoryFromGlobes = false;
	public static boolean isSponsoredArticleNadlanFromGlobes = false;
	
	public static boolean isOpenVideoAsArticle = false;
	
		// Globes Map URL mode , Debug/Production
//	public static final String URLGlobesMap = "http://www.globes.co.il/data/webservices/apps.asmx/Map";
	public static final String URLGlobesMap="http://www.globes.co.il/data/webservices/apps.asmx/MapByMode?";
	public static final String URLGlobesAllWithMap="http://www.globes.co.il/apps/m.asmx/AllWithMap?";
	public static final int MAP_URL_MODE_DEBUG = 0;
	public static final int MAP_URL_MODE_PRODUCTION = 1;
	public static int TIME_MAAVARON = 0;
	public static String DocumentUrlJson = "";
	
	public static String AdsPreRollUrl = "";
	public static String AdsIMAPreRollUrl = "";

	public static int MapURLMode = MAP_URL_MODE_PRODUCTION; //MAP_URL_MODE_DEBUG;
		/* Rich media view time */
	public static final long RICH_MEDIA_VIEW_TIME = 13 * 1000;
	public static final String RICH_MEDIA_AD_UNIT = "/7263/Android.RichMedia";
	/* BB 10 Splash and Banner ad units */
	public final static String ADUNIT_SPLASH_SCREEN_BB10 = "/7263/BB.mavaron";
	public final static String ADUNIT_SPLASH_BETWEEN_ARTICLES_BB10 = "/7263/BB.mavaron.pnimi";
	public final static String ADUNIT_BOTTOM_BANNER_PORTRAIT_BB10 = "/7263/BB.banner";
	public final static String ADUNIT_BOTTOM_BANNER_LANDSCAPE_BB10 = "/7263/Android.article_banner";
	public final static String ADUNIT_OPTIONAL_PORTAL_FINANCY_BB10 = "/7263/BB.Banner.portal";

	/* Splash and Banner ad units */
	// public final static String ADUNIT_SPLASH_SCREEN =
	// "/7263/Android.mavaron";
//	public final static String ADUNIT_SPLASH_SCREEN = "/7263/IPhone.mavaron";
//	public final static String ADUNIT_SPLASH_SCREEN =  "/43010785/globes/hasot.globes"; //"/43010785/globes/Interstitial.globes"; // "/7263/Android.mavaron";
//	public final static String ADUNIT_SPLASH_BETWEEN_ARTICLES = "/43010785/globes/Interstitial.globes"; // "/7263/Android.mavaron.pnimi";
	public final static String ADUNIT_BOTTOM_BANNER_PORTRAIT = "/7263/Android.462_100";
//	public final static String ADUNIT_MAIN_LIST_BETWEEN_ARTICELS_HP = "/43010785/globes/king//slider.globes.hp";
//	public final static String ADUNIT_MAIN_LIST_BETWEEN_ARTICELS = "/43010785/globes/king//slider.globes.ros"; // "/7263/Android.462_100";
//	public final static String ADUNIT_MAIN_LIST_BETWEEN_ARTICELS_VERTICAL_STRIP = "/43010785/globes/strip.globes.ros";
	public final static String ADUNIT_BOTTOM_BANNER_LANDSCAPE = "/7263/Android.article_banner";
	public final static String ADUNIT_OPTIONAL_PORTAL_FINANCY = "/7263/Android.320.50.portal";

//	public final static String ADUNIT_KATAVA_PIRSUMIT_MAIN_LIST = "/43010785/globes/catava_mobile.globes.rashi1";//"/7263/Android.catava_mobile";
//	public final static String ADUNIT_KATAVA_PIRSUMIT_MAIN_LIST_2 = "/43010785/globes/catava_mobile.globes.rashi2";
//	public final static String ADUNIT_KATAVA_PIRSUMIT_NADLAN_LIST = "/43010785/globes/catava_mobile.globes.nadlan"; //"/7263/Android.catava_mobile.nadlan";
//	public static final String ADUNIT_KATAVA_PIRSUMIT_MAIN_LIST_HOT_STORY = "/43010785/globes/catava_mobile.globes.hotstories"; //"/7263/Android.catava_mobile.hot_stories";

	//public static final String ADUNIT_SEKINDO = "/43010785/globes/cube.globes";
	//public static final String ADUNIT_FLOATING_DOCUMENT = "/43010785/globes/floating.globes.ros";

	/*  DFP tags */

	public static String dfp_articles_template_id = "";
	public static String ADUNIT_KATAVA_PIRSUMIT_MAIN_LIST_HOT_STORY = "";
	public static String ADUNIT_KATAVA_PIRSUMIT_NADLAN_LIST = "";
	public static String ADUNIT_KATAVA_PIRSUMIT_MAIN_LIST = "";
	public static String ADUNIT_KATAVA_PIRSUMIT_MAIN_LIST_2 = "";
	public static String dfp_sekindo = "";
	public static String dfp_floating = "";
	public static String dfp_floating_hp = "";
	public static String dfp_interstitial_main = "";
	public static String dfp_interstitial_article = "";
	public static String ADUNIT_MAIN_LIST_BETWEEN_ARTICELS = "";
	public static String ADUNIT_MAIN_LIST_BETWEEN_ARTICELS_HP = "";
	public static String dfp_strip_vertical = "";
	
	/*  Walla Spaces  */
	
	public final static String 	WALLA_SPACE_MAIN_SPLASH = "n_sponsorship";
	public final static String 	WALLA_SPACE_MAAVARON = "n_interstitial";
	public final static String 	WALLA_SPACE_FLOATING_BOTTOM = "n_floating_banner";
	public final static String 	WALLA_SPACE_KATAVA_PIRSUMIT_MAIN = "n_spons_article_main";
	public final static String 	WALLA_SPACE_KATAVA_PIRSUMIT_MAIN_2 = "n_spons_article_main_2";
	public final static String 	WALLA_SPACE_KATAVA_PIRSUMIT_STORY = "n_spons_article_story";
	public final static String 	WALLA_SPACE_KATAVA_PIRSUMIT_REALESTATE = "n_spons_article_realestate";
	public final static String 	WALLA_SPACE_VERTICAL_STRIP = "vertical_strip";
	public final static String 	WALLA_SPACE_ARTICLE_AFTER_RESPONSES = "item_bottom";
	public final static String 	WALLA_SPACE_TOP_FLOAT = "n_slider";
	public final static String 	WALLA_SPACE_KING = "n_king";
	public final static String 	WALLA_SPACE_CATEGORY_STRIP = "category_strip";
	public final static String 	WALLA_SPACE_N_RM1 = "n_rm1";
	public final static String 	WALLA_SPACE_N_RM2 = "n_rm2";
	public final static String 	WALLA_SPACE_DC1 = "dc1";
	public final static String 	WALLA_SPACE_DC2 = "dc2";
	public final static String 	WALLA_SPACE_P1 = "p1";
	public final static String 	WALLA_SPACE_P2 = "p2";
	public final static String 	WALLA_SPACE_INNERSPOT = "innerspot";
	public final static String 	WALLA_SPACE_BOTTOM_PHONE = "bottom_phone";
	public final static String 	WALLA_SPACE_VERTICAL_STRIP_STORY = "vertical_strip_story"; //"n_floating_banner";
	
	/* Add Positions  */
	public static int adKatavaPirsumitMain1AfterArticle = 0;
	public static int adKatavaPirsumitMain2AfterArticle = 0;
	
	public static int adKatavaPirsumitMain1Pos = 0;
	public static int adKatavaPirsumitMain2Pos = 0;
	public static int adKatavaPirsumitHotStoryPos = 0;
	public static int adKatavaPirsumitRealEstatePos = 0;	
	
	
	/* Positive Mobile Ad Links */
	
	public static final String AD_POSITIVEMOBILE_MAIN_SPLASH = "http://track.mobiyield.com/ad/5422/tag.js?pubid=";
	public static final String AD_POSITIVEMOBILE_HOT_STORIES = "http://track.mobiyield.com/ad/5423/tag.js?pubid=";
	public static final String AD_POSITIVEMOBILE_NADLAN = "http://track.mobiyield.com/ad/5421/tag.js?pubid=";

	/* Ad failure TAGS Android each screen prefix is added respectively */
	//public final static String AD_FAILURE_TAG_SPLASH = "/" + Definitions.ADUNIT_SPLASH_SCREEN + "/";
	//public final static String AD_FAILURE_TAG_SPLASH_BETWEEN_ARTICLES = "/" + Definitions.ADUNIT_SPLASH_BETWEEN_ARTICLES + "/";
	public final static String AD_FAILURE_TAG_BOTTOM_BANNER_PORTRAIT = "/" + Definitions.ADUNIT_BOTTOM_BANNER_PORTRAIT + "/";
	public final static String AD_FAILURE_TAG_BOTTOM_BANNER_LANDSCAPE = "/" + Definitions.ADUNIT_BOTTOM_BANNER_LANDSCAPE + "/";
	public final static String AD_FAILURE_TAG_OPTIONAL_PORTAL_FINANCY = "/" + Definitions.ADUNIT_OPTIONAL_PORTAL_FINANCY + "/";
    
	public final static boolean show_dfp_banners_on_splash_screen = true;
	/** FaceBook measurements */
	public static final String FACEBOOK_APP_ID = "159652850757022";

	/** Notification title */
	public static final String NOTIFICATION_TITLE = "גלובס";

	/**
	 * set to true when mainTabActivity is loading to indicate the scheme the
	 * app is alive
	 */
	public static boolean appActive = false;

	/** article splash display time */
	public static final long ARTICLE_SPLASH_DISPLAY_LENGTH = 6000;

	/** URL Scheme Intent Extra definitions */
	public static final String KEY_BUNDLE_SCHEME_URI_SHARE = "schemeURIShare";
	public static final String SCHEME_DOC_ID = "schemeDocID";
	public static final String SCHEME_COMING_FROM_SCHEME = "schemeComingFromScheme";
	public static final String KEY_BUNDLE_PUSH_NOTIFICATIONS_DOC_ID = "pushNotificationDocID";

	public static final int MarketingContentFolder = 4049;
	
	public static final int Sekindo_Unique_Space_Identifier = 58866; //51694;
	public static final String Exit_Splash_Preference = "Exit_Splash_Preference";
	public static final int Exit_Splash_Max_Show_Count = 3;
	
	/**
	 * set to true when coming from notification to signal the parseDocFromXml
	 * in order to track with google analytics
	 * 
	 */
	private static boolean isComingFromPush = false;
	public static synchronized boolean isComingFromPush()
	{
		return isComingFromPush;
	}

	public static synchronized void setComingFromPush(boolean isComingFromPush)
	{
		Definitions.isComingFromPush = isComingFromPush;
	}

	/** Google analytics Tracking URL for URLMainScreen */
	public static final String URLprefix = "/android.app/"; // "/android.app/"
															// // "/BB10.app/"
	public static final String URLMainScreenTracking = URLprefix + "Main/m.asmx/All";
	public static final String SHAREsuffix = "android.app";// "android.app" //
															// "BB10.app"

	/** AppsFlyer Dev */
	public static final String APPSFLYER_DEV = "gPjUAKEjYkmB8re8yWgzPM";
	/**
	 * flipAlignment - align text mediaPlayerInBrowser - play videos in browser
	 * or device checkBoxNotifications - get notifications or don't
	 */
	public static boolean flipAlignment = true;
	public static boolean mediaPlayerInBrowser = false;
	public static boolean checkBoxNotfifications = true;
	public static boolean PUSH_WAS_HANDLE = true;

	public static boolean MainSlidingActivityActive;
	public static String pushDid;

	public static final String MY_DEBUG_TAG = "Globes";
	public static final String RED_ALERT_MAIL_TARGET = "redemail@globes.co.il";

	public static final int RED_EMAIL_DIALOG = 5;
	public static final int SEND_CLIP_DIALOG = 6;
	public static final int SEND_PIC_DIALOG = 7;
	public static final int CHOOSE_PIC_DIALOG = 8;
	public static final int REQUEST_CLIP_CAPTURE = 1011;

	// ********* Callers ******//
	public static final String MAINTABACTVITY = "MainTabActivity";
	public static final String URI_TO_PARSE = "URI";
	public static final String CALLER = "caller";
	public static final String PARSER = "parser";
	public static final String SECTIONS = "Sections";
	public static final String MAINSCREEN = "MainScreen";
	public static final String SHARES = "Shares";
	public static final String MARKETS = "Markets";
	public static final String SEARCH = "Search";
	public static final String HEADER = "Header";
	public static final String OPINIONS = "Opinions";
	public static final String ISINSTRUMENT = "isInstrument";
	public static final String PORTFOLIO = "portfolio";
	public static final String LIST_OPINIONS_LEFT_MENU = "listOpinions";
	public static final String DUNS_100 = "duns100";

	public static final String ARTICLE_DUNS_100 = "articleduns100";
	public static final String COMPANY_DUNS_100 = "companyDuns100";

	public static final int ConnectTimeout = 10000; // 4000
	public static final int readTimeout = 10000; // 6000

	public static final int CLIP_ATTACHMENT = 0;
	public static final int PIC_ATTACHMENT = 1;
	public static final int CHOOSE_ATTACHMENT = 0;
	public static final int MAKE_ATTACHMENT = 1;

	// ********* Strings ******//
	public static final String Loading = "טוען...";
	public static final String ErrorLoading = "...טעינה נכשלה, אנא בדוק חיבור לאינטרנט";
	public static final String Share = "שתף כתבה מגלובס ";
	public static final String ShareClip = "שיתוף כתבת וידיאו";
	public static final String watchArticle = "לינק לקריאת הכתבה";
	public static final String watchShare = "לינק לצפייה בנתוני המנייה";
	public static final String watchVideo = "לינק לצפייה בסרטון";
	public static final String ShareString = " נשלח מאפליקציית אנדרואיד של";// " נשלח מאפליקציית אנדרואיד של";
																			// //
																			// "נשלח מאפליקציית בלקברי של"
	public static final String shareGlobesWithQuates = "\"גלובס\"";
	public static final String SendingResponseError = "קרתה בעיה בעת שליחת ההודעה, אנא נסה שוב";
	public static final String SendingResponseSuccess = "תגובתך התקבלה";
	public static final String MissingItemsInResponse = "יש למלא כינוי ונושא לפני משלוח תגובה";
	public static final String shareCellAppsPart1 = " להורדה וצפייה במגוון אפליקציות";
	public static final String shareCellAppsPart2 = " http://www.globes.co.il/news/cellular";
	public static final String fromPlatformAppAnalytics = "#from=" + SHAREsuffix; //

	// ********* Dialogs ******//
	public static final int DIALOGADDRESPONSE = 0;
	public static final int DIALOGOPENRESPONSETEXT = 1;

	// ********* Gemius ******//
	public static final String GEMIUS_SERVER_PREFIX = "pro"; // "http://gail.hit.gemius.pl/redot.gif";
	public static final String GEMIUS_ID = "baowqw7Gk0GDGkHeb6SokJSsze.6ehuIbwfwzH7rZ3L.g7";

	// **** Push ****
	public static final String GCM_SENDER_ID = "273040325684";

	public static final String LAUNCH_APP = "il.co.globes.android.LAUNCH_APP";
	public static final String URL_REGISTER_OR_UPDATE = "http://www.quickode.com/Apps/basics/Registration/RegisterOrUpdate";
	public static final String URL_UNREGISTER_FOR_NOTIFICATIONS = "http://www.quickode.com/Apps/basics/Registration/UnregisterForNotifications";
	
	public static final String SPECIAL_FOLDER_FOR_PUSH = "הסיפורים הגדולים של היום";

	/** constant caller name to let us know we come from webview inside document */
	public static final String WEBVIEW = "webview";

	public static final String VIDEO_DP = "&MobileStreamType=dp";

	public static final String AJILLION_AD_ID_SPLASH_SCREEN = "965";
	public static final String AJILLION_AD_ID_BTWEEN_ARTICLE = "967";
	public static final String AJILLION_AD_ID_HEAD_BANNER = "963";
	public static final String AJILLION_AD_ID_INNER_BANNER = "2873";
	 
	public static final String MANDATORY_UPDATE_KEY = "globes_last_mandatory_update_Shown";
	public static final String MANDATORY_UPDATE_MESSAGE = "על מנת להשתמש באפליקציה יש לעדכן לגירסה החדשה";
	public static final long MANDATORY_UPDATE_INTERVAL = 5;
	
	public static final String WALLA_COOKIE_NAME = "Fusion.testCookie";
	public static final String [] WallaSpaces = {"n_slider", "n_king", "n_floating_banner", "n_sponsorship", "n_interstitial", "exit_spons", "n_spons_article", "vertical_strip",
            "category_strip", "sub_category_strip", "n_rm1", "n_rm2", "n_rm3", "n_rm4", "item_bottom", "innerspot", "bottom_phone", "kidum_strip", "n_spons_article_main", "n_spons_article_main_2", "n_spons_article_story", "n_spons_article_realestate", "dc1", "dc2", "vertical_strip_story"};

	public static final String []  WallaAttributes = {"ADID", "ADSERVER", "ADSRV", "ADTYPE", "ADURL_01", "BGCOLOR", "CLICKREPORT", "CLICKURL", "CONTENTTYPE", "DELAYTIME", "EXTERNALBROWSER", 
            "HEIGHT_01", "Payload", "TRANSPARENT", "VIEWREPORT", "WIDTH_01", "ADTITLE"};

	public static String DEVICEMODEL = Build.MODEL.toUpperCase(); //Build.MANUFACTURER.toUpperCase()
	
	// ********* Enums ********//
	public enum ArticleType
	{
		TEXT_ARTICLE(0), VIDEO_ARTICLE(1);

		// private static final Map<Integer,Status> lookup
		// = new HashMap<Integer,Status>();
		//
		// static {
		// for(Status s : EnumSet.allOf(Status.class))
		// lookup.put(s.getCode(), s);
		// }

		private int articleType;

		private ArticleType(int articleType)
		{
//			Log.e("eli", "articleType "+articleType);
			this.articleType = articleType;
		}

		public int getType()
		{
			return articleType;
		}

		// public static Status get(int code) {
		// return lookup.get(code);
		// }
	}

}
