package il.co.globes.android.objects.ShareObjects;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ShareAnalystRecommendation {

	String DateAdded; //should be formatted
	String analystName;
	String rank;
	
	
	public String getDate() {
		return DateAdded;
	}
	public void setDate(String DateAdded) {
		Locale.setDefault(Locale.ENGLISH);
		SimpleDateFormat  sdf = new SimpleDateFormat("yyyy-mm-dd");
		try {
			Date date = sdf.parse(DateAdded);

			SimpleDateFormat dateToPrint = 
				new SimpleDateFormat ("dd/MM/yyyy");
			this.DateAdded = dateToPrint.format(date);

		} catch (Exception e) {
			this.DateAdded = "";
		}		
	}
	public String getAnalystName() {
		return analystName;
	}
	public void setAnalystName(String analystName) {
		this.analystName = analystName;
	}
	public String getRank() {
		return rank;
	}
	public void setRank(String rank) {
		this.rank = rank;
	}
	
	
}
