package il.co.globes.android;

import il.co.globes.android.objects.DocumentResponses;
import il.co.globes.android.objects.GlobesURL;
import il.co.globes.android.objects.Response;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AppEventsLogger;

public class DocumentResponsesActivity extends Activity
{

	private ProgressDialog pd;
	ListView responsesList;
	Context context;
	int responsesCount = 1000000;
	int responsePosition = 0;
	String documentId;

	Activity documentResponsesActivity = this;
	DocumentResponses parsedDocumentResponses;

	Button buttonEnlargeFontSize;
	Button buttonReduceFontSize;
	Button buttonGetMoreResponses;
	Button buttonAddResponse;

	TextView textViewResponseCount;
	TextView textViewResponseSerialNumber;
	TextView textViewResponseSubject;
	TextView textViewResponseDate;
	TextView textViewResponseUserName;

	ResponseAdapter responsesListAdapter;
	@Override
	public void onCreate(Bundle icicle)
	{
		super.onCreate(icicle);
		context = this;

		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.layout_document_responses);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.globes_title);

		Intent intent = getIntent();
		documentId = intent.getStringExtra("documentId");

		SetInitialData();
		createActionBarButtons();

		responsesList = (ListView) findViewById(R.id.listViewDocumentResponses);
		responsesList.setOnItemClickListener(new OnItemClickListener()
		{

			@Override
			public void onItemClick(final AdapterView<?> parent, final View v, final int location, final long id)
			{
				responsePosition = location;
				if (parsedDocumentResponses.responses.get(responsePosition).getResponseText().length() >= 2)
				{
					showDialog(Definitions.DIALOGOPENRESPONSETEXT);
				}
			}
			
		});

	}

	public void parseXmlFromUrlUsingHandler(URL url, DefaultHandler handler) throws Exception
	{
		SAXParserFactory spf = SAXParserFactory.newInstance();
		SAXParser sp = spf.newSAXParser();
		XMLReader xr = sp.getXMLReader();

		URLConnection connection = url.openConnection();
		connection.setConnectTimeout(Definitions.ConnectTimeout);
		connection.setReadTimeout(Definitions.readTimeout);

		xr.setContentHandler(handler);
		xr.parse(new InputSource(connection.getInputStream()));
	}

	@Override
	protected void onPrepareDialog(int id, Dialog dialog)
	{

		switch (id)
		{
			case Definitions.DIALOGOPENRESPONSETEXT :

				prepareDialogResponseText(dialog);
		}
	}

	@Override
	protected Dialog onCreateDialog(int id)
	{
		switch (id)
		{

			case Definitions.DIALOGADDRESPONSE :
				return createDialogAddResponse();

			case Definitions.DIALOGOPENRESPONSETEXT :
				return createDialogResponsesText();

		}
		return null;
	}

	public Dialog createDialogAddResponse()
	{
		// This example shows how to add a custom layout to an AlertDialog
		LayoutInflater dialogAddResponse = LayoutInflater.from(this);
		View viewTextEntry = dialogAddResponse.inflate(R.layout.alert_dialog_add_response, null);
		final EditText responseUserName = (EditText) viewTextEntry.findViewById(R.id.editTextResponseUserName);
		final EditText responseSubject = (EditText) viewTextEntry.findViewById(R.id.editTextResponseSubject);
		final EditText responseText = (EditText) viewTextEntry.findViewById(R.id.editTextResponseText);

		return new AlertDialog.Builder(DocumentResponsesActivity.this).setTitle("הוסף תגובה").setView(viewTextEntry)
				.setPositiveButton("שלח", new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int whichButton)
					{

						String responseUserNameString = responseUserName.getText().toString();
						String responseSubjectString = responseSubject.getText().toString();
						String responseTextString = responseText.getText().toString();

						createAndSendResponse(responseUserNameString, responseSubjectString, responseTextString);
						dismissDialog(Definitions.DIALOGADDRESPONSE);
					}
				}).setNegativeButton("בטל", new DialogInterface.OnClickListener()
				{
					@Override
					public void onClick(DialogInterface arg0, int arg1)
					{
						dismissDialog(Definitions.DIALOGADDRESPONSE);
					}
				}).create();
	}

	public Dialog createDialogResponsesText()
	{
		// LayoutInflater dialogResponseOpenText = LayoutInflater.from(this);
		// View viewResponseOpenText =
		// dialogResponseOpenText.inflate(R.layout.alert_dialog_open_response_text,
		// null);
		// TextView textViewResponseText = (TextView)
		// viewResponseOpenText.findViewById(R.id.textViewResponseText);
		// textViewResponseText.setText(Html.fromHtml(parsedDocumentResponses.responses.get(responsePosition).getResponseText()));
		//
		//
		// return new AlertDialog.Builder(DocumentResponsesActivity.this)
		// .setTitle(parsedDocumentResponses.responses.get(responsePosition).getResponseSubject())
		// .setView(textViewResponseText)
		// .setPositiveButton("ñâåø", new DialogInterface.OnClickListener() {
		// public void onClick(DialogInterface dialog, int whichButton) {
		// dismissDialog(Definitions.DIALOGOPENRESPONSETEXT);
		// }
		// })
		// .create();

		// return new AlertDialog.Builder(DocumentResponsesActivity.this)
		// .setPositiveButton("ñâåø", new DialogInterface.OnClickListener() {
		// public void onClick(DialogInterface dialog, int whichButton) {
		// dismissDialog(Definitions.DIALOGOPENRESPONSETEXT);
		// }
		//
		//
		// }).create();

		// AlertDialog.Builder builder = new AlertDialog.Builder(this);
		// builder.setCancelable(true)
		// .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
		// public void onClick(DialogInterface dialog, int id) {
		// dismissDialog(Definitions.DIALOGOPENRESPONSETEXT);
		// }
		//
		// });
		// AlertDialog alert = builder.create();
		// return alert;

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		AlertDialog alert;
		if (parsedDocumentResponses != null)
		{
			builder.setMessage(parsedDocumentResponses.responses.get(responsePosition).getResponseText()).setCancelable(true)
					.setTitle(parsedDocumentResponses.responses.get(responsePosition).getResponseSubject());
			alert = builder.create();

		}
		else
		{
			builder.setMessage("ללא תוכן אנא נסה/י שנית").setCancelable(true).setTitle("שגיאת תוכן");
			alert = builder.create();
		}

		return alert;

	}

	void prepareDialogResponseText(Dialog dialog)
	{

		dialog.setContentView(R.layout.alert_dialog_open_response_text);

		TextView subject = (TextView) dialog.findViewById(R.id.textViewResponseSubject);
		TextView text = (TextView) dialog.findViewById(R.id.textViewResponseText);

		if (parsedDocumentResponses != null && parsedDocumentResponses.responses != null
				&& parsedDocumentResponses.responses.get(responsePosition) != null)
		{
			subject.setText(Html.fromHtml(parsedDocumentResponses.responses.get(responsePosition).getResponseSubject()));
			text.setText(Html.fromHtml(parsedDocumentResponses.responses.get(responsePosition).getResponseText()));
		}

		if (Definitions.flipAlignment)

		{
			subject.setGravity(android.view.Gravity.RIGHT);
			text.setGravity(android.view.Gravity.RIGHT);
		}
	}

	// TODO sends the response
	void createAndSendResponse(String responseUserName, String responseSubjectString, String responseTextString)
	{
		ResponseAsyncTask task = new ResponseAsyncTask(context);

		try
		{
			String responseToSend = GlobesURL.APIlocation_ArticleResponsesAddNew
					.replaceAll("XXXX", URLEncoder.encode(documentId, "UTF-8"))
					.replace("ZZZZ", URLEncoder.encode(responseUserName, "UTF-8"))
					.replace("KKKK", URLEncoder.encode(responseSubjectString, "UTF-8"))
					.replace("EEEE", URLEncoder.encode(responseTextString, "UTF-8"));

			// Send data
			URL url = new URL(responseToSend);
			task.execute(url);
		}
		catch (Exception e)
		{
			Toast.makeText(this, Definitions.SendingResponseError, Toast.LENGTH_SHORT).show();
		}

	}

	void SetInitialData()
	{
		pd = ProgressDialog.show(this, "", Definitions.Loading, true, false);
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					/*
					 * get the XML just for the number of responses at the
					 * bottom of the view -rotemm
					 */
					URL documentResponesUrl = new URL(GlobesURL.APILocation_ArticleResponses.replace("XXXX", documentId).replace("YYYY",
							Integer.toString(responsesCount)));

					parsedDocumentResponses = DocumentResponses.parseDocumentResponses(documentResponesUrl);
					handler.sendEmptyMessage(0);

				}
				catch (Exception e)
				{
					handler.sendEmptyMessage(1);
					// Toast toast = Toast.makeText(context,
					// Definitions.ErrorLoading, Toast.LENGTH_LONG);
					// toast.show();
					// documentResponsesActivity.finish();
				}
			}
		}).start();
	}

	private Handler handler = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			if (msg.what == 0)
			{
				responsesListAdapter = new ResponseAdapter(context, android.R.layout.simple_list_item_1, parsedDocumentResponses.responses);
				responsesList.setAdapter(responsesListAdapter);
				try
				{
					pd.dismiss();
				}
				catch (Exception e)
				{
					// TODO: handle exception
				}
			}
			else
			{
				try
				{
					pd.dismiss();
				}
				catch (Exception e)
				{
					// TODO: handle exception
				}
				Toast toast = Toast.makeText(context, Definitions.ErrorLoading, Toast.LENGTH_LONG);
				toast.show();
				documentResponsesActivity.finish();
			}
		}
	};

	void createActionBarButtons()
	{
		// buttonEnlargeFontSize = (Button)
		// findViewById(R.id.buttonEnlargeFontSize);
		// buttonEnlargeFontSize.setOnClickListener(new View.OnClickListener()
		// {
		// @Override
		// public void onClick(View v) {
		// changeFontSize(ENLARGETEXTSIZE);
		// }
		// });
		//
		// buttonReduceFontSize = (Button)
		// findViewById(R.id.buttonReduceFontSize);
		// buttonReduceFontSize.setOnClickListener(new View.OnClickListener()
		// {
		// @Override
		// public void onClick(View v) {
		// changeFontSize(REDUCETEXTSIZE);
		// }
		// });
		// buttonGetMoreResponses = (Button)
		// findViewById(R.id.buttonGetMoreResponses);
		// buttonGetMoreResponses.setOnClickListener(new View.OnClickListener()
		// {
		// @Override
		// public void onClick(View v) {
		// responsesCount+=20;
		// SetInitialData();
		// responsesListAdapter = new
		// ResponseAdapter(context,android.R.layout.simple_list_item_1,
		// parsedDocumentResponses.responses);
		// responsesList.setAdapter(responsesListAdapter);
		// responsesListAdapter.notifyDataSetChanged();
		//
		// }
		// });

		buttonAddResponse = (Button) findViewById(R.id.buttonAddResponse);
		buttonAddResponse.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				showDialog(Definitions.DIALOGADDRESPONSE);
			}
		});
	}

	// void changeFontSize(int what){
	//
	// switch (what)
	// {
	// case ENLARGETEXTSIZE:
	// if (baseTextSize<=MAXFONTSIZE)
	// {
	// baseTextSize++;
	// }
	// break;
	// case REDUCETEXTSIZE:
	// if (baseTextSize>=MINFONTSIZE)
	// {
	// baseTextSize--;
	// }
	// break;
	//
	// case RESETTEXTSIZE:
	// baseTextSize=12;
	// break;
	// }
	// textViewResponseCount.setTextSize((float)baseTextSize+4);
	//
	// textViewResponseSubject.setTextSize((float)baseTextSize+2);
	// textViewResponseSerialNumber.setTextSize((float)baseTextSize+2);
	// textViewResponseDate.setTextSize((float)baseTextSize);
	// textViewResponseUserName.setTextSize((float)baseTextSize);
	//
	// }

	// Our array adapter, in our view, we will create a title, a description and
	// an icon for each row
	private class ResponseAdapter extends ArrayAdapter<Response>
	{

		public List<Response> responses;

		public ResponseAdapter(Context context, int textViewResourceId, List<Response> items)
		{
			super(context, textViewResourceId, items);
			this.responses = items;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			View view = convertView;
			if (view == null)
			{
				LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				view = vi.inflate(R.layout.row_layout_response, null);
			}
			Response response = responses.get(position);
			if (response != null)
			{
				textViewResponseCount = (TextView) findViewById(R.id.textViewResponsesCount);
				textViewResponseSerialNumber = (TextView) view.findViewById(R.id.textViewResponseSerialNumber);
				textViewResponseSubject = (TextView) view.findViewById(R.id.textViewResponseSubject);
				textViewResponseDate = (TextView) view.findViewById(R.id.textViewResponseDate);
				textViewResponseUserName = (TextView) view.findViewById(R.id.textViewResponseUserName);
				// TextView textViewResponseText = (TextView)
				// v.findViewById(R.id.textViewResponseText);

				textViewResponseCount.setText(parsedDocumentResponses.getTotalNumberOfResponses());
				textViewResponseSerialNumber.setText("  ." + responses.get(position).getResponseSerialNumber());
				textViewResponseSubject.setText(responses.get(position).getResponseSubject());
				textViewResponseDate.setText(responses.get(position).getResponseDate());
				textViewResponseUserName.setText(responses.get(position).getResponseUserName());

				if (responses.get(position).getResponseText().length() >= 2)
				{
					ImageView imageViewIfResponseTextExsits = (ImageView) view.findViewById(R.id.imageViewIfResponseTextExsits);
					imageViewIfResponseTextExsits.setImageResource(R.drawable.arrowsmall);
				}
				else
				{
					ImageView imageViewIfResponseTextExsits = (ImageView) view.findViewById(R.id.imageViewIfResponseTextExsits);
					imageViewIfResponseTextExsits.setImageResource(R.drawable.transparent);
				}

				// extViewResponseText.setText(Html.fromHtml(responses.get(position).getResponseText()));

				if (Definitions.flipAlignment)
				{
					textViewResponseSubject.setGravity(android.view.Gravity.RIGHT);
					textViewResponseCount.setGravity(android.view.Gravity.RIGHT);
				}
			}
			return view;
		}
	}

	// TODO ResponseAsyncTask
	/**
	 * 
	 * @author Eviatar Saidoff Created to Avoid network operations on UI thread
	 * 
	 */
	private class ResponseAsyncTask extends AsyncTask<URL, Void, Boolean>
	{
		private Context context;

		/**
		 * Def Ctor
		 * 
		 * @param context
		 *            for Toast
		 * @param url
		 *            string to make URL from and catch exception if occured
		 */
		public ResponseAsyncTask(Context context)
		{
			this.context = context;

		}

		@Override
		protected Boolean doInBackground(URL... urls)
		{
			try
			{
				boolean success;
				HttpURLConnection con = (HttpURLConnection) urls[0].openConnection();

				// Get the response
				BufferedReader readerForHTTPResponse = new BufferedReader(new InputStreamReader(con.getInputStream()));
				String line = "";
				String buffer;
				while ((buffer = readerForHTTPResponse.readLine()) != null)
				{
					line += buffer;
				}
				if (line.contains("500")) // error on response...
				{
					success = false;
				}
				else
				{
					success = true;
				}
				readerForHTTPResponse.close();
				return success;

			}
			catch (Exception e)
			{
				return false;
			}
		}

		@Override
		protected void onPostExecute(Boolean result)
		{
			if (result)
			{
				Toast.makeText(this.context, Definitions.SendingResponseSuccess, Toast.LENGTH_SHORT).show();
			}
			else
			{
				Toast.makeText(this.context, Definitions.SendingResponseError, Toast.LENGTH_SHORT).show();
			}
		}

	}

	@Override
	protected void onResume()
	{
		super.onResume();
		if (UtilsWebServices.checkInternetConnection(context))
		{
			AppEventsLogger.activateApp(context, Definitions.FACEBOOK_APP_ID);
		}
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
