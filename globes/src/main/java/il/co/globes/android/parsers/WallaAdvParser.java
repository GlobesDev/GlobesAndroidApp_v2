package il.co.globes.android.parsers;

import il.co.globes.android.AddRemoveShareTikAishiActivity;
import il.co.globes.android.Definitions;
import il.co.globes.android.HttpClientWallaSingleton;
import il.co.globes.android.HttpGetWallaSingleton;
import il.co.globes.android.R;
import il.co.globes.android.Utils;
import il.co.globes.android.WoorldsSDKInit;
import il.co.globes.android.adapters.AdapterListWithTextAndV;
import il.co.globes.android.fragments.MainFragment;
import il.co.globes.android.interfaces.WallaAdvTaskCompleted;
import il.co.globes.android.objects.GlobesURL;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;
import com.outbrain.OBSDK.HttpClient.PersistentCookieStore;
import com.squareup.picasso.Picasso;
import com.woorlds.woorldssdk.server.Segment;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebSettings.PluginState;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ImageView.ScaleType;

public class WallaAdvParser extends AsyncTask<Void , Void, Map<String, Map<String, String>>>
{
	private String _page;
	private String _layout;
	private View _view;
	private String _space_array;
	private int _space_array_position;
	private String _attributes = "attributes";
	private Map<String, Map<String, String>> mapSpaces;
	private ImageView wallaObjImage;
	private WallaAdvTaskCompleted _listener;
		
	private static Context _context;
	private static Application _app;
	//private static DefaultHttpClient httpClient;

	private static AsyncHttpClient httpClient = HttpClientWallaSingleton.getInstance();
	
	public static PersistentCookieStore mCookieStore;
	
	public static boolean isRedirected = false;
	private static boolean isFirstLoading = false;
	
	public WallaAdvParser(Context ctx, Application app, WallaAdvTaskCompleted list, String page, String layout, String space_array, int space_array_position)
	{
		this._page = page;
		this._layout = layout;
		this._listener = list;
		this._space_array = space_array;
		this._space_array_position = space_array_position;
		this._context = ctx;
		this._app = app;
		
		//Log.e("alex", "Walla Page: " +  page + "=== Layout:" + layout);
	}	

