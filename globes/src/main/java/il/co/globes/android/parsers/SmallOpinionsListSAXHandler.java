package il.co.globes.android.parsers;

import il.co.globes.android.objects.ArticleSmallOpinion;
import il.co.globes.android.objects.NewsSet;
import il.co.globes.android.objects.Tagit;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.List;

public class SmallOpinionsListSAXHandler extends DefaultHandler {

    // ===========================================================
    // Fields
    // ===========================================================

    // article
    private ArticleSmallOpinion curArticle;

    // tagiot
    private List<Tagit> curTagiot;

    private NewsSet newsSet;

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    public NewsSet getParsedData() {
        return this.newsSet;
    }

    // ===========================================================
    // Methods
    // ===========================================================
    @Override
    public void startDocument() throws SAXException {
        this.newsSet = new NewsSet();
    }

    /**
     * Gets be called on opening tags like: <tag> Can provide attribute(s), when
     * xml was like: <tag attribute="attributeValue">
     */
    @Override
    public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
        // <document>
        if (localName.equals("document")) {
            curArticle = new ArticleSmallOpinion();
            curTagiot = new ArrayList<Tagit>();

            // add values
            if (atts.getValue("doc_id") != null) curArticle.setDoc_id(atts.getValue("doc_id"));
            if (atts.getValue("title") != null) curArticle.setTitle(atts.getValue("title"));
            if (atts.getValue("sub_title") != null) curArticle.setSubTitle(atts.getValue("sub_title"));
            if (atts.getValue("f7") != null) curArticle.setF7(atts.getValue("f7"));
            String author = atts.getValue("author_icon");
            author = extractImageUrl(author);
            if (atts.getValue("author_icon") != null) curArticle.setAuthorImgURL(author);
            if (atts.getValue("Created_On") != null) curArticle.setCreatedOn(atts.getValue("Created_On"));

        }
        // <f2>
        else if (localName.equals("f2")) {
            if (atts.getValue("src") != null) curArticle.setF2(atts.getValue("src"));

            // currently not holding image photographer via "title="
        }
        // <f3>
        if (localName.equals("f3")) {
            if (atts.getValue("src") != null) curArticle.setF3(atts.getValue("src"));
            // currently not holding image photographer via "title="
        }
        // <f4>
        else if (localName.equals("f4")) {
            if (atts.getValue("src") != null) curArticle.setF4(atts.getValue("src"));
            // currently not holding image photographer via "title="
        }
        // <tagit>
        else if (localName.equals("tagit")) {
            Tagit tagit = new Tagit(atts.getValue("id"), atts.getValue("simplified"), "");
            curTagiot.add(tagit);
        }

    }

    /**
     * Gets be called on closing tags like: </tag>
     */
    @Override
    public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
        if (localName.equals("document")) {
            // add tagiot to article
            curArticle.setTagiot(curTagiot);

            // add article to newSet
            newsSet.addItem(curArticle);
        }
    }

    /**
     * Gets be called on the following structure: <tag>characters</tag>
     */
    @Override
    public void characters(char ch[], int start, int length) {

    }

    private String extractImageUrl(String imageUrl) {
        String parsedImageUrl = "";
        //Log.e("alex", "ExtractImageUrl: " + imageUrl);

        if (imageUrl != null) {
            String extension = null;
            String[] extensions = {"gif", "jpg"};
            int xCount = extensions.length;

            for (int i = 0; i < xCount; i++) {
                if (imageUrl.contains("." + extensions[i])) {
                    extension = "." + extensions[i];
                    // BUG FIX for URLs like "http://images.globes.co.il/images/NewGlobes/big_image_800/2016/h02_bursa.jpg-800.jpg"
                    parsedImageUrl = imageUrl.substring(imageUrl.indexOf("http:"), imageUrl.lastIndexOf(extension)) + extension;

                    break;
                }
            }
        }
        return parsedImageUrl;
    }
}
