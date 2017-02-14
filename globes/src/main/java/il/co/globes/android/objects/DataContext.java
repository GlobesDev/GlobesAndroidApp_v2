package il.co.globes.android.objects;

import java.util.Hashtable;

public class DataContext {

	Hashtable<String, NewsSet> articles;
	static DataContext instance;
	String accessKey = "";
	String portfolioID = "";
	
	public String getPortfolioID()
	{
		return portfolioID;
	}

	public void setPortfolioID(String portfolioID)
	{
		this.portfolioID = portfolioID;
	}

	private DataContext()
	{
		articles = new Hashtable<String, NewsSet>();
	}
	
	public void SetArticleList(String unique, NewsSet set)
	{
		articles.put(unique, set);
	}
	
	
	
	public Hashtable<String, NewsSet> getArticles()
	{
		return articles;
	}

	public NewsSet GetArticleList(String unique)
	{
		if (articles.containsKey(unique))
			return articles.get(unique);
		
		return null;
	}
	
	public synchronized static DataContext Instance()
	{
		if (instance == null)
			instance = new DataContext();
		
		return instance;
	}

	public String getAccessKey()
	{
		return accessKey;
	}

	public void setAccessKey(String accessKey)
	{
		this.accessKey = accessKey;
	}
	
}
