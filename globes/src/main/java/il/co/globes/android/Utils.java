package il.co.globes.android;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.support.v4.util.ArrayMap;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import com.google.android.gms.analytics.Tracker;
import com.google.gson.Gson;
import il.co.globes.android.objects.AjillionObj;
import il.co.globes.android.parsers.GlobesAllWithMapSAXHandler;
import net.tensera.sdk.api.TenseraApi;
import net.tensera.sdk.api.TenseraResponseStream;
import net.tensera.sdk.fetch.CacheMode;
import org.apache.http.*;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.*;
import java.net.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.net.HttpURLConnection.HTTP_OK;

//import com.google.analytics.tracking.android.EasyTracker;
//import com.google.analytics.tracking.android.Fields;
//import com.google.analytics.tracking.android.MapBuilder;
//import com.google.analytics.tracking.android.Tracker;

public class Utils {

    // public final GoogleAnalyticsTracker tracker =
    // GoogleAnalyticsTracker.getInstance();

    public static void CopyStream(InputStream is, OutputStream os) {
        final int buffer_size = 1024;
        try {
            byte[] bytes = new byte[buffer_size];
            for (; ; ) {
                int count = is.read(bytes, 0, buffer_size);
                if (count == -1) break;
                os.write(bytes, 0, count);
            }
        } catch (Exception ex) {
        }
    }

    public static Bitmap DownloadImage(String URL) // throws Exception
    {
        Bitmap bitmap = null;
        InputStream in = null;
        boolean wasAttemptSuccessful = false;
        for (int attempt = 0; !wasAttemptSuccessful && attempt < 4; attempt++) {
            TenseraResponseStream tenseraResponseStream = null;
            if (AppRegisterForPushApps.enableTensera) {
                tenseraResponseStream = TenseraApi.fetchUrl(URL, new ArrayMap<String, String>(), CacheMode.CACHE_OTHERWISE_NETWORK);
                if (tenseraResponseStream != null) {
                    bitmap = BitmapFactory.decodeStream(tenseraResponseStream.stream);
                    wasAttemptSuccessful = true;
                    tenseraResponseStream.close();
                }
            }
            if (tenseraResponseStream == null) {
                // tensera inactive or failed to load content, use legacy code:
                try {
                    in = OpenHttpConnection(URL);
                    bitmap = BitmapFactory.decodeStream(in);
                    if (in != null) {
                        in.close();
                    }
                } catch (IOException e1) {

                    if (in != null) {
                        try {
                            in.close();
                            Thread.sleep(20);
                        } catch (Exception e) {

                        }
                    }
                }
            }
        }
        if (!wasAttemptSuccessful) {
            // throw new Exception();
        }
        return bitmap;
    }

    public static InputStream OpenHttpConnection(String urlString) throws IOException {
        // this function called if tensera is inactive or failed
        InputStream in = null;
        int response = -1;

        URL url = new URL(urlString);

        // tensera inactive or failed to download content, use legacy code
        URLConnection conn = url.openConnection();

        if (!(conn instanceof HttpURLConnection)) throw new IOException("Not an HTTP connection");

        HttpURLConnection httpConn = (HttpURLConnection) conn;
        httpConn.setAllowUserInteraction(false);
        httpConn.setInstanceFollowRedirects(true);
        httpConn.setRequestMethod("GET");
        httpConn.setConnectTimeout(4000);
        httpConn.setReadTimeout(6000);

        httpConn.connect();

        response = httpConn.getResponseCode();
        if (response == HTTP_OK) {
            in = httpConn.getInputStream();
        }

        return in;
    }

