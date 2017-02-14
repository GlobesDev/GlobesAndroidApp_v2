package il.co.globes.android.parsers;

import il.co.globes.android.objects.Group;
import il.co.globes.android.objects.GroupPortfolio;
import il.co.globes.android.objects.NewsSet;
import il.co.globes.android.objects.PortfolioInstrument;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class PortfolioSAXHandler extends DefaultHandler
{
	// ===========================================================
	// Fields
	// ===========================================================

	private boolean in_InstrumentId = false;
	private boolean in_NameHeb = false;
	private boolean in_Last = false;
	private boolean in_percentageChange = false;
	private boolean in_ChangepcntAdd = false;
	private boolean in_Portfolio_name = false;

	private NewsSet newsSet;
	private PortfolioInstrument portfolioInstrument;
	// private Group stockTitle;
	private GroupPortfolio stockTitle;

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

	}

	/* Gets called on opening tags like: <tag> */
	@Override
	public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException
	{

		if (localName.equalsIgnoreCase("error"))
		{
			// Log.d("SAX_HANDLER", localName);
		}
		else if (localName.equalsIgnoreCase("instrument"))
		{
			this.portfolioInstrument = new PortfolioInstrument();
		}
		else if (localName.equalsIgnoreCase("InstrumentId"))
		{
			this.in_InstrumentId = true;
		}
		else if (localName.equalsIgnoreCase("NameHeb"))
		{
			this.in_NameHeb = true;
		}
		else if (localName.equalsIgnoreCase("Last"))
		{
			this.in_Last = true;
		}
		else if (localName.equalsIgnoreCase("percentageChange"))
		{
			this.in_percentageChange = true;
		}
		else if (localName.equalsIgnoreCase("ChangepcntAdd"))
		{
			this.in_ChangepcntAdd = true;
		}
		else if (localName.equalsIgnoreCase("portfolio_name"))
		{
			this.stockTitle = new GroupPortfolio();
			this.in_Portfolio_name = true;
		}
	}

	/* Gets called on the following structure: <tag>characters</tag> */
	@Override
	public void characters(char ch[], int start, int length)
	{

		if (this.in_InstrumentId)
		{
			this.portfolioInstrument.setId(new String(ch, start, length));
			this.portfolioInstrument.setFeeder(/* atts.getValue("feeder") */"0");
		}
		else if (this.in_NameHeb)
		{
			this.portfolioInstrument.setHe(new String(ch, start, length));
		}
		else if (this.in_Last)
		{
			// check if it is currency
			String lastCheck = new String(ch, start, length);
			this.portfolioInstrument.setLast(lastCheck);
			if (checkIsCurrency(lastCheck))
				this.portfolioInstrument.setCurrency(true);
			else this.portfolioInstrument.setCurrency(false);
		}
		else if (this.in_percentageChange)
		{
			this.portfolioInstrument.setPercentage_change(new String(ch, start, length));
		}
		else if (this.in_ChangepcntAdd)
		{
			this.portfolioInstrument.setChangeSinceAdded(new String(ch, start, length));
		}
		else if (this.in_Portfolio_name)
		{

			this.stockTitle.setTitle(new String(ch, start, length));

		}
	}

	// Check if this is a foreign currency
	private boolean checkIsCurrency(String lastCheck)
	{
		int dotIndex = lastCheck.indexOf(".");
		String afterDot = new String(lastCheck.substring(dotIndex + 1, lastCheck.length()));
		if (afterDot.length() > 2) return true;
		return false;
	}

	/* Gets called on closing tags like: * </tag> */
	@Override
	public void endElement(String namespaceURI, String localName, String qName) throws SAXException
	{

		if (localName.equalsIgnoreCase("portfolio_name"))
		{
			this.in_Portfolio_name = false;

			newsSet.addItem(this.stockTitle);
		}
		else if (localName.equalsIgnoreCase("instrument"))
		{
			setInstrument();
		}
		else if (localName.equalsIgnoreCase("InstrumentId"))
		{
			this.in_InstrumentId = false;
		}
		else if (localName.equalsIgnoreCase("NameHeb"))
		{
			this.in_NameHeb = false;
		}
		else if (localName.equalsIgnoreCase("Last"))
		{
			this.in_Last = false;
		}
		else if (localName.equalsIgnoreCase("percentageChange"))
		{
			this.in_percentageChange = false;
		}
		else if (localName.equalsIgnoreCase("ChangepcntAdd"))
		{
			this.in_ChangepcntAdd = false;
		}
	}

	private void setInstrument()
	{
		newsSet.addItem(this.portfolioInstrument);
	}
}
