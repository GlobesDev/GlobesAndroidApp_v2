/*
 * Copyright 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package il.co.globes.android;

// ATTENTION this class must stay in package where name is equal to package name 

import il.co.globes.android.objects.GlobesURL;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.telephony.TelephonyManager;
import android.util.Log;

//import com.google.android.gcm.GCMBaseIntentService;

/**
 * IntentService responsible for handling GCM messages.
 */
public class GCMIntentServiceOld /*extends GCMBaseIntentService*/
{
//	private static final String TAG = "GCMIntentService";
//	private static final int REGULAR_DID_LENGTH = 6;
//
//	public GCMIntentService()
//	{
//		super(Definitions.GCM_SENDER_ID);
//		Log.i(TAG, "GCMIntent service created");
//	}
//
//	/*
//	 * (non-Javadoc)
//	 *
//	 * @see
//	 * com.google.android.gcm.GCMBaseIntentService#onRegistered(android.content
//	 * .Context, java.lang.String)
//	 *
//	 * onRegistered(Context context, String regId): Called after a registration
//	 * intent is received, passes the registration ID assigned by GCM to that
//	 * device/application pair as parameter. Typically, you should send the
//	 * regid to your server so it can use it to send messages to this device.
//	 */
//	@Override
//	protected void onRegistered(Context context, String registrationId)
//	{
//		Log.i(TAG, "Device registered: regId = " + registrationId);
//
//		// Intent intent = new Intent(context,
//		// il.co.globes.android.MainActivity.class);
//		// // set intent so it does not start a new activity
//		// intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
//		// Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//		// intent.putExtra("regId", registrationId);
//		// context.startActivity(intent);
//
///*		SharedPreferences 	preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
//		final boolean register = preferences.getBoolean("notificationsPref", true);
//
//		sendRequestToServer(register, registrationId);*/
//
//	}
//	public String getRegistrationPostContent(boolean register,String registrationId)
//	{
//		String versionName = "";
//		TelephonyManager tManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
//		String uid = tManager.getDeviceId();
//		try
//		{
//			versionName = getApplicationContext().getPackageManager().getPackageInfo(getApplicationContext().getPackageName(), 0).versionName;
//		}
//		catch (NameNotFoundException e)
//		{
//			Log.e(TAG, "Error NameNotFoundException @ getRegistrationPostContent ");
//			e.printStackTrace();
//		}
//
//		StringBuffer sb = new StringBuffer();
//		sb.append("{");
//		sb.append("\"ApplicationName\" : \"globes1\"");
//		sb.append(",");
//		sb.append("\"AppVersion\" :" + "\"" + versionName + "\"");
//		sb.append(",");
//		sb.append("\"DeviceID\" :" + "\"" + uid + "\"");
//		sb.append(",");
//		sb.append("\"OSVersion\" : \"" + VERSION.RELEASE + "\"");
//		sb.append(",");
//		sb.append("\"PlatformID\":5");
//		sb.append(",");
//		if (register)
//		{
//			sb.append("\"RegistrationUID\" : \"" + registrationId + "\"");
//		}
//		else
//		{
//			sb.append("\"RegistrationUID\" : \"" + "" + "\"");
//		}
//		sb.append("}");
//		return sb.toString();
//	}
//	private void sendRequestToServer(final boolean register,final String registrationId)
//	{
//
//		new AsyncTask<Void, Void, Void>()
//		{
//
//			@Override
//			protected Void doInBackground(Void... params)
//			{
//				try
//				{
//					if (register)
//					{
//						UtilsWebServices.postHTTPData(Definitions.URL_REGISTER_OR_UPDATE, "text/json", getRegistrationPostContent(true,registrationId));
//					}
//					else
//					{
//						UtilsWebServices.postHTTPData(Definitions.URL_REGISTER_OR_UPDATE, "text/json", getRegistrationPostContent(false,registrationId));
//					}
//				}
//				catch (Exception e)
//				{
//					e.printStackTrace();
//				}
//				return null;
//			}
//
//		}.execute();
//	}
//
//	/*
//	 * (non-Javadoc)
//	 *
//	 * @see
//	 * com.google.android.gcm.GCMBaseIntentService#onUnregistered(android.content
//	 * .Context, java.lang.String) onUnregistered(Context context, String
//	 * regId): Called after the device has been unregistered from GCM.
//	 * Typically, you should send the regid to the server so it unregisters the
//	 * device.
//	 */
//	@Override
//	protected void onUnregistered(Context context, String registrationId)
//	{
//		Log.e(TAG, "Received message: " + registrationId);
//	}
//
//	/*
//	 * (non-Javadoc)
//	 *
//	 * @see
//	 * com.google.android.gcm.GCMBaseIntentService#onMessage(android.content
//	 * .Context, android.content.Intent)
//	 *
//	 * onMessage(Context context, Intent intent): Called when your server sends
//	 * a message to GCM, and GCM delivers it to the device. If the message has a
//	 * payload, its contents are available as extras in the intent
//	 */
//	@Override
//	protected void onMessage(Context context, Intent intent)
//	{
//		/*if (intent != null)
//		{
//			Intent i;
//			String isSilent = intent.getExtras().getString("quickode-silent");
//			if (isSilent != null && isSilent.compareTo("true")==0)
//			{
//				Utils.writeEventToGoogleAnalytics(context, "silentPush", "isLive", "uuid_"+ Utils.getUUIDForPush(context));
//				return;
//			}
//			String msg = intent.getExtras().getString("message");
//			String did = intent.getStringExtra("did");
//			if (did != null)
//			{
//				Definitions.PUSH_WAS_HANDLE = false;
//				// browser intent
//				if (did.length() < REGULAR_DID_LENGTH)
//				{
//					// TODO add real URL
//					String urlWithParams = GlobesURL.URLPushNewsPage;
//					urlWithParams += "?did=" + did;
//					Uri uri = Uri.parse(urlWithParams);
//					i = new Intent(Intent.ACTION_VIEW, uri);
//				}
//				else
//				{
//					// if the app is not active
//					if (!Definitions.appActive || !Definitions.MainSlidingActivityActive)
//					{
//						Log.e("eli", "appActive = false");
//						i = new Intent(this, SplashScreen.class);
//					}
//					else
//					{
//						Log.e("eli", "appActive = false");
//						// if app is active go to document activity
//						i = new Intent(this, DocumentActivity_new.class);
//						//					i.putExtra("pushGoToDocumentActivity", "1");
//					}
//
//					i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//					i.putExtra("pushNotificationDocID", did);
//				}
//				generateNotification(this, msg, i);
//			}
//		}
//		// playEffects(this);
//*/	}
//
//	/*
//	 * (non-Javadoc)
//	 *
//	 * @see
//	 * com.google.android.gcm.GCMBaseIntentService#onError(android.content.Context
//	 * , java.lang.String)
//	 *
//	 * onError(Context context, String errorId): Called when the device tries to
//	 * register or unregister, but GCM returned an error. Typically, there is
//	 * nothing to be done other than evaluating the error (returned by errorId)
//	 * and trying to fix the problem.
//	 */
//	@Override
//	public void onError(Context context, String errorId)
//	{
//		Log.i(TAG, "Received error: " + errorId);
//	}
//
//	@Override
//	protected boolean onRecoverableError(Context context, String errorId)
//	{
//		// log message
//		Log.i(TAG, "Received recoverable error: " + errorId);
//		return super.onRecoverableError(context, errorId);
//	}
//
//	/**
//	 * Issues a notification to inform the user that server has sent a message.
//	 *
//	 * @param destActivity
//	 */
//	private static void generateNotification(Context context, String message, Intent notificationIntent)
//	{
//		/*
//		 * int icon = R.drawable.icon_launcher; long when =
//		 * System.currentTimeMillis(); NotificationManager notificationManager =
//		 * (NotificationManager)
//		 * context.getSystemService(Context.NOTIFICATION_SERVICE); Notification
//		 * notification = new Notification(icon, message, when); String title =
//		 * context.getString(R.string.app_name); PendingIntent intent =
//		 * PendingIntent.getActivity(context, 0, notificationIntent, 0);
//		 * notification.setLatestEventInfo(context, title, message, intent);
//		 * notification.flags |= Notification.FLAG_AUTO_CANCEL;
//		 * notificationManager.notify(0, notification);
//		 */
//
//		// //////////////////////////////////////////////////
//		// Sets an ID for the notification, so it can be updated
//		int notifyID = 1;
//		Uri defaultRingtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
//		mBuilder.setSmallIcon(R.drawable.icon_launcher).setContentTitle(context.getString(R.string.app_name))
//		.setTicker(Definitions.NOTIFICATION_TITLE);
//		// set the message
//		mBuilder.setContentText(message);
//		mBuilder.setStyle(new android.support.v4.app.NotificationCompat.BigTextStyle().bigText(message));
//
//		PendingIntent pendIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//		mBuilder.setContentIntent(pendIntent);
//		mBuilder.setAutoCancel(true);
//		// load the prefs
//		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
//		boolean sound = sharedPrefs.getBoolean("notificationSoundPref", true);
//		if (sound)
//		{
//			mBuilder.setSound(defaultRingtoneUri);
//		}
//		mBuilder.setWhen(System.currentTimeMillis());
//		NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//		// mId allows you to update the notification later on.
//		mNotificationManager.notify(notifyID, mBuilder.build());
//		// ///////////////////////////////////////////////
//
//	}
//	// --------------------------------------------------------------------------------------------------------------
//	//
//	// ************************************************ CREATE NOTIFICATION
//	// ***********************************
//	//
//	// --------------------------------------------------------------------------------------------------------------
//
//	/*
//	 * private void playEffects(Context context) { KeyguardLock keyLock;
//	 * WakeLock wakeLock; KeyguardManager key;
//	 *
//	 * try {
//	 *
//	 * key = (KeyguardManager)
//	 * context.getSystemService(Context.KEYGUARD_SERVICE); keyLock =
//	 * key.newKeyguardLock(""); keyLock.disableKeyguard();
//	 *
//	 * } catch (Exception e) { Log.e("lock  error", "", e); }
//	 *
//	 * try { PowerManager pm = (PowerManager)
//	 * context.getSystemService(Context.POWER_SERVICE); wakeLock =
//	 * pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK |
//	 * PowerManager.ACQUIRE_CAUSES_WAKEUP, ""); wakeLock.acquire(15000);
//	 *
//	 * } catch (Exception e) { Log.e("power power error", "", e); }
//	 *
//	 *
//	 * }
//	 */
//
//
//
}
