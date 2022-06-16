package com.dataczar.main.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.android.volley.Header;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dataczar.R;
import com.dataczar.main.activity.Dashboard;
import com.dataczar.main.activity.WSMethods;
import com.dataczar.main.viewmodel.ClsCommon;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    RequestQueue requestQueue;
    SharedPreferences.Editor editor;
    SharedPreferences sharedPref;
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());

            try {

                Gson gsonObj = new Gson();
                String jsonStr = gsonObj.toJson(remoteMessage.getData());
                System.out.println("jsonStr : " + jsonStr);

                JSONObject jsonObject = new JSONObject(jsonStr);

                String status = jsonObject.getString("status");
                String body = jsonObject.getString("body");
                String title = jsonObject.getString("title");

                SecureRandom random = new SecureRandom();
                int num = random.nextInt(100000);
                String strRandomNo = String.format("%05d", num);
                int intNo=Integer.parseInt(strRandomNo);

                sendNotification(title.toString(), body.toString(),intNo);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            SecureRandom random = new SecureRandom();
            int num = random.nextInt(100000);
            String strRandomNo = String.format("%05d", num);
            int intNo=Integer.parseInt(strRandomNo);

            sendNotification("Notification", ""+remoteMessage.getNotification().getBody(),intNo);

        }

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
    private void sendNotification(String strTitle, String messageBody,int intRandomNo) {
        Intent intent = new Intent(this, Dashboard.class);//TripRequest
        intent.putExtra("NeedNavigate", ClsCommon.NOTIFICATION);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);
        String channelId = getString(R.string.default_notification_channel_id);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        // int icon = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ? R.mipmap.ic_app_icon : R.mipmap.ic_app_icon;
        int icon = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ? R.drawable.ic_applogo : R.mipmap.ic_launcher;
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this,channelId)
                        .setSmallIcon(icon)//ic_launcher ic_app_icon
                        // .setLargeIcon()
                        //   .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_stat_maps_local_taxi))
                        .setContentTitle(strTitle.toString())//Adani Driver
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(messageBody))
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent)
                        .setColor(Color.BLUE)
                        .setVibrate(new long[]{2000, 2000})
                        .setPriority(Notification.PRIORITY_MAX);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(/*0*/intRandomNo /* ID of notification */, notificationBuilder.build());
    }



    private void sendRegistrationToServer(String token) {
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        sharedPref = getSharedPreferences(ClsCommon.PREFDATA, Context.MODE_PRIVATE);
        editor = sharedPref.edit();

        editor.putString(ClsCommon.FCM_TOKEN, token);
        editor.apply();

        editor.putBoolean(ClsCommon.NOTIFICATION_STATUS, true);
        editor.apply();
        //new AddNotificationToken(getApplicationContext(),token).execute();

    }

    class AddNotificationToken extends AsyncTask<String, Void, Boolean> {
        ProgressDialog pd;
        String token;

        public AddNotificationToken(Context context, String token) {
            pd = new ProgressDialog(context, R.style.ProgressDialog);
            pd.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            this.token = token;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd.setCancelable(false);
            if (!pd.isShowing())
                pd.show();
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            return null;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);

            StringRequest stringRequest = new StringRequest(Request.Method.PUT, WSMethods.ADD_NOTIFICATION,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            if (pd.isShowing())
                                pd.dismiss();

                            if (response != null && !response.isEmpty()) {
                                try {
                                    JSONObject jsonResponse = new JSONObject(response);
                                    JSONObject data_response = jsonResponse.getJSONObject("response");
                                    if (!data_response.getBoolean("success")) {

                                    } else {
                                        //Toast.makeText(context, "Login success - Dashboard", Toast.LENGTH_SHORT).show();

                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (pd != null && pd.isShowing())
                        pd.dismiss();

                    //Toast.makeText(context,"Response Error: "+ error + " Can't Connect to server.", Toast.LENGTH_LONG).show();
                }
            }
            ) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put(ClsCommon.COOKIE, getCookie());
                    return params;
                }

                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("token", token);
                    params.put("description", "Android");
                    return params;
                }

                @Override
                protected Response<String> parseNetworkResponse(NetworkResponse response) {
                    try {
                        //String jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers,"utf-8"));

                        List<Header> jsonheader = response.allHeaders;

                        String Cookie = "";
                        for (int i = 0; i < jsonheader.size(); i++) {
                            Header header = jsonheader.get(i);
                            if (header.getName().equals("Set-Cookie")) {
                                Cookie += header.getValue().split(";")[0] + ";";
                            }
                        }

                        Cookie = Cookie.substring(0, Cookie.length() - 1);
                        editor.putString(ClsCommon.COOKIE, Cookie);
                        editor.apply();

                    } catch (Exception je) {
                        return Response.error(new ParseError(je));
                    }

                    return super.parseNetworkResponse(response);
                }
            };
            requestQueue.add(stringRequest);
        }
    }

    public String getCookie() {
        SharedPreferences prefs = getSharedPreferences(ClsCommon.PREFDATA, Context.MODE_PRIVATE);
        String Cookie = prefs.getString(ClsCommon.COOKIE, "");
        return Cookie;
    }

}
