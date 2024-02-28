package com.dataczar.main.activity;

import static com.dataczar.main.utils.AppUtils.getCookie;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
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
import com.dataczar.main.utils.AppPreferenceManager;
import com.dataczar.main.utils.AppUtils;
import com.dataczar.main.viewmodel.ClsCommon;
import com.dataczar.main.viewmodel.LoginVM;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener
{

    ClsCommon clsCommon;
    RequestQueue requestQueue;
    Context context;
    String UserName, Password;
    EditText eduser, edpass;
    SignInButton btnGoogleSignIn;

    private FirebaseAuth mAuth;
    GoogleSignInOptions gso;
    GoogleSignInClient mGoogleSignInClient;

    private static final int G_SIGN_IN = 999;
    private static final String TAG = "LoginAc";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Setting up the layout
       setContentView(R.layout.login_user);


        this.context = LoginActivity.this;
        mAuth = FirebaseAuth.getInstance();
        //initUi(loginUserBinding);

        LoginVM lvm = new LoginVM();

        //Setting up the default user's credentials
        //Setup all pending binding to UI
        //loginUserBinding.setViewModel(lvm);
        //loginUserBinding.executePendingBindings();

        eduser   = findViewById(R.id.edEmail);
        edpass   = findViewById(R.id.edPassword);
        btnGoogleSignIn = findViewById(R.id.GoogleSignIn);

        findViewById(R.id.btnLogin).setOnClickListener(this);
        findViewById(R.id.tvForgotPass).setOnClickListener(this);
        findViewById(R.id.tvtSignup).setOnClickListener(this);
        btnGoogleSignIn.setOnClickListener(this);

        ActivityCompat.requestPermissions(LoginActivity.this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                1);


        initUi();
        setDefaultUser(lvm);

        requestQueue = Volley.newRequestQueue(context);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(LoginActivity.this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private void initUi()
    {
        clsCommon = new ClsCommon(getApplicationContext());


        btnGoogleSignIn.setSize(SignInButton.SIZE_WIDE);

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken("762954311229-vtif3b52rtgmpramu5si54avh512e8t8.apps.googleusercontent.com") //client id from google-service.json
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

         checkUserLoginStatus();
    }

    private void setDefaultUser(LoginVM lvm)
    {
     /*   lvm.setUserEmail("test@dataczar.com");
        lvm.setUserPassword("test.123");
*/
       /* eduser.setText("test@dataczar.com");
        edpass.setText("test.123");*/
    }

    private void checkUserLoginStatus()
    {
         if(getCookie(LoginActivity.this).toString() != null && !getCookie(LoginActivity.this).toString().isEmpty())
             new GetLoginStatus(context).execute();
         else
             new checkLoginAuth(context, eduser.getText().toString(), edpass.getText().toString()).execute();

    }

    @Override
    public void onClick(View view)
    {
        if(view.getId() == R.id.btnLogin)
        {
            if(!clsCommon.checkConnection())
            {
                clsCommon.showSnackBar(false, view);
            }else
            {
                if(eduser.getText().length() <= 0)
                    showAlertDialog( "OOPS", "Please enter email.");
                else if(edpass.getText().length() <= 0)
                    showAlertDialog( "OOPS", "Please enter password.");
                else
                    new checkLoginAuth(context, eduser.getText().toString(), edpass.getText().toString()).execute();
            }
        }
        else  if(view.getId() == R.id.tvForgotPass)
        {
            //Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(WSMethods.URL_FORGOTPASS));
            //startActivity(browserIntent);

            Intent intent = new Intent(context, WebviewLP.class);
            intent.putExtra(ClsCommon.WEBSITE, ClsCommon.FORGOTPASS);
            startActivity(intent);

        }else  if(view.getId() == R.id.tvtSignup)
        {
            Intent intent = new Intent(context, RegistrationActivity.class);
            //intent.putExtra(ClsCommon.WEBSITE, ClsCommon.SIGNUP);
            startActivity(intent);
        }
        else if(view.getId() == R.id.GoogleSignIn)
        {
            signInGoogle();
        }
    }

    private void signInGoogle()
    {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, G_SIGN_IN);
    }

    private void showAlertDialog(String Title, String Message)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        // Set a title for alert dialog
        builder.setTitle(Title);

        // Show a message on alert dialog
        builder.setMessage(Message);

        // Set the positive button
        builder.setPositiveButton("Yes",null);

        // Set the negative button
        builder.setNegativeButton("No", null);

        // Create the alert dialog
        AlertDialog dialog = builder.create();

        // Finally, display the alert dialog
        dialog.show();

        // Get the alert dialog buttons reference
        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        //Button negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);

        // Change the alert dialog buttons text and background color
        positiveButton.setTextColor(Color.parseColor("#FF0400"));
        positiveButton.setText("Ok");
        //positiveButton.setBackgroundColor(Color.parseColor("#FFE1FCEA"));

        //negativeButton.setTextColor(Color.parseColor("#f1592a"));
        //negativeButton.setBackgroundColor(Color.parseColor("#FFFCB9B7"));

        /*positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {

            }
        });*/

    }

    class GetLoginStatus extends AsyncTask<String, Void, Boolean>
    {
        ProgressDialog pd;
        public GetLoginStatus(Context context)
        {
            pd = new ProgressDialog(context, R.style.ProgressDialog);
            pd.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd.setCancelable(false);
            if(!pd.isShowing())
                pd.show();

        }

        @Override
        protected Boolean doInBackground(String... strings)
        {
            return null;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean)
        {
            super.onPostExecute(aBoolean);

            StringRequest stringRequest = new StringRequest(Request.Method.GET,  WSMethods.GETLOGINSTATUS,
                    new Response.Listener<String>()
                    {
                        @Override
                        public void onResponse(String response)
                        {
                            if(pd.isShowing())
                                pd.dismiss();

                            if(response!= null && !response.isEmpty())
                            {
                                try {
                                    JSONObject jsonResponse = new JSONObject(response);
                                    if(!jsonResponse.getBoolean("success"))
                                    {
                                        //Toast.makeText(context, "Login failed - Move to Login", Toast.LENGTH_SHORT).show();
                                    }else
                                    {
                                        getFirebaseToken();
                                        Intent iv = new Intent(context, Dashboard.class);
                                        iv.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
                                        iv.putExtra("NeedNavigate", ClsCommon.LOGIN);
                                        startActivity(iv);
                                        LoginActivity.this.finish();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error)
                {
                    if(pd != null && pd.isShowing())
                        pd.dismiss();

                    Toast.makeText(context,"Response Error: "+ error + " Can't Connect to server.", Toast.LENGTH_LONG).show();
                }
            }
            )
            {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError
                {
                    Map<String, String>  params = new HashMap<String, String>();
                    params.put(clsCommon.COOKIE, getCookie(LoginActivity.this));
                    return params;
                }

            };
            requestQueue.add(stringRequest);
        }
    }

    class checkLoginAuth extends AsyncTask<String, Void, Boolean>
    {
        ProgressDialog pd;
        String username, password;

        public checkLoginAuth(Context context, String username, String password)
        {
            pd = new ProgressDialog(context, R.style.ProgressDialog);
            pd.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            this.username = username;
            this.password = password;
        }

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            pd.setCancelable(false);
            if(!pd.isShowing())
                pd.show();
        }

        @Override
        protected Boolean doInBackground(String... strings)
        {
            return null;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean)
        {
            super.onPostExecute(aBoolean);

            StringRequest stringRequest = new StringRequest(Request.Method.POST, WSMethods.CHECKLOGINAUTH,
                    new Response.Listener<String>()
                    {
                        @Override
                        public void onResponse(String response)
                        {
                            Log.e("Response",""+response);
                            if(pd.isShowing())
                                pd.dismiss();

                            if(response!= null && !response.isEmpty())
                            {
                                try {
                                    JSONObject jsonResponse = new JSONObject(response);
                                    JSONObject data_response = jsonResponse.getJSONObject("response");
                                    if(!data_response.getBoolean("success"))
                                    {
                                        //Toast.makeText(context, "Login failed - Move to Login", Toast.LENGTH_SHORT).show();
                                        AppUtils.showDialog(LoginActivity.this,"OOPS", "These credentials dose not match in our records","Ok");
                                    }else
                                    {
                                        //Toast.makeText(context, "Login success - Dashboard", Toast.LENGTH_SHORT).show();
                                        getFirebaseToken();
                                        Intent iv = new Intent(context, Dashboard.class);
                                        iv.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
                                        iv.putExtra("NeedNavigate", ClsCommon.LOGIN);
                                        startActivity(iv);
                                        LoginActivity.this.finish();

                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error)
                {
                    if(pd != null && pd.isShowing())
                        pd.dismiss();

                    //Toast.makeText(context,"Response Error: "+ error + " Can't Connect to server.", Toast.LENGTH_LONG).show();
                    AppUtils.showDialog(LoginActivity.this,"OOPS", "These credentials do not match our records","Ok");
                }
            }
            )
            {
                @Override
                public Map<String, String> getHeaders()
                {
                    Map<String, String>  params = new HashMap<String, String>();
                    params.put(clsCommon.COOKIE, getCookie(LoginActivity.this));
                    return params;
                }

                @Override
                protected  Map<String,String> getParams()
                {
                    Map<String,String> params = new HashMap<String, String>();

                        params.put("email", username);
                        params.put("password", password);
                        params.put("remember", "true");

                    return params;
                }

                @Override
                protected Response<String> parseNetworkResponse(NetworkResponse response)
                {
                    try {
                        //String jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers,"utf-8"));

                        List<Header> jsonheader = response.allHeaders;

                        String Cookie = "";
                        for(int i=0; i< jsonheader.size(); i++)
                        {
                            Header header = jsonheader.get(i);
                            if(header.getName().equals("Set-Cookie"))
                            {
                                Cookie+= header.getValue().split(";")[0] + ";";
                            }
                        }

                        Cookie = Cookie.substring(0, Cookie.length()-1);
                        AppUtils.saveCookie(LoginActivity.this,Cookie);

                    } catch (Exception je) {
                        return Response.error(new ParseError(je));
                    }

                    return super.parseNetworkResponse(response);
                }
            };


            requestQueue.add(stringRequest);
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == G_SIGN_IN)
        {

            try {
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(TAG, "Token ID:" + account.getIdToken());
                firebaseAuthWithGoogle(account.getIdToken());

            } catch (ApiException e) {
                Log.w(TAG, "Google sign in failed", e);
                Toast.makeText(context, "Login failed! can not connect to server, please try again.", Toast.LENGTH_SHORT).show();
                //updateUI(null);
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken)
    {
        try {
            AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
            mAuth.signInWithCredential(credential)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "signInWithCredential:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                updateUI(user);
                            } else {
                                Log.w(TAG, "signInWithCredential:failure", task.getException());
                                updateUI(null);
                            }
                        }
                    });
        }catch (Exception ex)
        {
            Toast.makeText(context, "Login failed! can not connect to server, please try again.", Toast.LENGTH_SHORT).show();
        }

    }

    private void updateUI(FirebaseUser user)
    {
        if (user != null)
        {
            String name = user.getDisplayName();
            String email = user.getEmail();
            //Uri photoUrl = user.getPhotoUrl();
            boolean emailVerified = user.isEmailVerified();
            String uid = user.getUid();

            new checkGoogleLoginAuth(context, name, email, uid).execute();

            ////https://connect.dataczar.com/api/login/google/callback?token=117804837185203264985&app_client_secret=90vyiuhnk2jn3krfnvdosuoyfdiukj23bkjnwemvdnckjbksjbjkfhaf&social_id=117804837185203264985&social_name=DipakChhag&social_email=chhagdipak@gmail.com

        } else {
            Toast.makeText(context, "Login failed! please try again.", Toast.LENGTH_SHORT).show();
        }
    }


    class checkGoogleLoginAuth extends AsyncTask<String, Void, Boolean>
    {
        ProgressDialog pd;
        String username, uid , emailid;

        public checkGoogleLoginAuth(Context context, String username, String emailid, String uid)
        {
            pd = new ProgressDialog(context, R.style.ProgressDialog);
            pd.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            this.username = username;
            this.uid = uid;
            this.emailid = emailid;
        }

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            pd.setCancelable(false);
            if(!pd.isShowing())
                pd.show();
        }

        @Override
        protected Boolean doInBackground(String... strings)
        {
            return null;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean)
        {
            super.onPostExecute(aBoolean);

            StringRequest stringRequest = new StringRequest(Request.Method.POST, WSMethods.CHECKLOGINAUTH_GOOGLE,
                    new Response.Listener<String>()
                    {
                        @Override
                        public void onResponse(String response)
                        {
                            if(pd.isShowing())
                                pd.dismiss();

                            if(response!= null && !response.isEmpty())
                            {
                                try {
                                    JSONObject jsonResponse = new JSONObject(response);
                                    JSONObject data_response = jsonResponse.getJSONObject("response");
                                    if(!data_response.getBoolean("success"))
                                    {
                                        //Toast.makeText(context, "Login failed - Move to Login", Toast.LENGTH_SHORT).show();
                                        AppUtils.showDialog(LoginActivity.this,"OOPS", "These credentials dose not match in our records","Ok");
                                    }else
                                    {
                                        getFirebaseToken();
                                        //Toast.makeText(context, "Login success - Dashboard", Toast.LENGTH_SHORT).show();
                                        Intent iv = new Intent(context, Dashboard.class);
                                        iv.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
                                        iv.putExtra("NeedNavigate", ClsCommon.LOGIN);
                                        startActivity(iv);
                                        LoginActivity.this.finish();

                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    Log.e("Error:", e.getMessage());
                                }
                            }

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error)
                {
                    Log.e("Error:", error.toString() + "");
                    if(pd != null && pd.isShowing())
                        pd.dismiss();

                    Toast.makeText(context,"Response Error: "+ error + " Can't Connect to server.", Toast.LENGTH_LONG).show();
                    AppUtils.showDialog(LoginActivity.this,"OOPS", "These credentials do not match our records","Ok");
                }
            }
            )
            {
                @Override
                public Map<String, String> getHeaders()
                {
                    Map<String, String>  params = new HashMap<String, String>();
                    params.put(clsCommon.COOKIE, getCookie(LoginActivity.this));
                    //params.put("Authorization", "gzip, deflate, br");
                    return params;
                }

                @Override
                protected  Map<String,String> getParams()
                {
                    Map<String,String> params = new HashMap<String, String>();

                    params.put("token", uid+"");
                    params.put("app_client_secret", "90vyiuhnk2jn3krfnvdosuoyfdiukj23bkjnwemvdnckjbksjbjkfhaf");
                    params.put("social_id", uid+"");
                    params.put("social_name", username+"");
                    params.put("social_email", emailid+"");

                    return params;
                }

                @Override
                protected Response<String> parseNetworkResponse(NetworkResponse response)
                {
                    try {
                        //String jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers,"utf-8"));

                        List<Header> jsonheader = response.allHeaders;

                        String Cookie = "";
                        for(int i=0; i< jsonheader.size(); i++)
                        {
                            Header header = jsonheader.get(i);
                            if(header.getName().equals("Set-Cookie"))
                            {
                                Cookie+= header.getValue().split(";")[0] + ";";
                            }
                        }

                        Cookie = Cookie.substring(0, Cookie.length()-1);
                        AppUtils.saveCookie(LoginActivity.this,Cookie);

                    } catch (Exception je) {
                        return Response.error(new ParseError(je));
                    }

                    return super.parseNetworkResponse(response);
                }
            };

            stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                    10000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            requestQueue.add(stringRequest);
        }
    }

    private void getFirebaseToken() {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            return;
                        }
                        // Get new FCM registration token
                        String token = task.getResult();
                        AppUtils.saveFMCToken(LoginActivity.this,token);

                        new AddNotificationToken(LoginActivity.this,token).execute();
                    }
                });
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

            StringRequest stringRequest = new StringRequest(Request.Method.POST, WSMethods.ADD_NOTIFICATION,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            if (pd.isShowing())
                                pd.dismiss();

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
                    params.put(ClsCommon.COOKIE, getCookie(LoginActivity.this));
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
                        AppUtils.saveCookie(LoginActivity.this,Cookie);
                    } catch (Exception je) {
                        return Response.error(new ParseError(je));
                    }

                    return super.parseNetworkResponse(response);
                }
            };
            requestQueue.add(stringRequest);
        }
    }

}