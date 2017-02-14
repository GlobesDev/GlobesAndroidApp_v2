package il.co.globes.android.objects;

import il.co.globes.android.Utils;
import il.co.globes.android.parsers.BannersSAXHandler;

import java.net.URL;

public class Banner {

	private String bannerURL;
	private String linkURL;
	
	public String getBannerURL() {
		return bannerURL;
	}
	public void setBannerURL(String bannerURL) {
		this.bannerURL = bannerURL;
	}
	public String getLinkURL() {
		return linkURL;
	}
	public void setLinkURL(String linkURL) {
		this.linkURL = linkURL;
	}
	
	
	public static Banner getBanner (URL url) throws Exception
	{
		BannersSAXHandler bannerHandler = new BannersSAXHandler();
		Utils.parseXmlFromUrlUsingHandler(url, bannerHandler);
		Banner banner = bannerHandler.getParsedData();
		return banner;
	}
	
}
