package com.topindustries.lifecycle;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.topindustries.helloandroid.R;

public class ActivityB extends AppCompatActivity {

    private static final String TAG = "Activity B";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_b);
        setTitle("B");
        Log.d(ActivityB.TAG, "onCreate");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(ActivityB.TAG, "onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(ActivityB.TAG, "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(ActivityB.TAG, "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(ActivityB.TAG, "onStop");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(ActivityB.TAG, "onRestart");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(ActivityB.TAG, "onDestroy");
    }

    public void clicked(View view) {
        switch (view.getId()) {
            case R.id.buttonA2:
                startActivity(new Intent(this, ActivityA.class));
                break;
            case R.id.buttonB2:
                startActivity(new Intent(this, ActivityB.class));
                break;
            case R.id.buttonC2:
                startActivity(new Intent(this, ActivityC.class));
                break;
        }
    }
}