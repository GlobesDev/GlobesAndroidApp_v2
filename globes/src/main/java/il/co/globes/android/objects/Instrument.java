package il.co.globes.android.objects;

import java.text.DecimalFormat;

public class Instrument
{

	private String feeder;
	private String id;
	private String he;
	private String last;
	private String percentage_change;
	private boolean isIndexWithInstruments = false;
	private boolean isCurrency;
	private boolean InStockPanel = false;

	public Instrument(String feeder, String id, String he, String last, String percentage_change, boolean isIndexWithInstruments)
	{
		super();
		this.feeder = feeder;
		this.id = id;
		this.he = he;
		this.last = last;
		this.percentage_change = percentage_change;
		this.isIndexWithInstruments = isIndexWithInstruments;
	}

	public boolean isCurrency()
	{
		return isCurrency;
	}

	public void setCurrency(boolean isCurrency)
	{
		this.isCurrency = isCurrency;
	}

	public Instrument()
	{
		super();
	}

	public String getFeeder()
	{
		return feeder;
	}

	public void setFeeder(String feeder)
	{
		if (feeder == null)
			this.feeder = "";
		else this.feeder = feeder;
	}

	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		if (id == null)
			this.id = "";
		else this.id = id;
	}

	public String getHe()
	{
		return he;
	}

	public void setHe(String he)
	{
		if (he == null)
			this.he = "";
		else this.he = he;
	}

	public String getLast()
	{
		if (last == null || last.length() == 0)
		{
			return "";
		}
		else
		{
			double aDouble = Double.parseDouble(last);
			if (isCurrency)
			{
				DecimalFormat df = new DecimalFormat("###,###.####");
				return df.format(aDouble);
			}
			else
			{
				DecimalFormat df = new DecimalFormat("###,###.##");
				return df.format(aDouble);
			}
		}
	}

	public void setLast(String last)
	{
		if (last == null)
			this.last = "";
		else this.last = last;
	}

	public String getPercentage_change()
	{
		if (percentage_change == null || percentage_change.length() == 0)
		{
			return "";
		}
		else
		{
			try
			{
				double aDouble = Double.parseDouble(percentage_change);
				DecimalFormat df = new DecimalFormat("###,###.##");
				return df.format(aDouble) + "%";
			}
			catch (NumberFormatException e)
			{
				return "";
			}
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

	public boolean isIndexWithInstruments()
	{
		return isIndexWithInstruments;
	}

	public void setIndexWithInstruments()
	{
		this.isIndexWithInstruments = true;
	}

	public boolean isInStockPanel()
	{
		return InStockPanel;
	}

	public void setInStockPanel(boolean inStockPanel)
	{
		InStockPanel = inStockPanel;
	}

	@Override
	public String toString()
	{
		return this.feeder + ", " + this.id + ", " + this.getLast() + ", " + this.getPercentage_change() + ", " + this.getHe();
	}

}
