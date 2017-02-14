package il.co.globes.android;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.view.Window;
import android.widget.TextView;

import com.facebook.AppEventsLogger;

public class AboutActivity extends Activity 
{
	Context context;
	
	@Override
	public void onCreate(Bundle icicle) 
	{
		super.onCreate(icicle);
		context = getApplicationContext();
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.layout_about_activity);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.globes_title);

		TextView aboutText =(TextView)findViewById( R.id.textViewAbout);
		appendWithNewLine2(aboutText, "<u>" + getString(R.string.aboutActivityText) + "</u>");
		appendWithNewLine2(aboutText,  "<u>" + getString(R.string.headerApplication) + "</u>");

		appendWithNewLine2(aboutText, getString(R.string.about_2));
		appendWithNewLine2(aboutText, getString(R.string.about_3));
		appendWithNewLine2(aboutText,  "<u>" + getString(R.string.headerCapabilities) + "</u>");

		appendWithNewLine2(aboutText, getString(R.string.about_5));
		appendWithNewLine2(aboutText, getString(R.string.about_6));
		appendWithNewLine2(aboutText, getString(R.string.about_7));
		appendWithNewLine2(aboutText, getString(R.string.about_8));
		appendWithNewLine2(aboutText, getString(R.string.about_9));
		appendWithNewLine2(aboutText, getString(R.string.about_10));
		appendWithNewLine2(aboutText, getString(R.string.about_11));
		appendWithNewLine2(aboutText, getString(R.string.about_12));
		
		appendWithNewLine2(aboutText, getString(R.string.version));
		
		if (Definitions.flipAlignment)
		{
			aboutText.setGravity(android.view.Gravity.RIGHT);
		}
		
		
		
	}
	
	@Override
	protected void onResume()
	{
		super.onResume();
		if (UtilsWebServices.checkInternetConnection(context)){
			AppEventsLogger.activateApp(context, Definitions.FACEBOOK_APP_ID);
		}
		
	}

	void appendWithNewLine(TextView textView, String text)
	{
		textView.append(Html.fromHtml(text));
		textView.append("\n");
	}
	
	void appendWithNewLine2(TextView textView, String text)
	{
		textView.append(Html.fromHtml(text));
		textView.append("\n\n");
	}
	
	
	@Override
	protected void onStart()
	{
//		EasyTracker.getInstance(this).activityStart(this);
		
		super.onStart();
	}
	@Override
	protected void onStop()
	{
//		EasyTracker.getInstance(this).activityStop(this);
		super.onStop();
	}
	
	

}