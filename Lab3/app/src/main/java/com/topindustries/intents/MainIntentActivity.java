package com.topindustries.intents;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.topindustries.helloandroid.R;

public class MainIntentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_intent);
    }

    public void clicked(View view){
        Uri uri;
        Intent intent;
        switch(view.getId()){
            case R.id.button1:
                intent = new Intent();;
                intent.setAction(Intent.ACTION_VIEW);
                uri = Uri.parse("https://www.google.com/");
                intent.setData(uri);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
                break;
            case R.id.button2:
                intent = new Intent();;
                intent.setAction(Intent.ACTION_VIEW);
                uri = Uri.parse("tel:00401213456");
                intent.setData(uri);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
                break;
            case R.id.button3:
                intent = new Intent();;
                intent.setAction("MSA.LAUNCH");
                uri = Uri.parse("https://www.google.com/");
                intent.setData(uri);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
                break;
            case R.id.button4:
                intent = new Intent();;
                intent.setAction("MSA.LAUNCH");
                uri = Uri.parse("tel:00401213456");
                intent.setData(uri);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
                break;
        }
    }
}