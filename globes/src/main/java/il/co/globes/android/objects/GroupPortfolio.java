package il.co.globes.android.objects;

//import java.util.Vector;



public class GroupPortfolio {

	private String title;
	//	public Vector<Article> articles = new Vector<Article>();
	//	public Vector<InnerGroup> innerGroups = new Vector<InnerGroup>();


	//	public Vector<InnerGroup> getInnerGroups() {
	//		return innerGroups;
	//	}
	//
	//	public void setInnerGroups(Vector<InnerGroup> innerGroups) {
	//		this.innerGroups = innerGroups;
	//	}

	public GroupPortfolio(String title) {
		super();
		this.title = title;
	}

	public GroupPortfolio() {
		super();
	}

	public String getTitle() {
		if (title!=null)return title;
		else return "";
	}



	public void setTitle(String title) {
		if (title!=null) this.title = title;
		else this.title ="";
	}
	public String toString(){
		return title;
	}

	@Override
	public boolean equals(Object object)
	{
		if(object.getClass()==this.getClass())
		{
			if (((GroupPortfolio)object).title.equals(this.title))
			{
				return true;
			}
		}
		return false;
	}


}