	@Override
	protected Map<String, Map<String, String>> doInBackground(Void... params)
	{
		if(!isFirstLoading)
		{
			//Log.e("alex", "isFirstLoading: " + _context);
			setStartCookie(_context);
			isFirstLoading = true;
		}
		
		mapSpaces = new HashMap<String, Map<String, String>>();
		Random rn = new Random();
		int rnd = rn.nextInt(100000) + 1;
		
		String [] arrSpaces = Definitions.WallaSpaces;
		String [] arrAttributes = Definitions.WallaAttributes;
		
		String campaignId = "";
		try
		{
			campaignId = new WoorldsSDKInit(_context).getInstance().getCampaign();		
		}
		catch(Exception ex){}
		
		String sRequestURL = GlobesURL.URLWallaAdvLink.replace("XXXXX", String.valueOf(rnd)).replace("YYYYY", this._page).replace("ZZZZZ", _layout).replace("DDDDD", Definitions.DEVICEMODEL.replaceAll(" ", "")).replace("VVVVV", Utils.getVersionName(this._context).replaceAll(" ", ""));
	    		
		Log.e("alex", "Walla RequestURL:" + sRequestURL);		
		
		if(campaignId != null && !campaignId.isEmpty() && !campaignId.equals("[]"))
		{		
			//sRequestURL += "&Target_group=" + campaignId.replaceAll(" ", "");
		}

	    //Log.e("alex", "WoorldsSDK WallaParser CampaignId3: " + sRequestURL);		
				
		//Log.e("alex",  "Walla Request URL: "  + sRequestURL);
		//Log.e("alex",  "Walla Request URL Page/Layout: "  + _page + "/" + _layout);
		
		try
		{
			sendWallaReportEventAsync(sRequestURL);
		}
		catch (ClientProtocolException e1)
		{
			// TODO Auto-generated catch block
			//Log.e("alex",  "sendWallaReportEventAsync Error: "  + e1);
			e1.printStackTrace();
		}
		catch (IOException e1)
		{
			// TODO Auto-generated catch block
			//Log.e("alex",  "sendWallaReportEventAsync Error: "  + e1);
			e1.printStackTrace();
		}
		
		String sJSon = Utils.getJSONString(sRequestURL);
				
		if(sJSon != "")
	    {
			JSONObject jObject;
			try
			{
				jObject = new JSONObject(sJSon);
				
				for(int i=0; i<arrSpaces.length; i++)
				{
					try
					{
						String sSpace = arrSpaces[i];
						mapSpaces.put(sSpace, null);
						
						if(jObject.has(sSpace))
						{
							JSONArray jSArray = (JSONArray)jObject.get(sSpace);
							int jsArrayLength = jSArray.length();							
							
							//Log.e("alex", "jSArray for space: " + sSpace + "===" + jSArray);
							
							//Log.e("alex", "EEEEE sSpace: " + sSpace + " Length: " + jsArrayLength + "=== Position: " + _space_array_position);
							
							if(jsArrayLength > 0)
							{
								//Log.e("alex", "EEEEE jsArrayLength:" + sSpace + "===" + _space_array + "===" + _space_array_position + "===" + jSArray.length());
								
								int index = (jsArrayLength >= _space_array_position && sSpace == _space_array) ? _space_array_position : 0;
																
								//Log.e("alex", "EEEEE sSpace: " + sSpace + " array: " + _space_array + "===" + index + " _space_array_position: " + _space_array_position);
								
								JSONObject jItem = jSArray.getJSONObject(index);
								JSONObject jAttributes = jItem.getJSONObject(_attributes);							
								
								Map<String, String> mapAttributes = new HashMap<String, String>();
								
								for(int j=0; j<arrAttributes.length; j++)
								{
									try
									{ 
										String sAttribute = arrAttributes[j].trim();
										
										String sValue = "";
										if(jAttributes.has(sAttribute))
										{
											sValue = jAttributes.getString(sAttribute).replace("\r\n", "");
										}										
										
										mapAttributes.put(sAttribute, sValue);
									}
									catch(Exception ex)
									{
										Log.e("alex", "Walla Error 2: " + ex);
									}									
								}						
								
								mapSpaces.put(sSpace, mapAttributes);
							}
						}
					}
					catch(Exception err)
					{
						Log.e("alex", "Walla Error 1: " + err);
					}
				}
			}
			catch (JSONException e)
			{
				// TODO Auto-generated catch block
				//Log.e("alex", "AAAAAAAAA JSONException : " + e);
			}		
			
	    }	
		
		return mapSpaces;
	}
	
	@Override
    protected void onPreExecute() {

	}
	
	@Override
	protected void onPostExecute(Map<String, Map<String, String>> obj) 
	{   
		//Log.e("alex", "AAAAAAAAA333: " + m.get("ADURL_01"));
		
		_listener.onTaskCompleted(mapSpaces);
	}
	