    /**
     * @param url
     * @param handler
     * @throws Exception
     */
    public static void parseXmlFromUrlUsingHandler(URL url, DefaultHandler handler) throws Exception {
        Log.e("alex", "parseXmlFromUrlUsingHandler: " + url);
        url = removeMultipleHTTP(url);
        SAXParserFactory spf = SAXParserFactory.newInstance();
        SAXParser sp = spf.newSAXParser();
        XMLReader xr = sp.getXMLReader();
        boolean wasAttemptSuccessful = false;
        // We will retry up to 10 times.
        for (int attempt = 0; !wasAttemptSuccessful && attempt < 4; attempt++) {
            try {
                if (url == null) {
                    return;
                }

                InputSource source = null;
                TenseraResponseStream tenseraResponseStream = null;
                if (AppRegisterForPushApps.enableTensera) {
                    tenseraResponseStream = TenseraApi.fetchUrl(url.toString(), new ArrayMap<String, String>(), CacheMode.CACHE_OTHERWISE_NETWORK);
                    if ((tenseraResponseStream != null) && (tenseraResponseStream.responseCode == 200)) {
                        source = new InputSource(tenseraResponseStream.stream);
                    }
                }
                if (source == null) {
                    // Tensera is inactive or failed to download
                    URLConnection connection = url.openConnection();
                    connection.setConnectTimeout(Definitions.ConnectTimeout + attempt * 600);
                    connection.setReadTimeout(Definitions.readTimeout + attempt * 600);
                    source = new InputSource(connection.getInputStream());
                }
                // final GoogleAnalyticsTracker tracker =
                // GoogleAnalyticsTracker.getInstance();
                xr.setContentHandler(handler);
                xr.parse(source);
                wasAttemptSuccessful = true;
                if (tenseraResponseStream != null) {
                    tenseraResponseStream.close();
                }

                //Log.e("alex", "PARSER!: " + url);
                // TODO problem with tracker here
                /* Tracking */
                // if (!theUrl.contains("total-media.net"))
                // {
                // /*
                // * track URL call with google analytics - Eviatar case
                // * coming from notifications we replace the url
                // * http://www.globes
                // * .co.il/data/webservices/library.asmx/NodeArticleHeaders
                // * ?node_id=607
                // */
                // // android.os.Debug.waitForDebugger();
                // if (Definitions.isComingFromPush())
                // {
                // Definitions.setComingFromPush(false);
                // String urlToTrackWithPush =
                // theUrl.replaceAll("http://www.globes.co.il/data/webservices/library.asmx",
                // Definitions.URLprefix + "push-notification");
                // tracker.trackPageView(urlToTrackWithPush);
                // }
                // else if
                // (theUrl.contains("http://www.globes.co.il/data/webservices/m.asmx/All")
                // && handler.getClass() == MainSAXHandler.class)
                // {
                // tracker.trackPageView(Definitions.URLMainScreenTracking);
                // }
                // else if
                // (theUrl.contains("http://www.globes.co.il/data/webservices/m.asmx/All")
                // && handler.getClass() == SectionsMainPageSAXHandler.class)
                // {
                //
                // tracker.trackPageView(theUrl.replaceAll("http://www.globes.co.il/data/webservices/",
                // Definitions.URLprefix
                // + "MadorList/")); // all mador list
                // }
                // else if (theUrl.contains(Definitions.URLDocument))
                // {
                // tracker.trackPageView(theUrl.replaceAll("http://www.globes.co.il/data/webservices/",
                // Definitions.URLprefix));
                // }
                // else if (theUrl.contains(Definitions.URLSections)) //
                // specific
                // // mador
                // {
                // tracker.trackPageView(theUrl.replaceAll("http://www.globes.co.il/data/webservices/",
                // Definitions.URLprefix
                // + "Mador/"));
                // }
                // else if (theUrl.contains(Definitions.URLMarkets +
                // Definitions.URLMarketsAllGroups)) // markets
                // // tab
                // {
                // tracker.trackPageView(theUrl.replaceAll("http://www.globes.co.il/data/webservices/",
                // Definitions.URLprefix
                // + "markets/"));
                // }
                // else if (theUrl.contains(Definitions.URLMarkets +
                // "index.instruments&feeder")) // specific
                // // market
                // {
                // tracker.trackPageView(theUrl.replaceAll("http://www.globes.co.il/data/webservices/",
                // Definitions.URLprefix
                // + "markets/"));
                // }
                // else if (theUrl.contains(Definitions.URLMarketsIndex))
                // {
                // tracker.trackPageView(Definitions.URLprefix + "markets/" +
                // theUrl);
                // }
                // else if (theUrl.contains(Definitions.URLInstrumentToShare))
                // {
                // tracker.trackPageView(theUrl + "from=" +
                // Definitions.SHAREsuffix);
                // }
                // else if
                // (theUrl.contains("http://www.globes.co.il/data/webservices/instruments.ashx?contains="))
                // {
                // tracker.trackPageView(Definitions.URLprefix + "SearchStock");
                // }
                // else if
                // (theUrl.contains(Definitions.APILocation_ArticleResponses))
                // {
                // tracker.trackPageView(theUrl.replaceAll("http://www.globes.co.il/data/webservices/",
                // Definitions.URLprefix));
                // }
                // else if
                // (theUrl.contains("http://www.globes.co.il/data/webservices/financial.asmx/getInstrumentById?for=mobile&source"))
                // {
                // tracker.trackPageView(theUrl.replaceAll("http://www.globes.co.il/data/webservices/",
                // Definitions.URLprefix));
                // }
                // else if
                // (theUrl.contains("http://www.globes.co.il/data/webservices/searcher.asmx/get_documents?query"))
                // {
                // tracker.trackPageView(theUrl.replaceAll("http://www.globes.co.il/data/webservices/",
                // Definitions.URLprefix));
                // }
                // else if
                // (theUrl.contains("http://www.globes.co.il/data/webservices/portfolios.asmx/GetPortfolios?"))
                // {
                // tracker.trackPageView(theUrl.replaceAll("http://www.globes.co.il/data/webservices/",
                // Definitions.URLprefix));
                // }
                // else if (theUrl.contains(Definitions.URLClips))
                // {
                // tracker.trackPageView(theUrl.replaceAll("http://www.globes.co.il/data/webservices/",
                // Definitions.URLprefix));
                // }
                // else
                // {
                // tracker.trackPageView(url.toString());
                // }
                // }
            } catch (Exception e) {
                Log.e("eli", "Attempt=" + attempt, e);
                Thread.sleep(20);
            }
        }
        if (!wasAttemptSuccessful) {
            throw new Exception();
        }
    }

