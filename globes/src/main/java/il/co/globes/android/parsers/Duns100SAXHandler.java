package il.co.globes.android.parsers;

import java.util.ArrayList;
import java.util.List;

import il.co.globes.android.objects.RowArticleDuns100;
import il.co.globes.android.objects.ArticleSmallOpinion;
import il.co.globes.android.objects.CompanyDuns100;
import il.co.globes.android.objects.NewsSet;
import il.co.globes.android.objects.RowCompanyDuns100;
import il.co.globes.android.objects.Tagit;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * 
 * @author Eviatar<br>
 * <br>
 * 
 *         Duns 100 sax parser
 * 
 */
public class Duns100SAXHandler extends DefaultHandler
{
	// data
	private NewsSet newsSet;

	// ===========================================================
	// Fields
	// ===========================================================

	// article
	private RowArticleDuns100 curArticle;

	private RowCompanyDuns100 curCompany;

	// tagiot
	private List<Tagit> curTagiot;

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public NewsSet getParsedData()
	{
		return this.newsSet;
	}

	@Override
	public void startDocument() throws SAXException
	{
		super.startDocument();
		this.newsSet = new NewsSet();
	}

	@Override
	public void endDocument() throws SAXException
	{
		// TODO Auto-generated method stub
		super.endDocument();
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException
	{
		if (localName.equals("article"))
		{
			curArticle = new RowArticleDuns100();
			curTagiot = new ArrayList<Tagit>();

			// add values
			if (atts.getValue("doc_id") != null) curArticle.setDoc_id(atts.getValue("doc_id").replaceAll("null", "").trim());
			if (atts.getValue("title") != null) curArticle.setTitle(atts.getValue("title").replaceAll("null", "").trim());
			if (atts.getValue("created_on") != null) curArticle.setCreatedOn(atts.getValue("created_on").replaceAll("null", "").trim());
			if (atts.getValue("emphasized") != null) curArticle.setEmphasized(atts.getValue("emphasized").replaceAll("null", "").trim());
			if (atts.getValue("is_duns") != null) curArticle.setDuns(atts.getValue("is_duns").replaceAll("null", "").trim());

		}
		else

		// <f3>
		if (localName.equals("f3"))
		{
			if (atts.getValue("src") != null) curArticle.setF3(atts.getValue("src").replaceAll("null", "").trim());

			// currently not holding image photographer via "title="
		}
		// <f4>
		else if (localName.equals("f4"))
		{
			if (atts.getValue("src") != null) curArticle.setF4(atts.getValue("src").replaceAll("null", "").trim());
			// currently not holding image photographer via "title="
		}
		// <tagit>
		else if (localName.equals("tagit"))
		{
			Tagit tagit = new Tagit(atts.getValue("id").replaceAll("null", "").trim(), atts.getValue("simplified").replaceAll("null", "")
					.trim(), "");
			curTagiot.add(tagit);
		}
		else if (localName.equals("company"))
		{
			curCompany = new RowCompanyDuns100();

			if (atts.getValue("company_doc_id") != null)
				curCompany.setCompanyDocId(atts.getValue("company_doc_id").replaceAll("null", "").trim());
			if (atts.getValue("name") != null) curCompany.setName(atts.getValue("name").replaceAll("null", "").trim());

		}
		else if (localName.equals("logo"))
		{
			if (atts.getValue("src") != null) curCompany.setLogo(atts.getValue("src").replaceAll("null", "").trim());

		}

	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException
	{
		if (localName.equals("article"))
		{
			// add tagiot to article
			curArticle.setTagiot(curTagiot);

			// add article to newSet
			newsSet.addItem(curArticle);
		}
		else if (localName.equals("company"))
		{
			newsSet.addItem(curCompany);

		}
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException
	{
		// TODO Auto-generated method stub
		super.characters(ch, start, length);
	}

}
