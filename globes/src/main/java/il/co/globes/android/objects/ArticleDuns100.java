package il.co.globes.android.objects;

import il.co.globes.android.Utils;
import il.co.globes.android.parsers.ArticleDuns100SAXHandler;
import il.co.globes.android.parsers.DocumentSAXHandler;

import java.net.URL;
import java.util.List;

import android.text.TextUtils;

public class ArticleDuns100
{

	private String f3, subTitle;
	private String doc_id;
	private String title = "";
	private boolean isDuns = false;
	private String author;

	private String createdOn;
	private String text;

	public String getSubTitle()
	{
		return TextUtils.isEmpty(subTitle) ? "" : subTitle;
	}

	public void setSubTitle(String subTitle)
	{
		if (this.subTitle == null)
		{
			this.subTitle = subTitle;
		}
		else
		{
			this.subTitle += subTitle;
		}
	}

	public String getF3()
	{
		return TextUtils.isEmpty(f3) ? "" : f3;
	}

	public void setF3(String f3)
	{
		if (this.f3 == null)
		{
			this.f3 = f3;
		}
		else
		{
			this.f3 += f3;
		}
	}

	public String getDoc_id()
	{
		if (doc_id != null)
			return doc_id;
		else return "";
	}

	public void setDoc_id(String doc_id)
	{
		this.doc_id = doc_id;
	}

	public String getTitle()
	{
		if (title != null)
			return title;
		else return "";
	}

	public void setTitle(String title)
	{
		this.title += title;
	}

	public String getCreatedOn()
	{
		return createdOn;
	}

	public void setCreatedOn(String createdOn)
	{
		this.createdOn = createdOn;
	}

	public boolean isDuns()
	{
		return isDuns;
	}

	public void setDuns(String isDuns)
	{
		if (isDuns.equals("1"))
		{
			this.isDuns = true;
		}
	}

	public String getAuthor()
	{

		if (author != null)
			return author;
		else return "";
	}

	public void setAuthor(String author)
	{
		this.author += author;
	}

	public String getText()
	{
		return TextUtils.isEmpty(text) ? "" : text;

	}

	public void setText(String text)
	{
		this.text += text;
	}

	public static ArticleDuns100 parseArticleDuns100(URL url) throws Exception
	{
		ArticleDuns100SAXHandler documentHandler = new ArticleDuns100SAXHandler();
		Utils.parseXmlFromUrlUsingHandler(url, documentHandler);
		ArticleDuns100 parsedDocument = documentHandler.getParsedData();
		return parsedDocument;
	}

}
