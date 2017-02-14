package il.co.globes.android;
import java.util.List;

import com.woorlds.woorldssdk.WoorldsSDK;
import com.woorlds.woorldssdk.server.Segment;

import android.app.Application;
import android.content.Context;
import android.util.Log;

public class WoorldsSDKInit extends Application
{
	private static WoorldsSDK _instance = null;
	private static Context _context;
	public WoorldsSDKInit(Context ctx) {this._context = ctx; }
	
	public static WoorldsSDK getInstance() {
		
		if (_instance == null) {			
			_instance = new WoorldsSDK(_context) {
				@Override
				public void onConnected() 
				{					
					super.onConnected();					
					//long sWVersion = mWoorldsSDK.WOORLDS_SDK_IMPLEMENTATION_VERSION;
					Log.e("alex", "WoorldsSDKInit: is null");						
				}
			};
			
		}
		else
		{
//			String campaignId = _instance.getCampaign();	
			Log.e("alex", "WoorldsSDKInit: is not null!!!");	
		}
		return _instance;
	}
	
	public static void destroy() {
		Log.e("alex", "WoorldsSDK destroy.......");
		_instance.destroy();
		_instance = null;
	}
		
	public static List<Segment> getSegments(String campaingID)
	{
		//String campaingID = _instance.getCampaign();
		
		if(campaingID != null && !campaingID.isEmpty() && !campaingID.equals("[]"))
		{
			return _instance.getSegmentations(campaingID);
		}
		
		return null;
	}
}
