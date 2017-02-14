package il.co.globes.android.objects;

import android.text.TextUtils;

public class ManagerItemDuns100
{
	private String manager_doc_id="", name="", role="", title="", managerImg="";

	public String getManager_doc_id()
	{

		return TextUtils.isEmpty(manager_doc_id) ? "" : manager_doc_id;

	}

	public void setManager_doc_id(String manager_doc_id)
	{
		this.manager_doc_id += manager_doc_id;
	}

	public String getName()
	{

		return TextUtils.isEmpty(name) ? "" : name;

	}

	public void setName(String name)
	{
		this.name += name;
	}

	public String getRole()
	{
		return TextUtils.isEmpty(role) ? "" : role;

	}

	public void setRole(String role)
	{
		this.role += role;
	}

	public String getTitle()
	{
		return TextUtils.isEmpty(title) ? "" : title;

	}

	public void setTitle(String title)
	{
		this.title += title;
	}

	public String getManagerImg()
	{
		return TextUtils.isEmpty(managerImg) ? "" : managerImg;
	}

	public void setManagerImg(String managerImg)
	{
		this.managerImg += managerImg;
	}

}
