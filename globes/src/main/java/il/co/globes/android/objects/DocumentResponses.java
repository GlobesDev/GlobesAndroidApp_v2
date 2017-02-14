package il.co.globes.android.objects;

import il.co.globes.android.Utils;
import il.co.globes.android.parsers.DocumentResponsesSAXHandler;

import java.net.URL;
import java.util.Vector;

public class DocumentResponses 
{
	private int totalNumberOfResponses=0;
	private static final String HEADER = "לכתבה זו ";
	public Vector<Response> responses = new Vector<Response>();

	public String getTotalNumberOfResponses() 
	{
		if (totalNumberOfResponses==0) return HEADER + "אין תגובות";
		else if (totalNumberOfResponses==1) return HEADER + "תגובה אחת";
		else return HEADER +Integer.toString(totalNumberOfResponses) + " תגובות";
	}

	public void setTotalNumberOfResponses(String total_number_of_responses) 
	{
		if (total_number_of_responses!=null)
		{
			this.totalNumberOfResponses = Integer.parseInt(total_number_of_responses);
		}
		else
		{
			this.totalNumberOfResponses=0;
		}
	}

	public int getResponsesStat()
	{
		return this.totalNumberOfResponses;
	}
	
	public static DocumentResponses parseDocumentResponses (URL url) throws Exception
	{
		//return new DocumentResponses();
		
		DocumentResponsesSAXHandler documentResponsesHandler = new DocumentResponsesSAXHandler();
		Utils.parseXmlFromUrlUsingHandler(url, documentResponsesHandler);
		DocumentResponses parsedDocumentResponses = documentResponsesHandler.getParsedData();
		return parsedDocumentResponses;		
	}
}



