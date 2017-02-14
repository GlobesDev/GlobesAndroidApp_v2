package il.co.globes.android.objects;

//import java.util.Vector;


public class InnerGroup {

	private String node_id;
	private String title;
	private String short_title;

	
	
	
	public InnerGroup(String title) {
		super();
		this.title = title;
	}
	
	
	public InnerGroup() {
		super();
	}


	public String getNode_id() {
		if (node_id!=null)return node_id;
		else return "";
	}
	public void setNode_id(String node_id) {
		this.node_id = node_id;
	}
	public String getTitle() {
		if (title!=null)return title;
		else return "";
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getShort_title() {
		if (short_title!=null)return short_title;
		else return "";
	}
	public void setShort_title(String short_title) {
		this.short_title = short_title;
	}
	
	public String toString(){
		if (title!=null)return title;
		else return "";
	}

	@Override
	public boolean equals(Object object)
	{
		if(object.getClass()==this.getClass())
		{
			if (((InnerGroup)object).getTitle().equals(this.getTitle()))
			{
				return true;
			}
		}
		return false;
	}


}
