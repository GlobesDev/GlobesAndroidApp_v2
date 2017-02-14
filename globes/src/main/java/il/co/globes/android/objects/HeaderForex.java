package il.co.globes.android.objects;

//import java.util.Vector;



public class HeaderForex {

	private String title;


	public HeaderForex(String title) {
		super();
		this.title = title;
	}

	public HeaderForex() {
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
			if (((HeaderForex)object).title.equals(this.title))
			{
				return true;
			}
		}
		return false;
	}


}
