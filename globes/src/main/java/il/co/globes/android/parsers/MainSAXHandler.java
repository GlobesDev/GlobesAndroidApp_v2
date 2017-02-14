package il.co.globes.android.parsers;

import java.util.Vector;

import il.co.globes.android.Definitions;
import il.co.globes.android.objects.Article;
import il.co.globes.android.objects.ArticleEmphasized;
import il.co.globes.android.objects.ArticleOpinion;
import il.co.globes.android.objects.DfpAdHolder;
import il.co.globes.android.objects.Group;
import il.co.globes.android.objects.InnerGroup;
import il.co.globes.android.objects.NewsSet;
import il.co.globes.android.objects.Stock;
import il.co.globes.android.objects.Tagit;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.google.android.gms.ads.AdSize;

import android.text.TextUtils;
import android.util.Log;

public class MainSAXHandler extends DefaultHandler
{
	private Vector<Object> vectorForExtra;

	// ===========================================================
	// Fields
	// ===========================================================

	// Fields for Sections
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
	private String curTitle = "";
	// TODO eli
	private String tilteForTheTagit;
	private Article extra;
	private boolean inExtra;
	
	private boolean isHeaderBannerAdded = false;

	public NewsSet getParsedData()
	{
		return this.newsSet;
	}

	// ===========================================================
	// Methods
	// ===========================================================
	@Override
	public void startDocument() throws SAXException
	{
		this.newsSet = new NewsSet();
		vectorForExtra = new Vector<Object>();
	}

	@Override
	public void endDocument() throws SAXException
	{

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
	}

