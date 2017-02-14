package il.co.globes.android.parsers;

import il.co.globes.android.objects.Article;
import il.co.globes.android.objects.NewsSet;
import il.co.globes.android.objects.Article.ArticleSearchType;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class SearchResultsSAXHandler extends DefaultHandler
{

	private NewsSet newsSet;
	// boolean articlesExist = false;
	// boolean clipsExist = false;
	// boolean tagsExist = false;

	// ===========================================================
	// Getter & Setter
	// ===========================================================

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
	}

	@Override
	public void endDocument() throws SAXException
	{
		// Nothing to do
	}

	/**
	 * Gets be called on opening tags like: <tag> Can provide attribute(s), when
	 * xml was like: <tag attribute="attributeValue">
	 */
	@Override
	public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException
	{

		if (localName.equals("article"))
		{
			Article article = createNewArticle(atts);
			article.setArticleSearchType(ArticleSearchType.ARTICLE);
			newsSet.addItem(article);
			// articlesExist = true;
		}
		else if (localName.equals("tagit"))
		{

			Article article = createNewArticle(atts);
			article.setArticleSearchType(ArticleSearchType.TAGIT);
			newsSet.addItem(article);
			// tagsExist = true;
		}
		else if (localName.equals("clip"))
		{
			Article article = createNewArticle(atts);
			article.setArticleSearchType(ArticleSearchType.CLIP);
			newsSet.addItem(article);
			// clipsExist = true;
		}
		// else if (localName.equals("articles"))
		// {
		// Group articlesGroup = new Group();
		// articlesGroup.setTitle("�����");
		// newsSet.addItem(articlesGroup);
		// }
		// else if (localName.equals("clips"))
		// {
		// Group clipsGroup = new Group();
		// clipsGroup.setTitle("������");
		// newsSet.addItem(clipsGroup);
		// }
		// else if (localName.equals("tagiot"))
		// {
		// Group tagiotGroup = new Group();
		// tagiotGroup.setTitle("�����");
		// newsSet.addItem(tagiotGroup);
		// }

	}

	@Override
	public void endElement(String namespaceURI, String localName, String qName) throws SAXException
	{

	}

	Article createNewArticle(Attributes atts)
	{
		Article article = new Article();
		if (atts.getValue("doc_id") != null) article.setDoc_id(atts.getValue("doc_id"));
		if (atts.getValue("created_on") != null) article.setCreatedOn(atts.getValue("created_on"));
		if (atts.getValue("title") != null) article.setTitle(atts.getValue("title"));
		if (atts.getValue("image") != null) article.setImage(atts.getValue("image"));
		if (atts.getValue("url") != null)
			article.setUrl(atts.getValue("url"));
		else if (atts.getValue("clip_url") != null) article.setUrl(atts.getValue("clip_url"));
		if (atts.getValue("emphasized") != null) article.setEmphasized(atts.getValue("emphasized"));
		return article;
	}
}
