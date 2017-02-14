package il.co.globes.android.objects;

import il.co.globes.android.Definitions;

import java.util.Locale;

import android.util.Log;

/**
 * 
 * @author Eviatar<br>
 * <br>
 * 
 *         Global URL holder for Map feature
 * 
 */
public class GlobesURL
{
	// main screen
	public static String URLMainScreen = "";

	public static String URLPushNewsPage = "";


	// sections wsurl name="node_articles"
	public static String URLSections = "node_id=";

	// Globes TV
	public static final String TVNode = "2007";

	// need to change from map
	public static final String DOC_PREFIX = "http://www.globes.co.il/apps/library.asmx/DocumentXml?";
	public static final String DOC_RESPONSESS_PREFIX = "http://www.globes.co.il/apps/responses.asmx/get_responses?";

	public static final String TV_PREFIX = "http://www.globes.co.il/news/article.aspx?did=";

	public static String URLClips = "node_id=" + TVNode;

	// document
	public static String URLDocument = "doc_id=XXXX&ied=true&from=app_android";
	public static String URLDocumentJson = "doc_id=XXXX&ied=true&from=app_android";

	// markets
	public static String URLMarkets = "rect=";
	public static String URLMarketsAllGroups = "All.groups";
	public static String URLMarketsIndex = "index.instruments&feeder=XXXX&instrumentid=YYYY";

	// stock
	public static String URLSharePage = "for=mobile&source=XXXX&instrumentID=YYYY";
	// stock graph "graph" on WSURL
	public static String URLShareGraph = "Exchange=EEEE&graph=XXXX&symbol=SSSS&width=WWWW&height=HHHH";
	// forex
	public static String URLForexGraph = "width=WWWW&height=HHHH&symbol=SSSS&feeder=0&days=XXXX";

	// Ticker
	public static String LOOPER_ITEMS_URL = "";
	public static String LOOPER_ITEM_REFRESH_URL = "ids=*<feeder>,<id>&format=withname";

	// financial portal
	public static String URLFinancialPortal = "";

	// left menu stocks with search
	public static String URLStockSearch = "";

	// feeder
	public static String URLFeeder = "#from=Globes_Android_App";

	// sponsor
	public static String URLSponser = "#from=Globes_Android_App";

	// share a document
	public static String URLDocumentToShare = "did=";

	// insturment to share
	public static String URLInstrumentToShare = "instrumentid=";

	// stocks start with
	public static String URLStocksStartWith = "starts_with=<query>";

	// stocks contains
	public static String URLStocksContains = "contains=<query>";

	// article responses
	public static String APILocation_ArticleResponses = "doc_id=XXXX&upper_bound=0&count=YYYY";

	// article responses - add new response
	public static String APIlocation_ArticleResponsesAddNew = "father_res=0&doc_id=XXXX&user_name=ZZZZ&subject=KKKK&text=EEEE&login_id=&password=";

	// search article
	public static String URLSearchArticle = "query=XXXX";

	// portfolio
	public static String URLPortfolio = "Mode=4&PortfolioId=&accessKey=XXXX";

	// *** additional portfolio urls ***

	// check in portfolio known as "check_if_in_portfolio"
	public static String URLCheckIfinPortfolio = "accessKey=<ACCESS_KEY>&amp;InstrumentID=<INSTURMENT_ID>&amp;feeder=<FEEDER>";

	// toggle portfolio item known as "share_activity-toggle_pf_item"
	public static String URLTogglePortfolioItem = "accessKey=<ACCESS_KEY>&amp;portfolioid=<PORTFOLIO_ID>&amp;InstrumentID=<INSTURMENT_ID>&amp;feeder=<FEEDER>&amp;action=<ACTION>";

	// get portfolio ID known as "share_activity-get_portfolio_ID"
	public static String URLGetPortfolioID = "accessKey=<ACCESS_KEY>&amp;mode=4&amp;portfolioid=<PORTFOLIO_ID>";

	
	public static String UrlDocumentJsonOnError = "http://www.globes.co.il/apps/apps_json.asmx/DocumentJson?doc_id=XXXX&ied=true&from=app_android&v=9";;
	
	// news week
	public static String URLGlobesNewsWeek = "";