	/**
	 * Gets be called on opening tags like: <tag> Can provide attribute(s), when
	 * xml was like: <tag attribute="attributeValue">
	 */
	@Override
	public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException
	{
		if(!isHeaderBannerAdded)
		{
			Log.e("alex" , "isHeaderBannerAdded startElement 1: ");
			//newsSet.addItem(new HeaderBanner());
			//newsSet.addItem(new DfpAdHolder(new AdSize(320, 50), Definitions.ADUNIT_MAIN_LIST_BETWEEN_ARTICELS, DfpAdHolder.TYPE_DFP_MAIN_LIST_BETWEEN_ARTICLES));
			newsSet.addItem(new DfpAdHolder(new AdSize(320, 50), Definitions.ADUNIT_MAIN_LIST_BETWEEN_ARTICELS, DfpAdHolder.TYPE_DFP_MAIN_LIST_BETWEEN_ARTICLES));
			isHeaderBannerAdded = true;
		}		
		
		if (qName.equals("clip"))
		{
			inExtra = true;
			extra = new Article();
			if (atts.getValue("created_on") != null) extra.setCreatedOn(atts.getValue("created_on"));
			if (atts.getValue("doc_id") != null) extra.setDoc_id(atts.getValue("doc_id"));
			if (atts.getValue("title") != null) extra.setTitle(atts.getValue("title"));
			if (atts.getValue("url") != null) extra.setUrl(atts.getValue("url"));
			else if (atts.getValue("clip_url") != null) extra.setUrl(atts.getValue("clip_url"));
			if (atts.getValue("emphasized") != null) extra.setEmphasized(atts.getValue("emphasized"));
			if (atts.getValue("url-ad") != null) extra.setUrlMarketingArticle(atts.getValue("url-ad"));
			if (atts.getValue("app_url") != null) extra.setUrlHTML5(atts.getValue("app_url"));
			if (atts.getValue("html5") != null) extra.setHTML5video(atts.getValue("html5"));
			if (atts.getValue("image") != null) extra.setImage(atts.getValue("image"));
		}
		else if (localName.equals("group"))
		{
			in_group = true;
			// indicate the first article is to be big
			isFirstArticle = true;
			String title = atts.getValue("title");
			if (title != null)
			{
				tilteForTheTagit = title;
				curTitle  = title;
				in_main_group_no_title = false;
				Group group = new Group();
				group.setTitle(title);
				newsSet.addItem(group);
				// add banner type
				if (title.equals("דעות"))
				{
					deotAdInEnd = true;
				}
				if (title.equals("הסיפורים החמים"))
				{
					hotStoryAdInEnd = true;
				}
				
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

			String title = atts.getValue("title");
			curTitle  = title;
			tilteForTheTagit = title;
			if (!TextUtils.isEmpty(title) && !title.equals("גלובס TV"))
			{
				isFirstArticle = true;
			}
			if (title != null && title.length() > 0 && title.equals("וול סטריט ושוקי עולם"))
			{
				// add Banner this is a test add this row back
				newsSet.addItem(new DfpAdHolder(new AdSize(320, 50), Definitions.ADUNIT_MAIN_LIST_BETWEEN_ARTICELS,
						DfpAdHolder.TYPE_DFP_MAIN_LIST_BETWEEN_ARTICLES));
			}
			InnerGroup innerGroup = new InnerGroup();
			innerGroup.setNode_id(atts.getValue("node_id"));
			innerGroup.setTitle(atts.getValue("title"));
			innerGroup.setShort_title(atts.getValue("short_title"));
			newsSet.addItem(innerGroup);

			// add banner as first cell inside נדל"ן ותשתיות
			if (title != null && title.length() > 0 && title.equals("נדל\"ן ותשתיות"))
			{
				// indicate that we entered נדל"ן ותשתיות so we can add the
				// katava pirsumit at the end
				in_inner_group_nadlan_tashtiot = true;
			}

		}
		// Article
		else if (localName.equals("article") || localName.equals("document"))
		{
			// emphasized
			if (atts.getValue("emphasized") != null && (atts.getValue("emphasized").equals("true")))
			{
				curArticleEmphasized = new ArticleEmphasized();

				in_emphasized_article = true;
				in_article = false;
				in_article_Opinion = false;
				if (atts.getValue("created_on") != null) curArticleEmphasized.setCreatedOn(atts.getValue("created_on"));
				if (atts.getValue("doc_id") != null) curArticleEmphasized.setDoc_id(atts.getValue("doc_id"));
				if (atts.getValue("title") != null) curArticleEmphasized.setTitle(atts.getValue("title"));
				if (atts.getValue("image") != null) curArticleEmphasized.setImage(atts.getValue("image"));
				if (atts.getValue("url") != null)
					curArticleEmphasized.setUrl(atts.getValue("url"));
				else if (atts.getValue("clip_url") != null) curArticleEmphasized.setUrl(atts.getValue("clip_url"));
				if (atts.getValue("emphasized") != null) curArticleEmphasized.setEmphasized(atts.getValue("emphasized"));

				// katava pirsumit
				if (atts.getValue("url-ad") != null) curArticleEmphasized.setUrlMarketingArticle(atts.getValue("url-ad"));
				if (atts.getValue("app_url") != null) curArticleEmphasized.setUrlHTML5(atts.getValue("app_url"));
				if (atts.getValue("html5") != null) curArticleEmphasized.setHTML5video(atts.getValue("html5"));
				newsSet.addItem(curArticleEmphasized);
			}
			// opinion
			else if (atts.getValue("author") != null || atts.getValue("author_node") != null)
			{
				curArticleOpinion = new ArticleOpinion();
				in_emphasized_article = false;
				in_article = false;
				in_article_Opinion = true;

				if (atts.getValue("created_on") != null) curArticleOpinion.setCreatedOn(atts.getValue("created_on"));
				if (atts.getValue("doc_id") != null) curArticleOpinion.setDoc_id(atts.getValue("doc_id"));
				if (atts.getValue("title") != null) curArticleOpinion.setTitle(atts.getValue("title"));
				if (atts.getValue("image") != null) curArticleOpinion.setImage(atts.getValue("image"));
				if (atts.getValue("author") != null) curArticleOpinion.setAuthorName(atts.getValue("author"));
				if (atts.getValue("url") != null)
					curArticleOpinion.setUrl(atts.getValue("url"));
				else if (atts.getValue("clip_url") != null) curArticleOpinion.setUrl(atts.getValue("clip_url"));
				if (atts.getValue("emphasized") != null) curArticleOpinion.setEmphasized(atts.getValue("emphasized"));
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
				if (atts.getValue("created_on") != null) curArticle.setCreatedOn(atts.getValue("created_on"));

				if (atts.getValue("doc_id") != null) curArticle.setDoc_id(atts.getValue("doc_id"));
				if (atts.getValue("title") != null) curArticle.setTitle(atts.getValue("title"));
				if (atts.getValue("image") != null) curArticle.setImage(atts.getValue("image"));
				// video url
				if (atts.getValue("url") != null)
					curArticle.setUrl(atts.getValue("url"));
				else if (atts.getValue("clip_url") != null) curArticle.setUrl(atts.getValue("clip_url"));
				if (atts.getValue("emphasized") != null) curArticle.setEmphasized(atts.getValue("emphasized"));
				// used for katava pirsumit
				if (atts.getValue("url-ad") != null) curArticle.setUrlMarketingArticle(atts.getValue("url-ad"));
				if (atts.getValue("app_url") != null) curArticle.setUrlHTML5(atts.getValue("app_url"));
				if (atts.getValue("html5") != null) curArticle.setHTML5video(atts.getValue("html5"));
				
				if (atts.getValue("canonical_folder") != null) curArticle.setCanonicalDynastyIds(atts.getValue("canonical_folder"));
				if (atts.getValue("canonical_dynasty_ids") != null) curArticle.setCanonicalDynastyIds(atts.getValue("canonical_dynasty_ids"));
												
				newsSet.addItem(curArticle);

				// Adding katava pirsumit in katava number 6
				if (in_main_group_no_title)
				{
					counter_main_articles++;
					if (counter_main_articles == 5)
					{
						newsSet.addItem(new DfpAdHolder(new AdSize(320, 75), Definitions.ADUNIT_KATAVA_PIRSUMIT_MAIN_LIST,
								DfpAdHolder.TYPE_DFP_KATAVA_PIRSUMIT_MAIN_LIST));
					}
				}
			}
		}
		else if(localName.equals("canonical_dynasty"))
		{
			if (atts.getValue("ids") != null)
			{
				try
				{
					curArticle.setCanonicalDynastyIds(atts.getValue("ids"));
				}
				catch(Exception ex){}
			}
		}
		else if (localName.equals("tagit"))
		{
			if (!TextUtils.isEmpty(atts.getValue("id")) && !TextUtils.isEmpty(atts.getValue("simplified")))
			{
				Tagit tagit = new Tagit(atts.getValue("id"), atts.getValue("simplified").replaceAll("_", " "), tilteForTheTagit);
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
				if (!TextUtils.isEmpty(atts.getValue("src")))
				{
					curArticle.setBigImage(atts.getValue("src"));
				}
				// TODO added for test
				else if (!TextUtils.isEmpty(atts.getValue("imgsrc")))
				{
					curArticle.setBigImage(atts.getValue("imgsrc"));
				}
			}
			else if (in_article_Opinion)
			{
				if (!TextUtils.isEmpty(atts.getValue("src")))
				{
					curArticleOpinion.setBigImage(atts.getValue("src"));
				}
			}
			else if (in_emphasized_article)
			{
				if (!TextUtils.isEmpty(atts.getValue("src")))
				{
					curArticleEmphasized.setBigImage(atts.getValue("src"));
				}
				// TODO added for test
				else if (!TextUtils.isEmpty(atts.getValue("imgsrc")))
				{
					curArticle.setBigImage(atts.getValue("imgsrc"));
				}
			}

		}
		//		else if (localName.equals("f11"))
		//		{
		//			this.in_f11 = true;
		//
		//			if (in_article)
		//			{
		//				if (!TextUtils.isEmpty(atts.getValue("imgsrc")))
		//				{
		//					curArticle.setImageUrlFromF11(atts.getValue("imgsrc"));
		//				}
		//			}
		//			else if (in_article_Opinion)
		//			{
		//				if (!TextUtils.isEmpty(atts.getValue("imgsrc")))
		//				{
		//					curArticle.setImageUrlFromF11(atts.getValue("imgsrc"));
		//				}
		//			}
		//			else if (in_emphasized_article)
		//			{
		//				if (!TextUtils.isEmpty(atts.getValue("imgsrc")))
		//				{
		//					curArticle.setImageUrlFromF11(atts.getValue("imgsrc"));
		//				}
		//			}
		//
		//		}
		// TODO currently not using f41
		// else if (localName.equals("f41"))
		// {
		// // add small preview image to article
		// if (in_article)
		// {
		// curArticle.setImage(atts.getValue("src"));
		// }
		// else if (in_article_Opinion)
		// {
		// curArticleOpinion.setImage(atts.getValue("src"));
		// }
		// else if (in_emphasized_article)
		// {
		// curArticleEmphasized.setImage(atts.getValue("src"));
		// }
		// }
		// Mador TV big image
		else if (inExtra && qName.equals("f20")) 
		{
			if (atts.getValue("src") != null) extra.setBigImage(atts.getValue("src"));
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
				if (!TextUtils.isEmpty(atts.getValue("src")))
				{
					curArticle.setBigImage(atts.getValue("src"));
				}
			}
			else if (in_article_Opinion)
			{
				if (!TextUtils.isEmpty(atts.getValue("src")))
				{
					curArticleOpinion.setBigImage(atts.getValue("src"));
				}
			}
			else if (in_emphasized_article)
			{
				if (!TextUtils.isEmpty(atts.getValue("src")))
				{
					curArticleEmphasized.setBigImage(atts.getValue("src"));
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
			stock.setFeeder(atts.getValue("feeder"));
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
	/**
	 * Gets be called on closing tags like: </tag>
	 */
	@Override
	public void endElement(String namespaceURI, String localName, String qName) throws SAXException
	{
		//Log.e("alex", "localName: " + localName);
		
		if (localName.equals("clipurl"))
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
			//Log.e("alex", "in_inner_group_nadlan_tashtiot: " + in_inner_group_nadlan_tashtiot);
			if (deotAdInEnd)
			{
				newsSet.addItem(new DfpAdHolder(new AdSize(320, 50), Definitions.ADUNIT_MAIN_LIST_BETWEEN_ARTICELS,	DfpAdHolder.TYPE_DFP_MAIN_LIST_BETWEEN_ARTICLES));
				deotAdInEnd = false;
			}
			if (hotStoryAdInEnd)
			{
				newsSet.addItem(new DfpAdHolder(new AdSize(320, 75), Definitions.ADUNIT_KATAVA_PIRSUMIT_MAIN_LIST_HOT_STORY,DfpAdHolder.TYPE_DFP_KATAVA_PIRSUMIT_MAIN_LIST));	
				hotStoryAdInEnd = false;
			}
			
			if (in_inner_group_nadlan_tashtiot)
			{
				newsSet.addItem(new DfpAdHolder(new AdSize(320, 75), Definitions.ADUNIT_KATAVA_PIRSUMIT_NADLAN_LIST,DfpAdHolder.TYPE_DFP_KATAVA_PIRSUMIT_NADLAN));
				in_inner_group_nadlan_tashtiot = false;
			}
			
			in_group = false;
		}
		else if (localName.equals("inner_group"))
		{
			///Log.e("alex", "inner_group");
			
			in_inner_group = false;
			
			// add katava pirsumit as last article inside נדל"ן ותשתיות
			if (in_inner_group_nadlan_tashtiot)
			{
				in_inner_group_nadlan_tashtiot = false;
				newsSet.addItem(new DfpAdHolder(new AdSize(320, 75), Definitions.ADUNIT_KATAVA_PIRSUMIT_NADLAN_LIST,DfpAdHolder.TYPE_DFP_KATAVA_PIRSUMIT_NADLAN));
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
