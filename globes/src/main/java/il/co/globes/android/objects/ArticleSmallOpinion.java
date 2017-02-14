package il.co.globes.android.objects;

import android.text.TextUtils;

public class ArticleSmallOpinion extends ArticleOpinion
{
	private String subTitle, f7, authorImgURL, f2, f3, f4;

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

	public String getF7()
	{
		return TextUtils.isEmpty(f7) ? "" : f7;
	}

	public void setF7(String f7)
	{
		if (this.f7 == null)
		{
			this.f7 = f7;
		}
		else
		{
			this.f7 += f7;
		}
	}

	public String getAuthorImgURL()
	{
		return TextUtils.isEmpty(authorImgURL) ? "" : authorImgURL;
	}

	public void setAuthorImgURL(String authorImgURL)
	{
		if (this.authorImgURL == null)
		{
			this.authorImgURL = authorImgURL;
		}
		else
		{
			this.authorImgURL += authorImgURL;
		}
	}

	public String getF2()
	{
		return TextUtils.isEmpty(f2) ? "" : f2;
	}

	public void setF2(String f2)
	{
		if (this.f2 == null)
		{
			this.f2 = f2;
		}
		else
		{
			this.f2 += f2;
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

	@Override
	public String toString()
	{
		return "ArticleSmallOpinion [subTitle=" + subTitle + ", f7=" + f7 + ", authorImgURL=" + authorImgURL + ", f2=" + f2 + ", f3="
				+ f3 + ", f4=" + f4 + "]";
	}
	
	

}
