package il.co.globes.android;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import tv.justad.sdk.view.JustAdView;
import tv.justad.sdk.view.JustAdViewDelegate;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * @author Eviatar Saidoff This class is for timer usage in the context of RichMediaAd & SplashArticle  display
 */

public class RichMediaAd 
{

	private SharedPreferences sharedPrefs;
	private Context appContext;
	private final static String TAG = "RichMediaAd";
	private static RichMediaAd instance = null; 
	
	/**
	 * prviate Ctor 
	 * @param context set up sharedPrefs & appContext 
	 */
	private RichMediaAd(Context context){
		this.appContext = context;
		this.sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
	}
	
	/**
	 * Singleton Thread protected
	 * @param context needs to base on app context
	 * @return the instance
	 */
	public static synchronized RichMediaAd getInstance(Context context){
		if (instance == null)
		{
			instance =  new RichMediaAd(context);
		}
		return instance;
	}


	/**
	 * Called to save the Date into SharedPrefs
	 * @param key the key to save to
	 * @param dateAndTime the dateAndTime to save as a String
	 */
	private void savePrefs(String key, String dateAndTime){
		SharedPreferences.Editor editor = sharedPrefs.edit();
		editor.putString(key, dateAndTime);
		editor.commit();
	}
	
	/**
	 * Called to load the Date as String stored on shared prefs
	 * @param key load the date by key
	 * @return the string of savedTimeAndDate or null if no such value returned
	 */
	private String loadPrefs(String key){
		String savedTimeAndDate = sharedPrefs.getString(key, null);
		return savedTimeAndDate;
	}
	
	
	/**
	 * @return true if need of RichMedia to show false if not
	 */
	public boolean checkNeedForRichMediaAd(){
		
		//get the current time
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss aa");
		Calendar c = Calendar.getInstance();
		Date curDate = c.getTime();
		
		//get the save time 
		String savedDateAndTime = loadPrefs("timeAndDate");
		Log.d(TAG, "Loaded timeAndDate from sharedPrefs is : "+savedDateAndTime);
		
		/**
		 * No sharedPrefs loaded , it is first time load
		 * Need to save the date to start count from 
		 * save as string in sharedPrefs 
		*/
		if (savedDateAndTime == null)
		{
			//save the dateAndTime in the sharedPrefs
			savePrefs( "timeAndDate",dateFormat.format(curDate));
			return true; 
		}
		else {
			
			try
			{
				/**get saved dateAndTime from string to Date object and set the Calendar to that time , add 8 HR and check.*/  
				Date savedDate = dateFormat.parse(savedDateAndTime);
				c.setTime(savedDate);
				
				//TODO RichMedia time interval choice
				c.add(Calendar.HOUR, 8);
				
				savedDate = c.getTime();
				Log.d(TAG, "loaded time + interval is: "+dateFormat.format(savedDate));
				
				/** time not passed */
				//an int < 0 if this Date is less than the specified Date, 0 if they are equal, and an int > 0 if this Date is greater. 
				if (curDate.compareTo(savedDate) < 0 )
				{
					Log.d(TAG, "need to show Rich media is: False");
					return false;
				}
				else {
					/** time has passed we save the new cur time that the Ad was shown*/
					savePrefs("timeAndDate",dateFormat.format(curDate));
					Log.d(TAG, "need to show Rich media is: True");
					return true;
				}
				
			}
			catch (ParseException e)
			{
				Log.d(TAG, "Error parsing save time string to Date Object "+e.toString());
				return false;
			}
		}
		
	}
	
	/**
	 * Uses Application context
	 * @return true if need of splashArticle to show false if not
	 */
	public boolean checkNeedForSplashArticle(){
		
		//get the current time
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss aa");
		Calendar c = Calendar.getInstance();
		Date curDate = c.getTime();
		
		//get the save time 
		String savedDateAndTime = loadPrefs("timeAndDateSplashArticle");
		Log.d(TAG, "Loaded timeAndDateSplashArticle from sharedPrefs is : "+savedDateAndTime);
		
		/**
		 * No sharedPrefs loaded , it is first time load
		 * Need to save the date to start count from 
		 * save as string in sharedPrefs 
		*/
		if (savedDateAndTime == null)
		{
			//save the dateAndTime in the sharedPrefs
			savePrefs("timeAndDateSplashArticle",dateFormat.format(curDate));
			Log.d(TAG, "timeAndDateSplashArticle from sharedPrefs is null");
			Log.d(TAG, "need to show splashArticle is: true");
			return true; 
		}
		else {
			
			try
			{
				/**get saved dateAndTime from string to Date object and set the Calendar to that time , add 12 HR and check.*/  
				Date savedDate = dateFormat.parse(savedDateAndTime);
				c.setTime(savedDate);
				
				//TODO SplashArticle time interval choice
				c.add(Calendar.HOUR, 12);
				
				savedDate = c.getTime();
				Log.d(TAG, "loaded timeAndDateSplashArticle + interval is: "+dateFormat.format(savedDate));
				
				/** time not passed */
				if (curDate.compareTo(savedDate) < 0 )
				{
					Log.d(TAG, "need to show SplashArticle is: False");
					return false;
				}
				else {
					/** time has passed we save the new cur time that the Ad was shown*/
					savePrefs("timeAndDateSplashArticle",dateFormat.format(curDate));
					Log.d(TAG, "need to show SplashArticle is: True");
					return true;
				}
				
			}
			catch (ParseException e)
			{
				Log.d(TAG, "Error parsing save time string to Date Object "+e.toString());
				return false;
			}
		}
		
	}
}
