package info.androidhive.navigationdrawer.service;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import info.androidhive.navigationdrawer.models.Notification;

/**
 * Created by raul on 12/11/2016.
 */

public class NotificationsService extends IntentService {
    private static final String TAG = "NotServiceTAG_";
    private EventBus eventBus = EventBus.getDefault();
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public NotificationsService(String name) {
        super(name);
    }

    public NotificationsService() {
        super("NotificationsService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        eventBus.register(this);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "onHandleIntent: ");

        String email = intent.getStringExtra("email");
        List<Notification> notificationsList = Notification.findWithQuery(
                Notification.class, "SELECT * FROM NOTIFICATION WHERE EMAIL=?", email);
    }

    @Override
    public void onDestroy() {
        eventBus.unregister(this);
        super.onDestroy();
    }
}
