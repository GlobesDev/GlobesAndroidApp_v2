package il.co.globes.android.parsers;

import il.co.globes.android.objects.Group;
import il.co.globes.android.objects.HeaderForex;
import il.co.globes.android.objects.HeaderFutures;
import il.co.globes.android.objects.HeaderInstruments;
import il.co.globes.android.objects.HeaderShares;
import il.co.globes.android.objects.Instrument;
import il.co.globes.android.objects.NewsSet;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class MarketsSAXHandler extends DefaultHandler
{

	// ===========================================================
	// Fields
	// ===========================================================

	private NewsSet newsSet;

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public NewsSet getParsedData()
	{
		return this.newsSet;
	}

	// ===========================================================
	// Methods
	// ===========================================================
	@Override
	public void startDocument() throws SAXException
	{
		this.newsSet = new NewsSet();
	}

	@Override
	public void endDocument() throws SAXException
	{
		for (int i = 0; i < newsSet.itemHolder.size(); i++)
		{
			if (newsSet.itemHolder.elementAt(i).getClass() == Group.class)
			{
				if (((Group) newsSet.itemHolder.elementAt(i)).getTitle().contains("îðéåú"))
				{
					newsSet.itemHolder.add(++i, new HeaderShares(""));
				}
				else if (((Group) newsSet.itemHolder.elementAt(i)).getTitle().contains("ñçåøåú"))
				{
					newsSet.itemHolder.add(++i, new HeaderFutures(""));
				}
				else if (((Group) newsSet.itemHolder.elementAt(i)).getTitle().contains("îè\"ç"))
				{
					newsSet.itemHolder.add(++i, new HeaderForex(""));
				}
				else
				{
					newsSet.itemHolder.add(++i, new HeaderInstruments(""));
				}

			}
		}

	}

	/**
	 * Gets be called on opening tags like: <tag> Can provide attribute(s), when
	 * xml was like: <tag attribute="attributeValue">
	 */
	@Override
	public void startElement(String namespaceURI, String localName,
			String qName, Attributes atts) throws SAXException 
			{

		if (localName.equals("group"))
		{

			if (atts.getValue("title")!=null)
			{
				Group group = new Group();
				group.setTitle(atts.getValue("title"));
				newsSet.addItem(group);
			}
		}
		else if (localName.equals("instrument")) 
		{
			Instrument instrument = new Instrument();
			instrument.setFeeder(atts.getValue("feeder"));
			instrument.setId(atts.getValue("id"));
			instrument.setHe(atts.getValue("he"));
			String lastCheck = atts.getValue("last");
			//check if it is currency
			if (checkIsCurrency(lastCheck))
				instrument.setCurrency(true);
			else
				instrument.setCurrency(false);
			instrument.setLast(atts.getValue("last"));
			instrument.setPercentage_change(atts.getValue("percentage_change"));
			if (atts.getValue("is_index_with_istruments")!=null) instrument.setIndexWithInstruments();
			newsSet.addItem(instrument);
		}

			}
	
	//Checks the length of last to set boolean saying it is a currency and should maintain 4 digits passed dot
	//WS : http://www.globes.co.il/data/webservices/litefinance.ashx?rect=All.groups
	//i.e <group title="מט"ח">
	// <instrument feeder="0" id="4186" he="דולר יציג" last="3.6954" percentage_change="-0.752"/>
	private boolean checkIsCurrency(String lastCheck)
	{
		int dotIndex = lastCheck.indexOf(".");
		String afterDot = new String (lastCheck.substring(dotIndex+1, lastCheck.length()));
		if (afterDot.length() > 2)
			return true; 
		return false;
	}

	/**
	 * Gets be called on closing tags like: </tag>
	 */
	@Override
	public void endElement(String namespaceURI, String localName, String qName) throws SAXException
	{

	}

	/**
	 * Gets be called on the following structure: <tag>characters</tag>
	 */
	@Override
	public void characters(char ch[], int start, int length)
	{

	}
}
