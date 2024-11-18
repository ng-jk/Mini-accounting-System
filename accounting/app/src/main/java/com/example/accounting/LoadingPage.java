package com.example.accounting;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONObject;

public class LoadingPage extends AppCompatActivity {

    public final static String USERNAME = "USERNAME";
    public final static String USER_BUSINESS = "USER_BUSINESS";

    private String ClintId = "ATy3TNYyNqL40ltisK_-oN991mzSo1nqkO2L-RnseLhRdPkLwNn5YHCWZosqsuTTbxRmVA36c_g2FlZ4";
    private String SecretKey = "EHyj4Bsl-GPEL7xjMxFDAs5fY3XkE7tIFWWicGFm7PV8P0DEK4_raWtXf_F5UGDnazgrZMK7dqzCSSE0";
    private String accessToken = "";
    private static final String TAG = "MyTag";

    public static final String PREF_NAME = "login_pref";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_IS_LOGGED_IN = "is_logged_in";
    public static final String KEY_PAYPAL_ACCESS_KEY= "PaypalAccessKey";

    boolean login = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
        SharedPreferences SH = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = SH.edit();
        try{
            login = SH.getBoolean(KEY_IS_LOGGED_IN, false);
        }catch(Exception e){
            login = false;
        }

        // Using Handler with Looper to run a task for a specific time interval
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            String authString = ClintId + ":" + SecretKey;
            String encodedAuthString = Base64.encodeToString(authString.getBytes(), Base64.NO_WRAP);
            AndroidNetworking.post("https://api-m.sandbox.paypal.com/v1/oauth2/token")
                            .addHeaders("Authorization", "Basic " + encodedAuthString)
                            .addHeaders("Content-Type", "application/x-www-form-urlencoded")
                            .addBodyParameter("grant_type", "client_credentials")
                            .setPriority(Priority.HIGH)
                            .build()
                            .getAsJSONObject(new JSONObjectRequestListener() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    accessToken = response.optString("access_token");
                                    Log.d(TAG, accessToken);
                                }
                                @Override
                                public void onError(ANError error) {
                                    Log.d(TAG, error.getErrorBody());
                                    Toast.makeText(LoadingPage.this, "Error Occurred!", Toast.LENGTH_SHORT).show();
                                }
                            });
            editor.putString(KEY_PAYPAL_ACCESS_KEY,accessToken);
            Intent i;
            if (login){
                i = new Intent(LoadingPage.this, MainCashBookActivity.class);
            }
            else {
                i = new Intent(LoadingPage.this, LoginActivity.class);
            }
            startActivity(i);
            finish();
        }, 1000);
    }
}
