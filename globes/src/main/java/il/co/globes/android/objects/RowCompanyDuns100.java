package il.co.globes.android.objects;

import android.text.TextUtils;

public class RowCompanyDuns100
{

	private String companyDocId="", logo, title, name;

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

}
