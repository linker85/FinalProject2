package info.androidhive.navigationdrawer.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.util.Log;

import com.onesignal.OSNotificationAction;
import com.onesignal.OSNotificationOpenResult;
import com.onesignal.OneSignal;

import org.json.JSONObject;

import info.androidhive.navigationdrawer.other.NotificationReceivedHandler;

/**
 * Created by raul on 06/11/2016.
 */

public class MyApplication extends com.orm.SugarApp {
    private static final String TAG = "MyApplicationTAG_";

    private static MyApplication singleton;

    public static MyApplication getInstance(){
        return singleton;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: MyApplication");

        // Logging set to help debug issues, remove before releasing your app.
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.DEBUG, OneSignal.LOG_LEVEL.WARN);

        OneSignal.startInit(this)
                .setNotificationOpenedHandler(new NotificationOpenedHandler())
                .setNotificationReceivedHandler(new NotificationReceivedHandler())
                .autoPromptLocation(true)
                .init();

        OneSignal.idsAvailable(new OneSignal.IdsAvailableHandler() {
            @Override
            public void idsAvailable(String userId, String registrationId) {
                Log.d("debug", "userId:" + userId);
                try {
                    SharedPreferences sharedPref = getApplicationContext().
                            getSharedPreferences("my_park_meter_pref", Context.MODE_PRIVATE);
                    // Get shared preferences from mock-backend
                    final SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString("userId", userId);
                    editor.commit();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        singleton = this;
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    private class NotificationOpenedHandler implements OneSignal.NotificationOpenedHandler {
        // This fires when a notification is opened by tapping on it.

        @Override
        public void notificationOpened(OSNotificationOpenResult result) {
            OSNotificationAction.ActionType actionType = result.action.type;
            JSONObject data = result.notification.payload.additionalData;
            String customKey;

            boolean extendTime = false;

            if (data != null) {
                customKey = data.optString("customkey", null);

                if (data.optInt("remaining", -2) > 0) {
                    extendTime = true;
                }

                if (customKey != null)
                    Log.i("OneSignalExample", "customkey set with value: " + customKey);
            }

            if (actionType == OSNotificationAction.ActionType.ActionTaken)
                Log.i("OneSignalExample", "Button pressed with id: " + result.action.actionID);

            // The following can be used to open an Activity of your choice.
            if (extendTime) {
                Intent intent = new Intent(getApplicationContext(),
                        MoreTimeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }

        }
    }

}