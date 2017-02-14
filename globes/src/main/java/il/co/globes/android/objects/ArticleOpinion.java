package il.co.globes.android.objects;


public class ArticleOpinion extends Article
{
	private String authorName;
	
	
	
	public String getAuthorName() 
	{
		if (authorName==null) return "";
		else return authorName;
	}
	public void setAuthorName(String authorName) 
	{
		if (this.authorName==null)
		{
			this.authorName = authorName;
		}
		else
		{
			this.authorName += authorName;
		}
		
	}
}