	// login activity known as "login_activity_sign_in"
	public static String URLloginSignIn = "";

	// Tagit article by name
	public static String URLTagitArticlesByCleanName = "tagit_clean_name=<TAGIT_NAME>";

	// Tagit article by id
	public static String URLTagitArticlesByID = "tagit_id=<TAGIT_ID>";

	// Top50SpokenIdeas
	public static String URLTop50SpokenIdeas = "";

	// DUN'S 100 known as "duns_node_articles"
	public static String URLDunsArticles = "";

	// DUN'S 100 specific article known as "duns_article"
	//public static String URLDunsSingleArticle = "doc_id=<DOC_ID>";
	public static String URLDunsSingleArticle = "";

	// DUN'S 100 company known as "duns_company"
	//public static String URLDunsCompany = "doc_id=<DOC_ID>";
	public static String URLDunsCompany = "";

	// DUN'S 100 manager known as "duns_manager"
	public static String URLDunsManager = "doc_id=<DOC_ID>";
	
	//Walla Adv link
	public static String URLWallaAdvLink = "http://fus.walla.co.il/json/XXXXX/mint.globes_app_android.YYYYY/ZZZZZ?devicemodel=DDDDD&app_version=VVVVV";
	
	public static  NewsSet newsNodes;


	public static void clearURLs()
	{
//		URLMainScreen = "";
//		URLPushNewsPage = "";
//		URLSections = "node_id=";
//		URLClips = "node_id=" + TVNode;
//		URLDocument = "doc_id=XXXX&ied=true&from=app_android";
//		URLMarkets = "rect=";
//		URLMarketsAllGroups = "All.groups";
//		URLMarketsIndex = "index.instruments&feeder=XXXX&instrumentid=YYYY";
//		URLSharePage = "for=mobile&source=XXXX&instrumentID=YYYY";
//		URLShareGraph = "Exchange=EEEE&graph=XXXX&symbol=SSSS&width=WWWW&height=HHHH";
//		URLForexGraph = "width=WWWW&height=HHHH&symbol=SSSS&feeder=0&days=XXXX";
//		LOOPER_ITEMS_URL = "";
//		LOOPER_ITEM_REFRESH_URL = "ids=*<feeder>,<id>&format=withname";
//		URLFinancialPortal = "";
//		URLStockSearch = "";
//		URLFeeder = "#from=Globes_Android_App";
//		URLSponser = "#from=Globes_Android_App";
//		URLDocumentToShare = "did=";
//		URLInstrumentToShare = "instrumentid=";
//		URLStocksStartWith = "starts_with=<query>";
//		URLStocksContains = "contains=<query>";
//		APILocation_ArticleResponses = "doc_id=XXXX&upper_bound=0&count=YYYY";
//		APIlocation_ArticleResponsesAddNew = "father_res=0&doc_id=XXXX&user_name=ZZZZ&subject=KKKK&text=EEEE&login_id=&password=";
//		URLSearchArticle = "query=XXXX";
//		URLPortfolio = "Mode=4&PortfolioId=&accessKey=XXXX";
//		URLCheckIfinPortfolio = "accessKey=<ACCESS_KEY>&amp;InstrumentID=<INSTURMENT_ID>&amp;feeder=<FEEDER>";
//		URLTogglePortfolioItem = "accessKey=<ACCESS_KEY>&amp;portfolioid=<PORTFOLIO_ID>&amp;InstrumentID=<INSTURMENT_ID>&amp;feeder=<FEEDER>&amp;action=<ACTION>";
//		URLGetPortfolioID = "accessKey=<ACCESS_KEY>&amp;mode=4&amp;portfolioid=<PORTFOLIO_ID>";
//		URLGlobesNewsWeek = "";
//		URLloginSignIn = "";
//		URLTagitArticlesByCleanName = "tagit_clean_name=<TAGIT_NAME>";
//		URLTagitArticlesByID = "tagit_id=<TAGIT_ID>";
//		URLTop50SpokenIdeas = "";
//		URLDunsArticles = "";
//		URLDunsSingleArticle = "";
//		URLDunsCompany = "";
//		URLDunsManager = "doc_id=<DOC_ID>";
	}

