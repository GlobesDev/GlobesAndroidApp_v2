package il.co.globes.android.parsers;

import il.co.globes.android.DataStore;
import il.co.globes.android.Definitions;
import il.co.globes.android.objects.GlobesURL;
import il.co.globes.android.objects.NewCustomMenuItem;
import il.co.globes.android.objects.NewsSet;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

public class GlobesMapSAXHandler extends DefaultHandler
{
	// ===========================================================
	// Fields
	// ===========================================================

	// indicators
	boolean in_debug_group = false;
	boolean in_production_group = false;
	boolean in_wsurl = false;

	// data
	private HashMap<String, String> mapProductionURLs;
	private HashMap<String, String> mapDebugURLs;
	private NewsSet newsNodesDebug, newsNodesProduction;
	private DataStore dataStore;
	@Override
	public void startDocument() throws SAXException
	{
		super.startDocument();
		this.mapProductionURLs = new HashMap<String, String>();
		this.mapDebugURLs = new HashMap<String, String>();
		this.newsNodesDebug = new NewsSet();
		this.newsNodesProduction = new NewsSet();

		dataStore = DataStore.getInstance();

	}

	@Override
	public void endDocument() throws SAXException
	{
		super.endDocument();

		// iterate over map and add values to GlobesURL accrording to the
		// current mode Debug/Production
		Iterator<Entry<String, String>> it;
		if (Definitions.MapURLMode == Definitions.MAP_URL_MODE_DEBUG)
		{
			it = this.mapDebugURLs.entrySet().iterator();
		}
		else
		{
			it = this.mapProductionURLs.entrySet().iterator();
		}
		while (it.hasNext())
		{
			Map.Entry<String, String> pairs = it.next();
			GlobesURL.addValueByName(pairs.getKey(), pairs.getValue());
		}

		if (Definitions.MapURLMode == Definitions.MAP_URL_MODE_DEBUG)
		{

			dataStore.setDynamicMenuItemsHolderDebugMap(this.newsNodesDebug);
		}
		else
		{
			dataStore.setDynamicMenuItemsHolderProductionMap(this.newsNodesProduction);

		}

	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
	{
		//Log.e("alex", "localName: " + localName);
		
		if (localName.equals("group") && !TextUtils.isEmpty(attributes.getValue("name"))
				&& (attributes.getValue("name")).equals("wsurl_debug"))
		{
			in_debug_group = true;
			//Log.e("alex","Android Version ID: " + attributes.getValue("android"));
			dataStore.setAppVersion(attributes.getValue("android"));
		}
		else if (localName.equals("group") && !TextUtils.isEmpty(attributes.getValue("name"))
				&& (attributes.getValue("name")).equals("wsurl_prod"))
		{
			in_production_group = true;
			//Log.e("alex","Android Version ID: " + attributes.getValue("android"));
			dataStore.setAppVersion(attributes.getValue("android"));

		}
		else if (localName.equals("wsurl"))
		{
			in_wsurl = true;
			if (!attributes.getValue("name").equals("financeLinks") || !attributes.getValue("name").equals("newsNodes"))
			{
				// add the attrs to the map of URLs
				addWSURLtagToMap(attributes);
			}
			if (attributes.getValue("name").equals("AdsPreRoll"))
			{
				// TODO eli add the toUseGIMA
				String link = attributes.getValue("link");
				
				Definitions.toUseGIMA = link.compareTo("true") == 0;
				// if (Build.VERSION.SDK_INT == 16)
				// {
				// Definitions.toUseGIMA = false;
				// }
			}

			
			
			
			if (attributes.getValue("name").equals("MaavaronWaitingTime"))
			{
				String link = attributes.getValue("link");
				Definitions.TIME_MAAVARON = Integer.valueOf(link);
			}
			
			if (attributes.getValue("name").equals("pm_livebox_HP"))
			{
				String link = attributes.getValue("link");
				Definitions.toUseLiveBoxMain = link.compareTo("true") == 0;
			}

			if (attributes.getValue("name").equals("outbrain"))
			{
				// TODO eli add the toUseOutbrain
				String link = attributes.getValue("link");
				Definitions.toUseOutbrain = false;

				// Definitions.toUseOutbrain = link.compareTo("true")==0;
			}
			if (attributes.getValue("name").equals("AdsMode"))
			{
				// TODO eli add the toUseAjillion
				String link = attributes.getValue("link");
				Log.e("eli", "map : name=" + attributes.getValue("name") + ", link=" + link);
				if (link.compareTo("1") == 0)
				{
					Definitions.toUseAjillion = false;
				}
				else
				{
					Definitions.toUseAjillion = true;
				}
			}
		}
		
		else if (localName.equals("link"))
		{
			NewCustomMenuItem customMenuItem = new NewCustomMenuItem();
			if (attributes.getValue("name") != null) customMenuItem.setName(attributes.getValue("name").replaceAll("null", "").trim());
			if (attributes.getValue("href") != null) customMenuItem.setHref(attributes.getValue("href").replaceAll("null", "").trim());
			customMenuItem.setFinancialLinkItem(true);

			if (in_debug_group)
			{

				this.newsNodesDebug.addItem(customMenuItem);
			}
			else if (in_production_group)
			{
				this.newsNodesProduction.addItem(customMenuItem);
			}

		}

		else if (localName.equals("node"))
		{
			NewCustomMenuItem customMenuItem = new NewCustomMenuItem();

			if (attributes.getValue("name") != null) customMenuItem.setName(attributes.getValue("name").replaceAll("null", "").trim());
			if (attributes.getValue("id") != null) customMenuItem.setId(attributes.getValue("id").replaceAll("null", "").trim());

			if (in_debug_group)
			{

				this.newsNodesDebug.addItem(customMenuItem);
			}
			else if (in_production_group)
			{
				this.newsNodesProduction.addItem(customMenuItem);
			}

		}
		else if (localName.equals("nodeWeb"))
		{

			NewCustomMenuItem customMenuItem = new NewCustomMenuItem();
			if (attributes.getValue("name") != null) customMenuItem.setName(attributes.getValue("name").replaceAll("null", "").trim());
			if (attributes.getValue("href") != null) customMenuItem.setHref(attributes.getValue("href").replaceAll("null", "").trim());
			if (attributes.getValue("image") != null) customMenuItem.setImgURL(attributes.getValue("image").replaceAll("null", "").trim());
			customMenuItem.setIsNodeWebItem(true);

			if (in_debug_group)
			{

				this.newsNodesDebug.addItem(customMenuItem);
			}
			else if (in_production_group)
			{
				this.newsNodesProduction.addItem(customMenuItem);
			}

		}

	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException
	{
		if (localName.equals("group") && in_debug_group)
		{
			in_debug_group = false;
		}
		else if (localName.equals("group") && in_production_group)
		{
			in_production_group = false;
		}
		else if (localName.equals("wsurl"))
		{
			in_wsurl = false;
		}
	}

	/**
	 * Add key = "name" value = "link" to map
	 * 
	 * @param attributes
	 *            {@link Attributes}
	 */
	private void addWSURLtagToMap(Attributes attributes)
	{
		if (Definitions.MapURLMode == Definitions.MAP_URL_MODE_DEBUG)
		{
			this.mapDebugURLs.put(attributes.getValue("name"), attributes.getValue("link"));

		}
		else
		{
			this.mapProductionURLs.put(attributes.getValue("name"), attributes.getValue("link"));
		}
	}

}
