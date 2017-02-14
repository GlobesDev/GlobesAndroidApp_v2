package il.co.globes.android.objects;

import android.util.Log;
import il.co.globes.android.Definitions.ArticleType;
import il.co.globes.android.Utils;
import il.co.globes.android.parsers.DocumentJsonHandler;
import il.co.globes.android.parsers.DocumentSAXHandler;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Document {
    private String modifiedOn;

    // ***Tags***
    private String title = "";
    private String subTitle = "";
    private String text = "";
    private String authorName = "";
    private String imageFromF2 = "";
    private String imageFromF3 = "";
    private String ClipURL = "";
    // Special field that contains clip url only from article type "35" regular
    // text with video inside
    private String clipURLFromF19 = "";
    private String imageUrlFromF19 = "";

    // HTML5 clip content
    private String clipUrlHTML5;
    private boolean isClipHTML5 = false;

    private String imageAuthor = "";
    private String clipImageUrl = "";
    private String videoArticleImage = "";
    private ArticleType doctype;

    private String playBuzz = "";

    private String embeddedClip = "";

    // Tagiot
    private List<Tagit> tagiot;
    private ArrayList<String> arrArticleImages = new ArrayList<String>();

    private String canonicalDynastyIds = "";
    private String canonicalFolder = "";

    public ArticleType getDoctype() {
        return doctype;
    }

    public void setDoctype(String doctype_id) {
        Log.e("eli", "doctype_id " + doctype_id);
        if (doctype_id.equals("56")) {
            this.doctype = ArticleType.VIDEO_ARTICLE;
        } else {
            this.doctype = ArticleType.TEXT_ARTICLE;
        }
    }

    /**
     * TODO this only gets the first image url , not dealing with multiple
     * images Need to create another seperated method to extract image/multiple
     * images for regular doc_type , not "56"
     *
     * @param imageUrl
     * @return clean url for image
     */
    private String extractImageUrl(String imageUrl) {
        String parsedImageUrl = "";
        //Log.e("alex", "ExtractImageUrl: " + imageUrl);

        for (String curVal : arrArticleImages) {
            if (imageUrl.contains(curVal)) {
                //Log.e("alex", "FFFFFFFFFFF: " + curVal);
                return curVal;
            }
        }

        if (imageUrl != null) {
            String extension = null;
            String[] extensions = {"gif", "jpg"};
            int xCount = extensions.length;

            for (int i = 0; i < xCount; i++) {
                if (imageUrl.contains("." + extensions[i])) {
                    extension = "." + extensions[i];
                    // BUG FIX for URLs like "http://images.globes.co.il/images/NewGlobes/big_image_800/2016/h02_bursa.jpg-800.jpg"
                    // BUT also: http://images.globes.co.il/Images/NewGlobes/NewPromo100x75/2015/na100jpg.jpg%20width=100%20height=75%20title=&quot;%D7%A0%D7%93%D7%9C&amp;quot;%D7%9F%20%D7%9E%D7%A0%D7%99%D7%91//%20%D7%A7%D7%A8%D7%93%D7%99%D7%98:%20:%20%20Shutterstock/%20%D7%90.%D7%A1.%D7%90.%D7%A4%20%D7%A7%D7%A8%D7%99%D7%99%D7%98%D7%99%D7%91&quot;%20alt=&quot;%D7%A0%D7%93%D7%9C&amp;quot;%D7%9F%20%D7%9E%D7%A0%D7%99%D7%91//%20%D7%A7%D7%A8%D7%93%D7%99%D7%98:%20:%20%20Shutterstock/%20%D7%90.%D7%A1.%D7%90.%D7%A4%20%D7%A7%D7%A8%D7%99%D7%99%D7%98%D7%99%D7%91&quot;%22/%3E%3C/Objects%3E%3CObjects%3E%3CObject%20nodeID=%222%22%20nodeName=%221376189%22%20nodeValue=%22http://images.globes.co.il/images/NewGlobes/big_image_800/2015/na800.jpg
                    int firstHttp = imageUrl.indexOf("http:");
                    int secondHttp = imageUrl.indexOf("http:", firstHttp + 1);
                    if (secondHttp > -1){
                        imageUrl = imageUrl.substring(firstHttp, secondHttp);
                    }
                    parsedImageUrl = imageUrl.substring(imageUrl.indexOf("http:"), imageUrl.lastIndexOf(extension)) + extension;
                    break;
                }
            }
        }

        //Log.e("alex", "parsedImageUrl: " + parsedImageUrl);
        arrArticleImages.add(parsedImageUrl);
        return parsedImageUrl;
    }

    public void setVideoArticleImageFromF9(String videoArticleImage) {
        this.videoArticleImage += videoArticleImage;
    }

    public String getClipImageUrl() {
        if (clipImageUrl == null) clipImageUrl = "";
        return clipImageUrl;
    }

    public void setClipImageUrl(String clipImageUrl) {
        this.clipImageUrl += clipImageUrl;
    }

    /**
     * @return a String with the photographer of a single and the first photo
     */
    public String getImageAuthor() {
        if (this.imageAuthor.contains("title=") && this.imageAuthor.contains("alt=")) {
            this.imageAuthor = this.imageAuthor.substring((this.imageAuthor.indexOf("title=") + 6), this.imageAuthor.indexOf("alt="));
            this.imageAuthor = this.imageAuthor.replaceAll("&quot;", "");
        } else {
            this.imageAuthor = "";
        }

        return this.imageAuthor;
    }

    public boolean isDeot() {
        // return (imageAuthor != null && !TextUtils.isEmpty(imageAuthor));
        return this.IsDeot;
        // return (imageAuthor != null &&
        // !TextUtils.isEmpty(imageAuthor))||(imageFromF2 != null &&
        // !TextUtils.isEmpty(imageFromF2));
    }

    // ***Fs***
    // block talkbacks if true
    private String f16;
    // block talkbacks if true
    private String f22;

    public String getImageUrlFromF19() {
        return imageUrlFromF19;
    }

    public void setImageUrlFromF19(String imageUrlFromF19) {
        this.imageUrlFromF19 = imageUrlFromF19;
    }

    public void setEmbeddedClip(String embeddedClip)
    {
        this.embeddedClip = embeddedClip;
    }

    public String getEmbeddedClip()
    {
        return embeddedClip;
    }

    // Attributes
    private String doc_id;

    private boolean IsDeot = false;

    public void setImageAuthor(String imageAuthor) {
        this.imageAuthor += imageAuthor;
    }

    public String getDoc_id() {
        return doc_id;
    }

    public void setDoc_id(String doc_id) {
        this.doc_id = doc_id;
    }

    public String getModifiedOn() {
        return modifiedOn;
    }

    public void setModifiedOn(String modifiedOn) {
        Locale.setDefault(Locale.ENGLISH);
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss 'UTC'ZZZ yyyy");
        try {
            Date date = sdf.parse(modifiedOn);

            SimpleDateFormat dateToPrint = new SimpleDateFormat("HH:mm dd/MM/yyyy");
            this.modifiedOn = dateToPrint.format(date);
        } catch (Exception e) {
            this.modifiedOn = "";
        }
    }

    public String getTitle() {
        if (title != null)
            return title;
        else return "";
    }

    public String getTitleText() {
        String title = getTitle().replaceAll("&lt;/p&gt; &lt;p&gt;", "\\n").replaceAll("<!--.*?-->", "").replaceAll("<[^>]+>", "")
                .replaceAll("&.*?;", ".");
        return title;
    }

    public void setTitle(String title) {
        this.title += title;
        // String theRawTitle = "" + title.trim();
        // Pattern pattern = Pattern.compile("\\p{InHebrew}");
        // Matcher matcher = pattern.matcher(theRawTitle);
        // String englishPrefix = "";
        // String rest = "";
        // if (matcher.find())
        // {
        // if (matcher.start() > 1)
        // {
        // englishPrefix += new String(theRawTitle.substring(0,
        // matcher.start()));
        // int idxOfColons = englishPrefix.indexOf(":");
        // if (idxOfColons != -1)
        // {
        // englishPrefix = englishPrefix.replace(":", "");
        // englishPrefix = ": " + englishPrefix;
        // }
        // rest += new String(theRawTitle.substring(matcher.start()));
        // theRawTitle = rest + " " + englishPrefix;
        //
        // }
        // }
        // this.title = theRawTitle;
    }

    public String getSubTitle() {
        if (subTitle != null) {
            return subTitle;
        }

        return "";
    }

    public String getSubTitleText() {
        return getSubTitle().replaceAll("&lt;/p&gt; &lt;p&gt;", "\\n").replaceAll("<!--.*?-->", "").replaceAll("<[^>]+>", "")
                .replaceAll("&.*?;", ".");
    }

    public void setSubTitle(String subTitle) {
        this.subTitle += subTitle;
    }

    public String getText() {
        if (text != null) {
            return text;
        } else {
            return "";
        }
    }

    public void setText(String text) {
        this.text += text;
    }

    public String getPlayBuzz() {
        if (playBuzz != null) {
            return playBuzz;
        } else {
            return "";
        }
    }

    public void setPlayBuzz(String playBuzz) {
        this.playBuzz = playBuzz;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName += authorName;
    }

    public String isF16() {
        return f16;
    }

    public void setF16(String f16) {
        this.f16 = f16;
    }

    public String getF22() {
        return f22;
    }

    public void setF22(String f22) {
        this.f22 = f22;
    }

    public String getImageFromF2() {
        String imageUrl = "";
        imageUrl = extractImageUrl(this.imageFromF2);
        return imageUrl;
    }

    public String getImageFromF9() {
        String imageUrl = "";
        imageUrl = extractImageUrl(this.videoArticleImage);
        return imageUrl;
    }

    public void setImageFromF3(String imageFromF3) {
        this.imageFromF3 += imageFromF3;
    }

    public String getImageFromF3() {
        return extractImageUrl(this.imageFromF3);
    }

    public void setImageFromF2(String image) {
        this.imageFromF2 += image;
    }

    public static Document parseDocument(URL url) throws Exception {
        DocumentSAXHandler documentHandler = new DocumentSAXHandler();
        Log.e("alex", "ParseDocument!");
        Utils.parseXmlFromUrlUsingHandler(url, documentHandler);
        Document parsedDocument = documentHandler.getParsedData();

        //Log.e("alex", "AAAAAAAAAAAAAAA: " + url);

        return parsedDocument;
    }

    public static Document parseDocumentJson(String url) throws Exception {
        if (url == null) {
            return null;
        }

        Log.e("alex", "parseDocumentJson: " + url);
        //Log.e("alex","ParsingJsonStart");

        boolean wasAttemptSuccessful = false;
        DocumentJsonHandler documentJson = new DocumentJsonHandler();
        Document parsedDocument = null;

        for (int attempt = 0; !wasAttemptSuccessful && attempt < 4; attempt++) {
            try {
                //url = url.replace("www.globes.co.il", "stage.globes.co.il");

                parsedDocument = documentJson.getParsedData(url);
                if (parsedDocument != null && parsedDocument.getTitle() != "") {
                    wasAttemptSuccessful = true;
                    //Log.e("alex","ParsingJsonAAAAA");
                }
            } catch (Exception ex) {
                Log.e("alex", "ParsingJsonError");
            }
        }

        //Log.e("alex","ParsingJsonEnd");

        //documentJson.startParsing("http://stage.globes.co.il/apps/apps.asmx/DocumentJson?doc_id=1001016669&ied=true&from=app_android&v=3");
        //documentJson.startParsing("http://stage.globes.co.il/apps/apps.asmx/DocumentJson?doc_id=1001021420&ied=true&from=app_android&v=5"); //with video

        return parsedDocument;
    }

    public String getClipURL() {

        if (ClipURL == null) {
            // if (this.clipImageUrl.toLowerCase().contains("cast-tv"))
            if (this.clipImageUrl.toLowerCase().contains("player")) {
                ClipURL = this.clipImageUrl;
            }
        }

        return ClipURL;
    }

    public void setClipURL(String clipURL) {
        ClipURL += clipURL;
    }

    // TODO redefine those
    public String getClipURLFromF19() {
        return clipURLFromF19;
    }

    public void setClipURLFromF19(String clipURLFromF19) {
        this.clipURLFromF19 += clipURLFromF19;
    }

    /**
     * Parses the F3 tag.
     *
     * @return parsed ArrayList of ArticleGalleryObject containing imageURL +
     * imageAuthor for gallery purpose
     */
    public ArrayList<ArticleGalleryObject> getGalleryObjects() {
        ArrayList<ArticleGalleryObject> arr = new ArrayList<ArticleGalleryObject>();
        String f3 = this.imageFromF3;

        String imageURL = "";
        String author = "";
        String curSubString = "";
        // parsedImageUrl = imageUrl.substring(imageUrl.indexOf("http:"),
        // imageUrl.indexOf(extension)) + extension;
        while (f3.indexOf("<Object") != -1) {
            curSubString = f3.substring(f3.indexOf("<Object"), (f3.indexOf("/><") + 2));
            f3 = f3.substring(curSubString.length());
            // extract the image url
            imageURL = extractImageUrl(curSubString);
            // extract the author name
            author = extractImageAuthor(curSubString);
            // add the results to the ArrayList<ArticleGalleryObject>
            arr.add(new ArticleGalleryObject(imageURL, author));
        }

        return arr;
    }

    public String testGetImageFromF3() {

        String f3 = this.imageFromF3;
        String imageURL = "";
        String curSubString = "";
        curSubString = f3.substring(f3.indexOf("<Object"), (f3.indexOf("/><") + 2));
        f3 = f3.substring(curSubString.length());
        imageURL = extractImageUrl(curSubString);
        return imageURL;
    }

    /**
     * @param unparsedAuthorString raw String from <object ... in F3
     * @return parsed author name
     */
    private String extractImageAuthor(String unparsedAuthorString) {
        String res = "";
        if (unparsedAuthorString.contains("title=") && unparsedAuthorString.contains("alt=")) {
            res = unparsedAuthorString.substring((unparsedAuthorString.indexOf("title=") + 6), unparsedAuthorString.indexOf("alt="));
            res = res.replaceAll("&quot;", "");
            res = res.replaceAll("&amp;", "");
            res = res.replaceAll("quot;", "\"");
        }
        return res;
    }

    public String getClipUrlHTML5() {
        return clipUrlHTML5;
    }

    public void setClipUrlHTML5(String clipUrlHTML5) {
        if (this.clipUrlHTML5 == null) {
            this.clipUrlHTML5 = "";
        }
        this.clipUrlHTML5 += clipUrlHTML5;

    }

    public boolean isClipHTML5() {
        return isClipHTML5;
    }

    public void setClipHTML5(boolean isClipHTML5) {
        this.isClipHTML5 = isClipHTML5;
    }

    public void setClipHTML5(String isClipHTML5) {
        this.isClipHTML5 = isClipHTML5.equals("true") ? true : false;
    }

    public List<Tagit> getTagiot() {
        return tagiot;
    }

    public void setTagiot(List<Tagit> tagiot) {
        this.tagiot = tagiot;
    }

    public void setIsDeot(boolean b) {
        this.IsDeot = b;
    }

    public String getCanonicalDynastyIds() {
        return this.canonicalDynastyIds;
    }

    public void setCanonicalDynastyIds(String canonicalDynastyIds) {
        this.canonicalDynastyIds = canonicalDynastyIds;
    }

    public String getCanonicalFolder() {
        return this.canonicalFolder;
    }

    public void setCanonicalFolder(String canonical_folder) {
        this.canonicalFolder = canonical_folder;
    }

}