    public static String resolveRedirect(String url, boolean trackWithAnalytics) throws ClientProtocolException, IOException {
        url = removeMultipleHTTP(new URL(url)).toString();
//Log.e("alex", "removeMultipleHTTP: " + url);
        if (Definitions.toUseGIMA) {
            if (url.startsWith("http://www.cast-tv.biz/")) {
                url += Definitions.VIDEO_DP;
            }
            Log.e("eli", "resolveRedirect " + url);
            return url;
        }
        HttpParams httpParameters = new BasicHttpParams();
        HttpClientParams.setRedirecting(httpParameters, false);

        HttpClient httpClient = new DefaultHttpClient(httpParameters);
        HttpGet httpget = new HttpGet(url);

        // TODO problem with tracker
        // if (trackWithAnalytics)
        // {
        // GoogleAnalyticsTracker tracker;
        // tracker = GoogleAnalyticsTracker.getInstance();
        // tracker.trackPageView(url);
        // }
        if (!Definitions.mediaPlayerInBrowser) {
            httpget.setHeader("User-Agent", "android");
        } else {
            httpget.setHeader("User-Agent", "mozilla");
        }

        HttpContext context = new BasicHttpContext();

        HttpResponse response = httpClient.execute(httpget, context);

        // If we didn't get a '302 Found' we aren't being redirected.
        // if (response.getStatusLine().getStatusCode() !=
        // HttpStatus.SC_MOVED_TEMPORARILY)
        // throw new IOException(response.getStatusLine().toString());

        Header loc[] = response.getHeaders("Location");
        final String TAG = "Utils";
        Log.d(TAG, "loc length is: " + loc.length);

        return loc.length > 0 ? loc[0].getValue() : null;
    }

    public static String getBannerParameters(Context context, String bannerURL, int width, int height) {
        String android_id = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
        bannerURL = bannerURL.replace("XXXX", android_id).replace("YYYY", Integer.toString((int) (Math.random() * 1000000000)));
        bannerURL = bannerURL.replace("WWWW", Integer.toString(width)).replace("HHHH", Integer.toString(height));
        return bannerURL;
    }

