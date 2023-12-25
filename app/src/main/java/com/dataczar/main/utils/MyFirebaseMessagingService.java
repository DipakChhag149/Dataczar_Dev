package com.dataczar.main.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.dataczar.R;
import com.dataczar.main.DataczarApp;
import com.dataczar.main.activity.Dashboard;
import com.dataczar.main.viewmodel.ClsCommon;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.tot.badges.IconBadgeNumManager;

import org.json.JSONObject;

import java.security.SecureRandom;

import me.leolin.shortcutbadger.ShortcutBadger;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    RequestQueue requestQueue;
    SharedPreferences.Editor editor;
    SharedPreferences sharedPref;
    IconBadgeNumManager setIconBadgeNumManager;

    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.e(TAG, "From: " + remoteMessage.getFrom());
        int badgeCount = 0;
        int intNo = 0;
        String title = "";
        String message = "";

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.e(TAG, "Message data payload: " + remoteMessage.getData());

            try {
                Gson gsonObj = new Gson();
                String jsonStr = gsonObj.toJson(remoteMessage.getData());
                Log.e("PAYLOAD", "" + jsonStr);

                JSONObject jsonObject = new JSONObject(jsonStr);
                badgeCount = jsonObject.getInt("badge");

                SecureRandom random = new SecureRandom();
                int num = random.nextInt(100000);
                String strRandomNo = String.format("%05d", num);
                intNo = Integer.parseInt(strRandomNo);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {


            try {
                Gson gsonObj = new Gson();
                String jsonStr = gsonObj.toJson(remoteMessage.getNotification());
                Log.e("PAYLOAD", "" + jsonStr);

                JSONObject jsonObject = new JSONObject(jsonStr);

                message = jsonObject.getString("body");
                title = jsonObject.getString("title");

                SecureRandom random = new SecureRandom();
                int num = random.nextInt(100000);
                String strRandomNo = String.format("%05d", num);
                intNo = Integer.parseInt(strRandomNo);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        sendNotification(title, message, intNo, badgeCount);

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }
    // [END receive_message]

    // [START on_new_token]

    /**
     * There are two scenarios when onNewToken is called:
     * 1) When a new token is generated on initial app startup
     * 2) Whenever an existing token is changed
     * Under #2, there are three scenarios when the existing token is changed:
     * A) App is restored to a new device
     * B) User uninstalls/reinstalls the app
     * C) User clears app data
     */
    @Override
    public void onNewToken(@NonNull String token) {
        Log.d(TAG, "Refreshed token: " + token);
        sendRegistrationToServer(token);

    }
    // [END on_new_token]

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */
    private void sendNotification(String strTitle, String messageBody, int intRandomNo, int badgeCount) {
        Intent intent = new Intent(this, Dashboard.class);
        intent.putExtra("NeedNavigate", ClsCommon.NOTIFICATION);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);
        String channelId = getString(R.string.default_notification_channel_id);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        // int icon = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ? R.mipmap.ic_app_icon : R.mipmap.ic_app_icon;
        int icon = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ? R.drawable.ic_applogo : R.mipmap.ic_launcher;
        Notification notification = null;
        // Since android Oreo notification channel is needed.
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "dataczar_notification",
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.setShowBadge(true);
            notificationManager.createNotificationChannel(channel);

        }

        try {
            notification = new NotificationCompat.Builder(this, channelId)
                    .setSmallIcon(icon)
                    .setContentTitle(strTitle.toString())
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText(messageBody))
                    .setContentTitle(strTitle.toString())
                    .setContentText(messageBody)
                    .setAutoCancel(true)
                    .setNumber(badgeCount)
                    .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
                    .setSound(defaultSoundUri)
                    .setContentIntent(pendingIntent)
                    .setColor(Color.BLUE)
                    .setVibrate(new long[]{2000, 2000})
                    .setPriority(Notification.PRIORITY_MAX)
                    .build();

            ShortcutBadger.applyCount(this, badgeCount);

            notificationManager.notify(/*0*/intRandomNo /* ID of notification */, notification);

            Dashboard.updateMyActivity(this, "Update");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void sendRegistrationToServer(String token) {
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        sharedPref = getSharedPreferences(ClsCommon.PREFDATA, Context.MODE_PRIVATE);
        editor = sharedPref.edit();

        editor.putString(ClsCommon.FCM_TOKEN, token);
        editor.apply();

        editor.putBoolean(ClsCommon.NOTIFICATION_STATUS, true);
        editor.apply();
    }


    public String getCookie() {
        SharedPreferences prefs = getSharedPreferences(ClsCommon.PREFDATA, Context.MODE_PRIVATE);
        String Cookie = prefs.getString(ClsCommon.COOKIE, "");
        return Cookie;
    }
}
