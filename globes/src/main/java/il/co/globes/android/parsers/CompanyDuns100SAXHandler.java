package il.co.globes.android.parsers;

import java.util.ArrayList;
import java.util.List;

import il.co.globes.android.objects.ArticleDuns100;
import il.co.globes.android.objects.Document;
import il.co.globes.android.objects.ManagerItemDuns100;
import il.co.globes.android.objects.RowArticleDuns100;
import il.co.globes.android.objects.ArticleSmallOpinion;
import il.co.globes.android.objects.CompanyDuns100;
import il.co.globes.android.objects.NewsSet;
import il.co.globes.android.objects.Tagit;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.text.TextUtils;

/**
 * 
 * @author Eviatar<br>
 * <br>
 * 
 *         Duns 100 sax parser
 * 
 */
public class CompanyDuns100SAXHandler extends DefaultHandler
{
	// data
	// private NewsSet curArticle;

	// ===========================================================
	// Fields
	// ===========================================================
	private boolean in_text = false;

	// article
	private CompanyDuns100 curCompany;

	private ManagerItemDuns100 managerDuns100;

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public CompanyDuns100 getParsedData()
	{
		return this.curCompany;
	}

	@Override
	public void startDocument() throws SAXException
	{
		this.curCompany = new CompanyDuns100();
		this.curCompany.setManagerDuns100s(new ArrayList<ManagerItemDuns100>());

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
		if (localName.equals("company"))
		{

			// add values
			if (atts.getValue("company_doc_id") != null)
				curCompany.setCompanyDocId(atts.getValue("company_doc_id").replaceAll("null", "").trim());

		}
		else if (localName.equals("company_details"))
		{
			if (atts.getValue("name") != null) curCompany.setName(atts.getValue("name").replaceAll("null", "").trim());
			if (atts.getValue("rank_id") != null) curCompany.setRank_id(atts.getValue("rank_id").replaceAll("null", "").trim());

			if (atts.getValue("rank_title") != null) curCompany.setRank_title(atts.getValue("rank_title").replaceAll("null", "").trim());
			if (atts.getValue("rank_ball") != null) curCompany.setRank_ball(atts.getValue("rank_ball").replaceAll("null", "").trim());

			if (atts.getValue("area") != null) curCompany.setArea(atts.getValue("area").replaceAll("null", "").trim());
			if (atts.getValue("phone") != null) curCompany.setPhone(atts.getValue("phone").replaceAll("null", "").trim());
			if (atts.getValue("fax") != null) curCompany.setFax(atts.getValue("fax").replaceAll("null", "").trim());
			if (atts.getValue("web_site") != null) curCompany.setWeb_site(atts.getValue("web_site").replaceAll("null", "").trim());
			if (atts.getValue("address") != null) curCompany.setAddress(atts.getValue("address").replaceAll("null", "").trim());

		}
		else if (localName.equals("logo"))
		{
			if (atts.getValue("src") != null) curCompany.setLogo(atts.getValue("src").replaceAll("null", "").trim());

		}

		else if (localName.equals("text"))
		{

			this.in_text = true;
		}
		else if (localName.equals("manager"))
		{
			managerDuns100 = new ManagerItemDuns100();

			if (atts.getValue("manager_doc_id") != null)
				managerDuns100.setManager_doc_id(atts.getValue("manager_doc_id").replaceAll("null", "").trim());
			if (atts.getValue("name") != null) managerDuns100.setName(atts.getValue("name").replaceAll("null", "").trim());
			if (atts.getValue("role") != null) managerDuns100.setRole(atts.getValue("role").replaceAll("null", "").trim());

		}
		else if (localName.equals("image"))

		{

			if (atts.getValue("src") != null) managerDuns100.setManagerImg(atts.getValue("src").replaceAll("null", "").trim());
			if (atts.getValue("title") != null) managerDuns100.setTitle(atts.getValue("title").replaceAll("null", "").trim());

		}
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException
	{
		if (localName.equals("text"))
		{
			this.in_text = false;
		}
		else if (localName.equals("manager"))
		{
			curCompany.getManagerDuns100s().add(managerDuns100);

		}

	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException
	{
		if (this.in_text)
		{
			curCompany.setText(new String(ch, start, length));
		}
	}

}
