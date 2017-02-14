package il.co.globes.android.objects;

import android.text.TextUtils;
import net.tensera.sdk.utils.Logger;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Article implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

	/**
     * @author Eviatar<br>
     *         <br>
     *         Created for general search distinction
     */
    public enum ArticleSearchType {
        ARTICLE,
        CLIP,
        TAGIT
    }

    private String imageUrlFromF11 = "";
    private String doc_id;
    private String title = "";
    private String image = "";
    private String bigImage = "";
    private String url; // only for video links
    private String urlHTML5; // only for HTML5 video link
    private boolean isHTML5video = false; /* indicate that this is an HTML5 video */
    // emphasized
    private boolean isEmphasized = false;
    // big article
    private boolean isBigArticle = false;
    // big article
    private boolean isBigGlobesTVArticle = false;
    /* General Search fields */
    private ArticleSearchType articleSearchType;
    private String createdOn;

    private String canonicalDynastyIds;

    // only for katava pirsumit
    private String urlMarketingArticle = "";
    // Tagiot
    private List<Tagit> tagiot;

    private String group;
    private boolean hasClip = false;
	public boolean isGlobesMetzig = false;
	public boolean isSponseredArticle = false;
	private String globesMetzigImg;
	private String externalURL;

    // ctor
    public Article() {
        tagiot = new ArrayList<Tagit>();
    }

    public String getDoc_id() {
        if (doc_id != null)
            return doc_id;
        else return "";
    }

    public void setDoc_id(String doc_id) {
        this.doc_id = doc_id;
    }

    public String getTitle() {
        if (title != null)
            return title;
        else return "";
    }

    public void setTitle(String title) {
        this.title += title;
    }
	public String getGlobesMetzigImg()
	{
		return this.globesMetzigImg;	  
	}
	
	public void setExternalURL(String url)
	{
		this.externalURL = url;
	}
	
	public String getExternalURL()
	{
		return this.externalURL;	  
	}

    public String getImage() {
        if (image.contains(".gif"))
            image = image.substring(image.indexOf("http:"), image.indexOf(".gif")) + ".gif";
        else if (image.contains(".jpg")) {
//			image = image.substring(image.indexOf("http:"), image.indexOf(".jpg")) + ".jpg";
//			http://images.globes.co.il/Images/NewGlobes/NewPromo100x75/2015/na100jpg.jpg%20width=100%20height=75%20title=&quot;%D7%A0%D7%93%D7%9C&amp;quot;%D7%9F%20%D7%9E%D7%A0%D7%99%D7%91//%20%D7%A7%D7%A8%D7%93%D7%99%D7%98:%20:%20%20Shutterstock/%20%D7%90.%D7%A1.%D7%90.%D7%A4%20%D7%A7%D7%A8%D7%99%D7%99%D7%98%D7%99%D7%91&quot;%20alt=&quot;%D7%A0%D7%93%D7%9C&amp;quot;%D7%9F%20%D7%9E%D7%A0%D7%99%D7%91//%20%D7%A7%D7%A8%D7%93%D7%99%D7%98:%20:%20%20Shutterstock/%20%D7%90.%D7%A1.%D7%90.%D7%A4%20%D7%A7%D7%A8%D7%99%D7%99%D7%98%D7%99%D7%91&quot;%22/%3E%3C/Objects%3E%3CObjects%3E%3CObject%20nodeID=%222%22%20nodeName=%221376189%22%20nodeValue=%22http://images.globes.co.il/images/NewGlobes/big_image_800/2015/na800.jpg
            int firstHttp = image.indexOf("http:");
            int secondHttp = image.indexOf("http:", firstHttp + 1);
            if (secondHttp > -1){
                image = image.substring(firstHttp, secondHttp);
            }
            image = image.substring(image.indexOf("http:"), image.lastIndexOf(".jpg")) + ".jpg";
        } else if (image != null)
            ;
        else image = "";

        return image;
    }

    // test for big image
    public String getFilteredBigImage() {
        // if (bigImage.contains(".gif"))
        // bigImage = bigImage.substring(bigImage.indexOf("http:"),
        // bigImage.indexOf(".gif")) + ".gif";
        // else if (image.contains(".jpg"))
        // bigImage = bigImage.substring(bigImage.indexOf("http:"),
        // bigImage.indexOf(".jpg")) + ".jpg";
        // else if (bigImage != null)
        // ;
        // else bigImage = "";

        return bigImage;
    }

    public void setImage(String image) {
        this.image += image;
    }

    public String toString() {
        return title;
    }

    // TODO check that we are coming from video url
    public String getUrl() {
        if (url != null && (url.contains("www.cast-tv.biz") || url.contains("http://www.globes.co.il")))
            return url;
        else return null;
        // return null;
    }

    public String toString2() {
        return "Article [imageUrlFromF11=" + imageUrlFromF11 + ", doc_id=" + doc_id + ", title=" + title + ", url=" + url
                + ", isEmphasized=" + isEmphasized + ", isBigArticle=" + isBigArticle + ", isBigGlobesTVArticle=" + isBigGlobesTVArticle
                + ", articleSearchType=" + articleSearchType + ", createdOn=" + createdOn + ", tagiot=" + tagiot + "]";
    }

    public void setUrl(String url) {
        if (this.url == null) {
            this.url = "";
        }

        this.url += url;
    }

    public boolean isEmphasized() {
        return isEmphasized;
    }

    public void setEmphasized(String isEmphasized) {
        if (isEmphasized.equals("true")) {
            this.isEmphasized = true;
        }
    }

    public boolean hasClip() {
        return hasClip;
    }

    public void setHasClip(String hasClip) {
        if (!hasClip.equals("")) {
            this.hasClip = true;
        }
    }

    public String getUrlMarketingArticle() {
        return urlMarketingArticle;
    }

    public void setUrlMarketingArticle(String urlMarketingArticle) {
        this.urlMarketingArticle += urlMarketingArticle;
    }

    public String getUrlHTML5() {
        return urlHTML5;
    }

    public void setUrlHTML5(String urlHTML5) {
        if (this.urlHTML5 == null) {
            this.urlHTML5 = "";
        }

        this.urlHTML5 += urlHTML5;
    }

    public boolean isHTML5video() {
        return isHTML5video;
    }

    public void setHTML5video(boolean isHTML5video) {
        this.isHTML5video = isHTML5video;
    }
	public void setGlobesMetzigImg(String globesMetzigImg)
	{
		this.globesMetzigImg = globesMetzigImg;
	}

    public void setHTML5video(String isHTML5video) {
        this.isHTML5video = isHTML5video.equals("true") ? true : false;
    }

    public ArticleSearchType getArticleSearchType() {
        return articleSearchType;
    }

    public void setArticleSearchType(ArticleSearchType articleSearchType) {
        this.articleSearchType = articleSearchType;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }

    public String getCanonicalDynastyIds() {
        return canonicalDynastyIds;
    }

    public void setCanonicalDynastyIds(String canonicalDynastyIds) {
        this.canonicalDynastyIds = canonicalDynastyIds;
    }

    public List<Tagit> getTagiot() {
        return tagiot;
    }

    public void setTagiot(List<Tagit> tagiot) {
        this.tagiot = tagiot;
    }

    public String getBigImage() {
        return bigImage;
    }

    public void setBigImage(String bigImage) {
//		Log.e("eli", "big " + bigImage);
        this.bigImage += bigImage;
    }

    public boolean isBigArticle() {
        return isBigArticle;
    }

    public void setBigArticle(boolean isBigArticle) {
        this.isBigArticle = isBigArticle;
    }

    public boolean isBigTVArticle() {
        return isBigGlobesTVArticle;
    }

    public void setBigTVArticle(boolean isBigTVArticle) {
        this.isBigGlobesTVArticle = isBigTVArticle;
    }

    public String getImageUrlFromF11() {
        return TextUtils.isEmpty(imageUrlFromF11) ? "" : imageUrlFromF11;
    }

    public void setImageUrlFromF11(String imageUrlFromF11) {

        this.imageUrlFromF11 += imageUrlFromF11;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

}
