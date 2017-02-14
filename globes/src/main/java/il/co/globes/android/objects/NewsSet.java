package il.co.globes.android.objects;

import il.co.globes.android.AppRegisterForPushApps;
import il.co.globes.android.DataStore;
import il.co.globes.android.Definitions;
import il.co.globes.android.Utils;
import il.co.globes.android.parsers.ArticleDuns100SAXHandler;
import il.co.globes.android.parsers.Duns100SAXHandler;
import il.co.globes.android.parsers.MainSAXHandler;
import il.co.globes.android.parsers.MarketsSAXHandler;
import il.co.globes.android.parsers.OpinionsSAXHandler;
import il.co.globes.android.parsers.PortfolioSAXHandler;
import il.co.globes.android.parsers.SearchResultsSAXHandler;
import il.co.globes.android.parsers.SectionsMainPageSAXHandler;
import il.co.globes.android.parsers.ShareSAXHandler;
import il.co.globes.android.parsers.SmallOpinionsListSAXHandler;

import java.net.URL;
import java.util.Vector;

import android.util.Log;
import net.tensera.sdk.api.TenseraApi;

public class NewsSet
{
	public Vector<Object> itemHolder = null;

	public NewsSet()
	{
		itemHolder = new Vector<Object>();
	}

	public Vector<Object> getNewsSet()
	{
		return itemHolder;
	}

	public void setNewsSet(Vector<Object> newsSet)
	{
		this.itemHolder = newsSet;
	}

	public void addItem(Object object)
	{
		itemHolder.add(object);
	}

	public static NewsSet parseURL(URL url, String parser) throws Exception
	{
		NewsSet parsedNewsSet;
		Log.e("alex", "NewsSet parseURL: " + parser);
		if (parser.equals(Definitions.SECTIONS))
		{
			SectionsMainPageSAXHandler sectionsHandler = new SectionsMainPageSAXHandler();
			Utils.parseXmlFromUrlUsingHandler(url, sectionsHandler);
			parsedNewsSet = sectionsHandler.getParsedData();
		}
		else if (parser.equals(Definitions.SHARES))
		{
			ShareSAXHandler sharesHandler = new ShareSAXHandler();
			Utils.parseXmlFromUrlUsingHandler(url, sharesHandler);
			parsedNewsSet = sharesHandler.getParsedData();
		}
		else if (parser.equals(Definitions.MARKETS))
		{
			MarketsSAXHandler marketsHandler = new MarketsSAXHandler();
			Utils.parseXmlFromUrlUsingHandler(url, marketsHandler);
			parsedNewsSet = marketsHandler.getParsedData();
		}
		else if (parser.equals(Definitions.SEARCH))
		{
			SearchResultsSAXHandler searchResultsHandler = new SearchResultsSAXHandler();
			Utils.parseXmlFromUrlUsingHandler(url, searchResultsHandler);
			parsedNewsSet = searchResultsHandler.getParsedData();
		}
		else if (parser.equals(Definitions.OPINIONS))
		{
			OpinionsSAXHandler opinionsHandler = new OpinionsSAXHandler();
			Utils.parseXmlFromUrlUsingHandler(url, opinionsHandler);
			parsedNewsSet = opinionsHandler.getParsedData();
		}
		else if (parser.equals(Definitions.PORTFOLIO))
		{
			PortfolioSAXHandler portfolioHandler = new PortfolioSAXHandler();
			Utils.parseXmlFromUrlUsingHandler(url, portfolioHandler);
			parsedNewsSet = portfolioHandler.getParsedData();
		}
		else if (parser.equals(Definitions.LIST_OPINIONS_LEFT_MENU))
		{
			SmallOpinionsListSAXHandler smallOpinionsListSAXHandler = new SmallOpinionsListSAXHandler();
			Utils.parseXmlFromUrlUsingHandler(url, smallOpinionsListSAXHandler);
			parsedNewsSet = smallOpinionsListSAXHandler.getParsedData();
		}
		else if (parser.equals(Definitions.DUNS_100))
		{
			Duns100SAXHandler duns100saxHandler = new Duns100SAXHandler();
			Utils.parseXmlFromUrlUsingHandler(url, duns100saxHandler);
			parsedNewsSet = duns100saxHandler.getParsedData();
		}

//		else if (parser.equals(Definitions.ARTICLE_DUNS_100))
//		{
//			ArticleDuns100SAXHandler duns100saxHandler = new ArticleDuns100SAXHandler();
//			Utils.parseXmlFromUrlUsingHandler(url, duns100saxHandler);
//			parsedNewsSet = duns100saxHandler.getParsedData();
//		}
		else
		{
			if (AppRegisterForPushApps.enableTensera) {
				TenseraApi.transmit();
			}
//			MainSAXHandler mainHandler = new MainSAXHandler();
//			Log.e("alex","Parser2: " + url);
//			Utils.parseXmlFromUrlUsingHandler(url, mainHandler);
//			parsedNewsSet = mainHandler.getParsedData();
						
			//Log.e("alex","Parser2: URLMainScreen: " + url);
			
			if(url.toString().equals(GlobesURL.URLMainScreen)) // first main screen parser
			{
				DataStore dataStore = DataStore.getInstance();
				parsedNewsSet = dataStore.getMainScreenItems();
				Log.e("alex", "MainScreenParserDone!!!!!!!!");
			}
			else
			{
				MainSAXHandler mainHandler = new MainSAXHandler();
				Utils.parseXmlFromUrlUsingHandler(url, mainHandler);
				parsedNewsSet = mainHandler.getParsedData();
				Log.e("alex", "Not MainScreenParserDone!!!!!!!!");
			}
		}
		
		return parsedNewsSet;
	}

}
