package il.co.globes.android.objects;

import java.text.DecimalFormat;

public class PortfolioInstrument extends Instrument
{
	private String changeSinceAdded;

	public PortfolioInstrument()
	{
		super();		
	}	

	public PortfolioInstrument(String feeder, String id, String he, String last, String percentage_change, boolean isIndexWithInstruments,
			String changeSinceAdded)
	{
		super(feeder, id, he, last, percentage_change, isIndexWithInstruments);
		this.changeSinceAdded = changeSinceAdded;
	}	
	
	public String getChangeSinceAdded()
	{
		if (this.changeSinceAdded == null || this.changeSinceAdded.length() == 0) 
		{
			return "";
		}
		else
		{
			try
			{
				double aDouble = Double.parseDouble(this.changeSinceAdded);
				//DecimalFormat df = new DecimalFormat("###,###.##");
				DecimalFormat df = new DecimalFormat("###.##");

				return df.format(aDouble)+"%";
			} 
			catch (NumberFormatException e) 
			{    
				return "";
			}
		}	
		
	}
	
	public void setChangeSinceAdded(String changeSinceAdded) 
	{
		if (changeSinceAdded == null) 
		{
			this.changeSinceAdded = "";
		}
		else 
		{
			this.changeSinceAdded = changeSinceAdded;
		}
	}
	
}
