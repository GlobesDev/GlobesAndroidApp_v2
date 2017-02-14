package il.co.globes.android.parsers;

import java.util.Vector;

import il.co.globes.android.objects.Group;
import il.co.globes.android.objects.NewsSet;
import il.co.globes.android.objects.ShareObjects.ShareAnalystRecommendation;
import il.co.globes.android.objects.ShareObjects.ShareCompanyDescription;
import il.co.globes.android.objects.ShareObjects.ShareData;
import il.co.globes.android.objects.ShareObjects.ShareNewsArticle;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.util.Log;

public class ShareSAXHandler extends DefaultHandler
{

	// ===========================================================
	// Fields
	// ===========================================================

	boolean in_symbol = false;
	boolean in_nameHe = false;
	boolean in_timestamp = false;
	boolean in_lastPrice = false;
	boolean in_dailyPercentageChange = false;
	boolean in_dailyPointsChange = false;
	boolean in_shareVolume = false;
	boolean in_dailyHigh = false;
	boolean in_dailyLow = false;
	boolean in_shareMarketCap = false;
	boolean in_companyDescription = false;
	boolean in_shareType = false;
	boolean in_exchangeInitials = false;
	boolean in_instrumentID = false;
	boolean in_ChangeFromLastMonth = false;
	boolean in_ChangeFromLastYear = false;
	boolean in_openPrice = false;
	boolean in_LastWeekClosePrice = false;
	boolean in_totVol = false;

	private NewsSet sharePageData;
	private ShareData shareData;
	private ShareCompanyDescription shareCompanyDescription;
	private Vector<Object> tempVectorForShareRecommendations;
	private Vector<Object> tempVectorForShareNewsArticles;

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public NewsSet getParsedData()
	{
		return this.sharePageData;
	}

	// ===========================================================
	// Methods
	// ===========================================================
	@Override
	public void startDocument() throws SAXException
	{
		this.sharePageData = new NewsSet();
		shareData = new ShareData();
		shareCompanyDescription = new ShareCompanyDescription();
		tempVectorForShareNewsArticles = new Vector<Object>();
		tempVectorForShareRecommendations = new Vector<Object>();
	}

	@Override
	public void endDocument() throws SAXException
	{
		Group lastNewsTitle = new Group();
		Group recommendationTitle = new Group();
		Group aboutCompany = new Group();
		lastNewsTitle.setTitle("חדשות אחרונות");
		recommendationTitle.setTitle("המלצות אנליסטים");
		aboutCompany.setTitle("על החברה");

		sharePageData.itemHolder.add(0, shareData);

		if (!tempVectorForShareNewsArticles.isEmpty())
		{
			sharePageData.itemHolder.add(lastNewsTitle);
			if (tempVectorForShareNewsArticles.size() >= 5)
			{
				tempVectorForShareNewsArticles.setSize(5);
				tempVectorForShareNewsArticles.trimToSize();
			}
			sharePageData.itemHolder.addAll(tempVectorForShareNewsArticles);
		}
		if (!tempVectorForShareRecommendations.isEmpty())
		{
			if (tempVectorForShareRecommendations.size() >= 5)
			{
				tempVectorForShareRecommendations.setSize(5);
				tempVectorForShareRecommendations.trimToSize();
			}
			sharePageData.itemHolder.add(recommendationTitle);
			sharePageData.itemHolder.addAll(tempVectorForShareRecommendations);
		}
		if (shareCompanyDescription.getCompanyDescription() != null)
		{
			sharePageData.itemHolder.add(aboutCompany);
			sharePageData.itemHolder.add(shareCompanyDescription);
		}

	}

