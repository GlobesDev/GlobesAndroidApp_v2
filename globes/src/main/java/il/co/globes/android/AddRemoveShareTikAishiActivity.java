package il.co.globes.android;

import il.co.globes.android.adapters.AdapterListWithTextAndV;
import il.co.globes.android.objects.DataContext;
import il.co.globes.android.objects.Tikim;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class AddRemoveShareTikAishiActivity extends Activity implements OnClickListener, OnItemClickListener
{
	private ListView listView_add_remove_share_from_tik ;
	private Button button_close;
	private List<Tikim> TikimList = new ArrayList<Tikim>();
	private AdapterListWithTextAndV adapter ;

	// Mode=4&amp;PortfolioId=&amp;accessKey={1}
	private String URLPortfolioLink="http://www.globes.co.il/apps/portfolios.asmx/GetPortfolios?Mode=4&PortfolioId=&accessKey=";
	// accessKey={0}&amp;InstrumentID={1}&amp;feeder={2}
	private String check_if_in_portfolioLink="http://www.globes.co.il/apps/portfolios.asmx/GetInstrumentInPortfolios?";
	// accessKey={0}&PortfolioID={1}&InstrumentID={2}&feeder={3}&Action={"add", "delete"}
	private String portfolioAction="http://www.globes.co.il/apps/portfolios.asmx/portfolioAction?";
	private String globesAccessKey;
	private String InstrumentID = "";
	private String ActionResult ="";
	private String feederId = "";

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_remove_share_tikaishi_layout);
		globesAccessKey = DataContext.Instance().getAccessKey();
//		InstrumentID = getIntent().getStringExtra("InstrumentID");
		InstrumentID = getIntent().getStringExtra("shareId");
		feederId = getIntent().getStringExtra("feederId");
		if (feederId==null || feederId == "")
		{
			
			
			
			feederId = getIntent().getStringExtra("feeder");
			if (feederId==null || feederId == "")
			{
				feederId = "0";
			}
		}
		Log.e("eli", "InstrumentID "+InstrumentID);
