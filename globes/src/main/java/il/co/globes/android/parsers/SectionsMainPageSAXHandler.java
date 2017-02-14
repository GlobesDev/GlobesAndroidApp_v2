package il.co.globes.android.parsers;

import il.co.globes.android.objects.InnerGroupSections;
import il.co.globes.android.objects.NewsSet;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


public class SectionsMainPageSAXHandler extends DefaultHandler{

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
		this.newsSet =  new NewsSet();
	}

	@Override
	public void endDocument() throws SAXException {
		// Nothing to do
	}
     
	/** Gets be called on opening tags like: 
	 * <tag> 
	 * Can provide attribute(s), when xml was like:
	 * <tag attribute="attributeValue">*/
	@Override
	public void startElement(String namespaceURI, String localName,
			String qName, Attributes atts) throws SAXException {


		if (localName.equals("inner_group")) {
			InnerGroupSections innerGroup = new InnerGroupSections();
			innerGroup.setNode_id(atts.getValue("node_id"));
			innerGroup.setTitle(atts.getValue("title"));
			innerGroup.setShort_title(atts.getValue("short_title"));
			newsSet.addItem(innerGroup);
		}

	}

	@Override
	public void endElement(String namespaceURI, String localName, String qName)
	throws SAXException {
	}
}