	/**
	 * Gets be called on opening tags like: <tag> Can provide attribute(s), when
	 * xml was like: <tag attribute="attributeValue">
	 */
	@Override
	public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException
	{		
		if (localName.equals("symbol"))
		{
			in_symbol = true;
			
		}
		else if (localName.equals("name_he"))
		{ // TODO test for getting share name
			in_nameHe = true;

		}
		else if (localName.equals("Instrument"))
		{
			shareData.setFeeder(atts.getValue("feeder"));
		}
		else if (localName.equals("timestamp"))
		{
			in_timestamp = true;
		}
		else if (localName.equals("instrumentId"))
		{
			in_instrumentID = true;
		}
		else if (localName.equals("last"))
		{
			in_lastPrice = true;
		}
		else if (localName.equals("percentageChange"))
		{
			in_dailyPercentageChange = true;
		}
		else if (localName.equals("change"))
		{
			in_dailyPointsChange = true;
		}
		else if (localName.equals("totVolMoney"))
		{
			in_shareVolume = true;
		}
		else if (localName.equals("high"))
		{
			in_dailyHigh = true;
		}
		else if (localName.equals("low"))
		{
			in_dailyLow = true;
		}
		else if (localName.equals("ShareMarketCap"))
		{
			in_shareMarketCap = true;
		}
		else if (localName.equals("recomendation")) /*
													 * yes, there's a type-O in
													 * the XML
													 */
		{
			ShareAnalystRecommendation analystRecommendation = new ShareAnalystRecommendation();
			analystRecommendation.setDate(atts.getValue("date"));
			analystRecommendation.setAnalystName(atts.getValue("analyst"));
			analystRecommendation.setRank(atts.getValue("rank"));
			tempVectorForShareRecommendations.add(analystRecommendation);
		}
		else if (localName.equals("article"))
		{
			ShareNewsArticle newsArticle = new ShareNewsArticle();
			newsArticle.setCreatedOn(atts.getValue("created_on"));
			newsArticle.setDocId(atts.getValue("doc_id"));
			newsArticle.setTitle(atts.getValue("title"));
			tempVectorForShareNewsArticles.add(newsArticle);
		}
		else if (localName.equals("CompanyDescription"))
		{
			in_companyDescription = true;
		}
		else if (localName.equals("type"))
		{
			in_shareType = true;
		}
		else if (localName.equals("exchange"))
		{
			in_exchangeInitials = true;
		}
		else if (localName.equals("openPrice"))
		{
			in_openPrice = true;

		}
		else if (localName.equals("ChangeFromLastMonth"))
		{
			in_ChangeFromLastMonth = true;

		}
		else if (localName.equals("ChangeFromLastYear"))
		{
			in_ChangeFromLastYear = true;

		}
		else if (localName.equals("LastWeekClosePrice"))
		{
			in_LastWeekClosePrice = true;
		}
		else if (localName.equals("totVol"))
		{
			in_totVol = true;
		}

	}
	/**
	 * Gets be called on closing tags like: </tag>
	 */
	@Override
	public void endElement(String namespaceURI, String localName, String qName) throws SAXException
	{
		if (localName.equals("symbol"))
		{
			in_symbol = false;
		}
		else if (localName.equals("instrumentId"))
		{
			in_instrumentID = false;
		}
		else if (localName.equals("name_he"))
		{
			in_nameHe = false;
		}
		else if (localName.equals("timestamp"))
		{
			in_timestamp = false;
		}
		else if (localName.equals("last"))
		{
			in_lastPrice = false;
		}
		else if (localName.equals("percentageChange"))
		{
			in_dailyPercentageChange = false;
		}
		else if (localName.equals("change"))
		{
			in_dailyPointsChange = false;
		}
		else if (localName.equals("totVolMoney"))
		{
			in_shareVolume = false;
		}
		else if (localName.equals("high"))
		{
			in_dailyHigh = false;
		}
		else if (localName.equals("low"))
		{
			in_dailyLow = false;
		}
		else if (localName.equals("ShareMarketCap"))
		{
			in_shareMarketCap = false;
		}
		else if (localName.equals("CompanyDescription"))
		{
			in_companyDescription = false;
		}
		else if (localName.equals("type"))
		{
			in_shareType = false;
		}
		else if (localName.equals("exchange"))
		{
			in_exchangeInitials = false;
		}
		else if (localName.equals("openPrice"))
		{
			in_openPrice = false;

		}
		else if (localName.equals("ChangeFromLastMonth"))
		{
			in_ChangeFromLastMonth = false;

		}
		else if (localName.equals("ChangeFromLastYear"))
		{
			in_ChangeFromLastYear = false;

		}
		else if (localName.equals("LastWeekClosePrice"))
		{
			in_LastWeekClosePrice = false;
		}
		else if (localName.equals("totVol"))
		{
			in_totVol = false;
		}
	}

	/**
	 * Gets be called on the following structure: <tag>characters</tag>
	 */
	@Override
	public void characters(char ch[], int start, int length)
	{
		if (this.in_symbol)
		{
			shareData.setShareSymbol(new String(ch, start, length));
		}
		else if (this.in_nameHe)
		{
			shareData.setShareNameHe(new String(ch, start, length));
		}
		else if (this.in_timestamp)
		{
			shareData.setTimestamp(new String(ch, start, length));
		}
		else if (this.in_instrumentID)
		{
			shareData.setInsturmentID(new String(ch, start, length));
		}
		else if (this.in_lastPrice)
		{
			shareData.setLastPrice(new String(ch, start, length));
		}
		else if (this.in_dailyPercentageChange)
		{
			shareData.setDailyPercentageChange(new String(ch, start, length));
		}
		else if (this.in_dailyPointsChange)
		{
			shareData.setDailyPointsChange(new String(ch, start, length));
		}
		else if (this.in_shareVolume)
		{
			shareData.setShareVolume(new String(ch, start, length));
		}
		else if (this.in_dailyHigh)
		{
			shareData.setDailyHigh(new String(ch, start, length));
		}
		else if (this.in_dailyLow)
		{
			shareData.setDailyLow(new String(ch, start, length));
		}
		else if (this.in_shareMarketCap)
		{
			shareData.setShareMarketCap(new String(ch, start, length));
		}
		else if (this.in_companyDescription)
		{
			shareCompanyDescription.setCompanyDescription(new String(ch, start, length));
		}
		else if (this.in_shareType)
		{
			shareData.setShareType(new String(ch, start, length));
		}
		else if (this.in_exchangeInitials)
		{
			shareData.setExchangeInitials(new String(ch, start, length));
		}
		else if (this.in_openPrice)
		{
			shareData.setOpenPrice(new String(ch, start, length));
		}
		else if (this.in_ChangeFromLastMonth)
		{
			shareData.setChangeFromLastMonth(new String(ch, start, length));
		}

		else if (this.in_ChangeFromLastYear)
		{
			shareData.setChangeFromLastYear(new String(ch, start, length));
		}
		else if (this.in_LastWeekClosePrice)
		{
			shareData.setLastWeekClosePrice(new String(ch, start, length));
		}
		else if (this.in_totVol)
		{
			shareData.settotVol(new String(ch, start, length));
		}

	}
}
