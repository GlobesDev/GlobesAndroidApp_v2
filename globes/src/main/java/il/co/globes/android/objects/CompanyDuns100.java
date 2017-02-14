package il.co.globes.android.objects;

import il.co.globes.android.Utils;
import il.co.globes.android.parsers.ArticleDuns100SAXHandler;
import il.co.globes.android.parsers.CompanyDuns100SAXHandler;

import java.net.URL;
import java.util.List;

import android.text.TextUtils;

public class CompanyDuns100
{
	private List<ManagerItemDuns100> managerDuns100s;

	private String companyDocId, logo, title, name, rank_id, rank_title, rank_ball, year, area, phone, fax, web_site, address, text;

	public String getLogo()
	{
		return TextUtils.isEmpty(logo) ? "" : logo;
	}

	public String getCompanyDocId()
	{
		return TextUtils.isEmpty(companyDocId) ? "" : companyDocId;
	}
	public void setCompanyDocId(String companyDocId)
	{
		this.companyDocId += companyDocId;
	}
	public String getRank_id()
	{
		return TextUtils.isEmpty(rank_id) ? "" : rank_id;

	}
	public void setRank_id(String rank_id)
	{
		this.rank_id += rank_id;
	}
	public String getYear()
	{
		return TextUtils.isEmpty(year) ? "" : year;
	}
	public void setYear(String year)
	{
		this.year += year;
	}
	public String getArea()
	{
		return TextUtils.isEmpty(area) ? "" : area;

	}
	public void setArea(String area)
	{
		this.area += area;
	}
	public String getPhone()
	{
		return TextUtils.isEmpty(phone) ? "" : phone;

	}
	public void setPhone(String phone)
	{
		this.phone += phone;
	}
	public String getFax()
	{

		return TextUtils.isEmpty(fax) ? "" : fax;

	}
	public void setFax(String fax)
	{
		this.fax += fax;
	}
	public String getWeb_site()
	{

		return TextUtils.isEmpty(web_site) ? "" : web_site;

	}
	public void setWeb_site(String web_site)
	{
		this.web_site += web_site;
	}
	public String getAddress()
	{
		return TextUtils.isEmpty(address) ? "" : address;

	}
	public void setAddress(String address)
	{
		this.address += address;
	}
	public void setLogo(String logo)
	{
		this.logo += logo;
	}
	public String getName()
	{
		return TextUtils.isEmpty(name) ? "" : name;
	}

	public void setName(String name)
	{
		this.name += name;
	}

	public String getTitle()
	{
		return TextUtils.isEmpty(title) ? "" : title;
	}

	public void setTitle(String title)
	{
		this.title += title;
	}

	public String getText()
	{
		return TextUtils.isEmpty(text) ? "" : text;

	}

	public void setText(String text)
	{
		this.text += text;
	}

	public String getRank_title()
	{
		return TextUtils.isEmpty(rank_title) ? "" : rank_title;

	}

	public void setRank_title(String rank_title)
	{
		this.rank_title += rank_title;
	}

	public String getRank_ball()
	{
		return TextUtils.isEmpty(rank_ball) ? "" : rank_ball;

	}

	public void setRank_ball(String rank_ball)
	{
		this.rank_ball += rank_ball;
	}

	public static CompanyDuns100 parseCompanyDuns100(URL url) throws Exception
	{
		CompanyDuns100SAXHandler documentHandler = new CompanyDuns100SAXHandler();
		Utils.parseXmlFromUrlUsingHandler(url, documentHandler);
		CompanyDuns100 parsedDocument = documentHandler.getParsedData();
		return parsedDocument;
	}
	public List<ManagerItemDuns100> getManagerDuns100s()
	{
		return managerDuns100s;
	}
	public void setManagerDuns100s(List<ManagerItemDuns100> managerDuns100s)
	{
		this.managerDuns100s = managerDuns100s;
	}

}
