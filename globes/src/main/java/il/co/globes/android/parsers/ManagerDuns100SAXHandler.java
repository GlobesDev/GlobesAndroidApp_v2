package il.co.globes.android.parsers;

import java.util.ArrayList;
import java.util.List;

import il.co.globes.android.objects.ArticleDuns100;
import il.co.globes.android.objects.Document;
import il.co.globes.android.objects.ManagerDuns100;
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
public class ManagerDuns100SAXHandler extends DefaultHandler
{
	// data
	// private NewsSet curArticle;

	// ===========================================================
	// Fields
	// ===========================================================
	private boolean in_text = false;

	// ManagerDuns100
	private ManagerDuns100 curManager;

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public ManagerDuns100 getParsedData()
	{
		return this.curManager;
	}

	@Override
	public void startDocument() throws SAXException
	{
		super.startDocument();
		this.curManager = new ManagerDuns100();
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
		if (localName.equals("manager"))
		{

			// add values
			if (atts.getValue("manager_doc_id") != null)
				curManager.setManager_doc_id(atts.getValue("manager_doc_id").replaceAll("null", "").trim());

			if (atts.getValue("name") != null) curManager.setName(atts.getValue("name").replaceAll("null", "").trim());

			if (atts.getValue("email") != null) curManager.setEmail(atts.getValue("email").replaceAll("null", "").trim());

			if (atts.getValue("education") != null) curManager.setEducation(atts.getValue("education").replaceAll("null", "").trim());

			if (atts.getValue("role") != null) curManager.setRole(atts.getValue("role").replaceAll("null", "").trim());

			if (atts.getValue("company_doc_id") != null)
				curManager.setCompany_doc_id(atts.getValue("company_doc_id").replaceAll("null", "").trim());

			if (atts.getValue("sub_title") != null) curManager.setSub_title(atts.getValue("sub_title").replaceAll("null", "").trim());

		}
		else

		if (localName.equals("image"))
		{
			if (atts.getValue("src") != null) curManager.setImageOfManager(atts.getValue("src").replaceAll("null", "").trim());
		}
		else if (localName.equals("logo"))
		{
			if (atts.getValue("src") != null) curManager.setImageLogoOfCompany(atts.getValue("src").replaceAll("null", "").trim());

		}

	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException
	{

	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException
	{
	}

}
