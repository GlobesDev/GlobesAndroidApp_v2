package il.co.globes.android.objects;

import java.util.List;

import android.text.TextUtils;

public class RowArticleDuns100 extends Article

{

	// Tagiot
	private List<Tagit> tagiot;

	private String f3, f4, subTitle;
	private String doc_id;
	private String title = "";
	private boolean isEmphasized = false;
	private boolean isDuns = false;

	private String createdOn;

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

	public String getF4()
	{
		return TextUtils.isEmpty(f4) ? "" : f4;
	}

	public void setF4(String f4)
	{
		if (this.f4 == null)
		{
			this.f4 = f4;
		}
		else
		{
			this.f4 += f4;
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

	public boolean isEmphasized()
	{
		return isEmphasized;
	}

	public void setEmphasized(String isEmphasized)
	{
		if (isEmphasized.equals("true"))
		{
			this.isEmphasized = true;
		}
	}
	public String getCreatedOn()
	{
		return createdOn;
	}

	public void setCreatedOn(String createdOn)
	{
		this.createdOn = createdOn;
	}

	public List<Tagit> getTagiot()
	{
		return tagiot;
	}

	public void setTagiot(List<Tagit> tagiot)
	{
		this.tagiot = tagiot;
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

}
