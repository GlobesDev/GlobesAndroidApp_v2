package il.co.globes.android.objects;

import java.text.DecimalFormat;

public class Stock
{

	private String symbol;
	private String name_he;
	private String name_en;
	private String instrumentid;
	private String percentage_change;
	private String last;
	private String feeder;

	public String getSymbol()
	{
		if (symbol != null)
			return symbol;
		else return "";

	}
	public void setSymbol(String symbol)
	{
		this.symbol = symbol;
	}
	public String getName_he()
	{
		if (name_he != null)
			return name_he;
		else return "";
	}
	public void setName_he(String name_he)
	{
		this.name_he = name_he;
	}
	public String getName_en()
	{
		if (name_en != null)
			return name_en;
		else return "";
	}
	public void setName_en(String name_en)
	{
		this.name_en = name_en;
	}
	public String getId()
	{
		if (instrumentid != null)
			return instrumentid;
		else return "";
	}
	public void setId(String instrumentid)
	{
		this.instrumentid = instrumentid;
	}

	public String getPercentage_change()
	{
		try
		{
			double percentageChange = Double.parseDouble(percentage_change);
			DecimalFormat df = new DecimalFormat("###,###.##");
			return df.format(percentageChange) + "%";
		}
		catch (NumberFormatException ex)
		{
			return "";
		}
	}

	public void setPercentage_change(String percentage_change)
	{
		if (percentage_change == null)
		{
			this.percentage_change = "";
		}
		else
		{
			this.percentage_change = percentage_change;
		}
	}
	public String getInstrumentid()
	{
		if (instrumentid != null)
			return instrumentid;
		else return "";
	}
	public void setInstrumentid(String instrumentid)
	{
		this.instrumentid = instrumentid;
	}
	public String getLast()
	{
		if (last == null)
		{
			return "";
		}
		else
		{
			double aDouble = Double.parseDouble(last);

			DecimalFormat df = new DecimalFormat("###,###.##");
			return df.format(aDouble);

		}
	}
	public void setLast(String last)
	{
		this.last = last;
	}

	public String getFeeder()
	{
		if (feeder != null && feeder.length() > 0)
			return feeder;
		else return "";
	}
	public void setFeeder(String feeder)
	{
		this.feeder = feeder;
	}

	@Override
	public String toString()
	{

		return this.feeder + ", " + this.instrumentid + ", " + this.getLast() + ", " + this.getPercentage_change() + ", "
				+ this.getName_he();
	}

}