	public static void loadImageIntoImageView(ViewGroup parent, final Context context, String adUrl, final String lnkClickReportUrl, final String clickURL)
	{
		ImageView wallaImage = new ImageView(context);				
		Utils.centerViewInRelative(wallaImage, true);
		wallaImage.setScaleType(ScaleType.FIT_XY);
		parent.addView(wallaImage);
		Picasso.with(context).load(adUrl).into(wallaImage);
		
		//Log.e("alex", "loadImageIntoImageView: " + adUrl);
		
		wallaImage.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				//Log.e("alex", "IIIII 1");
				
				try
				{
					sendWallaReportEvent(lnkClickReportUrl);
				}
				catch (MalformedURLException e)
				{
					e.printStackTrace();
				}
				
				Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(clickURL));
				browserIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(browserIntent);
			}
		});
	}
	
	public static void loadImageIntoWebView(ViewGroup parent, final Context context, String adUrl, final String lnkClickReportUrl, final String clickURL, int width, int height)
	{
		WebView wallaImageGif = new WebView(context);
	    Utils.centerViewInRelative(wallaImageGif, false);
	    parent.addView(wallaImageGif);
	    		
	    //Log.e("alex", "loadImageIntoWebView!!! clickURL: " + clickURL);
	    
		wallaImageGif.getSettings().setJavaScriptEnabled(true);
		wallaImageGif.getSettings().setAppCacheEnabled(false);
		
		if(width > 0 && height > 60)
		{
			//Log.e("alex", "loadImageIntoWebView centerHTMLWithSize: " + clickURL);
			wallaImageGif.loadData(Utils.centerHTMLWithSize(adUrl,  width, height), "text/html; charset=utf-8", "UTF-8");
		}
		else
		{
			wallaImageGif.loadData(Utils.centerHTML(adUrl, true), "text/html; charset=utf-8", "UTF-8");
		}
		
		parent.setVisibility(View.VISIBLE);
		
		wallaImageGif.setOnTouchListener(new OnTouchListener()
		{
			@Override
			public boolean onTouch(View v, MotionEvent event)
			{
				if (event.getAction() == MotionEvent.ACTION_DOWN)
				{
					//Log.e("alex", "IIIII 2");
					
					try
					{
						sendWallaReportEvent(lnkClickReportUrl);
					}
					catch (MalformedURLException e)
					{
						e.printStackTrace();
					}
					
					Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(clickURL));
					browserIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					context.startActivity(browserIntent);
					return true;
			    }
				return false;
			}
		});
	}
	
	public static void loadDataIntoWebView2(ViewGroup parent, final Context context, String dataToload, String adUrl, final String lnkClickReportUrl, final String clickURL)
	{
		WebView wallaAdType = new WebView(context);
		parent.addView(wallaAdType);
		wallaAdType.setPadding(0, 0, 0, 0);
		WebSettings webViewSettings = wallaAdType.getSettings();
		webViewSettings.setJavaScriptCanOpenWindowsAutomatically(false);
		webViewSettings.setJavaScriptEnabled(true);
		webViewSettings.setUseWideViewPort(false);
		webViewSettings.setBuiltInZoomControls(false);
		webViewSettings.setLoadWithOverviewMode(false);
		webViewSettings.setSupportZoom(false);
		webViewSettings.setPluginState(PluginState.ON);
		
		if(adUrl.trim() == "" || adUrl.trim().equals("http://"))
		{
			wallaAdType.loadData(dataToload, "text/html; charset=utf-8", "UTF-8");
		}
		else
		{
			wallaAdType.loadData(Utils.centerHTML(adUrl, false), "text/html; charset=utf-8", "UTF-8");
		}
		
		wallaAdType.setOnTouchListener(new OnTouchListener()
		{
			@Override
			public boolean onTouch(View v, MotionEvent event)
			{
				if (event.getAction() == MotionEvent.ACTION_DOWN)
				{
					try
					{
						sendWallaReportEvent(lnkClickReportUrl);
					}
					catch (MalformedURLException e)
					{
						e.printStackTrace();
					}
					
					//Log.e("alex", "ZZZZZ loadDataIntoWebView2");
					
					Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(clickURL));
					browserIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					context.startActivity(browserIntent);
					return true;
				}
				return false;
			}
		});	
	}
	
	public static void loadDataIntoWebView(ViewGroup parent, final Context context, String dataToload, String adUrl, final String lnkClickReportUrl, final String clickURL)
	{		
		try
		{
			WebView wallaAdType = new WebView(context);
			parent.addView(wallaAdType);
			wallaAdType.setPadding(0, 0, 0, 0);
			wallaAdType.setWebChromeClient(new WebChromeClient());
			WebSettings webViewSettings = wallaAdType.getSettings();
			webViewSettings.setJavaScriptCanOpenWindowsAutomatically(false);
			webViewSettings.setJavaScriptEnabled(true);
			webViewSettings.setUseWideViewPort(false);
			webViewSettings.setBuiltInZoomControls(false);
			webViewSettings.setLoadWithOverviewMode(false);
			webViewSettings.setSupportZoom(false);
			webViewSettings.setPluginState(PluginState.ON);
			webViewSettings.setSupportMultipleWindows(true);			
					
			if(dataToload != "" && (adUrl.trim() == "" || adUrl.trim().equals("http://")))
			{		
				wallaAdType.loadDataWithBaseURL(null, dataToload, "text/html", "utf-8", null);
				parent.setVisibility(View.VISIBLE);
				
				wallaAdType.setWebChromeClient(new WebChromeClient() {
					@Override
				    public boolean onCreateWindow(WebView view, boolean dialog, boolean userGesture, android.os.Message resultMsg) {
						//Log.e("alex", "LoadDataIntoWebView WebChromeClient");
						WebView newWebView = new WebView(view.getContext());
						view.addView(newWebView);
				        WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
				        newWebView.setWebViewClient(new WebViewClient() {
				            @Override
				            public boolean shouldOverrideUrlLoading(WebView view, String url) {
				            	
				                if (null != url) {
				                    try {
				                      //Log.e("alex", "LoadDataIntoWebView func:" + url);
				                      sendWallaReportEvent(lnkClickReportUrl);
				                      Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
				                      browserIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				  					  context.startActivity(browserIntent);
				                    } catch (Exception ex) {Log.e("alex", "LoadDataIntoWebView ex:" + ex);}
				                }
				                return true;
				            }
	
				        });
				        transport.setWebView(newWebView);
				        resultMsg.sendToTarget();
	
						
				        return true;
					}
				});				
			}
			else
			{
				wallaAdType.loadData(Utils.centerHTML(adUrl, false), "text/html; charset=utf-8", "UTF-8");
				parent.setVisibility(View.VISIBLE);
				
				wallaAdType.setOnTouchListener(new OnTouchListener()
				{
					@Override
					public boolean onTouch(View v, MotionEvent event)
					{
						if (event.getAction() == MotionEvent.ACTION_DOWN)
						{
							try
							{
								sendWallaReportEvent(lnkClickReportUrl);
							}
							catch (MalformedURLException e)
							{
								e.printStackTrace();
							}
							
							Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(clickURL));
							browserIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							context.startActivity(browserIntent);
							return true;
						}
						return false;
					}
				});	
			}
		}
		catch(Exception ex)
		{
			Log.e("alex", "LoadDataIntoWebView error: " + ex);
		}
	}
	
	public static void loadSimplyDataIntoWebView(ViewGroup parent,  final Context context, String dataToload)
	{
		WebView wallaAdType = new WebView(context);
		parent.addView(wallaAdType);
		wallaAdType.setPadding(0, 0, 0, 0);
		WebSettings webViewSettings = wallaAdType.getSettings();
		webViewSettings.setJavaScriptCanOpenWindowsAutomatically(false);
		webViewSettings.setJavaScriptEnabled(true);
		webViewSettings.setUseWideViewPort(false);
		webViewSettings.setBuiltInZoomControls(false);
		webViewSettings.setLoadWithOverviewMode(false);
		webViewSettings.setSupportZoom(false);
		webViewSettings.setPluginState(PluginState.ON);
		
		//Log.e("alex", "loadSimplyDataIntoWebView: " + dataToload);
		
		wallaAdType.loadData(dataToload, "text/html; charset=utf-8", "UTF-8");
		parent.setVisibility(View.VISIBLE);
	}	
		
	public static void drawWallaBanner(RelativeLayout parent, Context context, Map<String, String> m)
	{
		try
		{			
			String contentType = m.get("CONTENTTYPE").toLowerCase();
			String adUrl = m.get("ADURL_01");
			String clickURL =  m.get("CLICKURL");		
			String viewReportURL = m.get("ADSRV") + m.get("VIEWREPORT");
			String clickReportUrl = m.get("ADSRV") + m.get("CLICKREPORT");
			String payLoad = m.get("Payload");
			
			int width = Utils.tryParseInt(m.get("WIDTH_01")) ? Integer.valueOf(m.get("WIDTH_01")) : 0;
			int height = Utils.tryParseInt(m.get("HEIGHT_01")) ? Integer.valueOf(m.get("HEIGHT_01")) : 0;
			
			//contentType = "image";
			//adUrl="http://x.walla.co.il/C6BDD2C8/E3FA170E.jpg";
			
			//Log.e("alex", "drawWallaBanner contentType: " + contentType + " adUrl: " + adUrl + " clickReportUrl: " + clickReportUrl + " viewReportURL: " + viewReportURL);
			
			final Context current_context = context;
			//final Uri lnkToRedirect = Uri.parse(clickURL);
			final String lnkClickReportUrl = clickReportUrl;
			
			if(clickReportUrl == "" || viewReportURL == ""){return;}	
						
			//parent.setVisibility(View.VISIBLE);
			parent.removeAllViews();
			
			//Log.e("alex", "IIIII: " + adUrl);
			
			if(contentType.equals("image") || contentType.equals("anigif"))
			{			
//				if (!adUrl.endsWith(".gif") && !adUrl.endsWith(".png"))
//				{
//					Log.e("alex", "loadImageIntoImageView: " + adUrl + "=== parent " + parent);
//					loadImageIntoImageView(parent, context, adUrl, lnkClickReportUrl,  clickURL);
//				}
//				else
//				{
				    if(adUrl == null || adUrl == "")
				    {
				    	loadDataIntoWebView(parent, context, payLoad, "", lnkClickReportUrl, clickURL);
				    }
				    else
				    {
				    	loadImageIntoWebView(parent, context, adUrl, lnkClickReportUrl, clickURL, width, height);
				    }
//				}		
			}
			
			if(contentType.equals("html") || contentType.equals("url"))
			{
				//Log.e("alex", "loadDataIntoWebView html||url: " + adUrl + "=== PAYLOAD: " + payLoad);
				loadDataIntoWebView(parent, context, payLoad, adUrl, lnkClickReportUrl, clickURL);
			}
			
			if(contentType.equals("mraid"))
			{
				//Log.e("alex", "GGGGG is Mraid");
			}		
		
			try
			{
				//Log.e("alex", "sendWallaReportEvent viewReportURL: " + viewReportURL);
				sendWallaReportEvent(viewReportURL);
			}
			catch (MalformedURLException e)
			{
				Log.e("alex", "GGGGG Walla Global Report Error: " + e);
				e.printStackTrace();
			}
		
		}
		catch(Exception ex)
		{
			Log.e("alex", "Walla drawWallaBanner Error: " + ex);
		}
	}
	
	public static void sendWallaReportEvent(String report_url) throws MalformedURLException
	{
		try
		{
			//Log.e("alex", "sendWallaReportEvent: 0");
			sendWallaReportEventAsync(report_url);
		}
		catch (ClientProtocolException e)
		{
			Log.e("alex", "sendWallaReportEvent Error 1: " + e);
			e.printStackTrace();
		}
		catch (IOException e)
		{
			Log.e("alex", "sendWallaReportEvent Error 2: " + e);
			e.printStackTrace();
		}
		
//		final URL uriReport = new URL(report_url);
//
//		Log.e("alex", "GGGGG Walla Report: " + report_url);		
		
//		new AsyncTask<Void, Void, Void>()
//		{			
//			@Override
//			protected Void doInBackground(Void... params)
//			{
//				try {					
//				    HttpURLConnection urlc = (HttpURLConnection) uriReport.openConnection();
//				    urlc.setRequestProperty("User-Agent", "Globes Android Application");				    
//				    urlc.setReadTimeout(10000); //Milliseconds
//				    urlc.setConnectTimeout(15000); //Milliseconds
//				    urlc.setRequestMethod("GET");
//				    urlc.setDoInput(true);		    
//				    
//				    urlc.connect();
//				    			    
//				    Log.e("alex", "GGGGG!!!!!!!!!!!!!!!!!: " + urlc.getResponseCode());
//				    if (urlc.getResponseCode() == 200 || urlc.getResponseCode() == 204) {
//				    	//Log.e("alex", "GGGGG Walla Report Success: " + uriReport.toString());
//				    	urlc.disconnect();
//				    }
//				} catch (MalformedURLException e1) {
//					Log.e("alex", "GGGGG Walla Report Error: " + e1);
//				    e1.printStackTrace();
//				} catch (IOException e2) {
//					Log.e("alex", "GGGGG Walla Report Error: " + e2);
//				    e2.printStackTrace();
//				}
//				
//				return null;
//			}
//
//			@Override
//			protected void onPostExecute(Void result) 
//			{
//				
//			}
//		}.execute();

	}
	
	
	public static void sendWallaReportEventAsync(final String report_url) throws ClientProtocolException, IOException
	{
	    //Log.e("alex", "FusionCookieURL sendWallaReportEventAsync" + report_url);
	    
	    if(httpClient == null){
			Log.e("alex", "httpClient is null (sendWallaReportEventAsync)");	
		}
		else
		{
			//Log.e("alex", "httpClient is NOT null (sendWallaReportEventAsync)");
		}
	    
	    if(report_url.indexOf("impression") != -1)
	    {
	    	//Log.e("alex",  "01.12.2015 - httpClient.get: " + report_url);
	    }
	    
	    httpClient.get(_app, report_url, new TextHttpResponseHandler(){
	    	@Override
	        public void onSuccess(int status, Header[] headers, String responseBody) {
	        super.onSuccess(status, headers, responseBody);
	        
	      //  Log.e("alex", "FusionCookieURL status" + status);
	        
	        if (status >= 400)
	        {
	        	//Log.e("alex",  "sendWallaReportEventAsync Error status: "  + status);
	        	// we assume that the response body contains the error message
	            //Log.d("RequestQueue", "Download error. url = " + queueItem.url);
	        }
	        else
	        {
	        	//Log.e("alex",  "FusionCookieURL sendWallaReportEventAsync Success status: "  + status);
	        	printCookies();
	        	//Log.d("RequestQueue", "download done");
	           
	        }
	    }

	    @Override
	    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error)
	    {
	        super.onFailure(statusCode, headers, responseBody, error);
	        Log.e("alex",  "FusionCookieURL sendWallaReportEventAsync onFailure : "  + error);
	    }
      });

	}
	
