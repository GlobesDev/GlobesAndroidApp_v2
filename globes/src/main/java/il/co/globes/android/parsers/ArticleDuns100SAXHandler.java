package il.co.globes.android.parsers;

import java.util.ArrayList;
import java.util.List;

import il.co.globes.android.objects.ArticleDuns100;
import il.co.globes.android.objects.Document;
import il.co.globes.android.objects.RowArticleDuns100;
import il.co.globes.android.objects.ArticleSmallOpinion;
import il.co.globes.android.objects.CompanyDuns100;
import il.co.globes.android.objects.NewsSet;
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
public class ArticleDuns100SAXHandler extends DefaultHandler
{
	// data
	// private NewsSet curArticle;

	// ===========================================================
	// Fields
	// ===========================================================
	private boolean in_text = false;

	// article
	private ArticleDuns100 curArticle;

	private Document curDocument;

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public ArticleDuns100 getParsedData()
	{
		return this.curArticle;
	}

	@Override
	public void startDocument() throws SAXException
	{
		super.startDocument();
		this.curArticle = new ArticleDuns100();
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
			// curArticle = new ArticleDuns100();

			// add values
			if (atts.getValue("doc_id") != null) curArticle.setDoc_id(atts.getValue("doc_id").replaceAll("null", "").trim());
			if (atts.getValue("title") != null) curArticle.setTitle(atts.getValue("title").replaceAll("null", "").trim());
			if (atts.getValue("created_on") != null) curArticle.setCreatedOn(atts.getValue("created_on").replaceAll("null", "").trim());
			if (atts.getValue("is_duns") != null) curArticle.setDuns(atts.getValue("is_duns").replaceAll("null", "").trim());
			if (atts.getValue("author") != null) curArticle.setAuthor(atts.getValue("author").replaceAll("null", "").trim());
			if (atts.getValue("sub_title") != null) curArticle.setSubTitle(atts.getValue("sub_title").replaceAll("null", "").trim());
		}
		else

		// <f3>
		if (localName.equals("f3"))
		{
			if (atts.getValue("src") != null) curArticle.setF3(atts.getValue("src").replaceAll("null", "").trim());

			// currently not holding image photographer via "title="
		}
		else if (localName.equals("text")) ;
		this.in_text = true;

	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException
	{
		if (localName.equals("text"))
		{
			this.in_text = false;
		}

	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException
	{
		if (this.in_text)
		{
			curArticle.setText(new String(ch, start, length));
		}
	}

}
