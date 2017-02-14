package il.co.globes.android.objects;

import il.co.globes.android.Utils;
import il.co.globes.android.parsers.ArticleDuns100SAXHandler;
import il.co.globes.android.parsers.ManagerDuns100SAXHandler;

import java.net.URL;

import android.text.TextUtils;

public class ManagerDuns100
{
	private String role="", name="", manager_doc_id="", email="", education="", company_doc_id="", sub_title="", imageOfManager="", imageLogoOfCompany="";

	public String getRole()
	{

		return TextUtils.isEmpty(role) ? "" : role;
	}

	public void setRole(String role)
	{
		this.role += role;
	}

	public String getName()
	{
		return TextUtils.isEmpty(name) ? "" : name;

	}

	public void setName(String name)
	{
		this.name += name;
	}

	public String getManager_doc_id()
	{
		return TextUtils.isEmpty(manager_doc_id) ? "" : manager_doc_id;

	}

	public void setManager_doc_id(String manager_doc_id)
	{
		this.manager_doc_id = manager_doc_id;
	}

	public String getEmail()
	{
		return TextUtils.isEmpty(email) ? "" : email;

	}

	public void setEmail(String email)
	{
		this.email += email;
	}

	public String getEducation()
	{
		return TextUtils.isEmpty(education) ? "" : education;

	}

	public void setEducation(String education)
	{
		this.education += education;
	}

	public String getCompany_doc_id()
	{
		return TextUtils.isEmpty(company_doc_id) ? "" : company_doc_id;

	}

	public void setCompany_doc_id(String company_doc_id)
	{
		this.company_doc_id += company_doc_id;
	}

	public String getSub_title()
	{
		return TextUtils.isEmpty(sub_title) ? "" : sub_title;

	}

	public void setSub_title(String sub_title)
	{
		this.sub_title += sub_title;
	}

	public String getImageOfManager()
	{
		return TextUtils.isEmpty(imageOfManager) ? "" : imageOfManager;

	}

	public void setImageOfManager(String imageOfManager)
	{
		this.imageOfManager = imageOfManager;
	}

	public String getImageLogoOfCompany()
	{
		return TextUtils.isEmpty(imageLogoOfCompany) ? "" : imageLogoOfCompany;

	}

	public void setImageLogoOfCompany(String imageLogoOfCompany)
	{
		this.imageLogoOfCompany += imageLogoOfCompany;
	}

	public static ManagerDuns100 parseManagerDuns100(URL url) throws Exception
	{
		ManagerDuns100SAXHandler documentHandler = new ManagerDuns100SAXHandler();
		Utils.parseXmlFromUrlUsingHandler(url, documentHandler);
		ManagerDuns100 parsedDocument = documentHandler.getParsedData();
		return parsedDocument;
	}
}
