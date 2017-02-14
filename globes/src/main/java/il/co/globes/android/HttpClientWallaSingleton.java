package il.co.globes.android;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;

import com.loopj.android.http.AsyncHttpClient;

import android.util.Log;

public class HttpClientWallaSingleton
{
	private static AsyncHttpClient _instance = null;

	private HttpClientWallaSingleton() { }
        
    public static AsyncHttpClient getInstance() {
		if (_instance == null) {
			_instance = new AsyncHttpClient();
			
			Log.e("alex", "HttpClientWallaSingleton init: null");
			
			//HttpParams httpParams = instance.getParams();
			//HttpConnectionParams.setConnectionTimeout(httpParams, Values.TIMEOUT);
			//HttpConnectionParams.setSoTimeout(httpParams, Values.TIMEOUT);
		}
		else
		{
			Log.e("alex", "HttpClientWallaSingleton init: not null");
		}
		return _instance;
	}

}