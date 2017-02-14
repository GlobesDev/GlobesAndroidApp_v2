package il.co.globes.android.objects;

import android.text.TextUtils;

public class NewCustomMenuItem
{

	private String name;
	private String id;
	private String href;
	private String ImgURL;
	private boolean isFinancialLinkItem,IsNodeWebItem;

	public String getName()
	{

		return TextUtils.isEmpty(name) ? "" : name;
	}
	public void setName(String name)
	{
		this.name += name;
	}
	public String getId()
	{
		return TextUtils.isEmpty(id) ? "" : id;

	}
	public void setId(String id)
	{
		this.id += id;

	}
	public String getHref()
	{
		return TextUtils.isEmpty(href) ? "" : href;

	}
	public void setHref(String href)
	{
		this.href += href;
	}
	public String getImgURL()
	{
		return TextUtils.isEmpty(ImgURL) ? "" : ImgURL;

	}
	public void setImgURL(String imgURL)
	{
		this.ImgURL += imgURL;
	}
	public boolean isIsNodeWebItem()
	{
		return IsNodeWebItem;
	}
	public void setIsNodeWebItem(boolean isNodeWebItem)
	{
		IsNodeWebItem = isNodeWebItem;
	}
	public boolean isFinancialLinkItem()
	{
		return isFinancialLinkItem;
	}
	public void setFinancialLinkItem(boolean isFinancialLinkItem)
	{
		this.isFinancialLinkItem = isFinancialLinkItem;
	}

}
