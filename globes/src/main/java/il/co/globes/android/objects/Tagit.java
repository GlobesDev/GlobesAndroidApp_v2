package il.co.globes.android.objects;

/**
 * 
 * @author Eviatar<br>
 * <br>
 *         Tagit from XML pages
 * 
 */
public class Tagit
{
	private String id;
	private String simplified;
	private String section;

	public Tagit(String id, String simplified, String section)
	{
		this.id = id;
		this.simplified = simplified;
		this.section = section;
	}

	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		this.id = id;
	}

	public String getSimplified()
	{
		// tagit name
		return simplified;
	}

	public void setSimplified(String simplified)
	{
		this.simplified = simplified;
	}

	public String getSection()
	{
		return section;
	}

	public void setSection(String section)
	{
		this.section = section;
	}



}
