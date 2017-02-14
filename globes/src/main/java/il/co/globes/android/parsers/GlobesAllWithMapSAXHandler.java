package il.co.globes.android.parsers;

import il.co.globes.android.DataStore;
import il.co.globes.android.Definitions;
import il.co.globes.android.objects.Article;
import il.co.globes.android.objects.ArticleEmphasized;
import il.co.globes.android.objects.ArticleOpinion;
import il.co.globes.android.objects.Banner;
import il.co.globes.android.objects.DfpAdHolder;
import il.co.globes.android.objects.GlobesURL;
import il.co.globes.android.objects.Group;
import il.co.globes.android.objects.HeaderBanner;
import il.co.globes.android.objects.InnerGroup;
import il.co.globes.android.objects.NewCustomMenuItem;
import il.co.globes.android.objects.NewsSet;
import il.co.globes.android.objects.Stock;
import il.co.globes.android.objects.Tagit;

import java.util.Vector;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.google.android.gms.ads.AdSize;

import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

public class GlobesAllWithMapSAXHandler extends DefaultHandler
{
    private Vector<Object> vectorForExtra;

    // ===========================================================
    // Fields
    // ===========================================================

    // indicators
    boolean in_debug_group = false;
    boolean in_production_group = false;
    boolean in_wsurl = false;

    // data
    private HashMap<String, String> mapProductionURLs;
    private HashMap<String, String> mapDebugURLs;
    private NewsSet newsNodesDebug, newsNodesProduction;
    private DataStore dataStore;

    // ===========================================================
    // Fields for Main Screen Items
    // ===========================================================

    // private boolean in_Object = false;
    private boolean in_author_icon = false;
    // Fields for TVTab
    private boolean in_Title = false;
    private boolean in_clipurl = false;
    private boolean in_imageurl = false;
    private boolean in_f2 = false;
    private boolean in_f3 = false;
    private boolean in_f4 = false;
    //private boolean in_f11 = false;

    // html 5 url for TV list
    private boolean in_app_url = false;
    // html 5 indicator whether to use the app_url or f4 for video
    private boolean in_html5 = false;

    // Fields for stocks from mobileapi.tapuz.co.il
    private boolean in_symbol = false;
    private boolean in_name_he = false;
    private boolean in_name_en = false;
    private boolean in_instrumentid = false;
    private boolean in_percentagechange = false;

    // Fields for dfp katava pirsumit
    private boolean in_inner_group_nadlan_tashtiot = false;
    private boolean in_main_group_no_title = false;
    private boolean in_inner_group_tv = false;
    private int counter_main_articles = 0;

    // *** F3 F41 ***
    // current article avoiding class cast exception
    private Article curArticle;
    private ArticleEmphasized curArticleEmphasized;
    private ArticleOpinion curArticleOpinion;

    // differentiate the current article for F3 and F41 tags
    private boolean in_article = false;
    private boolean in_emphasized_article = false;
    private boolean in_article_Opinion = false;

    // indicators to map Big Articles
    private boolean in_group = false;
    private boolean in_inner_group = false;
    private boolean isFirstArticle = false;
    protected NewsSet newsSet;
    // ===========================================================
    // Getter & Setter
    // ===========================================================
    private boolean deotAdInEnd;
    private boolean hotStoryAdInEnd;
    private boolean hotStoryAdStrip = false;
    private String curTitle = "";
    // TODO eli
    private String tilteForTheTagit;
    private Article extra;
    private boolean inExtra;

    private boolean isHeaderBannerAdded = false;

    private int iMainMadorSponsoredArticlesCounter = 0;

    Map<String, String> dicRightMenuItems;

    @Override
    public void startDocument() throws SAXException
    {
        super.startDocument();
        this.mapProductionURLs = new HashMap<String, String>();
        this.mapDebugURLs = new HashMap<String, String>();
        this.newsNodesDebug = new NewsSet();
        this.newsNodesProduction = new NewsSet();
        this.newsSet = new NewsSet();
        vectorForExtra = new Vector<Object>();

        dataStore = DataStore.getInstance();

        dicRightMenuItems = new HashMap<String, String>();
    }

