package il.co.globes.android.objects;

//import java.util.Vector;



public class HeaderInstruments {

	private String title;


	public HeaderInstruments(String title) {
		super();
		this.title = title;
	}

	public HeaderInstruments() {
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
			if (((HeaderInstruments)object).title.equals(this.title))
			{
				return true;
			}
		}
		return false;
	}


}
