package com.topindustries.lifecycle;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.topindustries.helloandroid.R;

public class ActivityA extends AppCompatActivity {

    private static final String TAG = "Activity A";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_a);
        setTitle("A");
        Log.d(ActivityA.TAG, "onCreate");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(ActivityA.TAG, "onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(ActivityA.TAG, "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(ActivityA.TAG, "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(ActivityA.TAG, "onStop");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(ActivityA.TAG, "onRestart");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(ActivityA.TAG, "onDestroy");
    }

    public void clicked(View view) {
        switch (view.getId()) {
            case R.id.buttonA:
                startActivity(new Intent(this, ActivityA.class));
                break;
            case R.id.buttonB:
                startActivity(new Intent(this, ActivityB.class));
                break;
            case R.id.buttonC:
                startActivity(new Intent(this, ActivityC.class));
                break;
        }
    }
}