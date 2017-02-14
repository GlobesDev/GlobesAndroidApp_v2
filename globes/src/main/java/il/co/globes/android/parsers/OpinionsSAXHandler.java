package il.co.globes.android.parsers;


import il.co.globes.android.objects.ArticleOpinion;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;



public class OpinionsSAXHandler extends MainSAXHandler{


	private boolean in_authorName;


	/** Gets be called on opening tags like: 
	 * <tag> 
	 * Can provide attribute(s), when xml was like:
	 * <tag attribute="attributeValue">*/
	@Override
	public void startElement(String namespaceURI, String localName,	String qName, Attributes atts) throws SAXException
	{
		super.startElement(namespaceURI, localName,qName, atts);		
		if (localName.equals("f7")) {
			in_authorName = true;
		}
	}

	@Override
	public void endElement(String namespaceURI, String localName, String qName)	throws SAXException
	{
		super.endElement(namespaceURI, localName,qName);
		if (localName.equals("f7")) {
			in_authorName = false;
		}
	}

	@Override
	public void characters(char ch[], int start, int length) 
	{
		super.characters(ch, start, length);
		if(this.in_authorName)
		{
			((ArticleOpinion)newsSet.itemHolder.lastElement()).setAuthorName(new String(ch, start, length));
		}
	}
}
