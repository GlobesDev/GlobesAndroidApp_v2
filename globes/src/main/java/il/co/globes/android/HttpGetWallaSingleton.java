package il.co.globes.android;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.util.Log;

public class HttpGetWallaSingleton
{
	private static HttpGet _instance = null;
	
	private HttpGetWallaSingleton() { }
    
    public static HttpGet getInstance() {
		if (_instance == null) {
			_instance = new HttpGet();
			
			Log.e("alex", "HttpGetWallaSingleton init: null");
			
			//HttpParams httpParams = instance.getParams();
			//HttpConnectionParams.setConnectionTimeout(httpParams, Values.TIMEOUT);
			//HttpConnectionParams.setSoTimeout(httpParams, Values.TIMEOUT);
		}
		else
		{
			Log.e("alex", "HttpGetWallaSingleton init: not null");
		}
		return _instance;
	}
}
