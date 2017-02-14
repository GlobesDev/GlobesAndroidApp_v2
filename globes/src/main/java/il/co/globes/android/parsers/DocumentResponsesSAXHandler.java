package il.co.globes.android.parsers;

import il.co.globes.android.objects.DocumentResponses;
import il.co.globes.android.objects.Response;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class DocumentResponsesSAXHandler extends DefaultHandler
{
	// ===========================================================
	// Fields
	// ===========================================================

	private boolean in_response  = false;

	private DocumentResponses parsedDocumentResponses = new DocumentResponses();

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public DocumentResponses getParsedData() 
	{
		return this.parsedDocumentResponses;
	}

	// ===========================================================
	// Methods
	// ===========================================================
	@Override
	public void startDocument() throws SAXException 
	{
		this.parsedDocumentResponses = new DocumentResponses();
	}

	@Override
	public void endDocument() throws SAXException 
	{
		// Nothing to do
	}

	/** Gets be called on opening tags like: 
	 * <tag> 
	 * Can provide attribute(s), when xml was like:
	 * <tag attribute="attributeValue">*/
	@Override
	public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException 
	{
		if (localName.equals("document_responses")) 
		{
			String responsesCount = atts.getValue("total_number_of_responses");
			
			if(responsesCount == null)
			{
				responsesCount = atts.getValue("total_number_of_responses_yesterday_and_today");				
			}			
			
			parsedDocumentResponses.setTotalNumberOfResponses(responsesCount);
		}
		else if (localName.equals("response")) 
		{
			this.in_response = true;
			Response response = new Response();
			response.setResponseSerialNumber(atts.getValue("serial"));
			response.setResponseDate(atts.getValue("datetime"));
			response.setResponseUserName(atts.getValue("user_name"));
			response.setResponseSubject(atts.getValue("subject"));
			parsedDocumentResponses.responses.add(response);
		}
	}
	
	/** Gets be called on closing tags like:  * </tag> */
	@Override
	public void endElement(String namespaceURI, String localName, String qName) throws SAXException 
	{
		if (localName.equals("response")) 
		{
			this.in_response = false;
		}
	}
	
	/** Gets be called on the following structure: * <tag>characters</tag> */
	@Override
	public void characters(char ch[], int start, int length) 
	{
		if(this.in_response)
		{
			parsedDocumentResponses.responses.lastElement().setText((new String(ch, start, length)));
		}
	}

}





