package il.co.globes.android;

import android.content.Context;
import android.content.Intent;
import mobi.pushapps.gcm.PARegistrationIntentService;
import mobi.pushapps.utils.PAGeneralUtils;
import mobi.pushapps.utils.PALogger;
import net.tensera.sdk.utils.Logger;

import static mobi.pushapps.PushApps.isPushEnabled;

public class PushRegistrationHelper {

    public static void register(Context context) {
        Logger.d("Register to PushApps called");
        String sdkKey = PAGeneralUtils.getSdkKey(context);
        if (sdkKey != null && sdkKey.length() != 0) {
            if (isPushEnabled(context)) {
                Intent intent = new Intent(context, PARegistrationIntentService.class);
                intent.putExtra("PAAction", "register");
                context.startService(intent);
            }
        } else {
            PALogger.logError("Can\'t find SDK key. Are you sure it\'s in your app manifest?");
        }
    }

    public static void unregister(Context context) {
        Logger.d("unRegister to PushApps called");
        String sdkKey = PAGeneralUtils.getSdkKey(context);
        if (sdkKey != null && sdkKey.length() != 0) {
            if (isPushEnabled(context)) {
                Intent intent = new Intent(context, PARegistrationIntentService.class);
                intent.putExtra("PAAction", "unregister");
                context.startService(intent);
            }
        } else {
            PALogger.logError("Can\'t find SDK key. Are you sure it\'s in your app manifest?");
        }
    }
}
