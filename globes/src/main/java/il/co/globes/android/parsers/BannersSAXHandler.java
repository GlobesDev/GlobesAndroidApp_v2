package il.co.globes.android.parsers;


import il.co.globes.android.objects.Banner;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


public class BannersSAXHandler extends DefaultHandler{

	// ===========================================================
	// Fields
	// ===========================================================

	private boolean in_bannerURL  = false;
	private boolean in_clickURL  = false;

	private Banner banner = new Banner();

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public Banner getParsedData() {
		return this.banner;
	}

	// ===========================================================
	// Methods
	// ===========================================================
	@Override
	public void startDocument() throws SAXException {
		this.banner = new Banner();
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

		if (localName.equals("bannerURL")) {
			this.in_bannerURL = true;
		}
		else if (localName.equals("clickURL")) {
			this.in_clickURL = true;
		}


	}
	/** Gets be called on closing tags like: 
	 * </tag> */
	@Override
	public void endElement(String namespaceURI, String localName, String qName)
	throws SAXException {
		
		if (localName.equals("bannerURL")) {
			this.in_bannerURL = false;
		}
		else if (localName.equals("clickURL")) {
			this.in_clickURL = false;
		}
	}
	/** Gets be called on the following structure: 
	 * <tag>characters</tag> */
	@Override
	public void characters(char ch[], int start, int length) {

		if(this.in_bannerURL){
			banner.setBannerURL((new String(ch, start, length)));
		}
		else if(this.in_clickURL){
			banner.setLinkURL((new String(ch, start, length)));
		}


	}

}





