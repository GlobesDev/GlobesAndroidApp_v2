package il.co.globes.android.objects;

import com.google.android.gms.ads.AdSize;



/**
 * 
 * @author Eviatar <br>
 * <br>
 *         Holds data for dfp Ad view , adisze , adunitID
 */
public class DfpAdHolder
{
	public static final int TYPE_DFP_KATAVA_PIRSUMIT_MAIN_LIST = 110;
	public static final int TYPE_DFP_MAIN_LIST_BETWEEN_ARTICLES = 120;
	public static final int TYPE_DFP_KATAVA_PIRSUMIT_NADLAN = 130;
	
	public static final int TYPE_DFP_KATAVA_PIRSUMIT_MAIN_LIST_2 = 140;
	public static final int TYPE_DFP_KATAVA_PIRSUMIT_HOT_STORY = 150;

	private AdSize adSize;
	private String adUnitID;
	private int adType;

	public DfpAdHolder(AdSize adSize, String AdunitID, int adType)
	{
		this.adSize = adSize;
		this.adUnitID = AdunitID;
		this.adType = adType;
	}

	public AdSize getAdSize()
	{
		return adSize;
	}

	public void setAdSize(AdSize adSize)
	{
		this.adSize = adSize;
	}

	public String getAdUnitID()
	{
		return adUnitID;
	}

	public void setAdUnitID(String adUnitID)
	{
		this.adUnitID = adUnitID;
	}

	public int getAdType()
	{
		return adType;
	}

	public void setAdType(int adType)
	{
		this.adType = adType;
	}

}
