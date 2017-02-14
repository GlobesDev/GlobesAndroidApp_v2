package il.co.globes.android.objects;

import android.graphics.Bitmap;

/**
 * @author Eviatar
 */
public class ArticleGalleryObject
{
	private Bitmap bitmap;
	private String imageAuthor;
	private String imageURL;
	
	public ArticleGalleryObject(String imageURL, String imageAuthor){
		this.imageAuthor = imageAuthor;
		this.imageURL = imageURL;
	}

	public Bitmap getBitmap()
	{
		return bitmap;
	}

	public void setBitmap(Bitmap bitmap)
	{
		this.bitmap = bitmap;
	}

	public String getImageAuthor()
	{
		return imageAuthor;
	}

	public void setImageAuthor(String imageAuthor)
	{
		this.imageAuthor = imageAuthor;
	}

	public String getImageURL()
	{
		return imageURL;
	}

	public void setImageURL(String imageURL)
	{
		this.imageURL = imageURL;
	}
	
	
}
