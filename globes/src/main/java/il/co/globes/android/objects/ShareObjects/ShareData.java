package il.co.globes.android.objects.ShareObjects;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.text.TextUtils;
import android.widget.TextView;

public class ShareData
{

	String shareSymbol;// ��' ��"�
	// TODO test adding the share name
	String shareNameHe;
	String timestamp; // ���� ������
	String lastPrice;// ��� �����
	String dailyPercentageChange; // ����� ����
	String dailyPointsChange;// ����� ���
	String shareVolume; // ��� ����
	String dailyHigh;// ���� ����
	String dailyLow;// ���� ����
	String shareMarketCap; // ���� ���
	String shareType;
	String exchangeInitials;
	String insturmentID;
	String feeder;
	String ChangeFromLastMonth;
	String openPrice;
	String ChangeFromLastYear;
	String LastWeekClosePrice;
	String totVol;

	public String getShareNameHe()
	{
		if (shareNameHe == null || (shareNameHe != null && shareNameHe.length() < 1))
		{
			return "";
		}
		return shareNameHe;
	}

	public void setShareNameHe(String shareNameHe)
	{
		this.shareNameHe = shareNameHe;
	}
	public String getShareSymbol()
	{
		if (shareSymbol == null)
			return "";
		else return shareSymbol;
	}
	public void setShareSymbol(String shareSymbol)
	{
		this.shareSymbol = shareSymbol;
	}
	public String getTimestamp()
	{
		if (timestamp == null)
			return "";
		else return timestamp;
	}
	public void setTimestamp(String timestamp)
	{
		Locale.setDefault(Locale.ENGLISH);
		SimpleDateFormat sdf = new SimpleDateFormat("MMM dd yyyy hh:mma");
		try
		{
			Date date = sdf.parse(timestamp);

			SimpleDateFormat dateToPrint = new SimpleDateFormat("dd/MM/yyyy HH:mm");
			this.timestamp = dateToPrint.format(date);

		}
		catch (Exception e)
		{
			this.timestamp = "";
		}
	}
	public String getLastPrice()
	{
		if (lastPrice == null)
			return "";
		else return lastPrice;
	}
	public void setLastPrice(String lastPrice)
	{
		if (lastPrice.length() == 0)
			this.lastPrice = "";
		else
		{
			double aDouble = Double.parseDouble(lastPrice);
			if (lastCheck(lastPrice))
			{
				DecimalFormat df = new DecimalFormat("###,###.####");
				this.lastPrice = df.format(aDouble);
			}
			else
			{
				DecimalFormat df = new DecimalFormat("###,###.##");
				this.lastPrice = df.format(aDouble);
			}
		}
	}
	private boolean lastCheck(String lastCheck)
	{
		int dotIndex = lastCheck.indexOf(".");
		String afterDot = new String(lastCheck.substring(dotIndex + 1, lastCheck.length()));
		if (afterDot.length() > 2) return true;
		return false;
	}
	public String getDailyPercentageChange()
	{
		if (dailyPercentageChange == null)
			return "";
		else return dailyPercentageChange + "%";
	}
	public void setDailyPercentageChange(String dailyPercentageChange)
	{
		if (dailyPercentageChange.length() == 0)
			this.dailyPercentageChange = "";
		else
		{
			double aDouble = Double.parseDouble(dailyPercentageChange);
			DecimalFormat df = new DecimalFormat("#.###");
			this.dailyPercentageChange = df.format(aDouble);
		}
	}
	public String getDailyPointsChange()
	{
		if (dailyPointsChange == null)
			return "";
		else return dailyPointsChange;
	}
	public void setDailyPointsChange(String dailyPointsChange)
	{
		this.dailyPointsChange = dailyPointsChange;
	}
	public String getShareVolume()
	{
		if (shareVolume == null)
			return "";
		else return shareVolume;
	}
	public void setShareVolume(String shareVolume)
	{
		if (shareVolume.length() == 0)
			this.shareVolume = "";
		else
		{
			double aDouble = Double.parseDouble(shareVolume);
			DecimalFormat df = new DecimalFormat("###,###.##");
			this.shareVolume = df.format(aDouble);
		}
	}
	public String getDailyHigh()
	{
		if (dailyHigh == null)
			return "";
		else return dailyHigh;
	}
	public void setDailyHigh(String dailyHigh)
	{
		if (dailyHigh.length() == 0)
			this.dailyHigh = "";
		else
		{
			double aDouble = Double.parseDouble(dailyHigh);
			DecimalFormat df = new DecimalFormat("###,###.##");
			this.dailyHigh = df.format(aDouble);
		}
	}
	public String getDailyLow()
	{
		if (dailyLow == null)
			return "";
		else return dailyLow;
	}
	public void setDailyLow(String dailyLow)
	{
		if (dailyLow.length() == 0)
			this.dailyLow = "";
		else
		{
			double aDouble = Double.parseDouble(dailyLow);
			DecimalFormat df = new DecimalFormat("###,###.##");
			this.dailyLow = df.format(aDouble);
		}
	}

	public String getShareMarketCap()
	{
		if (shareMarketCap == null)
			return "";
		else return shareMarketCap;
	}

	public void setShareMarketCap(String shareMarketCap)
	{
		if (shareMarketCap.length() == 0)
			this.shareMarketCap = "";
		else
		{
			double aDouble = Double.parseDouble(shareMarketCap);
			DecimalFormat df = new DecimalFormat("###,###.##");
			this.shareMarketCap = df.format(aDouble);
		}
	}

	public String getShareType()
	{
		if (shareType == null)
			return "";
		else return shareType;
	}
	public void setShareType(String shareType)
	{
		this.shareType = shareType;
	}

	public String getExchangeInitials()
	{
		if (exchangeInitials == null)
			return "";
		else return exchangeInitials;
	}
	public void setExchangeInitials(String exchangeInitials)
	{
		this.exchangeInitials = exchangeInitials;
	}
	public String getInsturmentID()
	{
		if (insturmentID != null)
			return insturmentID;
		else return "";
	}
	public void setInsturmentID(String insturmentID)
	{
		this.insturmentID = insturmentID;
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

	public String getChangeFromLastMonth()
	{
		return TextUtils.isEmpty(ChangeFromLastMonth) ? "" : ChangeFromLastMonth;
	}
	public void setChangeFromLastMonth(String changeFromLastMonth)
	{
		ChangeFromLastMonth += changeFromLastMonth;
	}
	public String getOpenPrice()
	{
		return TextUtils.isEmpty(openPrice) ? "" : openPrice;

	}
	public void setOpenPrice(String openPrice)
	{
		this.openPrice += openPrice;
	}
	public String getChangeFromLastYear()
	{
		return TextUtils.isEmpty(ChangeFromLastYear) ? "" : ChangeFromLastYear;

	}
	public void setChangeFromLastYear(String changeFromLastYear)
	{
		ChangeFromLastYear += changeFromLastYear;
	}
	public String getLastWeekClosePrice()
	{
		return TextUtils.isEmpty(LastWeekClosePrice) ? "" : LastWeekClosePrice;

	}
	public void setLastWeekClosePrice(String lastWeekClosePrice)
	{
		LastWeekClosePrice += lastWeekClosePrice;
	}
	public String gettotVol()
	{
		return TextUtils.isEmpty(totVol) ? "" : totVol;

	}
	public void settotVol(String lastWeekClosePrice)
	{
		totVol += lastWeekClosePrice;
	}

}
