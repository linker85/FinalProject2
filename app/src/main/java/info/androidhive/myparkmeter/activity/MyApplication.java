package info.androidhive.myparkmeter.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.util.Log;

import com.onesignal.OSNotification;
import com.onesignal.OSNotificationAction;
import com.onesignal.OSNotificationOpenResult;
import com.onesignal.OneSignal;

import org.json.JSONObject;

import info.androidhive.myparkmeter.models.Notification;

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
                .setNotificationReceivedHandler(new NotificationReceivedHandler2())
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
            } else {
                Intent intent = new Intent(getApplicationContext(),
                        MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }

        }
    }

    public class NotificationReceivedHandler2 implements OneSignal.NotificationReceivedHandler {
        private static final String TAG = "HandleTAG_";

        @Override
        public void notificationReceived(OSNotification notification) {
            JSONObject data = notification.payload.additionalData;

            if (data != null && notification.payload.body != null && notification.payload.title != null) {
                String email;
                int    remaining;
                String dateSend;
                String coordinates;

                Notification notification1 = new Notification();

                Log.d(TAG, "notificationReceived: " + Thread.currentThread());

                dateSend = data.optString("date_send", null);

                email       = data.optString("email", null);
                remaining   = data.optInt("remaining", -2);
                coordinates = data.optString("coordinates", null);

                notification1.setTitle(notification.payload.title);
                notification1.setBody(notification.payload.body);

                notification1.setRemaining(remaining);
                notification1.setDateS(dateSend);
                notification1.setEmail(email);
                notification1.setCoordinates(coordinates);
                try {
                    notification1.save();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (remaining <= 0) {
                    Intent intent = new Intent(getApplicationContext(),
                            MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
                Log.d(TAG, "notificationReceived2: " + Thread.currentThread());
            }
        }
    }

}