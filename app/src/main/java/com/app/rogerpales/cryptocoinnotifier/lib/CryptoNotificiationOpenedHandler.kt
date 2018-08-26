package com.app.rogerpales.cryptocoinnotifier.lib

import android.content.Context
import android.content.Intent
import android.util.Log
import com.app.rogerpales.cryptocoinnotifier.AddAlertActivity
import com.app.rogerpales.cryptocoinnotifier.AppActivity
import com.app.rogerpales.cryptocoinnotifier.MainActivity
import com.app.rogerpales.cryptocoinnotifier.lib.AppUtils.Companion.deserializeAlert
import com.onesignal.OSNotificationAction
import com.onesignal.OSNotificationOpenResult
import com.onesignal.OneSignal


internal class CryptoNotificiationOpenedHandler : OneSignal.NotificationOpenedHandler {

    val activity: AppActivity

    constructor(activity: AppActivity) {
        this.activity = activity
    }

    // This fires when a notification is opened by tapping on it.
    override fun notificationOpened(result: OSNotificationOpenResult) {
        val actionType = result.action.type
        val data = result.notification.payload.additionalData

        var intent = Intent(activity, MainActivity::class.java)

        if (data != null) {
            val alert = data.optString("alert", null)
            if (alert != null) {
                intent = Intent(activity, AddAlertActivity::class.java)
                Log.i("OneSignalExample", "alert set with value: $alert")
                intent.putExtra("ALERT_FROM_NOTIFICATION", alert);
            }
        }

        if (actionType == OSNotificationAction.ActionType.ActionTaken) {
            Log.i("OneSignalExample", "Button pressed with id: " + result.action.actionID)
        }

        activity.startActivity(intent)

        // The following can be used to open an Activity of your choice.
        // Replace - getApplicationContext() - with any Android Context.
        // Intent intent = new Intent(getApplicationContext(), YourActivity.class);
        // intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
        // startActivity(intent);

        // Add the following to your AndroidManifest.xml to prevent the launching of your main Activity
        //   if you are calling startActivity above.
        /*
        <application ...>
          <meta-data android:name="com.onesignal.NotificationOpened.DEFAULT" android:value="DISABLE" />
        </application>
     */
    }
}