package il.co.globes.android.objects;

//import java.util.Vector;



public class HeaderFutures {

	private String title;


	public HeaderFutures(String title) {
		super();
		this.title = title;
	}

	public HeaderFutures() {
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
			if (((HeaderFutures)object).title.equals(this.title))
			{
				return true;
			}
		}
		return false;
	}


}
