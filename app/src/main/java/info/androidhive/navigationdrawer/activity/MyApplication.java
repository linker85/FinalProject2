package info.androidhive.navigationdrawer.activity;

import android.content.res.Configuration;
import android.util.Log;

import com.onesignal.OneSignal;

import info.androidhive.navigationdrawer.other.NotificationOpenedHandler;
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

}