package il.co.globes.android.objects;

//import java.util.Vector;



public class HeaderShares {

	private String title;


	public HeaderShares(String title) {
		super();
		this.title = title;
	}

	public HeaderShares() {
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
			if (((HeaderShares)object).title.equals(this.title))
			{
				return true;
			}
		}
		return false;
	}


}
