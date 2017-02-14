package il.co.globes.android;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.json.JSONObject;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;


public class UtilsWebServices {

	private final static int BUFFER_SIZE = 10240;
	private final static int HTTP_TIME_OUT = 5000;
	private final static int HTTP_SOCKET_TIME_OUT = 5000;
	private static final String TAG = "UtilsWebServices";

	// TODO ************** public methods **************/

	/**
	 * Checks Internet connection via mobile or Wi-Fi
	 * 
	 * @param context
	 *            - avoid memory leak use application context
	 * @return true if connected
	 */
	public static boolean checkInternetConnection(Context context) {
		boolean haveConnectedWifi = false;
		boolean haveConnectedMobile = false;

		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo[] netInfo = cm.getAllNetworkInfo();
		for (NetworkInfo ni : netInfo) {
			if (ni.getTypeName().equalsIgnoreCase("WIFI"))
				if (ni.isConnected())
					haveConnectedWifi = true;
			if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
				if (ni.isConnected())
					haveConnectedMobile = true;
		}
		return haveConnectedWifi || haveConnectedMobile;
	}

	/**
	 * Check the response from server after request.
	 * 
	 * @param json
	 *            - String representation of JSON
	 * @return String response from "ResponseObject" JOSN , or throws exception
	 *         if error or not exists.
	 * @throws Exception
	 *             Response doesn't have ResponseObject ,
	 *             obj.getString("ErrorMessage") from the JSON response.
	 */
	public static String getResponseOrThrowException(String json)
			throws Exception {
		JSONObject obj = new JSONObject(json);

		if (obj.getInt("ResponseStatus") == 1) {

			if (obj.has("ResponseObject")) {
				JSONObject object = obj.getJSONObject("ResponseObject");
				return object.toString();
			}

			throw new Exception("Response doesn't have ResponseObject");
		}
		throw new Exception(obj.getString("ErrorMessage"));
	}

	/**
	 * 
	 * @param url
	 *            - the url
	 * @return - String data from request
	 * @throws Exception
	 */
	public static String getHTTPText(String url) throws Exception {
		byte[] data = getHTTPData(url);
		if (data != null) {
			return new String(data);
		}
		return null;
	}

	/**
	 * performs a HTTP get and saves the data into a chosen file in mode private
	 * (can only be accessed by the this application)
	 * 
	 * @param context
	 *            - use application context to avoid memory leaks
	 * @param url
	 *            - the url
	 * @param fileName
	 *            - the file to write the data into
	 */
	public static void saveHTTPDataToFileModePrivate(Context context,
			String url, String fileName) throws Exception {

		HttpURLConnection urlConnection = null;

		try {
			URL _url = new URL(url);
			urlConnection = (HttpURLConnection) _url.openConnection();
			InputStream in = new BufferedInputStream(
					urlConnection.getInputStream());
			urlConnection.setConnectTimeout(3500);
			urlConnection.setReadTimeout(3500);
			saveStream(context, in, fileName);
		} finally {
			if (urlConnection != null)
				urlConnection.disconnect();
		}

	}

	/**
	 * posts json to server
	 * 
	 * @param url
	 *            - the url
	 * @param data
	 *            - string representation of the JSON data to send
	 * @return String response from server
	 */
	public static String postJson(String url, String data) throws Exception {
		return new String(postHTTPData(url, "text/json", data, HTTP_TIME_OUT));
	}

	/**
	 * posts json to server
	 * 
	 * @param url
	 *            - the url
	 * @param data
	 *            - JSONObject to be posted
	 * @return String response from server
	 * @throws Exception
	 */
	public static String postJson(String url, JSONObject data) throws Exception {

		return new String(postHTTPData(url, "text/json", data.toString(),
				HTTP_TIME_OUT));
	}

	/**
	 * @param url
	 *            - the url
	 * @param contentType
	 *            - constant for the content type e.g. "text/json"
	 * @param data
	 *            - the data to send respectively
	 */
	public static String postHTTPData(String url, String contentType,
			String data) throws Exception {
		return new String(postHTTPData(url, contentType, data, HTTP_TIME_OUT));

	}

	/**
	 * @param url
	 *            - the url
	 * @param contentType
	 *            - constant for the content type e.g. "text/json"
	 * @param data
	 *            - the data to send respectively
	 */
	public static String postHTTPDataNoTimeOut(String url, String contentType,
			String data) throws Exception {
		return new String(postHTTPData(url, contentType, data, -1));

	}