//		Log.e("eli", "InstrumentID 2 "+getIntent().getStringExtra("InstrumentID"));
		Log.e("eli", "globesAccessKey " +globesAccessKey);
		Log.e("eli", "feederId " +feederId);


		URLPortfolioLink="http://www.globes.co.il/apps/portfolios.asmx/GetPortfolios?Mode=4&PortfolioId=&accessKey="+globesAccessKey;
		buildListOfPortfolios();

		button_close = (Button)findViewById(R.id.button_close_add_remove);
		button_close.setOnClickListener(this);




	}

	@Override
	public void onClick(View v)
	{
		finish();
	}

	private void buildListOfPortfolios()
	{
		new AsyncTask<Void, Void, Void>()
		{
			@Override
			protected Void doInBackground(Void... params)
			{
				HttpGetOfPortfolios();
				checkIfInPortfalio();
				return null;
			}

			@Override
			protected void onPostExecute(Void result) 
			{
				listView_add_remove_share_from_tik = (ListView) findViewById(R.id.listView_add_remove_share_from_tik);
				adapter = new AdapterListWithTextAndV(AddRemoveShareTikAishiActivity.this, TikimList);
				listView_add_remove_share_from_tik.setOnItemClickListener(AddRemoveShareTikAishiActivity.this);
				listView_add_remove_share_from_tik.setAdapter(adapter);
			}
		}.execute();
	}

	private void checkIfInPortfalio()
	{
		check_if_in_portfolioLink="http://www.globes.co.il/" +
				"apps/portfolios.asmx/GetInstrumentInPortfolios?" +
				"accessKey="+globesAccessKey+"&InstrumentID="+InstrumentID+"&feeder="+feederId;
		Log.e("eli", "check_if_in = "+ check_if_in_portfolioLink);
		HttpGet uri = new HttpGet(check_if_in_portfolioLink);    
		DefaultHttpClient client = new DefaultHttpClient();
		HttpResponse resp = null;
		try	{resp = client.execute(uri);}
		catch (ClientProtocolException e){e.printStackTrace();	}
		catch (IOException e){	e.printStackTrace();}
		try	  { extractInstrumentInPortfolioID(resp.getEntity().getContent());	}
		catch (IllegalStateException e)	{	e.printStackTrace();}
		catch (IOException e){	e.printStackTrace();	}

	}

	private void extractInstrumentInPortfolioID(InputStream is)
	{
		final String PORTFOLIO = "portfolio";

		final String ID = "id";
		final String NAME= "name";
		final String ERROR_MESSAGE = "Error";

		try
		{
			XmlPullParser parser = Xml.newPullParser();
			parser.setInput(is, null);
			int eventType = parser.getEventType();
			boolean done = false;
			while (eventType != XmlPullParser.END_DOCUMENT && !done)
			{
				switch (eventType)
				{
					case XmlPullParser.START_TAG :
						String name = parser.getName();
						if (name.equalsIgnoreCase(PORTFOLIO))
						{
							@SuppressWarnings("unused")
							String atrName = parser.getAttributeValue(null, NAME); 
							String atrID = parser.getAttributeValue(null, ID); 
							for (Tikim tik : TikimList)
							{
								if (tik.getPortfolioID().compareTo(atrID)==0)
								{
									tik.setSelected(true);
								}
							}
							//							Log.e("eli", atrName  + "  "+ atrID);
						}
						if (name.equalsIgnoreCase(ERROR_MESSAGE))
						{
							done = true;
						}
					case XmlPullParser.END_TAG :
						break;
				}
				eventType = parser.next();
			}
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id)
	{
		Tikim tik = TikimList.get(position);
		String deletOrAdd = tik.isSelected() ? "delete":"add";
		String tikId = tik.getPortfolioID();
		portfolioAction = "http://www.globes.co.il/apps/portfolios.asmx/portfolioAction?" +
				"accessKey="+globesAccessKey+"&PortfolioID="+tikId+"&InstrumentID="+InstrumentID+"&feeder="+feederId+"&Action="+deletOrAdd;

		Log.e("eli","portfolioAction " + portfolioAction);
		deleteOrAddAsync(position);


	}


	private void deleteOrAddAsync(final int position)
	{
		new AsyncTask<Void, Void, Void>()
		{
			@Override
			protected Void doInBackground(Void... params)
			{
				HttpGet uri = new HttpGet(portfolioAction);    
				DefaultHttpClient client = new DefaultHttpClient();
				HttpResponse resp = null;
				try	{resp = client.execute(uri);}
				catch (ClientProtocolException e){e.printStackTrace();	}
				catch (IOException e){	e.printStackTrace();}
				try	  {ActionResult = extractActionResult(resp.getEntity().getContent());	}
				catch (IllegalStateException e)	{	e.printStackTrace();}
				catch (IOException e){	e.printStackTrace();	}
				return null;
			}
			@Override
			protected void onPostExecute(Void result) 
			{
				if (ActionResult.compareTo("true")==0)
				{
					// custom dialog
					final Dialog dialog = new Dialog(AddRemoveShareTikAishiActivity.this);
					dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
					dialog.setContentView(R.layout.custom_dialog_add_remove_tik);

					// set the custom dialog components - text, image and button
					TextView text = (TextView) dialog.findViewById(R.id.text);
					if (TikimList.get(position).isSelected())
					{
						String toWrite = " המניה הוסרה בהצלחה מתיק" +"\n  "+ TikimList.get(position).getTik();
						text.setText(toWrite);
					}else {
						String toWrite = " המניה התווספה בהצלחה לתיק" +"\n  "+ TikimList.get(position).getTik();
						text.setText(toWrite);					
					}
					Button dialogButton = (Button) dialog.findViewById(R.id.dialogButtonOK);
					dialogButton.setOnClickListener(new OnClickListener() 
					{
						@Override
						public void onClick(View v) 
						{
							dialog.dismiss();
						}
					});

					dialog.show();	
					TikimList.get(position).setSelected(!TikimList.get(position).isSelected());
					adapter.notifyDataSetChanged();
				}
			}
		}.execute();
	}

	public void HttpGetOfPortfolios()
	{
		Log.e("eli", URLPortfolioLink);
		HttpGet uri = new HttpGet(URLPortfolioLink);    
		DefaultHttpClient client = new DefaultHttpClient();
		HttpResponse resp = null;
		try	{resp = client.execute(uri);}
		catch (ClientProtocolException e){e.printStackTrace();	}
		catch (IOException e){	e.printStackTrace();}
		try	  { extractPortfolioID(resp.getEntity().getContent());	}
		catch (IllegalStateException e)	{	e.printStackTrace();}
		catch (IOException e){	e.printStackTrace();	}
	}

	private void extractPortfolioID(InputStream is)
	{
		final String PORTFOLIO_ID = "portfolio_id";
		final String PORTFOLIO_NAME= "portfolio_name";
		final String FEEDER = "feeder";
		final String ERROR_MESSAGE = "Error";

//		try
//		{
			XmlPullParser parser = Xml.newPullParser();
			try
			{
				parser.setInput(is, null);
			}
			catch (XmlPullParserException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			int eventType = 0;
			try
			{
				eventType = parser.getEventType();
			}
			catch (XmlPullParserException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			boolean done = false;

			Tikim t =  new Tikim();
			while (eventType != XmlPullParser.END_DOCUMENT && !done)
			{
				switch (eventType)
				{
					case XmlPullParser.START_TAG :
						String name = parser.getName();
						if (name.equalsIgnoreCase(PORTFOLIO_ID))
						{
							try
							{
								t.setPortfolioID( parser.nextText());
							}
							catch (XmlPullParserException e)
							{
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							catch (IOException e)
							{
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						if (name.equalsIgnoreCase(PORTFOLIO_NAME))
						{
							try
							{
								t.setTik(parser.nextText());
							}
							catch (XmlPullParserException e)
							{
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							catch (IOException e)
							{
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						if (name.equalsIgnoreCase(FEEDER))
						{
							try
							{
								t.setFeeder(parser.nextText());
							}
							catch (XmlPullParserException e)
							{
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							catch (IOException e)
							{
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							if (feederId!=null&& t.getFeeder()!=null&& t.getFeeder().compareTo(feederId)==0)
							{
								TikimList.add(t);
								t = new Tikim();
							}
						}
						if (name.equalsIgnoreCase(ERROR_MESSAGE))
						{
							done = true;
						}
					case XmlPullParser.END_TAG :
						break;
				}
				try
				{
					eventType = parser.next();
				}
				catch (XmlPullParserException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				catch (IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
//		}
//		catch (Exception e)
//		{
//			throw new RuntimeException(e);
//		}
	}


	private String extractActionResult(InputStream is)
	{
		final String RESULT = "string";
		final String ERROR_MESSAGE = "Error";
		String message = "";

		try
		{
			XmlPullParser parser = Xml.newPullParser();
			parser.setInput(is, null);
			int eventType = parser.getEventType();
			boolean done = false;
			while (eventType != XmlPullParser.END_DOCUMENT && !done)
			{
				switch (eventType)
				{
					case XmlPullParser.START_TAG :
						String name = parser.getName();
						if (name.equalsIgnoreCase(RESULT))
						{
							message = parser.nextText();
							done = true;
						}
						else if (name.equalsIgnoreCase(ERROR_MESSAGE))
						{
							done = true;
						}

						break;
					case XmlPullParser.END_TAG :
						done = true;

						break;
				}

				eventType = parser.next();
			}
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}

		return message;
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
