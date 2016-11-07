package info.androidhive.navigationdrawer.other;

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
    @Override
    public void notificationReceived(OSNotification notification) {
        JSONObject data = notification.payload.additionalData;

        if (data != null && notification.payload.body != null && notification.payload.title != null) {
            String email;
            int    remaining;
            String dateSend;

            Notification notification1 = new Notification();

            dateSend  = data.optString("date_send", null);
            email     = data.optString("email", null);
            remaining = data.optInt("remaining", -2);

            notification1.setTitle(notification.payload.title);
            notification1.setBody(notification.payload.body);

            notification1.setRemaining(remaining);
            notification1.setDate(dateSend);
            notification1.setEmail(email);
            notification1.save();
        }
    }
}