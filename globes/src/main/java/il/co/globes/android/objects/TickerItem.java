package il.co.globes.android.objects;

import il.co.globes.android.objects.ShareObjects.ShareData;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class TickerItem
{
	private static final String TAG = "TickerItem";
	
	private String insturmentID;
	private String nameHE;
	private String percentage_change;
	private String last;
	// added because of url and refresh url
	private String feeder;

	public TickerItem(String insturmentID, String nameHE, String percentage_change, String last, String feeder)
	{
		this.insturmentID = insturmentID;
		this.last = last;
		this.nameHE = nameHE;
		this.percentage_change = percentage_change;
		this.feeder = feeder;
	}

	public TickerItem(Stock stock)
	{
		this.insturmentID = stock.getId();
		this.last = stock.getLast();
		this.nameHE = stock.getName_he().replaceAll("\n","");
		this.percentage_change = stock.getPercentage_change();
		this.feeder = stock.getFeeder();

	}

	public TickerItem(ShareData shareData)
	{
		this.insturmentID = shareData.getInsturmentID();
		this.last = shareData.getLastPrice();
		this.nameHE = shareData.getShareNameHe().replaceAll("\n","");
		this.percentage_change = shareData.getDailyPercentageChange();
		this.feeder = shareData.getFeeder();

	}

	public TickerItem(Instrument instrument)
	{
		this.insturmentID = instrument.getId();
		this.last = instrument.getLast();
		this.nameHE = instrument.getHe().replaceAll("\n","");
		this.percentage_change = instrument.getPercentage_change();
		this.feeder = instrument.getFeeder();
	}

	public TickerItem(TickerItem o)
	{
		this.insturmentID = o.insturmentID;
		this.last = o.last;
		this.nameHE = o.nameHE;
		this.percentage_change = o.percentage_change;
		this.feeder = o.feeder;
	}

	public TickerItem(JSONObject object)
	{
		try
		{
			this.insturmentID = object.getString("insturmentID");
			this.last = object.getString("last");
			this.nameHE = object.getString("nameHE");
			this.percentage_change = object.getString("percentage_change");
			this.feeder = object.getString("feeder");
		}
		catch (JSONException e)
		{
			Log.e(TAG, "TickerItem(JSONObject object) : " + e.toString());
			e.printStackTrace();
		}

	}

	public JSONObject toJson()
	{
		JSONObject jsonObject = new JSONObject();
		try
		{
			jsonObject.put("insturmentID", this.insturmentID);
			jsonObject.put("last", this.last);
			jsonObject.put("nameHE", this.nameHE);
			jsonObject.put("percentage_change", this.percentage_change);
			jsonObject.put("feeder", this.feeder);
		}
		catch (JSONException e)
		{
			Log.e(TAG, "JSONObject toJson() : " + e.toString());
			e.printStackTrace();
		}
		return jsonObject;
	}

	// ///GETTERS/SETTERS/////
	public String getInsturmentID()
	{
		return insturmentID;
	}

	public void setInsturmentID(String insturmentID)
	{
		this.insturmentID = insturmentID;
	}

	public String getNameHE()
	{
		return nameHE;
	}

	public void setNameHE(String nameHE)
	{
		this.nameHE = nameHE.replaceAll("\n", "");
	}

	public String getPercentage_change()
	{
		return percentage_change;
	}

	public void setPercentage_change(String percentage_change)
	{
		this.percentage_change = percentage_change;
	}

	public String getLast()
	{
		return last;
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

	// /GETTERS/SETTERS//////

	@Override
	public boolean equals(Object obj)
	{
		if (obj == null)
		{
			return false;
		}
		if (obj == this)
		{
			return true;
		}
		if (obj.getClass() != getClass())
		{
			return false;
		}
		TickerItem rhs = (TickerItem) obj;
		return new EqualsBuilder().append(insturmentID, rhs.insturmentID).isEquals();
	}

}