    /**
     * Creates a Bundle with the following params
     *
     * @param caller     caller
     * @param parser     parser
     * @param URItoParse the uriToParse to parse
     * @return {@link Bundle} assembled from params
     */
    public static Bundle createBundleFromArgs(String caller, String parser, String URItoParse) {
        Bundle b = new Bundle();
        b.putString(Definitions.CALLER, caller);
        b.putString(Definitions.PARSER, parser);
        b.putString(Definitions.URI_TO_PARSE, URItoParse);
        return b;
    }

    /**
     * By Eli - Save string in shared preferences.
     *
     * @param c    - context of activity {@link Context}.
     * @param id   - the id of the string.
     * @param text - the string you want to save.
     * @return boolean - if string successfully saved.
     */
    public static boolean putStringInShardPref(Context c, String id, String text) {
        SharedPreferences sharedpreferences = c.getSharedPreferences("GLOBES", Context.MODE_PRIVATE);
        Editor editor = sharedpreferences.edit();
        editor.putString(id, text);
        return editor.commit();
    }

    /**
     * By Eli - Get string from shared preferences.
     *
     * @param c  - context of activity {@link Context}.
     * @param id - the id of the string that you want to retrieve.
     * @return String - the string, or "" if not exist
     */
    public static String getStringFromShardPref(Context c, String id) {
        SharedPreferences sharedpreferences = c.getSharedPreferences("GLOBES", Context.MODE_PRIVATE);
        return sharedpreferences.getString(id, "");
    }

    /**
     * By Eli - check if string is in shared preferences.
     *
     * @param c  - context of activity {@link Context}.
     * @param id - the id of the string that you want to check.
     * @return boolean - if string exist.
     */
    public static boolean isContainsInShardPref(Context c, String id) {
        SharedPreferences sharedpreferences = c.getSharedPreferences("GLOBES", Context.MODE_PRIVATE);
        return sharedpreferences.contains(id);
    }

    // public static void sendGemiusData(Activity activity)
    // {
    // Intent intent = new Intent("com.gemius.sdk.MOBILEPLUGIN");
    // intent.putExtra(MobilePlugin.IDENTIFIER, Definitions.GEMIUS_ID);
    // activity.startService(intent);
    // }

    public static void writeEventToGoogleAnalytics(Context a, String category, String action, String label) {
        Log.i("eli", "GA-EVENT: category: " + category + ", action: " + action + ", label: " + label);
//
//        try
//		{
//			EasyTracker easyTracker = EasyTracker.getInstance(a);
//			if (easyTracker != null)
//			{
//				easyTracker.send(MapBuilder.createEvent(category /* required */, action /* required */, label, null).build());
//			}
//		}
//		catch (Exception e)
//		{
//			Log.e("GoogleAnalytics", "writeEventToGoogleAnalytics ", e);
//		}
    }

    public static void writeScreenToGoogleAnalytics(Context a, String value) {
        Log.i("eli", "GA-SCREEN: " + value);
//		try
//		{
//			Tracker easyTracker = EasyTracker.getInstance(a);
//			if (easyTracker != null && value != null && value.length() > 0)
//			{
//				easyTracker.set(Fields.SCREEN_NAME, value);
//				easyTracker.send(MapBuilder.createAppView().build());
//			}
//		}
//		catch (Exception e)
//		{
//			Log.e("GoogleAnalytics", "writeScreenToGoogleAnalytics ", e);
//		}
    }

    private static URL removeMultipleHTTP(URL url) {
        try {
            String http = "http://";
            String urlStr = url.toString();
            int lastHTTP = urlStr.lastIndexOf(http);
            urlStr = urlStr.substring(lastHTTP);
            try {
                return new URL(urlStr);
            } catch (MalformedURLException e) {
                return url;
            }
        } catch (Exception e) {
            return url;
        }
    }

    public static String getUUIDForPush(Context c) {
        // The device id for push GCM
        TelephonyManager telephonyManager = (TelephonyManager) c.getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager.getDeviceId();
    }

