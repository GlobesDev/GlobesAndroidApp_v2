package il.co.globes.android.parsers;

import il.co.globes.android.objects.Document;
import il.co.globes.android.objects.Tagit;

import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.text.TextUtils;
import android.util.Log;

public class DocumentSAXHandler extends DefaultHandler
{

	// ===========================================================
	// Fields
	// ===========================================================

	private boolean in_title = false;
	private boolean in_sub_title = false;
	private boolean in_text = false;
	private boolean in_writerName = false;

	/** in F2 */
	private boolean in_imageLink = false;

	/** in F3 */
	private boolean in_videoImageLink = false;

	private boolean in_videoImageLinkBackup = false;

	private boolean in_authorIcon = false;

	private boolean in_f16 = false;/*
									 * change names when handling the usage of
									 * these items
									 */
	private boolean in_f22 = false;
	//private boolean in_f11 = false;

	private boolean videoImageExist = false;
	/** in F9 */
	private boolean in_videoArticleImage = false;
	private boolean in_CastTV_node = false;
	/** in F4 */
	private boolean in_link_to_clip = false;

	private Document parsedDocument = new Document();

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public Document getParsedData()
	{
		return this.parsedDocument;
	}

	// ===========================================================
	// Methods
	// ===========================================================
	@Override
	public void startDocument() throws SAXException
	{
		this.parsedDocument = new Document();
		this.parsedDocument.setTagiot(new ArrayList<Tagit>());
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
		if (localName.equals("document"))
		{
			parsedDocument.setDoc_id(atts.getValue("doc_id"));
			parsedDocument.setModifiedOn(atts.getValue("modified_on"));
			parsedDocument.setDoctype(atts.getValue("doctyp_id"));
		}
		else if (localName.equals("title"))
		{
			this.in_title = true;
		}
		else if (localName.equals("sub_title"))
		{
			this.in_sub_title = true;
		}
		else if (localName.equals("text"))
		{
			this.in_text = true;
		}
		else if (localName.equals("f7"))
		{
			this.in_writerName = true;
		}
		else if (localName.equals("f16"))
		{
			this.in_f16 = true;
		}
	
		else if (localName.equals("f22"))
		{
			this.in_f22 = true;
		}
		else if (localName.equals("f2"))
		{
			this.in_imageLink = true;
		}
		else if (localName.equals("f3"))
		{
			this.in_videoImageLink = true;
		}
		else if (localName.equals("f4"))
		{
			// this.in_videoImageLinkBackup = true;
			this.in_link_to_clip = true;
		}
		else if (localName.equals("author_icon"))
		{
			this.in_authorIcon = true;
		}
		else if (localName.equals("f19"))
		{
			parsedDocument.setClipURL(atts.getValue("clip_url"));
			parsedDocument.setClipURLFromF19(atts.getValue("clip_url"));
			parsedDocument.setImageUrlFromF19(atts.getValue("clip_f9.imgsrc"));
			// html5 content
			if (atts.getValue("app_url") != null) parsedDocument.setClipUrlHTML5(atts.getValue("app_url"));
			if (atts.getValue("html5") != null) parsedDocument.setClipHTML5(atts.getValue("html5"));

		}
		else if (localName.equals("f9"))
		{
			this.in_videoArticleImage = true;
		}
		else if (localName.equals("tagit"))
		{
			if (!TextUtils.isEmpty(atts.getValue("id")) && !TextUtils.isEmpty(atts.getValue("simplified")))
			{
				Tagit tagit = new Tagit(atts.getValue("id"), atts.getValue("simplified"), "");

				// add to document
				parsedDocument.getTagiot().add(tagit);
			}
		}
		// else if (localName.equals("node"))
		// {
		// if(atts.getValue("id") != null && (atts.getValue("id").equals("2")))
		// {
		// parsedDocument.setClipURL(atts.getValue("url"));
		// this.in_CastTV_node = true;
		// }
		// }
	}
	/** Gets be called on closing tags like: </tag> **/
	@Override
	public void endElement(String namespaceURI, String localName, String qName) throws SAXException
	{
		//Log.e("alex","localName:" + localName);
		
		if (localName.equals("title"))
		{
			this.in_title = false;
		}
		else if (localName.equals("sub_title"))
		{
			this.in_sub_title = false;
		}
		else if (localName.equals("text"))
		{
			this.in_text = false;
		}
		else if (localName.equals("f7"))
		{
			this.in_writerName = false;
		}
		else if (localName.equals("f16"))
		{
			this.in_f16 = false;
		}
		else if (localName.equals("f22"))
		{
			this.in_f22 = false;
		}

//		else if (localName.equals("f11"))
//		{
//			this.in_f11 = false;
//		}
		else if (localName.equals("f2"))
		{
			this.in_imageLink = false;
		}
		else if (localName.equals("f3"))
		{
			this.in_videoImageLink = false;
		}
		else if (localName.equals("f4"))
		{
			// this.in_videoImageLinkBackup = false;
			in_link_to_clip = false;
		}
		else if (localName.equals("author_icon"))
		{
			this.in_authorIcon = false;
		}
		else if (localName.equals("f9"))
		{
			this.in_videoArticleImage = false;
		}
		else if (localName.equals("node"))
		{
			this.in_CastTV_node = false;
		}
	}
	/**
	 * Gets be called on the following structure: <tag>characters</tag>
	 */
	@Override
	public void characters(char ch[], int start, int length)
	{
		if (this.in_title)
		{
			parsedDocument.setTitle(new String(ch, start, length));
		}
		else if (this.in_sub_title)
		{
			parsedDocument.setSubTitle(new String(ch, start, length));
		}
		else if (this.in_text)
		{
			parsedDocument.setText(new String(ch, start, length));
		}
		else if (this.in_writerName)
		{
			parsedDocument.setAuthorName(new String(ch, start, length));
		}
		else if (this.in_f16)
		{
			parsedDocument.setF16(new String(ch, start, length));
		}

		else if (this.in_f22)
		{
			parsedDocument.setF22(new String(ch, start, length));
		}
		else if (this.in_imageLink || this.in_authorIcon)
		{
			parsedDocument.setImageFromF2(new String(ch, start, length));
			parsedDocument.setImageAuthor(new String(ch, start, length));
			parsedDocument.setIsDeot(this.in_authorIcon);
		}
		else if (this.in_videoImageLink)
		{
			parsedDocument.setImageFromF3(new String(ch, start, length));
			parsedDocument.setImageAuthor(new String(ch, start, length));
			videoImageExist = true;
		}
		else if (this.in_videoImageLinkBackup)
		{
			parsedDocument.setClipImageUrl(new String(ch, start, length));
			if (!videoImageExist)
			{
				parsedDocument.setImageFromF3(new String(ch, start, length));
			}
		}
		else if (this.in_videoArticleImage)
		{
			parsedDocument.setVideoArticleImageFromF9(new String(ch, start, length));
		}
		else if (this.in_link_to_clip)
		{
			parsedDocument.setClipURL(new String(ch, start, length));
		}
	}

}
