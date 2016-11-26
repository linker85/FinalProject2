package info.androidhive.navigationdrawer.other;

import android.util.Log;

import com.onesignal.OSNotification;
import com.onesignal.OneSignal;

import org.json.JSONObject;

import info.androidhive.navigationdrawer.models.Notification;

/**
 * Created by raul on 06/11/2016.
 */

/*
* Fires when a OneSignal notification is displayed to the user, or when it is received if it is a background / silent notification.
*
* */
public class NotificationReceivedHandler implements OneSignal.NotificationReceivedHandler {
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

            Log.d(TAG, "notificationReceived2: " + Thread.currentThread());
        }
    }
}