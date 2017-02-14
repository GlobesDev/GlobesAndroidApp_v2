package il.co.globes.android.objects;

public class Tikim
{

	private String tik;
	private String portfolioID;
	private boolean isSelected;
	private String feeder;
	
	public Tikim(String tik, boolean isSelected, String portfolioID, String feeder)
	{
		super();
		this.tik = tik;
		this.isSelected = isSelected;
		this.portfolioID=portfolioID;
		this.feeder=feeder;
	}
	
	


	public Tikim()
	{
		isSelected = false;
		tik = "";
		portfolioID="";
		feeder = "";
	}




	public String getFeeder()
	{
		return feeder;
	}




	public void setFeeder(String feeder)
	{
		this.feeder = feeder;
	}




	public String getPortfolioID()
	{
		return portfolioID;
	}



	public void setPortfolioID(String portfolioID)
	{
		this.portfolioID = portfolioID;
	}



	public String getTik()
	{
		return tik;
	}
	public void setTik(String tik)
	{
		this.tik = tik;
	}
	public boolean isSelected()
	{
		return isSelected;
	}
	public void setSelected(boolean isSelected)
	{
		this.isSelected = isSelected;
	}




	@Override
	public String toString()
	{
		return "Tikim [tik=" + tik + ", portfolioID=" + portfolioID + ", isSelected=" + isSelected + ", feeder=" + feeder + "]";
	}


	
	
}
