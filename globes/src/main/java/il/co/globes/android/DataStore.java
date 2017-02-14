package il.co.globes.android;

import il.co.globes.android.objects.Article;
import il.co.globes.android.objects.ArticleSmallOpinion;
import il.co.globes.android.objects.NewsSet;

import java.util.ArrayList;

import android.os.Build;
import android.util.Log;

/**
 * 
 * @author Eviatar<br>
 * <br>
 *         Singleton holds application wide data
 * 
 */
public class DataStore
{
	// the instance
	private static DataStore theInstance = null;

	private boolean showAdMob=false;
	// dynamic menu items parsed before menu appears
	private NewsSet dynamicMenuItemsHolderDebugMap;
	private NewsSet dynamicMenuItemsHolderProductionMap;
	private NewsSet mainScreenItems;
    private int commentPos=0;
	// screen dimensions
	private int screenWidth, screenHeight;

	// device visible model
	private String deviceModel = Build.MODEL;
	
	private String liveBoxMainURL;
	private String liveBoxArticleURL;

	private ArrayList<Article>documents;
	private ArrayList<ArticleSmallOpinion> ArticleSmallOpinion;
	
	private String appVersion ;
	private boolean isSplashScreenClosed=false;

	private DataStore()
	{
		// add stuff if required
		dynamicMenuItemsHolderDebugMap = new NewsSet();
		dynamicMenuItemsHolderProductionMap = new NewsSet();
		mainScreenItems = new NewsSet();
	}

	public static DataStore getInstance()
	{
		if (theInstance == null)
		{
			theInstance = new DataStore();
		}
		return theInstance;
	}
	
	public NewsSet getMainScreenItems()
	{
		return mainScreenItems;
	}

	public void setMainScreenItems(NewsSet mainScreenItems)
	{
		this.mainScreenItems = mainScreenItems;
	}

	// *** Getters/Setters ***
	public NewsSet getDynamicMenuItemsHolderDebugMap()
	{
		return dynamicMenuItemsHolderDebugMap;
	}

	public void setDynamicMenuItemsHolderDebugMap(NewsSet dynamicMenuItemsHolder)
	{
		this.dynamicMenuItemsHolderDebugMap = dynamicMenuItemsHolder;
	}

	public NewsSet getDynamicMenuItemsHolderProductionMap()
	{
		return dynamicMenuItemsHolderProductionMap;
	}

	public void setDynamicMenuItemsHolderProductionMap(NewsSet dynamicMenuItemsHolder)
	{
		this.dynamicMenuItemsHolderProductionMap = dynamicMenuItemsHolder;
	}

	public int getScreenWidth()
	{
		return screenWidth;
	}

	public void setScreenWidth(int screenWidth)
	{
		this.screenWidth = screenWidth;
	}

	public int getScreenHeight()
	{
		return screenHeight;
	}

	public void setScreenHeight(int screenHeight)
	{
		this.screenHeight = screenHeight;
	}

	public String getDeviceModel()
	{
		return deviceModel;
	}

	public void setDeviceModel(String deviceModel)
	{
		this.deviceModel = deviceModel;
	}

	public ArrayList<ArticleSmallOpinion> getArticleSmallOpinion()
	{
		return ArticleSmallOpinion;
	}

	public void setArticleSmallOpinion(ArrayList<ArticleSmallOpinion> articleSmallOpinion)
	{
		ArticleSmallOpinion = articleSmallOpinion;
	}

	public boolean showAdMob()
	{
		return showAdMob;
//		return true;

	}

	public void setShowAdMob(boolean showAdMob)
	{
		Log.e("alex", "setShowAdMob: " + showAdMob);
		this.showAdMob = showAdMob;
	}

	public int getCommentPos()
	{
		return commentPos;
	}

	public void setCommentPos(int commentPos)
	{
		this.commentPos = commentPos;
	}

	public String getAppVersion()
	{
		return appVersion;
	}

	public void setAppVersion(String appVersion)
	{
		this.appVersion = appVersion;
	}

	public ArrayList<Article> getDocuments()
	{
		return documents;
	}

	public void setDocuments(ArrayList<Article> documents)
	{
		this.documents = documents;
	}

	public String getLiveBoxMainURL()
	{
		return liveBoxMainURL;
	}

	public void setLiveBoxMainURL(String liveBoxMainURL)
	{
		this.liveBoxMainURL = liveBoxMainURL;
	}

	public String getLiveBoxArticleURL()
	{
		return liveBoxArticleURL;
	}

	public void setLiveBoxArticleURL(String liveBoxArticleURL)
	{
		this.liveBoxArticleURL = liveBoxArticleURL;
	}
	
	public boolean getSplashScreenClosed()
	{
		return isSplashScreenClosed;
	}

	public void setSplashScreenClosed(boolean isSplashScreenClosed)
	{
		this.isSplashScreenClosed = isSplashScreenClosed;
	}


	

}
