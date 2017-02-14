package il.co.globes.android.objects;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Response {
	String responseSerialNumber;
	String responseDate;
	String responseUserName;
	String responseSubject;
	String responseText="";


	public String getResponseSerialNumber() {
		if (responseSerialNumber!=null)return responseSerialNumber;
		else return "";
	}
	public void setResponseSerialNumber(String responseSerialNumber) {
		this.responseSerialNumber = responseSerialNumber;
	}
	public String getResponseDate() {
		if (responseDate!=null)return responseDate;
		else return "";
	}
	public void setResponseDate(String responseDate) {
		Locale.setDefault(Locale.ENGLISH);
		SimpleDateFormat  sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			Date date = sdf.parse(responseDate);

			SimpleDateFormat dateToPrint = 
				new SimpleDateFormat ("HH:mm dd/MM/yyyy");
			this.responseDate = dateToPrint.format(date);

		} catch (Exception e) {
			this.responseDate = "";
		}		
	}
	public String getResponseUserName() {
		if (responseUserName!=null)return responseUserName;
		else return "";
	}
	public void setResponseUserName(String responseUserName) {
		this.responseUserName = responseUserName;
	}
	public String getResponseSubject() {
		if (responseSubject!=null)return responseSubject;
		else return "";
	}
	public void setResponseSubject(String responseSubject) {
		this.responseSubject = responseSubject;
	}
	public String getResponseText() {
		if (responseText!=null)
		{
			responseText = responseText.replaceAll("\\[", "\\<")
										.replaceAll("\\]", "\\>")
										.replaceAll("</div><div>", "</br><br>");
			return responseText;
		}
		else return "";
	}
	public void setText(String responseText) {
		this.responseText += responseText;
	}






}