//	private static void updateFusionCookie(HttpResponse response)
//	{
//		try
//		{
//			Log.e("alex", "sendWallaReportEvent updateFusionCookie start"); 
//		    Header[] cookieHeaders = response.getHeaders("Set-Cookie");
//		    if (cookieHeaders.length > 0)
//		    {
//		        String cookieValue = cookieHeaders[0].getValue();
//		        if (cookieValue.length() > 0)
//		        {
//		            SharedPreferences prefs = _app.getSharedPreferences(Definitions.WALLA_COOKIE_NAME, Context.MODE_PRIVATE);
//		            SharedPreferences.Editor editor = prefs.edit();
//		            editor.putString(Definitions.WALLA_COOKIE_NAME, cookieValue);
//		            editor.commit();		            
//		            
//		            Log.e("alex", "WallaFusion11 set update: " + cookieValue);
//		        }
//		    }
//		    else
//		    {
//		    	Log.e("alex", "WallaFusion11 NO COOKIES!!!!!!!!!!!!");
//		    }
//		    
//		    Log.e("alex", "sendWallaReportEvent updateFusionCookie end"); 
//		}
//		catch(Exception ex)
//		{
//			Log.e("alex", "sendWallaReportEvent updateFusionCookie Error: " + ex); 
//		}
//	}
	