	/**
	 * @param url
	 *            - the url
	 * @param contentType
	 *            - constant for the content type
	 * @param params
	 *            - List "<"NameValuePair">"
	 * */
	public static String postHTTPData(String url, String contentType,
			List<NameValuePair> params) throws Exception {

		byte[] returnData = null;
		InputStream is = null;

		try {
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpConnectionParams.setConnectionTimeout(httpClient.getParams(),
					HTTP_TIME_OUT);
			HttpConnectionParams.setSoTimeout(httpClient.getParams(),
					HTTP_SOCKET_TIME_OUT);
			HttpPost post = new HttpPost(url);
			post.setEntity(new UrlEncodedFormEntity(params));
			if (contentType != null) {
				post.addHeader("Content-Type", contentType);
			}
			HttpResponse response = httpClient.execute(post);

			is = response.getEntity().getContent();

			returnData = convertStreamToByteArray(is);

		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (Exception e) {
					e.printStackTrace();
					logMsg(e.getMessage());
				}
			}

		}
		return new String(returnData);

	}

	/**
	 * Downloads a file and writes it into the desired fileName , MODE PRIVATE
	 * 
	 * @param context
	 *            - use application context to avoid memory leaks
	 * @param url
	 *            - the url
	 * @param fileName
	 *            - the desired fileName
	 * 
	 * */
	public static void downloadFileAndSave(Context context, String url,
			String fileName) throws Exception {

		byte[] data = getHTTPData(url);
		if (data != null) {

			UtilsFiles.writeTextFile(context, fileName, new String(data));
		}

	}

	/**
	 * 
	 * @param is
	 *            - the input stream
	 * @return byte[] - the converted byte[] from the stream
	 * @throws Exception
	 */
	public static byte[] convertStreamToByteArray(InputStream is)
			throws Exception {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buffer = new byte[BUFFER_SIZE];
		byte[] result = null;
		int b;
		try {
			while ((b = is.read(buffer, 0, buffer.length)) != -1) {
				baos.write(buffer, 0, b);
			}

			result = baos.toByteArray();

		} finally {
			try {
				baos.close();
			} catch (IOException e) {
				e.printStackTrace();
				logMsg(e.getMessage());
			}
		}
		return result;
	}

	// TODO ************** private methods **************/

	/**
	 * 
	 * @param url
	 *            - the url
	 * @param contentType
	 *            - type of content
	 * @param data
	 *            - string representation of the JSON data to send
	 * @param timeOut
	 *            - http connection timeout (see constant HTTP_TIME_OUT)
	 * @return byte[]
	 */
	private static byte[] postHTTPData(String url, String contentType,
			String data, int timeOut) throws Exception {

		URL _url;
		byte[] returnData = null;
		InputStream is = null;
		URLConnection conn = null;
		OutputStreamWriter wr = null;
		try {
			_url = new URL(url);

			conn = _url.openConnection();
			conn.setDoOutput(true);
			conn.setDoInput(true);

			if (timeOut > 0) {
				conn.setConnectTimeout(timeOut);
				conn.setReadTimeout(timeOut);
			}
			conn.setRequestProperty("Content-Type", contentType);
			wr = new OutputStreamWriter(conn.getOutputStream());
			wr.write(data);
			wr.flush();
			is = conn.getInputStream();
			returnData = convertStreamToByteArray(is);

		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (Exception e) {
					e.printStackTrace();
					logMsg(e.getMessage());
				}
			}
			if (wr != null) {
				try {
					wr.close();
				} catch (Exception e) {
					e.printStackTrace();
					logMsg(e.getMessage());
				}
			}

		}
		return returnData;

	}

	private static byte[] getHTTPData(String url) throws Exception {

		byte[] data = null;
		HttpURLConnection urlConnection = null;

		try {
			URL _url = new URL(url);
			urlConnection = (HttpURLConnection) _url.openConnection();
			InputStream in = new BufferedInputStream(
					urlConnection.getInputStream());
			urlConnection.setConnectTimeout(3500);
			urlConnection.setReadTimeout(3500);
			data = convertStreamToByteArray(in);
		} finally {
			if (urlConnection != null)
				urlConnection.disconnect();
		}
		return data;

	}

	/**
	 * Saves the data into the file (mode private)
	 * 
	 * @param context
	 *            - use application context to avoid memory leaks
	 * @param is
	 *            = the input Stream
	 * @param fileName
	 *            - the file to write the data into
	 */
	private static void saveStream(Context context, InputStream is,
			String fileName) throws Exception {
		byte[] buffer = new byte[10240];
		int count;

		FileOutputStream fos = null;

		try {

			fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);

			while ((count = is.read(buffer, 0, buffer.length)) != -1) {

				fos.write(buffer, 0, count);
				fos.flush();
			}

		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (Exception e) {
					e.printStackTrace();
					logMsg(e.getMessage());
				}
			}
		}
	}

	/**
	 * logs error msg's
	 * 
	 * @param theMsg
	 */
	private static void logMsg(String theMsg) {
		Log.e(TAG, theMsg);
	}

}