	// add value to URL
	public static void addValueByName(String name, String value)
	{
		Log.d("addValueByName",name+ " " +value);
//		Log.e("eli", Locale.getDefault().getLanguage());
		if (name.contains("http://") || name.contains("https://"))
		{
			if (value.startsWith("http://") || value.startsWith("https://"))
			{
				return;
			}
		}
		if (name.equals("URLMainScreen"))
			URLMainScreen += value;
		else if (name.equals("share_activity-get_portfolio_ID"))
			URLGetPortfolioID = value + URLGetPortfolioID;
		else if (name.equals("share_activity-toggle_pf_item"))
			URLTogglePortfolioItem = value + URLTogglePortfolioItem;
		else if (name.equals("check_if_in_portfolio"))
			URLCheckIfinPortfolio = value + URLCheckIfinPortfolio;
		else if (name.equals("duns_manager"))
			URLDunsManager = value + URLDunsManager;
		else if (name.equals("duns_company"))
			URLDunsCompany = value + URLDunsCompany;
		else if (name.equals("duns_article"))
			URLDunsSingleArticle = value + URLDunsSingleArticle;
		else if (name.equals("duns_node_articles"))
			URLDunsArticles = value + URLDunsArticles;
		else if (name.equals("Top50SpokenIdeas"))
			URLTop50SpokenIdeas += value;
		else if (name.equals("TagitArticlesByID"))
			URLTagitArticlesByID = value + URLTagitArticlesByID;
		else if (name.equals("TagitArticlesByCleanName"))
			URLTagitArticlesByCleanName = value + URLTagitArticlesByCleanName;
		else if (name.equals("graph"))
			URLShareGraph = value + URLShareGraph;
		else if (name.equals("login_activity_sign_in"))
			URLloginSignIn += value;
		else if (name.equals("node_articles"))
			URLSections = value + URLSections;
		else if (name.equals("PushNewsPage"))
			URLPushNewsPage = value + URLPushNewsPage;
		else if (name.equals("URLClips"))
			URLClips = value + URLClips;
		else if (name.equals("URLDocument"))
			URLDocument = value + URLDocument;
		else if (name.equals("URLMarkets"))
			URLMarkets = value + URLMarkets;
		else if (name.equals("URLSharePage"))
			URLSharePage = value + URLSharePage;
		else if (name.equals("URLForexGraph"))
			URLForexGraph = value + URLForexGraph;
		else if (name.equals("LOOPER_ITEMS_URL"))
			LOOPER_ITEMS_URL += value;
		else if (name.equals("LOOPER_ITEM_REFRESH_URL"))
			LOOPER_ITEM_REFRESH_URL = value + LOOPER_ITEM_REFRESH_URL;
		else if (name.equals("URLFinancialPortal"))
			URLFinancialPortal += value;
		else if (name.equals("URLStockSearch"))
			URLStockSearch += value;
		else if (name.equals("URLFeeder"))
			URLFeeder = value + URLFeeder;
		else if (name.equals("URLSponser"))
			URLSponser = value + URLSponser;
		else if (name.equals("URLSharePage"))
			URLSharePage = value + URLSharePage;
		else if (name.equals("URLDocumentToShare"))
			URLDocumentToShare = value + URLDocumentToShare;
		else if (name.equals("URLInstrumentToShare"))
			URLInstrumentToShare = value + URLInstrumentToShare;
		else if (name.equals("URLStocksStartWith"))
			URLStocksStartWith = value + URLStocksStartWith;
		else if (name.equals("URLStocksContains"))
			URLStocksContains = value + URLStocksContains;
		else if (name.equals("APILocation_ArticleResponses"))
			APILocation_ArticleResponses = value + APILocation_ArticleResponses;
		else if (name.equals("APIlocation_ArticleResponsesAddNew"))
			APIlocation_ArticleResponsesAddNew = value + APIlocation_ArticleResponsesAddNew;
		else if (name.equals("URLSearchArticle"))
			URLSearchArticle = value + URLSearchArticle;
		else if (name.equals("URLPortfolio"))
			URLPortfolio = value + URLPortfolio;
		else if (name.equals("URLGlobesNewsWeek")) URLGlobesNewsWeek += value;


	}


}