    public static void reloadGlobesURLs(final Context c) {
        GlobesAllWithMapSAXHandler globesAllWithMapSAXHandler = new GlobesAllWithMapSAXHandler();

        URL url;
        try {
            String android_id = Secure.getString(c.getApplicationContext().getContentResolver(), Secure.ANDROID_ID);
            Log.e("eli", "device id: " + android_id);
            PackageInfo pInfo = c.getPackageManager().getPackageInfo(c.getPackageName(), 0);
            String version = pInfo.versionName;

            if (Definitions.MapURLMode == Definitions.MAP_URL_MODE_DEBUG) {
                url = new URL(Definitions.URLGlobesAllWithMap + "&UDID=" + android_id + "&mapMode=debug&ver=" + version);
            } else {
                url = new URL(Definitions.URLGlobesAllWithMap + "&UDID=" + android_id + "&mapMode=prod&ver=" + version);
            }

            Log.e("alex", "ReloadGlobesURLs() " + url);
            Utils.parseXmlFromUrlUsingHandler(url, globesAllWithMapSAXHandler);

        } catch (MalformedURLException e) {
            Log.e("eli", "Error getGlobesURLs(): " + e.getMessage());
        } catch (Exception e) {
            Log.e("eli", "Error getGlobesURLs(): " + e.getMessage());
        }
    }

//	public static void reloadGlobesURLs(final Context c)
//	{
//		GlobesMapSAXHandler globesMapSAXHandler = new GlobesMapSAXHandler();
//		URL url;
//		try
//		{
//			String android_id = Secure.getString(c.getApplicationContext().getContentResolver(), Secure.ANDROID_ID);
//			Log.e("eli", "device id: " + android_id);
//			PackageInfo pInfo = c.getPackageManager().getPackageInfo(c.getPackageName(), 0);
//			String version = pInfo.versionName;
//
//			url = new URL(Definitions.URLGlobesMap + "UDID=" + android_id + "&mode=prod&ver=" + version);
//			//Log.e("alex","Parser2: " + url);
//			Utils.parseXmlFromUrlUsingHandler(url, globesMapSAXHandler);
//		}
//		catch (MalformedURLException e)
//		{
//			Log.e("eli", "getGlobesURLs(): " + e.getMessage());
//			e.printStackTrace();
//		}
//		catch (Exception e)
//		{
//			Log.e("eli", "getGlobesURLs(): " + e.getMessage());
//			e.printStackTrace();
//		}
//	}

    public static String getSubStringByValue(String row, String value) {
        try {
            Log.e("eli", row);
            String mydata = row;
            Pattern pattern = Pattern.compile("\\?" + value + "=(.*?)&");
            Matcher matcher = pattern.matcher(mydata);
            if (matcher.find()) {
                return matcher.group(1);
            }
            return "unknown";
        } catch (Exception e) {
            return "unknown";
        }
    }

    public static String getSubStringByValueAd(String row, String startWith, String endWith) {
        try {
            String mydata = row;
            Pattern pattern = Pattern.compile(startWith + "(.*?)" + endWith);
            Matcher matcher = pattern.matcher(mydata);
            if (matcher.find()) {
                return matcher.group(1);
            }
            return "unknown";
        } catch (Exception e) {
            return "unknown";
        }
    }

    public static AjillionObj getAjillionObj(Context context, String id, int width, int height, boolean isLiveBox)

    {
        String url = "";
        if (isLiveBox) {

            url = "http://ad.ajillionmax.com/ad/" + id + "/4?format=json&MediaType=App&uid=" + getDeviceId(context);

        } else {

            url = "http://ad.ajillionmax.com/ad/" + id + "/4?format=json&size=" + width + "x" + height + "&uid=" + getDeviceId(context);
        }

        Log.e("eli", "getAjillionObj " + url);
        InputStream source = retrieveStream(url);
        Gson gson = new Gson();
        if (source != null) {
            Reader reader = new InputStreamReader(source);
            AjillionObj ajillionObj = gson.fromJson(reader, AjillionObj.class);
            return ajillionObj;
        } else {
            return new AjillionObj();
        }
    }

