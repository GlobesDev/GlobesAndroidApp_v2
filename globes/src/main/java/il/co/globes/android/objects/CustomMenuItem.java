package il.co.globes.android.objects;

/**
 * 
 * @author Eviatar<br>
 * <br>
 *         custom menu item for sliding menu , holds essential data per item ,
 *         the image , text and colors resources etc
 * 
 */
public class CustomMenuItem
{
	public String name;
	public int imageResId;
	public int separatorResId;
	public int textColor;
	public String uriToParse;
	public String parser;
	public String caller;
	public boolean isDynamicItem, isNodeWeb, isFinancialLink;
	public String ImgUrl;

	// related to dynamic menu items
	public String sectionId;

	public CustomMenuItem(String itemName, int imageResId, int separatorResId, int textColor, String ImgUrl, boolean isNodeWeb,
			boolean isFinancialLink)
	{
		this.name = itemName;
		this.imageResId = imageResId;
		this.separatorResId = separatorResId;
		this.textColor = textColor;
		this.isDynamicItem = false;
		this.ImgUrl = ImgUrl;
		this.isNodeWeb = isNodeWeb;
		this.isFinancialLink = isFinancialLink;

	}

	public CustomMenuItem(String itemName, int imageResId, int separatorResId, int textColor, String uri, boolean isDynamicItem,
			String ImgUrl, boolean isNodeWeb, boolean isFinancialLink)
	{
		this.name = itemName;
		this.imageResId = imageResId;
		this.separatorResId = separatorResId;
		this.textColor = textColor;
		this.uriToParse = uri;
		this.isDynamicItem = isDynamicItem;
		this.ImgUrl = ImgUrl;
		this.isNodeWeb = isNodeWeb;
		this.isFinancialLink = isFinancialLink;

	}

	public CustomMenuItem(String itemName, int imageResId, int separatorResId, int textColor, String uri, String parser, String caller,
			String sectionId, boolean isDynamicItem, String ImgUrl, boolean isNodeWeb, boolean isFinancialLink)
	{
		this.name = itemName;
		this.imageResId = imageResId;
		this.separatorResId = separatorResId;
		this.textColor = textColor;
		this.uriToParse = uri;
		this.sectionId = sectionId;
		this.parser = parser;
		this.caller = caller;
		this.isDynamicItem = isDynamicItem;
		this.ImgUrl = ImgUrl;
		this.isNodeWeb = isNodeWeb;
		this.isFinancialLink = isFinancialLink;
	}

	public CustomMenuItem(String itemName)
	{
		this.name = itemName;

	}

	public CustomMenuItem(String itemName, int imageResId, int separatorResId, int textColor, String uri, String parser, String caller,
			boolean isDynamicItem, String ImgUrl, boolean isNodeWeb, boolean isFinancialLink)
	{
		this.name = itemName;
		this.imageResId = imageResId;
		this.separatorResId = separatorResId;
		this.textColor = textColor;
		this.uriToParse = uri;
		this.parser = parser;
		this.caller = caller;
		this.isDynamicItem = isDynamicItem;
		this.ImgUrl = ImgUrl;
		this.isNodeWeb = isNodeWeb;
		this.isFinancialLink = isFinancialLink;
	}

	@Override
	public String toString()
	{
		return "CustomMenuItem [name=" + name + ", imageResId=" + imageResId + ", separatorResId=" + separatorResId + ", textColor="
				+ textColor + ", uriToParse=" + uriToParse + ", parser=" + parser + ", caller=" + caller + ", isDynamicItem="
				+ isDynamicItem + ", sectionId=" + sectionId + "]";
	}

}
