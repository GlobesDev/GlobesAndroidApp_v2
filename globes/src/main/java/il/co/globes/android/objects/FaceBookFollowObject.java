package il.co.globes.android.objects;


import il.co.globes.android.UtilsFiles;
import android.content.Context;

public class FaceBookFollowObject
{
	private String htmlFollow;
	private static FaceBookFollowObject theInstance = null;
	
	
	
	
	private FaceBookFollowObject(Context context){
		
		try
		{
			this.htmlFollow = new String(UtilsFiles.readAssetsFile(context, "follow.txt"));
		}
		catch (Exception e1)
		{
			e1.printStackTrace();
		}
	}
	
	public static FaceBookFollowObject getInstance(Context context){
		if (theInstance == null)
		{
			theInstance = new FaceBookFollowObject(context.getApplicationContext());
		}
		return theInstance;
	}

	public String getHtmlFollow()
	{
		return htmlFollow;
	}

	
}