    private static InputStream retrieveStream(String url) {
        DefaultHttpClient client = new DefaultHttpClient();
        HttpGet getRequest = new HttpGet(url);
        try {
            HttpResponse getResponse = client.execute(getRequest);
            final int statusCode = getResponse.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK) return null;
            HttpEntity getResponseEntity = getResponse.getEntity();
            return getResponseEntity.getContent();
        } catch (IOException e) {
        }
        return null;
    }

    private static String getDeviceId(Context context) {
        String deviceId = "";
        final TelephonyManager mTelephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (mTelephony.getDeviceId() != null) {
            deviceId = mTelephony.getDeviceId();
        } else {
            deviceId = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
        }
        return deviceId;
    }

    // public static String getFromSharedPref(Context c, String id)
    // {
    // SharedPreferences sp =
    // PreferenceManager.getDefaultSharedPreferences(c.getApplicationContext());
    // return sp.getString(id, "");
    // }
    //
    // public static void putIntoSharedPref(Context c, String id, String val)
    // {
    // SharedPreferences appSharedPrefs =
    // PreferenceManager.getDefaultSharedPreferences(c.getApplicationContext());
    // Editor prefsEditor = appSharedPrefs.edit();
    // prefsEditor.putString(id, val);
    // prefsEditor.commit();
    // }

    public static void centerViewInRelative(View v, boolean isJpeg) {
        RelativeLayout.LayoutParams layoutParams;
        if (isJpeg) {
            layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT);
        } else {
            layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT);
        }
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        v.setLayoutParams(layoutParams);
    }

    public static String centerHTML(String html, boolean isGif) {
        if (!isGif) {
            html = "<div style=\"text-align:center;\">" + html + "</div>";
        } else {
            html = "<img width=\"100%\" height=\"50\" src=" + html + "><img/>";
        }
        return html;
    }

    public static String centerHTMLWithSize(String html, int width, int height) {
        return "<div align=\"center\"><img src=\"" + html + "\" width=\"" + width + "\" height=\"" + height + "\" /></div>";
        //return "<div align=\"center\"><img src=\"" + html + "\" width=\"100%\" height=\"" + height + "\" /></div>";
    }

    public static String getJsonData(String url) {
        Log.e("alex", "GetJsonData_1 (start loading...)");

        HttpParams params = new BasicHttpParams();
        params.setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);

        HttpConnectionParams.setTcpNoDelay(params, true);
        DefaultHttpClient httpclient = new DefaultHttpClient(params);
        HttpPost httppost = new HttpPost(url);
        httppost.setHeader("Content-type", "application/json");
        httppost.setHeader("User-agent", "android");
        //Log.e("alex","ParsingJsonStart_2_2");

        InputStream inputStream = null;
        String result = "";
        try {
            HttpResponse response = httpclient.execute(httppost);
            //Log.e("alex","ParsingJsonStart_2_3");
            HttpEntity entity = response.getEntity();
            //Log.e("alex","ParsingJsonStart_2_4");
            inputStream = entity.getContent();
            //BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            //Log.e("alex","ParsingJsonStart_2_5");
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                //sb.append(line + "\n");
                sb.append(line);
            }
            result = sb.toString();

            //Log.e("alex","ParsingJsonStart_2_6");
        } catch (Exception e) {
            Log.e("alex", "Parsing JSon Error: " + e);
        } finally {
            try {
                if (inputStream != null) inputStream.close();
            } catch (Exception ex) {
                Log.e("alex", "getJsonData Error: " + ex);
            }
        }

        Log.e("alex", "GetJsonData_2 (end loading...)");
        return result;
    }

    public static String getJSONString(String urlString) {
        StringBuilder builder = new StringBuilder();
        HttpClient client = new DefaultHttpClient();

        HttpGet httpGet = new HttpGet(urlString);
        try {
            HttpResponse response = client.execute(httpGet);
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            if (statusCode == 200) {
                HttpEntity entity = response.getEntity();
                InputStream content = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
            } else {
                Log.e("alex", "Failed to download json response");
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return builder.toString();
    }

    public static Map<String, String> splitQuery(String query) throws UnsupportedEncodingException {
        Map<String, String> query_pairs = new LinkedHashMap<String, String>();
        try {
            String[] pairs = query.split("&");
            for (String pair : pairs) {
                int idx = pair.indexOf("=");
                query_pairs.put(URLDecoder.decode(pair.substring(0, idx), "UTF-8"), URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
            }
        } catch (Exception e) {
            Log.e("alex", "FOLDERID ERROR2:" + e);
        }
        return query_pairs;
    }

    public static String[] GetWallaDataForSpace(int folder) {
        String[] arrData = new String[2];

        if (folder > 0) {
            switch (folder) {
                case 1225:
                    arrData[0] = "wallstreetwordmarket";
                    arrData[1] = "l_globes_app_c";
                    break;
                case 585:
                    arrData[0] = "capitalmarket";
                    arrData[1] = "l_globes_app_c";
                    break;
                case 607:
                    arrData[0] = "realestate";
                    arrData[1] = "l_globes_app_c";
                    break;
                case 2007:
                    arrData[0] = "tv";
                    arrData[1] = "l_globes_app_c";
                    break;
                case 594:
                    arrData[0] = "tech";
                    arrData[1] = "l_globes_app_c";
                    break;
                case 821:
                    arrData[0] = "consumerism";
                    arrData[1] = "l_globes_app_c";
                    break;
                case 829:
                    arrData[0] = "law";
                    arrData[1] = "l_globes_app_c";
                    break;
                case 4621:
                    arrData[0] = "oil";
                    arrData[1] = "l_globes_app_c";
                    break;
                case 3266:
                    arrData[0] = "career";
                    arrData[1] = "l_globes_app_c";
                    break;
                case 2605:
                    arrData[0] = "sport";
                    arrData[1] = "l_globes_app_c";
                    break;
                case 3317:
                    arrData[0] = "leisure";
                    arrData[1] = "l_globes_app_c";
                    break;
                case 3220:
                    arrData[0] = "cars";
                    arrData[1] = "l_globes_app_c";
                    break;
                case 845:
                    arrData[0] = "opinion";
                    arrData[1] = "l_globes_app_c";
                    break;
                default:
                    arrData[0] = "homepage";
                    arrData[1] = "l_globes_app_h";
                    break;

            }
        }

        return arrData;

    }

    public static String GetFolderNameByID(int id) {
        String sRes = "";

        if (id > 0) {
            switch (id) {
                case 2:
                    sRes = "ראשי";
                    break;
                case 585:
                    sRes = "שוק ההון";
                    break;
                case 1225:
                    sRes = "וול סטריט ושוקי עולם";
                    break;
                case 607:
                    sRes = "נדל\"ן ותשתיות";
                    break;
                case 2007:
                    sRes = "גלובס TV";
                    break;
                case 594:
                    sRes = "היי-טק תקשורת ואינטרנט";
                    break;
                case 821:
                    sRes = "נתח שוק וצרכנות";
                    break;
                case 829:
                    sRes = "דין וחשבון";
                    break;
                case 4621:
                    sRes = "גז ונפט";
                    break;
                case 3266:
                    sRes = "קריירה ויזמות";
                    break;
                case 2605:
                    sRes = "עסקי ספורט";
                    break;
                case 3317:
                    sRes = "פנאי";
                    break;
                case 3220:
                    sRes = "רכב";
                    break;
                case 845:
                    sRes = "דעות";
                    break;
                default:
                    sRes = "ראשי";
                    break;
            }

        }

        return sRes;
    }

    public static boolean tryParseInt(String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }

    public static boolean tryParseDouble(String value) {
        try {
            Double.parseDouble(value);
            return true;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }

    public static String[] getWallaDataForFolderL(String url) {
        String[] arrData = new String[2];
        try {
            Uri uri = Uri.parse(url);
            String sFolderID = uri.getQueryParameter("node_id");

            if (tryParseInt(sFolderID)) {
                int iFolderID = Integer.parseInt(sFolderID);
                arrData = GetWallaDataForSpace(iFolderID);
            }
        }
        catch(Exception ex){}
        return arrData;
    }

    public static String getVersionName(Context c) {
        String versionName = "";
        try {
            PackageInfo pInfo = c.getPackageManager().getPackageInfo(c.getPackageName(), 0);
            versionName = pInfo.versionName;
        } catch (NameNotFoundException e) {
            versionName = "";
        }
        return versionName;
    }

    public static Map<String, Map<String, String>> getFakeKingBanner() {
        Map<String, Map<String, String>> map = new HashMap<String, Map<String, String>>();
        Map<String, String> mapAttributes = new HashMap<String, String>();
        String Payload = "<!doctype html> <html> <head> <meta content=\"width=device-width,minimum-scale=1.0,maximum-scale=1.0,user-scalable=no\" name=\"viewport\"> </head> <body style=\"text-align: center;font-family:Arial,Helvetica,sans-serif;font-size:10pt;margin:0\"> <script type=\"text/javascript\"> var fusionVars = { 'ADID': '3830059881', 'ADTYPE': 'Inline', 'CONTENTTYPE':Html, 'ADURL_01':'', 'WIDTH_01': '320', 'HEIGHT_01': '250', 'CLICKURL':'', 'CLICKREPORT': 'event/rucmm/mint.globes_app_android.article/3830059881/click', 'ADSERVER': 'http://fus.walla.co.il:80/', 'ADSRV': 'http://fus.walla.co.il/', 'VIEWREPORT': 'impression/rucmm/mint.globes_app_android.article/l_globes_app_a/3830059882', 'BGCOLOR':000000, 'CONTENTID':, 'DELAYTIME':'100', 'EXTERNALBROWSER':'True' } </script> <!--advertiser tag goes here--> <center> <div id='aniplayer'></div> <script type=\"text/javascript\" id=\"aniviewJS\"> var adConfig = { publisherID : '41751', channelID : '775986', GetVASTRetry :5, pause : false, loop : true, logo : false, loopVideo : true, autoClose : false, contentID : '', closeButton : false, showCloseButon :5, skipText : '', pauseButton : true, soundButton : true, fullScreenButton : false, passBackUrl : '', isSmart : false, playerType : 2, clickTracking : 'https://www.tapuchips.co.il/?utm_source=walla_mobile&utm_medium=cpm&utm_campaign=king', subExternalID :'', ref1 :'', cdnUrl :'http://d41751.ani-view.com/', backgroundImageUrl:'', backgroundColor:'#FFF', position :'aniplayer' }; var PlayerUrl = 'http://eu.ani-view.com/script/1/aniview.js'; function downloadScript(src,adData) { var scp = document.createElement('script'); scp.src = src; scp.onload = function() { var myPlayer= new aniviewPlayer; myPlayer.onClick = function() {(new Image).src ='event/rucmm/mint.globes_app_android.article/3830059881/click';}; myPlayer.onPlay= function () { }; myPlayer.play(adConfig); }; document.getElementsByTagName('head')[0].appendChild(scp); } downloadScript(PlayerUrl,adConfig); </script> </center> <!--ends here--> </body> </html>";

        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        Date date = new Date();
        String sCD = dateFormat.format(date);

        if (sCD.startsWith("20150609")) {
            mapAttributes.put("ADID", "3838117185");
            mapAttributes.put("ADSERVER", "http://fus.walla.co.il:80/");
            mapAttributes.put("ADSRV", "http://fus.walla.co.il/");
            mapAttributes.put("ADTYPE", "ADTYPE");
            mapAttributes.put("CLICKREPORT", "event/mwuul/mint.globes_app_android.homepage/3838117185/click");
            mapAttributes.put("BGCOLOR", "000000");
            mapAttributes.put("CONTENTTYPE", "Image");
            mapAttributes.put("DELAYTIME", "100");
            mapAttributes.put("EXTERNALBROWSER", "False");
            mapAttributes.put("Payload", Payload);
            mapAttributes.put("TRANSPARENT", "False");
            mapAttributes.put("VIEWREPORT", "impression/mwuul/mint.globes_app_android.homepage/l_globes_app_h/3838117186");
        }

        map.put(Definitions.WALLA_SPACE_KING, mapAttributes);

        return map;
    }

}
