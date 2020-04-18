package com.example.binreden;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.example.binreden.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


//danke an chris fuer die rechenleistung, ein dualcore ist doch ein wenig grausam um darauf zu arbeiten <3

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);
        WebView webView  = new WebView(this);
        webView = (WebView) findViewById(R.id.web_view);
        webView.setWebViewClient(new WebViewClient() {

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Toast.makeText(getApplicationContext(), description, Toast.LENGTH_SHORT).show();
            }
            @TargetApi(android.os.Build.VERSION_CODES.M)
            @Override
            public void onReceivedError(WebView view, WebResourceRequest req, WebResourceError rerr) {
                // Redirect to deprecated method, so you can use it in all SDK versions
                onReceivedError(view, rerr.getErrorCode(), rerr.getDescription().toString(), req.getUrl().toString());
            }
        });
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient(){
            public boolean shouldOverrideUrlLoading(WebView view, String url){
                String def = "https://binhacken.app/";
                if (url != null && url.startsWith(def)){
                    return false;
                }
                view.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                return true;
            }
        });
        webView.loadUrl("https://binhacken.app/home");


        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w("MainActivity", "getInstanceId failed", task.getException());
                            return;
                        }
                        // Get new Instance ID token
                        WebView wv = (WebView) findViewById((R.id.web_view));
                        String token = task.getResult().getToken();
                        // Log and toast
                        //String msg = getString(token);
                        Log.d("Main Activity, Token", token);
                        Toast.makeText(MainActivity.this, token, Toast.LENGTH_SHORT).show();
                        Log.d("Exists", wv.getUrl());
                        if (wv.getUrl().contains("home")){
                            Log.d("URLTEST", "Passed if");
                            String ur = "https://binhacken.app/token?data=" + token;
                            wv.loadUrl(ur);
                            wv.loadUrl("https://binhacken.app/home");
                            Toast.makeText(MainActivity.this, "Sent request", Toast.LENGTH_LONG).show();
                        }
                        else{
                            Toast.makeText(MainActivity.this, "lol you failed, reload app", Toast.LENGTH_SHORT).show();
                        }





                    }
                });
        //Log.d("something", FirebaseInstanceId.getInstance().getInstanceId().getResult().getToken());



    }


}
