package il.co.globes.android;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.preference.PreferenceManager;
import android.support.multidex.MultiDexApplication;
import android.support.v4.app.NotificationCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.RemoteViews;

import mobi.pushapps.PushApps;

import net.tensera.sdk.api.TenseraApi;

import java.io.File;

//public class AppRegisterForPushApps extends Application {
public class AppRegisterForPushApps extends MultiDexApplication {
    private static final int REGULAR_DID_LENGTH = 6;
    private static final String TAG = "AppRegisterForPushApps";
    boolean NotificationPref;
    SharedPreferences sharedPrefs;
    public static boolean enableTensera = true;

    @Override
    public void onCreate() {
        super.onCreate();
        // Definitions.appActive = true;
        Log.e("eli", "Application onCreate (class AppRegisterForPushApps)");

        // eli ^^^^^^^^^^^^ PUSH APSS SDK ^^^^^^^^^^^^^^^^^

        registerPushApps();
    }

    public void registerPushApps() {
        Log.e("alex", "PushNotification register");
        if (enableTensera) {
            TenseraApi.init(this);
        }
        try {
            PushApps.register(this);
            PushApps.setIconAsDefaultImageForSmallView(getApplicationContext(), true);

            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            boolean sendPush = sharedPrefs.getBoolean("notificationsPref", true);
            if (!sendPush) {
                PushApps.enablePush(getApplicationContext(), false);
                PushRegistrationHelper.unregister(getApplicationContext());
            } else {
                // todo - check if necessary
                PushRegistrationHelper.register(getApplicationContext());
            }
        } catch (Exception ex) {
            Log.e("alex", "PushApps Error on Register: " + ex.getMessage());
        }
        //PushApps.addTag(getApplicationContext(), new PATag("tester1", true));

		/*
        PushApps.setNotificationListener(this, new PANotificationListener() {
		      
		      @Override
		      public void onMessageReceived(Context context, Bundle extras) {
		    	  Log.e("alex", "PushNotification onMessageReceived");
		      }
		});
		*/
    }

    private static void generateNotification(Context context, String message, Intent notificationIntent) {
        // Sets an ID for the notification, so it can be updated
        int notifyID = 1;
        Uri defaultRingtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
        mBuilder.setSmallIcon(R.drawable.icon_launcher).setContentTitle(context.getString(R.string.app_name))
                .setTicker(Definitions.NOTIFICATION_TITLE);
        // set the message
        mBuilder.setContentText(message);
        mBuilder.setStyle(new android.support.v4.app.NotificationCompat.BigTextStyle().bigText(message));

        PendingIntent pendIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(pendIntent);
        mBuilder.setAutoCancel(true);
        // load the prefs
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        boolean sound = sharedPrefs.getBoolean("notificationSoundPref", true);
        if (sound) {
            mBuilder.setSound(defaultRingtoneUri);
        }
        mBuilder.setWhen(System.currentTimeMillis());
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        mNotificationManager.notify(notifyID, mBuilder.build());
    }

    private static void generateNotification_v2(Context context, String message, Intent notificationIntent) {
        // Sets an ID for the notification, so it can be updated
        int notifyID = 1;
        Uri defaultRingtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
//		mBuilder.setSmallIcon(R.drawable.icon_launcher).setContentTitle(context.getString(R.string.app_name))
//				.setTicker(Definitions.NOTIFICATION_TITLE);
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

        PendingIntent pendIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        File imgFile = new File("http://images.globes.co.il/images/NewGlobes/big_image_800/2015/c10_lalum-800.jpg");
        Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

        RemoteViews expandedView = new RemoteViews(
                context.getPackageName(), R.layout.pushapp_custom_notification);
        expandedView.setTextViewText(R.id.notification_title, "Title");
        expandedView.setTextViewText(R.id.notification_subtitle, "SubTitile");
        expandedView.setImageViewBitmap(R.id.notification_main_image_view, myBitmap);

        Notification notification = new NotificationCompat.Builder(
                context)
                .setSmallIcon(R.drawable.icon_launcher)
                .setSound(defaultRingtoneUri)
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setAutoCancel(true)
                .setContentIntent(pendIntent)
                .setContentTitle("Title1")
                .setContentText("SubTitleTxt")
                .build();

        notification.bigContentView = expandedView;

        Log.e("alex", "PushApp1 BeforeNotify...");

        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(notifyID, notification);

    }

    private void sendRequestToServer(final boolean register, final String registrationId) {

        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    if (register) {
                        UtilsWebServices.postHTTPData(Definitions.URL_REGISTER_OR_UPDATE, "text/json",
                                getRegistrationPostContent(true, registrationId));
                    } else

                    {

                        UtilsWebServices.postHTTPData(Definitions.URL_REGISTER_OR_UPDATE, "text/json",
                                getUnregisterForNotificationsPostContent());
                        // UtilsWebServices.postHTTPData(Definitions.URL_REGISTER_OR_UPDATE,
                        // "text/json",
                        // getRegistrationPostContent(false, registrationId));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            protected void onPostExecute(Void result) {
                sharedPrefs.edit().putBoolean("notificationsPref", register).commit();
            }

            ;

        }.execute();
    }

    public String getRegistrationPostContent(boolean register, String registrationId) {
        String versionName = "";
        TelephonyManager tManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String uid = tManager.getDeviceId();
        try {
            versionName = getApplicationContext().getPackageManager().getPackageInfo(getApplicationContext().getPackageName(), 0).versionName;
        } catch (NameNotFoundException e) {
            Log.e(TAG, "Error NameNotFoundException @ getRegistrationPostContent ");
            e.printStackTrace();
        }

        StringBuffer sb = new StringBuffer();
        sb.append("{");
        sb.append("\"ApplicationName\" : \"globes1\"");
        sb.append(",");
        sb.append("\"AppVersion\" :" + "\"" + versionName + "\"");
        sb.append(",");
        sb.append("\"DeviceID\" :" + "\"" + uid + "\"");
        sb.append(",");
        sb.append("\"OSVersion\" : \"" + VERSION.RELEASE + "\"");
        sb.append(",");
        sb.append("\"PlatformID\":5");
        sb.append(",");
        if (register) {
            sb.append("\"RegistrationUID\" : \"" + registrationId + "\"");
        } else {
            sb.append("\"RegistrationUID\" : \"" + "" + "\"");
        }
        sb.append("}");
        return sb.toString();
    }

    /**
     * getUnregisterForNotificationsPostContent
     *
     * @return String device id
     */
    private String getUnregisterForNotificationsPostContent() {
        StringBuffer sb = new StringBuffer();
        TelephonyManager tManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String uid = tManager.getDeviceId();
        sb.append("{");
        sb.append("\"DeviceID\" :" + "\"" + uid + "\"");
        sb.append("}");
        return sb.toString();
    }

}
