package il.co.globes.android.objects;

public class AjillionObj
{
	private int width;
	private int height;
	private String click_url;
	private String creative_display;
	private Boolean success;
	private String error;
	private long placement_id;
	private String creative_url;
	private String creative_type;
	private int pmodel;
	private float price;
	
	
	public AjillionObj()
	{
	}

	public AjillionObj(int width, int height, String click_url, String creative_display, Boolean success, String error, long placement_id,
			String creative_url, String creative_type, int pmodel, float price)
	{
		this.width = width;
		this.height = height;
		this.click_url = click_url;
		this.creative_display = creative_display;
		this.success = success;
		this.error = error;
		this.placement_id = placement_id;
		this.creative_url = creative_url;
		this.creative_type = creative_type;
		this.pmodel = pmodel;
		this.price = price;
	}

	@Override
	public String toString()
	{
		return "AjillionObj [width=" + width + ", height=" + height + ", click_url=" + click_url + ", creative_display=" + creative_display
				+ ", success=" + success + ", error=" + error + ", placement_id=" + placement_id + ", creative_url=" + creative_url
				+ ", creative_type=" + creative_type + ", pmodel=" + pmodel + ", price=" + price + "]";
	}

	public int getWidth()
	{
		return width;
	}

	public void setWidth(int width)
	{
		this.width = width;
	}

	public int getHeight()
	{
		return height;
	}

	public void setHeight(int height)
	{
		this.height = height;
	}

	public String getClick_url()
	{
		return click_url;
	}

	public void setClick_url(String click_url)
	{
		this.click_url = click_url;
	}

	public String getCreative_display()
	{
		return creative_display;
	}

	public void setCreative_display(String creative_display)
	{
		this.creative_display = creative_display;
	}

	public Boolean getSuccess()
	{
		return success;
	}

	public void setSuccess(Boolean success)
	{
		this.success = success;
	}

	public String getError()
	{
		return error;
	}

	public void setError(String error)
	{
		this.error = error;
	}

	public long getPlacement_id()
	{
		return placement_id;
	}

	public void setPlacement_id(long placement_id)
	{
		this.placement_id = placement_id;
	}

	public String getCreative_url()
	{
		return creative_url;
	}

	public void setCreative_url(String creative_url)
	{
		this.creative_url = creative_url;
	}

	public String getCreative_type()
	{
		return creative_type;
	}

	public void setCreative_type(String creative_type)
	{
		this.creative_type = creative_type;
	}

	public int getPmodel()
	{
		return pmodel;
	}

	public void setPmodel(int pmodel)
	{
		this.pmodel = pmodel;
	}

	public float getPrice()
	{
		return price;
	}

	public void setPrice(float price)
	{
		this.price = price;
	}
	
	
	
	
}