    @Override
    public void endDocument() throws SAXException
    {
        super.endDocument();

        // iterate over map and add values to GlobesURL accrording to the
        // current mode Debug/Production
        Iterator<Entry<String, String>> it;
        if (Definitions.MapURLMode == Definitions.MAP_URL_MODE_DEBUG)
        {
            it = this.mapDebugURLs.entrySet().iterator();
        }
        else
        {
            it = this.mapProductionURLs.entrySet().iterator();
        }
        while (it.hasNext())
        {
            Map.Entry<String, String> pairs = it.next();
            GlobesURL.addValueByName(pairs.getKey(), pairs.getValue());
        }

        if (Definitions.MapURLMode == Definitions.MAP_URL_MODE_DEBUG)
        {

            dataStore.setDynamicMenuItemsHolderDebugMap(this.newsNodesDebug);
        }
        else
        {
            dataStore.setDynamicMenuItemsHolderProductionMap(this.newsNodesProduction);
        }

        Group toRemove = new Group("מדורים");
        newsSet.itemHolder.removeElement(toRemove);

        Group toReplace = new Group("דעות");
        InnerGroup toReplaceWith = new InnerGroup("דעות");

        int indexToReplace = newsSet.itemHolder.indexOf(toReplace);
        int indexToReplaceWith = newsSet.itemHolder.indexOf(toReplaceWith);
        if (indexToReplace != -1 && indexToReplaceWith != -1)
        {
            Object objectToReplaceWith = newsSet.itemHolder.get(indexToReplaceWith);
            newsSet.itemHolder.remove(indexToReplaceWith);
            newsSet.itemHolder.set(indexToReplace, objectToReplaceWith);
        }

        //5 from saveThenewsSet 2 from vectorForExtra
        if (vectorForExtra.size() == 1)
        {
            newsSet.itemHolder.add(5, vectorForExtra.get(0));
        }
        else
        {
            int j = 0;
            for (int i = 0; i < vectorForExtra.size()-1; i+=2)
            {
                Object current1 = vectorForExtra.get(i);
                Object current2 = vectorForExtra.get(i+1);
                if ((5 + 7*i) < newsSet.itemHolder.size() && (6 + 7*i) < newsSet.itemHolder.size())
                {
                    newsSet.itemHolder.add(5 + 7 * j, current1);
                    newsSet.itemHolder.add(6 + 7 * j, current2);
                    j++;
                }
            }
        }

        dataStore.setMainScreenItems(newsSet);

    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
    {
        //Log.e("alex", "WWWWWWWWWWWWWWW:" + uri + "===" + localName + "===" + qName);




        if(!isHeaderBannerAdded)
        {
            //Log.e("alex" , "ADDDDDDD 1: ");
            //newsSet.addItem(new HeaderBanner());
            newsSet.addItem(new DfpAdHolder(new AdSize(320, 50), Definitions.ADUNIT_MAIN_LIST_BETWEEN_ARTICELS, DfpAdHolder.TYPE_DFP_MAIN_LIST_BETWEEN_ARTICLES));
            isHeaderBannerAdded = true;
        }


        if (localName.equals("group") && !TextUtils.isEmpty(attributes.getValue("name"))
                && (attributes.getValue("name")).equals("wsurl_debug"))
        {
            in_debug_group = true;
            //Log.e("alex","Android Version ID: " + attributes.getValue("android"));
            dataStore.setAppVersion(attributes.getValue("android"));
        }
        else if (localName.equals("group") && !TextUtils.isEmpty(attributes.getValue("name"))
                && (attributes.getValue("name")).equals("wsurl_prod"))
        {
            in_production_group = true;
            //Log.e("alex","Android Version ID: " + attributes.getValue("android"));
            dataStore.setAppVersion(attributes.getValue("android"));

        }
        else if (localName.equals("wsurl"))
        {
            in_wsurl = true;
            if (!attributes.getValue("name").equals("financeLinks") || !attributes.getValue("name").equals("newsNodes"))
            {
                // add the attrs to the map of URLs
                addWSURLtagToMap(attributes);
            }
            if (attributes.getValue("name").equals("AdsPreRoll"))
            {
                // TODO eli add the toUseGIMA
                String link = attributes.getValue("link");

                Definitions.toUseGIMA = link.compareTo("true") == 0;

                Log.e("alex", "AdsPreRoll: " + Definitions.toUseGIMA);


                // if (Build.VERSION.SDK_INT == 16)
                // {
                // Definitions.toUseGIMA = false;
                // }
            }

            if (attributes.getValue("name").equals("AdsPreRollUrl"))
            {
                String link = attributes.getValue("link");
                if(link != "")
                {
                    Definitions.AdsPreRollUrl = link;
                }
            }

            if (attributes.getValue("name").equals("AdsIMAPreRollUrl"))
            {
                String link = attributes.getValue("link");
                if(link != "")
                {
                    Definitions.AdsIMAPreRollUrl = link;
                }
            }


            if (attributes.getValue("name").equals("DocumentUrlJson"))
            {
                String link = attributes.getValue("link");
                if(link != "")
                {
                    Definitions.DocumentUrlJson = link + GlobesURL.URLDocumentJson;
                }
            }

            if (attributes.getValue("name").equals("MaavaronWaitingTime"))
            {
                String link = attributes.getValue("link");
                Definitions.TIME_MAAVARON = Integer.valueOf(link); //120;
            }

            if (attributes.getValue("name").equals("pm_livebox_HP"))
            {
                String link = attributes.getValue("link");
                Definitions.toUseLiveBoxMain = link.compareTo("true") == 0;
            }

            if (attributes.getValue("name").equals("outbrain"))
            {
                // TODO eli add the toUseOutbrain
                String link = attributes.getValue("link");
                Definitions.toUseOutbrain = false;

                // Definitions.toUseOutbrain = link.compareTo("true")==0;
            }

            if (attributes.getValue("name").equals("taboola"))
            {
                String link = attributes.getValue("link");
                Definitions.toUseTaboola = link.compareTo("true")==0;
            }

            if(attributes.getValue("name").equals("wallafusion"))
            {
                String link = attributes.getValue("link");
                Definitions.wallafusion = false; /////////////////////////////// link.compareTo("true")==0;
            }

            if(attributes.getValue("name").equals("SpotImID"))
            {
                String link = attributes.getValue("link");
                Definitions.spotImID = link;
            }

            if(attributes.getValue("name").equals("SpotImIsEnabled"))
            {
                String link = attributes.getValue("link");
                Definitions.spotImEnabled = link.compareTo("true")==0;  //true;
            }

            if (attributes.getValue("name").equals("AdsMode"))
            {
                // TODO eli add the toUseAjillion
                String link = attributes.getValue("link");
                Log.e("eli", "map : name=" + attributes.getValue("name") + ", link=" + link);
                if (link.compareTo("1") == 0)
                {
                    Definitions.toUseAjillion = false;
                }
                else
                {
                    Definitions.toUseAjillion = false; ///////////////////////////////////////true;
                }
            }

            else if (attributes.getValue("name").equals("dfp_articles_template_id")) {
                Definitions.dfp_articles_template_id = attributes.getValue("link");
            }
            else if (attributes.getValue("name").equals("dfp_article_hotstories")) {
                Definitions.ADUNIT_KATAVA_PIRSUMIT_MAIN_LIST_HOT_STORY = attributes.getValue("link");
            }
            else if (attributes.getValue("name").equals("dfp_article_nadlan")) {
                Definitions.ADUNIT_KATAVA_PIRSUMIT_NADLAN_LIST = attributes.getValue("link");
            }
            else if (attributes.getValue("name").equals("dfp_article_rashi1")) {
                Definitions.ADUNIT_KATAVA_PIRSUMIT_MAIN_LIST = attributes.getValue("link");
            }
            else if (attributes.getValue("name").equals("dfp_article_rashi2")) {
                Definitions.ADUNIT_KATAVA_PIRSUMIT_MAIN_LIST_2 = attributes.getValue("link");
            }
            else if (attributes.getValue("name").equals("dfp_sekindo")) {
                Definitions.dfp_sekindo = attributes.getValue("link");
            }
            else if (attributes.getValue("name").equals("dfp_floating")) {
                Definitions.dfp_floating = attributes.getValue("link");
            }
            else if (attributes.getValue("name").equals("dfp_floating_hp")) {
                Definitions.dfp_floating_hp = attributes.getValue("link");
            }
            else if (attributes.getValue("name").equals("dfp_interstitial_main")) {
                Definitions.dfp_interstitial_main = attributes.getValue("link");
            }
            else if (attributes.getValue("name").equals("dfp_interstitial_article")) {
                Definitions.dfp_interstitial_article = attributes.getValue("link");
            }
            else if (attributes.getValue("name").equals("dfp_king_slider")) {
                Definitions.ADUNIT_MAIN_LIST_BETWEEN_ARTICELS = attributes.getValue("link");
            }
            else if (attributes.getValue("name").equals("dfp_king_slider_hp")) {
                Definitions.ADUNIT_MAIN_LIST_BETWEEN_ARTICELS_HP = attributes.getValue("link");
            }
            else if (attributes.getValue("name").equals("dfp_strip_vertical")) {
                Definitions.dfp_strip_vertical = attributes.getValue("link");
            }

            if(attributes.getValue("name").equals("adKatavaPirsumitMain1AfterArticle"))
            {
                String link = attributes.getValue("link");
                int pos = Integer.parseInt(link);
                Definitions.adKatavaPirsumitMain1AfterArticle = pos;
            }

            if(attributes.getValue("name").equals("adKatavaPirsumitMain2AfterArticle"))
            {
                String link = attributes.getValue("link");
                int pos = Integer.parseInt(link);
                Definitions.adKatavaPirsumitMain2AfterArticle = pos;
            }

            if(attributes.getValue("name").equals("adKatavaPirsumitMain1Pos"))
            {
                String link = attributes.getValue("link");
                int pos = Integer.parseInt(link);
                Definitions.adKatavaPirsumitMain1Pos = pos;
            }

            if(attributes.getValue("name").equals("adKatavaPirsumitMain2Pos"))
            {
                String link = attributes.getValue("link");
                int pos = Integer.parseInt(link);
                Definitions.adKatavaPirsumitMain2Pos = pos;
            }

            if(attributes.getValue("name").equals("adKatavaPirsumitHotStoryPos"))
            {
                String link = attributes.getValue("link");
                int pos = Integer.parseInt(link);
                Definitions.adKatavaPirsumitHotStoryPos = pos;
            }

            if(attributes.getValue("name").equals("adKatavaPirsumitRealEstatePos"))
            {
                String link = attributes.getValue("link");
                int pos = Integer.parseInt(link);
                Definitions.adKatavaPirsumitRealEstatePos = pos;
            }

            if(attributes.getValue("name").equals("OpenVideoAsArticle"))
            {
                String link = attributes.getValue("link");
                Definitions.isOpenVideoAsArticle = link.compareTo("true") == 0;
            }
        }

        else if (localName.equals("link"))
        {
            NewCustomMenuItem customMenuItem = new NewCustomMenuItem();
            if (attributes.getValue("name") != null) customMenuItem.setName(attributes.getValue("name").replaceAll("null", "").trim());
            if (attributes.getValue("href") != null) customMenuItem.setHref(attributes.getValue("href").replaceAll("null", "").trim());
            customMenuItem.setFinancialLinkItem(true);

            if (in_debug_group)
            {

                this.newsNodesDebug.addItem(customMenuItem);
            }
            else if (in_production_group)
            {
                if(!dicRightMenuItems.containsKey(attributes.getValue("name").replaceAll("null", "").trim()))
                {
                    //Log.e("alex","AAAAAAAAAAAAAAAAAAAAA");
                    dicRightMenuItems.put(attributes.getValue("name").replaceAll("null", "").trim(), attributes.getValue("href").replaceAll("null", "").trim());
                    //Log.e("alex","ITTTTEM: " + attributes.getValue("name"));

                    this.newsNodesProduction.addItem(customMenuItem);
                }
            }

        }
        else if (localName.equals("node"))
        {
            NewCustomMenuItem customMenuItem = new NewCustomMenuItem();

            String curr_name = attributes.getValue("name").replaceAll("null", "").trim();
            String curr_id = attributes.getValue("id").replaceAll("null", "").trim();

            if (attributes.getValue("name") != null) customMenuItem.setName(curr_name);
            if (attributes.getValue("id") != null) customMenuItem.setId(curr_id);

            if (in_debug_group)
            {
                this.newsNodesDebug.addItem(customMenuItem);
            }
            else if (in_production_group)
            {

                if(!dicRightMenuItems.containsKey(curr_name))
                {
                    //Log.e("alex","AAAAAAAAAAAAAAAAAAAAA");
                    dicRightMenuItems.put(curr_name, curr_id);
                    this.newsNodesProduction.addItem(customMenuItem);
                }

                //this.newsNodesProduction.addItem(customMenuItem);
            }
        }
        else if (localName.equals("nodeWeb"))
        {
            NewCustomMenuItem customMenuItem = new NewCustomMenuItem();

            String curr_name = attributes.getValue("name").replaceAll("null", "").trim();
            String curr_href = attributes.getValue("href").replaceAll("null", "").trim();

            if (attributes.getValue("name") != null) customMenuItem.setName(curr_name);
            if (attributes.getValue("href") != null) customMenuItem.setHref(curr_href);
            if (attributes.getValue("image") != null) customMenuItem.setImgURL(attributes.getValue("image").replaceAll("null", "").trim());
            customMenuItem.setIsNodeWebItem(true);

            if (in_debug_group)
            {
                this.newsNodesDebug.addItem(customMenuItem);
            }
            else if (in_production_group)
            {
                if(!dicRightMenuItems.containsKey(curr_name))
                {
                    dicRightMenuItems.put(curr_name, curr_href);
                    this.newsNodesProduction.addItem(customMenuItem);
                }

                //this.newsNodesProduction.addItem(customMenuItem);
            }

        }

        else if (qName.equals("clip"))
        {
            inExtra = true;
            extra = new Article();
            if (attributes.getValue("created_on") != null) extra.setCreatedOn(attributes.getValue("created_on"));
            if (attributes.getValue("canonical_folder") != null) extra.setCanonicalDynastyIds(attributes.getValue("canonical_folder"));
            if (attributes.getValue("canonical_dynasty_ids") != null) extra.setCanonicalDynastyIds(attributes.getValue("canonical_dynasty_ids"));
            if (attributes.getValue("doc_id") != null) extra.setDoc_id(attributes.getValue("doc_id"));
            if (attributes.getValue("title") != null) extra.setTitle(attributes.getValue("title"));
            if (attributes.getValue("url") != null) extra.setUrl(attributes.getValue("url"));
            else if (attributes.getValue("clip_url") != null) extra.setUrl(attributes.getValue("clip_url"));
            if (attributes.getValue("emphasized") != null) extra.setEmphasized(attributes.getValue("emphasized"));
            if (attributes.getValue("url-ad") != null) extra.setUrlMarketingArticle(attributes.getValue("url-ad"));
            if (attributes.getValue("app_url") != null) extra.setUrlHTML5(attributes.getValue("app_url"));
            if (attributes.getValue("html5") != null) extra.setHTML5video(attributes.getValue("html5"));
            if (attributes.getValue("image") != null) extra.setImage(attributes.getValue("image"));
        }
        else if (localName.equals("group"))
        {
            in_group = true;
            // indicate the first article is to be big
            isFirstArticle = true;
            String title = attributes.getValue("title");
            if (title != null)
            {
                tilteForTheTagit = title;
                curTitle  = title;
                in_main_group_no_title = false;
                Group group = new Group();
                group.setTitle(title);

                // add banner type
                if (title.equals("דעות"))
                {
                    deotAdInEnd = true;
                }
                if (title.equals("הסיפורים החמים"))
                {
                    hotStoryAdInEnd = true;

                    if(!hotStoryAdStrip)
                    {
                        newsSet.addItem(new DfpAdHolder(new AdSize(320, 50), Definitions.ADUNIT_MAIN_LIST_BETWEEN_ARTICELS,	DfpAdHolder.TYPE_DFP_MAIN_LIST_BETWEEN_ARTICLES));
                        hotStoryAdStrip = true;
                    }
                }
                newsSet.addItem(group);
                // add banner as first cell inside נדל"ן ותשתיות
                if (title != null && title.length() > 0 && title.equals("נדל\"ן ותשתיות"))
                {
                    // indicate that we entered נדל"ן ותשתיות so we can add the
                    // katava pirsumit at the end
                    in_inner_group_nadlan_tashtiot = true;
                }
            }
            else
            {
                tilteForTheTagit = "ראשי";
                // count 6 articles and place a katava pirsumit
                in_main_group_no_title = true;
            }
        }
        else if (localName.equals("inner_group"))
        {
            in_inner_group = true;

            String title = attributes.getValue("title");
            curTitle  = title;

            tilteForTheTagit = title;
            if (!TextUtils.isEmpty(title) && !title.equals("גלובס TV"))
            {
                isFirstArticle = true;
            }
            if (title != null && title.length() > 0 && title.equals("וול סטריט ושוקי עולם"))
            {
                // add Banner this is a test add this row back
                //newsSet.addItem(new DfpAdHolder(new AdSize(320, 50), Definitions.ADUNIT_MAIN_LIST_BETWEEN_ARTICELS, DfpAdHolder.TYPE_DFP_MAIN_LIST_BETWEEN_ARTICLES));
            }

            InnerGroup innerGroup = new InnerGroup();
            innerGroup.setNode_id(attributes.getValue("node_id"));
            innerGroup.setTitle(attributes.getValue("title"));
            innerGroup.setShort_title(attributes.getValue("short_title"));
            newsSet.addItem(innerGroup);

            // add banner as first cell inside נדל"ן ותשתיות
            if (title != null && title.length() > 0 && title.equals("נדל\"ן ותשתיות"))
            {
                // indicate that we entered נדל"ן ותשתיות so we can add the
                // katava pirsumit at the end
                in_inner_group_nadlan_tashtiot = true;
            }

            if (title.equals("גלובס TV"))
            {
                in_inner_group_tv = true;
            }
        }
        else if (localName.equals("metzig")) {
            Log.e("alex", "metzig doc_id:" + attributes.getValue("doc_id"));

            curArticle = new Article();
            in_emphasized_article = false;
            in_article = true;
            in_article_Opinion = false;

            curArticle.isGlobesMetzig = true;
            if (attributes.getValue("sponser") != null) curArticle.setGlobesMetzigImg(attributes.getValue("sponser"));
            if (attributes.getValue("doc_id") != null) curArticle.setDoc_id(attributes.getValue("doc_id"));
            if (attributes.getValue("title") != null) curArticle.setTitle(attributes.getValue("title"));
            if (attributes.getValue("image") != null) curArticle.setImage(attributes.getValue("image"));
            if (attributes.getValue("url") != null) curArticle.setExternalURL(attributes.getValue("url"));
            if (attributes.getValue("canonical_folder") != null) curArticle.setCanonicalDynastyIds(attributes.getValue("canonical_folder"));

            Log.e("alex", "MadorTitle: " + curTitle);

            newsSet.addItem(curArticle);

        }

        else if (localName.equals("sponsored"))
        {
            curArticle = new Article();
            in_emphasized_article = false;
            in_article = true;
            in_article_Opinion = false;
            curArticle.isSponseredArticle = true;

            if (attributes.getValue("doc_id") != null) curArticle.setDoc_id(attributes.getValue("doc_id"));
            if (attributes.getValue("url") != null) curArticle.setExternalURL(attributes.getValue("url"));
            if (attributes.getValue("title") != null) curArticle.setTitle(attributes.getValue("title"));
            if (attributes.getValue("image") != null) curArticle.setImage(attributes.getValue("image"));

            newsSet.addItem(curArticle);

            if(curTitle.equals("")) //Main Mador
            {
                if(iMainMadorSponsoredArticlesCounter == 0) // כתבה פרסומית ראשונה מדור ראשי
                {
                    Definitions.isSponsoredArticleMainMadorFromGlobes_1 = true;
                }

                if(iMainMadorSponsoredArticlesCounter == 1) // כתבה פרסומית שנייה מדור רשאשי
                {
                    Definitions.isSponsoredArticleMainMadorFromGlobes_2 = true;
                }
                iMainMadorSponsoredArticlesCounter++;
            }
            if(curTitle.equals("הסיפורים החמים"))
            {
                Definitions.isSponsoredArticleStoryFromGlobes = true;
            }

            if(curTitle.equals("נדל\"ן ותשתיות"))
            {
                Definitions.isSponsoredArticleNadlanFromGlobes = true;
            }

            Log.e("alex", "MadorTitle: " + curTitle);
        }

        // Article
        else if (localName.equals("article") || localName.equals("document"))
        {
            // emphasized
            if (attributes.getValue("emphasized") != null && (attributes.getValue("emphasized").equals("true")))
            {
                curArticleEmphasized = new ArticleEmphasized();

                in_emphasized_article = true;
                in_article = false;
                in_article_Opinion = false;
                if (attributes.getValue("created_on") != null) curArticleEmphasized.setCreatedOn(attributes.getValue("created_on"));
                if (attributes.getValue("doc_id") != null) curArticleEmphasized.setDoc_id(attributes.getValue("doc_id"));
                if (attributes.getValue("title") != null) curArticleEmphasized.setTitle(attributes.getValue("title"));
                if (attributes.getValue("image") != null) curArticleEmphasized.setImage(attributes.getValue("image"));
                if (attributes.getValue("url") != null)
                    curArticleEmphasized.setUrl(attributes.getValue("url"));
                else if (attributes.getValue("clip_url") != null) curArticleEmphasized.setUrl(attributes.getValue("clip_url"));
                if (attributes.getValue("emphasized") != null) curArticleEmphasized.setEmphasized(attributes.getValue("emphasized"));
                if (attributes.getValue("has_clip") != null) curArticleEmphasized.setHasClip(attributes.getValue("has_clip"));

                // katava pirsumit
                if (attributes.getValue("url-ad") != null) curArticleEmphasized.setUrlMarketingArticle(attributes.getValue("url-ad"));
                if (attributes.getValue("app_url") != null) curArticleEmphasized.setUrlHTML5(attributes.getValue("app_url"));
                if (attributes.getValue("html5") != null) curArticleEmphasized.setHTML5video(attributes.getValue("html5"));
                newsSet.addItem(curArticleEmphasized);
            }
            // opinion
            else if (attributes.getValue("author") != null || attributes.getValue("author_node") != null)
            {
                curArticleOpinion = new ArticleOpinion();

                in_emphasized_article = false;
                in_article = false;
                in_article_Opinion = true;

                if (attributes.getValue("created_on") != null) curArticleOpinion.setCreatedOn(attributes.getValue("created_on"));
                if (attributes.getValue("doc_id") != null) curArticleOpinion.setDoc_id(attributes.getValue("doc_id"));
                if (attributes.getValue("title") != null) curArticleOpinion.setTitle(attributes.getValue("title"));
                if (attributes.getValue("image") != null) curArticleOpinion.setImage(attributes.getValue("image"));
                if (attributes.getValue("author") != null) curArticleOpinion.setAuthorName(attributes.getValue("author"));
                if (attributes.getValue("url") != null)
                    curArticleOpinion.setUrl(attributes.getValue("url"));
                else if (attributes.getValue("clip_url") != null) curArticleOpinion.setUrl(attributes.getValue("clip_url"));
                if (attributes.getValue("emphasized") != null) curArticleOpinion.setEmphasized(attributes.getValue("emphasized"));
                newsSet.addItem(curArticleOpinion);
            }
            else
            {
                curArticle = new Article();
                // eli add parser
                curArticle.setGroup(curTitle);
                // set as big article
                if (isFirstArticle)
                {
                    isFirstArticle = false;
                    curArticle.setBigArticle(true);
                }

                in_emphasized_article = false;
                in_article = true;
                in_article_Opinion = false;
                if (attributes.getValue("created_on") != null) curArticle.setCreatedOn(attributes.getValue("created_on"));
                if (attributes.getValue("canonical_folder") != null) curArticle.setCanonicalDynastyIds(attributes.getValue("canonical_folder"));
                if (attributes.getValue("canonical_dynasty_ids") != null) curArticle.setCanonicalDynastyIds(attributes.getValue("canonical_dynasty_ids"));
                if (attributes.getValue("doc_id") != null) curArticle.setDoc_id(attributes.getValue("doc_id"));
                if (attributes.getValue("title") != null) curArticle.setTitle(attributes.getValue("title"));
                if (attributes.getValue("image") != null) curArticle.setImage(attributes.getValue("image"));
                // video url
                if (attributes.getValue("url") != null)
                    curArticle.setUrl(attributes.getValue("url"));
                else if (attributes.getValue("clip_url") != null) curArticle.setUrl(attributes.getValue("clip_url"));
                if (attributes.getValue("emphasized") != null) curArticle.setEmphasized(attributes.getValue("emphasized"));
                if (attributes.getValue("has_clip") != null) curArticle.setHasClip(attributes.getValue("has_clip"));
                // used for katava pirsumit
                if (attributes.getValue("url-ad") != null) curArticle.setUrlMarketingArticle(attributes.getValue("url-ad"));
                if (attributes.getValue("app_url") != null) curArticle.setUrlHTML5(attributes.getValue("app_url"));
                if (attributes.getValue("html5") != null) curArticle.setHTML5video(attributes.getValue("html5"));
                newsSet.addItem(curArticle);

                // Adding katava pirsumit in katava number 6
                if (in_main_group_no_title)
                {
                    counter_main_articles++;
                    if (counter_main_articles == Definitions.adKatavaPirsumitMain1AfterArticle)
                    {
                        Log.e("alex", "ADUNIT_KATAVA_PIRSUMIT_MAIN_LIST");
                        newsSet.addItem(new DfpAdHolder(new AdSize(320, 75), Definitions.ADUNIT_KATAVA_PIRSUMIT_MAIN_LIST,
                                DfpAdHolder.TYPE_DFP_KATAVA_PIRSUMIT_MAIN_LIST));
                    }

                    if(counter_main_articles == Definitions.adKatavaPirsumitMain2AfterArticle)
                    {
                        Log.e("alex", "ADUNIT_KATAVA_PIRSUMIT_MAIN_LIST 2");
                        newsSet.addItem(new DfpAdHolder(new AdSize(320, 75), Definitions.ADUNIT_KATAVA_PIRSUMIT_MAIN_LIST_2,
                                DfpAdHolder.TYPE_DFP_KATAVA_PIRSUMIT_MAIN_LIST_2));
                    }
                }
            }
        }
        else if (localName.equals("tagit"))
        {
            if (!TextUtils.isEmpty(attributes.getValue("id")) && !TextUtils.isEmpty(attributes.getValue("simplified")))
            {
                Tagit tagit = new Tagit(attributes.getValue("id"), attributes.getValue("simplified").replaceAll("_", " "), tilteForTheTagit);
                // add to article
                if (in_article)
                {
                    // add to article
                    if (in_article)
                    {
                        curArticle.getTagiot().add(tagit);
                    }
                    else if (in_article_Opinion)
                    {
                        curArticleOpinion.getTagiot().add(tagit);
                    }
                    else if (in_emphasized_article)
                    {
                        curArticleEmphasized.getTagiot().add(tagit);
                    }
                }
            }
        }
        else if (localName.equals("f3"))
        {
            in_f3 = true;
            // add big image to article
            if (in_article)
            {
                if (!TextUtils.isEmpty(attributes.getValue("src")))
                {
                    curArticle.setBigImage(attributes.getValue("src"));
                }
                // TODO added for test
                else if (!TextUtils.isEmpty(attributes.getValue("imgsrc")))
                {
                    curArticle.setBigImage(attributes.getValue("imgsrc"));
                }
            }
            else if (in_article_Opinion)
            {
                if (!TextUtils.isEmpty(attributes.getValue("src")))
                {
                    curArticleOpinion.setBigImage(attributes.getValue("src"));
                }
            }
            else if (in_emphasized_article)
            {
                if (!TextUtils.isEmpty(attributes.getValue("src")))
                {
                    curArticleEmphasized.setBigImage(attributes.getValue("src"));
                }
                // TODO added for test
                else if (!TextUtils.isEmpty(attributes.getValue("imgsrc")))
                {
                    curArticle.setBigImage(attributes.getValue("imgsrc"));
                }
            }

        }

        // Mador TV big image
        else if (inExtra && qName.equals("f20"))
        {
            if (attributes.getValue("src") != null) extra.setBigImage(attributes.getValue("src"));
            if (vectorForExtra.size()%2 ==0)
            {
                extra.setBigTVArticle(true);
            }else {
                extra.setBigTVArticle(false);
                extra.setBigArticle(false);
            }
            vectorForExtra.add(extra);
        }
        else if (localName.equals("f20"))
        {
            // add small preview image to article
            if (in_article)
            {
                if (!TextUtils.isEmpty(attributes.getValue("src")))
                {
                    curArticle.setBigImage(attributes.getValue("src"));
                }
            }
            else if (in_article_Opinion)
            {
                if (!TextUtils.isEmpty(attributes.getValue("src")))
                {
                    curArticleOpinion.setBigImage(attributes.getValue("src"));
                }
            }
            else if (in_emphasized_article)
            {
                if (!TextUtils.isEmpty(attributes.getValue("src")))
                {
                    curArticleEmphasized.setBigImage(attributes.getValue("src"));
                }
            }

        }
        else if (localName.equals("Article"))
        {
            Article article = new Article();
            newsSet.addItem(article);

        }
        else if (localName.equals("Title") || localName.equals("title"))
        {
            this.in_Title = true;

        }
        else if (localName.equals("clipurl"))
        {
            this.in_clipurl = true;
        }
        else if (localName.equals("imageurl"))
        {
            this.in_imageurl = true;

        }
        else if (localName.equals("Stock"))
        {
            Stock stock = new Stock();
            stock.setFeeder(attributes.getValue("feeder"));
            newsSet.addItem(stock);
        }
        else if (localName.equals("symbol"))
        {
            this.in_symbol = true;
        }
        else if (localName.equals("name_he"))
        {
            this.in_name_he = true;
        }
        else if (localName.equals("name_en"))
        {
            this.in_name_en = true;
        }
        else if (localName.equals("instrumentid"))
        {
            this.in_instrumentid = true;
        }
        else if (localName.equals("percentageChange"))
        {
            this.in_percentagechange = true;
        }
        else if (localName.equals("f2"))
        {
            in_f2 = true;
        }
        else if (localName.equals("f3"))
        {
            in_f3 = true;
        }
        else if (localName.equals("author_icon"))
        {
            in_author_icon = true;
        }
        else if (localName.equals("f4"))
        {
            in_f4 = true;
        }
        else if (localName.equals("app_url"))
        {
            in_app_url = true;
        }
        else if (localName.equals("html5"))
        {
            in_html5 = true;
        }

    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException
    {
        if (localName.equals("group") && in_debug_group)
        {
            in_debug_group = false;
        }
        else if (localName.equals("group") && in_production_group)
        {
            in_production_group = false;
        }
        else if (localName.equals("wsurl"))
        {
            in_wsurl = false;
        }

        else if (localName.equals("clipurl"))
        {
            this.in_clipurl = false;
        }
        else if (localName.equals("Title") || localName.equals("title"))
        {
            this.in_Title = false;
        }
        else if (localName.equals("imageurl"))
        {
            this.in_imageurl = false;
        }
        else if (localName.equals("symbol"))
        {
            this.in_symbol = false;
        }
        else if (localName.equals("name_he"))
        {
            this.in_name_he = false;
        }
        else if (localName.equals("name_en"))
        {
            this.in_name_en = false;
        }
        else if (localName.equals("instrumentid"))
        {
            this.in_instrumentid = false;
        }
        else if (localName.equals("percentageChange"))
        {
            this.in_percentagechange = false;
        }
        else if (localName.equals("f2"))
        {
            in_f2 = false;
        }
        else if (localName.equals("f3"))
        {
            in_f3 = false;
        }
        //
        //		else if (localName.equals("f11"))
        //		{
        //			in_f11 = false;
        //		}
        else if (localName.equals("author_icon"))
        {
            in_author_icon = false;
        }
        else if (localName.equals("f4"))
        {
            in_f4 = false;
        }
        else if (localName.equals("group"))
        {
            Log.e("alex", "YYYYY in_inner_group_nadlan_tashtiot: " + in_inner_group_nadlan_tashtiot);
            if (deotAdInEnd)
            {
                //newsSet.addItem(new DfpAdHolder(new AdSize(320, 50), Definitions.ADUNIT_MAIN_LIST_BETWEEN_ARTICELS,	DfpAdHolder.TYPE_DFP_MAIN_LIST_BETWEEN_ARTICLES));
                deotAdInEnd = false;
            }
            if (hotStoryAdInEnd)
            {
                newsSet.addItem(new DfpAdHolder(new AdSize(320, 75), Definitions.ADUNIT_KATAVA_PIRSUMIT_MAIN_LIST_HOT_STORY,DfpAdHolder.TYPE_DFP_KATAVA_PIRSUMIT_HOT_STORY));
                hotStoryAdInEnd = false;
            }
			/*
			if(hotStoryAdStrip)
			{
				newsSet.addItem(new DfpAdHolder(new AdSize(320, 50), Definitions.ADUNIT_MAIN_LIST_BETWEEN_ARTICELS,	DfpAdHolder.TYPE_DFP_MAIN_LIST_BETWEEN_ARTICLES));
				hotStoryAdStrip = false;
			}
			*/
            if (in_inner_group_nadlan_tashtiot)
            {
                newsSet.addItem(new DfpAdHolder(new AdSize(320, 75), Definitions.ADUNIT_KATAVA_PIRSUMIT_NADLAN_LIST,DfpAdHolder.TYPE_DFP_KATAVA_PIRSUMIT_NADLAN));
                in_inner_group_nadlan_tashtiot = false;
            }

            in_group = false;
        }
        else if (localName.equals("inner_group"))
        {
            Log.e("alex", "YYYYY inner_group");

            in_inner_group = false;

            // add katava pirsumit as last article inside נדל"ן ותשתיות
            if (in_inner_group_nadlan_tashtiot)
            {
                in_inner_group_nadlan_tashtiot = false;
                newsSet.addItem(new DfpAdHolder(new AdSize(320, 75), Definitions.ADUNIT_KATAVA_PIRSUMIT_NADLAN_LIST, DfpAdHolder.TYPE_DFP_KATAVA_PIRSUMIT_NADLAN));
            }

            if (in_inner_group_tv)
            {
                in_inner_group_tv = false;
                //newsSet.addItem(new DfpAdHolder(new AdSize(320, 50), Definitions.ADUNIT_MAIN_LIST_BETWEEN_ARTICELS, DfpAdHolder.TYPE_DFP_MAIN_LIST_BETWEEN_ARTICLES));
            }

        }
        else if (localName.equals("app_url"))
        {
            in_app_url = false;
        }
        else if (localName.equals("html5"))
        {
            in_html5 = false;
        }
    }

    /**
     * Add key = "name" value = "link" to map
     *
     * @param attributes
     *            {@link Attributes}
     */
    private void addWSURLtagToMap(Attributes attributes)
    {
        if (Definitions.MapURLMode == Definitions.MAP_URL_MODE_DEBUG)
        {
            this.mapDebugURLs.put(attributes.getValue("name"), attributes.getValue("link"));

        }
        else
        {
            this.mapProductionURLs.put(attributes.getValue("name"), attributes.getValue("link"));
        }
    }

    /**
     * Gets be called on the following structure: <tag>characters</tag>
     */
    @Override
    public void characters(char ch[], int start, int length)
    {
        if (this.in_clipurl)
        {
            ((Article) newsSet.itemHolder.lastElement()).setUrl(new String(ch, start, length));
        }
        else if (this.in_Title)
        {
            ((Article) newsSet.itemHolder.lastElement()).setTitle(new String(ch, start, length));
        }
        else if (this.in_imageurl)
        {
            ((Article) newsSet.itemHolder.lastElement()).setImage(new String(ch, start, length));
        }
        else if (this.in_symbol)
        {
            ((Stock) newsSet.itemHolder.lastElement()).setSymbol(new String(ch, start, length));
        }
        else if (this.in_name_he)
        {
            ((Stock) newsSet.itemHolder.lastElement()).setName_he(new String(ch, start, length));
        }
        else if (this.in_name_en)
        {
            ((Stock) newsSet.itemHolder.lastElement()).setName_en(new String(ch, start, length));
        }
        else if (this.in_percentagechange)
        {
            ((Stock) newsSet.itemHolder.lastElement()).setPercentage_change(new String(ch, start, length));
        }
        else if (this.in_instrumentid)
        {
            ((Stock) newsSet.itemHolder.lastElement()).setId(new String(ch, start, length));
        }
        else if (this.in_f2)
        {
            ((Article) newsSet.itemHolder.lastElement()).setImage(new String(ch, start, length));
        }
        else if (this.in_f3 || this.in_author_icon)
        {
            // if
            // (!((Article)newsSet.itemHolder.lastElement()).getImage().contains("jpg"))
            ((Article) newsSet.itemHolder.lastElement()).setImage(new String(ch, start, length));
        }
        else if (this.in_f4)
        {
            String temp = new String(ch, start, length);
            ((Article) newsSet.itemHolder.lastElement()).setUrl(temp);
        }
        else if (this.in_app_url)
        {
            // html5 video url
            String temp2 = new String(ch, start, length);
            ((Article) newsSet.itemHolder.lastElement()).setUrlHTML5(temp2);

        }
        else if (this.in_html5)
        {
            // html5 video url indicator
            String temp = new String(ch, start, length);
            ((Article) newsSet.itemHolder.lastElement()).setHTML5video(temp);
        }
    }

}