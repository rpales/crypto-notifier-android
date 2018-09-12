package com.app.rogerpales.cryptocoinnotifier.lib

import android.content.Context
import android.content.Intent
import android.util.Log
import com.app.rogerpales.cryptocoinnotifier.AddAlertActivity
import com.app.rogerpales.cryptocoinnotifier.MainActivity
import com.onesignal.OSNotificationAction
import com.onesignal.OSNotificationOpenResult
import com.onesignal.OneSignal


internal class CryptoNotificiationOpenedHandler : OneSignal.NotificationOpenedHandler {

    val context: Context

    constructor(context: Context) {
        this.context = context
    }

    // This fires when a notification is opened by tapping on it.
    override fun notificationOpened(result: OSNotificationOpenResult) {
        val actionType = result.action.type
        val data = result.notification.payload.additionalData

        var intent = Intent(context, MainActivity::class.java)

        if (data != null) {
            val alert = data.optString("alert", null)
            val authToken = data.optString("user_auth_token", null)
            if (alert != null && authToken != null) {
                intent = Intent(context, MainActivity::class.java)
                intent.putExtra("ALERT_FROM_NOTIFICATION", alert)
                intent.putExtra("AUTH_TOKEN_FROM_NOTIFICATION", authToken)
                Log.d("OneSignalExample", "alert set with value: $alert")
            }
        }

        if (actionType == OSNotificationAction.ActionType.ActionTaken) {
            Log.d("OneSignalExample", "Button pressed with id: " + result.action.actionID)
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent)

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