//	private static HttpGet setFusionCookie(HttpGet request)
//	{
//		try
//		{
//		    Log.e("alex", "sendWallaReportEvent setFusionCookie start"); 
//			
//		    SharedPreferences prefs = _app.getSharedPreferences(Definitions.WALLA_COOKIE_NAME, Context.MODE_PRIVATE);
//		    String fusionCookie = "Fusion.testCookie=TestCookieValue;" + prefs.getString(Definitions.WALLA_COOKIE_NAME, "");
//		    request.addHeader("Cookie", fusionCookie);
//		    
//		    Log.e("alex", "WallaFusion11 set cookie: " + prefs.getString(Definitions.WALLA_COOKIE_NAME, ""));	  
//		    
//		   // Log.e("alex", "sendWallaReportEvent setFusionCookie end: " + prefs.getString(Definitions.WALLA_COOKIE_NAME, ""));	    
//	    
//		}
//		catch(Exception ex)
//		{
//			Log.e("alex", "sendWallaReportEvent setFusionCookie Error: " + ex); 
//		}
//		
//		return request;
//	}
	
	public static void setStartCookie(Context context)
	{
		//Log.e("alex", "setStartCookie: 1");
		
		try
		{
			httpClient = HttpClientWallaSingleton.getInstance();
			
			if(httpClient == null){
				Log.e("alex", "httpClient is null (setStartCookie)");	
			}
			else
			{
				//Log.e("alex", "httpClient is NOT null (setStartCookie)");
			}
			
			mCookieStore = new PersistentCookieStore(context);
			BasicClientCookie newCookie = new BasicClientCookie(Definitions.WALLA_COOKIE_NAME, "TestCookieValue");
			newCookie.setVersion(1);
			newCookie.setDomain("fus.walla.co.il");
			newCookie.setPath("/");
			mCookieStore.addCookie(newCookie);
			
			//DefaultHttpClient mHttpClient=new DefaultHttpClient();
		    //DefaultHttpClient httpClient = HttpClientWallaSingleton.getInstance();			
			
			httpClient.setCookieStore(mCookieStore);
			
			//Log.e("alex", "setStartCookie: 2");			
			
		}
		catch(Exception ex)
		{
			Log.e("alex", "TestCookies Error: " + ex);
		}
	}
	
	private static void printCookies()
	{
		 List<Cookie> cookies = mCookieStore.getCookies();
	     for (int i = 0; i < cookies.size(); i++)
	     {
	        Cookie cookie = cookies.get(i);
	       // Log.e("alex", "FusionCookie " + cookie.getName() + " = " + cookie.getValue());
	     }
	}
	
	public static void showLiveBox(Map<String, String> m, RelativeLayout rl, Context ctx, Activity act)
	{
		//Log.e("alex", "showLiveBox on Lazy WallaBanner 1");
		
//		if(m == null){
//			Log.e("alex", "showLiveBox Utils.getFakeKingBanner()");
//			Map<String, Map<String, String>> map = Utils.getFakeKingBanner();
//			m = map.get(Definitions.WALLA_SPACE_KING);
//		}
		
		LayoutInflater inflater =  act.getLayoutInflater();
		RelativeLayout rowView = (RelativeLayout)inflater.inflate(R.layout.row_layout_live_box, null);
		drawWallaBanner(rowView, ctx, m);
		
		rl.addView(rowView);
		rl.setVisibility(View.VISIBLE);
		//rowView.setPadding(0, 20, 0, 20);
		
		//Log.e("alex", "showLiveBox on Lazy WallaBanner 2");
	}

}
