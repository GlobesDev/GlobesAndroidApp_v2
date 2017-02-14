package il.co.globes.android;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;
import il.co.globes.android.fragments.TickerPreferencesFragment;
import mobi.pushapps.PushApps;

//import com.groboot.pushapps.PushManager;

public class SettingsActivity extends Activity {
    boolean lastSavedNotificationPref;
    SharedPreferences sharedPrefs;
    Preference notificationsPrefs;
    CheckBox checkBoxPush;
    TextView textView_passmaddadin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);

        setContentView(R.layout.test_settings_activity);
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        lastSavedNotificationPref = sharedPrefs.getBoolean("notificationsPref", true);

        initUI();
    }

    private void initUI() {
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.a_test_settings_title);
        ImageView imageViewBack = (ImageView) findViewById(R.id.btn_back);

        textView_passmaddadin = (TextView) findViewById(R.id.textView_passmaddadin);
        textView_passmaddadin.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                TickerPreferencesFragment fragment = new TickerPreferencesFragment();
                // switch content
                MainSlidingActivity.getMainSlidingActivity().onSwitchContentFragment(fragment,
                        TickerPreferencesFragment.class.getSimpleName(), true, false, false); // to
                SettingsActivity.this.finish();

                // pass
                // maddadmim

            }
        });
        imageViewBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                SettingsActivity.this.finish();
            }
        });

        checkBoxPush = (CheckBox) findViewById(R.id.checkBoxPush);
        checkBoxPush.setChecked(lastSavedNotificationPref);
        checkBoxPush.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    sharedPrefs.edit().putBoolean("notificationsPref", true).commit();
                    PushApps.enablePush(getApplicationContext(), true);
                    PushRegistrationHelper.register(getApplicationContext());

                    //sharedPrefs.edit().putBoolean("notificationtest1", true).commit();
                    //PushManager.getInstance(getApplicationContext()).register();
                } else {
                    sharedPrefs.edit().putBoolean("notificationsPref", false).commit();
                    PushApps.enablePush(getApplicationContext(), false);
                    PushRegistrationHelper.unregister(getApplicationContext());
                    //PushManager.getInstance(getApplicationContext()).unregister();
                }

                //sendRequestToServer(isChecked);
            }
        });
    }

    private void sendRequestToServer(final boolean register) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    if (register) {
                        UtilsWebServices.postHTTPData(Definitions.URL_REGISTER_OR_UPDATE, "text/json", getRegistrationPostContent());
                    } else {
                        UtilsWebServices.postHTTPData(Definitions.URL_UNREGISTER_FOR_NOTIFICATIONS, "text/json",
                                getUnregisterForNotificationsPostContent());
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

    /**
     * @param register - calls weather to register for notifications or send false
     *                 data to unregister
     * @return string
     */
    private String getRegistrationPostContent() {
        TelephonyManager tManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String uid = tManager.getDeviceId();
        String versionName = "";
        try {
            versionName = getApplicationContext().getPackageManager().getPackageInfo(getApplicationContext().getPackageName(), 0).versionName;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String registrationId = preferences.getString("registrationId", "");

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
        sb.append("\"RegistrationUID\" : \"" + registrationId + "\"");
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
