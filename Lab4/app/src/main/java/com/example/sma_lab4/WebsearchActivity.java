package com.example.sma_lab4;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class WebsearchActivity extends AppCompatActivity {
    public static String EXTRA_URL;

    private class MyCustomWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (Uri.parse(url).toString().startsWith("https://www.google.com/search?q=")
                    && Uri.parse(url).toString().contains("tbm=isch")) {
                return false;
            }
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
            return true;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_websearch);

        WebView myWebView = (WebView) findViewById(R.id.webview);
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        myWebView.setWebViewClient(new MyCustomWebViewClient());
        myWebView.loadUrl("https://www.google.com/search?q=cat&tbm=isch&source=lnms&sa=X");
    }

    public void clicked(View view) {
        // simple way to extract clipboard data (string)
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData abc = clipboard.getPrimaryClip();
        ClipData.Item item = abc.getItemAt(0);
        String url = item.getText().toString();

        // check url for validity
        if (!url.contains("https://images.app.goo.gl"))
            Toast.makeText(this, "URL not valid. Try another.", Toast.LENGTH_SHORT).show();
        else {
            // start background service to download in background
            if (view.getId() == R.id.buttonLoadWithBackgroundService) {
                Intent intent = new Intent(this, ImageIntentService.class);
                intent.putExtra(EXTRA_URL, url);
                startService(intent);
            }
            // start foreground service to download with notification
            else if (view.getId() == R.id.buttonLoadWithForegroundService) {
                Intent startIntent = new Intent(this, ForegroundImageService.class);
                startIntent.setAction(ForegroundImageService.STARTFOREGROUND_ACTION);
                startIntent.putExtra(EXTRA_URL, url);
                startService(startIntent);
            }
        }
    }
}

